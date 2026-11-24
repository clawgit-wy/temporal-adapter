package com.hik.worker.activity;

import com.hik.worker.model.StaffInfo;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.util.List;

/**
 * 校验 Activity 接口
 * 校验白名单与设备权限
 */
@ActivityInterface
public interface ValidateActivity {
    @ActivityMethod
    boolean validateWhitelist(String staffNo);

    @ActivityMethod
    boolean validateDevicePermission(String staffNo, List<Integer> deviceNos);

    @ActivityMethod
    List<StaffInfo> filterWhitelistStaff(List<StaffInfo> staffList);
}
