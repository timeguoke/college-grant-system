package com.collegegrant.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.entity.Student;

public interface StudentService {
    /**
     *
     * 新增学生
     * @param student
     */
    void insert(Student student);

    /**
     * 查询学生详情
     * @param id
     * @return
     */
    Student  getStudentById(Long id);

    /**
     * 修改学生信息
     *
     * @param student
     */
    void update(Student student);

    /**
     * 删除学生
     * @param id
     */
    void deleteById(Long id);

    /**
     * 学生分页
     * @param page
     * @param size
     * @param keyword
     * @param college
     * @param isPoor
     * @return
     */
    Page<Student> list(int page, int size, String keyword, String college, Integer isPoor);

    /**
     * 查询学生总数
     * @return
     */
    Long countStudent();
}
