package com.yutong.safety.core.handler;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.constants.VisitorHairpinEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CardRefundAddParam;
import com.yutong.safety.common.param.web.CardRefundListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.SensitiveUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyCardRefundDO;
import com.yutong.safety.core.service.SafetyCardRefundService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2024-1-18 10:11
 */
@Component
public class SafetyCardRefundHandler {
    @Autowired
    private SafetyCardRefundService safetyCardRefundService;

    /**
     * 分页查询
     * @param param
     * @param token
     * @return
     */
    public Object pageList(CardRefundListParam param, String token) {
        List<SafetyCardRefundDO> list = safetyCardRefundService.pageList(param);
        long total = safetyCardRefundService.pageListCount(param);
        return new PageVO<SafetyCardRefundDO>(total, list);
    }

    /**
     * 数据添加接口
     * @param param
     * @return
     */
    public Object add(CardRefundAddParam param) {
        safetyCardRefundService.add(param.getVisitorId());
        return null;
    }

    /**
     * 数据导出
     * @param param
     * @param token
     * @return
     */
    public HSSFWorkbook export(CardRefundListParam param, String token) {
        List<SafetyCardRefundDO> list = safetyCardRefundService.export(param);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"导出数据不能超过5000条！");
        }
        String[] title = new String[]{"发卡时间", "发卡厂区", "发卡人", "卡遗失天数","卡ID","访客单位","访客姓名","访客手机号",
                "访客身份证号","访问厂区","访问区域","绝密区","被访人部门","被访人姓名","被访人手机号","访问事由","入厂开始时间","入厂结束时间","照片","访问状态"};
        String[][] values = new String[list.size()][];
        SafetyCardRefundDO safetyCardRefundDO;
        for(int i=0; i<list.size(); i++) {
            safetyCardRefundDO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = Objects.nonNull(safetyCardRefundDO.getHairpinTime()) ? DateUtils.format(safetyCardRefundDO.getHairpinTime(),2) : "";
            values[i][1] = safetyCardRefundDO.getHairpinFactory();
            values[i][2] = safetyCardRefundDO.getUserName();
            values[i][3] = String.valueOf(safetyCardRefundDO.getLoseDay());
            values[i][4] = safetyCardRefundDO.getHairpinNo();
            values[i][5] = SensitiveUtils.maskName(safetyCardRefundDO.getVendorUnit());
            values[i][6] = SensitiveUtils.maskName(safetyCardRefundDO.getVisitorName());
            values[i][7] = SensitiveUtils.maskPhoneNumber(safetyCardRefundDO.getVisitorPhone());
            values[i][8] = SensitiveUtils.maskCardId(safetyCardRefundDO.getVisitorIdCard());
            values[i][9] = safetyCardRefundDO.getVendorFactory();
            values[i][10] = safetyCardRefundDO.getVendorRegion();
            values[i][11] = safetyCardRefundDO.getSecretRegion();
            values[i][12] = safetyCardRefundDO.getVisitedDept();
            values[i][13] = safetyCardRefundDO.getVisitedName();
            values[i][14] = safetyCardRefundDO.getVisitedPhone();
            values[i][15] = safetyCardRefundDO.getVendorMatter();
            values[i][16] = DateUtils.format(safetyCardRefundDO.getStartTime(),3);
            values[i][17] = DateUtils.format(safetyCardRefundDO.getEndTime(),3);
            values[i][18] = safetyCardRefundDO.getPhoto();
            values[i][19] = VisitorHairpinEnum.values()[safetyCardRefundDO.getVendorState()].getName();
        }
        String sheetName = "访客未退卡管理查询列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 定时任务更新卡遗失天数
     */
    public void taskCardRefundDays() {
        safetyCardRefundService.taskCardRefundDays();
    }
}
