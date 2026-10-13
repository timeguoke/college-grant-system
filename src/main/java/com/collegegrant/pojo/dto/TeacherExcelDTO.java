package com.collegegrant.pojo.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 教师导入/导出共用的 Excel 模型（表头与 teacher 表字段对应）
 */
@Data
public class TeacherExcelDTO {

    @ExcelProperty("工号")
    @ColumnWidth(16)
    private String teacherNo;

    @ExcelProperty("姓名")
    @ColumnWidth(10)
    private String name;

    @ExcelProperty("性别")
    @ColumnWidth(8)
    private String gender;

    @ExcelProperty("院系")
    @ColumnWidth(18)
    private String college;

    @ExcelProperty("职称")
    @ColumnWidth(12)
    private String title;

    @ExcelProperty("联系电话")
    @ColumnWidth(16)
    private String phone;
}
