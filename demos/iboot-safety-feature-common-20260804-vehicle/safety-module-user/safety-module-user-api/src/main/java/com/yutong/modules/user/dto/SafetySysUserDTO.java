package com.yutong.modules.user.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yutong.modules.user.entity.YtsafetySysUserDataRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
public class SafetySysUserDTO {
    
	/**id*/
    @ApiModelProperty(value = "id")
	private Long id;

	/**用户名*/
	@Excel(name = "员工名")
    @ApiModelProperty(value = "员工名")
    @NotBlank(message = "员工名不能为空")
	private String empName;

	/**员工号*/
	@Excel(name = "员工号")
    @ApiModelProperty(value = "员工号")
    @NotBlank(message = "员工号不能为空")
	private String empNo;

    @Excel(name = "eip用户名")
    @ApiModelProperty(value = "eip用户名")
    @NotBlank(message = "eip用户名不能为空")
    private String userName;

	/**访客发卡工厂id，多个以逗号分割*/
	@Excel(name = "访客发卡工厂id，多个以逗号分割")
    @ApiModelProperty(value = "访客发卡工厂id，多个以逗号分割")
	private String factoryId;

	/**授权厂区厂id，多个以逗号分割*/
	@Excel(name = "授权厂区厂")
    @ApiModelProperty(value = "授权厂区厂id，多个以逗号分割")
//    @NotBlank(message = "授权厂区不能为空")
    private String powerFactoryId;

    private String role;

    @Excel(name = "角色")
    @NotBlank(message = "角色不能为空")
    @ApiModelProperty(value = "角色id，多个以逗号分割")
    private String roleId;

    @ApiModelProperty(value = "手机号")
    private String phone;


    @Excel(name = "责任部门")
    @ApiModelProperty(value = "责任部门id，多个以逗号分割")
    private String departmentId;
    @Valid
    private List<YtsafetySysUserDataRuleDTO> userDataRule;
}
