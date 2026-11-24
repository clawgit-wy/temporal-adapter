package com.yutong.modules.vehicle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yutong.modules.vehicle.api.IYTSafetyCarPersonnelChangeInfoService;
import com.yutong.modules.vehicle.convert.SafetyCarPersonnelChangeInfoConvert;
import com.yutong.modules.vehicle.dto.YTSafetyCarPersonnelChangeInfoDTO;
import com.yutong.modules.vehicle.entity.YTSafetyCarPersonnelChangeInfo;
import com.yutong.modules.vehicle.mapper.YTSafetyCarPersonnelChangeInfoMapper;
import com.yutong.modules.vehicle.vo.YTSafetyCarPersonnelChangeInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: 车辆人员变更信息表
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Service
public class YTSafetyCarPersonnelChangeInfoServiceImpl extends ServiceImpl<YTSafetyCarPersonnelChangeInfoMapper, YTSafetyCarPersonnelChangeInfo> implements IYTSafetyCarPersonnelChangeInfoService {

    @Autowired
    SafetyCarPersonnelChangeInfoConvert safetyCarPersonnelChangeInfoConvert;
    @Override
    public void add(YTSafetyCarPersonnelChangeInfo personnelChangeInfo) {
        baseMapper.insert(personnelChangeInfo);
    }

    @Override
    public List<YTSafetyCarPersonnelChangeInfoVO> queryByProcessId(Long processId) {
        LambdaQueryWrapper<YTSafetyCarPersonnelChangeInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(YTSafetyCarPersonnelChangeInfo::getProcessId, processId);
        List<YTSafetyCarPersonnelChangeInfo> carPersonnelChangeInfos = baseMapper.selectList(lambdaQueryWrapper);
        return safetyCarPersonnelChangeInfoConvert.entityListToVoList(carPersonnelChangeInfos);
    }

    @Override
    public void updateList(List<YTSafetyCarPersonnelChangeInfoDTO> carPersonnelChangeInfoList) {
        if (carPersonnelChangeInfoList == null || carPersonnelChangeInfoList.isEmpty()){
            return;
        }
        // 转为 Map<Long, YTSafetyCarPersonnelChangeInfoVO>，key 是 id，value 是对象本身
        Map<Long, YTSafetyCarPersonnelChangeInfoDTO> idMap = carPersonnelChangeInfoList.stream()
                .filter(item -> item.getId() != null)  // 过滤 id 为空的
                .collect(Collectors.toMap(
                        YTSafetyCarPersonnelChangeInfoDTO::getId,
                        Function.identity(),
                        (existing, replacement) -> existing // 冲突处理：有重复 id 时保留第一个
                ));

        List<Long> ids = carPersonnelChangeInfoList.stream()
                .map(YTSafetyCarPersonnelChangeInfoDTO::getId)
                .collect(Collectors.toList());
        if (ids.isEmpty()){
            return;
        }
        List<YTSafetyCarPersonnelChangeInfo> carPersonnelChangeInfos = baseMapper.selectBatchIds(ids);
        for (YTSafetyCarPersonnelChangeInfo carPersonnelChangeInfo : carPersonnelChangeInfos){
            YTSafetyCarPersonnelChangeInfoDTO changeInfoDTO = idMap.get(carPersonnelChangeInfo.getId());
            carPersonnelChangeInfo.setIsReplacePersonnel(changeInfoDTO.getIsReplacePersonnel());
            carPersonnelChangeInfo.setChangePersonnel(changeInfoDTO.getChangePersonnel());
            baseMapper.updateById(carPersonnelChangeInfo);
        }
    }
}
