package com.collegegrant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.dto.StudentExcelDTO;
import com.collegegrant.pojo.entity.Student;
import com.collegegrant.pojo.entity.SysUser;
import com.collegegrant.mapper.StudentMapper;
import com.collegegrant.mapper.SysUserMapper;
import com.collegegrant.service.StudentService;
import com.collegegrant.pojo.vo.ImportResultVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 添加学生
     *
     * @param student
     */
    @Override
    @Transactional
    public void insert(Student student) {
        // ===== 新增：参数非空校验 =====
        if (student.getStudentNo() == null || student.getStudentNo().trim().isEmpty()) {
            throw new RuntimeException("学号不能为空");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new RuntimeException("姓名不能为空");
        }
        // ===== 新增结束 =====

        //先判断该学生是否已经存在
        String studentNo = student.getStudentNo();
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentNo, studentNo);

        Student studentOne = studentMapper.selectOne(wrapper);
        if (studentOne != null) {
            throw new RuntimeException("学号已存在:" + studentNo);
        }


        //学生不存在进行添加
        studentMapper.insert(student);
        System.out.println("添加之后后去学生id=="+student.getId());
        //TODO需要增加一个新增学生新创建一个学生的账号
        SysUser sysUser = SysUser.builder()
                .status(1)     //默认状态为1
                .username(student.getStudentNo())    //账号名称就是学号
                .password("e10adc3949ba59abbe56e057f20f883e")  //初始密码为123456
                .role(2)             //角色就是学生  2
                .userId(student.getId()) //userId在上面的新增时,mybatis-plus已经自动创建了,所以这里能够取到
                .name(student.getName())  //学生姓名
                .createTime(LocalDateTime.now())  //创建时间
                .updateTime(LocalDateTime.now())  //修改时间
                .build();
        sysUserMapper.insert(sysUser);
    }

    /**
     * 查询学生详情
     *
     * @param id
     * @return
     */
    @Override
    public Student getStudentById(Long id) {
        Student student = studentMapper.selectById(id);
        return student;
    }

    /**
     * 按学号查询学生（学生端个人资料用）
     *
     * @param studentNo 学号
     */
    @Override
    public Student getStudentByNo(String studentNo) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentNo, studentNo);
        return studentMapper.selectOne(wrapper);
    }

    /**
     * 修改学生信息
     *
     * @param student
     */
    @Override
    @Transactional
    public void update(Student student) {

        // 修改部分：先查出旧学生信息，判断是否存在
        Student oldStudent = studentMapper.selectById(student.getId());
        if (oldStudent == null) {
            throw new RuntimeException("学生不存在，无法修改");
        }

        // 修改部分：如果学号变了，检查新学号是否与其他学生重复
        if (!oldStudent.getStudentNo().equals(student.getStudentNo())) {
            LambdaQueryWrapper<Student> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(Student::getStudentNo, student.getStudentNo());
            checkWrapper.ne(Student::getId, student.getId()); // 排除自己
            if (studentMapper.selectOne(checkWrapper) != null) {
                throw new RuntimeException("学号已存在：" + student.getStudentNo());
            }
        }

        studentMapper.updateById(student);

        // 修改部分：学号变了，同步更新 sys_user 的 username
        if (!oldStudent.getStudentNo().equals(student.getStudentNo())) {
            LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SysUser::getUserId, student.getId());//where条件
            updateWrapper.eq(SysUser::getRole, 2);
            updateWrapper.set(SysUser::getUsername, student.getStudentNo()); //set修改值
            sysUserMapper.update(null, updateWrapper);
        }


        //如果姓名变了，同步更新 sys_user.name
        if (student.getName() != null && !student.getName().equals(oldStudent.getName())) {
            LambdaUpdateWrapper<SysUser> nameWrapper = new LambdaUpdateWrapper<>();
            nameWrapper.eq(SysUser::getUserId, student.getId())
                    .eq(SysUser::getRole, 2)
                    .set(SysUser::getName, student.getName());
            sysUserMapper.update(null, nameWrapper);
        }
        //学号未修改 账号也不进行修改
    }

    /**
     * 删除学生
     *
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        studentMapper.deleteById(id);
        //TODO 删除学生,对账号进行注销
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserId, id);
        wrapper.eq(SysUser::getRole, 2);//角色为学生
        sysUserMapper.delete(wrapper);

    }

    /**
     * 学生分页
     *
     * @param page
     * @param size
     * @param keyword
     * @param college
     * @param specialType  特殊困难类型：0无，1建档立卡，2低保家庭，3孤儿，4残疾
     * @return
     */
    @Override
    public Page<Student> list(int page, int size, String keyword, String college, String className, Integer specialType) {
        //页码参数
        Page<Student> pageParam = new Page<>(page, size);
        Page<Student> result = studentMapper.selectPage(pageParam, buildListWrapper(keyword, college, className, specialType));

        return result;
    }

    /**
     * 分页/导出共用的查询条件构建
     */
    private LambdaQueryWrapper<Student> buildListWrapper(String keyword, String college, String className, Integer specialType) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        //keyword学号姓名不为空时加入选项条件
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Student::getStudentNo, keyword)
                    .or().like(Student::getName, keyword));
        }
        //college学院不为空时加入选项条件
        if (college != null && !college.isEmpty()) {
            wrapper.eq(Student::getCollege, college);
        }
        //className班级不为空时模糊匹配
        if (className != null && !className.isEmpty()) {
            wrapper.like(Student::getClassName, className);
        }
        // specialType不为空时，按特殊困难类型筛选
        if (specialType != null) {
            wrapper.eq(Student::getSpecialType, specialType);
        }
        //按照创建时间的降序进行排序
        wrapper.orderByDesc(Student::getCreateTime);
        return wrapper;
    }

    /**
     * 学院下拉列表（去重、去除空值）
     */
    @Override
    public List<String> getColleges() {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT college")
                .isNotNull("college")
                .ne("college", "")
                .orderByAsc("college");
        return studentMapper.selectObjs(wrapper).stream()
                .filter(java.util.Objects::nonNull)
                .map(String::valueOf)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 班级下拉列表（去重、可按学院过滤，供级联）
     */
    @Override
    public List<String> getClasses(String college) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT class_name")
                .isNotNull("class_name")
                .ne("class_name", "");
        if (college != null && !college.isEmpty()) {
            wrapper.eq("college", college);
        }
        wrapper.orderByAsc("class_name");
        return studentMapper.selectObjs(wrapper).stream()
                .filter(java.util.Objects::nonNull)
                .map(String::valueOf)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 统计学生总数
     *
     * @return
     */
    @Override
    public Long countStudent() {
        Long count = studentMapper.selectCount(null);
        return count;
    }

    /**
     * 导出用：按筛选条件查询全部学生（不分页）
     */
    @Override
    public List<Student> listForExport(String keyword, String college, Integer specialType) {
        return studentMapper.selectList(buildListWrapper(keyword, college, null, specialType));
    }

    /**
     * 批量导入学生：逐行转换校验，失败的行记录原因不中断，成功的行复用 insert()（含账号创建）
     */
    @Override
    public ImportResultVO importStudents(List<StudentExcelDTO> rows) {
        int success = 0;
        List<String> failMessages = new ArrayList<>();
        int lineNo = 1; // Excel行号：表头占第1行，数据从第2行开始
        for (StudentExcelDTO row : rows) {
            lineNo++;
            // 整行为空的数据直接跳过
            if (row == null || (isBlank(row.getStudentNo()) && isBlank(row.getName()))) {
                continue;
            }
            try {
                insert(convertToStudent(row));
                success++;
            } catch (Exception e) {
                failMessages.add("第" + lineNo + "行：" + e.getMessage());
            }
        }
        return ImportResultVO.builder()
                .total(rows.size())
                .successCount(success)
                .failCount(failMessages.size())
                .failMessages(failMessages)
                .build();
    }

    /**
     * Excel行 → 学生实体（文本映射：性别 男=1 女=2；特殊类型 普通0 建档立卡1 低保2 孤儿3 残疾4）
     */
    private Student convertToStudent(StudentExcelDTO row) {
        if (isBlank(row.getStudentNo())) {
            throw new RuntimeException("学号不能为空");
        }
        if (isBlank(row.getName())) {
            throw new RuntimeException("姓名不能为空");
        }
        Student s = new Student();
        s.setStudentNo(row.getStudentNo().trim());
        s.setName(row.getName().trim());
        if ("男".equals(row.getGender())) {
            s.setGender(1);
        } else if ("女".equals(row.getGender())) {
            s.setGender(2);
        } else {
            s.setGender(1); // 缺省按男处理
        }
        s.setCollege(row.getCollege());
        s.setMajor(row.getMajor());
        s.setClassName(row.getClassName());
        s.setPhone(row.getPhone());
        if (!isBlank(row.getSpecialType())) {
            switch (row.getSpecialType().trim()) {
                case "建档立卡": s.setSpecialType(1); break;
                case "低保家庭":
                case "低保":     s.setSpecialType(2); break;
                case "孤儿":     s.setSpecialType(3); break;
                case "残疾":     s.setSpecialType(4); break;
                default:        s.setSpecialType(0);
            }
        } else {
            s.setSpecialType(0);
        }
        return s;
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

}
