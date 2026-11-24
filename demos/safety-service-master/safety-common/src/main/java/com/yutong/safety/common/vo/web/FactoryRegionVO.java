package com.yutong.safety.common.vo.web;

import lombok.Data;

@Data
public class FactoryRegionVO {
    private Integer parentId;
    private Integer sonId;
    private String parentName;
    private String sonName;
    private String sonCode;
}
