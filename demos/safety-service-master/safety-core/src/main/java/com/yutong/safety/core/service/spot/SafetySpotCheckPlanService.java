package com.yutong.safety.core.service.spot;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.FieldMoveEnum;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.param.web.spot.SpotCheckPlanDormancyParam;
import com.yutong.safety.common.param.web.spot.SpotCheckPlanListParam;
import com.yutong.safety.common.param.web.spot.SpotCheckPlanSaveParam;
import com.yutong.safety.common.param.web.spot.SpotCheckTaskListParam;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.web.SpotCheckPlanVO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckPlanDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckPlanDOExample;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckTaskDO;
import com.yutong.safety.core.db.mapper.spot.SafetySpotCheckPlanDOBizMapper;
import com.yutong.safety.core.db.mapper.spot.SafetySpotCheckTaskDOBizMapper;
import com.yutong.safety.core.service.SafetyLogMessageService;
import com.yutong.safety.core.service.SafetySysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gwxiexj
 * @since 2024-05-07
 */
@Service
public class SafetySpotCheckPlanService {
    @Autowired
    private SafetySpotCheckPlanDOBizMapper safetySpotCheckPlanDOBizMapper;

    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    @Autowired
    private SafetySysUserService safetySysUserService;

    @Autowired
    private SafetySpotCheckTaskDOBizMapper safetySpotCheckTaskDOBizMapper;

