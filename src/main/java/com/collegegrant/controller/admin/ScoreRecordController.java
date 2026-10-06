package com.collegegrant.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.*;
import com.collegegrant.service.ScoreRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminScoreRecordController")
@RequestMapping("/api/admin/score")
@Api(tags = "评分相关接口")
@Slf4j
public class ScoreRecordController {
    @Autowired
    private ScoreRecordService scoreRecordService;


    @GetMapping("/list")
    @ApiOperation("评分列表:管理员端")
    public PageResult<ScoreRecordVO> list(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(required = false) Long applicationId) {

        log.info("评分列表: 管理员端  page={},size={},applicationId={}", page, size, applicationId);
        Page<ScoreRecordVO> result = scoreRecordService.list(page, size, null,applicationId,null);
        long total = result.getTotal();
        List<ScoreRecordVO> records = result.getRecords();
        return PageResult.success(total, records);
    }

    @GetMapping("/{applicationId}")
    @ApiOperation("评分详情")
    public Result getScoreDetail(@PathVariable Long applicationId) {
        log.info("评分详情  applicationId ={}", applicationId);
        ScoreRecordVO scoreRecordVO = scoreRecordService.getScoreDetail(applicationId);
        return Result.success(scoreRecordVO);

    }


}
