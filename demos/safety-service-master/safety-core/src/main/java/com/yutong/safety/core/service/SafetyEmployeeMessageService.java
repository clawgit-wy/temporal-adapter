package com.yutong.safety.core.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.FireInspectionEnum;
import com.yutong.safety.common.param.web.EmployeeMessageListParam;
import com.yutong.safety.common.param.web.EmployeeMessageUpdateParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.WebserviceUtil;
import com.yutong.safety.common.vo.web.HkMiddleTableVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDOExample;
import com.yutong.safety.core.db.entity.emp.common.SafetyEmployeeConstants;
import com.yutong.safety.core.db.entity.result.DictItemRt;
import com.yutong.safety.core.db.mapper.SafetyEmployeeMessageDOBizMapper;
import com.yutong.safety.core.db.mapper.SafetyEmployeeMessageDOMapper;
import com.yutong.safety.core.db.mapper.SafetyZCustomUtilDOBizMapper;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.handler.visitor.SafetyVisitorAuthorizationApprovalHandler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author gwcuiyx
 * @date 2024-4-2 15:50
 */
@Service
public class SafetyEmployeeMessageService {

    @Autowired
    private SafetyEmployeeMessageDOMapper safetyEmployeeMessageDOMapper;

    @Value("${iam.employee.url}")
    private String iamEmpInfoUrl;

    @Value("${iam.emplyee.byWorkNo.url}")
    private String iamEmployForWorkNoUrl;

    @Autowired
    private WebserviceUtil webserviceUtil;

    @Autowired
    private SafetyEmployeeMessageDOBizMapper safetyEmployeeMessageDOBizMapper;

    @Autowired
    private SafetyZCustomUtilDOBizMapper safetyZCustomUtilDOBizMapper;

    @Resource
    private SafetyBaseHandler safetyBaseHandler;

    @Resource
    private SafetyDictItemService dictItemService;

    @Resource
    private SafetyVisitorAuthorizationApprovalHandler safetyVisitorAuthorizationApprovalHandler;

    /**
     * 分页查询
     * @param param 检索条件
     * @return 结果集
     */
    public List<SafetyEmployeeMessageDO> pageList(EmployeeMessageListParam param) {
        SafetyEmployeeMessageDOExample example = convertExample(param);
        if(StringUtils.isNotBlank(param.getEmpName())){
            example.setOrderByClause(" emp_name asc ");
        }else {
            example.setOrderByClause(" create_time desc ");
        }
        if(param.getPageFlag() != null && param.getPageFlag()){
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }
        return safetyEmployeeMessageDOMapper.selectByExample(example);
    }

    /**
     *
     * @param empNos 数组员工号
     * @return 员工信息台账
     */
    public List<SafetyEmployeeMessageDO> selectListForObject(JSONArray empNos){
        List<SafetyEmployeeMessageDO> empInfos= new ArrayList<>();
        List<String> datas = JSON.parseArray(empNos.toString(), String.class);
        List<List<String>> partitions = com.google.common.collect.Lists.partition(datas, 1000);
        EmployeeMessageListParam param = new EmployeeMessageListParam();
        for (List<String> partition : partitions) {
            param.setEmpNoList(partition);
            empInfos.addAll(pageList(param));
        }
        return empInfos;
    }

    /**
     * 总条数
     * @param param
     * @return
     */
    public long pageListCount(EmployeeMessageListParam param) {
        SafetyEmployeeMessageDOExample example = convertExample(param);
        return safetyEmployeeMessageDOMapper.countByExample(example);
    }

    /**
     * 数据导出
     * @param param
     * @return
     */
    public List<SafetyEmployeeMessageDO> export(EmployeeMessageListParam param) {
        SafetyEmployeeMessageDOExample example = convertExample(param);
        example.setOrderByClause(" create_time desc ");
        return safetyEmployeeMessageDOMapper.selectByExample(example);
    }

