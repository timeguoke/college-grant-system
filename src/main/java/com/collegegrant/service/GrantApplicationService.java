package com.collegegrant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.dto.ApplicationDTO;
import com.collegegrant.pojo.vo.ApplicationVO;

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
     * 申请详情
     * @param id
     * @param studentNO
     * @return
     */
    ApplicationVO getApplicationDetail(Long id,String studentNO);

    /**
     * 申请列表
     * @param page
     * @param size
     * @param keyword
     * @param status
     * @param typeId
     * @return
     */
    Page<ApplicationVO> list(int page, int size, String keyword, Integer status, Long typeId, Integer specialType, Long counselorId);

    /**
     * 通过申请 /驳回申请
     * @param id
     * @param reviewComment
     * @param status
     */
    void updateStatus(Long id, String reviewComment,Integer status);

}
