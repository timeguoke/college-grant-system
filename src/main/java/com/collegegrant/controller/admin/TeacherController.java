package com.collegegrant.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.PageResult;
import com.collegegrant.common.Result;
import com.collegegrant.entity.Teacher;
import com.collegegrant.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin/teacher")
@Slf4j
@Api(tags = "教师管理相关接口")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;


    @GetMapping("/list")
    @ApiOperation("教师分页")
    public PageResult<Teacher> pageTeacher(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        log.info("教师分页,分页信息 page={},size={},keyword={} ", page, size, keyword);
        Page<Teacher> result = teacherService.list(page, size, keyword);
        //总记录数
        Long total = result.getTotal();
        //数据
        List<Teacher> records = result.getRecords();
        return PageResult.success(total, records);
    }

    @PostMapping
    @ApiOperation("新增教师")
    public Result save(@RequestBody Teacher teacher) {
        log.info("新增教师 :teacher={}", teacher);
        teacherService.insert(teacher);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("查询教师详情")
    public Result getTeacherDetail(@PathVariable Long id) {
        log.info("查询教师详情 :id={}", id);
        Teacher teacher = teacherService.getTeacherById(id);
        if (teacher == null) {
            return Result.error("教师不存在");
        }
        return Result.success(teacher);
    }

    @PutMapping("/{id}")
    @ApiOperation("修改教师信息")
    public Result updateTeacher(@PathVariable Long id, @RequestBody Teacher teacher) {
        log.info("修改教师信息 :id={},Teacher ={}", id, teacher);
        teacher.setId(id);
        teacherService.update(teacher);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除教师")
    public Result deleteTeacher(@PathVariable Long id) {
        log.info("删除教师 :id={} ", id);
        teacherService.deleteById(id);
        return Result.success();
    }



    @GetMapping("/count")
    @ApiOperation("统计教师总数")
    public Result countTeacher() {
        log.info("统计教师总数");
        Long count = teacherService.countTeacher();
        return Result.success(count);
    }

}
