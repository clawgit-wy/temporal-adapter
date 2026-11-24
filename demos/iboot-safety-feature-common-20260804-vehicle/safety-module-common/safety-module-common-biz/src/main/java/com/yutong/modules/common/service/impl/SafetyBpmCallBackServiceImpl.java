package com.yutong.modules.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.common.api.bpm.ISafetyBpmCallBackService;
import com.yutong.modules.common.dto.SafetyBpmCallBackDTO;
import com.yutong.modules.common.dto.SafetyBpmCallBackQueryDTO;
import com.yutong.modules.common.entity.SafetyBpmCallBack;
import com.yutong.modules.common.mapper.SafetyBpmCallBackMapper;
import com.yutong.modules.common.vo.SafetyBpmCallBackVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: BPM回调方法
 * @Author: lss
 * @Date:   2026-06-18
 * @Version: V1.0
 */
@Service
public class SafetyBpmCallBackServiceImpl extends ServiceImpl<SafetyBpmCallBackMapper, SafetyBpmCallBack> implements ISafetyBpmCallBackService {

    @Autowired
    private SafetyBpmCallBackMapper safetyBpmCallBackMapper;

    @Override
    public SafetyBpmCallBack addSafetyBpmCall(SafetyBpmCallBackDTO safetyBpmCallBackDTO) {
        SafetyBpmCallBack funcEntity = new SafetyBpmCallBack();
        BeanUtils.copyProperties(safetyBpmCallBackDTO,funcEntity);
        Boolean isExist = safetyBpmCallBackMapper.isFuncExist(safetyBpmCallBackDTO.getFunctionId(),safetyBpmCallBackDTO.getModelId());
        if(isExist) throw new RuntimeException("模型方法已经存在");
        safetyBpmCallBackMapper.insert(funcEntity);
        return funcEntity;
    }

    @Override
    public SafetyBpmCallBack deleteSafetyBpmCall(Long id) {
        SafetyBpmCallBack funcEntity = safetyBpmCallBackMapper.selectById(id);
        safetyBpmCallBackMapper.deleteById(funcEntity);
        return funcEntity;
    }

    @Override
    public SafetyBpmCallBack editSafetyBpmCall(SafetyBpmCallBackDTO safetyBpmCallBackDTO) {
        SafetyBpmCallBack funcEntity = safetyBpmCallBackMapper.selectById(safetyBpmCallBackDTO.getId());
        Boolean isExist = safetyBpmCallBackMapper.isFuncExist(safetyBpmCallBackDTO.getFunctionId(),funcEntity.getModelId());
        if(isExist&&!funcEntity.getFunctionId().equals(safetyBpmCallBackDTO.getFunctionId())) throw new RuntimeException("模型方法已经存在");
        SafetyBpmCallBack modelUpdate = new SafetyBpmCallBack();
        BeanUtils.copyProperties(safetyBpmCallBackDTO,modelUpdate);
        safetyBpmCallBackMapper.updateById(modelUpdate);
        return funcEntity;
    }

    @Override
    public SafetyBpmCallBackVO queryById(Long id) {
        return safetyBpmCallBackMapper.queryById(id);
    }

    @Override
    public IPage<SafetyBpmCallBackVO> queryPageList(Page<SafetyBpmCallBackVO> page, SafetyBpmCallBackQueryDTO safetyBpmModelQueryDTO) {
        LambdaQueryWrapper<SafetyBpmCallBack> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotEmpty(safetyBpmModelQueryDTO.getFunctionId())){
            queryWrapper.eq(SafetyBpmCallBack::getFunctionId,safetyBpmModelQueryDTO.getFunctionId());
        }
        if(StringUtils.isNotEmpty(safetyBpmModelQueryDTO.getFunctionName())){
            queryWrapper.eq(SafetyBpmCallBack::getFunctionName,safetyBpmModelQueryDTO.getFunctionName());
        }
        if(StringUtils.isNotEmpty(safetyBpmModelQueryDTO.getModelId())){
            queryWrapper.eq(SafetyBpmCallBack::getModelId,safetyBpmModelQueryDTO.getModelId());
        }
        return safetyBpmCallBackMapper.queryPage(page,queryWrapper);
    }

    @Override
    public List<SafetyBpmCallBackVO> queryList(SafetyBpmCallBackQueryDTO safetyBpmModelQueryDTO) {
        LambdaQueryWrapper<SafetyBpmCallBack> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotEmpty(safetyBpmModelQueryDTO.getFunctionId())){
            queryWrapper.eq(SafetyBpmCallBack::getFunctionId,safetyBpmModelQueryDTO.getFunctionId());
        }
        if(StringUtils.isNotEmpty(safetyBpmModelQueryDTO.getFunctionName())){
            queryWrapper.eq(SafetyBpmCallBack::getFunctionName,safetyBpmModelQueryDTO.getFunctionName());
        }
        if(StringUtils.isNotEmpty(safetyBpmModelQueryDTO.getModelId())){
            queryWrapper.eq(SafetyBpmCallBack::getModelId,safetyBpmModelQueryDTO.getModelId());
        }
        return safetyBpmCallBackMapper.queryList(queryWrapper);
    }
}
