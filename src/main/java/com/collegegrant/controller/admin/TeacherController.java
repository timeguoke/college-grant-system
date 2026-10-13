package com.collegegrant.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.result.PageResult;
import com.collegegrant.common.result.Result;
import com.collegegrant.pojo.dto.TeacherExcelDTO;
import com.collegegrant.pojo.entity.Teacher;
import com.collegegrant.service.TeacherService;
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
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String college) {

        log.info("教师分页,分页信息 page={},size={},keyword={},college={} ", page, size, keyword, college);
        Page<Teacher> result = teacherService.list(page, size, keyword, college);
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

    @GetMapping("/export")
    @ApiOperation("导出教师Excel（支持当前筛选条件）")
    public void export(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String college,
                       HttpServletResponse response) throws Exception {
        log.info("导出教师Excel :keyword={},college={}", keyword, college);
        List<Teacher> teachers = teacherService.listForExport(keyword, college);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("教师名单", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), TeacherExcelDTO.class)
                .sheet("教师名单")
                .doWrite(convertToExcelList(teachers));
    }

    @GetMapping("/export-template")
    @ApiOperation("下载教师导入模板")
    public void exportTemplate(HttpServletResponse response) throws Exception {
        log.info("下载教师导入模板");
        TeacherExcelDTO demo = new TeacherExcelDTO();
        demo.setTeacherNo("120240001");
        demo.setName("张老师");
        demo.setGender("男");
        demo.setCollege("信息学院");
        demo.setTitle("讲师");
        demo.setPhone("13800000000");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("教师导入模板", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        List<TeacherExcelDTO> data = new ArrayList<>();
        data.add(demo);
        EasyExcel.write(response.getOutputStream(), TeacherExcelDTO.class)
                .sheet("教师导入")
                .doWrite(data);
    }

    @PostMapping("/import")
    @ApiOperation("批量导入教师Excel")
    public Result importTeachers(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("批量导入教师,文件名={}", file == null ? "" : file.getOriginalFilename());
        if (file == null || file.isEmpty()) {
            return Result.error("请选择要导入的Excel文件");
        }
        List<TeacherExcelDTO> rows = EasyExcel.read(file.getInputStream())
                .head(TeacherExcelDTO.class)
                .sheet()
                .doReadSync();
        if (rows == null || rows.isEmpty()) {
            return Result.error("Excel中没有可导入的数据");
        }
        return Result.success(teacherService.importTeachers(rows));
    }

    /**
     * 教师实体 → Excel导出行（性别0男1女、职称0助教1讲师2副教授3教授 转中文）
     */
    private List<TeacherExcelDTO> convertToExcelList(List<Teacher> teachers) {
        List<TeacherExcelDTO> data = new ArrayList<>();
        for (Teacher t : teachers) {
            TeacherExcelDTO d = new TeacherExcelDTO();
            d.setTeacherNo(t.getTeacherNo());
            d.setName(t.getName());
            if (t.getGender() != null) {
                d.setGender(t.getGender() == 0 ? "男" : "女");
            }
            d.setCollege(t.getCollege());
            if (t.getTitle() != null) {
                d.setTitle(new String[]{"助教", "讲师", "副教授", "教授"}[t.getTitle()]);
            }
            d.setPhone(t.getPhone());
            data.add(d);
        }
        return data;
    }

}
