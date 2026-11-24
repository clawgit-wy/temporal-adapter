package com.hik.worker.activity;

import com.hik.worker.client.FujiApiClient;
import com.hik.worker.model.FaceIncrementRequest;
import com.hik.worker.model.FaceIssueResponse;
import io.temporal.activity.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 人脸增量权限 Activity 实现
 */
@Slf4j
@Component
public class FaceIncrementActivityImpl implements FaceIncrementActivity {

    private final FujiApiClient fujiApiClient;

    public FaceIncrementActivityImpl(FujiApiClient fujiApiClient) {
        this.fujiApiClient = fujiApiClient;
    }

    @Override
    public FaceIssueResponse modifyByPowerType(FaceIncrementRequest request) {
        log.info("人脸增量权限, StaffNo: {}, powerType: {} ({})",
                request.getStaff().getStaffNo(),
                request.getPowerType(),
                request.getPowerType() == 0 ? "增加" : "删除");
        try {
            return fujiApiClient.faceModifyByPowerType(request);
        } catch (RuntimeException e) {
            log.warn("人脸增量权限失败, StaffNo: {}", request.getStaff().getStaffNo());
            throw Activity.wrap(e);
        }
    }
}
