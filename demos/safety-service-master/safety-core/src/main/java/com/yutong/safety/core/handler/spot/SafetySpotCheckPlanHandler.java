package com.yutong.safety.core.handler.spot;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.FieldMoveEnum;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.param.web.spot.*;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.web.SpotCheckPlanVO;
import com.yutong.safety.core.db.entity.spot.*;
import com.yutong.safety.core.service.SafetyLogMessageService;
import com.yutong.safety.core.service.spot.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gwxiexj
 * @since 2024-05-07 14:19
 */
@Component
public class SafetySpotCheckPlanHandler {
    @Autowired
    private SafetySpotCheckPlanService safetySpotCheckPlanService;

    @Autowired
    private SafetySpotCheckTaskService safetySpotCheckTaskService;

    @Autowired
    private SafetySpotCheckDeviceInfoService safetySpotCheckDeviceInfoService;

    @Autowired
    private SafetySpotCheckRecordService safetySpotCheckRecordService;

    @Autowired
    private SafetySpotCheckRuleService safetySpotCheckRuleService;

    @Autowired
    private SafetySpotLinkRecordRuleService safetySpotLinkRecordRuleService;

    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    /**
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    public Object pageList(SpotCheckPlanListParam param,String token) {
        List<SpotCheckPlanVO> list = safetySpotCheckPlanService.pageList(param,token,true);
        long total = safetySpotCheckPlanService.pageListCount(param,token,true);
        return new PageVO<>(total, list);
    }

    /**
     * 数据导出操作
     * @param param 入参
     * @param token token
     * @return 返回值
     */
    public HSSFWorkbook export(SpotCheckPlanListParam param, String token) {
        List<SpotCheckPlanVO> list = safetySpotCheckPlanService.list(param,token,true);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"任务名称", "应用厂区", "任务周期", "应用项目", "任务开始日期", "任务结束日期","状态", "休眠开始日期","休眠结束日期", "备注"};
        String[][] values = new String[list.size()][];
        SpotCheckPlanVO safetySpotCheckPlanDO;
        for(int i=0; i<list.size(); i++) {
            safetySpotCheckPlanDO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = safetySpotCheckPlanDO.getPlanName();//任务名称
            values[i][1] = safetySpotCheckPlanDO.getFactoryName();//应用厂区
            values[i][2] = safetySpotCheckPlanDO.getCycle()+"天/次";//任务周期
            values[i][3] = String.valueOf(safetySpotCheckPlanDO.getTeamName());//应用项目
            values[i][4] = String.valueOf(safetySpotCheckPlanDO.getBeginDate());//任务开始日期
            values[i][5] = String.valueOf(safetySpotCheckPlanDO.getEndDate());//任务结束日期
            values[i][6] = safetySpotCheckPlanDO.getStatus()==FieldMoveEnum.UP.getType()?"启用":"禁用"; // 状态0-启用 1-禁用
            values[i][7] = String.valueOf(safetySpotCheckPlanDO.getDormancyBegin());
            values[i][8] = String.valueOf(safetySpotCheckPlanDO.getDormancyEnd());
            values[i][9] = safetySpotCheckPlanDO.getRemark();//备注
        }
        String sheetName = "设备设施点检-任务设置";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 数据添加接口
     * @param param 入参
     * @return 返回
     */
    public Object add(SpotCheckPlanSaveParam param, String token) {
        safetySpotCheckPlanService.saveOrUpdate(param,token);
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
        return safetySpotCheckPlanService.modifyStatus(idList,status,token);
    }

    /**
     * 批量设置休眠时间
     * @param param 入参
     * @param token 用户token
     * @return 返回值
     * @throws Exception 异常
     */
    public Object modifyDormancy(SpotCheckPlanDormancyParam param,String token) throws Exception{
        //1.更新设备设施点检任务的休眠设置
        safetySpotCheckPlanService.modifyDormancy(param,token);
        //2.更新设备设置周期任务的截止时间
        SpotCheckTaskListParam taskParam = new SpotCheckTaskListParam();
        taskParam.setTaskBeginDate(param.getDormancyBegin());
        taskParam.setTaskEndDate(param.getDormancyEnd());
        taskParam.setPlanIdStr(param.getIds());

        return safetySpotCheckTaskService.modifyDormancy(taskParam,token);
    }

    /**
     * 周期点检任务生成
     * @return 返回状态
     */
    public Object taskSpotCheck() {
        //获取T+1
        String beginDate = DateUtils.format(DateUtils.addDays(new Date(),1),1);
        SpotCheckPlanListParam param = new SpotCheckPlanListParam();
        param.setBeginDate(beginDate);
        param.setEndDate(beginDate);
        param.setStatus(0);  //正常状态的plan
        param.setTaskFlag(1);
        //1.获取当前时间生效的任务清单
        List<SpotCheckPlanVO> doList = safetySpotCheckPlanService.selectList(param);
        if(CollectionUtils.isEmpty(doList)){
            return null;
        }
        //2.获取现有最新的task清单
        SpotCheckTaskListParam taskListParam = new SpotCheckTaskListParam();
        List<SafetySpotCheckTaskDO> maxTaskList = safetySpotCheckPlanService.selectMaxTask(taskListParam);
        Map<Integer,List<SafetySpotCheckTaskDO>> taskMap = new HashMap<>();

        if(!CollectionUtils.isEmpty(maxTaskList)){
            taskMap = maxTaskList.stream().collect(Collectors.groupingBy(SafetySpotCheckTaskDO::getPlanId));
        }
        int taskNum = dealTaskList(taskMap,doList,beginDate);
        if(taskNum > 0){
            dealTaskDevice(beginDate);
            dealDeviceRule(beginDate);
        }

        return taskNum;
    }

