package com.collegegrant.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.vo.GrantTypeWithLevelVO;
import com.collegegrant.common.result.PageResult;
import com.collegegrant.common.result.Result;
import com.collegegrant.pojo.entity.GrantType;
import com.collegegrant.service.GrantTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController("adminGrantTypeController")
@RequestMapping("/api/admin/grant-type")
@Slf4j
@Api(tags = "助学金类型管理相关接口")
public class GrantTypeController {

    @Autowired
    private GrantTypeService grantTypeService;


    @GetMapping("/list")
    @ApiOperation("助学金分页")
    public PageResult<GrantType> pageGrantType(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {

        log.info("助学金分页,分页信息 page={},size={},status={} ", page, size, status);
        Page<GrantType> result = grantTypeService.list(page, size, status);
        //总记录数
        Long total = result.getTotal();
        //数据
        List<GrantType> records = result.getRecords();
        return PageResult.success(total, records);
    }

    @PostMapping
    @ApiOperation("新增助学金")
    public Result save(@RequestBody GrantType grantType) {
        log.info("新增助学金 :grantType={}", grantType);
        grantTypeService.insert(grantType);
        return Result.success();
    }

    @GetMapping("/available")
    @ApiOperation("可申请助学金列表（带等级）")
    public PageResult<GrantTypeWithLevelVO> availableList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("可申请助学金列表 page={},size={}", page, size);
        Page<GrantTypeWithLevelVO> result = grantTypeService.selectAvailableList(page, size);
        return PageResult.success(result.getTotal(), result.getRecords());
    }

    @PutMapping("/{id}/status")
    @ApiOperation("启用禁用助学金")
    public Result updateGrantTypeStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("启用禁用助学金 :id={},status ={}", id, status);
        GrantType grantType = GrantType.builder()
                .status(status)
                .id(id)
                .build();
        grantTypeService.update(grantType);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("修改助学金信息")
    public Result updateGrantType(@PathVariable Long id, @RequestBody GrantType grantType) {
        log.info("修改助学金信息 :id={},GrantType ={}", id, grantType);
        grantType.setId(id);
        grantTypeService.update(grantType);
        return Result.success();
    }


    @DeleteMapping("/{id}")
    @ApiOperation("删除助学金")
    public Result deleteGrantType(@PathVariable Long id) {
        log.info("删除助学金 :id={} ", id);
        grantTypeService.deleteById(id);

        return Result.success();
    }


    @GetMapping("/count")
    @ApiOperation("统计助学金总数")
    public Result countGrantType() {
        log.info("统计助学金总数");
        Long count = grantTypeService.countGrantType();
        return Result.success(count);
    }

    @GetMapping("/{id}")
    @ApiOperation("助学金类型详情")
    public Result GrantTypeDetail(@PathVariable Long id) {
        log.info("助学金类型详情 :id={} ", id);
        GrantType grantType = grantTypeService.GrantTypeDetail(id);
        return Result.success(grantType);

    }

}
