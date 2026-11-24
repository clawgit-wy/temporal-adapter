package com.yutong.safety.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.Base64ImageDecoder;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.WebserviceUtil;
import com.yutong.safety.core.db.entity.SafetyBlackMessageDO;
import com.yutong.safety.core.db.entity.SafetyBlackMessageDOExample;
import com.yutong.safety.core.db.entity.SafetyEmpowerDeviceDO;
import com.yutong.safety.core.db.mapper.SafetyBlackMessageDOMapper;
import com.yutong.safety.core.db.mapper.SafetyVisitorSecretDOBizMapper;
import com.yutong.safety.core.dto.SafetyVisitorSecretDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SafetyBaseService {

    @Autowired
    private WebserviceUtil webserviceUtil;

    @Value("${iam.employee.workNoConvert.url}")
    private String employeeWorkNoConvertUrl;

    @Autowired
    private SafetyBlackMessageDOMapper safetyBlackMessageDOMapper;

    @Value("${ehr.token}")
    private String tokenUrl;

    @Value("${ehr.blank.search}")
    private String ehrBlankSearchUrl;

    @Value("${iam.employee.getInfoByIdCard}")
    private String iamGetEmpByIdCarUrl;

    @Resource
    private SafetyVisitorSecretDOBizMapper safetyVisitorSecretDOBizMapper;

    @Value("${iam.apikey}")
    private String apikey;

    @Value("${iam.updatePhoto.url}")
    private String updatePhotoUrl;
    /**
     * 获取EHR的token
     * @return
     */
    public String getEhrToken(){
        String token = "";
        JSONObject tokenParam = new JSONObject();
        tokenParam.put("grant_type","client_credentials");
        tokenParam.put("client_id","eip");
        tokenParam.put("client_secret","eip@123");
        try{
            String tokenResult = webserviceUtil.sendHttp(tokenUrl, "1", tokenParam.toString(), new HashMap(), "POST");
            JSONObject tokenObj = JSONObject.parseObject(tokenResult);
            token = tokenObj.getString("access_token");
        }catch (Exception e){
            LogUtils.info(ResponseEnum.EXCEPTION.getCode(),"获取EHRtoken异常");
            LogUtils.error("获取EHRtoken异常",e);
        }
        return token;
    }
    /**
     * 转换员工号
     * @param oldWorkNo 旧员工号
     * @return 返回值
     */
    public String employeeConvertWorkNo(String oldWorkNo){
        String newWorkNo = oldWorkNo;
        if(StringUtils.isEmpty(employeeWorkNoConvertUrl)){
            LogUtils.info("转换员工号接口“iam.employee.workNoConvert.url”为空");
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"转换员工号接口“iam.employee.workNoConvert.url”为空");
        }
        //请求接口查询员工信息
        String url = String.format(employeeWorkNoConvertUrl, "ABYJS", oldWorkNo);
        String result = webserviceUtil.sendHttpForGet(url);
        if(StringUtils.isNotEmpty(result)) {
            JSONObject userJson = (JSONObject) JsonUtils.parse(result);
            if(result.contains("success") && userJson.getBoolean("success")) {
                newWorkNo = userJson.getString("result");
            }
        }
        return newWorkNo;
    }


    /**
     * 去除黑名单人员
     *
     * @param empNos 原数据集
     * @return 筛选出黑名单人员的数据
     */
    public List<String> filterBlackEmployee(List<String> empNos) {
        if(CollectionUtils.isEmpty(empNos)){
            return null;
        }
        SafetyBlackMessageDOExample example = new SafetyBlackMessageDOExample();
        SafetyBlackMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andCardNoIn(empNos);
        criteria.andStateEqualTo(1); // 状态=1 表示禁止入厂
        criteria.andRemarkEqualTo("禁止授权");
        List<SafetyBlackMessageDO> infoList = safetyBlackMessageDOMapper.selectByExample(example);
        // 2.返回去除黑名单中的人员信息
        List<String> empCodeList = infoList.stream().map(SafetyBlackMessageDO::getCardNo).filter(org.apache.commons.lang3.StringUtils::isNotEmpty).collect(Collectors.toList());
        LogUtils.info("员工“{}”在黑名单中，禁止进行人脸下发和卡下发！",String.join(",", empCodeList));
        return empCodeList;
    }

    /**
     * 整理有权限的通道号转为int[]
     * @param list 通道号集合
     * @return int[]
     */
    public int[] fromatCrkDevice(List<SafetyEmpowerDeviceDO> list){
        List<Integer> nos = new ArrayList<>();
        for (SafetyEmpowerDeviceDO channel : list) {
            if (org.apache.commons.lang3.StringUtils.isBlank(channel.getEmpowerNo())) {
                LogUtils.info(channel.getFactoryName() + "通道号为空");
                continue;
            }
            String channelNo = channel.getEmpowerNo();
            String[] channelNos = channelNo.split(",");
            for (String no : channelNos) {
                nos.add(Integer.parseInt(no));
            }
        }
        LogUtils.info("当前用户所有通道号："+nos);
        int[] nums = new int[nos.size()];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = nos.get(i);
        }
        return nums;
    }

    /**
     * 外协回写EHR
     */
    public List<SafetyBlackMessageDO> selectEhrBlank(String begTime,String endTime) {
        List<SafetyBlackMessageDO> blackList = new ArrayList<>();
        //获取EHR的Token
        String token = getEhrToken();
        JSONObject dataHeader = new JSONObject();
        dataHeader.put("Content-Type","application/json");
        dataHeader.put("Authorization", "Bearer "+token);

        JSONObject dataParam = new JSONObject();
        dataParam.put("headerParamMap",new HashMap<>());
        dataParam.put("requestParamMap",new HashMap<>());
        JSONObject pathVariableMap = new JSONObject();
        pathVariableMap.put("organizationId","0");
        dataParam.put("pathVariableMap",pathVariableMap);

        JSONObject requestParamMap = new JSONObject();
        requestParamMap.put("begTime",begTime);
        requestParamMap.put("endTime",endTime);
        dataParam.put("requestParamMap",requestParamMap);

        String dataResult = webserviceUtil.sendHttp(ehrBlankSearchUrl, "2", dataParam.toString(), dataHeader, "POST");
        JSONObject dataObj = JSONObject.parseObject(dataResult);
        if(!ObjectUtils.isEmpty(dataObj) && "200".equals(dataObj.getString("status"))){
            String retPayload = dataObj.getString("payload");
            LogUtils.info("获取EHR黑名单清单={}",retPayload);
            if(StringUtils.isNotEmpty(retPayload)){
                JSONArray retArr = JSONArray.parseArray(retPayload);
                blackList = formatBlackVO(blackList,retArr);
            }
        }
        return blackList;
    }

    /**
     * EHR返回的JSOAN 整理成LIST
     * @param blackList 黑名单对象集合
     * @param retArr EHR返回的JSON集合-有序
     */
    private List<SafetyBlackMessageDO> formatBlackVO(List<SafetyBlackMessageDO> blackList,JSONArray retArr){
        if(retArr.size()<=0){
            return blackList;
        }
        Map<String,SafetyBlackMessageDO> newBlackMap = new HashMap<>();
        for(int i=0;i<retArr.size();i++){
            SafetyBlackMessageDO newBlack = new SafetyBlackMessageDO();
            JSONObject curBlack = retArr.getJSONObject(i);
            String idCardNo = curBlack.getString("hhrCertificateNum");
            String deleteFlag = curBlack.getString("deleteFalg");
            if(StringUtils.isEmpty(idCardNo)){
                continue;
            }
            newBlack.setIdCardNo(idCardNo); //身份证号
            newBlack.setSource(Constants.BLANK_FIRST_SOURCE);
            newBlack.setEmpType(Constants.BLANK_FIRST_SOURCE);
            newBlack.setState(1); //禁止入厂
            if("0".equals(deleteFlag)){
                newBlack.setDeleteFalg(true);
            }else{
                newBlack.setDeleteFalg(false);
                newBlack.setEmpName(curBlack.getString("hhrName")); //姓名
                newBlack.setPhoneNo(curBlack.getString("hhrPhone")); //手机号
                newBlack.setRemark(curBlack.getString("hhrComment"));
            }
            newBlackMap.put(idCardNo,newBlack);
        }
        return new ArrayList<>(newBlackMap.values());
    }

    /**
     * 从中台获取黑名单对应员工信息
     * @param blackList 黑名单
     */
    public void formatBlackMessageFromMp(List<SafetyBlackMessageDO> blackList){
        for(SafetyBlackMessageDO black:blackList){
            String idCard = black.getIdCardNo();
            try {
                JSONObject retObj = getEmpInfoByIdCard(idCard);
                if (retObj != null) {
                    String empType = retObj.getString("empType");
                    String empNo = retObj.getString("workNo");
                    String photo = retObj.getString("photo");
                    black.setIdPhoto(this.getPhotoUrl(photo));
                    String lifePhoto = retObj.getString("lifePhoto");
                    black.setLifePhoto(this.getPhotoUrl(lifePhoto));
                    black.setCardNo(empNo);
                    black.setEmpType("离职人员");
                }
            }catch (Exception e){
                LogUtils.error("9999身份证号转员工号异常",e.getMessage());
            }
        }
    }

    public String getPhotoUrl(String address) {
        if (org.springframework.util.StringUtils.isEmpty(address)) {
            return null;
        }
        //图片转化为二进制
        if(address.startsWith("http")){
            if (Base64ImageDecoder.isPhoto(address)) {
                return address;
            } else {
                return address + "&apikey=" + apikey;
            }
        }else{
            return String.format(updatePhotoUrl, address);
        }
    }


    public JSONObject getEmpInfoByIdCard(String idCard){
        if(StringUtils.isEmpty(iamGetEmpByIdCarUrl)){
            LogUtils.info("根据身份证号获取员工信息接口“iam.employee.getInfoByIdCard”为空");
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"iamGetEmpByIdCarUrl“iam.employee.getInfoByIdCard”为空");
        }
        //请求接口查询员工信息
        String url = String.format(iamGetEmpByIdCarUrl,idCard,"SAFETY");
        String result = webserviceUtil.sendHttpForGet(url);
        if(StringUtils.isNotEmpty(result)) {
            JSONObject userJson = (JSONObject) JsonUtils.parse(result);
            if(result.contains("success") && userJson.getBoolean("success")) {
                JSONObject retObj = userJson.getJSONObject("result");
                return retObj;
            }
        }
        return null;
    }

    /**
     * 拼接厂区名
     * @param factoryIds
     * @param factoryMap
     * @return
     */
    public String getFactoryName(String factoryIds, Map<Integer, String> factoryMap) {
        List<String> split = new ArrayList<>();
        if(org.apache.commons.lang3.StringUtils.isNotBlank(factoryIds)){
            split = Arrays.asList(factoryIds.split(","));
        }
        List<String> values = new ArrayList<>();
        for (String str : split) {
            if(org.apache.commons.lang3.StringUtils.isBlank(str)){
                continue;
            }
            try {
                Integer id = Integer.parseInt(str);
                String facName = factoryMap.get(id);
                if (facName != null) {
                    values.add(facName);
                }
            } catch (NumberFormatException e) {
                // 处理无效的数字格式
                System.err.println("Invalid number format: " + str);
            }
        }
        return String.join(",", values);
    }

    /**
     * 拼接厂区名
     * @param keyStr
     * @param valueMap
     * @return
     */
    public String getValueForKey(String keyStr, Map<String, String> valueMap) {
        List<String> split = new ArrayList<>();
        if(org.apache.commons.lang3.StringUtils.isNotBlank(keyStr)){
            split = Arrays.asList(keyStr.split(","));
        }
        List<String> values = new ArrayList<>();
        for (String str : split) {
            if(org.apache.commons.lang3.StringUtils.isBlank(str)){
                continue;
            }
            try {
                String facName = valueMap.get(str);
                if (facName != null) {
                    values.add(facName);
                }
            } catch (NumberFormatException e) {
                // 处理无效的数字格式
                System.err.println("Invalid number format: " + str);
            }
        }
        return String.join(",", values);
    }

    /**
     * 获取加密区信息
     */
    public Map<String, SafetyVisitorSecretDTO> getSecretFactoryMap() {
        List<SafetyVisitorSecretDTO> secretFactoryList = safetyVisitorSecretDOBizMapper.getSecretFactoryList();
        if (CollectionUtils.isEmpty(secretFactoryList)) {
            return Maps.newHashMap();
        }
        return secretFactoryList.stream().collect(Collectors.toMap(
                SafetyVisitorSecretDTO::getSecretPosition,
                Function.identity(),
                (v1, v2) -> v2
        ));
    }
}
