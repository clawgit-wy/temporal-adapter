package com.yutong.modules.vehicle.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 日航品牌库
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="日韩品牌库对象", description="日韩品牌库")
public class YtsafetyBrandVehicleDTO {
    
	/**主键ID*/
    @ApiModelProperty(value = "主键ID")
	private Integer id;
	/**品牌名称*/
    @ApiModelProperty(value = "品牌名称")
	private String brandName;
	/**品牌产地*/
    @ApiModelProperty(value = "品牌产地")
	private String brandPlace;
}
