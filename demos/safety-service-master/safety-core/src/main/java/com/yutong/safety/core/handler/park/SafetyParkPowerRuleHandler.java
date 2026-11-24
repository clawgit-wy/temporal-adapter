package com.yutong.safety.core.handler.park;

import com.yutong.safety.common.param.web.EmployeeMessageListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.emp.SafetyDepartChangeListDO;
import com.yutong.safety.core.db.entity.emp.param.DepartChangeListParam;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.db.entity.park.SafetyParkPowerRuleDO;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeePowerListParam;
import com.yutong.safety.core.db.entity.park.param.ParkPowerRuleAddParam;
import com.yutong.safety.core.db.entity.park.param.ParkPowerRuleListParam;
import com.yutong.safety.core.db.entity.park.vo.ParkPowerRuleVO;
import com.yutong.safety.core.dto.ParamDTO;
import com.yutong.safety.core.handler.employeeprivatecar.SafetyEmployeePrivateCarHandler;
import com.yutong.safety.core.service.SafetyFieldManageService;
import com.yutong.safety.core.service.SafetyOutsiderPromiseChangeService;
import com.yutong.safety.core.service.emp.SafetyDepartChangeListService;
import com.yutong.safety.core.service.park.SafetyParkEmpPowerReportService;
import com.yutong.safety.core.service.park.SafetyParkPowerProcessService;
import com.yutong.safety.core.service.park.SafetyParkPowerRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SafetyParkPowerRuleHandler {

    @Autowired
    private SafetyParkPowerRuleService safetyParkPowerRuleService;

    @Resource
    private SafetyFieldManageService safetyFieldManageService;

    @Resource
    private SafetyDepartChangeListService safetyDepartChangeListService;

    @Resource
    private SafetyParkPowerProcessService safetyParkPowerProcessService;

    @Resource
    private SafetyParkPowerProcessHandler safetyParkPowerProcessHandler;
    @Resource
    private SafetyOutsiderPromiseChangeService safetyOutsiderPromiseChangeService;

    @Resource
    private SafetyParkEmpPowerReportService safetyParkEmpPowerReportService;

    @Resource
    private SafetyEmployeePrivateCarHandler safetyEmployeePrivateCarHandler;
    /**
     * 保存更新
     * @param addParam 入参
     * @return 返回值
     */
    public int saveOrUpdate(ParkPowerRuleAddParam addParam){
        if(StringUtils.isNotBlank(addParam.getFactoryPower())){
            Map<Integer,String> factoryList = safetyFieldManageService.getFactoryNameList();
            String[] idArr = addParam.getFactoryPower().split(",");
            StringBuilder factoryNameStr = new StringBuilder();
            for(String id:idArr){
                String curName = factoryList.get(Integer.parseInt(id.trim()));
                if(StringUtils.isNotBlank(curName)){
                    if(!"".contentEquals(factoryNameStr)){
                        factoryNameStr.append(",");
                    }
                    factoryNameStr.append(curName);
                }
            }
            addParam.setFactoryNamePower(factoryNameStr.toString());
        }
        if(addParam.getId() == null){
            return safetyParkPowerRuleService.insert(addParam);
        }
        return safetyParkPowerRuleService.update(addParam);
    }

    /**
     * 更新
     * @param addParam
     * @return
     */
    public int update(ParkPowerRuleAddParam addParam){
        if(addParam.getId() == null){
            return 0;
        }
        return safetyParkPowerRuleService.update(addParam);
    }

    /**
     * 批量删除
     * @param idStr
     * @return
     */
    public int deleteBatch(String idStr){
        if(StringUtils.isBlank(idStr)){
            return 0;
        }
        ParkPowerRuleAddParam param = new ParkPowerRuleAddParam();
        param.setIsDelete(1);
        List<String> idList = Arrays.asList(idStr.split(","));
        return safetyParkPowerRuleService.updateBatchByIdList(param,idList);
    }

    /**
     * 分页查询
     * @param param
     * @return
     */
    public Object pageList(ParkPowerRuleListParam param){
        List<ParkPowerRuleVO> list = safetyParkPowerRuleService.selectPageList(param);
        long total = safetyParkPowerRuleService.selectCount(param);

        return new PageVO<>(total,list);
    }

    /**
     * 只更新增量人员的人车权限
     * @param beginTime 起始时间
     * @return 执行结果
     */
    public Object doTaskOnlyEmployee(String beginTime){
        if(StringUtils.isBlank(beginTime)){
            // 时间格式：yyyy-MM-dd HH:mm:ss
            beginTime = DateUtils.format(DateUtils.addDays(new Date(),-1),2);
        }
        DepartChangeListParam changeParam = new DepartChangeListParam();
        changeParam.setUpdateTimeBegin(beginTime);
        changeParam.setChangeType("否");
        List<SafetyDepartChangeListDO> changeListDOS = safetyDepartChangeListService.selectList(changeParam);
        if(CollectionUtils.isEmpty(changeListDOS)){
            LogUtils.info("自{}以来未检测到组织变更的人员信息",beginTime);
            return true;
        }
        String empNoList = changeListDOS.stream().map(SafetyDepartChangeListDO::getEmployeeCode).collect(Collectors.joining(","));
        EmployeeMessageListParam empParam = new EmployeeMessageListParam();
        empParam.setUserType("ZS");
        List<ParamDTO> employeeList = safetyParkPowerProcessService.getUserListByEmpNo(empNoList,"ZS");
        if(CollectionUtils.isEmpty(employeeList)){
            LogUtils.info("自{}以来未检测到组织变更的正式员工信息",beginTime);
            return true;
        }
        issusePowerByEmp(employeeList);
        return true;
    }

    /**
     * 逐个员工下发权限，需要单独匹配每个人使用的规则
     * @param employeeList 员工现有默认权限
     * @return 执行状态
     */
    public boolean issusePowerByEmp(List<ParamDTO> employeeList){
        boolean falg = true;
        for(ParamDTO curDto:employeeList){
            String empNo = curDto.getEmpNo();
            SafetyParkPowerRuleDO powerRuleDO = safetyParkPowerRuleService.getFirstRuleByEmpNo(empNo);
            if(powerRuleDO == null){
                LogUtils.info("用户{}未找到默认规则",empNo);
                continue;
            }
            String factoryId = powerRuleDO.getFactoryPower();
            List<ParamDTO> nowList = new ArrayList<>();
            nowList.add(curDto);
            try{
                safetyParkPowerProcessHandler.downPower(nowList,"1",factoryId,curDto.getPowerEndTime(),"ZS","");
            }catch (Exception e){
                falg = false;
                LogUtils.error("员工{}权限下发失败，{}",empNo,e);
            }
        }

        return falg;
    }

    /**
     * 根据人员厂区权限同步车权限
     * @param empNo 员工号拼接串
     */
    public void syncCarParkPower(String empNo){
        ParkEmployeePowerListParam listParam = new ParkEmployeePowerListParam();
        listParam.setPageFlag(true);
        listParam.setDownMark("1"); //只查询未同步车辆的员工信息
        List<String> empNoList;
        if(StringUtils.isBlank(empNo)){
            empNoList = safetyParkEmpPowerReportService.getUnSyncCarList();
        }else{
            empNoList = Arrays.asList(empNo.split(","));
        }
        int total = empNoList.size();
        int pageSize = 1;
        if(total > 500){
            pageSize = total/500+(total%500>0?1:0);
        }
        for(int i=0;i<pageSize;i++){
            List<String> sucessEmp = new ArrayList<>();

            int begin = i*500;
            int end = Math.min((i + 1) * 500, total);
            List<String> subList = empNoList.subList(begin,end);
            String subEmpStr = subList.stream().collect(Collectors.joining(","));
            List<ParamDTO> userList = safetyParkPowerProcessService.getUserListByEmpNo(subEmpStr,"ZS");

            List<String> empnoList = userList.stream().map(ParamDTO::getEmpNo).collect(Collectors.toList());
            //查询员工对应的车辆信息
            SafetyEmployeePrivateCarDO privateCarDO = new SafetyEmployeePrivateCarDO();
            privateCarDO.setEmpNoList(empnoList);
            privateCarDO.setLockFlag("0"); //只查询未锁定的车辆
            List<SafetyEmployeePrivateCarDO> privateCarDOS = safetyEmployeePrivateCarHandler.selectList(privateCarDO);
            Map<String, List<SafetyEmployeePrivateCarDO>> carinfoMap = privateCarDOS.stream().collect(Collectors.groupingBy(SafetyEmployeePrivateCarDO::getEmployeeNo));

            for(ParamDTO curDto:userList){
                List<SafetyEmployeePrivateCarDO> carList = carinfoMap.get(curDto.getEmpNo());
                if (CollectionUtils.isEmpty(carList)) {
                    continue;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("regionName", "大门车行");
                map.put("empowerType", "员工车辆");
                String carDevices = safetyOutsiderPromiseChangeService.findDevices(map);

                if (StringUtils.isEmpty(carDevices)) {
                    LogUtils.info("{\"code\":\"9999\",\"msg\":\"员工号：" + curDto.getEmpNo() + "，未获取到 设备类型(门禁) \"员工车辆\" 授权设备号！\"}");
                } else {
                    boolean carResult = safetyParkPowerProcessHandler.downCarPower(curDto, carDevices, carList,curDto.getUserType());
                    if(carResult){
                        sucessEmp.add(curDto.getEmpNo());
                    }
                }
            }
            //避免in条件吃熬过1000，在分页处理逻辑内更新
            if(!CollectionUtils.isEmpty(sucessEmp)){
                safetyParkEmpPowerReportService.modifyDownMark(sucessEmp);
            }
        }
    }
}
