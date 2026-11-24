package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2024-4-29 15:12
 */
@Data
@ApiModel("")
public class SpotCheckBatchUpdateParam implements Check {
    /**
     * 选择数据的id,以英文逗号分割
     */
    @ApiModelProperty("选择数据的id,以英文逗号分割")
    private String ids;
    /**
     * 临时点检人名称
     */
    @ApiModelProperty("临时点检人名称")
    private String proTemCheckEmp;
    /**
     * 临时点检人的员工号
     */
    @ApiModelProperty("临时点检人的员工号")
    private String proTemCheckEmpNo;
    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    private String proTemBeginDate;
    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    private String proTemEndDate;
    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(ids), ResponseEnum.PARAM_ERROR.getCode(), "ids不能为空！");
    }

    /**
     * 点检人名称
     */
    @ApiModelProperty("点检人名称")
    private String checkEmp;
    /**
     * 点检人的员工号
     */
    @ApiModelProperty("点检人的员工号")
    private String checkEmpNo;
}
