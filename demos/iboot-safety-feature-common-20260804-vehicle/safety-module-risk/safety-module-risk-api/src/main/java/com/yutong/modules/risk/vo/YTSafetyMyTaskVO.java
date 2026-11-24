package com.yutong.modules.risk.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class YTSafetyMyTaskVO {

    @ApiModelProperty(value = "任务总数")
    private Integer taskTotal;

    @ApiModelProperty(value = "完成数")
    private Integer completedQuantity;

    IPage<YTSafetyMyTaskInfoVO> taskInfoList;

}
