package com.yutong.safety.core.db.mapper;

import com.yutong.safety.core.db.entity.SafetyFieldManageDO;
import com.yutong.safety.core.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SafetyOutsiderPromiseChangeMapper {

    int insert(SafetyOutsiderPromiseChangeDTO dto);

    void insertDetail(@Param("detailDTOS") List<SafetyOutsiderPromiseChangeDetailDTO> detailDTOS, @Param("flowId") String flowId);

    void insertOldDetail(@Param("oldDetailDTOS") List<SafetyOutsiderPromiseChangeDetailDTO> oldDetailDTOS, @Param("flowId") String flowId);

    SafetyOutsiderPromiseChangeDTO findByFlowId(String flowId);

    List<SafetyOutsiderPromiseChangeDetailDTO> findDetailsByFlowId(String flowId);

    List<SafetyOutsiderPromiseChangeDetailDTO> findOldDetailsByFlowId(String flowId);

    List<SafetyOutsiderPromiseDTO> findPageList(@Param("dto") SafetyOutsiderPromiseDTO dto, @Param("start") int start);

    int findCount(@Param("dto") SafetyOutsiderPromiseDTO dto);

    List<SafetyOutsiderPromiseDTO> findList(@Param("dto") SafetyOutsiderPromiseDTO dto);

    void updateFlowStatus(String processId);

    List<SafetyOutsiderPromiseDTO> findFlowData(String processId);

    void saveOrUpdate(List<SafetyOutsiderPromiseDTO> list);

    Map<String, String> getEmpDept(@Param("empNos") String empNos);

    List<SafetyOutsiderPromiseDTO> findOldPromise(@Param("empNos") List<String> empNos, @Param("creatorNo") String creatorNo);

    String findFactoryIds(@Param("factory") List<String> factory);

    List<Map<String, String>> findEmpDatas(@Param("empNos") List<String> empNos);

    List<SafetyOutsiderPromiseDTO> findOldPromiseForView(@Param("details") List<SafetyOutsiderPromiseChangeDetailDTO> details);

    String findFactoryCodes(@Param("factory") List<String> factory);

    void updateDetails(@Param("details")List<SafetyOutsiderPromiseChangeDetailDTO> details);

    String findFaceDevices(String factoryId);
    List<String> findDevices(Map<String,Object> paramMap);

    String findCardDevices(String factoryId);

    List<SafetyOutsiderPromiseDTO> getForCardDistribute();

    List<SafetyOutsiderPromiseDTO> findPromiseByName(@Param("empStr")String empStr, @Param("creatorNo") String creatorNo);

    List<Map<String,String>> getEmpListByDept(String empNos);

    void updatePromiseCardStatus(String empNo);

    int cancelPower(@Param("empNos") String empNos,@Param("record") SafetyOutsiderPromiseDTO record);

    String getEndDateByEmpNo(String empNo);
    List<SafetyOutsiderPromiseDTO> getEndDateByEmpNoList(@Param("list")List<String> list);

    List<String> findFactoryByEmpNo(String workNo);
    List<SafetyOutsiderPromiseDTO> findFactoryByEmpNoList(@Param("list")List<String> list);

    //查询几个小时之前的数据集
    List<String> getLastTimeData(@Param("beginTime") String beginTime,@Param("endTime") String endTime);

    //查询上次下发失败的数据（最多50条，超过的就人工）
    List<String> getIssueFailData();

    //流程下发历史记录
    void saveIssueDate(@Param("flowIdList")List<String> flowIdList);
    void deleteIssueDate(@Param("flowIdList")List<String> flowIdList);

    List<SafetyOutsiderPromiseDTO> findDetailsByFlowIdList(@Param("flowIdList") List<String> flowIdList);
    List<SafetyOutsiderPromiseDTO> findFlowDataList(@Param("processIdList") List<String>  processId);

    List<SafetyOutsiderPromiseProcessDTO> findPageProcessList(SafetyOutsiderPromiseProcessDTO dto);
    Integer getProcessCount(SafetyOutsiderPromiseProcessDTO dto);
    void updateCreator(SafetyOutsiderPromiseChangeDTO dto);
    Integer validProcess(@Param("curEmpNo") String curEmpNo, @Param("chooseEmpNo") String chooseEmpNo);

    void updatePromiseByFlowId(@Param("flowId") String flowId);

    List<SafetyFieldManageDO> processFactoryByEmpNo(@Param("empNo")String empNo);

    List<SafetyOutsiderPromiseDTO> getChangeInfoByProcessId(@Param("flowId") String flowId);
    List<SafetyOutsiderPromiseDTO> getEnById(@Param("id") String id);

    /**
     * 获取外协需要下发权限的数据，包含下发和注销
     * @return 数据集（包含下发和注销的数据）
     */
    List<ParamDTO> selectListByWxInfo(@Param("empNos")List<String> empNos);

    /**
     * 批量修改下发状态
     * @param list 员工号
     */
    void updateStatus(@Param("empNoList") List<String> list);

    List<ParamDTO> selectListByEmpNoList(@Param("empNoList") List<String> list);

    /**
     * 批量更新厂区编码
     * @param details
     */
    void updateOutsiderInfo(@Param("details")List<SafetyOutsiderPromiseDTO> details);
    void saveOrUpdateTemp(List<SafetyOutsiderPromiseDTO> list);
    int batchUpdate(@Param("list")List<ParamDTO> list);

    int batchUpdateForPhoto(@Param("list")List<ParamDTO> list);
}
