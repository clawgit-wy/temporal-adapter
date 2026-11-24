package com.yutong.modules.risk.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.mapper.YTSafetyMonitorPostMapper;
import com.yutong.modules.risk.dto.SafetyMonitorPostProcessDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 监控岗位上报流程服务实现
 * @author lukunpeng
 * @time 2024-4-30
 */
@Service
public class YTSafetyMonitorPostService {

    @Autowired
    YTSafetyMonitorPostMapper safetyMonitorPostMapper;

    public Long addData(SafetyMonitorPostProcessDTO dto) {
        return safetyMonitorPostMapper.insert(dto);
    }

    public Long updateData(SafetyMonitorPostProcessDTO dto) {
        return safetyMonitorPostMapper.updateData(dto);
    }

    public Long updateFlowId(Long id, String flowId, Long modelId) {
        return safetyMonitorPostMapper.updateFlowId(id, flowId, modelId);
    }

    public SafetyMonitorPostProcessDTO getDetail(Long id) {
        return safetyMonitorPostMapper.queryById(id);
    }

    public void insertCheckor(SafetyMonitorPostProcessDTO dto) {
        if (StringUtils.isBlank(dto.getCheckUserno())) {
            return;
        }
        List<String> checkUsernoList = Arrays.asList(dto.getCheckUserno().split(","));
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < checkUsernoList.size(); i++) {
            ids.add(IdWorker.getId());
        }
        safetyMonitorPostMapper.insertCheckor(ids, checkUsernoList, dto.getId());
    }

    public IPage<SafetyMonitorPostProcessDTO> getList(Page<SafetyMonitorPostProcessDTO> page, SafetyMonitorPostProcessDTO processDTO) {
        return safetyMonitorPostMapper.findPageList(page, processDTO);
    }

    public List<SafetyMonitorPostProcessDTO> exportData(SafetyMonitorPostProcessDTO processDTO) {
        return safetyMonitorPostMapper.findList(processDTO);
    }
}
