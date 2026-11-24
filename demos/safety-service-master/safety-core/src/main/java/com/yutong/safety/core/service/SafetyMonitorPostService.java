package com.yutong.safety.core.service;


import com.yutong.safety.common.param.web.ProcessParam;
import com.yutong.safety.core.db.mapper.SafetyMonitorPostMapper;
import com.yutong.safety.core.dto.SafetyMonitorPostProcessDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 监控岗位上报流程服务实现
 * @author lukunpeng
 * @time 2024-4-30
 */
@Service
public class SafetyMonitorPostService {

    @Autowired
    SafetyMonitorPostMapper safetyMonitorPostMapper;

    public int addData(SafetyMonitorPostProcessDTO dto) {
        return safetyMonitorPostMapper.insert(dto);
    }

    public int updateData(SafetyMonitorPostProcessDTO dto) {
        return safetyMonitorPostMapper.updateData(dto);
    }

    public SafetyMonitorPostProcessDTO getDetail(int id) {
        return safetyMonitorPostMapper.queryById(id);
    }

    public void insertCheckor(SafetyMonitorPostProcessDTO dto) {
        safetyMonitorPostMapper.insertCheckor(Arrays.asList(dto.getCheckUserno().split(",")),dto.getId());
    }

    public List<SafetyMonitorPostProcessDTO> getList(SafetyMonitorPostProcessDTO processDTO) {
        int pageNo = processDTO.getPageNum();
        pageNo = pageNo==0 ? 1:pageNo;
        return safetyMonitorPostMapper.findPageList(processDTO, (pageNo-1)*15);
    }

    public int getCount(SafetyMonitorPostProcessDTO processDTO) {
        return safetyMonitorPostMapper.findCount(processDTO);
    }

    public List<SafetyMonitorPostProcessDTO> exportData(SafetyMonitorPostProcessDTO processDTO) {
        return safetyMonitorPostMapper.findList(processDTO);
    }
}
