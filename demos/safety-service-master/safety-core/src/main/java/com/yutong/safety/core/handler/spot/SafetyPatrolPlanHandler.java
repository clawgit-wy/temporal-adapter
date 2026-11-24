package com.yutong.safety.core.handler.spot;

import com.google.common.base.Joiner;
import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.FieldMoveEnum;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.spot.PatrolPlanAddParam;
import com.yutong.safety.common.param.web.spot.PatrolPlanListParam;
import com.yutong.safety.common.param.web.spot.PatrolTaskListParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.sys.SysUserVO;
import com.yutong.safety.common.vo.web.spot.PatrolPlanVO;
import com.yutong.safety.core.db.entity.SafetyFieldManageDO;
import com.yutong.safety.core.db.entity.spot.SafetyPatrolPositionValidQuestDO;
import com.yutong.safety.core.db.entity.spot.SafetyPatrolTaskDO;
import com.yutong.safety.core.db.mapper.SafetySysUserDOBizMapper;
import com.yutong.safety.core.db.mapper.spot.SafetyPatrolPositionValidQuestDOBizMapper;
import com.yutong.safety.core.dto.spot.SafetyPatrolPositionValidQuestDTO;
import com.yutong.safety.core.service.SafetyFieldManageService;
import com.yutong.safety.core.service.spot.SafetyPatrolPlanService;
import com.yutong.safety.core.service.spot.SafetyPatrolTaskService;
import lombok.val;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 治安巡查-任务设置
 * @author gwxiexj
 * @since 2024-05-22 16:48
 */
@Component
public class SafetyPatrolPlanHandler {
    //安保小助手机器人ID
    @Value("${iam.retention.robotId}")
    private String incomintRebotId;

    @Autowired
    private SafetyPatrolPlanService safetyPatrolPlanService;

    @Autowired
    private SafetyPatrolTaskService safetyPatrolTaskService;

    @Autowired
    private SafetyPatrolPositionValidQuestDOBizMapper safetyPatrolPositionValidQuestDOBizMapper;

    @Autowired
    private SafetySysUserDOBizMapper safetySysUserDOBizMapper;

    @Autowired
    private YxtRebotUtils yxtRebotUtils;

    @Autowired
    private MailCenterUtils mailCenterUtils;
    
    @Autowired
    private SafetyFieldManageService safetyFieldManageService;

    @Value("${ZAXC.JKGW}")
    private String JKGW_URL;

    @Value("${ZAXC.XCGW}")
    private String XCGW_URL;

    @Value("${ZAXC.MNGW}")
    private String MNGW_URL;

    /**
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    public Object pageList(PatrolPlanListParam param, String token) {
        List<PatrolPlanVO> list = safetyPatrolPlanService.pageList(param,token,true);
        long total = safetyPatrolPlanService.pageListCount(param,token,true);
        return new PageVO<>(total, list);
    }

    /**
     * 数据导出操作
     * @param param 入参
     * @param token token
     * @return 返回值
     */
    public HSSFWorkbook export(PatrolPlanListParam param, String token) {
        List<PatrolPlanVO> list = safetyPatrolPlanService.list(param,token,true);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"任务名称", "应用厂区", "应用角色", "事件确认角色", "任务周期", "应用项目", "任务开始日期", "任务结束日期","状态", "休眠开始日期","休眠结束日期", "备注"};
        String[][] values = new String[list.size()][];
        PatrolPlanVO patrolPlanVO;
        for(int i=0; i<list.size(); i++) {
            patrolPlanVO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = patrolPlanVO.getPlanName();//任务名称
            values[i][1] = patrolPlanVO.getFactoryName();//应用厂区
            values[i][2] = patrolPlanVO.getCycle()+"天/次";//任务周期
            values[i][3] = String.valueOf(patrolPlanVO.getTeamName());//应用项目
            values[i][4] = String.valueOf(patrolPlanVO.getBeginDate());//任务开始日期
            values[i][5] = String.valueOf(patrolPlanVO.getEndDate());//任务结束日期
            values[i][6] = patrolPlanVO.getStatus()== FieldMoveEnum.UP.getType()?"启用":"禁用"; // 状态0-启用 1-禁用
            values[i][7] = String.valueOf(patrolPlanVO.getDormancyBegin());
            values[i][8] = String.valueOf(patrolPlanVO.getDormancyEnd());
            values[i][9] = patrolPlanVO.getRemark();//备注
        }
        String sheetName = "设备设施点检-任务设置";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 数据添加接口
     * @param param 入参
     * @return 返回
     */
    public Object add(PatrolPlanAddParam param, String token) throws Exception{
        safetyPatrolPlanService.saveOrUpdate(param,token);
        return null;
    }

