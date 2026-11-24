package com.yutong.safety.core.db.entity.build.vo;

import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDO;
import lombok.Data;

@Data
public class BuildEmployeePowerVO extends SafetyBuildEmployeePowerDO {

        private String anFactoryCode;

        private String anFactoryName;

        private String anBuildingCode;

        private String anBuilding;

        private String deptName;

        private String deptCode;

        private String isTopSecurity;

        //厂区权限主键ID-用于跟厂区权限关联查询时使用
        private Integer parkId;
}
