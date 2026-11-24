package com.yutong.modules.risk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.common.constant.enums.ProcessStatusEnums;
import com.yutong.modules.common.dto.YtsafetySummaryProcessQueryDTO;
import com.yutong.modules.risk.constants.enums.ApplyTypeEnum;
import com.yutong.modules.risk.dto.YtsafetyRiskDataDTO;
import com.yutong.modules.risk.dto.YtsafetyRiskDataDetailsDTO;
import com.yutong.modules.risk.dto.YtsafetyRiskDataQueryDTO;
import com.yutong.modules.risk.vo.YtsafetyRiskDataExportVO;
import com.yutong.modules.risk.vo.YtsafetyRiskDataVO;
import com.yutong.modules.risk.entity.YtsafetyRiskData;
import com.yutong.modules.risk.mapper.YtsafetyRiskDataMapper;
import com.yutong.modules.risk.service.IYtsafetyRiskDataProcessService;
import com.yutong.modules.risk.service.IYtsafetyRiskDataService;
import com.yutong.modules.risk.service.SafetyResponsibleNameResolver;
import com.yutong.modules.risk.vo.YtsafetyTaskRiskDataVO;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 风险数据表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
@Log4j2
@Service
public class YtsafetyRiskDataServiceImpl extends ServiceImpl<YtsafetyRiskDataMapper, YtsafetyRiskData> implements IYtsafetyRiskDataService {

    @Autowired
    private YtsafetyRiskDataMapper riskDataMapper;

    @Autowired
    private IYtsafetyRiskDataProcessService processService;

    @Autowired
    private SafetyResponsibleNameResolver responsibleNameResolver;

    @Override
    public IPage<YtsafetyRiskDataVO> queryPageList(Page<YtsafetyRiskDataVO> page, YtsafetyRiskDataQueryDTO dto) {
        LambdaQueryWrapper<YtsafetyRiskData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YtsafetyRiskData::getDelFlag, 0);
        // 作业环节编码 - 模糊
        if (StringUtils.isNotBlank(dto.getJobStepCode())) {
            wrapper.like(YtsafetyRiskData::getJobStepCode, dto.getJobStepCode());
        }
        // 作业环节名称 - 模糊
        if (StringUtils.isNotBlank(dto.getJobStepName())) {
            wrapper.like(YtsafetyRiskData::getJobStepName, dto.getJobStepName());
        }
        // 厂区 - 多选
        if (!CollectionUtils.isEmpty(dto.getFactoryIds())) {
            wrapper.in(YtsafetyRiskData::getFactoryId, dto.getFactoryIds());
        }
        // 区域 - 多选
        if (!CollectionUtils.isEmpty(dto.getAreaIds())) {
            wrapper.in(YtsafetyRiskData::getAreaId, dto.getAreaIds());
        }
        // 位置 - 模糊
        if (StringUtils.isNotBlank(dto.getLocationName())) {
            wrapper.like(YtsafetyRiskData::getLocationName, dto.getLocationName());
        }
        // 所属组织 - 单选
        if (dto.getOrgId() != null) {
            wrapper.eq(YtsafetyRiskData::getOrgId, dto.getOrgId());
        }
        // 风险类别 - 单选
        if (dto.getRiskCategoryId() != null) {
            wrapper.eq(YtsafetyRiskData::getRiskCategoryId, dto.getRiskCategoryId());
        }
        // 风险编码 - 模糊
        if (StringUtils.isNotBlank(dto.getRiskCode())) {
            wrapper.like(YtsafetyRiskData::getRiskCode, dto.getRiskCode());
        }
        if (dto.getRiskTypeId() != null) {
            wrapper.eq(YtsafetyRiskData::getRiskTypeId, dto.getRiskTypeId());
        }
        if (StringUtils.isNotBlank(dto.getRiskLevel())) {
            wrapper.eq(YtsafetyRiskData::getRiskLevel, dto.getRiskLevel());
        }
        if (StringUtils.isNotBlank(dto.getRiskDescription())) {
            wrapper.like(YtsafetyRiskData::getRiskDescription, dto.getRiskDescription());
        }
        // 最近更新人 - 模糊（按姓名或工号）
        if (StringUtils.isNotBlank(dto.getUpdateName())) {
            wrapper.and(w -> w.like(YtsafetyRiskData::getUpdateName, dto.getUpdateName())
                    .or().like(YtsafetyRiskData::getUpdateUser, dto.getUpdateName()));
        }
        // 时间区间
        if (dto.getStartTime() != null) {
            String startDate = DateUtils.formatDate(dto.getStartTime(),"yyyy-MM-dd")+" 00:00:00";
            dto.setStartTime(DateUtils.parseDatetime(startDate));
            wrapper.ge(YtsafetyRiskData::getUpdateTime, dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            String endDate = DateUtils.formatDate(dto.getEndTime(),"yyyy-MM-dd")+" 23:59:59";
            dto.setEndTime(DateUtils.parseDatetime(endDate));
            wrapper.le(YtsafetyRiskData::getUpdateTime, dto.getEndTime());
        }

        if (dto.getRiskValueStart() != null) {
            wrapper.ge(YtsafetyRiskData::getRiskValue, dto.getRiskValueStart());
        }
        if (dto.getRiskValueEnd() != null) {
            wrapper.le(YtsafetyRiskData::getRiskValue, dto.getRiskValueEnd());
        }

        wrapper.orderByDesc(YtsafetyRiskData::getUpdateTime);

        IPage<YtsafetyRiskDataVO> result = riskDataMapper.queryPageList(page, wrapper, dto);
        // 回填安全员、安全主管名称
        fillNames(result.getRecords());
        return result;
    }

