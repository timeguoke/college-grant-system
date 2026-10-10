package com.collegegrant.pojo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 评定结果详情返回值
 * 用于：评定结果列表展示、评定结果详情展示
 * 列表接口返回时4个维度得分为null，详情接口返回时才包含
 */
@Data
public class EvaluationResultVO {
    /** 评定结果ID */
    private Long id;
    /** 申请ID */
    private Long applicationId;
    /** 学生姓名 */
    private String studentName;
    /** 学生学号 */
    private String studentNo;
    /** 助学金类型名称 */
    private String typeName;
    /** AHP加权总分 */
    private BigDecimal score;
    /** 排名（普通学生从1开始，特殊学生排前面） */
    private Integer ranking;
    /** 是否特殊困难学生：0否，1是 */
    private Integer isSpecial;
    /** 评定等级名称（如"一等"、"二等"） */
    private String levelName;
    /** 资助金额 */
    private BigDecimal grantAmount;
    // ===== 以下字段仅详情接口返回 =====
    /** 经济状况评分（详情专属） */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal economicScore;
    /** 家庭负担评分（详情专属） */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal familyBurdenScore;
    /** 特殊困难评分（详情专属） */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal specialHardshipScore;
    /** 突发困难评分（详情专属） */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal disasterScore;
    /** 评定时间 */
    private String createTime;
}
