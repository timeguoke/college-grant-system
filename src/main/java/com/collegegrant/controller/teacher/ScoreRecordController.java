package com.collegegrant.controller.teacher;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.*;
import com.collegegrant.service.ScoreRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("teacherScoreRecordController")
@RequestMapping("/api/teacher/score")
@Api(tags = "评分相关接口")
@Slf4j
public class ScoreRecordController {
    @Autowired
    private ScoreRecordService scoreRecordService;

    @PostMapping
    @ApiOperation("提交评分")
    public Result submitScore(@RequestBody ScoreRecordDTO scoreRecordDTO) {
        log.info("提交评分 scoreRecordDTO = {}", scoreRecordDTO);
        scoreRecordService.submitScore(scoreRecordDTO);
        return Result.success();
    }

    @PutMapping("/{applicationId}")
    @ApiOperation("修改评分")
    public Result updateScore(@PathVariable Long applicationId, @RequestBody ScoreRecordDTO scoreRecordDTO) {
        log.info("修改评分 applicationId={},scoreRecordDTO={}", applicationId, scoreRecordDTO);
        scoreRecordDTO.setApplicationId(applicationId);
        scoreRecordService.updateScore(scoreRecordDTO);
        return Result.success();
    }

    @GetMapping("/my-list")
    @ApiOperation("评分列表:教师端")
    public PageResult<ScoreRecordVO> list(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) Long typeId) {

        log.info("评分列表:教师端  page={},size={},keyword={},typeId={}", page, size, keyword, typeId);
        //修改人的id
        Long scorerId = UserContext.getUserId();
        Page<ScoreRecordVO> result = scoreRecordService.list(page, size, keyword, typeId,scorerId);
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

    @GetMapping("/pending")
    @ApiOperation("待评分申请列表（分页）")
    public PageResult<ApplicationVO> listPendingApply(@RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam(required = false) String keyword,
                                                         @RequestParam(required = false) Long typeId){
        log.info("待评分申请列表page={},size={},keyword={},typeId={}", page, size, keyword, typeId);
        Page<ApplicationVO> result=  scoreRecordService.listPendingApply(page, size, keyword, typeId);
        long total = result.getTotal();
        List<ApplicationVO> records = result.getRecords();
        return PageResult.success(total, records);

    }
}
