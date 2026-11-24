package com.yutong.safety.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.WebserviceUtil;
import com.yutong.safety.common.vo.GroupTreeVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDOExample;
import com.yutong.safety.core.db.entity.SafetyGroupInfoDO;
import com.yutong.safety.core.db.entity.SafetyGroupInfoDOExample;
import com.yutong.safety.core.db.mapper.SafetyCarOutRuleDOBizMapper;
import com.yutong.safety.core.db.mapper.SafetyEmployeeMessageDOMapper;
import com.yutong.safety.core.db.mapper.SafetyGroupInfoDOMapper;
import com.yutong.safety.core.db.mapper.SafetyGroupManageDOMapper;
import com.yutong.safety.core.dto.sys.GroupListParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gwxiexj
 * @since 2024-04-02 16:04:00
 */
@Service
public class SafetyGroupService {

    @Autowired
    private SafetyGroupInfoDOMapper safetyGroupInfoDOMapper;

    @Autowired
    private SafetyGroupManageDOMapper safetyGroupManageDOMapper;

    @Value("${iam.group.url}")
    private String iamGroupUrl;

    @Autowired
    private WebserviceUtil webserviceUtil;

    private static final String TARGET_ID = "ABYJS";

    private static final Boolean IS_ALL_FLAG = false;

    private static final String SUCCESS_FLAG = "success";

    private static final String TOP_PARENT_CODE = "50004002";

    @Autowired
    private SafetyCarOutRuleDOBizMapper safetyCarOutRuleDOBizMapper;

    @Autowired
    SafetyEmployeeMessageDOMapper safetyEmployeeMessageDOMapper;

    /**
     * 获取组织结构树-异步
     * @param key 上级code
     * @return 返回
     */
    public List<GroupTreeVO> selectGroupTree(String key) {
        LogUtils.info("查询组织结构树：key="+key);
        List<GroupTreeVO> treeList = new LinkedList<>();
        SafetyGroupInfoDOExample example = new SafetyGroupInfoDOExample();
        SafetyGroupInfoDOExample.Criteria criteria = example.createCriteria();

        if (StringUtils.isBlank(key)) {
            String GROUP_TOP_CODE = "50004002";
            criteria.andParentCodeEqualTo(GROUP_TOP_CODE);
            criteria.andCodeEqualTo(GROUP_TOP_CODE);
        } else {
            criteria.andParentCodeEqualTo(key);
            criteria.andCodeNotEqualTo(key);
        }
        List<SafetyGroupInfoDO> groupList = this.safetyGroupInfoDOMapper.selectByExample(example);
        for (SafetyGroupInfoDO safetyGroupInfo : groupList) {
            GroupTreeVO dto = new GroupTreeVO();
            dto.setKey(safetyGroupInfo.getCode());
            dto.setTitle(safetyGroupInfo.getName());
            dto.setParent(safetyGroupInfo.getParentCode());
            dto.setCodePath(safetyGroupInfo.getOrgCodePath());
            dto.setNamePath(safetyGroupInfo.getOrgNamePath());
            treeList.add(dto);
        }
        return treeList;
    }

