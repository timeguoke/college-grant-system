package com.collegegrant.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.vo.ApplicationVO;
import com.collegegrant.common.result.PageResult;
import com.collegegrant.common.result.Result;
import com.collegegrant.service.GrantApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminGrantApplicationController")
@RequestMapping("/api/admin/application")
@Api(tags = "管理员:申请管理接口")
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
        log.info("管理员-申请列表 page={},size={},keyword={},status={},typeId={}", page, size, keyword, status, typeId);
        Page<ApplicationVO> result = grantApplicationService.list(page, size, keyword, status, typeId, null);
        return PageResult.success(result.getTotal(), result.getRecords());
    }

    @GetMapping("/{id}")
    @ApiOperation("申请详情")
    public Result getApplicationDetail(@PathVariable Long id) {
        log.info("管理员-申请详情 id={}", id);
        ApplicationVO applicationVO = grantApplicationService.getApplicationDetail(id, null);
        return Result.success(applicationVO);
    }

}