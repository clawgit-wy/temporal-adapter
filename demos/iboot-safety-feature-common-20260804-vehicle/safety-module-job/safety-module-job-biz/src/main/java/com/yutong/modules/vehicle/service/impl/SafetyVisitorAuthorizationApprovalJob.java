package com.yutong.modules.vehicle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yutong.modules.common.factory.ThirdApiFactory;
import com.yutong.modules.entity.SafetyGeneralConfig;
import com.yutong.modules.entity.SafetyVisitorAuthorizationApproval;
import com.yutong.modules.entity.SafetyVisitorAuthorizationApprovalDetail;
import com.yutong.modules.vehicle.mapper.SafetyVisitorAuthorizationApprovalJobMapper;
import com.yutong.modules.vehicle.service.ISafetyGeneralConfigJob;
import com.yutong.modules.vehicle.service.ISafetyVisitorAuthorizationApprovalDetailJob;
import com.yutong.modules.vehicle.service.ISafetyVisitorAuthorizationApprovalJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: 访客审批授权表
 * @Author: jeecg-boot
 * @Date:   2026-08-10
 * @Version: V1.0
 */
@Slf4j
@Service
public class SafetyVisitorAuthorizationApprovalJob extends ServiceImpl<SafetyVisitorAuthorizationApprovalJobMapper, SafetyVisitorAuthorizationApproval> implements ISafetyVisitorAuthorizationApprovalJob {

    /**
     * 消息模板类型：被授权人组织变更
     */
    private static final String MSG_TYPE_GRANTEE_POSITION_CHANGE = "postBsq";

    /**
     * 消息模板类型：授权人组织变更
     */
    private static final String MSG_TYPE_GRANTOR_POSITION_CHANGE = "postSq";

    /**
     * 数据状态：正常
     */
    private static final Integer STATUS_NORMAL = 1;

    /**
     * 删除标记：正常
     */
    private static final Integer DEL_FLAG_NORMAL = 0;

    /**
     * 通用配置：展示状态
     */
    private static final Integer GENERAL_SHOW_VALID = 0;

    /**
     * 访客授权审批前端页面
     */
    private static final String VISITOR_APPROVAL_APPLY_PAGE = "registration/visitorApprovalApply";

    //安保小助手机器人ID
    @Value("${ytsafety.yxt.robotId}")
    private String robotId;

    //前端门户地址
    @Value("${ytsafety.excel.exportUrl}")
    private String portalUrl;

    @Autowired
    private ISafetyVisitorAuthorizationApprovalDetailJob safetyVisitorAuthorizationApprovalDetailJob;

    @Autowired
    private ISafetyGeneralConfigJob safetyGeneralConfigJob;

    @Autowired
    private ThirdApiFactory thirdApiFactory;

