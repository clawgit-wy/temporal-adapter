package com.yutong.safety.core.db.mapper;

import java.util.List;
import java.util.Map;

public interface SafetySysPermDOBizMapper extends SafetySysPermDOMapper {
    /**
     * 查询权限资源
     * @return
     */
    List<Map> selectSysPermList();
}