package com.yutong.safety.core.handler.fireInspection;

import com.yutong.safety.common.constants.FireInspectionEnum;
import com.yutong.safety.common.param.web.EmployeeMessageListParam;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetyFieldManageDO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionWorkStepDO;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskObjectQueryParams;
import com.yutong.safety.core.dto.fireInspection.FireInspectionMethodDto;
import com.yutong.safety.core.dto.fireInspection.FireInspectionRiskLevelDto;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.SafetyFieldManageService;
import com.yutong.safety.core.service.fireInspection.SafetyFireInspectionWorkStepService;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SafetyFireInspectionSelectHandler {

    @Resource
    private SafetyFireInspectionWorkStepService safetyFireInspectionWorkStepService;
    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Resource
    private SafetyFieldManageService safetyFieldManageService;

    /**
     * 风险等级
     *
     * @return 返回集合
     */
    public List<FireInspectionRiskLevelDto> getRiskLevel() {
        return FireInspectionEnum.generateRiskLevelList().stream()
                .map(BigDecimalEnum -> {
                    FireInspectionRiskLevelDto dto = new FireInspectionRiskLevelDto();
                    BeanUtils.copyProperties(BigDecimalEnum, dto);
                    return dto;
                }).collect(Collectors.toList());
    }

    /**
     * 检查方法
     *
     * @return 返回集合
     */
    public List<FireInspectionMethodDto> getInspectionMethod() {
        List<FireInspectionMethodDto> list = new ArrayList<>();
        List<SafetyFieldManageDO> doList = safetyFieldManageService.getInfoByFieldType("validation_jcff_0_jcff_0");
        if (CollectionUtils.isEmpty(doList)) {
            return list;
        }
        // 因为开发前期用的Code和Name，前端已经做了匹配，后期放在了数据字典中，为了避免前端修改，所以此处在做了处理
        doList.forEach(li -> {
            FireInspectionMethodDto dto = new FireInspectionMethodDto();
            dto.setCode(li.getId());
            dto.setName(li.getFieldName());
            list.add(dto);
        });
        return list;
    }

    /**
     * 作业环节
     *
     * @return 返回作业环节名称和编码的集合
     */
    public List<SafetyFireInspectionWorkStepDO> getWorkStep() {
        List<SafetyFireInspectionWorkStepDO> workStepList = safetyFireInspectionWorkStepService.selectByExample(null);
        if (CollectionUtils.isEmpty(workStepList)) {
            return Lists.newArrayList();
        }
        return workStepList;
    }

    /**
     * 默认的任务对象
     *
     * @param params 作业环节编码和风险等级
     */
    public List<SafetyEmployeeMessageDO> getDefaultTaskObject(SafetyFireInspectionTaskObjectQueryParams params) {
        String workDeptCode = params.getWorkDeptCode();
        String riskLevel = params.getRiskLevel();
        if (StringUtils.isBlank(workDeptCode) || StringUtils.isBlank(riskLevel)) {
            return Lists.newArrayList();
        }
        if (!FireInspectionEnum.getRiskCodeList().contains(riskLevel)) {
            return Lists.newArrayList();
        }
        String inspectionPosition = FireInspectionEnum.getFireInspectionEnumByCode(riskLevel).getInspectionPosition();
        // 岗位
        List<String> position = Arrays.asList(inspectionPosition.split("/"));

        List<SafetyEmployeeMessageDO> dataList = safetyEmployeeMessageService.selectInfoByDeptPosition(Collections.singletonList(workDeptCode));
        if (CollectionUtils.isEmpty(dataList)) return Lists.newArrayList();

        return dataList.stream().filter(li -> {
            String positionName = li.getPositionName();
            return position.stream().anyMatch(positionName::contains);
        }).collect(Collectors.toList());
    }

    /**
     * 自定义的任务对象
     *
     * @param params 员工名称或者工号
     */
    public List<SafetyEmployeeMessageDO> getTaskObject(SafetyFireInspectionTaskObjectQueryParams params) {
        if (StringUtils.isBlank(params.getEmpNoAndName())) {
            return Lists.newArrayList();
        }
        EmployeeMessageListParam param = new EmployeeMessageListParam();
        param.setEmpNoAndName(params.getEmpNoAndName());
        return safetyEmployeeMessageService.getUserListByCondition(param);
    }
}
