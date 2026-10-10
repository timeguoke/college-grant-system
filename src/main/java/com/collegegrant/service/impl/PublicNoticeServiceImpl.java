package com.collegegrant.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.dto.NoticeDTO;
import com.collegegrant.pojo.vo.NoticeDetailVO;
import com.collegegrant.pojo.vo.NoticeVO;
import com.collegegrant.common.context.UserContext;
import com.collegegrant.pojo.entity.GrantApplication;
import com.collegegrant.pojo.entity.PublicNotice;
import com.collegegrant.mapper.GrantApplicationMapper;
import com.collegegrant.mapper.PublicNoticeMapper;
import com.collegegrant.service.PublicNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PublicNoticeServiceImpl implements PublicNoticeService {

    @Autowired
    private PublicNoticeMapper publicNoticeMapper;
    @Autowired
    private GrantApplicationMapper grantApplicationMapper;

    // ============================================================
    // 一、查询类
    // ============================================================

    @Override
    public Page<NoticeVO> list(int page, int size, String keyword, Integer status) {
        Page<NoticeVO> pageParam = new Page<>(page, size);
        return publicNoticeMapper.selectNoticePage(pageParam, keyword, status);
    }

    @Override
    public Page<NoticeVO> listPublished(int page, int size, String keyword) {
        Page<NoticeVO> pageParam = new Page<>(page, size);
        return publicNoticeMapper.selectPublishedNoticePage(pageParam, keyword);
    }

    @Override
    public NoticeDetailVO detail(Long id) {
        NoticeDetailVO detail = publicNoticeMapper.selectNoticeDetail(id);
        if (detail == null) {
            return null;
        }
        // 按typeId查询评上的受助学生列表
        List<NoticeDetailVO.NoticeStudentVO> students = publicNoticeMapper.selectNoticeStudents(detail.getTypeId());
        detail.setStudents(students);
        return detail;
    }

    // ============================================================
    // 二、管理类
    // ============================================================

    @Override
    @Transactional
    public Long add(NoticeDTO noticeDTO) {
        PublicNotice notice = PublicNotice.builder()
                .title(noticeDTO.getTitle())
                .content(noticeDTO.getContent())
                .typeId(noticeDTO.getTypeId())
                .status(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        publicNoticeMapper.insert(notice);
        return notice.getId();
    }

    @Override
    @Transactional
    public void update(Long id, NoticeDTO noticeDTO) {
        PublicNotice notice = publicNoticeMapper.selectById(id);
        if (notice == null) {
            throw new RuntimeException("公示不存在");
        }
        if (notice.getStatus() != 0) {
            throw new RuntimeException("已发布的公示不能修改");
        }
        LambdaUpdateWrapper<PublicNotice> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PublicNotice::getId, id)
                .set(PublicNotice::getTitle, noticeDTO.getTitle())
                .set(PublicNotice::getContent, noticeDTO.getContent())
                .set(PublicNotice::getTypeId, noticeDTO.getTypeId())
                .set(PublicNotice::getUpdateTime, LocalDateTime.now());
        publicNoticeMapper.update(null, wrapper);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PublicNotice notice = publicNoticeMapper.selectById(id);
        if (notice == null) {
            throw new RuntimeException("公示不存在");
        }
        if (notice.getStatus() != 0) {
            throw new RuntimeException("已发布的公示不能删除");
        }
        publicNoticeMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void publish(Long id) {
        // 1. 校验公示存在
        PublicNotice notice = publicNoticeMapper.selectById(id);
        if (notice == null) {
            throw new RuntimeException("公示不存在");
        }
        if (notice.getStatus() != 0) {
            throw new RuntimeException("该公示已发布，不能重复发布");
        }
        // 2. 更新公示状态为公示中
        Long publisherId = UserContext.getUserId();
        LambdaUpdateWrapper<PublicNotice> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PublicNotice::getId, id)
                .set(PublicNotice::getStatus, 1)
                .set(PublicNotice::getPublisherId, publisherId)
                .set(PublicNotice::getStartTime, LocalDateTime.now())
                .set(PublicNotice::getUpdateTime, LocalDateTime.now());
        publicNoticeMapper.update(null, wrapper);
        // 3. 更新该类型下已评定的申请状态为"已公示"（status=5）
        LambdaUpdateWrapper<GrantApplication> appWrapper = new LambdaUpdateWrapper<>();
        appWrapper.eq(GrantApplication::getTypeId, notice.getTypeId())
                .eq(GrantApplication::getStatus, 4)
                .set(GrantApplication::getStatus, 5);
        grantApplicationMapper.update(null, appWrapper);
    }
}
