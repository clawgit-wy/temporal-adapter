package com.yutong.modules.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yutong.modules.auth.api.UserAuthDataApi;
import com.yutong.modules.user.api.ISafetyCommonService;
import com.yutong.modules.user.entity.YtsafetySysUserDataRule;
import com.yutong.modules.user.mapper.SafetySysUserMapper;
import com.yutong.modules.user.mapper.YtsafetySysUserDataRuleMapper;
import com.yutong.modules.user.api.SafetySysPermService;
import com.yutong.modules.user.vo.PublicClass;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysAuthBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserAuthDataApiImpl implements UserAuthDataApi {
    @Lazy
    @Resource
    private ISysAuthBaseAPI isysAuthBaseAPI;

    @Autowired
    private YtsafetySysUserDataRuleMapper userDataRuleMapper;

    @Autowired
    private SafetySysPermService safetySysPermService;

    @Autowired
    private SafetySysUserMapper safetySysUserMapper;

    @Autowired
    private ISafetyCommonService safetyCommonService;

    @Override
    public String queryUserDataRule(String permCode) {
        Result<JSONObject> result = isysAuthBaseAPI.getUserInfo();
        String workNo = "";
        if (Objects.nonNull(result) && Objects.nonNull(result.getResult())) {
            JSONObject userInfo = result.getResult().getJSONObject("userInfo");
            if (Objects.nonNull(userInfo)) {
                workNo = userInfo.getString("workNo");
            }
        }
        Long userId  = safetySysUserMapper.selectIdByEmpNo(workNo);
        List<YtsafetySysUserDataRule> list = new ArrayList<>();
        if(Objects.nonNull(userId)){
            list = userDataRuleMapper.getListByUserId(userId, permCode);
        }
        //List<YtsafetySysUserDataRule> list = setListParam();
        StringBuilder stringBuilder = new StringBuilder();
        if(!CollectionUtils.isEmpty(list)){
            /**查询权限配置的反射class，用于判断当前code在不在配置表中*/
            List<PublicClass> publicClassList = safetySysPermService.getMenuClassInfo(list.get(0).getPermId().toString());
            for(YtsafetySysUserDataRule rule : list){
                /**判断code在不在反射class中，如果在就写入拼接sql，不在就跳出循环*begin*/
                boolean found = false;
                for (PublicClass pc : publicClassList) {
                    if (pc.getDbCode().contains(rule.getFieldCode())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    continue;
                }
                /**判断code在不在反射class中，如果在就写入拼接sql，不在就跳出循环*end*/
                stringBuilder.append(rule.getRelation() + " ");
                //stringBuilder.append("and ");
                stringBuilder.append(" tb."+rule.getFieldCode() + " ");
                stringBuilder.append(rule.getRuleConditions() + " ");
                String ruleValue = rule.getRuleValue();
                if (ruleValue != null && !ruleValue.isEmpty() && !ruleValue.matches("-?\\d+(\\.\\d+)?")) {
                    String wildcard = ("like".equals(rule.getRuleConditions()) || "not like".equals(rule.getRuleConditions())) ? "%" : "";
                    stringBuilder.append("'").append(wildcard).append(ruleValue).append(wildcard).append("'").append(" ");
                } else {
                    if ("like".equals(rule.getRuleConditions()) || "not like".equals(rule.getRuleConditions())){
                        String wildcard = "%";
                        stringBuilder.append("'").append(wildcard).append(ruleValue).append(wildcard).append("'").append(" ");
                    }else{
                        stringBuilder.append(ruleValue).append(" ");
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public LoginUser queryLoginUser(String empNo) {
        LoginUser loginUser = safetySysUserMapper.queryLoginUser(empNo);
        if (loginUser == null) {
            return null;
        }
        Long num = safetyCommonService.checkDefend(empNo);
        if (num > 0) {
            loginUser.setIsDefend(1);
        } else {
            loginUser.setIsDefend(0);
        }
        return loginUser;
    }
}
