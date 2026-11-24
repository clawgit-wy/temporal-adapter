package com.yutong.modules.vehicle.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.vehicle.api.ScanInOutService;
import com.yutong.modules.vehicle.dto.ManualReleaseDTO;
import com.yutong.modules.vehicle.dto.ScanRegisterDTO;
import com.yutong.modules.vehicle.dto.YtsafetyReleaseInfoDTO;
import com.yutong.modules.vehicle.entity.YtsafetyReleaseInfo;
import com.yutong.modules.vehicle.mapper.YtsafetyReleaseInfoMapper;
import com.yutong.modules.vehicle.vo.YtsafetyReleaseInfoExporVO;
import com.yutong.modules.vehicle.vo.YtsafetyReleaseInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ScanInOutServiceImpl extends ServiceImpl<YtsafetyReleaseInfoMapper, YtsafetyReleaseInfo> implements ScanInOutService {

    /** 每批查询条数 */
    private static final int EXPORT_BATCH_SIZE = 2000;

    @Resource
    YtsafetyReleaseInfoMapper ytsafetyReleaseInfoMapper;

    @Override
    public void manualRelease(ManualReleaseDTO manualReleaseDTO) {
        Map map = ytsafetyReleaseInfoMapper.vehicleInformation(manualReleaseDTO.getPlateNumber());
        YtsafetyReleaseInfo releaseInfo = new YtsafetyReleaseInfo();
        if (map != null && !map.isEmpty()) {
            // 车牌号
            releaseInfo.setPlateNumber(getMapValue(map, "fd_car_no"));
            // 供应商名称
            releaseInfo.setSupplierName(getMapValue(map, "fd_supplier_name"));
            // 供应商代码
            releaseInfo.setSupplierCode(getMapValue(map, "fd_supplier_code"));
            // 运输货物名称
            releaseInfo.setGoodsName(getMapValue(map, "fd_goods_name"));
            // 货物装载量（吨）
            releaseInfo.setGoodsWeight(getMapValue(map, "fd_goods_weight"));
            // 环保等级
            releaseInfo.setProtectGrade(getMapValue(map, "fd_protect_grade"));
            // 随车清单
            releaseInfo.setDetailedImg(getMapValue(map, "fd_detailed_img"));
            // VIN号
            releaseInfo.setVin(getMapValue(map, "fd_vin"));
            // 发动机号码
            releaseInfo.setEngineNum(getMapValue(map, "fd_engine"));
            // 行驶证正面照片
            releaseInfo.setCarImg(getMapValue(map, "fd_car_img"));
            // 行驶证注册日期
            releaseInfo.setCarDate(getMapValue(map, "fd_car_date"));
            // 车辆归属
            releaseInfo.setCarBelong(getMapValue(map, "fd_car_belong"));
            // 车主姓名/运输公司名称
            releaseInfo.setDriverName(getMapValue(map, "fd_driver_name"));
            // 车辆类型
            releaseInfo.setVehicleType(getMapValue(map, "fd_filing_car_type"));
        }

        // 所属厂区名称
        releaseInfo.setFactoryName(manualReleaseDTO.getFactoryName());
        // 所属厂区ID
        releaseInfo.setFactoryId(manualReleaseDTO.getFactoryId());
        // 位置名称
        releaseInfo.setLocationName(manualReleaseDTO.getLocationName());
        // 位置ID
        releaseInfo.setLocationId(manualReleaseDTO.getLocationId());
        // 车牌号
        releaseInfo.setPlateNumber(manualReleaseDTO.getPlateNumber());
        // 车辆类型
        releaseInfo.setVehicleType("物流车辆");
        // 进出类型（1-进场 2-出场）
        releaseInfo.setInOutType(manualReleaseDTO.getInOutType());
        // 放行人员工号
        releaseInfo.setEmpNo(manualReleaseDTO.getEmpNo());
        // 放行人姓名
        releaseInfo.setEmpName(manualReleaseDTO.getEmpName());
        Date releaseDateTime = manualReleaseDTO.getReleaseDate();
        //放行日期
        if (releaseDateTime != null) {
            // 转换为 LocalDateTime
            LocalDateTime dateTime = releaseDateTime.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            // 放行日期 - 只保留年月日
            LocalDate date = dateTime.toLocalDate();
            releaseInfo.setReleaseDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            // 放行时间 - 只保留时分秒，日期固定为 1970-01-01
            LocalTime time = dateTime.toLocalTime();
            releaseInfo.setReleaseTime(Date.from(
                    LocalDateTime.of(1970, 1, 1, time.getHour(), time.getMinute(), time.getSecond())
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
            ));
        }
        this.save(releaseInfo);
    }

    private String getMapValue(Map map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    @Override
    public IPage<YtsafetyReleaseInfoVO> queryPageList(YtsafetyReleaseInfoDTO dto) {
        Page<YtsafetyReleaseInfoVO> page = new Page<>(dto.getPageNo(), dto.getPageSize());
        return ytsafetyReleaseInfoMapper.queryPageList(page, dto);
    }

    @Override
    public List<YtsafetyReleaseInfoExporVO> exportList(YtsafetyReleaseInfoDTO dto) {
        long total = ytsafetyReleaseInfoMapper.countExportList(dto);
        log.info("车辆放行台账导出总数: {}", total);
        if (total == 0) {
            return new ArrayList<>();
        }
        // 总数在单批范围内，一次性查询
        if (total <= EXPORT_BATCH_SIZE) {
            return ytsafetyReleaseInfoMapper.exportList(dto);
        }
        // 分批查询合并结果
        List<YtsafetyReleaseInfoExporVO> resultList = new ArrayList<>((int) total);
        int offset = 0;
        while (offset < total) {
            List<YtsafetyReleaseInfoExporVO> batch = ytsafetyReleaseInfoMapper.exportListBatch(dto, offset, EXPORT_BATCH_SIZE);
            if (batch == null || batch.isEmpty()) {
                break;
            }
            resultList.addAll(batch);
            offset += EXPORT_BATCH_SIZE;
            log.info("车辆放行台账分批导出进度: {}/{}", Math.min(offset, total), total);
        }
        return resultList;
    }
}
