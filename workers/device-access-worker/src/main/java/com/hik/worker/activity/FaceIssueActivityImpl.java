package com.hik.worker.activity;

import com.hik.worker.client.FujiApiClient;
import com.hik.worker.model.FaceIssueRequest;
import com.hik.worker.model.FaceIssueResponse;
import io.temporal.activity.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 人脸发行 Activity 实现
 */
@Slf4j
@Component
public class FaceIssueActivityImpl implements FaceIssueActivity {

    private final FujiApiClient fujiApiClient;

    public FaceIssueActivityImpl(FujiApiClient fujiApiClient) {
        this.fujiApiClient = fujiApiClient;
    }

    @Override
    public FaceIssueResponse issueFace(FaceIssueRequest request) {
        log.info("人脸发行, StaffNo: {}, OperType: {}", request.getStaff().getStaffNo(), request.getOperType());
        try {
            return fujiApiClient.faceIssue(request);
        } catch (RuntimeException e) {
            log.warn("人脸发行失败, StaffNo: {}", request.getStaff().getStaffNo());
            throw Activity.wrap(e);
        }
    }
}
