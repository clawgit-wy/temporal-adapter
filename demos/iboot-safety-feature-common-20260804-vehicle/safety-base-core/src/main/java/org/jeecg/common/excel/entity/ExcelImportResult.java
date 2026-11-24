package org.jeecg.common.excel.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel 导入结果
 */
@Data
public class ExcelImportResult<T> {

    /**
     * 成功导入的数据列表
     */
    private List<T> successList = new ArrayList<>();

    /**
     * 导入失败的数据列表
     */
    private List<ExcelFailRow> failList = new ArrayList<>();

    /**
     * 是否全部成功
     */
    public boolean isAllSuccess() {
        return failList.isEmpty();
    }

    /**
     * 成功数量
     */
    public int getSuccessCount() {
        return successList.size();
    }

    /**
     * 失败数量
     */
    public int getFailCount() {
        return failList.size();
    }
}