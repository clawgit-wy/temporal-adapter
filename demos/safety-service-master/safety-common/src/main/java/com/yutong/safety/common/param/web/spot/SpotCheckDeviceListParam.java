package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.param.web.WebPageParam;
import lombok.Data;

import java.util.List;

/**
 * @author gwcuiyx
 * @date 2024-4-28 17:01
 */
@Data
public class SpotCheckDeviceListParam extends WebPageParam {
    /**
     * 厂区id
     */
    private Integer factoryId;
    /**
     * 设备类型
     */
    private String deviceType;
    /**
     * 项目名称
     */
    private String teamName;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 位置
     */
    private String location;
    /**
     * 设备编号
     */
    private String deviceCode;
    /**
     * 责任部门
     */
    private String dutyName;
    /**
     * 点检人
     */
    private String checkEmp;

    /**
     * 工厂字段ID拼接
     */
    private String factoryIds;

    /**
     * 类目ID
     */
    private Integer teamId;

    /**
     * 临时点检人结束时间
     */
    private String proEndDate;

    /**
     * 设备状态 0-正常 1-异常
     */
    private Integer deviceStatus;

    /**
     * 点检项目ID集合
     */
    private List<Integer> teamIdList;
}
