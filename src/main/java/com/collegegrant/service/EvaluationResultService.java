package com.collegegrant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.EvaluateDTO;
import com.collegegrant.common.EvaluateResultVO;
import com.collegegrant.common.EvaluationResultVO;

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
}
