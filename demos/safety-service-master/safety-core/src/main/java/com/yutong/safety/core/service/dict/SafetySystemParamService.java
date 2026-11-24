package com.yutong.safety.core.service.dict;

import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.param.web.sys.SystemParamAddParam;
import com.yutong.safety.common.param.web.sys.SystemParamListParam;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.core.db.entity.dict.SafetySystemParamDO;
import com.yutong.safety.core.db.entity.dict.SafetySystemParamDOExample;
import com.yutong.safety.core.db.mapper.dict.SafetySystemParamDOMapper;
import com.yutong.safety.core.service.SafetyLogMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SafetySystemParamService {

    @Autowired
    private SafetySystemParamDOMapper safetySystemParamDOMapper;

    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    public int saveOrUpdate(SystemParamAddParam paramAddParam){
        SafetySystemParamDO systemParamDO = new SafetySystemParamDO();
        BeanUtils.copyProperties(paramAddParam,systemParamDO);
        int i;
        String modifyBy = paramAddParam.getModifyBy();
        String ability = "更新";
        if(paramAddParam.getId() == null){
            systemParamDO.setCreateTime(new Date());
            systemParamDO.setCreateUser(modifyBy);
            ability = "新增";
            i = safetySystemParamDOMapper.insert(systemParamDO);
        }else{
            systemParamDO.setUpdateTime(new Date());
            systemParamDO.setUpdateUser(modifyBy);
            i = safetySystemParamDOMapper.updateByPrimaryKeySelective(systemParamDO);
        }
        String paramStr = JsonUtils.toString(paramAddParam);
        int maxLen = Math.min(paramStr.length(), 200);
        this.saveLog(ability,modifyBy, "入参："+paramStr.substring(0,maxLen));
        return i;
    }

    /**
     * 做删除标记
     *
     * @param ruleId 规则主键
     * @return 返回
     */
    public int deleteById(List<Integer> ruleId,String modify) {
        SafetySystemParamDOExample example = new SafetySystemParamDOExample();
        example.createCriteria().andIdIn(ruleId);
        List<SafetySystemParamDO> queryList = safetySystemParamDOMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(queryList)){
            String paramCodes = queryList.stream().map(SafetySystemParamDO::getParamCode).collect(Collectors.joining(","));
            String paramName =  queryList.stream().map(SafetySystemParamDO::getParamName).collect(Collectors.joining(","));
            int i  = safetySystemParamDOMapper.deleteByExample(example);
            this.saveLog("删除",modify, "删除范围："+paramCodes+","+paramName);

            return i;
        }

        return 0;
    }

    /**
     * 分页查询
     *
     * @param param 查询入参
     * @return 返回
     */
    public List<SafetySystemParamDO> pageList(SystemParamListParam param) {
        SafetySystemParamDOExample example = formatExample(param);
        example.setOrderByClause(" team_id asc,rule_sort asc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        example.setOrderByClause(param.getOrderByClause());
        return safetySystemParamDOMapper.selectByExample(example);
    }

    /**
     * 分页总条数
     *
     * @param param 查询入参
     * @return 返回
     */
    public long pageListCount(SystemParamListParam param) {
        SafetySystemParamDOExample example = formatExample(param);
        return safetySystemParamDOMapper.countByExample(example);
    }

    /**
     * 查询-不分页
     *
     * @param param 查询入参
     * @return 返回
     */
    public List<SafetySystemParamDO> list(SystemParamListParam param) {

        SafetySystemParamDOExample example = formatExample(param);
        example.setOrderByClause(" team_id asc,rule_sort asc ");

        return safetySystemParamDOMapper.selectByExample(example);
    }

    public SafetySystemParamDO getByParamCode(String paramCode){
        if(StringUtils.isBlank(paramCode)){
            return new SafetySystemParamDO();
        }
        SystemParamListParam param = new SystemParamListParam();
        param.setParamCode(paramCode);
        SafetySystemParamDOExample example = formatExample(param);

        List<SafetySystemParamDO> list = safetySystemParamDOMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            return new SafetySystemParamDO();
        }else{
            return list.get(0);
        }
    }

    /**
     * 处理检索条件
     * @param param 入参
     * @return 返回值
     */
    private SafetySystemParamDOExample formatExample(SystemParamListParam param){
        SafetySystemParamDOExample example = new SafetySystemParamDOExample();
        SafetySystemParamDOExample.Criteria criteria = example.createCriteria();
        //只查询未删除状态的规则
        if(StringUtils.isNotBlank(param.getParamCode())){
            criteria.andParamCodeEqualTo(param.getParamCode());
        }
        if(StringUtils.isNotBlank(param.getParamName())){
            criteria.andParamNameLike("%" + param.getParamName() + "%");
        }
        if(StringUtils.isNotEmpty(param.getParamDesc())){
            criteria.andParamDescLike(param.getParamDesc());
        }
        if(param.getUnId() != null){
            criteria.andIdNotEqualTo(param.getUnId());
        }
        if(param.getBegin() != null){
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }
        return example;
    }

    /**
     * 新增日志记录
     *
     * @param ability  操作类型
     * @param realName 操作人
     * @param message  操作明细
     */
    private void saveLog(String ability, String realName, String message){
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("系统参数配置");
        log.setAbility(ability);
        log.setMessage(message);
        log.setOperator(realName);
        safetyLogMessageService.addLogMessage(log);
    }

    public void updateByPrimaryKey(SafetySystemParamDO dto) {
        safetySystemParamDOMapper.updateByPrimaryKeySelective(dto);
    }
}
