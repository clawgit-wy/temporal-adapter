package com.yutong.modules.risk.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.yutong.modules.risk.vo.YTSafetyTaskInfoImportVO;
import org.jeecg.common.excel.core.AbstractImportListener;

public class SafetyTaskImportListener extends AbstractImportListener<YTSafetyTaskInfoImportVO> {

    @Override
    protected void doBusinessValidate(YTSafetyTaskInfoImportVO data, AnalysisContext context) {
        // 在这里添加业务校验逻辑
        // 例如：检查工号是否已存在、部门是否有效等
        // if (data.getEmpNo()) {
//            data.addError("员工工号[" + data.getEmpNo() + "]已存在");
        // }
    }
}
