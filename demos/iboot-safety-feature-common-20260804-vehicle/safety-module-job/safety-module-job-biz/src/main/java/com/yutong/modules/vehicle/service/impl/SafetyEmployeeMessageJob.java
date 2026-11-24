package com.yutong.modules.vehicle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.yutong.modules.common.entity.eip.user.EipUser;
import com.yutong.modules.common.entity.eip.user.EipUserResponse;
import com.yutong.modules.common.entity.eip.user.EipUserResult;
import com.yutong.modules.common.factory.ThirdApiFactory;
import com.yutong.modules.common.utils.DateUtils;
import com.yutong.modules.entity.SafetyEmployeeMessage;
import com.yutong.modules.kafka.user.EmployeeChangeMessage;
import com.yutong.modules.kafka.user.EmployeeKafkaProducer;
import com.yutong.modules.vehicle.handle.SafetyPhotoService;
import com.yutong.modules.vehicle.mapper.SafetyEmployeeMessageJobMapper;
import com.yutong.modules.vehicle.service.ISafetyEmployeeMessageJob;
import com.yutong.modules.vehicle.service.ISafetyVisitorAuthorizationApprovalJob;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: 人员信息表
 * @Author: jeecg-boot
 * @Date:   2026-08-10
 * @Version: V1.0
 */
@Slf4j
@Service
public class SafetyEmployeeMessageJob extends ServiceImpl<SafetyEmployeeMessageJobMapper, SafetyEmployeeMessage> implements ISafetyEmployeeMessageJob {

    @Autowired
    private ThirdApiFactory thirdApiFactory;

    @Autowired
    private SafetyPhotoService safetyPhotoService;

    @Autowired
    private ISafetyVisitorAuthorizationApprovalJob visitorAuthorizationApprovalJob;

    @Autowired
    private EmployeeKafkaProducer employeeKafkaProducer;

    /**
     * 职种白名单：不在该列表内的职种统一归一为“员工”
     */
    private static final List<String> JOB_TYPE_NAME = Lists.newArrayList(
            "高层管理", "科级管理", "部级管理", "处级管理", "室级管理", "班组级管理", "模块级管理", "院级管理");

    /**
     * 在职状态
     */
    private static final String STATUS_NORMAL = "NORMAL";

    /**
     * 离职状态
     */
    private static final String STATUS_DISABLED = "DISABLED";

    @Override
    public void tasksMessage(Boolean isAll, String dateTime) {
        if (StringUtils.isEmpty(dateTime)) {
            //前一天
            dateTime = DateUtils.format(DateUtils.addDays(new Date(),-1), 2);
        }
        String endTime = DateUtils.format(new Date(),2);

        //第一页
        EipUserResponse<EipUserResult> resp = queryUserPage(isAll, dateTime, endTime, 1);
        if(isValidResponse(resp)) return;
        taskInsertBlackNew(resp.getResult().getRecords());
        if(resp.getResult().getPages()<=1) return;

        //后续分页
        for (int pageNo = 2; pageNo <= resp.getResult().getPages(); pageNo++) {
            EipUserResponse<EipUserResult> pageResp = queryUserPage(isAll, dateTime, endTime, pageNo);
            if(isValidResponse(pageResp)) break;
            taskInsertBlackNew(pageResp.getResult().getRecords());
        }
    }

    /**
     * 查询指定页的人员同步数据
     * @param isAll 是否全量
     * @param dateTime 开始时间
     * @param endTime 结束时间
     * @param pageNo 页码
     */
    private EipUserResponse<EipUserResult> queryUserPage(Boolean isAll, String dateTime, String endTime, int pageNo){
        String params = String.format("isAll=%s&date=%s&endTime=%s&pageNo=%s", isAll, dateTime, endTime, pageNo);
        String result = thirdApiFactory.pushUserSyncInfo(params);
        return JsonUtils.parseObject(result,new TypeReference<EipUserResponse<EipUserResult>>() {});
    }

    /**
     * 校验返回结果是否有效
     * @param resp 返回结果
     */
    private boolean isValidResponse(EipUserResponse<EipUserResult> resp){
        return resp == null || !resp.isSuccess()
                || resp.getResult() == null
                || resp.getResult().getRecords() == null
                || resp.getResult().getRecords().isEmpty();
    }