    /**
     * 批量修改任务的状态
     * @param ids id拼接
     * @param token 用户token
     * @return 返回值
     */
    public Object modifyStatus(String ids,Integer status,String token) throws Exception{
        String[] idArr = ids.split(",");
        List<Integer> idList =  Arrays.stream(idArr).map(Integer::valueOf).collect(Collectors.toList());
        return safetyPatrolPlanService.modifyStatus(idList,status,token);
    }

    /**
     * 批量设置休眠时间
     * @param param 入参
     * @param token 用户token
     * @return 返回值
     * @throws Exception
     */
    public Object modifyDormancy(PatrolPlanAddParam param, String token) throws Exception{
        return safetyPatrolPlanService.modifyDormancy(param,token);

        // TODO: 2024/5/24  2.更新设备设置周期任务的截止时间
        /*PatrolTaskListParam taskParam = new PatrolTaskListParam();
        taskParam.setTaskBeginDate(param.getDormancyBegin());
        taskParam.setTaskEndDate(param.getDormancyEnd());

        safetyPatrolTaskService.modifyDormancy(taskParam,token);*/
    }

    /**
     * 治安巡查任务-定时任务
     * @return 返回值
     */
    public Object taskPatrolCheck() throws Exception{
        //获取T+1
        String beginDate = DateUtils.format(DateUtils.addDays(new Date(),1),1);
        PatrolPlanListParam param = new PatrolPlanListParam();
        param.setBeginDate(beginDate);
        param.setEndDate(beginDate);
        param.setStatus(0);
        param.setTaskFlag(1);
        //1.获取当前时间生效的任务清单
        List<PatrolPlanVO> doList = safetyPatrolPlanService.selectList(param);
        if(doList == null || doList.size() <= 0){
            return null;
        }
        //2.获取现有最新的task清单
        PatrolTaskListParam taskListParam = new PatrolTaskListParam();
        List<SafetyPatrolTaskDO> maxTaskList = safetyPatrolPlanService.selectMaxTask(taskListParam);
        Map<Integer,List<SafetyPatrolTaskDO>> taskMap = new HashMap<>();

        if(maxTaskList != null && maxTaskList.size() > 0){
            taskMap = maxTaskList.stream().collect(Collectors.groupingBy(item -> {return item.getPlanId();}));
        }
        return dealTaskList(taskMap, doList, beginDate);
    }

