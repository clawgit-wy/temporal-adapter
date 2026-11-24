package com.yutong.safety.core.db.mapper;

import com.yutong.safety.core.db.entity.SafetyCarOutRuleDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SafetyZCustomUtilDOBizMapper {
    List<Map> getPost();

    List<String> findEmpByRule(List<Map> newRulesList);

    List<Map> findChangedRule();
    List<Map> findChangedRuleAll();

    List<String> findChangedEmp();

    Map<String,String> findDefFlag(@Param("empNo") String empNo);

    Map<String, String> findDeptFlag(String empNo);

    Map<String, String> findPostFlag(String empNo);

    int deleteRule();

    int insertHistory(@Param("ruleDO") SafetyCarOutRuleDO ruleDO);

    int updateHistory(@Param("ruleDO") SafetyCarOutRuleDO ruleDO);

    int findEmpNums(String empNo);
}