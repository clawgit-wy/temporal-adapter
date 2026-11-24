package org.jeecg.common.excel.core;

import com.alibaba.excel.annotation.ExcelIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.ArrayList;
import java.util.List;


@Data
public abstract class BaseImportData {

    /**
     * 行号（Excel解析时自动记录，用于错误定位）
     */
    @ExcelIgnore
    @ApiModelProperty(value = "行号")
    private Integer rowIndex;

    @ExcelIgnore
    @ApiModelProperty(value = "错误信息列表")
    private List<String> errorList = new ArrayList<>();

    @Excel(name = "错误信息", width = 50, orderNum = "99")
    @ApiModelProperty(value = "错误信息（多个错误用分号分隔）")
    private String errorMsg;

    /**
     * 添加错误信息
     */
    public void addError(String error) {
        if (this.errorList == null) {
            this.errorList = new ArrayList<>();
        }
        this.errorList.add(error);
    }

    /**
     * 获取合并的错误信息（用于导出显示）
     */
    public String getErrorMsg() {
        if (errorList == null || errorList.isEmpty()) {
            return null;
        }
        return String.join("; ", errorList);
    }

}
