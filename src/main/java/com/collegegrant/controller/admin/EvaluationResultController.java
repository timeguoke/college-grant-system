package com.collegegrant.controller.admin;

import com.collegegrant.common.Result;
import com.collegegrant.service.EvaluationResultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/evaluation")
@Api(tags = "AHP评定相关接口")
@Slf4j
public class EvaluationResultController {

    @Autowired
    private EvaluationResultService evaluationResultService;

    @PostMapping("/do")
    @ApiOperation("AHP评定功能")
    public Result AHP(){
return null;
    }
}
