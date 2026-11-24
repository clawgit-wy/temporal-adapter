package com.yutong.safety.core.dto;

import com.yutong.safety.core.db.entity.SafetyFieldManageDO;
import lombok.Data;

import java.util.List;

/**
 * create by gwcuiyx on 2023.08.03
 */
@Data
public class FieldManageDTO extends SafetyFieldManageDO {
    private List<FieldManageDTO> regionList;
}
