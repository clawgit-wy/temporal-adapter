package com.yutong.safety.common.vo.web;

import lombok.Data;

import java.util.List;

@Data
public class FieldManageVO {
    private Integer id;
    private Integer parentId;
    private String fieldName;
    private List<FieldManageVO> regionList;
}
