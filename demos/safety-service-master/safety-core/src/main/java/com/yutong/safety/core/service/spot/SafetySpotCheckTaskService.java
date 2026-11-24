package com.yutong.safety.core.service.spot;

import com.alibaba.fastjson.JSONObject;
import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.app.SpotTaskListAppParam;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.param.web.spot.SpotCheckTaskListParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.app.SpotCheckToDoVO;
import com.yutong.safety.common.vo.web.SpotCheckTaskReportVO;
import com.yutong.safety.core.db.entity.spot.*;
import com.yutong.safety.core.db.mapper.spot.SafetySpotCheckRecordDOMapper;
import com.yutong.safety.core.db.mapper.spot.SafetySpotCheckTaskDOBizMapper;
import com.yutong.safety.core.db.mapper.spot.SafetySpotCheckTaskDOMapper;
import com.yutong.safety.core.db.mapper.spot.SafetySpotLinkRecordRuleDOMapper;
import com.yutong.safety.core.service.SafetyLogMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gwxiexj
 * @since 2024-05-09 16:14
 */
@Service
public class SafetySpotCheckTaskService {

    @Autowired
    private SafetySpotCheckTaskDOMapper safetySpotCheckTaskDOMapper;

    @Autowired
    private SafetySpotCheckTaskDOBizMapper safetySpotCheckTaskDOBizMapper;

    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    @Autowired
    private WebserviceUtil webserviceUtil;

    @Value("${iam.employee.workNoConvert.url}")
    private String employeeWorkNoConvertUrl;

    @Autowired
    private SafetySpotLinkRecordRuleService safetySpotLinkRecordRuleService;

    @Autowired
    private SafetySpotCheckRuleService safetySpotCheckRuleService;

    @Autowired
    private SafetySpotCheckRecordDOMapper safetySpotCheckRecordDOMapper;

    @Autowired
    private SafetySpotLinkRecordRuleDOMapper safetySpotLinkRecordRuleDOMapper;

    /**
     * 新增设备点检-周期内任务
     * @param taskDO 入参
     * @return 返回值
     */
    public int save(SafetySpotCheckTaskDO taskDO){
        return safetySpotCheckTaskDOMapper.insert(taskDO);
    }

    /**
     * 更新 设备点检-周期内任务
     * @param taskDO 入参
     * @return 返回值
     */
    public int update(SafetySpotCheckTaskDO taskDO){
        return safetySpotCheckTaskDOMapper.updateByPrimaryKeySelective(taskDO);
    }

    /**
     * 根据条件批量更新
     * @param taskDO 入参
     * @param example 条件
     * @return 返回值
     */
    public int updateByExample(SafetySpotCheckTaskDO taskDO, SafetySpotCheckTaskDOExample example){
        return safetySpotCheckTaskDOMapper.updateByExample(taskDO,example);
    }

    /**
     * 获取列表
     * @param example 检索条件
     * @return 返回值
     */
    public List<SafetySpotCheckTaskDO> selecTasktList(SafetySpotCheckTaskDOExample example){
        return safetySpotCheckTaskDOMapper.selectByExample(example);
    }

    /**
     * 批量增加周期任务
     * @param list 入参
     * @return 返回值
     */
    public int batchInsertTask(List<SafetySpotCheckTaskDO> list){
        return safetySpotCheckTaskDOBizMapper.batchInsertTask(list);
    }

    /**
     * 批量增加 周期点检设备
     * @param list 入参
     * @return 返回值
     */
    public int batchInsertRecord(List<SafetySpotCheckRecordDO> list){
        return safetySpotCheckTaskDOBizMapper.batchInsertRecord(list);
    }

    /**
     * 批量增加 周期点检设备点检标准
     * @param list 入参
     * @return 返回值
     */
    public int batchInsertRecordRule(List<SafetySpotLinkRecordRuleDO> list){
        return safetySpotCheckTaskDOBizMapper.batchInsertRecordRule(list);
    }