    public List<GroupTreeVO> selectGroupTreeWithPeople(String key) {
        LogUtils.info("查询组织结构树：key="+key);
        List<GroupTreeVO> treeList = new LinkedList<>();
        SafetyGroupInfoDOExample example = new SafetyGroupInfoDOExample();
        SafetyGroupInfoDOExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("Y");

        if (StringUtils.isBlank(key)) {
            key = "50004002";
            String GROUP_TOP_CODE = "50004002";
            criteria.andParentCodeEqualTo(GROUP_TOP_CODE);
            criteria.andCodeEqualTo(GROUP_TOP_CODE);
        } else {
            criteria.andParentCodeEqualTo(key);
            criteria.andCodeNotEqualTo(key);
        }

        List<SafetyGroupInfoDO> groupList = this.safetyGroupInfoDOMapper.selectByExample(example);
        for (SafetyGroupInfoDO safetyGroupInfo : groupList) {
            GroupTreeVO dto = new GroupTreeVO();
            dto.setKey(safetyGroupInfo.getCode());
            dto.setTitle(safetyGroupInfo.getName());
            dto.setParent(safetyGroupInfo.getParentCode());
            dto.setCodePath(safetyGroupInfo.getOrgCodePath());
            dto.setNamePath(safetyGroupInfo.getOrgNamePath());
            treeList.add(dto);
        }

        //组织的集合为空的时候查询其下面的人员
        SafetyEmployeeMessageDOExample messageDOExample = new SafetyEmployeeMessageDOExample();
        SafetyEmployeeMessageDOExample.Criteria messageCriteria = messageDOExample.createCriteria();
        messageCriteria.andOrgCodeEqualTo(key);
        messageCriteria.andStatusEqualTo("NORMAL");
        messageDOExample.setOrderByClause(" FIELD(SUBSTRING(emp_no, 1, 1), '9', '0', '6', '5') ");
        List<SafetyEmployeeMessageDO> safetyEmployeeMessageDOS = safetyEmployeeMessageDOMapper.selectByExample(messageDOExample);

        for (SafetyEmployeeMessageDO en : safetyEmployeeMessageDOS) {
            GroupTreeVO dto = new GroupTreeVO();
            dto.setKey(en.getEmpNo());
            dto.setTitle(en.getEmpName());
            dto.setParent(en.getOrgCode());
            dto.setCodePath(en.getOrgCodePath());
            dto.setNamePath(en.getOrgNamePath());
            dto.setNodeType(2);
            dto.setDataId(en.getEmail());
            treeList.add(dto);
        }
        return treeList;
    }

    public List<GroupTreeVO> selectGroupTreeWithPeopleBelong(String key,boolean isFirst) {
        LogUtils.info("查询组织结构树：key="+key);
        List<GroupTreeVO> treeList = new LinkedList<>();
        SafetyGroupInfoDOExample example = new SafetyGroupInfoDOExample();
        SafetyGroupInfoDOExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("Y");

        if (isFirst) {
            criteria.andCodeEqualTo(key);
        }else{
            criteria.andParentCodeEqualTo(key);
            criteria.andCodeNotEqualTo(key);
        }

        List<SafetyGroupInfoDO> groupList = this.safetyGroupInfoDOMapper.selectByExample(example);
        for (SafetyGroupInfoDO safetyGroupInfo : groupList) {
            GroupTreeVO dto = new GroupTreeVO();
            dto.setKey(safetyGroupInfo.getCode());
            dto.setTitle(safetyGroupInfo.getName());
            dto.setParent(safetyGroupInfo.getParentCode());
            dto.setCodePath(safetyGroupInfo.getOrgCodePath());
            dto.setNamePath(safetyGroupInfo.getOrgNamePath());
            treeList.add(dto);
        }

        if (isFirst) {
            return treeList;
        }

        //组织的集合为空的时候查询其下面的人员
        SafetyEmployeeMessageDOExample messageDOExample = new SafetyEmployeeMessageDOExample();
        SafetyEmployeeMessageDOExample.Criteria messageCriteria = messageDOExample.createCriteria();
        messageCriteria.andOrgCodeEqualTo(key);
        messageCriteria.andStatusEqualTo("NORMAL");
        messageDOExample.setOrderByClause(" FIELD(SUBSTRING(emp_no, 1, 1), '9', '0', '6', '5') ");
        List<SafetyEmployeeMessageDO> safetyEmployeeMessageDOS = safetyEmployeeMessageDOMapper.selectByExample(messageDOExample);

        for (SafetyEmployeeMessageDO en : safetyEmployeeMessageDOS) {
            GroupTreeVO dto = new GroupTreeVO();
            dto.setKey(en.getEmpNo());
            dto.setTitle(en.getEmpName());
            dto.setParent(en.getOrgCode());
            dto.setCodePath(en.getOrgCodePath());
            dto.setNamePath(en.getOrgNamePath());
            dto.setNodeType(2);
            dto.setDataId(en.getEmail());
            treeList.add(dto);
        }
        return treeList;
    }