    /**
     * 组织变更的人员处理：注销对应的访客授权审批数据，并发送宇信通提醒消息
     *
     * @param empNos 组织变更/离职的人员员工号集合
     */
    @Override
    public void positionChange(Set<String> empNos) {
        if (CollectionUtils.isEmpty(empNos)) {
            return;
        }
        List<String> empNoList = new ArrayList<>(empNos);

        // 被授权人组织变更：被授权人工号命中且状态为正常的明细数据
        LambdaQueryWrapper<SafetyVisitorAuthorizationApprovalDetail> granteeWrapper = new LambdaQueryWrapper<>();
        granteeWrapper.in(SafetyVisitorAuthorizationApprovalDetail::getGranteeNo, empNoList)
                .eq(SafetyVisitorAuthorizationApprovalDetail::getStatus, STATUS_NORMAL)
                .eq(SafetyVisitorAuthorizationApprovalDetail::getDelFlag, DEL_FLAG_NORMAL);
        List<SafetyVisitorAuthorizationApprovalDetail> granteeList = safetyVisitorAuthorizationApprovalDetailJob.list(granteeWrapper);
        if (!CollectionUtils.isEmpty(granteeList)) {
            // 存在数据，注销+发送消息
            List<Integer> ids = granteeList.stream().map(SafetyVisitorAuthorizationApprovalDetail::getId).collect(Collectors.toList());
            safetyVisitorAuthorizationApprovalDetailJob.cancelAuthorizationInfo(ids, "被授权人调岗/离职");
            sendYxtMsgByPositionChangeOfVisitor(granteeList, MSG_TYPE_GRANTEE_POSITION_CHANGE);
        }

        // 授权人组织变更：授权人工号命中的主表数据
        LambdaQueryWrapper<SafetyVisitorAuthorizationApproval> grantorWrapper = new LambdaQueryWrapper<>();
        grantorWrapper.in(SafetyVisitorAuthorizationApproval::getGrantorNo, empNoList)
                .eq(SafetyVisitorAuthorizationApproval::getDelFlag, DEL_FLAG_NORMAL);
        List<SafetyVisitorAuthorizationApproval> approvalList = this.list(grantorWrapper);
        if (CollectionUtils.isEmpty(approvalList)) {
            return;
        }

        // 授权人名下状态为正常的明细数据
        List<Integer> approvalIds = approvalList.stream().map(SafetyVisitorAuthorizationApproval::getId).collect(Collectors.toList());
        LambdaQueryWrapper<SafetyVisitorAuthorizationApprovalDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.in(SafetyVisitorAuthorizationApprovalDetail::getAuthorizationApprovalId, approvalIds)
                .eq(SafetyVisitorAuthorizationApprovalDetail::getStatus, STATUS_NORMAL)
                .eq(SafetyVisitorAuthorizationApprovalDetail::getDelFlag, DEL_FLAG_NORMAL);
        List<SafetyVisitorAuthorizationApprovalDetail> grantorDetailList = safetyVisitorAuthorizationApprovalDetailJob.list(detailWrapper);
        if (!CollectionUtils.isEmpty(grantorDetailList)) {
            // 存在数据，注销+发送消息
            List<Integer> ids = grantorDetailList.stream().map(SafetyVisitorAuthorizationApprovalDetail::getId).collect(Collectors.toList());
            safetyVisitorAuthorizationApprovalDetailJob.cancelAuthorizationInfo(ids, "授权人调岗/离职");
            sendYxtMsgByPositionChangeOfVisitor(grantorDetailList, MSG_TYPE_GRANTOR_POSITION_CHANGE);
        }
    }

    /**
     * 访客授权组织变更宇信通消息提醒：按消息模板类型查询通用配置，向被授权人发送提醒
     *
     * @param detailList 明细数据集
     * @param type       消息模板类型（通用配置表 general_type）
     */
    private void sendYxtMsgByPositionChangeOfVisitor(List<SafetyVisitorAuthorizationApprovalDetail> detailList, String type) {
        // 查询消息模板配置
        LambdaQueryWrapper<SafetyGeneralConfig> configWrapper = new LambdaQueryWrapper<>();
        configWrapper.eq(SafetyGeneralConfig::getGeneralShow, GENERAL_SHOW_VALID)
                .eq(SafetyGeneralConfig::getGeneralType, type)
                .last("limit 1");
        SafetyGeneralConfig config = safetyGeneralConfigJob.getOne(configWrapper, false);
        if (config == null || StringUtils.isBlank(config.getContent())) {
            return;
        }

        String content = config.getContent();
        for (SafetyVisitorAuthorizationApprovalDetail detail : detailList) {
            String granteeNo = detail.getGranteeNo();
            if (StringUtils.isBlank(granteeNo)) {
                continue;
            }
            String msg = content.replace("visitedNo", granteeNo)
                    .replace("visitedName", StringUtils.defaultString(detail.getGranteeName()))
                    .replace("link", portalUrl + VISITOR_APPROVAL_APPLY_PAGE);
            // 发送给被授权人
            Map<String, String> params = new HashMap<>(4);
            params.put("content", msg);
            params.put("from", robotId);
            params.put("to", granteeNo);
            try {
                thirdApiFactory.pushYxtRebot(params);
            } catch (Exception e) {
                log.error("访客授权组织变更宇信通消息发送失败: empNo={}", granteeNo, e);
            }
        }
    }
}
