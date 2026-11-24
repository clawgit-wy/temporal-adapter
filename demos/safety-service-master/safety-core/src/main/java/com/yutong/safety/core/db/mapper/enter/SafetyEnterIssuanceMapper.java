package com.yutong.safety.core.db.mapper.enter;

import com.yutong.safety.core.db.entity.enter.SafetyEnterIssuanceDO;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterIssuanceRT;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterIssuanceSh;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SafetyEnterIssuanceMapper {

    Integer queryCount(SafetyEnterIssuanceSh enterFactoryIssuance);

    List<SafetyEnterIssuanceRT> queryList(SafetyEnterIssuanceSh enterFactoryIssuance);

    int save(@Param("data") SafetyEnterIssuanceDO enterFactoryIssuance);

    int saveBatch(List<SafetyEnterIssuanceDO> issuanceDOList);

    int update(@Param("data")SafetyEnterIssuanceDO enterFactoryIssuance);

    void updateBatch(@Param("list") List<SafetyEnterIssuanceDO> list);
}
