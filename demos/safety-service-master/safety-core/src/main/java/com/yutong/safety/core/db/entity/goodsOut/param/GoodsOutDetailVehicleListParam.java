package com.yutong.safety.core.db.entity.goodsOut.param;

import com.yutong.safety.common.param.web.WebPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.jws.WebParam;
import java.util.List;

@Data
public class GoodsOutDetailVehicleListParam extends WebPageParam {

    private String processId;

    private String vehicleNo;

    private String vinNo;

    @ApiModelProperty("出门证流程ID集合-后台使用")
    private List<String> processIdList;

    @ApiModelProperty("分页标识-后台使用")
    private Integer begin;
}
