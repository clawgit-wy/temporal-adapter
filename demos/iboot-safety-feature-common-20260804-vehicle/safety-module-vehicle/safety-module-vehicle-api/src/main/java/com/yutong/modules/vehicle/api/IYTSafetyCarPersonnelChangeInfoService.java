package com.yutong.modules.vehicle.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.vehicle.dto.YTSafetyCarPersonnelChangeInfoDTO;
import com.yutong.modules.vehicle.entity.YTSafetyCarPersonnelChangeInfo;
import com.yutong.modules.vehicle.vo.YTSafetyCarPersonnelChangeInfoVO;

import java.util.List;

/**
 * @Description: 车辆人员变更信息表
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
public interface IYTSafetyCarPersonnelChangeInfoService extends IService<YTSafetyCarPersonnelChangeInfo> {

    void add(YTSafetyCarPersonnelChangeInfo personnelChangeInfo);

    List<YTSafetyCarPersonnelChangeInfoVO> queryByProcessId(Long id);

    void updateList(List<YTSafetyCarPersonnelChangeInfoDTO> carPersonnelChangeInfoList);
}
