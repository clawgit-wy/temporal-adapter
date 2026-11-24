package com.yutong.safety.core.handler.common;

import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildIssuedLogDO;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerInfoDO;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeePowerListParam;
import com.yutong.safety.core.db.entity.park.param.ParkPowerRuleListParam;
import com.yutong.safety.core.db.entity.park.vo.ParkPowerRuleVO;
import com.yutong.safety.core.dto.ParamDTO;
import com.yutong.safety.core.dto.SafetyOutsiderPromiseDTO;
import com.yutong.safety.core.dto.park.SafetyParkPowerRuleDto;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.handler.employeeprivatecar.SafetyEmployeePrivateCarHandler;
import com.yutong.safety.core.handler.park.SafetyParkPowerProcessHandler;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.SafetyFieldManageService;
import com.yutong.safety.core.service.SafetyOutsiderPromiseChangeService;
import com.yutong.safety.core.service.build.SafetyBuildIssuedLogService;
import com.yutong.safety.core.service.park.SafetyParkEmpPowerReportService;
import com.yutong.safety.core.service.park.SafetyParkEmployeePowerInfoService;
import com.yutong.safety.core.service.park.SafetyParkPowerProcessService;
import com.yutong.safety.core.service.park.SafetyParkPowerRuleService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限下发 handler
 * 2025-12-30 16:15
 */
@Component
public class SafetyPowerIssueHandler extends VoUtil {

    @Resource
    private SafetyParkPowerRuleService safetyParkPowerRuleService;

    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;

    @Resource
    private SafetyFieldManageService safetyFieldManageService;

    @Resource
    private SafetyBaseHandler safetyBaseHandler;

    @Resource
    private SafetyParkPowerProcessHandler safetyParkPowerProcessHandler;

    @Resource
    private SafetyParkEmpPowerReportService safetyParkEmpPowerReportService;

    @Resource
    private SafetyParkPowerProcessService safetyParkPowerProcessService;

    @Resource
    private SafetyParkEmployeePowerInfoService safetyParkEmployeePowerInfoService;

    @Resource
    private SafetyOutsiderPromiseChangeService safetyOutsiderPromiseChangeService;

    @Resource
    private SafetyEmployeePrivateCarHandler safetyEmployeePrivateCarHandler;

    @Resource
    private SafetyBuildIssuedLogService safetyBuildIssuedLogService;

    /**
     * 大门规则权限下发 一次性的
     *
     * @param beginNum     开始的数量
     * @param operationNum 操作的数量
     */
    public VO issueDoorRulePower(Integer beginNum, Integer operationNum) {
        if (beginNum == null || beginNum == 0) {
            beginNum = 1;
        }
        if (operationNum == null) {
            operationNum = 5;
        }

        Integer totalNum = beginNum - 1 + operationNum;
        // 1.获取规则
        ParkPowerRuleListParam param = new ParkPowerRuleListParam();
        param.setPageSize(totalNum);
        List<ParkPowerRuleVO> ruleList = safetyParkPowerRuleService.selectPageList(param);
        LogUtils.info("规则数量" + ruleList.size());

        // 2.人员数据-根据岗位
        Set<String> postNames = getDistinctPostNames(ruleList);
        Map<String, List<SafetyEmployeeMessageDO>> positionMap = getPositionMapByNames(postNames);
        LogUtils.info("人员数据-根据岗位:" + positionMap.keySet());

        // 3.人员数据-根据组织
        Set<String> orgCodes = getDistinctOrgCodes(ruleList);
        Map<String, List<SafetyEmployeeMessageDO>> orgCodeMap = getOrgCodeMapByCodes(orgCodes);
        LogUtils.info("人员数据-根据组织:" + orgCodeMap.keySet());

        // 4.数据整理
        List<SafetyParkPowerRuleDto> resultList = disList(ruleList, positionMap, orgCodeMap);

        // 5.确定需要下发的权限数据
        List<SafetyParkPowerRuleDto> issueList = getIssueList(resultList, beginNum);
        if (CollectionUtils.isEmpty(issueList)) {
            return fail("没有需要下发的规则");
        }
        LogUtils.info("本次待下发的规则详情:" + issueList);

        // 6.获取人员信息
        List<String> empNoList = issueList.stream().flatMap(li -> li.getEmpList().stream()).collect(Collectors.toList());
        Pair<Map<String, List<SafetyParkEmployeePowerInfoDO>>, Map<String, SafetyOutsiderPromiseDTO>> pair = getHistoryPowerByEmpNo(empNoList);

        // 获取所有的厂区名称
        Map<Integer, String> factoryMap = safetyFieldManageService.getFactoryNameList();

        // 7.增量厂区入库
        List<String> empInfo = saveInfo(issueList, pair.getLeft(), pair.getRight(), factoryMap);
        LogUtils.info("本次待下发权限的员工:" + empInfo);

        // 8.权限下发
//        issuePower(empInfo);
        return success();
    }

