package com.yutong.safety.core.handler.emp;

import com.alibaba.excel.util.StringUtils;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.YxtRebotUtils;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetyGroupInfoDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildInfoConfigDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildInfoRecordDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildWhiteListDO;
import com.yutong.safety.core.db.entity.emp.SafetyDepartChangeListDO;
import com.yutong.safety.core.db.entity.emp.param.DepartChangeListParam;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.SafetyGroupService;
import com.yutong.safety.core.service.build.*;
import com.yutong.safety.core.service.emp.SafetyDepartChangeListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SafetyDepartChangeListHandler {
    @Autowired
    SafetyDepartChangeListService safetyDepartChangeListService;

    //安保小助手机器人ID
    @Value("${iam.retention.robotId}")
    private String incomintRebotId;

    @Autowired
    private YxtRebotUtils yxtRebotUtils;
    @Autowired
    SafetyBuildPowerService safetyBuildPowerService;
    @Autowired
    SafetyBuildWhiteListService safetyBuildWhiteListService;
    @Autowired
    private SafetyGroupService safetyGroupService;
    @Autowired
    SafetyBuildInfoConfigService safetyBuildInfoConfigService;
    @Autowired
    SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Autowired
    SafetyBuildInfoRecordService safetyBuildInfoRecordService;
    @Resource
    private SafetyBuildEmployeePowerService safetyBuildEmployeePowerService;

    public void save(SafetyDepartChangeListDO en) {
        //处理新部门的责任部门
        if(StringUtils.isNotBlank(en.getNewDeptCode())){
            try{
                SafetyGroupInfoDO groupInfoDO = safetyGroupService.getGroupByCode(en.getNewDeptCode());
                if(groupInfoDO != null){
                    en.setNewDeptName(groupInfoDO.getName());

                    if(StringUtils.isNotBlank(groupInfoDO.getOrgCodePath())){
                        List<SafetyBuildPowerDO> buildPowerDOS = safetyBuildPowerService.selectListByDept(groupInfoDO.getOrgCodePath());
                        if(!CollectionUtils.isEmpty(buildPowerDOS)){
                            en.setDutyDeptCode(buildPowerDOS.get(0).getDutyDeptCode());
                            en.setDutyDeptName(buildPowerDOS.get(0).getDutyDeptName());
                            String dutyEmpCode = buildPowerDOS.stream().map(SafetyBuildPowerDO::getEmployeeCode).distinct().collect(Collectors.joining(","));
                            String dutyEmpName = buildPowerDOS.stream().map(SafetyBuildPowerDO::getEmployeeName).distinct().collect(Collectors.joining(","));
                            en.setDutyEmpCode(dutyEmpCode);
                            en.setDutyEmpName(dutyEmpName);
                        }
                    }
                }
            }catch (Exception e){
                LogUtils.error("获取员工责任部门异常",e);
            }
        }
        safetyDepartChangeListService.saveOrUpdate(en);
        if("是".equals(en.getChangeType())){
            //离职
            dealResign(en.getEmployeeCode());
        } else {
            // 没有离职时固定门禁变更的时间
            safetyBuildEmployeePowerService.updateDepartChangeTime(Collections.singletonList(en.getEmployeeCode()));
        }
    }

    public void dealResign(String empNo){
        //查询离职人员所添加过的白名单
        SafetyBuildWhiteListDO param = new SafetyBuildWhiteListDO();
        param.setSourceFrom("0");
        param.setCreateUserCode(empNo);
        List<SafetyBuildWhiteListDO> list = safetyBuildWhiteListService.selectPageList(param);
        if(CollectionUtils.isEmpty(list)){return;}

        //获取涉及的责任部门
        List<String> collect = list.stream().map(SafetyBuildWhiteListDO::getDutyDeptCode)
                .distinct()
                .collect(Collectors.toList());

        //根据这些责任部门取查询剩余的保密负责人
        List<SafetyBuildPowerDO> buildPowerDOList = safetyBuildPowerService.getWithOutResinPeople(collect, empNo);
        if(CollectionUtils.isEmpty(buildPowerDOList)){
            //如果剩余的保密负责人为空，就将创建人滞空
            LogUtils.info("{}离职，清空其添加 的白名单的创建人",empNo);
            safetyBuildWhiteListService.updateByEmpNo(empNo,null);
        }else{
            //将剩余的保密负责人更新到创建人列
            //取除当前离职人之外还有保密负责人的组织
            List<String> deptCodeList = buildPowerDOList.stream().map(SafetyBuildPowerDO::getDutyDeptCode)
                    .distinct()
                    .collect(Collectors.toList());

            //更新除当前离职人没有保密负责人的部门
            collect.removeAll(deptCodeList);
            safetyBuildWhiteListService.updateByEmpNo(empNo,collect);

            //更新还有保密负责人的部分
            Map<String, List<SafetyBuildPowerDO>> deprtMap = buildPowerDOList.stream().collect(Collectors.groupingBy(SafetyBuildPowerDO::getDutyDeptCode));

            //创建更新的数据集
            for (SafetyBuildWhiteListDO en : list) {
                List<SafetyBuildPowerDO> doList = deprtMap.get(en.getDutyDeptCode());
                en.setCreateUserCode(doList.get(0).getEmployeeCode());
                en.setCreateUserName(doList.get(0).getEmployeeName());
                en.setUpdateTime(DateUtils.format(new Date(),2));
            }
            safetyBuildWhiteListService.batchUpdate(list);
        }
    }

    /**
     * 组织发生变化时，给对应人发送信息
     */
    public void sendMesWithDeptChange() {
        List<SafetyBuildInfoRecordDO> recordList = new ArrayList<>();
        //查询昨儿个岗位变动的人
        SafetyDepartChangeListDO en = new SafetyDepartChangeListDO();
        en.setCreateTime(DateUtils.format(DateUtils.addDays(new Date(), -1), 1));
        en.setChangeType("否");
        List<String> list = safetyDepartChangeListService.getChangeEmpCodeList(en);
        List<SafetyEmployeeMessageDO> listByStaffNo = safetyEmployeeMessageService.getListByStaffNo(list);
        Map<String, String> collect1 = listByStaffNo.stream().collect(Collectors.toMap(
                SafetyEmployeeMessageDO::getEmpNo,
                SafetyEmployeeMessageDO::getEmpName,
                (oldV, newV) -> newV
        ));

        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        //查询信息模板
        SafetyBuildInfoConfigDO param = new SafetyBuildInfoConfigDO();
        param.setId(6);
        param.setShowFlag("1");
        List<SafetyBuildInfoConfigDO> queryList = safetyBuildInfoConfigService.query(param);
        if(!CollectionUtils.isEmpty(queryList)){
            //发消息
            String sendmsg = queryList.get(0).getInfoContent();
            LogUtils.info("kafka部门调整发宇信通消息：{}", list);
            RestTemplate restTemplate = new RestTemplate();
            for (String code : list) {
                yxtRebotUtils.postForEntity(null, incomintRebotId, code, sendmsg, restTemplate);
                SafetyBuildInfoRecordDO safetyBuildInfoRecordDO = new SafetyBuildInfoRecordDO();
                safetyBuildInfoRecordDO.setInfoConfirm("1");
                String mes = "保密负责人 "+collect1.get(code)+" 所在部门发生变动，请及时调整处理";
                safetyBuildInfoRecordDO.setInfoContent(mes);
                safetyBuildInfoRecordDO.setInfoType("人员变动");
                safetyBuildInfoRecordDO.setInfoTime(DateUtils.format(new Date(),2));
                recordList.add(safetyBuildInfoRecordDO);
            }
        }

        //昨儿个离职的
        en.setChangeType("是");
        List<String> leaveList = safetyDepartChangeListService.getChangeEmpCodeList(en);
        if (CollectionUtils.isEmpty(leaveList)) {
            return;
        }
        List<SafetyEmployeeMessageDO> leaveStaffNo = safetyEmployeeMessageService.getListByStaffNo(list);
        for (SafetyEmployeeMessageDO safetyEmployeeMessageDO : leaveStaffNo) {
            SafetyBuildInfoRecordDO safetyBuildInfoRecordDO = new SafetyBuildInfoRecordDO();
            safetyBuildInfoRecordDO.setInfoConfirm("1");
            String mes = "保密负责人 "+safetyEmployeeMessageDO.getEmpName()+" 已离职，已自动取消其保密负责人权限，请及时调整处理";
            safetyBuildInfoRecordDO.setInfoContent(mes);
            safetyBuildInfoRecordDO.setInfoType("人员变动");
            safetyBuildInfoRecordDO.setInfoTime(DateUtils.format(new Date(),2));
            recordList.add(safetyBuildInfoRecordDO);
        }

        //查询离职人员对应的门禁信息
        List<SafetyBuildPowerDO> buildList = safetyBuildPowerService.getListByUserCode(leaveList);
        if (CollectionUtils.isEmpty(buildList)) {
            return;
        }

        Map<String, List<SafetyBuildPowerDO>> collect = buildList.stream().collect(Collectors.groupingBy(SafetyBuildPowerDO::getDutyDeptCode));

        //查询信息模板
        param.setId(7);
        List<SafetyBuildInfoConfigDO> leaveInfoList = safetyBuildInfoConfigService.query(param);
        if(!CollectionUtils.isEmpty(queryList)){
            String leaveMes = leaveInfoList.get(0).getInfoContent();
            for (Map.Entry<String, List<SafetyBuildPowerDO>> entry : collect.entrySet()) {
                //去除只有一个保密负责人的部门
                if (entry.getValue().size() < 2) {
                    SafetyBuildInfoRecordDO safetyBuildInfoRecordDO = new SafetyBuildInfoRecordDO();
                    safetyBuildInfoRecordDO.setInfoConfirm("1");
                    String mes = "保密负责人 "+entry.getValue().get(0).getEmployeeName()+"已离职，责任部门"+entry.getValue().get(0).getDutyDeptName()+"当前无保密负责人，请及时调整处理";
                    safetyBuildInfoRecordDO.setInfoContent(mes);
                    safetyBuildInfoRecordDO.setInfoType("人员变动");
                    safetyBuildInfoRecordDO.setInfoTime(DateUtils.format(new Date(),2));
                    recordList.add(safetyBuildInfoRecordDO);
                    continue;
                }

                //去除该部门下所有的保密负责人都离职的数据
                List<SafetyBuildPowerDO> value = entry.getValue();
                boolean allLeave = true;
                StringBuilder unLeaveCode = new StringBuilder();
                StringBuilder leaveName = new StringBuilder();
                for (SafetyBuildPowerDO build : value) {
                    String employeeCode = build.getEmployeeCode();
                    if (!leaveList.contains(employeeCode)) {
                        unLeaveCode.append(build.getEmployeeCode()).append(",");
                        allLeave = false;
                    } else {
                        leaveName.append(build.getEmployeeName()).append(",");
                    }
                }
                if (allLeave) {
                    continue;
                }

                //发消息
                unLeaveCode = new StringBuilder(unLeaveCode.substring(0, unLeaveCode.length() - 1));
                leaveName = new StringBuilder(Objects.requireNonNull(leaveName.toString()).substring(0, leaveName.length() - 1));
                String[] codeArray = unLeaveCode.toString().split(",");
                leaveMes = leaveMes.replace("{secretUser}", leaveName.toString());

                RestTemplate restTemplate = new RestTemplate();
                for (String code : codeArray) {
                    yxtRebotUtils.postForEntity(null, incomintRebotId, code, leaveMes, restTemplate);
                }
            }
        }

        if(!CollectionUtils.isEmpty(recordList)){
            safetyBuildInfoRecordService.batchSave(recordList);
        }
    }

    /**
     * 获取员工的变更记录
     * @param employeeCode 员工号
     * @return 返回记录
     */
    public SafetyDepartChangeListDO getChangeInfoByEmployeeCode(String employeeCode){
        if(StringUtils.isBlank(employeeCode)){
            return null;
        }
        DepartChangeListParam param = new DepartChangeListParam();
        param.setEmployeeCode(employeeCode);
        List<SafetyDepartChangeListDO> changeList = safetyDepartChangeListService.selectList(param);
        if(CollectionUtils.isEmpty(changeList)){
            return null;
        }
        return changeList.get(0);
    }
}
