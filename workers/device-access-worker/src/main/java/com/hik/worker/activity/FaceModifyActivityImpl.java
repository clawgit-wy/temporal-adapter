package com.hik.worker.activity;

import com.hik.worker.client.FujiApiClient;
import com.hik.worker.model.FaceIssueRequest;
import com.hik.worker.model.FaceIssueResponse;
import io.temporal.activity.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 人脸改写 Activity 实现
 */
@Slf4j
@Component
public class FaceModifyActivityImpl implements FaceModifyActivity {

    private final FujiApiClient fujiApiClient;

    public FaceModifyActivityImpl(FujiApiClient fujiApiClient) {
        this.fujiApiClient = fujiApiClient;
    }

    @Override
    public FaceIssueResponse modifyFace(FaceIssueRequest request) {
        log.info("人脸改写, StaffNo: {}", request.getStaff().getStaffNo());
        try {
            return fujiApiClient.faceModify(request);
        } catch (RuntimeException e) {
            log.warn("人脸改写失败, StaffNo: {}", request.getStaff().getStaffNo());
            throw Activity.wrap(e);
        }
    }
}
