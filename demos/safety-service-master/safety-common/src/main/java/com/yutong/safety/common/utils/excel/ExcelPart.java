package com.yutong.safety.common.utils.excel;

import lombok.Data;

import java.util.List;

@Data
public class ExcelPart {
    private int cellIndex;
    private List<String> partList;
}
