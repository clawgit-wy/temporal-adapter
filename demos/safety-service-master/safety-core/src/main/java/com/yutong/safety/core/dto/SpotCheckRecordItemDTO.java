package com.yutong.safety.core.dto;

import lombok.Data;

import java.util.List;

@Data
public class SpotCheckRecordItemDTO{

    private String deviceType;
    private List<SpotCheckRecordDTO> subList;
}
