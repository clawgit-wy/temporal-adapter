package com.yutong.safety.core.db.mapper;



import java.util.Map;

public interface SafetySysRolePermDOBizMapper extends SafetySysRolePermDOMapper {

    /**
     * 数据批量添加
     * @param map
     */
    public Integer setRolePerms(Map map);

}