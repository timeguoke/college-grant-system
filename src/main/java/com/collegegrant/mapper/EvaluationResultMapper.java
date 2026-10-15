package com.collegegrant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.vo.EvaluationResultVO;
import com.collegegrant.pojo.entity.EvaluationResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EvaluationResultMapper extends BaseMapper<EvaluationResult> {

    /**
     * 分页查询评定结果列表
     * @param page        分页对象
     * @param typeId      助学金类型ID
     * @param keyword     关键词（学号/姓名）
     * @param counselorId 辅导员ID（教师端隔离用，管理员端传null）
     * @return            评定结果VO分页
     */
    Page<EvaluationResultVO> selectResultPage(Page<EvaluationResultVO> page,
                                              @Param("typeId") Long typeId,
                                              @Param("keyword") String keyword,
                                              @Param("counselorId") Long counselorId);

    /**
     * 查询评定结果详情（含4个维度得分）
     * @param id 评定结果ID
     * @return 评定结果VO
     */
    EvaluationResultVO selectResultDetail(@Param("id") Long id);

    /**
     * 查询该评定结果所属学生的辅导员ID（教师端详情越权校验用）
     * @param id 评定结果ID
     * @return 辅导员ID（学生未分配辅导员时返回null）
     */
    Long selectCounselorIdByResultId(@Param("id") Long id);
}
