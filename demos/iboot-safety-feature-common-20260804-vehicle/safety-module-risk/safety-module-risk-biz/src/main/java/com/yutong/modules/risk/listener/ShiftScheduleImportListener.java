package com.yutong.modules.risk.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.yutong.modules.risk.vo.ShiftScheduleImportVO;
import org.jeecg.common.excel.core.AbstractImportListener;

/**
 * @Description: 排班表导入监听器
 * @Author: jeecg-boot
 * @Date: 2026-08-04
 * @Version: V1.0
 */
public class ShiftScheduleImportListener extends AbstractImportListener<ShiftScheduleImportVO> {

    @Override
    protected void doBusinessValidate(ShiftScheduleImportVO data, AnalysisContext context) {
        // 无需额外业务校验
    }
}
