package com.yutong.safety.core.db.mapper.visitor;

import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationApprovalDO;
import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationApprovalDOExample;
import com.yutong.safety.core.db.entity.visitor.param.SafetyVisitorAuthorizationQueryParam;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import com.yutong.safety.core.dto.visitor.SafetyVisitorAuthorizationDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyVisitorAuthorizationApprovalDOMapper extends SafetyWebBaseMapper<SafetyVisitorAuthorizationApprovalDO, SafetyVisitorAuthorizationApprovalDOExample> {

    List<SafetyVisitorAuthorizationDto> selectPageList(SafetyVisitorAuthorizationQueryParam param);

    long selectPageCount(SafetyVisitorAuthorizationQueryParam param);

    void batchSave(@Param("list") List<SafetyVisitorAuthorizationApprovalDO> saveList);
}