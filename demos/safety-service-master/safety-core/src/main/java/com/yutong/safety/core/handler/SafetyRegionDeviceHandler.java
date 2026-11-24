package com.yutong.safety.core.handler;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.RegionDeviceEnum;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.RegionDeviceAddParam;
import com.yutong.safety.common.param.web.RegionDeviceListParam;
import com.yutong.safety.common.param.web.RegionDeviceUpdateParam;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyRegionDeviceDO;
import com.yutong.safety.core.service.SafetyRegionDeviceService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *  create gwcuiyx 2023.07.17
 *  区域设备绑定--handler
 */
@Component
public class SafetyRegionDeviceHandler {
    @Autowired
    private SafetyRegionDeviceService safetyRegionDeviceService;

    /**
     * 数据列表
     * @param param
     * @return
     */
    public Object pageList(RegionDeviceListParam param) {
        List<SafetyRegionDeviceDO> list = safetyRegionDeviceService.pageList(param);
        long total = safetyRegionDeviceService.pageListCount(param);
        return new PageVO<SafetyRegionDeviceDO>(total, list);
    }

    /**
     * 数据详情
     * @param id
     * @return
     */
    public Object view(Integer id) {
        return safetyRegionDeviceService.view(id);
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    public Object delete(Integer id, String tocken) {
        safetyRegionDeviceService.delete(id, tocken);
        return null;
    }

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    public Object add(RegionDeviceAddParam param, String tocken) {
        safetyRegionDeviceService.add(param, tocken);
        return null;
    }

    /**
     * 数据更新操作
     * @param param
     * @return
     */
    public Object update(RegionDeviceUpdateParam param, String tocken) {
        safetyRegionDeviceService.update(param, tocken);
        return null;
    }

    /**
     * 数据导出操作
     * @param param
     * @return
     */
    public HSSFWorkbook export(RegionDeviceListParam param, String tocken) {
        List<SafetyRegionDeviceDO> list = safetyRegionDeviceService.export(param, tocken);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"厂区", "区域", "设备类型", "设备号","设备名称","备注"};
        String[][] values = new String[list.size()][];
        SafetyRegionDeviceDO regionDeviceDO;
        for(int i=0; i<list.size(); i++) {
            regionDeviceDO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = regionDeviceDO.getFactoryName();
            values[i][1] = regionDeviceDO.getRegionName();
            values[i][2] = RegionDeviceEnum.values()[regionDeviceDO.getDeviceType()].getName();
            values[i][3] = regionDeviceDO.getDeviceNo();
            values[i][4] = regionDeviceDO.getDeviceName();
            values[i][5] = regionDeviceDO.getRemark();
        }
        String sheetName = "区域设备绑定信息列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 数据导入操作
     * @param param
     * @return
     */
    public Object importRegionDevice(CommonImportParam param, String tocken) {
        return safetyRegionDeviceService.importRegionDevice(param, tocken);
    }

    /**
     * 根据厂区和区域id获取对应数据列表
     * @param factoryId
     * @param regionId
     * @return
     */
    public Object getDeviceNos(Integer factoryId, Integer regionId) {
        return safetyRegionDeviceService.getDeviceNos(factoryId, regionId);
    }

}
