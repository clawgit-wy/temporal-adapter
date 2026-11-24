package com.yutong.safety.core.service.spot;

import com.yutong.safety.common.constants.FieldMoveEnum;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.param.web.spot.PatrolPlanAddParam;
import com.yutong.safety.common.param.web.spot.PatrolPlanListParam;
import com.yutong.safety.common.param.web.spot.PatrolTaskListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.web.spot.PatrolPlanVO;
import com.yutong.safety.core.db.entity.*;
import com.yutong.safety.core.db.entity.spot.*;
import com.yutong.safety.core.db.mapper.SafetyFieldManageDOBizMapper;
import com.yutong.safety.core.db.mapper.SafetyFieldManageDOMapper;
import com.yutong.safety.core.db.mapper.SafetySysUserDOBizMapper;
import com.yutong.safety.core.db.mapper.SafetySysUserRoleDOMapper;
import com.yutong.safety.core.db.mapper.login.LoginMapper;
import com.yutong.safety.core.db.mapper.spot.SafetyPatrolPlanDOBizMapper;
import com.yutong.safety.core.db.mapper.spot.SafetyPatrolPlanDOMapper;
import com.yutong.safety.core.db.mapper.spot.SafetyPatrolPositionValidQuestDOBizMapper;
import com.yutong.safety.core.db.mapper.spot.SafetyPatrolPositionValidQuestDOMapper;
import com.yutong.safety.core.dto.SafetyUserDTO;
import com.yutong.safety.core.dto.spot.SafetyPatrolPositionValidQuestDTO;
import com.yutong.safety.core.service.SafetyLogMessageService;
import com.yutong.safety.core.service.SafetySysUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gwxiexj
 * @since 2024-05-22 16:45
 */
@Service
public class SafetyPatrolPlanService {

    @Autowired
    private SafetyPatrolPlanDOMapper safetyPatrolPlanDOMapper;

    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    @Autowired
    private SafetySysUserService safetySysUserService;

    @Autowired
    private SafetyPatrolPlanDOBizMapper safetyPatrolPlanDOBizMapper;

    @Autowired
    private SafetyFieldManageDOBizMapper safetyFieldManageDOBizMapper;

    @Autowired
    private SafetyFieldManageDOMapper safetyFieldManageDOMapper;

    @Autowired
    private SafetyPatrolPositionValidQuestDOMapper safetyPatrolPositionValidQuestDOMapper;

    @Autowired
    private SafetyPatrolPositionValidQuestDOBizMapper safetyPatrolPositionValidQuestDOBizMapper;

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    SafetySysUserRoleDOMapper safetySysUserRoleDOMapper;

    @Autowired
    SafetySysUserDOBizMapper safetySysUserDOBizMapper;

    /**
     * 保存更新
     * @param param 入参
     * @param token  用户token
     * @return 返回值
     */
    public Object saveOrUpdate(PatrolPlanAddParam param, String token) {
        int i = 0;
        SafetyPatrolPlanDO planDO = new SafetyPatrolPlanDO();
        BeanUtils.copyProperties(param,planDO);
        String realName = TokenUtils.getRealName(token);
        String workNo = TokenUtils.getWorkNo(token);
        planDO.setUpdateEmp(realName);
        planDO.setUpdateEmpNo(workNo);
        planDO.setUpdateTime(new Date());
        if(planDO.getStatus() == null){
            planDO.setStatus(0);
        }

        //当是岗位验证的时候，特殊处理
        if(!StringUtils.isEmpty(param.getGwRwlx()) && "1".equals(param.getGwRwlx())){
            SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
            SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
            criteria.andFieldCodeEqualTo("inspect_003");
            criteria.andDeleteFalgEqualTo(false);
            List<SafetyFieldManageDO> safetyFieldManageDOS = safetyFieldManageDOMapper.selectByExample(example);
            if(CollectionUtils.isNotEmpty(safetyFieldManageDOS)){
                planDO.setTypeId(safetyFieldManageDOS.get(0).getId());
                planDO.setTypeName(safetyFieldManageDOS.get(0).getFieldName());
            }
        }

        //判断没有typeId时，自己赋值
        Integer typeId = param.getTypeId();
        if(typeId == null){
            SafetyFieldManageDO parentFieldDO = safetyFieldManageDOBizMapper.getParentFieldById(param.getTeamId());
            if(parentFieldDO != null){
                planDO.setTypeId(parentFieldDO.getId());
                planDO.setTypeName(parentFieldDO.getFieldName());
            }
        }
        if(param.getId() ==null){
            planDO.setCreateTime(new Date());
            i = safetyPatrolPlanDOMapper.insert(planDO);
        }else{
            i = safetyPatrolPlanDOMapper.updateByPrimaryKeySelective(planDO);
            safetyPatrolPlanDOBizMapper.deleteByPlanId(planDO.getId());
        }
        //添加关系
        String ids = param.getFactoryId();
        List<Map<String,Object>> linkList = new ArrayList<>();
        for(String s:ids.split(",")){
            Map<String,Object> curMap = new HashMap<>();
            curMap.put("planId",planDO.getId());
            curMap.put("factoryId",Integer.parseInt(s));
            linkList.add(curMap);
        }
        safetyPatrolPlanDOBizMapper.batchInsert(linkList);

        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("治安巡检-任务设置");
        log.setAbility("更新");
        log.setMessage(param.getPlanName());
        log.setOperator(realName);
        safetyLogMessageService.addLogMessage(log);
        return i;
    }

