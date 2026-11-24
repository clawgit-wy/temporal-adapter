package com.yutong.safety.common.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author caomg
 * @date 2024-11-19 13:05
 * @description KafkaVO
 */
public class KafkaVO {
    /**
     * 门禁数据
     */
    public  static String DMS ="DBO.DMSOPENRECORD";


    /**
     * 私家车的数据数据 进厂
     */
    public  static String INV ="DBO.INVEHICLERECORD";

    /**
     * 私家车的数据数据 出厂
     */
    public  static String OUTV ="DBO.OUTVEHICLE";

    /**
     * 进厂
     */
    public  static String P10 ="P10";
    /**
     * 出厂
     */
    public  static String P20 ="P20";

    /**
     * 进厂
     */
    public  static String _P10 ="入口";
    /**
     * 出厂
     */
    public  static String _P20 ="出口";

    //已进厂
    public  static Integer YJC = 8;

    //已出厂
    public  static Integer YCC = 9;

    //长期车辆
    public  static String CQCL ="C33E7736-9FB3-4932-B4CC-030FAA76EBFC";

    //临时车辆
    public  static String LSCL ="1E0667B0-90D7-46A9-8A68-50D7E4673759";

}