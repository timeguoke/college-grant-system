package com.collegegrant.controller.teacher;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.result.PageResult;
import com.collegegrant.common.result.Result;
import com.collegegrant.pojo.vo.ApplicationVO;
import com.collegegrant.service.GrantApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController("teacherGrantApplicationController")
@RequestMapping("/api/teacher/application")
@Api(tags = "教师端:申请表相关接口")
@Slf4j
public class GrantApplicationController {

    @Autowired
    private GrantApplicationService grantApplicationService;

    @GetMapping("/list")
    @ApiOperation("申请列表")
    public PageResult<ApplicationVO> listPage(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam(required = false) Long typeId) {
        log.info("申请列表分页,分页信息 page={},size={},keyword={},status={},typeId={}", page, size, keyword, status, typeId);
        Page<ApplicationVO> result = grantApplicationService.list(page, size, keyword, status, typeId);
        //总记录数
        Long total = result.getTotal();
        //数据
        List<ApplicationVO> records = result.getRecords();
        return PageResult.success(total, records);
    }


    @GetMapping("/{id}")
    @ApiOperation("申请详情")
    public Result getApplicationDetail(@PathVariable Long id) {
        log.info("申请详情:{}", id);
        ApplicationVO applicationVO = grantApplicationService.getApplicationDetail(id, null);
        return Result.success(applicationVO);
    }

    @PutMapping("/{id}/pass")
    @ApiOperation("通过申请")
    public Result pass(@PathVariable Long id, @RequestParam(required = false) String reviewComment) {
        log.info("通过申请 id={},reviewComment={}", id, reviewComment);
        grantApplicationService.updateStatus(id, reviewComment, 1);
        return Result.success();
    }

    @PutMapping("/{id}/reject")
    @ApiOperation("驳回申请")
    public Result reject(@PathVariable Long id, @RequestParam String reviewComment) {
        log.info("驳回申请 id={},reviewComment={}", id, reviewComment);
        grantApplicationService.updateStatus(id, reviewComment, 2);
        return Result.success();
    }


}
