package com.yutong.modules.common.api;

public interface IamTokenApiService {

    /**
     * 掌宇sessionKey登录获取IAM token
     */
    String getToken(String sessionKey);
}
