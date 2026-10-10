package com.collegegrant.controller.admin;

import com.collegegrant.common.result.Result;
import com.collegegrant.pojo.entity.GrantLevel;
import com.collegegrant.service.GrantLevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员-助学金等级管理控制器
 */
@RestController("adminGrantLevelController")
@RequestMapping("/api/admin/grant-level")
@Slf4j
@Api(tags = "助学金等级管理相关接口")
public class GrantLevelController {

    @Autowired
    private GrantLevelService grantLevelService;

    /**
     * 查询某个助学金下的所有等级
     */
    @GetMapping("/list/{typeId}")
    @ApiOperation("等级列表")
    public Result<List<GrantLevel>> list(@PathVariable Long typeId) {
        log.info("等级列表 typeId={}", typeId);
        List<GrantLevel> list = grantLevelService.listByTypeId(typeId);
        return Result.success(list);
    }

    /**
     * 查询等级详情
     */
    @GetMapping("/{id}")
    @ApiOperation("等级详情")
    public Result<GrantLevel> detail(@PathVariable Long id) {
        log.info("等级详情 id={}", id);
        GrantLevel grantLevel = grantLevelService.getById(id);
        return Result.success(grantLevel);
    }

    /**
     * 新增等级
     */
    @PostMapping("/{typeId}")
    @ApiOperation("新增等级")
    public Result save(@PathVariable Long typeId, @RequestBody GrantLevel grantLevel) {
        log.info("新增等级 typeId={}, grantLevel={}", typeId, grantLevel);
        grantLevelService.insert(typeId, grantLevel);
        return Result.success();
    }

    /**
     * 修改等级
     */
    @PutMapping("/{id}")
    @ApiOperation("修改等级")
    public Result update(@PathVariable Long id, @RequestBody GrantLevel grantLevel) {
        log.info("修改等级 id={}, grantLevel={}", id, grantLevel);
        grantLevel.setId(id);
        grantLevelService.update(grantLevel);
        return Result.success();
    }

    /**
     * 删除等级
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除等级")
    public Result delete(@PathVariable Long id) {
        log.info("删除等级 id={}", id);
        grantLevelService.deleteById(id);
        return Result.success();
    }
}
