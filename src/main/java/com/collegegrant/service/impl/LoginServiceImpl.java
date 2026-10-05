package com.collegegrant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.collegegrant.common.LoginDTO;
import com.collegegrant.common.LoginVO;
import com.collegegrant.entity.Student;
import com.collegegrant.entity.SysUser;
import com.collegegrant.entity.Teacher;
import com.collegegrant.mapper.StudentMapper;
import com.collegegrant.mapper.SysUserMapper;
import com.collegegrant.mapper.TeacherMapper;
import com.collegegrant.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SysUserMapper  sysUserMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private StudentMapper studentMapper;

    /**
     * 用户登录
     *
     * @param loginDTO
     * @return
     */
    @Override
    @Transactional
    public LoginVO login(LoginDTO loginDTO) {
        //获取用户名和密码
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();




        //先查询该用户名是否存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser sysUser = sysUserMapper.selectOne(wrapper);

        if (sysUser == null) {
            throw new RuntimeException("该用户名不存在");

        }
        //查看账号的状态是什么
        if(sysUser.getStatus()==0){
            throw new RuntimeException("账号禁用中...");
        }

        //用户名存在,判断用户密码是否正确

        //md5加密前的密码
        System.out.println("加密前的password = " + password);
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println("加密后的password = " + password);
        //进行密码比较
        if (!password.equals(sysUser.getPassword())) {
            //密码错误
            throw new RuntimeException("密码错误,请重新输入");
        }

        //密码正确,返回前端数据

        //通过role进行分类操作
        Integer role = sysUser.getRole();
        if (role == null) {
            throw new RuntimeException("用户状态异常");
        }
        String name = null;
        if (role == 0) {
            //管理员
            name = "管理员";

        } else if (role == 1) {
            //教师
            Teacher teacher = teacherMapper.selectById(sysUser.getUserId());
            if (teacher == null) {
                throw new RuntimeException("教师信息不存在");
            }
            name = teacher.getName();
        } else if (role == 2) {
            //学生
            Student student = studentMapper.selectById(sysUser.getUserId());
            if (student == null) {
                throw new RuntimeException("学生信息不存在");
            }
            name = student.getName();
        } else {
            throw new RuntimeException("未知用户角色");
        }


        // admin的user_id为null，默认设为0
        Long userId = sysUser.getUserId() != null ? sysUser.getUserId() : 0L;

        return LoginVO.builder()
                .username(sysUser.getUsername())
                .role(sysUser.getRole())
                .name(name)
                .userId(userId)
                .build();
    }
}