    /**
     * 同步组织结构任务
     * @param isAll 是否查询全部
     * @param dateTime 修改时间
     */
    public void taskGroup(Boolean isAll, String dateTime){
        if (StringUtils.isEmpty(dateTime)) {
            //前一天
            dateTime = DateUtils.format(LocalDateTime.now().minusDays(1).toDate(), 1);
        }
        if(isAll == null){
            isAll = IS_ALL_FLAG;
        }
        JSONObject resultJSON;
        JSONObject groupJson;
        JSONArray records;
        int pageSize;
        //定义url
        String url = String.format(iamGroupUrl,TARGET_ID, isAll, dateTime, 1);
        //根据url从中台获取增量信息
        String result = webserviceUtil.sendHttpForGet(url);

        if (StringUtils.isEmpty(result)) {
            LogUtils.info("中台接口返回信息为空");
            return;
        }
        groupJson=(JSONObject) JsonUtils.parse(result);
        if (!groupJson.containsKey(SUCCESS_FLAG) || !groupJson.getBoolean(SUCCESS_FLAG)) {
            LogUtils.info("中台获取数据失败！返回值="+result);
            return;
        }
        //获取result对象
        resultJSON = groupJson.getJSONObject("result");
        if (ObjectUtils.isEmpty(resultJSON)){
            LogUtils.info("获取result对象为空！");
            return;
        }
        //获取总页码数
        pageSize = resultJSON.getInteger("pages");
        if (pageSize < 1) {
            return;
        }
        JSONArray firstRecords = resultJSON.getJSONArray("records");
        if(firstRecords == null || firstRecords.size() <= 0){
            return;
        }
        try {
            //处理第一页数据
            taskInsertGroupCopy(firstRecords);
        }catch (Exception e){
            LogUtils.error("批量保存异常",e);
        }
        if(pageSize > 1){
            for (int i = 2; i <=pageSize ; i++) {
                url = String.format(iamGroupUrl,TARGET_ID, isAll, dateTime, i);
                String result1 = webserviceUtil.sendHttpForGet(url);
                if (!StringUtils.isNotEmpty(result1)) {
                    continue;
                }
                groupJson = (JSONObject) JsonUtils.parse(result1);
                if (!groupJson.containsKey(SUCCESS_FLAG) || !groupJson.getBoolean(SUCCESS_FLAG)) {
                    LogUtils.info("中台获取数据失败！第"+i+"页返回值="+result1);
                    continue;
                }
                resultJSON = groupJson.getJSONObject("result");
                records = resultJSON.getJSONArray("records");
                try {
                    //处理第一页数据
                    taskInsertGroupCopy(records);
                }catch (Exception e){
                    LogUtils.error("批量保存异常",e);
                }
            }
        }
    }

