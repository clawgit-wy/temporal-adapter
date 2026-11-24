package com.yutong.safety.core.handler.build;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.build.*;
import com.yutong.safety.core.service.build.SafetyBuildConfigService;
import com.yutong.safety.core.service.build.SafetyBuildEmployeePowerService;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class SafetyBuildConfigHandler {

    @Resource
    private SafetyBuildConfigService safetyBuildConfigService;

    public SafetyBuildConfigDO query() {
        List<SafetyBuildConfigDO> list = safetyBuildConfigService.query();

        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    public void saveOrUpdate(SafetyBuildConfigDO param, String token) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        String workNo = tokenUser.get("workNo");
        if(com.yutong.gw.utils.StringUtils.isEmpty(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }

        param.setOperateTime(DateUtils.format(new Date(),2));
        param.setOperateUser(tokenUser.get("userName"));
        param.setIsDelete("0");
        LogUtils.info("{}对门禁权限配置进行变动，变动内容：{}",param.getOperateUser(),param.toString());

        safetyBuildConfigService.saveOrUpdate(param);
    }
}
