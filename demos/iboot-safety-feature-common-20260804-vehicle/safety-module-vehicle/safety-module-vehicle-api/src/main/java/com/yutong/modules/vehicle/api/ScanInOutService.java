package com.yutong.modules.vehicle.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.vehicle.dto.ManualReleaseDTO;
import com.yutong.modules.vehicle.dto.ScanRegisterDTO;
import com.yutong.modules.vehicle.dto.YtsafetyReleaseInfoDTO;
import com.yutong.modules.vehicle.entity.YtsafetyReleaseInfo;
import com.yutong.modules.vehicle.vo.YtsafetyReleaseInfoExporVO;
import com.yutong.modules.vehicle.vo.YtsafetyReleaseInfoVO;

import java.util.List;

public interface ScanInOutService extends IService<YtsafetyReleaseInfo> {

    void manualRelease(ManualReleaseDTO scanRegisterDTO);

    IPage<YtsafetyReleaseInfoVO> queryPageList(YtsafetyReleaseInfoDTO dto);

    List<YtsafetyReleaseInfoExporVO> exportList(YtsafetyReleaseInfoDTO dto);

}
