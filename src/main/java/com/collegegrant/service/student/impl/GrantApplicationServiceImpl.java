package com.collegegrant.service.student.impl;

import com.collegegrant.common.ApplicationDTO;
import com.collegegrant.entity.GrantApplication;
import com.collegegrant.mapper.GrantApplicationMapper;
import com.collegegrant.service.student.GrantApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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
}
