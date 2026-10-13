package com.collegegrant.service;

import com.collegegrant.pojo.dto.PasswordDTO;

public interface PasswordService {
    /**
     * 修改密码
     * @param passwordDTO 旧密码+新密码
     */
    void changePassword(PasswordDTO passwordDTO);
}
