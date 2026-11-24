package com.yutong.modules.vehicle.api;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.vehicle.entity.YtsafetyVehicleViolationsReport;
import com.yutong.modules.vehicle.param.YtsafetyVehicleViolationsReportAddParam;
import com.yutong.modules.vehicle.param.YtsafetyVehicleViolationsReportEditParam;
import com.yutong.modules.vehicle.param.YtsafetyVehicleViolationsReportQueryParam;
import com.yutong.modules.vehicle.vo.YtsafetyVehicleViolationsReportVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 车辆违规上报
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Service
public interface IYtsafetyVehicleViolationsReportService extends IService<YtsafetyVehicleViolationsReport> {

    /**
     * 违规车辆冻结时长配置列表
     * @param page
     * @param queryParam
     * @return
     */
    public IPage<YtsafetyVehicleViolationsReportVo> queryList(Page<YtsafetyVehicleViolationsReport> page, YtsafetyVehicleViolationsReportQueryParam queryParam);

    /**
     * 违规车辆登记草稿箱
     * @param addParam
     * @return
     */
    YtsafetyVehicleViolationsReportVo saveVehicleViolations(YtsafetyVehicleViolationsReportAddParam addParam);

    /**
     * 违规车辆登记草稿箱更新
     * @param addParam
     * @return
     */
    YtsafetyVehicleViolationsReportVo updateVehicleViolations(YtsafetyVehicleViolationsReportAddParam addParam);


    /** 提交上报 */
    YtsafetyVehicleViolationsReportVo submitReport(YtsafetyVehicleViolationsReportAddParam ytsafetyVehicleViolationsReportAddParam);

    /** 车辆违规上报-审核*/
    void reviewReport(YtsafetyVehicleViolationsReportEditParam editParam);

    /** 获取完整详情（主表+描述+结果+延期+历史） */
    YtsafetyVehicleViolationsReportVo getReportDetail(Long id);

    /** 根据车牌号获取车辆违规记录 */
    List<YtsafetyVehicleViolationsReportVo> getVehicleViolationsByVehicleNo(String  vehicleNo);

    /**
     * 车辆违规上报批量删除
     * @param ids
     * @return
     */
    void removeVehicleViolationsByIds(List<String> ids);


    BpmReponseParamsVO executeProcess(BpmProcessParamDTO processParamDTO);


}
