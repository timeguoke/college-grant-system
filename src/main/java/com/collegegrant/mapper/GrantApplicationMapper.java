package com.collegegrant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.ApplicationVO;
import com.collegegrant.entity.GrantApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GrantApplicationMapper extends BaseMapper<GrantApplication> {

    /**
     * 分页查询学生的申请列表（联查助学金类型、审核人、学生信息）
     *
     * @param page      分页参数
     * @param studentId 学生ID
     * @return 申请VO分页
     */
    Page<ApplicationVO> selectMyApplicationPage(Page<ApplicationVO> page, @Param("studentId") Long studentId);

    /**
     * 根据ID查询申请详情（联查助学金类型、审核人、学生信息）
     *
     * @param id 申请ID
     * @return 申请VO
     */
    ApplicationVO selectDetailById(@Param("id") Long id);
}
