package com.yutong.modules.risk.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.yutong.modules.risk.vo.RiskDataImportVO;
import com.yutong.modules.risk.vo.YTSafetyTaskInfoImportVO;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoImportVO;
import com.yutong.modules.risk.vo.YtsafetyRiskDataDetailsImportVO;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.excel.core.AbstractImportListener;

import java.util.ArrayList;
import java.util.List;

public class RiskDataImportListener extends AbstractImportListener<YtsafetyRiskDataDetailsImportVO> {

    @Override
    protected void doBusinessValidate(YtsafetyRiskDataDetailsImportVO data, AnalysisContext context) {

    }
}
