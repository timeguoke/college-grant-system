package com.collegegrant.entity;

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
    /** 性别：0男，1女 */
    private Integer gender;
    /** 学院 */
    private String college;
    /** 专业 */
    private String major;
    /** 班级 */
    private String className;
    /** 联系电话 */
    private String phone;
    /** 家庭年收入（元） */
    private BigDecimal familyIncome;
    /** 家庭人口数 */
    private Integer familyMembers;
    /** 人均年收入（元，自动计算） */
    private BigDecimal perCapitaIncome;
    /** 是否建档立卡：0否，1是 */
    private Integer isPoor;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}
