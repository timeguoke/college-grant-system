package com.collegegrant.controller.login;

import com.collegegrant.common.JwtUtil;
import com.collegegrant.common.LoginDTO;
import com.collegegrant.common.LoginVO;
import com.collegegrant.common.Result;
import com.collegegrant.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/login")
@Api(tags = "用户登录相关接口")
@Slf4j
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    @ApiOperation("用户登录")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        log.info("登录者的信息 loginDTO={}",loginDTO);
        LoginVO loginVO = loginService.login(loginDTO);
        System.out.println("学生id----:"+loginVO.getUserId());

        //登录成功,生成jwt令牌
        String token = JwtUtil.createToken(loginVO.getUserId(), loginVO.getUsername() ,loginVO.getRole() );
        loginVO.setToken(token);
        return Result.success(loginVO);
    }
}
