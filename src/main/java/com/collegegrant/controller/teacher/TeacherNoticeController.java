package com.collegegrant.controller.teacher;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.result.PageResult;
import com.collegegrant.pojo.vo.NoticeDetailVO;
import com.collegegrant.pojo.vo.NoticeVO;
import com.collegegrant.service.PublicNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("teacherNoticeController")
@RequestMapping("/api/teacher/notice")
@Api(tags = "教师端:公示查看")
@Slf4j
public class TeacherNoticeController {

    @Autowired
    private PublicNoticeService publicNoticeService;

    @GetMapping("/list")
    @ApiOperation("已发布公示列表")
    public PageResult<NoticeVO> list(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) String keyword) {
        log.info("教师端公示列表 page={},size={},keyword={}", page, size, keyword);
        Page<NoticeVO> result = publicNoticeService.listPublished(page, size, keyword);
        Long total = result.getTotal();
        List<NoticeVO> records = result.getRecords();
        return PageResult.success(total, records);
    }

    @GetMapping("/{id}")
    @ApiOperation("公示详情")
    public PageResult<NoticeDetailVO> detail(@PathVariable Long id) {
        log.info("教师端公示详情 id={}", id);
        NoticeDetailVO vo = publicNoticeService.detail(id);
        return PageResult.success(1L, java.util.Collections.singletonList(vo));
    }
}
