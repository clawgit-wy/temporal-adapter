package com.yutong.modules.vehicle.handle;

import com.yutong.modules.common.utils.Base64ImageDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SafetyPhotoService {

    @Value("${iam.apikey}")
    private String apikey;

    @Value("${iam.updatePhoto.url}")
    private String updatePhotoUrl;

    public String getPhotoUrl(String address) {
        if (StringUtils.isEmpty(address)) {
            return null;
        }
        //图片转化为二进制
        if(address.startsWith("http")){
            if (Base64ImageDecoder.isPhoto(address)) {
                return address;
            } else {
                return address + "&apikey=" + apikey;
            }
        }else{
            return String.format(updatePhotoUrl, address);
        }
    }

}
