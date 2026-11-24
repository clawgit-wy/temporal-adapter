package com.yutong.safety.common.vo.sys;

import lombok.Data;

import java.util.List;

@Data
public class SysRolePermVO {
    private Integer id;
    private String roleName;
    private List<Integer> permIds;
}
