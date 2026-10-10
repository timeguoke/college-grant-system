package com.collegegrant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.vo.ScoreRecordVO;
import com.collegegrant.pojo.entity.ScoreRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ScoreRecordMapper extends BaseMapper<ScoreRecord> {

    /**
     * 分页查询评分列表（通用）
     *
     * @param page      分页对象
     * @param keyword   关键词（学号/学生姓名）
     * @param typeId    助学金类型ID
     * @param scorerId  评分教师ID（教师端传，管理员端传null查全部）
     * @return
     */
    Page<ScoreRecordVO> selectScorePage(Page<ScoreRecordVO> page,
                                          @Param("keyword") String keyword,
                                          @Param("typeId") Long typeId,
                                          @Param("scorerId") Long scorerId);

    /**
     * 根据申请ID查询评分详情
     *
     * @param applicationId 申请ID
     * @return
     */
    ScoreRecordVO selectByApplicationId(@Param("applicationId") Long applicationId);
}
