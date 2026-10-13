package com.collegegrant.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 助学金类型分页列表VO（含等级数量）
 */
@Data
public class GrantTypePageVO {

    /** 主键ID */
    private Long id;

    /** 助学金名称 */
    private String typeName;

    /** 状态：0停用，1可申请 */
    private Integer status;

    /** 描述说明 */
    private String description;

    /** 等级数量 */
    private Integer levelCount;

    /** 创建时间 */
    private LocalDateTime createTime;
}
