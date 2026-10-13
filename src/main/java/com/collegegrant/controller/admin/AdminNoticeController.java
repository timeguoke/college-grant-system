package com.collegegrant.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.result.PageResult;
import com.collegegrant.common.result.Result;
import com.collegegrant.pojo.dto.NoticeDTO;
import com.collegegrant.pojo.dto.NoticeStudentExcelDTO;
import com.collegegrant.pojo.vo.NoticeDetailVO;
import com.collegegrant.pojo.vo.NoticeVO;
import com.collegegrant.service.PublicNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @PutMapping("/end/{id}")
    @ApiOperation("结束公示")
    public Result end(@PathVariable Long id) {
        log.info("结束公示 id={}", id);
        publicNoticeService.end(id);
        return Result.success();
    }

    @GetMapping("/stats")
    @ApiOperation("公示状态统计")
    public Result stats() {
        Map<String, Long> stats = publicNoticeService.stats();
        return Result.success(stats);
    }

    @GetMapping("/{id}/export")
    @ApiOperation("导出公示名单Excel")
    public void export(@PathVariable Long id, HttpServletResponse response) throws Exception {
        log.info("导出公示名单 id={}", id);
        NoticeDetailVO detail = publicNoticeService.detail(id);
        if (detail == null) {
            throw new RuntimeException("公示不存在");
        }
        List<NoticeStudentExcelDTO> data = detail.getStudents().stream()
                .map(s -> {
                    NoticeStudentExcelDTO dto = new NoticeStudentExcelDTO();
                    dto.setStudentName(s.getStudentName());
                    dto.setStudentNo(s.getStudentNo());
                    dto.setLevelName(s.getLevelName());
                    dto.setGrantAmount(s.getGrantAmount() == null ? "" : s.getGrantAmount().toPlainString());
                    return dto;
                })
                .collect(Collectors.toList());
        // 补充序号
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setIndex(i + 1);
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("公示名单-" + detail.getTitle(), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        // 写出Excel
        EasyExcel.write(response.getOutputStream(), NoticeStudentExcelDTO.class)
                .sheet("公示名单")
                .doWrite(data);
    }
}
