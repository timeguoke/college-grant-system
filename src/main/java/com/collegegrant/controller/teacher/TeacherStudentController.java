package com.collegegrant.controller.teacher;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.context.UserContext;
import com.collegegrant.common.result.PageResult;
import com.collegegrant.common.result.Result;
import com.collegegrant.mapper.GrantApplicationMapper;
import com.collegegrant.mapper.StudentMapper;
import com.collegegrant.pojo.entity.Student;
import com.collegegrant.pojo.vo.ApplicationVO;
import com.collegegrant.pojo.vo.TeacherStudentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/student")
@Api(tags = "教师端-我的学生")
@Slf4j
public class TeacherStudentController {

    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private GrantApplicationMapper grantApplicationMapper;

    @GetMapping("/page")
    @ApiOperation("我的学生分页列表（按学生聚合，附申请统计）")
    public PageResult<TeacherStudentVO> page(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) String keyword) {
        // 从JWT token获取当前教师ID，只查自己名下的学生
        Long counselorId = UserContext.getUserId();
        log.info("我的学生分页列表 page={},size={},keyword={},counselorId={}", page, size, keyword, counselorId);
        Page<TeacherStudentVO> result = studentMapper.selectTeacherStudentPage(new Page<>(page, size), keyword, counselorId);
        return PageResult.success(result.getTotal(), result.getRecords());
    }

    @GetMapping("/{studentId}/applications")
    @ApiOperation("指定学生的申请记录（仅限自己名下的学生）")
    public Result<List<ApplicationVO>> applications(@PathVariable Long studentId) {
        Long counselorId = UserContext.getUserId();
        log.info("查看学生申请记录 studentId={},counselorId={}", studentId, counselorId);

        // 权限校验：只能查看自己名下的学生
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("该学生不存在");
        }
        if (!counselorId.equals(student.getCounselorId())) {
            throw new RuntimeException("无权查看该学生的申请记录");
        }

        Page<ApplicationVO> page = grantApplicationMapper.selectApplicationPage(
                new Page<>(1, 100), studentId, null, null, null, null, null);
        return Result.success(page.getRecords());
    }
}
