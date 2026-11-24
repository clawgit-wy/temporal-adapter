package com.yutong.safety.core.db.mapper.enter;

import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingDock;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterAuthRt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SafetyEnterCheckMapper {
    /**
     * 验证是否在黑名单
     * */
    Integer checkUserInBlack(Map map);
    Integer checkUserInBlackBatch(@Param("list") List<String> list);
    /**
     * 验证接口人
     * */
    SafetyEnterFilingDock checkDockInfo(SafetyEnterFilingDock dock);
    List<SafetyEnterAuthRt> checkDockAuthBlack(@Param("arr") String[] arr);
}
