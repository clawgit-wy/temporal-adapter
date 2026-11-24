package com.yutong.safety.web.config;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.RedisUtil;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.login.UserVO;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class SSOLoginFilter implements Filter {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestUrl = request.getRequestURI();
        try {
            String tocken = request.getHeader("X-Access-Token");
            if (checkUrl(requestUrl)) {
                Cookie[] cookies = request.getCookies();
                boolean isValid = false;
                String MPToken = "";
                String EIPToken = "";
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (Constants.MP_TOKEN_KEY.equals(cookie.getName()) && StringUtils.isNotEmpty(cookie.getValue())) {
                            MPToken =  cookie.getValue();
                            continue;
                        }
                        if (Constants.EIP_TOKEN_KEY.equals(cookie.getName()) && StringUtils.isNotEmpty(cookie.getValue())) {
                            EIPToken =  cookie.getValue();
                        }
                    }
                }
                if(StringUtils.isNotEmpty(EIPToken) || StringUtils.isNotEmpty(MPToken)){
                    isValid = true;
                }
                if(!isValid){
                    goLogin(response,"");
                    return;
                }
                if(StringUtils.isEmpty(tocken)){
                    goLogin(response,"1");
                    return;
                }
                UserVO userVO = getUserByTocken(tocken);
                if(userVO == null || StringUtils.isEmpty(userVO.getWorkNo())){
                    goLogin(response,"1");
                    return;
                }
                String redisUser = String.valueOf(redisUtil.get(Constants.REDIS_TOKEN_PRE+userVO.getWorkNo()));
                if(StringUtils.isEmpty(redisUser) || "null".equals(redisUser)){
                    goLogin(response,"1");
                    return;
                }
                String[] redisArr = redisUser.split(";");
                if (redisArr.length >= 3) {
                    if (tocken.equals(redisArr[1]) && EIPToken.equals(redisArr[2])) {
                        filterChain.doFilter(request, response);
                    } else {
                        goLogin(response, "1");
                    }
                } else {
                    goLogin(response, "");
                }
            } else {
                filterChain.doFilter(request, response);
            }
        } catch (Exception e) {
            LogUtils.error("{\"code\":\"9999\",\"msg\":\"请求异常：URL="+requestUrl+",异常信息："+e.getMessage()+" \"}");
            LogUtils.error("请求异常：URL="+requestUrl+",异常信息,",e);
            goLogin(response,"");
        }
    }

    @Override
    public void destroy() {

    }

    public UserVO getUserByTocken(String tocken) {
        if (StringUtils.isEmpty(tocken)) {
            return null;
        }
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        if (tockenUser.isEmpty()) {
            return null;
        }
        if (!tockenUser.containsKey("workNo")) {
            return null;
        }
        if (tockenUser.get("workNo") == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        userVO.setTocken(tocken);
        userVO.setWorkNo(tockenUser.get("workNo"));
        userVO.setRealName(tockenUser.get("realName"));
        userVO.setPhone(tockenUser.get("phone"));
        userVO.setUserName(tockenUser.get("userName"));
        userVO.setEmail(tockenUser.get("email"));
        return userVO;
    }

    @SneakyThrows
    private void goLogin(HttpServletResponse response,String flag){
        LogUtils.info("跳转登录fakg={}",flag);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String str;
        if("1".equals(flag)){
            str = "{\"code\":\"402\",\"msg\":\"token error\"}";
        }else{
            str = "{\"code\":\"401\",\"msg\":\"token loss\"}";
        }
        try (ServletOutputStream out = response.getOutputStream()) {
            out.print(str);
            out.flush();
        } catch (Exception e) {
            LogUtils.error("跳转登录异常", e);
        }
    }

    public Boolean checkUrl(String url) {
        return !url.contains("/authc/login") && !url.contains("/authc/logout")
                && !url.contains("/pub/file/picUpload");

    }
}
