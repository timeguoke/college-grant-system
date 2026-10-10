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
 * 教师信息表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("teacher")
public class Teacher {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 工号 */
    private String teacherNo;

    /** 姓名 */
    private String name;

    /** 性别：0男，1女 */
    private Integer gender;

    /** 所在学院 */
    private String college;

    /** 联系电话 */
    private String phone;

    /** 职称：0助教，1讲师，2副教授，3教授 */
    private Integer title;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
