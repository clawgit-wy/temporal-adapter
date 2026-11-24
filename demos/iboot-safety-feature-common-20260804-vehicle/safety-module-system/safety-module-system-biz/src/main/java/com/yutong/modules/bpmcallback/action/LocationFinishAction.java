package com.yutong.modules.bpmcallback.action;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yutong.modules.common.dto.BpmMethodProcessDTO;
import com.yutong.modules.risk.entity.YtsafetyLocationInfo;
import com.yutong.modules.risk.entity.YtsafetyLocationInfoProcess;
import com.yutong.modules.risk.entity.YtsafetyLocationInfoProcessDetails;
import com.yutong.modules.risk.service.IYtsafetyLocationInfoProcessDetailsService;
import com.yutong.modules.risk.service.IYtsafetyLocationInfoProcessService;
import com.yutong.modules.risk.service.IYtsafetyLocationInfoService;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoProcessDetailsVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 位置信息流程完成回调，同步明细数据到位置信息主表
 */
@Slf4j
@Component("ytsafety_location_finish_callback")
public class LocationFinishAction extends NodeAction {

    @Autowired
    private IYtsafetyLocationInfoProcessService locationInfoProcessService;

    @Autowired
    private IYtsafetyLocationInfoProcessDetailsService locationInfoProcessDetailsService;

    @Autowired
    private IYtsafetyLocationInfoService locationInfoService;

    @Override
    public void execute(BpmMethodProcessDTO event) {
        Long processId = Long.valueOf(event.getFormId().getFormInstanceId());
        syncLocationInfo(processId);
    }

    /**
     * 同步流程明细数据到位置信息主表：位置编码存在则更新，不存在则新增
     */
    private void syncLocationInfo(Long processId) {
        log.info("处理位置信息流程完成回调, processId: {}", processId);
        YtsafetyLocationInfoProcess process = locationInfoProcessService.getById(processId);
        if (process == null) {
            log.warn("位置信息流程不存在, processId: {}", processId);
            return;
        }
        List<YtsafetyLocationInfoProcessDetailsVO> details = locationInfoProcessDetailsService.queryList(processId);
        if (details == null || details.isEmpty()) {
            log.warn("位置信息流程明细为空, processId: {}", processId);
            return;
        }
        for (YtsafetyLocationInfoProcessDetailsVO detail : details) {
            String updateMsg = processDetail(process, detail);
            saveUpdateMsg(detail.getId(), updateMsg);
        }
    }

    /**
     * 处理单条明细，返回执行结果信息
     */
    private String processDetail(YtsafetyLocationInfoProcess process, YtsafetyLocationInfoProcessDetailsVO detail) {
        if (StringUtils.isEmpty(detail.getLocationCode())) {
            log.warn("明细位置编码为空, 跳过, detailId: {}", detail.getId());
            return "位置编码为空，跳过同步";
        }
        YtsafetyLocationInfo existing = findExistingLocation(detail);
        if (existing == null) {
            insertLocationInfo(process, detail);
            return "位置信息已同步新增";
        }
        if (existing.getRiskQuantity() != null && existing.getRiskQuantity() > 0) {
            log.info("位置信息被使用中，跳过更新, locationCode: {}, riskQuantity: {}", existing.getLocationCode(), existing.getRiskQuantity());
            return "位置信息被使用中，跳过更新";
        }
        updateLocationInfo(process, existing, detail);
        return "位置信息已同步更新";
    }

    /**
     * 查找已有位置信息：先按名称+区域+工厂+组织查询，未命中再按位置编码查询
     */
    private YtsafetyLocationInfo findExistingLocation(YtsafetyLocationInfoProcessDetailsVO detail) {
        YtsafetyLocationInfo existing = locationInfoService.getOne(new LambdaQueryWrapper<YtsafetyLocationInfo>()
                .eq(YtsafetyLocationInfo::getLocationName, detail.getLocationName())
                .eq(YtsafetyLocationInfo::getAreaId, detail.getAreaId())
                .eq(YtsafetyLocationInfo::getFactoryId, detail.getFactoryId())
                .eq(YtsafetyLocationInfo::getOrgId, detail.getOrgId()));
        if (existing == null) {
            existing = locationInfoService.getOne(new LambdaQueryWrapper<YtsafetyLocationInfo>()
                    .eq(YtsafetyLocationInfo::getLocationCode, detail.getLocationCode()));
        }
        return existing;
    }

