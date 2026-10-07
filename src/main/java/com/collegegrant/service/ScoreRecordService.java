package com.collegegrant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.ApplicationVO;
import com.collegegrant.common.ScoreRecordDTO;
import com.collegegrant.common.ScoreRecordVO;

public interface ScoreRecordService {
    /**
     * 提交评分
     *
     * @param scoreRecordDTO
     */
    void submitScore(ScoreRecordDTO scoreRecordDTO);

    /**
     * 修改评分
     *
     * @param scoreRecordDTO
     */
    void updateScore(ScoreRecordDTO scoreRecordDTO);

    /**
     * 评分列表
     *
     * @param page
     * @param size
     * @param keyword
     * @param typeId
     * @param scorerId
     * @return
     */
    Page<ScoreRecordVO> list(int page, int size, String keyword, Long typeId, Long scorerId);

    /**
     * 评分详情
     *
     * @param applicationId
     * @return
     */
    ScoreRecordVO getScoreDetail(Long applicationId);

    /**
     * 待评分申请列表（分页）
     *
     * @param page
     * @param size
     * @param keyword
     * @param typeId
     * @return
     */
    Page<ApplicationVO> listPendingApply(int page, int size, String keyword, Long typeId);
}
