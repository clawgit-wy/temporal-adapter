package com.yutong.safety.core.handler.build;

import com.alibaba.excel.util.StringUtils;
import com.google.common.collect.Sets;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.utils.YxtRebotUtils;
import com.yutong.safety.common.vo.EipToDoVO;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetyGroupInfoDO;
import com.yutong.safety.core.db.entity.build.*;
import com.yutong.safety.core.db.entity.build.param.BuildConfirmAddParam;
import com.yutong.safety.core.db.entity.build.param.BuildEmployeePowerListParam;
import com.yutong.safety.core.db.entity.build.param.BuildLongConfirmListParam;
import com.yutong.safety.core.db.entity.build.param.BuildPowerListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildDepartConfirmVO;
import com.yutong.safety.core.db.entity.build.vo.BuildGroupDutyVO;
import com.yutong.safety.core.db.entity.dict.SafetySystemParamDO;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerInfoDO;
import com.yutong.safety.core.dto.ParamDTO;
import com.yutong.safety.core.dto.SafetyOutsiderPromiseDTO;
import com.yutong.safety.core.dto.build.SafetyBuildCancelAuthorizeDto;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.handler.employeeprivatecar.SafetyEmployeePrivateCarHandler;
import com.yutong.safety.core.handler.park.SafetyParkPowerProcessHandler;
import com.yutong.safety.core.service.*;
import com.yutong.safety.core.service.build.*;
import com.yutong.safety.core.service.dict.SafetySystemParamService;
import com.yutong.safety.core.service.eip.EipCommonService;
import com.yutong.safety.core.service.park.SafetyParkEmployeePowerInfoService;
import com.yutong.safety.core.service.park.SafetyParkPowerProcessService;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SafetyBuildDepartConfirmHandler {

    @Resource
    private SafetyBuildDepartConfirmService safetyBuildDepartConfirmService;

    @Resource
    private SafetyBuildProcessHandler safetyBuildProcessHandler;

    @Resource
    private SafetyBuildEmployeePowerService safetyBuildEmployeePowerService;

    @Resource
    private SafetyBuildPowerService safetyBuildPowerService;

    @Resource
    private SafetyBuildWhiteListService safetyBuildWhiteListService;

    @Resource
    private EipCommonService eipCommonService;

    @Resource
    private SafetyBuildDeviceService safetyBuildDeviceService;

    @Resource
    private SafetyBuildProcessOpenService safetyBuildProcessOpenService;

    @Resource
    private SafetyParkEmployeePowerInfoService safetyParkEmployeePowerInfoService;

    @Resource
    private SafetyOutsiderPromiseChangeService safetyOutsiderPromiseChangeService;

    @Resource
    private SafetyFieldManageService safetyFieldManageService;

    @Resource
    private SafetyParkPowerProcessService safetyParkPowerProcessService;
    @Resource
    private SafetyBuildDeviceCollectService safetyBuildDeviceCollectService;
    @Resource
    private SafetyParkPowerProcessHandler safetyParkPowerProcessHandler;
    @Resource
    private SafetyEmployeePrivateCarHandler safetyEmployeePrivateCarHandler;
    @Resource
    private SafetyBuildIssuedLogService safetyBuildIssuedLogService;
    @Resource
    private SafetySystemParamService safetySystemParamService;
    @Resource
    private SafetyGroupService safetyGroupService;
    @Autowired
    private SafetyBaseHandler safetyBaseHandler;
    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Resource
    private YxtRebotUtils yxtRebotUtils;
    @Resource
    private SafetyLogMessageService safetyLogMessageService;

    @Value("${an.eip.todo.backUrl}")
    private String anEipTodoUrl;
    //人员权限变更确认Form
    private static final String curFormId = "personChangeAuthConfirm";
    private static String corresFormId = "deptSecrecyPerAuthConfirm";
    //代办的发起人待定
    @Value("${an.eip.todo.from}")
    private String formEmpNo;

    //安保小助手机器人ID
    @Value("${iam.retention.robotId}")
    private String incomintRebotId;

    /**
     * 查询组织变更人员清单
     * @param listParam
     * @return
     */
    public Object selectDepartPageList(BuildLongConfirmListParam listParam,String token){
        listParam.setPageFlag(true);
        String workNo = TokenUtils.getWorkNo(token);
        //根据当前用户类型 设置检索条件 1-员工 只能看自己提交的；2-管理员查看所有 3-保密负责人 能看部门以及自己提交的
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if(!"2".equals(userType)){
            listParam.setCurEmpNo(workNo);
            if(userType.startsWith("3")){
                listParam.setFilterDutyEmpCode(workNo);
            }else{
                listParam.setFilterDutyEmpCode(null);
            }
        }else{
            listParam.setFilterDutyEmpCode(null);
        }
        listParam.setIsCheckBuild(true);
        Long total = safetyBuildDepartConfirmService.selectDepartCount(listParam);
        List<BuildDepartConfirmVO> list =safetyBuildDepartConfirmService.selectDepartList(listParam);
        if(CollectionUtils.isEmpty(list)){
            return new PageVO<>(total,list);
        }
        // 获取部门路径信息
        getOrgCodePath(list);

        disList(list);
        return new PageVO<>(total,list);
    }

    private void disList(List<BuildDepartConfirmVO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<String> employeeCodes = list.stream().map(BuildDepartConfirmVO::getEmployeeCode).distinct().collect(Collectors.toList());
        BuildEmployeePowerListParam listParam1 = new BuildEmployeePowerListParam();
        listParam1.setPageFlag(false);
        listParam1.setEmpCodeList(employeeCodes);
        //根据员工号获取员工所有已开通门禁权限
        List<SafetyBuildEmployeePowerDO> employeePowerDOS = safetyBuildEmployeePowerService.selectByExample(listParam1);
        if(CollectionUtils.isEmpty(employeeCodes)){
            return ;
        }
        Map<String,List<SafetyBuildEmployeePowerDO>> powerMap = employeePowerDOS.stream().collect(Collectors.groupingBy(SafetyBuildEmployeePowerDO::getEmployeeCode));
        //根据设备，获取各设备的责任部门
        List<String> deviceCodes = employeePowerDOS.stream().map(SafetyBuildEmployeePowerDO::getDeviceCode).distinct().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(deviceCodes)){
            deviceCodes.add("-1");
        }
        list.forEach(e->{
            List<SafetyBuildEmployeePowerDO> curEmpPowerList = powerMap.get(e.getEmployeeCode());
            e.setAllPowerList(curEmpPowerList);
            if(!CollectionUtils.isEmpty(curEmpPowerList)){
                //设置员工已开通门禁列表
                String allPowerStr = curEmpPowerList.stream().map(SafetyBuildEmployeePowerDO::getDeviceName).distinct().collect(Collectors.joining(","));
                e.setAllPowerStr(allPowerStr);

                Date changeTime = DateUtils.parse(e.getCreateTime(),2);
                List<SafetyBuildEmployeePowerDO> newPowerList = curEmpPowerList.stream()
                        .filter(power -> Objects.isNull(power.getDepartChangeTime()) || power.getDepartChangeTime().getTime() > changeTime.getTime())
                        .collect(Collectors.toList());
                e.setLocalPowerList(newPowerList);
                if(!CollectionUtils.isEmpty(newPowerList)){
                    String newPowerStr = newPowerList.stream().map(SafetyBuildEmployeePowerDO::getDeviceName).distinct().collect(Collectors.joining(","));
                    e.setLocalPowerStr(newPowerStr);
                }
            }
        });
    }

    /**
     * 初始话部门信息
     */
    private void getOrgCodePath(List<BuildDepartConfirmVO> list) {
        // 提取所有旧部门代码和新部门代码，去重处理
        Set<String> deptCodes = list.stream()
                .flatMap(li -> Stream.of(
                        li.getOldDeptCode(),
                        li.getNewDeptCode()))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(deptCodes)) {
            return;
        }
        // 查询部门信息
        List<SafetyGroupInfoDO> listByCode = safetyGroupService.getListByCode(new ArrayList<>(deptCodes));
        if (CollectionUtils.isEmpty(listByCode)) {
            return;
        }
        // 建立部门代码与信息的映射关系
        Map<String, SafetyGroupInfoDO> deptMap = listByCode.stream().collect(Collectors.toMap(SafetyGroupInfoDO::getCode, t -> t));
        // 为每个对象设置组织名称路径
        list.forEach(li -> {
            // 处理旧部门代码
            SafetyGroupInfoDO oldDeptInfo = deptMap.get(li.getOldDeptCode());
            if (oldDeptInfo != null) {
                li.setOldOrgNamePath(oldDeptInfo.getOrgNamePath());
            }
            // 处理新部门代码
            SafetyGroupInfoDO newDeptInfo = deptMap.get(li.getNewDeptCode());
            if (newDeptInfo != null) {
                li.setNewOrgNamePath(newDeptInfo.getOrgNamePath());
            }
        });
    }

    /**
     * 待确认门禁清单
     * @param listParam
     * @return
     */
    public Object selectPageList(BuildLongConfirmListParam listParam,String token){
        Map<String,String> tokenUser = TokenUtils.getTockenUser(token);
        //根据当前用户类型 设置检索条件 1-员工 只能看自己提交的；2-管理员查看所有 3-保密负责人 能看部门以及自己提交的
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if(!"2".equals(userType)){
            listParam.setCurEmpNo(tokenUser.get("workNo"));
        }
        List<SafetyBuildDepartConfirmDO> list = safetyBuildDepartConfirmService.selectList(listParam);
        Long total = safetyBuildDepartConfirmService.selectCount(listParam);
        if (CollectionUtils.isEmpty(list)) {
            return new PageVO<>(total,list);
        }
        // 获取对应员工工号
        String collect = list.stream().map(SafetyBuildDepartConfirmDO::getCorresEmpCode).filter(StringUtils::isNotBlank).map(li -> li.replace(";", ",")).collect(Collectors.joining(","));
        if (StringUtils.isBlank(collect)) {
            return new PageVO<>(total,list);
        }
        // 获取对应员工信息
        List<String> empCodes = Arrays.asList(collect.split(","));
        List<SafetyEmployeeMessageDO> employeeList = safetyEmployeeMessageService.getListByStaffNo(empCodes);
        if (CollectionUtils.isEmpty(employeeList)) {
            return new PageVO<>(total,list);
        }
        // 设置员工姓名
        Map<String, String> dataMap = employeeList.stream().collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, SafetyEmployeeMessageDO::getEmpName));
        list.forEach(li -> {
            String corresEmpCode = li.getCorresEmpCode();
            if (StringUtils.isNotBlank(corresEmpCode)) {
                String empName = Arrays.stream(corresEmpCode.split(";")).map(dataMap::get).filter(ln -> StringUtils.isNotBlank(ln) && !("null").equals(ln)).collect(Collectors.joining(";"));
                li.setCorresEmpName(empName);
            }
        });
        return new PageVO<>(total,list);
    }

    /**
     * 新部门负责人确认员工历史门禁权限（批量确认）
     *
     * @param dealType 处理类型 1-保留 2-关闭
     * @param confirmList 确认明细
     * @param token 秘钥
     * @return 返回值
     */
    public Object batchDealBuild(String dealType, List<BuildConfirmAddParam> confirmList,String token){
        List<BuildDepartConfirmVO> todoList = new ArrayList<>();
        try {
            //根据当前用户类型 设置检索条件 1-员工 只能看自己提交的；2-管理员查看所有 3-保密负责人 能看部门以及自己提交的
            String userType = safetyBuildProcessHandler.checkUserType(token);
            if ("1".equals(userType)) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "无权限处理");
            }
            Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
            List<Integer> changeId = confirmList.stream().map(e -> Integer.parseInt(e.getChangeId())).collect(Collectors.toList());
            //查询对应的组织变更信息
            BuildLongConfirmListParam listParam1 = new BuildLongConfirmListParam();
            listParam1.setPageFlag(false);
            listParam1.setIdList(changeId);
            listParam1.setIsCheckBuild(true);
            List<BuildDepartConfirmVO> departList = safetyBuildDepartConfirmService.selectDepartList(listParam1);
            if (CollectionUtils.isEmpty(departList)) {
                LogUtils.info("未找到要处理的门禁信息，入参=" + changeId.toArray());
                return null;
            }
            todoList.addAll(departList);
            if ("1".equals(dealType)) {
                departList = disListInfo(departList);
            }
            List<String> empCodeList = departList.stream().map(BuildDepartConfirmVO::getEmployeeCode).collect(Collectors.toList());
            String localIdStr = confirmList.stream().map(e -> e.getLocalPowerIdStr() == null ? "" : e.getLocalPowerIdStr()).collect(Collectors.joining(","));
            List<String> localIdList = Arrays.asList(localIdStr.split(","));
            BuildEmployeePowerListParam empPowerParam = new BuildEmployeePowerListParam();
            empPowerParam.setPageFlag(false);
            empPowerParam.setNotIdList(localIdList);
            empPowerParam.setEmpCodeList(empCodeList);
            //查询批量处理时的门禁授权列表
            List<SafetyBuildEmployeePowerDO> list = safetyBuildEmployeePowerService.selectByExample(empPowerParam);
            if (CollectionUtils.isEmpty(list)) {
                LogUtils.info("未找到要处理的门禁信息，入参=" + empCodeList.toArray());
                return null;
            }
            privateDealBuild(dealType, userType, departList, tokenUser, list, changeId, "组织变更取消权限");
            try {
                //操作日志记录
                LogMessageAddParam log = new LogMessageAddParam();
                log.setOperatePage("组织变更权限确认");
                log.setAbility("批量确认门禁");
                String dealt = "保留";
                if ("2".equals(dealType)) {
                    dealt = "关闭";
                }
                log.setMessage(dealt + empCodeList.stream().collect(Collectors.joining(",")));
                log.setOperator(tokenUser.get("realName"));
                safetyLogMessageService.addLogMessage(log);
            } catch (Exception e) {
                LogUtils.error("保存日志异常", e);
            }
        } catch (Exception e) {
            LogUtils.error("一键处理门禁授权失败，操作人=" + TokenUtils.getWorkNo(token));
        } finally {
            // 消代办
            doneDepartChangeEip(todoList);
        }
        return null;
    }

    /**
     * 新部门负责人确认员工历史门禁权限（单个确认）
     * @param param 入参
     * @param token 秘钥
     * @return 返回值
     */
    public Object dealBuild(BuildConfirmAddParam param,String token){
        List<BuildDepartConfirmVO> todoList = new ArrayList<>();
        try {
            //根据当前用户类型 设置检索条件 1-员工 只能看自己提交的；2-管理员查看所有 3-保密负责人 能看部门以及自己提交的
            String userType = safetyBuildProcessHandler.checkUserType(token);
            if ("1".equals(userType)) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "无权限处理");
            }
            Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
            Set<String> idSet = Sets.newHashSet(param.getChangeId().split(","));
            List<Integer> changeId = idSet.stream().map(Integer::parseInt).collect(Collectors.toList());
            //查询对应的组织变更信息
            BuildLongConfirmListParam listParam1 = new BuildLongConfirmListParam();
            listParam1.setPageFlag(false);
            listParam1.setIdList(changeId);
            listParam1.setIsCheckBuild(true);
            List<BuildDepartConfirmVO> departList = safetyBuildDepartConfirmService.selectDepartList(listParam1);
            if (CollectionUtils.isEmpty(departList)) {
                LogUtils.info("未找到要处理的门禁信息，入参=" + changeId.toArray());
                return null;
            }
            todoList.addAll(departList);
            if (StringUtils.isNotBlank(param.getKeepIdStr())) {
                departList = disListInfo(departList);
            }
            List<String> empCodeList = departList.stream().map(BuildDepartConfirmVO::getEmployeeCode).collect(Collectors.toList());
            BuildEmployeePowerListParam empPowerParam = new BuildEmployeePowerListParam();
            empPowerParam.setPageFlag(false);
            empPowerParam.setEmpCodeList(empCodeList);
            if (StringUtils.isNotBlank(param.getKeepIdStr())) {
                empPowerParam.setIdList(Arrays.asList(param.getKeepIdStr().split(",")));
                //查询批量处理时的门禁授权列表
                List<SafetyBuildEmployeePowerDO> list = safetyBuildEmployeePowerService.selectByExample(empPowerParam);
                privateDealBuild("1", userType, departList, tokenUser, list, changeId, "");
            }
            if (StringUtils.isNotBlank(param.getCloseIdStr())) {
                empPowerParam.setIdList(Arrays.asList(param.getCloseIdStr().split(",")));
                //查询批量处理时的门禁授权列表
                List<SafetyBuildEmployeePowerDO> list = safetyBuildEmployeePowerService.selectByExample(empPowerParam);
                privateDealBuild("2", userType, departList, tokenUser, list, changeId, "组织变更取消权限");
            }
            try {
                //操作日志记录
                LogMessageAddParam log = new LogMessageAddParam();
                log.setOperatePage("组织变更权限确认");
                log.setAbility("单个确认门禁");
                String dealt = "确认";
                log.setMessage(dealt + empCodeList.stream().collect(Collectors.joining(",")));
                log.setOperator(tokenUser.get("realName"));
                safetyLogMessageService.addLogMessage(log);
            } catch (Exception e) {
                LogUtils.error("保存日志异常", e);
            }
        } catch (Exception e) {
            LogUtils.error("单个处理门禁授权失败，操作人=" + TokenUtils.getWorkNo(token));
        } finally {
            // 消代办
            doneDepartChangeEip(todoList);
        }
        return null;
    }

    /**
     * 新旧责任人一致的情况下，并且保留时，直接保留，不进行处理
     * @param departList 数据集
     * @return 返回不相等的数据
     */
    public List<BuildDepartConfirmVO> disListInfo(List<BuildDepartConfirmVO> departList) {
        if (CollectionUtils.isEmpty(departList)) {
            return departList;
        }
        Set<Integer> ids = new HashSet<>();
        List<BuildDepartConfirmVO> list = new ArrayList<>();
        departList.forEach(li -> {
            String dutyEmpCode = li.getDutyEmpCode();
            String oldDutyEmpCode = li.getOldDutyEmpCode();
            if (StringUtils.isNotBlank(dutyEmpCode) && StringUtils.isNotBlank(oldDutyEmpCode) && oldDutyEmpCode.equals(dutyEmpCode)) {
                ids.add(li.getId());
            } else {
                list.add(li);
            }
        });
        if (!CollectionUtils.isEmpty(ids)) {
            safetyBuildDepartConfirmService.batchUpdate(ids);
        }
        return list;
    }

    /**
     * 新部门负责人确认员工历史门禁权限
     * @param dealType 处理类型 1-开通 2-关闭
     * @param userType 角色类型
     * @param departList 部门确认对象
     * @param tokenUser
     * @param list
     */
    private void privateDealBuild(String dealType,String userType,List<BuildDepartConfirmVO> departList,
                                  Map<String,String> tokenUser,List<SafetyBuildEmployeePowerDO> list,
                                  List<Integer> changeId,String reason){
        Map<String,BuildDepartConfirmVO> departMap = departList.stream()
                .collect(Collectors.toMap(BuildDepartConfirmVO::getEmployeeCode, e -> e));
        List<SafetyBuildDepartConfirmDO> departConfirmDOS = new ArrayList<>();
        String workNo = tokenUser.get("workNo");
        String realName = tokenUser.get("realName");
        //251210增加逻辑 处理已存在确认信息的数据
        //1.查询已存在的确认信息
        Map<String,List<SafetyBuildDepartConfirmDO>> confirmMap = new HashMap<>();
        try{
            List<String> empCodeList = departList.stream().map(BuildDepartConfirmVO::getEmployeeCode).distinct().collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(empCodeList)){
                BuildLongConfirmListParam listParam = new BuildLongConfirmListParam();
                listParam.setEmployeeCodeList(empCodeList);
                listParam.setConfirmFlag("1"); //查询已确认的数据
                List<SafetyBuildDepartConfirmDO> confirmList = safetyBuildDepartConfirmService.selectList(listParam);
                if(!CollectionUtils.isEmpty(confirmList)){
                    safetyBuildDepartConfirmService.batchInsertHistory(confirmList);
                }
                confirmMap = confirmList.stream().collect(Collectors.groupingBy(SafetyBuildDepartConfirmDO::getEmployeeCode));

                listParam.setConfirmFlag("0"); //查询未确认的数据
                safetyBuildDepartConfirmService.deleteConfirmByExample(listParam);
            }
        }catch (Exception e){
            LogUtils.error("处理历史确认信息异常",e);
        }

        // 获取责任部门
        List<SafetyBuildPowerDO> buildPowerDOList = safetyBuildPowerService.getListByDeviceCode(new ArrayList<>());
        Map<String,List<SafetyBuildPowerDO>> buildPowerMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(buildPowerDOList)){
            buildPowerMap = buildPowerDOList.stream().filter(li -> StringUtils.isNotBlank(li.getDutyDeptCode())).collect(Collectors.groupingBy(SafetyBuildPowerDO::getDutyDeptCode));
        }

        int size = list.size();
        int pageNum = 1000;
        int totalPage = size/pageNum+(size%pageNum>0?1:0);

        List<SafetyBuildDepartConfirmDO> todoConfirmDOS = new ArrayList<>();
        String roleType = userType.split(":")[0];
        LogUtils.info("roleType={}",roleType);
        for(int i=0;i<totalPage;i++){
            int curMax = Math.min((i+1)*pageNum,size);
            List<SafetyBuildEmployeePowerDO> subList = list.subList(i*pageNum,curMax);

            List<SafetyBuildIssuedLogDO> issuedLogDOS = new ArrayList<>();
            Map<String, List<SafetyBuildPowerDO>> finalBuildPowerMap = buildPowerMap;
            Map<String, List<SafetyBuildDepartConfirmDO>> finalConfirmMap = confirmMap;
            subList.forEach(e->{
                BuildDepartConfirmVO curDepratVo = departMap.get(e.getEmployeeCode());
                if(curDepratVo == null){
                    return;
                }
                /* 251210 - 2.判断已确认的信息是否未当前变更的数据 */
                List<SafetyBuildDepartConfirmDO> curConfirmList = finalConfirmMap.get(e.getEmployeeCode());
                checkEmpConfirmData(curConfirmList,curDepratVo);
                /*组织变更时间之后变更的门禁权限不作处理*/
                e.setDeleteFlag("0");
                if (StringUtils.isBlank(curDepratVo.getCreateTime())) {
                    return;
                }
                Date changeTime = DateUtils.parse(curDepratVo.getCreateTime(),2);
                if(Objects.isNull(e.getDepartChangeTime()) || e.getDepartChangeTime().getTime()>changeTime.getTime()){
                    return;
                }
                e.setRoleType(roleType);
                e.setDutyDeptCode(curDepratVo.getDutyDeptCode());
                e.setDutyDeptName(curDepratVo.getDutyDeptName());
                if("2".equals(dealType)){
                    e.setDutyEmpNo(curDepratVo.getDutyEmpCode());
                    e.setDutyEmpName(curDepratVo.getDutyEmpName());
                }else{
                    e.setDutyEmpNo(workNo);
                    e.setDutyEmpName(realName);
                }
                SafetyBuildDepartConfirmDO curConfirmDO = new SafetyBuildDepartConfirmDO();
                BeanUtils.copyProperties(curDepratVo,curConfirmDO);
                //关闭的权限不需要再次确认
                if("2".equals(dealType)){
                    e.setDeleteFlag("1"); //权限标记删除

                    SafetyBuildIssuedLogDO logDO = new SafetyBuildIssuedLogDO();
                    BeanUtils.copyProperties(e,logDO);
                    logDO.setRoleType(roleType);
                    logDO.setCreateBy(realName);
                    logDO.setRemark(reason);
                    logDO.setIssuedType("关闭");
                    logDO.setSourceType("3"); //长期未进出
                    issuedLogDOS.add(logDO);

                    curConfirmDO.setConfirmFlag("2"); //关闭
                }else{
                    curConfirmDO.setConfirmFlag("1"); //保留
                }
                curConfirmDO.setDutyDeptCode(e.getDutyDeptCode());
                curConfirmDO.setDutyDeptName(e.getDutyDeptName());
                curConfirmDO.setDutyEmpCode(e.getDutyEmpNo());
                curConfirmDO.setDutyEmpName(e.getDutyEmpName());
                curConfirmDO.setDeviceCode(e.getDeviceCode());
                curConfirmDO.setDeviceName(e.getDeviceName());
                curConfirmDO.setConfirmFlag(dealType);
                curConfirmDO.setConfirmEmpCode(workNo);
                curConfirmDO.setConfirmEmpName(realName);
                curConfirmDO.setCreateTime(curDepratVo.getCreateTime()); //组织变更时间
                curConfirmDO.setCorresFlag("1");  //0-否  1-是

                // 判断是否跨部门
                SafetyBuildProcessOpenDO info = safetyBuildProcessOpenService.selectProcessDeptInfo(curConfirmDO.getDeviceCode(), curConfirmDO.getEmployeeCode());
                if (info != null ) {
                    String dockingDeptCode = info.getDockingDeptCode();
                    // 保留权限时 并且对接部门不为空和对应发起部门不一致时 为跨部门
                    if ("1".equals(dealType) && StringUtils.isNotBlank(dockingDeptCode) &&
                            StringUtils.isNotBlank(curDepratVo.getDutyDeptCode()) && !curDepratVo.getDutyDeptCode().equals(dockingDeptCode)) {
                        curConfirmDO.setCorresDeptCode(dockingDeptCode);
                        curConfirmDO.setCorresDeptName(info.getDockingDeptName());
                        curConfirmDO.setCorresFlag("0"); //跨部门
                        curConfirmDO.setConfirmFlag("0"); //待确认
                        List<SafetyBuildPowerDO> curPowerList = finalBuildPowerMap.get(dockingDeptCode);
                        if (!CollectionUtils.isEmpty(curPowerList)) {
                            String corresEmpCode = curPowerList.stream().map(SafetyBuildPowerDO::getEmployeeCode).distinct().collect(Collectors.joining(";"));
                            curConfirmDO.setCorresEmpCode(corresEmpCode);
                        }
                        //需触发代办的信息集合
                        todoConfirmDOS.add(curConfirmDO);
                    }
                }
                departConfirmDOS.add(curConfirmDO);
            });
            // 公共楼宇关闭
            safetyBaseHandler.getCommonEmpNoBuildInfo(issuedLogDOS, reason);
            //批量保存
            if(!CollectionUtils.isEmpty(departConfirmDOS)){
                safetyBuildDepartConfirmService.batchInsert(departConfirmDOS);
            }
            /*关闭权限*/
            if(!CollectionUtils.isEmpty(issuedLogDOS)){
                safetyBuildProcessHandler.issuedPowerByListNew(dealType,issuedLogDOS);
            }
            //更新授权记录
            if(!CollectionUtils.isEmpty(subList)){
                safetyBuildEmployeePowerService.insertBatch(subList);
            }
        }

        //批量更新组织变更状态
        SafetyBuildDepartChangeDO changeDO = new SafetyBuildDepartChangeDO();
        changeDO.setBuildConfirmFlag("1");
        safetyBuildDepartConfirmService.updateDepartChangeByIdList(changeId,changeDO);

        //给对接部门的保密负责人发代办
        SafetySystemParamDO systemParamDO = safetySystemParamService.getByParamCode("BUILD_DEPART_TODO");
        if(systemParamDO != null && "1".equals(systemParamDO.getParamValue())){
            if(!CollectionUtils.isEmpty(todoConfirmDOS)){
                try{
                    sendDepartCorresToDo(todoConfirmDOS);
                }catch (Exception el){
                    LogUtils.info("{\"code\":\"9999\",\"msg\":\"对接部门负责人发送代办异常！\"}");
                    LogUtils.error("对接部门负责人发送代办异常",el);
                }
            }
        }

    }

    /**
     * 处理完毕后取消EIP代办
     * @param departList
     */
    public void doneDepartChangeEipTodo(List<BuildDepartConfirmVO> departList){
        //取消相关人员的代办
        String empStr = departList.stream().filter(e->StringUtils.isNotBlank(e.getDutyEmpCode()))
                .map(BuildDepartConfirmVO::getDutyEmpCode).collect(Collectors.joining(","));
        if(StringUtils.isBlank(empStr) ||"null".equals(empStr)){
            return;
        }
        Set<String> empSet = Sets.newHashSet(empStr.split(","));
        //查询该责任人有其他代办时，不对其进行删除
        BuildLongConfirmListParam confirmListParam = new BuildLongConfirmListParam();
        //取消待办的判断中，也过滤掉没有门禁权限的员工数据
        confirmListParam.setIsCheckBuild(true);
        List<BuildDepartConfirmVO> undoList = safetyBuildDepartConfirmService.selectDepartList(confirmListParam);
        if(!CollectionUtils.isEmpty(undoList)){
            String undoEmpStr = undoList.stream().filter(e->StringUtils.isNotBlank(e.getDutyEmpCode()))
                    .map(BuildDepartConfirmVO::getDutyEmpCode).collect(Collectors.joining(","));
            if(StringUtils.isNotBlank(undoEmpStr) && !"null".equals(undoEmpStr)){
                Set<String> undoEmpSet = Sets.newHashSet(undoEmpStr.split(","));
                empSet.removeAll(undoEmpSet);
            }
        }
        for(String empCode:empSet){
            if(StringUtils.isBlank(empCode)){
                continue;
            }
            //发送代办 modeId = buildDepartConfirm + 员工号
            String modelId = "SAFETY-" + curFormId + "-" + empCode;
            try{
                EipToDoVO eipToDoVO = new EipToDoVO(modelId,"","","","","","","","");
                eipCommonService.setEipToDoDone(eipToDoVO);
            }catch (Exception e){
                LogUtils.error("【消除代办异常】"+",from "+formEmpNo+" to "+empCode+":",e);
            }
        }
    }


    /**
     * 处理完毕后取消EIP代办
     * @param departList
     */
    public void doneDepartChangeEip(List<BuildDepartConfirmVO> departList) {
        Set<String> empSet = getEmpCodes(departList);
        LogUtils.info("【取消代办的相关人员】{}", empSet);

        // 获取仍有待办任务的员工编号
        BuildLongConfirmListParam confirmListParam = new BuildLongConfirmListParam();
        confirmListParam.setIsCheckBuild(true);
        List<BuildDepartConfirmVO> undoList = safetyBuildDepartConfirmService.selectDepartList(confirmListParam);
        if (!CollectionUtils.isEmpty(undoList)) {
            Set<String> undoEmpSet = getEmpCodes(undoList);
            empSet.removeAll(undoEmpSet);
        }
        if (CollectionUtils.isEmpty(empSet)) {
            return;
        }
        // 处理待办事项
        for (String empCode : empSet) {
            if (StringUtils.isBlank(empCode)) {
                continue;
            }
            String modelId = "SAFETY-" + curFormId + "-" + empCode;
            eipCommonService.eipToDoDone(modelId, empCode);
        }
    }

    private Set<String> getEmpCodes(List<BuildDepartConfirmVO> departList) {
        String empStr = departList.stream()
                .map(li ->
                        StringUtils.isNotBlank(li.getDutyEmpCode()) ? li.getDutyEmpCode()
                                : StringUtils.isNotBlank(li.getOldDutyEmpCode()) ? li.getOldDutyEmpCode()
                                : ""
                )
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(","));
        empStr = empStr.replace(",", ";");
        return Sets.newHashSet(empStr.split(";"));
    }

    /**
     * 批量处理白名单
     * @param dealType 处理类型 1-保留 2-删除
     * @param changeIdStr 变更记录ID
     * @param reason 变更原因
     * @param token 登录许可
     * @return 返回状态
     */
    public Object batchDealWhite(String dealType,String changeIdStr,String reason,String token){
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if("1".equals(userType)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"无权限处理");
        }
        Map<String,String> tokenUser = TokenUtils.getTockenUser(token);

        String dealDesc = "1".equals(dealType)?"保留":"取消";
        if(StringUtils.isBlank(reason)){
            reason = "部门变更"+dealDesc+"白名单";
        }
        Set<String> idSet = Sets.newHashSet(changeIdStr.split(","));changeIdStr.split(",");
        List<Integer> changeId = idSet.stream().map(Integer::parseInt).collect(Collectors.toList());
        //查询对应的组织变更信息
        BuildLongConfirmListParam listParam1 = new BuildLongConfirmListParam();
        listParam1.setPageFlag(false);
        listParam1.setIdList(changeId);
        List<BuildDepartConfirmVO> departList =safetyBuildDepartConfirmService.selectDepartList(listParam1);
        if(CollectionUtils.isEmpty(departList)){
            LogUtils.info("未找到要处理的门禁信息，入参="+changeId.toArray());
            return null;
        }
        batchModifyWhite(dealType,changeId,tokenUser,reason,userType,departList);
        //把EIP代办设置为已办
        doneDepartChangeEipTodo(departList);
        try{
            String empStr = departList.stream().map(BuildDepartConfirmVO::getEmployeeCode).distinct().collect(Collectors.joining(","));
            //操作日志记录
            LogMessageAddParam log = new LogMessageAddParam();
            log.setOperatePage("组织变更权限确认");
            log.setAbility("批量确认白名单");
            String dealt = "保留";
            if("2".equals(dealType)){
                dealt = "关闭";
            }
            log.setMessage(dealt+empStr);
            log.setOperator(tokenUser.get("realName"));
            safetyLogMessageService.addLogMessage(log);
        } catch (Exception e) {
            LogUtils.error("保存日志异常",e);
        }
        return null;
    }

    /**
     * 批量处理人员白名单
     * @param dealType
     * @param changeId
     * @param tokenUser
     * @param reason
     * @param userType
     * @param departList
     */
    private void batchModifyWhite(String dealType,List<Integer> changeId,
                                  Map<String,String> tokenUser,String reason,
                                  String userType,
                                  List<BuildDepartConfirmVO> departList){
        // 批量标记部门白名单状态
        SafetyBuildDepartChangeDO changeDO = new SafetyBuildDepartChangeDO();
        changeDO.setWhiteConfirmFlag("1");
        safetyBuildDepartConfirmService.updateDepartChangeByIdList(changeId,changeDO);

        SafetyBuildWhiteListDO buildWhiteListDO = new SafetyBuildWhiteListDO();
        if("2".equals(dealType)){
            buildWhiteListDO.setIsDelete("1");
        }
        buildWhiteListDO.setUpdateTime(DateUtils.format(new Date(),2));

        //批量更新白名单数据
        String s = userType.split(":")[0];
        String role = s;
        if ("1".equals(s)) {
            role = "普通员工";
        } else if ("2".equals(s)) {
            role = "门禁系统管理员";
        } else if ("3".equals(s)){
            role = "门禁管理员";
        }
        buildWhiteListDO.setCreateReason(reason);
        buildWhiteListDO.setCreateUserCode(tokenUser.get("workNo"));
        buildWhiteListDO.setCreateUserName(tokenUser.get("realName"));
        buildWhiteListDO.setCreateUserRole(role);

        List<String> empCodeList = departList.stream().map(BuildDepartConfirmVO::getEmployeeCode).collect(Collectors.toList());

        //修改白名单记录
        safetyBuildWhiteListService.updateByExample(empCodeList,buildWhiteListDO);
    }

    /**
     * 跨部门权限批量确认
     * @param dealType 1-保留 2-关闭
     * @param idStr 变更ID拼接
     * @param token 秘钥
     * @return 执行状态
     */
    public Object batchCrocessConfirm(String dealType,String idStr,String token){
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if("1".equals(userType)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"无权限处理");
        }
        Map<String,String> tokenUser = TokenUtils.getTockenUser(token);
        Set<String> idSet = Sets.newHashSet(idStr.split(","));
        List<Integer> idList = idSet.stream().map(Integer::parseInt).collect(Collectors.toList());
        BuildLongConfirmListParam listParam = new BuildLongConfirmListParam();
        listParam.setIdList(idList);
        listParam.setPageFlag(false);
        List<SafetyBuildDepartConfirmDO> confirmDOS = safetyBuildDepartConfirmService.selectList(listParam);
        if(CollectionUtils.isEmpty(confirmDOS)){
            throw new BizException(ResponseEnum.FAIL.getCode(),"未找到待确认的数据");
        }
        List<String> deviceCodes = confirmDOS.stream().map(SafetyBuildDepartConfirmDO::getDeviceCode)
                .distinct().collect(Collectors.toList());
        List<SafetyBuildDeviceDO> deviceList = safetyBuildDeviceService.selectList(deviceCodes);
        Map<String,SafetyBuildDeviceDO> deviceMap;
        if(!CollectionUtils.isEmpty(deviceList)){
            deviceMap = deviceList.stream().collect(Collectors.toMap(SafetyBuildDeviceDO::getDeviceCode, e -> e, (existing, replacement) -> existing));
        } else {
            deviceMap = new HashMap<>();
        }
        SafetyBuildDepartConfirmDO curConfirmDO = new SafetyBuildDepartConfirmDO();
        List<SafetyBuildIssuedLogDO> issuedLogDOS = new ArrayList<>();
        String endTime = DateUtils.format(DateUtils.addDays(new Date(),-1),2);
        if("2".equals(dealType)){
            confirmDOS.forEach(e->{
                SafetyBuildIssuedLogDO logDO = new SafetyBuildIssuedLogDO();
                String deviceCode = e.getDeviceCode();
                SafetyBuildDeviceDO curDev = deviceMap.get(deviceCode);
                if(curDev != null && "2".equals(curDev.getDeviceType())){
                    logDO.setDeviceType("2");
                }else{
                    logDO.setDeviceType("1");
                }
                BeanUtils.copyProperties(e,logDO);
                logDO.setBeginTime(endTime);
                logDO.setEndTime(endTime);
                logDO.setRoleType(userType.split(":")[0]);
                logDO.setCreateBy(tokenUser.get("realName"));
                logDO.setRemark("组织变更取消权限");
                logDO.setIssuedType("关闭");
                logDO.setSourceType("3"); //长期未进出
                issuedLogDOS.add(logDO);
            });
            curConfirmDO.setConfirmFlag("2"); //确认关闭
        }else{
            curConfirmDO.setConfirmFlag("1"); //确认保留
        }
        curConfirmDO.setConfirmRoleType(userType.split(":")[0]);
        /*关闭权限*/
        if(!CollectionUtils.isEmpty(issuedLogDOS)){
            safetyBuildProcessHandler.issuedPowerByListNew("2",issuedLogDOS);
        }
        curConfirmDO.setConfirmEmpCode(tokenUser.get("workNo"));
        curConfirmDO.setConfirmEmpName(tokenUser.get("realName"));
        //更新状态
        safetyBuildDepartConfirmService.updateByExample(listParam,curConfirmDO);
        //取消代办
        try {
            cancelCorresTODO(confirmDOS);
        }catch (Exception e){
            LogUtils.error("取消原对接部门的代办异常",e);
        }

        try{
            String empStr = confirmDOS.stream().map(SafetyBuildDepartConfirmDO::getEmployeeCode).distinct().collect(Collectors.joining(","));
            //操作日志记录
            LogMessageAddParam log = new LogMessageAddParam();
            log.setOperatePage("组织变更权限跨部门确认");
            log.setAbility("确认门禁权限");
            String dealt = "保留";
            if("2".equals(dealType)){
                dealt = "关闭";
            }
            String message = dealt+empStr+"；涉及门禁："+deviceCodes.stream().collect(Collectors.joining(","));
            if(message.length()>1500){
                message = message.substring(0,1500)+"...";
            }
            log.setMessage(message);
            log.setOperator(tokenUser.get("realName"));
            safetyLogMessageService.addLogMessage(log);
        } catch (Exception e) {
            LogUtils.error("保存日志异常",e);
        }
        return true;
    }

    /**
     * 取消对接部门的代办
     * @param confirmDOS 确认的清单
     */
    public void cancelCorresTODO(List<SafetyBuildDepartConfirmDO> confirmDOS){
        if(CollectionUtils.isEmpty(confirmDOS)){
            return;
        }
        String empNos = confirmDOS.stream().map(SafetyBuildDepartConfirmDO::getCorresEmpCode).distinct().collect(Collectors.joining(";"));
        if(StringUtils.isBlank(empNos)){
            return;
        }
        Set<String> set = Arrays.stream(empNos.split(";")).collect(Collectors.toSet());
        if(set.isEmpty()){
            return;
        }
        BuildLongConfirmListParam listParam = new BuildLongConfirmListParam();
        List<String> empCodeList = set.stream().collect(Collectors.toList());
        listParam.setEmployeeCodeList(empCodeList);
        List<SafetyBuildDepartConfirmDO> list = safetyBuildDepartConfirmService.selectUnConfirmList(listParam);
        //还有数据未处理的不取消代办
        if(!CollectionUtils.isEmpty(list) && !set.isEmpty()){
           for(String empNo:set){
               if(StringUtils.isBlank(empNo.trim())){
                   continue;
               }
               List<SafetyBuildDepartConfirmDO> subList=list.stream().filter(e->e.getCorresEmpCode().contains(empNo)).collect(Collectors.toList());
               if(!CollectionUtils.isEmpty(subList)){
                   set.remove(empNo);
               }
           }
        }
        if(set.isEmpty()){
            return;
        }
        for(String empNo:set){
            if(StringUtils.isBlank(empNo)){
                continue;
            }
            String modelId = "SAFETY-" + corresFormId + "-" + empNo;
            try{
                EipToDoVO eipToDoVO = new EipToDoVO(modelId,"","","","","","","","");
                eipCommonService.setEipToDoDone(eipToDoVO);
            }catch (Exception e){
                LogUtils.error("【消除代办异常】"+",from "+formEmpNo+" to "+empNo+":",e);
            }
        }

    }

    /**
     * 整理待确认的部门变更人员信息，并发送代办
     * @param isAll 是否全量 true/false
     * @return
     */
    public Object sendDepartToDo(String isAll){
        String beginTime = "";
        if(StringUtils.isBlank(isAll) || "false".equals(isAll)){
            beginTime = DateUtils.format(DateUtils.addDays(new Date(),-1),1) + " 09:00:00";
        }else{
            //全量执行时，删除历史数据
            BuildLongConfirmListParam listParam = new BuildLongConfirmListParam();
            listParam.setConfirmFlag("0");
            safetyBuildDepartConfirmService.deleteChangeByExample(listParam);
        }
        //增加过滤条件员工台账中创建时间<t-2的数据，排除掉新员工,离职员工
        String empCreateBegin = DateUtils.format(DateUtils.addDays(new Date(),-2),2);
        List<SafetyBuildDepartChangeDO> list = safetyBuildDepartConfirmService.selectDepartChangeList(beginTime,empCreateBegin);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        //获取员工组织对应的责任部门
        List<String> orgList = list.stream().flatMap(li -> {
            List<String> codes = new ArrayList<>();
            if (StringUtils.isNotBlank(li.getNewDeptCode())) {
                codes.add(li.getNewDeptCode());
            }
            if (StringUtils.isNotBlank(li.getOldDeptCode())) {
                codes.add(li.getOldDeptCode());
            }
            return codes.stream();
        }).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        List<BuildGroupDutyVO> buildPowerDOS = safetyBuildPowerService.getGroupDuty(orgList);
        Map<String,BuildGroupDutyVO> deptDutyMap;
        List<String> dutyCodeList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(buildPowerDOS)){
            dutyCodeList = buildPowerDOS.stream().map(BuildGroupDutyVO::getDutyDeptCode).collect(Collectors.toList());
            deptDutyMap = buildPowerDOS.stream().collect(Collectors.toMap(
                    BuildGroupDutyVO::getCode,
                    dto -> dto,
                    (oldValue, newValue) -> oldValue
            ));
        }else{
            deptDutyMap = new HashMap<>();
        }
        //获取员工组织对应的责任部门对应的责任人
        //所有设备对应的责任部门
        Map<String,List<SafetyBuildPowerDO>> deviceDutyMap;
        BuildPowerListParam buildPowerListParam = new BuildPowerListParam();
        buildPowerListParam.setDutyDeptCodeList(dutyCodeList);

        List<SafetyBuildPowerDO> deviceDutyList = safetyBuildPowerService.selectList(buildPowerListParam);
        if(!CollectionUtils.isEmpty(deviceDutyList)){
            deviceDutyMap = deviceDutyList.stream().collect(Collectors.groupingBy(SafetyBuildPowerDO::getDutyDeptCode));
        }else{
            deviceDutyMap = new HashMap<>();
        }
        list.forEach(cur -> {
            // 处理新部门代码
            processDept(cur, deptDutyMap, deviceDutyMap, true);
            // 处理旧部门代码
            processDept(cur, deptDutyMap, deviceDutyMap, false);
        });

        // 过滤掉原责任人和当前责任人都为空的数据
        list = list.stream().filter(li -> {
            boolean b = StringUtils.isNotBlank(li.getDutyEmpCode()) || StringUtils.isNotBlank(li.getOldDutyEmpCode());
            if (!b) {
                LogUtils.info("新责任人和旧责任人为空：{}", li);
            }
            return b;
        }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        int updateRows = safetyBuildDepartConfirmService.insertBatchDepartChange(list);
        //发送代办 - 开关开放式，发送代办
        SafetySystemParamDO systemParamDO = safetySystemParamService.getByParamCode("BUILD_DEPART_TODO");
        if(systemParamDO != null && "1".equals(systemParamDO.getParamValue())){

            List<String> empNoList = list.stream().map(SafetyBuildDepartChangeDO::getEmployeeCode).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
            BuildLongConfirmListParam listParam =  new BuildLongConfirmListParam();
            listParam.setEmployeeCodeList(empNoList);
            listParam.setIsCheckBuild(true);
            listParam.setPageFlag(false);
            List<BuildDepartConfirmVO> confirmVOList = safetyBuildDepartConfirmService.selectDepartList(listParam);
            disList(confirmVOList);
            List<Integer> ids = confirmVOList.stream().filter(li -> StringUtils.isNotBlank(li.getAllPowerStr()) && StringUtils.isNotBlank(li.getLocalPowerStr()) && StringUtils.equals(li.getAllPowerStr(), li.getLocalPowerStr())).map(BuildDepartConfirmVO::getId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(ids)) {
                //批量更新组织变更状态
                SafetyBuildDepartChangeDO changeDO = new SafetyBuildDepartChangeDO();
                changeDO.setBuildConfirmFlag("1");
                safetyBuildDepartConfirmService.updateDepartChangeByIdList(ids,changeDO);
            }
            List<BuildDepartConfirmVO> todoList = confirmVOList.stream().filter(li -> StringUtils.isBlank(li.getAllPowerStr()) || StringUtils.isBlank(li.getLocalPowerStr()) || !StringUtils.equals(li.getAllPowerStr(), li.getLocalPowerStr())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(todoList)) {
                return null;
            }
            sendDepartTodoSend(todoList);
            //TODO变更通知和关闭通知（未实现）暂不上线
            //给组织变更人员发送小程序提醒
            //sendMsgToChangeList(list);
        }

        return updateRows;
    }

    // 处理部门的辅助方法
    private void processDept(SafetyBuildDepartChangeDO cur, Map<String, BuildGroupDutyVO> deptDutyMap, Map<String, List<SafetyBuildPowerDO>> deviceDutyMap, boolean isNewDept) {
        String deptCode = isNewDept ? cur.getNewDeptCode() : cur.getOldDeptCode();
        if (StringUtils.isBlank(deptCode)) {
            return;
        }

        BuildGroupDutyVO dutyDept = deptDutyMap.get(deptCode);
        if (ObjectUtils.isEmpty(dutyDept)) {
            return;
        }

        // 获取或设置职责部门代码和名称
        setDutyDeptInfo(cur, dutyDept, isNewDept);

        // 获取设备职责数据
        List<SafetyBuildPowerDO> buildPowerDOList = deviceDutyMap.get(dutyDept.getDutyDeptCode());
        if (CollectionUtils.isEmpty(buildPowerDOList)) {
            return;
        }

        // 设置员工代码和名称
        setEmployeeInfo(cur, buildPowerDOList, isNewDept);
    }

    // 设置职责部门信息的辅助方法
    private void setDutyDeptInfo(SafetyBuildDepartChangeDO cur, BuildGroupDutyVO dutyDept, boolean isNewDept) {
        if (isNewDept) {
            cur.setDutyDeptCode(dutyDept.getDutyDeptCode());
            cur.setDutyDeptName(dutyDept.getDutyDeptName());
        }
        // 对于旧部门，可能不需要设置这些信息，具体根据业务需求决定
    }

    // 设置员工信息的辅助方法
    private void setEmployeeInfo(SafetyBuildDepartChangeDO cur, List<SafetyBuildPowerDO> buildPowerDOList, boolean isNewDept) {
        String employeeCodes = buildPowerDOList.stream()
                .map(SafetyBuildPowerDO::getEmployeeCode)
                .distinct()
                .collect(Collectors.joining(","));
        String employeeNames = buildPowerDOList.stream()
                .map(SafetyBuildPowerDO::getEmployeeName)
                .distinct()
                .collect(Collectors.joining(","));

        if (isNewDept) {
            cur.setDutyEmpCode(employeeCodes);
            cur.setDutyEmpName(employeeNames);
        } else {
            cur.setOldDutyEmpCode(employeeCodes);
            cur.setOldDutyEmpName(employeeNames);
        }
    }

    /**
     * 整理待确认的部门变更人员信息 - 发送代办动作
     * @param list 集合
     */
    private void sendDepartTodoSend(List<BuildDepartConfirmVO> list){
        String empStr = list.stream()
                .map(li -> {
                    String dutyEmpCode = li.getDutyEmpCode();
                    String oldDutyEmpCode = li.getOldDutyEmpCode();
                    if (StringUtils.isNotBlank(dutyEmpCode)) {
                        return dutyEmpCode;
                    } else if (StringUtils.isNotBlank(oldDutyEmpCode)) {
                        return oldDutyEmpCode;
                    } else {
                        return "";
                    }
                })
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(","));

        Set<String> empSet = Sets.newHashSet(empStr.split(","));
        for(String empCode:empSet){
            if(StringUtils.isBlank(empCode)){
                continue;
            }
            String modelId = "SAFETY-" + curFormId + "-" + empCode;
            // 消除代办
            eipCommonService.eipToDoDone(modelId, empCode);

            String subject = "[安保管理平台]请确认部门变更人员的门禁权限";
            String modelName = "部门变动人员权限变更确认";
            String link = anEipTodoUrl + curFormId + "?tab=1";
            EipToDoVO eipToDoVO = new EipToDoVO(modelId, modelName, subject, link, empCode, "isOpenInMobile");
            eipCommonService.createEipOfDefaultCreator(eipToDoVO, empCode);
        }
    }

    /**
     * 给变更人发送权限变更的提醒-发机器人
     * @param list 变更清单
     */
    private void sendMsgToChangeList(List<SafetyBuildDepartChangeDO> list){

        RestTemplate restTemplate = new RestTemplate();
        String sendMsg = "【重要】楼宇门禁权限变更提醒，请关注！" +
                "　　系统检测到您的组织架构已发生变更。为确保涉密区信息安全，根据权限最小化原则，系统将于7天后自动关闭原楼宇门禁权限。\n" +
                "　　如果您需要保留原楼宇门禁权限，请在7天内联系本部门门禁管理员及对应权限的部门门禁管理员进行确认。逾期未确认，权限将于（XX月XX日）后自动关闭。请及时处理以避免不便。\n" +
                "　　感谢配合，如有疑问可联系司法保卫室（6671-8119";
        Map<String,List<SafetyBuildDepartChangeDO>> groupByDutyMap = list.stream()
                .collect(Collectors.groupingBy(SafetyBuildDepartChangeDO::getDutyDeptCode));
        groupByDutyMap.forEach((dutyCode,empList)->{
            String[] dutyEmpNo = empList.get(0).getDutyEmpCode().split(",");
            String[] dutyEmpName = empList.get(0).getDutyEmpName().split(",");
            if (dutyEmpNo.length != dutyEmpName.length) {
                LogUtils.error(ResponseEnum.EXCEPTION.getCode(),"【组织变更权限确认】责任部门"+dutyCode+"的责任人编码和姓名不一致，相关变个人机器人提醒异常");
                return;
            }
            StringBuilder dutyEmpStr = new StringBuilder(dutyEmpName.length * 20); // 初始容量
            for(int i=0;i<dutyEmpName.length;i++){
                if(!"".equals(dutyEmpStr.toString())){
                    dutyEmpStr.append(";");
                }
                dutyEmpStr.append(dutyEmpName[i]).append("(").append(dutyEmpNo[i]).append(")");

            }
            String toEmpNos = empList.stream().map(SafetyBuildDepartChangeDO::getEmployeeCode).collect(Collectors.joining(","));
            String newMsg = String.format(sendMsg, dutyEmpStr);
            yxtRebotUtils.postForEntity(null, incomintRebotId, toEmpNos, newMsg, restTemplate);
        });

    }

    /**
     * 给对接部门负责人发送代办
     * @param todoConfirmDOS 待确认信息
     * @return
     */
    public void sendDepartCorresToDo(List<SafetyBuildDepartConfirmDO> todoConfirmDOS){
        //发送代办
        String empStr = todoConfirmDOS.stream().filter(e->StringUtils.isNotBlank(e.getDutyEmpCode()))
                .map(SafetyBuildDepartConfirmDO::getCorresEmpCode).collect(Collectors.joining(";"));
        empStr = empStr.replace(",", ";");
        Set<String> empSet = Sets.newHashSet(empStr.split(";"));
        for(String empCode:empSet){
            if(StringUtils.isBlank(empCode)){
                continue;
            }
            String modelId = "SAFETY-" + corresFormId + "-" + empCode;
            try{
                EipToDoVO eipToDoVO = new EipToDoVO(modelId,"","","","","","","","");
                eipCommonService.setEipToDoDone(eipToDoVO);
            }catch (Exception e){
                LogUtils.error("【消除代办异常】"+",from "+formEmpNo+" to "+empCode+":",e);
            }
            try {
                String taragets = "[{\"PersonNo\":\"" + empCode + "\"}]";
                String docCreator = "{\"PersonNo\":\"" + formEmpNo + "\"}";
                String subject = "[安保管理平台]请确认人员跨部门的门禁权限";
                String modelName = "部门变动人员权限变更确认";
                String link = anEipTodoUrl + corresFormId;
                //others = isOpenInMobile 标识禁止移动端预览
                EipToDoVO eipToDoVO = new EipToDoVO(modelId, modelName, subject, link, null, taragets, null, docCreator, null,"isOpenInMobile");
                eipCommonService.createEipToDo(eipToDoVO);
            }catch (Exception e){
                LogUtils.error("【发送代办失败】请确认人员跨部门的门禁权限 to "+empCode+":",e);
            }
        }
    }

    /**
     * 变更时间+7天内未确认的信息，自动取消授权并移除白名单
     * @return
     */
    public Object autoConfirmPower(){
        //查询对应的组织变更时间>7天的
        BuildLongConfirmListParam listParam1 = new BuildLongConfirmListParam();
        listParam1.setPageFlag(false);
        String beginTime = DateUtils.format(DateUtils.addDays(new Date(),-7),2);
        listParam1.setChangeTimeEnd(beginTime);
        List<BuildDepartConfirmVO> departList =safetyBuildDepartConfirmService.selectDepartList(listParam1);
        if(CollectionUtils.isEmpty(departList)){
            LogUtils.info("自动取消授权任务获取到的数据为0条");
            return null;
        }
        Map<String,String> tokenUser = new HashMap<>();
        tokenUser.put("workNo","定时作业");
        tokenUser.put("realName","定时作业");

        //取消门禁
        try {
            autoConfirmBuildPower(departList, tokenUser,beginTime);
        }catch (Exception e){
            LogUtils.error("自动取消门禁权限异常，{}",e);
        }
        //取消白名单
        try {
            autoConfirmWhitePower(departList, tokenUser);
        }catch (Exception e){
            LogUtils.error("自动取消白名单权限异常，{}",e);
        }

        //把EIP代办设置为已办
        doneDepartChangeEipTodo(departList);

        try{
            String empStr = departList.stream().map(BuildDepartConfirmVO::getEmployeeCode).distinct().collect(Collectors.joining(","));
            //操作日志记录
            LogMessageAddParam log = new LogMessageAddParam();
            log.setOperatePage("组织变更权限确认");
            log.setAbility("超7天自动关闭");
            String dealt = "关闭";
            log.setMessage(dealt+empStr);
            log.setOperator(tokenUser.get("realName"));
            safetyLogMessageService.addLogMessage(log);
        } catch (Exception e) {
            LogUtils.error("保存日志异常",e);
        }
        return null;
    }

    /**
     * 自动取消门禁授权
     * @param departList 查询到的所有信息
     * @param tokenUser 执行人信息
     * @param beginTime 门禁开通时间
     */
    private void autoConfirmBuildPower(List<BuildDepartConfirmVO> departList,
                                       Map<String,String> tokenUser,String beginTime){
        List<BuildDepartConfirmVO> cancelBuildList = departList.stream().filter(e->"0".equals(e.getConfirmFlag())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(cancelBuildList)){
            LogUtils.info("自动取消授权任务获取到的数据为0条");
            return;
        }
        List<String> empCodeList = cancelBuildList.stream().map(BuildDepartConfirmVO::getEmployeeCode).collect(Collectors.toList());
        List<Integer> changeId = cancelBuildList.stream().map(e->e.getId()).collect(Collectors.toList());
        BuildEmployeePowerListParam empPowerParam = new BuildEmployeePowerListParam();
        empPowerParam.setPageFlag(false);
        empPowerParam.setEmpCodeList(empCodeList);
        empPowerParam.setUpdateTimeEnd(beginTime);
        //查询批量处理时的门禁授权列表
        List<SafetyBuildEmployeePowerDO> list = safetyBuildEmployeePowerService.selectByExample(empPowerParam);
        if(CollectionUtils.isEmpty(list)){
            LogUtils.info("未找到要处理的门禁信息，入参="+empCodeList.toArray());
            return;
        }
        privateDealBuild("2","2",cancelBuildList,tokenUser,list,changeId,"部门变更超过7自动取消授权");

    }

    /**
     * 取消白名单授权
     * @param departList 待确认清单
     * @param tokenUser 执行人信息
     */
    private void autoConfirmWhitePower(List<BuildDepartConfirmVO> departList,Map<String,String> tokenUser) {
        List<BuildDepartConfirmVO> cancelWhiteList = departList.stream().filter(e->"是".equals(e.getIsWhite())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(cancelWhiteList)){
            LogUtils.info("自动取消授权任务获取到的数据为0条");
            return;
        }
        List<Integer> changeId = cancelWhiteList.stream().map(e->e.getId()).collect(Collectors.toList());
        batchModifyWhite("2",changeId,tokenUser,"部门变更超过7自动取消授权","2",cancelWhiteList);
    }

    /**
     * 定时任务，自动确认调岗原对接门禁管理员变更门禁权限
     */
    public void autoDepartConfirmPower() {
        // 查询数据创建时间大于7天的
        String beginTime = DateUtils.format(DateUtils.addDays(new Date(), -7), 2);
        BuildLongConfirmListParam listParam = new BuildLongConfirmListParam();
        listParam.setLtUpdateTime(beginTime);
        listParam.setConfirmFlag("0"); //查询未确认的变更信息
        listParam.setCorresFlag("0"); //判断涉及到原部门确认的门禁
        listParam.setPageFlag(false);
        List<SafetyBuildDepartConfirmDO> confirmDOS = safetyBuildDepartConfirmService.selectList(listParam);
        if (CollectionUtils.isEmpty(confirmDOS)) {
            LogUtils.info("自动取消授权任务获取到的数据为0条");
            return;
        }
        // 获取设备信息
        try{
            List<String> deviceCodes = confirmDOS.stream().map(SafetyBuildDepartConfirmDO::getDeviceCode).distinct().collect(Collectors.toList());
            List<SafetyBuildDeviceDO> deviceList = safetyBuildDeviceService.selectList(deviceCodes);
            Map<String, SafetyBuildDeviceDO> deviceMap = deviceList.stream().collect(Collectors.toMap(SafetyBuildDeviceDO::getDeviceCode, e -> e, (existing, replacement) -> existing));

            SafetyBuildDepartConfirmDO curConfirmDO = new SafetyBuildDepartConfirmDO();
            String endTime = DateUtils.format(DateUtils.addDays(new Date(), -1), 2);
            List<SafetyBuildIssuedLogDO> issuedLogDOS = new ArrayList<>();

            confirmDOS.forEach(e -> {
                String deviceCode = e.getDeviceCode();
                SafetyBuildIssuedLogDO logDO = new SafetyBuildIssuedLogDO();
                BeanUtils.copyProperties(e, logDO);
                SafetyBuildDeviceDO curDev = deviceMap.get(deviceCode);
                if (curDev != null && "2".equals(curDev.getDeviceType())) {
                    logDO.setDeviceType("2");
                } else {
                    logDO.setDeviceType("1");
                }
                logDO.setBeginTime(endTime);
                logDO.setEndTime(endTime);
                logDO.setRoleType("2");
                logDO.setCreateBy("定时作业");
                logDO.setRemark("定时任务-组织变更取消权限");
                logDO.setIssuedType("关闭");
                logDO.setSourceType("3"); //长期未进出
                issuedLogDOS.add(logDO);
            });
            // 公共楼宇关闭
            safetyBaseHandler.getCommonEmpNoBuildInfo(issuedLogDOS, "自动确认调岗原对接门禁管理员变更门禁权限");
            curConfirmDO.setConfirmFlag("2"); //确认关闭

            curConfirmDO.setConfirmRoleType("2");
            /*关闭权限*/
            if (!CollectionUtils.isEmpty(issuedLogDOS)) {
                safetyBuildProcessHandler.issuedPowerByListNew("2",issuedLogDOS);
            }
            curConfirmDO.setConfirmEmpCode("定时作业");
            curConfirmDO.setConfirmEmpName("定时作业");
            safetyBuildDepartConfirmService.updateByExample(listParam, curConfirmDO);
        }catch (Exception e){
            LogUtils.error("自动关闭原对接部门权限异常",e);
            LogUtils.error(ResponseEnum.EXCEPTION.getCode(),"自动关闭原对接部门权限异常");
        }
        //取消代办
        try {
            cancelCorresTODO(confirmDOS);
        }catch (Exception e){
            LogUtils.error("取消原对接部门的代办异常",e);
        }
        try{
            String empStr = confirmDOS.stream().map(SafetyBuildDepartConfirmDO::getEmployeeCode).distinct().collect(Collectors.joining(","));
            //操作日志记录
            LogMessageAddParam log = new LogMessageAddParam();
            log.setOperatePage("组织变更权限跨部门确认");
            log.setAbility("超7天自动关闭");
            String dealt = "关闭";
            log.setMessage(dealt+empStr);
            log.setOperator("定时作业");
            safetyLogMessageService.addLogMessage(log);
        } catch (Exception e) {
            LogUtils.error("保存日志异常",e);
        }
    }

    /**
     * 取消授权
     * @param cancelAuthorizeDtoList 取消授权的人员信息集合
     * @param remark 备注
     */
    private void cancelAuthorize(List<SafetyBuildCancelAuthorizeDto> cancelAuthorizeDtoList, String remark) {
        // 数据排查
        cancelAuthorizeDtoList= cancelAuthorizeDtoList.stream().filter(li -> StringUtils.isNotBlank(li.getEmpNo()) && StringUtils.isNotBlank(li.getUserType())  && !CollectionUtils.isEmpty(li.getDeviceCode())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(cancelAuthorizeDtoList)) return;

        // 移除员工门禁权限信息
        cancelAuthorizeDtoList.forEach(li -> {
            // 移除门禁信息
            BuildEmployeePowerListParam listParam = new BuildEmployeePowerListParam();
            listParam.setDeviceCodeList(li.getDeviceCode());
            SafetyBuildEmployeePowerDO employeePowerDO = new SafetyBuildEmployeePowerDO();
            employeePowerDO.setRemark(remark);
            safetyBuildEmployeePowerService.deleteByExample(listParam, employeePowerDO);
        });

        // 根据楼宇门禁编码获取厂区编码
        List<SafetyBuildDeviceDO> safetyBuildDeviceDOS = safetyBuildDeviceCollectService.selectGuard(null);
        Map<String, String> dataMap = safetyBuildDeviceDOS.stream().filter(li -> StringUtils.isNotBlank(li.getDeviceCode())).collect(Collectors.toMap(SafetyBuildDeviceDO::getDeviceCode, SafetyBuildDeviceDO::getFactoryCode));

        // 厂区对比， 用员工门禁对应的厂区和员工厂区作对比
        // 获取员工厂区权限明细
        List<String> empNos = cancelAuthorizeDtoList.stream().filter(li -> li.getUserType().equals("ZS")).map(SafetyBuildCancelAuthorizeDto::getEmpNo).collect(Collectors.toList());
        List<ParamDTO> paramDTOS = Lists.newArrayList();
        // 正式工
        if (!CollectionUtils.isEmpty(empNos)) {
            paramDTOS = safetyParkEmployeePowerInfoService.selectByEmpNoList(empNos);
        }
        // 外协
        empNos = cancelAuthorizeDtoList.stream().filter(li -> li.getUserType().equals("WX")).map(SafetyBuildCancelAuthorizeDto::getEmpNo).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(empNos)) {
            paramDTOS.addAll(safetyOutsiderPromiseChangeService.selectListByEmpNoList(empNos));
        }

        Map<String, ParamDTO> parkDataMap = paramDTOS.stream().collect(Collectors.toMap(ParamDTO::getEmpNo, t -> t));

        // 获取员工楼宇门禁对应的厂区明细 基础数据
        empNos = cancelAuthorizeDtoList.stream().map(SafetyBuildCancelAuthorizeDto::getEmpNo).collect(Collectors.toList());
        List<ParamDTO> factoryIdByBuildPower = safetyBuildEmployeePowerService.getFactoryIdByBuildPower(empNos);

        Map<String, String> buildDataMap = factoryIdByBuildPower.stream().collect(Collectors.toMap(ParamDTO::getEmpNo, ParamDTO::getFactoryId));

        // 赋值厂区名称 1.获取所有的厂区名称；
        Map<Integer, String> map = safetyFieldManageService.getFactoryNameList();
        List<SafetyParkEmployeePowerInfoDO> zsList = new ArrayList<>(); // 正式工变更数据的集合
        List<SafetyOutsiderPromiseDTO> wxList = new ArrayList<>(); // 外协工变更数据的集合
        List<ParamDTO> list = new ArrayList<>();
        List<SafetyOutsiderPromiseDTO> wxSendEhr = new ArrayList<>(); // 外协回传的数据
        cancelAuthorizeDtoList.forEach(li -> {
            String empNo = li.getEmpNo();
            String buildFactoryCode = buildDataMap.get(empNo);
            ParamDTO parkParamDTO = parkDataMap.get(empNo);
            if (parkParamDTO != null) {
                String parkFactoryCode = parkParamDTO.getFactoryId();
                // 如果员工没有门禁权限，也没有厂区权限，不管他，反之，员工有门禁权限，但是有厂区权限，需要清空厂区权限
                if (StringUtils.isBlank(buildFactoryCode) && StringUtils.isNotBlank(parkFactoryCode)) {
                    // 删除处理
                    li.setDeleteFactoryCode(Arrays.asList(parkFactoryCode.split(","))); // (正式工的处理方式)
                    // 外协的处理 标记为 DELETE 表示要做清空处理
                    li.setLastFactoryCode("DELETE");
                } else if (StringUtils.isNotBlank(buildFactoryCode) && StringUtils.isBlank(parkFactoryCode)) {
                    // 新增处理：员工有门禁权限， 但是员工没有厂区权限，需要增加对应的厂区（正式员工的处理方式）
                    li.setSaveFactoryCode(Arrays.asList(buildFactoryCode.split(",")));
                    // 外协的处理
                    li.setLastFactoryCode(buildFactoryCode);
                } else if (StringUtils.isNotBlank(buildFactoryCode) && StringUtils.isNotBlank(parkFactoryCode)) {
                    // 有增有删的操作
                    // 员工有门禁权限， 并且也有厂区权限，需要匹配门禁对应的厂区和厂区是否一致， 以员工门禁对应的厂区权限为基础，对厂区权限进行多删少补的操作
                    List<String> buildList = Arrays.asList(buildFactoryCode.split(","));
                    List<String> parkList = Arrays.asList(parkFactoryCode.split(","));
                    // parkList 中没有的 buildList 的数据需要补充
                    List<String> saveList = buildList.stream().filter(ln -> !parkList.contains(ln)).collect(Collectors.toList());
                    li.setSaveFactoryCode(saveList);
                    // parkList 中比 buildList 多的数据需要删除
                    List<String> deleteList = parkList.stream().filter(ln -> !buildList.contains(ln)).collect(Collectors.toList());
                    li.setDeleteFactoryCode(deleteList);

                    List<String> wxData = new ArrayList<>(parkList);
                    if (!CollectionUtils.isEmpty(saveList)) {
                        parkList.addAll(saveList);
                    }
                    if (!CollectionUtils.isEmpty(deleteList)) {
                        wxData.removeAll(deleteList);
                    }
                    li.setLastFactoryCode(String.join(",", wxData));
                }
                if (li.getUserType().equals("WX")) {
                    // 外协的数据处理
                    SafetyOutsiderPromiseDTO promiseDTO = wxDataUpdate(li, remark);
                    if (promiseDTO != null) {
                        wxList.add(promiseDTO);
                    }
                }
                if (li.getUserType().equals("ZS")) {
                    // 删除和创建新增的对象, 返回值为新增的数据， 方法中执行了删除的逻辑
                    zsList.addAll(zsDataSaveAndDelete(li, remark, map));
                }

                // 关闭的数据
                String factoryId = li.getDeviceCode().stream().map(dataMap::get).filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
                if ("DELETE".equals(li.getLastFactoryCode())) {
                    // 要做清空处理, 所以直接关闭所有即可
                    factoryId = parkFactoryCode;
                } else {
                    // 没有清空的时候需要下发
                    list.add(generalParamDto(li, parkParamDTO, StringUtils.isNotBlank(li.getLastFactoryCode()) ? li.getLastFactoryCode() : parkFactoryCode, "1"));
                }
                list.add(generalParamDto(li, parkParamDTO, factoryId, "0"));

                // 不为空并且是外协的时候需要回传EHR
                if (li.isSendEhr()) {
                    SafetyOutsiderPromiseDTO dto = new SafetyOutsiderPromiseDTO();
                    BeanUtils.copyProperties(parkParamDTO, dto);
                    dto.setFactory(li.getLastFactoryCode());
                    dto.setServiceEndDate(parkParamDTO.getPowerEndTime());
                    wxSendEhr.add(dto);
                }
            }
        });
        // 关闭权限
        if (!CollectionUtils.isEmpty(wxList)) {
            safetyOutsiderPromiseChangeService.updateOutsiderInfo(wxList);
        }
        if (!CollectionUtils.isEmpty(zsList)) {
            safetyParkPowerProcessService.dealPower(zsList, "ZS");
        }

        // 权限下发
        // 匹配设备ID
        safetyParkPowerProcessHandler.getDeviceId(list);
        // 初始化组织ID和curId
        safetyParkPowerProcessHandler.generalOrgId(list);
        List<String> empnoList = list.stream().map(ParamDTO::getEmpNo).collect(Collectors.toList());
        //查询员工对应的车辆信息
        SafetyEmployeePrivateCarDO privateCarDO = new SafetyEmployeePrivateCarDO();
        privateCarDO.setEmpNoList(empnoList);
        privateCarDO.setLockFlag("0"); //只查询未锁定的车辆
        List<SafetyEmployeePrivateCarDO> privateCarDOS = safetyEmployeePrivateCarHandler.selectList(privateCarDO);
        Map<String, List<SafetyEmployeePrivateCarDO>> carinfoMap = privateCarDOS.stream().collect(Collectors.groupingBy(SafetyEmployeePrivateCarDO::getEmployeeNo));

        List<SafetyBuildIssuedLogDO> logList = new ArrayList<>();

        // 注销
        List<ParamDTO> closeList = list.stream().filter(li -> "1".equals(li.getType())).collect(Collectors.toList());
        // 注销开始时间
        String zxBeginDate = DateUtils.format(DateUtils.addDays(new Date(), -2), 1) + " 00:00:00";
        // 注销结束时间
        String zxEndDate = DateUtils.format(DateUtils.addDays(new Date(), -1), 1) + " 23:59:59";
        closeList.forEach(li -> {
            li.setBeginDate(zxBeginDate);
            li.setEndDate(zxEndDate);
            li.setPowerEndTime(zxEndDate);
            List<SafetyEmployeePrivateCarDO> carinfoList = carinfoMap.get(li.getEmpNo());
            boolean b = safetyParkPowerProcessHandler.powerUpdate(li, carinfoList);

            String message = li.getEmpName() + "[" + li.getEmpNo() + "]:人脸、卡、车辆关闭" +  (b ? "成功" : "失败。");
            safetyParkPowerProcessHandler.generatePowerLogRecord(logList, li, "关闭", message, b);
        });

        // 下发
        List<ParamDTO> openList = list.stream().filter(li -> "0".equals(li.getType())).collect(Collectors.toList());
        // 下发开始时间
        String xfBeginDate = DateUtils.format(new Date(), 1) + " 00:00:00";
        // 下发结束时间
        String xfEndDate = "2099-12-31 23:59:59";
        openList.forEach(li -> {
            li.setBeginDate(xfBeginDate);
            if (StringUtils.isBlank(li.getPowerEndTime())) {
                li.setEndDate(xfEndDate);
                li.setPowerEndTime(xfEndDate);
            }
            List<SafetyEmployeePrivateCarDO> carinfoList = carinfoMap.get(li.getEmpNo());
            // 正式工权限下发
            if (li.getUserType().equals("ZS")) {
                boolean b = safetyParkPowerProcessHandler.powerUpdate(li, carinfoList);

        // TODO: 2025/7/25
                String message = li.getEmpName() + "[" + li.getEmpNo() + "]:人脸、卡、车辆下发" +  (b ? "成功" : "失败。");
                safetyParkPowerProcessHandler.generatePowerLogRecord(logList, li, "开通", message, b);
            }
            // 外协权限下发
            if (li.getUserType().equals("WX")) {
                boolean result = safetyParkPowerProcessHandler.wxPowerUpdate(li, logList, carinfoList);
            }
        });

        // 日志记录
        if (!CollectionUtils.isEmpty(logList)) {
            safetyBuildIssuedLogService.batchInsert(logList);
        };

        // 回写EHR
        if (!CollectionUtils.isEmpty(wxSendEhr)) {
            safetyBaseHandler.sendToEHRForFactory(wxSendEhr);
        }
    }

    /**
     * 生成下发和注销的对象
     * @param dto 基础数据
     * @param parkParamDTO 门禁对应厂区权限明细信息
     * @param factory 厂区编码，本次要关闭的厂区编码 type = 0 时使用
     * @param type 0 关闭， 1 下发
     * @return
     */
    private ParamDTO generalParamDto(SafetyBuildCancelAuthorizeDto dto, ParamDTO parkParamDTO, String factory ,String type) {
        // 表示还有权限，需要进行下发
        ParamDTO paramDTO = new ParamDTO();
        BeanUtils.copyProperties(parkParamDTO, paramDTO);
        paramDTO.setEndDate(parkParamDTO.getPowerEndTime());
        paramDTO.setFactoryId(factory);
        paramDTO.setType(type);
        return paramDTO;
    }

    /**
     * 正式员工的厂区权限维护
     * @param dto 数据对象
     * @param remark 备注
     * @param map 所有的厂区信息 map
     * @return 返回新增的数据列表，进行批量新增，
     */
    private List<SafetyParkEmployeePowerInfoDO> zsDataSaveAndDelete(SafetyBuildCancelAuthorizeDto dto, String remark, Map<Integer, String> map) {
        // 需要删除的厂区ID
        List<String> deleteFactoryCode = dto.getDeleteFactoryCode();
        // 需要新增的厂区ID
        List<String> saveFactoryCode = dto.getSaveFactoryCode();
        // 执行删除
        if (!CollectionUtils.isEmpty(deleteFactoryCode)) {
            // 直接删除
            SafetyParkEmployeePowerInfoDO powerInfoDO = new SafetyParkEmployeePowerInfoDO();
            powerInfoDO.setUpdateTime(new Date());
            powerInfoDO.setIsDelete((short)1);
            powerInfoDO.setRemark(remark);
            safetyParkEmployeePowerInfoService.cancelEmployeePower(powerInfoDO, dto.getEmpNo(), deleteFactoryCode);
        }
        // 执行新增
        String powerBeginTime = DateUtils.format(new Date(), 1) + " 00:00:00";
        List<SafetyParkEmployeePowerInfoDO> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(saveFactoryCode)) {
            // 创建对象，走后面的批量新增，减少数据库的消耗
            saveFactoryCode.forEach(li -> {
                SafetyParkEmployeePowerInfoDO powerInfo = new SafetyParkEmployeePowerInfoDO();
                powerInfo.setEmployeeCode(dto.getEmpNo());
                powerInfo.setFactoryId(li);
                powerInfo.setFactoryName(StringUtils.isNotBlank(map.get(Integer.parseInt(li))) ? map.get(Integer.parseInt(li)) : "");
                powerInfo.setPowerBeginTime(powerBeginTime);
                powerInfo.setPowerEndTime("2099-12-31 23:59:59");
                powerInfo.setCreateTime(new Date());
                powerInfo.setUpdateTime(new Date());
                powerInfo.setIsDelete((short) 0);
                powerInfo.setDownMark("0");
                list.add(powerInfo);
            });
        }
        return list;
    }

    /**
     * 外协数据变更实现
     * @param dto 对象
     * @param remark 备注
     * @retur 需要修改的就是数据对象，不需要修改的返回null
     */
    private SafetyOutsiderPromiseDTO wxDataUpdate(SafetyBuildCancelAuthorizeDto dto, String remark) {
        // 外协的需要变更的数据
        String wxLastFactoryCode = dto.getLastFactoryCode();
        if (StringUtils.isBlank(wxLastFactoryCode)) return null;
        SafetyOutsiderPromiseDTO promiseDTO = new SafetyOutsiderPromiseDTO();
        promiseDTO.setEmpNo(dto.getEmpNo());
        promiseDTO.setRemark(remark);
        if (!wxLastFactoryCode.equals("DELETE")) {
            // 变更 为空的话，就直接清空即可
            promiseDTO.setFactory(dto.getLastFactoryCode());
        }
        // 要推送到EHR
        dto.setSendEhr(true);
        return promiseDTO;
    }

    /**
     * 测试发送代办
     * @param empNos 接收代办的人员
     */
    public void testSendEipToDO(String empNos){
        if(StringUtils.isBlank(empNos)){
            return;
        }
        String[] set = empNos.split(",");
        for(String empCode:set) {
            String modelId = "SAFETY-" + corresFormId + "-" + empCode;
            try{
                EipToDoVO eipToDoVO = new EipToDoVO(modelId,"","","","","","","","");
                eipCommonService.setEipToDoDone(eipToDoVO);
            }catch (Exception e){
                LogUtils.error("【消除代办异常】"+",from "+formEmpNo+" to "+empCode+":",e);
            }
            try {
                String taragets = "[{\"PersonNo\":\"" + empCode + "\"}]";
                String docCreator = "{\"PersonNo\":\"" + formEmpNo + "\"}";
                String subject = "[安保管理平台]请确认人员跨部门的门禁权限";
                String modelName = "部门变动人员权限变更确认";
                String link = anEipTodoUrl + corresFormId;
                //others = isOpenInMobile 标识禁止移动端预览
                EipToDoVO eipToDoVO = new EipToDoVO(modelId, modelName, subject, link, null, taragets, null, docCreator, null, "isOpenInMobile");
                eipCommonService.createEipToDo(eipToDoVO);
            } catch (Exception e) {
                LogUtils.error("【发送代办失败】请确认人员跨部门的门禁权限 to " + empCode + ":", e);
            }
        }

    }

    /**
     * 校验已确认的数据用不用删除
     * @param curConfirmList 当前确认信息
     * @param curDepratVo 组织变更信息
     */
    private void checkEmpConfirmData(List<SafetyBuildDepartConfirmDO> curConfirmList,BuildDepartConfirmVO curDepratVo){
        if(CollectionUtils.isEmpty(curConfirmList)){
            return;
        }
        String confirmNewDeptCode = curConfirmList.get(0).getNewDeptCode();
        String chageNewDeptCode = curDepratVo.getNewDeptCode();
        String empCode = curConfirmList.get(0).getEmployeeCode();
        if(StringUtils.isBlank(confirmNewDeptCode) || !confirmNewDeptCode.equals(chageNewDeptCode)){
            BuildLongConfirmListParam listParam = new BuildLongConfirmListParam();
            listParam.setEmployeeCode(empCode);
            listParam.setNewDeptCode(confirmNewDeptCode);
            safetyBuildDepartConfirmService.deleteConfirmByExample(listParam);
        }
    }
}
