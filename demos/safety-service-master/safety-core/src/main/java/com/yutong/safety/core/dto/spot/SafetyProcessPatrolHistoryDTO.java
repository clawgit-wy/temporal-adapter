package com.yutong.safety.core.dto.spot;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SafetyProcessPatrolHistoryDTO extends WebPageParam {

    @ApiModelProperty(value = "主键ID", example = "1")
    private int id;

    @ApiModelProperty(value = "safety_process_patrol ID", example = "234567890")
    private int patrolId;

    @ApiModelProperty(value = "事件发生时间", example = "2023-10-01 12:00:00")
    private String occurTime;

    @ApiModelProperty(value = "工厂ID", example = "123456789")
    private int factoryId;

    @ApiModelProperty(value = "工厂名称", example = "工厂A")
    private String factory;

    @ApiModelProperty(value = "事件类型", example = "安全检查")
    private String eventType;

    @ApiModelProperty(value = "事件ID", example = "102456789")
    private int eventId;

    @ApiModelProperty(value = "事项名称", example = "设备维护")
    private String teamName;

    @ApiModelProperty(value = "事项ID", example = "345678901")
    private int teamId;

    @ApiModelProperty(value = "事件等级", example = "高风险")
    private String eventLevel;

    @ApiModelProperty(value = "责任部门", example = "安全管理部门")
    private String dutyDept;

    @ApiModelProperty(value = "地理位置", example = "A区车间")
    private String eventAddress;

    @ApiModelProperty(value = "是否重点部位 0-否 1-是", example = "0")
    private int isKeynote;

    @ApiModelProperty(value = "问题描述", example = "设备故障")
    private String problemDesc;

    @ApiModelProperty(value = "问题图片", example = "image.jpg")
    private String problemImage;

    @ApiModelProperty(value = "是否当场处理 0-否 1-是", example = "1")
    private int isScene;

    @ApiModelProperty(value = "更新人", example = "张三")
    private String updateUser;

    private String flowNo;
}
