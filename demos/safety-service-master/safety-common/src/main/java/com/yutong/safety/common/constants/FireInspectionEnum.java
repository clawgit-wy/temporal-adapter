package com.yutong.safety.common.constants;

import com.yutong.safety.common.exception.BizException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 消防巡检风险等级判定标准
 */
public enum FireInspectionEnum {

    RED("1","红", "红", "红", "部门", "安全管理员", "1次/每周", "#F44A45"),
    ORANGE("2","橙", "橙级以上", "红,橙", "部门", "安全管理员", "1次/每周", "#FF6D14"),
    YELLOW("3","黄", "黄级以上", "红,橙,黄", "单元", "科长/经理", "1次/每周", " #FAAC13"),
    BLUE("4","蓝", "蓝级以上", "红,橙,黄,蓝", "工段", "段长/主管", "2次/每周", " #197CFF"),
    ;

    @Getter
    @Setter
    private String code;
    /**
     * 风险等级简称
     */
    @Getter
    @Setter
    private String riskLevel;
    /**
     * 风险等级详情
     */
    @Getter
    @Setter
    private String riskLevelDetail;
    /**
     * 检查项
     */
    @Getter
    @Setter
    private String inspectionItems;
    /**
     * 检查级别
     */
    @Getter
    @Setter
    private String inspectionLevel;
    /**
     * 巡检人岗位
     */
    @Getter
    @Setter
    private String inspectionPosition;
    /**
     * 检查周期
     */
    @Getter
    @Setter
    private String inspectionCycle;

    /**
     * 颜色
     */
    @Getter
    @Setter
    private String color;

    FireInspectionEnum(String code, String riskLevel, String riskLevelDetail, String inspectionItems, String inspectionLevel, String inspectionPosition, String inspectionCycle, String color) {
        this.code = code;
        this.riskLevel = riskLevel;
        this.riskLevelDetail = riskLevelDetail;
        this.inspectionItems = inspectionItems;
        this.inspectionLevel = inspectionLevel;
        this.inspectionPosition = inspectionPosition;
        this.inspectionCycle = inspectionCycle;
        this.color = color;
    }

    /**
     * 生成一个包含所有风险等级枚举值的列表。
     *
     * @return 不可变的风险等级列表
     */
    public static List<FireInspectionEnum> generateRiskLevelList() {
        return Arrays.stream(FireInspectionEnum.values()).collect(Collectors.toList());
    }

    /**
     * 生成一个包含所有风险等级枚举值的列表。
     *
     * @return 不可变的风险等级列表
     */
    public static List<String> getRiskLevelList() {
        return Arrays.stream(FireInspectionEnum.values()).map(FireInspectionEnum::getRiskLevel).collect(Collectors.toList());
    }

    public static List<String> getRiskCodeList() {
        return Arrays.stream(FireInspectionEnum.values()).map(FireInspectionEnum::getCode).collect(Collectors.toList());
    }

    public static Set<String> getRiskPositionList() {
        List<String> list = Arrays.stream(FireInspectionEnum.values()).map(FireInspectionEnum::getInspectionPosition).distinct().collect(Collectors.toList());
        Set<String> resultList = new HashSet<>();
        list.forEach(li -> Collections.addAll(resultList, li.split("/")));
        return resultList;
    }

    /**
     * 根据风险等级简称获取对应的枚举实例。
     *
     * @param riskLevel 风险等级简称
     * @return 匹配的FireInspectionEnum实例
     */
    public static FireInspectionEnum getFireInspectionEnumByRiskLevel(String riskLevel) {
        for (FireInspectionEnum fireInspectionEnum : FireInspectionEnum.values()) {
            if (fireInspectionEnum.getRiskLevel().equals(riskLevel)) {
                return fireInspectionEnum;
            }
        }
        throw new BizException(ResponseEnum.FAIL.getCode(), "风险等级获取失败：" + riskLevel);
    }

    /**
     * 根据风险等级编码获取对应的枚举实例。
     *
     * @param code 风险等级编码
     * @return 匹配的FireInspectionEnum实例
     */
    public static FireInspectionEnum getFireInspectionEnumByCode(String code) {
        for (FireInspectionEnum fireInspectionEnum : FireInspectionEnum.values()) {
            if (fireInspectionEnum.getCode().equals(code)) {
                return fireInspectionEnum;
            }
        }
        throw new BizException(ResponseEnum.FAIL.getCode(), "风险等级获取失败：" + code);
    }

    /**
     * 根据风险等级简称获取检查项的字符串列表。
     *
     * @param code 风险等级简称
     * @return 检查项列表，如果未找到或riskLevel为空，则返回空列表
     */
    public static List<String> getRiskLevelByItems(String code) {
        List<String> list = Lists.newArrayList();
        if (StringUtils.isBlank(code)) {
            return list;
        }
        for (FireInspectionEnum fireInspectionEnum : FireInspectionEnum.values()) {
            int mustCode = Integer.parseInt(code);
            int nowCode = Integer.parseInt(fireInspectionEnum.getCode());
            if (mustCode >= nowCode) {
                list.add(fireInspectionEnum.getCode());
            }
        }
        return list;
    }
}
