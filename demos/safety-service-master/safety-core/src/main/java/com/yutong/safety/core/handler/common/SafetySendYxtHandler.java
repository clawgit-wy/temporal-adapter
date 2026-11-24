package com.yutong.safety.core.handler.common;


import com.google.common.collect.Lists;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.RedisUtil;
import com.yutong.safety.common.utils.YxtRebotUtils;
import com.yutong.safety.core.db.entity.SafetyGeneralConfigDO;
import com.yutong.safety.core.db.entity.SafetyGeneralConfigDOExample;
import com.yutong.safety.core.db.entity.build.vo.BuildAutoDeptVO;
import com.yutong.safety.core.db.entity.emp.SafetyDepartChangeListDO;
import com.yutong.safety.core.db.mapper.SafetyGeneralConfigDOMapper;
import com.yutong.safety.core.dto.visitor.SafetyVisitorAuthorizationDto;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.service.build.SafetyBuildInfoConfigService;
import com.yutong.safety.core.service.build.SafetyBuildManageProcessService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 宇信通消息handler
 * 2025-12-29 16:15
 */
@Component
public class SafetySendYxtHandler {

    //安保小助手机器人ID
    @Value("${iam.retention.robotId}")
    private String incomintRebotId;

    @Value("${an.eip.todo.backUrl}")
    private String anEipTodoUrl;

    @Resource
    private YxtRebotUtils yxtRebotUtils;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private SafetyBuildManageProcessService safetyBuildManageProcessService;

    @Resource
    private SafetyBuildInfoConfigService safetyBuildInfoConfigService;

    @Resource
    private SafetyBaseHandler safetyBaseHandler;

    @Resource
    private SafetyGeneralConfigDOMapper safetyGeneralConfigDOMapper;

    /**
     * 楼宇门禁申请流程 - 保密负责人发送宇信通消息
     * 内容：您的门禁权限有调整，点击查看个人门禁授权情况。如有疑问请联系本部门门禁管理员{secretUser}
     *
     * @param empCode            人员
     * @param createEmployeeName 门禁管理员
     */
    public void sendYxtMsgBySecrecyManager(Set<String> empCode, String createEmployeeName) {

        LogUtils.info("sendYxtMsgBySecrecyManager- 楼宇门禁申请流程，接收人={}", empCode);
        Set<String> lastEmpNoList = safetyBaseHandler.excludeLeader(empCode);
        LogUtils.info("sendYxtMsgBySecrecyManager- 楼宇门禁申请流程，排除中高层接收人={}", lastEmpNoList);
        if (CollectionUtils.isEmpty(lastEmpNoList)) {
            return;
        }
        String content = safetyBuildInfoConfigService.queryById(2);
        if (StringUtils.isBlank(content)) {
            return;
        }
        String msg = content.replace("{secretUser}", createEmployeeName).replace("{buildProcessLink}", anEipTodoUrl + "personAccessDetail?creator={empCode}");
        RestTemplate restTemplate = new RestTemplate();
        lastEmpNoList.forEach((key) -> yxtRebotUtils.postForEntity(null, incomintRebotId, key, msg.replace("{empCode}", key), restTemplate));
    }

    /**
     * 楼宇门禁申请流程 - 个人发送宇信通消息
     *
     * @param employeeNo 个人员工号
     * @param id         流程的数据ID
     * @param bpmFlowId  流程ID
     */
    public void sendYxtMsgByPerson(String employeeNo, String id, String bpmFlowId) {
        // 链接
        String bpmPreUrl = safetyBuildManageProcessService.getBpmPreUrl("safety_build_process");
        String processLink = String.format("%s&formInstanceId=%s&processId=%s", bpmPreUrl, id, bpmFlowId);
        // 消息主体
        String content = safetyBuildInfoConfigService.queryById(1);
        if (StringUtils.isBlank(content)) {
            return;
        }
        // 消息内容
        String msg = content.replace("{buildProcessLink}", processLink);
        // 消息发送
        yxtRebotUtils.postForEntity(null, incomintRebotId, employeeNo, msg, new RestTemplate());
    }

