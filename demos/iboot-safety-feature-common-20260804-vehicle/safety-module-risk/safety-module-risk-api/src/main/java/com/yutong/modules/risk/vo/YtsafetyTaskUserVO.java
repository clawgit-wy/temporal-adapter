package com.yutong.modules.risk.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;



@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="任务对象", description="任务对象")
public class YtsafetyTaskUserVO {

	/**用户主键*/
    @ApiModelProperty(value = "用户主键")
	private Long userId;

    /**姓名*/
    @Excel(name = "员工名", width = 15)
    @ApiModelProperty(value = "员工名")
    private String empName;

    /**员工号*/
    @Excel(name = "员工号", width = 15)
    @ApiModelProperty(value = "员工号")
    private String empNo;

    /**授权厂区厂id，多个以逗号分割*/
    @Excel(name = "授权厂区", width = 15)
    @ApiModelProperty(value = "授权厂区厂，多个以逗号分割")
    private String powerFactoryName;

    @Excel(name = "组织code", width = 15)
    @ApiModelProperty(value = "组织code")
    private String orgCode;

    @Excel(name = "厂区ID", width = 15)
    @ApiModelProperty(value = "厂区ID")
    private String factoryId;

}
