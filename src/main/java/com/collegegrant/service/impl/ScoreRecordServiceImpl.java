package com.collegegrant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.ApplicationVO;
import com.collegegrant.common.ScoreRecordDTO;
import com.collegegrant.common.ScoreRecordVO;
import com.collegegrant.common.UserContext;
import com.collegegrant.entity.GrantApplication;
import com.collegegrant.entity.ScoreRecord;
import com.collegegrant.entity.Student;
import com.collegegrant.mapper.GrantApplicationMapper;
import com.collegegrant.mapper.ScoreRecordMapper;
import com.collegegrant.mapper.StudentMapper;
import com.collegegrant.service.ScoreRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ScoreRecordServiceImpl implements ScoreRecordService {
    @Autowired
    private ScoreRecordMapper scoreRecordMapper;
    @Autowired
    private GrantApplicationMapper grantApplicationMapper;
    @Autowired
    private StudentMapper studentMapper;

    /**
     * 提交评分
     *
     * @param scoreRecordDTO
     */
    @Transactional
    @Override
    public void submitScore(ScoreRecordDTO scoreRecordDTO) {
        Long applicationId = scoreRecordDTO.getApplicationId();
        //1. 判断申请ID不能为空
        if (applicationId == null) {
            throw new RuntimeException("申请ID不能为空");
        }
        //2. 判断申请是否存在

        GrantApplication grantApplication = grantApplicationMapper.selectById(applicationId);
        if (grantApplication == null) {
            throw new RuntimeException("该助学金申请不存在");
        }
        //只有通过申请的才能进行评分
        if (grantApplication.getStatus() != 1) {
            throw new RuntimeException("未通过申请,无法评分");
        }

        // 校验：特殊困难学生不需要评分
        if (grantApplication.getSpecialType() != null && grantApplication.getSpecialType() > 0) {
            throw new RuntimeException("特殊困难学生无需评分，将直接参与评定");
        }
        //3. 校验：该申请已经评过分，禁止重复评分
        LambdaQueryWrapper<ScoreRecord> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(ScoreRecord::getApplicationId, applicationId);
        ScoreRecord existRecord = scoreRecordMapper.selectOne(existWrapper);
        if (existRecord != null) {
            throw new RuntimeException("该申请已完成评分，不可重复提交");
        }

        //4. 校验：4个维度分数必须在0-100之间
        validateScoreRange(scoreRecordDTO);

        //进行数据拷贝
        ScoreRecord scoreRecord = new ScoreRecord();
        BeanUtils.copyProperties(scoreRecordDTO, scoreRecord);

        //5. 后端填充字段（重点！前端不传）
        Long teacherId = UserContext.getUserId();
        scoreRecord.setScorerId(teacherId);

        scoreRecord.setScoreTime(LocalDateTime.now());
        scoreRecord.setCreateTime(LocalDateTime.now());
        scoreRecord.setUpdateTime(LocalDateTime.now());

        scoreRecordMapper.insert(scoreRecord);

        // 更新申请状态为 已评分(3)
        GrantApplication updateApp = new GrantApplication();
        updateApp.setId(applicationId);
        updateApp.setStatus(3);
        updateApp.setUpdateTime(LocalDateTime.now());
        grantApplicationMapper.updateById(updateApp);


    }

    /**
     * 修改评分
     *
     * @param scoreRecordDTO 评分信息
     */
    @Transactional
    @Override
    public void updateScore(ScoreRecordDTO scoreRecordDTO) {
        Long applicationId = scoreRecordDTO.getApplicationId();
        //1. 判断申请是否存在
        GrantApplication grantApplication = grantApplicationMapper.selectById(applicationId);
        if (grantApplication == null) {
            throw new RuntimeException("该申请不存在");
        }
        //判断状态
        if (grantApplication.getStatus() != 3) {
            if (grantApplication.getStatus() >= 4) {
                throw new RuntimeException("该申请已完成评定，无法修改评分");
            }
            throw new RuntimeException("只能对已评分的申请进行修改");
        }

        //4. 校验：4个维度分数必须在0-100之间
        validateScoreRange(scoreRecordDTO);

        //5. 查询评分记录
        LambdaQueryWrapper<ScoreRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScoreRecord::getApplicationId, applicationId);
        ScoreRecord score = scoreRecordMapper.selectOne(wrapper);
        if (score == null) {
            throw new RuntimeException("该申请没有评分记录");
        }

        //6. 校验：只能修改自己打的分
        Long currentTeacherId = UserContext.getUserId();
        if (!score.getScorerId().equals(currentTeacherId)) {
            throw new RuntimeException("只能修改自己的评分记录");
        }

        //7. 拷贝数据并更新
        ScoreRecord scoreRecord = new ScoreRecord();
        BeanUtils.copyProperties(scoreRecordDTO, scoreRecord);
        scoreRecord.setScorerId(currentTeacherId);
        scoreRecord.setScoreTime(LocalDateTime.now());
        scoreRecord.setUpdateTime(LocalDateTime.now());

        LambdaUpdateWrapper<ScoreRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ScoreRecord::getApplicationId, applicationId);

        scoreRecordMapper.update(scoreRecord, updateWrapper);
    }

    /**
     * 校验4个维度分数是否在0-100之间
     *
     * @param scoreRecordDTO 评分信息
     */
    private void validateScoreRange(ScoreRecordDTO scoreRecordDTO) {
        BigDecimal min = BigDecimal.ZERO;
        BigDecimal max = new BigDecimal("100");

        if (scoreRecordDTO.getEconomicScore() == null
                || scoreRecordDTO.getEconomicScore().compareTo(min) < 0
                || scoreRecordDTO.getEconomicScore().compareTo(max) > 0) {
            throw new RuntimeException("家庭经济情况分必须在0-100之间");
        }
        if (scoreRecordDTO.getFamilyBurdenScore() == null
                || scoreRecordDTO.getFamilyBurdenScore().compareTo(min) < 0
                || scoreRecordDTO.getFamilyBurdenScore().compareTo(max) > 0) {
            throw new RuntimeException("家庭负担情况分必须在0-100之间");
        }
        if (scoreRecordDTO.getSpecialHardshipScore() == null
                || scoreRecordDTO.getSpecialHardshipScore().compareTo(min) < 0
                || scoreRecordDTO.getSpecialHardshipScore().compareTo(max) > 0) {
            throw new RuntimeException("特殊困难程度分必须在0-100之间");
        }
        if (scoreRecordDTO.getDisasterScore() == null
                || scoreRecordDTO.getDisasterScore().compareTo(min) < 0
                || scoreRecordDTO.getDisasterScore().compareTo(max) > 0) {
            throw new RuntimeException("突发困难情况分必须在0-100之间");
        }
    }


    /**
     * 评分列表
     * @param page
     * @param size
     * @param keyword
     * @param typeId
     * @param scorerId
     * @return
     */
    @Override
    public Page<ScoreRecordVO> list(int page, int size, String keyword, Long typeId, Long scorerId) {
        //页码信息
        Page<ScoreRecordVO> pageParam = new Page<>(page, size);


        Page<ScoreRecordVO> VOPage = scoreRecordMapper.selectScorePage(pageParam, keyword, typeId, scorerId);
        return VOPage;

    }

    /**
     * 评分详情
     *
     * @param applicationId
     * @return
     */
    @Override
    public ScoreRecordVO getScoreDetail(Long applicationId) {

        //1. 判断申请是否存在
        GrantApplication grantApplication = grantApplicationMapper.selectById(applicationId);
        if (grantApplication == null) {
            throw new RuntimeException("该申请不存在");
        }

        ScoreRecordVO scoreRecordVO = scoreRecordMapper.selectByApplicationId(applicationId);
        if (scoreRecordVO == null) {
            throw new RuntimeException("该申请暂无评分记录");
        }
        return scoreRecordVO;

    }

    /**
     * 待评分申请列表（分页）
     *
     * @param page
     * @param size
     * @param keyword
     * @param typeId
     * @return
     */
    @Override
    public Page<ApplicationVO> listPendingApply(int page, int size, String keyword, Long typeId) {
        //页码信息
        Page<ApplicationVO> pageParam = new Page<>(page, size);

        Page<ApplicationVO> voPage = grantApplicationMapper.selectPendingScorePage(pageParam, keyword, typeId);

        //填充状态码文字
        for (ApplicationVO VO : voPage.getRecords()) {
            VO.setStatusText(getStatusText(VO.getStatus()));
        }


        return voPage;

    }

    /**
     * 状态码转文字
     */
    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0:
                return "待审核";
            case 1:
                return "通过";
            case 2:
                return "驳回";
            case 3:
                return "已评分";
            case 4:
                return "已评定";
            case 5:
                return "已公示";
            default:
                return "未知";
        }

    }
}
