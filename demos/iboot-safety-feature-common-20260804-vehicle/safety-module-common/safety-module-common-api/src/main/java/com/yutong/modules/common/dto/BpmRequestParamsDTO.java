package com.yutong.modules.common.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@ApiModel(value="流程执行请求参数", description="流程执行请求参数")
public class BpmRequestParamsDTO {
    /**
     * 表单实例ID
     */
    @NotNull
    private Long formInstanceId;
    /**
     * 流程编码
     */
    private String flowCode;
    /**
     * 流程主题
     */
    private String flowSubject;
    /**
     * 模型标识
     */
    private String modelFlag;
    /**
     * 登录账号
     */
    private String applyEipName;
    /**
     * 申请员工登录账号
     */
    private String loginName;
    /**
     * 申请员工号
     */
    private String applyNo;
    /**
     * 申请员工名
     */
    private String applyName;
    /**
     * 申请员工部门编号
     */
    private String applyDepartCode;
    /**
     * 申请员工部门名称
     */
    private String applyDepartName;
    /**
     * 所属部门主键
     */
    private Long factoryId;
    /**
     * 所属厂区名称
     */
    private String factoryName;
    /**
     * 当前节点审批人 多个以“,”隔开
     */
    private String curNodeHandler;
    /**
     * 前一环节处理人
     */
    private String preNodeHandler;
    /**
     * 前一环节处理人
     */
    private String preNodeHandlerName;
    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 是否昵称
     */
    private String isNickname;


    /**
     * 昵称
     */
    private String nickName;




}
