package com.yutong.safety.core.db.mapper.spot;


import com.yutong.safety.common.param.web.spot.SpotCheckDeviceListParam;
import com.yutong.safety.common.vo.web.SpotCheckDeviceInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetySpotCheckDeviceInfoDOBizMapper extends SafetySpotCheckDeviceInfoDOMapper {

    /**
     * 分页数据查询
     * @param param
     * @param start
     * @param end
     * @return
     */
    List<SpotCheckDeviceInfoVO> queryPageList(@Param("param") SpotCheckDeviceListParam param, @Param("start") int start, @Param("end") int end);

    /**
     * 数据导出功能
     * @param param
     * @return
     */
    List<SpotCheckDeviceInfoVO> exportList(@Param("param") SpotCheckDeviceListParam param);

    Integer pageListCount(@Param("param") SpotCheckDeviceListParam param);

}