    /**
     * 增量厂区入库，根据外协和正式区分需要下发的权限，判断人员已有的权限是否包含本次需要新增的权限，全部存在跳过，部分不存在的需要补充并下发
     *
     * @param issueList  需要下发的数据信息
     * @param zsDataMap  正式员工信息
     * @param wxDataMap  外协信息
     * @param factoryMap 厂区信息
     */
    private List<String> saveInfo(List<SafetyParkPowerRuleDto> issueList,
                                  Map<String, List<SafetyParkEmployeePowerInfoDO>> zsDataMap,
                                  Map<String, SafetyOutsiderPromiseDTO> wxDataMap,
                                  Map<Integer, String> factoryMap) {

        List<String> issuePowerList = new ArrayList<>();
        // 外协和正式需要增加的厂区
        List<SafetyParkEmployeePowerInfoDO> zsList = new ArrayList<>();
        List<SafetyOutsiderPromiseDTO> wxList = new ArrayList<>();
        for (SafetyParkPowerRuleDto ruleDto : issueList) {
            // 过滤掉没有员工号的数据
            if (CollectionUtils.isEmpty(ruleDto.getEmpList())) {
                continue;
            }
            // 厂区信息
            List<String> factoryIds = Arrays.asList(ruleDto.getFactoryPower().split(","));
            ruleDto.getEmpList().stream().filter(StringUtils::isNotBlank).forEach(empNo -> {
                List<SafetyParkEmployeePowerInfoDO> employeePowers = zsDataMap.get(empNo);
                if (!CollectionUtils.isEmpty(employeePowers)) {
                    // 正式的员工判断
                    handleInternalEmployee(empNo, factoryIds, employeePowers, zsList, issuePowerList, factoryMap);
                } else {
                    // 外协的员工判断
                    handleExternalEmployee(empNo, ruleDto, factoryIds, wxDataMap, wxList, issuePowerList);
                }
            });
        }

        if (!CollectionUtils.isEmpty(zsList)) {
            // 临时
            safetyParkPowerProcessService.dealPowerTemp(zsList);
        }
        if (!CollectionUtils.isEmpty(wxList)) {
            // 临时
//            safetyOutsiderPromiseChangeService.updateOutsiderInfo(wxList);
            safetyOutsiderPromiseChangeService.saveOutsiderInfoTemp(wxList);
            // 回写EHR
//            safetyBaseHandler.sendToEHRForFactory(wxList);
        }
        return issuePowerList;
    }

