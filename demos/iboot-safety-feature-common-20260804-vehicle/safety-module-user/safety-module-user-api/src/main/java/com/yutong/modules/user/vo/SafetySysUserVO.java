package com.yutong.modules.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yutong.modules.user.entity.YtsafetySysUserDataRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.desensitization.annotation.DataPermissionField;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * @Description: 系统用户表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="safety_sys_user对象", description="系统用户表")
public class SafetySysUserVO {
    
	private Long id;

	/**用户名*/
    @DataPermissionField(deCode = "emp_name", deName = "员工名")
	@Excel(name = "员工名", width = 15, orderNum = "1")
    @ApiModelProperty(value = "员工名")
    private String empName;
	/**员工号*/
    @DataPermissionField(deCode = "emp_no", deName = "员工号")
    @Excel(name = "员工号", width = 15, orderNum = "2")
    @ApiModelProperty(value = "员工号")
    private String empNo;

    /**密码*/
    @DataPermissionField(deCode = "user_name", deName = "eip用户名")
    @Excel(name = "eip用户名", width = 15, orderNum = "5")
    @ApiModelProperty(value = "eip用户名")
    private String userName;

	/**修改时间*/
    @DataPermissionField(deCode = "update_time", deName = "修改日期")
    @Excel(name = "修改日期", width = 20, format = "yyyy-MM-dd HH:mm:ss", orderNum = "10")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改日期")
	private Date updateTime;

    /**授权厂区厂id，多个以逗号分割*/
    @DataPermissionField(deCode = "factory_names", deName = "授权厂区")
    @Excel(name = "授权厂区", width = 15, orderNum = "8")
    @ApiModelProperty(value = "授权厂区厂id，多个以逗号分割")
    private String factoryNames;

    //    @Excel(name = "部门", width = 15)

    @ApiModelProperty(value = "部门")
    private String orgName;

    @DataPermissionField(deCode = "role_names", deName = "角色")
    @Excel(name = "角色", orderNum = "9")
    @ApiModelProperty(value = "角色")
    private String roleNames;

    @DataPermissionField(deCode = "depart_name", deName = "部门")
    @Excel(name = "部门", orderNum = "6")
    @ApiModelProperty(value = "责任部门")
    private String departName;

    private String powerFactoryId;
    private String departmentId;
    private String roleId;

    private List<YtsafetySysUserDataRule> userDataRule;

}