    @Override
    public YtsafetyRiskDataVO queryByDataId(Long id) {
        YtsafetyRiskDataVO vo = riskDataMapper.queryByDataId(id);
        if (vo != null) {
            fillNames(Collections.singletonList(vo));
        }
        return vo;
    }

    @Override
    public void pushOverdueData(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -6);
        Date sixMonthsAgo = calendar.getTime();

        List<YtsafetyRiskData> overdueList = lambdaQuery()
                .lt(YtsafetyRiskData::getUpdateTime, sixMonthsAgo)
                .eq(YtsafetyRiskData::getSyncStatus, 0)
                .eq(YtsafetyRiskData::getDelFlag, 0)
                .select(YtsafetyRiskData.class, info -> !"processId".equals(info.getColumn())
                        && !"version".equals(info.getColumn())
                        && !"delFlag".equals(info.getColumn())
                        && !"createTime".equals(info.getColumn())
                        && !"createUser".equals(info.getColumn())
                        && !"createName".equals(info.getColumn())
                        && !"updateTime".equals(info.getColumn())
                        && !"updateUser".equals(info.getColumn())
                        && !"updateName".equals(info.getColumn()))
                .list();

        if (CollectionUtils.isEmpty(overdueList)) {
            log.info("pushOverdueData: 无更新时间超过半年的风险数据");
            return;
        }

        Map<String, List<YtsafetyRiskData>> grouped = overdueList.stream()
                .collect(Collectors.groupingBy(
                        data -> data.getRiskTypeId() + "_" + data.getFactoryId() + "_" + data.getOrgId()));

        for (Map.Entry<String, List<YtsafetyRiskData>> entry : grouped.entrySet()) {
            List<YtsafetyRiskData> groupData = entry.getValue();
            YtsafetyRiskData first = groupData.get(0);

            List<YtsafetyRiskDataDetailsDTO> details = groupData.stream()
                    .map(data -> {
                        YtsafetyRiskDataDetailsDTO detail = new YtsafetyRiskDataDetailsDTO();
                        BeanUtils.copyProperties(data, detail);
                        return detail;
                    })
                    .collect(Collectors.toList());

            YtsafetyRiskDataDTO dto = new YtsafetyRiskDataDTO();
            dto.setChangeType(ApplyTypeEnum.BATCH_PUSH.getCode());
            dto.setApplyReason("风险数据超过半年未更新，自动推送");
            dto.setDetails(details);
            syncJobAdd(dto);
        }

