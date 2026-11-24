package com.yutong.modules.user.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="部门树对象", description="部门树对象")
public class SafetyGroupInfoTreeVO {

    /**id*/
    @ApiModelProperty(value = "id")
    private Long id;
    /**编码*/
    @ApiModelProperty(value = "编码")
    private String code;
    /**名称*/
    @ApiModelProperty(value = "名称")
    private String name;
    /**上级组织code*/
    @ApiModelProperty(value = "上级组织code")
    private String parentCode;
    /**层级编码*/
    @ApiModelProperty(value = "层级编码")
    private String levelCode;
    /**层级名称*/
    @ApiModelProperty(value = "层级名称")
    private String levelName;
    /**上级组织路径名称*/
    @ApiModelProperty(value = "上级组织路径名称")
    private String orgCodePath;
    /**上级组织路径名称*/
    @ApiModelProperty(value = "上级组织路径名称")
    private String orgNamePath;

    /** 子节点集合*/
    private List<SafetyGroupInfoTreeVO> children;
}
