package com.hik.worker.activity;

import com.hik.worker.model.CardIssueRequest;
import com.hik.worker.model.CardIssueResponse;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * 卡发行 Activity 接口
 */
@ActivityInterface
public interface CardIssueActivity {
    @ActivityMethod
    CardIssueResponse issueCard(CardIssueRequest request);
}