    public void selectTaskReport(List<SpotCheckTaskReportVO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<Integer> idx = list.stream().map(SpotCheckTaskReportVO::getId).collect(Collectors.toList());
        List<SpotCheckTaskReportVO> reportVOList = safetySpotCheckTaskDOBizMapper.selectCheckRecord(idx);
        Map<Integer, SpotCheckTaskReportVO> dataMap = reportVOList.stream().collect(Collectors.toMap(SpotCheckTaskReportVO::getId, t -> t));
        list.forEach(li -> {
            SpotCheckTaskReportVO info = dataMap.get(li.getId());
            if (info != null) {
                li.setDeviceNum(info.getDeviceNum());
                li.setCheckNum(info.getCheckNum());
                li.setUnCheckNum(info.getUnCheckNum());
                li.setExceptionNum(info.getExceptionNum());
                li.setCheckRate(info.getCheckRate());
                li.setReformNum(info.getReformNum());
                li.setValidateNum(info.getValidateNum());
                li.setSpotDelayNum(info.getSpotDelayNum());
                li.setReformDelayNum(info.getReformDelayNum());
                li.setReformRate(info.getReformRate());
                li.setSpotDelayRate(info.getSpotDelayRate());
                li.setReformDelayRate(info.getReformDelayRate());
            }
        });
    }

    /**
     * 获取周期任务报表统计
     *
     * @param param 入参
     * @return 返回值
     */
    public List<SpotCheckTaskReportVO> selectTaskReport(SpotCheckTaskListParam param) {
        return safetySpotCheckTaskDOBizMapper.selectTaskReport(param);
    }

    /**
     * 获取周期任务报表统计条数
     *
     * @param param 入参
     * @return 返回值
     */
    public long selectTaskReportCount(SpotCheckTaskListParam param) {
        return safetySpotCheckTaskDOBizMapper.selectTaskReportCount(param);
    }

    /**
     * 获取设备设施点检代办
     *
     * @param param 入参
     * @return 返回值
     */
    public List<SpotCheckToDoVO> selectSpotToDo(SpotTaskListAppParam param) {
        return safetySpotCheckTaskDOBizMapper.selectSpotToDo(param);
    }

