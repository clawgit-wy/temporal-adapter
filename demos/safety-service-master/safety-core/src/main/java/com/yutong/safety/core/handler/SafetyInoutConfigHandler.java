package com.yutong.safety.core.handler;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.*;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyInoutConfigDO;
import com.yutong.safety.core.service.SafetyFieldManageService;
import com.yutong.safety.core.service.SafetyInoutConfigService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author gwcuiyx
 * @date 2024-4-8 16:16
 */
@Component
public class SafetyInoutConfigHandler {
    @Autowired
    private SafetyInoutConfigService safetyInoutConfigService;
    @Autowired
    private SafetyFieldManageService safetyFieldManageService;

    /**
     * 分页查询接口
     * @param param
     * @return
     */
    public Object pageList(InoutConfigListParam param) {
        Map<Integer, String> factoryNames = safetyFieldManageService.getFactoryNameList();
        List<SafetyInoutConfigDO> list = safetyInoutConfigService.pageList(param, factoryNames);
        long total = safetyInoutConfigService.pageListCount(param);
        return new PageVO<SafetyInoutConfigDO>(total, list);
    }

    /**
     * 出入信息查询添加接口
     * @param param
     * @return
     */
    public Object add(InoutConfigAddParam param, String token) {
        safetyInoutConfigService.add(param, token);
        return null;
    }

    /**
     * 数据更新接口
     * @param param
     * @param token
     * @return
     */
    public Object update(InoutConfigUpdateParam param, String token) {
        safetyInoutConfigService.update(param, token);
        return null;
    }

    /**
     * 删除接口
     * @param param
     * @param token
     * @return
     */
    public Object delete(InoutConfigDelParam param, String token) {
        safetyInoutConfigService.delete(param.getId(), token);
        return null;
    }

    /**
     * 导出功能
     * @param param
     * @param token
     * @return
     */
    public HSSFWorkbook export(InoutConfigListParam param, String token) {
        Map<Integer, String> factoryNames = safetyFieldManageService.getFactoryNameList();
        List<SafetyInoutConfigDO> list = safetyInoutConfigService.export(param, token);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"员工号", "姓名", "厂区", "权限类型", "查询时间"};
        String[][] values = new String[list.size()][];
        SafetyInoutConfigDO inoutConfigDO;
        for(int i=0; i<list.size(); i++) {
            inoutConfigDO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = inoutConfigDO.getEmpCode();//员工号
            values[i][1] = inoutConfigDO.getEmpName();//姓名
            values[i][2] = factoryNames.containsKey(inoutConfigDO.getFactoryId()) ? factoryNames.get(inoutConfigDO.getFactoryId()) : inoutConfigDO.getFactoryName();//厂区
            values[i][3] = inoutConfigDO.getPermissionType();//权限类型
            values[i][4] = Objects.isNull(inoutConfigDO.getSearchDay()) ? "" : String.valueOf(inoutConfigDO.getSearchDay());//查询时间
        }
        String sheetName = "出入信息查询权限";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 根据员工号获取数据
     * @param empCode
     * @return
     */
    public Object getPermission(String empCode) {
        return safetyInoutConfigService.getObjectPermission(empCode);
    }

}
