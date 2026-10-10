package com.collegegrant.controller.student;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.*;
import com.collegegrant.service.PublicNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/notice")
@Api(tags = "学生-公示查看")
@Slf4j
public class StudentNoticeController {

    @Autowired
    private PublicNoticeService publicNoticeService;

    @GetMapping("/list")
    @ApiOperation("公示列表（分页）")
    public PageResult<NoticeVO> list(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) String keyword) {
        log.info("学生公示列表 page={}, size={}, keyword={}", page, size, keyword);
        Page<NoticeVO> result = publicNoticeService.listPublished(page, size, keyword);
        return PageResult.success(result.getTotal(), result.getRecords());
    }

    @GetMapping("/{id}")
    @ApiOperation("公示详情")
    public Result detail(@PathVariable Long id) {
        log.info("学生公示详情 id={}", id);
        NoticeDetailVO detail = publicNoticeService.detail(id);
        if (detail == null) {
            return Result.error("公示不存在");
        }
        // 学生端只能查看已发布的公示
        if (detail.getStatus() == null || detail.getStatus() != 1) {
            return Result.error("公示未发布");
        }
        return Result.success(detail);
    }
}
