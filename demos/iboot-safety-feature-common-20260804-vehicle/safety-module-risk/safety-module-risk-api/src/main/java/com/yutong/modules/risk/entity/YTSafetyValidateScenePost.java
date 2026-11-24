package com.yutong.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 现场验证岗位表
 * @Author: jeecg-boot
 * @Date:   2024-06-24
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_validate_scene_post")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_validate_scene_post对象", description="现场验证岗位表")
public class YTSafetyValidateScenePost implements Serializable {

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private Long id;

    /**数据类型*/
    @Excel(name = "数据类型", width = 15)
    @ApiModelProperty(value = "数据类型")
    private Integer dataType;

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

    /**事项ID*/
    @Excel(name = "事项ID", width = 15)
    @ApiModelProperty(value = "事项ID")
    private String teamId;

    /**事项*/
    @Excel(name = "事项", width = 15)
    @ApiModelProperty(value = "事项")
    private String teamName;

    /**人员类别*/
    @Excel(name = "人员类别", width = 15)
    @ApiModelProperty(value = "人员类别")
    private String personCategory;

    /**岗位名称*/
    @Excel(name = "岗位名称", width = 15)
    @ApiModelProperty(value = "岗位名称")
    private String postName;

    /**员工工号*/
    @Excel(name = "员工工号", width = 15)
    @ApiModelProperty(value = "员工工号")
    private String empNo;

    /**员工姓名*/
    @Excel(name = "员工姓名", width = 15)
    @ApiModelProperty(value = "员工姓名")
    private String empName;

    /**员工部门*/
    @Excel(name = "员工部门", width = 15)
    @ApiModelProperty(value = "员工部门")
    private String empDept;

    /**到达时长*/
    @Excel(name = "到达时长", width = 15)
    @ApiModelProperty(value = "到达时长")
    private Integer reachDuration;

    /**负责人工号*/
    @Excel(name = "负责人工号", width = 15)
    @ApiModelProperty(value = "负责人工号")
    private String leaderEmpNo;

    /**负责人姓名*/
    @Excel(name = "负责人姓名", width = 15)
    @ApiModelProperty(value = "负责人姓名")
    private String leaderEmpName;

    /**验证组*/
    @Excel(name = "验证组", width = 15)
    @ApiModelProperty(value = "验证组")
    private String validateTeam;

    /**验证描述*/
    @Excel(name = "验证描述", width = 15)
    @ApiModelProperty(value = "验证描述")
    private String validateDesc;

    /**验证开始时间*/
    @Excel(name = "验证开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "验证开始时间")
    private String validateBeginTime;

    /**验证结束时间*/
    @Excel(name = "验证结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "验证结束时间")
    private String validateEndTime;

    /**创建人工号*/
    @Excel(name = "创建人工号", width = 15)
    @ApiModelProperty(value = "创建人工号")
    private String createEmpNo;

    /**创建人*/
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private String createEmp;

    /**创建人部门*/
    @Excel(name = "创建人部门", width = 15)
    @ApiModelProperty(value = "创建人部门")
    private String createDept;

    /**创建时间*/
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**是否通过(0-未通过 1-通过)*/
    @Excel(name = "是否通过", width = 15)
    @ApiModelProperty(value = "是否通过(0-未通过 1-通过)")
    private Integer isPass;

    /**是否审核(0-未审核 1-已审核)*/
    @Excel(name = "是否审核", width = 15)
    @ApiModelProperty(value = "是否审核(0-未审核 1-已审核)")
    private Integer isExamine;

    /**整改描述*/
    @Excel(name = "整改描述", width = 15)
    @ApiModelProperty(value = "整改描述")
    private String reformDesc;

    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;

    /**是否整改(0-未整改 1-已整改)*/
    @Excel(name = "是否整改", width = 15)
    @ApiModelProperty(value = "是否整改(0-未整改 1-已整改)")
    private Integer isReform;

    /**处理时间*/
    @Excel(name = "处理时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "处理时间")
    private Date dealTime;

    /**更新人工号*/
    @Excel(name = "更新人工号", width = 15)
    @ApiModelProperty(value = "更新人工号")
    private String updateEmpNo;

    /**更新人*/
    @Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
    private String updateEmp;

    /**更新时间*/
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**节点标识*/
    @Excel(name = "节点标识", width = 15)
    @ApiModelProperty(value = "节点标识")
    private String nodeFlag;

    /**删除标记*/
    @Excel(name = "删除标记", width = 15)
    @ApiModelProperty(value = "删除标记")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;

    /**修改版本*/
    @Excel(name = "修改版本", width = 15)
    @ApiModelProperty(value = "修改版本")
    @Version
    private Integer version;

    private static final long serialVersionUID = 1L;
}