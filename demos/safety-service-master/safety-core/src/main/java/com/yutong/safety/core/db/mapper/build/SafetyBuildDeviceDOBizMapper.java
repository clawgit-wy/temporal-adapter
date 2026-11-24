package com.yutong.safety.core.db.mapper.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceDOExample;
import com.yutong.safety.core.db.entity.build.param.BuildDeviceListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildDeviceVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyBuildDeviceDOBizMapper extends SafetyBuildDeviceDOMapper{

    int batchUpdate(@Param("list") List<SafetyBuildDeviceDO> list);

    List<BuildDeviceVO> selectByExampleBiz(SafetyBuildDeviceDOExample example);

    List<SafetyBuildDeviceDO> getListByCondition(SafetyBuildDeviceDO param);

    List<SafetyBuildDeviceDO> selectBuildCollectDetail(@Param("id") String id);

    List<BuildDeviceVO> selectByExampleBizTwo(BuildDeviceListParam param);

    Long  countByExampleBizTwo(BuildDeviceListParam param);

    List<SafetyBuildDeviceDO> selectByRemoveAndDisableList();

    /**
     * 根据厂区名称获取对应的设备编码
     * @param factoryNameList 厂区集合
     * @return 设备编码集合
     */
    List<String> getListByFactoryName(@Param("list") List<String> factoryNameList);
}
