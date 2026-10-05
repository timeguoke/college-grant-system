package com.collegegrant.service.student;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.ApplicationDTO;
import com.collegegrant.common.ApplicationVO;
import com.collegegrant.entity.Student;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface GrantApplicationService {
    /**
     * 提交申请
     * @param applicationDTO
     * @param studentId
     */
    void submitApplication(ApplicationDTO applicationDTO, Long studentId);

    /**
     * 我的申请列表展示
     * @param page
     * @param size
     * @return
     */
    Page<ApplicationVO> myApplicationList(int page, int size);

    /**
     *
     * 我的申请详情
     * @param id
     * @return
     */
    ApplicationVO getApplicationDetail(Long id);
}
