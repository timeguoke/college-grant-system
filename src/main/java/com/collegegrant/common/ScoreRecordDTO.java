package com.collegegrant.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 评分提交DTO
 * 教师提交评分时的入参对象
 * 包含4个困难维度的分数（0-100分）和评分评语
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRecordDTO {

    /** 申请ID（提交评分时必填，修改评分时从路径传） */
    private Long applicationId;

    /** 家庭经济情况分（0-100），收入越低分越高 */
    private BigDecimal economicScore;

    /** 家庭负担情况分（0-100），负担越重分越高（家庭人口多、老人孩子多等） */
    private BigDecimal familyBurdenScore;

    /** 特殊困难程度分（0-100），困难程度越高分越高（建档立卡/低保/孤儿/残疾等） */
    private BigDecimal specialHardshipScore;

    /** 突发困难情况分（0-100），受灾/重大疾病等突发情况 */
    private BigDecimal disasterScore;

    /** 评分评语（可选） */
    private String scoreComment;
}