    /**
     * 启用禁用
     *
     * @param planId 规则主键
     * @return 返回
     */
    public int modifyStatus(List<Integer> planId,Integer status,String token) throws Exception{
        SafetyPatrolPlanDO planDO = new SafetyPatrolPlanDO();
        planDO.setStatus(status);
        SafetyPatrolPlanDOExample example = new SafetyPatrolPlanDOExample();
        SafetyPatrolPlanDOExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(planId);
        List<SafetyPatrolPlanDO> list = safetyPatrolPlanDOMapper.selectByExample(example);
        String changeMsg = list.stream().map(a -> a.getPlanName()).collect(Collectors.joining(","));
        int cont = safetyPatrolPlanDOMapper.updateByExampleSelective(planDO,example);
        //操作日志记录
        String statusDes = FieldMoveEnum.UP.getType() == status?"启用":"禁用";
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("治安巡检-任务设置");
        log.setAbility(statusDes);
        String realName = TokenUtils.getRealName(token);
        log.setMessage("“"+changeMsg+"”设置为“"+statusDes+"”");
        log.setOperator(realName);
        safetyLogMessageService.addLogMessage(log);
        return cont;
    }

    /**
     * 分页查询
     *
     * @param param 查询入参
     * @param token 用户登录token
     * @param param 是否过滤工厂
     * @return 返回
     */
    public List<PatrolPlanVO> pageList(PatrolPlanListParam param, String token, boolean filterFlag) {
        //1.根据token中的员工号获取工厂权限，过滤厂区权限
        if(filterFlag){
            //获取登录用户授权工厂
            String workNo = TokenUtils.getWorkNo(token);
            String idStr = safetySysUserService.getPowerFactory(workNo);
            param.setIdStr(idStr);
        }
        param.setBegin(param.getLimit());
        return safetyPatrolPlanDOBizMapper.selectList(param);
    }

    /**
     * 分页总条数
     *
     * @param param 查询入参
     * @param token 用户登录token
     * @param param 是否过滤工厂
     * @return 返回
     */
    public long pageListCount(PatrolPlanListParam param,String token,boolean filterFlag) {
        param.setBegin(null);
        //1.根据token中的员工号获取工厂权限，过滤厂区权限
        if(filterFlag){
            //获取登录用户授权工厂
            String workNo = TokenUtils.getWorkNo(token);
            String idStr = safetySysUserService.getPowerFactory(workNo);
            param.setIdStr(idStr);
        }
        return safetyPatrolPlanDOBizMapper.selectCount(param);
    }

    /**
     * 查询-不分页
     *
     * @param param 查询入参
     * @param token 用户登录token
     * @param param 是否过滤工厂
     * @return 返回
     */
    public List<PatrolPlanVO> list(PatrolPlanListParam param, String token, boolean filterFlag) {
        //1.根据token中的员工号获取工厂权限，过滤厂区权限
        if(filterFlag){
            //获取登录用户授权工厂
            String workNo = TokenUtils.getWorkNo(token);
            String idStr = safetySysUserService.getPowerFactory(workNo);
            param.setIdStr(idStr);
        }
        param.setBegin(null);
        return safetyPatrolPlanDOBizMapper.selectList(param);
    }

