package com.yutong.safety.core.service.tree;

import com.alibaba.fastjson.JSON;
import com.yutong.safety.core.db.entity.ztree.ZTree;
import com.yutong.safety.core.db.entity.ztree.ZTreeBase;
import com.yutong.safety.core.db.mapper.tree.TreeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ZTreeService {
    @Autowired
    private TreeMapper treeMapper;

    public String selectZTreeDataRoot(int dt, int all) {
        List<ZTreeBase> list;
        if(all == 0){
            list = this.treeMapper.selectPermZTreeAllJson();
        }else {
            list = this.treeMapper.selectPermZTreeJson();
        }
        ZTree child = new ZTree();
        child.setPId("");
        child.setId("0");
        child.setName("全部");
        child.setOpen(true);
        child.setIsParent("true");
        if(dt == 0){
            child.setChkDisabled(true);
        }
        return getZTreeChildrenRoot(list, child);
    }

    public String selectZTreeData(int all) {
        List<ZTreeBase> list;
        if(all == 0){
            list = this.treeMapper.selectPermZTreeAllJson();
        }else {
            list = this.treeMapper.selectPermZTreeJson();
        }
        return getZTreeChildren(list,"0",false);
    }

    public String getZTreeChildrenRoot(List<ZTreeBase> list, ZTree root) {
        List<ZTree> treeList = new LinkedList<>();
        treeList.add(root);
        //设置子元素
        for(int i=0;i<treeList.size();i++){
            getChildren(treeList.get(i),list);
        }

        String jsonString = "";
        if(treeList.size() > 0){
            jsonString = JSON.toJSONString(treeList);
        }
        return jsonString;
    }

    public String getZTreeChildren(List<ZTreeBase> list, String pId, boolean soleParent) {
        List<ZTree> treeList = new LinkedList<>();
        for(int i=0,len=list.size();i<len;i++){
            if(list.get(i).getPId().equals(pId)){
                list.get(i).setOpen(true);
                list.get(i).setIsParent("true");
                treeList.add(childTree(list.get(i)));
                list.remove(i);
                len--;
                i--;
                if(soleParent){
                    break;
                }
            }
        }
        //设置子元素
        for(int i=0;i<treeList.size();i++){
            getChildren(treeList.get(i),list);
        }

        String jsonString = "";
        if(treeList.size() > 0){
            jsonString = JSON.toJSONString(treeList);
        }
        return jsonString;
    }

    public String getZTreeToString(List<ZTreeBase> list) {
        String jsonString = "";
        if(list != null){
            jsonString = JSON.toJSONString(list);
        }
        return jsonString;
    }

    /*递归，设置子元素*/
    public void getChildren(ZTree zTree, List<ZTreeBase> list){
        String pId = zTree.getId();
        List<ZTree> childList = new LinkedList<>();
        for(int j=0,len=list.size();j<len;j++){
            String sId = list.get(j).getPId();
            if(pId.equals(sId)){
                childList.add(childTree(list.get(j)));
                list.remove(j);
                len--;
                j--;
            }
        }
        if(childList.size() > 0){
            zTree.setChildren(childList);
            zTree.setIsParent("true");
            for(int i=0;i<childList.size();i++){
                getChildren(childList.get(i), list);
            }
        }
    }

    public ZTree childTree(ZTreeBase treeBase){
        ZTree child = new ZTree();
        child.setId(treeBase.getId());
        child.setName(treeBase.getName());
        child.setPId(treeBase.getPId());
        child.setOpen(treeBase.getOpen());
        child.setChecked(treeBase.getChecked());
        child.setIsParent(treeBase.getIsParent());
        if(treeBase.getDt() == 0){
            child.setChkDisabled(true);
        }
        return child;
    }
}
