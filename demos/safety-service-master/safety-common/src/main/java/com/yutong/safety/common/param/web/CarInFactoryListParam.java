package com.yutong.safety.common.param.web;

import lombok.Data;

/**
 * @author gwxiexj
 * @since 2024-04-11 10:47
 */
@Data
public class CarInFactoryListParam extends WebPageParam {

    private String carNo;
    private String carType;
    private String factory;
    private String beginDate;
    private String endDate;
    private String staffNo;
    private String staffName;
    private Integer isRetention;
    private String remark;
}