    /**
     * 根据休眠逻辑批量更新周期任务
     * @param param 入参
     * @param token
     * @return
     */
    public int modifyDormancy(SpotCheckTaskListParam param,String token) {
        List<SpotCheckTaskReportVO> taskList = safetySpotCheckTaskDOBizMapper.getCurrentTaskList(param);
        if(taskList == null || taskList.size() <= 0){
            return 0;
        }
        int row = 0;
        List<SafetySpotCheckTaskDO> updateList = new ArrayList();
        String planName = taskList.stream().map(SpotCheckTaskReportVO::getPlanName).collect(Collectors.joining(","));
        StringBuilder updateEndDate = new StringBuilder();
        for(SpotCheckTaskReportVO taskDO:taskList){
            String beginDate = taskDO.getTaskBeginDate();
            String dormancyBegin = param.getTaskBeginDate();
            String dormancyEnd = param.getTaskEndDate();
            String cycle = taskDO.getCycleDesc();
            SafetySpotCheckTaskDO taskDO1 = new SafetySpotCheckTaskDO();
            taskDO1.setId(taskDO.getId());
            String endDate = calculateEndDate(beginDate,Integer.parseInt(cycle),dormancyBegin,dormancyEnd);
            taskDO1.setTaskEndDate(endDate);
            updateList.add(taskDO1);
            updateEndDate.append(endDate).append(",");
        }
        row = safetySpotCheckTaskDOBizMapper.batchUpdateTask(updateList);

        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("设备设施点检-周期任务");
        log.setAbility("批量休眠");
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        log.setMessage("修改“"+planName+"”的周期结束时间为："+updateEndDate);
        log.setOperator(tokenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);

        return row;
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
    public String calculateEndDate(String beginDate,int cycle,String dormancyBegin,String dormancyEnd){
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
     * 转换员工号
     * @param oldWorkNo 旧员工号
     * @return 返回值
     */
    public String employeeConvertWorkNo(String oldWorkNo){
        String newWorkNo = oldWorkNo;
        if(StringUtils.isEmpty(employeeWorkNoConvertUrl)){
            LogUtils.info("转换员工号接口“iam.employee.workNoConvert.url”为空");
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"转换员工号接口“iam.employee.workNoConvert.url”为空");
        }
        //请求接口查询员工信息
        String url = String.format(employeeWorkNoConvertUrl, "ABYJS", oldWorkNo);
        String result = webserviceUtil.sendHttpForGet(url);
        if(StringUtils.isNotEmpty(result)) {
            JSONObject userJson = (JSONObject) JsonUtils.parse(result);
            if(result.contains("success") && userJson.getBoolean("success")) {
                newWorkNo = userJson.getString("result");
            }
        }
        return newWorkNo;
    }

    /**
     * 查询
     * @param param 入参
     * @return 返回值
     */
    public List<SafetySpotCheckTaskDO> selectByExample(SpotCheckTaskListParam param){

        SafetySpotCheckTaskDOExample example = new SafetySpotCheckTaskDOExample();
        SafetySpotCheckTaskDOExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(0);
        if(StringUtils.isNotEmpty(param.getPlanIdStr())){
            String[] planIdArr = param.getPlanIdStr().split(",");
            List<Integer> planIdList = Arrays.stream(planIdArr).map(Integer::valueOf).collect(Collectors.toList());
            criteria.andPlanIdIn(planIdList);
        }
        if(StringUtils.isNotEmpty(param.getTaskEndDate())){
            criteria.andTaskEndDateGreaterThanOrEqualTo(param.getTaskEndDate());
        }

        return safetySpotCheckTaskDOMapper.selectByExample(example);
    }

    /**
     * 查询
     * @param taskDO 入参
     * @return 返回值
     */
    public int modifyTaskDevice(SafetySpotCheckTaskDO taskDO){
        int row = 0;
        List<SafetySpotCheckDeviceInfoDO> deviceInfoDOList = safetySpotCheckTaskDOBizMapper.getTaskDeviceUpdateList(taskDO.getId());
        if(ObjectUtils.isEmpty(deviceInfoDOList)){
            return row;
        }
        List<SafetySpotCheckRecordDO> recordDOList = new ArrayList<>();
        for(SafetySpotCheckDeviceInfoDO deviceInfoDO:deviceInfoDOList){
            SafetySpotCheckRecordDO recordDO = new SafetySpotCheckRecordDO();
            recordDO.setTaskId(taskDO.getId());
            recordDO.setTaskBeginDate(taskDO.getTaskBeginDate());
            recordDO.setTaskEndDate(taskDO.getTaskEndDate());
            recordDO.setDeviceId(deviceInfoDO.getId());
            recordDO.setStatus(0);
            recordDO.setCreateTime(new Date());

            String beginDate = DateUtils.format(new Date(),1);
            //判定是否使用临时点检人
            boolean tempFlag = isUseEmpCheck(beginDate,deviceInfoDO);
            if(tempFlag){
                recordDO.setPlanCheckEmpNo(deviceInfoDO.getProTemCheckEmpNo());
                recordDO.setPlanCheckEmp(deviceInfoDO.getProTemCheckEmp());
            }else{
                recordDO.setPlanCheckEmpNo(deviceInfoDO.getCheckEmpNo());
                recordDO.setPlanCheckEmp(deviceInfoDO.getCheckEmp());
            }

            recordDOList.add(recordDO);
        }

        row = safetySpotCheckTaskDOBizMapper.batchInsertRecord(recordDOList);

        List<Integer> deviceIdList = deviceInfoDOList.stream().map(SafetySpotCheckDeviceInfoDO::getId).collect(Collectors.toList());
        SafetySpotCheckRecordDOExample example = new SafetySpotCheckRecordDOExample();
        SafetySpotCheckRecordDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeviceIdIn(deviceIdList);
        criteria.andTaskIdEqualTo(taskDO.getId());

        List<SafetySpotCheckRecordDO> modityList = safetySpotCheckRecordDOMapper.selectByExample(example);
        modifyDeviceRules(modityList);
        return row;
    }

    /**
     * 更新设备巡检规则
     * @param recordDOList 巡检设备清单
     * @return 返回值
     */
    public int modifyDeviceRules(List<SafetySpotCheckRecordDO> recordDOList){
        int rows = 0;
        List<SafetySpotLinkRecordRuleDO> recordRuleDOList = new ArrayList<>();
        for(SafetySpotCheckRecordDO recordDO:recordDOList) {
            Integer deviceId = recordDO.getDeviceId();
            Integer recordId = recordDO.getId();
            //判断已存在recordRule时，不重复添加
            SafetySpotLinkRecordRuleDOExample example1 = new SafetySpotLinkRecordRuleDOExample();
            SafetySpotLinkRecordRuleDOExample.Criteria criteria1 = example1.createCriteria();
            criteria1.andRecordIdEqualTo(recordId);
            Long recordRuleDOS = safetySpotLinkRecordRuleService.countByExample(example1);
            if (recordRuleDOS > 0) {
                return rows;
            }

            Map<String, Object> param = new HashMap<>();
            param.put("deviceId", deviceId);
            List<SafetySpotCheckRuleDO> ruleDOList = safetySpotCheckRuleService.selectRuleList(param);
            if (ruleDOList != null && ruleDOList.size() <= 0) {
                return rows;
            }
            for (SafetySpotCheckRuleDO ruleDO : ruleDOList) {
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
            rows = this.batchInsertRecordRule(recordRuleDOList);
        }

        return rows;
    }

    /**
     * 更新删除已删除编号
     * @param taskDO 周期任务
     * @return 返回值
     */
    public int modifyDelTaskDevice(SafetySpotCheckTaskDO taskDO){
        int rows = 0;
        Map<String,Object> deviceMap = new HashMap<>();
        deviceMap.put("taskId",taskDO.getId());
        deviceMap.put("deleteFalg",1);
        List<SafetySpotCheckDeviceInfoDO> delList = safetySpotCheckTaskDOBizMapper.getTaskDevice(deviceMap);
        if(ObjectUtils.isEmpty(delList)){
            return rows;
        }
        List<Integer> deviceIdList = delList.stream().map(SafetySpotCheckDeviceInfoDO::getId).collect(Collectors.toList());
        SafetySpotCheckRecordDOExample example = new SafetySpotCheckRecordDOExample();
        example.createCriteria().andTaskIdEqualTo(taskDO.getId()).andDeviceIdIn(deviceIdList).andStatusEqualTo(0);

        List<SafetySpotCheckRecordDO> checkRecordDOS = safetySpotCheckRecordDOMapper.selectByExample(example);
        if(ObjectUtils.isEmpty(checkRecordDOS)){
            return rows;
        }
        List<Integer> recordRuleIdList = checkRecordDOS.stream().map(SafetySpotCheckRecordDO::getId).collect(Collectors.toList());
        SafetySpotLinkRecordRuleDOExample recordRuleDOExample = new SafetySpotLinkRecordRuleDOExample();
        recordRuleDOExample.createCriteria().andRecordIdIn(recordRuleIdList);
        //删除recordRule方法
        safetySpotLinkRecordRuleDOMapper.deleteByExample(recordRuleDOExample);
        //删除record方法
        safetySpotCheckRecordDOMapper.deleteByExample(example);
        return rows;
    }

    //校验是否使用临时点检人
    private boolean isUseEmpCheck(String beginDate, SafetySpotCheckDeviceInfoDO deviceInfoVO){
        if(deviceInfoVO == null || StringUtils.isEmpty(deviceInfoVO.getProTemBeginDate())
                || StringUtils.isEmpty(deviceInfoVO.getProTemCheckEmp())){
            return false;
        }
        //周期任务开始时间与临时点检人启用时间相同时
        if(deviceInfoVO.getProTemBeginDate().equals(beginDate)){
            return true;
        }
        return DateUtils.loseDayNew(deviceInfoVO.getProTemBeginDate(), beginDate) >= 0
                && DateUtils.loseDayNew(beginDate, deviceInfoVO.getProTemEndDate()) >= 0;
    }

    //查询与当前taskID对应的最新的数据的id
    public List<Map<String,Object>> getRelationNewAndLast(){
        return safetySpotCheckTaskDOMapper.getRelationNewAndLast();
    }

    public void updateStatu(List<Integer> list){
        safetySpotCheckTaskDOMapper.updateStatu(list);
    }
}
