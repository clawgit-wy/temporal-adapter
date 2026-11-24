package com.hik.worker.activity;

import com.hik.worker.model.DeviceSyncActivityRequest;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.util.List;
import java.util.Map;

/**
 * 设备数据同步 Activity 接口
 * 从第三方门禁系统同步设备数据
 */
@ActivityInterface
public interface DeviceSyncActivity {
    /**
     * 拉取单页设备数据
     */
    @ActivityMethod
    List<Map<String, Object>> fetchDevicePage(DeviceSyncActivityRequest request);

    /**
     * 保存设备数据到本地（通过webhook回调业务系统）
     */
    @ActivityMethod
    boolean saveDevices(List<Map<String, Object>> devices, String deviceType);

    /**
     * 标记已移除设备
     */
    @ActivityMethod
    boolean markRemovedDevices(List<String> deviceCodes);

    /**
     * 发送新设备通知
     */
    @ActivityMethod
    boolean notifyNewDevices(List<String> deviceCodes);
}
