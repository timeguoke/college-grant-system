package com.collegegrant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.collegegrant.mapper.*;
import com.collegegrant.pojo.entity.*;
import com.collegegrant.pojo.vo.DashboardVO;
import com.collegegrant.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private GrantTypeMapper grantTypeMapper;
    @Autowired
    private PublicNoticeMapper publicNoticeMapper;
    @Autowired
    private GrantApplicationMapper grantApplicationMapper;
    @Autowired
    private EvaluationResultMapper evaluationResultMapper;

    @Override
    public DashboardVO getDashboardData() {
        DashboardVO vo = new DashboardVO();

        // 1. 基础统计
        vo.setStudentCount(studentMapper.selectCount(null));
        vo.setTeacherCount(teacherMapper.selectCount(null));
        vo.setGrantTypeCount(grantTypeMapper.selectCount(null));
        vo.setNoticeCount(publicNoticeMapper.selectCount(null));

        // 2. 申请统计（按状态）
        DashboardVO.ApplicationStats appStats = new DashboardVO.ApplicationStats();
        appStats.setTotalCount(grantApplicationMapper.selectCount(null));

        LambdaQueryWrapper<GrantApplication> w0 = new LambdaQueryWrapper<>();
        w0.eq(GrantApplication::getStatus, 0);
        appStats.setPendingCount(grantApplicationMapper.selectCount(w0));

        LambdaQueryWrapper<GrantApplication> w1 = new LambdaQueryWrapper<>();
        w1.eq(GrantApplication::getStatus, 1);
        appStats.setApprovedCount(grantApplicationMapper.selectCount(w1));

        LambdaQueryWrapper<GrantApplication> w2 = new LambdaQueryWrapper<>();
        w2.eq(GrantApplication::getStatus, 2);
        appStats.setRejectedCount(grantApplicationMapper.selectCount(w2));

        LambdaQueryWrapper<GrantApplication> w3 = new LambdaQueryWrapper<>();
        w3.eq(GrantApplication::getStatus, 3);
        appStats.setScoredCount(grantApplicationMapper.selectCount(w3));

        LambdaQueryWrapper<GrantApplication> w4 = new LambdaQueryWrapper<>();
        w4.eq(GrantApplication::getStatus, 4);
        appStats.setEvaluatedCount(grantApplicationMapper.selectCount(w4));

        LambdaQueryWrapper<GrantApplication> w5 = new LambdaQueryWrapper<>();
        w5.eq(GrantApplication::getStatus, 5);
        appStats.setPublishedCount(grantApplicationMapper.selectCount(w5));

        vo.setApplicationStats(appStats);

        // 3. 评定统计（按助学金类型）
        List<GrantType> types = grantTypeMapper.selectList(null);
        List<DashboardVO.EvaluationStats> evalList = new ArrayList<>();
        for (GrantType type : types) {
            DashboardVO.EvaluationStats stats = new DashboardVO.EvaluationStats();
            stats.setTypeId(type.getId());
            stats.setTypeName(type.getTypeName());

            // 通过申请表关联type_id，先查该type的申请ID
            LambdaQueryWrapper<GrantApplication> appW = new LambdaQueryWrapper<>();
            appW.eq(GrantApplication::getTypeId, type.getId());
            List<GrantApplication> apps = grantApplicationMapper.selectList(appW);
            if (!apps.isEmpty()) {
                List<Long> appIds = new ArrayList<>();
                for (GrantApplication app : apps) {
                    appIds.add(app.getId());
                }
                LambdaQueryWrapper<EvaluationResult> evalW = new LambdaQueryWrapper<>();
                evalW.in(EvaluationResult::getApplicationId, appIds);
                Long totalEval = evaluationResultMapper.selectCount(evalW);
                stats.setNotAwardedCount(totalEval);

                LambdaQueryWrapper<EvaluationResult> awardW = new LambdaQueryWrapper<>();
                awardW.in(EvaluationResult::getApplicationId, appIds).gt(EvaluationResult::getLevelId, 0);
                Long awarded = evaluationResultMapper.selectCount(awardW);
                stats.setAwardedCount(awarded);
                stats.setNotAwardedCount(totalEval - awarded);
            } else {
                stats.setAwardedCount(0L);
                stats.setNotAwardedCount(0L);
            }

            // 该类型特殊困难学生数
            LambdaQueryWrapper<GrantApplication> specialW = new LambdaQueryWrapper<>();
            specialW.eq(GrantApplication::getTypeId, type.getId()).gt(GrantApplication::getSpecialType, 0);
            stats.setSpecialCount(grantApplicationMapper.selectCount(specialW));

            evalList.add(stats);
        }
        vo.setEvaluationStatsList(evalList);

        return vo;
    }
}
