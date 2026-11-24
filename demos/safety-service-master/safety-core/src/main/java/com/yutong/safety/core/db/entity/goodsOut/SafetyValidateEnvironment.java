package com.yutong.safety.core.db.entity.goodsOut;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SafetyValidateEnvironment extends WebPageParam implements Serializable {

	private static final long serialVersionUID =  4992731550947352817L;

   	@ApiModelProperty("null")
	private Integer id;

	/**
	 * 事件编号
	 */
   	@ApiModelProperty("事件编号")
	private String flowNo;

   	@ApiModelProperty("工厂id")
	private Integer factoryId;

	/**
	 * 工厂
	 */
   	@ApiModelProperty("工厂")
	private String factory;

	/**
	 * 抽查时间
	 */
   	@ApiModelProperty("抽查时间")
	private String checkTime;

	/**
	 * 位置
	 */
   	@ApiModelProperty("位置")
	private String location;

	/**
	 * 车牌号
	 */
   	@ApiModelProperty("车牌号")
	private String carNumber;

	/**
	 * 驾驶员姓名
	 */
   	@ApiModelProperty("驾驶员姓名")
	private String driverName;

	/**
	 * 联系电话
	 */
   	@ApiModelProperty("联系电话")
	private String telephone;

	/**
	 * 排放标准
	 */
   	@ApiModelProperty("排放标准")
	private String emissionStandard;

	/**
	 * 是否加入黑名单 0-否 1-是
	 */
   	@ApiModelProperty("是否加入黑名单 0-否 1-是")
	private Integer isBlacklist;

	/**
	 * 抽查人
	 */
   	@ApiModelProperty("抽查人")
	private String checkPerson;

	/**
	 * 抽查人员工号
	 */
   	@ApiModelProperty("抽查人员工号")
	private String checkPersonNo;

	/**
	 * 抽查人部门
	 */
   	@ApiModelProperty("抽查人部门")
	private String checkDept;

	/**
	 * 加入黑名单时间
	 */
   	@ApiModelProperty("加入黑名单时间")
	private String blackTime;

	/**
	 * 加入黑名单结果
	 */
   	@ApiModelProperty("加入黑名单结果")
	private String blackResult;

	/**
	 * 出入口接口返回值
	 */
   	@ApiModelProperty("出入口接口返回值")
	private String interfaceStatus;

	/**
	 * 创建时间
	 */
   	@ApiModelProperty("创建时间")
	private Date createTime;

	/**
	 * 更新时间
	 */
   	@ApiModelProperty("更新时间")
	private Date updateTime;

	/**
	 * 更新人员工号
	 */
   	@ApiModelProperty("更新人员工号")
	private String updatePersonNo;

	/**
	 * 事件ID
	 */
   	@ApiModelProperty("事件ID")
	private Integer eventId;

	/**
	 * 事件类型
	 */
   	@ApiModelProperty("事件类型")
	private String eventType;

	/**
	 * 事项ID
	 */
   	@ApiModelProperty("事项ID")
	private Integer teamId;

	/**
	 * 事项
	 */
   	@ApiModelProperty("事项")
	private String teamName;

	/**
	 * 任务ID
	 */
   	@ApiModelProperty("任务ID")
	private Integer taskId;

	/**
	 * 计划ID
	 */
   	@ApiModelProperty("计划ID")
	private Integer planId;

	/**
	 * 计划名称
	 */
   	@ApiModelProperty("计划名称")
	private String planName;

	/**
	 * 事件来源 1-周期巡查 2-事件上报
	 */
   	@ApiModelProperty("事件来源 1-周期巡查 2-事件上报")
	private Integer eventSource;

	/**
	 * 周期任务开始日期
	 */
   	@ApiModelProperty("周期任务开始日期")
	private String taskBeginDate;

	/**
	 * 周期任务截止日期
	 */
   	@ApiModelProperty("周期任务截止日期")
	private String taskEndDate;

	/**
	 * 创建人
	 */
   	@ApiModelProperty("创建人")
	private String creator;

	/**
	 * 磅单重量
	 */
   	@ApiModelProperty("磅单重量")
	private String weightListWeight;

	/**
	 * 复检重量
	 */
   	@ApiModelProperty("复检重量")
	private String recheckWeight;

	/**
	 * 业务类型
	 */
   	@ApiModelProperty("业务类型")
	private String businessType;

	/**
	 * 业务类型编码
	 */
   	@ApiModelProperty("业务类型编码")
	private String businessTypeCode;

}
