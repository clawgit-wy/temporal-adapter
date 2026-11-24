package com.yutong.safety.common.utils.excel;

/**
 * Created by gwrenhx on 2017-7-25.
    错误信息 对象
 */
public class ErrorInfo {
    /**
     * sheetName:单元
     * rowNum:行号
     * columnName:列名
     * errorInfo:错误描述
     */
    private String sheetName ;
    private Integer rowNum ;
    private String columnName;
    private String errorInfo;

    public ErrorInfo() {
    }
    public ErrorInfo(Integer rowNum, String columnName, String errorInfo) {
        this.rowNum = rowNum;
        this.columnName = columnName;
        this.errorInfo = errorInfo;
    }
    public ErrorInfo(String sheetName, Integer rowNum, String columnName, String errorInfo) {
        this.sheetName = sheetName;
        this.rowNum = rowNum;
        this.columnName = columnName;
        this.errorInfo = errorInfo;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}
