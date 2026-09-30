package com.collegegrant.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.entity.SysUser;
import com.collegegrant.mapper.SysUserMapper;
import com.collegegrant.service.admin.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;


    /**
     * 用户列表（分页）
     * name 字段直接从 sys_user 表查，不用关联其他表
     */
    @Override
    public Page<SysUser> list(int page, int size, String keyword, Integer role) {
        Page<SysUser> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        // keyword 不为空时，按用户名模糊查询
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SysUser::getUsername, keyword);
        }

        // role 不为空时，按角色筛选
        if (role != null) {
            wrapper.eq(SysUser::getRole, role);
        }

        // 按创建时间倒序
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> result = sysUserMapper.selectPage(pageParam, wrapper);
        return result;
    }

    /**
     * 用户查询
     *
     * @param id
     * @return
     */
    @Override
    public SysUser selectById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        return sysUser;

    }

    /**
     * 启用禁用账号
     *
     * @param sysUser
     */
    @Override
    public void update(SysUser sysUser) {
        // 修改部分：先检查用户是否存在
        SysUser existUser = sysUserMapper.selectById(sysUser.getId());
        if (existUser == null) {
            throw new RuntimeException("用户不存在，无法修改");
        }

        // 修改部分：校验 status 取值范围（只能是0或1）
        if (sysUser.getStatus() != null && sysUser.getStatus() != 0 && sysUser.getStatus() != 1) {
            throw new RuntimeException("状态值非法，只能是0（禁用）或1（启用）");
        }

        sysUserMapper.updateById(sysUser);
    }

    /**
     * 重置密码
     * @param id 用户ID
     */
    @Override
    public void resetPassword(Long id) {
        // 修改部分：先检查用户是否存在
        SysUser existUser = sysUserMapper.selectById(id);
        if (existUser == null) {
            throw new RuntimeException("用户不存在，无法重置密码");
        }

        SysUser sysUser = SysUser.builder()
                .id(id)
                // 默认密码 123456 的 MD5 值
                .password("e10adc3949ba59abbe56e057f20f883e")
                .build();
        //只根据id重置了密码
        sysUserMapper.updateById(sysUser);
    }

}
