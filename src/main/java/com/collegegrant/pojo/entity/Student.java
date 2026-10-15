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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("student")
public class Student {
    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 学号 */
    private String studentNo;
    /** 姓名 */
    private String name;
    /** 性别：1男，2女 */
    private Integer gender;
    /** 学院 */
    private String college;
    /** 专业 */
    private String major;
    /** 班级 */
    private String className;
    /** 联系电话 */
    private String phone;
    /** 特殊困难类型：0无，1建档立卡，2低保家庭，3孤儿，4残疾 */
    private Integer specialType;
    /** 辅导员ID（关联teacher表） */
    private Long counselorId;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
