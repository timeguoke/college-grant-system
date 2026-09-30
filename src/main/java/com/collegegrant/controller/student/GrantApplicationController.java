package com.collegegrant.controller.student;

import com.collegegrant.common.ApplicationDTO;
import com.collegegrant.common.Result;
import com.collegegrant.service.student.GrantApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/student/application")
@Api(tags = "申请表相关接口")
@Slf4j
public class GrantApplicationController {

    @Autowired
    private GrantApplicationService grantApplicationService;

    @PostMapping
    @ApiOperation("提交申请")
    public Result submitApplication(@RequestBody ApplicationDTO applicationDTO, HttpServletRequest request) {
        log.info("提交申请:applicationDTO ={}", applicationDTO);
        Long studentId = Long.valueOf(request.getAttribute("userId").toString());
         log.info("申请人:studentId ={}",studentId);
        grantApplicationService.submitApplication(applicationDTO,studentId);
        return Result.success();
    }
}
