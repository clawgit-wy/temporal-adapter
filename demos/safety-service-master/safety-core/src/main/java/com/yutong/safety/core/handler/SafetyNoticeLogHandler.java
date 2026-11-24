package com.yutong.safety.core.handler;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.constants.VisitorHairpinEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.NoticeLogParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.vo.NoticeLogVO;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyCardRefundDO;
import com.yutong.safety.core.db.entity.SafetyStaffEvectionDO;
import com.yutong.safety.core.service.SafetyNoticeLogService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author lukunpeng
 * @time 2024-4-15
 */
@Component
public class SafetyNoticeLogHandler {

    @Autowired
    private SafetyNoticeLogService safetyNoticeLogService;

    public Object getNotice(NoticeLogParam param) {
        List<NoticeLogVO> list =  safetyNoticeLogService.queryList(param);
        long total = safetyNoticeLogService.pageListCount(param);
        return new PageVO<NoticeLogVO>(total, list);
    }

    public HSSFWorkbook export(NoticeLogParam param) {
        param.setPageNum(1);
        param.setPageSize(5100);
        List<NoticeLogVO> list =  safetyNoticeLogService.queryList(param);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"导出数据不能超过5000条！");
        }
        String[] title = new String[]{"车牌号","入厂时间","车辆类别","车辆类型","姓名","员工号","手机号","厂区","停车时长","通知次数","滞留次数","应有通知方式","当前通知方式","通知时间"};
        String[][] values = new String[list.size()][];
        NoticeLogVO noticeLogVO;
        for(int i=0; i<list.size(); i++) {
            noticeLogVO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = noticeLogVO.getCarNo();
            values[i][1] = noticeLogVO.getInTime();
            values[i][2] = noticeLogVO.getCarType();
            values[i][3] = noticeLogVO.getCarCate();
            values[i][4] = noticeLogVO.getStaffName();
            values[i][5] = noticeLogVO.getStaffNo();
            values[i][6] = noticeLogVO.getPhone();
            values[i][7] = noticeLogVO.getFactory();
            values[i][8] = noticeLogVO.getStopHours();
            values[i][9] = noticeLogVO.getNoticeNums()+"";
            values[i][10] = noticeLogVO.getRetentionNums()+"";
            values[i][11] = noticeLogVO.getNoticeType()==null?"":noticeLogVO.getNoticeType().replace("1,2","短信+掌上宇通").
                    replace("1","短信").replace("2","掌上宇通");
            values[i][12] = noticeLogVO.getRemindType()==null?"":noticeLogVO.getRemindType().replace("1","短信").replace("2","掌上宇通");
            values[i][13] = noticeLogVO.getNoticeTime();
        }
        String sheetName = "滞留车辆通知发送记录";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }
}
