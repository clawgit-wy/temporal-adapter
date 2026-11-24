package com.yutong.safety.core.db.entity.park.vo;

import com.alibaba.excel.util.StringUtils;
import com.yutong.safety.common.constants.UserTypeEnum;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.core.db.entity.park.ParkEmployeeClassifiedDO;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 前端需要的数据实体类（无涉密业务关联员工）
 */
@Data
public class ParkEmployeeClassifiedVo extends ParkEmployeeClassifiedDO implements Serializable {

    /**
     * 有效期 (拼接)
     */
    private String periodValidity;

    /**
     * 员工车辆(和锁定车辆拼接)
     */
    private String allCarNo;
    /**
     * 员工类型（中文说明）
     */
    private String userTypeCh;

    public void init(){
        this.periodValidity = initPeriodValidity();
        this.allCarNo = initAllCarNo();
        this.userTypeCh = initUserTypeCh();
    }

    private String initPeriodValidity() {

        if (StringUtils.isNotBlank(getBeginDate()) && StringUtils.isNotBlank(getEndDate())) {
            return DateUtils.format(DateUtils.parse(getBeginDate(), 1), 1) + " - " + DateUtils.format(DateUtils.parse(getEndDate(), 1), 1);
        }
        return periodValidity;
    }

    private String initAllCarNo() {
        String carNo = getCarNo();
        String lockCarNo = getLockCarNo();
        if (StringUtils.isBlank(carNo) && StringUtils.isBlank(lockCarNo)) {
            return allCarNo;
        } else if (StringUtils.isBlank(carNo)) {
            return lockCarNo + "（锁定）";
        } else if (StringUtils.isBlank(lockCarNo)) {
            return carNo;
        }
        return carNo + "," + lockCarNo + "（锁定）";
    }

    private String initUserTypeCh() {
        return UserTypeEnum.valueOf(getUserType()).getName();
    }
}
