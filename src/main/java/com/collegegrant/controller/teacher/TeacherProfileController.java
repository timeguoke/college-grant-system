package com.collegegrant.controller.teacher;

import com.collegegrant.common.context.UserContext;
import com.collegegrant.common.result.Result;
import com.collegegrant.pojo.entity.Teacher;
import com.collegegrant.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("teacherProfileController")
@RequestMapping("/api/teacher/profile")
@Api(tags = "教师端:个人资料")
@Slf4j
public class TeacherProfileController {

    @Autowired
    private TeacherService teacherService;

    @GetMapping
    @ApiOperation("教师个人资料")
    public Result<Teacher> getProfile() {
        Long userId = UserContext.getUserId();
        log.info("教师个人资料 userId={}", userId);
        Teacher teacher = teacherService.getTeacherById(userId);
        return Result.success(teacher);
    }
}
