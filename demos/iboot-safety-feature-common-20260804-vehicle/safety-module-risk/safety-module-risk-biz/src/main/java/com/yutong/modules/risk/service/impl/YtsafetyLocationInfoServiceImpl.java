package com.yutong.modules.risk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.risk.dto.YtsafetyLocationInfoQueryDTO;
import com.yutong.modules.risk.entity.YTSafetyHazardReport;
import com.yutong.modules.risk.entity.YTSafetyRiskLevelDict;
import com.yutong.modules.risk.entity.YtsafetyLocationInfo;
import com.yutong.modules.risk.mapper.YTSafetyHazardReportMapper;
import com.yutong.modules.risk.mapper.YtsafetyLocationInfoMapper;
import com.yutong.modules.risk.service.IYTSafetyRiskLevelDictService;
import com.yutong.modules.risk.service.IYtsafetyLocationInfoService;
import com.yutong.modules.risk.service.SafetyResponsibleNameResolver;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoVO;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: 位置信息表
 * @Author: jeecg-boot
 * @Date: 2026-06-29
 * @Version: V1.0
 */
@Log4j2
@Service
public class YtsafetyLocationInfoServiceImpl extends ServiceImpl<YtsafetyLocationInfoMapper, YtsafetyLocationInfo> implements IYtsafetyLocationInfoService {

    @Autowired
    private YtsafetyLocationInfoMapper locationInfoMapper;

    @Autowired
    private YTSafetyHazardReportMapper hazardReportMapper;

    @Autowired
    private IYTSafetyRiskLevelDictService riskLevelDictService;

    @Autowired
    private SafetyResponsibleNameResolver responsibleNameResolver;

    @Override
    public IPage<YtsafetyLocationInfoVO> queryPageList(Page<YtsafetyLocationInfoVO> page, YtsafetyLocationInfoQueryDTO dto) {
        LambdaQueryWrapper<YtsafetyLocationInfo> wrapper = buildQueryWrapper(dto);
        IPage<YtsafetyLocationInfoVO> result = locationInfoMapper.queryPageList(page, wrapper, dto);
        // 回填安全员名称
        fillSafetyNames(result.getRecords());
        return result;
    }

    @Override
    public List<YtsafetyLocationInfoVO> queryList(YtsafetyLocationInfoQueryDTO dto) {
        LambdaQueryWrapper<YtsafetyLocationInfo> wrapper = buildQueryWrapper(dto);
        List<YtsafetyLocationInfoVO> result = locationInfoMapper.queryList(wrapper, dto);
        fillSafetyNames(result);
        return result;
    }

    @Override
    public YtsafetyLocationInfoVO queryById(Long id) {
        YtsafetyLocationInfoVO vo = locationInfoMapper.queryById(id);
        // 回填安全员名称
        vo.setSafetyNames(queryResponsibleNames(vo.getOrgCode(), vo.getSafetyRoleCode()));
        return vo;
    }

    @Override
    public void updateStatus(List<Long> ids, Integer status) {
        List<YtsafetyLocationInfo> entities = this.listByIds(ids);
        if (entities.size() != ids.size()) {
            Set<Long> existIds = entities.stream().map(YtsafetyLocationInfo::getId).collect(Collectors.toSet());
            List<Long> missingIds = ids.stream().filter(id -> !existIds.contains(id)).collect(Collectors.toList());
            throw new RuntimeException("位置信息不存在，ID：" + missingIds);
        }
        entities.forEach(entity -> entity.setStatus(status));
        this.updateBatchById(entities);
    }

