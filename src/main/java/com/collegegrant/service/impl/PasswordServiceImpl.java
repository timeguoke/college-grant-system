package com.collegegrant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.collegegrant.common.context.UserContext;
import com.collegegrant.mapper.SysUserMapper;
import com.collegegrant.pojo.entity.SysUser;
import com.collegegrant.pojo.dto.PasswordDTO;
import com.collegegrant.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordServiceImpl implements PasswordService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public void changePassword(PasswordDTO passwordDTO) {
        // 1. 获取当前登录用户ID（JwtInterceptor中设置的username）
        String username = UserContext.getUsername();
        if (username == null) {
            throw new RuntimeException("请先登录");
        }

        // 2. 查询用户信息
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = sysUserMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 3. 校验旧密码（MD5比较）
        String oldMd5 = md5(passwordDTO.getOldPassword());
        if (!oldMd5.equals(user.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }

        // 4. 更新密码
        String newMd5 = md5(passwordDTO.getNewPassword());
        user.setPassword(newMd5);
        sysUserMapper.updateById(user);
    }

    /**
     * MD5加密（与登录时保持一致）
     */
    private String md5(String text) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(text.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密失败");
        }
    }
}
