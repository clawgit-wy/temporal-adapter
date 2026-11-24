package com.yutong.safety.core.handler;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.PersonCarOutRuleAddParam;
import com.yutong.safety.common.param.web.PersonCarOutRuleDeleteParam;
import com.yutong.safety.common.param.web.PersonCarOutRuleListParam;
import com.yutong.safety.common.param.web.PersonCarOutRuleUpdateParam;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyPersonCarOutRuleDO;
import com.yutong.safety.core.dto.SafetyPersonCarOutRuleDTO;
import com.yutong.safety.core.service.SafetyPersonCarOutRuleService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2024-10-15 10:33
 */
@Component
public class SafetyPersonCarOutRuleHandler {
    @Autowired
    private SafetyPersonCarOutRuleService safetyPersonCarOutRuleService;

    /**
     * 新增数据
     * @param param
     */
    public void add(PersonCarOutRuleAddParam param) {
        //第一步检查是否已经存在此数据
        SafetyPersonCarOutRuleDO safetyPersonCarOutRuleDO = safetyPersonCarOutRuleService.getInfo(null, param.getEmployeeId(), null);
        if (safetyPersonCarOutRuleDO != null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"改人员数据已存在！");
        }
        safetyPersonCarOutRuleDO = new SafetyPersonCarOutRuleDO();
        safetyPersonCarOutRuleDO.setCarNo(param.getCarNo());
        safetyPersonCarOutRuleDO.setEmployeeId(param.getEmployeeId());
        safetyPersonCarOutRuleDO.setCheckFlag(param.getCheckFlag());
        safetyPersonCarOutRuleDO.setRemark(param.getRemark());
        safetyPersonCarOutRuleDO.setCreateTime(new Date());
        safetyPersonCarOutRuleDO.setUpdateTime(new Date());
        safetyPersonCarOutRuleDO.setIsDelete(false);
        safetyPersonCarOutRuleService.add(safetyPersonCarOutRuleDO);
    }

    /**
     * 数据更新
     * @param param
     */
    public void update(PersonCarOutRuleUpdateParam param) {
        SafetyPersonCarOutRuleDO safetyPersonCarOutRuleDO = safetyPersonCarOutRuleService.getInfo(param.getId(), null, null);
        if (safetyPersonCarOutRuleDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该数据不存在！");
        }
        safetyPersonCarOutRuleDO.setUpdateTime(new Date());
        if (Objects.nonNull(param.getEmployeeId())) {
            safetyPersonCarOutRuleDO.setEmployeeId(param.getEmployeeId());
        }
        if (StringUtils.isNotEmpty(param.getCarNo())) {
            safetyPersonCarOutRuleDO.setCarNo(param.getCarNo());
        }
        if (StringUtils.isNotEmpty(param.getCheckFlag())) {
            safetyPersonCarOutRuleDO.setCheckFlag(param.getCheckFlag());
        }
        if (StringUtils.isNotEmpty(param.getRemark())) {
            safetyPersonCarOutRuleDO.setRemark(param.getRemark());
        }
        safetyPersonCarOutRuleService.update(safetyPersonCarOutRuleDO);
    }

    /**
     * 数据删除动作
     * @param param
     */
    public void delete(PersonCarOutRuleDeleteParam param) {
        SafetyPersonCarOutRuleDO safetyPersonCarOutRuleDO = safetyPersonCarOutRuleService.getInfo(param.getId(), null, null);
        if (safetyPersonCarOutRuleDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该数据不存在！");
        }
        safetyPersonCarOutRuleDO.setIsDelete(true);
        safetyPersonCarOutRuleDO.setUpdateTime(new Date());
        safetyPersonCarOutRuleService.update(safetyPersonCarOutRuleDO);
    }

    /**
     * 分页查询接口
     * @param param
     * @return
     */
    public Object pageList(PersonCarOutRuleListParam param) {
        List<SafetyPersonCarOutRuleDTO> list = safetyPersonCarOutRuleService.pageList(param);
        if (CollectionUtils.isEmpty(list)) {
            return new PageVO<SafetyPersonCarOutRuleDTO>(0, Lists.newArrayList());
        }
        long total = safetyPersonCarOutRuleService.pageListCount(param);
        return new PageVO<SafetyPersonCarOutRuleDTO>(total, list);
    }
}
