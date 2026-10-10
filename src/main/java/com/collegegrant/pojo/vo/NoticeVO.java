package com.collegegrant.pojo.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 公示列表返回值
 */
@Data
public class NoticeVO {
    /** 公示ID */
    private Long id;
    /** 公示标题 */
    private String title;
    /** 公示内容摘要（列表展示用，太长的话前端截取） */
    private String content;
    /** 公示开始时间 */
    private LocalDateTime startTime;
    /** 公示结束时间 */
    private LocalDateTime endTime;
    /** 状态：0未发布，1公示中，2已结束 */
    private Integer status;
    /** 状态文字描述 */
    private String statusText;
    /** 发布人姓名 */
    private String publisherName;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 助学金类型ID */
    private Long typeId;
    /** 助学金类型名称 */
    private String typeName;
}
