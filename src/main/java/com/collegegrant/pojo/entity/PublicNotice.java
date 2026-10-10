package com.collegegrant.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 公示表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("public_notice")
public class PublicNotice {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 公示标题 */
    private String title;

    /** 公示内容 */
    private String content;

    /** 助学金类型ID */
    private Long typeId;

    /** 公示开始时间 */
    private LocalDateTime startTime;

    /** 公示结束时间 */
    private LocalDateTime endTime;

    /** 状态：0未发布，1公示中，2已结束 */
    private Integer status;

    /** 发布人ID */
    private Long publisherId;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
