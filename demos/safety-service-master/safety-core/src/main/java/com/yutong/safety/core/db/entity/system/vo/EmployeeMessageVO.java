package com.yutong.safety.core.db.entity.system.vo;

import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import lombok.Data;

@Data
public class EmployeeMessageVO extends SafetyEmployeeMessageDO {

    //出入口人事ID-用于验证出入口人员情况
    private String crkId;
    //一卡通卡ID-用于发卡
    private String tokenId;
    //卡下发状态 0-未授权 1-已授权 2-授权失败
    private String cardStatus;
}