    /**
     * 根据计划生成代办任务
     * @param taskMap 最新任务列表
     * @param doList 有效的计划列表
     * @param beginDate 下个周期开始日期
     * @return 返回值
     */
    public int dealTaskList(Map<Integer, List<SafetyPatrolTaskDO>> taskMap,
                            List<PatrolPlanVO> doList, String beginDate)
            throws Exception{
        int rows = 0;
        List<SafetyPatrolTaskDO> taskDOList = new ArrayList<>();

        //查询角色与人员的对应map
        Map<Integer, List<String>> userRoleMap = safetyPatrolPlanService.getUserRoleList();

        int initStatus = 0;
        for(PatrolPlanVO planDO:doList){
            String dormancyBegin = planDO.getDormancyBegin();
            String dormancyEnd = planDO.getDormancyEnd();
            Integer planId = planDO.getId();
            String curEndDate = "";
            Map<Integer,List<SafetyPatrolTaskDO>> factoryTaskMap = new HashMap<>();
            List<SafetyPatrolTaskDO> lastTask = taskMap.get(planId);
            if(lastTask != null && lastTask.size() > 0){
                curEndDate = lastTask.get(0).getTaskEndDate();
                factoryTaskMap = lastTask.stream().collect(Collectors.groupingBy(SafetyPatrolTaskDO::getFactoryId));
            }
            //上个周期未结束的任务不用处理
            if(!"".equals(curEndDate) && DateUtils.loseDay(curEndDate,beginDate)<=0){
                continue;
            }
            if(StringUtils.isNotEmpty(dormancyBegin) && StringUtils.isNotEmpty(dormancyEnd)){
                //休眠期内任务不用处理
                if(DateUtils.loseDay(beginDate,dormancyBegin)<= 0 && DateUtils.loseDay(dormancyEnd,beginDate)<=0){
                    continue;
                }
            }
            int cycle = planDO.getCycle();
            String taskEndDate = calculateEndDate(beginDate,cycle,dormancyBegin,dormancyEnd);
            Integer teamId = planDO.getTeamId();

            String factoryIdStr = planDO.getFactoryId();
            String[] factoryIdArr = factoryIdStr.split(",");

            Integer cycleRoleId = planDO.getCycleRoleId();
            if(null == cycleRoleId || (null != cycleRoleId && CollectionUtils.isEmpty(userRoleMap.get(cycleRoleId)))){continue;}
            List<String> userCodeList = userRoleMap.get(cycleRoleId);

            for(val fidStr :factoryIdArr){
                //设置任务期序号
                int curOrder = 1;
                List<SafetyPatrolTaskDO> curList = factoryTaskMap.get(fidStr);
                if(curList != null && curList.size() > 0){
                    SafetyPatrolTaskDO preTask = curList.get(0);
                    if(preTask != null){
                        curOrder = preTask.getCurOrder()+1;
                    }
                }

                //3.保存task任务
                //此处先不管这个人有哪些授权厂区的维度，先假设他有  当前任务所有的授权厂区  的权限，之后查询的时候再做处理
                for (String userCode : userCodeList) {
                    SafetyPatrolTaskDO taskDO = new SafetyPatrolTaskDO();
                    taskDO.setPlanId(planId);
                    taskDO.setPlanName(planDO.getPlanName());
                    taskDO.setTaskBeginDate(beginDate);
                    taskDO.setTaskEndDate(taskEndDate);
                    taskDO.setCreateTime(new Date());
                    taskDO.setTeamId(teamId);
                    taskDO.setTeamName(planDO.getTeamName());
                    taskDO.setTypeId(planDO.getTypeId()!=null?Integer.valueOf(planDO.getTypeId()):null);
                    taskDO.setTypeName(planDO.getTypeName());
                    taskDO.setFactoryId(Integer.valueOf(fidStr));
                    taskDO.setStatus(initStatus);
                    taskDO.setCurOrder(curOrder);  //设置任务期序号
                    taskDO.setUserCode(userCode);

                    taskDOList.add(taskDO);
                }
            }
        }

        if(taskDOList.size() >0 ){
           rows = safetyPatrolPlanService.batchInsertTask(taskDOList);
        }
        return rows;
    }


    /**
     * 根据休眠日期推算 任务截止日期
     *
     * @param beginDate 开始日期
     * @param cycle 周期
     * @param dormancyBegin 休眠开始日期
     * @param dormancyEnd 休眠结束日期
     * @return 返回值
     */
    private String calculateEndDate(String beginDate,int cycle,String dormancyBegin,String dormancyEnd){
        int diffDay = cycle;
        if(diffDay > 0){
            diffDay = diffDay - 1;
        }
        if(StringUtils.isNotEmpty(dormancyBegin) && StringUtils.isNotEmpty(dormancyEnd)){
            if((DateUtils.loseDay(dormancyBegin,beginDate)>0 || beginDate.equals(dormancyBegin))
                    && DateUtils.loseDay(beginDate,dormancyEnd) > 0){
                diffDay = cycle+Integer.parseInt(DateUtils.loseDay(beginDate,dormancyEnd)+"");
            }
        }
        return DateUtils.format(DateUtils.addDays(DateUtils.parse(beginDate,1),diffDay),1);
    }

