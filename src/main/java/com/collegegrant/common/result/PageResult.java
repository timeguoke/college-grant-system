package com.collegegrant.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private Integer code;      // 状态码：200成功
    private String msg;         // 提示信息
    private Long total;         // 总记录数（比如156条）
    private List<T> rows;       // 当前页的数据列表

    public static <T> PageResult<T> success(Long total, List<T> rows){
        return new PageResult <>( 200 , "操作成功" , total, rows);
    }


}
