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
 * 评分记录表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("score_record")
public class ScoreRecord {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 申请ID */
    private Long applicationId;

    /** 经济情况分（0-100） */
    private BigDecimal economicScore;

    /** 学习成绩分（0-100） */
    private BigDecimal academicScore;

    /** 家庭困难分（0-100） */
    private BigDecimal familyScore;

    /** 奖惩情况分（0-100） */
    private BigDecimal rewardScore;

    /** 评分教师ID */
    private Long scorerId;

    /** 评分时间 */
    private LocalDateTime scoreTime;
}
