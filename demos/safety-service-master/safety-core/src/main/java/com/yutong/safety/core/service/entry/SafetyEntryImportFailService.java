package com.yutong.safety.core.service.entry;

import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDetailDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfig;
import com.yutong.safety.core.db.entity.entry.SafetyEntryImportFailDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryImportFailDOExample;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryImportFailQueryParams;
import com.yutong.safety.core.db.mapper.SafetyBlackManageDOBizMapper;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import com.yutong.safety.core.db.mapper.entry.SafetyEntryImportFailDOMapper;
import com.yutong.safety.core.dto.BlackMessageDto;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SafetyEntryImportFailService extends AbstractSafetyWebBaseService<SafetyEntryImportFailDO, SafetyEntryImportFailDOExample, SafetyEntryImportFailQueryParams> {

    @Resource
    private SafetyEntryImportFailDOMapper safetyEntryImportFailDOMapper;
    @Resource
    private SafetyBlackManageDOBizMapper safetyBlackManageDOBizMapper;

    @Override
    protected SafetyWebBaseMapper<SafetyEntryImportFailDO, SafetyEntryImportFailDOExample> getMapper() {
        return safetyEntryImportFailDOMapper;
    }

    @Override
    protected void formatExample(SafetyEntryImportFailDOExample example, SafetyEntryImportFailQueryParams param, boolean pageType) {
        example.setOrderByClause(" create_time desc");
        SafetyEntryImportFailDOExample.Criteria criteria = example.createCriteria();
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);
        if (StringUtils.isNotBlank(param.getVersionNo())) {
            criteria.andVersionNoEqualTo(param.getVersionNo());
        }
        if(StringUtils.isNotBlank(param.getFailRemark())){
            criteria.andFailRemarkEqualTo(param.getFailRemark());
        }
    }

    @Override
    protected void batchDelete(SafetyEntryImportFailDOExample example, List<String> idList) {

    }

    @Override
    protected void isExist(SafetyEntryImportFailDO data, SafetyEntryImportFailDOExample example) {

    }
    /**
     * 批量新增
     */
    public void batchSave(List<SafetyEntryImportFailDO> dataList) {
        List<List<SafetyEntryImportFailDO>> list = ListUtils.partition(dataList, 1000);
        list.forEach(li -> safetyEntryImportFailDOMapper.batchSave(li));
    }

    /**
     * 根据版本删除
     * @param versionNo 版本号
     */
    public void deleteByVersionNo(String versionNo) {
        SafetyEntryImportFailDO importFailDO = new SafetyEntryImportFailDO();
        importFailDO.setDelFlag(SafetyWebBaseEntity.DELETE);
        SafetyEntryImportFailDOExample example = new SafetyEntryImportFailDOExample();
        SafetyEntryImportFailDOExample.Criteria criteria = example.createCriteria();
        criteria.andVersionNoEqualTo(versionNo);
        criteria.andDelFlagEqualTo(SafetyWebBaseEntity.NORMAL);

        safetyEntryImportFailDOMapper.updateByExampleSelective(importFailDO, example);
    }
}
