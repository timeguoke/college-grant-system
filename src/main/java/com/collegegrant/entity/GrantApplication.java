package com.collegegrant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * 助学金申请表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("grant_application")
public class GrantApplication {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 助学金类型ID
     */
    private Long typeId;

    /**
     * 申请理由
     */
    private String applyReason;

    /**
     * 家庭情况描述
     */
    private String familySituation;
    /**
     * 特殊困难类型：0无，1建档立卡，2低保家庭，3孤儿，4残疾
     */
    @Min(0)
    @Max(4)
    private Integer specialType;
    /**
     * 特殊困难证明材料路径
     */
    private String specialProof;

    /**
     * 状态：0待审核，1通过，2驳回，3已评分，4已评定，5已公示
     */
    private Integer status;

    /**
     * 审核教师ID
     */
    private Long reviewerId;

    /**
     * 审核意见
     */
    private String reviewComment;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
