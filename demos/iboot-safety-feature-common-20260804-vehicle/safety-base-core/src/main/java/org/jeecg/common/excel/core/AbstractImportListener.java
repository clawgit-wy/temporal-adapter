package org.jeecg.common.excel.core;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Getter
public abstract class AbstractImportListener<T extends BaseImportData> extends AnalysisEventListener<T> {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 成功数据列表
     */
    private final List<T> successList = new ArrayList<>();

    /**
     * 失败数据列表
     */
    private final List<T> failDataList = new ArrayList<>();

    @Override
    public void invoke(T data, AnalysisContext context) {
        Integer rowIndex = context.readRowHolder().getRowIndex() + 1;
        data.setRowIndex(rowIndex);

        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(data);

        if (!violations.isEmpty()) {
            List<String> errors = violations.stream()
                    .map(ConstraintViolation::getMessage)
//                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.toList());

            errors.forEach(data::addError);
            log.warn("第{}行数据校验失败：{}", rowIndex, String.join("; ", errors));
            failDataList.add(data);
            return;
        }

        try {
            doBusinessValidate(data, context);
            if (data.getErrorList() != null && !data.getErrorList().isEmpty()) {
                failDataList.add(data);
            } else {
                successList.add(data);
            }
        } catch (Exception e) {
            log.error("第{}行业务校验异常：{}", rowIndex, e.getMessage(), e);
            data.setErrorMsg("业务校验失败：" + e.getMessage());
            failDataList.add(data);
        }
    }

    /**
     * 业务校验方法（子类实现）
     *
     * @param data 待校验数据
     * @param context 上下文
     */
    protected abstract void doBusinessValidate(T data, AnalysisContext context);

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        String errMsg;
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException e = (ExcelDataConvertException) exception;
            errMsg = String.format("第%d行第%d列数据格式转换异常",
                    e.getRowIndex() + 1, e.getColumnIndex() + 1);
        } else {
            errMsg = "解析异常：" + exception.getMessage();
        }
        log.warn(errMsg);
        throw new RuntimeException(errMsg, exception);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        int total = successList.size() + failDataList.size();
        log.info("所有数据解析完成，共{}行，成功{}条，失败{}条",
                total, successList.size(), failDataList.size());
    }
}