    /**
     * 根据计划生成代办任务
     * @param taskMap 最新任务列表
     * @param doList 有效的计划列表
     * @param beginDate 下个周期开始日期
     * @return
     */
    public int dealTaskList(Map<Integer, List<SafetySpotCheckTaskDO>> taskMap,
                            List<SpotCheckPlanVO> doList, String beginDate) {
        int rows = 0;
        List<SafetySpotCheckTaskDO> taskDOList = new ArrayList<>();

        int initStatus = 0;
        for(SpotCheckPlanVO planDO:doList){
            String dormancyBegin = planDO.getDormancyBegin();
            String dormancyEnd = planDO.getDormancyEnd();
            Integer planId = planDO.getId();
            String curEndDate = "";
            Map<String,List<SafetySpotCheckTaskDO>> factoryTaskMap = new HashMap<>();
            List<SafetySpotCheckTaskDO> lastTask = taskMap.get(planId);
            if(lastTask != null && lastTask.size() > 0){
                curEndDate = lastTask.get(0).getTaskEndDate();
                factoryTaskMap = lastTask.stream().collect(Collectors.groupingBy(SafetySpotCheckTaskDO::getFactoryId));
            }
            //上个周期未结束的任务不用处理
            if(!"".equals(curEndDate) && !DateUtils.isBeforeStr(curEndDate,beginDate)){
                continue;
            }
            if(StringUtils.isNotEmpty(dormancyBegin) && StringUtils.isNotEmpty(dormancyEnd)){
                //休眠期内任务不用处理
                if(DateUtils.loseDay(beginDate,dormancyBegin)<= 0 && DateUtils.loseDay(dormancyEnd,beginDate)<=0){
                    continue;
                }
            }
            //周期<0时设置为1
            int cycle = planDO.getCycle();
            if(cycle <= 0){
                cycle = 1;
            }
            String taskEndDate = safetySpotCheckTaskService.calculateEndDate(beginDate,cycle,dormancyBegin,dormancyEnd);
            Integer teamId = planDO.getTeamId();

            String factoryIdStr = planDO.getFactoryId();
            String[] factoryIdArr = factoryIdStr.split(",");
            for(String fidStr :factoryIdArr){
                //设置任务期序号
                int curOrder = 1;
                List<SafetySpotCheckTaskDO> curList = factoryTaskMap.get(fidStr);
                if(curList != null && curList.size() >0){
                    SafetySpotCheckTaskDO preTask = curList.get(0);
                    if(preTask != null){
                        curOrder = preTask.getCurOrder()+1;
                    }
                }
                //3.保存task任务
                SafetySpotCheckTaskDO taskDO = new SafetySpotCheckTaskDO();
                taskDO.setPlanId(planId);
                taskDO.setPlanName(planDO.getPlanName());
                taskDO.setTaskBeginDate(beginDate);
                taskDO.setTaskEndDate(taskEndDate);
                taskDO.setCreateTime(new Date());
                taskDO.setTeamId(teamId);
                taskDO.setFactoryId(fidStr);
                taskDO.setStatus(initStatus);
                taskDO.setCurOrder(curOrder);  //设置任务期序号

                taskDOList.add(taskDO);
            }
        }

        if(taskDOList.size() >0 ){
            rows = safetySpotCheckTaskService.batchInsertTask(taskDOList);
        }
        return rows;
    }

    /**
     * 根据任务设置批量处理点检的设备信息
     *
     * @param beginDate 周期开始时间
     * @return 返回值
     */
    private int dealTaskDevice(String beginDate) {
        int rows = 0;

        SafetySpotCheckTaskDOExample example = new SafetySpotCheckTaskDOExample();
        SafetySpotCheckTaskDOExample.Criteria criteria = example.createCriteria();
        criteria.andTaskBeginDateEqualTo(beginDate);
        criteria.andStatusEqualTo(0);

        List<SafetySpotCheckTaskDO> taskDOList = safetySpotCheckTaskService.selecTasktList(example);
        for(SafetySpotCheckTaskDO taskDO:taskDOList){
            int curRows = 0;
            List<SafetySpotCheckRecordDO> linkList = new ArrayList<>();
            Integer taskId = taskDO.getId();
            Integer teamId = taskDO.getTeamId();
            String factoryId = taskDO.getFactoryId();

            //任务下有巡检项时，不再生成设备信息
            SafetySpotCheckRecordDOExample recordDOExample = new SafetySpotCheckRecordDOExample();
            SafetySpotCheckRecordDOExample.Criteria criteria1 = recordDOExample.createCriteria();
            criteria1.andTaskIdEqualTo(taskId);
            criteria1.andTaskBeginDateEqualTo(beginDate);
            List<SafetySpotCheckRecordDO> checkRecordDOS = safetySpotCheckRecordService.selectList(recordDOExample);
            if(checkRecordDOS.size()>0){
                continue;
            }
            SpotCheckDeviceListParam param = new SpotCheckDeviceListParam();
            param.setFactoryId(Integer.valueOf(factoryId));
            param.setTeamId(teamId);
            //获取各周期任务的设备信息
            List<SafetySpotCheckDeviceInfoDO> deviceList = safetySpotCheckDeviceInfoService.selectList(param);
            if(deviceList != null && deviceList.size() > 0){
                for(SafetySpotCheckDeviceInfoDO deviceInfoVO:deviceList){
                    SafetySpotCheckRecordDO taskDeviceDO = new SafetySpotCheckRecordDO();
                    taskDeviceDO.setTaskId(taskId);
                    taskDeviceDO.setDeviceId(deviceInfoVO.getId());
                    //判定是否使用临时点检人
                    boolean tempFlag = isUseEmpCheck(beginDate,deviceInfoVO);
                    if(tempFlag){
                        taskDeviceDO.setPlanCheckEmpNo(deviceInfoVO.getProTemCheckEmpNo());
                        taskDeviceDO.setPlanCheckEmp(deviceInfoVO.getProTemCheckEmp());
                    }else{
                        taskDeviceDO.setPlanCheckEmpNo(deviceInfoVO.getCheckEmpNo());
                        taskDeviceDO.setPlanCheckEmp(deviceInfoVO.getCheckEmp());
                    }
                    taskDeviceDO.setCreateTime(new Date());
                    taskDeviceDO.setTaskBeginDate(taskDO.getTaskBeginDate());
                    taskDeviceDO.setTaskEndDate(taskDO.getTaskEndDate());
                    taskDeviceDO.setStatus(0);

                    linkList.add(taskDeviceDO);
                }
            }
            if(linkList.size() > 0){
                curRows = safetySpotCheckTaskService.batchInsertRecord(linkList);
            }
            rows += curRows;
        }
        return rows;
    }

