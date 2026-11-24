package com.yutong.modules.user.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;

@Data
public class SafetySysUserImpoetDTO {


    /**用户名*/
    @Excel(name = "员工名", width = 15)
    private String empName;
    /**员工号*/
    @Excel(name = "员工号*", width = 15)
    private String empNo;

    /**授权厂区厂id，多个以逗号分割*/
    @Excel(name = "授权厂区")
    private String powerFactoryId;

    @Excel(name = "角色*")
    private String roleId;

    @Excel(name = "部门")
    @ApiModelProperty(value = "责任部门id，多个以逗号分割")
    private String departmentId;

    /**
     * 错误信息（Jeecg固定字段，必须加）
     */
    @Excel(name = "异常信息")
    private String errorMsg;
}
