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

    /** 新方案:家庭经济情况分（0-100），收入越低分越高 */
    private BigDecimal economicScore;

    /** 新方案:家庭负担情况分（0-100），负担越重分越高 */
    private BigDecimal familyBurdenScore;

    /** 新方案:特殊困难程度分（0-100），困难程度越高分越高 */
    private BigDecimal specialHardshipScore;

    /** 新方案:突发困难情况分（0-100），受灾/重大疾病等 */
    private BigDecimal disasterScore;

    /** 评分教师ID */
    private Long scorerId;

    /** 新方案:评分评语 */
    private String scoreComment;

    /** 评分时间 */
    private LocalDateTime scoreTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
