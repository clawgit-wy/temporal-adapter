package com.yutong.safety.core.handler.build;

import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.build.SafetyBuildWhiteListDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildWhiteRuleDO;
import com.yutong.safety.core.db.entity.build.param.BuildWhiteRuleListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildWhiteRuleVO;
import com.yutong.safety.core.service.build.SafetyBuildWhiteListService;
import com.yutong.safety.core.service.build.SafetyBuildWhiteRuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SafetyBuildWhiteRuleHandler {

    @Resource
    private SafetyBuildWhiteRuleService safetyBuildWhiteRuleService;

    @Resource
    private SafetyBuildWhiteListService safetyBuildWhiteListService;

    /**
     * 保存
     * @param buildWhiteRuleDO 保存的对象
     * @param token 秘钥
     * @return 保存条数
     */
    public int save(SafetyBuildWhiteRuleDO buildWhiteRuleDO,String token){
        String realName = TokenUtils.getRealName(token);
        buildWhiteRuleDO.setCreateUser(realName);
        return safetyBuildWhiteRuleService.save(buildWhiteRuleDO);
    }

    public int deleteBatchs(String ids,String token){
        String realName = TokenUtils.getRealName(token);
        return safetyBuildWhiteRuleService.deleteById(ids,realName);
    }

    public Object pageList(BuildWhiteRuleListParam listParam,String token){
        listParam.setPageFlag(true);
        List<SafetyBuildWhiteRuleDO> list = safetyBuildWhiteRuleService.selectList(listParam);
        List<BuildWhiteRuleVO> voList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            List<String> reasonList = list.stream().map(e->e.getRemark()+"-"+e.getId()).collect(Collectors.toList());
            BuildWhiteRuleListParam param = new BuildWhiteRuleListParam();
            param.setCreateReasons(reasonList);
            List<SafetyBuildWhiteListDO> listDOS = safetyBuildWhiteListService.selectList(param);
            Map<String, Long> empNoCounts;
            if(!CollectionUtils.isEmpty(listDOS)){
                empNoCounts = listDOS.stream()
                        .collect(Collectors.groupingBy(
                                SafetyBuildWhiteListDO::getCreateReason,
                                Collectors.counting()
                        ));
            }else{
                empNoCounts = new HashMap<>();
            }
            list.forEach(e->{
                String curReason = e.getRemark()+"-"+e.getId();
                BuildWhiteRuleVO newVO = new BuildWhiteRuleVO();
                BeanUtils.copyProperties(e,newVO);
                Long size = empNoCounts.get(curReason);
                newVO.setWhiteCount(size==null?0:size.intValue());
                voList.add(newVO);
            });
        }
        Long total = safetyBuildWhiteRuleService.selectCount(listParam);
        return new PageVO<>(total,voList);
    }

    public BuildWhiteRuleVO getById(Integer id){
        BuildWhiteRuleVO retRuleVO = new BuildWhiteRuleVO();
        SafetyBuildWhiteRuleDO ruleDO = safetyBuildWhiteRuleService.getById(id);
        if(ruleDO != null){
            BeanUtils.copyProperties(ruleDO,retRuleVO);
        }
        return retRuleVO;
    }
}
