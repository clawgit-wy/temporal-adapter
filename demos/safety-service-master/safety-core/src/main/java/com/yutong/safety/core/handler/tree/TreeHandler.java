package com.yutong.safety.core.handler.tree;

import com.alibaba.fastjson.JSON;
import com.yutong.safety.core.service.tree.DTreeService;
import com.yutong.safety.core.service.tree.ZTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TreeHandler {
    @Autowired
    private ZTreeService zTreeService;

    @Autowired
    private DTreeService dTreeService;

    /**
     * 获取全部树形数据
     * @param dt
     * @return
     */
    public Object getZTreeRoot(int dt) {
        String treeRoot = zTreeService.selectZTreeDataRoot(dt, 0);
        return JSON.parse(treeRoot);
    }

    /**
     * 获取全部树数据
     * @return
     */
    public Object getZTree() {
        String treeData = zTreeService.selectZTreeData(0);
        return JSON.parse(treeData);
    }
}
