package com.yutong.safety.common.param.web;

import lombok.Data;

/**
 * @author gwxiexj
 * @since 2024-04-12 15:18
 */
@Data
public class LaneFactoryListParam extends WebPageParam{

    private String factoryName;
    private String laneName;
}
