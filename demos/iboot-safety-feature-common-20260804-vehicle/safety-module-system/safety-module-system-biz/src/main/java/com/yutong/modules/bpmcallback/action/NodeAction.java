package com.yutong.modules.bpmcallback.action;

import cn.hutool.core.collection.CollUtil;
import com.yutong.modules.common.dto.BpmMethodProcessDTO;
import com.yutong.modules.risk.mapper.YtsafetyRiskDataProcessMapper;
import com.yutong.modules.user.api.ISafetyGroupInfoService;
import com.yutong.modules.user.entity.SafetyGroupInfo;
import com.yutong.modules.user.entity.YtsafetySysResponsible;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * bpm执行完成
 */
@Slf4j
public abstract class NodeAction {

    @Autowired
    protected YtsafetyRiskDataProcessMapper processMapper;

    @Autowired
    protected ISafetyGroupInfoService safetyGroupInfoService;

    /**
     * 执行回调
     * @param event
     */
    public abstract void execute(BpmMethodProcessDTO event);

    /**
     * 按部门层级向上查找安全责任人，本部门找不到则向上级部门查找，依此类推
     */
    protected List<YtsafetySysResponsible> findResponsibleByDepartHierarchy(String departCode, List<String> roleCodes) {
        String currentCode = departCode;
        while (StringUtils.isNotBlank(currentCode)) {
            List<YtsafetySysResponsible> responsibleList = processMapper.queryResponsibleByDepartAndRoleCodes(currentCode, roleCodes);
            if (CollUtil.isNotEmpty(responsibleList)) {
                log.info("在部门 {} 找到安全责任人", currentCode);
                return responsibleList;
            }
            SafetyGroupInfo dept = safetyGroupInfoService.lambdaQuery()
                    .eq(SafetyGroupInfo::getCode, currentCode)
                    .one();
            if (dept == null || StringUtils.isBlank(dept.getParentCode())) {
                log.warn("部门 {} 无上级部门，停止查找", currentCode);
                break;
            }
            log.info("部门 {} 未找到安全责任人，向上级部门 {} 查找", currentCode, dept.getParentCode());
            currentCode = dept.getParentCode();
        }
        return null;
    }

    /**
     * 按部门层级向上查找安全责任人（同时限定厂区），本部门找不到则向上级部门查找，依此类推
     */
    protected List<YtsafetySysResponsible> findResponsibleByFactoryAndDepartHierarchy(Long factoryId, String departCode, List<String> roleCodes) {
        String currentCode = departCode;
        while (StringUtils.isNotBlank(currentCode)) {
            List<YtsafetySysResponsible> responsibleList = processMapper.queryResponsibleByFactoryAndDepartAndRoleCodes(factoryId, currentCode, roleCodes);
            if (CollUtil.isNotEmpty(responsibleList)) {
                log.info("在部门 {}、厂区 {} 找到安全责任人", currentCode, factoryId);
                return responsibleList;
            }
            SafetyGroupInfo dept = safetyGroupInfoService.lambdaQuery()
                    .eq(SafetyGroupInfo::getCode, currentCode)
                    .one();
            if (dept == null || StringUtils.isBlank(dept.getParentCode())) {
                log.warn("部门 {} 无上级部门，停止查找", currentCode);
                break;
            }
            log.info("部门 {} 未找到安全责任人，向上级部门 {} 查找", currentCode, dept.getParentCode());
            currentCode = dept.getParentCode();
        }
        return null;
    }
}
