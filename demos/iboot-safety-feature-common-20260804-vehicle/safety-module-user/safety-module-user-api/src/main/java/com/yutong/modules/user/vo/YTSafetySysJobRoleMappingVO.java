package com.yutong.modules.user.vo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.desensitization.annotation.DataPermissionField;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 岗位角色映射表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
@Data
@TableName("ytsafety_sys_job_role_mapping")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ytsafety_sys_job_role_mapping对象", description="岗位角色映射表")
public class YTSafetySysJobRoleMappingVO {
    
	/**id*/
    @ApiModelProperty(value = "id")
	private String id;
	/**岗位编码*/
    @DataPermissionField(deCode = "job_code", deName = "岗位编码")
	@Excel(name = "岗位编码", width = 15)
    @ApiModelProperty(value = "岗位编码")
	private String jobCode;
	/**岗位名称*/
    @DataPermissionField(deCode = "job_name", deName = "岗位名称")
	@Excel(name = "岗位名称", width = 15)
    @ApiModelProperty(value = "岗位名称")
	private String jobName;
	/**角色主键*/
//	@Excel(name = "角色主键", width = 15)
    @ApiModelProperty(value = "角色主键")
	private String roleId;
	/**角色名称*/
    @DataPermissionField(deCode = "role_name", deName = "角色名称")
	@Excel(name = "角色名称", width = 15)
    @ApiModelProperty(value = "角色名称")
	private String roleName;

}
