package com.collegegrant.controller.common;

import com.collegegrant.common.result.Result;
import com.collegegrant.pojo.dto.PasswordDTO;
import com.collegegrant.service.PasswordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@Api(tags = "通用-密码管理")
@Slf4j
public class PasswordController {

    @Autowired
    private PasswordService passwordService;

    @PutMapping("/password")
    @ApiOperation("修改密码")
    public Result changePassword(@RequestBody @Valid PasswordDTO passwordDTO) {
        log.info("修改密码");
        passwordService.changePassword(passwordDTO);
        return Result.success();
    }
}