    /**
     * 组织变更宇信通消息发送
     *
     * @param personMsg  个人的消息体
     * @param managerMsg 组织的消息体
     * @param curValue   组织对应的信息
     * @param key        组织
     */
    public void sendYxtMsgByAutoDept(String personMsg, String managerMsg, List<BuildAutoDeptVO> curValue, List<SafetyDepartChangeListDO> value, String key) {
        if (StringUtils.isBlank(personMsg)) {
            return;
        }
        try {
            // 必要额的参数
            StringBuilder deviceNameBuilder = new StringBuilder();
            Set<String> empCodeSet = new HashSet<>();
            Set<String> empNameSet = new HashSet<>();

            for (BuildAutoDeptVO buildAutoDeptVO : curValue) {
                if (StringUtils.isNotBlank(buildAutoDeptVO.getDeviceName())) {
                    deviceNameBuilder.append(buildAutoDeptVO.getDeviceName()).append("、");
                }
                if (StringUtils.isNotBlank(buildAutoDeptVO.getCreateEmployeeNo())) {
                    empCodeSet.add(buildAutoDeptVO.getCreateEmployeeNo());
                }
                if (StringUtils.isNotBlank(buildAutoDeptVO.getCreateEmployeeName())) {
                    empNameSet.add(buildAutoDeptVO.getCreateEmployeeName());
                }
            }
            String deviceName = deviceNameBuilder.toString();
            if (!deviceName.isEmpty() && deviceName.endsWith("、")) {
                deviceName = deviceName.substring(0, deviceName.length() - 1);
            }
            // 排除中高层员工
            Set<String> emoCodes = value.stream().map(SafetyDepartChangeListDO::getEmployeeCode).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
            Set<String> lastEmpNoList = safetyBaseHandler.excludeLeader(emoCodes);

            //消息体
            String msg = personMsg
                    .replace("{grantBuild}", deviceName)
                    .replace("{secretUser}", String.join("、", empNameSet));

            //获取变动的员工名
            RestTemplate restTemplate = new RestTemplate();
            for (SafetyDepartChangeListDO li : value) {
                String employeeCode = li.getEmployeeCode();
                String employeeName = li.getEmployeeName();
                // 通过redis判断今天是否已经发过消息， 发过的直接结束
                String redisKey = Constants.REDIS_AUTO_DEPT_YXT_MSG_PRE + "empInfos" + key + employeeCode;
                Object redisValue = redisUtil.get(redisKey);
                if (redisValue != null) {
                    continue;
                }
                if (lastEmpNoList.contains(employeeCode)) {
                    // 个人
                    yxtRebotUtils.postForEntity(null, incomintRebotId, employeeCode, msg, restTemplate);
                }

                // 保存redis
                redisUtil.set(redisKey, (key + employeeCode), DateUtils.getRemainderMinute());

                if (StringUtils.isBlank(managerMsg) || CollectionUtils.isEmpty(empCodeSet)) {
                    continue;
                }
                //组织
                String secretMes = managerMsg
                        .replace("{person}", employeeName)
                        .replace("{grantBuild}", deviceName);
                empCodeSet.forEach(ln -> yxtRebotUtils.postForEntity(null, incomintRebotId, ln, secretMes, restTemplate));
            }
        } catch (Exception e) {
            LogUtils.info("组织部门新进人员自动下发门禁，发消息的时候报错={}", e.getMessage());
        }
    }

    /**
     * 定时删除员工出门证权限时，发送的宇信通消息
     * @param empNo 员工号
     */
    public void sendYxtMsgByGoodsOut(String empNo) {
        // 消息内容
        String msg = "您好，因您的组织发生变更，系统自动关闭了您的出门证审核权限。已为您自动触发eip流程（草稿状态），若您仍有出门证审核需求，可在我的草稿中重新提交流程申请授权，如无需要可进行废弃处理。";
        // 消息发送
        yxtRebotUtils.postForEntity(null, incomintRebotId, empNo, msg, new RestTemplate());
    }


    /**
     * 访客授权到期前提醒
     * @param list 待提醒的人
     */
    public void sendYxtMsgByVisitor(List<SafetyVisitorAuthorizationDto> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        SafetyGeneralConfigDOExample example = new SafetyGeneralConfigDOExample();
        SafetyGeneralConfigDOExample.Criteria criteria = example.createCriteria();
        criteria.andGeneralShowEqualTo(0);
        criteria.andGeneralTypeIn(Lists.newArrayList("expireBsq", "expireSq"));
        List<SafetyGeneralConfigDO> configList = safetyGeneralConfigDOMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(configList)) {
            return;
        }

        String expireBsq = "";
        String expireSq = "";
        for (SafetyGeneralConfigDO safetyGeneralConfigDO : configList) {
            String generalType = safetyGeneralConfigDO.getGeneralType();
            if (StringUtils.isNotBlank(generalType) && "expireBsq".equals(generalType)) {
                expireBsq = safetyGeneralConfigDO.getContent();
            }
            if (StringUtils.isNotBlank(generalType) && "expireSq".equals(generalType)) {
                expireSq = safetyGeneralConfigDO.getContent();
            }
        }

        Map<Integer, List<SafetyVisitorAuthorizationDto>> dataMap = list.stream().filter(li -> StringUtils.isNotBlank(li.getGranteeNo()) && li.getAuthorizationApprovalId() != null).collect(Collectors.groupingBy(SafetyVisitorAuthorizationDto::getAuthorizationApprovalId));

