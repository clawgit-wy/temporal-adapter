package com.yutong.safety.web.config;

import com.alibaba.excel.util.StringUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.RedisUtil;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.entry.SafetyEntryCompanyRegisterDO;
import com.yutong.safety.core.service.entry.SafetyEntryCompanyRegisterService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.yutong.safety.common.constants.Constants.REDIS_ENTRY_TOKEN_EXPIR_TIME;
import static com.yutong.safety.common.constants.Constants.REDIS_ENTRY_TOKEN_PRE;
import static com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants.COMPANY_REGISTER_STATUS_PASS;

@Component
public class EntryNetworkFilter implements Filter {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SafetyEntryCompanyRegisterService safetyEntryCompanyRegisterService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestUrl = request.getRequestURI();

        if (!checkUrl(requestUrl)) {
            String token = request.getHeader("X-Access-Token");
            if (StringUtils.isBlank(token)) {
                goLogin(response);
                return;
            }
            String key = TokenUtils.getString(token, "redisKey");
            String phone = TokenUtils.getString(token, "phone");
            String redisUser = String.valueOf(redisUtil.get(REDIS_ENTRY_TOKEN_PRE + key));

            // redis信息为空， 手机号不存在， Redis的手机号和token的手机号不一致，token失效
            if (StringUtils.isBlank(redisUser) || StringUtils.isBlank(phone) || !redisUser.equals(phone) || !TokenUtils.validateToken(token)) {
                goLogin(response);
                return;
            }
            // 判断账号的状态
            SafetyEntryCompanyRegisterDO info = safetyEntryCompanyRegisterService.selectInfo(phone);
            if (info == null ||!COMPANY_REGISTER_STATUS_PASS.equals(info.getStatus())) {
                redisUtil.del(REDIS_ENTRY_TOKEN_PRE + key);
                goLogin(response);
                return;
            }
            // 续期
            redisUtil.expire(REDIS_ENTRY_TOKEN_PRE + key, REDIS_ENTRY_TOKEN_EXPIR_TIME);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }


    @SneakyThrows
    private void goLogin(HttpServletResponse response) {
        LogUtils.info("跳转登录fakg={}");
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String str = "{\"code\":\"401\",\"msg\":\"请重新登陆\"}";
        try (PrintWriter writer = response.getWriter()) {
            writer.print(str);
            writer.flush();
        } catch (Exception e) {
            LogUtils.error("跳转登录异常", e);
        }
    }

    private boolean checkUrl(String url) {
        return url.contains("/login") || url.contains("/register") || url.contains("/sendVerifyCode") || url.contains("/captcha");
    }
}