    /**
     * 批量设置休眠时间
     * @param param 入参
     * @param token token
     * @return 返回值
     * @throws Exception
     */
    public int modifyDormancy(PatrolPlanAddParam param, String token) throws Exception{
        SafetyPatrolPlanDOExample example = new SafetyPatrolPlanDOExample();
        SafetyPatrolPlanDOExample.Criteria criteria = example.createCriteria();
        String ids = param.getIdStr();
        String[] idArr = ids.split(",");
        List<Integer> idList =  Arrays.stream(idArr).map(Integer::valueOf).collect(Collectors.toList());
        criteria.andIdIn(idList);

        SafetyPatrolPlanDO planDO = new SafetyPatrolPlanDO();
        planDO.setDormancyBegin(param.getDormancyBegin());
        planDO.setDormancyEnd(param.getDormancyEnd());

        List<SafetyPatrolPlanDO> list = safetyPatrolPlanDOMapper.selectByExample(example);
        String changeMsg = list.stream().map(a -> a.getPlanName()).collect(Collectors.joining(","));

        int rows = safetyPatrolPlanDOMapper.updateByExampleSelective(planDO,example);
        // TODO: 2024/5/22 修改现有任务的截止日期
        //修改逻辑

        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("治安巡检-任务设置");
        log.setAbility("批量休眠");
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        log.setMessage("设置为“"+changeMsg+"”的休眠时间为："+param.getDormancyBegin()+","+param.getDormancyEnd());
        log.setOperator(tokenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);

        return rows;
    }

    /**
     *
     * @param param 入参
     * @return
     */
    public List<PatrolPlanVO> selectList(PatrolPlanListParam param){
        return safetyPatrolPlanDOBizMapper.selectList(param);
    }

    /**
     *
     * @param param 入参
     * @return
     */
    public List<SafetyPatrolTaskDO> selectMaxTask(PatrolTaskListParam param){
        return safetyPatrolPlanDOBizMapper.selectMaxTask(param);
    }

    /**
     * 批量增加task
     * @param list
     * @return
     */
    public int batchInsertTask(List<SafetyPatrolTaskDO> list){
        return safetyPatrolPlanDOBizMapper.batchInsertTask(list);
    }

    public SafetyPatrolPlanDO getBeanById(Integer planId){
        return safetyPatrolPlanDOMapper.selectByPrimaryKey(planId);
    }

    public List<SafetyPatrolPlanDO> selectPlanListByEmpNo(String workNo){
        if(StringUtils.isBlank(workNo)){
            return new ArrayList<>();
        }
        List<Integer> roleIdList = loginMapper.getRoleIdList(workNo);
        if(CollectionUtils.isEmpty(roleIdList)){
            roleIdList.add(0);
        }
        SafetyPatrolPlanDOExample example = new SafetyPatrolPlanDOExample();
        example.createCriteria().andCycleRoleIdIn(roleIdList).andStatusEqualTo(0);

        return safetyPatrolPlanDOMapper.selectByExample(example);
    }

    /**
     * 获取指定角色用户
     * @param params
     * @return
     */
    public String selectRoleUser(Map<String,String> params){
        return safetyPatrolPlanDOBizMapper.selectRoleUser(params);
    }

    public List<PatrolPlanVO> getInUsePositionPlan(Map<String, Object> paramMap){
        return safetyPatrolPlanDOBizMapper.getInUsePositionPlan(paramMap);
    }

    public List<SafetyPatrolPositionValidQuestDO> getQuestList(Map<String, Object> paramMap){
        return safetyPatrolPositionValidQuestDOBizMapper.getQuestList(paramMap);
    }

    public List<SafetyPatrolPositionValidQuestDTO> getNoMesList(){
        SafetyPatrolPositionValidQuestDTO en = new SafetyPatrolPositionValidQuestDTO();
        en.setBeginDate(DateUtils.parse(DateUtils.format(new Date(),1),1));
        en.setEndDate(DateUtils.parse(DateUtils.format(new Date(),1),1));

        return safetyPatrolPositionValidQuestDOBizMapper.getTaskList(en);
    }

    public Map<Integer,List<String>> getUserRoleList(){
        Map<Integer, List<String>> resultMap = new HashMap<>();
        List<SafetyUserDTO> list = safetySysUserDOBizMapper.getAllUserInfo();

        if (!CollectionUtils.isEmpty(list)) {
            resultMap = list.stream().collect(Collectors.groupingBy(SafetyUserDTO::getRoleId,
                    Collectors.mapping(SafetyUserDTO::getUserCode, Collectors.toList())));
        }

        return resultMap;
    }
}
