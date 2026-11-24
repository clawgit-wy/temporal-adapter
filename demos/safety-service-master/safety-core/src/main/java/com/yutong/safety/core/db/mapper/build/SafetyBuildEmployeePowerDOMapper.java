package com.yutong.safety.core.db.mapper.build;


import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDOExample;
import com.yutong.safety.core.db.entity.build.param.BuildEmployeePowerListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildEmpNoMaxPowerTimeVo;
import com.yutong.safety.core.db.entity.build.vo.BuildEmployeePowerVO;
import com.yutong.safety.core.dto.ParamDTO;
import com.yutong.safety.core.dto.SafetyClosePublicBuildDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyBuildEmployeePowerDOMapper {

    List<BuildEmployeePowerVO> selectList(BuildEmployeePowerListParam listParam);

    long selectCount(BuildEmployeePowerListParam listParam);

    long countByExample(SafetyBuildEmployeePowerDOExample example);

    int deleteByExample(SafetyBuildEmployeePowerDOExample example);

    int deleteByPrimaryKey(String id);

    int insert(SafetyBuildEmployeePowerDO record);

    int insertSelective(SafetyBuildEmployeePowerDO record);

    List<SafetyBuildEmployeePowerDO> selectByExample(SafetyBuildEmployeePowerDOExample example);

    SafetyBuildEmployeePowerDO selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SafetyBuildEmployeePowerDO record, @Param("example") SafetyBuildEmployeePowerDOExample example);

    int updateByExample(@Param("record") SafetyBuildEmployeePowerDO record, @Param("example") SafetyBuildEmployeePowerDOExample example);

    int updateByPrimaryKeySelective(SafetyBuildEmployeePowerDO record);

    int updateByPrimaryKey(SafetyBuildEmployeePowerDO record);

    int insertBatch(@Param(value = "list") List<SafetyBuildEmployeePowerDO> list);

    int updateBatch(@Param(value = "list") List<SafetyBuildEmployeePowerDO> list);

    int insertBatchForInit(@Param(value = "list") List<SafetyBuildEmployeePowerDO> list);

    List<SafetyEmployeeMessageDO> getEmployeeForEmpPower(BuildEmployeePowerListParam listParam);

    List<SafetyBuildEmployeePowerDO> getEmpPowerConcat(BuildEmployeePowerListParam listParam);

    List<SafetyBuildEmployeePowerDO> getEmpPowerList(BuildEmployeePowerListParam listParam);

    List<ParamDTO> getFactoryIdByBuildPower(@Param(value = "empNoList") List<String> list);

    List<BuildEmployeePowerVO> selectEmpPowerList(BuildEmployeePowerListParam listParam);

    /**
     * 获取员工拥有权限的楼栋信息
     * @param empNoList 员工号
     */
    List<SafetyClosePublicBuildDto> selectPowerByEmpNo(@Param(value = "empNoList") List<String> empNoList);

    /**
     * 批量标记删除
     * @param deleteIds 数据行ID
     */
    void batchUpdate(@Param(value = "ids") List<String> deleteIds, @Param(value = "remark") String msg);

    /**
     * 获取指定员工非公共楼宇的最大时间
     * @param empNoList 员工
     * @return 员工、楼栋编码、最大时间
     */
    List<BuildEmpNoMaxPowerTimeVo> selectMaxEndTimeByEmpNo(@Param(value = "empNoList") List<String> empNoList);

    List<BuildEmployeePowerVO> selectCommonEmpPowerList(@Param(value = "ids") List<String> ids);

    void updateDepartChangeTime(@Param(value = "empNoList") List<String> empNos);
}