package com.yutong.safety.core.handler.build;

import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.build.SafetyBuildInfoRecordDO;
import com.yutong.safety.core.dto.build.SafetyBuildInfoRecordDTO;
import com.yutong.safety.core.service.build.SafetyBuildInfoRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SafetyBuildInfoRecordHandler {
    @Autowired
    SafetyBuildInfoRecordService safetyBuildInfoRecordService;

    public Object query(SafetyBuildInfoRecordDTO param) {
        param.setNeedPage("1");
        List<SafetyBuildInfoRecordDO> list = safetyBuildInfoRecordService.selectPageList(param);
        long total = safetyBuildInfoRecordService.selectListCount(param);
        return new PageVO<>(total, list);
    }

    public void update(SafetyBuildInfoRecordDTO param) {
        SafetyBuildInfoRecordDO en = new SafetyBuildInfoRecordDO();
        BeanUtils.copyProperties(param, en);
        safetyBuildInfoRecordService.update(en);
    }
}
