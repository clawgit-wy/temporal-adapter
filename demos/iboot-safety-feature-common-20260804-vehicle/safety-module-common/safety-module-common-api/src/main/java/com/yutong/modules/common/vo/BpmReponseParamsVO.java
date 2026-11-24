package com.yutong.modules.common.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@ApiModel(value="流程响应参数", description="流程响应参数")
public class BpmReponseParamsVO {
    /**
     * 表单实例ID
     */
    private Long formInstanceId;
    /**
     * 模型标识
     */
    private String modelFlag;
    /**
     * 流程主键
     */
    private String flowId;
    /**
     * 流程编码
     */
    private String flowCode;
    /**
     * 流程主题
     */
    private String flowSubject;
    /**
     * 流程名称
     */
    private String flowName;
    /**
     * 流程状态
     */
    private Integer flowStatus;
    /**
     * 申请员工号
     */
    private String applyNo;

    /**
     * 申请人登录账号
     */
    private String applyLogin;
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
     * 任务主键
     */
    private String taskId;
    /**
     * 节点主键
     */
    private String nodeId;
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 节点定义处理人登录名
     */
    private String handlers;
    /**
     * 当前节点审批人 多个以“,”隔开
     */
    private String curNodeHandler;
    /**
     * 前一环节处理人
     */
    private String preNodeHandler;
    /**
     * 前一节点处理人名称
     */
    private String preNodeHandlerName;
    /**
     * 开始时间
     */
    private Date startDate;
    /**
     * 开始时间
     */
    private Date handlerDate;
    /**
     * 表单地址
     */
    private String formUrl;
    /**
     * 是否昵称
     */
    private String isNickName;
    /**
     * 昵称
     */
    private String nickName;

    /**
     * 昵称账号
     */
    private String nickAccount;


}
