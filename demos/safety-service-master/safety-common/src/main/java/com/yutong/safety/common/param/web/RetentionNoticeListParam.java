package com.yutong.safety.common.param.web;

import lombok.Data;

/**
 * @author gwxiexj
 */
@Data
public class RetentionNoticeListParam extends WebPageParam {

    private String carType;
    /**
     * 通知方式  ("1":"短信","2":"掌上宇通":"1,2":"短信+掌上宇通")
     */
    private String remindType;
    private Integer isUsed;
}
