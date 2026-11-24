package com.hik.worker.activity;

import com.hik.worker.client.FujiApiClient;
import com.hik.worker.model.CardIssueRequest;
import com.hik.worker.model.CardIssueResponse;
import io.temporal.activity.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 卡发行 Activity 实现
 */
@Slf4j
@Component
public class CardIssueActivityImpl implements CardIssueActivity {

    private final FujiApiClient fujiApiClient;

    public CardIssueActivityImpl(FujiApiClient fujiApiClient) {
        this.fujiApiClient = fujiApiClient;
    }

    @Override
    public CardIssueResponse issueCard(CardIssueRequest request) {
        log.info("卡发行, staffNo: {}, tokenId: {}", request.getStaffNo(), request.getTokenId());
        try {
            return fujiApiClient.cardIssue(request);
        } catch (RuntimeException e) {
            log.warn("卡发行失败, staffNo: {}", request.getStaffNo());
            throw Activity.wrap(e);
        }
    }
}
