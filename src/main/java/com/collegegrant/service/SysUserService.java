package com.collegegrant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.entity.SysUser;

public interface SysUserService {

    /**
     * 用户列表（分页）
     * @param page 页码
     * @param size 每页条数
     * @param keyword 搜索关键词（用户名）
     * @param role 角色筛选
     * @return 分页结果
     */
    Page<SysUser> list(int page, int size, String keyword, Integer role);

    /**
     * 用户查询
     * @param id
     * @return
     */
    SysUser selectById(Long id);

    /**
     * 修改用户信息
     * @param sysUser
     */
    void update(SysUser sysUser);

    /**
     * 重置密码
     * @param id
     */
    void resetPassword(Long id);
}