    /**
     * 插入数据：将 EIP 人员转换台账实体后批量 upsert，并回补创建时间
     * @param users EIP 人员信息
     */
    private void taskInsertBlackNew(List<EipUser> users){
        List<SafetyEmployeeMessage> insertList = Lists.newArrayList();
        for (EipUser user : users) {
            try {
                insertList.add(convertToMessage(user));
            } catch (Exception e) {
                log.error("员工{}整理数据异常:{}", user == null ? null : user.getWorkNo(), e.getMessage(), e);
            }
        }
        if (insertList.isEmpty()) {
            return;
        }
        // 判断组织是否变更：离职/调岗人员触发访客授权审批的注销与提醒（须在批量新增/更新之前执行，以便与库中现有岗位信息比对）
        Map<String, String> changeMap = positionChange(insertList);
        // 批量新增/更新
        baseMapper.batchUpdateNew(insertList);
        // 更新创建时间：在职（NORMAL）且创建时间为空的员工
        List<String> empNos = insertList.stream().map(SafetyEmployeeMessage::getEmpNo).collect(Collectors.toList());
        updateCreateTime(empNos);
        // 数据更新完成后，发送离职/岗位变更 kafka 消息通知
        sendEmployeeChangeMessage(insertList, changeMap);
    }

    /**
     * 组织变更处理：整理离职及岗位变动人员，触发访客授权审批的注销与提醒
     * 说明：须在台账批量新增/更新之前执行，以便与库中现有岗位信息进行比对
     *
     * @param insertList 本次同步整理的员工台账数据
     * @return 变更人员及变更类型映射（员工号 -> 变更类型），无变更或识别异常时为空集合
     */
    private Map<String, String> positionChange(List<SafetyEmployeeMessage> insertList) {
        Map<String, String> changeMap = new HashMap<>();
        try {
            // 离职的人员
            Set<String> empNoList = insertList.stream()
                    .filter(li -> StringUtils.equals(li.getStatus(), STATUS_DISABLED))
                    .map(SafetyEmployeeMessage::getEmpNo)
                    .collect(Collectors.toSet());
            empNoList.forEach(empNo -> changeMap.put(empNo, EmployeeChangeMessage.CHANGE_TYPE_DISABLED));

            // 整理出组织变更的数据
            Map<String, String> dataMap = insertList.stream()
                    .filter(li -> StringUtils.isNotBlank(li.getPositionName()))
                    .collect(Collectors.toMap(SafetyEmployeeMessage::getEmpNo, SafetyEmployeeMessage::getPositionName, (existing, replacement) -> existing));
            Set<String> sets = new HashSet<>();
            sets.addAll(dataMap.keySet());
            sets.addAll(empNoList);
            if (CollectionUtils.isEmpty(sets)) {
                return changeMap;
            }
            List<SafetyEmployeeMessage> listByStaffNo = getListByStaffNo(new ArrayList<>(sets));
            if (!CollectionUtils.isEmpty(listByStaffNo)) {
                // 库中岗位与同步岗位不一致的为调岗人员
                List<String> empNos = listByStaffNo.stream()
                        .filter(li -> !StringUtils.equals(dataMap.get(li.getEmpNo()), li.getPositionName()))
                        .map(SafetyEmployeeMessage::getEmpNo)
                        .collect(Collectors.toList());
                empNoList.addAll(empNos);
                // 离职优先，不重复标记为岗位变更
                empNos.forEach(empNo -> changeMap.putIfAbsent(empNo, EmployeeChangeMessage.CHANGE_TYPE_POSITION_CHANGE));
                visitorAuthorizationApprovalJob.positionChange(empNoList);
            }
        } catch (Exception e) {
            log.error("获取组织变更数据异常:", e);
            return Collections.emptyMap();
        }
        return changeMap;
    }

