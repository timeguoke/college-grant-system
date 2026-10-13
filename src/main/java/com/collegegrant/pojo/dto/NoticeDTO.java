package com.collegegrant.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

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
    /** 公示开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    /** 公示结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
