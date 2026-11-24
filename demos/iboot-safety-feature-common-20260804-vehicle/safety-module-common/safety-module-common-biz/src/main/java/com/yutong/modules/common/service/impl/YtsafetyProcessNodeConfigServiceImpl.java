package com.yutong.modules.common.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yutong.modules.common.api.bpm.IYtsafetyProcessNodeConfigService;
import com.yutong.modules.common.entity.YtsafetyProcessNodeConfig;
import com.yutong.modules.common.mapper.YtsafetyProcessNodeConfigMapper;
import com.yutong.modules.common.vo.SysResponsibleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description: 流程节点配置
 * @Author: jeecg-boot
 * @Date:   2026-07-23
 * @Version: V1.0
 */
@Service
public class YtsafetyProcessNodeConfigServiceImpl extends ServiceImpl<YtsafetyProcessNodeConfigMapper, YtsafetyProcessNodeConfig> implements IYtsafetyProcessNodeConfigService {

    @Autowired
    private YtsafetyProcessNodeConfigMapper processNodeConfigMapper;

    @Override
    public String queryApprovers(String modelFlag, String nodeFlag,String dataType, Long factoryId, String orgCode) {
        if (StringUtils.isEmpty(modelFlag) || StringUtils.isEmpty(nodeFlag)) return null;
        List<YtsafetyProcessNodeConfig> configs = queryProcessNodeConfigsCached(modelFlag, nodeFlag, dataType);
        if (configs == null || configs.isEmpty()) return null;
        Set<String> approverUserNames = new LinkedHashSet<>();

        for (YtsafetyProcessNodeConfig config : configs) {
            //厂区
            if (0 == config.getRoleScode()) {
                List<SysResponsibleVO> factoryResponsibles = processNodeConfigMapper.queryResponsibleByFactory(factoryId, config.getRoleCode());
                if (CollUtil.isNotEmpty(factoryResponsibles)) {
                    factoryResponsibles.forEach(r -> approverUserNames.add(r.getUserName()));
                }
            }
            //部门：沿组织路径向上查找
            if (1 == config.getRoleScode()) {
                List<SysResponsibleVO> responsibles = processNodeConfigMapper.batchQueryResponsibleByOrgCodes(Collections.singletonList(orgCode), config.getRoleCode());
                if (CollUtil.isNotEmpty(responsibles)) {
                    responsibles.forEach(r -> approverUserNames.add(r.getUserName()));
                }
            }
            //部门厂区
            if (2 == config.getRoleScode()) {
                List<SysResponsibleVO> responsibles = processNodeConfigMapper.batchQueryResponsibleByFactoryAndDeparts(Collections.singletonList(orgCode), factoryId, config.getRoleCode());
                if (CollUtil.isNotEmpty(responsibles)) {
                    responsibles.forEach(r -> approverUserNames.add(r.getUserName()));
                }
            }
            if(1==config.getRunType()) break;
        }
        if (approverUserNames.isEmpty()) {
            return null;
        }
        return String.join(",", approverUserNames);
    }

    private final Map<String, List<YtsafetyProcessNodeConfig>> configCache = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public boolean updateById(YtsafetyProcessNodeConfig entity) {
        boolean success = super.updateById(entity);
        if (success) {
            //配置变更后清除缓存，保证审批人查询使用最新配置
            configCache.clear();
        }
        return success;
    }

    private List<YtsafetyProcessNodeConfig> queryProcessNodeConfigsCached(String modelFlag, String nodeFlag, String dataType) {
        String cacheKey = modelFlag + ":" + nodeFlag + ":" + (dataType != null ? dataType : "");
        return configCache.computeIfAbsent(cacheKey, k -> queryProcessNodeConfigs(modelFlag, nodeFlag, dataType));
    }

    @Override
    public String queryProposalApprovers(String modelFlag, String nodeFlag, String dataType, List<String> orgCodes) {
        if (StringUtils.isEmpty(modelFlag) || StringUtils.isEmpty(nodeFlag)) return null;
        List<YtsafetyProcessNodeConfig> configs = queryProcessNodeConfigsCached(modelFlag, nodeFlag, dataType);
        if (configs == null || configs.isEmpty()) return null;
        Set<String> approverUserNames = new LinkedHashSet<>();

        for (YtsafetyProcessNodeConfig config : configs) {
            //部门：批量查询，一次SQL沿组织路径向上查找最近的安全责任人
            if (1 == config.getRoleScode()) {
                List<SysResponsibleVO> responsibles = processNodeConfigMapper.batchQueryResponsibleByOrgCodes(orgCodes, config.getRoleCode());
                if (CollUtil.isNotEmpty(responsibles)) {
                    responsibles.forEach(r -> approverUserNames.add(r.getUserName()));
                }
            }
            if (1 == config.getRunType()) break;
        }
        if (approverUserNames.isEmpty()) {
            return null;
        }
        return String.join(",", approverUserNames);
    }

    private List<YtsafetyProcessNodeConfig> queryProcessNodeConfigs(String modelFlag, String nodeFlag,String dataType){
        LambdaQueryWrapper<YtsafetyProcessNodeConfig> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotEmpty(modelFlag)){
            queryWrapper.eq(YtsafetyProcessNodeConfig::getModelFlag,modelFlag);
        }
        if(StringUtils.isNotEmpty(nodeFlag)){
            queryWrapper.eq(YtsafetyProcessNodeConfig::getNodeFlag,nodeFlag);
        }
        if(StringUtils.isNotEmpty(dataType)){
            queryWrapper.eq(YtsafetyProcessNodeConfig::getDataType,dataType);
        }
        queryWrapper.orderByAsc(YtsafetyProcessNodeConfig::getSortIndex);
        return processNodeConfigMapper.selectList(queryWrapper);
    }

}
