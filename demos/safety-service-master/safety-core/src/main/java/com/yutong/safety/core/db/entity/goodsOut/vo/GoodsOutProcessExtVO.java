package com.yutong.safety.core.db.entity.goodsOut.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 物资出门证流程扩展对象
 */
@Data
public class GoodsOutProcessExtVO implements Serializable {

    private String relationId;

    private String goodsFileUrl;

    private String authId;
}
