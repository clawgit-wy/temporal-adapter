package com.yutong.safety.core.db.entity.openclose;

import lombok.Data;

/**
 * @author caomg
 * @date 2024-11-21 10:55
 * @description OpenCloseDO
 */
@Data
public class SafetyOpenCloseDO {
    private Integer id;
    private String openName;
    private Integer openOrClose;
    private Integer deleteFlag;
}
