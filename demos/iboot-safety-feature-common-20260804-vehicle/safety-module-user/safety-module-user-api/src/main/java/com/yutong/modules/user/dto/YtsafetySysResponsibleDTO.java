package com.yutong.modules.user.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yutong.modules.user.entity.YtsafetySysUserDataRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * @Description: 安全责任信息表
 * @Author: jeecg-boot
 * @Date:   2026-05-27
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="安全责任信息表DTO", description="安全责任信息表")
public class YtsafetySysResponsibleDTO {

    /**id*/
    @ApiModelProperty(value = "id")
    private Long id;

    /**用户主键*/
    @ApiModelProperty(value = "用户主键")
    private Long userId;

    @ApiModelProperty(value = "用户类型")
    private Integer userType;

    @Excel(name = "所属厂区名称")
    @ApiModelProperty(value = "所属厂区名称")
    private String belongFactoryName;

    @Excel(name = "所属厂区Id")
    @ApiModelProperty(value = "所属厂区Id")
    private Long belongFactoryId;

    /**用户名*/
    @ApiModelProperty(value = "姓名")
    @NotBlank(message = "姓名不能为空")
    private String empName;

    /**员工号*/
    @ApiModelProperty(value = "员工号")
    @NotBlank(message = "员工号不能为空")
    private String empNo;

    /**EIP登录账号*/
    @ApiModelProperty(value = "EIP登录账号")
    @NotBlank(message = "EIP登录账号不能为空")
    private String userName;

    /**授权厂区厂id，多个以逗号分割*/
    @ApiModelProperty(value = "授权厂区厂id，多个以逗号分割")
    private String powerFactoryId;

    /**部门主键*/
    @ApiModelProperty(value = "部门，多个以逗号分割")
    private String departId;

    @ApiModelProperty(value = "用户角色，多个以逗号分割")
    private String roleId;

    /**有效期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "有效期")
    private Date validPeriod;

    /**状态:0 正常 1 冻结*/
    @Excel(name = "状态:0 正常 1 冻结", width = 15)
    @ApiModelProperty(value = "状态:0 正常 1 冻结")
    private Integer status;

    /**描述*/
    @Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private String remark;

}
