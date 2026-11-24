package com.yutong.safety.core.dto;

import com.yutong.safety.core.db.entity.SafetyFieldManageDO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author gwxiexj
 * @since 2024-04-29 14:58
 */
@Data
public class FieldManagerVODTO extends SafetyFieldManageDO {
    private Integer maxSort;
    private List<FieldManagerVODTO> regionList;
}
