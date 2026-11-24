package com.yutong.modules.user.service.impl;


import com.yutong.modules.user.entity.YtsafetySysUserDataRule;
import com.yutong.modules.user.mapper.SafetySysUserMapper;
import com.yutong.modules.user.mapper.YtsafetySysUserDataRuleMapper;
import com.yutong.modules.user.api.IYtsafetySysUserDataRuleService;
import com.yutong.modules.user.api.SafetySysPermService;
import com.yutong.modules.user.vo.PublicClass;
import org.jeecg.common.api.vo.ColumnPermVO;
import org.jeecg.common.util.CommonUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 用户菜单数据规则表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
@Service
public class YtsafetySysUserDataRuleServiceImpl extends ServiceImpl<YtsafetySysUserDataRuleMapper, YtsafetySysUserDataRule> implements IYtsafetySysUserDataRuleService {

    @Autowired
    private YtsafetySysUserDataRuleMapper userDataRuleMapper;

    @Autowired
    private SafetySysPermService safetySysPermService;

    @Autowired
    private SafetySysUserMapper safetySysUserMapper;


    @Override
    public String getListByUserId(String workNo, String permCode) {
        Long userId  = safetySysUserMapper.selectIdByEmpNo(workNo);
        List<YtsafetySysUserDataRule> list = userDataRuleMapper.getListByUserId(userId, permCode);
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
                    stringBuilder.append("'").append(ruleValue).append("'").append(" ");
                } else {
                    stringBuilder.append(ruleValue).append(" ");
                }
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public List<ColumnPermVO> getColumnPermissions(String workNo,String permCode) {
        Long userId  = safetySysUserMapper.selectIdByEmpNo(workNo);
        List<ColumnPermVO> voList = new ArrayList<>();
        List<YtsafetySysUserDataRule> list = new ArrayList<>();
        if(Objects.nonNull(userId)){
            list = userDataRuleMapper.getColumnPermissionByUserId(userId, "column_hide",permCode);
            if(!CollectionUtils.isEmpty(list)){
                for (YtsafetySysUserDataRule p : list) {
                    ColumnPermVO vo = new ColumnPermVO();
                    vo.setColumnKey(CommonUtils.underlineToCamel(p.getFieldCode()));
                    vo.setPermType("HIDE");
                    voList.add(vo);
                }
            }
        }
        return voList;
    }


    private List<YtsafetySysUserDataRule> setListParam(){
        List<YtsafetySysUserDataRule> list = new ArrayList<>();
        YtsafetySysUserDataRule dataRuleObj = new YtsafetySysUserDataRule();
        dataRuleObj.setFieldCode("supper_order");
        dataRuleObj.setRuleConditions(">");
        dataRuleObj.setRuleValue("1");
        list.add(dataRuleObj);
        return list;
    }



}
