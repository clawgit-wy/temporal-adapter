package com.yutong.safety.core.dto;

import com.yutong.safety.core.db.entity.spot.SafetyPatrolDealHistoryDO;
import com.yutong.safety.core.db.entity.spot.SafetyPatrolRecordHistoryDO;
import com.yutong.safety.core.db.entity.spot.SafetyProcessPatrolDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SafetyProcessPatrolDTO extends SafetyProcessPatrolDO {

    @ApiModelProperty("事件信息修改记录")
    private List<SafetyPatrolRecordHistoryDO> recordHistoryDOS;

    @ApiModelProperty("巡查处理结果记录")
    private List<SafetyPatrolDealHistoryDO> dealHistoryDOS;

    @ApiModelProperty("任务-备注")
    private String planRemark;
}
