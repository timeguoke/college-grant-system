package com.collegegrant.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 助学金类型表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("grant_type")
public class GrantType {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 助学金名称 */
    private String typeName;

    /** 状态：0停用，1可申请 */
    private Integer status;

    /** 描述说明 */
    private String description;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}