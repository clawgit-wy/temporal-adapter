package com.yutong.safety.core.db.entity.enter.search;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EmpCardParam implements Serializable {
    private String updateTimeBeign;
    private String cardStatus;
    private Boolean isNullTokenId;
    private List<String> empNos;
}
