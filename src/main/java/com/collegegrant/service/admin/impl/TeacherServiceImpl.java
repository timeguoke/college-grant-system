package com.collegegrant.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.entity.SysUser;
import com.collegegrant.entity.Teacher;
import com.collegegrant.mapper.SysUserMapper;
import com.collegegrant.mapper.TeacherMapper;
import com.collegegrant.service.admin.TeacherService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 添加教师
     *
     * @param teacher
     */
    @Override
    @Transactional
    public void insert(Teacher teacher) {
        //先判断该教师是否已经存在
        String teacherNo = teacher.getTeacherNo();
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getTeacherNo, teacherNo);

        Teacher teacherOne = teacherMapper.selectOne(wrapper);
        if (teacherOne != null) {
            throw new RuntimeException("工号已存在:" + teacherNo);
        }


        //教师不存在进行新增
        teacherMapper.insert(teacher);
        //TODO需要增加一个新增教师新创建一个教师的账号
        SysUser sysUser = SysUser.builder()
                .status(1)     //默认状态为1
                .username(teacher.getTeacherNo())    //账号名称就是 教师号
                .password("e10adc3949ba59abbe56e057f20f883e")  //初始密码为123456
                .role(1)             //角色就是教师  1
                .userId(teacher.getId()) //userId在上面的新增时,MP已经自动创建了,所以这里能够取到
                .name(teacher.getName())  //教师姓名
                .createTime(LocalDateTime.now())  //创建时间
                .updateTime(LocalDateTime.now())  //修改时间
                .build();
        sysUserMapper.insert(sysUser);
    }

    /**
     * 查询教师详情
     *
     * @param id
     * @return
     */
    @Override
    public Teacher getTeacherById(Long id) {
        Teacher teacher = teacherMapper.selectById(id);
        return teacher;
    }

    /**
     * 修改教师信息
     *
     * @param teacher
     */
    @Override
    @Transactional
    public void update(Teacher teacher) {

        // 修改部分：先查出旧教师信息，判断是否存在
        Teacher oldTeacher = teacherMapper.selectById(teacher.getId());
        if (oldTeacher == null) {
            throw new RuntimeException("教师不存在，无法修改");
        }

        // 修改部分：如果工号变了，检查新工号是否与其他教师重复
        if (!oldTeacher.getTeacherNo().equals(teacher.getTeacherNo())) {
            LambdaQueryWrapper<Teacher> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(Teacher::getTeacherNo, teacher.getTeacherNo());
            checkWrapper.ne(Teacher::getId, teacher.getId()); // 排除自己
            if (teacherMapper.selectOne(checkWrapper) != null) {
                throw new RuntimeException("工号已存在：" + teacher.getTeacherNo());
            }
        }

        teacherMapper.updateById(teacher);

        // 修改部分：工号变了，同步更新 sys_user 的 username
        if (!oldTeacher.getTeacherNo().equals(teacher.getTeacherNo())) {
            LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SysUser::getUserId, teacher.getId());//where条件
            updateWrapper.eq(SysUser::getRole, 1);
            updateWrapper.set(SysUser::getUsername, teacher.getTeacherNo()); //set修改值
            sysUserMapper.update(null, updateWrapper);
        }


        //如果姓名变了，同步更新 sys_user.name
        if (teacher.getName() != null && !teacher.getName().equals(oldTeacher.getName())) {
            LambdaUpdateWrapper<SysUser> nameWrapper = new LambdaUpdateWrapper<>();
            nameWrapper.eq(SysUser::getUserId, teacher.getId())
                    .eq(SysUser::getRole, 1)
                    .set(SysUser::getName, teacher.getName());
            sysUserMapper.update(null, nameWrapper);
        }
        //学号未修改 账号也不进行修改
    }

    /**
     * 删除教师
     *
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        teacherMapper.deleteById(id);
        //TODO 删除教师,对账号进行注销
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserId, id);
        wrapper.eq(SysUser::getRole, 1);//角色为老师
        sysUserMapper.delete(wrapper);
    }

    /**
     * 教师分页
     *
     * @param page
     * @param size
     * @param keyword
     * @return
     */
    @Override
    public Page<Teacher> list(int page, int size, String keyword) {
        //页码参数
        Page<Teacher> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        //添加选项条件
        //keyword工号不为空时加入选项条件
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Teacher::getTeacherNo, keyword)
                    .or().like(Teacher::getName, keyword));
        }

        //按照创建时间的降序进行排序
        wrapper.orderByDesc(Teacher::getCreateTime);
        Page<Teacher> result = teacherMapper.selectPage(pageParam, wrapper);

        return result;
    }

    /**
     * 统计教师总数
     *
     * @return
     */
    @Override
    public Long countTeacher() {
        Long count = teacherMapper.selectCount(null);
        return count;
    }

}
