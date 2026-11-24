package com.yutong.safety.core.service.build;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.build.*;
import com.yutong.safety.core.db.entity.build.param.BuildPowerListParam;
import com.yutong.safety.core.db.entity.build.param.BuildProcessAddParam;
import com.yutong.safety.core.db.entity.build.param.BuildProcessListParam;
import com.yutong.safety.core.db.entity.build.param.BuildProcessOpenListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildProcessListVO;
import com.yutong.safety.core.db.mapper.build.SafetyBuildDeviceDOMapper;
import com.yutong.safety.core.db.mapper.build.SafetyBuildProcessDOBizMapper;
import com.yutong.safety.core.db.mapper.build.SafetyBuildProcessDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SafetyBuildProcessService {

    @Autowired
    private SafetyBuildProcessDOMapper safetyBuildProcessDOMapper;

    @Autowired
    private SafetyBuildProcessDOBizMapper safetyBuildProcessDOBizMapper;

    @Autowired
    private SafetyBuildDeviceDOMapper safetyBuildDeviceDOMapper;

    @Autowired
    private SafetyBuildPowerService safetyBuildPowerService;

    public int updateByPrimaryKeySelective(SafetyBuildProcessDOWithBLOBs processDO){
        return safetyBuildProcessDOMapper.updateByPrimaryKeySelective(processDO);
    }

    public long countWithBlobs(BuildProcessListParam param) {
        return safetyBuildProcessDOBizMapper.countByExampleWithBLOBs(param);
    }

    /**
     *
     * @param param 入参
     * @return 返回
     */
    public List<BuildProcessListVO> selectListWithBlobs(BuildProcessListParam param) {
        param.setPageFlag(null);
        if(StringUtils.isBlank(param.getOrderByClause())){
            param.setOrderByClause(" create_time desc ");
        }
        return safetyBuildProcessDOBizMapper.selectByExampleWithBLOBs(param);
    }

    /**
     *
     * @param param 入参
     * @return 返回
     */
    public List<BuildProcessListVO> selectPageListWithBlobs(BuildProcessListParam param) {
        param.setPageFlag(1);
        if(StringUtils.isBlank(param.getOrderByClause())){
            param.setOrderByClause(" create_time desc ");
        }
        return safetyBuildProcessDOBizMapper.selectByExampleWithBLOBs(param);
    }

    /**
     *
     * @param param 入参
     * @return 返回
     */
    public List<SafetyBuildProcessDO> selectList(BuildProcessListParam param) {
        param.setPageFlag(null);
        SafetyBuildProcessDOExample example = formatExample(param);
        return safetyBuildProcessDOMapper.selectByExample(example);
    }

    public int updateByExampleSelective(BuildProcessListParam param,SafetyBuildProcessDOWithBLOBs record){
        SafetyBuildProcessDOExample example = formatExample(param);
        return safetyBuildProcessDOMapper.updateByExampleSelective(record,example);
    }

    /**
     *
     * @param param 入参
     * @return 返回
     */
    public List<SafetyBuildProcessDOWithBLOBs> selectPageList(BuildProcessListParam param) {
        param.setPageFlag(1);
        SafetyBuildProcessDOExample example = formatExample(param);
        return safetyBuildProcessDOMapper.selectByExampleWithBLOBs(example);
    }

    /**
     * 分页总条数
     * @param param 查询入参
     * @return 返回
     */
    public long selectListCount(BuildProcessListParam param) {
        SafetyBuildProcessDOExample example = formatExample(param);
        return safetyBuildProcessDOMapper.countByExample(example);
    }

    /**
     * 根据ID获取详情
     * @param id 主键ID
     * @return 接收结构
     */
    public SafetyBuildProcessDO selectByPrimaryKey(String id){
        return safetyBuildProcessDOMapper.selectByPrimaryKey(id);
    }

    /**
     * 更新规则
     * @param param 更新入参
     * @return 返回
     * @throws Exception 异常
     */
    public int save(BuildProcessAddParam param) throws Exception{
        SafetyBuildProcessDOWithBLOBs process = new SafetyBuildProcessDOWithBLOBs();
        BeanUtils.copyProperties(param,process);
        String createDate = DateUtils.format(new Date(),2);
        process.setCreateTime(createDate);
        process.setStatus("20"); //审批中
        StringBuilder powerType = new StringBuilder();
        //添加流程相关明细表
        List<SafetyBuildProcessOpenDO> openDOList = param.getOpenDOList();
        String adminApprovalStatus = "0";
        if(!CollectionUtils.isEmpty(openDOList)){
            List<String> deviceCodeList = openDOList.stream().map(SafetyBuildProcessOpenDO::getDeviceCode).distinct().collect(Collectors.toList());
            SafetyBuildDeviceDOExample example = new SafetyBuildDeviceDOExample();
            example.createCriteria().andDeviceCodeIn(deviceCodeList);
            List<SafetyBuildDeviceDO> deviceDOS =  safetyBuildDeviceDOMapper.selectByExample(example);
            if(CollectionUtils.isEmpty(deviceDOS)){
                throw new BizException(ResponseEnum.FAIL.getCode(),"指定要开通的门禁编码异常，请确认数据："+ Arrays.toString(deviceCodeList.toArray()));
            }
            Map<String,List<SafetyBuildDeviceDO>> deviceDOMap = deviceDOS.stream().collect(Collectors.groupingBy(SafetyBuildDeviceDO::getDeviceCode));
            powerType.append("1");
            //给开通门禁设置主表ID，设置厂区，楼栋等基本信息（便于后期检索）
            for (SafetyBuildProcessOpenDO element : openDOList) {
                element.setProcessId(param.getId());
                element.setCreateTime(createDate);
                List<SafetyBuildDeviceDO> curList = deviceDOMap.get(element.getDeviceCode());
                if (CollectionUtils.isEmpty(curList)) {
                    throw new BizException(ResponseEnum.FAIL.getCode(), "指定要开通的门禁编码异常，请确认数据：" + element.getDeviceCode());
                }
                SafetyBuildDeviceDO curDeviceDO = curList.get(0);
                element.setAnFactoryCode(curDeviceDO.getAnFactoryCode());
                element.setAnFactoryName(curDeviceDO.getAnFactoryName());
                element.setAnBuildingCode(curDeviceDO.getAnBuildingCode());
                element.setAnBuilding(curDeviceDO.getAnBuilding());
                element.setDeviceName(curDeviceDO.getAnDeviceName());
                if ("1".equals(param.getRoleType())) {
                    element.setEmpowerType("1"); //员工
                    element.setEmpowerAreaCode(param.getCreateEmployeeNo());
                    element.setEmpowerAreaName(param.getCreateEmployeeName());
                }
                String beginTime = element.getPowerBeginTime();
                if (StringUtils.isBlank(beginTime)) {
                    beginTime = createDate;
                }
                String endTime = element.getPowerEndTime();
                if (StringUtils.isBlank(endTime)) {
                    throw new BizException(ResponseEnum.FAIL.getCode(), "申请期限截止日期不能为空!");
                }
                element.setPowerBeginTime(formatTime(beginTime));
                element.setPowerEndTime(formatTime(endTime));
                if ("0".equals(adminApprovalStatus)) {
                    adminApprovalStatus = getApprovalStatus(element.getEmpowerType(), element.getEmpowerAreaCode());
                }
            }
            safetyBuildProcessDOBizMapper.batchInsertBuildOpen(openDOList);

            String factoryName = openDOList.stream().map(SafetyBuildProcessOpenDO::getAnFactoryName).distinct().collect(Collectors.joining(";"));
            String buildName = openDOList.stream().map(SafetyBuildProcessOpenDO::getAnBuilding).distinct().collect(Collectors.joining(";"));
            String deptName = openDOList.stream().map(SafetyBuildProcessOpenDO::getDockingDeptName).distinct().collect(Collectors.joining(";"));
            String deviceName = openDOList.stream().map(SafetyBuildProcessOpenDO::getDeviceName).distinct().collect(Collectors.joining(";"));
            String timeStr = openDOList.stream().map(e->e.getPowerBeginTime()+"-"+e.getPowerEndTime()).distinct().collect(Collectors.joining(";"));
            process.setMontageFactoryName(factoryName.getBytes());
            process.setMontageBuildName(buildName.getBytes());
            process.setMontageDept(deptName.getBytes());
            process.setMontageDeviceName(deviceName.getBytes());
            process.setMontageTime(timeStr.getBytes());
        }
        List<SafetyBuildProcessCloseDO> closeDOList = param.getCloseList();
        if(!CollectionUtils.isEmpty(closeDOList)){
            if(!powerType.toString().isEmpty()){
                powerType.append(",");
            }
            powerType.append("2");
            for (SafetyBuildProcessCloseDO element : closeDOList) {
                element.setProcessId(param.getId());
                if ("0".equals(adminApprovalStatus)) {
                    adminApprovalStatus = getApprovalStatus(element.getEmpowerType(), element.getEmpowerAreaCode());
                }
            }
            safetyBuildProcessDOBizMapper.batchInsertBuildClose(closeDOList);
        }
        //根据入参处理其他流程信息
        process = initSubmitParam(param,process);
        process.setPowerType(powerType.toString());
        process.setAdminApprovalStatus(adminApprovalStatus);
        process.setAdminApprovalEipRole("安保平台_门禁系统管理员");
        return safetyBuildProcessDOMapper.insert(process);
    }

    /**
     * 判断是否需要管理员审批
     * @param empowerType  授权方式 1-按人员 2-按组织
     * @param areaCode 授权范围
     * @return 0：默认审批，不需要管理员审批， 1： 管理员审批
     */
    private String getApprovalStatus(String empowerType, String areaCode) {
        // 为空或者是人员时
        if (StringUtils.isBlank(empowerType) || StringUtils.isBlank(areaCode) || Objects.equals("1", empowerType)) {
            return "0";
        }
        String[] split = areaCode.split(",");
        if (split.length <= 2) {
            return "1";
        } else {
            return "0";
        }
    }

    private String formatTime(String time) {
        return time.substring(0, 10) + " 00:00:00";
    }

    public void batchUpdateOpen(List<SafetyBuildProcessOpenDO> openDOList){
        safetyBuildProcessDOBizMapper.batchUpdateOpen(openDOList);
    }

    public void batchUpdateClose(List<SafetyBuildProcessCloseDO> closeDOList){
        safetyBuildProcessDOBizMapper.batchUpdateClose(closeDOList);
    }

    /**
     * 处理创建流程的参数
     * @param param 入参
     * @param processDO 接收对象
     * @return 返回 processDO
     */
    public SafetyBuildProcessDOWithBLOBs initSubmitParam(BuildProcessAddParam param,SafetyBuildProcessDOWithBLOBs processDO){
        processDO.setTopSecretFlag("0");
        processDO.setAcrossFlag("0"); //默认

        //设置流程的责任部门审批人以及责任部门
        processDO.setDepartEmployeeLogin(param.getCreateEmployeeLogin());
        processDO.setDepartEmployeeName(param.getCreateEmployeeName());
        processDO.setDutyDeptCode(param.getCreateDeptCode());
        processDO.setDutyDeptName(param.getCreateDeptName());
        List<SafetyBuildPowerDO> powerDOList = new ArrayList<>();
        //保密负责人提交时，获取此保密负责人的责任部门
        if("3".equals(param.getRoleType())) {
            BuildPowerListParam listParam = new BuildPowerListParam();
            listParam.setEmpNo(param.getCreateEmployeeNo());
            try{
                powerDOList = safetyBuildPowerService.selectList(listParam);
                if (!CollectionUtils.isEmpty(powerDOList)) {
                    processDO.setDutyDeptCode(powerDOList.get(0).getDutyDeptCode());
                    processDO.setDutyDeptName(powerDOList.get(0).getDutyDeptName());
                    processDO.setBuildSysManagerRole(powerDOList.get(0).getBuildSysManagerRole());
                }
            }catch (Exception e){
                LogUtils.error(ResponseEnum.FAIL.getCode(),"获取保密负责人的责任部门信息失败{}",e);
            }
            // 否涉及绝密和跨部门
            handleOpenDeptInfo(param, processDO, powerDOList);
        }
        //普通员工取所在责任部门逻辑
        if("1".equals(param.getRoleType()) || ("3".equals(param.getRoleType())&&CollectionUtils.isEmpty(powerDOList))){
            String createDeptCode = param.getCreateDeptCode();
            //获取责任部门以及保密负责人信息
            try {
                List<SafetyBuildPowerDO> buildPowerDOS = safetyBuildPowerService.selectListByDept(createDeptCode);
                if(!CollectionUtils.isEmpty(buildPowerDOS)){
                    String deptEmpLoginStr = buildPowerDOS.stream().map(SafetyBuildPowerDO::getEmployeeLogin).distinct().collect(Collectors.joining(","));
                    processDO.setDepartEmployeeLogin(deptEmpLoginStr);
                    processDO.setDutyDeptCode(buildPowerDOS.get(0).getDutyDeptCode());
                    processDO.setDutyDeptName(buildPowerDOS.get(0).getDutyDeptName());
                    processDO.setBuildSysManagerRole(buildPowerDOS.get(0).getBuildSysManagerRole());
                }else{
                    LogUtils.info(ResponseEnum.FAIL.getCode(),"未找到员工{}所属组织{}的责任部门",param.getCreateEmployeeName(),createDeptCode);
                }
                // 否涉及绝密和跨部门
                handleOpenDeptInfo(param, processDO, buildPowerDOS);
            }catch (Exception e){
                LogUtils.error(ResponseEnum.FAIL.getCode(),"获取员工的责任部门信息失败{}",e);
            }
        }
        return processDO;
    }

    /**
     * 判断是否涉及绝密和跨部门
     * @param param 入参
     * @param processDO 接收对象
     * @param powerDOList 创建人的责任部门信息
     */
    private void handleOpenDeptInfo(BuildProcessAddParam param, SafetyBuildProcessDOWithBLOBs processDO, List<SafetyBuildPowerDO> powerDOList) {
        List<SafetyBuildProcessOpenDO> openDOList = param.getOpenDOList();
        if (CollectionUtils.isEmpty(openDOList)) {
            return;
        }

        // 判断是否涉及绝密区域
        List<SafetyBuildProcessOpenDO> secOpenList = openDOList.stream()
                .filter(e -> StringUtils.isNotBlank(e.getTopSecretEmployeeLogin()))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(secOpenList)) {
            processDO.setTopSecretFlag("1");
            processDO.setTopSecretEmployeeLogin(secOpenList.stream()
                    .map(SafetyBuildProcessOpenDO::getTopSecretEmployeeLogin)
                    .distinct()
                    .collect(Collectors.joining(",")));
            processDO.setTopSecretEmployeeName(secOpenList.stream()
                    .map(SafetyBuildProcessOpenDO::getTopSecretEmployeeName)
                    .distinct()
                    .collect(Collectors.joining(",")));
        }

        // 判断是否跨部门
        List<String> duibiDeptCode;
        if("3".equals(param.getRoleType())){
            //保密负责人时，跟责任部门进行对比判断是否跨部门
            duibiDeptCode = powerDOList.stream().map(SafetyBuildPowerDO::getDutyDeptCode).distinct().collect(Collectors.toList());
        } else {
            //正常员工时，跟创建人所在部门对比判断是否跨部门
            duibiDeptCode = Collections.singletonList(param.getCreateDeptCode());
        }

        // 检查是否存在跨部门的情况
        List<SafetyBuildProcessOpenDO> arcossList = openDOList.stream()
                .filter(li -> duibiDeptCode.stream()
                        .noneMatch(deptCode -> deptCode.contains(li.getDockingDeptCode())))
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(arcossList)) {
            // 跨部门的数据

            processDO.setAcrossFlag("1");
            processDO.setAcrossEmployeeLogin(arcossList.stream()
                    .map(SafetyBuildProcessOpenDO::getAcrossEmployeeLogin)
                    .distinct()
                    .collect(Collectors.joining(",")));
            processDO.setAcrossEmployeeName(arcossList.stream()
                    .map(SafetyBuildProcessOpenDO::getAcrossEmployeeName)
                    .distinct()
                    .collect(Collectors.joining(",")));
            processDO.setAcrossBuildSysManagerRole(arcossList.stream()
                    .filter(e -> StringUtils.isNotBlank(e.getBuildSysManagerRole()))
                    .map(SafetyBuildProcessOpenDO::getBuildSysManagerRole)
                    .distinct()
                    .collect(Collectors.joining(",")));
        }
    }

    /**
     *
     * @param param-入参
     * @return 返回值
     */
    public int modifyFlowId(BuildProcessAddParam param){
        if(StringUtils.isBlank(param.getId())){
            return 0;
        }
        return safetyBuildProcessDOBizMapper.modifyFlowId(param);
    }

    /**
     * 参数整理
     * @param param 入参
     * @return example
     */
    private SafetyBuildProcessDOExample formatExample(BuildProcessListParam param){
        SafetyBuildProcessDOExample example = new SafetyBuildProcessDOExample();
        SafetyBuildProcessDOExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(param.getStatus())){
            criteria.andStatusEqualTo(param.getStatus());
        }
        if(!CollectionUtils.isEmpty(param.getIdList())){
            criteria.andIdIn(param.getIdList());
        }
        if(StringUtils.isNotBlank(param.getCreateTimeBegin())){
            criteria.andCreateTimeGreaterThan(param.getCreateTimeBegin());
        }
        if(StringUtils.isNotBlank(param.getCreateTimeEnd())){
            criteria.andCreateTimeLessThan(param.getCreateTimeEnd());
        }
        if(StringUtils.isNotBlank(param.getBpmFlowId())){
            criteria.andBpmFlowIdEqualTo(param.getBpmFlowId());
        }
        example.setOrderByClause(" create_time desc");
        if(param.getPageFlag() != null){
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
            if(StringUtils.isNotBlank(param.getOrderByClause())){
                example.setOrderByClause(param.getOrderByClause());
            }
        }
        return example;
    }

    public List<SafetyBuildProcessOpenDO> selectOpenList(BuildProcessOpenListParam listParam){
        listParam.setPageFlag(null);
        return safetyBuildProcessDOBizMapper.selectOpenByExampleBiz(listParam);
    }

    public List<SafetyBuildProcessOpenDO> selectOpenList(String processId){
        BuildProcessOpenListParam listParam = new BuildProcessOpenListParam();
        listParam.setProcessId(processId);
        listParam.setPageFlag(null);
        return safetyBuildProcessDOBizMapper.selectOpenByExampleBiz(listParam);
    }

    public long selectOpenCount(BuildProcessOpenListParam listParam){
        return safetyBuildProcessDOBizMapper.countOpenByExampleBiz(listParam);
    }

    public List<SafetyBuildProcessCloseDO> selectCloseList(String processId) {
        return safetyBuildProcessDOBizMapper.selectCloseList(processId);
    }

    public int updateOpenStatusByProcessId(String processId){
        return safetyBuildProcessDOBizMapper.updateOpenStatusByProcessId(processId);
    }

    public List<SafetyBuildProcessCloseDO> selectCloseListByProcessIds(List<String> processIds) {
        if(CollectionUtils.isEmpty(processIds)){
            return new ArrayList<>();
        }
        return safetyBuildProcessDOBizMapper.selectCloseListByProcessIds(processIds);
    }
}
