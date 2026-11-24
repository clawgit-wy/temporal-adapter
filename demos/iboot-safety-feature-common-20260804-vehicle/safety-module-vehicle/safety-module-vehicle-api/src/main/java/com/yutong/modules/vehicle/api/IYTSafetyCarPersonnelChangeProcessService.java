package com.yutong.modules.vehicle.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.vehicle.dto.YTSafetyCarPersonnelChangeProcessDTO;
import com.yutong.modules.vehicle.entity.YTSafetyCarPersonnelChangeProcess;
import com.yutong.modules.vehicle.vo.YTSafetyCarPersonnelChangeProcessVO;

import javax.validation.Valid;

/**
 * @Description: 车辆人员变更流程表
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
public interface IYTSafetyCarPersonnelChangeProcessService extends IService<YTSafetyCarPersonnelChangeProcess> {


    YTSafetyCarPersonnelChangeProcessVO queryById(Long id);

    BpmReponseParamsVO executeProcess(BpmProcessParamDTO processParamDTO);

    void turnover(YTSafetyCarPersonnelChangeProcessDTO changeProcessDTO);
}
