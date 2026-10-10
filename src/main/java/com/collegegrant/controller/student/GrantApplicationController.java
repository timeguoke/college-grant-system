package com.collegegrant.controller.student;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.context.UserContext;
import com.collegegrant.common.result.PageResult;
import com.collegegrant.common.result.Result;
import com.collegegrant.pojo.dto.ApplicationDTO;
import com.collegegrant.pojo.vo.ApplicationVO;
import com.collegegrant.service.GrantApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController("studentGrantApplicationController")
@RequestMapping("/api/student/application")
@Api(tags = "学生端:申请表相关接口")
@Slf4j
public class GrantApplicationController {

    @Autowired
    private GrantApplicationService grantApplicationService;

    @PostMapping
    @ApiOperation("提交申请")
    public Result submitApplication(@RequestBody ApplicationDTO applicationDTO) {
        log.info("提交申请:applicationDTO ={}", applicationDTO);
        Long studentId = UserContext.getUserId();
        log.info("申请人:studentId ={}", studentId);
        grantApplicationService.submitApplication(applicationDTO, studentId);
        return Result.success();
    }

    @GetMapping("/my-list")
    @ApiOperation("我的申请列表")
    public PageResult<ApplicationVO> myApplicationList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {//参数设置上默认值
        log.info("我的申请列表 ");
        Page<ApplicationVO> result = grantApplicationService.myApplicationList(page, size);
        long total = result.getTotal();
        List<ApplicationVO> records = result.getRecords();

        return PageResult.success(total, records);
    }

    @GetMapping("/{id}")
    @ApiOperation("我的申请详情")
    public Result getApplicationDetail(@PathVariable Long id) {
        log.info("我的申请详情:{}", id);
        //取出学生学号, 即存在令牌里的账号名称
        String studentNO = UserContext.getUsername();

        ApplicationVO applicationVO = grantApplicationService.getApplicationDetail(id,studentNO);
        return Result.success(applicationVO);
    }


}
