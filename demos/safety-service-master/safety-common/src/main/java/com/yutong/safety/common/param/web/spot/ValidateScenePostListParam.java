package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ValidateScenePostListParam extends WebPageParam {

    @ApiModelProperty("数据类型 1-现场岗位 2-模拟岗位")
    private Integer dataType;
    @ApiModelProperty("厂区DI")
    private Integer factoryId;
    @ApiModelProperty("厂区")
    private String factory;
    @ApiModelProperty("位置/岗位")
    private String postName;
    @ApiModelProperty("验证项目类型ID")
    private String teamId;
    @ApiModelProperty("验证项目类型")
    private String teamName;
    @ApiModelProperty("验证时间-开始")
    private String validateBegin;
    @ApiModelProperty("验证时间-截止")
    private String validateEnd;
    @ApiModelProperty("人员分类")
    private String personCategory;
    @ApiModelProperty("验证对象员工号")
    private String empNo;
    @ApiModelProperty("验证对象")
    private String empName;
    @ApiModelProperty("验证对象部门班组")
    private String empDept;
    @ApiModelProperty("验证对象直系领导员工号")
    private String leaderEmpNo;
    @ApiModelProperty("验证对象直系领导")
    private String leaderEmpName;
    @ApiModelProperty("是否合格 0-否 1-是")
    private Integer isPass;
    @ApiModelProperty("是否考核 0-否 1-是")
    private Integer isExamine;
    @ApiModelProperty("验证人员工号")
    private String createEmpNo;
    @ApiModelProperty("验证人姓名")
    private String createEmp;
    @ApiModelProperty("验证人班组")
    private String createDept;
    @ApiModelProperty("登记时间-开始")
    private String createBegin;
    @ApiModelProperty("登记时间-开始")
    private String createEnd;
    @ApiModelProperty("处理时间-开始")
    private String dealBegin;
    @ApiModelProperty("处理时间-开始")
    private String dealEnd;
    @ApiModelProperty("事件编号")
    private String flowNo;
    @ApiModelProperty("整改结果")
    private String reformDesc;
    @ApiModelProperty("工厂ID拼接-后台使用")
    private String factoryIds;
    @ApiModelProperty("分页标记-后台使用")
    private boolean pageFlag;
}
