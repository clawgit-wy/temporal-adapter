package com.yutong.modules.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelVerify;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="安全责任信息表导入DTO", description="安全责任信息表导入DTO")
public class YtsafetySysResponsibleImportDTO {

    /**员工号*/
    @Excel(name="员工号*",width=15)
    @ApiModelProperty(value = "员工号")
    private String empNo;

    /**用户名*/
    @Excel(name="员工名",width=15)
    @ApiModelProperty(value = "员工名")
    private String empName;

    /**角色名称*/
    @Excel(name="角色名称*",width=15)
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    /**有效期*/
    @Excel(name="有效期",width=15,format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "有效期")
    private Date validPeriod;

    /**部门名称*/
    @Excel(name="授权部门",width=15)
    @ApiModelProperty(value = "授权部门")
    private String departName;

    /**厂区名称*/
    @Excel(name="授权厂区",width=15)
    @ApiModelProperty(value = "授权厂区")
    private String factoryName;

    /**
     * 错误信息（Jeecg固定字段，必须加）
     */
    @Excel(name="异常信息",width=15)
    @ApiModelProperty(value = "异常信息")
    private String errorMsg;

    private Integer isRepeat=0;

    public boolean IsEmpty() {
        return this.empName == null && this.departName == null && this.empNo == null && this.factoryName == null && this.roleName == null && this.validPeriod == null;
    }
}
