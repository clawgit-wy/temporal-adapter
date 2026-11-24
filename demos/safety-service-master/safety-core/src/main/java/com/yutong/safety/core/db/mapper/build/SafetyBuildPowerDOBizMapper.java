package com.yutong.safety.core.db.mapper.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildManageProcessDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDO;
import com.yutong.safety.core.db.entity.build.vo.BuildGroupDutyEmpNoVO;
import com.yutong.safety.core.db.entity.build.vo.BuildGroupDutyVO;
import com.yutong.safety.core.dto.build.SafetyBuildPowerDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyBuildPowerDOBizMapper {
    List<SafetyBuildPowerDTO> selectPageList(SafetyBuildPowerDTO param);
    List<SafetyBuildPowerDTO> selectPageListExport(SafetyBuildPowerDO param);
    Integer selectCountPageList(SafetyBuildPowerDTO param);
    List<SafetyBuildPowerDTO> getPeopleList(SafetyBuildPowerDTO param);

    void insertSafetyBuildPower(@Param("list") List<SafetyBuildPowerDO> param);

    void updateData(SafetyBuildPowerDO en);

    List<SafetyBuildPowerDO> getListByUserCode(@Param("list") List<String> userCodeList);
    List<SafetyBuildPowerDTO> getListWithPosByUserCode(@Param("list") List<String> userCodeList);

    void batchUpdate(@Param("list")List<String> dutyDeptCode,@Param("process")SafetyBuildManageProcessDO processDTO);

    void deleteByDeptCode(@Param("deptCode")String deptCode,@Param("remark") String remark);

    //获取员工对应的责任部门
    List<BuildGroupDutyVO> getGroupDuty(@Param("list") List<String> orgCoeList);

    // 获取各责任部门的责任人
    List<SafetyBuildPowerDO> getDutyEmpByGroup();

    //获取各责任部门的责任人
    List<SafetyBuildPowerDO> getDutyInfoByDevice(@Param("list") List<String> deviceCodeList);

    // 每个责任部门只获取一条记录 
    List<SafetyBuildPowerDO> getOnyDutyDeptList(@Param("list") List<String> dutyDeptList);

    /**
     * 获取员工对应的责任部门和门禁ID
     * @param orgCode 组织编码
     */
    List<BuildGroupDutyEmpNoVO> getGroupDutyDeviceCode(String orgCode);
}