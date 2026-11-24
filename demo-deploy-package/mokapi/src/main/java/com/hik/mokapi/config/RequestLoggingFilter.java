package com.hik.mokapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * HTTP 请求/响应日志过滤器
 * 记录所有进入 mokapi 的请求和返回的响应，便于追踪 Worker 调用过程
 */
@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final int MAX_LOG_LENGTH = 2000;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 跳过健康检查接口
        if (request.getRequestURI().contains("/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // 记录请求日志
            String requestBody = getRequestBody(wrappedRequest);
            log.info("[Worker调用] {} {} | 请求体: {} | 状态码: {} | 耗时: {}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    truncate(requestBody),
                    wrappedResponse.getStatus(),
                    duration);

            // 记录响应日志
            String responseBody = getResponseBody(wrappedResponse);
            if (wrappedResponse.getStatus() >= 400) {
                log.warn("[Worker调用-异常响应] {} {} | 响应: {}",
                        request.getMethod(), request.getRequestURI(), truncate(responseBody));
            } else {
                log.info("[Worker调用-响应] {} {} | 响应: {}",
                        request.getMethod(), request.getRequestURI(), truncate(responseBody));
            }

            // 必须调用此方法将响应体写回原始 response
            wrappedResponse.copyBodyToResponse();
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length == 0) {
            return "(空)";
        }
        return new String(buf, StandardCharsets.UTF_8);
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        if (buf.length == 0) {
            return "(空)";
        }
        return new String(buf, StandardCharsets.UTF_8);
    }

    private String truncate(String str) {
        if (str == null) {
            return "(null)";
        }
        if (str.length() <= MAX_LOG_LENGTH) {
            return str;
        }
        return str.substring(0, MAX_LOG_LENGTH) + "...(截断,总长度:" + str.length() + ")";
    }
}
