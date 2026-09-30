package com.collegegrant.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.entity.Teacher;


public interface TeacherService {
    /**
     *
     * 新增教师
     * @param teacher
     */
    void insert(Teacher teacher);

    /**
     * 查询教师详情
     * @param id
     * @return
     */
    Teacher  getTeacherById(Long id);

    /**
     * 修改教师信息
     *
     * @param teacher
     */
    void update(Teacher teacher);

    /**
     * 删除教师
     * @param id
     */
    void deleteById(Long id);

    /**
     * 教师分页
     * @param page
     * @param size
     * @param keyword
     * @return
     */
    Page<Teacher> list(int page, int size, String keyword );

    /**
     * 查询教师总数
     * @return
     */
    Long countTeacher();
}
