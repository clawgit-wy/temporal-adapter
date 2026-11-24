package com.yutong.modules.risk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yutong.modules.risk.api.IYTSafetyHazardReportNodeRoleService;
import com.yutong.modules.risk.entity.YTSafetyHazardReportNodeRole;
import com.yutong.modules.risk.mapper.YTSafetyHazardReportNodeRoleMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Collections;
import java.util.List;

/**
 * @Description: 隐患上报表流程节点配置角色
 * @Author: jeecg-boot
 * @Date:   2026-07-15
 * @Version: V1.0
 */
@Service
public class YTSafetyHazardReportNodeRoleServiceImpl extends ServiceImpl<YTSafetyHazardReportNodeRoleMapper, YTSafetyHazardReportNodeRole> implements IYTSafetyHazardReportNodeRoleService {

    @Override
    public List<YTSafetyHazardReportNodeRole> queryList(YTSafetyHazardReportNodeRole param) {
        LambdaQueryWrapper<YTSafetyHazardReportNodeRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(StringUtils.isNotBlank(param.getNodeType()),YTSafetyHazardReportNodeRole::getNodeType,param.getNodeType())
                .eq(param.getPriority() != null, YTSafetyHazardReportNodeRole::getPriority, param.getPriority())
                .eq(StringUtils.isNotBlank(param.getFunctionFlag()), YTSafetyHazardReportNodeRole::getFunctionFlag, param.getFunctionFlag())
                .eq(StringUtils.isNotBlank(param.getModelFlag()), YTSafetyHazardReportNodeRole::getModelFlag, param.getModelFlag())
                .eq(param.getHazardCategory() != null, YTSafetyHazardReportNodeRole::getHazardCategory, param.getHazardCategory())
                .eq(StringUtils.isNotBlank(param.getProblemSource()), YTSafetyHazardReportNodeRole::getProblemSource, param.getProblemSource())
                .eq(param.getProblemLevel() != null, YTSafetyHazardReportNodeRole::getProblemLevel, param.getProblemLevel())
                .eq(StringUtils.isNotBlank(param.getScopeType()), YTSafetyHazardReportNodeRole::getScopeType, param.getScopeType())
                .eq(StringUtils.isNotBlank(param.getRoleCode()), YTSafetyHazardReportNodeRole::getRoleCode, param.getRoleCode())
                .orderByDesc(YTSafetyHazardReportNodeRole::getCreateTime);
        return baseMapper.selectList(queryWrapper);
    }
}
