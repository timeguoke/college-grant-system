package com.collegegrant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.dto.TeacherExcelDTO;
import com.collegegrant.pojo.entity.SysUser;
import com.collegegrant.pojo.entity.Teacher;
import com.collegegrant.mapper.SysUserMapper;
import com.collegegrant.mapper.TeacherMapper;
import com.collegegrant.service.TeacherService;
import com.collegegrant.pojo.vo.ImportResultVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        // ===== 新增：参数非空校验 =====
        if (teacher.getTeacherNo() == null || teacher.getTeacherNo().trim().isEmpty()) {
            throw new RuntimeException("工号不能为空");
        }
        if (teacher.getName() == null || teacher.getName().trim().isEmpty()) {
            throw new RuntimeException("姓名不能为空");
        }
        // ===== 新增结束 =====

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
    public Page<Teacher> list(int page, int size, String keyword, String college) {
        //页码参数
        Page<Teacher> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        //keyword工号/姓名不为空时加入选项条件
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Teacher::getTeacherNo, keyword)
                    .or().like(Teacher::getName, keyword));
        }
        //college学院不为空时模糊匹配
        if (college != null && !college.isEmpty()) {
            wrapper.like(Teacher::getCollege, college);
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

    /**
     * 导出用：按筛选条件查询全部教师（不分页）
     */
    @Override
    public List<Teacher> listForExport(String keyword, String college) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Teacher::getTeacherNo, keyword)
                    .or().like(Teacher::getName, keyword));
        }
        if (college != null && !college.isEmpty()) {
            wrapper.like(Teacher::getCollege, college);
        }
        wrapper.orderByDesc(Teacher::getCreateTime);
        return teacherMapper.selectList(wrapper);
    }

    /**
     * 批量导入教师：逐行校验转换，失败行记录原因不中断，成功行复用 insert()（含账号创建）
     */
    @Override
    public ImportResultVO importTeachers(List<TeacherExcelDTO> rows) {
        int success = 0;
        List<String> failMessages = new ArrayList<>();
        int lineNo = 1; // Excel 行号：表头占第1行，数据从第2行开始
        for (TeacherExcelDTO row : rows) {
            lineNo++;
            // 整行为空直接跳过
            if (row == null || (isBlank(row.getTeacherNo()) && isBlank(row.getName()))) {
                continue;
            }
            try {
                insert(convertToTeacher(row));
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
     * Excel行 → 教师实体（文本映射：性别 男=0 女=1；职称 助教=0 讲师=1 副教授=2 教授=3）
     */
    private Teacher convertToTeacher(TeacherExcelDTO row) {
        if (isBlank(row.getTeacherNo())) {
            throw new RuntimeException("工号不能为空");
        }
        if (isBlank(row.getName())) {
            throw new RuntimeException("姓名不能为空");
        }
        Teacher t = new Teacher();
        t.setTeacherNo(row.getTeacherNo().trim());
        t.setName(row.getName().trim());
        if ("男".equals(row.getGender())) {
            t.setGender(0);
        } else if ("女".equals(row.getGender())) {
            t.setGender(1);
        } else {
            t.setGender(0); // 缺省按男
        }
        t.setCollege(row.getCollege());
        if (!isBlank(row.getTitle())) {
            switch (row.getTitle().trim()) {
                case "助教":   t.setTitle(0); break;
                case "讲师":   t.setTitle(1); break;
                case "副教授": t.setTitle(2); break;
                case "教授":   t.setTitle(3); break;
                default:       t.setTitle(0);
            }
        } else {
            t.setTitle(0);
        }
        t.setPhone(row.getPhone());
        return t;
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

}
