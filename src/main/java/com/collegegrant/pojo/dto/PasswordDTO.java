package com.collegegrant.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 修改密码请求参数
 */
@Data
public class PasswordDTO {

    /** 旧密码 */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /** 新密码 */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
