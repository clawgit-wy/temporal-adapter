package com.yutong.safety.core.db.entity.result;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 数据字典子项目表
 * </p>
 *
 * @author renhaoxiang
 * @since 2019-05-05
 */
@Data
public class DictItemRt implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典子项目编码
     */
    private String itemCode;

    /**
     * 字典子项目名称
     */
    private String itemName;

    /**
     * 记录排序
     */
    private Integer itemOrder;

    private String showFlag;

}
