package com.collegegrant.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * 首页仪表盘统计数据
 */
@Data
public class DashboardVO {
    /** 学生总数 */
    private Long studentCount;
    /** 教师总数 */
    private Long teacherCount;
    /** 助学金类型数 */
    private Long grantTypeCount;
    /** 公示数量 */
    private Long noticeCount;

    /** 申请统计（按状态分类） */
    private ApplicationStats applicationStats;

    /** 评定统计（按助学金类型） */
    private List<EvaluationStats> evaluationStatsList;

    /**
     * 申请统计
     */
    @Data
    public static class ApplicationStats {
        private Long totalCount;        // 总申请数
        private Long pendingCount;     // 待审核
        private Long approvedCount;    // 已通过
        private Long rejectedCount;    // 已驳回
        private Long scoredCount;      // 已评分
        private Long evaluatedCount;   // 已评定
        private Long publishedCount;   // 已公示
    }

    /**
     * 评定统计（按助学金类型）
     */
    @Data
    public static class EvaluationStats {
        private Long typeId;
        private String typeName;
        private Long awardedCount;     // 获助人数
        private Long notAwardedCount;  // 未获助人数
        private Long specialCount;     // 特殊困难学生数
    }
}
