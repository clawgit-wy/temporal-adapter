package com.yutong.modules.bpmcallback.action;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yutong.modules.common.constant.ProcessConstant;
import com.yutong.modules.common.dto.BpmMethodProcessDTO;
import com.yutong.modules.common.dto.BpmTemplateFormDTO;
import com.yutong.modules.user.api.IYtsafetyResponsibleProcessDetailsService;
import com.yutong.modules.user.api.IYtsafetySysResponsibleService;
import com.yutong.modules.user.dto.YtsafetySysResponsibleDTO;
import com.yutong.modules.user.entity.YtsafetyResponsibleProcess;
import com.yutong.modules.user.entity.YtsafetySysResponsible;
import com.yutong.modules.user.service.IYtsafetyResponsibleProcessService;
import com.yutong.modules.user.vo.YtsafetyResponsibleProcessDetailsVO;
import com.yutong.modules.user.vo.YtsafetySysResponsibleEditVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 责任人信息库变更
 */
@Slf4j
@Component("ytsafety_reponsible_publish")
public class ReponsibleAction extends NodeAction {

    @Autowired
    private IYtsafetyResponsibleProcessService responsibleProcessService;

    @Autowired
    private IYtsafetyResponsibleProcessDetailsService responsibleProcessDetailsService;

    @Autowired
    private IYtsafetySysResponsibleService sysResponsibleService;

    @Override
    public void execute(BpmMethodProcessDTO event) {
        Long processId = Long.valueOf(event.getFormId().getFormInstanceId());
        updateBusiness(processId);
    }

    /**
     * 更新业务
     * @param processId
     */
    private void updateBusiness(Long processId){
        log.info("处理安全责任人变更回调, processId: {}", processId);
        YtsafetyResponsibleProcess process = responsibleProcessService.getById(processId);
        if (process == null) {
            log.warn("安全责任人流程不存在, processId: {}", processId);
            return;
        }
        List<YtsafetyResponsibleProcessDetailsVO> details = responsibleProcessDetailsService.queryList(processId);
        if (details == null || details.isEmpty()) {
            log.warn("安全责任人流程明细为空, processId: {}", processId);
            return;
        }
        for (YtsafetyResponsibleProcessDetailsVO detail : details) {
            if (process.getChangeType() != null && process.getChangeType() == 1) {
                processChangeDetail(detail);
            } else {
                processDetail(detail);
            }
        }

    }

    /**
     * 处理变更单条明细：判定变更人存在，更新安全责任人（同新增逻辑），再删掉变更人的授权数据
     */
    private void processChangeDetail(YtsafetyResponsibleProcessDetailsVO detail) {
        if (StringUtils.isEmpty(detail.getChangeEmpNo())) {
            log.warn("明细变更员工号为空, 跳过");
            return;
        }
        YtsafetySysResponsibleEditVO changeEditVO = sysResponsibleService.queryResponsibleByEmpNo(detail.getChangeEmpNo());
        if (changeEditVO == null) {
            log.warn("变更人不存在, 走新增逻辑, changeEmpNo: {}", detail.getChangeEmpNo());
            processDetail(detail);
            return;
        }
        processDetail(detail);
        String newRoleId = removeIds(changeEditVO.getRoleId(), detail.getRoleId());
        if (StringUtils.isEmpty(newRoleId)) {
            sysResponsibleService.deleteSysResponsible(changeEditVO.getId());
            log.info("变更人角色为空, 已删除安全责任人记录, changeEmpNo: {}", detail.getChangeEmpNo());
        } else {
            YtsafetySysResponsibleDTO changeDTO = new YtsafetySysResponsibleDTO();
            changeDTO.setId(changeEditVO.getId());
            changeDTO.setUserId(changeEditVO.getUserId());
            changeDTO.setEmpNo(detail.getChangeEmpNo());
            changeDTO.setEmpName(detail.getChangeEmpName());
            changeDTO.setUserName(detail.getUserName());
            changeDTO.setPowerFactoryId(removeIds(changeEditVO.getPowerFactoryId(), detail.getFactoryId()));
            changeDTO.setDepartId(removeIds(changeEditVO.getDepartId(), detail.getDepartId()));
            changeDTO.setRoleId(newRoleId);
            changeDTO.setUserType(changeEditVO.getUserType());
            changeDTO.setStatus(changeEditVO.getStatus());
            changeDTO.setValidPeriod(maxDate(changeEditVO.getValidPeriod(), detail.getValidPeriod()));
            sysResponsibleService.editSysResponsible(changeDTO);
            log.info("变更人授权数据已移除, changeEmpNo: {}", detail.getChangeEmpNo());
        }
    }

