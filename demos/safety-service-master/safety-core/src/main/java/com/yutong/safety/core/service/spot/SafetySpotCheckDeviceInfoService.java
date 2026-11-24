package com.yutong.safety.core.service.spot;

import com.google.common.collect.Sets;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.param.web.spot.SpotCheckBatchDeleteParam;
import com.yutong.safety.common.param.web.spot.SpotCheckBatchUpdateParam;
import com.yutong.safety.common.param.web.spot.SpotCheckDeviceAddParam;
import com.yutong.safety.common.param.web.spot.SpotCheckDeviceListParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.web.SpotCheckDeviceInfoVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDOExample;
import com.yutong.safety.core.db.entity.SafetyFieldManageDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckDeviceInfoDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckDeviceInfoDOExample;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckRuleDO;
import com.yutong.safety.core.db.mapper.SafetyEmployeeMessageDOMapper;
import com.yutong.safety.core.db.mapper.spot.SafetySpotCheckDeviceInfoDOBizMapper;
import com.yutong.safety.core.db.mapper.spot.SafetySpotCheckDeviceInfoDOMapper;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.SafetyFieldManageService;
import com.yutong.safety.core.service.SafetyLogMessageService;
import com.yutong.safety.core.service.SafetyWaterFlowNoService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author gwcuiyx
 * @date 2024-4-28 11:27
 */
@Service
public class SafetySpotCheckDeviceInfoService {
    @Autowired
    private SafetySpotCheckDeviceInfoDOBizMapper safetySpotCheckDeviceInfoDOBizMapper;
    @Autowired
    private SafetySpotCheckDeviceInfoDOMapper safetySpotCheckDeviceInfoDOMapper;
    @Autowired
    private SafetySpotRulePermService safetySpotRulePermService;
    @Autowired
    private SafetyFieldManageService safetyFieldManageService;
    @Autowired
    private SafetySpotCheckRuleService safetySpotCheckRuleService;
    @Autowired
    private SafetyEmployeeMessageDOMapper safetyEmployeeMessageDOMapper;
    @Autowired
    private SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Autowired
    private SafetyWaterFlowNoService safetyWaterFlowNoService;
    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    /**
     * 分页查询接口
     * @param param
     * @return
     */
    public List<SpotCheckDeviceInfoVO> pageList(SpotCheckDeviceListParam param) {
        return safetySpotCheckDeviceInfoDOBizMapper.queryPageList(param, param.getOffset(), param.getLimit());
    }

    /**
     * 分页查询总条数
     * @param param
     * @return
     */
    public long pageListCount(SpotCheckDeviceListParam param) throws Exception{
        return safetySpotCheckDeviceInfoDOBizMapper.pageListCount(param);
    }

    /**
     * 数据导出功能
     * @param param
     * @return
     */
    public List<SpotCheckDeviceInfoVO> export(SpotCheckDeviceListParam param) {
        return safetySpotCheckDeviceInfoDOBizMapper.exportList(param);
    }

