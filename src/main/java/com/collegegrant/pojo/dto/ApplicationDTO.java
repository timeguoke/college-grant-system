package com.collegegrant.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 助学金申请DTO
 * 学生提交申请时只填这3个字段
 * studentId 从token里获取，status 后端自动设为0（待审核）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {

    /** 助学金类型ID */
    private Long typeId;

    /** 申请理由 */
    private String applyReason;

    /** 家庭情况描述 */
    private String familySituation;

    /** 特殊困难类型：0无，1建档立卡，2低保家庭，3孤儿，4残疾 */
    private Integer specialType;

    /** 特殊困难证明材料路径（特殊困难学生必填） */
    private String specialProof;
}
