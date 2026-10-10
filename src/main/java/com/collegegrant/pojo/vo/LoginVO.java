package com.collegegrant.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {
    private String token;      // JWT令牌
    private String username;   // 用户名
    private Integer role;      // 角色：0管理员，1教师，2学生
    private Long userId;       // 关联的学生/教师ID
    private String name;       // 真实姓名（管理员显示"管理员"，教师显示教师名，学生显示学生名）
}