    /**
     * 保存执行结果到明细表 updateMsg 字段
     */
    private void saveUpdateMsg(Long detailId, String updateMsg) {
        if (detailId == null || updateMsg == null) {
            return;
        }
        locationInfoProcessDetailsService.update(new LambdaUpdateWrapper<YtsafetyLocationInfoProcessDetails>()
                .eq(YtsafetyLocationInfoProcessDetails::getId, detailId)
                .set(YtsafetyLocationInfoProcessDetails::getUpdateMsg, updateMsg));
    }

    /**
     * 更新已有位置信息
     */
    private void updateLocationInfo(YtsafetyLocationInfoProcess process, YtsafetyLocationInfo existing, YtsafetyLocationInfoProcessDetailsVO detail) {
        existing.setProcessId(process.getId());
        existing.setFactoryId(detail.getFactoryId());
        existing.setFactoryCode(detail.getFactoryCode());
        existing.setFactoryName(detail.getFactoryName());
        existing.setAreaId(detail.getAreaId());
        existing.setAreaCode(detail.getAreaCode());
        existing.setAreaName(detail.getAreaName());
        existing.setOrgId(detail.getOrgId());
        existing.setOrgCode(detail.getOrgCode());
        existing.setOrgName(detail.getOrgName());
        existing.setLocationName(detail.getLocationName());
        existing.setSafetyRoleCode(detail.getSafetyRoleCode());
        existing.setMainImage(detail.getMainImage());
        existing.setStatus(detail.getStatus());
        existing.setUpdateTime(new Date());
        existing.setUpdateName(process.getUpdateName());
        existing.setUpdateUser(process.getUpdateUser());
        existing.setCreateName(detail.getCreateName());
        existing.setCreateTime(detail.getCreateTime());
        existing.setCreateUser(detail.getCreateUser());
        locationInfoService.updateById(existing);
        log.info("更新位置信息成功, locationCode: {}", detail.getLocationCode());
    }

    /**
     * 新增位置信息
     */
    private void insertLocationInfo(YtsafetyLocationInfoProcess process, YtsafetyLocationInfoProcessDetailsVO detail) {
        YtsafetyLocationInfo entity = new YtsafetyLocationInfo();
        entity.setProcessId(detail.getProcessId());
        entity.setFactoryId(detail.getFactoryId());
        entity.setFactoryCode(detail.getFactoryCode());
        entity.setFactoryName(detail.getFactoryName());
        entity.setAreaId(detail.getAreaId());
        entity.setAreaCode(detail.getAreaCode());
        entity.setAreaName(detail.getAreaName());
        entity.setOrgId(detail.getOrgId());
        entity.setOrgCode(detail.getOrgCode());
        entity.setOrgName(detail.getOrgName());
        entity.setLocationName(detail.getLocationName());
        entity.setLocationCode(detail.getLocationCode());
        entity.setSafetyRoleCode(detail.getSafetyRoleCode());
        entity.setMainImage(detail.getMainImage());
        entity.setRiskQuantity(0);
        entity.setStatus(detail.getStatus() != null ? detail.getStatus() : 0);
        entity.setUpdateTime(new Date());
        entity.setUpdateName(process.getUpdateName());
        entity.setUpdateUser(process.getUpdateUser());
        entity.setCreateName(detail.getCreateName());
        entity.setCreateTime(detail.getCreateTime());
        entity.setCreateUser(detail.getCreateUser());
        entity.setDelFlag(0);
        locationInfoService.save(entity);
        log.info("新增位置信息成功, locationCode: {}", detail.getLocationCode());
    }
}
