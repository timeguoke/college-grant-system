package com.collegegrant.controller.admin;

import com.collegegrant.common.result.Result;
import com.collegegrant.pojo.vo.DashboardVO;
import com.collegegrant.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@Api(tags = "首页仪表盘")
@Slf4j
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    @ApiOperation("获取首页统计数据")
    public Result<DashboardVO> getDashboard() {
        log.info("获取首页统计数据");
        DashboardVO vo = dashboardService.getDashboardData();
        return Result.success(vo);
    }
}