        List<Long> ids = overdueList.stream()
                .map(YtsafetyRiskData::getId)
                .collect(Collectors.toList());
        lambdaUpdate()
                .set(YtsafetyRiskData::getSyncStatus, 1)
                .in(YtsafetyRiskData::getId, ids)
                .update();
        log.info("pushOverdueData: 推送完成，更新sync_status条数: {}", ids.size());
    }

    @Async
    public void syncJobAdd(YtsafetyRiskDataDTO dto){
        processService.addJob(dto);
    }

    @Override
    public List<YtsafetyRiskDataVO> queryList(YtsafetyRiskDataQueryDTO dto) {
        LambdaQueryWrapper<YtsafetyRiskData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YtsafetyRiskData::getDelFlag, 0);
        if (StringUtils.isNotBlank(dto.getJobStepCode())) {
            wrapper.like(YtsafetyRiskData::getJobStepCode, dto.getJobStepCode());
        }
        if (StringUtils.isNotBlank(dto.getJobStepName())) {
            wrapper.like(YtsafetyRiskData::getJobStepName, dto.getJobStepName());
        }
        if (!CollectionUtils.isEmpty(dto.getFactoryIds())) {
            wrapper.in(YtsafetyRiskData::getFactoryId, dto.getFactoryIds());
        }
        if (!CollectionUtils.isEmpty(dto.getAreaIds())) {
            wrapper.in(YtsafetyRiskData::getAreaId, dto.getAreaIds());
        }
        if (StringUtils.isNotBlank(dto.getLocationName())) {
            wrapper.like(YtsafetyRiskData::getLocationName, dto.getLocationName());
        }
        if (dto.getOrgId() != null) {
            wrapper.eq(YtsafetyRiskData::getOrgId, dto.getOrgId());
        }
        if (dto.getRiskCategoryId() != null) {
            wrapper.eq(YtsafetyRiskData::getRiskCategoryId, dto.getRiskCategoryId());
        }
        if (StringUtils.isNotBlank(dto.getRiskCode())) {
            wrapper.like(YtsafetyRiskData::getRiskCode, dto.getRiskCode());
        }
        if (dto.getRiskTypeId() != null) {
            wrapper.eq(YtsafetyRiskData::getRiskTypeId, dto.getRiskTypeId());
        }
        if (StringUtils.isNotBlank(dto.getRiskLevel())) {
            wrapper.eq(YtsafetyRiskData::getRiskLevel, dto.getRiskLevel());
        }
        if (StringUtils.isNotBlank(dto.getRiskDescription())) {
            wrapper.like(YtsafetyRiskData::getRiskDescription, dto.getRiskDescription());
        }
        if (StringUtils.isNotBlank(dto.getUpdateName())) {
            wrapper.and(w -> w.like(YtsafetyRiskData::getUpdateName, dto.getUpdateName())
                    .or().like(YtsafetyRiskData::getUpdateUser, dto.getUpdateName()));
        }
        if (StringUtils.isNotBlank(dto.getSelections())) {
            List<Long> ids = java.util.Arrays.stream(dto.getSelections().split(","))
                    .map(String::trim).map(Long::parseLong)
                    .collect(Collectors.toList());
            wrapper.in(YtsafetyRiskData::getId, ids);
        }
        if (dto.getStartTime() != null) {
            wrapper.ge(YtsafetyRiskData::getUpdateTime, dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            wrapper.le(YtsafetyRiskData::getUpdateTime, dto.getEndTime());
        }
        wrapper.orderByDesc(YtsafetyRiskData::getUpdateTime);

        List<YtsafetyRiskDataVO> list = riskDataMapper.queryList(wrapper, dto);
        // 回填安全员、安全主管名称
        fillNames(list);
        return list;
    }

    /**
     * 回填安全员/安全主管名称：按 org_code + role_code 批量查询安全责任人名称，
     * 多个人员用 "," 隔开；无匹配时置为空串
     */
    private void fillNames(List<YtsafetyRiskDataVO> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        List<String[]> pairs = new ArrayList<>();
        for (YtsafetyRiskDataVO record : records) {
            pairs.add(new String[]{record.getOrgCode(), record.getSafetyRoleCode()});
            pairs.add(new String[]{record.getOrgCode(), record.getManagerRoleCode()});
        }
        Map<String, String> nameMap = responsibleNameResolver.resolveNames(pairs);
        for (YtsafetyRiskDataVO record : records) {
            record.setSafetyNames(getRoleNames(nameMap, record.getOrgCode(), record.getSafetyRoleCode()));
            record.setManagerNames(getRoleNames(nameMap, record.getOrgCode(), record.getManagerRoleCode()));
        }
    }

    private String getRoleNames(Map<String, String> nameMap, String orgCode, String roleCode) {
        if (StringUtils.isBlank(orgCode) || StringUtils.isBlank(roleCode)) {
            return "";
        }
        return nameMap.getOrDefault(orgCode + "_" + roleCode, "");
    }

}
