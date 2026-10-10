package com.collegegrant.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.result.PageResult;
import com.collegegrant.common.result.Result;
import com.collegegrant.pojo.dto.NoticeDTO;
import com.collegegrant.pojo.vo.NoticeDetailVO;
import com.collegegrant.pojo.vo.NoticeVO;
import com.collegegrant.service.PublicNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/notice")
@Api(tags = "管理员-公示管理")
@Slf4j
public class AdminNoticeController {

    @Autowired
    private PublicNoticeService publicNoticeService;

    @GetMapping("/list")
    @ApiOperation("公示列表（分页）")
    public PageResult<NoticeVO> list(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) Integer status) {
        log.info("公示列表 page={}, size={}, keyword={}, status={}", page, size, keyword, status);
        Page<NoticeVO> result = publicNoticeService.list(page, size, keyword, status);
        return PageResult.success(result.getTotal(), result.getRecords());
    }

    @PostMapping
    @ApiOperation("新增公示")
    public Result add(@RequestBody NoticeDTO noticeDTO) {
        log.info("新增公示 title={}, typeId={}", noticeDTO.getTitle(), noticeDTO.getTypeId());
        Long id = publicNoticeService.add(noticeDTO);
        return Result.success(id);
    }

    @GetMapping("/{id}")
    @ApiOperation("公示详情")
    public Result detail(@PathVariable Long id) {
        log.info("公示详情 id={}", id);
        NoticeDetailVO detail = publicNoticeService.detail(id);
        if (detail == null) {
            return Result.error("公示不存在");
        }
        return Result.success(detail);
    }

    @PutMapping("/{id}")
    @ApiOperation("修改公示")
    public Result update(@PathVariable Long id, @RequestBody NoticeDTO noticeDTO) {
        log.info("修改公示 id={}", id);
        publicNoticeService.update(id, noticeDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除公示")
    public Result delete(@PathVariable Long id) {
        log.info("删除公示 id={}", id);
        publicNoticeService.delete(id);
        return Result.success();
    }

    @PutMapping("/publish/{id}")
    @ApiOperation("发布公示")
    public Result publish(@PathVariable Long id) {
        log.info("发布公示 id={}", id);
        publicNoticeService.publish(id);
        return Result.success();
    }
}
