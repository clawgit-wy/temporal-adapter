package com.yutong.safety.core.service.spot;

import com.google.common.collect.Sets;
import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.param.web.spot.ValidateScenePostAddParam;
import com.yutong.safety.common.param.web.spot.ValidateScenePostListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.SafetyFieldManageDO;
import com.yutong.safety.core.db.entity.spot.SafetyValidateScenePostDO;
import com.yutong.safety.core.db.entity.spot.SafetyValidateScenePostDOExample;
import com.yutong.safety.core.db.mapper.spot.SafetyValidateScenePostDOBizMapper;
import com.yutong.safety.core.service.SafetyFieldManageService;
import com.yutong.safety.core.service.SafetyLogMessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SafetyValidateScenePostService {

    @Autowired
    private SafetyValidateScenePostDOBizMapper safetyValidateScenePostDOMapper;

    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    @Autowired
    private SafetyFieldManageService safetyFieldManageService;

    private static String FILED_TYPE_CODE = "inspect_003_xcgwsb_1,inspect_003_mngwsb_2";

    /**
     * 新增修改
     * @param param 入参
     * @param token token
     * @return 更新条数
     */
    public SafetyValidateScenePostDO saveOrUpdate(ValidateScenePostAddParam param,String token)
    {
        int row;
        String realName = TokenUtils.getRealName(token);
        String workNo = TokenUtils.getWorkNo(token);
        if(StringUtils.isEmpty(workNo) || StringUtils.isEmpty(realName)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token失效，请重新登录");
        }
        SafetyValidateScenePostDO scenePostDO = new SafetyValidateScenePostDO();

        if(param.getDataType() == null){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"入参“dataType”不能为空！");
        }
        String ability = "更新";
        String operatePage = "岗位验证-现场岗位";
        if(param.getDataType() == 2){
            operatePage = "岗位验证-模拟岗位";
        }

        BeanUtils.copyProperties(param,scenePostDO);
        scenePostDO.setUpdateTime(new Date());
        scenePostDO.setUpdateEmpNo(workNo);
        scenePostDO.setUpdateEmp(realName);
        //整改处理结果不为空时，可设置处理时间
        if(StringUtils.isNotEmpty(scenePostDO.getReformDesc())){
            scenePostDO.setDealTime(new Date());
        }
        if(scenePostDO.getId() == null){
            ability = "新增";
            scenePostDO.setCreateTime(new Date());
            row = safetyValidateScenePostDOMapper.insert(scenePostDO);
        }else{
            row = safetyValidateScenePostDOMapper.updateByPrimaryKeySelective(scenePostDO);
        }
        //保存日志
        String paramStr = JsonUtils.toString(param);
        int maxLen = paramStr.length()>200?200:paramStr.length();
        this.saveLog(operatePage,ability,realName, paramStr.substring(0,maxLen));
        return scenePostDO;
    }

    /**
     * 获取列表
     * @param param 入参
     * @param pageFlag 是否分页 0-否 1-是
     * @return 返回值
     */
    public List<SafetyValidateScenePostDO> selectList(ValidateScenePostListParam param, boolean pageFlag) {
        //是否分页
        if(pageFlag){
            param.setPageFlag(pageFlag);
        }
        //添加默认字段排序
        if(StringUtils.isEmpty(param.getOrderByClause())){
            param.setOrderByClause(" create_time desc ");
        }
        List<SafetyValidateScenePostDO> scenePostDOS = safetyValidateScenePostDOMapper.selectList(param);
        if(CollectionUtils.isEmpty(scenePostDOS)){
            return scenePostDOS;
        }
        Map<Integer, SafetyFieldManageDO> filedMap = safetyFieldManageService.getFiledManageList(FILED_TYPE_CODE);
        for(SafetyValidateScenePostDO scenePostDO:scenePostDOS){
            scenePostDO.setTeamName(StringUtils.isNotEmpty(scenePostDO.getTeamId()) ? getTeamNames(scenePostDO.getTeamId(), filedMap) : "");
        }
        return scenePostDOS;
    }

    /**
     * 根据id获取名称值
     * @param factoryMap
     * @return
     */
    public String getTeamNames(String teamIds, Map<Integer, SafetyFieldManageDO> factoryMap) {
        Set<String> ids = Sets.newHashSet(teamIds.split(","));
        StringBuilder factoryName = new StringBuilder();
        for (String s : ids) {
            if (factoryMap.containsKey(Integer.valueOf(s.trim()))) {
                SafetyFieldManageDO fieldManageDO = factoryMap.get(Integer.valueOf(s));
                if(!ObjectUtils.isEmpty(fieldManageDO)){
                    factoryName.append(fieldManageDO.getFieldName()).append(",");
                }
            }
        }
        if (StringUtils.isNotEmpty(factoryName.toString())) {
            return factoryName.substring(0, factoryName.length()-1);
        }
        return "";
    }


    /**
     * 获取条数
     * @param param 入参
     * @return 返回值
     */
    public Long selectCount(ValidateScenePostListParam param){
        return safetyValidateScenePostDOMapper.selectCount(param);
    }

    /**
     * 参数整理
     * @param param 入参
     * @return 返回值
     */
    private SafetyValidateScenePostDOExample formatCriteria(ValidateScenePostListParam param) {
        SafetyValidateScenePostDOExample example = new SafetyValidateScenePostDOExample();
        SafetyValidateScenePostDOExample.Criteria criteria = example.createCriteria();
        criteria.andDataTypeEqualTo(param.getDataType());
        if (param.getFactoryId() != null) {
            criteria.andFactoryIdEqualTo(param.getFactoryId());
        }
        if (StringUtils.isNotEmpty(param.getFactory())) {
            criteria.andFactoryLike("%"+param.getFactory()+"%");
        }
        if (StringUtils.isNotEmpty(param.getPostName())) {
            criteria.andPostNameLike("%" + param.getPostName() + "%");
        }
        if (StringUtils.isNotEmpty(param.getTeamName())) {
            criteria.andTeamNameLike("%" + param.getTeamName() + "%");
        }
        if(StringUtils.isNotEmpty(param.getTeamId())){
            criteria.andTeamIdLike(param.getTeamId());
        }
        if (StringUtils.isNotEmpty(param.getValidateBegin()) && StringUtils.isNotEmpty(param.getValidateEnd())) {
            String checkEnd = DateUtils.format(DateUtils.addDays(DateUtils.parse(param.getValidateEnd(), 1), 1), 1);
            criteria.andValidateBeginTimeBetween(param.getValidateBegin(), checkEnd);
        }
        if(StringUtils.isNotEmpty(param.getPersonCategory())){
            criteria.andPersonCategoryEqualTo(param.getPersonCategory());
        }
        if(StringUtils.isNotEmpty(param.getEmpName())){
            criteria.andEmpNameLike("%"+param.getEmpName()+"%");
        }
        if(StringUtils.isNotEmpty(param.getEmpNo())){
            criteria.andEmpNoEqualTo(param.getEmpNo());
        }
        if(StringUtils.isNotEmpty(param.getEmpDept())){
            criteria.andEmpDeptLike("%"+param.getEmpDept()+"%");
        }
        if(StringUtils.isNotEmpty(param.getLeaderEmpName())){
            criteria.andLeaderEmpNameLike("%"+param.getLeaderEmpName()+"%");
        }
        if(StringUtils.isNotEmpty(param.getLeaderEmpNo())){
            criteria.andLeaderEmpNoEqualTo(param.getLeaderEmpNo());
        }
        if(param.getIsPass() != null){
            criteria.andIsPassEqualTo(param.getIsPass());
        }
        if(param.getIsExamine() != null){
            criteria.andIsExamineEqualTo(param.getIsExamine());
        }
        if(StringUtils.isNotEmpty(param.getReformDesc())){
            criteria.andReformDescLike("%"+param.getReformDesc()+"%");
        }
        if(StringUtils.isNotEmpty(param.getCreateEmp())){
            criteria.andCreateEmpLike("%"+param.getCreateEmp()+"%");
        }
        if(StringUtils.isNotEmpty(param.getCreateEmpNo())){
            criteria.andCreateEmpNoEqualTo(param.getCreateEmpNo());
        }
        if(StringUtils.isNotEmpty(param.getFlowNo())){
            criteria.andFlowNoLike("%"+param.getFlowNo()+"%");
        }
        if(StringUtils.isNotEmpty(param.getCreateDept())){
            criteria.andCreateDeptLike("%"+param.getCreateDept()+"%");
        }
        if(StringUtils.isNotEmpty(param.getCreateBegin()) && StringUtils.isNotEmpty(param.getCreateEnd())){
            Date newBegin = DateUtils.parse(param.getCreateBegin(),1);
            Date newEnd = DateUtils.addDays(DateUtils.parse(param.getCreateEnd(), 1), 1);
            criteria.andCreateTimeBetween(newBegin, newEnd);
        }
        if(StringUtils.isNotEmpty(param.getDealBegin()) && StringUtils.isNotEmpty(param.getDealEnd())){
            Date newBegin = DateUtils.parse(param.getDealBegin(),1);
            Date newEnd = DateUtils.addDays(DateUtils.parse(param.getDealEnd(), 1), 1);
            criteria.andDealTimeBetween(newBegin,newEnd);
        }
        //工厂权限
        String factoryIdStr = param.getFactoryIds();
        if(StringUtils.isNotEmpty(factoryIdStr)){
            String[] factoryArr = factoryIdStr.split(",");
            List<Integer> factoryList = Arrays.stream(factoryArr).map(Integer::valueOf).collect(Collectors.toList());
            criteria.andFactoryIdIn(factoryList);
        }

        return example;
    }

    /**
     * 新增日志记录
     * @param operatePage 操作项
     * @param ability 操作类型
     * @param realName 操作人
     * @param message 操作明细
     */
    private void saveLog(String operatePage,String ability,String realName,String message){
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage(operatePage);
        log.setAbility(ability);
        log.setMessage(message);
        log.setOperator(realName);
        safetyLogMessageService.addLogMessage(log);
    }

    public SafetyValidateScenePostDO selectByParimaryKey(Integer id){

        SafetyValidateScenePostDO scenePostDO = safetyValidateScenePostDOMapper.selectByPrimaryKey(id);
        if(scenePostDO == null){
            return scenePostDO;
        }
        Map<Integer, SafetyFieldManageDO> filedMap = safetyFieldManageService.getFiledManageList(FILED_TYPE_CODE);
        scenePostDO.setTeamName(StringUtils.isNotEmpty(scenePostDO.getTeamId()) ? getTeamNames(scenePostDO.getTeamId(), filedMap) : "");
        return scenePostDO;
    }
}
