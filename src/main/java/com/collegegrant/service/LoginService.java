package com.collegegrant.service;

import com.collegegrant.pojo.dto.LoginDTO;
import com.collegegrant.pojo.vo.LoginVO;

public interface LoginService {
    /**
     * 用户登录
     * @param loginDTO
     * @return
     */
    LoginVO login(LoginDTO loginDTO);
}
