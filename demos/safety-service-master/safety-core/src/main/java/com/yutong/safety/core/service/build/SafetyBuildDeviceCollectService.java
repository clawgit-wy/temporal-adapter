package com.yutong.safety.core.service.build;

import com.yutong.safety.common.vo.GroupTreeVO;
import com.yutong.safety.core.db.entity.SafetyFieldManageDO;
import com.yutong.safety.core.db.entity.SafetyFieldManageDOExample;
import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceCollectDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceCollectDOExample;
import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceDOExample;
import com.yutong.safety.core.db.mapper.SafetyFieldManageDOMapper;
import com.yutong.safety.core.db.mapper.build.SafetyBuildDeviceCollectDOMapper;
import com.yutong.safety.core.db.mapper.build.SafetyBuildDeviceDOBizMapper;
import com.yutong.safety.core.db.mapper.build.SafetyBuildDeviceDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SafetyBuildDeviceCollectService {

    @Autowired
    SafetyBuildDeviceCollectDOMapper safetyBuildDeviceCollectDOMapper;
    @Autowired
    SafetyBuildDeviceDOMapper safetyBuildDeviceDOMapper;
    @Autowired
    SafetyBuildDeviceDOBizMapper safetyBuildDeviceDOBizMapper;
    @Autowired
    SafetyFieldManageDOMapper safetyFieldManageDOMapper;

    public List<GroupTreeVO> selectBuildTree(String key) {
        if(StringUtils.isBlank(key)){
            key = "buildingAccessFactoryManage";
        }

        List<GroupTreeVO> treeList = new LinkedList<>();
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andFieldTypeEqualTo(key);
        criteria.andDeleteFalgEqualTo(false);
        criteria.andRemarkIsNull();
        example.setOrderByClause(" field_sort ");
        List<SafetyFieldManageDO> list = safetyFieldManageDOMapper.selectByExample(example);

        for (SafetyFieldManageDO entity : list) {
            GroupTreeVO dto = new GroupTreeVO();
            dto.setKey(entity.getFieldCode());
            dto.setTitle(entity.getFieldName());
            dto.setParent(entity.getFieldType());
            dto.setNodeType(1);
            treeList.add(dto);
        }

        //查询门禁
        SafetyBuildDeviceDOExample deviceDOExample = new SafetyBuildDeviceDOExample();
        SafetyBuildDeviceDOExample.Criteria criteria2 = deviceDOExample.createCriteria();
        criteria2.andAnBuildingCodeEqualTo(key);
        criteria2.andRemoveFlagEqualTo("0"); //未移除的设备
        criteria2.andDeviceStatusEqualTo("1"); //启用状态
        criteria2.andIsDeleteEqualTo("0"); // 没有删除
        deviceDOExample.setOrderByClause(" AN_SHORT asc ");

        List<SafetyBuildDeviceDO> deviceDOList = safetyBuildDeviceDOMapper.selectByExample(deviceDOExample);
        for (SafetyBuildDeviceDO entity : deviceDOList) {
            GroupTreeVO dto = new GroupTreeVO();
            dto.setKey(entity.getDeviceCode());
            dto.setTitle(StringUtils.isBlank(entity.getAnDeviceName())?entity.getDeviceName():entity.getAnDeviceName());
            dto.setParent(key);
            dto.setDataId(entity.getId());
            dto.setNodeType(2);
            treeList.add(dto);
        }

        return treeList;
    }

    public List<GroupTreeVO> selectAllGuard(String key) {
        List<GroupTreeVO> treeList = new LinkedList<>();
        selectGuard(treeList,key);
        return treeList;
    }

    /**
     * 查询所有的门禁
     * @param treeList
     * @param key
     */
    public void selectGuard(List<GroupTreeVO> treeList,String key) {
        if(StringUtils.isBlank(key)){
            key = "buildingAccessFactoryManage";
        }

        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andFieldTypeEqualTo(key);
        criteria.andDeleteFalgEqualTo(false);
        criteria.andRemarkIsNull();
        example.setOrderByClause(" field_sort ");
        List<SafetyFieldManageDO> list = safetyFieldManageDOMapper.selectByExample(example);

        for (SafetyFieldManageDO entity : list) {
            if(StringUtils.isBlank(entity.getFieldCode())){continue;}
            selectGuard(treeList,entity.getFieldCode());
        }

        //查询门禁
        SafetyBuildDeviceDOExample deviceDOExample = new SafetyBuildDeviceDOExample();
        SafetyBuildDeviceDOExample.Criteria criteria2 = deviceDOExample.createCriteria();
        criteria2.andAnBuildingCodeEqualTo(key);
        criteria2.andIsDeleteEqualTo("0"); //未删除的设备
        criteria2.andRemoveFlagEqualTo("0"); //未移除的设备
        criteria2.andDeviceStatusEqualTo("1"); //启用状态
        deviceDOExample.setOrderByClause(" AN_SHORT asc ");

        List<SafetyBuildDeviceDO> deviceDOList = safetyBuildDeviceDOMapper.selectByExample(deviceDOExample);
        for (SafetyBuildDeviceDO entity : deviceDOList) {
            GroupTreeVO dto = new GroupTreeVO();
            dto.setKey(entity.getDeviceCode());
            dto.setTitle(StringUtils.isBlank(entity.getAnDeviceName())?entity.getDeviceName():entity.getAnDeviceName());
            dto.setParent(key);
            dto.setDataId(entity.getId());
            dto.setNodeType(2);
            treeList.add(dto);
        }
    }

    public List<SafetyBuildDeviceDO> selectGuard(String guard){
        SafetyBuildDeviceDO param = new SafetyBuildDeviceDO();
        param.setDeviceName(guard);

        return safetyBuildDeviceDOBizMapper.getListByCondition(param);
    }

    public Object saveBuildCollect(SafetyBuildDeviceCollectDO param){
        param.setUpdateTime(new Date());
        safetyBuildDeviceCollectDOMapper.insertSelective(param);
        return null;
    }

    public List<SafetyBuildDeviceCollectDO> selectBuildCollect(String userCode){
        SafetyBuildDeviceCollectDOExample e = new SafetyBuildDeviceCollectDOExample();
        SafetyBuildDeviceCollectDOExample.Criteria criteria = e.createCriteria();
        criteria.andCollectUserCodeEqualTo(userCode);
        e.setOrderByClause(" update_time desc");

        return safetyBuildDeviceCollectDOMapper.selectByExample(e);
    }

    public List<GroupTreeVO> selectBuildCollectDetail(String id){
        List<SafetyBuildDeviceDO> deviceDOList = safetyBuildDeviceDOBizMapper.selectBuildCollectDetail(id);
        List<GroupTreeVO> treeList = new ArrayList<>();
        for (SafetyBuildDeviceDO entity : deviceDOList) {
            GroupTreeVO dto = new GroupTreeVO();
            dto.setKey(entity.getDeviceCode());
            dto.setTitle(entity.getDeviceName());
            dto.setDataId(entity.getId());
            dto.setNodeType(2);
            treeList.add(dto);
        }
        return treeList;
    }

    public void deleteBuildCollect(Integer id){
        SafetyBuildDeviceCollectDOExample e = new SafetyBuildDeviceCollectDOExample();
        SafetyBuildDeviceCollectDOExample.Criteria criteria = e.createCriteria();
        criteria.andIdEqualTo(id);
        safetyBuildDeviceCollectDOMapper.deleteByExample(e);
    }

    /**
     * 根据厂区名称获取对应的设备编码
     * @param factoryNameList 厂区集合
     * @return 设备编码集合
     */
    public List<String> getListByFactoryName(List<String> factoryNameList) {
        return safetyBuildDeviceDOBizMapper.getListByFactoryName(factoryNameList);
    }
}
