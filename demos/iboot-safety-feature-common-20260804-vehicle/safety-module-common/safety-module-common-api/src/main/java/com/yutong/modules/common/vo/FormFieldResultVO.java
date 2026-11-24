package com.yutong.modules.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 表单模板反馈
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@ApiModel(value="获取异构系统表单字段记录", description="获取异构系统表单字段记录")
public class FormFieldResultVO {

    @ApiModelProperty(value = "表单字段标识")
    private String fieldId;

    @ApiModelProperty(value = "表单字段名称")
    private String fieldName;

    @ApiModelProperty(value = "表单字段类型,String(推荐使用string类型);Number(只有需要比数字大小的场景才使用此类型);Boolean;Date(不建议使用);Array_String;Array_Number(不建议使用)")
    private String type;

}
