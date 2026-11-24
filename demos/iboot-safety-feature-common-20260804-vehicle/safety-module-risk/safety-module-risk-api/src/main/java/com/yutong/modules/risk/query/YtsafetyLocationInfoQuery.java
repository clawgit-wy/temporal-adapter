package com.yutong.modules.risk.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.domain.BaseQuery;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

@Data
public class YtsafetyLocationInfoQuery extends BaseQuery {

    @ApiModelProperty(value = "id集合")
    private List<Long> idList;

    @ApiModelProperty(value = "所属厂区id")
    private String factoryId;

    @ApiModelProperty(value = "所属区域")
    private Long areaId;

    @ApiModelProperty(value = "所属组织id")
    private Integer orgId;

    @ApiModelProperty(value = "位置名称")
    private String locationName;

    @ApiModelProperty(value = "位置编码")
    private String locationCode;

    @ApiModelProperty(value = "安全员userId，逗号分割")
    private String safetyStaff;

    @ApiModelProperty(value = "风险等级")
    private String riskLevelId;

    @ApiModelProperty(value = "状态 0启用 停用")
    private Integer status;

    private String safetyOfficer;


}