        for (Map.Entry<Integer, List<SafetyVisitorAuthorizationDto>> entry : dataMap.entrySet()) {
            List<SafetyVisitorAuthorizationDto> v = entry.getValue();
            if (CollectionUtils.isEmpty(v)) {
                continue;
            }
            SafetyVisitorAuthorizationDto dto = v.get(0);

            // 被授权人
            if (StringUtils.isBlank(expireBsq)) {
                continue;
            }
            for (SafetyVisitorAuthorizationDto li : v) {

                String msg = expireBsq.replace("{visitedNo}", dto.getGrantorNo())
                        .replace("{visitedName}", dto.getGrantorName())
                        .replace("{link}", anEipTodoUrl + "registration/visitorApprovalApply");

                // 被授权人
                yxtRebotUtils.postForEntity(null, incomintRebotId, li.getGranteeNo(), msg, new RestTemplate());
            }

            // 授权人
            if (StringUtils.isBlank(expireSq)) {
                continue;
            }
            String collect = v.stream().map(li -> li.getGranteeName() + "(" + li.getGranteeNo() + ")").collect(Collectors.joining(","));
            String msg = expireSq.replace("{visitedInfo}", collect)
                    .replace("{link}", anEipTodoUrl + "registration/visitorApprovalApply");
            yxtRebotUtils.postForEntity(null, incomintRebotId, dto.getGrantorNo(), msg, new RestTemplate());
        }
    }

    /**
     * 访客授权生效提醒
     * @param dataList 待提醒的数据
     */
    public void sendYxtMsgByAgreeVisitor(List<SafetyVisitorAuthorizationDto> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        SafetyGeneralConfigDOExample example = new SafetyGeneralConfigDOExample();
        SafetyGeneralConfigDOExample.Criteria criteria = example.createCriteria();
        criteria.andGeneralShowEqualTo(0);
        criteria.andGeneralTypeIn(Lists.newArrayList("assertBsq", "assertSq"));
        List<SafetyGeneralConfigDO> configList = safetyGeneralConfigDOMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(configList)) {
            return;
        }

        String assertBsq = "";
        String assertSq = "";
        for (SafetyGeneralConfigDO safetyGeneralConfigDO : configList) {
            String generalType = safetyGeneralConfigDO.getGeneralType();
            if (StringUtils.isNotBlank(generalType) && "assertBsq".equals(generalType)) {
                assertBsq = safetyGeneralConfigDO.getContent();
            }
            if (StringUtils.isNotBlank(generalType) && "assertSq".equals(generalType)) {
                assertSq = safetyGeneralConfigDO.getContent();
            }
        }

        SafetyVisitorAuthorizationDto info = dataList.get(0);

        // 授权人
        if (StringUtils.isNotBlank(assertSq)) {
            String collect = dataList.stream().map(li -> li.getGranteeName() + "(" + li.getGranteeNo() + ")").collect(Collectors.joining(","));
            String msg = assertSq.replace("{visitedInfo}", collect)
                    .replace("{startTime}", info.getAuthorizationBeginTime())
                    .replace("{endTime}", info.getAuthorizationEndTime());
            yxtRebotUtils.postForEntity(null, incomintRebotId, info.getGrantorNo(), msg, new RestTemplate());
        }

        // 被授权人
        if (StringUtils.isNotBlank(assertBsq)) {
            for (SafetyVisitorAuthorizationDto dto : dataList) {
                String msg = assertBsq.replace("{visitedNo}", dto.getGrantorNo())
                        .replace("{visitedName}", dto.getGrantorName())
                        .replace("{startTime}", info.getAuthorizationBeginTime())
                        .replace("{endTime}", info.getAuthorizationEndTime());

                // 被授权人
                yxtRebotUtils.postForEntity(null, incomintRebotId, info.getGranteeNo(), msg, new RestTemplate());
            }
        }
    }

    /**
     * 访客授权组织变更提醒
     * @param dtoList 数据集
     * @param type 消息类型
     */
    public void sendYxtMsgByPositionChangeOfVisitor(List<SafetyVisitorAuthorizationDto> dtoList, String type) {
        // TODO: 2026/5/25 需要确认是否需要区分流程和普通数据
        SafetyGeneralConfigDOExample example = new SafetyGeneralConfigDOExample();
        SafetyGeneralConfigDOExample.Criteria criteria = example.createCriteria();
        criteria.andGeneralShowEqualTo(0);
        criteria.andGeneralTypeEqualTo(type);
        List<SafetyGeneralConfigDO> configList = safetyGeneralConfigDOMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(configList)) {
            return;
        }
        String content = configList.get(0).getContent();
        if (StringUtils.isBlank(content)) {
            return;
        }

        dtoList.forEach(li -> {
            if (StringUtils.isBlank(li.getGranteeNo())) {
                return;
            }
            String msg = content.replace("visitedNo", li.getGranteeNo())
                    .replace("visitedName", li.getGranteeName())
                    .replace("link", anEipTodoUrl + "registration/visitorApprovalApply");
            // 被授权人
            yxtRebotUtils.postForEntity(null, incomintRebotId, li.getGranteeNo(), msg, new RestTemplate());
        });
    }
}
