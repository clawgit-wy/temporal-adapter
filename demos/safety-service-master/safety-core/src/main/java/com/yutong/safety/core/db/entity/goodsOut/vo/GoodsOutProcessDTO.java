package com.yutong.safety.core.db.entity.goodsOut.vo;

import com.yutong.safety.core.db.entity.goodsOut.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GoodsOutProcessDTO extends SafetyGoodsOutProcessDO {

    @ApiModelProperty("车辆清单")
    private List<SafetyGoodsOutDetailVehicleDO> vehicleList;

    @ApiModelProperty("物资清单")
    private List<SafetyGoodsOutDetailGoodsDO> goodsList;

    @ApiModelProperty("接收人清单")
    private List<SafetyGoodsOutDetailReceiveDO> receiveList;

    @ApiModelProperty("物资清单附件-提交时上传")
    private String goodsFileUrl;

    @ApiModelProperty("审核人权限ID")
    private String authId;
}
