package com.collegegrant.controller.teacher;

import com.collegegrant.common.result.Result;
import com.collegegrant.pojo.entity.GrantType;
import com.collegegrant.service.GrantTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/grant-type")
public class TeacherGrantTypeController {

    @Autowired
    private GrantTypeService grantTypeService;

    @GetMapping("/list")
    public Result<List<GrantType>> list() {
        return Result.success(grantTypeService.listAll(1));
    }
}
