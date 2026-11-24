package com.yutong.modules.vehicle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.vehicle.dto.YtsafetyReleaseInfoDTO;
import com.yutong.modules.vehicle.entity.YtsafetyReleaseInfo;
import com.yutong.modules.vehicle.vo.YtsafetyReleaseInfoExporVO;
import com.yutong.modules.vehicle.vo.YtsafetyReleaseInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface YtsafetyReleaseInfoMapper extends BaseMapper<YtsafetyReleaseInfo> {

    Map vehicleInformation(@Param("plateNumber") String plateNumber);

    IPage<YtsafetyReleaseInfoVO> queryPageList(Page<YtsafetyReleaseInfoVO> page, @Param("dto") YtsafetyReleaseInfoDTO dto);

    List<YtsafetyReleaseInfoExporVO> exportList(@Param("dto") YtsafetyReleaseInfoDTO dto);

    long countExportList(@Param("dto") YtsafetyReleaseInfoDTO dto);

    List<YtsafetyReleaseInfoExporVO> exportListBatch(@Param("dto") YtsafetyReleaseInfoDTO dto,
                                                     @Param("offset") int offset,
                                                     @Param("limit") int limit);

}