    /**
     * 根据身份证号获取数据
     * @param code 组织编码
     * @return 返回值
     */
    public SafetyGroupInfoDO getGroupByCode(String code){
        SafetyGroupInfoDOExample example = new SafetyGroupInfoDOExample();
        SafetyGroupInfoDOExample.Criteria criteria = example.createCriteria();
        criteria.andCodeEqualTo(code);
        List<SafetyGroupInfoDO> list = safetyGroupInfoDOMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 组装数据
     * @param jsonArray 查询到的组织结构
     */
    public void taskInsertGroup(JSONArray jsonArray) throws Exception{
        if(jsonArray == null){
            return;
        }
        //组装数据
        List<SafetyGroupInfoDO> insertList = Lists.newArrayList();
        List<SafetyGroupInfoDO> updateList = Lists.newArrayList();
        JSONObject record;
        SafetyGroupInfoDO groupInfoDO;
        SafetyGroupInfoDO safetyGroupInfoDO;
        for (int i = 0; i < jsonArray.size(); i++) {
            record = jsonArray.getJSONObject(i);
            groupInfoDO = new SafetyGroupInfoDO();
            //组织结构编码
            String code = record.getString("code");
            //组织结构名称
            String name = record.getString("name");
            if (StringUtils.isEmpty(code) || StringUtils.isEmpty(name)) {
                LogUtils.error("code/name为空不做同步:{}{}",code,name);
                continue;
            }
            safetyGroupInfoDO = getGroupByCode(code);
            //存在时更新，设置ID值
            if(safetyGroupInfoDO != null){
                groupInfoDO.setId(safetyGroupInfoDO.getId());
                groupInfoDO.setCreateTime(new Date());
            }
            //根据身份证检查是否存在
            groupInfoDO.setCode(code);
            groupInfoDO.setName(name);
            groupInfoDO.setParentCode(record.getString("parentCode"));
            groupInfoDO.setUpdateTime(new Date());
            groupInfoDO.setLevelCode(record.getString("levelCode") == null ?"":record.getString("levelCode"));
            groupInfoDO.setLevelName(record.getString("levelName") == null ?"":record.getString("levelName"));
            groupInfoDO.setDeptShort(record.getInteger("deptShort"));
            groupInfoDO.setOrgCodePath(record.getString("orgCodePath") == null ?"":record.getString("orgCodePath"));
            groupInfoDO.setOrgNamePath(record.getString("orgNamePath") == null ?"":record.getString("orgNamePath"));
            groupInfoDO.setStatus(record.getString("status"));

            if(groupInfoDO.getId() != null){
                updateList.add(groupInfoDO);
            }else{
                insertList.add(groupInfoDO);
            }
        }
        if (CollectionUtils.isNotEmpty(insertList)) {
            safetyGroupManageDOMapper.batchInsert(insertList);
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            for(SafetyGroupInfoDO groupDO:updateList){
                safetyGroupInfoDOMapper.updateByPrimaryKey(groupDO);
            }
        }

    }

    public void taskInsertGroupCopy(JSONArray jsonArray) throws Exception{
        if(jsonArray == null){
            return;
        }
        //组装数据
        List<SafetyGroupInfoDO> insertList = Lists.newArrayList();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject record = jsonArray.getJSONObject(i);
            //组织结构编码
            String code = record.getString("code");
            //组织结构名称
            String name = record.getString("name");
            if (StringUtils.isEmpty(code) || StringUtils.isEmpty(name)) {
                LogUtils.error("code/name为空不做同步:{}{}",code,name);
                continue;
            }
            SafetyGroupInfoDO groupInfoDO = convertCopyBean(record);
            insertList.add(groupInfoDO);
        }
        safetyCarOutRuleDOBizMapper.batchUpdate(insertList);
    }

    private SafetyGroupInfoDO convertCopyBean(JSONObject record){
        SafetyGroupInfoDO copyDO = new SafetyGroupInfoDO();

        copyDO.setCode(record.getString("code"));
        copyDO.setName(record.getString("name"));
        copyDO.setParentCode(record.getString("parentCode"));
        copyDO.setLevelCode(record.getString("levelCode") == null ?"":record.getString("levelCode"));
        copyDO.setLevelName(record.getString("levelName") == null ?"":record.getString("levelName"));
        copyDO.setDeptShort(record.getInteger("deptShort"));
        copyDO.setOrgCodePath(record.getString("orgCodePath") == null ?"":record.getString("orgCodePath"));
        copyDO.setOrgNamePath(record.getString("orgNamePath") == null ?"":record.getString("orgNamePath"));
        copyDO.setStatus(record.getString("status"));

        return copyDO;
    }

