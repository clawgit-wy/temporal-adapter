package com.yutong.safety.core.handler.spot;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.StopEmpowerListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.SensitiveUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyStopEmpowerDO;
import com.yutong.safety.core.service.spot.SafetyStopEmpowerService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *  create gwcuiyx 2023.07.18
 *  停车场授权记录--handler
 */
@Component
public class SafetyStopEmpowerHandler {
    @Autowired
    private SafetyStopEmpowerService safetyStopEmpowerService;

    /**
     * 数据列表
     * @param param
     * @return
     */
    public Object pageList(StopEmpowerListParam param) {
        List<SafetyStopEmpowerDO> list = safetyStopEmpowerService.pageList(param);
        long total = safetyStopEmpowerService.pageListCount(param);
        return new PageVO<SafetyStopEmpowerDO>(total, list);
    }

    /**
     * 数据导出操作
     * @param param
     * @return
     */
    public HSSFWorkbook export(StopEmpowerListParam param, String token) {
        List<SafetyStopEmpowerDO> list = safetyStopEmpowerService.export(param, token);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"车牌号", "登记时间", "访客姓名", "访客身份证号", "访客手机号", "授权厂区", "授权通道号", "开始时间", "结束时间", "备注"};
        String[][] values = new String[list.size()][];
        SafetyStopEmpowerDO stopEmpowerDO;
        for(int i=0; i<list.size(); i++) {
            stopEmpowerDO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = stopEmpowerDO.getCardNo();
            values[i][1] = DateUtils.format(stopEmpowerDO.getRegisterTime(), 2);
            values[i][2] = SensitiveUtils.maskName(stopEmpowerDO.getVisitorName());
            values[i][3] = SensitiveUtils.maskCardId(stopEmpowerDO.getVisitorIdCard());
            values[i][4] = SensitiveUtils.maskPhoneNumber(stopEmpowerDO.getVisitorPhone());
            values[i][5] = stopEmpowerDO.getEmpowerFactory();
            values[i][6] = stopEmpowerDO.getEmpowerNo();
            values[i][7] = DateUtils.format(stopEmpowerDO.getStartTime(), 2);
            values[i][8] = DateUtils.format(stopEmpowerDO.getEndTime(), 2);
            values[i][9] = stopEmpowerDO.getRemark();
        }
        String sheetName = "停车场授权记录信息列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }
}
