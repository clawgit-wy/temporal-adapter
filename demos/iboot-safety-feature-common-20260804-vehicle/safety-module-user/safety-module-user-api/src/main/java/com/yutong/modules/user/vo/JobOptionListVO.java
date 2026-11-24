package com.yutong.modules.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="岗位下拉框VO对象", description="岗位下拉框VO对象")
public class JobOptionListVO {

    @ApiModelProperty("岗位编码")
    private String jobCode;

    @ApiModelProperty("岗位名称")
    private String jobName;
}
