package com.collegegrant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 评定结果表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("evaluation_result")
public class EvaluationResult {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 申请ID */
    private Long applicationId;

    /** 学生ID */
    private Long studentId;

    /** AHP加权总分 */
    private BigDecimal totalScore;

    /** 排名 */
    private Integer scoreRank;

    /** 评定等级：1一等，2二等，3三等 */
    private Long levelId;

    /** 评定人ID */
    private Long evaluatorId;

    /** 评定时间 */
    private LocalDateTime evaluateTime;
}
