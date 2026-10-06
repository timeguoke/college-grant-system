package com.collegegrant.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 评分记录返回VO
 * 用于评分列表和评分详情的返回，包含关联的学生信息、助学金类型、评分教师等
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRecordVO {

    /** 评分记录ID */
    private Long id;

    /** 申请ID */
    private Long applicationId;

    /** 学生ID */
    private Long studentId;

    /** 学号 */
    private String studentNo;

    /** 学生姓名 */
    private String studentName;

    /** 助学金类型ID */
    private Long typeId;

    /** 助学金类型名称 */
    private String typeName;

    /** 家庭经济情况分（0-100），收入越低分越高 */
    private BigDecimal economicScore;

    /** 家庭负担情况分（0-100），负担越重分越高 */
    private BigDecimal familyBurdenScore;

    /** 特殊困难程度分（0-100），困难程度越高分越高 */
    private BigDecimal specialHardshipScore;

    /** 突发困难情况分（0-100），受灾/重大疾病等 */
    private BigDecimal disasterScore;

    /** 评分教师ID */
    private Long scorerId;

    /** 评分教师姓名 */
    private String scorerName;

    /** 评分评语 */
    private String scoreComment;

    /** 评分时间 */
    private LocalDateTime scoreTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
