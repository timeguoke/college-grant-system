package com.collegegrant.service.student.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.ApplicationDTO;
import com.collegegrant.common.ApplicationVO;
import com.collegegrant.common.UserContext;
import com.collegegrant.entity.GrantApplication;
import com.collegegrant.mapper.GrantApplicationMapper;
import com.collegegrant.mapper.GrantTypeMapper;
import com.collegegrant.mapper.StudentMapper;
import com.collegegrant.mapper.SysUserMapper;
import com.collegegrant.service.student.GrantApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;


@Service
public class GrantApplicationServiceImpl implements GrantApplicationService {

    @Autowired
    private GrantApplicationMapper grantApplicationMapper;

    /**
     * 提交申请
     *
     * @param applicationDTO
     * @param studentId
     */
    @Override
    public void submitApplication(ApplicationDTO applicationDTO, Long studentId) {
        // 修改部分：重复申请校验
        // 校验：同一学生对同一助学金类型，是否已有待审核或通过状态的申请
        LambdaQueryWrapper<GrantApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GrantApplication::getStudentId, studentId)
                .eq(GrantApplication::getTypeId, applicationDTO.getTypeId())
                .in(GrantApplication::getStatus, 0, 1);  // 0待审核 或 1通过，都不允许重复提交
        Long count = grantApplicationMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new RuntimeException("该助学金类型您已提交过申请，请勿重复提交");
        }

        GrantApplication grantApplication = GrantApplication.builder()
                .createTime(LocalDateTime.now())
                .status(0)
                .studentId(studentId)
                .build();

        //数据拷贝拷贝
        BeanUtils.copyProperties(applicationDTO, grantApplication);
        //提交申请
        grantApplicationMapper.insert(grantApplication);


    }

    /**
     * 我的申请列表展示
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<ApplicationVO> myApplicationList(int page, int size) {
        //先取出登录者的学号
        Long student = UserContext.getUserId();
        Page<ApplicationVO> pageParam = new Page<>(page, size);

        Page<ApplicationVO> voPage = grantApplicationMapper.selectMyApplicationPage(pageParam, student);
        //填充状态码文字
        for (ApplicationVO VO : voPage.getRecords()) {
            VO.setStatusText(getStatusText(VO.getStatus()));
        }

        return voPage;

    }

    /**
     * 状态码转文字
     */
    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0:
                return "待审核";
            case 1:
                return "通过";
            case 2:
                return "驳回";
            case 3:
                return "已评分";
            case 4:
                return "已评定";
            case 5:
                return "已公示";
            default:
                return "未知";
        }

    }

    /**
     *
     * 我的申请详情
     *
     * @param id
     * @return
     */
    @Override
    public ApplicationVO getApplicationDetail(Long id) {
        ApplicationVO applicationVO = grantApplicationMapper.selectDetailById(id);
        //判断申请是否存在
        if (applicationVO == null) {
            throw new RuntimeException("申请不存在");
        }
        //判断是否是该账号的申请
        //账号学号                 申请人的学号
        if (!UserContext.getUserId().equals(applicationVO.getStudentId())) {
            throw new RuntimeException("无权查看他人申请");
        }

        //设置状态码文字
        applicationVO.setStatusText(getStatusText(applicationVO.getStatus()));
        return applicationVO;
    }
}