    /**
     * 查询参数转example
     * @param param 入参
     * @return example
     */
    public SafetyEmployeeMessageDOExample convertExample(EmployeeMessageListParam param){
        SafetyEmployeeMessageDOExample example = new SafetyEmployeeMessageDOExample();
        SafetyEmployeeMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(Constants.NORMAL);
        if(StringUtils.isNotBlank(param.getFullEmpNo())){
            criteria.andEmpNoEqualTo(param.getFullEmpNo());
        }
        if (StringUtils.isNotEmpty(param.getEmpNo())) {
            criteria.andEmpNoLike("%"+param.getEmpNo()+"%");
        }
        if (StringUtils.isNotEmpty(param.getEmpName())) {
            criteria.andEmpNameLike(param.getEmpName()+"%");
        }
        if (StringUtils.isNotEmpty(param.getPhoneNo())) {
            criteria.andPhoneNoLike("%"+param.getPhoneNo()+"%");
        }
        if (StringUtils.isNotEmpty(param.getRemark())) {
            criteria.andRemarkLike("%"+param.getRemark()+"%");
        }
        if (StringUtils.isNotEmpty(param.getUserType())) {
            criteria.andUserTypeEqualTo(param.getUserType());
        }
        if(!ObjectUtils.isEmpty(param.getEmpNoList())){
            criteria.andEmpNoIn(param.getEmpNoList());
        }
        if (StringUtils.isNotEmpty(param.getPositionType())) {
            if ("空".equals(param.getPositionType())) {
                criteria.andPositionTypeIsNull();
            } else {
                criteria.andPositionTypeEqualTo(param.getPositionType());
            }
        }
        if (StringUtils.isNotEmpty(param.getPositionName())) {
            if ("空".equals(param.getPositionName())) {
                criteria.andPositionNameIsNull();
            } else {
                criteria.andPositionNameLike("%"+param.getPositionName()+"%");
            }
        }
        if (StringUtils.isNotEmpty(param.getOrgCode())) {
            criteria.andOrgCodePathLike("%"+param.getOrgCode()+"%");
        }
        if(StringUtils.isNotBlank(param.getEmail())){
            criteria.andEmailLike(param.getEmail()+"%");
        }
        if(StringUtils.isNotBlank(param.getCreateBegin())){
            Date beginDate = DateUtils.parse(param.getCreateBegin(),1);
            criteria.andCreateTimeGreaterThanOrEqualTo(beginDate);
        }
        if(StringUtils.isNotBlank(param.getCreateBegin())){
            Date beginDate = DateUtils.parse(param.getCreateBegin()+" 00:00:00",2);
            criteria.andCreateTimeGreaterThanOrEqualTo(beginDate);
        }
        if(StringUtils.isNotBlank(param.getCreateEnd())){
            Date endDate = DateUtils.parse(param.getCreateEnd()+" 23:59:59",2);
            criteria.andCreateTimeLessThan(endDate);
        }
        if(StringUtils.isNotBlank(param.getNoOrgPatchName())){
            criteria.andOrgNamePathNotLike("%"+param.getNoOrgPatchName()+"%");
        }
        if(StringUtils.isNotBlank(param.getNullOrgPathName())){
            criteria.andOrgNamePathIsNull();
        }
        if(param.getFaceIssueFlag() != null){
            criteria.andFaceIssueFlagEqualTo(param.getFaceIssueFlag());
        }
        if(param.getCardIssueFlag() != null){
            criteria.andCardIssueFlagEqualTo(param.getCardIssueFlag());
        }
        if(StringUtils.isNotBlank(param.getNoUserType())){
            criteria.andUserTypeNotEqualTo(param.getNoUserType());
        }
        if (StringUtils.isNotBlank(param.getEmpTypes())) {
            List<DictItemRt> empTypeList = dictItemService.selectDictItemListBySupperCode(SafetyEmployeeConstants.EMP_LEDGER_EMP_TYPE);
            List<String> list = Arrays.asList(param.getEmpTypes().split(","));
            List<String> itemNames = empTypeList.stream().filter(li -> list.contains(li.getItemCode())).map(DictItemRt::getItemName).collect(Collectors.toList());
            criteria.andEmpTypeIn(itemNames);
        }
        if (StringUtils.isNotBlank(param.getJobTypeNames())) {
            List<DictItemRt> empTypeList = dictItemService.selectDictItemListBySupperCode(SafetyEmployeeConstants.EMP_LEDGER_JOB_TYPE);
            List<String> list = Arrays.asList(param.getJobTypeNames().split(","));
            List<String> itemNames = empTypeList.stream().filter(li -> list.contains(li.getItemCode())).map(DictItemRt::getItemName).collect(Collectors.toList());
            criteria.andJobTypeNameIn(itemNames);
        }
        return example;
    }

    /**
     * 批量更新根据id
     * @param param
     */
    public void updateRemark(EmployeeMessageUpdateParam param) {
        SafetyEmployeeMessageDO employeeMessageDO = new SafetyEmployeeMessageDO();
        employeeMessageDO.setRemark(param.getContent());

        SafetyEmployeeMessageDOExample example = formatUpdateParam(param);
        safetyEmployeeMessageDOMapper.updateByExampleSelective(employeeMessageDO, example);
    }

    /**
     * 批量更新最后更新时间
     * @param empNos 员工号集合
     */
    public void batchUpdateTimeByEmpNos(List<String> empNos){
        if(CollectionUtils.isEmpty(empNos)){
            return;
        }
        try {
            EmployeeMessageUpdateParam param = new EmployeeMessageUpdateParam();
            param.setEmpNos(empNos);
            param.setStatus(Constants.NORMAL); //只更新状态正常的员工
            batchUpdateTime(param);
        }catch (Exception e){
            LogUtils.error("批量更新员工最后更新时间异常",e);
        }
    }

