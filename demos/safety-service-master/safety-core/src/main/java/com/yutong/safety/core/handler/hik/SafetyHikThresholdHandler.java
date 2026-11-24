package com.yutong.safety.core.handler.hik;

import cn.hutool.core.lang.UUID;
import com.yutong.safety.core.db.entity.hik.SafetyHikThresholdDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikThresholdDOExample;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikThresholdAddParam;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikThresholdQueryParam;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import com.yutong.safety.core.service.hik.SafetyHikThresholdService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class SafetyHikThresholdHandler extends AbstractSafetyWebBaseHandler<SafetyHikThresholdDO, SafetyHikThresholdDOExample, SafetyHikThresholdQueryParam, SafetyHikThresholdAddParam> {

    @Resource
    private SafetyHikThresholdService safetyHikThresholdService;

    @Override
    public AbstractSafetyWebBaseService<SafetyHikThresholdDO, SafetyHikThresholdDOExample, SafetyHikThresholdQueryParam> getService() {
        return safetyHikThresholdService;
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
    public Pair<String[], String[][]> processExportData(List<SafetyHikThresholdDO> dataList) {
        return null;
    }

    @Override
    protected void beforeSave(SafetyHikThresholdDO data) {
        if (data.saveOrUpdate() && StringUtils.isBlank(data.getSceneCode())) {
            data.setSceneCode("scene_" + UUID.randomUUID().toString());
        }
    }
}
