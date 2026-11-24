package com.yutong.modules.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yutong.modules.user.model.SysDictSelect;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.desensitization.annotation.DataPermissionField;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="安全责任人变更人员信息", description="安全责任人变更人员信息")
public class YtsafetyResponsibleChangeVO {
    /**主键*/
    @ApiModelProperty(value = "主键")
    private Long id;
    /**变更人员主键*/
    @ApiModelProperty(value = "变更人员主键")
    private Long userId;
    /**工号*/
    @ApiModelProperty(value = "工号")
    private String empNo;
    /**姓名*/
    @ApiModelProperty(value = "姓名")
    private String empName;
    /**EIP用户名*/
    @ApiModelProperty(value = "EIP用户名")
    private String userName;
    /**手机号*/
    @ApiModelProperty(value = "手机号")
    private String phone;
    /**组织机构*/
    @ApiModelProperty(value = "组织机构")
    private String orgName;
    /**岗位*/
    @ApiModelProperty(value = "岗位")
    private String positionName;
    /**邮箱*/
    @ApiModelProperty(value = "邮箱")
    private String email;
    /**授权厂区*/
    @ApiModelProperty(value = "授权厂区")
    private List<SysDictSelect> factoryId;
    /**责任部门*/
    @ApiModelProperty(value = "责任部门")
    private List<SysDictSelect> departId;
    /**授权角色*/
    @ApiModelProperty(value = "授权角色")
    private List<SysDictSelect> roleId;
    /**有效期*/
    @DataPermissionField(deName="有效期",deCode = "valid_period")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "有效期")
    private Date validPeriod;
}
