package com.collegegrant.common;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 公示详情返回值
 */
@Data
public class NoticeDetailVO {
    /** 公示ID */
    private Long id;
    /** 公示标题 */
    private String title;
    /** 公示内容 */
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
    /** 更新时间 */
    private LocalDateTime updateTime;
    /** 助学金类型ID */
    private Long typeId;
    /** 助学金类型名称 */
    private String typeName;
    /** 受助学生列表 */
    private List<NoticeStudentVO> students;

    /**
     * 公示中的受助学生信息
     */
    @Data
    public static class NoticeStudentVO {
        /** 申请ID */
        private Long applicationId;
        /** 学生ID */
        private Long studentId;
        /** 学生姓名 */
        private String studentName;
        /** 学生学号 */
        private String studentNo;
        /** 等级名称 */
        private String levelName;
        /** 资助金额 */
        private BigDecimal grantAmount;
    }
}
