package com.yutong.modules.risk.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yutong.modules.risk.out.SafetyGoodsOutExceptionDO;
import com.yutong.modules.risk.vo.YTSafetyGoodsOutDetailGoodsDOVO;
import com.yutong.modules.risk.web.WebPageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 环境验证表
 * @Author: jeecg-boot
 * @Date:   2024-12-17
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_validate_environment")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_validate_environment对象", description="环境验证表")
public class YTSafetyValidateEnvironment extends WebPageParam implements Serializable {
    private static final long serialVersionUID =  4992731550947352817L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

    /**事件编号*/
    @Excel(name = "事件编号", width = 15)
    @ApiModelProperty(value = "事件编号")
    private String flowNo;

    /**工厂id*/
    @ApiModelProperty(value = "工厂id")
    private Long factoryId;

    /**工厂*/
    @Excel(name = "工厂", width = 15)
    @ApiModelProperty(value = "工厂")
    private String factory;

    /**抽查时间*/
    @Excel(name = "抽查时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "抽查时间")
    private String checkTime;

    /**位置*/
    @Excel(name = "位置", width = 15)
    @ApiModelProperty(value = "位置")
    private String location;

    /**车牌号*/
    @Excel(name = "车牌号", width = 15)
    @ApiModelProperty(value = "车牌号")
    private String carNumber;

    /**驾驶员姓名*/
    @Excel(name = "驾驶员姓名", width = 15)
    @ApiModelProperty(value = "驾驶员姓名")
    private String driverName;

    /**联系电话*/
    @Excel(name = "联系电话", width = 15)
    @ApiModelProperty(value = "联系电话")
    private String telephone;

    /**排放标准*/
    @Excel(name = "排放标准", width = 15)
    @ApiModelProperty(value = "排放标准")
    private String emissionStandard;

    /**是否加入黑名单 0-否 1-是*/
    @Excel(name = "是否加入黑名单", width = 15)
    @ApiModelProperty(value = "是否加入黑名单 0-否 1-是")
    private Integer isBlacklist;

    /**抽查人*/
    @Excel(name = "抽查人", width = 15)
    @ApiModelProperty(value = "抽查人")
    private String checkPerson;

    /**抽查人员工号*/
    @Excel(name = "抽查人员工号", width = 15)
    @ApiModelProperty(value = "抽查人员工号")
    private String checkPersonNo;

    /**抽查人部门*/
    @Excel(name = "抽查人部门", width = 15)
    @ApiModelProperty(value = "抽查人部门")
    private String checkDept;

    /**加入黑名单时间*/
    @Excel(name = "加入黑名单时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "加入黑名单时间")
    private String blackTime;

    /**加入黑名单结果*/
    @Excel(name = "加入黑名单结果", width = 15)
    @ApiModelProperty(value = "加入黑名单结果")
    private String blackResult;

    /**出入口接口返回值*/
    @Excel(name = "出入口接口返回值", width = 15)
    @ApiModelProperty(value = "出入口接口返回值")
    private String interfaceStatus;

    /**创建时间*/
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**更新时间*/
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**更新人员工号*/
    @Excel(name = "更新人员工号", width = 15)
    @ApiModelProperty(value = "更新人员工号")
    private String updatePersonNo;

    /**事件ID*/
    @Excel(name = "事件ID", width = 15)
    @ApiModelProperty(value = "事件ID")
    private Integer eventId;

    /**事件类型*/
    @Excel(name = "事件类型", width = 15)
    @ApiModelProperty(value = "事件类型")
    private String eventType;

    /**事项ID*/
    @Excel(name = "事项ID", width = 15)
    @ApiModelProperty(value = "事项ID")
    private Integer teamId;

    /**事项*/
    @Excel(name = "事项", width = 15)
    @ApiModelProperty(value = "事项")
    private String teamName;

    /**任务ID*/
    @Excel(name = "任务ID", width = 15)
    @ApiModelProperty(value = "任务ID")
    private Integer taskId;

    /**计划ID*/
    @Excel(name = "计划ID", width = 15)
    @ApiModelProperty(value = "计划ID")
    private Integer planId;

    /**计划名称*/
    @Excel(name = "计划名称", width = 15)
    @ApiModelProperty(value = "计划名称")
    private String planName;

    /**事件来源 1-周期巡查 2-事件上报*/
    @Excel(name = "事件来源", width = 15)
    @ApiModelProperty(value = "事件来源 1-周期巡查 2-事件上报")
    private Integer eventSource;

    /**周期任务开始日期*/
    @Excel(name = "周期任务开始日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "周期任务开始日期")
    private String taskBeginDate;

    /**周期任务截止日期*/
    @Excel(name = "周期任务截止日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "周期任务截止日期")
    private String taskEndDate;

    /**创建人*/
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private String creator;

    /**磅单重量*/
    @Excel(name = "磅单重量", width = 15)
    @ApiModelProperty(value = "磅单重量")
    private String weightListWeight;

    /**复检重量*/
    @Excel(name = "复检重量", width = 15)
    @ApiModelProperty(value = "复检重量")
    private String recheckWeight;

    /**业务类型*/
    @Excel(name = "业务类型", width = 15)
    @ApiModelProperty(value = "业务类型")
    private String businessType;

    /**业务类型编码*/
    @Excel(name = "业务类型编码", width = 15)
    @ApiModelProperty(value = "业务类型编码")
    private String businessTypeCode;

    @ExcelIgnore
    private String isExport;

    @ExcelIgnore
    private String validAccess;
    @ExcelIgnore
    private String exceptionType;
    @ExcelIgnore
    private String remark;


    @ExcelIgnore
    @ApiModelProperty("当前登录人id")
    private String loginNameEip;
    @ExcelIgnore
    @ApiModelProperty("当前登录人code")
    private String loginNameCode;
    @ExcelIgnore
    @ApiModelProperty("当前登录人")
    private String loginName;
    @ExcelIgnore
    @ApiModelProperty("当前登录人电话")
    private String loginNamePhone;
    @ExcelIgnore
    @ApiModelProperty("当前登录人部门")
    private String loginNameDept;


    @ExcelIgnore
    @ApiModelProperty("车辆及货物信息")
    private Map<String,Object> carAndGoodsInfoMap;

    @ExcelIgnore
    @ApiModelProperty("货物明细")
    private List<YTSafetyGoodsOutDetailGoodsDOVO> googsList;

    @ApiModelProperty("异常信息")
    private SafetyGoodsOutExceptionDO exceptionDo;
}