package com.yutong.safety.core.handler;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.DataSendLogListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.SensitiveUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyDataSendLogDO;
import com.yutong.safety.core.service.SafetyDataSendLogService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 *  create gwcuiyx 2023.08.08
 *  数据下发日志管理--handler
 */
@Component
public class SafetyDataSendLogHandler {
    @Autowired
    private SafetyDataSendLogService safetyDataSendLogService;

    /**
     * 数据列表
     * @param param
     * @return
     */
    public Object pageList(DataSendLogListParam param) {
        List<SafetyDataSendLogDO> list = safetyDataSendLogService.pageList(param);
        long total = safetyDataSendLogService.pageListCount(param);
        return new PageVO<SafetyDataSendLogDO>(total, list);
    }

    /**
     * 数据导出操作
     * @param param
     * @return
     */
    public HSSFWorkbook export(DataSendLogListParam param, String token) {
        List<SafetyDataSendLogDO> list = safetyDataSendLogService.export(param, token);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"起草时间", "访客姓名", "访客身份证号", "手机号" , "入厂开始时间", "入厂结束时间","照片","访问厂区","访问区域","下发时间","下发结果"};
        String[][] values = new String[list.size()][];
        SafetyDataSendLogDO dataSendLogDO;
        for(int i=0; i<list.size(); i++) {
            values[i] = new String[title.length];
            dataSendLogDO = list.get(i);
            values[i][0] = (Objects.nonNull(dataSendLogDO.getEipCreateTime()) ? DateUtils.format(dataSendLogDO.getEipCreateTime(), 2) : "");//eip起草时间
            values[i][1] = SensitiveUtils.maskName(dataSendLogDO.getVisitorName());//访客姓名
            values[i][2] = SensitiveUtils.maskCardId(dataSendLogDO.getVisitorIdCard());//访客身份证号
            values[i][3] = SensitiveUtils.maskPhoneNumber(dataSendLogDO.getVisitorPhone());//手机号
            values[i][4] = DateUtils.format(dataSendLogDO.getStartTime(), 2);//入厂开始时间
            values[i][5] = DateUtils.format(dataSendLogDO.getEndTime(), 2);//入厂结束时间
            values[i][6] = dataSendLogDO.getPhoto();//照片
            values[i][7] = dataSendLogDO.getVendorFactory();//访问厂区
            values[i][8] = dataSendLogDO.getVendorRegion();//访问区域
            values[i][9] = DateUtils.format(dataSendLogDO.getSendTime(), 2);//下发时间
            values[i][10] = dataSendLogDO.getSendState()+"";//下发结果
        }
        String sheetName = "数据下发日志列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    public Object sendAgain(Integer id, String token) {
        safetyDataSendLogService.sendAgain(id, token);
        return null;
    }

    public Object test() {
        safetyDataSendLogService.test();
        return null;
    }

}
