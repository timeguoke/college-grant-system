package com.collegegrant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.EvaluateDTO;
import com.collegegrant.common.EvaluateResultVO;
import com.collegegrant.common.EvaluationResultVO;
import com.collegegrant.common.UserContext;
import com.collegegrant.entity.*;
import com.collegegrant.mapper.*;
import com.collegegrant.service.EvaluationResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EvaluationResultServiceImpl implements EvaluationResultService {
    @Autowired
    private EvaluationResultMapper evaluationResultMapper;
    @Autowired
    private GrantTypeMapper grantTypeMapper;
    @Autowired
    private GrantLevelMapper grantLevelMapper;
    @Autowired
    private GrantApplicationMapper grantApplicationMapper;
    @Autowired
    private ScoreRecordMapper scoreRecordMapper;

    /**
     * AHP评定
     *
     * @param evaluateDTO
     * @return
     */
    @Override
    @Transactional
    public EvaluateResultVO AHP(EvaluateDTO evaluateDTO) {

        // ============================================================
        // 一、评定前校验
        // ============================================================

        // 注：参数非空校验已由DTO的@NotNull注解和Controller的@Valid处理

        // 1. 助学金类型校验
        Long typeId = evaluateDTO.getTypeId();
        GrantType grantType = grantTypeMapper.selectById(typeId);
        if (grantType == null) {
            throw new RuntimeException("该助学金不存在");
        }

        // 2. 权重之和校验（必须等于100）
        int sum = evaluateDTO.getEconomicWeight()
                + evaluateDTO.getFamilyBurdenWeight()
                + evaluateDTO.getSpecialHardshipWeight()
                + evaluateDTO.getDisasterWeight();
        if (sum != 100) {
            throw new RuntimeException("权重之和必须等于100，当前和为：" + sum);
        }

        // ============================================================
        // 二、准备数据
        // ============================================================

        // 1. 查询特殊学生申请（特殊学生不评分，审核通过即可参与评定：status=1 + special_type>0）
        LambdaQueryWrapper<GrantApplication> specialWrapper = new LambdaQueryWrapper<>();
        specialWrapper.eq(GrantApplication::getTypeId, typeId)
                .eq(GrantApplication::getStatus, 1)
                .gt(GrantApplication::getSpecialType, 0);
        List<GrantApplication> specialApps = grantApplicationMapper.selectList(specialWrapper);

        // 2. 查询普通学生申请（已评分才能参与评定：status=3 + special_type=0）
        LambdaQueryWrapper<GrantApplication> normalWrapper = new LambdaQueryWrapper<>();
        normalWrapper.eq(GrantApplication::getTypeId, typeId)
                .eq(GrantApplication::getStatus, 3)
                .eq(GrantApplication::getSpecialType, 0);
        List<GrantApplication> normalApps = grantApplicationMapper.selectList(normalWrapper);

        // 3. 查询等级配置（按sort升序，一等在前）
        LambdaQueryWrapper<GrantLevel> levelWrapper = new LambdaQueryWrapper<>();
        levelWrapper.eq(GrantLevel::getTypeId, typeId)
                .orderByAsc(GrantLevel::getSort);
        List<GrantLevel> levels = grantLevelMapper.selectList(levelWrapper);

        // 4. 等级配置校验：没有等级配置无法评定
        if (levels == null || levels.isEmpty()) {
            throw new RuntimeException("该助学金未配置等级，请先配置等级后再评定");
        }

        // 5. 可评定学生校验：没有特殊生也没有普通生，直接返回空统计
        //    （避免后面批量更新生成 WHERE id IN () 报错）
        if (specialApps.isEmpty() && normalApps.isEmpty()) {
            EvaluateResultVO vo = new EvaluateResultVO();
            vo.setTypeId(typeId);
            vo.setTypeName(grantType.getTypeName());
            vo.setEvaluateTime(LocalDateTime.now().toString());
            vo.setTotalCount(0);
            vo.setSpecialCount(0);
            vo.setNormalCount(0);
            vo.setAwardedCount(0);
            vo.setNotAwardedCount(0);
            // 等级统计也返回（各等级人数为0），方便前端展示
            List<EvaluateResultVO.LevelResultVO> levelResults = new ArrayList<>();
            for (GrantLevel level : levels) {
                EvaluateResultVO.LevelResultVO lr = new EvaluateResultVO.LevelResultVO();
                lr.setLevelName(level.getLevelName());
                lr.setCount(0);
                lr.setAmount(level.getAmount());
                levelResults.add(lr);
            }
            vo.setLevelResults(levelResults);
            return vo;
        }

        // 6. 权重转小数（35 → 0.35）
        double economicW = evaluateDTO.getEconomicWeight() / 100.0;
        double familyW = evaluateDTO.getFamilyBurdenWeight() / 100.0;
        double specialW = evaluateDTO.getSpecialHardshipWeight() / 100.0;
        double disasterW = evaluateDTO.getDisasterWeight() / 100.0;

        // ============================================================
        // 三、评定计算
        // ============================================================

        Long evaluatorId = UserContext.getUserId();
        List<EvaluationResult> allResults = new ArrayList<>();

        // 1. 特殊学生评定：直接评最高等级，总分100，排名0（不参与排名）
        for (GrantApplication app : specialApps) {
            EvaluationResult result = EvaluationResult.builder()
                    .applicationId(app.getId())
                    .studentId(app.getStudentId())
                    .totalScore(new BigDecimal("100"))   // 特殊生直接满分
                    .scoreRank(0)                          // 0表示不参与排名
                    .levelId(levels.get(0).getId())        // 最高等级（一等）
                    .evaluatorId(evaluatorId)
                    .evaluateTime(LocalDateTime.now())
                    .build();
            allResults.add(result);
        }

        // 2. 普通学生评定：查评分记录 + 计算AHP加权总分
        List<Long> appIds = normalApps.stream().map(GrantApplication::getId).collect(Collectors.toList());

        if (!appIds.isEmpty()) {
            // 2.1 批量查询评分记录
            LambdaQueryWrapper<ScoreRecord> scoreWrapper = new LambdaQueryWrapper<>();
            scoreWrapper.in(ScoreRecord::getApplicationId, appIds);
            List<ScoreRecord> scores = scoreRecordMapper.selectList(scoreWrapper);

            // 2.2 转成Map，key=applicationId，方便查找
            Map<Long, ScoreRecord> scoreMap = scores.stream()
                    .collect(Collectors.toMap(ScoreRecord::getApplicationId, s -> s));

            // 2.3 遍历每个普通学生，计算加权总分
            List<EvaluationResult> normalResults = new ArrayList<>();
            for (GrantApplication app : normalApps) {
                ScoreRecord sr = scoreMap.get(app.getId());
                // 没有评分记录的跳过
                if (sr == null) continue;

                // AHP加权总分 = 经济分×经济权重 + 家庭负担分×家庭负担权重
                //             + 特殊困难分×特殊困难权重 + 突发困难分×突发困难权重
                double total = sr.getEconomicScore().doubleValue() * economicW
                        + sr.getFamilyBurdenScore().doubleValue() * familyW
                        + sr.getSpecialHardshipScore().doubleValue() * specialW
                        + sr.getDisasterScore().doubleValue() * disasterW;

                EvaluationResult result = EvaluationResult.builder()
                        .applicationId(app.getId())
                        .studentId(app.getStudentId())
                        .totalScore(BigDecimal.valueOf(total)
                                .setScale(2, RoundingMode.HALF_UP)) // 保留2位小数
                        .scoreRank(0)                          // 排名后面统一算
                        .levelId(0L)                           // 等级后面匹配
                        .evaluatorId(evaluatorId)
                        .evaluateTime(LocalDateTime.now())
                        .build();
                normalResults.add(result);
            }

            // 2.4 按总分降序排序，设置排名和等级
            normalResults.sort((a, b) -> b.getTotalScore().compareTo(a.getTotalScore()));

            for (int i = 0; i < normalResults.size(); i++) {
                EvaluationResult r = normalResults.get(i);
                r.setScoreRank(i + 1);  // 排名从1开始

                // 从高到低匹配等级，匹配到就停
                for (GrantLevel level : levels) {
                    if (r.getTotalScore().compareTo(level.getMinScore()) >= 0) {
                        r.setLevelId(level.getId());
                        break;
                    }
                }
                // 都不匹配则levelId保持0（未评上）
            }

            // 2.5 普通学生结果加入总列表
            allResults.addAll(normalResults);
        }

        // ============================================================
        // 四、数据落库
        // ============================================================

        // 1. 批量插入评定结果
        for (EvaluationResult r : allResults) {
            evaluationResultMapper.insert(r);
        }

        // 2. 更新申请状态为"已评定"（status=4）
        List<Long> allAppIds = allResults.stream()
                .map(EvaluationResult::getApplicationId)
                .collect(Collectors.toList());
        LambdaUpdateWrapper<GrantApplication> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(GrantApplication::getId, allAppIds)
                .set(GrantApplication::getStatus, 4);
        grantApplicationMapper.update(null, updateWrapper);

        // ============================================================
        // 五、返回统计结果
        // ============================================================

        EvaluateResultVO vo = new EvaluateResultVO();
        vo.setTypeId(typeId);
        vo.setTypeName(grantType.getTypeName());
        vo.setEvaluateTime(LocalDateTime.now().toString());

        // 总人数 = 特殊生 + 普通生
        int totalCount = specialApps.size() + normalApps.size();
        vo.setTotalCount(totalCount);
        vo.setSpecialCount(specialApps.size());
        vo.setNormalCount(normalApps.size());

        // 获助人数：levelId不为0
        long awardedCount = allResults.stream()
                .filter(r -> r.getLevelId() != null && r.getLevelId() != 0L)
                .count();
        vo.setAwardedCount((int) awardedCount);
        // 未获助人数 = 总人数 - 获助人数
        vo.setNotAwardedCount((int) (totalCount - awardedCount));

        // 各等级统计
        List<EvaluateResultVO.LevelResultVO> levelResults = new ArrayList<>();
        for (GrantLevel level : levels) {
            long count = allResults.stream()
                    .filter(r -> level.getId().equals(r.getLevelId()))
                    .count();
            EvaluateResultVO.LevelResultVO lr = new EvaluateResultVO.LevelResultVO();
            lr.setLevelName(level.getLevelName());
            lr.setCount((int) count);
            lr.setAmount(level.getAmount());
            levelResults.add(lr);
        }
        vo.setLevelResults(levelResults);

        return vo;
    }

    /**
     * 评定结果列表（分页）
     *
     * @param page
     * @param size
     * @param typeId
     * @param keyword
     * @return
     */
    @Override
    public Page<EvaluationResultVO> list(int page, int size, Long typeId, String keyword) {
        Page<EvaluationResultVO> pageParam = new Page<>(page, size);
        return evaluationResultMapper.selectResultPage(pageParam, typeId, keyword);
    }

    /**
     * 评定结果详情
     *
     * @param id
     * @return
     */
    @Override
    public EvaluationResultVO getEvalDetail(Long id) {
        return evaluationResultMapper.selectResultDetail(id);
    }

}
