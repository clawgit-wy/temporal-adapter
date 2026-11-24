package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.utils.PreconditonsEx;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

/**
 * @author gwxiexj
 * @since 2024-04-25 19:21
 */
@Data
public class SpotCheckRuleSaveParam implements Check  {

    @ApiModelProperty("主键ID")
    private Integer id;
    /**
     * 项目id
     */
    @ApiModelProperty("项目ID")
    private Integer teamId;
    /**
     * 项目名称
     */
    @ApiModelProperty("项目名称")
    private String teamName;
    /**
     * 标准类目
     */
    @ApiModelProperty("标准类目")
    private String ruleName;
    /**
     * 点检标准描述
     */
    @ApiModelProperty("点检标准描述")
    private String ruleInfo;
    /**
     * 正常图片上传-是否展示
     */
    @ApiModelProperty("正常图片上传-是否展示")
    private Integer normalImgShow = 1;
    /**
     * 正常图片上传-是否必填
     */
    @ApiModelProperty("正常图片上传-是否必填")
    private Boolean normalImgRequire;
    /**
     * 异常上传图片-是否显示
     */
    @ApiModelProperty("异常上传图片-是否显示")
    private Integer exceptionImgShow = 1;
    /**
     * 异常上传图片-是否必填
     */
    @ApiModelProperty("异常上传图片-是否必填")
    private Boolean exceptionImgRequire;
    /**
     * 整改上传图片-是否展示
     */
    @ApiModelProperty("整改上传图片-是否展示")
    private Integer reformImgShow = 1;
    /**
     * 整改上传图片-是否必填 0-否 1-是
     */
    @ApiModelProperty("整改上传图片-是否必填 0-否 1-是")
    private Boolean reformImgRequire;
    /**
     * 验证上传图片-是否显示 0-否 1-是
     */
    @ApiModelProperty("验证上传图片-是否显示 0-否 1-是")
    private Integer validateImgShow = 1;
    /**
     * 验证上传图片-是否必填 0-否 1-是
     */
    @ApiModelProperty("验证上传图片-是否必填 0-否 1-是")
    private Boolean validateImgRequire;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("排序号")
    private Integer ruleSort;

    @Override
    public void check() {
        PreconditonsEx.checkArgument(Objects.nonNull(teamName), ResponseEnum.PARAM_ERROR.getCode(), "设备设施点检标准“项目”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(ruleName), ResponseEnum.PARAM_ERROR.getCode(), "“巡检标准类目”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(ruleInfo), ResponseEnum.PARAM_ERROR.getCode(), "“巡检标准”不能为空！");
        PreconditonsEx.checkArgument(Objects.nonNull(ruleSort), ResponseEnum.PARAM_ERROR.getCode(), "“排序号”不能为空！");
    }
}