    /**
     * 保存更新
     * @param param 入参
     * @param token  用户token
     * @return 返回值
     */
    public Object saveOrUpdate(SpotCheckPlanSaveParam param, String token) {
        int i = 0;
        SafetySpotCheckPlanDO planDO = new SafetySpotCheckPlanDO();
        BeanUtils.copyProperties(param,planDO);
        String realName = TokenUtils.getRealName(token);
        String workNo = TokenUtils.getWorkNo(token);
        planDO.setUpdatePerson(realName);
        planDO.setUpdatePersonNo(workNo);
        planDO.setUpdateTime(new Date());
        //默认禁用
        if(planDO.getStatus() == null){
            planDO.setStatus(1);
        }
        if(param.getId() ==null){
            planDO.setCreateTime(new Date());
            i = safetySpotCheckPlanDOBizMapper.insert(planDO);
        }else{
            i = safetySpotCheckPlanDOBizMapper.updateByPrimaryKeySelective(planDO);
            //删除关系
            safetySpotCheckPlanDOBizMapper.deleteByPlanId(planDO.getId());
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
        safetySpotCheckPlanDOBizMapper.batchInsert(linkList);

        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("设备设施点检-任务设置");
        log.setAbility("更新");
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        log.setMessage(param.getPlanName());
        log.setOperator(tockenUser.get("realName"));
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
        SafetySpotCheckPlanDO planDO = new SafetySpotCheckPlanDO();
        planDO.setStatus(status);
        planDO.setUpdateTime(new Date());
        SafetySpotCheckPlanDOExample example = new SafetySpotCheckPlanDOExample();
        SafetySpotCheckPlanDOExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(planId);
        List<SafetySpotCheckPlanDO> list = safetySpotCheckPlanDOBizMapper.selectByExample(example);
        String changeMsg = list.stream().map(a -> a.getPlanName()).collect(Collectors.joining(","));
        int cont = safetySpotCheckPlanDOBizMapper.updateByExampleSelective(planDO,example);
        //操作日志记录
        String statusDes = FieldMoveEnum.UP.getType() == status?"启用":"禁用";
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("设备设施点检-任务设置");
        log.setAbility(statusDes);
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        log.setMessage("“"+changeMsg+"”设置为“"+statusDes+"”");
        log.setOperator(tockenUser.get("realName"));
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
    public List<SpotCheckPlanVO> pageList(SpotCheckPlanListParam param,String token,boolean filterFlag) {
        //1.根据token中的员工号获取工厂权限，过滤厂区权限
        if(filterFlag){
            //获取登录用户授权工厂
            String workNo = TokenUtils.getWorkNo(token);
            String idStr = safetySysUserService.getPowerFactory(workNo);
            param.setIdStr(idStr);
        }
        param.setBegin(param.getLimit());
        return safetySpotCheckPlanDOBizMapper.selectList(param);
    }

    /**
     * 分页总条数
     *
     * @param param 查询入参
     * @param token 用户登录token
     * @param param 是否过滤工厂
     * @return 返回
     */
    public long pageListCount(SpotCheckPlanListParam param,String token,boolean filterFlag) {
        param.setBegin(null);
        //1.根据token中的员工号获取工厂权限，过滤厂区权限
        if(filterFlag){
            //获取登录用户授权工厂
            String workNo = TokenUtils.getWorkNo(token);
            String idStr = safetySysUserService.getPowerFactory(workNo);
            param.setIdStr(idStr);
        }
        return safetySpotCheckPlanDOBizMapper.selectCount(param);
    }

    /**
     * 查询-不分页
     *
     * @param param 查询入参
     * @param token 用户登录token
     * @param param 是否过滤工厂
     * @return 返回
     */
    public List<SpotCheckPlanVO> list(SpotCheckPlanListParam param, String token, boolean filterFlag) {
        //1.根据token中的员工号获取工厂权限，过滤厂区权限
        if(filterFlag){
            //获取登录用户授权工厂
            String workNo = TokenUtils.getWorkNo(token);
            String idStr = safetySysUserService.getPowerFactory(workNo);
            param.setIdStr(idStr);
        }
        param.setBegin(null);
        return safetySpotCheckPlanDOBizMapper.selectList(param);
    }

    /**
     * 批量设置休眠时间
     * @param param
     * @param token
     * @return
     */
    public int modifyDormancy(SpotCheckPlanDormancyParam param, String token) {
        SafetySpotCheckPlanDOExample example = new SafetySpotCheckPlanDOExample();
        SafetySpotCheckPlanDOExample.Criteria criteria = example.createCriteria();
        String ids = param.getIds();
        String[] idArr = ids.split(",");
        List<Integer> idList =  Arrays.stream(idArr).map(Integer::valueOf).collect(Collectors.toList());
        criteria.andIdIn(idList);

        SafetySpotCheckPlanDO planDO = new SafetySpotCheckPlanDO();
        planDO.setDormancyBegin(param.getDormancyBegin());
        planDO.setDormancyEnd(param.getDormancyEnd());

        List<SafetySpotCheckPlanDO> list = safetySpotCheckPlanDOBizMapper.selectByExample(example);
        String changeMsg = list.stream().map(a -> a.getPlanName()).collect(Collectors.joining(","));

        int rows = safetySpotCheckPlanDOBizMapper.updateByExampleSelective(planDO,example);

        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("设备设施点检-任务设置");
        log.setAbility("批量休眠");
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        log.setMessage("设置为“"+changeMsg+"”的休眠时间为："+param.getDormancyBegin()+","+param.getDormancyEnd());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);

        return rows;
    }

    /**
     *
     * @param param
     * @return
     */
    public List<SpotCheckPlanVO> selectList(SpotCheckPlanListParam param){
        return safetySpotCheckPlanDOBizMapper.selectList(param);
    }

    /**
     * 获取最新周期任务列表
     * @param param
     * @return
     */
    public List<SafetySpotCheckTaskDO> selectMaxTask(SpotCheckTaskListParam param){
        return safetySpotCheckTaskDOBizMapper.selectMaxTask(param);
    }

    /**
     * 获取指定开始日期的周期任务列表
     * @param param
     * @return
     */
    public List<SafetySpotCheckTaskDO> getCurrentTask(SpotCheckTaskListParam param){
        return safetySpotCheckTaskDOBizMapper.getCurrentTask(param);
    }

    /**
     * 查询列表
     * @param planIdStr 字段ID集合
     * @return 返回值
     */
    public List<SafetySpotCheckPlanDO> selectExample(String planIdStr){
        SafetySpotCheckPlanDOExample example = new SafetySpotCheckPlanDOExample();
        SafetySpotCheckPlanDOExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(planIdStr)){
            String[] planIdArr = planIdStr.split(",");
            List<Integer> planIdList =  Arrays.stream(planIdArr).map(Integer::valueOf).collect(Collectors.toList());
            criteria.andIdIn(planIdList);
        }

        return safetySpotCheckPlanDOBizMapper.selectByExample(example);
    }
}
