package com.collegegrant.controller.teacher;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.context.UserContext;
import com.collegegrant.common.result.PageResult;
import com.collegegrant.pojo.vo.EvaluationResultVO;
import com.collegegrant.service.EvaluationResultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("teacherEvaluationController")
@RequestMapping("/api/teacher/evaluation")
@Api(tags = "教师端:评定结果查看")
@Slf4j
public class TeacherEvaluationController {

    @Autowired
    private EvaluationResultService evaluationResultService;

    @GetMapping("/list")
    @ApiOperation("评定结果列表")
    public PageResult<EvaluationResultVO> list(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(required = false) Long typeId,
                                               @RequestParam(required = false) String keyword) {
        log.info("教师端评定结果列表 page={},size={},typeId={},keyword={}", page, size, typeId, keyword);
        // 从JWT token获取当前教师ID，只查自己负责的学生
        Long counselorId = UserContext.getUserId();
        Page<EvaluationResultVO> result = evaluationResultService.list(page, size, typeId, keyword, counselorId);
        Long total = result.getTotal();
        List<EvaluationResultVO> records = result.getRecords();
        return PageResult.success(total, records);
    }

    @GetMapping("/{id}")
    @ApiOperation("评定结果详情")
    public PageResult<EvaluationResultVO> detail(@PathVariable Long id) {
        Long counselorId = UserContext.getUserId();
        log.info("教师端评定结果详情 id={},counselorId={}", id, counselorId);
        EvaluationResultVO vo = evaluationResultService.getEvalDetail(id);
        if (vo == null) {
            throw new RuntimeException("评定结果不存在");
        }
        // 权限校验：只能查看自己名下学生的评定结果
        Long ownerCounselorId = evaluationResultService.getCounselorIdByResultId(id);
        if (ownerCounselorId == null || !ownerCounselorId.equals(counselorId)) {
            throw new RuntimeException("无权查看该评定结果");
        }
        return PageResult.success(1L, java.util.Collections.singletonList(vo));
    }
}
