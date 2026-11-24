package com.yutong.modules.risk.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import com.yutong.modules.risk.entity.YtsafetyHomeworkStageProcess;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.risk.vo.HomeworkStageParamVO;

/**
 * @Description: 作业环节流程信息表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
public interface YtsafetyHomeworkStageProcessMapper extends BaseMapper<YtsafetyHomeworkStageProcess> {

    /**
     * 查询所有厂区列表
     * @return
     */
    List<HomeworkStageParamVO> getFactoryInfoList();

    /**
     * 查询所有区域列表
     * @return
     */
    List<HomeworkStageParamVO> getAreaInfoList();

    /**
     * 查询所有组织列表
     * @return
     */
    List<HomeworkStageParamVO> getOrgInfoByList();

    /**
     * 根据名称、厂区、区域、组织查询位置ID列表
     * @param locationNames 位置名称列表
     * @param factoryId 厂区ID
     * @param areaId 区域ID
     * @param orgId 组织ID
     * @return 位置ID列表
     */
    List<Long> selectIdsByNameAndIds(@Param("locationNames") List<String> locationNames,
                                     @Param("factoryId") Long factoryId,
                                     @Param("areaId") Long areaId,
                                     @Param("orgId") Long orgId);


}
