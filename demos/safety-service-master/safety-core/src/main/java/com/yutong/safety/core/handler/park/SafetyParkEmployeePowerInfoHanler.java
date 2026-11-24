package com.yutong.safety.core.handler.park;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.EmpowerDeviceListParam;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetyEmpowerDeviceDO;
import com.yutong.safety.core.db.entity.build.param.BuildEmployeePowerListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildEmployeePowerVO;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerInfoDO;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeePowerListParam;
import com.yutong.safety.core.dto.SafetyOutsiderPromiseDTO;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.SafetyEmpowerDeviceService;
import com.yutong.safety.core.service.SafetyOutsiderPromiseChangeService;
import com.yutong.safety.core.service.park.SafetyParkEmployeePowerInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SafetyParkEmployeePowerInfoHanler {
    @Resource
    private SafetyParkEmployeePowerInfoService safetyParkEmployeePowerInfoService;
    @Resource
    private SafetyEmpowerDeviceService safetyEmpowerDeviceService;
    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Resource
    private SafetyOutsiderPromiseChangeService safetyOutsiderPromiseChangeService;

    /**
     * 根据员工号获取详情
     * @param userCode
     * @return
     */
    public List<SafetyParkEmployeePowerInfoDO> getDetailById(String userCode) {
        if (StringUtils.isBlank(userCode)) {
            return null;
        }

        return safetyParkEmployeePowerInfoService.getDetailById(userCode);
    }

    public String getEmpCarDevices(String empNo){
        //1.取员工信息台账，判断是否正式工
        SafetyEmployeeMessageDO employeeDO = safetyEmployeeMessageService.getByNo(empNo);
        if(ObjectUtils.isEmpty(employeeDO)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"未找到员工"+empNo+"的信息");
        }
        List<Integer> factoryIdList = new ArrayList<>();
        //2.正式工，取ParkEmployeePowerInfo表，非正式工取外包外协权限表
        if("ZS".equals(employeeDO.getUserType())){
            List<SafetyParkEmployeePowerInfoDO> list = safetyParkEmployeePowerInfoService.getValidPowerByEmp(empNo);
            if(CollectionUtils.isEmpty(list)){
                LogUtils.info("用户{}当前无工厂权限，请联系管理员处理",empNo);
                throw new BizException(ResponseEnum.FAIL.getCode(),"员工"+empNo+"当前无工厂权限，请联系管理员处理");
            }
            factoryIdList = list.stream().map(e->Integer.valueOf(e.getFactoryId())).distinct().collect(Collectors.toList());
        }
        else{
            List<SafetyOutsiderPromiseDTO> dtos = safetyOutsiderPromiseChangeService.findOldPromise(empNo,"");
            if(CollectionUtils.isEmpty(dtos)){
                LogUtils.info("用户{}当前无工厂权限，请联系管理员处理",empNo);
                throw new BizException(ResponseEnum.FAIL.getCode(),"员工"+empNo+"当前无工厂权限，请联系管理员处理");
            }
            String factoryId = dtos.get(0).getFactory();
            if(StringUtils.isNotBlank(factoryId)){
                factoryIdList = Arrays.stream(factoryId.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            }
        }
        if(CollectionUtils.isEmpty(factoryIdList)){
            LogUtils.info("用户{}当前无工厂权限，请联系管理员处理",empNo);
            throw new BizException(ResponseEnum.FAIL.getCode(),"员工"+empNo+"当前无工厂权限，请联系管理员处理");
        }
        EmpowerDeviceListParam param = new EmpowerDeviceListParam();
        param.setFactoryIdList(factoryIdList);
        param.setEmpowerType("员工车辆");
        param.setRegionName("大门车行");
        List<SafetyEmpowerDeviceDO>  deviceDOS = safetyEmpowerDeviceService.selectList(param);

        String deviceStr = deviceDOS.stream().map(SafetyEmpowerDeviceDO::getEmpowerNo).collect(Collectors.joining(","));
        if(StringUtils.isNotBlank(deviceStr)){
            Set<String> device = Arrays.stream(deviceStr.split(","))
                    .collect(Collectors.toSet());
            return String.join(",",device);
        }
        return deviceStr;
    }

    /**
     * 校验员工是否有厂区权限
     * @param empNo 员工号
     * @return 返回值
     */
    public boolean checkEmployeeForPark(String empNo){
        if(org.springframework.util.StringUtils.isEmpty(empNo)){
            return false;
        }
        if(empNo.startsWith("9")){
            ParkEmployeePowerListParam listParam = new ParkEmployeePowerListParam();
            listParam.setEmpNo(empNo);
            listParam.setDeleteFlag("0");
            listParam.setPageFlag(false);
            List<SafetyParkEmployeePowerInfoDO> empParkPowerList = safetyParkEmployeePowerInfoService.selectList(listParam);
            if(CollectionUtils.isEmpty(empParkPowerList)){
                return false;
            }
            return true;
        }
        SafetyOutsiderPromiseDTO curOut = safetyOutsiderPromiseChangeService.getOutsiderPromiseByEmp(empNo);
        if(curOut == null || com.alibaba.excel.util.StringUtils.isBlank(curOut.getFactory())){
            return false;
        }
        return true;
    }
}
