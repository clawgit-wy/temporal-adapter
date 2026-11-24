package com.yutong.modules.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.common.api.bpm.ISafetyBpmModelService;
import com.yutong.modules.common.dto.SafetyBpmModelDTO;
import com.yutong.modules.common.dto.SafetyBpmModelQueryDTO;
import com.yutong.modules.common.entity.SafetyBpmCallBack;
import com.yutong.modules.common.entity.SafetyBpmModel;
import com.yutong.modules.common.mapper.SafetyBpmCallBackMapper;
import com.yutong.modules.common.mapper.SafetyBpmModelMapper;
import com.yutong.modules.common.vo.SafetyBpmModelVO;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 对接BPM系统模块表
 * @Author: jeecg-boot
 * @Date:   2026-06-18
 * @Version: V1.0
 */
@Service
public class SafetyBpmModelServiceImpl extends ServiceImpl<SafetyBpmModelMapper, SafetyBpmModel> implements ISafetyBpmModelService {

    @Autowired
    private SafetyBpmModelMapper safetyBpmModelMapper;

    @Autowired
    private SafetyBpmCallBackMapper  safetyBpmCallBackMapper;

    @Value("${ytsafety.custom.constant.bpmSysId}")
    private String bpmSysId;

    @Value("${ytsafety.custom.constant.sysGuestRoleCode}")
    private String sysGuestRoleCode;

    @Override
    public SafetyBpmModel addSafetyBpmModel(SafetyBpmModelDTO safetyBpmModelDTO) {
        SafetyBpmModel model = new SafetyBpmModel();
        BeanUtils.copyProperties(safetyBpmModelDTO,model);
        model.setSysId(bpmSysId);
        Boolean isExist = safetyBpmModelMapper.isModelExist(safetyBpmModelDTO.getModelId());
        if(isExist) throw new RuntimeException("模型已经存在");
        safetyBpmModelMapper.insert(model);
        return model;
    }

    @Override
    public SafetyBpmModel deleteSafetyBpmModel(Long id) {
        SafetyBpmModel model = safetyBpmModelMapper.selectById(id);
        safetyBpmModelMapper.deleteById(model);
        LambdaQueryWrapper<SafetyBpmCallBack> queryCallBack = new LambdaQueryWrapper<>();
        queryCallBack.eq(SafetyBpmCallBack::getModelId,model.getModelId());
        safetyBpmCallBackMapper.delete(queryCallBack);
        return model;
    }

    @Override
    public SafetyBpmModel editSafetyBpmModel(SafetyBpmModelDTO safetyBpmModelDTO) {
        SafetyBpmModel model = safetyBpmModelMapper.selectById(safetyBpmModelDTO.getId());
        String modelId = model.getModelId();
        SafetyBpmModel modelUpdate = new SafetyBpmModel();
        BeanUtils.copyProperties(safetyBpmModelDTO,modelUpdate);
        modelUpdate.setModelId(modelId);
        safetyBpmModelMapper.updateById(modelUpdate);
        return model;
    }

    @Override
    public SafetyBpmModelVO queryById(Long id) {
        return safetyBpmModelMapper.queryById(id);
    }

    @Override
    public IPage<SafetyBpmModelVO> queryPageList(Page<SafetyBpmModelVO> page, SafetyBpmModelQueryDTO safetyBpmModelQueryDTO) {
        LambdaQueryWrapper<SafetyBpmModel> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotEmpty(safetyBpmModelQueryDTO.getModelId())){
            queryWrapper.eq(SafetyBpmModel::getModelId,safetyBpmModelQueryDTO.getModelId());
        }
        if(StringUtils.isNotEmpty(safetyBpmModelQueryDTO.getModelName())){
            queryWrapper.like(SafetyBpmModel::getModelName,safetyBpmModelQueryDTO.getModelName());
        }
        if(safetyBpmModelQueryDTO.getModelType()!=null){
            queryWrapper.eq(SafetyBpmModel::getModelType,safetyBpmModelQueryDTO.getModelType());
        }
        return safetyBpmModelMapper.queryPage(page,queryWrapper);
    }

    @Override
    public List<SafetyBpmModelVO> queryList(SafetyBpmModelQueryDTO safetyBpmModelQueryDTO) {
        LambdaQueryWrapper<SafetyBpmModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SafetyBpmModel::getIsShow,1);
        if(StringUtils.isNotEmpty(safetyBpmModelQueryDTO.getModelId())){
            queryWrapper.eq(SafetyBpmModel::getModelId,safetyBpmModelQueryDTO.getModelId());
        }
        if(StringUtils.isNotEmpty(safetyBpmModelQueryDTO.getModelName())){
            queryWrapper.like(SafetyBpmModel::getModelName,safetyBpmModelQueryDTO.getModelName());
        }
        if(safetyBpmModelQueryDTO.getModelType()!=null){
            queryWrapper.eq(SafetyBpmModel::getModelType,safetyBpmModelQueryDTO.getModelType());
        }
        LoginUser user = BusUtits.getLoginUser();
        List<Long> permIds=null;
        if(user==null||user.getUserId()==null){
            permIds = safetyBpmModelMapper.queryUserFlowPermByRole(sysGuestRoleCode);
        }else{
            permIds = safetyBpmModelMapper.queryUserFlowPerm(user.getUserId());
        }
        if(permIds==null|| permIds.isEmpty()) new ArrayList<SafetyBpmModelVO>();
        if(permIds!=null&& !permIds.isEmpty()){
            queryWrapper.in(SafetyBpmModel::getPermId,permIds);
        }
        return safetyBpmModelMapper.queryList(queryWrapper);
    }


}
