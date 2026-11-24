package com.hik.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 统一设备权限下发请求（工作流层使用）
 * deviceType: FUJI=富士设备, XINKAIPU=新开普设备
 * operationType: ISSUE=发行, MODIFY=改写, INCREMENT_ADD=增量增加, INCREMENT_DELETE=增量删除
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevicePermissionRequest implements Serializable {
    private String batchId;
    private String deviceType;
    private String operationType;
    private List<StaffInfo> staffList;
    private List<Integer> authorizeDeviceNo;
    private List<String> authDevice;
    private String beginTime;
    private String endTime;
    private String facePicture;
    private List<String> projectGids;
}
