package com.yutong.modules.common.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.common.entity.YTSafetyFileInfo;
import org.springframework.web.multipart.MultipartFile;



public interface FileService extends IService<YTSafetyFileInfo> {

    YTSafetyFileInfo uploadFile(MultipartFile file,String token);

    void deleteFile(String fileIds);

}
