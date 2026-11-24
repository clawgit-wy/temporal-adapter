package com.yutong.modules.risk.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.yutong.modules.risk.vo.YtsafetyHomeworkStageImportVO;
import org.jeecg.common.excel.core.AbstractImportListener;

/**
 * 作业环节导入监听器
 */
public class HomeworkStageImportListener extends AbstractImportListener<YtsafetyHomeworkStageImportVO> {

    @Override
    protected void doBusinessValidate(YtsafetyHomeworkStageImportVO data, AnalysisContext context) {
        // 业务校验：根据实际业务需求扩展
    }
}
