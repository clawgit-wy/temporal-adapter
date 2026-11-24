package com.hik.worker.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.util.List;
import java.util.Map;

/**
 * 权限查询 Activity 接口
 * 查询新开普门禁授权信息
 */
@ActivityInterface
public interface PermissionQueryActivity {
    @ActivityMethod
    Map<String, Object> queryGrantInfo(String outid);

    @ActivityMethod
    List<Map<String, Object>> batchQueryGrantInfo(List<String> outids);
}
