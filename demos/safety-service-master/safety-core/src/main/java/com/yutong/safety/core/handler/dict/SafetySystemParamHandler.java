package com.yutong.safety.core.handler.dict;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.sys.SystemParamAddParam;
import com.yutong.safety.common.param.web.sys.SystemParamListParam;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.dict.SafetySystemParamDO;
import com.yutong.safety.core.service.dict.SafetySystemParamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SafetySystemParamHandler {

    @Autowired
    private SafetySystemParamService safetySystemParamService;

    /**
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    public Object pageList(SystemParamListParam param) {
        List<SafetySystemParamDO> list = safetySystemParamService.pageList(param);
        long total = safetySystemParamService.pageListCount(param);
        return new PageVO<SafetySystemParamDO>(total, list);
    }

    /**
     * 数据添加接口
     * @param param
     * @return
     */
    public Object saveOrUpdate(SystemParamAddParam param, String token){
        String realName = TokenUtils.getRealName(token);
        param.setModifyBy(realName);
        SystemParamListParam listParam = new SystemParamListParam();
        listParam.setParamCode(param.getParamCode()); //编码不能重复
        listParam.setUnId(param.getId());
        Long count = safetySystemParamService.pageListCount(listParam);
        if(count > 0){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"系统参数，已存在编码“"+param.getParamCode()+"”");
        }
        return safetySystemParamService.saveOrUpdate(param);
    }

    /**
     * 删除-支持批量
     * @param id  ID拼接串,
     * @return 返回值
     * @throws Exception
     */
    public Object deleteById(String id,String token){
        if(StringUtils.isEmpty(id)){
            return 0;
        }
        String realName = TokenUtils.getRealName(token);
        String[] idArr = id.split(",");
        List<Integer> idList =  Arrays.stream(idArr).map(Integer::valueOf).collect(Collectors.toList());

        return safetySystemParamService.deleteById(idList,realName);
    }

    /**
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    public List<SafetySystemParamDO> getList(SystemParamListParam param) {
        return safetySystemParamService.list(param);
    }

}
