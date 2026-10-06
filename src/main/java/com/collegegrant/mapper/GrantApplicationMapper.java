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
   // Page<ApplicationVO> selectMyApplicationPage(Page<ApplicationVO> page, @Param("studentId") Long studentId);

    /**
     * 分页查询申请列表（通用）
     *
     * @param page      分页对象
     * @param studentId 学生ID（学生端必传，教师/管理员传null查全部）
     * @param status    状态筛选
     * @param typeId    助学金类型ID
     * @param keyword   关键词（学号/姓名）
     */
    Page<ApplicationVO> selectApplicationPage(Page<ApplicationVO> page,
                                   @Param("studentId") Long studentId,
                                   @Param("status") Integer status,
                                   @Param("typeId") Long typeId,
                                   @Param("keyword") String keyword);

    /**
     * 根据ID查询申请详情（联查助学金类型、审核人、学生信息）
     *
     * @param id 申请ID
     * @return 申请VO
     */
    ApplicationVO selectDetailById(@Param("id") Long id);

    /**
     * 新方案:分页查询待评分申请列表（教师端用）
     * 筛选条件：状态=通过 + 普通学生 + 尚未评分
     *
     * @param page      分页对象
     * @param keyword   关键词（学号/姓名）
     * @param typeId    助学金类型ID
     * @return
     */
    Page<ApplicationVO> selectPendingScorePage(Page<ApplicationVO> page,
                                                @Param("keyword") String keyword,
                                                @Param("typeId") Long typeId);
}
