package com.collegegrant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.dto.TeacherExcelDTO;
import com.collegegrant.pojo.entity.Teacher;
import com.collegegrant.pojo.vo.ImportResultVO;

import java.util.List;


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
    Page<Teacher> list(int page, int size, String keyword, String college);

    /**
     * 查询教师总数
     * @return
     */
    Long countTeacher();

    /**
     * 导出用：按筛选条件查询全部教师（不分页）
     * @param keyword 工号/姓名
     * @param college 院系
     * @return
     */
    List<Teacher> listForExport(String keyword, String college);

    /**
     * 批量导入教师（逐行校验，失败行记录原因，成功行复用 insert 自动创建账号）
     * @param rows Excel 解析出的数据行
     * @return 导入结果
     */
    ImportResultVO importTeachers(List<TeacherExcelDTO> rows);
}
