package com.yutong.safety.core.handler;

import com.yutong.safety.common.param.web.CarOutRuleAddParam;
import com.yutong.safety.common.param.web.CarOutRuleListParam;
import com.yutong.safety.common.param.web.CarRuleWhiteListParam;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.SafetyCarOutRuleVO;
import com.yutong.safety.core.db.entity.SafetyCarRuleWhiteDO;
import com.yutong.safety.core.service.SafetyCarOutRuleHistoryService;
import com.yutong.safety.core.service.SafetyCarOutRuleService;
import com.yutong.safety.core.service.SafetyCarRuleWhiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author lkp
 * @since 2024-04-21
 */
@Component
public class SafetyCarRuleWhiteHandler {

    @Autowired
    private SafetyCarRuleWhiteService safetyCarRuleWhiteService;
    /**
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    public Object pageList(CarRuleWhiteListParam param) {
        List<SafetyCarRuleWhiteDO> list = safetyCarRuleWhiteService.pageList(param);
        long total = safetyCarRuleWhiteService.pageListCount(param);
        return new PageVO<SafetyCarRuleWhiteDO>(total, list);
    }

    /**
     * 数据添加接口
     * @param param
     * @return
     */
    public Object update(CarRuleWhiteListParam param){
        return safetyCarRuleWhiteService.saveOrUpdate(param);
    }

    /**
     * 删除
     * @param id
     * @return
     * @throws Exception
     */
    public Object deleteById(int id){
        return safetyCarRuleWhiteService.deleteById(id);
    }

    public Object importData(CommonImportParam param) {
        return safetyCarRuleWhiteService.importData(param);
    }
}
