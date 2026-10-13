package com.collegegrant.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Excel 批量导入结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultVO {
    /** 读取到的数据总行数 */
    private Integer total;
    /** 成功导入条数 */
    private Integer successCount;
    /** 失败条数 */
    private Integer failCount;
    /** 失败明细（第N行：原因） */
    private List<String> failMessages;
}
