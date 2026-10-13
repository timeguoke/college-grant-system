package com.collegegrant.pojo.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 公示名单导出DTO（EasyExcel列定义）
 */
@Data
public class NoticeStudentExcelDTO {

    @ExcelProperty(value = "序号", index = 0)
    @ColumnWidth(10)
    private Integer index;

    @ExcelProperty(value = "姓名", index = 1)
    @ColumnWidth(15)
    private String studentName;

    @ExcelProperty(value = "学号", index = 2)
    @ColumnWidth(20)
    private String studentNo;

    @ExcelProperty(value = "资助等级", index = 3)
    @ColumnWidth(15)
    private String levelName;

    @ExcelProperty(value = "资助金额(元)", index = 4)
    @ColumnWidth(15)
    private String grantAmount;
}
