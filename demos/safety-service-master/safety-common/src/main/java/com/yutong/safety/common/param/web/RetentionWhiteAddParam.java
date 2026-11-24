package com.yutong.safety.common.param.web;

import com.yutong.safety.common.param.Check;
import lombok.Data;

/**
 * @author gwxiexj
 * @since 2024-04-11 16:47
 */
@Data
public class RetentionWhiteAddParam implements Check {

    private Integer id;
    private String carNo;
    private String staffNo;
    private String staffName;
    private String factory;
    private String carType;
    private String beginTime;
    private String endTime;
    private String remark;

    @Override
    public void check() {

    }
}
