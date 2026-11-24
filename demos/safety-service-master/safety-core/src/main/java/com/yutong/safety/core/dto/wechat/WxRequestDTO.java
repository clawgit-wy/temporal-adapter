package com.yutong.safety.core.dto.wechat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author caomg
 * @date 2024-8-12 11:24
 * @description WxRequestDTO
 */
@ApiModel("请求实体类")
@Data
public class WxRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("所需下发的订阅模板id")
    private String template_id;

    @ApiModelProperty("点击模板卡片后的跳转页面，仅限本小程序内的页面。支持带参数,（示例index?foo=bar）。该字段不填则模板无跳转")
    private String page;

    @ApiModelProperty("跳转小程序类型：developer为开发版；trial为体验版；formal为正式版；默认为正式版")
    private String miniprogram_state;

    @ApiModelProperty("模板内容，格式形如 { \"key1\": { \"value\": any }, \"key2\": { \"value\": any } }的object")
    private Object data;
}
