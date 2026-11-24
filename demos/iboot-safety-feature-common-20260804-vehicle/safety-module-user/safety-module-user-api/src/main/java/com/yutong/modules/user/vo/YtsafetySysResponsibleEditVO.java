package com.yutong.modules.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yutong.modules.user.dto.YtsafetySysUserDataRuleDTO;
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
@ApiModel(value="安全责任信息库编辑回显VO", description="安全责任信息表")
public class YtsafetySysResponsibleEditVO {

    /**id*/
    @ApiModelProperty(value = "id")
    private Long id;

    /**用户主键*/
    @ApiModelProperty(value = "用户主键")
    private Long userId;

    /**EIP用户名*/
    @ApiModelProperty(value = "EIP用户名")
    private String userName;

    /**姓名*/
    @ApiModelProperty(value = "姓名")
    private String empName;

    /**员工号*/
    @ApiModelProperty(value = "员工号")
    private String empNo;

    @ApiModelProperty(value = "电话")
    private String phone;

    /**组织名称*/
    @ApiModelProperty(value = "组织名称")
    private String orgName;

    /**电子邮箱*/
    @ApiModelProperty(value = "电子邮箱")
    private String email;

    /**所属厂区主键*/
    @ApiModelProperty(value = "所属厂区主键")
    private Long belongFactoryId;

    /**所属厂区*/
    @ApiModelProperty(value = "所属厂区名称")
    private String belongFactoryName;

    /**授权厂区厂id，多个以逗号分割*/
    @ApiModelProperty(value = "授权厂区厂id，多个以逗号分割")
    private String powerFactoryId;

    /**部门主键*/
    @ApiModelProperty(value = "部门，多个以逗号分割")
    private String departId;


    @ApiModelProperty(value = "用户角色，多个以逗号分割")
    private String roleId;

    /**有效期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "有效期")
    private Date validPeriod;

    /**状态:0 正常 1 冻结*/
    @ApiModelProperty(value = "状态:0 正常 1 冻结")
    private Integer status;

    /**人员类型 0 非保卫人员 1 保卫人员*/
    @ApiModelProperty(value = "人员类型 0 非保卫人员 1 保卫人员")
    private Integer userType;

    /**描述*/
    @Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private String remark;

}
