package com.yutong.safety.core.handler.build;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.easyExcel.ColumnHeightAuto;
import com.yutong.safety.common.easyExcel.ColumnWidthAuto;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.BuildEncryptUtil;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.build.SafetyBuildIssuedLogDO;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.dto.ParamDTO;
import com.yutong.safety.core.dto.build.SafetyBuildIssuedLogDTO;
import com.yutong.safety.core.handler.employeeprivatecar.SafetyEmployeePrivateCarHandler;
import com.yutong.safety.core.handler.park.SafetyParkPowerProcessHandler;
import com.yutong.safety.core.service.build.SafetyBuildIssuedLogService;
import com.yutong.safety.core.service.park.SafetyParkEmployeePowerInfoService;
import com.yutong.safety.core.service.park.SafetyParkPowerProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SafetyBuildIssuedLogHandler {
    @Autowired
    SafetyBuildIssuedLogService safetyBuildIssuedLogService;
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
    SafetyParkEmployeePowerInfoService safetyParkEmployeePowerInfoService;

    @Autowired
    SafetyParkPowerProcessService safetyParkPowerProcessService;

    @Autowired
    SafetyParkPowerProcessHandler safetyParkPowerProcessHandler;

    @Autowired
    SafetyEmployeePrivateCarHandler safetyEmployeePrivateCarHandler;

    public Object pageList(SafetyBuildIssuedLogDTO param) {
        List<SafetyBuildIssuedLogDTO> list = safetyBuildIssuedLogService.selectPageList(param);
        long total = safetyBuildIssuedLogService.selectListCount(param);
        return new PageVO<>(total, list);
    }

    public void exportExcel(SafetyBuildIssuedLogDTO param, OutputStream outputStream) {
        param.setExport("export");
        List<SafetyBuildIssuedLogDTO> list = safetyBuildIssuedLogService.selectPageList(param);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        list.stream().forEach(en -> {
            //授权有效期
            String validDate = en.getBeginTime() + "-" + en.getEndTime();
            en.setValidDate(validDate);

            //来源
            String sourceType = en.getSourceType();
            if ("2".equals(en.getSourceType())) {
                en.setTargetId("人员变更权限确认");
            }
        });

        EasyExcel.write(outputStream, SafetyBuildIssuedLogDTO.class)
                .registerWriteHandler(new ColumnHeightAuto())
                .registerWriteHandler(new ColumnWidthAuto())
                .sheet("Sheet1")
                .doWrite(list);
    }

    /**
     * 手动下发所有下发异常的门禁
     * @param token
     */
    public void downBySelf(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已过期，请重新登录！");
        }
        Map<String, String> user = TokenUtils.getTockenUser(token);

        //查询所有下发异常的门禁
        List<SafetyBuildIssuedLogDO> exceptDataList = safetyBuildIssuedLogService.getExceptData();
        if (CollectionUtils.isEmpty(exceptDataList)) {return;}

        ArrayList<Integer> successList = new ArrayList<>();
        //过滤出下发异常的门禁
        List<SafetyBuildIssuedLogDO> buildList = exceptDataList.stream().filter(e -> StringUtils.isBlank(e.getModelId())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(buildList)){
            return;
        }
        List<SafetyBuildIssuedLogDO> subBuildList = buildList.stream().filter(e->"1".equals(e.getDeviceType())).collect(Collectors.toList());

        //一个人的下发门禁记录可能有多条，因此按照创建时间顺序排列，之后挨个下发，记录下发成功的日志ID，之后在更新该日志的状态
        for (SafetyBuildIssuedLogDO e : subBuildList) {
            String employeeCode = e.getEmployeeCode();
            String deviceCode = e.getDeviceCode();
            if(StringUtils.isBlank(deviceCode) || StringUtils.isBlank(employeeCode)){continue;}

            String sendStatus = safetyBuildIssuedLogService.issuedBuildPower(e);
            if("成功".equals(sendStatus)){
                //下发成功更新门禁信息
                successList.add(e.getId());
            }
        }

        //下发成功后更新门禁下发的状态
        if(!CollectionUtils.isEmpty(successList)){
            safetyBuildIssuedLogService.batchUpdate(successList,StringUtils.isBlank(user.get("realName"))?"":user.get("realName"));
        }
        //过滤出下发异常的大门
        List<SafetyBuildIssuedLogDO> guardList = exceptDataList.stream().filter(e -> "park_power_process".equals(e.getModelId())).collect(Collectors.toList());
        //更具userCode查询出对应的大门信息
        if (!CollectionUtils.isEmpty(guardList)) {
            String realName = StringUtils.isBlank(user.get("realName"))?"":user.get("realName");
            downGuardPower(guardList,realName);
        }
    }

    /**
     * 下发厂区大门权限
     * @param guardList
     */
    public void downGuardPower(List<SafetyBuildIssuedLogDO> guardList,String realName){
        List<String> successList = new ArrayList<>();
        List<String> hasDownList = new ArrayList<>();
        List<String> collect = guardList.stream().map(e -> e.getEmployeeCode()).collect(Collectors.toList());
        List<ParamDTO> userList = safetyParkEmployeePowerInfoService.getUserList(collect);

        if(CollectionUtils.isEmpty(userList)){return;}
        List<String> empnoList = userList.stream().map(ParamDTO::getEmpNo).collect(Collectors.toList());
        //查询员工对应的车辆信息
        SafetyEmployeePrivateCarDO privateCarDO = new SafetyEmployeePrivateCarDO();
        privateCarDO.setEmpNoList(empnoList);
        privateCarDO.setLockFlag("0"); //只查询未锁定的车辆
        List<SafetyEmployeePrivateCarDO> privateCarDOS = safetyEmployeePrivateCarHandler.selectList(privateCarDO);
        Map<String, List<SafetyEmployeePrivateCarDO>> carinfoMap = privateCarDOS.stream().collect(Collectors.groupingBy(SafetyEmployeePrivateCarDO::getEmployeeNo));

        // 初始化组织ID和curId
        safetyParkPowerProcessHandler.generalOrgId(userList);

        for (ParamDTO paramDTO : userList) {
            String empNo = paramDTO.getEmpNo();
            //因为是取得这个人的所有大门权限进行下发的，所以一个人只需要下发一次就行
            if(hasDownList.contains(empNo)){continue;}

            hasDownList.add(paramDTO.getEmpNo());
            String powerEndTime = DateUtils.format(DateUtils.parse(paramDTO.getPowerEndTime(), 1),1)+" 23:59:59";
            paramDTO.setBeginDate(DateUtils.format(DateUtils.addDays(new Date(), -1), 1) + " 00:00:00");
            paramDTO.setEndDate(powerEndTime);
            List<SafetyEmployeePrivateCarDO> carList = carinfoMap.get(empNo);
            boolean b = safetyParkPowerProcessHandler.powerUpdate(paramDTO, carList);

            if(b){
                successList.add(paramDTO.getEmpNo());
            }
        }

        LogUtils.info("大门权限手动下发的人员有：{}，操作人：{}",successList,realName);
        if(!CollectionUtils.isEmpty(successList)){
            safetyBuildIssuedLogService.batchUpdateGuard(successList,realName);
        }
    }

    /**
     * 根据员工号和设备号对设备进行去重
     * @param map
     * @param employeeCode
     * @param deviceCode
     * @return
     */
    public String dealData(Map<String, String> map,String employeeCode,String deviceCode){
        String s = map.get(employeeCode);
        if(StringUtils.isBlank(s)){
            return deviceCode;
        }

        StringBuilder resultStr = new StringBuilder();
        String[] split = deviceCode.split(",");
        for (String str : split){
            if(s.contains(str)){continue;}
            resultStr.append(str).append(",");
        }

        if(resultStr.toString().endsWith(",")){
            resultStr = new StringBuilder(resultStr.substring(0, resultStr.length() - 1));
        }

        return resultStr.toString();
    }
}