    @Async
    @Override
    public void syncLocationRiskLevel(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        List<YTSafetyHazardReport> reports = hazardReportMapper.selectList(
                new LambdaQueryWrapper<YTSafetyHazardReport>()
                        .in(YTSafetyHazardReport::getLocationId, ids)
                        .eq(YTSafetyHazardReport::getDelFlag, 0)
                        .select(YTSafetyHazardReport::getLocationId, YTSafetyHazardReport::getProblemLevel));

        Map<Long, Map<Integer, Long>> locationRiskCounts;
        if (CollectionUtils.isEmpty(reports)) {
            locationRiskCounts = Collections.emptyMap();
        } else {
            locationRiskCounts = reports.stream()
                    .collect(Collectors.groupingBy(
                            YTSafetyHazardReport::getLocationId,
                            Collectors.groupingBy(
                                    r -> r.getProblemLevel() != null ? r.getProblemLevel() : 0,
                                    Collectors.counting())));
        }

        List<YtsafetyLocationInfo> updateList = new ArrayList<>();
        for (Long locationId : ids) {
            Map<Integer, Long> levelCounts = locationRiskCounts.getOrDefault(locationId, Collections.emptyMap());

            long redCount = levelCounts.getOrDefault(4, 0L);
            long orangeCount = levelCounts.getOrDefault(3, 0L);
            long yellowCount = levelCounts.getOrDefault(2, 0L);
            long blueCount = levelCounts.getOrDefault(1, 0L);
            long totalCount = redCount + orangeCount + yellowCount + blueCount;

            YtsafetyLocationInfo info = new YtsafetyLocationInfo();
            info.setId(locationId);
            info.setRiskQuantity((int) totalCount);

            if (totalCount == 0) {
                info.setRiskLevel(null);
                info.setRiskColor(null);
            } else {
                double riskScore = Math.round(
                        (redCount * 10000.0 + orangeCount * 3000.0 + yellowCount * 750.0 + blueCount * 150.0)
                                / totalCount * 100.0) / 100.0;

                if (riskScore == 0) {
                    info.setRiskLevel(null);
                    info.setRiskColor(null);
                } else {
                    YTSafetyRiskLevelDict dict = riskLevelDictService.calculateRiskLevel(riskScore);
                    if (dict != null) {
                        info.setRiskLevel(dict.getRiskLevel());
                        info.setRiskColor(dict.getRiskColor());
                    }
                }
            }
            updateList.add(info);
        }

        locationInfoMapper.updateRiskLevel(updateList);
        log.info("syncLocationRiskLevel: 更新位置风险等级完成, 位置数={}", ids.size());
    }

    /**
     * 回填安全员名称：按 org_code + safety_role_code 批量查询安全责任人名称，
     * 多个人员用 "," 隔开；无匹配时置为空串
     */
    private void fillSafetyNames(List<YtsafetyLocationInfoVO> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        List<String[]> pairs = records.stream()
                .map(record -> new String[]{record.getOrgCode(), record.getSafetyRoleCode()})
                .collect(Collectors.toList());
        Map<String, String> nameMap = responsibleNameResolver.resolveNames(pairs);
        for (YtsafetyLocationInfoVO record : records) {
            String orgCode = record.getOrgCode();
            String roleCode = record.getSafetyRoleCode();
            if (StringUtils.isBlank(orgCode) || StringUtils.isBlank(roleCode)) {
                record.setSafetyNames("");
                continue;
            }
            record.setSafetyNames(nameMap.getOrDefault(orgCode + "_" + roleCode, ""));
        }
    }

    /**
     * 查询安全责任人名称：在 ytsafety_sys_responsible_mapping 中按组织编码+角色编码查询，
     * 当前组织查不到责任人时向上级组织递归查找，取组织路径上最近一级有责任人的部门，
     * 多人用 "," 隔开，无匹配时返回空串
     */
    private String queryResponsibleNames(String orgCode, String roleCode) {
        if (StringUtils.isBlank(orgCode) || StringUtils.isBlank(roleCode)) {
            return "";
        }
        String names = locationInfoMapper.queryResponsibleNames(orgCode, roleCode);
        return names == null ? "" : names;
    }

    private LambdaQueryWrapper<YtsafetyLocationInfo> buildQueryWrapper(YtsafetyLocationInfoQueryDTO dto) {
        LambdaQueryWrapper<YtsafetyLocationInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YtsafetyLocationInfo::getDelFlag, 0);
        // 位置编码 - 模糊
        if (StringUtils.isNotBlank(dto.getLocationCode())) {
            wrapper.like(YtsafetyLocationInfo::getLocationCode, dto.getLocationCode());
        }
        // 厂区 - 多选
        if (!CollectionUtils.isEmpty(dto.getFactoryIds())) {
            wrapper.in(YtsafetyLocationInfo::getFactoryId, dto.getFactoryIds());
        }
        // 区域 - 多选
        if (!CollectionUtils.isEmpty(dto.getAreaIds())) {
            wrapper.in(YtsafetyLocationInfo::getAreaId, dto.getAreaIds());
        }
        // 所属组织 - 单选
        if (dto.getOrgId() != null) {
            wrapper.eq(YtsafetyLocationInfo::getOrgId, dto.getOrgId());
        }
        // 位置名称 - 模糊
        if (StringUtils.isNotBlank(dto.getLocationName())) {
            wrapper.like(YtsafetyLocationInfo::getLocationName, dto.getLocationName());
        }
        // 风险等级 - 单选
        if (StringUtils.isNotBlank(dto.getRiskLevel())) {
            wrapper.eq(YtsafetyLocationInfo::getRiskLevel, dto.getRiskLevel());
        }
        // 状态 - 单选
        if (dto.getStatus() != null) {
            wrapper.eq(YtsafetyLocationInfo::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(YtsafetyLocationInfo::getUpdateTime);
        return wrapper;
    }

}
