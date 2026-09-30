package com.collegegrant.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.PageResult;
import com.collegegrant.common.Result;
import com.collegegrant.entity.Student;
import com.collegegrant.service.admin.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin/student")
@Slf4j
@Api(tags = "学生管理相关接口")
public class StudentController {

    @Autowired
    private StudentService studentService;


    @GetMapping("/list")
    @ApiOperation("学生分页")
    public PageResult<Student> pageStudent(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String college,
            @RequestParam(required = false) Integer isPoor) {

        log.info("学生分页,分页信息 page={},size={},keyword={},college={},isPoor={}", page, size, keyword, college, isPoor);
        Page<Student> result = studentService.list(page, size, keyword, college, isPoor);
        //总记录数
        Long total = result.getTotal();
        //数据
        List<Student> records = result.getRecords();
        return PageResult.success(total, records);
    }

    @GetMapping("/count")
    @ApiOperation("统计学生总数")
    public Result countStudent() {
        log.info("统计学生总数");
        Long count = studentService.countStudent();
        return Result.success(count);
    }

    @PostMapping
    @ApiOperation("新增学生")
    public Result save(@RequestBody Student student) {
        log.info("新增学生 :student={}", student);
        studentService.insert(student);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("查询学生详情")
    public Result getStudentDetail(@PathVariable Long id) {
        log.info("查询学生详情 :id={}", id);
        Student student = studentService.getStudentById(id);
        if (student == null) {
            return Result.error("学生不存在");
        }
        return Result.success(student);
    }

    @PutMapping("/{id}")
    @ApiOperation("修改学生信息")
    public Result updateStudent(@PathVariable Long id, @RequestBody Student student) {
        log.info("修改学生信息 :id={},Student ={}", id, student);
        student.setId(id);
        studentService.update(student);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除学生")
    public Result deleteStudent(@PathVariable Long id) {
        log.info("删除学生 :id={} ", id);
        studentService.deleteById(id);
        return Result.success();
    }



}
