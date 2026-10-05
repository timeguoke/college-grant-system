package com.collegegrant.controller.teacher;

import com.collegegrant.common.Result;
import com.collegegrant.service.ScoreRecordServcie;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teacher/score")
@Api(tags = "评分相关接口")
@Slf4j
public class ScoreRecordController {
    @Autowired
    private ScoreRecordServcie scoreRecordServcie;




}
