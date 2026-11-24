package com.yutong.modules.user.model;

import lombok.Data;

@Data
public class TreeSelectModel {

    private String key;

    private String title;
    /**
     * 是否叶子节点
     */
    private boolean isLeaf;

    private String icon;

    private String parentId;

    private String value;

    private String code;
}
