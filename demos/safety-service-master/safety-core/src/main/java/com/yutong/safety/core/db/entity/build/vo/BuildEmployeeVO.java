package com.yutong.safety.core.db.entity.build.vo;

import com.yutong.safety.common.vo.sys.SysUserVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDO;
import lombok.Data;

import java.util.List;

/**
 * @author gwxiexj
 * 楼宇门禁-员工个性化定制VI
 */
@Data
public class BuildEmployeeVO extends SafetyEmployeeMessageDO {

    /**
     * 本人已授权门禁
     */
    private List<SafetyBuildEmployeePowerDO> buildPowerList;

    private String dutyPersonStr;

    //对应EIP角色
    private String buildSysManagerRole;
    /**
     * 本部门负责人清单
     */
    private List<SafetyEmployeeMessageDO> dutyPersonList;

    /**
     * 楼宇门禁管理员账号清单
     */
    private List<SafetySysUserDO> buildAdminList;
}
