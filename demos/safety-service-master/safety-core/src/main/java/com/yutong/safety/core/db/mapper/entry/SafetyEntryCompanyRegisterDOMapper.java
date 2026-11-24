package com.yutong.safety.core.db.mapper.entry;

import com.yutong.safety.core.db.entity.entry.SafetyEntryCompanyRegisterDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryCompanyRegisterDOExample;
import java.util.List;

import com.yutong.safety.core.db.entity.entry.vo.EntryOverdueTaskVO;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import org.apache.ibatis.annotations.Param;

public interface SafetyEntryCompanyRegisterDOMapper extends SafetyWebBaseMapper<SafetyEntryCompanyRegisterDO, SafetyEntryCompanyRegisterDOExample> {
    List<EntryOverdueTaskVO> selectEntryOverdueTask();
}