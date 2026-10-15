package com.collegegrant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.dto.EvaluateDTO;
import com.collegegrant.pojo.dto.EvaluationExportDTO;
import com.collegegrant.pojo.vo.EvaluateResultVO;
import com.collegegrant.pojo.vo.EvaluationResultVO;

import java.util.List;

public interface EvaluationResultService {
    /**
     * AHP评定
     * @param evaluateDTO
     * @return
     */
    EvaluateResultVO AHP(EvaluateDTO evaluateDTO);

    /**
     * 评定结果列表（分页
     * @param page
     * @param size
     * @param typeId
     * @param keyword
     * @param counselorId 辅导员ID（教师端按此隔离，管理员端传null查全部）
     * @return
     */
    Page<EvaluationResultVO> list(int page, int size, Long typeId, String keyword, Long counselorId);

    /**
     * 评定结果详情
     * @param id
     * @return
     */
    EvaluationResultVO getEvalDetail(Long id);

    /**
     * 查询评定结果所属学生的辅导员ID（教师端详情越权校验用）
     * @param id 评定结果ID
     * @return 辅导员ID
     */
    Long getCounselorIdByResultId(Long id);

    /**
     * 导出评定结果
     * @param typeId 助学金类型ID
     * @return 导出数据列表
     */
    List<EvaluationExportDTO> getExportData(Long typeId);
}
