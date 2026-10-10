package com.collegegrant.common;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 执行AHP评定后的统计汇总返回值
 * 用于：POST /api/admin/evaluation/do 接口返回
 */
@Data
public class EvaluateResultVO {
    /** 助学金类型ID */
    private Long typeId;
    /** 助学金类型名称 */
    private String typeName;
    /** 参与评定总人数（特殊生+普通生） */
    private Integer totalCount;
    /** 特殊困难学生人数 */
    private Integer specialCount;
    /** 普通学生人数 */
    private Integer normalCount;
    /** 获助人数（匹配到等级的人数） */
    private Integer awardedCount;
    /** 未获助人数（低于最低等级分数线的人数） */
    private Integer notAwardedCount;
    /** 各等级评定统计 */
    private List<LevelResultVO> levelResults;
    /** 评定时间 */
    private String evaluateTime;

    /**
     * 单个等级的评定统计
     */
    @Data
    public static class LevelResultVO {
        /** 等级名称（如"一等"、"二等"） */
        private String levelName;
        /** 该等级评定人数 */
        private Integer count;
        /** 该等级资助金额 */
        private BigDecimal amount;
    }
}
