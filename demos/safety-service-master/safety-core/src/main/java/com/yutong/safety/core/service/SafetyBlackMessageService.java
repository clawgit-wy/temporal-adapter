package com.yutong.safety.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.HikMiddleTableSqlEnum;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.app.BlackDepartAddParam;
import com.yutong.safety.common.param.web.*;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.web.HkMiddleTableVO;
import com.yutong.safety.core.db.entity.SafetyBlackMessageDO;
import com.yutong.safety.core.db.entity.SafetyBlackMessageDOExample;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikOperationGroupDO;
import com.yutong.safety.core.db.mapper.SafetyBlackManageDOBizMapper;
import com.yutong.safety.core.db.mapper.SafetyBlackMessageDOMapper;
import com.yutong.safety.core.db.mapper.SafetyEmployeeMessageDOBizMapper;
import com.yutong.safety.core.dto.BlackMessageDto;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.service.hik.SafetyArtemisService;
import com.yutong.safety.core.service.hik.SafetyHikOperationGroupService;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.joda.time.LocalDateTime;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


/**
 *  create gwcuiyx 2023.07.10
 *  黑名单信息管理--service
 */
@Service
public class SafetyBlackMessageService {

    @Autowired
    private SafetyBlackMessageDOMapper safetyBlackMessageDOMapper;
    @Autowired
    private SafetyBlackManageDOBizMapper safetyBlackManageDOBizMapper;
    @Autowired
    private SafetyLogMessageService safetyLogMessageService;
    @Value("${iam.blackMessage.url}")
    private String iamBlackUrl;
    @Autowired
    private WebserviceUtil webserviceUtil;
    @Autowired
    private SafetyExternalDetectionService safetyExternalDetectionService;
    @Autowired
    private SafetyEmployeeMessageDOBizMapper safetyEmployeeMessageDOBizMapper;

    @Resource
    public SafetyArtemisService safetyArtemisService;
    @Resource
    public SafetyHikOperationGroupService safetyHikOperationGroupService;
    @Resource
    public SafetyBaseHandler safetyBaseHandler;

    /**
     * 海康默认的在职人员的组织编码
     */
    private static final String HIK_DEFAULT_ORG_CODE = "ZZRY0001";

