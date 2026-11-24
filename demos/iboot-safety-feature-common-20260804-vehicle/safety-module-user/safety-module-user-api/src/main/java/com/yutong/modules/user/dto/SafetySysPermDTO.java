package com.yutong.modules.user.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Date;

/**
 * @Description: 1
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Data
public class SafetySysPermDTO {


    @ApiModelProperty(value = "记录ID")
    private Long id;
	/**权限名称*/
    @ApiModelProperty(value = "权限名称")
	private String permName;
	/**权限访问路径*/
    @ApiModelProperty(value = "权限访问路径")
	private String permUrl;
	/**父ID*/
    @ApiModelProperty(value = "父ID")
	private Long parentId;
	/**目录层级*/
    @ApiModelProperty(value = "目录层级")
	private Integer level;
	/**类型（1目录2页面菜单3按钮）*/
    @ApiModelProperty(value = "类型（1目录2页面菜单3按钮）")
	private Integer cate;
	/**特定编码*/
    @ApiModelProperty(value = "特定编码")
	private String code;
	/**排序*/
    @ApiModelProperty(value = "排序")
	private Integer sort;
	/**是否系统菜单（1普通菜单，2开发使用）*/
    @ApiModelProperty(value = "是否系统菜单（1普通菜单，2开发使用）")
	private String sys;
	/**权限表达式*/
    @ApiModelProperty(value = "权限表达式")
	private String permission;
	/**权限类型（按角色，按权限表达式）*/
    @ApiModelProperty(value = "权限类型（按角色，按权限表达式）")
	private String permCate;
	/**有效状态（0正常1删除）*/

    @ApiModelProperty(value = "有效状态（0正常1删除）")
	private String deleteFalg;
	/**操作人*/
    @ApiModelProperty(value = "操作人")
	private String lastUser;
	/**操作时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "操作时间")
	private Date lastTime;
	/**映射类*/
    @ApiModelProperty(value = "映射类")
	private String mappingClass;

    @ApiModelProperty(value = "是否显示")
    private String isShow;

    @TableField(exist = false)
    private boolean isLeaf;

    @ApiModelProperty("外部链接地址")
    private String externalUrl;


    @ApiModelProperty("组件")
    private String component;

    @ApiModelProperty("组件名字")
    private String componentName;

    @ApiModelProperty("路径")
    private String path;

    @ApiModelProperty("菜单图标")
    private String icon;

    @ApiModelProperty("重定向")
    private String redirect;

    @ApiModelProperty("是否流程显示")
    private String processDisplay;

    @ApiModelProperty("是否缓存路由")
    private String keepAlive;


}