    /**
     * 定时生成岗位验证任务
     */
    public void taskPositionEdit(){
        //查询生效的计划
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("beginDate",DateUtils.format(new Date(),1));
        paramMap.put("endDate",DateUtils.format(new Date(),1));
        List<PatrolPlanVO> planList = safetyPatrolPlanService.getInUsePositionPlan(paramMap);

        //查询现有的任务
        List<SafetyPatrolPositionValidQuestDO> taskList = safetyPatrolPlanService.getQuestList(paramMap);
        // 使用流将list转换为map，重复的plantId会覆盖旧值
        Map<Integer, SafetyPatrolPositionValidQuestDO> taskMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(taskList)){
            taskMap = taskList.stream()
                    .collect(Collectors.toMap(SafetyPatrolPositionValidQuestDO::getPlantId,
                            item -> item,
                            (existingItem, newItem) -> newItem));
        }


        List<SafetyPatrolPositionValidQuestDO> list = new ArrayList<>();

        //遍历生成任务
        for(PatrolPlanVO en:planList){
            Integer plantId = en.getId();

            if(!ObjectUtils.isEmpty(taskMap) && taskMap.containsKey(plantId)){
                //往后生成2周期的任务
                SafetyPatrolPositionValidQuestDO task = taskMap.get(plantId);
                createTwoTask(task,en,list);
            }else{
                //一次性生成3周期的任务，任务开始时间当前时间往前推一天
                createThreeTask(en,list);
            }
        }

