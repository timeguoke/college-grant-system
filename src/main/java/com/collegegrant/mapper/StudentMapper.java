package com.collegegrant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.entity.Student;
import com.collegegrant.pojo.vo.TeacherStudentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 教师端"我的学生"分页列表（按学生聚合，附带申请次数和最新申请状态）
     *
     * @param page        分页参数
     * @param keyword     学号/姓名关键词
     * @param counselorId 辅导员ID（权限隔离：只看自己名下的学生）
     * @return 学生分页列表
     */
    Page<TeacherStudentVO> selectTeacherStudentPage(Page<TeacherStudentVO> page,
                                                    @Param("keyword") String keyword,
                                                    @Param("counselorId") Long counselorId);
}
