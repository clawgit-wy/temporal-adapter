package com.yutong.safety.core.dto;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;

@Data
public class SafetyOutsiderPromiseDTO extends WebPageParam implements Serializable {

    @ApiModelProperty("流程表主键ID")
    private String id;
    @ApiModelProperty("流程创建人员工号")
    private String creatorNo;
    @ApiModelProperty("创建人姓名")
    private String creatorName;
    @ApiModelProperty("员工号")
    private String empNo;
    @ApiModelProperty("姓名")
    private String empName;
    @ApiModelProperty("外协组织")
    private String empOrg;
    @ApiModelProperty("授权工厂")
    private String factory;
    @ApiModelProperty("调整时间")
    private String updateTime;
    @ApiModelProperty("状态")
    private Integer status;
    @ApiModelProperty("卡下发状态")
    private String cardStatus;
    @ApiModelProperty("服务截止日期")
    private String serviceEndDate;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("过滤登录人员工号-后台使用")
    private String powerNo;
    @ApiModelProperty("调整时间_开始")
    private String updateTimeStart;
    @ApiModelProperty("调整时间_结束")
    private String updateTimeEnd;
    @ApiModelProperty("流程号")
    private String flowId;
    @ApiModelProperty("手机号")
    private String phoneNo;

    @ApiModelProperty("车牌号")
    private List<String> carList;
    @ApiModelProperty("授权工厂-用户展示")
    private String factoryName;
    @ApiModelProperty("照片路径")
    private String photoUrl;

    /*
    在岗+时间正常=权限正常。
    在岗+时间到期=授权已到期
    离岗+时间正常=卡已注销
    离岗+时间到期=已离职
     */
    @ApiModelProperty("状态描述-用于展示")
    private String statusDesc;

    /* 增加检索条件 */
    @ApiModelProperty("权限状态:1-权限正常 2-授权已到期 3-卡已注销 4-已离职")
    private String powerStatus;
    @ApiModelProperty("是否有照片:1-有照片 0-无照片")
    private String photoFlag;

    /**
     * SQL条件，存在检索条件的话为false， 没有检索条件的话为true
     * true：只能看到发起人是自己的的数据，过滤掉为空的数据
     * false：可以看到发起人是自己的数据和查到的数据， 允许发起人为空或者是自己，
     */
    private boolean sqlWhereType = true;

    @Override
    public void check() {
        sqlWhereType = StringUtils.isEmpty(empNo) && StringUtils.isEmpty(empName) && StringUtils.isEmpty(phoneNo) && StringUtils.isEmpty(photoFlag)
                && StringUtils.isEmpty(status) && StringUtils.isEmpty(powerStatus) && StringUtils.isEmpty(updateTimeStart) && StringUtils.isEmpty(updateTimeEnd);
    }
}