        //批量保存
        if(!CollectionUtils.isEmpty(list)){
            safetyPatrolPositionValidQuestDOBizMapper.insertSelectiveBiz(list);
        }
    }

    /**
     * 往后生成2批次的任务
     * @param task
     * @param en
     * @param list
     */
    public void createTwoTask(SafetyPatrolPositionValidQuestDO task, PatrolPlanVO en, List<SafetyPatrolPositionValidQuestDO> list) {
        //获取最新任务的结束时间
        Date endDate = task.getEndDate();
        Date beginDate = task.getBeginDate();
        Date now = DateUtils.parse(DateUtils.format(new Date(), 1), 1);

        //只有当前时间处于最后一个批次的时候才会生成新的任务
        if(!(now.compareTo(beginDate)>=0 && now.compareTo(endDate)<=0)){return;}

        //周期
        Integer cycle = en.getCycle();
        //休眠日期
        String dormancyBegin = en.getDormancyBegin();
        String dormancyEnd = en.getDormancyEnd();

        for (int i = 0; i < 2; i++) {
            int beginAdd = 1 + cycle * i;
            int endAdd = cycle * (i + 1);
            //计算第i批次的开始和结束时间
            Date calBeginDate = DateUtils.addDays(endDate, beginAdd);
            Date calEndDate = DateUtils.addDays(endDate, endAdd);
            if (StringUtils.isNotEmpty(dormancyBegin)
                && StringUtils.isNotEmpty(dormancyEnd)
                && !isWithinDormancyPeriod(calBeginDate, calEndDate, DateUtils.parse(dormancyBegin, 1), DateUtils.parse(dormancyEnd, 1))) {
                break;
            }

            SafetyPatrolPositionValidQuestDO entity = new SafetyPatrolPositionValidQuestDO();
            entity.setPlantId(en.getId());
            entity.setRoleId(en.getCycleRoleId());
            entity.setRoleName(en.getCycleRoleName());
            entity.setFactoryId(en.getFactoryId());
            entity.setBeginDate(calBeginDate);
            entity.setEndDate(calEndDate);
            entity.setCreateTime(new Date());
            entity.setTeamId(en.getTeamId());
            entity.setTeamName(en.getTeamName());
            entity.setHasMes(0);
            entity.setPlanName(en.getPlanName());
            entity.setEventType(en.getTypeName());
            entity.setCycle(en.getCycle());
            entity.setEventTypeId(en.getTypeId());
            list.add(entity);
        }
    }

    /**
     * 往后生成3批次的任务
     * @param en
     * @param list
     */
    public void createThreeTask(PatrolPlanVO en,List<SafetyPatrolPositionValidQuestDO> list){
        //周期
        Integer cycle = en.getCycle();
        //休眠日期
        String dormancyBegin = en.getDormancyBegin();
        String dormancyEnd = en.getDormancyEnd();
        //开始时间
        Date beginDate = new Date();
        if(DateUtils.loseDay(DateUtils.format(new Date(),1),en.getBeginDate()) > 0){
            beginDate = DateUtils.parse(en.getBeginDate(),1);
        }

        for (int i = 0; i < 3; i++) {
            int beginAdd = 1 + cycle * i;
            int endAdd = cycle * (i + 1);
            //计算第i批次的开始和结束时间
            Date calBeginDate = DateUtils.addDays(beginDate, beginAdd);
            Date calEndDate = DateUtils.addDays(beginDate, endAdd);
            if (StringUtils.isNotEmpty(dormancyBegin)
                && StringUtils.isNotEmpty(dormancyEnd)
                && !isWithinDormancyPeriod(calBeginDate, calEndDate, DateUtils.parse(dormancyBegin, 1), DateUtils.parse(dormancyEnd, 1))) {
                break;
            }

            SafetyPatrolPositionValidQuestDO entity = new SafetyPatrolPositionValidQuestDO();
            entity.setPlantId(en.getId());
            entity.setRoleId(en.getCycleRoleId());
            entity.setRoleName(en.getCycleRoleName());
            entity.setFactoryId(en.getFactoryId());
            entity.setBeginDate(calBeginDate);
            entity.setEndDate(calEndDate);
            entity.setCreateTime(new Date());
            entity.setTeamId(en.getTeamId());
            entity.setTeamName(en.getTeamName());
            entity.setHasMes(0);
            entity.setPlanName(en.getPlanName());
            entity.setEventType(en.getTypeName());
            entity.setCycle(en.getCycle());
            entity.setEventTypeId(en.getTypeId());
            list.add(entity);
        }
    }

    /**
     * 判断是否处于休眠
     * @param beginDate
     * @param endDate
     * @param dormancyStart
     * @param dormancyEnd
     * @return
     */
    private static boolean isWithinDormancyPeriod(Date beginDate, Date endDate, Date dormancyStart, Date dormancyEnd) {
        return beginDate.after(dormancyEnd) && endDate.before(dormancyStart);
    }

    /**
     * 定时生成岗位验证通知
     */
    public void taskPositionMes(){
        List<SafetyPatrolPositionValidQuestDO> taskLists = new ArrayList<>();
        //获取当前批次还未通知过的生效的任务
        List<SafetyPatrolPositionValidQuestDTO> taskList = safetyPatrolPlanService.getNoMesList();

        //查询各个角色对应的人员信息
        List<SysUserVO> allUserRole = safetySysUserDOBizMapper.getAllUserRole();
        if(CollectionUtils.isEmpty(allUserRole)){return;}
        Map<String, List<SysUserVO>> roleUserMap = allUserRole.stream()
                .collect(Collectors.groupingBy(SysUserVO::getRoleId));
        
        //查询岗位上报的编码
        List<SafetyFieldManageDO> inspect003 = safetyFieldManageService.getInfoByFieldType("inspect_003");
        Map<Integer, SafetyFieldManageDO> codeMap = inspect003.stream()
                .collect(Collectors.toMap(SafetyFieldManageDO::getId,
                        item -> item,
                        (existingItem, newItem) -> newItem));

        //发宇信通和邮件
        if (CollectionUtils.isEmpty(taskList)){return;}
        for (SafetyPatrolPositionValidQuestDTO task : taskList) {
            task.setHasMes(1);

            SafetyPatrolPositionValidQuestDO en = new SafetyPatrolPositionValidQuestDO();
            BeanUtils.copyProperties(task, en);
            taskLists.add(en);

            String roleId = task.getRoleId()+"";
            if(CollectionUtils.isEmpty(roleUserMap.get(roleId))){continue;}
            List<SysUserVO> sysUserVOS = roleUserMap.get(roleId);
            if (CollectionUtils.isEmpty(sysUserVOS)){continue;}

            //根据工厂进行过滤
            String factoryId = task.getFactoryId();
            List<SysUserVO> sysUserList = sysUserVOS.stream().
                    filter(e -> e != null && StringUtils.isNotEmpty(e.getPowerFactoryId()) && e.getPowerFactoryId().contains(factoryId))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(sysUserList)){continue;}

            //拼消息体
            if(StringUtils.isEmpty(task.getEventTypeId())){continue;}
            String sendm = "您有一条任务待处理——"+task.getTeamName()+"-"+task.getPlanName()+"，任务周期："
                    +DateUtils.format(task.getBeginDate(),1) + "-" + DateUtils.format(task.getEndDate(),1)+"，"
                    +task.getPcycle()+"天"+task.getFrequency()+"次，请使用电脑点击链接提交;";

            String sendmsg = "";
            String sendmsgYXT = "";
            //监控岗位上报
            SafetyFieldManageDO safetyFieldManageDO = codeMap.get(task.getTeamId());
            if("inspect_003_jkgwsb_0".equals(safetyFieldManageDO.getFieldCode())){
                sendmsg = Joiner.on("").join(
                        sendm,
                        "<a href='",
                        JKGW_URL,
                        "'  target='_blank'>",
                        JKGW_URL,
                        "</a><br>");
                sendmsgYXT = Joiner.on("").join(
                        sendm,
                        JKGW_URL);
            }else if("inspect_003_xcgwsb_1".equals(safetyFieldManageDO.getFieldCode())){
                //现场岗位上报
                sendmsg = Joiner.on("").join(
                        sendm,
                        "<a href='",
                        XCGW_URL,
                        "'  target='_blank'>",
                        XCGW_URL,
                        "</a><br>");
                sendmsgYXT = Joiner.on("").join(
                        sendm,
                        XCGW_URL);
            }else if("inspect_003_mngwsb_2".equals(safetyFieldManageDO.getFieldCode())){
                //模拟岗位上报
                sendmsg = Joiner.on("").join(
                        sendm,
                        "<a href='",
                        MNGW_URL,
                        "'  target='_blank'>",
                        MNGW_URL,
                        "</a><br>");
                sendmsgYXT = Joiner.on("").join(
                        sendm,
                        MNGW_URL);
            }

            sendmsg += ("任务详情描述：<br>" + task.getDetailInfo().replaceAll("\n","<br>"));

            sendYXT(sysUserList,sendmsgYXT);
            sendMail(sysUserList,sendmsg);
        }
        safetyPatrolPositionValidQuestDOBizMapper.updateSelectiveBiz(taskLists);
    }

    /**
     * 发送宇信通消息
     * @param sysUserVOS
     * @param sendmsg
     */
    public void sendYXT(List<SysUserVO> sysUserVOS,String sendmsg){
        for (SysUserVO sysUserVO : sysUserVOS) {
            RestTemplate restTemplate = new RestTemplate();
            LogUtils.info("治安巡查定时任务发宇信通消息：{}", sysUserVO.getEmpNo());
            yxtRebotUtils.postForEntity(null,incomintRebotId,sysUserVO.getEmpNo(),sendmsg,restTemplate);
        }
    }

    /**
     * 发邮件
     * @param sysUserVOS
     * @param sendmsg
     */
    public void sendMail(List<SysUserVO> sysUserVOS,String sendmsg){
        for (SysUserVO sysUserVO : sysUserVOS) {
            if (StringUtils.isEmpty(sysUserVO.getRoleName())){continue;}
            mailCenterUtils.sendMailNew("治安巡查岗位上报提醒", sendmsg, sysUserVO.getRoleName(), "治安管理邮箱", "zagl@yutong.com", null);
        }
    }
}
