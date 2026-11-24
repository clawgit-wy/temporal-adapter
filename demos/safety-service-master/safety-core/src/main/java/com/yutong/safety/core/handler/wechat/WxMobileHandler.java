package com.yutong.safety.core.handler.wechat;

import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.dto.wechat.WxRequestDTO;

/**
 * @author caomg
 * @date 2024-8-12 11:03
 * @description WxMobileHandler
 */
public interface WxMobileHandler {
    //向订阅消息用户推送消息
    VO pushMessageToWx(WxRequestDTO en);

    //获取openID
    String getOpenId(String mobile);

    //获取订阅次数
    String getSubscribe(String openId);

}
