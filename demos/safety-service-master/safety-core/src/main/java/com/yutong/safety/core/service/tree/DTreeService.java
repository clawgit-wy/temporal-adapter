package com.yutong.safety.core.service.tree;

import com.yutong.safety.core.db.entity.dtree.DTree;
import com.yutong.safety.core.db.entity.dtree.DTreeBase;
import com.yutong.safety.core.db.entity.dtree.DTreeVo;
import com.yutong.safety.core.db.mapper.tree.TreeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 树形结构数据公用类
 * create gwcuiyx 2023.07.27
 */
@Service
public class DTreeService {
    @Autowired
    private TreeMapper treeMapper;

    /**
     * 获取权限资源表属性结构数据
     * @param dt
     * @return
     */
    public Map selectPermDTreeList(Integer dt) {
        List<Map> list;
        if(dt == 0){
            list = this.treeMapper.selectPermDTreeAllList();
        }else {
            list = this.treeMapper.selectPermDTreeList();
        }

        Map map = new HashMap();
        if(list != null){
            map.put("status",new HashMap(){{
                put("code",200);
                put("message","成功");
            }});
            map.put("data",list);
        }
        return map;
    }

    public DTreeVo selectPermDTreeJson(Integer dt) {
        List<DTreeBase> list;
        if(dt == 0){
            list = this.treeMapper.selectPermDTreeAllJson();
        }else {
            list = this.treeMapper.selectPermDTreeJson();
        }
        DTreeVo dTreeVo = getDTreeChildren(list);
        return dTreeVo;
    }

    public DTreeVo getDTreeChildren(List<DTreeBase> list) {
        List<DTree> treeList = new LinkedList<>();
        for(int i=0,len=list.size();i<len;i++){
            if(list.get(i).getParentId().equals("0")){
                treeList.add(childTree(list.get(i)));
                list.remove(i);
                len--;
                i--;
            }
        }
        //设置子元素
        for(int i=0;i<treeList.size();i++){
            getChildren(treeList.get(i),list);
        }

        DTreeVo dTreeVo = new DTreeVo();
        dTreeVo.setData(treeList);
        return dTreeVo;
    }

    /*递归，设置子元素*/
    public void getChildren(DTree dTree, List<DTreeBase> list){
        String pId = dTree.getId();
        List<DTree> childList = new LinkedList<>();
        for(int j=0,len=list.size();j<len;j++){
            String sId = list.get(j).getParentId();
            if(pId.equals(sId)){
                childList.add(childTree(list.get(j)));
                list.remove(j);
                len--;
                j--;
            }
        }
        if(childList.size() > 0){
            dTree.setChildren(childList);
            for(int i=0;i<childList.size();i++){
                getChildren(childList.get(i), list);
            }
        }
    }

    public DTree childTree(DTreeBase treeBase){
        DTree child = new DTree();
        child.setId(treeBase.getId());
        child.setTitle(treeBase.getTitle());
        child.setParentId(treeBase.getParentId());
        if(treeBase.getDt() == 0){
            child.setDisabled(true);
        }
        return child;
    }
}
