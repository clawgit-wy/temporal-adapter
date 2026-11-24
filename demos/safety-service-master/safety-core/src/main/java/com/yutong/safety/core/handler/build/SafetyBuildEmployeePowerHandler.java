package com.yutong.safety.core.handler.build;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDO;
import com.yutong.safety.core.db.entity.build.param.BuildDeviceListParam;
import com.yutong.safety.core.db.entity.build.param.BuildEmployeePowerListParam;
import com.yutong.safety.core.db.entity.build.param.BuildPowerListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildEmployeePowerVO;
import com.yutong.safety.core.db.entity.build.vo.BuildEmployeeVO;
import com.yutong.safety.core.dto.build.SafetyBuildEmpPowerDTO;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.SafetySysUserService;
import com.yutong.safety.core.service.build.SafetyBuildDeviceService;
import com.yutong.safety.core.service.build.SafetyBuildEmployeePowerService;
import com.yutong.safety.core.service.build.SafetyBuildPowerService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SafetyBuildEmployeePowerHandler {

    private static String BUILD_ADMIN_ROLE = "门禁系统管理员,超级管理员";
    @Resource
    private SafetyBuildEmployeePowerService safetyBuildEmployeePowerService;
    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Resource
    private SafetyBuildPowerService safetyBuildPowerService;
    @Resource
    private SafetyBuildProcessHandler safetyBuildProcessHandler;
    @Autowired
    private SafetySysUserService safetySysUserService;
    @Autowired
    private SafetyBaseHandler safetyBaseHandler;
    @Autowired
    private SafetyBuildDeviceService safetyBuildDeviceService;

    /**
     * @param listParam 入参
     * @param token     令牌
     * @return 集合
     */
    public List<SafetyBuildEmployeePowerDO> selectList(BuildEmployeePowerListParam listParam,
                                                       String token) {
        listParam.setPageFlag(false);
        return safetyBuildEmployeePowerService.selectByExample(listParam);
    }

    /**
     * 分页查询
     * @param listParam
     * @param token
     * @return
     */
    public Object pageList(BuildEmployeePowerListParam listParam,
                           String token) {
        //根据当前用户类型 设置检索条件 1-员工 只能看自己提交的；2-管理员查看所有 3-保密负责人 能看部门以及自己提交的
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if (userType.startsWith("3")) {
            String loginEmpNo = TokenUtils.getWorkNo(token);
            String dutyDeptCode = userType.split(":")[1];
            listParam.setFilterDutyDept(dutyDeptCode);
            //获取门禁管理员管理的门禁，可查看门禁下的所有员工
            List<String> devoceCodeList = initDutyDeviceList(loginEmpNo);
            listParam.setDeviceCodeList(devoceCodeList);
        } else if (userType.startsWith("1")) {
            listParam.setRoleWorkNo(TokenUtils.getWorkNo(token));
        } else {
            listParam.setFilterDutyDept(null);
        }
        if (StringUtils.isNotBlank(listParam.getPowerTimeEnd())) {
            String endDate = DateUtils.format(DateUtils.addDays(DateUtils.parse(listParam.getPowerTimeEnd(), 1), 1), 1);
            listParam.setPowerTimeEnd(endDate);
        }
        listParam.setPageFlag(true);
        // 根据部门获取对应的所有下属部门
        if (StringUtils.isNotBlank(listParam.getDeptCode())) {
            List<String> empCodeList = safetyBaseHandler.getDeptCodeList(listParam.getDeptCode());
            if (!CollectionUtils.isEmpty(empCodeList)) {
                listParam.setDeptCodeList(empCodeList);
            }
            listParam.setDeptName(null);
        }
        long total = safetyBuildEmployeePowerService.selectCount(listParam);
        List<BuildEmployeePowerVO> list = safetyBuildEmployeePowerService.selectList(listParam);
        return new PageVO<>(total, list);
    }

    /**
     * 获取责任部门管理的门禁
     * @param empNo
     * @return
     */
    public List<String> initDutyDeviceList(String empNo){
        if(StringUtils.isBlank(empNo)){
            return null;
        }
        BuildPowerListParam listParam = new BuildPowerListParam();
        listParam.setEmpNo(empNo);
        List<SafetyBuildPowerDO> buildPowerDOS = safetyBuildPowerService.selectList(listParam);
        List<String> deviceCodeList = buildPowerDOS.stream().map(SafetyBuildPowerDO::getDeviceCode).distinct().collect(Collectors.toList());
        if(deviceCodeList.isEmpty()){
            return null;
        }
        return deviceCodeList;
    }

    /**
     * 导出
     * @param param
     * @param token
     * @return
     */
    public HSSFWorkbook export(BuildEmployeePowerListParam param, String token) {
        String userName = TokenUtils.getUserName(token);
        if (StringUtils.isBlank(userName)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "token已失效");
        }
        if (StringUtils.isNotBlank(param.getPowerTimeEnd())) {
            String endDate = DateUtils.format(DateUtils.addDays(DateUtils.parse(param.getPowerTimeEnd(), 1), 1), 1);
            param.setPowerTimeEnd(endDate);
        }
        //根据当前用户类型 设置检索条件 1-员工 只能看自己提交的；2-管理员查看所有 3-保密负责人 能看部门以及自己提交的
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if (userType.startsWith("3")) {
            String dutyDeptCode = userType.split(":")[1];
            param.setFilterDutyDept(dutyDeptCode);
            String loginEmpNo = TokenUtils.getWorkNo(token);
            List<String> devoceCodeList = initDutyDeviceList(loginEmpNo);
            param.setDeviceCodeList(devoceCodeList);
        } else if (userType.startsWith("1")) {
            param.setRoleWorkNo(TokenUtils.getWorkNo(token));
        }  else {
            param.setFilterDutyDept(null);
        }
        if (StringUtils.isNotBlank(param.getDeptCode())) {
            List<String> empCodeList = safetyBaseHandler.getDeptCodeList(param.getDeptCode());
            if (!CollectionUtils.isEmpty(empCodeList)) {
                param.setDeptCodeList(empCodeList);
            }
            param.setDeptName(null);
        }
        long count = safetyBuildEmployeePowerService.selectCount(param);
        if (count > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过" + Constants.FILE_SIZE + "条！");
        }
        List<BuildEmployeePowerVO> list = safetyBuildEmployeePowerService.selectList(param);
        String[] title = new String[]{"员工号", "姓名", "部门", "厂区权限", "楼栋权限", "门禁权限", "是否涉及绝密区", "保密负责人",
                "责任部门", "来源", "事由"};
        String[][] values = new String[list.size()][];
        BuildEmployeePowerVO employeePowerDO;
        for (int i = 0; i < list.size(); i++) {
            employeePowerDO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = employeePowerDO.getEmployeeCode();
            values[i][1] = employeePowerDO.getEmployeeName();
            values[i][2] = employeePowerDO.getDeptName();
            values[i][3] = employeePowerDO.getAnFactoryName();
            values[i][4] = employeePowerDO.getAnBuilding();
            values[i][5] = employeePowerDO.getDeviceName();
            values[i][6] = employeePowerDO.getIsTopSecurity();
            values[i][7] = employeePowerDO.getDutyEmpName();
            values[i][8] = employeePowerDO.getDutyDeptName();
            values[i][9] = employeePowerDO.getSourceType();
            values[i][10] = employeePowerDO.getRemark();
        }
        String sheetName = "楼宇门禁申请流程列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 根据员工号，获取员工信息-授权门禁-以及部门保密负责人等信息
     *
     * @param employeeCode 员工号
     * @return 返回值
     */
    public BuildEmployeeVO getBuildByEmployeeForDiy(String employeeCode) {
        BuildEmployeePowerListParam listParam = new BuildEmployeePowerListParam();
        listParam.setFilterFlag(1);
        listParam.setEmployeeCode(employeeCode);
        BuildEmployeeVO buildEmployeeVO = new BuildEmployeeVO();

        SafetyEmployeeMessageDO employeeMessageDO = safetyEmployeeMessageService.getByNo(employeeCode);
        if (ObjectUtils.isEmpty(employeeMessageDO)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "未找到员工" + employeeCode + "的信息");
        }
        BeanUtils.copyProperties(employeeMessageDO, buildEmployeeVO);
        //获取员工的已授权门禁
        List<SafetyBuildEmployeePowerDO> empBuildPowerDO = safetyBuildEmployeePowerService.selectByExample(listParam);
        buildEmployeeVO.setBuildPowerList(empBuildPowerDO);

        try {
            List<SafetyBuildPowerDO> buildManageList = safetyBuildPowerService.selectListByDept(employeeMessageDO.getOrgCodePath());
            if (!CollectionUtils.isEmpty(buildManageList)) {
                List<String> empNoList = buildManageList.stream().map(SafetyBuildPowerDO::getEmployeeCode).distinct().collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(empNoList)) {
                    List<SafetyEmployeeMessageDO> dutyEmpList = safetyEmployeeMessageService.getListByStaffNo(empNoList);
                    buildEmployeeVO.setDutyPersonList(dutyEmpList);
                    String personStr = dutyEmpList.stream().map(e -> e.getEmpName() + e.getEmpNo() + e.getPhoneNo()).distinct().collect(Collectors.joining(";"));
                    buildEmployeeVO.setDutyPersonStr(personStr);
                    buildEmployeeVO.setBuildSysManagerRole(buildManageList.get(0).getBuildSysManagerRole()); //设置EIP角色
                }
            }

            if (CollectionUtils.isEmpty(buildEmployeeVO.getDutyPersonList())) {
                UserMessageListParam userParam = new UserMessageListParam();
                userParam.setRoleName(BUILD_ADMIN_ROLE);
                List<SafetySysUserDO> adminList = safetySysUserService.getList(userParam);
                buildEmployeeVO.setBuildAdminList(adminList);
            }

        } catch (Exception e) {
            LogUtils.error(ResponseEnum.FAIL.getCode(), e);
        }
        return buildEmployeeVO;
    }

    /**
     * 初始化富士门禁权限
     * @return 返回
     */
    public Object initCrkBuildPower(String empNos){
        String pageSize = "500";
        JSONObject firstObjet = safetyBaseHandler.getCrkBuildPower(empNos,pageSize,"1");
        JSONObject state = firstObjet.getJSONObject("State");
        Boolean isSuccess = state.getBoolean("IsSucess");
        if(!isSuccess){
            LogUtils.info("获取门禁权限异常，返回参数为{}",firstObjet.toJSONString());
        }
        JSONObject pageAttri = firstObjet.getJSONObject("PageAttri");
        Integer totalCount = pageAttri.getInteger("TotalCount");
        if(totalCount <= 0){
            LogUtils.info("获取门禁权限异常，返回参数为{}",firstObjet.toJSONString());
        }
        //取出所有的富士门禁
        BuildDeviceListParam deviceParam = new BuildDeviceListParam();
        deviceParam.setDeviceType("2");
        List<SafetyBuildDeviceDO> deviceDOS = safetyBuildDeviceService.selectByExample(deviceParam);
        Map<String,SafetyBuildDeviceDO> deviceMap = deviceDOS.stream().collect(Collectors.toMap(SafetyBuildDeviceDO::getDeviceCode, o->o));
        //对第一页的数据进行保存
        int totalPage = totalCount/500 + totalCount%500>0?1:0;
        //分页查询并保存
        crkBuildPowerSave(firstObjet,deviceMap);
        for(int i=2;i<=totalPage;i++){
            JSONObject pageObject = safetyBaseHandler.getCrkBuildPower(empNos,pageSize,String.valueOf(i));
            crkBuildPowerSave(pageObject,deviceMap);
        }
        return null;
    }

    /**
     * 处理接收到的数据，并保存入库
     * @param object
     * @param deviceMap
     */
    public void crkBuildPowerSave(JSONObject object,Map<String,SafetyBuildDeviceDO> deviceMap){
        JSONArray records = object.getJSONArray("Records");
        List<SafetyBuildEmpPowerDTO> empDtos = safetyBaseHandler.formatBuildEmpPowerDTO(records);
        if(!CollectionUtils.isEmpty(empDtos)){
            List<String> powerEmpNos = empDtos.stream().map(SafetyBuildEmpPowerDTO::getEmployeeCode).distinct().collect(Collectors.toList());
            List<SafetyEmployeeMessageDO> employeeMessageDOS = safetyEmployeeMessageService.getListByStaffNo(powerEmpNos);
            if(CollectionUtils.isEmpty(employeeMessageDOS)){
                LogUtils.info("根据员工号{}，未找到有效员工信息");
                return;
            }
            Map<String,SafetyEmployeeMessageDO> employeeMap = employeeMessageDOS.stream().collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, o->o));;
            List<SafetyBuildEmployeePowerDO> employeePowerDOS = safetyBaseHandler.formatEmpDataForUpdate(deviceMap,empDtos,employeeMap);
            if(!CollectionUtils.isEmpty(employeePowerDOS)){
                safetyBuildEmployeePowerService.updateBatchForCrkInit(employeePowerDOS);
            }
        }
    }
}
