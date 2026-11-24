package com.yutong.safety.core.service.login;

import com.alibaba.fastjson.JSONObject;
import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.WebserviceUtil;
import com.yutong.safety.common.vo.login.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TockenService {
    @Value("${ima.sso.userUrl}")
    private String userUrl;
    @Autowired
    private WebserviceUtil webserviceUtil;

    public UserVO getUserByTocken(String tocken) {
        if (StringUtils.isNotEmpty(tocken)) {
            String iamUserUrl = String.format(userUrl, tocken);
            String tockenUser = webserviceUtil.sendHttps(iamUserUrl, tocken);
            if(StringUtils.isNotEmpty(tockenUser)) {
                JSONObject userJson = (JSONObject) JsonUtils.parse(tockenUser);
                if(userJson.containsKey("workNo")) {
                    UserVO userVO = new UserVO();
                    userVO.setTocken(tocken);
                    userVO.setWorkNo(userJson.getString("workNo"));
                    userVO.setRealName(userJson.getString("realName"));
                    userVO.setPhone(userJson.getString("phone"));
                    userVO.setUserName(userJson.getString("userName"));
                    userVO.setEmail(userJson.getString("email"));
                    return userVO;
                } else {
                    throw new BizException(ResponseEnum.FAIL.getCode(),"通过tocken获取用户信息失败");
                }
            }
        } else {
            throw new BizException(ResponseEnum.FAIL.getCode(),"tocken值为空");
        }
        return null;
    }
}
