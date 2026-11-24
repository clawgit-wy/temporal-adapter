package com.yutong.safety.core.handler.common;

import com.alibaba.excel.util.StringUtils;
import com.yutong.safety.common.vo.competitor.CompetitorMangeVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDO;
import com.yutong.safety.core.db.entity.build.vo.BuildEmployeePowerVO;
import com.yutong.safety.core.dto.SafetyClosePublicBuildDto;
import com.yutong.safety.core.dto.SafetyClosePublicBuildParams;
import com.yutong.safety.core.dto.SafetyKeyValueDto;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class SafetyBaseCommonHandler {


    protected void handlePublicBuildingCase(String empNo, List<String> buildingCodes, List<String> closeDeviceCode,
                                            Map<String, SafetyClosePublicBuildDto> publicBuildingMap,
                                            Map<String, SafetyClosePublicBuildDto> noPublicBuildingMap,
                                            Set<String> deleteIds,
                                            List<SafetyKeyValueDto> result) {

        buildingCodes.stream().map(code -> empNo + code).forEach(key -> {
            // 非公共楼宇不存在并且公共楼宇存在
            if (!noPublicBuildingMap.containsKey(key) && publicBuildingMap.containsKey(key)) {
                addIdsToDelete(publicBuildingMap.get(key), deleteIds, result);
            }
            // 排除的楼宇ID不为空并且公共楼宇存在
            if (!CollectionUtils.isEmpty(closeDeviceCode) && publicBuildingMap.containsKey(key) && noPublicBuildingMap.containsKey(key)) {
                // 获取非公共楼宇的信息
                SafetyClosePublicBuildDto noPublicDto = noPublicBuildingMap.get(key);
                // 排除掉非公共楼宇
                List<String> remainingDevices = getRemainingDevices(noPublicDto.getDeviceCode(), closeDeviceCode);
                // 结果为空的话，说明剩余的非公共楼宇是本次要关闭的楼宇，符合要求，可以对公共区域进行删除
                if (CollectionUtils.isEmpty(remainingDevices)) {
                    addIdsToDelete(publicBuildingMap.get(key), deleteIds, result);
                }
            }
        });
    }

    protected void handleNonPublicBuildingCase(String empNo, List<String> closeDeviceCode,
                                               List<SafetyClosePublicBuildDto> safetyClosePublicBuildDtos,
                                               Map<String, SafetyClosePublicBuildDto> publicBuildingMap,
                                               Map<String, SafetyClosePublicBuildDto> noPublicBuildingMap,
                                               Set<String> deleteIds,
                                               List<SafetyKeyValueDto> result) {
        // 当前员工的公共楼宇权限
        List<SafetyClosePublicBuildDto> publicBuildings = safetyClosePublicBuildDtos.stream()
                .filter(dto -> dto.getEmpNo().equals(empNo) && "1".equals(dto.getIsPublic()))
                .collect(Collectors.toList());
        // 为空表示没有此员工没有公共楼宇权限，直接结束
        if (CollectionUtils.isEmpty(publicBuildings)) {
            return;
        }
        // 非公共楼宇权限的楼栋
        List<String> noPublicBuildingCodes = noPublicBuildingMap.keySet().stream()
                .filter(k -> k.startsWith(empNo))
                .map(k -> k.substring(empNo.length()))
                .collect(Collectors.toList());

        // 根据楼栋，判断员工存在此楼栋的公共权限时，是否存在非公共权限， 如果不存在非公共权限，就要关闭此员工的公共权限
        publicBuildings.forEach(dto -> {
            String key = dto.getEmpNo() + dto.getBuildingCode();
            // 不包含的数据，直接删除公共权限
            if (!noPublicBuildingCodes.contains(dto.getBuildingCode())) {
                addIdsToDelete(publicBuildingMap.get(key), deleteIds, result);
            } else {
                // 包含的数据，需要排除掉本次需要关闭的楼宇
                SafetyClosePublicBuildDto noPublicDto = noPublicBuildingMap.get(key);
                List<String> remainingDevices = getRemainingDevices(noPublicDto.getDeviceCode(), closeDeviceCode);
                if (CollectionUtils.isEmpty(remainingDevices)) {
                    addIdsToDelete(publicBuildingMap.get(key), deleteIds, result);
                }
            }
        });
    }

    /**
     * 结果集， 生成结果，结果包含两部分，一部分是发放内执行的结果，一部分是方法外执行的结果
     *
     * @param dto       生成结果的源对象，公共楼宇的信息
     * @param deleteIds 方法内的结果集
     * @param result    方法外的结果集
     */
    private void addIdsToDelete(SafetyClosePublicBuildDto dto, Set<String> deleteIds, List<SafetyKeyValueDto> result) {
        String[] ids = dto.getIds().split(",");
        deleteIds.addAll(Arrays.asList(ids));
        // 方法外的结果集
        SafetyKeyValueDto info = new SafetyKeyValueDto();
        info.setKey(dto.getEmpNo());
        info.setValue(dto.getDeviceCode());
        info.setValueTwo(dto.getDeviceName());
        result.add(info);
    }

    private List<String> getRemainingDevices(String deviceCodeString, List<String> closeDeviceCode) {
        return Arrays.stream(deviceCodeString.split(","))
                .filter(li -> !closeDeviceCode.contains(li))
                .collect(Collectors.toList());
    }



    protected void filterDeviceCodes(Map<String, List<BuildEmployeePowerVO>> empPowerMap, List<SafetyKeyValueDto> result) {
        empPowerMap.forEach((key, powerVos) -> {
            SafetyKeyValueDto dto = result.stream().filter(item -> item.getKey().equals(key)).findFirst().orElse(null);
            if (dto != null) {
                Set<String> deviceCodes = powerVos.stream().map(SafetyBuildEmployeePowerDO::getDeviceCode).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
                Set<String> deviceNames = powerVos.stream().map(SafetyBuildEmployeePowerDO::getDeviceName).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
                dto.setValue(filterDeviceString(dto.getValue(), deviceCodes));
                dto.setValueTwo(filterDeviceString(dto.getValueTwo(), deviceNames));
                if (StringUtils.isBlank(dto.getValue())) {
                    result.remove(dto);
                }
            }
        });
    }

    private String filterDeviceString(String source, Set<String> excludeItems) {
        return Arrays.stream(source.split(","))
                .filter(item -> !excludeItems.contains(item))
                .collect(Collectors.joining(","));
    }

    /**
     * 创建SafetyClosePublicBuildParams对象
     */
    public SafetyClosePublicBuildParams createClosePublicBuildParams(String empNo, List<String> deviceCodeList) {
        SafetyClosePublicBuildParams info = new SafetyClosePublicBuildParams();
        info.setEmpNo(empNo);
        info.setCloseDeviceCode(deviceCodeList);
        info.setType(true);
        return info;
    }
    // 辅助方法：提取员工编号列表
    protected List<String> extractEmpNos(List<CompetitorMangeVO.AccInfo> accInfos) {
        return accInfos.stream()
                .map(CompetitorMangeVO.AccInfo::getOwnerEmployeeId)
                .collect(Collectors.toList());
    }
    // 辅助方法：将员工消息列表转换为员工编号集合
    protected Set<String> convertToEmpNoSet(List<SafetyEmployeeMessageDO> messageDOList) {
        return messageDOList.stream()
                .map(SafetyEmployeeMessageDO::getEmpNo)
                .collect(Collectors.toSet());
    }

    // 辅助方法：构建结果映射
    protected Map<String, String> buildResultMap(List<CompetitorMangeVO.AccInfo> accInfos, Set<String> empNoSet) {
        Map<String, String> resultMap = new HashMap<>();

        for (CompetitorMangeVO.AccInfo accInfo : accInfos) {
            if (isValidAccInfo(accInfo)) {
                String key = accInfo.getUsci();
                String value = constructValue(accInfo, empNoSet);
                resultMap.put(key, value);
            }
        }

        return resultMap;
    }


    // 辅助方法：检查账号信息有效性
    private boolean isValidAccInfo(CompetitorMangeVO.AccInfo accInfo) {
        return !StringUtils.isEmpty(accInfo.getUsci())
                && !StringUtils.isEmpty(accInfo.getIsNoAccess())
                && Objects.equals(accInfo.getIsNoAccess(), "T");
    }

    // 辅助方法：构造结果值
    private String constructValue(CompetitorMangeVO.AccInfo accInfo, Set<String> empNoSet) {
        String ownerEmployeeId = accInfo.getOwnerEmployeeId();
        String ownerName = accInfo.getOwnerName();

        if (empNoSet.contains(ownerEmployeeId)) {
            return String.format("%s#%s#%s",
                    accInfo.getIsNoAccess(),
                    ownerEmployeeId,
                    ownerName);
        } else {
            return accInfo.getIsNoAccess();
        }
    }
}
