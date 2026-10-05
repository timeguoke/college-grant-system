package com.collegegrant.service;

import com.collegegrant.common.LoginDTO;
import com.collegegrant.common.LoginVO;

public interface LoginService {
    /**
     * 用户登录
     * @param loginDTO
     * @return
     */
    LoginVO login(LoginDTO loginDTO);
}
