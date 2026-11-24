package com.yutong.modules.user.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.DataPermission;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.desensitization.annotation.DataPermissionField;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 安全责任信息表
 * @Author: jeecg-boot
 * @Date:   2026-05-27
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="安全责任信息库VO", description="安全责任信息表")
public class YtsafetySysResponsibleVO {
    
	/**主键*/
    @ApiModelProperty(value = "主键")
	private Long id;

	/**用户主键*/
    @ApiModelProperty(value = "用户主键")
	private Long userId;

    /**姓名*/
    @DataPermissionField(deName="员工名",deCode = "emp_name")
    @Excel(name = "员工名", width = 15)
    @ApiModelProperty(value = "员工名")
    private String empName;

    /**用户类型*/
    @DataPermissionField(deName="类型",deCode = "user_type",enumMappings = {"0:非保卫类型","1:保卫类型"})
    @Excel(name = "类型",replace = {"非保卫类型_0", "保卫类型_1"}, width = 15)
    @ApiModelProperty(value = "类型")
    private Integer userType;

    /**部门名称*/
    @DataPermissionField(deName="部门",deCode = "depart_name")
    @Excel(name = "部门", width = 15)
    @ApiModelProperty(value = "部门名称")
    private String departName;

	/**有效期*/
    @DataPermissionField(deName="有效期",deCode = "valid_period")
	@Excel(name = "有效期", width = 20, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "有效期")
	private Date validPeriod;

    /**员工号*/
    @DataPermissionField(deName="员工号",deCode = "emp_no")
    @Excel(name = "员工号", width = 15)
    @ApiModelProperty(value = "员工号")
    private String empNo;

    /**员工号*/
    @DataPermissionField(deName="EIP用户名",deCode = "user_name")
    @Excel(name = "EIP用户名", width = 15)
    @ApiModelProperty(value = "EIP用户名")
    private String userName;

    /**授权厂区厂id，多个以逗号分割*/
    @DataPermissionField(deName="授权厂区",deCode = "power_factory_name")
    @Excel(name = "授权厂区", width = 15)
    @ApiModelProperty(value = "授权厂区厂，多个以逗号分割")
    private String powerFactoryName;

    /**平台角色*/
    @DataPermissionField(deName="平台角色",deCode = "role_name")
    @Excel(name = "平台角色", width = 15)
    @ApiModelProperty(value = "平台角色")
    private String roleName;

    /**描述*/
    @DataPermissionField(deName="描述",deCode = "remark")
    @Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private String remark;

    /**更新时间*/
    @DataPermissionField(deName="更新时间",deCode = "update_time")
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**状态:0 正常 1 冻结*/
    @DataPermissionField(deName="状态",deCode = "status",enumMappings = {"0:正常","1:冻结"})
    @Excel(name = "状态",replace = {"正常_0", "冻结_1"}, width = 15)
    @ApiModelProperty(value = "状态:0 正常 1 冻结")
    private Integer status;

	/**更新来源：0 手工创建 1 EHR匹配*/
    @DataPermissionField(deName="更新来源",deCode = "update_source",enumMappings = {"0:手工创建","1:EHR匹配"})
	@Excel(name = "更新来源",replace = {"手工创建_0", "EHR匹配_1"}, width = 15)
    @ApiModelProperty(value = "更新来源：0 手工创建 1 EHR匹配")
	private Integer updateSource;

}
