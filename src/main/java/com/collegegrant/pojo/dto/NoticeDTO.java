package com.collegegrant.pojo.dto;

import lombok.Data;

/**
 * 新增/修改公示DTO
 */
@Data
public class NoticeDTO {
    /** 公示标题 */
    private String title;
    /** 公示内容 */
    private String content;
    /** 助学金类型ID */
    private Long typeId;
}
