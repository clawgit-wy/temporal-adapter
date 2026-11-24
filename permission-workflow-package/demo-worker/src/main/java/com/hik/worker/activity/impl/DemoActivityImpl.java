package com.hik.worker.activity.impl;

import com.hik.worker.activity.DemoActivity;
import com.hik.worker.model.DemoRequest;
import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Demo Activity 实现
 * 使用 RestTemplate 调用 mokapi 模拟第三方接口, 无需数据库
 */
@Slf4j
@Component
public class DemoActivityImpl implements DemoActivity {

    @Value("${mokapi.url:http://localhost:19090}")
    private String mokapiUrl;

    private final RestTemplate restTemplate;

    public DemoActivityImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String callMockApi(DemoRequest request) {
        log.info("调用 mokapi 校验接口, personId: {}, name: {}", request.getPersonId(), request.getName());
        ActivityExecutionContext ctx = Activity.getExecutionContext();
        ctx.heartbeat("调用mokapi开始");
        String url = mokapiUrl + "/api/hikvision/verify";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> body = new HashMap<>();
            body.put("personId", request.getPersonId());
            body.put("name", request.getName());
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            String response = restTemplate.postForObject(url, entity, String.class);
            log.info("mokapi 校验接口返回: {}", response);
            ctx.heartbeat("调用mokapi完成");
            return response;
        } catch (RuntimeException e) {
            log.error("调用 mokapi 校验接口失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    @Override
    public int processData(DemoRequest request) {
        log.info("数据处理开始, personId: {}", request.getPersonId());
        ActivityExecutionContext ctx = Activity.getExecutionContext();
        try {
            // 模拟数据处理 (sleep), 长耗时操作通过 heartbeat 上报进度
            for (int i = 1; i <= 3; i++) {
                Thread.sleep(500);
                ctx.heartbeat("数据处理进度 " + i + "/3");
            }
            int processedCount = 3;
            log.info("数据处理完成, processedCount: {}", processedCount);
            return processedCount;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw Activity.wrap(e);
        } catch (RuntimeException e) {
            log.error("数据处理失败: {}", e.getMessage());
            throw Activity.wrap(e);
        }
    }

    @Override
    public String generateReport(DemoRequest request) {
        log.info("生成汇总报告, workflowId: {}", request.getWorkflowId());
        String report = String.format("人员[%s](%s)的电话为%s, 报告生成成功",
                request.getName(), request.getPersonId(), request.getPhone());
        log.info("汇总报告: {}", report);
        return report;
    }
}