    /**
     * 获取组织结构树-非异步
     * @return 返回
     */
    public List<GroupTreeVO> selectGroupTreeNonAsyn() {
        LogUtils.info("非异步查询组织结构树");
        List<GroupTreeVO> treeList = new LinkedList<>();
        SafetyGroupInfoDOExample example = new SafetyGroupInfoDOExample();
        example.createCriteria().andStatusEqualTo("Y");

        List<SafetyGroupInfoDO> groupList = this.safetyGroupInfoDOMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(groupList)){
            return treeList;
        }
        //根据上级部门编码进行分组
        Map<String,List<SafetyGroupInfoDO>> groupInfoMap = groupList.stream()
                        .filter(e->!StringUtils.isBlank(e.getParentCode()))
                .collect(Collectors.groupingBy(e->e.getParentCode()));
        List<SafetyGroupInfoDO> oneLevelList = groupInfoMap.get(TOP_PARENT_CODE);
        if(CollectionUtils.isEmpty(oneLevelList)){
            return treeList;
        }
        //code和parentCode都等于TOP_PARENT_CODE说明该信息为根
        GroupTreeVO topGroup = new GroupTreeVO();
        for(SafetyGroupInfoDO groupInfoDO:oneLevelList){
            if(groupInfoDO.getCode().equals(TOP_PARENT_CODE)){
                continue;
            }
            GroupTreeVO curTree = new GroupTreeVO();
            curTree.setKey(groupInfoDO.getCode());
            curTree.setTitle(groupInfoDO.getName());
            curTree.setCodePath(groupInfoDO.getOrgCodePath());
            curTree.setNamePath(groupInfoDO.getOrgNamePath());
            curTree.setParent(groupInfoDO.getParentCode());
            //递归查子
            curTree.setChildren(recursionChildren(groupInfoDO.getCode(),groupInfoMap));
            treeList.add(curTree);
        }
        treeList.add(topGroup);

