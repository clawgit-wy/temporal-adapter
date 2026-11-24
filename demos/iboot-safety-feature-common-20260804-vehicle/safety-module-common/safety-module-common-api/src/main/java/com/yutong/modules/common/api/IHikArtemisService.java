package com.yutong.modules.common.api;

import com.yutong.modules.common.vo.HikPictureCheckVO;

public interface IHikArtemisService {

    /**
     * 照片评分-URL
     */
    HikPictureCheckVO artemisPictureCheckOfUrl(String content) throws Exception;

    /**
     * 照片评分-Base64
     */
    HikPictureCheckVO artemisPictureCheckOfData(String content) throws Exception;

}
