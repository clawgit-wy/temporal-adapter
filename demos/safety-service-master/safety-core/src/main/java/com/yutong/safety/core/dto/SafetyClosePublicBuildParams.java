package com.yutong.safety.core.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 关闭员工公共楼宇门禁的参数
 */
@Data
public class SafetyClosePublicBuildParams {
    /**
     * 员工号
     */
    private String empNo;
    /**
     * 楼栋编码，可以自己传入，也可以不传，当type为true时，具体的执行方法中会根据关闭的设备编码，获取对应的楼栋编码
     */
    private List<String> buildingCode;
    /**
     * 过滤掉的设备编码，表示本次需要关闭的, 如果为空表示不过滤
     */
    private List<String> closeDeviceCode = new ArrayList<>();
    /**
     * 关闭的类型，
     * true 表示只检查和关闭指定的楼栋编码，如果楼栋编码为空，就不检查和关闭，默认是true
     * false 表示检查所有，此时楼栋编码字段无效
     */
    private boolean type = true;
}
