package com.collegegrant.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教师端"我的学生"返回VO
 * 按学生聚合，附带申请统计（申请次数、最新申请状态）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherStudentVO {

    /** 学生ID */
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

    /** 特殊困难类型：0无，1建档立卡，2低保家庭，3孤儿，4残疾 */
    private Integer specialType;

    /** 申请次数 */
    private Integer applyCount;

    /** 最新一条申请的状态：0待审核，1通过，2驳回，3已评分，4已评定，5已公示（无申请时为null） */
    private Integer latestStatus;
}
