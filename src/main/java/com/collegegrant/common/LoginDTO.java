package com.collegegrant.common;

import lombok.Data;

@Data
public class LoginDTO {
    private String username;  // 用户名（管理员账号/教师工号/学生学号）
    private String password;  // 密码
}
