package com.yutong.safety.core.service.entry;

import com.yutong.safety.core.db.entity.entry.SafetyEntryCompanyRegisterHistoryDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryCompanyRegisterHistoryDOExample;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryCompanyRegisterQueryParams;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import com.yutong.safety.core.db.mapper.entry.SafetyEntryCompanyRegisterHistoryDOMapper;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 因为SafetyEntryCompanyRegisterHistoryDO 和SafetyEntryCompanyRegisterDO 实体类对应的表字段一致， 所以SafetyEntryCompanyRegisterQueryParams 不变
 */
@Service
public class SafetyEntryCompanyRegisterHistoryService extends AbstractSafetyWebBaseService<SafetyEntryCompanyRegisterHistoryDO, SafetyEntryCompanyRegisterHistoryDOExample, SafetyEntryCompanyRegisterQueryParams> {

    @Resource
    private SafetyEntryCompanyRegisterHistoryDOMapper safetyEntryCompanyRegisterHistoryDOMapper;

    @Override
    protected SafetyWebBaseMapper<SafetyEntryCompanyRegisterHistoryDO, SafetyEntryCompanyRegisterHistoryDOExample> getMapper() {
        return safetyEntryCompanyRegisterHistoryDOMapper;
    }

    @Override
    protected void formatExample(SafetyEntryCompanyRegisterHistoryDOExample example, SafetyEntryCompanyRegisterQueryParams param, boolean pageType) {

    }

    @Override
    protected void batchDelete(SafetyEntryCompanyRegisterHistoryDOExample example, List<String> idList) {

    }

    @Override
    protected void isExist(SafetyEntryCompanyRegisterHistoryDO data, SafetyEntryCompanyRegisterHistoryDOExample example) {

    }

    /**
     * 根据员工号查询表safety_entry_company_register的数据插入到 safety_entry_company_register_history表中
     * @param empNo 对接人员工号
     */
    public void saveSelect(String empNo) {
        safetyEntryCompanyRegisterHistoryDOMapper.saveSelect(empNo);
    }
}