    /**
     * 正式员工的判断逻辑
     *
     * @param empNo          员工号
     * @param factoryIds     规则中的厂区ID
     * @param employeePowers 员工已存在的厂区信息
     * @param zsList         入库的list
     * @param issuePowerList 下发的员工
     * @param factoryMap     全量厂区信息
     */
    private void handleInternalEmployee(String empNo, List<String> factoryIds,
                                        List<SafetyParkEmployeePowerInfoDO> employeePowers,
                                        List<SafetyParkEmployeePowerInfoDO> zsList,
                                        List<String> issuePowerList,
                                        Map<Integer, String> factoryMap) {
        Set<String> existingFactoryIds = employeePowers.stream()
                .map(SafetyParkEmployeePowerInfoDO::getFactoryId)
                .collect(Collectors.toSet());

        List<String> newFactoryIds = factoryIds.stream()
                .filter(id -> !existingFactoryIds.contains(id))
                .collect(Collectors.toList());

        if (!newFactoryIds.isEmpty()) {
            zsList.addAll(newFactoryIds.stream().map(lv -> {
                // 人员权限补充
                SafetyParkEmployeePowerInfoDO info = new SafetyParkEmployeePowerInfoDO();
                info.setEmployeeCode(empNo);
                info.setFactoryId(lv);
                info.setPowerBeginTime(DateUtils.format(new Date(), 2));
                info.setPowerEndTime("2099-12-31");
                info.setRemark("规则初始化");
                info.setCreateTime(new Date());
                info.setUpdateTime(new Date());
                info.setIsDelete((short) 0);
                String factoryValue = factoryMap.get(Integer.parseInt(lv));
                info.setFactoryName(factoryValue);
                info.setDownMark("1");
                return info;
            }).collect(Collectors.toList()));
            issuePowerList.add(empNo);
        }
    }

    /**
     * 外协员工的判断逻辑
     *
     * @param empNo          员工号
     * @param ruleDto        规则信息
     * @param factoryIds     规则的厂区ID
     * @param wxDataMap      外协员工已存在的厂区信息
     * @param wxList         入库的list
     * @param issuePowerList 下发的员工
     */
    private void handleExternalEmployee(String empNo, SafetyParkPowerRuleDto ruleDto,
                                        List<String> factoryIds, Map<String, SafetyOutsiderPromiseDTO> wxDataMap,
                                        List<SafetyOutsiderPromiseDTO> wxList, List<String> issuePowerList) {

        SafetyOutsiderPromiseDTO promiseDTO = wxDataMap.get(empNo);
        if (promiseDTO == null) {
            LogUtils.info("没有权限的人员信息:" + empNo);
            return;
        }
        String currentFactory = promiseDTO.getFactory();
        if (StringUtils.isBlank(currentFactory)) {
            // 当前工厂为空，直接设置新的工厂权限
            promiseDTO.setFactory(ruleDto.getFactoryPower());
            promiseDTO.setRemark(promiseDTO.getRemark() + "规则初始化" + String.join(",", ruleDto.getFactoryPower()));
        } else {
            // 当前工厂不为空，追加新工厂权限
            String[] split = currentFactory.split(",");
            List<String> currentFactoryList = Arrays.stream(split).collect(Collectors.toList());
            List<String> newFactoryIds = factoryIds.stream()
                    .filter(factoryId -> !currentFactoryList.contains(factoryId))
                    .collect(Collectors.toList());
            if (!newFactoryIds.isEmpty()) {
                currentFactoryList.addAll(newFactoryIds);
                promiseDTO.setFactory(String.join(",", currentFactoryList));
                promiseDTO.setRemark(promiseDTO.getRemark() + "规则初始化" + String.join(",", newFactoryIds));
            }
        }
        wxList.add(promiseDTO);
        issuePowerList.add(empNo);

    }

    /**
     * 获取正式和外协的员工权限信息
     *
     * @param empNoList 员工号
     * @return left：正式， right：外协
     */
    private Pair<Map<String, List<SafetyParkEmployeePowerInfoDO>>, Map<String, SafetyOutsiderPromiseDTO>> getHistoryPowerByEmpNo(List<String> empNoList) {
        // 获取正式员工权限
        ParkEmployeePowerListParam param = new ParkEmployeePowerListParam();
        param.setEmpNoList(empNoList);
        List<SafetyParkEmployeePowerInfoDO> powerInfo = safetyParkEmpPowerReportService.selectByExample(param);
        Map<String, List<SafetyParkEmployeePowerInfoDO>> zsDataMap = powerInfo.stream().collect(Collectors.groupingBy(SafetyParkEmployeePowerInfoDO::getEmployeeCode));

        // 获取外包外协权限
        List<SafetyOutsiderPromiseDTO> outSideList = safetyOutsiderPromiseChangeService.findOldPromise(empNoList);
        Map<String, SafetyOutsiderPromiseDTO> wxDataMap = outSideList.stream().collect(Collectors.toMap(SafetyOutsiderPromiseDTO::getEmpNo, li -> li));
        return Pair.of(zsDataMap, wxDataMap);
    }

