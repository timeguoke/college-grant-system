package com.collegegrant.pojo.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 评定结果导出DTO（EasyExcel列定义）
 */
@Data
public class EvaluationExportDTO {

    @ExcelProperty(value = "排名", index = 0)
    @ColumnWidth(10)
    private Integer ranking;

    @ExcelProperty(value = "姓名", index = 1)
    @ColumnWidth(15)
    private String studentName;

    @ExcelProperty(value = "学号", index = 2)
    @ColumnWidth(20)
    private String studentNo;

    @ExcelProperty(value = "助学金类型", index = 3)
    @ColumnWidth(20)
    private String typeName;

    @ExcelProperty(value = "总分", index = 4)
    @ColumnWidth(10)
    private String score;

    @ExcelProperty(value = "是否特殊困难", index = 5)
    @ColumnWidth(15)
    private String isSpecial;

    @ExcelProperty(value = "等级", index = 6)
    @ColumnWidth(10)
    private String levelName;

    @ExcelProperty(value = "金额", index = 7)
    @ColumnWidth(10)
    private String grantAmount;
}
