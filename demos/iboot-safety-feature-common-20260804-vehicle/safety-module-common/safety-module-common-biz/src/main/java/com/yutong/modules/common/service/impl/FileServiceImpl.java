package com.yutong.modules.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.common.api.FileService;
import org.jeecg.common.util.HttpFileUtil;
import org.jeecg.common.util.BizException;
import org.jeecg.common.util.NewJsonUtils;
import org.jeecg.common.util.ResponseEnum;
import com.yutong.modules.common.entity.YTSafetyFileInfo;
import com.yutong.modules.common.mapper.YTSafetyFileInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<YTSafetyFileInfoMapper, YTSafetyFileInfo> implements FileService {

    @Value("${ytsafety.file.uploadUrl}")
    private String picUploadUrl;

    @Override
    public YTSafetyFileInfo uploadFile(MultipartFile file, String token) {
        try {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("上传文件不能为空");
            }

            log.info("picUploadUrl:{}", picUploadUrl);
            String picFileUrl = HttpFileUtil.uploadNewFile(picUploadUrl, file, token);
            if(StringUtils.isEmpty(picFileUrl)) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "上传异常");
            }

            JSONObject resultJson = (JSONObject) NewJsonUtils.parse(picFileUrl);
            if(!"0".equals(resultJson.getString("code"))) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "上传异常");
            }

            String picFilePath = resultJson.getString("data");
            picFilePath = StringUtils.substringBefore(picFilePath, "_");
            log.info("文件上传响应: {}", picFilePath);

            String fileName = file.getOriginalFilename();
            String fileType = fileName != null && fileName.contains(".")
                    ? fileName.substring(fileName.lastIndexOf(".") + 1)
                    : "";
            Long fileSize = file.getSize();
            YTSafetyFileInfo fileInfo = new YTSafetyFileInfo();
            fileInfo.setId(IdWorker.getId());
            fileInfo.setFilePath(picFilePath);
            fileInfo.setFileName(fileName);
            fileInfo.setFileType(fileType);
            fileInfo.setFileSize(fileSize);
            fileInfo.setDelFlag(0);

            save(fileInfo);
            log.info("文件上传并保存成功: fileName={}, fileId={}",
                    fileName, fileInfo.getFileId());
            return fileInfo;
        } catch (Exception e) {
            log.error("文件上传异常", e);
            throw new RuntimeException("文件上传异常: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String fileIds) {

        List<String> fileIdList = Arrays.stream(fileIds.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        LambdaQueryWrapper<YTSafetyFileInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(YTSafetyFileInfo::getId, fileIdList);

        remove(queryWrapper);
    }



}
