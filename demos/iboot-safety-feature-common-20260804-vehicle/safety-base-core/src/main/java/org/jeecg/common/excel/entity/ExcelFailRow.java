package org.jeecg.common.excel.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Excel 导入失败行数据
 */
@Data
public class ExcelFailRow {

    /**
     * 行号（从 1 开始）
     */
    private int rowNum;

    /**
     * 行数据（列名 -> 值）
     */
    private Map<String, String> rowData = new HashMap<>();

    /**
     * 错误原因
     */
    private String errorMsg;
}