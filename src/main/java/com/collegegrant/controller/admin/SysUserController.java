package com.collegegrant.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collegegrant.common.PageResult;
import com.collegegrant.common.Result;
import com.collegegrant.entity.SysUser;
import com.collegegrant.service.admin.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员-用户管理控制器
 */
@RestController
@RequestMapping("/api/admin/user")
@Slf4j
@Api(tags = "用户管理相关接口")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 用户列表（分页）
     */
    @GetMapping("/list")
    @ApiOperation("用户列表（分页）")
    public PageResult<SysUser> pageUser(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer role) {

        log.info("用户分页, page={}, size={}, keyword={}, role={}", page, size, keyword, role);
        Page<SysUser> result = sysUserService.list(page, size, keyword, role);
        //总记录数
        Long total = result.getTotal();
        //数据
        List<SysUser> records = result.getRecords();
        return PageResult.success(total, records);
    }

    @GetMapping("/{id}")
    @ApiOperation("用户查询")
    public Result selectById(@PathVariable Long id) {
        log.info("用户查询:id = {}", id);
        SysUser sysUser = sysUserService.selectById(id);
        return Result.success(sysUser);
    }



    @PutMapping("/{id}/status")
    @ApiOperation("启用禁用账号")
    public Result updateSysStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("启用禁用账号 :id={},status ={}", id, status);
        SysUser sysUser = SysUser.builder()
                .status(status)
                .id(id)
                .build();
        sysUserService.update(sysUser);
        return Result.success();
    }

    @PutMapping("/reset/{id}")
    @ApiOperation("重置密码")
    public Result resetPassword(@PathVariable Long id){
        log.info("重置密码 id ={}",id);
        sysUserService.resetPassword(id);
        return Result.success();

    }
}
