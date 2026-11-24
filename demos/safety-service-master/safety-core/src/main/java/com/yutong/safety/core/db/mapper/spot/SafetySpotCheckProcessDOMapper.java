package com.yutong.safety.core.db.mapper.spot;

import com.yutong.safety.core.db.entity.spot.SafetySpotCheckRecordDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckResultDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckRuleDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotLinkRecordRuleDO;
import com.yutong.safety.core.dto.SafetySpotCheckProcessDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SafetySpotCheckProcessDOMapper {

    SafetySpotCheckProcessDTO findForAdd(String deviceId);

    int authUser(@Param("deviceId") String deviceId, @Param("username") String username,@Param("dat") String dat);

    List<String> findIds(String deviceId);

    List<String> findIdsAudit(String flowId);

    void insert(SafetySpotCheckProcessDTO processDTO);

    void updateRecord(@Param("recordDO") SafetySpotCheckRecordDO recordDO, @Param("recordIds") List<String> recordIds, @Param("flowId") String flowId);

    void updateLinkRecordRule(@Param("recordRuleDOS") List<SafetySpotLinkRecordRuleDO> recordRuleDOS, @Param("recordIds") List<String> recordIds);

    SafetySpotCheckProcessDTO findForView(String flowId);

    void updateProcessData(SafetySpotCheckProcessDTO processDTO);

    void insertResult(@Param("resultDTO") SafetySpotCheckResultDO resultDTO, @Param("recordIds") List<String> recordIds);

    void updateProcessDelayTime(@Param("flowId") String flowId, @Param("delayTime") String delayTime);

    void updateRecordDelayTime(@Param("flowId") String flowId, @Param("delayTime") String delayTime);

    List<SafetySpotCheckRuleDO> findRules(String deviceId);

    List<SafetySpotLinkRecordRuleDO> findLinkRules(String deviceId);

    List<SafetySpotLinkRecordRuleDO> findLinkRulesTwo(@Param("deviceId")String deviceId,@Param("recordId")Integer recordId);

    void updateRecordStatus(@Param("flowId") String flowId, @Param("user") Map<String, String> reformUser, @Param("type") String type);

    List<SafetySpotCheckResultDO> findRecordResult(@Param("recordId") String recordId);

    Map<String, String> getEmpInfoByUsername(String username);

    List<String> selectHistoryResult(String flowId);

    void updateHistoryResult(@Param("resultIds") List<String> resultIds, @Param("delayTime") String delayTime);

    String getFactoryById(String deviceId);

    List<SafetySpotCheckProcessDTO> selectRemindProcessList(Map<String,Object> params);

    void updateValidDetailU(@Param("validResult") String validResult,@Param("flowId") String flowId,@Param("validRemark")String validRemark,@Param("user")Map<String, String> user);
}