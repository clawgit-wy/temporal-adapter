package com.yutong.modules.common.entity.bpm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@ApiModel(value="流程节点附件", description="流程节点附件")
public class NoteAttachments {

    /**
     * 附件唯一标识
     */
    @ApiModelProperty(value = "附件唯一标识")
    private String attId;

    /**
     * 附件名称
     */
    @ApiModelProperty(value = "附件名称")
    private String attName;

    /**
     * 附件地址
     */
    @ApiModelProperty(value = "附件地址")
    private String attUrl;
}
