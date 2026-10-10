package com.collegegrant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.vo.NoticeDetailVO;
import com.collegegrant.pojo.vo.NoticeVO;
import com.collegegrant.pojo.entity.PublicNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PublicNoticeMapper extends BaseMapper<PublicNotice> {

    /**
     * 管理员分页查询公示列表
     */
    Page<NoticeVO> selectNoticePage(Page<NoticeVO> page,
                                    @Param("keyword") String keyword,
                                    @Param("status") Integer status);

    /**
     * 学生端分页查询已发布的公示列表
     */
    Page<NoticeVO> selectPublishedNoticePage(Page<NoticeVO> page,
                                             @Param("keyword") String keyword);

    /**
     * 查询公示详情（含发布人姓名、助学金类型名称）
     */
    NoticeDetailVO selectNoticeDetail(@Param("id") Long id);

    /**
     * 查询公示关联的受助学生列表（按typeId查评定结果）
     */
    List<NoticeDetailVO.NoticeStudentVO> selectNoticeStudents(@Param("typeId") Long typeId);
}
