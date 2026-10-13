package com.collegegrant.pojo.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 学生导入/导出共用的 Excel 模型（表头与 student 表字段对应）
 */
@Data
public class StudentExcelDTO {

    @ExcelProperty("学号")
    @ColumnWidth(16)
    private String studentNo;

    @ExcelProperty("姓名")
    @ColumnWidth(10)
    private String name;

    @ExcelProperty("性别")
    @ColumnWidth(8)
    private String gender;

    @ExcelProperty("学院")
    @ColumnWidth(18)
    private String college;

    @ExcelProperty("专业")
    @ColumnWidth(18)
    private String major;

    @ExcelProperty("班级")
    @ColumnWidth(14)
    private String className;

    @ExcelProperty("联系电话")
    @ColumnWidth(16)
    private String phone;

    @ExcelProperty("特殊类型")
    @ColumnWidth(12)
    private String specialType;
}