    /**
     * 处理单条明细：存在则合并后更新，不存在则新增
     */
    private void processDetail(YtsafetyResponsibleProcessDetailsVO detail) {
        if (StringUtils.isEmpty(detail.getEmpNo())) {
            log.warn("明细员工号为空, 跳过");
            return;
        }
        LambdaQueryWrapper<YtsafetySysResponsible> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(YtsafetySysResponsible::getEmpNo, detail.getEmpNo());
        YtsafetySysResponsible existing = sysResponsibleService.getOne(wrapper);
        YtsafetySysResponsibleDTO dto = buildDTO(detail);
        if (existing != null) {
            YtsafetySysResponsibleEditVO editVO = sysResponsibleService.querySysUserByEmpNo(detail.getEmpNo());
            dto.setId(existing.getId());
            dto.setUserId(existing.getUserId());
            if (editVO != null) {
                dto.setPowerFactoryId(mergeIds(editVO.getPowerFactoryId(), detail.getFactoryId()));
                dto.setDepartId(mergeIds(editVO.getDepartId(), detail.getDepartId()));
                dto.setRoleId(mergeIds(editVO.getRoleId(), detail.getRoleId()));
            }
            dto.setValidPeriod(maxDate(existing.getValidPeriod(), detail.getValidPeriod()));
            sysResponsibleService.editSysResponsible(dto);
            log.info("更新安全责任人成功, empNo: {}", detail.getEmpNo());
        } else {
            sysResponsibleService.saveSysResponsible(dto);
            log.info("新增安全责任人成功, empNo: {}", detail.getEmpNo());
        }
    }

    /**
     * 从流程明细构建DTO
     */
    private YtsafetySysResponsibleDTO buildDTO(YtsafetyResponsibleProcessDetailsVO detail) {
        YtsafetySysResponsibleDTO dto = new YtsafetySysResponsibleDTO();
        dto.setEmpNo(detail.getEmpNo());
        dto.setEmpName(detail.getEmpName());
        dto.setUserType(detail.getUserType());
        dto.setStatus(detail.getStatus() != null ? detail.getStatus() : 0);
        dto.setValidPeriod(detail.getValidPeriod());
        dto.setPowerFactoryId(detail.getFactoryId());
        dto.setDepartId(detail.getDepartId());
        dto.setRoleId(detail.getRoleId());
        dto.setUserName(detail.getUserName());
        return dto;
    }

    /**
     * 合并去重两个逗号分隔的ID字符串
     */
    private String mergeIds(String existingIds, String newIds) {
        Set<String> merged = new LinkedHashSet<>();
        if (StringUtils.isNotEmpty(existingIds)) {
            for (String id : existingIds.split(",")) {
                String trimmed = id.trim();
                if (StringUtils.isNotEmpty(trimmed)) {
                    merged.add(trimmed);
                }
            }
        }
        if (StringUtils.isNotEmpty(newIds)) {
            for (String id : newIds.split(",")) {
                String trimmed = id.trim();
                if (StringUtils.isNotEmpty(trimmed)) {
                    merged.add(trimmed);
                }
            }
        }
        return String.join(",", merged);
    }

    /**
     * 从逗号分隔的ID字符串中移除指定ID
     */
    private String removeIds(String existingIds, String idsToRemove) {
        if (StringUtils.isEmpty(existingIds) || StringUtils.isEmpty(idsToRemove)) {
            return existingIds;
        }
        Set<String> removeSet = new LinkedHashSet<>();
        for (String id : idsToRemove.split(",")) {
            String trimmed = id.trim();
            if (StringUtils.isNotEmpty(trimmed)) {
                removeSet.add(trimmed);
            }
        }
        StringBuilder result = new StringBuilder();
        for (String id : existingIds.split(",")) {
            String trimmed = id.trim();
            if (StringUtils.isNotEmpty(trimmed) && !removeSet.contains(trimmed)) {
                if (result.length() > 0) {
                    result.append(",");
                }
                result.append(trimmed);
            }
        }
        return result.toString();
    }

    /**
     * 取两个日期中较晚的一个
     */
    private Date maxDate(Date d1, Date d2) {
        if (d1 == null) return d2;
        if (d2 == null) return d1;
        return d1.after(d2) ? d1 : d2;
    }
}
