package com.collegegrant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.NoticeDTO;
import com.collegegrant.common.NoticeDetailVO;
import com.collegegrant.common.NoticeVO;

public interface PublicNoticeService {

    /**
     * 管理员分页查询公示列表
     */
    Page<NoticeVO> list(int page, int size, String keyword, Integer status);

    /**
     * 学生端分页查询已发布的公示列表
     */
    Page<NoticeVO> listPublished(int page, int size, String keyword);

    /**
     * 新增公示
     */
    Long add(NoticeDTO noticeDTO);

    /**
     * 公示详情
     */
    NoticeDetailVO detail(Long id);

    /**
     * 修改公示
     */
    void update(Long id, NoticeDTO noticeDTO);

    /**
     * 删除公示
     */
    void delete(Long id);

    /**
     * 发布公示
     */
    void publish(Long id);
}