    private Set<String> getDistinctPostNames(List<ParkPowerRuleVO> ruleList) {
        return ruleList.stream()
                .map(ParkPowerRuleVO::getPostName)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
    }

    private Map<String, List<SafetyEmployeeMessageDO>> getPositionMapByNames(Set<String> postNames) {
        if (CollectionUtils.isEmpty(postNames)) {
            return Collections.emptyMap();
        }
        List<SafetyEmployeeMessageDO> empList = safetyEmployeeMessageService.selectInfoByPositionName(new ArrayList<>(postNames));
        return empList.stream()
                .collect(Collectors.groupingBy(SafetyEmployeeMessageDO::getPositionName));
    }

    private Set<String> getDistinctOrgCodes(List<ParkPowerRuleVO> ruleList) {
        return ruleList.stream()
                .map(ParkPowerRuleVO::getOrgCode)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
    }

    private Map<String, List<SafetyEmployeeMessageDO>> getOrgCodeMapByCodes(Set<String> orgCodes) {
        if (CollectionUtils.isEmpty(orgCodes)) {
            return Collections.emptyMap();
        }
        List<SafetyEmployeeMessageDO> orgEmpList = safetyEmployeeMessageService.selectListByOrgCode(new ArrayList<>(orgCodes));
        return orgEmpList.stream()
                .collect(Collectors.groupingBy(SafetyEmployeeMessageDO::getOrgCode));
    }

    /**
     * 数据处理， 匹配规则对应的员工信息
     *
     * @param ruleList    规则信息
     * @param positionMap 岗位人员信息
     * @param orgCodeMap  组织人员信息
     * @return 返回匹配后的员工信息
     */
    private List<SafetyParkPowerRuleDto> disList(List<ParkPowerRuleVO> ruleList, Map<String, List<SafetyEmployeeMessageDO>> positionMap, Map<String, List<SafetyEmployeeMessageDO>> orgCodeMap) {
        Set<String> allEmpList = new HashSet<>();
        List<SafetyParkPowerRuleDto> list = new ArrayList<>();
        ruleList.forEach(li -> {
            SafetyParkPowerRuleDto dto = new SafetyParkPowerRuleDto();
            dto.setId(li.getId());
            dto.setFactoryPower(li.getFactoryPower());
            String postName = li.getPostName();
            String orgCode = li.getOrgCode();
            Set<String> empList = new HashSet<>();
            if (StringUtils.isNotBlank(postName)) {
                dto.setName(postName);
                positionMap.keySet().forEach(ln -> {
                    if (ln.contains(postName)) {
                        Set<String> collect = positionMap.getOrDefault(ln, new ArrayList<>()).stream()
                                .map(SafetyEmployeeMessageDO::getEmpNo)
                                .filter(ll -> StringUtils.isNotBlank(ll) && !allEmpList.contains(ll))
                                .collect(Collectors.toSet());
                        empList.addAll(collect);
                    }
                });
            } else if (StringUtils.isNotBlank(orgCode)) {
                dto.setName(orgCode);
                Set<String> collect = orgCodeMap.getOrDefault(orgCode, new ArrayList<>()).stream()
                        .map(SafetyEmployeeMessageDO::getEmpNo)
                        .filter(ll -> StringUtils.isNotBlank(ll) && !allEmpList.contains(ll))
                        .collect(Collectors.toSet());
                empList.addAll(collect);
            }
            allEmpList.addAll(empList);
            dto.setEmpList(empList);
            list.add(dto);
        });
        return list;
    }

