package com.collegegrant.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户修改DTO
 * 只包含允许修改的字段，防止前端篡改role、password等敏感字段
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUserDTO {

    /** 登录账号 */
    private String username;

    /** 姓名 */
    private String name;

    /** 状态：0禁用，1正常 */
    private Integer status;
}