    /**
     * 发送人员离职/岗位变更 kafka 消息通知
     * 说明：在台账批量新增/更新完成之后发送，消息体使用同步后的最新台账信息
     *
     * @param insertList 本次同步整理的员工台账数据
     * @param changeMap  变更人员及变更类型映射（员工号 -> 变更类型）
     */
    private void sendEmployeeChangeMessage(List<SafetyEmployeeMessage> insertList, Map<String, String> changeMap) {
        if (CollectionUtils.isEmpty(changeMap)) {
            return;
        }
        for (SafetyEmployeeMessage employeeMessage : insertList) {
            String changeType = changeMap.get(employeeMessage.getEmpNo());
            if (StringUtils.isBlank(changeType)) {
                continue;
            }
            EmployeeChangeMessage message = new EmployeeChangeMessage();
            message.setChangeType(changeType);
            message.setEmployeeMessage(employeeMessage);
            employeeKafkaProducer.sendEmployeeChange(message);
        }
    }

    /**
     * 根据员工号集合获取在职员工信息台账
     *
     * @param staffNoList 员工号集合
     * @return 在职员工信息台账
     */
    private List<SafetyEmployeeMessage> getListByStaffNo(List<String> staffNoList) {
        if (CollectionUtils.isEmpty(staffNoList)) {
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<SafetyEmployeeMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SafetyEmployeeMessage::getStatus, STATUS_NORMAL)
                .in(SafetyEmployeeMessage::getEmpNo, staffNoList);
        return this.list(queryWrapper);
    }

    /**
     * EIP 人员信息转换为员工台账实体
     * @param user EIP 人员信息
     * @return 员工台账实体
     */
    private SafetyEmployeeMessage convertToMessage(EipUser user){
        SafetyEmployeeMessage message = new SafetyEmployeeMessage();
        message.setCreateTime(new Date());
        message.setUpdateTime(new Date());
        message.setEmpNo(user.getWorkNo());
        message.setEmpName(user.getName());
        String email = user.getEmail();
        message.setEmail(email);
        if (StringUtils.isNotEmpty(email)) {
            message.setEmpLogin(email.split("@")[0]);
        }
        message.setPhoneNo(user.getPhone());
        message.setUserType(user.getUserType());
        message.setPositionCode(user.getPositionCode());
        message.setPositionName(user.getPositionName());
        // 岗位类别归一
        String positionType = user.getPositionType();
        if (StringUtils.isNotEmpty(positionType)) {
            if (positionType.startsWith("直接")) {
                positionType = "直接岗位";
            } else if (positionType.startsWith("间接")) {
                positionType = "间接岗位";
            } else if (positionType.startsWith("外包")) {
                positionType = "外包岗位";
            }
            message.setPositionType(positionType);
        }
        message.setOrgCode(user.getOrgCode());
        message.setOrgName(user.getOrgName());
        message.setOrgCodePath(user.getOrgCodePath());
        message.setOrgNamePath(user.getOrgNamePath());
        message.setParentCode(user.getParentCode());
        message.setStatus(user.getStatus());
        message.setEmpType(user.getEmpType());
        message.setEmpTypeCode(user.getEmpTypeCode());
        // 职种归一：不在白名单内的统一为“员工”
        String jobTypeName = user.getJobTypeName();
        if (StringUtils.isNotBlank(jobTypeName) && !JOB_TYPE_NAME.contains(jobTypeName)) {
            jobTypeName = "员工";
        }
        message.setJobTypeName(jobTypeName);
        // 照片：优先生活照，为空则取标准照（新接口返回的已是完整 URL）
        String photo = safetyPhotoService.getPhotoUrl(user.getLifePhoto());
        if (StringUtils.isBlank(photo)) {
            photo =  safetyPhotoService.getPhotoUrl(user.getPhoto());
        }
        message.setPhoto(photo);
        // 性别：MALE-0，其余-1
        message.setSex("MALE".equals(user.getGender()) ? "0" : "1");
        return message;
    }

    /**
     * 回补创建时间：在职（NORMAL）且创建时间为空的员工，将创建时间置为当前时间
     * @param empNos 员工号集合
     */
    private void updateCreateTime(List<String> empNos){
        if (empNos == null || empNos.isEmpty()) {
            return;
        }
        LambdaUpdateWrapper<SafetyEmployeeMessage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(SafetyEmployeeMessage::getCreateTime, new Date())
                .eq(SafetyEmployeeMessage::getStatus, STATUS_NORMAL)
                .in(SafetyEmployeeMessage::getEmpNo, empNos)
                .isNull(SafetyEmployeeMessage::getCreateTime);
        baseMapper.update(null, wrapper);
    }
}