    /**
     * 获取需要下发的数据， 操作的数据从所有数据的中倒数的操作数量
     *
     * @param resultList 需要获取的最大数据（beginNum + operationNum - 1）
     * @param beginNum   开始数
     * @return 返回需要操作的数据
     */
    private List<SafetyParkPowerRuleDto> getIssueList(List<SafetyParkPowerRuleDto> resultList, Integer beginNum) {
        // 确保参数有效，避免空指针异常
        if (CollectionUtils.isEmpty(resultList) || resultList.size() < beginNum) {
            return new ArrayList<>();
        }

        List<SafetyParkPowerRuleDto> safetyParkPowerRuleDtos = resultList.subList(beginNum - 1, resultList.size());
        return safetyParkPowerRuleDtos;
    }

    /**
     * 权限下发
     *
     * @param empNos 需要下发权限的数据
     */
    private void issuePower(List<String> empNos) {
        if (CollectionUtils.isEmpty(empNos)) {
            return;
        }
        // 获取正式员工和外协员工的用户列表
        List<ParamDTO> userList = safetyParkEmployeePowerInfoService.getUserList(empNos);
        userList.addAll(safetyOutsiderPromiseChangeService.selectListByWxInfo(empNos));
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }

        List<String> empNoList = userList.stream().map(ParamDTO::getEmpNo).collect(Collectors.toList());
        //查询员工对应的车辆信息
        SafetyEmployeePrivateCarDO privateCarDO = new SafetyEmployeePrivateCarDO();
        privateCarDO.setEmpNoList(empNoList);
        privateCarDO.setLockFlag("0"); //只查询未锁定的车辆
        List<SafetyEmployeePrivateCarDO> privateCarDOS = safetyEmployeePrivateCarHandler.selectList(privateCarDO);
        Map<String, List<SafetyEmployeePrivateCarDO>> carInfoMap = privateCarDOS.stream().collect(Collectors.groupingBy(SafetyEmployeePrivateCarDO::getEmployeeNo));

        // 获取员工的门禁权限和门禁对应的设备ID
        safetyParkPowerProcessHandler.getDeviceId(userList);

        // 初始化组织ID和curId
        safetyParkPowerProcessHandler.generalOrgId(userList);

        List<String> updateStatusList = new ArrayList<>();
        List<SafetyBuildIssuedLogDO> logList = new ArrayList<>();
        Set<String> hasDownList = new HashSet<>();
        userList.forEach(paramDTO -> {
            String empNo = paramDTO.getEmpNo();
            //因为是取得这个人的所有大门权限进行下发的，所以一个人只需要下发一次就行
            if (hasDownList.contains(empNo)) {
                return;
            }

            hasDownList.add(empNo);

            paramDTO.setCreateBy("规则匹配下发");
            paramDTO.setBeginDate(DateUtils.format(DateUtils.addDays(new Date(), -1), 1) + " 00:00:00");
            paramDTO.setEndDate(DateUtils.format(DateUtils.parse(paramDTO.getPowerEndTime(), 1), 1) + " 23:59:59");
            List<SafetyEmployeePrivateCarDO> carList = carInfoMap.getOrDefault(empNo, new ArrayList<>());
            if ("WX".equals(paramDTO.getUserType())) {
                // 外协权限下发
                safetyParkPowerProcessHandler.wxPowerUpdate(paramDTO, logList, carList);
            } else {
                // 正式工权限下发
                boolean result = safetyParkPowerProcessHandler.powerUpdate(paramDTO, carList);
                if (result) {
                    // 下发成功，回写状态
                    updateStatusList.add(paramDTO.getEmpNo());
                }
                String message = paramDTO.getEmpName() + "[" + paramDTO.getEmpNo() + "]:人脸、卡、车辆下发" + (result ? "成功" : "失败。");
                safetyParkPowerProcessHandler.generatePowerLogRecord(logList, paramDTO, "开通", message, result);
            }
        });

        // 日志记录
        if (!CollectionUtils.isEmpty(logList)) {
            safetyBuildIssuedLogService.batchInsert(logList);
        }

        // 状态回写
        if (!CollectionUtils.isEmpty(updateStatusList)) {
            safetyParkEmployeePowerInfoService.updateZsStatus(updateStatusList, "0");
        }
    }
}
