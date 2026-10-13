package com.collegegrant.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.pojo.vo.GrantTypePageVO;
import com.collegegrant.pojo.vo.GrantTypeWithLevelVO;
import com.collegegrant.common.result.PageResult;
import com.collegegrant.common.result.Result;
import com.collegegrant.pojo.dto.GrantTypeDTO;
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


    @GetMapping("/page")
    @ApiOperation("助学金分页（支持名称搜索，含等级数量）")
    public PageResult<GrantTypePageVO> pageGrantType(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String name) {

        log.info("助学金分页,分页信息 page={},size={},status={},name={} ", page, size, status, name);
        Page<GrantTypePageVO> result = grantTypeService.listPage(page, size, status, name);
        //总记录数
        Long total = result.getTotal();
        //数据
        return PageResult.success(total, result.getRecords());
    }

    @GetMapping("/list")
    @ApiOperation("助学金全量列表（下拉选择用）")
    public Result<List<GrantType>> listAll(@RequestParam(required = false) Integer status) {
        log.info("助学金全量列表 status={}", status);
        return Result.success(grantTypeService.listAll(status));
    }

    @PostMapping
    @ApiOperation("新增助学金（含等级列表）")
    public Result save(@RequestBody GrantTypeDTO grantTypeDTO) {
        log.info("新增助学金 :grantType={}", grantTypeDTO);
        grantTypeService.insertWithLevels(grantTypeDTO);
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
        // 仅更新状态，不传等级列表，避免影响已有等级
        GrantTypeDTO dto = new GrantTypeDTO();
        dto.setId(id);
        dto.setStatus(status);
        grantTypeService.updateWithLevels(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("修改助学金信息（含等级列表）")
    public Result updateGrantType(@PathVariable Long id, @RequestBody GrantTypeDTO grantTypeDTO) {
        log.info("修改助学金信息 :id={},GrantType ={}", id, grantTypeDTO);
        grantTypeDTO.setId(id);
        grantTypeService.updateWithLevels(grantTypeDTO);
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
    @ApiOperation("助学金类型详情（带等级列表）")
    public Result GrantTypeDetail(@PathVariable Long id) {
        log.info("助学金类型详情 :id={} ", id);
        GrantTypeWithLevelVO grantType = grantTypeService.detailWithLevels(id);
        return Result.success(grantType);

    }

}
