package com.yutong.safety.core.db.mapper.tree;


import com.yutong.safety.core.db.entity.dtree.DTreeBase;
import com.yutong.safety.core.db.entity.ztree.ZTreeBase;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TreeMapper {
    /*dtree 权限资源树*/
    List<Map> selectPermDTreeList();
    List<Map> selectPermDTreeAllList();
    List<DTreeBase> selectPermDTreeJson();
    List<DTreeBase> selectPermDTreeAllJson();

    /*ztree 权限资源树*/
    List<ZTreeBase> selectZTreeData();
    List<Map> selectPermZTreeList();
    List<Map> selectPermZTreeAllList();
    List<ZTreeBase> selectPermZTreeJson();
    List<ZTreeBase> selectPermZTreeAllJson();
}