        return treeList;
    }

    public List<GroupTreeVO> selectGroupTreeContain(String orgCodePath) {
        LogUtils.info("获取组织结构树-非异步(查询子组织)");
        List<GroupTreeVO> treeList = new LinkedList<>();
        SafetyGroupInfoDOExample example = new SafetyGroupInfoDOExample();
        example.createCriteria().andStatusEqualTo("Y")
                .andOrgCodePathLike(orgCodePath+"%");

        List<SafetyGroupInfoDO> groupList = this.safetyGroupInfoDOMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(groupList)){
            return treeList;
        }
        //根据上级部门编码进行分组
        Map<String,List<SafetyGroupInfoDO>> groupInfoMap = groupList.stream().collect(Collectors.groupingBy(e->e.getParentCode()));

        //查父编码
        SafetyGroupInfoDOExample example1 = new SafetyGroupInfoDOExample();
        example1.createCriteria().andStatusEqualTo("Y")
                .andOrgCodePathEqualTo(orgCodePath);

        List<SafetyGroupInfoDO> groupList1 = this.safetyGroupInfoDOMapper.selectByExample(example1);
        String parentCode = groupList1.get(0).getParentCode();

        List<SafetyGroupInfoDO> oneLevelList = groupInfoMap.get(parentCode);
        if(CollectionUtils.isEmpty(oneLevelList)){
            return treeList;
        }

        GroupTreeVO curTree = new GroupTreeVO();
        curTree.setKey(groupList1.get(0).getCode());
        curTree.setTitle(groupList1.get(0).getName());
        curTree.setCodePath(groupList1.get(0).getOrgCodePath());
        curTree.setNamePath(groupList1.get(0).getOrgNamePath());
        curTree.setParent(groupList1.get(0).getParentCode());
        //递归查子
        curTree.setChildren(recursionChildren(groupList1.get(0).getCode(),groupInfoMap));
        treeList.add(curTree);

        return treeList;
    }

    /**
     * 递归处理子树结构
     * @param parentCode 上级组织编码
     * @param groupInfoMap 组织编码组
     * @return
     */
    private List<GroupTreeVO> recursionChildren(String parentCode,Map<String,List<SafetyGroupInfoDO>> groupInfoMap){
        List<GroupTreeVO> subTrees = new ArrayList<>();
        List<SafetyGroupInfoDO> subGroups = groupInfoMap.get(parentCode);
        if(CollectionUtils.isEmpty(subGroups)){
            return subTrees;
        }
        for(SafetyGroupInfoDO infoDO:subGroups){
            GroupTreeVO curTree = new GroupTreeVO();
            curTree.setKey(infoDO.getCode());
            curTree.setTitle(infoDO.getName());
            curTree.setCodePath(infoDO.getOrgCodePath());
            curTree.setNamePath(infoDO.getOrgNamePath());
            curTree.setParent(infoDO.getParentCode());
            curTree.setChildren(recursionChildren(infoDO.getCode(),groupInfoMap));
            subTrees.add(curTree);
        }
        return subTrees;
    }

    public Object getDepartName(String keyWords,String orgCode){
        return safetyGroupInfoDOMapper.getDepartName(keyWords,orgCode);
    }

    /**
     * 查询组织
     * @param listParam
     * @return
     */
    public List<SafetyGroupInfoDO> selectGroupList(GroupListParam listParam){
        SafetyGroupInfoDOExample example = new SafetyGroupInfoDOExample();
        SafetyGroupInfoDOExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(listParam.getStatus())){
            criteria.andStatusEqualTo(listParam.getStatus());
        }
        if(StringUtils.isNotBlank(listParam.getUpdateTimeBegin())){
            criteria.andUpdateTimeGreaterThanOrEqualTo(DateUtils.parse(listParam.getUpdateTimeBegin(),2));
        }
        if(StringUtils.isNotBlank(listParam.getUpdateTimeEnd())){
            criteria.andUpdateTimeLessThanOrEqualTo(DateUtils.parse(listParam.getUpdateTimeEnd(),2));
        }
        return safetyGroupInfoDOMapper.selectByExample(example);
    }

    public List<SafetyGroupInfoDO> getListByCode(List<String> orgCode) {
        SafetyGroupInfoDOExample example = new SafetyGroupInfoDOExample();
        SafetyGroupInfoDOExample.Criteria criteria = example.createCriteria().andStatusEqualTo("Y");
        if (CollectionUtils.isNotEmpty(orgCode)) {
            criteria.andCodeIn(orgCode);
        }
        return safetyGroupInfoDOMapper.selectByExample(example);
    }
    public List<SafetyGroupInfoDO> getListByName(String empName) {
        SafetyGroupInfoDOExample example = new SafetyGroupInfoDOExample();
        example.createCriteria().andStatusEqualTo("Y").andNameLike(empName);
        return safetyGroupInfoDOMapper.selectByExample(example);
    }

    /**
     * 获取当千组织ID下的所有子组织信息 （依赖与数据库递归取数据库查询，层级越高。耗时越长）
     * @param key 组织ID
     * @return 子组织信息 返回的数据全部在同一级，没有放在子级中
     */
    public List<GroupTreeVO> selectGroupTreeAll(String key) {
        if (StringUtils.isBlank(key)) return Lists.newArrayList();
        List<GroupTreeVO> treeList = new LinkedList<>();
        SafetyGroupInfoDOExample example = new SafetyGroupInfoDOExample();
        SafetyGroupInfoDOExample.Criteria criteria = example.createCriteria();
        criteria.andParentCodeEqualTo(key);
        criteria.andCodeNotEqualTo(key);
        List<SafetyGroupInfoDO> groupList = this.safetyGroupInfoDOMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(groupList)) return Lists.newArrayList();
        for (SafetyGroupInfoDO safetyGroupInfo : groupList) {
            GroupTreeVO dto = new GroupTreeVO();
            dto.setKey(safetyGroupInfo.getCode());
            treeList.addAll(this.selectGroupTreeAll(safetyGroupInfo.getCode()));
            dto.setTitle(safetyGroupInfo.getName());
            dto.setParent(safetyGroupInfo.getParentCode());
            dto.setCodePath(safetyGroupInfo.getOrgCodePath());
            dto.setNamePath(safetyGroupInfo.getOrgNamePath());
            treeList.add(dto);
        }
        return treeList;
    }


    /**
     * 获取当千组织ID下的所有子组织信息(优化，在代码中处理，)
     * @param key 组织ID
     * @return 子组织信息 返回的数据全部在同一级，没有放在子级中
     */
    public List<GroupTreeVO> selectGroupAll(String key) {
        List<GroupTreeVO> treeList = new LinkedList<>();
        if (StringUtils.isBlank(key)) return treeList;

        // 获取所有的组织信息
        SafetyGroupInfoDOExample example = new SafetyGroupInfoDOExample();
        example.createCriteria().andStatusEqualTo("Y");
        List<SafetyGroupInfoDO> groupList = this.safetyGroupInfoDOMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(groupList)) {
            return treeList;
        }
        Map<String, List<SafetyGroupInfoDO>> groupInfoMap = groupList.stream()
                .filter(e -> StringUtils.isNotBlank(e.getParentCode()))
                .collect(Collectors.groupingBy(SafetyGroupInfoDO::getParentCode));

        treeList.addAll(getChildren(key, groupInfoMap));

        return treeList;
    }

    private List<GroupTreeVO> getChildren(String parentCode, Map<String, List<SafetyGroupInfoDO>> groupInfoMap) {
        List<SafetyGroupInfoDO> childGroups = groupInfoMap.get(parentCode);
        if (CollectionUtils.isEmpty(childGroups)) {
            return new ArrayList<>();
        }
        // 过滤掉与父节点代码相同的条目（避免自循环）
        List<SafetyGroupInfoDO> filteredChildGroups = childGroups.stream()
                .filter(li -> !parentCode.equals(li.getCode()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filteredChildGroups)) {
            return new ArrayList<>();
        }
        List<GroupTreeVO> subTrees = new ArrayList<>();

        for (SafetyGroupInfoDO safetyGroupInfo : filteredChildGroups) {
            GroupTreeVO dto = new GroupTreeVO();
            dto.setKey(safetyGroupInfo.getCode());
            dto.setTitle(safetyGroupInfo.getName());
            dto.setParent(safetyGroupInfo.getParentCode());
            dto.setCodePath(safetyGroupInfo.getOrgCodePath());
            dto.setNamePath(safetyGroupInfo.getOrgNamePath());
            subTrees.addAll(this.getChildren(safetyGroupInfo.getCode(), groupInfoMap));
            subTrees.add(dto);
        }
        return subTrees;
    }

    /**
     * 获取当前部门的所有子部门 和当前部门
     *
     * @param empCode 当前部门编码
     * @return 返回所有子部门编码和当前部门编码
     */
    public List<String> getEmpCodeList(String empCode) {
        // 作业部门匹配
        List<String> codeList = new ArrayList<>();
        if (StringUtils.isNotBlank(empCode)) {
            codeList.add(empCode);
            // 获取所有子组织编码数据
            List<GroupTreeVO> groupTreeVOS = selectGroupAll(empCode);
            if (!CollectionUtils.isEmpty(groupTreeVOS)) {
                codeList.addAll(groupTreeVOS.stream().map(GroupTreeVO::getKey).collect(Collectors.toList()));
            }
        }
        return codeList;
    }

    public List<SafetyGroupInfoDO> selectUpdateList(Date start, Date end) {
        SafetyGroupInfoDOExample example = new SafetyGroupInfoDOExample();
        example.createCriteria().andUpdateTimeBetween(start, end);;
        return this.safetyGroupInfoDOMapper.selectByExample(example);
    }
}
