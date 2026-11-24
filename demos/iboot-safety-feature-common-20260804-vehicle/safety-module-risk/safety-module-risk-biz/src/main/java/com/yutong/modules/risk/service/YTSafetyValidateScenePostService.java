package com.yutong.modules.risk.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Sets;
import com.yutong.modules.risk.YTSafetyValidateScenePostExample;
import com.yutong.modules.risk.constants.YTResponseEnum;
import com.yutong.modules.risk.entity.YTSafetyValidateScenePost;
import com.yutong.modules.risk.exception.YTBizException;
import com.yutong.modules.risk.mapper.YTSafetyValidateScenePostDOBizMapper;
import com.yutong.modules.risk.util.YTDateUtils;
import com.yutong.modules.risk.web.ValidateScenePostAddParam;
import com.yutong.modules.risk.web.ValidateScenePostListParam;
import com.yutong.modules.user.api.ISafetyFieldManageService;
import com.yutong.modules.user.entity.SafetyFieldManage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class YTSafetyValidateScenePostService {

    @Autowired
    private YTSafetyValidateScenePostDOBizMapper safetyValidateScenePostDOMapper;

    @Autowired
    private ISafetyFieldManageService safetyFieldManageService;


    private static String FILED_TYPE_CODE = "inspect_003_xcgwsb_1,inspect_003_mngwsb_2";

    /**
     * 新增修改
     * @param param 入参
     * @return 更新条数
     */
    public YTSafetyValidateScenePost saveOrUpdate(ValidateScenePostAddParam param)
    {
        int row;
        String realName ;
        String workNo ;
        try{
            LoginUser loginUser = BusUtits.getLoginUser();
            log.info("loginUser测试token是否有效:{}", loginUser);
            realName = loginUser != null ? loginUser.getRealname() : null;
            workNo = loginUser != null ? loginUser.getEmpNo() : null;
        }catch (Exception e){
            throw new YTBizException(YTResponseEnum.TOCKENFAIL.getCode(),"登录用户信息获取失败，请重新登录");
        }
        YTSafetyValidateScenePost scenePostDO = new YTSafetyValidateScenePost();

        if(param.getDataType() == null){
            throw new YTBizException(YTResponseEnum.EXCEPTION.getCode(),"入参“dataType”不能为空！");
        }

        BeanUtils.copyProperties(param,scenePostDO);
        scenePostDO.setUpdateTime(new Date());
        scenePostDO.setUpdateEmpNo(workNo);
        scenePostDO.setUpdateEmp(realName);
        if(StringUtils.isNotEmpty(scenePostDO.getReformDesc())){
            scenePostDO.setDealTime(new Date());
        }
        if(scenePostDO.getId() == null){
            scenePostDO.setId(IdWorker.getId());
            scenePostDO.setCreateTime(new Date());
            row = safetyValidateScenePostDOMapper.insert(scenePostDO);
        }else{
            row = safetyValidateScenePostDOMapper.updateByPrimaryKeySelective(scenePostDO);
        }
        return scenePostDO;
    }

    /**
     * 获取列表
     * @param param 入参
     * @param pageFlag 是否分页 0-否 1-是
     * @return 返回值
     */
    public List<YTSafetyValidateScenePost> selectList(ValidateScenePostListParam param, boolean pageFlag) {
        //是否分页
        if(pageFlag){
            param.setPageFlag(pageFlag);
        }
        //添加默认字段排序
        if(StringUtils.isEmpty(param.getOrderByClause())){
            param.setOrderByClause(" create_time desc ");
        }
        List<YTSafetyValidateScenePost> scenePostDOS = safetyValidateScenePostDOMapper.selectList(param);
        return scenePostDOS;
    }

    /**
     * 根据id获取名称值
     * @param factoryMap
     * @return
     */
    public String getTeamNames(String teamIds, Map<Long, SafetyFieldManage> factoryMap) {
        Set<String> ids = Sets.newHashSet(teamIds.split(","));
        StringBuilder factoryName = new StringBuilder();
        for (String s : ids) {
            if (factoryMap.containsKey(Long.valueOf(s.trim()))) {
                SafetyFieldManage fieldManage = factoryMap.get(Long.valueOf(s.trim()));
                if(!ObjectUtils.isEmpty(fieldManage)){
                    factoryName.append(fieldManage.getFieldName()).append(",");
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
     * MyBatis-Plus 分页查询
     * @param page 分页对象
     * @param param 查询入参
     * @return IPage 分页结果
     */
    public IPage<YTSafetyValidateScenePost> pageList(Page<YTSafetyValidateScenePost> page, ValidateScenePostListParam param) {

        //添加默认字段排序
        if(StringUtils.isEmpty(param.getOrderByClause())){
            param.setOrderByClause(" create_time desc ");
        }
        List<YTSafetyValidateScenePost> scenePostDOS = safetyValidateScenePostDOMapper.selectList(param);
        long count = safetyValidateScenePostDOMapper.countByExample(param);
//        if (!CollectionUtils.isEmpty(scenePostDOS)) {
//            List<SafetyFieldManage> fieldManageList = safetyFieldManageService.lambdaQuery()
//                    .in(SafetyFieldManage::getFieldCode, Arrays.asList(FILED_TYPE_CODE.split(",")))
//                    .eq(SafetyFieldManage::getDeleteFalg, 0)
//                    .list();
//            Map<Long, SafetyFieldManage> filedMap = fieldManageList.stream()
//                    .collect(Collectors.toMap(SafetyFieldManage::getId, Function.identity()));
//            for (YTSafetyValidateScenePost scenePostDO : scenePostDOS) {
//                scenePostDO.setTeamName(StringUtils.isNotEmpty(scenePostDO.getTeamId()) ? getTeamNames(scenePostDO.getTeamId(), filedMap) : "");
//            }
//        }
        page.setTotal(count);
        page.setRecords(scenePostDOS);
        return page;
    }

    /**
     * 参数整理
     * @param param 入参
     * @return 返回值
     */
    private YTSafetyValidateScenePostExample formatCriteria(ValidateScenePostListParam param) {
        YTSafetyValidateScenePostExample example = new YTSafetyValidateScenePostExample();
        YTSafetyValidateScenePostExample.Criteria criteria = example.createCriteria();
        criteria.andDataTypeEqualTo(param.getDataType());
        if (param.getFactoryId() != null) {
            criteria.andFactoryIdEqualTo(param.getFactoryId().longValue());
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
            String checkEnd = YTDateUtils.format(YTDateUtils.addDays(YTDateUtils.parse(param.getValidateEnd(), 1), 1), 1);
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
            Date newBegin = YTDateUtils.parse(param.getCreateBegin(),1);
            Date newEnd = YTDateUtils.addDays(YTDateUtils.parse(param.getCreateEnd(), 1), 1);
            criteria.andCreateTimeBetween(newBegin, newEnd);
        }
        if(StringUtils.isNotEmpty(param.getDealBegin()) && StringUtils.isNotEmpty(param.getDealEnd())){
            Date newBegin = YTDateUtils.parse(param.getDealBegin(),1);
            Date newEnd = YTDateUtils.addDays(YTDateUtils.parse(param.getDealEnd(), 1), 1);
            criteria.andDealTimeBetween(newBegin,newEnd);
        }
        //工厂权限
        String factoryIdStr = param.getFactoryIds();
        if(StringUtils.isNotEmpty(factoryIdStr)){
            String[] factoryArr = factoryIdStr.split(",");
            List<Long> factoryList = Arrays.stream(factoryArr).map(Long::valueOf).collect(Collectors.toList());
            criteria.andFactoryIdIn(factoryList);
        }

        return example;
    }

    public YTSafetyValidateScenePost selectByParimaryKey(Long id){

        YTSafetyValidateScenePost scenePostDO = safetyValidateScenePostDOMapper.selectByPrimaryKey(id);
        if(scenePostDO == null){
            return scenePostDO;
        }
        return scenePostDO;
    }
}
