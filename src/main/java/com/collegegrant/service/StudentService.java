package com.collegegrant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.dto.StudentExcelDTO;
import com.collegegrant.pojo.entity.Student;
import com.collegegrant.pojo.vo.ImportResultVO;

import java.util.List;

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
     * 按学号查询学生（学生端个人资料用）
     * @param studentNo 学号
     * @return
     */
    Student getStudentByNo(String studentNo);

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
     * @param keyword 学号/姓名
     * @param college 学院
     * @param className 班级（模糊匹配）
     * @param specialType 特殊困难类型
     * @return
     */
    Page<Student> list(int page, int size, String keyword, String college, String className, Integer specialType);

    /**
     * 学院下拉列表（去重）
     * @return
     */
    List<String> getColleges();

    /**
     * 班级下拉列表（去重，可按学院过滤，供级联）
     * @param college 学院（可为空）
     * @return
     */
    List<String> getClasses(String college);

    /**
     * 查询学生总数
     * @return
     */
    Long countStudent();

    /**
     * 导出用：按筛选条件查询全部学生（不分页）
     * @param keyword 学号/姓名
     * @param college 学院
     * @param specialType 特殊困难类型
     * @return
     */
    List<Student> listForExport(String keyword, String college, Integer specialType);

    /**
     * 批量导入学生（逐行校验，失败的行记录原因，成功的行正常入库并创建账号）
     * @param rows Excel 解析出的数据行
     * @return 导入结果（总数/成功数/失败数/失败明细）
     */
    ImportResultVO importStudents(List<StudentExcelDTO> rows);
}
