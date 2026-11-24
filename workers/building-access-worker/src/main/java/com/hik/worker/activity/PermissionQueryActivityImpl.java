package com.hik.worker.activity;

import com.hik.worker.client.XinkaipuApiClient;
import io.temporal.activity.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限查询 Activity 实现
 * 使用 XinkaipuApiClient.getGrantInfo() 查询授权信息
 */
@Slf4j
@Component
public class PermissionQueryActivityImpl implements PermissionQueryActivity {

    private final XinkaipuApiClient xinkaipuApiClient;

    public PermissionQueryActivityImpl(XinkaipuApiClient xinkaipuApiClient) {
        this.xinkaipuApiClient = xinkaipuApiClient;
    }

    @Override
    public Map<String, Object> queryGrantInfo(String outid) {
        log.info("查询单个授权信息, outid: {}", outid);
        try {
            Object result = xinkaipuApiClient.getGrantInfo(outid);
            return toMap(result, outid);
        } catch (RuntimeException e) {
            log.warn("查询单个授权信息失败, outid: {}", outid);
            throw Activity.wrap(e);
        }
    }

    @Override
    public List<Map<String, Object>> batchQueryGrantInfo(List<String> outids) {
        log.info("批量查询授权信息, 数量: {}", outids.size());
        List<Map<String, Object>> results = new ArrayList<>();
        for (String outid : outids) {
            try {
                Object result = xinkaipuApiClient.getGrantInfo(outid);
                results.add(toMap(result, outid));
            } catch (Exception e) {
                log.warn("批量查询授权信息失败, outid: {}, 错误: {}", outid, e.getMessage());
                Map<String, Object> err = new LinkedHashMap<>();
                err.put("outid", outid);
                err.put("success", false);
                err.put("error", e.getMessage());
                results.add(err);
            }
        }
        return results;
    }

    /**
     * 将 XinkaipuApiClient 返回结果转换为 Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(Object result, String outid) {
        if (result instanceof Map) {
            return (Map<String, Object>) result;
        }
        Map<String, Object> wrapper = new LinkedHashMap<>();
        wrapper.put("outid", outid);
        wrapper.put("data", result);
        return wrapper;
    }
}
