package com.collegegrant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.entity.Student;
import com.collegegrant.entity.SysUser;
import com.collegegrant.mapper.StudentMapper;
import com.collegegrant.mapper.SysUserMapper;
import com.collegegrant.service.StudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


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
        //先判断该学生是否已经存在
        String studentNo = student.getStudentNo();
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentNo, studentNo);

        Student studentOne = studentMapper.selectOne(wrapper);
        if (studentOne != null) {
            throw new RuntimeException("学号已存在:" + studentNo);
        }

        // 自动计算人均年收入 = 家庭年收入 / 家庭人口数
        if (student.getFamilyIncome() != null && student.getFamilyMembers() != null && student.getFamilyMembers() > 0) {
            student.setPerCapitaIncome(
                    student.getFamilyIncome().divide(
                            new java.math.BigDecimal(student.getFamilyMembers()),
                            2, //保留两位小数
                            java.math.BigDecimal.ROUND_HALF_UP //运算规则四舍五入
                    )
            );
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
     * 修改学生信息
     *
     * @param student
     */
    @Override
    @Transactional
    public void update(Student student) {

        // 自动计算人均年收入 = 家庭年收入 / 家庭人口数
        if (student.getFamilyIncome() != null && student.getFamilyMembers() != null && student.getFamilyMembers() > 0) {
            student.setPerCapitaIncome(
                    student.getFamilyIncome().divide(
                            new java.math.BigDecimal(student.getFamilyMembers()),
                            2, //保留两位小数
                            java.math.BigDecimal.ROUND_HALF_UP //运算规则四舍五入
                    )
            );
        }

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
    public Page<Student> list(int page, int size, String keyword, String college, Integer specialType) {
        //页码参数
        Page<Student> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        //添加选项条件
        //keyword学号姓名不为空时加入选项条件
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Student::getStudentNo, keyword)
                    .or().like(Student::getName, keyword));
        }
        //college学院不为空时加入选项条件
        if (college != null && !college.isEmpty()) {
            wrapper.eq(Student::getCollege, college);
        }
        // specialType不为空时，按特殊困难类型筛选
        if (specialType != null) {
            wrapper.eq(Student::getSpecialType, specialType);
        }
        //按照创建时间的降序进行排序
        wrapper.orderByDesc(Student::getCreateTime);
        Page<Student> result = studentMapper.selectPage(pageParam, wrapper);

        return result;
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

}
