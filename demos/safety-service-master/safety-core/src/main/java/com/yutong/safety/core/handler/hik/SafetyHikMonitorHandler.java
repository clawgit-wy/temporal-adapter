package com.yutong.safety.core.handler.hik;

import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.hik.SafetyHikMonitorDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikMonitorDOExample;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikMonitorAddParam;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikMonitorQueryParam;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import com.yutong.safety.core.service.hik.SafetyHikMonitorService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SafetyHikMonitorHandler extends AbstractSafetyWebBaseHandler<SafetyHikMonitorDO, SafetyHikMonitorDOExample, SafetyHikMonitorQueryParam, SafetyHikMonitorAddParam> {
    @Resource
    private SafetyArtemisHandler safetyArtemisHandler;

    @Resource
    private SafetyHikMonitorService safetyHikMonitorService;

    @Override
    public AbstractSafetyWebBaseService<SafetyHikMonitorDO, SafetyHikMonitorDOExample, SafetyHikMonitorQueryParam> getService() {
        return safetyHikMonitorService;
    }

    @Override
    public String getModuleName() {
        return null;
    }

    @Override
    protected String getTemplateName() {
        return null;
    }

    @Override
    protected String getRoleName() {
        return null;
    }

    @Override
    public Pair<String[], String[][]> processExportData(List<SafetyHikMonitorDO> dataList) {
        return null;
    }

    public VO syncUpdateInfo(String type, String realName) {
        // 海康平台数据
        List<SafetyHikMonitorDO> hikList = safetyArtemisHandler.queryCameraAdvance(type, 1);
        if (CollectionUtils.isEmpty(hikList)) {
            return VoUtil.success();
        }

        List<SafetyHikMonitorDO> list = safetyHikMonitorService.selectList(new SafetyHikMonitorQueryParam(), false);
        if (CollectionUtils.isEmpty(list)) {
            hikList.forEach(li -> li.setCreates("", realName));
            safetyHikMonitorService.batchSave(hikList);
        } else {
            Set<String> indexCodes = list.stream().map(SafetyHikMonitorDO::getIndexCode).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
            List<SafetyHikMonitorDO> updateList = new ArrayList<>();
            List<SafetyHikMonitorDO> saveList = new ArrayList<>();
            hikList.forEach(li -> {
                // 存在
                if (indexCodes.contains(li.getIndexCode())) {
                    li.setUpdateBy(realName);
                    li.setUpdateTime(new Date());
                    updateList.add(li);
                } else {
                    li.setCreates("", realName);
                    saveList.add(li);
                }
            });
            Set<String> hikIndexCodes = hikList.stream().map(SafetyHikMonitorDO::getIndexCode).filter(StringUtils::isNotBlank).collect(Collectors.toSet());

            List<SafetyHikMonitorDO> collect = list.stream().filter(li -> StringUtils.isNotBlank(li.getIndexCode()) && !hikIndexCodes.contains(li.getIndexCode())).map(li -> {
                SafetyHikMonitorDO info = new SafetyHikMonitorDO();
                BeanUtils.copyProperties(li, info);
                info.setUpdateBy(realName);
                info.setUpdateTime(new Date());
                info.setDelFlag(1);
                return info;
            }).collect(Collectors.toList());
            updateList.addAll(collect);

            if (!CollectionUtils.isEmpty(updateList)) {
                safetyHikMonitorService.batchUpdate(updateList);
            }

            if (!CollectionUtils.isEmpty(saveList)) {
                safetyHikMonitorService.batchSave(saveList);
            }
        }
        return VoUtil.success();
    }
}