    //校验是否使用临时点检人
    private boolean isUseEmpCheck(String beginDate,SafetySpotCheckDeviceInfoDO deviceInfoVO){
        if(deviceInfoVO == null || StringUtils.isEmpty(deviceInfoVO.getProTemBeginDate())
            || StringUtils.isEmpty(deviceInfoVO.getProTemCheckEmp())){
            return false;
        }
        //周期任务开始时间与临时点检人启用时间相同时
        if(deviceInfoVO.getProTemBeginDate().equals(beginDate)){
            return true;
        }
        if(!DateUtils.isAfterStr(deviceInfoVO.getProTemBeginDate(),beginDate)
           && !DateUtils.isAfterStr(beginDate,deviceInfoVO.getProTemEndDate())){
            return true;
        }
        return false;
    }

    /**
     * 批量处理点检设备的点检标准
     * @param beginDate 周期开始时间
     * @return 返回值
     */
    private int dealDeviceRule(String beginDate) {
        int rows = 0;
        SafetySpotCheckRecordDOExample example = new SafetySpotCheckRecordDOExample();
        SafetySpotCheckRecordDOExample.Criteria criteria = example.createCriteria();
        criteria.andTaskBeginDateEqualTo(beginDate);
        criteria.andStatusEqualTo(0);

        List<SafetySpotCheckRecordDO> recordDOList = safetySpotCheckRecordService.selectList(example);
        if(recordDOList == null || recordDOList.size() <= 0){
            return rows;
        }
        List<SafetySpotLinkRecordRuleDO> recordRuleDOList = new ArrayList<>();
        for(SafetySpotCheckRecordDO recordDO:recordDOList){
            Integer deviceId = recordDO.getDeviceId();
            Integer recordId = recordDO.getId();

            //判断已存在recordRule时，不重复添加
            SafetySpotLinkRecordRuleDOExample example1 = new SafetySpotLinkRecordRuleDOExample();
            SafetySpotLinkRecordRuleDOExample.Criteria criteria1 = example1.createCriteria();
            criteria1.andRecordIdEqualTo(recordId);
            Long recordRuleDOS = safetySpotLinkRecordRuleService.countByExample(example1);
            if(recordRuleDOS > 0){
                continue;
            }

            Map<String,Object> param = new HashMap<>();
            param.put("deviceId",deviceId);
            List<SafetySpotCheckRuleDO> ruleDOList = safetySpotCheckRuleService.selectRuleList(param);
            if(ruleDOList != null && ruleDOList.size()<=0){
                continue;
            }
            for(SafetySpotCheckRuleDO ruleDO:ruleDOList){
                Integer ruleId = ruleDO.getId();

                SafetySpotLinkRecordRuleDO recordRuleDO = new SafetySpotLinkRecordRuleDO();
                recordRuleDO.setDeviceId(deviceId);
                recordRuleDO.setRecordId(recordId);
                recordRuleDO.setRuleId(ruleId);
                recordRuleDO.setRuleName(ruleDO.getRuleName());
                recordRuleDO.setRuleInfo(ruleDO.getRuleInfo());
                recordRuleDO.setRuleSort(ruleDO.getRuleSort());
                recordRuleDOList.add(recordRuleDO);
            }
        }

        if(recordRuleDOList.size()>0){
            rows = safetySpotCheckTaskService.batchInsertRecordRule(recordRuleDOList);
        }

        return rows;
    }

    /**
     * 超过任务截止时间依旧未点检的信息标记为 延期点检
     * @return 返回值
     */
    public int taskInitDelaySpot(String taskEnd){
        if(StringUtils.isEmpty(taskEnd)){
            taskEnd = DateUtils.format(new Date(),1);
        }
        //超过任务截止时间依旧未点检的信息标记为 延期点检 delayCheckFlag = 1
        SafetySpotCheckRecordDO recordDO = new SafetySpotCheckRecordDO();
        recordDO.setDelayCheckFlag(1);

        SafetySpotCheckRecordDOExample example = new SafetySpotCheckRecordDOExample();
        example.createCriteria().andTaskEndDateLessThan(taskEnd).andStatusEqualTo(0);

        return safetySpotCheckRecordService.updateByExampleSelective(recordDO,example);
    }

