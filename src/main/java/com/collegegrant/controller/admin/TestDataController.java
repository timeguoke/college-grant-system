package com.collegegrant.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.collegegrant.pojo.dto.StudentExcelDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 临时工具接口：生成多学院学生测试Excel（用于验证级联筛选）
 * 验证完成后删除此类
 */
@Slf4j
@Api(tags = "测试工具")
@RestController
@RequestMapping("/test")
public class TestDataController {

    @GetMapping("/student-excel")
    @ApiOperation("生成多学院学生测试Excel")
    public void genStudentExcel(HttpServletResponse response) throws Exception {
        List<StudentExcelDTO> rows = new ArrayList<>();
        rows.add(build("20240101", "张明", "男", "信息学院", "计算机科学", "计科2401", "13810000001", "普通"));
        rows.add(build("20240102", "李华", "男", "信息学院", "软件工程", "软工2402", "13810000002", "建档立卡"));
        rows.add(build("20240103", "王芳", "女", "信息学院", "软件工程", "软工2402", "13810000003", "低保家庭"));
        rows.add(build("20240201", "陈强", "男", "计算机学院", "人工智能", "智科2401", "13820000001", "普通"));
        rows.add(build("20240202", "刘洋", "男", "计算机学院", "网络工程", "网工2401", "13820000002", "孤儿"));
        rows.add(build("20240203", "赵琳", "女", "计算机学院", "人工智能", "智科2401", "13820000003", "普通"));
        rows.add(build("20240301", "周敏", "女", "外国语学院", "英语", "英语2401", "13830000001", "普通"));
        rows.add(build("20240302", "吴磊", "男", "外国语学院", "日语", "日语2401", "13830000002", "残疾"));
        rows.add(build("20240303", "郑洁", "女", "外国语学院", "英语", "英语2402", "13830000003", "普通"));
        rows.add(build("20240401", "孙浩", "男", "经济管理学院", "工商管理", "工管2401", "13840000001", "普通"));
        rows.add(build("20240402", "钱莹", "女", "经济管理学院", "会计学", "会计2401", "13840000002", "低保家庭"));
        rows.add(build("20240403", "冯涛", "男", "经济管理学院", "金融学", "金融2401", "13840000003", "普通"));

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("多学院学生测试", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), StudentExcelDTO.class)
                .sheet("学生导入")
                .doWrite(rows);
        log.info("生成多学院测试Excel，共{}条", rows.size());
    }

    private StudentExcelDTO build(String no, String name, String gender, String college,
                                  String major, String className, String phone, String special) {
        StudentExcelDTO d = new StudentExcelDTO();
        d.setStudentNo(no);
        d.setName(name);
        d.setGender(gender);
        d.setCollege(college);
        d.setMajor(major);
        d.setClassName(className);
        d.setPhone(phone);
        d.setSpecialType(special);
        return d;
    }
}
