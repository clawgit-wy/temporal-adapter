package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.param.web.WebPageParam;
import lombok.Data;

import java.io.Serializable;

@Data
public class PatrolRecordHistoryListParam extends WebPageParam implements Serializable {

    private Integer processId;

    private String eventLevel;
}
