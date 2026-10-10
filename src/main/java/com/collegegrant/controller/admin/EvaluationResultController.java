package com.collegegrant.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.*;
import com.collegegrant.entity.Student;
import com.collegegrant.service.EvaluationResultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/evaluation")
@Api(tags = "AHP评定相关接口")
@Slf4j
public class EvaluationResultController {

    @Autowired
    private EvaluationResultService evaluationResultService;

    @PostMapping("/do")
    @ApiOperation("AHP评定功能")
    public Result AHP(@RequestBody @Valid EvaluateDTO evaluateDTO) {
        log.info("AHP评定功能");
        EvaluateResultVO evaluateResultVO = evaluationResultService.AHP(evaluateDTO);
        return Result.success(evaluateResultVO);

    }

    @GetMapping("/list")
    @ApiOperation("评定结果列表（分页）")
    public PageResult getAssessResultPage(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(required = false) Long typeId,
                                          @RequestParam(required = false) String keyword) {
        log.info("评定结果列表（分页）page={},size={},typeId={},keyword={} ", page, size, typeId, keyword);
        Page<EvaluationResultVO> result = evaluationResultService.list(page, size, typeId, keyword);
        //总记录数
        Long total = result.getTotal();
        //数据
        List<EvaluationResultVO> records = result.getRecords();
        return PageResult.success(total, records);

    }

    @GetMapping("/{id}")
    @ApiOperation("评定结果详情")
    public Result getEvalDetail(@PathVariable Long id) {
        log.info("评定结果详情 :id={}", id);
        EvaluationResultVO evaluationResultVO = evaluationResultService.getEvalDetail(id);
        if (evaluationResultVO == null) {
            return Result.error("评定结果不存在");
        }
        return Result.success(evaluationResultVO);
    }
}
