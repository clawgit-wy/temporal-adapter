package com.yutong.safety.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author gwxiexj
 * @since 2024-04-02 16:12:00
 */
@Data
public class GroupTreeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 节点ID*/
    private String key;

    /** 节点名称*/
    private String title;

    /** 父节点ID*/
    private String parent;

    /** 编号路径*/
    private String codePath;
    /** 名称路径*/
    private String namePath;

    /** 节点类型(1、机构；2、人)*/
    private Integer nodeType;

    private String dataId;

    /** 子节点集合*/
    private List<GroupTreeVO> children;
}
