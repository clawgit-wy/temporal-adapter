package com.yutong.safety.publicKafka;

import com.yutong.framework.ldapsync.LdapEvent;
import com.yutong.framework.ldapsync.bean.PersonEvent;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.YxtRebotUtils;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetyGroupInfoDO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildInfoRecordDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDO;
import com.yutong.safety.core.db.entity.build.param.BuildPowerListParam;
import com.yutong.safety.core.db.entity.emp.SafetyDepartChangeListDO;
import com.yutong.safety.core.db.entity.emp.SafetyEmployeeOrgChangeDO;
import com.yutong.safety.core.db.entity.emp.param.SafetyEmployeeOrgChangeQueryParam;
import com.yutong.safety.core.handler.SafetyEmployeeMessageHandler;
import com.yutong.safety.core.handler.emp.SafetyDepartChangeListHandler;
import com.yutong.safety.core.service.SafetyGroupService;
import com.yutong.safety.core.service.SafetySysUserService;
import com.yutong.safety.core.service.build.SafetyBuildInfoRecordService;
import com.yutong.safety.core.service.build.SafetyBuildPowerService;
import com.yutong.safety.core.service.emp.SafetyEmployeeOrgChangeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class KafkaMessageListener {
    @Autowired
    SafetyDepartChangeListHandler safetyDepartChangeListHandler;
    @Autowired
    SafetyEmployeeMessageHandler safetyEmployeeMessageHandler;
    @Autowired
    SafetyBuildPowerService safetyBuildPowerService;
    @Autowired
    YxtRebotUtils yxtRebotUtils;
    @Autowired
    private SafetySysUserService safetySysUserService;
    @Autowired
    private SafetyBuildInfoRecordService safetyBuildInfoRecordService;
    @Autowired
    private SafetyGroupService safetyGroupService;

    @Resource
    private SafetyEmployeeOrgChangeService safetyEmployeeOrgChangeService;

    @Value("${kafka.topic}")
    public String kafkaTopic;

    @Value("${iam.retention.robotId}")
    private String incomintRebotId;

    private static final String DELETED = "0";

//    @KafkaListener(topics = "#{kafkaMessageListener.kafkaTopic}")
    public void listen(ConsumerRecord<String, LdapEvent> data) {
        try {
            LogUtils.info("ldap:{}", data);
            LogUtils.info("ldapValue:{}", data.value());
            LdapEvent ldapEvent = data.value();
            LogUtils.info("收到LDAP同步消息, type = {}, domain = {}", ldapEvent.getType().name(), ldapEvent.getDomain().name());
            //只处理员工信息
            if (!ldapEvent.isPerson()) {
                return;
            }
            PersonEvent personEvent = ldapEvent.getPersonEvent();
            if (personEvent == null) {
                LogUtils.info("员工personEvent为null");
                return;
            }
            String uid = personEvent.getUid();
            if (StringUtils.isBlank(uid)) {
                LogUtils.info("员工uid不存在");
                return;
            }

            SafetyDepartChangeListDO departChange = new SafetyDepartChangeListDO();
            String currentTime = DateUtils.format(new Date(), 1);
            String updateTime = DateUtils.format(new Date(), 2);
            departChange.setCreateTime(currentTime);
            departChange.setUpdateTime(updateTime);
            departChange.setChangeType("否");

            //查询现有的人员数据
            SafetyEmployeeMessageDO empInfo = safetyEmployeeMessageHandler.getEmpById(uid);
            departChange.setEmployeeCode(uid);
            departChange.setEmployeeName(personEvent.getCn());

            String oldOrgCode = empInfo != null ? empInfo.getOrgCode() : "";
            String oldOrgName = empInfo != null ? empInfo.getOrgName() : "";
            departChange.setOldDeptCode(oldOrgCode);
            departChange.setOldDeptName(oldOrgName);

            //职位变动
            String newOrgCode = personEvent.getDepartNumber();
            LogUtils.info("员工newOrgCode={}",newOrgCode);
            int type =2;
            if (ldapEvent.isAdd() || ldapEvent.isModdn() || ldapEvent.isModrdn() || ldapEvent.isModify()) {
                //同步过来的数据可能已经存在 查看员工状态是否是已离职状态
                //0是删除，1是正常，2是禁用
                if (DELETED.equals(personEvent.getIsActive())) {
                    departChange.setChangeType("是");
                    type = 1;
                }
                else{
                    //前后都为空或前后相等不记录
                    if ((StringUtils.isBlank(oldOrgCode) && StringUtils.isBlank(newOrgCode))
                            ||oldOrgCode.equals(newOrgCode)) {
                        return;
                    }
                    departChange.setNewDeptCode(newOrgCode);
                }
            }else if (ldapEvent.isDelete()) {
                departChange.setChangeType("是");
                type = 1;
            } else {
                // 前后都为空或前后相等不记录
                if (Objects.equals(oldOrgCode, newOrgCode)) {
                    return;
                }
                departChange.setNewDeptCode(newOrgCode);
                type = 2;
            }
            //在职人员的新组织为空时不保存记录
            if("否".equals(departChange.getChangeType()) && StringUtils.isBlank(newOrgCode)){
                LogUtils.info("在职员工{},新组织为空，不更新",uid);
                return;
            }
            SafetyGroupInfoDO groupInfo = safetyGroupService.getGroupByCode(newOrgCode);
            if(groupInfo == null || !"Y".equals(groupInfo.getStatus())){
                LogUtils.info("在职员工{},新组织{} 无效，不更新",uid,newOrgCode);
                return;
            }

            //获取该员工已存在的变更记录，判断是否有信息变化
            SafetyDepartChangeListDO oldChange = safetyDepartChangeListHandler.getChangeInfoByEmployeeCode(uid);
            if(oldChange != null){
                String changeType = departChange.getChangeType();
                String newDeptCode = departChange.getNewDeptCode();
                //已离职状态不再重复更新
                if("是".equals(changeType) && "是".equals(oldChange.getChangeType())){
                    LogUtils.info("离职员工{},已存在变更记录，不再处理",uid);
                    return;
                }
                //在岗状态，若新组织数据一致，也不再重复更新
                if("否".equals(changeType) && Objects.equals(newDeptCode, oldChange.getNewDeptCode())){
                    LogUtils.info("在职员工{},新组织{}，已存在变更记录，不再处理",uid,newDeptCode);
                    return;
                }
            }
            departChange.setNewDeptName(groupInfo.getName());
            // 发送宇信通消息
            sendYuXinTongMsg(personEvent.getUid(), type);
            safetyDepartChangeListHandler.save(departChange);
        } catch (Exception e) {
            LogUtils.error("处理同步消息出错", e);
        }
    }

    /**
     * 发送消息(宇信通) 是保密负责人，才发送对应的消息通知
     * 消息发送给同部门的其他保密负责人和门禁系统管理员
     *
     * @param empNo 员工工号
     * @param type 类型， type=1 是离职消息； type=2 是组织变更消息
     */
    private void sendYuXinTongMsg(String empNo, int type) {
        // 1.判断变动人员是否是保密负责人
        BuildPowerListParam listParam = new BuildPowerListParam();
        listParam.setEmpNo(empNo);
        List<SafetyBuildPowerDO> powerList = safetyBuildPowerService.selectList(listParam);
        if (CollectionUtils.isEmpty(powerList)) return;
        SafetyBuildPowerDO safetyBuildPowerDO = powerList.get(0);

        // 2.获取当前保密负责人的责任部门
        Set<String> depthSet = powerList.stream().map(SafetyBuildPowerDO::getDutyDeptCode).collect(Collectors.toSet());

        // 3.获取同部门的保密负责人
        listParam = new BuildPowerListParam();
        listParam.setDutyDeptCodeList(new ArrayList<>(depthSet));
        powerList = safetyBuildPowerService.selectList(listParam);
        Set<String> empNoList = new HashSet<>(); // 员工工号
        if (!CollectionUtils.isEmpty(powerList)) {
            empNoList = powerList.stream().filter(li -> !li.getEmployeeCode().equals(empNo)).map(SafetyBuildPowerDO::getEmployeeCode).collect(Collectors.toSet());
        }

        // 4.获取门禁管理员的人员信息
        UserMessageListParam param = new UserMessageListParam();
        param.setRoleName("门禁系统管理员");
        List<SafetySysUserDO> list = safetySysUserService.getList(param);
        if (!CollectionUtils.isEmpty(list)) {
            empNoList.addAll(list.stream().map(SafetySysUserDO::getEmpNo).collect(Collectors.toSet()));
        }

        // 5.发送消息
        if (CollectionUtils.isEmpty(empNoList)) return;
        if (type == 1) {
            String resignMsg = "保密负责人"+ safetyBuildPowerDO.getEmployeeName() +"已离职，责任部门"+ safetyBuildPowerDO.getDutyDeptName() +"当前无保密负责人，请及时调整处理";
            saveInfoRecord(resignMsg);
            empNoList.forEach(li -> yxtRebotUtils.postForEntity(null, incomintRebotId, li, resignMsg, new RestTemplate()));
        } else {
            String changeMsg = "保密负责人"+ safetyBuildPowerDO.getEmployeeName() +"组织变更，请及时跟进处理";
            saveInfoRecord(changeMsg);
            empNoList.forEach(li -> yxtRebotUtils.postForEntity(null, incomintRebotId, li, changeMsg, new RestTemplate()));
        }
    }

    /**
     * 记录人员变动记录信息
     *
     * @param msg 消息
     */
    private void saveInfoRecord(String msg) {
        SafetyBuildInfoRecordDO info = new SafetyBuildInfoRecordDO();
        info.setInfoConfirm("1");
        info.setInfoContent(msg);
        info.setInfoType("人员变动");
        info.setInfoTime(DateUtils.format(new Date(),2));
        safetyBuildInfoRecordService.save(info);
    }

    @KafkaListener(topics = "#{kafkaMessageListener.kafkaTopic}")
    public void listenForOrg(ConsumerRecord<String, LdapEvent> data) {
        try {
            LogUtils.info("ldap:{}", data);
            LogUtils.info("ldapValue:{}", data.value());
            LdapEvent ldapEvent = data.value();
            LogUtils.info("收到LDAP同步消息, type = {}, domain = {}", ldapEvent.getType().name(), ldapEvent.getDomain().name());
            //只处理员工信息
            if (!ldapEvent.isPerson()) {
                return;
            }
            PersonEvent personEvent = ldapEvent.getPersonEvent();
            if (personEvent == null) {
                LogUtils.info("员工personEvent为null");
                return;
            }
            String uid = personEvent.getUid();
            if (StringUtils.isBlank(uid)) {
                LogUtils.info("员工uid不存在");
                return;
            }

            SafetyDepartChangeListDO departChange = new SafetyDepartChangeListDO();
            String currentTime = DateUtils.format(new Date(), 1);
            String updateTime = DateUtils.format(new Date(), 2);
            departChange.setCreateTime(currentTime);
            departChange.setUpdateTime(updateTime);
            departChange.setChangeType("否");
            departChange.setEmployeeCode(uid);
            departChange.setEmployeeName(personEvent.getCn());

            final String oldOrgCode = getOldOrgCode(personEvent, DELETED.equals(personEvent.getIsActive()) || ldapEvent.isDelete());
            String oldOrgName = "";
            if (StringUtils.isNotBlank(oldOrgCode)) {
                SafetyGroupInfoDO groupInfo = safetyGroupService.getGroupByCode(oldOrgCode);
                if (groupInfo != null) {
                    oldOrgName = groupInfo.getName();
                }
            }
            departChange.setOldDeptCode(oldOrgCode);
            departChange.setOldDeptName(oldOrgName);

            //职位变动
            String newOrgCode = personEvent.getDepartNumber();
            LogUtils.info("员工newOrgCode={}",newOrgCode);
            int type =2;
            if (ldapEvent.isAdd() || ldapEvent.isModdn() || ldapEvent.isModrdn() || ldapEvent.isModify()) {
                //同步过来的数据可能已经存在 查看员工状态是否是已离职状态
                //0是删除，1是正常，2是禁用
                if (DELETED.equals(personEvent.getIsActive())) {
                    departChange.setChangeType("是");
                    type = 1;
                }
                else{
                    //前后都为空或前后相等不记录
                    if ((StringUtils.isBlank(oldOrgCode) && StringUtils.isBlank(newOrgCode))
                            ||oldOrgCode.equals(newOrgCode)) {
                        return;
                    }
                    departChange.setNewDeptCode(newOrgCode);
                }
            }else if (ldapEvent.isDelete()) {
                departChange.setChangeType("是");
                type = 1;
            } else {
                // 前后都为空或前后相等不记录
                if (Objects.equals(oldOrgCode, newOrgCode)) {
                    return;
                }
                departChange.setNewDeptCode(newOrgCode);
                type = 2;
            }
            LogUtils.info("2-departChange:{}", departChange);
            //在职人员的新组织为空时不保存记录
            if("否".equals(departChange.getChangeType()) && StringUtils.isBlank(newOrgCode)){
                LogUtils.info("在职员工{},新组织为空，不更新",uid);
                return;
            }
            SafetyGroupInfoDO groupInfo = safetyGroupService.getGroupByCode(newOrgCode);
            if(groupInfo == null || !"Y".equals(groupInfo.getStatus())){
                LogUtils.info("在职员工{},新组织{} 无效，不更新",uid,newOrgCode);
                return;
            }

            //获取该员工已存在的变更记录，判断是否有信息变化
            SafetyDepartChangeListDO oldChange = safetyDepartChangeListHandler.getChangeInfoByEmployeeCode(uid);
            LogUtils.info("SafetyDepartChangeListDO:{}", oldChange);
            if(oldChange != null){
                String changeType = departChange.getChangeType();
                String newDeptCode = departChange.getNewDeptCode();
                //已离职状态不再重复更新
                if("是".equals(changeType) && "是".equals(oldChange.getChangeType())){
                    LogUtils.info("离职员工{},已存在变更记录，不再处理",uid);
                    return;
                }
                //在岗状态，若新组织数据一致，也不再重复更新
                if("否".equals(changeType) && Objects.equals(newDeptCode, oldChange.getNewDeptCode())){
                    LogUtils.info("在职员工{},新组织{}，已存在变更记录，不再处理",uid,newDeptCode);
                    return;
                }
            }
            departChange.setNewDeptName(groupInfo.getName());
            LogUtils.info("3-departChange:{}", departChange);
            // 发送宇信通消息
            sendYuXinTongMsg(personEvent.getUid(), type);
            safetyDepartChangeListHandler.save(departChange);
        } catch (Exception e) {
            LogUtils.error("处理同步消息出错", e);
        }
    }


    /**
     * 获取组织信息，并保存进组织变更表
     * @param personEvent 人员信息
     * @param deleteType 离职状态， true 离职， false 正常
     */
    private String getOldOrgCode(PersonEvent personEvent, boolean deleteType) {
        String status = Constants.NORMAL;
        if (deleteType) {
            status = Constants.DISABLED;
        }
        LogUtils.info("状态:{}", status);
        SafetyEmployeeOrgChangeQueryParam queryParam = new SafetyEmployeeOrgChangeQueryParam();
        queryParam.setEmpNo(personEvent.getUid());
        queryParam.setStatus(status);
        SafetyEmployeeOrgChangeDO changeDO = safetyEmployeeOrgChangeService.selectOne(queryParam);
        LogUtils.info("changeDO:{}", changeDO);
        if (changeDO == null) {
            // 不存在 更新数据
            changeDO = new SafetyEmployeeOrgChangeDO();
            changeDO.setEmpNo(personEvent.getUid());
            changeDO.setOrgCode(personEvent.getDepartNumber());
            changeDO.setStatus(status);
            changeDO.setCreateTime(new Date());
            changeDO.setUpdateTime(new Date());
            safetyEmployeeOrgChangeService.saveInfo(changeDO);
            return "";
        } else {
            // 存在 判断组织信息
            final String oldOrgCode = changeDO.getOrgCode();
            final boolean bool = Objects.equals(oldOrgCode, personEvent.getDepartNumber());
            if (!bool) {
                // 不相等
                SafetyEmployeeOrgChangeDO updateInfo = new SafetyEmployeeOrgChangeDO();
                updateInfo.setOrgCode(personEvent.getDepartNumber());
                updateInfo.setId(changeDO.getId());
                updateInfo.setUpdateTime(new Date());
                safetyEmployeeOrgChangeService.updateByPrimaryKey(updateInfo);
            }
            LogUtils.info("oldOrgCode:{}", "oldOrgCode");
            return oldOrgCode;
        }
    }
}
