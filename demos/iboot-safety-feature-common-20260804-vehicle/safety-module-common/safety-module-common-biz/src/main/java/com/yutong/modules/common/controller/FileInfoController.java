package com.yutong.modules.common.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yutong.modules.common.api.FileService;
import com.yutong.modules.common.entity.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Api(tags="文件上传下载")
@RestController
@RequestMapping("/api/file")
public class FileInfoController {

    @Autowired
    private FileService fileService;

//    @AutoLog(value = "文件上传")
    @ApiOperation(value="文件上传", notes="文件上传")
    @PostMapping(value = "/upload")
    public Result<?> upload(@RequestParam("file") MultipartFile file,@RequestHeader("X-Access-Token") String token) {
        YTSafetyFileInfo fileInfo = fileService.uploadFile(file,token);
        return Result.OK("上传成功！",fileInfo);
    }

    @ApiOperation(value="文件上传", notes="文件上传")
    @PostMapping(value = "/upload/base64")
    public Result<?> uploadBase64(
            @RequestParam("fileBase64") String fileBase64,
            @RequestParam("fileName") String fileName,
            @RequestHeader("X-Access-Token") String token) {

        try {
            if (StringUtils.isEmpty(fileName) || !fileName.contains(".")) {
                return Result.error("文件名不能为空，且必须包含文件后缀名");
            }
            if (fileBase64.contains(",")) {
                fileBase64 = fileBase64.substring(fileBase64.indexOf(",") + 1);
            }
            byte[] fileBytes = Base64.getDecoder().decode(fileBase64);
            UrlMultipartFile mulFile = new UrlMultipartFile(fileName, "application/octet-stream", fileBytes);

            YTSafetyFileInfo fileInfo = fileService.uploadFile(mulFile, token);
            return Result.OK("上传成功！", fileInfo);

        } catch (IllegalArgumentException e) {
            return Result.error("Base64 解码失败，请检查数据格式");
        } catch (Exception e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    @ApiOperation(value="单个文件查询", notes="单个文件查询")
    @GetMapping(value = "/query/{id}")
    public Result<?> query(@PathVariable("id") String fileId) {
        return Result.OK(fileService.getById(fileId));
    }

    @ApiOperation(value="列表文件查询", notes="列表文件查询")
    @PostMapping(value = "/queryList")
    public Result<?> queryList(@RequestParam("fileIds") String fileIds) {

        List<String> fileIdList = Arrays.stream(fileIds.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        List<YTSafetyFileInfo> list = fileService.listByIds(fileIdList);
        return Result.OK(list);
    }

    @ApiOperation(value="文件删除", notes="文件删除")
    @GetMapping(value = "/delete")
    public Result<?> delete(@RequestParam("fileIds") String fileIds) {
        fileService.deleteFile(fileIds);
        return Result.OK();
    }
}
