package com.yutong.safety.web.controller.web.login;

import com.alibaba.fastjson.JSONObject;
import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.common.vo.login.UserVO;
import com.yutong.safety.core.handler.login.LoginHandler;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * create gwcuiyx 2023.08.07
 * 登录模块--controller
 */
@Api(tags = "登录模块功能")
@RestController
@RequestMapping("/web/safety/login")
public class LoginController {

    @Autowired
    private LoginHandler loginHandler;

    @Value("${iam.sso.tockenUrl}")
    private String tockenUrl;

    @Value("${ima.sso.userUrl}")
    private String userUrl;

    @Value("${iam.loginOut.url}")
    private String loginOutUrl;

    @Autowired
    private WebserviceUtil webserviceUtil;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取用户菜单
     * @return
     */
    @RequestMapping("/authc/getUserMainMenus")
    @ResponseBody
    public Object getUserMainMenus(@RequestHeader("X-Access-Token") String tocken){
        return loginHandler.selectMainMenuList(tocken);
    }

    /**
     *  单点中台sso接口地址
     * @param code
     * @return
     */
    @RequestMapping("/authc/login")
    public Object login(String code, ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String iamTockenUrl = null;
        String iamUserUrl = null;
        if (StringUtils.isNotEmpty(code)) {
            iamTockenUrl = String.format(tockenUrl, code);
            String result = webserviceUtil.sendHttp(iamTockenUrl, "1", null, new HashMap(), "POST");
            if (StringUtils.isNotEmpty(result)) {
                JSONObject resultJson = (JSONObject) JsonUtils.parse(result);
                String tocken = resultJson.getString("accessToken");
                LogUtils.info("请求"+iamTockenUrl+";获取到的resultJson="+resultJson);
                if(StringUtils.isEmpty(tocken)){
                    throw new BizException(ResponseEnum.FAIL.getCode(),"通过tocken获取用户信息失败");
                }
                iamUserUrl = String.format(userUrl, tocken);
                String tockenUser = webserviceUtil.sendHttps(iamUserUrl, tocken);
                LogUtils.info("请求"+iamUserUrl+";获取到的tockenUser="+tockenUser);

                Cookie[] cookies = request.getCookies();
                String LRToken = "";
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (Constants.EIP_TOKEN_KEY.equals(cookie.getName())) {
                            LRToken =  cookie.getValue();
                            break;
                        }
                    }
                }
                LogUtils.info("获取到的LRToken="+LRToken);
                if(StringUtils.isNotEmpty(tockenUser)) {
                    JSONObject userJson = (JSONObject) JsonUtils.parse(tockenUser);
                    if(userJson.containsKey("workNo")) {
                        String workNo = userJson.getString("workNo");
                        UserVO userVO = new UserVO();
                        userVO.setWorkNo(userJson.getString("workNo"));
                        userVO.setRealName(userJson.getString("realName"));
                        userVO.setPhone(userJson.getString("phone"));
                        userVO.setUserName(userJson.getString("userName"));
                        userVO.setEmail(userJson.getString("email"));
                        userVO.setEipToken(LRToken);
                        //生成自己的tocken
                        String safetyTocken = TokenUtils.createToken(userJson.getString("workNo"), userJson.getString("realName"), userJson.getString("phone"), userJson.getString("userName"), userJson.getString("email"));
                        userVO.setTocken(safetyTocken);
                        try {
                            //放入redis
                            redisUtil.set(Constants.REDIS_TOKEN_PRE+workNo,workNo+";"+safetyTocken+";"+LRToken,TokenUtils.EXPIRE_DATE/1000);
                            LogUtils.info("key="+Constants.REDIS_TOKEN_PRE+workNo);
                            Object redisUser = redisUtil.get(Constants.REDIS_TOKEN_PRE+workNo);
                            LogUtils.info("获取到的redisUser="+redisUser);
                        }catch (Exception e){
                            LogUtils.error("信息保存redis失败",e);
                        }
                        return userVO;
                    } else {
                        throw new BizException(ResponseEnum.FAIL.getCode(),"通过tocken获取用户信息失败");
                    }
                } else {
                    throw new BizException(ResponseEnum.FAIL.getCode(),"根据tocken获取用户信息为空");
                }
            } else {
                throw new BizException(ResponseEnum.FAIL.getCode(),"获取tocken值为空");
            }
        } else {
            throw new BizException(ResponseEnum.FAIL.getCode(),"获取ima对应的code值为空");
        }
    }

    /**
     * 获取用户菜单
     * @return
     */
    @RequestMapping("/authc/getUserByTocken")
    public Object getUserByTocken(@RequestHeader("X-Access-Token") String tocken) {
        if(StringUtils.isNotEmpty(tocken)) {
            Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
            if(!tockenUser.isEmpty()) {
                LogUtils.info("getUserByTocken-token="+tocken);
                LogUtils.info("getUserByTocken-tockenUser="+tockenUser.toString());
                if (StringUtils.isNotEmpty(tockenUser.get("workNo"))) {
                    UserVO userVO = new UserVO();
                    userVO.setTocken(tocken);
                    userVO.setWorkNo(tockenUser.get("workNo"));
                    userVO.setRealName(tockenUser.get("realName"));
                    userVO.setPhone(tockenUser.get("phone"));
                    userVO.setUserName(tockenUser.get("userName"));
                    userVO.setEmail(tockenUser.get("email"));

                    return userVO;
                } else {
                    throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"获取tocken值为空");
                }
            }
        } else {
            throw new BizException(ResponseEnum.FAIL.getCode(),"获取tocken值为空");
        }
        return null;
    }

    @RequestMapping(value = "/authc/logout")
    public VO logout(HttpServletRequest request, HttpServletResponse response){
        VO vo;
        try {
            //删除token
            String tocken = request.getHeader("X-Access-Token");
            if(StringUtils.isNotEmpty(tocken)){
                String workNo = TokenUtils.getWorkNo(tocken);
                if (StringUtils.isNotEmpty(workNo)) {
                    String tokenKey = Constants.REDIS_TOKEN_PRE+workNo;
                    redisUtil.del(Constants.REDIS_TOKEN_PRE+workNo);
                }
            }
            Cookie[] cookies = request.getCookies();
            String mpToken = "";
            if (cookies != null) {
                String clearStr = "MP-Access-Token;LRToken;MP-X-Access-Token;X-Access-Token";
                String[] clearArr = clearStr.split(";");
                for (Cookie cookie : cookies) {
                    for(String clear : clearArr){
                        if(Constants.MP_TOKEN_KEY.equals(cookie.getName())){
                            mpToken = cookie.getValue();
                        }
                        if (clear.equals(cookie.getName())) {
                            cookie.setPath("/");
                            cookie.setValue("");
                            cookie.setMaxAge(0);
                            cookie.setDomain("yutong.com");
                            response.addCookie(cookie);
                            break;
                        }
                    }
                }
            }
            if(StringUtils.isNotEmpty(mpToken)){
                Map headParams = new HashMap(4);
                headParams.put("X-Access-Token",mpToken);
                String loginReturn = webserviceUtil.postJsonStr(loginOutUrl, "{}", headParams);
                LogUtils.info("调用退出返回值{}",loginReturn);
            }
            // 重定向至登录页
            String loginUrl = "http://antest.yutong.com";
            vo = VoUtil.successData(loginUrl);
        }catch (Exception e) {
            e.printStackTrace();
            vo = VoUtil.fail("退出登录执行异常");
        }
        return vo;
    }
}
