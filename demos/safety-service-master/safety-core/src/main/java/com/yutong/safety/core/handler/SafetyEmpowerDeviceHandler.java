package com.yutong.safety.core.handler;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.EmpowerDeviceAddParam;
import com.yutong.safety.common.param.web.EmpowerDeviceListParam;
import com.yutong.safety.common.param.web.EmpowerDeviceUpdateParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyEmpowerDeviceDO;
import com.yutong.safety.core.service.SafetyEmpowerDeviceService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *  create gwcuiyx 2023.07.17
 *  授权区域设备管理--handler
 */
@Component
public class SafetyEmpowerDeviceHandler {
    @Autowired
    private SafetyEmpowerDeviceService safetyEmpowerDeviceService;

    /**
     * 数据列表
     * @param param
     * @return
     */
    public Object pageList(EmpowerDeviceListParam param) {
        List<SafetyEmpowerDeviceDO> list = safetyEmpowerDeviceService.pageList(param);
        long total = safetyEmpowerDeviceService.pageListCount(param);
        return new PageVO<SafetyEmpowerDeviceDO>(total, list);
    }

    /**
     * 数据详情
     * @param id
     * @return
     */
    public Object view(Integer id) {
        return safetyEmpowerDeviceService.view(id);
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    public Object delete(Integer id, String tocken) {
        safetyEmpowerDeviceService.delete(id, tocken);
        return null;
    }

    /**
     * 数据更新操作
     * @param param
     * @return
     */
    public Object update(EmpowerDeviceUpdateParam param, String tocken) {
        safetyEmpowerDeviceService.update(param, tocken);
        return null;
    }

    /**
     * 数据导出操作
     * @param param
     * @return
     */
    public HSSFWorkbook export(EmpowerDeviceListParam param, String tocken) {
        List<SafetyEmpowerDeviceDO> list = safetyEmpowerDeviceService.export(param, tocken);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"厂区", "区域", "授权设备号", "更新时间", "备注"};
        String[][] values = new String[list.size()][];
        SafetyEmpowerDeviceDO empowerDeviceDO;
        for(int i=0; i<list.size(); i++) {
            empowerDeviceDO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = empowerDeviceDO.getFactoryName();
            values[i][1] = empowerDeviceDO.getRegionName();
            values[i][2] = empowerDeviceDO.getEmpowerNo();
            values[i][3] = DateUtils.format(empowerDeviceDO.getUpdateTime(), 2);
            values[i][4] = empowerDeviceDO.getRemark();
        }
        String sheetName = "区域设备绑定信息列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 数据导入操作
     * @param param
     * @return
     */
    public Object importEmpowerDevice(CommonImportParam param, String tocken) {
        return safetyEmpowerDeviceService.importEmpowerDevice(param, tocken);
    }

    /**
     * 数据添加功能
     * @param param
     */
    public Object add(EmpowerDeviceAddParam param, String tocken) {
        safetyEmpowerDeviceService.add(param, tocken);
        return null;
    }
}
