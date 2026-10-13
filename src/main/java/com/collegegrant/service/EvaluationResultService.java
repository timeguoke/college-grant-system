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
     * @return
     */
    Page<EvaluationResultVO> list(int page, int size, Long typeId, String keyword);

    /**
     * 评定结果详情
     * @param id
     * @return
     */
    EvaluationResultVO getEvalDetail(Long id);

    /**
     * 导出评定结果
     * @param typeId 助学金类型ID
     * @return 导出数据列表
     */
    List<EvaluationExportDTO> getExportData(Long typeId);
}