    /**
     * 批量删除动作
     * @param param
     * @param token
     */
    public void batchDelete(SpotCheckBatchDeleteParam param, String token) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        Set<String> ids = Sets.newHashSet(param.getIds().split(","));
        List<Integer> idList = ids.stream().map(Integer::parseInt).collect(Collectors.toList());
        SafetySpotCheckDeviceInfoDO safetySpotCheckDeviceInfoDO = new SafetySpotCheckDeviceInfoDO();
        safetySpotCheckDeviceInfoDO.setDeleteFalg(true);
        safetySpotCheckDeviceInfoDO.setUpdateTime(new Date());
        safetySpotCheckDeviceInfoDO.setUpdateEmp(tokenUser.get("realName"));
        safetySpotCheckDeviceInfoDO.setUpdateEmpNo(tokenUser.get("workNo"));
        SafetySpotCheckDeviceInfoDOExample example = new SafetySpotCheckDeviceInfoDOExample();
        example.createCriteria().andIdIn(idList);
        safetySpotCheckDeviceInfoDOBizMapper.updateByExampleSelective(safetySpotCheckDeviceInfoDO, example);
        safetySpotRulePermService.deleteBySpotList(idList);
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    public boolean delete(Integer id, String token) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        SafetySpotCheckDeviceInfoDO safetySpotCheckDeviceInfoDO = safetySpotCheckDeviceInfoDOBizMapper.selectByPrimaryKey(id);
        if(safetySpotCheckDeviceInfoDO == null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        safetySpotCheckDeviceInfoDO.setDeleteFalg(true);
        safetySpotCheckDeviceInfoDO.setUpdateEmpNo(tokenUser.get("workNo"));
        safetySpotCheckDeviceInfoDO.setUpdateEmp(tokenUser.get("realName"));
        safetySpotCheckDeviceInfoDO.setUpdateTime(new Date());
        boolean flag = safetySpotCheckDeviceInfoDOBizMapper.updateByPrimaryKey(safetySpotCheckDeviceInfoDO) > 0;
        safetySpotRulePermService.deleteBySpotId(id);
        return flag;
    }

