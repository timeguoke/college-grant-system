package com.collegegrant.pojo.dto;

import com.collegegrant.pojo.entity.GrantLevel;
import lombok.Data;

import java.util.List;

/**
 * 助学金类型新增/编辑DTO（类型信息 + 等级列表整体提交）
 */
@Data
public class GrantTypeDTO {

    /** 主键ID（编辑时使用） */
    private Long id;

    /** 助学金名称 */
    private String typeName;

    /** 状态：0停用，1可申请 */
    private Integer status;

    /** 描述说明 */
    private String description;

    /** 等级列表（有id为更新，无id为新增） */
    private List<GrantLevel> levels;
}
