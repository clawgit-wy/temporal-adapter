package com.yutong.safety.core.dto.fireInspection;

import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionTaskInfoDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FireInspectionTaskInfoDto extends SafetyFireInspectionTaskInfoDO {

    @ApiModelProperty("未完成数据量")
    private Integer waitCount = 0;

    @ApiModelProperty("已完成数据量")
    private Integer doneCount = 0;

    @ApiModelProperty("总数")
    private Integer totalCount = 0;

    @ApiModelProperty("颜色")
    private String taskColor = "";

    public void initCount(FireInspectionTaskInfoCountDto dto) {
        if (dto != null) {
            this.waitCount = dto.getWaitCount();
            this.doneCount = dto.getDoneCount();
            this.totalCount = dto.getTotalCount();
        } else {
            this.waitCount = 0;
            this.doneCount = 0;
            this.totalCount = 0;
        }
    }
}
