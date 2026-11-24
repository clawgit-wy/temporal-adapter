package com.yutong.modules.risk.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.yutong.modules.risk.dto.YtsafetyRiskDataDetailsDTO;
import com.yutong.modules.risk.entity.YtsafetyRiskDataProcessDetails;
import com.yutong.modules.risk.mapper.YtsafetyRiskDataProcessDetailsMapper;
import com.yutong.modules.risk.service.IYtsafetyRiskDataProcessDetailsService;
import com.yutong.modules.risk.service.SafetyResponsibleNameResolver;
import com.yutong.modules.risk.vo.YtsafetyRiskDataDetailsExportVO;
import com.yutong.modules.risk.vo.YtsafetyRiskDataDetailsVO;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 风险数据变更流程明细表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
@Service
public class YtsafetyRiskDataProcessDetailsServiceImpl extends ServiceImpl<YtsafetyRiskDataProcessDetailsMapper, YtsafetyRiskDataProcessDetails> implements IYtsafetyRiskDataProcessDetailsService {

    @Autowired
    private YtsafetyRiskDataProcessDetailsMapper detailsMapper;

    @Autowired
    private SafetyResponsibleNameResolver responsibleNameResolver;

    @Override
    public void save(Long processId, List<YtsafetyRiskDataDetailsDTO> dtos) {
        save(processId, dtos, BusUtits.getLoginUser());
    }

    @Override
    public void save(Long processId, List<YtsafetyRiskDataDetailsDTO> dtos, LoginUser loginUser) {
        if (dtos == null || dtos.isEmpty() || processId == null) return;
        detailsMapper.deleteByProcessId(processId);
        List<YtsafetyRiskDataProcessDetails> details = new ArrayList<>(dtos.size());
        for (YtsafetyRiskDataDetailsDTO dto : dtos) {
            YtsafetyRiskDataProcessDetails item = new YtsafetyRiskDataProcessDetails();
            BeanUtils.copyProperties(dto, item);
            item.setChangeId(item.getId());
            item.setId(IdWorker.getId());
            item.setProcessId(processId);
            item.setDelFlag(0);
            item.setVersion(0);
            item.setCreateTime(new java.util.Date());
            item.setUpdateTime(new java.util.Date());
            if (loginUser != null) {
                item.setCreateUser(loginUser.getEmpNo());
                item.setCreateName(loginUser.getRealname());
                item.setUpdateUser(loginUser.getEmpNo());
                item.setUpdateName(loginUser.getRealname());
            }
            details.add(item);
        }
        detailsMapper.batchInsert(details);
    }

    @Override
    public List<YtsafetyRiskDataDetailsVO> queryList(Long processId) {
        List<YtsafetyRiskDataDetailsVO> list = detailsMapper.queryList(processId);
        // 回填安全员、安全主管名称
        fillNames(list);
        return list;
    }

    /**
     * 回填安全员/安全主管名称：按 org_code + role_code 批量查询安全责任人名称，
     * 当前组织查不到责任人时向上级组织递归查找，多个人员用 "," 隔开；无匹配时置为空串
     */
    private void fillNames(List<YtsafetyRiskDataDetailsVO> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        List<String[]> pairs = new ArrayList<>();
        for (YtsafetyRiskDataDetailsVO record : records) {
            pairs.add(new String[]{record.getOrgCode(), record.getSafetyRoleCode()});
            pairs.add(new String[]{record.getOrgCode(), record.getManagerRoleCode()});
        }
        Map<String, String> nameMap = responsibleNameResolver.resolveNames(pairs);
        for (YtsafetyRiskDataDetailsVO record : records) {
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

    @Override
    public List<YtsafetyRiskDataDetailsExportVO> queryExportList(Long processId) {
        List<YtsafetyRiskDataDetailsVO> list = detailsMapper.queryList(processId);
        return list.stream().map(vo -> {
            YtsafetyRiskDataDetailsExportVO exportVO = new YtsafetyRiskDataDetailsExportVO();
            BeanUtils.copyProperties(vo, exportVO);
            return exportVO;
        }).collect(Collectors.toList());
    }

}
