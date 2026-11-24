package com.yutong.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yutong.modules.user.api.ISafetyCommonService;
import com.yutong.modules.user.api.IYtsafetyResponsibleProcessDetailsService;
import com.yutong.modules.user.dto.YtsafetyResponsibleProcessDetailsDTO;
import com.yutong.modules.user.entity.SafetySysRole;
import com.yutong.modules.user.entity.YTSafetySysOrgFactory;
import com.yutong.modules.user.entity.YtsafetyResponsibleProcessDetails;
import com.yutong.modules.user.mapper.*;
import com.yutong.modules.user.vo.SafetyFieldManageVO;
import com.yutong.modules.user.vo.YtsafetyResponsibleProcessDetailsVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: 安全责任人变更流程明细表
 * @Author: lvss
 * @Date:   2026-06-22
 * @Version: V1.0
 */
@Service
public class YtsafetyResponsibleProcessDetailsServiceImpl extends ServiceImpl<YtsafetyResponsibleProcessDetailsMapper, YtsafetyResponsibleProcessDetails> implements IYtsafetyResponsibleProcessDetailsService {

    @Autowired
    private YtsafetyResponsibleProcessDetailsMapper responsibleProcessDetailsMapper;

    @Autowired
    private SafetySysRoleMapper safetySysRoleMapper;

    @Autowired
    private SafetyGroupInfoMapper safetyGroupInfoMapper;

    @Autowired
    private ISafetyCommonService commonService;

    @Override
    public void save(Long processId,List<YtsafetyResponsibleProcessDetailsDTO> dtos) {
        if(dtos==null||dtos.isEmpty()||processId==null) return;
        responsibleProcessDetailsMapper.deleteProcessDetailsByProcessId(processId);
        List<YtsafetyResponsibleProcessDetails> details = new ArrayList<YtsafetyResponsibleProcessDetails>();
        for (YtsafetyResponsibleProcessDetailsDTO responsibleProcessDetails:dtos){
            YtsafetyResponsibleProcessDetails item = new YtsafetyResponsibleProcessDetails();
            BeanUtils.copyProperties(responsibleProcessDetails,item);
            item.setFactoryId(responsibleProcessDetails.getPowerFactoryId());
            item.setLoginName(responsibleProcessDetails.getUserName());
            item.setProcessId(processId);
            details.add(item);
        }
        this.saveBatch(details);
    }

    @Override
    public List<YtsafetyResponsibleProcessDetailsVO> queryList(Long processId) {
        List<YtsafetyResponsibleProcessDetailsVO> list = responsibleProcessDetailsMapper.queryList(processId);
        if (list.isEmpty()) {
            return list;
        }

        Set<Long> roleIds = new LinkedHashSet<>();
        Set<Long> departIds = new LinkedHashSet<>();
        Set<Long> factoryIds = new LinkedHashSet<>();
        for (YtsafetyResponsibleProcessDetailsVO vo : list) {
            collectIds(vo.getRoleId(), roleIds);
            collectIds(vo.getDepartId(), departIds);
            collectIds(vo.getFactoryId(), factoryIds);
        }

        Map<Long, String> roleNameMap = Collections.emptyMap();
        if (!roleIds.isEmpty()) {
            roleNameMap = safetySysRoleMapper.selectList(
                    new LambdaQueryWrapper<SafetySysRole>()
                            .in(SafetySysRole::getId, roleIds)
                            .eq(SafetySysRole::getDeleteFalg, "0"))
                    .stream()
                    .collect(Collectors.toMap(SafetySysRole::getId, SafetySysRole::getRoleName));
        }

        Map<Long, String> departNameMap = Collections.emptyMap();
        if (!departIds.isEmpty()) {
            departNameMap = commonService.queryGroupInfoByIds(new ArrayList<>(departIds));
        }

        Map<Long, String> factoryNameMap = Collections.emptyMap();
        if (!factoryIds.isEmpty()) {
            factoryNameMap = commonService.queryFactoryDrop()
                    .stream()
                    .collect(Collectors.toMap(SafetyFieldManageVO::getId, SafetyFieldManageVO::getFieldName));
        }

        for (YtsafetyResponsibleProcessDetailsVO vo : list) {
            vo.setRoleNames(joinNames(vo.getRoleId(), roleNameMap));
            vo.setDepartNames(joinNames(vo.getDepartId(), departNameMap));
            vo.setFactoryNames(joinNames(vo.getFactoryId(), factoryNameMap));
        }

        return list;
    }

    private void collectIds(String ids, Set<Long> result) {
        if (StringUtils.isBlank(ids)) {
            return;
        }
        Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(Long::parseLong)
                .forEach(result::add);
    }

    private String joinNames(String ids, Map<Long, String> nameMap) {
        if (StringUtils.isBlank(ids) || nameMap.isEmpty()) {
            return "";
        }
        return Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(id -> nameMap.getOrDefault(Long.parseLong(id), ""))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(","));
    }

}
