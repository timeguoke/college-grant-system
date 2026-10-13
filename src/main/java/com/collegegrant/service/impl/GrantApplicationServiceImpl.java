package com.collegegrant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.dto.ApplicationDTO;
import com.collegegrant.pojo.vo.ApplicationVO;
import com.collegegrant.common.context.UserContext;
import com.collegegrant.pojo.entity.GrantApplication;
import com.collegegrant.pojo.entity.Student;
import com.collegegrant.mapper.GrantApplicationMapper;
import com.collegegrant.mapper.StudentMapper;
import com.collegegrant.service.GrantApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;


@Service
public class GrantApplicationServiceImpl implements GrantApplicationService {

    @Autowired
    private GrantApplicationMapper grantApplicationMapper;
    @Autowired
    private StudentMapper studentMapper;

    /**
     * 提交申请
     *
     * @param applicationDTO
     * @param studentId
     */
    @Override
    public void submitApplication(ApplicationDTO applicationDTO, Long studentId) {
        // ===== 新增：参数非空校验 =====
        if (applicationDTO.getTypeId() == null) {
            throw new RuntimeException("助学金类型不能为空");
        }
        if (applicationDTO.getApplyReason() == null || applicationDTO.getApplyReason().trim().isEmpty()) {
            throw new RuntimeException("申请理由不能为空");
        }
        // ===== 新增结束 =====

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

        // 特殊困难学生必须上传证明材料
        if (applicationDTO.getSpecialType() != null && applicationDTO.getSpecialType() > 0) {
            if (applicationDTO.getSpecialProof() == null || applicationDTO.getSpecialProof().trim().isEmpty()) {
                throw new RuntimeException("特殊困难学生必须上传证明材料");
            }
        }

        // 家庭经济信息校验与人均收入计算（随本次申请记录保存，历史申请互不影响）
        if (applicationDTO.getFamilyIncome() == null || applicationDTO.getFamilyMembers() == null
                || applicationDTO.getFamilyMembers() <= 0) {
            throw new RuntimeException("请填写家庭年收入和家庭人口数");
        }

        GrantApplication grantApplication = GrantApplication.builder()
                .createTime(LocalDateTime.now())
                .status(0)
                .studentId(studentId)
                .build();

        //数据拷贝拷贝
        BeanUtils.copyProperties(applicationDTO, grantApplication);
        // 自动计算人均年收入 = 家庭年收入 / 家庭人口数
        grantApplication.setPerCapitaIncome(
                applicationDTO.getFamilyIncome().divide(
                        new java.math.BigDecimal(applicationDTO.getFamilyMembers()),
                        2, java.math.BigDecimal.ROUND_HALF_UP));
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
        Long studentId = UserContext.getUserId();
        Page<ApplicationVO> pageParam = new Page<>(page, size);

        Page<ApplicationVO> voPage = grantApplicationMapper.selectApplicationPage(pageParam, studentId, null, null, null, null);
        //填充状态码文字
        for (ApplicationVO VO : voPage.getRecords()) {
            VO.setStatusText(getStatusText(VO.getStatus()));
        }

        return voPage;

    }

    /**
     * 状态码转文字
     */
    private   String getStatusText(Integer status) {
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
     * 申请详情
     * @param id
     * @param studentNO
     * @return
     */

    @Override
    public ApplicationVO getApplicationDetail(Long id, String studentNO) {
        ApplicationVO applicationVO = grantApplicationMapper.selectDetailById(id);
        //判断申请是否存在
        if (applicationVO == null) {
            throw new RuntimeException("申请不存在");
        }
        //判断是否是该账号的申请
        //账号学号                 申请人的学号
        // 如果传了 studentNO（学生端调用），校验是否是本人的申请
        if (studentNO != null) {
            if (!studentNO.equals(applicationVO.getStudentNo())) {
                throw new RuntimeException("无权查看他人申请");
            }
        }

        //设置状态码文字
        applicationVO.setStatusText(getStatusText(applicationVO.getStatus()));
        return applicationVO;
    }


    /**
     * 申请列表
     *
     * @param page
     * @param size
     * @param keyword
     * @param status
     * @param typeId
     * @return
     */
    @Override
    public Page<ApplicationVO> list(int page, int size, String keyword, Integer status, Long typeId, Integer specialType) {

        Page<ApplicationVO> pageParam = new Page<>(page, size);

        Page<ApplicationVO> voPage = grantApplicationMapper
                .selectApplicationPage(pageParam, null, status, typeId, keyword, specialType);
        //填充状态码文字
        for (ApplicationVO VO : voPage.getRecords()) {
            VO.setStatusText(getStatusText(VO.getStatus()));
        }

        return voPage;
    }


    /**
     * 通过申请 /驳回申请
     *
     * @param id
     * @param reviewComment
     */
    @Override
    @Transactional
    public void updateStatus(Long id, String reviewComment, Integer status) {
        //只能对待申请的进行操作
        GrantApplication grantApplication = grantApplicationMapper.selectById(id);
        //判断申请是否存在
        if (grantApplication == null) {
            throw new RuntimeException("申请不存在");
        }
        //先判断状态
        if (grantApplication.getStatus() != 0) {
            throw new RuntimeException("申请状态错误");
        }


        //进行状态修改
        LambdaUpdateWrapper<GrantApplication> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(GrantApplication::getReviewComment, reviewComment);
        updateWrapper.set(GrantApplication::getReviewerId, UserContext.getUserId());//审核人
        updateWrapper.set(GrantApplication::getReviewTime, LocalDateTime.now());//审核时间
        updateWrapper.set(GrantApplication::getStatus, status);//进行状态修改,通过为1,驳回为2
        updateWrapper.eq(GrantApplication::getId, id);
        updateWrapper.eq(GrantApplication::getStatus, 0); // 只更新状态为0的

        int rows = grantApplicationMapper.update(null, updateWrapper);
        //防止并发问题
        if (rows == 0) {
            throw new RuntimeException("申请状态已变更，请刷新后重试");
        }

        // 教师审核通过时，同步更新学生表的specialType
        if (status == 1) {
            LambdaUpdateWrapper<Student> studentWrapper = new LambdaUpdateWrapper<>();
            studentWrapper.eq(Student::getId, grantApplication.getStudentId());
            studentWrapper.set(Student::getSpecialType,
                    grantApplication.getSpecialType() != null ? grantApplication.getSpecialType() : 0);
            studentMapper.update(null, studentWrapper);
        }

    }
}