    /**
     * 批量更新员工的最后更新时间
     */
    public void batchUpdateTime(EmployeeMessageUpdateParam param){
        SafetyEmployeeMessageDO employeeMessageDO = new SafetyEmployeeMessageDO();
        employeeMessageDO.setUpdateTime(new Date());
        SafetyEmployeeMessageDOExample example = formatUpdateParam(param);
        safetyEmployeeMessageDOMapper.updateByExampleSelective(employeeMessageDO, example);
    }

    private SafetyEmployeeMessageDOExample formatUpdateParam(EmployeeMessageUpdateParam param){
        SafetyEmployeeMessageDOExample example = new SafetyEmployeeMessageDOExample();
        SafetyEmployeeMessageDOExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(param.getIds())) {
            Set<String> setIds = Sets.newHashSet(param.getIds().split(","));
            List<Integer> listIds = setIds.stream().map(Integer::parseInt).collect(Collectors.toList());
            criteria.andIdIn(listIds);
        } else {
            if(!CollectionUtils.isEmpty(param.getEmpNos())){
                criteria.andEmpNoIn(param.getEmpNos());
            }
            if (StringUtils.isNotEmpty(param.getEmpNo())) {
                criteria.andEmpNoLike("%"+param.getEmpNo()+"%");
            }
            if (StringUtils.isNotEmpty(param.getEmpName())) {
                criteria.andEmpNameLike("%"+param.getEmpName()+"%");
            }
            if (StringUtils.isNotEmpty(param.getPhoneNo())) {
                criteria.andPhoneNoLike("%"+param.getPhoneNo()+"%");
            }
            if (StringUtils.isNotEmpty(param.getRemark())) {
                criteria.andRemarkLike("%"+param.getRemark()+"%");
            }
            if (StringUtils.isNotEmpty(param.getUserType())) {
                criteria.andUserTypeEqualTo(param.getUserType());
            }
            if (StringUtils.isNotEmpty(param.getPositionType())) {
                criteria.andPositionTypeEqualTo(param.getPositionType());
            }
            if(StringUtils.isNotBlank(param.getStatus())){
                criteria.andStatusEqualTo(param.getStatus());
            }
        }
        return example;
    }

    /**
     * 增量同步或者全量同步数据
     * @param isAll 是否全部
     * @param dateTime iamBlackUrl
     */
    public void tasksMessage(Boolean isAll, String dateTime) {
        if (StringUtils.isEmpty(dateTime)) {
            //前一天
            dateTime = DateUtils.format(DateUtils.addDays(new Date(),-1), 2);
        }
        String endTime = DateUtils.format(new Date(),2);
        //第一页的数据
        String url = String.format(iamEmpInfoUrl, isAll, dateTime,endTime, "ABYJS", 1);
        //定义总页数
        int pageSize = 0;
        //根据第一条数据，循环后边的数据
        String result = webserviceUtil.sendHttpForGet(url);
        JSONObject userJson;
        JSONObject resultJSON;
        JSONArray records;
        if(StringUtils.isNotEmpty(result)) {
            userJson = (JSONObject) JsonUtils.parse(result);
            if(userJson.containsKey("success") && userJson.getBoolean("success")) {
                //获取result对象
                resultJSON = userJson.getJSONObject("result");
                pageSize = resultJSON.getInteger("pages");
                LogUtils.info("获取接口数据量：{total:{},pages:{}}",resultJSON.getInteger("total"),resultJSON.getInteger("pages"));
                //获取对象数据数组
                records = resultJSON.getJSONArray("records");
                //解析数据，插入
                taskInsertBlackNew(records);
            }
            if (pageSize > 1) {
                for (int i = 2; i <=pageSize ; i++) {
                    url = String.format(iamEmpInfoUrl, isAll, dateTime,endTime, "ABYJS", i);
                    result = webserviceUtil.sendHttpForGet(url);
                    if(StringUtils.isNotEmpty(result)) {
                        userJson = (JSONObject) JsonUtils.parse(result);
                        if(userJson.containsKey("success") && userJson.getBoolean("success")) {
                            resultJSON = userJson.getJSONObject("result");
                            records = resultJSON.getJSONArray("records");
                            //解析数据，插入
                            taskInsertBlackNew(records);
                        }
                    }
                }
            }
        }
    }

    public void taskInsertBlack(JSONArray jsonArray, Boolean isAll) {
        //组装插入数据
        List<SafetyEmployeeMessageDO> insertList = Lists.newArrayList();
        //组装更新数据
        List<SafetyEmployeeMessageDO> updateList = Lists.newArrayList();
        JSONObject record = null;
        SafetyEmployeeMessageDO safetyEmployeeMessageDO = null;
        boolean updateFlag = false;
        for (int i = 0; i < jsonArray.size(); i++) {
            updateFlag = false;
            record = jsonArray.getJSONObject(i);
            String empNo = record.getString("workNo");
            if (!isAll) {
                updateFlag = true;
                safetyEmployeeMessageDO = getByNo(empNo);
                if (safetyEmployeeMessageDO == null) {
                    safetyEmployeeMessageDO = new SafetyEmployeeMessageDO();
                    updateFlag = false;
                }
            } else {
                safetyEmployeeMessageDO = new SafetyEmployeeMessageDO();
            }
            if (!updateFlag) {
                safetyEmployeeMessageDO.setCreateTime(new Date());
            }
            safetyEmployeeMessageDO.setUpdateTime(new Date());
            safetyEmployeeMessageDO.setEmpNo(empNo);
            safetyEmployeeMessageDO.setEmpName(record.getString("name"));
            safetyEmployeeMessageDO.setEmail(record.getString("email"));
            safetyEmployeeMessageDO.setPhoneNo(record.getString("phone"));
            safetyEmployeeMessageDO.setUserType(record.getString("userType"));
            safetyEmployeeMessageDO.setPositionCode(record.getString("positionCode"));
            safetyEmployeeMessageDO.setPositionName(record.getString("positionName"));
            if (record.containsKey("positionType") && StringUtils.isNotEmpty(record.getString("positionType"))) {
                String positionType = record.getString("positionType");
                if (positionType.startsWith("直接")) {
                    positionType = "直接岗位";
                } else if(positionType.startsWith("间接")) {
                    positionType = "间接岗位";
                } else if (positionType.startsWith("外包")) {
                    positionType = "外包岗位";
                }
                safetyEmployeeMessageDO.setPositionType(positionType);
            }
            safetyEmployeeMessageDO.setOrgCode(record.getString("orgCode"));
            safetyEmployeeMessageDO.setOrgName(record.getString("orgName"));
            safetyEmployeeMessageDO.setOrgCodePath(record.getString("orgCodePath"));
            safetyEmployeeMessageDO.setOrgNamePath(record.getString("orgNamePath"));
            safetyEmployeeMessageDO.setParentCode(record.getString("parentCode"));
            safetyEmployeeMessageDO.setStatus(record.getString("status"));
            if (updateFlag) {
                updateList.add(safetyEmployeeMessageDO);
            } else {
                insertList.add(safetyEmployeeMessageDO);
            }
        }
        //批量插入
        if (CollectionUtils.isNotEmpty(insertList)) {
            safetyEmployeeMessageDOBizMapper.batchInsert(insertList);
        }
        //批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            safetyEmployeeMessageDOBizMapper.batchUpdate(updateList);
        }
    }

    /**
     * 批量更新用户 - 240612
     * @param jsonArray
     */
    public void taskInsertBlackNew(JSONArray jsonArray) {
        //组装插入数据
        List<SafetyEmployeeMessageDO> insertList = Lists.newArrayList();
        JSONObject record;
        SafetyEmployeeMessageDO safetyEmployeeMessageDO = null;
        for (int i = 0; i < jsonArray.size(); i++) {
            record = jsonArray.getJSONObject(i);
            try {
                String empNo = record.getString("workNo");
                safetyEmployeeMessageDO = new SafetyEmployeeMessageDO();
                safetyEmployeeMessageDO.setCreateTime(new Date());
                safetyEmployeeMessageDO.setUpdateTime(new Date());
                safetyEmployeeMessageDO.setEmpNo(empNo);
                safetyEmployeeMessageDO.setEmpName(record.getString("name"));
                String email = record.getString("email");
                safetyEmployeeMessageDO.setEmail(email);
                if (StringUtils.isNotEmpty(email)) {
                    safetyEmployeeMessageDO.setEmpLogin(email.split("@")[0]);
                }
                safetyEmployeeMessageDO.setPhoneNo(record.getString("phone"));
                safetyEmployeeMessageDO.setUserType(record.getString("userType"));
                safetyEmployeeMessageDO.setPositionCode(record.getString("positionCode"));
                safetyEmployeeMessageDO.setPositionName(record.getString("positionName"));
                if (record.containsKey("positionType") && StringUtils.isNotEmpty(record.getString("positionType"))) {
                    String positionType = record.getString("positionType");
                    if (positionType.startsWith("直接")) {
                        positionType = "直接岗位";
                    } else if (positionType.startsWith("间接")) {
                        positionType = "间接岗位";
                    } else if (positionType.startsWith("外包")) {
                        positionType = "外包岗位";
                    }
                    safetyEmployeeMessageDO.setPositionType(positionType);
                }
                safetyEmployeeMessageDO.setOrgCode(record.getString("orgCode"));
                safetyEmployeeMessageDO.setOrgName(record.getString("orgName"));
                safetyEmployeeMessageDO.setOrgCodePath(record.getString("orgCodePath"));
                safetyEmployeeMessageDO.setOrgNamePath(record.getString("orgNamePath"));
                safetyEmployeeMessageDO.setParentCode(record.getString("parentCode"));
                safetyEmployeeMessageDO.setStatus(record.getString("status"));
                safetyEmployeeMessageDO.setEmpType(record.getString("empType"));
                safetyEmployeeMessageDO.setEmpTypeCode(record.getString("empTypeCode"));
                String jobTypeName = record.getString("jobTypeName");
                if (StringUtils.isNotBlank(jobTypeName) && !SafetyEmployeeConstants.JOB_TYPE_NAME.contains(jobTypeName)) {
                    jobTypeName = "员工";
                }
                safetyEmployeeMessageDO.setJobTypeName(jobTypeName);
                String lifePhoto = safetyBaseHandler.getPhotoUrl(record.getString("lifePhoto"));
                LogUtils.info("照片信息-empNo:{}---lifePhoto:{}", lifePhoto);
                if (StringUtils.isBlank(lifePhoto)) {
                    lifePhoto = safetyBaseHandler.getPhotoUrl(record.getString("photo"));
                    LogUtils.info("照片信息-empNo:{}---photo:{}", lifePhoto);
                }
                LogUtils.info("照片信息-empNo:{}---lastPhoto:{}", lifePhoto);
                safetyEmployeeMessageDO.setPhoto(lifePhoto);
                String gender = record.getString("gender");
                safetyEmployeeMessageDO.setSex("MALE".equals(gender) ? "0" : "1");
                insertList.add(safetyEmployeeMessageDO);
            }catch (Exception e){
                LogUtils.error("员工{}整理数据异常:{}",record.toJSONString(),e);
            }
        }
        //批量插入
        if (CollectionUtils.isNotEmpty(insertList)) {
            // 判断组织是否变更
            positionChange(insertList);
            batchUpdateNew(insertList);
            // 更新创建时间，条件创建时间为空，status为NORMAL
            List<String> collect = insertList.stream().map(SafetyEmployeeMessageDO::getEmpNo).collect(Collectors.toList());
            this.updateCreateTime(collect, Constants.NORMAL);
        }

    }

    private void positionChange(List<SafetyEmployeeMessageDO> insertList) {
        try {
            // 离职的人员
            Set<String> empNoList = insertList.stream().filter(li -> StringUtils.equals(li.getStatus(), "DISABLED")).map(SafetyEmployeeMessageDO::getEmpNo).collect(Collectors.toSet());

            // 整理出组织变更的数据
            Map<String, String> dataMap = insertList.stream().filter(li -> StringUtils.isNotBlank(li.getPositionName())).collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, SafetyEmployeeMessageDO::getPositionName));
            Set<String> sets = new HashSet<>();
            sets.addAll(dataMap.keySet());
            sets.addAll(empNoList);
            if (CollectionUtils.isEmpty(sets)) {
                return;
            }
            List<SafetyEmployeeMessageDO> listByStaffNo = getListByStaffNo(new ArrayList<>(sets));
            if (!CollectionUtils.isEmpty(listByStaffNo)) {
                List<String> empNos = listByStaffNo.stream().filter(li -> !StringUtils.equals(dataMap.get(li.getEmpNo()), li.getPositionName())).map(SafetyEmployeeMessageDO::getEmpNo).collect(Collectors.toList());
                empNoList.addAll(empNos);
                safetyVisitorAuthorizationApprovalHandler.positionChange(empNoList);
            }
        } catch (Exception e) {
            LogUtils.error("获取组织变更数据异常:{}", e);
        }
    }

    public SafetyEmployeeMessageDO getByNo(String empNo) {
        SafetyEmployeeMessageDOExample example = new SafetyEmployeeMessageDOExample();
        example.createCriteria().andEmpNoEqualTo(empNo).andStatusEqualTo("NORMAL");
        List<SafetyEmployeeMessageDO> list = safetyEmployeeMessageDOMapper.selectByExample(example);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    //不排除已离职人员
    public SafetyEmployeeMessageDO getByNoForAll(String empNo) {
        SafetyEmployeeMessageDOExample example = new SafetyEmployeeMessageDOExample();
        example.createCriteria().andEmpNoEqualTo(empNo);
        List<SafetyEmployeeMessageDO> list = safetyEmployeeMessageDOMapper.selectByExample(example);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    /**
     * 根据员工号获取用户名
     * @param empNo 员工号
     * @return 登录名
     */
    public String getLoginNameByNo(String empNo) {
        if(StringUtils.isBlank(empNo)){
            return "";
        }
        SafetyEmployeeMessageDOExample example = new SafetyEmployeeMessageDOExample();
        example.createCriteria().andEmpNoEqualTo(empNo).andStatusEqualTo("NORMAL");
        List<SafetyEmployeeMessageDO> list = safetyEmployeeMessageDOMapper.selectByExample(example);
        SafetyEmployeeMessageDO employeeMessageDO = CollectionUtils.isEmpty(list) ? null : list.get(0);
        if(employeeMessageDO != null && StringUtils.isNotBlank(employeeMessageDO.getEmail())){
            String email = employeeMessageDO.getEmail();
            int endIdx = email.lastIndexOf("@");
            return employeeMessageDO.getEmail().substring(0,endIdx);
        }
        return "";
    }

    /**
     * 根据手机号获取对应人员数据
     * @param phoneNo 手机号
     * @return 返回值
     */
    public SafetyEmployeeMessageDO getInfoByPhone(String phoneNo) {
        SafetyEmployeeMessageDOExample example = new SafetyEmployeeMessageDOExample();
        example.createCriteria().andStatusEqualTo(Constants.NORMAL).andPhoneNoEqualTo(phoneNo);
        List<SafetyEmployeeMessageDO> list = safetyEmployeeMessageDOMapper.selectByExample(example);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    public Object getPost() {
        return safetyZCustomUtilDOBizMapper.getPost();
    }

    /**
     * 格局员工号获取详情
     * @param staffNoList 员工号集合
     * @return 返回结果
     */
    public List<SafetyEmployeeMessageDO> getListByStaffNo(List<String> staffNoList) {
        EmployeeMessageListParam param = new EmployeeMessageListParam();
        param.setEmpNoList(staffNoList);

        SafetyEmployeeMessageDOExample example = convertExample(param);
        return safetyEmployeeMessageDOMapper.selectByExample(example);
    }

    /**
     * 数据导出
     * @param staffNoList 员工号集合
     * @return
     */
    public Map<String,SafetyEmployeeMessageDO> getMaptByStaffNo(List<String> staffNoList) {
        EmployeeMessageListParam param = new EmployeeMessageListParam();
        param.setEmpNoList(staffNoList);

        SafetyEmployeeMessageDOExample example = convertExample(param);
        List<SafetyEmployeeMessageDO> list = safetyEmployeeMessageDOMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            return new HashMap<>();
        }
        return list.stream().collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, e -> e));
    }

    /**
     * 查询
     * @param param 入参
     * @return 返回结果
     */
    public List<SafetyEmployeeMessageDO> selectList(EmployeeMessageListParam param) {
        SafetyEmployeeMessageDOExample example = convertExample(param);
        if(StringUtils.isNotBlank(param.getEmpName())){
            example.setOrderByClause(" emp_name asc ");
        }else {
            example.setOrderByClause(" create_time desc ");
        }
        return safetyEmployeeMessageDOMapper.selectByExample(example);
    }

    /**
     * 根据员工号同步员工信息
     * @param workNo 员工号
     * @return 返回值
     */
    public Object tasksMessageByWorkNo(String workNo){
        if(StringUtils.isBlank(workNo)){
            return null;
        }
        if(StringUtils.isBlank(iamEmployForWorkNoUrl)){
            LogUtils.info("根据员工号更新员工信息接口“iam.emplyee.byWorkNo.url”未维护!");
            return null;
        }
        //请求接口查询员工信息
        String url = String.format(iamEmployForWorkNoUrl, "ABYJS", workNo);
        String result = webserviceUtil.sendHttpForGet(url);
        if(StringUtils.isNotEmpty(result)) {
            JSONObject userJson = (JSONObject) JsonUtils.parse(result);
            if(userJson.containsKey("success") && userJson.getBoolean("success")) {
                JSONArray resultJSON = userJson.getJSONArray("result");
                JSONObject record = resultJSON.getJSONObject(0);
                //解析数据，插入
                SafetyEmployeeMessageDO employeeMessageDO = convertEmployee(record);
                List<SafetyEmployeeMessageDO> list = new ArrayList<>();
                list.add(employeeMessageDO);
                batchUpdateNew(list);
            }
        }
        return null;
    }

    /**
     * 批量更新员工信息
     * @param employeeMessageDOList 入参
     */
    public void batchUpdateNew(List<SafetyEmployeeMessageDO> employeeMessageDOList){
        safetyEmployeeMessageDOBizMapper.batchUpdateNew(employeeMessageDOList);
    }

    /**
     * 接口返回值转化为SafetyEmployeeMessageDO对象
     * @param record 入参JSONObject
     * @return 返回值
     */
    private SafetyEmployeeMessageDO convertEmployee(JSONObject record){
        SafetyEmployeeMessageDO employeeMessageDO = new SafetyEmployeeMessageDO();
        employeeMessageDO.setEmpNo(record.getString("workNo"));
        employeeMessageDO.setEmpName(record.getString("name"));
        employeeMessageDO.setEmail(record.getString("email"));
        employeeMessageDO.setPhoneNo(record.getString("phone"));
        employeeMessageDO.setUserType(record.getString("userType"));
        employeeMessageDO.setPositionCode(record.getString("positionCode"));
        employeeMessageDO.setPositionName(record.getString("positionName"));
        if (record.containsKey("positionType") && StringUtils.isNotEmpty(record.getString("positionType"))) {
            String positionType = record.getString("positionType");
            if (positionType.startsWith("直接")) {
                positionType = "直接岗位";
            } else if(positionType.startsWith("间接")) {
                positionType = "间接岗位";
            } else if (positionType.startsWith("外包")) {
                positionType = "外包岗位";
            }
            employeeMessageDO.setPositionType(positionType);
        }
        employeeMessageDO.setOrgCode(record.getString("orgCode"));
        employeeMessageDO.setOrgName(record.getString("orgName"));
        employeeMessageDO.setOrgCodePath(record.getString("orgCodePath"));
        employeeMessageDO.setOrgNamePath(record.getString("orgNamePath"));
        employeeMessageDO.setParentCode(record.getString("parentCode"));
        employeeMessageDO.setStatus(record.getString("status"));
        employeeMessageDO.setEmpType(record.getString("empType"));
        employeeMessageDO.setEmpTypeCode(record.getString("empTypeCode"));
        String jobTypeName = record.getString("jobTypeName");
        if (StringUtils.isNotBlank(jobTypeName) && !SafetyEmployeeConstants.JOB_TYPE_NAME.contains(jobTypeName)) {
            jobTypeName = "员工";
        }
        employeeMessageDO.setJobTypeName(jobTypeName);
        String lifePhoto = safetyBaseHandler.getPhotoUrl(record.getString("lifePhoto"));
        if (StringUtils.isBlank(lifePhoto)) {
            lifePhoto = safetyBaseHandler.getPhotoUrl(record.getString("photo"));
        }
        employeeMessageDO.setPhoto(lifePhoto);
        employeeMessageDO.setCreateTime(new Date());
        employeeMessageDO.setUpdateTime(new Date());

        return employeeMessageDO;
    }

    public List<SafetyEmployeeMessageDO> selectByExample(SafetyEmployeeMessageDOExample example){
        return safetyEmployeeMessageDOMapper.selectByExample(example);
    }

    /**
     * 根据员工号更新更新时间
     * @param param 入参
     * @return 返回值
     */
    public Object updateTimeByEmpNo(Map<String, Object> param) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(ObjectUtil.isEmpty(param.get("updateTime"))){
            param.put("updateTime",format.format(new Date()));
        }else{
            String updateTime = param.get("updateTime").toString();
            try {
                param.put("updateTime",format.parse(updateTime));
            } catch (ParseException e) {
                LogUtils.info("转码异常");
            }
        }

        safetyEmployeeMessageDOMapper.updateTimeByEmpNo(param);

        return null;
    }

    public List<SafetyEmployeeMessageDO> getUserListByCondition(EmployeeMessageListParam param) {
        if(StringUtils.isNotBlank(param.getEmpName()) || StringUtils.isNotBlank(param.getEmpNoAndName())){
            param.setOrderByClause(" emp_name asc ");
        }else {
            param.setOrderByClause(" create_time desc ");
        }
        return safetyEmployeeMessageDOMapper.getUserListByCondition(param);
    }

    public long getCountUserListByCondition(EmployeeMessageListParam param) {
        return safetyEmployeeMessageDOMapper.getCountUserListByCondition(param);
    }

    public List<SafetyEmployeeMessageDO> selectListByUserCode(List<String> userCodeList){
        SafetyEmployeeMessageDOExample example = new SafetyEmployeeMessageDOExample();
        SafetyEmployeeMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("NORMAL")
                .andEmpNoIn(userCodeList);
        return safetyEmployeeMessageDOMapper.selectByExample(example);
    }

    /**
     * 批量更新发脸状态
     * @param empNoList 员工号集合
     */
    public void modifyFaceIssueFlag(List<String> empNoList){
        if(org.springframework.util.CollectionUtils.isEmpty(empNoList)){
            return;
        }
        safetyEmployeeMessageDOBizMapper.modifyFaceIssueFlag(empNoList);
    }

    /**
     * 清空和更新 员工台账中的员工的创建时间
     *
     * @param empNoList 员工工号
     * @param status 状态，DISABLED代表离职的员工，离职的员工符合条件需要清空创建时间，NORMAL代表在职的员工符合条件时需要更新创建时间
     * @return 成功的数量
     */
    public int updateCreateTime(List<String> empNoList, String status){
        SafetyEmployeeMessageDOExample example = new SafetyEmployeeMessageDOExample();
        SafetyEmployeeMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(status);
        criteria.andEmpNoIn(empNoList);

        SafetyEmployeeMessageDO record = new SafetyEmployeeMessageDO();
        if (status.equals(Constants.NORMAL)) {
            // 更新的条件创建时间为空，设置更新的时间
            record.setCreateTime(new Date());
            criteria.andCreateTimeIsNull();
        } else {
            // 清空的条件创建时间不等于空，更新时间为空，不设置值更新时会赋值为null
            criteria.andCreateTimeIsNotNull();
            record.setFaceIssueFlag(Short.valueOf("0")); //脸发行标记置空
        }
        return safetyEmployeeMessageDOMapper.updateCreateTime(record, example);
    }

    /**
     * 在员工台账中信息表中，根据员工号 获取员工台账信息
     * @param empNo 员工号
     * @return 返回员工号对应的员工信息
     */
    public SafetyEmployeeMessageDO selectInfoByEmpNo(String empNo) {
        return safetyEmployeeMessageDOMapper.selectInfoByEmpNo(empNo);
    }

    /**
     * 更新员工对应组织
     * @param orgCodeList 有变更的组织
     * @return 更新条数
     */
    public int updateEmpPathByOrg(List<String> orgCodeList){
        AtomicInteger updateRow = new AtomicInteger();
        if(CollectionUtils.isEmpty(orgCodeList)){
            return updateRow.get();
        }
        // 使用 ListUtils.partition 将列表分成每组200个元素
        List<List<String>> partition = ListUtils.partition(orgCodeList, 400);
        // 使用并行流来提高性能
        partition.forEach(li ->
                updateRow.addAndGet(safetyEmployeeMessageDOBizMapper.updateEmpPathByOrg(li)));
        return updateRow.get();
    }

    /**
     * 根据作业部门编码 获取指定的几个岗位的数据
     * @param emgCode 组织编码
     */
    public List<SafetyEmployeeMessageDO> selectInfoByDeptPosition(List<String> emgCode) {
        List<SafetyEmployeeMessageDO> list = safetyEmployeeMessageDOMapper.selectInfoByDeptPosition(emgCode);
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        Set<String> riskPositionList = FireInspectionEnum.getRiskPositionList();
        return list.stream()
                .filter(item -> StringUtils.isNotBlank(item.getPositionName()))
                .filter(item -> riskPositionList.stream().anyMatch(item.getPositionName()::contains))
                .collect(Collectors.toList());
    }

    public List<SafetyEmployeeMessageDO> selectListByOrgCodePath(List<String> orgCodePath) {
        if (CollectionUtils.isEmpty(orgCodePath)) {
            return Lists.newArrayList();
        }
        SafetyEmployeeMessageDOExample example = new SafetyEmployeeMessageDOExample();
        SafetyEmployeeMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("NORMAL")
                .andOrgCodePathIn(orgCodePath);
        return safetyEmployeeMessageDOMapper.selectByExample(example);
    }

    public List<SafetyEmployeeMessageDO> selectInfoByPositionName(List<String> positionNameList) {
        return safetyEmployeeMessageDOMapper.selectInfoByPositionName(positionNameList);
    }

    public List<SafetyEmployeeMessageDO> selectListByOrgCode(List<String> orgCode) {
        if (CollectionUtils.isEmpty(orgCode)) {
            return Lists.newArrayList();
        }
        SafetyEmployeeMessageDOExample example = new SafetyEmployeeMessageDOExample();
        SafetyEmployeeMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("NORMAL")
                .andOrgCodeIn(orgCode);
        return safetyEmployeeMessageDOMapper.selectByExample(example);
    }

    public List<SafetyEmployeeMessageDO> getLeaderList() {
        SafetyEmployeeMessageDOExample example = new SafetyEmployeeMessageDOExample();
        SafetyEmployeeMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("NORMAL")
                .andEmpTypeEqualTo("中高层");
        return safetyEmployeeMessageDOMapper.selectByExample(example);
    }

    /**
     * 获取增量数据
     * @param start 开始时间
     * @param end 截止时间
     * @return 返回数据列表
     */
    public List<SafetyEmployeeMessageDO> selectUpdateList(Date start, Date end) {
        SafetyEmployeeMessageDOExample example = new SafetyEmployeeMessageDOExample();
        SafetyEmployeeMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andUpdateTimeBetween(start, end);
        return safetyEmployeeMessageDOMapper.selectByExample(example);
    }

    public List<String> selectDistinctList(String fieldName) {
        return safetyEmployeeMessageDOMapper.selectDistinctList(fieldName);
    }

    /**
     * 获取指定职种的数据
     * @param jobTypeNames 职种信息
     * @param type 为空时获取增量，不能于空时全量
     * @return 返回存在的数据
     */
    public List<SafetyEmployeeMessageDO> selectListByJobName(List<String> jobTypeNames, String type) {
        SafetyEmployeeMessageDOExample example = new SafetyEmployeeMessageDOExample();
        SafetyEmployeeMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(Constants.NORMAL);
        criteria.andJobTypeNameIn(jobTypeNames);
        if (StringUtils.isBlank(type)) {
            Date beginDate = DateUtils.addDays(new Date(), -1);
            criteria.andUpdateTimeBetween(beginDate, new Date());
        }
        return safetyEmployeeMessageDOMapper.selectByExample(example);
    }

    public List<SafetyEmployeeMessageDO> selectListOfFaceUnqualified() {
        return safetyEmployeeMessageDOMapper.selectListOfFaceUnqualified();
    }
}
