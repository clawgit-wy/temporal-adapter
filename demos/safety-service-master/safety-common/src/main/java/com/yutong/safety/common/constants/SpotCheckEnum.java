package com.yutong.safety.common.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备设施点检静态枚举
 *
 * @author gwxiexj
 * @since 2024-05-16 20:56
 */
public class SpotCheckEnum {

    public static Map<Integer,String> CHECK_STATUS_MAP = new HashMap<>();

    public static Map<Integer,String> DEVICE_STATUS_MAP = new HashMap<>();

    public static Map<Integer,String> REFORM_STATUS_MAP = new HashMap<>();

    public static Map<Integer,String> VALIDATE_STATUS_MAP = new HashMap<>();

    static{
        CHECK_STATUS_MAP.put(0,"待点检");
        CHECK_STATUS_MAP.put(1,"点检中");
        CHECK_STATUS_MAP.put(2,"已点检");

        DEVICE_STATUS_MAP.put(0,"正常");
        DEVICE_STATUS_MAP.put(1,"异常");

        REFORM_STATUS_MAP.put(0,"待整改");
        REFORM_STATUS_MAP.put(1,"已整改");

        VALIDATE_STATUS_MAP.put(0,"待验证");
        VALIDATE_STATUS_MAP.put(1,"已验证");
    }
}
