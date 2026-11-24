package com.yutong.safety.core.handler;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.LogMessageListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.SensitiveUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyLogMessageDO;
import com.yutong.safety.core.service.SafetyLogMessageService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *  create gwcuiyx 2023.07.13
 *  日志管理--handler
 */
@Component
public class SafetyLogMessageHandler {

    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    /**
     * 数据查询列表
     * @param param
     * @return
     */
    public Object pageList(LogMessageListParam param) {
        List<SafetyLogMessageDO> list = safetyLogMessageService.pageList(param);
        long total = safetyLogMessageService.pageListCount(param);
        return new PageVO<SafetyLogMessageDO>(total, list);
    }

    /**
     * 数据导出
     * @param param
     * @return
     */
    public HSSFWorkbook export(LogMessageListParam param, String tocken) {
        List<SafetyLogMessageDO> list = safetyLogMessageService.export(param, tocken);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"操作页面", "功能", "信息", "操作人","操作时间"};
        String[][] values = new String[list.size()][];
        SafetyLogMessageDO safetyLogMessageDO;
        for(int i=0; i<list.size(); i++) {
            safetyLogMessageDO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = safetyLogMessageDO.getOperatePage();
            values[i][1] = safetyLogMessageDO.getAbility();
            if ("访客发卡".equals(safetyLogMessageDO.getOperatePage())) {
                values[i][2] = SensitiveUtils.maskLogData(safetyLogMessageDO.getMessage());
            } else {
                values[i][2] = safetyLogMessageDO.getMessage();
            }
            values[i][3] = safetyLogMessageDO.getOperator();
            values[i][4] = DateUtils.format(safetyLogMessageDO.getCreateTime(), 2);
        }
        String sheetName = "日志管理信息列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }
}
