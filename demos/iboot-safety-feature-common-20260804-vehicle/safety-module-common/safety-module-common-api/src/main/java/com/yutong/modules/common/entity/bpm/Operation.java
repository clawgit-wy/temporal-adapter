package com.yutong.modules.common.entity.bpm;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@ApiModel(value="流程操作参数", description="流程操作参数")
public class Operation {

    private String toOtherHandlerIds;
    /**
     * 驳回目标节点编号
     */
    @ApiModelProperty(value = "驳回目标节点编号")
    private String jumpToNodeId;

    /**
     * 驳回通过后是否直接返回本节点
     */
    @ApiModelProperty(value = "驳回通过后是否直接返回本节点")
    private Boolean refusePassedToThisNode;

    /**
     * 通知类型
     */
    @ApiModelProperty(value = "通知类型")
    private String notifyType;

    /**
     * 审批意见
     */
    @ApiModelProperty(value = "审批意见")
    private String auditNote;

    private String auditNoteFdId;

    private String identityId;

    /**
     * 流程结束时是否通知本人
     */
    @ApiModelProperty(value = "流程结束时是否通知本人")
    private Boolean notifyOnFinish;

    /**
     * 附件
     */
    @ApiModelProperty(value = "附件")
    private List<NoteAttachments> auditNoteAttachments;
}
