package com.yutong.safety.core.handler;

import com.yutong.safety.common.param.app.PersonStateAddParam;
import com.yutong.safety.common.param.web.SafetyStaffEvectionParam;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.SafetyCarOutRuleVO;
import com.yutong.safety.core.db.entity.SafetyStaffEvectionDO;
import com.yutong.safety.core.service.SafetyStaffEvectionService;
import com.yutong.sugar.api.IRpcLdapService;
import com.yutong.sugar.api.vo.LdapResult;
import com.yutong.sugar.api.vo.LdapUser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lukunpeng
 * @time 2024-4-12
 */
@Component
public class SafetyStaffEvectionHandler {

    @Autowired
    SafetyStaffEvectionService safetyStaffEvectionService;

    @Autowired
    private IRpcLdapService rpcLdapService;

    public Object importData(MultipartFile file) {
        return safetyStaffEvectionService.importData(file);
    }

    public Object queryList(SafetyStaffEvectionParam param) {
        List<SafetyStaffEvectionDO> list =  safetyStaffEvectionService.queryList(param);
        long total = safetyStaffEvectionService.pageListCount(param);
        return new PageVO<SafetyStaffEvectionDO>(total, list);
    }

    public Object add(SafetyStaffEvectionParam param) {
        return safetyStaffEvectionService.add(param);
    }

    public Object update(SafetyStaffEvectionParam param) {
        return safetyStaffEvectionService.update(param);
    }

    public Object addList(List<PersonStateAddParam> list) {
        if(CollectionUtils.isEmpty(list)) {
            return null;
        }
        //去除不是公出状态的数据
        list.removeIf(o -> !o.getState().equals(2));
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<String> users = list.stream().map(PersonStateAddParam::getUsername).collect(Collectors.toList());
        LdapResult<Map<String, LdapUser>> ldapResult = rpcLdapService.findUserinfoByUids(users);
        Map<String, LdapUser> userList = ldapResult.getData();
        //在根据员工号查询出来，表中的数据，最新一条数据
        List<SafetyStaffEvectionDO> evectionList = safetyStaffEvectionService.queryListByEmpNos(users);
        //转map
        Map<String, SafetyStaffEvectionDO> evectionMap = evectionList.stream().collect(Collectors.toMap(SafetyStaffEvectionDO::getStaffNo, o->o));
        List<SafetyStaffEvectionDO> staffList = Lists.newArrayList();
        for (PersonStateAddParam stateAddParam : list) {
            if (evectionMap.containsKey(stateAddParam.getUsername())) {
                if (stateAddParam.getStartDate().equals(evectionMap.get(stateAddParam.getUsername()).getBeginDate())
                && stateAddParam.getEndDate().equals(evectionMap.get(stateAddParam.getUsername()).getEndDate())) {
                    continue;
                }
            }
            SafetyStaffEvectionDO safetyStaffEvectionDO = new SafetyStaffEvectionDO();
            safetyStaffEvectionDO.setStaffNo(stateAddParam.getUsername());
            safetyStaffEvectionDO.setStaffName(userList.containsKey(stateAddParam.getUsername()) ? userList.get(stateAddParam.getUsername()).getName() : "");
            safetyStaffEvectionDO.setBeginDate(stateAddParam.getStartDate());
            safetyStaffEvectionDO.setEndDate(stateAddParam.getEndDate());
            safetyStaffEvectionDO.setRemark(stateAddParam.getReplyContent());
            staffList.add(safetyStaffEvectionDO);
        }
        if (CollectionUtils.isNotEmpty(staffList)) {
            safetyStaffEvectionService.batchInsert(staffList);
        }
        return null;
    }
}
