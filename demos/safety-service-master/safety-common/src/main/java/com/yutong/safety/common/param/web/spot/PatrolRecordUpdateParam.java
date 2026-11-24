package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.param.web.ProcessParam;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class PatrolRecordUpdateParam implements Check {

    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("事件编号")
    private String flowNo;

    @ApiModelProperty("事件来源 1-周期巡查 2-事件上报")
    private Integer eventSource;

    @ApiModelProperty("事件类型ID")
    private Integer eventId;

    @ApiModelProperty("事件类型")
    private String eventType;

    @ApiModelProperty("事项ID")
    private Integer teamId;

    @ApiModelProperty("事项")
    private String teamName;

    @ApiModelProperty("事件等级")
    private String eventLevel;

    @ApiModelProperty("责任部门")
    private String dutyDept;

    @ApiModelProperty("地址位置")
    private String eventAddress;

    @ApiModelProperty("是否重点部门 0-否 1-是")
    private Integer isKeynote;

    @ApiModelProperty("问题描述")
    private String problemDesc;

    @ApiModelProperty("问题图片")
    private String problemImage;

    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty("更新人")
    private String updatePerson;

    @ApiModelProperty("更新人员工号")
    private String updatePersonNo;

    @ApiModelProperty("节点标识 1治安管理员 2整改责任人 3验证节点")
    private String nodeFlag;

    @ApiModelProperty("当前用户登录名")
    private String loginName;

    @ApiModelProperty("bpm流程ID")
    private String flowId;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(id), ResponseEnum.PARAM_ERROR.getCode(), "id不能为空！");
    }
}
