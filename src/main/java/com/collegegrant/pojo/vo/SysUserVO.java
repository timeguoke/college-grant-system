package com.collegegrant.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户信息返回对象（给前端用）
 * 比 SysUser 实体多一个 name 字段，少一个 password 字段（密码不返回）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUserVO {

    /** 主键ID */
    private Long id;

    /** 登录账号（学号/工号） */
    private String username;

    /** 角色：0管理员，1教师，2学生 */
    private Integer role;

    /** 关联ID（教师关联teacher表id，学生关联student表id） */
    private Long userId;

    /** 姓名：管理员显示"管理员"，教师显示教师姓名，学生显示学生姓名 */
    private String name;

    /** 状态：0禁用，1正常 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;
}