    /**
     * 数据列表-fenye
     * @param param
     * @return
     */
    public List<SafetyBlackMessageDO> pageList(BlackMessageListParam param) {
        SafetyBlackMessageDOExample example = formatExample(param);
        example.setOrderByClause(" create_time desc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        return safetyBlackMessageDOMapper.selectByExample(example);
    }

    /**
     * bufenye
     * @param param
     * @return
     */
    public List<SafetyBlackMessageDO> selectList(BlackMessageListParam param) {
        SafetyBlackMessageDOExample example = formatExample(param);
        example.setOrderByClause(" create_time desc ");
        return safetyBlackMessageDOMapper.selectByExample(example);
    }


    public List<SafetyBlackMessageDO> selectUpdateList(Date start, Date end) {

        SafetyBlackMessageDOExample example = new SafetyBlackMessageDOExample();
        SafetyBlackMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andUpdateTimeBetween(start, end);
        return safetyBlackMessageDOMapper.selectByExample(example);
    }

    public List<SafetyBlackMessageDO> selectToHkList(Integer beginId, Integer endId) {
        SafetyBlackMessageDOExample example = new SafetyBlackMessageDOExample();
        SafetyBlackMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andPhotoIsNotNull();
        criteria.andIdBetween(beginId, endId);
        return safetyBlackMessageDOMapper.selectByExample(example);
    }

    private SafetyBlackMessageDOExample formatExample(BlackMessageListParam param){
        SafetyBlackMessageDOExample example = new SafetyBlackMessageDOExample();
        SafetyBlackMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if (StringUtils.isNotEmpty(param.getCompanyName())) {
            criteria.andCompanyNameLike("%"+param.getCompanyName()+"%");
        }
        if(StringUtils.isNotEmpty(param.getEmpName())) {
            criteria.andEmpNameLike("%"+param.getEmpName()+"%");
        }
        if(StringUtils.isNotEmpty(param.getCardNo())) {
            criteria.andCardNoLike("%"+param.getCardNo()+"%");
        }
        if(StringUtils.isNotEmpty(param.getPhoneNo())) {
            criteria.andPhoneNoLike("%"+param.getPhoneNo()+"%");
        }
        if(StringUtils.isNotEmpty(param.getIdCardNo())) {
            criteria.andIdCardNoLike("%"+param.getIdCardNo()+"%");
        }
        if(StringUtils.isNotEmpty(param.getCarNo())) {
            criteria.andCarNoLike("%"+param.getCarNo()+"%");
        }
        if(StringUtils.isNotEmpty(param.getOperator())) {
            criteria.andOperatorLike("%"+param.getOperator()+"%");
        }
        if (StringUtils.isNotEmpty(param.getEmpType())) {
            criteria.andEmpTypeEqualTo(param.getEmpType());
        }
        if(StringUtils.isNotEmpty(param.getStartTime())) {
            String start = param.getStartTime() + " 00:00:00";
            criteria.andUpdateTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
        }
        if(StringUtils.isNotEmpty(param.getEndTime())) {
            String end = param.getEndTime() + " 23:59:59";
            criteria.andUpdateTimeLessThanOrEqualTo(DateUtils.parse(end,2));
        }
        if (StringUtils.isNotEmpty(param.getSource())) {
            criteria.andSourceEqualTo(param.getSource());
        }
        if(Objects.nonNull(param.getState())) {
            criteria.andStateEqualTo(param.getState());
        }
        if(!CollectionUtils.isEmpty(param.getCardNoList())){
            criteria.andCardNoIn(param.getCardNoList());
        }
        if(!CollectionUtils.isEmpty(param.getIdCardNoList())){
            criteria.andIdCardNoIn(param.getIdCardNoList());
        }
        if (!CollectionUtils.isEmpty(param.getPositionTypes()) && Boolean.TRUE.equals(param.isNullPositionType())) {
            criteria.andPositionTypeInAndIsNull(param.getPositionTypes());
        }
        if (!CollectionUtils.isEmpty(param.getPositionTypes()) && Boolean.FALSE.equals(param.isNullPositionType())) {
            criteria.andPositionTypeIn(param.getPositionTypes());
        }
        if (CollectionUtils.isEmpty(param.getPositionTypes()) && Boolean.TRUE.equals(param.isNullPositionType())) {
            criteria.andPositionTypeIsNull();
        }
        if(Boolean.TRUE.equals(param.getAuthorizationDateIsNotNull())){
            criteria.andAuthorizationDateNotNull();
        }
        if(StringUtils.isNotBlank(param.getLessAuthorizationDate())){
            criteria.andAuthorizationDateLessThan(param.getLessAuthorizationDate());
        }
        return example;
    }

    /**
     * 总数据量
     * @param param
     * @return
     */
    public long pageListCount(BlackMessageListParam param) {
        SafetyBlackMessageDOExample example = formatExample(param);
        return safetyBlackMessageDOMapper.countByExample(example);
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    public boolean delete(Integer id, String tocken) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(tocken);
        SafetyBlackMessageDO safetyBlackMessageDO = safetyBlackMessageDOMapper.selectByPrimaryKey(id);
        if(safetyBlackMessageDO == null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        safetyBlackMessageDO.setDeleteFalg(true);
        safetyBlackMessageDO.setOperator(tokenUser.get("realName"));
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("黑名单");
        log.setAbility("删除");
        log.setMessage(safetyBlackMessageDO.getEmpName());
        log.setOperator(tokenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        return safetyBlackMessageDOMapper.updateByPrimaryKey(safetyBlackMessageDO) > 0;
    }

    /**
     * 数据修改功能
     * @return
     */
    public void update(BlackMessageUpdateParam param, String tocken) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(tocken);
        SafetyBlackMessageDO safetyBlackMessageDO = safetyBlackMessageDOMapper.selectByPrimaryKey(param.getId());
        if(safetyBlackMessageDO==null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        BeanUtils.copyProperties(param, safetyBlackMessageDO);
        safetyBlackMessageDO.setUpdateTime(new Date());
        safetyBlackMessageDO.setOperator(tokenUser.get("realName"));
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("黑名单");
        log.setAbility("修改");
        log.setMessage(safetyBlackMessageDO.getEmpName());
        log.setOperator(tokenUser.get("realName"));
        if (StringUtils.equals(safetyBlackMessageDO.getPositionType(), "无")) {
            safetyBlackMessageDO.setPositionType(null);
        }
        safetyLogMessageService.addLogMessage(log);
        PreconditonsEx.checkArgument(safetyBlackMessageDOMapper.updateByPrimaryKey(safetyBlackMessageDO) == 1, ResponseEnum.FAIL);
    }

    /**
     * 数据添加功能
     * @param param
     * @return
     */
    public void add(BlackMessageAddParam param, String token) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        //根据身份证号做数据匹配，一个身份证号只能有一条数据
        SafetyBlackMessageDO safetyBlackMessageDO = getBlackMessage(param.getIdCardNo());
        if(safetyBlackMessageDO == null) {
            safetyBlackMessageDO = new SafetyBlackMessageDO();
            safetyBlackMessageDO.setCreateTime(new Date());
        } else {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该身份证号已存在！");
        }
        BeanUtils.copyProperties(param, safetyBlackMessageDO);
        safetyBlackMessageDO.setUpdateTime(new Date());
        safetyBlackMessageDO.setDeleteFalg(false);
        safetyBlackMessageDO.setSource("本地添加");
        safetyBlackMessageDO.setOperator(tokenUser.get("realName"));
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("黑名单");
        log.setAbility("添加");
        log.setMessage(safetyBlackMessageDO.getEmpName());
        log.setOperator(tokenUser.get("realName"));
        if (StringUtils.equals(safetyBlackMessageDO.getPositionType(), "无")) {
            safetyBlackMessageDO.setPositionType(null);
        }
        safetyLogMessageService.addLogMessage(log);
        PreconditonsEx.checkArgument(safetyBlackMessageDOMapper.insert(safetyBlackMessageDO) == 1, ResponseEnum.FAIL);
    }

    /**
     * 根据身份证号获取数据
     * @param idCardNo
     * @return
     */
    public SafetyBlackMessageDO getBlackMessage(String idCardNo) {
        SafetyBlackMessageDOExample example = new SafetyBlackMessageDOExample();
        SafetyBlackMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andIdCardNoEqualTo(idCardNo);
        List<SafetyBlackMessageDO> list = safetyBlackMessageDOMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 数据展示
     * @param id
     * @return
     */
    public SafetyBlackMessageDO view(Integer id) {
        SafetyBlackMessageDO safetyBlackMessageDO = safetyBlackMessageDOMapper.selectByPrimaryKey(id);
        if(safetyBlackMessageDO==null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        return safetyBlackMessageDO;
    }

    /**
     * 导出数据操作
     * @param param
     * @return
     */
    public List<SafetyBlackMessageDO> export(BlackMessageListParam param, String tocken) {
        SafetyBlackMessageDOExample example = new SafetyBlackMessageDOExample();
        SafetyBlackMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        String searchStr = "";
        if (StringUtils.isNotEmpty(param.getCompanyName())) {
            criteria.andCompanyNameLike("%"+param.getCompanyName()+"%");
            searchStr += param.getCompanyName() + ";";
        }
        if(StringUtils.isNotEmpty(param.getEmpName())) {
            criteria.andEmpNameLike("%"+param.getEmpName()+"%");
            searchStr += param.getEmpName() + ";";
        }
        if(StringUtils.isNotEmpty(param.getCardNo())) {
            criteria.andCardNoLike("%"+param.getCardNo()+"%");
            searchStr += param.getCardNo() + ";";
        }
        if(StringUtils.isNotEmpty(param.getPhoneNo())) {
            criteria.andPhoneNoLike("%"+param.getPhoneNo()+"%");
            searchStr += param.getPhoneNo() + ";";
        }
        if(StringUtils.isNotEmpty(param.getIdCardNo())) {
            criteria.andIdCardNoLike("%"+param.getIdCardNo()+"%");
            searchStr += param.getIdCardNo() + ";";
        }
        if(StringUtils.isNotEmpty(param.getCarNo())) {
            criteria.andCarNoLike("%"+param.getCarNo()+"%");
            searchStr += param.getCarNo() + ";";
        }
        if(StringUtils.isNotEmpty(param.getOperator())) {
            criteria.andOperatorLike("%"+param.getOperator()+"%");
            searchStr += param.getOperator() + ";";
        }
        if (StringUtils.isNotEmpty(param.getEmpType())) {
            criteria.andEmpTypeEqualTo(param.getEmpType());
            searchStr += param.getEmpType() + ";";
        }
        if(StringUtils.isNotEmpty(param.getStartTime())) {
            String start = param.getStartTime() + " 0:0:0";
            criteria.andUpdateTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
            searchStr += param.getStartTime() + ";";
        }
        if(StringUtils.isNotEmpty(param.getEndTime())) {
            String end = param.getEndTime() + " 23:59:59";
            criteria.andUpdateTimeLessThanOrEqualTo(DateUtils.parse(end,2));
            searchStr += param.getEndTime() + ";";
        }
        if (StringUtils.isNotEmpty(param.getSource())) {
            criteria.andSourceEqualTo(param.getSource());
            searchStr += param.getSource() + ";";
        }
        if(Objects.nonNull(param.getState())) {
            criteria.andStateEqualTo(param.getState());
            searchStr += param.getState() + ";";
        }
        if(!CollectionUtils.isEmpty(param.getCardNoList())){
            criteria.andCardNoIn(param.getCardNoList());
        }
        if (!CollectionUtils.isEmpty(param.getPositionTypes()) && Boolean.TRUE.equals(param.isNullPositionType())) {
            criteria.andPositionTypeInAndIsNull(param.getPositionTypes());
        }
        if (!CollectionUtils.isEmpty(param.getPositionTypes()) && Boolean.FALSE.equals(param.isNullPositionType())) {
            criteria.andPositionTypeIn(param.getPositionTypes());
        }
        if (CollectionUtils.isEmpty(param.getPositionTypes()) && Boolean.TRUE.equals(param.isNullPositionType())) {
            criteria.andPositionTypeIsNull();
        }
        example.setOrderByClause(" create_time desc ");

        //根据tocken当前用户人员
        Map<String, String> tokenUser = TokenUtils.getTockenUser(tocken);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("黑名单");
        log.setAbility("导出");
        log.setMessage(StringUtils.isNotEmpty(searchStr) ? searchStr : "所有");
        log.setOperator(tokenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        return safetyBlackMessageDOMapper.selectByExample(example);
    }

    /**
     * 数据导入操作
     * @param param
     * @return
     */
    public Object importBlackMessage(CommonImportParam param, String tocken) {
        Workbook workbook = ExcelUtils.getWorkbook(param.getFile());
        Sheet sheet = workbook.getSheetAt(0);
        //获得当前sheet的开始行
        int firstRowNum  = sheet.getFirstRowNum();
        //获得当前sheet的结束行
        int lastRowNum = sheet.getLastRowNum();
        String result = "";
        List<SafetyBlackMessageDO> insertList = Lists.newArrayList();
        SafetyBlackMessageDO blackMessageDO = null;
        SafetyBlackMessageDO safetyBlackMessageDO = null;
        //根据tocken当前用户人员
        Map<String, String> tokenUser = TokenUtils.getTockenUser(tocken);
        boolean flag = false;
        int rum = 0;
        String expenseMessage = "";
        for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++) {
            //获得当前行
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            try {
                String companyName = ExcelUtils.getValue(row.getCell(1));//公司
                String empName = ExcelUtils.getValue(row.getCell(2));//姓名
                String cardNo = ExcelUtils.getValue(row.getCell(3));//卡号
                String phoneNo = ExcelUtils.getValue(row.getCell(4));//手机号
                String idCardNo = ExcelUtils.getValue(row.getCell(5));//身份证号
                String empType = ExcelUtils.getValue(row.getCell(6));//人员类型
                String carNo =  ExcelUtils.getValue(row.getCell(7));//车辆
                String state = ExcelUtils.getValue(row.getCell(8));//状态
                String remark = ExcelUtils.getValue(row.getCell(9));//备注
                if(StringUtils.isEmpty(companyName) && StringUtils.isEmpty(empName) && StringUtils.isEmpty(phoneNo) && StringUtils.isEmpty(idCardNo) && StringUtils.isEmpty(empType) && StringUtils.isEmpty(state)){
                    continue;
                }
                if(StringUtils.isEmpty(companyName) || StringUtils.isEmpty(empName) || StringUtils.isEmpty(phoneNo) || StringUtils.isEmpty(idCardNo) || StringUtils.isEmpty(empType) || StringUtils.isEmpty(state)){
                    rum = rowNum;
                    flag = true;
                    expenseMessage = "第"+rum+"行，有必填项为空！请检查后再导入数据！";
                    break;
                }
                safetyBlackMessageDO = getBlackMessage(idCardNo);
                if (safetyBlackMessageDO != null) {
                    rum = rowNum;
                    flag = true;
                    expenseMessage = "第"+rum+"行，身份证号重复！请检查后再导入数据！";
                    break;
                }
                blackMessageDO = new SafetyBlackMessageDO();
                blackMessageDO.setCompanyName(companyName);
                blackMessageDO.setEmpName(empName);
                blackMessageDO.setCardNo(cardNo);
                blackMessageDO.setPhoneNo(phoneNo);
                blackMessageDO.setIdCardNo(idCardNo);
                blackMessageDO.setEmpType(empType);
                blackMessageDO.setCreateTime(new Date());
                blackMessageDO.setUpdateTime(new Date());
                blackMessageDO.setCarNo(carNo);
                blackMessageDO.setSource("本地添加");
                blackMessageDO.setState("禁止入厂".equals(state) ? 1 : (StringUtils.isEmpty(state) ? 1 : 2));
                blackMessageDO.setOperator(tokenUser.get("realName"));
                blackMessageDO.setRemark(remark);
                insertList.add(blackMessageDO);
            } catch (Exception e) {
                LogUtils.error("数据第"+rowNum+"导入行读取异常",e);
            }
        }
        if (flag) {
            throw new BizException(ResponseEnum.FAIL.getCode(), expenseMessage);
        }
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("黑名单");
        log.setAbility("导入");
        log.setMessage("导入");
        log.setOperator(tokenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        if(!CollectionUtils.isEmpty(insertList)){
            List<String> idCardds = insertList.stream().map(SafetyBlackMessageDO::getIdCardNo).collect(Collectors.toList());
            String idCardsExits = checkCardNoExist(idCardds);
            if(StringUtils.isNotEmpty(idCardsExits)) {
                throw new BizException(ResponseEnum.FAIL.getCode(),"以下身份证号已存在："+idCardsExits);
            } else {
                safetyBlackManageDOBizMapper.batchInsert(insertList);
            }
        }
        result = "导入成功:"+insertList.size()+"条!";
        return result;
    }

    /**
     * 检查数据是否存在
     * @param idCards
     * @return
     */
    public String checkCardNoExist(List<String> idCards) {
        SafetyBlackMessageDOExample example = new SafetyBlackMessageDOExample();
        SafetyBlackMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andIdCardNoIn(idCards);
        criteria.andDeleteFalgEqualTo(false);
        List<SafetyBlackMessageDO> list = safetyBlackMessageDOMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            List<String> idCardds = list.stream().map(SafetyBlackMessageDO::getIdCardNo).collect(Collectors.toList());
            return idCardds.toString();
        }
        return null;
    }

    /**
     * 检查是否在黑名单里面
     * @param idCardNo
     * @return
     */
    public boolean check(String idCardNo) {
        SafetyBlackMessageDOExample example = new SafetyBlackMessageDOExample();
        SafetyBlackMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andIdCardNoEqualTo(idCardNo);
        criteria.andStateEqualTo(1);
        return safetyBlackMessageDOMapper.countByExample(example) > 0 ? true : false;
    }

    /**
     * 校验车牌号
     * @param cardNo
     * @return
     */
    public boolean checkCardNo(String cardNo) {
        SafetyBlackMessageDOExample example = new SafetyBlackMessageDOExample();
        SafetyBlackMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andCardNoEqualTo(cardNo);
        criteria.andStateEqualTo(1);
        return safetyBlackMessageDOMapper.countByExample(example) > 0 ? true : false;
    }

    /**
     * 增量同步或者全量同步数据
     * @param isAll
     * @param dateTime iamBlackUrl
     */
    public void tasksMessage(Boolean isAll, String dateTime) {
        if (StringUtils.isEmpty(dateTime)) {
            //前一天
            dateTime = DateUtils.format(LocalDateTime.now().minusDays(1).toDate(), 1);
        }
        //第一页的数据
        String url = String.format(iamBlackUrl, dateTime, isAll, 1);
        //定义总页数
        int pageSize = 0;
        //根据第一条数据，循环后边的数据
        String result = webserviceUtil.sendHttpForGet(url);
        JSONObject userJson;
        JSONObject resultJSON;
        JSONArray records;
        if(StringUtils.isNotEmpty(result)) {
            userJson = (JSONObject) JsonUtils.parse(result);
            if(userJson.containsKey("success") && userJson.getBoolean("success")) {
                //获取result对象
                resultJSON = userJson.getJSONObject("result");
                pageSize = resultJSON.getInteger("pages");
                LogUtils.info("获取接口数据量：{total:{},pages:{}}",resultJSON.getInteger("total"),resultJSON.getInteger("pages"));
                //获取对象数据数组
                records = resultJSON.getJSONArray("records");
                //解析数据，插入
                taskInsertBlack(records);
            }
            if (pageSize > 1) {
                for (int i = 2; i <=pageSize ; i++) {
                    url = String.format(iamBlackUrl, dateTime, isAll, i);
                    result = webserviceUtil.sendHttpForGet(url);
                    if(StringUtils.isNotEmpty(result)) {
                        userJson = (JSONObject) JsonUtils.parse(result);
                        if(userJson.containsKey("success") && userJson.getBoolean("success")) {
                            resultJSON = userJson.getJSONObject("result");
                            records = resultJSON.getJSONArray("records");
                            //解析数据，插入
                            taskInsertBlack(records);
                        }
                    }
                }
            }
        }
    }

    public void taskInsertBlack(JSONArray jsonArray) {
        //组装数据
        List<SafetyBlackMessageDO> insertList = Lists.newArrayList();
        JSONObject record = null;
        SafetyBlackMessageDO blackMessage;
        List<String> empNos = Lists.newArrayList();
        
        List<String> cardNoList = new ArrayList<>();
        for (Object obj : jsonArray) {
            if (obj instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) obj;
                String fieldValue = jsonObject.getString("idCard");
                if (isNotBlank(fieldValue)) {
                    cardNoList.add(fieldValue);
                }
            }
        }
        BlackMessageListParam param = new BlackMessageListParam();
        param.setCardNoList(cardNoList);
        List<SafetyBlackMessageDO> messageDOS = this.selectList(param);
        Map<String,List<SafetyBlackMessageDO>> cardBeanMap = messageDOS.stream().collect(Collectors.groupingBy(SafetyBlackMessageDO::getIdCardNo));

        for (int i = 0; i < jsonArray.size(); i++) {
            record = jsonArray.getJSONObject(i);
            blackMessage = new SafetyBlackMessageDO();
            //身份证号
            String idCard = record.getString("idCard");
            //卡号
            String cardNo = record.getString("workNo");
            if (StringUtils.isEmpty(idCard) && idCard == null) {
                LogUtils.warn("idCard为空不做同步:{}",idCard);
                continue;
            }
            List<SafetyBlackMessageDO> curList = cardBeanMap.get(idCard);
            if (!CollectionUtils.isEmpty(curList)) {
                LogUtils.warn("数据已存在不做同步:{}",idCard);
                continue;
            }
            //根据身份证检查是否存在
            blackMessage.setCompanyName(record.getString("companyName") == null ? "宇通" : record.getString("companyName"));
            blackMessage.setEmpName(record.getString("name"));
            blackMessage.setCardNo(cardNo);
            blackMessage.setPhoneNo(record.getString("phone"));
            blackMessage.setIdCardNo(record.getString("idCard"));
            // 增加岗位信息(人员信息)
            String empType = record.getString("empType");
            blackMessage.setPositionType(empType);
            blackMessage.setPositionCode(record.getString("empTypeCode"));
            blackMessage.setEmpType("离职人员");
            blackMessage.setCarNo("");
            blackMessage.setOperator("系统");
            blackMessage.setCreateTime(new Date());
            blackMessage.setUpdateTime(new Date());
            blackMessage.setRemark("");
            blackMessage.setSource("中台导入");
            String photo = record.getString("photo");
            blackMessage.setIdPhoto(safetyBaseHandler.getPhotoUrl(photo));
            String lifePhoto = record.getString("lifePhoto");
            blackMessage.setLifePhoto(safetyBaseHandler.getPhotoUrl(lifePhoto));
            if ((StringUtils.isNotEmpty(cardNo) && (cardNo.startsWith("5") || cardNo.startsWith("6") || cardNo.startsWith("0"))) || "短期工".equals(empType) || "实习生".equals(empType)) {
                blackMessage.setState(2);
            } else {
                blackMessage.setState(1);
            }
            blackMessage.setDeleteFalg(false);
            if (!cardNo.startsWith("9")) {
                empNos.add(cardNo);
            }
            insertList.add(blackMessage);
        }
        if (!CollectionUtils.isEmpty(insertList)) {
            saveAndUpdate(insertList);
            // 同步海康
//            syncHikMiddleTable(insertList);
        }
        //离职人员数据更新外协免检台账数据
        if (!CollectionUtils.isEmpty(empNos)) {
            safetyExternalDetectionService.updateByEmpNos(empNos, true);
        }
    }

    public static List<HkMiddleTableVO> distinctByCardNo(List<HkMiddleTableVO> wppTegMeasureF) {
        Set<HkMiddleTableVO> list = new TreeSet<>((o1, o2) -> o1.getJobNo().compareTo(o2.getJobNo()));
        list.addAll(wppTegMeasureF);

        return new ArrayList<>(list);
    }
    /**
     * 同步海康的操作（黑名单）
     */
    public void syncHikMiddleTable(List<SafetyBlackMessageDO> list) {
        LogUtils.info("syncHikMiddleTable 开始同步----size={}", list.size());
        List<HkMiddleTableVO> tableVOList = new ArrayList<>();
        List<SafetyHikOperationGroupDO> groupDOList = new ArrayList<>();
        String black_message_group = safetyArtemisService.queryFaceGroup("BLACK_MESSAGE_GROUP");
        String faceGroup = isNotBlank(black_message_group) ? black_message_group : "1";
        for (SafetyBlackMessageDO li : list) {
            if (StringUtils.isBlank(li.getCardNo()) || StringUtils.isBlank(li.getEmpName()) || StringUtils.isBlank(li.getIdCardNo())) {
                continue;
            }
            // 正常的数据，并且照片都不为空的情况下
            if (StringUtils.isBlank(li.getLifePhoto()) && StringUtils.isBlank(li.getIdPhoto()) && !li.getDeleteFalg()) {
                continue;
            }
            SafetyHikOperationGroupDO groupDO = new SafetyHikOperationGroupDO();
            groupDO.setEmpNo(li.getCardNo());
            groupDO.setEmpName(li.getEmpName());
            groupDO.setCardNo(li.getIdCardNo());
            groupDO.setFaceGroup(faceGroup);
            groupDO.setCreateTime(new Date());
            if (li.getDeleteFalg()) {
                groupDO.setOperationType("DELETE");
            } else {
                groupDO.setOperationType("ADD");
                HkMiddleTableVO info = new HkMiddleTableVO();
                info.setStatus(-1);
                info.setOrgId("HDM00001");
                info.setJobNo(li.getCardNo());
                info.setCertificateNo(li.getIdCardNo());
                info.setName(li.getEmpName());
                info.setSex(0);
                info.setMobile(li.getPhoneNo());
                info.setTimeStr(LocalDate.now());
                info.setPositionOrgId("");
                String photo = li.getLifePhoto();
                if (isBlank(photo)) {
                    photo = li.getIdPhoto();
                } else {
                    photo = safetyBaseHandler.getPhotoUrl(photo);
                }
                try {
                    info.setFaceData(Base64ImageDecoder.imgBase64(photo));
                } catch (Exception e) {
                    continue;
                }
                tableVOList.add(info);
            }
            groupDOList.add(groupDO);
        }
        LogUtils.info("syncHikMiddleTable黑名单-增量同步----={}", tableVOList.size());
        if (!CollectionUtils.isEmpty(tableVOList)) {
            tableVOList = distinctByCardNo(distinctByCardNo(tableVOList));
            safetyArtemisService.syncHikMiddleTable(tableVOList, HikMiddleTableSqlEnum.TB_MIDDLE_PERSON);
        }
        if (!CollectionUtils.isEmpty(groupDOList)) {
            safetyHikOperationGroupService.batchSave(groupDOList);
        }
    }

    /**
     * 同步海康的操作（员工台账）
     */
    public void syncHikMiddleTableInfo(List<SafetyEmployeeMessageDO> list) {
        LogUtils.info("syncHikMiddleTableInfo开始同步----size={}", list.size());
        List<HkMiddleTableVO> tableVOList = new ArrayList<>();
        for (SafetyEmployeeMessageDO li : list) {// 必填项
            if (isBlank(li.getEmpNo()) || isBlank(li.getEmpName()) || isBlank(li.getOrgCode())) {
                continue;
            }
            // 图片
            String photo = safetyBaseHandler.getLifePhoto(li.getEmpNo());
            if (isBlank(photo)) {
                continue;
            }

            HkMiddleTableVO info = new HkMiddleTableVO();
            info.setOrgId(HIK_DEFAULT_ORG_CODE);
            info.setJobNo(li.getEmpNo());
            info.setCertType(131);
            info.setCertificateNo(li.getEmpNo());
            info.setName(li.getEmpName());
            if (StringUtils.equals("0", li.getSex())) {
                info.setSex(1);
            } else if (StringUtils.equals("1", li.getSex())) {
                info.setSex(2);
            } else {
                info.setSex(0);
            }
            if ("DISABLED".equals(li.getStatus())) {
                info.setStatus(-1);
            }
            info.setMobile(li.getPhoneNo());
            info.setTimeStr(LocalDate.now());
            info.setPositionOrgId("");
            info.setFaceData(Base64ImageDecoder.imgBase64(photo));
            tableVOList.add(info);
        }
        LogUtils.info("syncHikMiddleTableInfo同步的数据----={}", tableVOList.size());
        if (!CollectionUtils.isEmpty(tableVOList)) {
            safetyArtemisService.syncHikMiddleTable(tableVOList, HikMiddleTableSqlEnum.TB_MIDDLE_PERSON);
        }
    }

    /**
     * 批量保存或者更新数据
     * @param list 同步的的数据
     */
    public void saveAndUpdate(List<SafetyBlackMessageDO> list) {
        list = list.stream().filter(li -> StringUtils.isNotBlank(li.getIdCardNo())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<String> idCards = list.stream().map(SafetyBlackMessageDO::getIdCardNo).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        List<SafetyBlackMessageDO> oldList = safetyBlackManageDOBizMapper.selectListByIdCard(idCards);
        // 没有数据直接新增
        if (CollectionUtils.isEmpty(oldList)) {
            safetyBlackManageDOBizMapper.batchInsert(list);
            return;
        }
        // 存在的身份证信息
        Set<String> oldCardNoList = oldList.stream().map(SafetyBlackMessageDO::getIdCardNo).collect(Collectors.toSet());
        // 新增的数据
        List<SafetyBlackMessageDO> saveList = list.stream().filter(li -> !oldCardNoList.contains(li.getIdCardNo())).collect(Collectors.toList());
        // 更新的数据
        List<SafetyBlackMessageDO> updateList = list.stream().filter(li -> oldCardNoList.contains(li.getIdCardNo())).collect(Collectors.toList());

        // 存在数据 获取ID 进行修改
        List<String> distinctList = new ArrayList<>();
        Map<String, Integer> dataMap = new HashMap<>();
        for (SafetyBlackMessageDO li : oldList) {
            String idCardNo = li.getIdCardNo();
            // 过滤重复的数据
            if (distinctList.contains(idCardNo)) {
                continue;
            }

            distinctList.add(idCardNo);
            dataMap.put(idCardNo, li.getId());
        }
        // 更新的数据
        List<SafetyBlackMessageDO> lastUpdate = Lists.newArrayList();
        updateList.forEach(li -> {
            String idCardNo = li.getIdCardNo();
            Integer id = dataMap.get(idCardNo);
            // 正常不存在为空的数据，如果存在，就新增
            if (id == null) {
                saveList.add(li);
            } else {
                SafetyBlackMessageDO info = new SafetyBlackMessageDO();
                BeanUtils.copyProperties(li, info);
                info.setId(id);
                lastUpdate.add(info);
            }
        });

        // 新增
        if (!CollectionUtils.isEmpty(saveList)) {
            safetyBlackManageDOBizMapper.batchInsert(saveList);
        }

        // 修改
        if (!CollectionUtils.isEmpty(lastUpdate)) {
            safetyBlackManageDOBizMapper.batchUpdate(lastUpdate);
        }
    }

    /**
     * 根据数据更新
     * @param idCardId
     */
    public void updateStateByCardId(String idCardId) {
        if (StringUtils.isEmpty(idCardId)) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"参数身份证号不能为空！");
        }
        SafetyBlackMessageDOExample example = new SafetyBlackMessageDOExample();
        SafetyBlackMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andIdCardNoEqualTo(idCardId);
        criteria.andDeleteFalgEqualTo(false);
        List<SafetyBlackMessageDO> list = safetyBlackMessageDOMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            SafetyBlackMessageDO safetyBlackMessageDO = list.get(0);
            safetyBlackMessageDO.setState(2);
            safetyBlackMessageDO.setUpdateTime(new Date());
            safetyBlackMessageDOMapper.updateByPrimaryKey(safetyBlackMessageDO);
        } else {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该身份证号不存在黑名单列表中！");
        }
    }


    /**
     * 批量保存或者更新数据
     * @param list 同步的的数据
     */
    public void batchUpdateForEhr(List<SafetyBlackMessageDO> list) {
        List<String> idCards = list.stream().map(SafetyBlackMessageDO::getIdCardNo).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        List<SafetyBlackMessageDO> oldList = safetyBlackManageDOBizMapper.selectListByIdCard(idCards);
        // 没有数据直接新增
        if (CollectionUtils.isEmpty(oldList)) {
            safetyBlackManageDOBizMapper.batchInsert(list);
            return;
        }
        // 存在的身份证信息
        Set<String> oldCardNoList = oldList.stream().map(SafetyBlackMessageDO::getIdCardNo).collect(Collectors.toSet());
        // 新增的数据
        List<SafetyBlackMessageDO> saveList = list.stream().filter(li -> !oldCardNoList.contains(li.getIdCardNo())).collect(Collectors.toList());
        // 更新的数据
        List<SafetyBlackMessageDO> updateList = list.stream().filter(li -> oldCardNoList.contains(li.getIdCardNo())).collect(Collectors.toList());
        // 存在数据 获取ID 进行修改
        List<String> distinctList = new ArrayList<>();
        Map<String, Integer> dataMap = new HashMap<>();
        for (SafetyBlackMessageDO li : oldList) {
            String idCardNo = li.getIdCardNo();
            // 过滤重复的数据
            if (distinctList.contains(idCardNo)) {
                continue;
            }
            distinctList.add(idCardNo);
            dataMap.put(idCardNo, li.getId());
        }
        // 更新的数据
        List<SafetyBlackMessageDO> lastUpdate = Lists.newArrayList();
        updateList.forEach(li -> {
            String idCardNo = li.getIdCardNo();
            Integer id = dataMap.get(idCardNo);
            // 正常不存在为空的数据，如果存在，就新增
            if (id == null) {
                saveList.add(li);
            } else {
                SafetyBlackMessageDO info = new SafetyBlackMessageDO();
                BeanUtils.copyProperties(li, info);
                info.setId(id);
                lastUpdate.add(info);
            }
        });

        // 新增
        if (!CollectionUtils.isEmpty(saveList)) {
            safetyBlackManageDOBizMapper.batchInsert(saveList);
        }
        // 修改
        if (!CollectionUtils.isEmpty(lastUpdate)) {
            safetyBlackManageDOBizMapper.batchUpdateForEhr(lastUpdate);
        }
    }

    /**
     * 批量保存或者更新数据
     * @param list 同步的的数据
     */
    public void batchDelForEhr(List<SafetyBlackMessageDO> list) {
        List<String> idCards = list.stream().map(SafetyBlackMessageDO::getIdCardNo).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        List<SafetyBlackMessageDO> oldList = safetyBlackManageDOBizMapper.selectListByIdCard(idCards);
        // 没有数据直接新增
        if (CollectionUtils.isEmpty(oldList)) {
            LogUtils.info("EHR删除黑名单，未找到对应身份证号，终止删除:{}",idCards.stream().collect(Collectors.joining(",")));
            return;
        }

        //有员工号重新判断逻辑
        // 更新的数据
        List<SafetyBlackMessageDO> lastUpdate =oldList.stream().filter(e-> isNotBlank(e.getCardNo())).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(lastUpdate)){
            lastUpdate.forEach(black->{
                String empNo = black.getCardNo();
                String empType = black.getEmpType();
                if ((StringUtils.isNotEmpty(empNo) && (empNo.startsWith("5") || empNo.startsWith("6") || empNo.startsWith("0"))) || "短期工".equals(empType) || "实习生".equals(empType)) {
                    black.setState(2);
                } else {
                    black.setState(1);
                }
                black.setSource("中台导入");
                black.setRemark("EHR删除");
            });
            safetyBlackManageDOBizMapper.batchUpdateForEhr(lastUpdate);
        }

        //没有员工号的标记删除
        List<SafetyBlackMessageDO> delList = oldList.stream().filter(e->isBlank(e.getCardNo())).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(delList)){
            List<String> delIdCardNos = delList.stream().map(SafetyBlackMessageDO::getIdCardNo).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
            SafetyBlackMessageDO delRecord = new SafetyBlackMessageDO();
            delRecord.setDeleteFalg(true);
            delRecord.setRemark("EHR删除");
            SafetyBlackMessageDOExample example = new SafetyBlackMessageDOExample();
            example.createCriteria().andIdCardNoIn(delIdCardNos);
            safetyBlackManageDOBizMapper.updateByExampleSelective(delRecord,example);
        }
    }


    /**
     * 特批授权截止日期入库
     * @param empNo 员工号
     * @param authorizationDate 特批授权截止日期
     */
    public void updateInfo(String empNo, String authorizationDate) {
        boolean afterStr = DateUtils.isAfterStr(DateUtils.format(new Date(), 1), authorizationDate);

        SafetyBlackMessageDO record = new SafetyBlackMessageDO();
        record.setUpdateTime(new Date());
        record.setAuthorizationDate(authorizationDate);
        if (!afterStr) {
            record.setState(2);
        }

        SafetyBlackMessageDOExample example = new SafetyBlackMessageDOExample();
        example.createCriteria().andCardNoEqualTo(empNo).andDeleteFalgEqualTo(false);
        safetyBlackManageDOBizMapper.updateByExampleSelective(record, example);
    }

    /**
     * 变更状态
     * @param ids 变更状态的数据ID
     */
    public void updateInfo(List<Integer> ids) {
        SafetyBlackMessageDO record = new SafetyBlackMessageDO();
        record.setUpdateTime(new Date());
        record.setState(1);

        SafetyBlackMessageDOExample example = new SafetyBlackMessageDOExample();
        example.createCriteria().andIdIn(ids);
        safetyBlackManageDOBizMapper.updateByExampleSelective(record, example);
    }

    /**
     * eip离职流程数据同步
     * @param param
     */
    public void addDepart(BlackDepartAddParam param) {
        //根据身份证号做数据匹配，一个身份证号只能有一条数据
        SafetyBlackMessageDO safetyBlackMessageDO = getBlackMessage(param.getIdCardNo());
        if (safetyBlackMessageDO == null) {
            return;
        }
        String authorizationDate = param.getAuthorizationDate();
        boolean afterStr = false;
        if (StringUtils.isNotBlank(authorizationDate)) {
            afterStr = DateUtils.isAfterStr(DateUtils.format(new Date(), 1), authorizationDate);
        }
        if (!afterStr) {
            safetyBlackMessageDO.setState(2);
        } else {
            safetyBlackMessageDO.setState(1);
        }
        safetyBlackMessageDO.setUpdateTime(new Date());
        safetyBlackMessageDO.setAuthorizationDate(authorizationDate);
        safetyBlackMessageDOMapper.updateByPrimaryKey(safetyBlackMessageDO);
    }

    public void test() {
        String dateTime = "2023-09-14";
        if (StringUtils.isEmpty(dateTime)) {
            //前一天
            dateTime = DateUtils.format(LocalDateTime.now().minusDays(1).toDate(), 1);
        }
        //第一页的数据
        String url = "http://sg.yutong.com/emp-center/findSeparatedEmployees?apikey=3d9a715ba60a46cfa3039f79a9764d35&date=20230914&isAll=true&pageNo=1&pageSize=10";
        //定义总页数
        int pageSize = 0;
        //根据第一条数据，循环后边的数据
        String result = webserviceUtil.sendHttpForGet(url);
        LogUtils.info("数据量:{}", result);
        JSONObject userJson = null;
        JSONObject resultJSON = null;
        JSONArray records = null;
        if(StringUtils.isNotEmpty(result)) {
            userJson = (JSONObject) JsonUtils.parse(result);
            if(userJson.containsKey("success") && userJson.getBoolean("success")) {
                //获取result对象
                resultJSON = userJson.getJSONObject("result");
                pageSize = resultJSON.getInteger("pages");
                //获取对象数据数组
                //records = resultJSON.getJSONArray("records");
            }
        }
    }

    public Object isBlackBatchQuery(String carNoList){
        if (isBlank(carNoList)) {return null;}

        List<String> list = Arrays.asList(carNoList.split(","));

        return safetyBlackMessageDOMapper.isBlackBatchQuery(list);
    }

    /**
     * 检查数据是否存在
     * @param idCards
     * @return
     */
    public List<SafetyBlackMessageDO> getBlackMessageList(List<String> idCards) {
        SafetyBlackMessageDOExample example = new SafetyBlackMessageDOExample();
        SafetyBlackMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andIdCardNoIn(idCards);
        criteria.andStateEqualTo(1);  //1-禁止入厂 2-特批入厂
        criteria.andDeleteFalgEqualTo(false);
        return safetyBlackMessageDOMapper.selectByExample(example);
    }

    /**
     * 根据身份证号获取员工离职前的员工类型和任职部门
     * @param idCards 身份证号
     * @return 返回数据集合
     */
    public List<BlackMessageDto> selectBlackEmpInfo(List<String> idCards) {
        return safetyBlackManageDOBizMapper.selectBlackEmpInfo(idCards);
    }

    public List<String> querySelect() {
        return safetyBlackManageDOBizMapper.querySelect();
    }

    public void batchUpdatePhoto(List<SafetyBlackMessageDO> updateList) {
        if (!CollectionUtils.isEmpty(updateList)) {
            safetyBlackManageDOBizMapper.batchUpdatePhoto(updateList);
        }
    }

    public Integer selectMaxId() {
        return safetyBlackManageDOBizMapper.selectMaxId();
    }
}
