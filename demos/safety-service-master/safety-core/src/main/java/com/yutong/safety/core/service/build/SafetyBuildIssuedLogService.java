package com.yutong.safety.core.service.build;

import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.utils.BuildEncryptUtil;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.build.SafetyBuildIssuedLogDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildIssuedLogDOExample;
import com.yutong.safety.core.db.mapper.build.SafetyBuildIssuedLogDOBizMapper;
import com.yutong.safety.core.dto.build.SafetyBuildIssuedLogDTO;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class SafetyBuildIssuedLogService {

    @Value("${build.grantBuild.url}")
    private String grantBuildUrl;

    @Value("${build.encryption.key}")
    private String buildEncryKey;

    @Value("${build.signature.key}")
    private String buildSignKey;

    @Value("${build.appid}")
    private String buildAppID;

    @Value("${build.dpcode}")
    private String buildDpcode;

    @Autowired
    SafetyBuildIssuedLogDOBizMapper safetyBuildIssuedLogDOBizMapper;

    public List<SafetyBuildIssuedLogDTO> selectPageList(SafetyBuildIssuedLogDTO param) {
        return safetyBuildIssuedLogDOBizMapper.selectPageList(param);
    }

    public long selectListCount(SafetyBuildIssuedLogDTO param) {
        return safetyBuildIssuedLogDOBizMapper.selectListCount(param);
    }

    public void batchInsert(List<SafetyBuildIssuedLogDO> list) {
        // 日志批量添加时 调整为每批200条添加
        List<List<SafetyBuildIssuedLogDO>> partition = ListUtils.partition(list, 200);
        partition.forEach(li -> safetyBuildIssuedLogDOBizMapper.batchInsert(li));
    }

    //批量更新所有下发异常的数据状态为正常
    public void batchUpdate(List<Integer> successList,String user) {
        safetyBuildIssuedLogDOBizMapper.batchUpdate(successList,user);
    }

    public List<SafetyBuildIssuedLogDO> selectList(SafetyBuildIssuedLogDO en){
        SafetyBuildIssuedLogDOExample example = new SafetyBuildIssuedLogDOExample();
        SafetyBuildIssuedLogDOExample.Criteria criteria = example.createCriteria();

        if(ObjectUtils.isEmpty(en)){return Collections.emptyList();}
        if(!StringUtils.isEmpty(en.getTargetId())){
            criteria.andTargetIdEqualTo(en.getTargetId());
        }
        if(!StringUtils.isEmpty(en.getIssuedStatus())){
            criteria.andIssuedStatusEqualTo(en.getIssuedStatus());
        }

        example.setOrderByClause(" create_time desc");

        return safetyBuildIssuedLogDOBizMapper.selectByExample(example);
    }

    public List<SafetyBuildIssuedLogDO> getExceptData(){
        return safetyBuildIssuedLogDOBizMapper.getExceptData();
    }

    /**
     * 下发门禁权限-新开普
     * @param issuedLogDO 汇总后的要下发的权限
     * @return 返回下发状态
     */
    public String issuedBuildPower(SafetyBuildIssuedLogDO issuedLogDO){
        AtomicReference<String> issuedStatus = new AtomicReference<>("成功");
        String employeeCode = issuedLogDO.getEmployeeCode();
        String deviceCode = issuedLogDO.getDeviceCode();
        if(StringUtils.isEmpty(deviceCode) || StringUtils.isEmpty(employeeCode)){return issuedStatus.get();}
        String dealDeviceCode = issuedLogDO.getDeviceCode();
        List<String> deviceList = Arrays.asList(dealDeviceCode.split(","));
        //新开普授权设备按200个分批授权
        List<List<String>> partition = ListUtils.partition(deviceList, 200);
        partition.forEach(li -> {
            String curStatus = "成功";
            String curDevice = li.stream().collect(Collectors.joining(","));
            //封装参数
            JSONObject firstData = new JSONObject();
            firstData.put("outid",employeeCode);
            firstData.put("extendids",curDevice);
            firstData.put("start", issuedLogDO.getBeginTime().substring(0,10)+" 00:00:00");
            firstData.put("end",issuedLogDO.getEndTime().substring(0,10)+" 23:59:59");
            firstData.put("status","开通".equals(issuedLogDO.getIssuedType())?1:0);
            firstData.put("empcode","mjqxdj"); //下发门禁指定账号
            LogUtils.info("新开普下发的参数：{}",firstData);

            try {
                //下发门禁
                String retStr = BuildEncryptUtil.sendBuildHttpPost(grantBuildUrl, firstData.toJSONString(),
                        buildEncryKey,buildSignKey,buildAppID,buildDpcode);
                LogUtils.info("新开普下发返回值：{}",retStr);
                if(ResponseEnum.FAIL.getName().equals(retStr)){
                    curStatus = "异常";
                }else{
                    JSONObject object = JSONObject.parseObject(retStr);
                    if(object.getInteger("result") != 100){
                        LogUtils.error("门禁权限下发异常："+retStr);
                        curStatus = "异常;"+retStr;
                    }
                }
            } catch (Exception ex) {
                curStatus = "异常";
                LogUtils.error("{\"code\":\"9999\",\"msg\":\"门禁权限下发异常，参数：param="+firstData+",异常信息："+ex+" \"}");
            }finally {
                if(!"成功".equals(curStatus)){
                    issuedStatus.set(curStatus);
                }
            }
        });

        return issuedStatus.get();
    }

    public void batchUpdateGuard(List<String> list,String user){
        safetyBuildIssuedLogDOBizMapper.batchUpdateGuard(list,user);
    }
 }
