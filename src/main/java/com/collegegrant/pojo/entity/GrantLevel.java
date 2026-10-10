package com.collegegrant.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 助学金等级表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("grant_level")
public class GrantLevel {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属助学金类型ID */
    private Long typeId;

    /** 等级名称，如一等、二等、获奖 */
    private String levelName;

    /** 金额（元） */
    private BigDecimal amount;

    /** 最低分数线 */
    private BigDecimal minScore;

    /** 排序号，越小等级越高 */
    private Integer sort;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}