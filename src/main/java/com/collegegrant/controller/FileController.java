package com.collegegrant.controller;

import com.collegegrant.common.result.Result;
import com.collegegrant.common.util.OssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/file")
@Api(tags = "文件上传")
public class FileController {

    @Autowired
    private OssUtil ossUtil;

    // 允许的文件后缀
    private static final List<String> ALLOWED_TYPES = Arrays.asList(".jpg", ".jpeg", ".png", ".pdf", ".doc", ".docx");

    @PostMapping("/upload")
    @ApiOperation("上传文件")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        // 1. 校验文件不为空
        if (file == null || file.isEmpty()) {
            return Result.error("请选择文件");
        }

        // 2. 校验文件大小（10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.error("文件大小不能超过10MB");
        }

        // 3. 校验文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            if (!ALLOWED_TYPES.contains(suffix)) {
                return Result.error("不支持的文件类型，仅允许：" + ALLOWED_TYPES);
            }
        }

        // 4. 上传到OSS
        String url = ossUtil.upload(file);
        return Result.success(url);
    }
}
