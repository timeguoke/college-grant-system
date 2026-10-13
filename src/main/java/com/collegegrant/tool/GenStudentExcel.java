package com.collegegrant.tool;

import com.alibaba.excel.EasyExcel;
import com.collegegrant.pojo.dto.StudentExcelDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 临时工具：生成多学院学生测试Excel（用于验证级联筛选效果）
 * 运行：在项目根目录执行 mvn exec:java -Dexec.mainClass=com.collegegrant.tool.GenStudentExcel
 */
public class GenStudentExcel {
    public static void main(String[] args) {
        List<StudentExcelDTO> rows = new ArrayList<>();

        // 信息学院（已有），补充不同班级
        rows.add(build("20240101", "张明", "男", "信息学院", "计算机科学", "计科2401", "13810000001", "普通"));
        rows.add(build("20240102", "李华", "男", "信息学院", "软件工程", "软工2402", "13810000002", "建档立卡"));
        rows.add(build("20240103", "王芳", "女", "信息学院", "软件工程", "软工2402", "13810000003", "低保家庭"));

        // 计算机学院（新增）
        rows.add(build("20240201", "陈强", "男", "计算机学院", "人工智能", "智科2401", "13820000001", "普通"));
        rows.add(build("20240202", "刘洋", "男", "计算机学院", "网络工程", "网工2401", "13820000002", "孤儿"));
        rows.add(build("20240203", "赵琳", "女", "计算机学院", "人工智能", "智科2401", "13820000003", "普通"));

        // 外国语学院（新增）
        rows.add(build("20240301", "周敏", "女", "外国语学院", "英语", "英语2401", "13830000001", "普通"));
        rows.add(build("20240302", "吴磊", "男", "外国语学院", "日语", "日语2401", "13830000002", "残疾"));
        rows.add(build("20240303", "郑洁", "女", "外国语学院", "英语", "英语2402", "13830000003", "普通"));

        // 经济管理学院（新增）
        rows.add(build("20240401", "孙浩", "男", "经济管理学院", "工商管理", "工管2401", "13840000001", "普通"));
        rows.add(build("20240402", "钱莹", "女", "经济管理学院", "会计学", "会计2401", "13840000002", "低保家庭"));
        rows.add(build("20240403", "冯涛", "男", "经济管理学院", "金融学", "金融2401", "13840000003", "普通"));

        String out = "student_test_multi_college.xlsx";
        EasyExcel.write(out, StudentExcelDTO.class)
                .sheet("学生导入")
                .doWrite(rows);
        System.out.println("已生成: " + out + "，共 " + rows.size() + " 条");
    }

    private static StudentExcelDTO build(String no, String name, String gender, String college,
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
