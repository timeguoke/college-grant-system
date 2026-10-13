package com.collegegrant.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.result.PageResult;
import com.collegegrant.common.result.Result;
import com.collegegrant.pojo.dto.StudentExcelDTO;
import com.collegegrant.pojo.entity.Student;
import com.collegegrant.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
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
            @RequestParam(required = false) String className,
            @RequestParam(required = false) Integer specialType) {

        log.info("学生分页,分页信息 page={},size={},keyword={},college={},className={},specialType={}", page, size, keyword, college, className, specialType);
        Page<Student> result = studentService.list(page, size, keyword, college, className, specialType);
        //总记录数
        Long total = result.getTotal();
        //数据
        List<Student> records = result.getRecords();
        return PageResult.success(total, records);
    }

    @GetMapping("/colleges")
    @ApiOperation("学院列表（去重，供筛选下拉）")
    public Result getColleges() {
        log.info("查询学院下拉列表");
        return Result.success(studentService.getColleges());
    }

    @GetMapping("/classes")
    @ApiOperation("班级列表（去重，可按学院过滤，供级联下拉）")
    public Result getClasses(@RequestParam(required = false) String college) {
        log.info("查询班级下拉列表,college={}", college);
        return Result.success(studentService.getClasses(college));
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

    @GetMapping("/export")
    @ApiOperation("导出学生Excel（支持当前筛选条件）")
    public void export(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String college,
                       @RequestParam(required = false) Integer specialType,
                       HttpServletResponse response) throws Exception {
        log.info("导出学生Excel :keyword={},college={},specialType={}", keyword, college, specialType);
        List<Student> students = studentService.listForExport(keyword, college, specialType);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("学生名单", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), StudentExcelDTO.class)
                .sheet("学生名单")
                .doWrite(convertToExcelList(students));
    }

    @GetMapping("/export-template")
    @ApiOperation("下载学生导入模板")
    public void exportTemplate(HttpServletResponse response) throws Exception {
        log.info("下载学生导入模板");
        // 模板带一行示例数据，便于填写
        StudentExcelDTO demo = new StudentExcelDTO();
        demo.setStudentNo("20240001");
        demo.setName("张三");
        demo.setGender("男");
        demo.setCollege("计算机学院");
        demo.setMajor("软件工程");
        demo.setClassName("软工2401");
        demo.setPhone("13800000000");
        demo.setSpecialType("普通");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("学生导入模板", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        List<StudentExcelDTO> data = new ArrayList<>();
        data.add(demo);
        EasyExcel.write(response.getOutputStream(), StudentExcelDTO.class)
                .sheet("学生导入")
                .doWrite(data);
    }

    @PostMapping("/import")
    @ApiOperation("批量导入学生Excel")
    public Result importStudents(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("批量导入学生,文件名={}", file == null ? "" : file.getOriginalFilename());
        if (file == null || file.isEmpty()) {
            return Result.error("请选择要导入的Excel文件");
        }
        List<StudentExcelDTO> rows = EasyExcel.read(file.getInputStream())
                .head(StudentExcelDTO.class)
                .sheet()
                .doReadSync();
        if (rows == null || rows.isEmpty()) {
            return Result.error("Excel中没有可导入的数据");
        }
        return Result.success(studentService.importStudents(rows));
    }

    /**
     * 学生实体 → Excel导出行
     */
    private List<StudentExcelDTO> convertToExcelList(List<Student> students) {
        List<StudentExcelDTO> data = new ArrayList<>();
        for (Student s : students) {
            StudentExcelDTO d = new StudentExcelDTO();
            d.setStudentNo(s.getStudentNo());
            d.setName(s.getName());
            if (s.getGender() != null) {
                d.setGender(s.getGender() == 1 ? "男" : "女");
            }
            d.setCollege(s.getCollege());
            d.setMajor(s.getMajor());
            d.setClassName(s.getClassName());
            d.setPhone(s.getPhone());
            if (s.getSpecialType() != null) {
                d.setSpecialType(new String[]{"普通", "建档立卡", "低保家庭", "孤儿", "残疾"}[s.getSpecialType()]);
            }
            data.add(d);
        }
        return data;
    }

}
