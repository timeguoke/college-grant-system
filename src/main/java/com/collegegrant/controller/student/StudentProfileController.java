package com.collegegrant.controller.student;

import com.collegegrant.common.context.UserContext;
import com.collegegrant.common.result.Result;
import com.collegegrant.pojo.entity.Student;
import com.collegegrant.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学生端个人资料接口
 */
@RestController("studentProfileController")
@RequestMapping("/api/student/profile")
@Api(tags = "学生端:个人资料相关接口")
@Slf4j
public class StudentProfileController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    @ApiOperation("查询我的个人资料")
    public Result getMyProfile() {
        // 当前登录账号即学号，学生只能查看自己的资料
        String studentNo = UserContext.getUsername();
        log.info("查询学生个人资料:studentNo ={}", studentNo);
        Student student = studentService.getStudentByNo(studentNo);
        return Result.success(student);
    }
}
