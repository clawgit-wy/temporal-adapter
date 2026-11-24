package com.yutong.safety.core.db.mapper.park;

import com.yutong.safety.core.db.entity.park.SafetyParkPowerRuleDO;
import com.yutong.safety.core.db.entity.park.param.ParkPowerRuleListParam;
import com.yutong.safety.core.db.entity.park.vo.ParkPowerRuleChangeVO;
import com.yutong.safety.core.db.entity.park.vo.ParkPowerRuleVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SafetyParkPowerRuleDOBizMapper {

    List<ParkPowerRuleVO> selectList(ParkPowerRuleListParam param);

    Long selectCount(ParkPowerRuleListParam param);

    int deleteByPrimaryKey(Integer id);

    int insert(SafetyParkPowerRuleDO record);

    int insertSelective(SafetyParkPowerRuleDO record);

    SafetyParkPowerRuleDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SafetyParkPowerRuleDO record);

    int updateBatchByIdList(@Param("record") SafetyParkPowerRuleDO record,@Param("idList") List<String> idList);

    int updateByPrimaryKey(SafetyParkPowerRuleDO record);

    List<String> selectEmpByRule(List<ParkPowerRuleChangeVO> newRulesList);

    List<SafetyParkPowerRuleDO> selectRuleByEmpNo(@Param("empNo") String empNo);

    List<ParkPowerRuleVO> selectRulesForEmp(@Param("list") List<String> empNoList);

}