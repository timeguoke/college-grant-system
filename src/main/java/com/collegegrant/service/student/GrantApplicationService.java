package com.collegegrant.service.student;

import com.collegegrant.common.ApplicationDTO;

import javax.servlet.http.HttpServletRequest;

public interface GrantApplicationService {
    /**
     * 提交申请
     * @param applicationDTO
     * @param studentId
     */
    void submitApplication(ApplicationDTO applicationDTO, Long studentId);

}
