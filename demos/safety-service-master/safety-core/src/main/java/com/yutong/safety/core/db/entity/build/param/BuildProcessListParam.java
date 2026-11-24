package com.yutong.safety.core.db.entity.build.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BuildProcessListParam extends WebPageParam {

    @ApiModelProperty("分页标记-不需前端传值")
    private Integer pageFlag;

    @ApiModelProperty("申请人姓名/员工号")
    private String creator;

    @ApiModelProperty("流程状态 10 草稿 20 待审批 30-发布")
    private String status;

    @ApiModelProperty("当前责任人-动态查询申请部门的保密负责人")
    private String curDutyPerson;

    @ApiModelProperty("申请人部门")
    private String createDept;

    @ApiModelProperty("对接部门")
    private String acrossDept;

    @ApiModelProperty("对接部门名称")
    private String acrossDeptName;

    @ApiModelProperty("厂区编码")
    private String factoryCode;

    @ApiModelProperty("厂区名称")
    private String factoryName;

    @ApiModelProperty("楼栋编码")
    private String buildingCode;

    @ApiModelProperty("楼栋名称")
    private String buildingName;

    @ApiModelProperty("门禁")
    private String deviceName;

    @ApiModelProperty("提交时间-起始")
    private String createTimeBegin;

    @ApiModelProperty("提交时间-截止")
    private String createTimeEnd;

    @ApiModelProperty("BPM流程ID")
    private String bpmFlowId;

    @ApiModelProperty("主键ID集合")
    private List<String> idList;

    @ApiModelProperty("过滤部门范围-后端使用")
    private String filterDutyDept;

    @ApiModelProperty("过滤登录人-后端使用")
    private String createEmpLogin;

    @ApiModelProperty("跳转链接-后端使用")
    private String bpmPreUrl;
}
