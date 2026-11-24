package com.yutong.modules.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.desensitization.annotation.DataPermissionField;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 组织架构映射表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="YTSafetySysOrgFactoryVO对象", description="组织架构映射表VO")
public class YTSafetySysOrgFactoryVO {
    
	/**id*/
    @ApiModelProperty(value = "id")
	private String id;
	/**组织编码*/
	@Excel(name = "组织编码", width = 15)
    @ApiModelProperty(value = "组织编码")
	private String orgCode;
	/**组织名称*/
    @DataPermissionField(deCode = "org_name", deName = "组织架构")
	@Excel(name = "组织名称", width = 15)
    @ApiModelProperty(value = "组织名称")
	private String orgName;

    @DataPermissionField(deCode = "org_name", deName = "组织架构")
    @Excel(name = "组织名称路径", width = 15)
    @ApiModelProperty(value = "组织名称路径")
    private String orgNamePath;
    /**厂区id*/
    @ApiModelProperty(value = "厂区id")
    private String factoryId;
	/**厂区编码*/
	@Excel(name = "厂区编码", width = 15)
    @ApiModelProperty(value = "厂区编码")
	private String factoryCode;
	/**厂区名称*/
    @DataPermissionField(deCode = "factory_name", deName = "厂区")
	@Excel(name = "厂区名称", width = 15)
    @ApiModelProperty(value = "厂区名称")
	private String factoryName;
}
