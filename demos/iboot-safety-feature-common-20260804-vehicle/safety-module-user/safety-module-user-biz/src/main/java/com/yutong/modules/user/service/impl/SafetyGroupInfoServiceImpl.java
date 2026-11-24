package com.yutong.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.user.api.ISafetyGroupInfoService;
import com.yutong.modules.user.convert.SafetyGroupInfoConvert;
import com.yutong.modules.user.entity.SafetyGroupInfo;
import com.yutong.modules.user.mapper.SafetyGroupInfoMapper;
import com.yutong.modules.user.vo.SafetyGroupInfoOptionListVO;
import com.yutong.modules.user.vo.SafetyGroupInfoOptionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 部门表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Service
public class SafetyGroupInfoServiceImpl extends ServiceImpl<SafetyGroupInfoMapper, SafetyGroupInfo> implements ISafetyGroupInfoService {

    @Autowired
    private SafetyGroupInfoConvert safetyGroupInfoConvert;

    @Autowired
    private SafetyGroupInfoMapper mapper;

    @Override
    public List<SafetyGroupInfoOptionListVO> optionlist() {
        LambdaQueryWrapper<SafetyGroupInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<SafetyGroupInfo> list = this.list(lambdaQueryWrapper);

        return safetyGroupInfoConvert.entityToOptionListVoList(list);
    }

    @Override
    public List<SafetyGroupInfoOptionVO> getList() {
        return mapper.getList();
    }
}
