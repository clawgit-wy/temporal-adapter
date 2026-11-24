package com.yutong.modules.risk.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessService;
import com.yutong.modules.common.constant.RoleCodeConstant;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.vo.BmpProcessDetailsVO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.dto.YtsafetyLocationInfoProcessDetailsDTO;
import com.yutong.modules.risk.entity.YtsafetyLocationInfoProcess;
import com.yutong.modules.risk.entity.YtsafetyLocationInfoProcessDetails;
import com.yutong.modules.risk.mapper.YtsafetyLocationInfoMapper;
import com.yutong.modules.risk.mapper.YtsafetyLocationInfoProcessDetailsMapper;
import com.yutong.modules.risk.mapper.YtsafetyLocationInfoProcessMapper;
import com.yutong.modules.risk.service.IYtsafetyLocationInfoProcessDetailsService;
import com.yutong.modules.risk.service.SafetyResponsibleNameResolver;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoDetailsExportVO;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoExportVO;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoProcessDetailsVO;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 位置信息变更流程明细表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
@Service
public class YtsafetyLocationInfoProcessDetailsServiceImpl extends ServiceImpl<YtsafetyLocationInfoProcessDetailsMapper, YtsafetyLocationInfoProcessDetails> implements IYtsafetyLocationInfoProcessDetailsService {

    @Autowired
    private YtsafetyLocationInfoProcessDetailsMapper detailsMapper;

    @Autowired
    private RoleCodeConstant roleCodeConstant;

    @Autowired
    private SafetyResponsibleNameResolver responsibleNameResolver;

    @Override
    public void save(Long processId, List<YtsafetyLocationInfoProcessDetailsDTO> dtos) {
        save(processId, dtos, BusUtits.getLoginUser());
    }

    @Override
    public void save(Long processId, List<YtsafetyLocationInfoProcessDetailsDTO> dtos, LoginUser loginUser) {
        if (dtos == null || dtos.isEmpty() || processId == null) return;
        detailsMapper.deleteByProcessId(processId);
        String safetyOfficer = roleCodeConstant.getSafetyOfficer();
        List<YtsafetyLocationInfoProcessDetails> details = new ArrayList<>(dtos.size());
        for (YtsafetyLocationInfoProcessDetailsDTO dto : dtos) {
            YtsafetyLocationInfoProcessDetails item = new YtsafetyLocationInfoProcessDetails();
            BeanUtils.copyProperties(dto, item);
            item.setChangeId(item.getId());
            item.setId(IdWorker.getId());
            item.setProcessId(processId);
            item.setSafetyRoleCode(safetyOfficer);
            item.setDelFlag(0);
            item.setVersion(0);
            item.setStatus(0);
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
    public List<YtsafetyLocationInfoProcessDetailsVO> queryList(Long processId) {
        List<YtsafetyLocationInfoProcessDetailsVO> list = detailsMapper.queryList(processId);
        // 回填安全员名称
        fillSafetyNames(list);
        return list;
    }

    /**
     * 回填安全员名称：按 org_code + safety_role_code 批量查询安全责任人名称，
     * 当前组织查不到责任人时向上级组织递归查找，多个人员用 "," 隔开；无匹配时置为空串
     */
    private void fillSafetyNames(List<YtsafetyLocationInfoProcessDetailsVO> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        List<String[]> pairs = records.stream()
                .map(record -> new String[]{record.getOrgCode(), record.getSafetyRoleCode()})
                .collect(Collectors.toList());
        Map<String, String> nameMap = responsibleNameResolver.resolveNames(pairs);
        for (YtsafetyLocationInfoProcessDetailsVO record : records) {
            String orgCode = record.getOrgCode();
            String roleCode = record.getSafetyRoleCode();
            if (StringUtils.isBlank(orgCode) || StringUtils.isBlank(roleCode)) {
                record.setSafetyNames("");
                continue;
            }
            record.setSafetyNames(nameMap.getOrDefault(orgCode + "_" + roleCode, ""));
        }
    }

    @Override
    public List<YtsafetyLocationInfoDetailsExportVO> queryExportList(Long processId) {
        List<YtsafetyLocationInfoProcessDetailsVO> list = detailsMapper.queryList(processId);
        return list.stream().map(vo -> {
            YtsafetyLocationInfoDetailsExportVO exportVO = new YtsafetyLocationInfoDetailsExportVO();
            BeanUtils.copyProperties(vo, exportVO);
            return exportVO;
        }).collect(Collectors.toList());
    }

    @Override
    public void compareWithExisting(Long processId, List<YtsafetyLocationInfoProcessDetailsVO> newData) {
        List<String> locationCodes = newData.stream()
                .map(YtsafetyLocationInfoProcessDetailsVO::getLocationCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<YtsafetyLocationInfoProcessDetailsVO> existingList = locationCodes.isEmpty() ? new ArrayList<>() : detailsMapper.queryByLocationCodes(locationCodes);
        Map<String, YtsafetyLocationInfoProcessDetailsVO> existingMap = existingList.stream()
                .filter(e -> e.getLocationCode() != null)
                .collect(Collectors.toMap(
                        YtsafetyLocationInfoProcessDetailsVO::getLocationCode,
                        e -> e,
                        (oldVal, newVal) -> oldVal));
        for (YtsafetyLocationInfoProcessDetailsVO vo : newData) {
            if (vo.getLocationCode() == null) {
                continue;
            }
            YtsafetyLocationInfoProcessDetailsVO existing = existingMap.get(vo.getLocationCode());
            if (existing == null) {
                continue;
            }

            List<String> fieldCodes = new ArrayList<>();
            HashMap<String, String> fieldsValues = new HashMap<>();

            compareField(fieldCodes, fieldsValues, "factoryName", existing.getFactoryName(), vo.getFactoryName());
            compareField(fieldCodes, fieldsValues, "areaName", existing.getAreaName(), vo.getAreaName());
            compareField(fieldCodes, fieldsValues, "orgName", existing.getOrgName(), vo.getOrgName());
            compareField(fieldCodes, fieldsValues, "riskLevel", existing.getRiskLevel(), vo.getRiskLevel());
            compareField(fieldCodes, fieldsValues, "riskColor", existing.getRiskColor(), vo.getRiskColor());
            compareField(fieldCodes, fieldsValues, "locationName", existing.getLocationName(), vo.getLocationName());
            compareField(fieldCodes, fieldsValues, "mainImage", existing.getMainImage(), vo.getMainImage());

            vo.setFieldCodes(fieldCodes);
            vo.setFieldsValues(fieldsValues);
        }
    }

    private void compareField(List<String> fieldCodes, HashMap<String, String> fieldsValues,
                              String fieldName, String oldValue, String newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            fieldCodes.add(fieldName);
            fieldsValues.put(fieldName, oldValue);
        }
    }

}
