package com.yutong.safety.core.handler.build;

import com.yutong.safety.common.vo.GroupTreeVO;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceCollectDO;
import com.yutong.safety.core.service.build.SafetyBuildDeviceCollectService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class SafetyBuildDeviceCollectHandler {
    @Autowired
    SafetyBuildDeviceCollectService safetyBuildDeviceCollectService;;

    public Object selectBuildTree(String key){
        List<GroupTreeVO> treeList = safetyBuildDeviceCollectService.selectBuildTree(key);
        long total = treeList.size();
        return new PageVO<GroupTreeVO>(total, treeList);
    }

    public Object selectAllGuard(String key){
        List<GroupTreeVO> treeList = safetyBuildDeviceCollectService.selectAllGuard(key);
        long total = treeList.size();
        return new PageVO<>(total, treeList);
    }

    public Object selectGuard(String guard){
        if (StringUtils.isBlank(guard)){return Collections.emptyList();}
        return safetyBuildDeviceCollectService.selectGuard(guard);
    }

    public Object saveBuildCollect(SafetyBuildDeviceCollectDO param){
        safetyBuildDeviceCollectService.saveBuildCollect(param);
        return null;
    }

    public Object selectBuildCollect(String userCode){
        if(StringUtils.isEmpty(userCode)){return null;}
        return safetyBuildDeviceCollectService.selectBuildCollect(userCode);
    }

    public Object selectBuildCollectDetail(String id){
        if(StringUtils.isEmpty(id)){return null;}
        return safetyBuildDeviceCollectService.selectBuildCollectDetail(id);
    }

    public void deleteBuildCollect(Integer id){
        safetyBuildDeviceCollectService.deleteBuildCollect(id);
    }
}
