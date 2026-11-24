package com.yutong.safety.core.dto;

import lombok.Data;

import java.util.List;

/**
 * create by gwcuiyx on 2023.07.12
 */
@Data
public class FieldManageListDTO {
    /**
     * 厂区名称列表
     */
    private List<FieldManageDTO> factoryList;
    /**
     * 来访事由列表
     */
    private List<FieldManageDTO> visitList;
}
