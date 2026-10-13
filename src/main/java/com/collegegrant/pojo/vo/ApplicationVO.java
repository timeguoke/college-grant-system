package com.collegegrant.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 助学金申请返回VO
 * 用于返回申请列表和详情，包含关联的助学金类型名称、审核人姓名、状态文字等
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationVO {

    /** 申请ID */
    private Long id;

    /** 助学金类型ID */
    private Long typeId;

    /** 助学金类型名称 */
    private String typeName;

    /** 申请理由 */
    private String applyReason;

    /** 家庭情况描述 */
    private String familySituation;

    /** 申请时填报的家庭年收入（元） */
    private java.math.BigDecimal familyIncome;

    /** 申请时填报的家庭人口数 */
    private Integer familyMembers;

    /** 人均年收入（元） */
    private java.math.BigDecimal perCapitaIncome;

    /** 特殊困难类型：0无，1建档立卡，2低保家庭，3孤儿，4残疾 */
    private Integer specialType;

    /** 特殊困难证明材料路径（特殊生有值，普通生为空） */
    private String specialProof;

    /** 状态：0待审核，1通过，2驳回，3已评分，4已评定，5已公示 */
    private Integer status;

    /** 状态文字描述 */
    private String statusText;

    /** 审核人ID */
    private Long reviewerId;

    /** 审核人姓名 */
    private String reviewerName;

    /** 审核意见 */
    private String reviewComment;

    /** 审核时间 */
    private LocalDateTime reviewTime;

    /** 学生ID */
    private Long studentId;

    /** 学号 */
    private String studentNo;

    /** 学生姓名 */
    private String studentName;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