    /**
     * 取消临时点检人
     * @return 返回值
     */
    public int taskCancelTempCheck(String taskEnd){
        int row = 0;
        if(StringUtils.isNotEmpty(taskEnd)){
            taskEnd = DateUtils.format(new Date(),1);
        }
        //获取临时点检人到期的设备列表
        SpotCheckDeviceListParam listParam = new SpotCheckDeviceListParam();
        listParam.setProEndDate(taskEnd);
        List<SafetySpotCheckDeviceInfoDO> deviceList = safetySpotCheckDeviceInfoService.selectList(listParam);
        if(deviceList != null && deviceList.size() > 0){
            List<Integer> deviceIdList = deviceList.stream().map(SafetySpotCheckDeviceInfoDO::getId).collect(Collectors.toList());
            SpotCheckRecordListParam recordListParam = new SpotCheckRecordListParam();
            recordListParam.setStatus(0);
            recordListParam.setSpotEndDate(taskEnd);
            recordListParam.setDeviceIdList(deviceIdList);
            row = safetySpotCheckRecordService.updatePlanEmpBatch(recordListParam);
        }

        return row;
    }


    /**
     * 周期点检任务生成 - 新逻辑
     * @return 返回状态
     */
    public Object taskSpotCheckNew(){
        //获取T+1
        String beginDate = DateUtils.format(DateUtils.addDays(new Date(),1),1);
        SpotCheckPlanListParam param = new SpotCheckPlanListParam();
        param.setBeginDate(beginDate);
        param.setEndDate(beginDate);
        param.setStatus(0);  //正常状态的plan
        param.setTaskFlag(1);
        //1.获取当前时间生效的任务清单
        List<SpotCheckPlanVO> doList = safetySpotCheckPlanService.selectList(param);
        if(CollectionUtils.isEmpty(doList)){
            return null;
        }
        //2.获取现有最新的task清单
        SpotCheckTaskListParam taskListParam = new SpotCheckTaskListParam();
        List<SafetySpotCheckTaskDO> maxTaskList = safetySpotCheckPlanService.selectMaxTask(taskListParam);
        //获取开工时间为beginDate的周期任务
        taskListParam.setTaskBeginDate(beginDate);
        List<SafetySpotCheckTaskDO> curTaskList = safetySpotCheckPlanService.getCurrentTask(taskListParam);
        //根据任务设置ID-planId分组
        Map<Integer,List<SafetySpotCheckTaskDO>> taskMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(maxTaskList)){
            taskMap = maxTaskList.stream().collect(Collectors.groupingBy(item -> item.getPlanId()));
        }
        return dealTaskListNew(taskMap,curTaskList,doList,beginDate);
    }

    /**
     * 根据计划生成代办任务 - 新逻辑
     * @param taskMap 最新任务列表
     * @param doList 有效的计划列表
     * @param beginDate 下个周期开始日期
     * @return 返回值
     */
    public int dealTaskListNew(Map<Integer, List<SafetySpotCheckTaskDO>> taskMap,
                               List<SafetySpotCheckTaskDO> curTaskList,
                            List<SpotCheckPlanVO> doList, String beginDate){
        List<SafetySpotCheckTaskDO> taskDOList;
        int rows = 0;
        Map<Integer,List<SafetySpotCheckTaskDO>> curTaskMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(curTaskList)){
            curTaskMap = curTaskList.stream().collect(Collectors.groupingBy(SafetySpotCheckTaskDO::getPlanId));
        }
        for(SpotCheckPlanVO planDO:doList){
            String newBeginDate = beginDate;
            String dormancyBegin = planDO.getDormancyBegin();
            String dormancyEnd = planDO.getDormancyEnd();
            Integer planId = planDO.getId();
            String curEndDate = "";
            Map<String,List<SafetySpotCheckTaskDO>> factoryTaskMap = new HashMap<>();
            List<SafetySpotCheckTaskDO> lastTask = taskMap.get(planId);
            if(!CollectionUtils.isEmpty(lastTask)){
                curEndDate = lastTask.get(0).getTaskEndDate();
                factoryTaskMap = lastTask.stream().collect(Collectors.groupingBy(SafetySpotCheckTaskDO::getFactoryId));
            }
            if(StringUtils.isNotEmpty(dormancyBegin) && StringUtils.isNotEmpty(dormancyEnd)){
                //休眠期内任务不用处理
                if(!DateUtils.isAfterStr(beginDate,dormancyBegin) && !DateUtils.isAfterStr(dormancyEnd,beginDate)){
                    newBeginDate = safetySpotCheckTaskService.calculateEndDate(beginDate,0,dormancyBegin,dormancyEnd);
                }
            }
            //周期<0时设置为1
            int cycle = planDO.getCycle()<=0?1:planDO.getCycle();

            //分两种情况处理周期任务 1.新任务/刚启用任务（最大任务时间<当前时间的）一次性生成前三个期次的周期任务
            if("".equals(curEndDate) || DateUtils.loseDayNew(curEndDate,beginDate)>0){
                taskDOList = generateTaskOneRule(planDO,newBeginDate,factoryTaskMap,cycle);
                if(!ObjectUtils.isEmpty(taskDOList)){
                    safetySpotCheckTaskService.batchInsertTask(taskDOList);
                }
            }
            //没有满足taskBeginDate = beginDate的数据时，不执行第二种生成规则
            if(CollectionUtils.isEmpty(curTaskList)){
                continue;
            }
            //当前planId下没有满足taskBeginDate = beginDate的数据时，不执行第二种生成规则
            List<SafetySpotCheckTaskDO> planCurTaskList = curTaskMap.get(planDO.getId());
            if(CollectionUtils.isEmpty(planCurTaskList)){
                continue;
            }
            // 2.已存在的周期任务t期次内T+2期次的任务
            //首次生成三个周期，只有当当前时间所处周期为第二周期的最后一天的时候，才会去生成T+2的新周期；新周期的开始时间 = 第二周期的结束时间 + 周期 + 1
            if(!(DateUtils.loseDayNew(curEndDate,beginDate)>0) && DateUtils.loseDay(beginDate,curEndDate)<cycle){
                newBeginDate = DateUtils.format(DateUtils.addDays(DateUtils.parse(beginDate, 1), cycle), 1);
                taskDOList = generateTaskTwoRule(planDO,planCurTaskList,factoryTaskMap,cycle,newBeginDate);
                if(!ObjectUtils.isEmpty(taskDOList)){
                    safetySpotCheckTaskService.batchInsertTask(taskDOList);
                }
            }
        }

        dealTaskDeviceNew();
        dealDeviceRuleNew();

        return rows;
    }

    /**
     * 生成周期任务的规则1
     * @param planDO 任务配置信息
     * @param beginDate 下个期次开始日期
     * @param factoryTaskMap 各厂区max任务
     * @param cycle 任务周期配置
     * @return 返回值
     */
    public List<SafetySpotCheckTaskDO> generateTaskOneRule(SpotCheckPlanVO planDO,String beginDate,
                                                         Map<String,List<SafetySpotCheckTaskDO>> factoryTaskMap,
                                                         int cycle){

        List<SafetySpotCheckTaskDO> taskDOList = new ArrayList<>();

        String factoryIdStr = planDO.getFactoryId();
        String[] factoryIdArr = factoryIdStr.split(",");
        for(String fidStr :factoryIdArr){
            //设置任务期序号
            int curOrder = 1;
            List<SafetySpotCheckTaskDO> curList = factoryTaskMap.get(fidStr);
            if(curList != null && curList.size() >0){
                SafetySpotCheckTaskDO preTask = curList.get(0);
                if(preTask != null){
                    curOrder = preTask.getCurOrder()+1;
                }
            }
            Integer maxOrder = curOrder+3;
            generateThreeTask(planDO,beginDate,cycle,fidStr,curOrder,maxOrder,taskDOList);

        }
        return taskDOList;
    }

    /**
     * 一次生成三个期次的任务
     * @param planDO 任务配置
     * @param beginDate 下个周期任务开始日期
     * @param cycle 周期间隔
     * @param factoryId 工厂ID
     * @param curOrder 档期期次
     * @param taskDOList 待添加任务集合
     */
    private void generateThreeTask(SpotCheckPlanVO planDO, String beginDate, Integer cycle,
                                   String factoryId, Integer curOrder, Integer maxOrder,
                                   List<SafetySpotCheckTaskDO> taskDOList){
        //首期次任务或启用后当前日期首次任务，直接生成第2期和第3期任务
        int initStatus = 0;
        Integer planId = planDO.getId();
        Integer teamId = planDO.getTeamId();
        String dormancyBegin = planDO.getDormancyBegin();
        String dormancyEnd = planDO.getDormancyEnd();

        String newBegin = "";
        for(int i=0;i<maxOrder-curOrder;i++){
            if(StringUtils.isEmpty(newBegin)){
                newBegin = beginDate;
            }
            //超出任务截止时间时不再执行
            if(!DateUtils.isBeforeStr(newBegin,planDO.getEndDate())){
                continue;
            }
            String nexTaskEndDate = safetySpotCheckTaskService.calculateEndDate(newBegin,cycle,dormancyBegin,dormancyEnd);
            //周期任务的截止日期>任务截止日期时，取任务截止日期
            if(DateUtils.isAfterStr(nexTaskEndDate,planDO.getEndDate())){
                nexTaskEndDate = planDO.getEndDate();
            }
            SafetySpotCheckTaskDO nexTaskDO = new SafetySpotCheckTaskDO();
            nexTaskDO.setPlanId(planId);
            nexTaskDO.setPlanName(planDO.getPlanName());
            nexTaskDO.setTaskBeginDate(newBegin);
            nexTaskDO.setTaskEndDate(nexTaskEndDate);
            nexTaskDO.setCreateTime(new Date());
            nexTaskDO.setTeamId(teamId);
            nexTaskDO.setFactoryId(factoryId);
            nexTaskDO.setStatus(initStatus);
            nexTaskDO.setCurOrder(curOrder+i);  //设置任务期序号

            newBegin = DateUtils.format(DateUtils.addDays(DateUtils.parse(nexTaskEndDate,1),1),1);;

            taskDOList.add(nexTaskDO);
        }
    }

    /**
     * 生成周期任务的规则2
     * @param planDO 任务配置信息
     * @param planCurTaskList taskBeginDate = beginDate的任务列表
     * @param factoryTaskMap 各长期任务
     * @param cycle 任务周期配置
     * @param beginDate 下个期次开始日期
     * @return 返回值
     */
    public List<SafetySpotCheckTaskDO> generateTaskTwoRule(SpotCheckPlanVO planDO,List<SafetySpotCheckTaskDO> planCurTaskList,
                                                        Map<String,List<SafetySpotCheckTaskDO>> factoryTaskMap,
                                                        int cycle,String beginDate){

        List<SafetySpotCheckTaskDO> taskDOList = new ArrayList<>();
        Map<String,SafetySpotCheckTaskDO> factoryCurTaskMap = planCurTaskList.stream().collect(Collectors.toMap(SafetySpotCheckTaskDO::getFactoryId, e->e));
        String factoryIdStr = planDO.getFactoryId();
        String[] factoryIdArr = factoryIdStr.split(",");
        for(String fidStr :factoryIdArr){
            //设置任务期序号
            int nextOrder = 1;
            List<SafetySpotCheckTaskDO> curList = factoryTaskMap.get(fidStr);
            if(curList != null && curList.size() >0){
                SafetySpotCheckTaskDO preTask = curList.get(0);
                if(preTask != null){
                    nextOrder = preTask.getCurOrder()+1;
                }
            }
            if(nextOrder == 1){
                Integer maxOrder = nextOrder+3;
                generateThreeTask(planDO, beginDate, cycle, fidStr, nextOrder, maxOrder, taskDOList);
            }else{
                SafetySpotCheckTaskDO curTask = factoryCurTaskMap.get(fidStr);
                if(curTask == null){
                    continue;
                }
                //判断当前任务的最大周期是否<t+2
                Integer curOrder = curTask.getCurOrder();
                if(nextOrder<=curOrder+2){
                    generateThreeTask(planDO,beginDate,cycle,fidStr,nextOrder,curOrder+2,taskDOList);
                }
            }
        }

        return taskDOList;
    }

    /**
     * 处理任务设备清单
     * @return 返回值
     */
    public Object dealTaskDeviceNew(){
        int rows = 0;

        SafetySpotCheckTaskDOExample example = new SafetySpotCheckTaskDOExample();
        SafetySpotCheckTaskDOExample.Criteria criteria = example.createCriteria();
        criteria.andCreateTimeGreaterThan(DateUtils.addHours(new Date(),-1));
        criteria.andStatusEqualTo(0);

        List<SafetySpotCheckTaskDO> taskDOList = safetySpotCheckTaskService.selecTasktList(example);
        if(CollectionUtils.isEmpty(taskDOList)){
            return 0;
        }
        for(SafetySpotCheckTaskDO taskDO:taskDOList){
            int curRows = 0;
            List<SafetySpotCheckRecordDO> linkList = new ArrayList<>();
            Integer taskId = taskDO.getId();
            Integer teamId = taskDO.getTeamId();
            String factoryId = taskDO.getFactoryId();

            //任务下有巡检项时，不再生成设备信息
            SafetySpotCheckRecordDOExample recordDOExample = new SafetySpotCheckRecordDOExample();
            SafetySpotCheckRecordDOExample.Criteria criteria1 = recordDOExample.createCriteria();
            criteria1.andTaskIdEqualTo(taskId);
            criteria1.andTaskBeginDateEqualTo(taskDO.getTaskBeginDate());
            List<SafetySpotCheckRecordDO> checkRecordDOS = safetySpotCheckRecordService.selectList(recordDOExample);
            if(checkRecordDOS.size()>0){
                continue;
            }
            SpotCheckDeviceListParam param = new SpotCheckDeviceListParam();
            param.setFactoryId(Integer.valueOf(factoryId));
            param.setTeamId(teamId);
            //获取各周期任务的设备信息
            List<SafetySpotCheckDeviceInfoDO> deviceList = safetySpotCheckDeviceInfoService.selectList(param);
            if(!CollectionUtils.isEmpty(deviceList)){
                for(SafetySpotCheckDeviceInfoDO deviceInfoVO:deviceList){
                    SafetySpotCheckRecordDO taskDeviceDO = new SafetySpotCheckRecordDO();
                    taskDeviceDO.setTaskId(taskId);
                    taskDeviceDO.setDeviceId(deviceInfoVO.getId());
                    //判定是否使用临时点检人
                    boolean tempFlag = isUseEmpCheck(taskDO.getTaskBeginDate(),deviceInfoVO);
                    if(tempFlag){
                        taskDeviceDO.setPlanCheckEmpNo(deviceInfoVO.getProTemCheckEmpNo());
                        taskDeviceDO.setPlanCheckEmp(deviceInfoVO.getProTemCheckEmp());
                    }else{
                        taskDeviceDO.setPlanCheckEmpNo(deviceInfoVO.getCheckEmpNo());
                        taskDeviceDO.setPlanCheckEmp(deviceInfoVO.getCheckEmp());
                    }
                    taskDeviceDO.setCreateTime(new Date());
                    taskDeviceDO.setTaskBeginDate(taskDO.getTaskBeginDate());
                    taskDeviceDO.setTaskEndDate(taskDO.getTaskEndDate());
                    taskDeviceDO.setStatus(0);

                    linkList.add(taskDeviceDO);
                }
            }
            if(linkList.size() > 0){
                curRows = safetySpotCheckTaskService.batchInsertRecord(linkList);
            }
            rows += curRows;
        }
        return rows;
    }

    /**
     * 批量处理点检设备的点检标准
     * @return 返回值
     */
    private int dealDeviceRuleNew() {
        int rows = 0;
        SafetySpotCheckRecordDOExample example = new SafetySpotCheckRecordDOExample();
        SafetySpotCheckRecordDOExample.Criteria criteria = example.createCriteria();
        criteria.andCreateTimeGreaterThan(DateUtils.addHours(new Date(),-1));
        criteria.andStatusEqualTo(0);

        List<SafetySpotCheckRecordDO> recordDOList = safetySpotCheckRecordService.selectList(example);
        if(CollectionUtils.isEmpty(recordDOList)){
            return rows;
        }
        List<SafetySpotLinkRecordRuleDO> recordRuleDOList = new ArrayList<>();
        for(SafetySpotCheckRecordDO recordDO:recordDOList){
            Integer deviceId = recordDO.getDeviceId();
            Integer recordId = recordDO.getId();

            //判断已存在recordRule时，不重复添加
            SafetySpotLinkRecordRuleDOExample example1 = new SafetySpotLinkRecordRuleDOExample();
            SafetySpotLinkRecordRuleDOExample.Criteria criteria1 = example1.createCriteria();
            criteria1.andRecordIdEqualTo(recordId);
            Long recordRuleDOS = safetySpotLinkRecordRuleService.countByExample(example1);
            if(recordRuleDOS > 0){
                continue;
            }

            Map<String,Object> param = new HashMap<>();
            param.put("deviceId",deviceId);
            List<SafetySpotCheckRuleDO> ruleDOList = safetySpotCheckRuleService.selectRuleList(param);
            if(CollectionUtils.isEmpty(ruleDOList)){
                continue;
            }
            for(SafetySpotCheckRuleDO ruleDO:ruleDOList){
                Integer ruleId = ruleDO.getId();

                SafetySpotLinkRecordRuleDO recordRuleDO = new SafetySpotLinkRecordRuleDO();
                recordRuleDO.setDeviceId(deviceId);
                recordRuleDO.setRecordId(recordId);
                recordRuleDO.setRuleId(ruleId);
                recordRuleDO.setRuleName(ruleDO.getRuleName());
                recordRuleDO.setRuleInfo(ruleDO.getRuleInfo());
                recordRuleDO.setRuleSort(ruleDO.getRuleSort());
                recordRuleDOList.add(recordRuleDO);
            }
        }

        if(recordRuleDOList.size()>0){
            rows = safetySpotCheckTaskService.batchInsertRecordRule(recordRuleDOList);
        }

        return rows;
    }

    /**
     * 批量更新任务列表
     * @param planIdStr 入参
     * @param token TOKEN
     */
    public Object modifyTasks(String planIdStr,String token){
        if(StringUtils.isEmpty(planIdStr)){
            return null;
        }

        List<SafetySpotCheckPlanDO> planVOList = safetySpotCheckPlanService.selectExample(planIdStr);
        if(ObjectUtils.isEmpty(planVOList)){
            return null;
        }
        SpotCheckTaskListParam taskListParam = new SpotCheckTaskListParam();
        taskListParam.setPlanIdStr(planIdStr);
        taskListParam.setTaskEndDate(DateUtils.format(new Date(),1));
        List<SafetySpotCheckTaskDO> taskDOS = safetySpotCheckTaskService.selectByExample(taskListParam);
        if(ObjectUtils.isEmpty(taskDOS)){
            return null;
        }
        for(SafetySpotCheckTaskDO checkTaskDO:taskDOS){
            //更新task下的teamId和factoryId下新增的设备信息
            safetySpotCheckTaskService.modifyTaskDevice(checkTaskDO);
            safetySpotCheckTaskService.modifyDelTaskDevice(checkTaskDO);
        }
        String planNameStr = planVOList.stream().map(SafetySpotCheckPlanDO::getPlanName).collect(Collectors.joining(","));
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("设备设施点检-任务设置");
        log.setAbility("更新任务设备清单");
        String realName = TokenUtils.getRealName(token);
        log.setMessage("更新“"+planNameStr+"”的点检设备");
        log.setOperator(realName);
        safetyLogMessageService.addLogMessage(log);
        return null;
    }

    /**
     * 定时将未完成的检修流程和最新的task任务关联起来
     * @return
     */
    public Object taskRelaProcessToNewTask() {
        //查询task任务表，查出当日新生成的一批task和上一版task的对应的map
        List<Map<String, Object>> relationNewAndLast = safetySpotCheckTaskService.getRelationNewAndLast();

        if(CollectionUtils.isEmpty(relationNewAndLast)){return null;}

        List<Integer> newTaskIdList = new ArrayList<>();
        List<Integer> oldTaskIdList = new ArrayList<>();
        relationNewAndLast.stream().forEach(m -> {
            if(ObjectUtils.isEmpty(m.get("lastid")) || ObjectUtils.isEmpty(m.get("newid"))){return;}

            int lastId = Integer.parseInt(m.get("lastid").toString());
            int newId = Integer.parseInt(m.get("newid").toString());

            newTaskIdList.add(newId);
            oldTaskIdList.add(lastId);
        });
        if(CollectionUtils.isEmpty(newTaskIdList) || CollectionUtils.isEmpty(oldTaskIdList)){
            return null;
        }
        List<SafetySpotCheckRecordDO> lastRecordList = safetySpotCheckRecordService.getRecordsByTaskId(oldTaskIdList,"old");
        List<SafetySpotCheckRecordDO> newRecordList = safetySpotCheckRecordService.getRecordsByTaskId(newTaskIdList,"new");
        if(CollectionUtils.isEmpty(lastRecordList) || CollectionUtils.isEmpty(newRecordList)){
            return null;
        }

        Map<Integer, List<SafetySpotCheckRecordDO>> lastRecordMap = lastRecordList.stream().collect(Collectors.groupingBy(SafetySpotCheckRecordDO::getTaskId));
        Map<Integer, List<SafetySpotCheckRecordDO>> newRecordMap = newRecordList.stream().collect(Collectors.groupingBy(SafetySpotCheckRecordDO::getTaskId));


        relationNewAndLast.stream().forEach(map -> {
            if(ObjectUtils.isEmpty(map.get("lastid")) || ObjectUtils.isEmpty(map.get("newid"))){return;}

            int lastId = Integer.parseInt(map.get("lastid").toString());
            int newId = Integer.parseInt(map.get("newid").toString());

            //根据lastid 查询record表，查出已点检待验证的记录
            List<SafetySpotCheckRecordDO> lastList = lastRecordMap.get(lastId);
            List<SafetySpotCheckRecordDO> newList = newRecordMap.get(newId);

            //将lastid对应的record数据更新到newid对应的record记录
            Map<String, Object> transMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(lastList) && !CollectionUtils.isEmpty(newList)){
                transMap = updateNewRecord(lastList, newList);
            }

            //如果transList不为空，继续更新safety_spot_link_record_rule
            if(ObjectUtils.isEmpty(transMap.get("relationList"))){return;}

            //更新safety_spot_link_record_rule
            updateRecordRule(transMap);
        });

        return null;
    }

    /**
     * 将上一条的record记录更新到最新的记录中
     * @param lastRecordList
     * @param newRecordList
     */
    public Map<String, Object> updateNewRecord(List<SafetySpotCheckRecordDO> lastRecordList, List<SafetySpotCheckRecordDO> newRecordList){
        Map<String, Object> resultMap = new HashMap<>();
        Map<Integer, Integer> relationMap = new HashMap<>();
        List<Integer> newList = new ArrayList<>();
        List<Integer> lastList = new ArrayList<>();
        Map<Integer,List<SafetySpotCheckRecordDO>> newRecordMap = newRecordList.stream().collect(Collectors.groupingBy(SafetySpotCheckRecordDO::getDeviceId));

        List<SafetySpotCheckRecordDO> updateList = new ArrayList<>();

        lastRecordList.stream().forEach(en -> {
            SafetySpotCheckRecordDO entity = new SafetySpotCheckRecordDO();


            List<SafetySpotCheckRecordDO> safetySpotCheckRecordDOS = newRecordMap.get(en.getDeviceId());
            if(CollectionUtils.isEmpty(safetySpotCheckRecordDOS)){return;}

            SafetySpotCheckRecordDO newRecord = safetySpotCheckRecordDOS.get(0);

            //生成新的实体
            BeanUtils.copyProperties(en,entity);
            entity.setId(newRecord.getId());
            entity.setTaskId(newRecord.getTaskId());
            entity.setDeviceId(newRecord.getDeviceId());
            entity.setUpdateTime(new Date());
            entity.setCreateTime(newRecord.getCreateTime());
            entity.setTaskBeginDate(newRecord.getTaskBeginDate());
            entity.setTaskEndDate(newRecord.getTaskEndDate());

            //记录转换过的record的id
            relationMap.put(en.getId(),newRecord.getId());
            newList.add(newRecord.getId());
            lastList.add(en.getId());

            updateList.add(entity);
        });

        if(!CollectionUtils.isEmpty(updateList)){
            safetySpotCheckRecordService.batchUpdateRecord(updateList);
        }

        resultMap.put("relationList",relationMap);
        resultMap.put("newList",newList);
        resultMap.put("lastList",lastList);

        return resultMap;
    }

    /**
     * 根据recordId更新safety_spot_link_record_rule
     * @param transMap
     */
    public void updateRecordRule(Map<String, Object> transMap){
        List<SafetySpotLinkRecordRuleDO> list = new ArrayList<>();

        Map<Integer, Integer> relationMap = (Map<Integer, Integer>) transMap.get("relationList");
        List<Integer> newList = (List<Integer>) transMap.get("newList");
        List<Integer> lastList = (List<Integer>) transMap.get("lastList");

        //查询上一次的record对应的记录
        List<SafetySpotLinkRecordRuleDO> lastRecordRuleList = safetySpotLinkRecordRuleService.getDataByList(lastList);

        //查询最新次的record对应的记录
        List<SafetySpotLinkRecordRuleDO> newRecordRuleList = safetySpotLinkRecordRuleService.getDataByList(newList);

        Map<String,SafetySpotLinkRecordRuleDO> newMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(newRecordRuleList)){
            newMap = newRecordRuleList.stream().collect(Collectors.toMap(
                    entity -> entity.getRecordId() + "_" + entity.getRuleId() + "_" + entity.getDeviceId(), // 拼接字段作为key
                    entity -> entity                                          // 实体类作为value
            ));
        }

        for (SafetySpotLinkRecordRuleDO en:lastRecordRuleList){
            Integer lastRecordId = en.getRecordId();
            Integer ruleId = en.getRuleId();
            Integer deviceId = en.getDeviceId();
            Integer newRecordId = relationMap.get(lastRecordId);

            SafetySpotLinkRecordRuleDO newEntity = newMap.get(newRecordId + "_" + ruleId + "_" + deviceId);

            if(ObjectUtils.isEmpty(newEntity)){continue;}

            newEntity.setIsException(en.getIsException());
            newEntity.setExcepDesc(en.getExcepDesc());
            newEntity.setImgUrl(en.getImgUrl());
            newEntity.setIsLocal(en.getIsLocal());
            newEntity.setFinishUrl(en.getFinishUrl());
            newEntity.setRemark(en.getRemark());

            list.add(newEntity);
        };

        if(!CollectionUtils.isEmpty(list)){
            safetySpotLinkRecordRuleService.batchUpdateByList(list);
        }
    }
}
