package com.yutong.safety.core.handler.park;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.constants.UserTypeEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildIssuedLogDO;
import com.yutong.safety.core.db.entity.build.param.BuildEmployeePowerListParam;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.db.entity.employeeprivatecar.vo.SafetyCarInfoVO;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerInfoDO;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeePowerListParam;
import com.yutong.safety.core.db.entity.park.vo.ParkEmployeePowerVO;
import com.yutong.safety.core.dto.ParamDTO;
import com.yutong.safety.core.dto.SafetyOutsiderPromiseDTO;
import com.yutong.safety.core.dto.park.SafetyParkEmployeePowerInfoDTO;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.handler.build.SafetyBuildProcessHandler;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.SafetyFieldManageService;
import com.yutong.safety.core.service.SafetyOutsiderPromiseChangeService;
import com.yutong.safety.core.service.build.SafetyBuildEmployeePowerService;
import com.yutong.safety.core.service.build.SafetyBuildIssuedLogService;
import com.yutong.safety.core.service.employeeprivatecar.SafetyEmployeePrivateCarService;
import com.yutong.safety.core.service.park.SafetyParkEmpPowerReportService;
import com.yutong.safety.core.service.park.SafetyParkEmployeePowerInfoService;
import com.yutong.safety.core.service.park.SafetyParkPowerProcessService;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SafetyParkEmpPowerReportHandler {

    @Resource
    private SafetyParkEmpPowerReportService safetyParkEmpPowerReportService;
    @Resource
    private SafetyFieldManageService safetyFieldManageService;
    @Resource
    private SafetyParkEmployeePowerInfoService safetyParkEmployeePowerInfoService;
    @Resource
    private SafetyOutsiderPromiseChangeService safetyOutsiderPromiseChangeService;
    @Resource
    private SafetyBuildEmployeePowerService safetyBuildEmployeePowerService;
    @Resource
    private SafetyParkPowerProcessHandler safetyParkPowerProcessHandler;
    @Resource
    private SafetyParkPowerProcessService safetyParkPowerProcessService;
    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Autowired
    SafetyBuildIssuedLogService safetyBuildIssuedLogService;
    @Autowired
    private SafetyEmployeePrivateCarService safetyEmployeePrivateCarService;
    @Autowired
    private SafetyBaseHandler safetyBaseHandler;
    @Resource
    private SafetyBuildProcessHandler safetyBuildProcessHandler;

    public Object getPageList(ParkEmployeePowerListParam param, String token){
        param.setPageFlag(true);
        // 数据参数处理
        beforeSelectList(param, token);
        List<ParkEmployeePowerVO> list = safetyParkEmpPowerReportService.selectPowerPageList(param);
        processData(list);
        Long total = safetyParkEmpPowerReportService.selectReportCount(param);
        return new PageVO<>(total,list);
    }

    private void beforeSelectList(ParkEmployeePowerListParam param, String token) {
        // 权限
        String roleName = "超级管理员,门禁系统管理员,门卫,保卫主管,领导,指挥中心,治安管理员,园区权限负责人";
        String userType = safetyBuildProcessHandler.checkUserType(token, roleName);
        if (userType.startsWith("3")) {
            String dutyDeptCode = userType.split(":")[1];
            param.setFilterDutyDept(dutyDeptCode);
        } else if (userType.startsWith("1")) {
            String workNo = TokenUtils.getWorkNo(token);
            param.setFixedEmpNo(workNo);
        }
        // 当前部门的所有子部门
        if (!StringUtils.isEmpty(param.getDeptCode())) {
            List<String> empCodeList = safetyBaseHandler.getDeptCodeList(param.getDeptCode());
            if (!CollectionUtils.isEmpty(empCodeList)) {
                param.setDeptCodeList(empCodeList);
            }
        }
        // 车牌号
        List<SafetyCarInfoVO> safetyCarInfoVOS = new ArrayList<>();
        if (!StringUtils.isEmpty(param.getCarNo())) {
            safetyCarInfoVOS.addAll(safetyEmployeePrivateCarService.selectListByGroupEmpNo(param.getCarNo(), null, null));
        }
        if (!StringUtils.isEmpty(param.getLockCar())) {
            safetyCarInfoVOS.addAll(safetyEmployeePrivateCarService.selectListByGroupEmpNo(null, param.getLockCar(), null));
        }
        if (!CollectionUtils.isEmpty(safetyCarInfoVOS)) {
            List<String> collect = safetyCarInfoVOS.stream().map(SafetyCarInfoVO::getEmpNo).filter(li -> !StringUtils.isEmpty(li)).distinct().collect(Collectors.toList());
            param.setEmpNoList(collect);
        }
    }

    private void processData(List<ParkEmployeePowerVO> list) {
        if(!CollectionUtils.isEmpty(list)){
            Map<Integer,String> factoryMap = safetyFieldManageService.getFactoryNameList();
            List<String> empCodeList = list.stream().map(ParkEmployeePowerVO::getEmpNo).distinct().collect(Collectors.toList());
            BuildEmployeePowerListParam curParam = new BuildEmployeePowerListParam();
            curParam.setEmpCodeList(empCodeList);
            List<SafetyBuildEmployeePowerDO> buildPowerList = safetyBuildEmployeePowerService.getEmpPowerList(curParam);
            Map<String, List<SafetyBuildEmployeePowerDO>> empBuildPowerMap;
            if(CollectionUtils.isEmpty(buildPowerList)){
                empBuildPowerMap = new HashMap<>();
            }else{
                empBuildPowerMap = buildPowerList.stream().collect(Collectors.groupingBy(SafetyBuildEmployeePowerDO::getEmployeeCode));
            }
            // 车牌信息
            List<SafetyCarInfoVO> safetyCarInfoVOS = safetyEmployeePrivateCarService.selectListByGroupEmpNo(null, null, empCodeList);
            Map<String, SafetyCarInfoVO> carDataMap = safetyCarInfoVOS.stream().filter(li -> !StringUtils.isEmpty(li.getEmpNo())).collect(Collectors.toMap(SafetyCarInfoVO::getEmpNo, t -> t, (t1, t2) -> t1));

            list.forEach(e->{
                String empCode = e.getEmpNo();
                StringBuilder fNameStr = new StringBuilder();
                String[] factoryCodeStr = e.getFactoryCode().trim().split(",");
                for(String fCode :factoryCodeStr){
                    String fName = factoryMap.get(Integer.valueOf(fCode.trim()));
                    if(!StringUtils.isEmpty(fName)){
                        if(!fNameStr.toString().isEmpty()){
                            fNameStr.append(",");
                        }
                        fNameStr.append(fName);
                    }
                }
                e.setFactoryName(fNameStr.toString());
                SafetyCarInfoVO safetyCarInfoVO = carDataMap.get(e.getEmpNo());
                if (safetyCarInfoVO != null) {
                    e.setCarInfos(safetyCarInfoVO.getCarInfos());
                    e.setLockCars(safetyCarInfoVO.getLockCars());
                }

                List<SafetyBuildEmployeePowerDO> employeePowerDOList = empBuildPowerMap.get(empCode);
                if (!CollectionUtils.isEmpty(employeePowerDOList)) {
                    String deviceCode = employeePowerDOList.stream().map(SafetyBuildEmployeePowerDO::getDeviceCode).filter(li -> !StringUtils.isEmpty(li)).collect(Collectors.joining(","));
                    String deviceName = employeePowerDOList.stream().map(SafetyBuildEmployeePowerDO::getDeviceName).filter(li -> !StringUtils.isEmpty(li)).collect(Collectors.joining(","));
                    e.setDeviceCode(deviceCode);
                    e.setDeviceName(deviceName);
                }
            });
        }
    }

    /**
     * 查看权限详情
     * @param empNo 员工号
     * @param userType 员工类型 外协和非外协人员取表罗不一致
     * @return 返回结果
     */
    public Object viewDetail(String empNo,String userType){
        List<Map<String,String>> retList = new ArrayList<>();
        if("WX".equals(userType)){
            List<SafetyOutsiderPromiseDTO> infoDos = safetyOutsiderPromiseChangeService.findPromiseByName(empNo,"");
            if(CollectionUtils.isEmpty(infoDos)){
                return retList;
            }
            Map<Integer,String> factoryMap = safetyFieldManageService.getFactoryNameList();
            
            for(SafetyOutsiderPromiseDTO promiseDTO:infoDos){
                Map<String,String> curMap = new HashMap<>();
                StringBuilder fNameStr = new StringBuilder();
                String[] factoryCodeStr = promiseDTO.getFactory().trim().split(",");
                for(String fCode :factoryCodeStr){
                    String fName = factoryMap.get(Integer.valueOf(fCode.trim()));
                    if(!StringUtils.isEmpty(fName)){
                        if(!fNameStr.toString().isEmpty()){
                            fNameStr.append(",");
                        }
                        fNameStr.append(fName);
                    }
                }
                curMap.put("factoryName",fNameStr.toString());
                curMap.put("powerBeginTime","");
                curMap.put("powerEndTime",StringUtils.isEmpty(promiseDTO.getServiceEndDate())?"2099-12-31":promiseDTO.getServiceEndDate());
                retList.add(curMap);
            }
        }else {
            List<SafetyParkEmployeePowerInfoDO> infoDos = safetyParkEmployeePowerInfoService.getDetailById(empNo);
            for(SafetyParkEmployeePowerInfoDO curInfo:infoDos){
                Map<String,String> curMap = new HashMap<>();
                curMap.put("factoryName",curInfo.getFactoryName());
                curMap.put("powerBeginTime", curInfo.getPowerBeginTime().substring(0,10));
                curMap.put("powerEndTime", curInfo.getPowerEndTime().substring(0,10));
                retList.add(curMap);
            }
        }
        return retList;
    }

    /**
     * 取消权限
     * @param empNo 员工号
     * @param userType 用户类型
     * @param token 秘钥
     * @return 返回
     */
    public Object cancelPower(String empNo,String userType,String token){
        String reason = "超级管理员注销权限";
        String realName = TokenUtils.getRealName(token);
        String curDate = DateUtils.format(new Date(),1);
        int i  = 0;
        if("WX".equals(userType)){
            List<SafetyOutsiderPromiseDTO> infoDos = safetyOutsiderPromiseChangeService.findPromiseByName(empNo,"");
            if(!CollectionUtils.isEmpty(infoDos)){
                //注销时获取信息，不排除已离职人员
                SafetyEmployeeMessageDO employeeMessageDO = safetyEmployeeMessageService.getByNoForAll(empNo);
                SafetyOutsiderPromiseDTO promiseDTO = infoDos.get(0);
                if(!StringUtils.isEmpty(promiseDTO.getFactory())){
                    String serverEndDate = StringUtils.isEmpty(promiseDTO.getServiceEndDate())?"2099-12-31":promiseDTO.getServiceEndDate();
                    ParamDTO paramDTO = new ParamDTO();
                    paramDTO.setEmpNo(empNo);
                    if(!ObjectUtils.isEmpty(employeeMessageDO)){
                        paramDTO.setEmpName(employeeMessageDO.getEmpName());
                        paramDTO.setPhoneNo(employeeMessageDO.getPhoneNo());
                    }
                    paramDTO.setBeginDate(curDate);
                    paramDTO.setEndDate(serverEndDate);
                    List<ParamDTO> userList = new ArrayList<>();
                    userList.add(paramDTO);
                    //注销人车权限-并保存日志
                    safetyParkPowerProcessHandler.downPower(userList,"2",promiseDTO.getFactory(),curDate,userType,"");
                    //更新外包外协权限厂区置空
                    promiseDTO.setFactory("");
                    promiseDTO.setRemark(reason);
                    i = safetyOutsiderPromiseChangeService.cancelPower(promiseDTO.getEmpNo(),promiseDTO);
                }else{
                    LogUtils.info("员工{}，工厂权限为空，不执行注销人车权限操作",empNo);
                }
            }
        }else {
            List<ParamDTO> userList = safetyParkPowerProcessService.getUserListByEmpNo(empNo,userType);
            if(!CollectionUtils.isEmpty(userList)){
                String factoryCodeStr = userList.stream().map(ParamDTO::getFactoryId).collect(Collectors.joining(","));
                SafetyParkEmployeePowerInfoDO infoDO = new SafetyParkEmployeePowerInfoDO();
                infoDO.setIsDelete((short) 1);
                infoDO.setRemark(reason);
                infoDO.setUpdateTime(new Date());
                //注销人车权限-并保存日志
                safetyParkPowerProcessHandler.downPower(userList,"2",factoryCodeStr,curDate,userType,"");
            }
        }

        //注销门禁权限-并保存日志
        BuildEmployeePowerListParam listParam = new BuildEmployeePowerListParam();
        listParam.setPageFlag(false);
        listParam.setEmployeeCode(empNo);
        List<SafetyBuildEmployeePowerDO> buildPowerList = safetyBuildEmployeePowerService.selectByExample(listParam);
        if(CollectionUtils.isEmpty(buildPowerList)){
            return true;
        }
        //更新员工门禁权限
        safetyBuildEmployeePowerService.cancelBuildPowerForEmployee(buildPowerList,realName,reason);
        return i;
    }

    /**
     * 正式工权限下发
     * @param empNo
     */
    public void downPowerByEmpNO(String empNo){
        List<SafetyParkEmployeePowerInfoDTO> list = safetyParkEmployeePowerInfoService.selectListByEmpNo(empNo);

        if(CollectionUtils.isEmpty(list)){return;}
        StringBuilder factoryCode = new StringBuilder();
        String powerEndTime = list.get(0).getPowerEndTime();
        String empName = list.get(0).getEmployeeName();

        for (SafetyParkEmployeePowerInfoDTO dto : list) {
            if(!factoryCode.toString().contains(dto.getFactoryId())){
                factoryCode.append(dto.getFactoryId()).append(",");
            }
        }

        if(factoryCode.toString().endsWith(",")){
            factoryCode = new StringBuilder(factoryCode.substring(0, factoryCode.length() - 1));
        }

        SafetyBuildIssuedLogDO en = new SafetyBuildIssuedLogDO();
        List<SafetyBuildIssuedLogDO> logList = new ArrayList<>();
        en.setCreateTime(new Date());
        en.setBeginTime(DateUtils.format(new Date(),1)+" 00:00:00");
        en.setEmployeeCode(empNo);
        en.setIssuedType("开通");
        en.setEndTime(powerEndTime);
        en.setEmployeeName(empName);

        if(StringUtils.isEmpty(factoryCode.toString())){
            en.setRemark(empNo+"没有对应的授权厂区，不下发大门门禁！");
            logList.add(en);
            safetyBuildIssuedLogService.batchInsert(logList);
            return;
        }

        SafetyBuildIssuedLogDO param = new SafetyBuildIssuedLogDO();
        param.setEmployeeCode(empNo);
        param.setEmployeeName(empName);
        param.setEndTime(powerEndTime);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("empowerType","正式员工");
        paramMap.put("factoryIds",Arrays.asList(factoryCode.toString().split(",")));
        paramMap.put("regionName","大门人脸");

        // 获取组织ID
        Pair<String, Object> pair = safetyParkPowerProcessHandler.generalOrgId(empNo);
        try {
            //根据工厂ID 设备类型(人脸) "外协" 获取授权设备号
            String faceDevices = safetyOutsiderPromiseChangeService.findDevices(paramMap);
            if(StringUtils.isEmpty(faceDevices)){
                LogUtils.info("downPowerByEmpNO:员工号："+empNo+"，未获取到 设备类型(人脸) 授权设备号！");
            }else{
                //更新门禁人脸权限
                safetyBaseHandler.updateFaceData(param, faceDevices,pair);
            }
            //根据工厂ID 设备类型(门禁) "外协" 获取授权设备号
            paramMap.put("regionName","大门门禁");
            String cardDevices = safetyOutsiderPromiseChangeService.findDevices(paramMap);
            //更新门禁卡权限
            if(StringUtils.isEmpty(cardDevices)){
                LogUtils.info("{\"code\":\"9999\",\"msg\":\"downPowerByEmpNO:员工号："+empNo+"，未获取到 设备类型(门禁) \"正式员工\" 授权设备号！\"}");
            }else{
                safetyBaseHandler.updateCardData(param, cardDevices, "modify",pair);
            }
        } catch (Exception e) {
            en.setRemark(empNo+"下发大门门禁失败！");
            logList.add(en);
            safetyBuildIssuedLogService.batchInsert(logList);
            LogUtils.info("SafetyParkEmpPowerReportHandler.downPowerByEmpNO:员工号："+empNo+"，下发大门门禁异常！");
            return;
        }

        //获取车辆信息
        List<ParamDTO> userList = new ArrayList<>();
        ParamDTO paramDTO = new ParamDTO();
        paramDTO.setEmpNo(empNo);
        userList.add(paramDTO);
        List<String> empNoList = new ArrayList<>();
        empNoList.add(empNo);
        SafetyEmployeePrivateCarDO privateCarDO = new SafetyEmployeePrivateCarDO();
        privateCarDO.setEmpNoList(empNoList);
        privateCarDO.setLockFlag("0"); //只查询未锁定的车辆
        List<SafetyEmployeePrivateCarDO> carinfoList = safetyEmployeePrivateCarService.selectList(privateCarDO);
        if(!CollectionUtils.isEmpty(carinfoList)){
            paramMap.put("empowerType","员工车辆");
            paramMap.put("regionName","大门车行");
            String carDevices = safetyOutsiderPromiseChangeService.findDevices(paramMap);

            paramDTO.setEmpName(empName);
            paramDTO.setBeginDate(DateUtils.format(new Date(),1)+" 00:00:00");
            paramDTO.setEndDate(powerEndTime);

            if (StringUtils.isEmpty(carDevices)) {
                LogUtils.info("SafetyParkEmpPowerReportHandler.downPowerByEmpNO:员工号："+empNo+"，未获取到 大门车行 授权设备号！");
            } else {
                boolean carResult = safetyParkPowerProcessHandler.downCarPower(paramDTO, carDevices, carinfoList,"ZS");
            }
        }

        en.setRemark(empNo+"下发大门门禁成功！");
        logList.add(en);
        safetyBuildIssuedLogService.batchInsert(logList);
    }

    /**
     * 外包外协权限下发
     * @param empNo
     */
    public void downPowerByEmpNOForOutSider(String empNo){
        List<SafetyOutsiderPromiseDTO> list = safetyOutsiderPromiseChangeService.findOldPromise(empNo,"");
        if(CollectionUtils.isEmpty(list)){return;}
        String factoryCodeStr = list.get(0).getFactory();
        String powerEndTime = list.get(0).getServiceEndDate();
        String empName = list.get(0).getEmpName();

        //无厂区权限时，退出
        if(StringUtils.isEmpty(factoryCodeStr) || list.get(0).getStatus() == 0){
            LogUtils.info("没有对应的授权厂区或员工已离职，不下发大门权限！");
            return;
        }

        SafetyBuildIssuedLogDO en = new SafetyBuildIssuedLogDO();
        List<SafetyBuildIssuedLogDO> logList = new ArrayList<>();
        en.setCreateTime(new Date());
        en.setBeginTime(DateUtils.format(new Date(),1)+" 00:00:00");
        en.setEmployeeCode(empNo);
        en.setIssuedType("开通");
        en.setEndTime(powerEndTime);
        en.setEmployeeName(empName);

        SafetyBuildIssuedLogDO param = new SafetyBuildIssuedLogDO();
        param.setEmployeeCode(empNo);
        param.setEmployeeName(empName);
        param.setEndTime(powerEndTime);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("empowerType","外协");
        paramMap.put("factoryIds",Arrays.asList(factoryCodeStr.split(",")));
        paramMap.put("regionName","大门人脸");

        // 获取组织ID
        Pair<String, Object> pair = safetyParkPowerProcessHandler.generalOrgId(empNo);
        try {
            //根据工厂ID 设备类型(人脸) "外协" 获取授权设备号
            String faceDevices = safetyOutsiderPromiseChangeService.findDevices(paramMap);
            if(StringUtils.isEmpty(faceDevices)){
                LogUtils.info("downPowerByEmpNOForOutSider:员工号：{},厂区：{}，未获取到 设备类型(人脸) 授权设备号！",empNo,factoryCodeStr);
            }else{
                //更新门禁人脸权限
                safetyBaseHandler.updateFaceData(param, faceDevices,pair);
            }
            //根据工厂ID 设备类型(门禁) "外协" 获取授权设备号
            paramMap.put("regionName","大门门禁");
            String cardDevices = safetyOutsiderPromiseChangeService.findDevices(paramMap);
            //更新门禁卡权限
            if(StringUtils.isEmpty(cardDevices)){
                LogUtils.info("{\"code\":\"9999\",\"msg\":\"downPowerByEmpNOForOutSider:员工号：{},厂区：{}，未获取到 设备类型(门禁)外协授权设备号！\"}",empNo,factoryCodeStr);
            }else{
                safetyBaseHandler.updateCardData(param, cardDevices, "modify",pair);
            }
        } catch (Exception e) {
            en.setRemark(empNo+"下发大门门禁失败！");
            logList.add(en);
            safetyBuildIssuedLogService.batchInsert(logList);
            LogUtils.info("downPowerByEmpNOForOutSider:员工号："+empNo+"，下发大门门禁异常！");
            return;
        }

        ParamDTO paramDTO = new ParamDTO();
        paramDTO.setEmpNo(empNo);
        //获取车辆信息

        List<String> empNoList = new ArrayList<>();
        empNoList.add(empNo);
        SafetyEmployeePrivateCarDO privateCarDO = new SafetyEmployeePrivateCarDO();
        privateCarDO.setEmpNoList(empNoList);
        privateCarDO.setLockFlag("0"); //只查询未锁定的车辆
        List<SafetyEmployeePrivateCarDO> carinfoList = safetyEmployeePrivateCarService.selectList(privateCarDO);

        if(!CollectionUtils.isEmpty(carinfoList)){
            paramMap.put("empowerType","外协车辆");
            paramMap.put("regionName","大门车行");
            String carDevices = safetyOutsiderPromiseChangeService.findDevices(paramMap);

            paramDTO.setEmpName(empName);
            paramDTO.setBeginDate(DateUtils.format(new Date(),1)+" 00:00:00");
            paramDTO.setEndDate(powerEndTime);
            if (StringUtils.isEmpty(carDevices)) {
                LogUtils.info("downPowerByEmpNOForOutSider:员工号：{},厂区：{}，未获取到 大门车行 授权设备号！",empNo,factoryCodeStr);
            } else {
                boolean carResult = safetyParkPowerProcessHandler.downCarPower(paramDTO, carDevices, carinfoList,"WX");
            }
        }

        en.setRemark(empNo+"下发大门门禁成功！");
        logList.add(en);
        safetyBuildIssuedLogService.batchInsert(logList);
    }

    /**
     * 数据导出
     */
    public void export(HttpServletResponse response, ParkEmployeePowerListParam param, String token) throws IOException {
        String fileName = new String(("员工及车辆入厂权限" + DateUtils.format(new Date(), 5) + ".xlsx").getBytes(), "ISO8859-1");
        // 数据参数处理
        beforeSelectList(param, token);
        Long count = safetyParkEmpPowerReportService.selectReportCount(param);
        if (count > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), String.format("导出数据不能超过 %s 条！", Constants.FILE_SIZE));
        }
        List<ParkEmployeePowerVO> list = safetyParkEmpPowerReportService.selectPowerList(param);
        // 数据处理
        processData(list);

        String[] title = {"员工号", "姓名", "组织", "人员类型", "员工车辆", "锁定车辆", "授权厂区", "授权门禁", "备注"};
        // 使用流操作简化填充数据
        String[][] values = list.stream()
                .map(info -> new String[]{
                        info.getEmpNo(),
                        info.getEmpName(),
                        info.getOrgName(),
                        UserTypeEnum.valueOf(info.getUserType()).getName(),
                        info.getCarInfos(),
                        info.getLockCars(),
                        info.getFactoryName(),
                        info.getDeviceName(),
                        info.getRemark()
                })
                .toArray(String[][]::new);

        String sheetName = "员工及车辆入厂权限";
        HSSFWorkbook hssfWorkbook = ExcelUtils.getHSSFWorkbook(sheetName, title, values);

        response.setContentType("application/octet-stream;charset=ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        OutputStream os = response.getOutputStream();
        hssfWorkbook.write(os);
        os.flush();
        os.close();
    }
}