    /**
     * 数据添加功能
     * @param param 入参
     * @return 返回值
     */
    public void add(SpotCheckDeviceAddParam param, String token) throws Exception{
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        SafetySpotCheckDeviceInfoDO safetySpotCheckDeviceInfoDO = new SafetySpotCheckDeviceInfoDO();
        BeanUtils.copyProperties(param, safetySpotCheckDeviceInfoDO);
        safetySpotCheckDeviceInfoDO.setUpdateTime(new Date());
        safetySpotCheckDeviceInfoDO.setDeleteFalg(false);
        safetySpotCheckDeviceInfoDO.setCreateTime(new Date());
        safetySpotCheckDeviceInfoDO.setUpdateEmp(tokenUser.get("realName"));
        safetySpotCheckDeviceInfoDO.setUpdateEmpNo(tokenUser.get("workNo"));
        //设备编号要自动生成---规则：项目字母缩写（最多取4位）+厂区编号（两位）+流水号（5位）
        SafetyFieldManageDO safetyFieldManageDO = safetyFieldManageService.getInfoById(param.getFactoryId());

        String deviceCode = getDeviceFlowNum(safetyFieldManageDO.getFieldCode(), param.getTeamName());
        LogUtils.info("设备编码deviceCode="+deviceCode);
        if(StringUtils.isBlank(deviceCode)){
            deviceCode = getDeviceFlowNum(safetyFieldManageDO.getFieldCode(), param.getTeamName());
        }
        safetySpotCheckDeviceInfoDO.setDeviceCode(deviceCode);
        PreconditonsEx.checkArgument(safetySpotCheckDeviceInfoDOBizMapper.insert(safetySpotCheckDeviceInfoDO) == 1, ResponseEnum.FAIL);
        //添加关系
        safetySpotRulePermService.insert(safetySpotCheckDeviceInfoDO.getId(), param.getRuleIds());

        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("设备设施点检-设备信息管理");
        log.setAbility("新增设备");
        log.setMessage("新增设备"+deviceCode);
        log.setOperator(tokenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 数据修改功能
     * @return
     */
    public void update(SpotCheckDeviceAddParam param, String token) throws Exception{
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        SafetySpotCheckDeviceInfoDO safetySpotCheckDeviceInfoDO = safetySpotCheckDeviceInfoDOBizMapper.selectByPrimaryKey(param.getId());
        if(safetySpotCheckDeviceInfoDO==null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        BeanUtils.copyProperties(param, safetySpotCheckDeviceInfoDO);
        safetySpotCheckDeviceInfoDO.setUpdateTime(new Date());
        safetySpotCheckDeviceInfoDO.setUpdateEmpNo(tokenUser.get("workNo"));
        safetySpotCheckDeviceInfoDO.setUpdateEmpNo(tokenUser.get("realName"));
        PreconditonsEx.checkArgument(safetySpotCheckDeviceInfoDOBizMapper.updateByPrimaryKeySelective(safetySpotCheckDeviceInfoDO) == 1, ResponseEnum.FAIL);

        //同步更新点检任务记录




        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("设备设施点检-设备信息管理");
        log.setAbility("更新设备");
        log.setMessage("更新设备"+safetySpotCheckDeviceInfoDO+","+JsonUtils.toString(param));
        log.setOperator(tokenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);

        safetySpotRulePermService.insert(param.getId(), param.getRuleIds());
    }

    /**
     * 一键应用临时点检人
     * @param param
     * @param token
     */
    public List<SafetySpotCheckDeviceInfoDO> updateCheckEmp(SpotCheckBatchUpdateParam param, String token) {

        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);

        SafetySpotCheckDeviceInfoDO safetySpotCheckDeviceInfoDO = new SafetySpotCheckDeviceInfoDO();
        safetySpotCheckDeviceInfoDO.setUpdateEmpNo(tokenUser.get("workNo"));
        safetySpotCheckDeviceInfoDO.setUpdateEmp(tokenUser.get("realName"));
        safetySpotCheckDeviceInfoDO.setUpdateTime(new Date());
        safetySpotCheckDeviceInfoDO.setProTemCheckEmp(param.getProTemCheckEmp());
        safetySpotCheckDeviceInfoDO.setProTemCheckEmpNo(param.getProTemCheckEmpNo());
        safetySpotCheckDeviceInfoDO.setProTemBeginDate(param.getProTemBeginDate());
        safetySpotCheckDeviceInfoDO.setProTemEndDate(param.getProTemEndDate());

        Set<String> ids = Sets.newHashSet(param.getIds().split(","));
        List<Integer> idList = ids.stream().map(Integer::parseInt).collect(Collectors.toList());
        SafetySpotCheckDeviceInfoDOExample example = new SafetySpotCheckDeviceInfoDOExample();
        example.createCriteria().andIdIn(idList);
        //获取涉及的设备范围
        List<SafetySpotCheckDeviceInfoDO> deviceList = safetySpotCheckDeviceInfoDOBizMapper.selectByExample(example);
        safetySpotCheckDeviceInfoDOBizMapper.updateByExampleSelective(safetySpotCheckDeviceInfoDO, example);
        String deviceCode = deviceList.stream().map(SafetySpotCheckDeviceInfoDO::getDeviceCode).collect(Collectors.joining(",")).toString();

        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("设备设施点检-设备信息管理");
        log.setAbility("设置临时点检人");
        log.setMessage("设备编号："+deviceCode+"临时点检人设置为"+param.getProTemCheckEmp()+",时间"+param.getProTemBeginDate()+"-"+param.getProTemEndDate());
        log.setOperator(tokenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);

        return deviceList;
    }

    public void updateCheckEmpData(List<String> idList){
        safetySpotCheckDeviceInfoDOMapper.updateCheckEmpData(idList);
    }

    /**
     * 一键取消临时点检人
     * @param param
     * @param token
     */
    public List<SafetySpotCheckDeviceInfoDO> batchCancelCheckEmp(SpotCheckBatchUpdateParam param, String token) {

        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);

        SafetySpotCheckDeviceInfoDO safetySpotCheckDeviceInfoDO = new SafetySpotCheckDeviceInfoDO();
        safetySpotCheckDeviceInfoDO.setUpdateEmpNo(tokenUser.get("workNo"));
        safetySpotCheckDeviceInfoDO.setUpdateEmp(tokenUser.get("realName"));
        safetySpotCheckDeviceInfoDO.setUpdateTime(new Date());
        safetySpotCheckDeviceInfoDO.setProTemCheckEmp(null);
        safetySpotCheckDeviceInfoDO.setProTemCheckEmpNo(null);
        safetySpotCheckDeviceInfoDO.setProTemBeginDate(null);
        safetySpotCheckDeviceInfoDO.setProTemEndDate(null);

        Set<String> ids = Sets.newHashSet(param.getIds().split(","));
        List<Integer> idList = ids.stream().map(Integer::parseInt).collect(Collectors.toList());
        SafetySpotCheckDeviceInfoDOExample example = new SafetySpotCheckDeviceInfoDOExample();
        example.createCriteria().andIdIn(idList);
        //获取涉及的设备范围
        List<SafetySpotCheckDeviceInfoDO> deviceList = safetySpotCheckDeviceInfoDOBizMapper.selectByExample(example);
        if(deviceList != null && deviceList.size() > 0){
            safetySpotCheckDeviceInfoDOBizMapper.batchCancelCheckEmp(safetySpotCheckDeviceInfoDO, idList);
            String deviceCode = deviceList.stream().map(SafetySpotCheckDeviceInfoDO::getDeviceCode).collect(Collectors.joining(",")).toString();

            //操作日志记录
            LogMessageAddParam log = new LogMessageAddParam();
            log.setOperatePage("设备设施点检-设备信息管理");
            log.setAbility("取消临时点检人");
            log.setMessage("设备编号："+deviceCode+"取消练市点检人,时间"+param.getProTemBeginDate()+"-"+param.getProTemEndDate());
            log.setOperator(tokenUser.get("realName"));
            safetyLogMessageService.addLogMessage(log);
        }
        return deviceList;
    }

    /**
     * 数据导入操作
     * @param param
     * @return
     */
    public Object importSpotCheckDevice(CommonImportParam param, String token) throws Exception{
        Workbook workbook = ExcelUtils.getWorkbook(param.getFile());
        Sheet sheet = workbook.getSheetAt(0);
        //获得当前sheet的开始行
        int firstRowNum  = sheet.getFirstRowNum();
        //获得当前sheet的结束行
        int lastRowNum = sheet.getLastRowNum();
        String result = "";
        //获取字段管理里面厂区和区域的数据
        Map<String, SafetyFieldManageDO> factoryFieldMaps = safetyFieldManageService.getFactoryList();
        Map<String, SafetySpotCheckRuleDO> ruleMaps = safetySpotCheckRuleService.getRuleList();
        Map<String, SafetyFieldManageDO> manageDOMaps = safetyFieldManageService.getSpotTeams();
        List<SafetySpotCheckDeviceInfoDO> insertList = Lists.newArrayList();
        List<String> paramList = Lists.newArrayList();
        //根据tocken当前用户人员
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        boolean flag = false;
        int rum = 0;
        String expenseMessage = "";

        //设备序号
        Long flowNum = getFlowNum();
        //点检人集合，方便统一查询
        List<String> checkWorkNoList = new ArrayList<>();
        SafetyFieldManageDO safetyFieldManageDO;
        SafetySpotCheckDeviceInfoDO spotCheckDeviceInfoDO;
        for(int rowNum = firstRowNum+1; rowNum <= lastRowNum; rowNum++) {
            //获得当前行
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            try {
                flowNum++;
                String factoryName = ExcelUtils.getValue(row.getCell(1));//厂区
                String deviceType = ExcelUtils.getValue(row.getCell(2));//类型
                String teamName = ExcelUtils.getValue(row.getCell(3));//项目
                String deviceName = ExcelUtils.getValue(row.getCell(4));//设备名称
                String location = ExcelUtils.getValue(row.getCell(5));//位置
                String dutyName = ExcelUtils.getValue(row.getCell(6));//责任部门
                String checkEmpNo =  ExcelUtils.getValue(row.getCell(7));//点检人员工号
                String rules = ExcelUtils.getValue(row.getCell(8));//应用巡检标准
                String remark = ExcelUtils.getValue(row.getCell(9));//备注
                if(StringUtils.isEmpty(factoryName) && StringUtils.isEmpty(deviceType) && StringUtils.isEmpty(teamName) && StringUtils.isEmpty(deviceName) && StringUtils.isEmpty(location) && StringUtils.isEmpty(checkEmpNo) && StringUtils.isEmpty(rules)){
                    continue;
                }
                if(StringUtils.isEmpty(factoryName) || StringUtils.isEmpty(deviceType) || StringUtils.isEmpty(teamName) || StringUtils.isEmpty(deviceName) || StringUtils.isEmpty(location) || StringUtils.isEmpty(checkEmpNo) && StringUtils.isEmpty(rules)){
                    rum = rowNum;
                    flag = true;
                    expenseMessage = "第"+rum+"行，有必填项为空！请检查后再导入数据！";
                    break;
                }
                spotCheckDeviceInfoDO = new SafetySpotCheckDeviceInfoDO();
                if (factoryFieldMaps.containsKey(factoryName)) {
                    safetyFieldManageDO = factoryFieldMaps.get(factoryName);
                    spotCheckDeviceInfoDO.setFactoryId(safetyFieldManageDO.getId());
                } else {
                    rum = rowNum;
                    flag = true;
                    expenseMessage = "第"+rum+"行，“厂区”数据对照有误！请检查后再导入数据！";
                    break;
                }
                spotCheckDeviceInfoDO.setDeviceType(deviceType);
                if (manageDOMaps.containsKey(deviceType)) {
                    spotCheckDeviceInfoDO.setDeviceFieldCode(manageDOMaps.get(deviceType).getId().toString());
                } else {
                    rum = rowNum;
                    flag = true;
                    expenseMessage = "第"+rum+"行，“类型”数据对照有误！请检查后再导入数据！";
                    break;
                }
                spotCheckDeviceInfoDO.setTeamName(teamName);
                if (manageDOMaps.containsKey(teamName)) {
                    spotCheckDeviceInfoDO.setTeamId(manageDOMaps.get(teamName).getId());
                } else {
                    rum = rowNum;
                    flag = true;
                    expenseMessage = "第"+rum+"行，“项目”数据对照有误！请检查后再导入数据！";
                    break;
                }
                spotCheckDeviceInfoDO.setDeleteFalg(false);
                spotCheckDeviceInfoDO.setDeviceName(deviceName);
                //设备编号要自动生成---规则：项目字母缩写（最多取4位）+厂区编号（两位）+流水号（5位）
                String deviceCode = getDeviceFlowNum(safetyFieldManageDO.getFieldCode(), teamName);
                spotCheckDeviceInfoDO.setDeviceCode(deviceCode);
                spotCheckDeviceInfoDO.setLocation(location);
                spotCheckDeviceInfoDO.setDutyName(dutyName);
                spotCheckDeviceInfoDO.setCheckEmpNo(checkEmpNo);
                //根据点检人员工号获取员工信息
                //String realName = getEmployeeByWorkNo(checkEmpNo).getEmpName();
                //spotCheckDeviceInfoDO.setCheckEmp(realName);
                //设备巡检标准分解解析
                String ruleIds = getRuleIds(ruleMaps, rules, teamName);
                if(StringUtils.isBlank(ruleIds)){
                    rum = rowNum;
                    flag = true;
                    expenseMessage = "第"+rum+"行，“巡检标准”未匹配到ID，请核对巡检表中内容！";
                    break;
                }
                paramList.add(ruleIds);
                spotCheckDeviceInfoDO.setRemark(remark);
                spotCheckDeviceInfoDO.setUpdateTime(new Date());
                spotCheckDeviceInfoDO.setUpdateEmp(tokenUser.get("realName"));
                spotCheckDeviceInfoDO.setUpdateEmpNo(tokenUser.get("workNo"));
                spotCheckDeviceInfoDO.setCreateTime(new Date());
                insertList.add(spotCheckDeviceInfoDO);
                //员工号统一检索员工台账
                checkWorkNoList.add(checkEmpNo);
            } catch (Exception e) {
                LogUtils.error("数据第"+rowNum+"导入行读取异常",e);
            }
        }
        if (flag) {
            throw new BizException(ResponseEnum.FAIL.getCode(), expenseMessage);
        }
        if (CollectionUtils.isNotEmpty(insertList)) {
            //获取导入的点检人集合
            Map<String,SafetyEmployeeMessageDO> employeeMaps = this.getEmployeeByWorkNo(checkWorkNoList);

            for (int j = 0; j < insertList.size(); j++) {
                //设置点检人姓名
                SafetySpotCheckDeviceInfoDO deviceInfoDO = insertList.get(j);
                String workNo = deviceInfoDO.getCheckEmpNo();
                String checkEmp = employeeMaps.get(workNo)==null?"":employeeMaps.get(workNo).getEmpName();
                if(StringUtils.isBlank(checkEmp)){
                    throw new BizException(ResponseEnum.FAIL.getCode(), "点检人员工号“"+workNo+"”,为找到对应员工信息！");
                }
                deviceInfoDO.setCheckEmp(checkEmp);
                addAndRule(insertList.get(j), token, paramList.get(j));
            }
        }
        result = "导入成功:"+insertList.size()+"条!";
        return result;
    }

    /**
     * 简单获取流水号
     * @return
     */
    public Long getFlowNum() {
        SafetySpotCheckDeviceInfoDOExample example = new SafetySpotCheckDeviceInfoDOExample();
        return safetySpotCheckDeviceInfoDOBizMapper.countByExample(example);
    }

    /**
     * 组装设备编号
     * @param code 编码
     * @return
     */
    public String getDeviceFlowNum(String code,String deviceName) {
        //根据中文名称获取字母缩写
        String pinYin = PinYinUtils.getPinYinHeadChar(deviceName);
        if (pinYin.length() > 4) {
            pinYin = pinYin.substring(0, 4);
        }
        String flowPre = pinYin + code;
        String nowNum = safetyWaterFlowNoService.getNowByTypeNoDay(flowPre);
        if (nowNum.length() < 5) {
            nowNum = String.format("%0" + 5 + "d", Integer.parseInt(nowNum));
        }
        return flowPre + nowNum;
    }

    /**
     * 根据数据做相应的数据对应工作
     * @param maps 入参
     * @param rules 标准ID拼接串
     * @return 返回值
     */
    public String getRuleIds(Map<String, SafetySpotCheckRuleDO> maps, String rules, String teamName) {
        String[] ruleArray = rules.split(";");
        String name = "";
        String resultStr = "";
        for (String s : ruleArray) {
            name = teamName + "_" + s;
            if (maps.containsKey(name)) {
                resultStr += maps.get(name).getId() + ",";
            }
        }
        return StringUtils.isEmpty(resultStr) ? "" : resultStr.substring(0, resultStr.length()-1);
    }

    /**
     * 数据添加功能
     * @param deviceInfoDO 设备DO
     * @param token 登录token
     * @param ruleIds 标准ID拼接串
     */
    public void addAndRule(SafetySpotCheckDeviceInfoDO deviceInfoDO, String token, String ruleIds) throws Exception{
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        //根据身份证号做数据匹配，一个身份证号只能有一条数据
        deviceInfoDO.setUpdateTime(new Date());
        deviceInfoDO.setDeleteFalg(false);
        deviceInfoDO.setCreateTime(new Date());
        deviceInfoDO.setUpdateEmp(tokenUser.get("realName"));
        deviceInfoDO.setUpdateEmpNo(tokenUser.get("workNo"));
        PreconditonsEx.checkArgument(safetySpotCheckDeviceInfoDOBizMapper.insert(deviceInfoDO) == 1, ResponseEnum.FAIL);
        //添加关系
        safetySpotRulePermService.insert(deviceInfoDO.getId(), ruleIds);
    }

    /**
     * 根据员工号获取员工信息
     * @param workNoList 员工号
     * @return
     */
    public Map<String,SafetyEmployeeMessageDO> getEmployeeByWorkNo(List<String> workNoList){
        SafetyEmployeeMessageDOExample example = new SafetyEmployeeMessageDOExample();
        example.createCriteria().andEmpNoIn(workNoList);

        List<SafetyEmployeeMessageDO> list = safetyEmployeeMessageDOMapper.selectByExample(example);
        return list.stream().collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, Function.identity()));
    }

    /**
     * 获取设备当前点检热信息
     * @param deviceId
     * @param curDate
     * @return
     */
    public Map<String,String> getPlanCheckEmp(String deviceId,String curDate){
        if(StringUtils.isBlank(curDate)){
            curDate = DateUtils.format(new Date(),1);
        }
        Map<String,String> checkEmpMap = new HashMap<>();
        SafetySpotCheckDeviceInfoDO deviceInfoDO = safetySpotCheckDeviceInfoDOMapper.selectByPrimaryKey(Integer.parseInt(deviceId));
        if(deviceInfoDO == null){
            return checkEmpMap;
        }
        checkEmpMap.put("planCheckEmp",deviceInfoDO.getCheckEmp());
        checkEmpMap.put("planCheckEmpNo",deviceInfoDO.getCheckEmpNo());

        if(StringUtils.isNotBlank(deviceInfoDO.getProTemBeginDate())
                && StringUtils.isNotBlank(deviceInfoDO.getProTemEndDate())
                && StringUtils.isNotBlank(deviceInfoDO.getProTemCheckEmpNo())){

            String proTemBegin = deviceInfoDO.getProTemBeginDate();
            String proTemEnd = deviceInfoDO.getProTemEndDate();
            long bDiff = DateUtils.loseDayNew(proTemBegin,curDate);
            long eDiff = DateUtils.loseDayNew(curDate,proTemEnd);
            if(bDiff >=0 && eDiff >= 0){
                checkEmpMap.put("planCheckEmp",deviceInfoDO.getProTemCheckEmp());
                checkEmpMap.put("planCheckEmpNo",deviceInfoDO.getProTemCheckEmpNo());
            }
        }

        //获取用户的用户名
        String planCheckEmpNo = checkEmpMap.get("planCheckEmpNo");

        if(StringUtils.isNotBlank(planCheckEmpNo)){
            //临时点检人是否是多人
            String[] split = planCheckEmpNo.split(",");
            StringBuilder planLoginName = new StringBuilder();

            for (String s : split) {
                String loginNameByNo = safetyEmployeeMessageService.getLoginNameByNo(s);
                planLoginName.append(StringUtils.isEmpty(loginNameByNo) ? "" : loginNameByNo + ",");
            }
            if(StringUtils.isNotBlank(planLoginName.toString())){
                checkEmpMap.put("planCheckEmpLogin",planLoginName.substring(0, planLoginName.length()-1));
            }
        }
        return checkEmpMap;
    }

    /**
     * 获取列表
     * @param listParam 入参
     * @return 返回值
     */
    public List<SafetySpotCheckDeviceInfoDO> selectList(SpotCheckDeviceListParam listParam){
        SafetySpotCheckDeviceInfoDOExample example = new SafetySpotCheckDeviceInfoDOExample();
        SafetySpotCheckDeviceInfoDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if(StringUtils.isNotBlank(listParam.getProEndDate())){
            criteria.andProTemEndDateEqualTo(listParam.getProEndDate());
        }
        if(listParam.getFactoryId() != null){
            criteria.andFactoryIdEqualTo(listParam.getFactoryId());
        }
        if(listParam.getTeamId() != null){
            criteria.andTeamIdEqualTo(listParam.getTeamId());
        }
        return safetySpotCheckDeviceInfoDOMapper.selectByExample(example);
    }

    /**
     * 根据id获取设备信息
     * @param id
     * @return
     */
    public SafetySpotCheckDeviceInfoDO getById(Integer id){
        return safetySpotCheckDeviceInfoDOMapper.selectByPrimaryKey(id);
    }

    public void batchUpdateCheckEmp(List<String> list, SafetySpotCheckDeviceInfoDO entity){
        safetySpotCheckDeviceInfoDOMapper.batchUpdateCheckEmp(list, entity);
    }
}
