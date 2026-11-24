package com.hik.worker.activity;

import com.hik.worker.client.FujiApiClient;
import com.hik.worker.model.UnregisterRequest;
import com.hik.worker.model.UnregisterResponse;
import io.temporal.activity.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * 权限注销 Activity 实现
 * 对3个固定GID分别注销人脸和卡权限
 */
@Slf4j
@Component
public class PermissionCancelActivityImpl implements PermissionCancelActivity {

    private final FujiApiClient fujiApiClient;

    public PermissionCancelActivityImpl(FujiApiClient fujiApiClient) {
        this.fujiApiClient = fujiApiClient;
    }

    @Override
    public boolean unregisterFace(List<UnregisterRequest> requests) {
        log.info("注销人脸权限, GID数量: {}", requests.size());
        boolean allSuccess = true;
        for (UnregisterRequest req : requests) {
            try {
                UnregisterResponse resp = fujiApiClient.unregister(req);
                log.info("注销人脸成功, Gid: {}", req.getGid());
            } catch (Exception e) {
                log.warn("注销人脸失败, Gid: {}, 错误: {}", req.getGid(), e.getMessage());
                allSuccess = false;
            }
        }
        return allSuccess;
    }

    @Override
    public boolean unregisterCard(List<UnregisterRequest> requests) {
        log.info("注销卡权限, GID数量: {}", requests.size());
        boolean allSuccess = true;
        for (UnregisterRequest req : requests) {
            try {
                UnregisterResponse resp = fujiApiClient.unregister(req);
                log.info("注销卡成功, Gid: {}", req.getGid());
            } catch (Exception e) {
                log.warn("注销卡失败, Gid: {}, 错误: {}", req.getGid(), e.getMessage());
                allSuccess = false;
            }
        }
        return allSuccess;
    }

    @Override
    public String queryCardNo(String phone) {
        log.info("查询卡号, phone: {}", phone);
        try {
            Object result = fujiApiClient.getByFunc(phone);
            if (result instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) result;
                Object reserve2 = map.get("Reserve2");
                if (reserve2 != null) {
                    return reserve2.toString();
                }
            }
            return null;
        } catch (Exception e) {
            log.warn("查询卡号失败, phone: {}, 错误: {}", phone, e.getMessage());
            throw Activity.wrap(e);
        }
    }
}
