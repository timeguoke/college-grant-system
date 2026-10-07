package com.collegegrant.controller.student;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.GrantTypeWithLevelVO;
import com.collegegrant.common.PageResult;
import com.collegegrant.service.GrantTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController("studentGrantTypeController")
@RequestMapping("/api/student/grant-type")
@Slf4j
@Api(tags = "学生端-助学金类型接口")
public class GrantTypeController {

    @Autowired
    private GrantTypeService grantTypeService;

    /**
     * 可申请助学金列表（带等级）
     */
    @GetMapping("/available")
    @ApiOperation("可申请助学金列表（带等级）")
    public PageResult<GrantTypeWithLevelVO> availableList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("学生端-可申请助学金列表 page={},size={}", page, size);
        Page<GrantTypeWithLevelVO> result = grantTypeService.selectAvailableList(page, size);
        return PageResult.success(result.getTotal(), result.getRecords());
    }

}
