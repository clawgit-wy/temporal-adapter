package com.yutong.safety.core.service.spot;

import com.google.common.collect.Sets;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.constants.RuleStateEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.param.web.spot.SpotCheckBatchDeleteParam;
import com.yutong.safety.common.param.web.spot.SpotCheckRuleListParam;
import com.yutong.safety.common.param.web.spot.SpotCheckRuleRequireParam;
import com.yutong.safety.common.param.web.spot.SpotCheckRuleSaveParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.core.db.entity.SafetyFieldManageDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckRuleDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckRuleDOExample;
import com.yutong.safety.core.db.entity.spot.SafetySpotRulePermDO;
import com.yutong.safety.core.db.mapper.spot.SafetySpotCheckRuleDOBizMapper;
import com.yutong.safety.core.service.SafetyFieldManageService;
import com.yutong.safety.core.service.SafetyLogMessageService;
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
 * @author gwxiexj
 * @since 2024-04-25 19:12
 */
@Service
public class SafetySpotCheckRuleService {

    @Autowired
    private SafetySpotCheckRuleDOBizMapper safetySpotCheckRuleDOMapper;

    @Autowired
    private SafetyFieldManageService safetyFieldManageService;

    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    @Autowired
    private SafetySpotRulePermService safetySpotRulePermService;

    /**
     *
     * @return 返回值
     */
    public Object saveOrUpdate(SpotCheckRuleSaveParam param, String token) {
        SafetySpotCheckRuleDO ruleDO = new SafetySpotCheckRuleDO();
        BeanUtils.copyProperties(param,ruleDO);
        String realName = TokenUtils.getRealName(token);
        String workNo = TokenUtils.getWorkNo(token);
        ruleDO.setUpdateEmp(realName);
        ruleDO.setUpdateEmpNo(workNo);
        ruleDO.setUpdateTime(new Date());
        ruleDO.setDeleteFalg(false);
        String ability = "新增";
        if(Objects.isNull(param.getId())){
            ruleDO.setCreateTime(new Date());
            List<SafetySpotCheckRuleDO> addList = new ArrayList<>();
            addList.add(ruleDO);
            PreconditonsEx.checkArgument(safetySpotCheckRuleDOMapper.insert(ruleDO) == 1, ResponseEnum.FAIL);
        }else{
            ability = "修改";
            PreconditonsEx.checkArgument(safetySpotCheckRuleDOMapper.updateByPrimaryKeySelective(ruleDO) == 1, ResponseEnum.FAIL);
        }
        String paramStr = JsonUtils.toString(param);
        int maxLen = paramStr.length()>200?200:paramStr.length();
        this.saveLog("设备设施点检-设备巡检标准配置",ability,realName, "入参："+paramStr.substring(0,maxLen));
        return ruleDO.getId();
    }

    /**
     * 做删除标记
     *
     * @param ruleId 规则主键
     * @return 返回
     */
    public int deleteById(List<Integer> ruleId) {
        SafetySpotCheckRuleDOExample example = new SafetySpotCheckRuleDOExample();
        example.createCriteria().andIdIn(ruleId);
        return safetySpotCheckRuleDOMapper.deleteByExample(example);
    }

    /**
     * 分页查询
     *
     * @param param 查询入参
     * @return 返回
     */
    public List<SafetySpotCheckRuleDO> pageList(SpotCheckRuleListParam param) {
        SafetySpotCheckRuleDOExample example = formatExample(param);
        example.setOrderByClause(" team_id asc,rule_sort asc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        return safetySpotCheckRuleDOMapper.selectByExampleNew(example);
    }

    /**
     * 分页总条数
     *
     * @param param 查询入参
     * @return 返回
     */
    public long pageListCount(SpotCheckRuleListParam param) {
        SafetySpotCheckRuleDOExample example = formatExample(param);
        return safetySpotCheckRuleDOMapper.countByExampleNew(example);
    }

    /**
     * 查询-不分页
     *
     * @param param 查询入参
     * @return 返回
     */
    public List<SafetySpotCheckRuleDO> list(SpotCheckRuleListParam param) {

        SafetySpotCheckRuleDOExample example = formatExample(param);
        example.setOrderByClause(" team_id asc,rule_sort asc ");

        return safetySpotCheckRuleDOMapper.selectByExampleNew(example);
    }


    /**
     * 导出数据查询
     * @param param 入参
     * @return 返回值
     */
    public List<SafetySpotCheckRuleDO> export(SpotCheckRuleListParam param) {
        SafetySpotCheckRuleDOExample example = formatExample(param);
        example.setOrderByClause(" team_id asc,rule_sort asc ");
        return safetySpotCheckRuleDOMapper.selectByExampleNew(example);
    }

    /**
     * 处理检索条件
     * @param param 入参
     * @return 返回值
     */
    private SafetySpotCheckRuleDOExample formatExample(SpotCheckRuleListParam param){
        SafetySpotCheckRuleDOExample example = new SafetySpotCheckRuleDOExample();
        SafetySpotCheckRuleDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        //只查询未删除状态的规则
        if(param.getTeamId() != null){
            criteria.andTeamIdEqualTo(param.getTeamId());
        }
        if(StringUtils.isNotBlank(param.getTeamName())){
            criteria.andTeamNameEqualTo(param.getTeamName());
        }
        if(StringUtils.isNotEmpty(param.getRuleName())){
            criteria.andRuleNameEqualTo(param.getRuleName());
        }
        if (StringUtils.isNotEmpty(param.getRemark())) {
            criteria.andRemarkLike("%" + param.getRemark() + "%");
        }
        if(param.getUnId() != null){
            criteria.andIdNotEqualTo(param.getUnId());
        }
        if(param.getIdList() != null && param.getIdList().size()>0){
            criteria.andIdIn(param.getIdList());
        }
        return example;
    }

    /**
     * 批量删除动作
     * @param param 入参
     * @param token token
     */
    public void batchDelete(SpotCheckBatchDeleteParam param, String token) {
        String realName = TokenUtils.getRealName(token);
        String workNo = TokenUtils.getWorkNo(token);

        Set<String> ids = Sets.newHashSet(param.getIds().split(","));
        List<Integer> idList = ids.stream().map(Integer::parseInt).collect(Collectors.toList());

        SafetySpotCheckRuleDO safetySpotCheckRuleDO = new SafetySpotCheckRuleDO();
        safetySpotCheckRuleDO.setDeleteFalg(true);
        safetySpotCheckRuleDO.setUpdateTime(new Date());
        safetySpotCheckRuleDO.setUpdateEmp(realName);
        safetySpotCheckRuleDO.setUpdateEmpNo(workNo);
        SafetySpotCheckRuleDOExample example = new SafetySpotCheckRuleDOExample();
        example.createCriteria().andIdIn(idList);

        String isUsedStr = checkRules(idList);
        if(StringUtils.isNotBlank(isUsedStr)){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"“"+isUsedStr+"“已被引用，不能删除");
        }
        //增加日志
        List<SafetySpotCheckRuleDO> ruleList = safetySpotCheckRuleDOMapper.selectByExample(example);
        String message = ruleList.stream().map(SafetySpotCheckRuleDO::getRuleName).collect(Collectors.joining(";"));
        this.saveLog("设备设施点检-设备巡检标准配置","批量删除",realName, "删除标准："+message);

        safetySpotCheckRuleDOMapper.deleteByExample(example);
    }

    /**
     * 数据删除操作
     * @param id 指定ID
     * @return 返回值
     */
    public boolean delete(Integer id, String token) {
        SafetySpotCheckRuleDO safetySpotCheckRuleDO = safetySpotCheckRuleDOMapper.selectByPrimaryKey(id);
        if(safetySpotCheckRuleDO == null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        List<Integer> idList = new ArrayList<>();
        idList.add(id);
        String isUsedStr = checkRules(idList);
        if(StringUtils.isNotBlank(isUsedStr)){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"“"+isUsedStr+"“已被引用，不能删除");
        }
        String realName = TokenUtils.getRealName(token);
        safetySpotCheckRuleDOMapper.deleteByPrimaryKey(id);

        this.saveLog("设备设施点检-设备巡检标准配置","删除",realName, "巡检标准"+safetySpotCheckRuleDO.getTeamName()+"-"+safetySpotCheckRuleDO.getRuleName());
        return true;
    }

    /**
     * 获取全部类目数据Map类
     * @return
     */
    public Map<String, SafetySpotCheckRuleDO> getRuleList() {
        SafetySpotCheckRuleDOExample example = new SafetySpotCheckRuleDOExample();
        example.createCriteria().andDeleteFalgEqualTo(false);
        List<SafetySpotCheckRuleDO> list = safetySpotCheckRuleDOMapper.selectByExampleNew(example);
        return list.stream().collect(Collectors.toMap(safetySpotCheckRuleDO -> safetySpotCheckRuleDO.getTeamName() + "_" + safetySpotCheckRuleDO.getRuleName(), Function.identity()));
    }

    /**
     * 数据导入操作
     * @param param 入参
     * @return 返回值
     */
    public Object importSpotCheckRule(CommonImportParam param, String token) {
        String realName = TokenUtils.getRealName(token);
        String workNo = TokenUtils.getWorkNo(token);

        Workbook workbook = ExcelUtils.getWorkbook(param.getFile());
        Sheet sheet = workbook.getSheetAt(0);
        //获得当前sheet的开始行
        int firstRowNum  = sheet.getFirstRowNum();
        //获得当前sheet的结束行
        int lastRowNum = sheet.getLastRowNum();
        String result = "";
        List<SafetySpotCheckRuleDO> insertList = Lists.newArrayList();
        SafetySpotCheckRuleDO spotCheckRuleDO = null;
        //根据tocken当前用户人员
        Map<String, SafetyFieldManageDO> manageDOMaps = safetyFieldManageService.getSpotTeams();
        boolean flag = false;
        int rum;
        String expenseMessage = "";
        for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++) {
            //获得当前行
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            try {
                String teamName = ExcelUtils.getValue(row.getCell(1));//项目
                String ruleName = ExcelUtils.getValue(row.getCell(2));//巡检标准类目
                String ruleInfo = ExcelUtils.getValue(row.getCell(3));//巡检标准
                String normalImgRequire = ExcelUtils.getValue(row.getCell(4));//设备正常是否上传照片
                String exceptionImgRequire = ExcelUtils.getValue(row.getCell(5));//设备异常是否上传照片
                String reformImgRequire = ExcelUtils.getValue(row.getCell(6));//当场整改是否上传照片
                String validateImgRequire =  ExcelUtils.getValue(row.getCell(7));//整改验证是否上传照片
                String ruleSort = ExcelUtils.getValue(row.getCell(8));//排序
                String remark = ExcelUtils.getValue(row.getCell(9));//备注
                if(StringUtils.isEmpty(teamName) && StringUtils.isEmpty(ruleName) && StringUtils.isEmpty(ruleInfo) && StringUtils.isEmpty(normalImgRequire) && StringUtils.isEmpty(exceptionImgRequire) && StringUtils.isEmpty(reformImgRequire) && StringUtils.isEmpty(validateImgRequire)){
                    continue;
                }
                if(StringUtils.isEmpty(teamName) || StringUtils.isEmpty(ruleName) || StringUtils.isEmpty(ruleInfo) || StringUtils.isEmpty(normalImgRequire) || StringUtils.isEmpty(exceptionImgRequire) || StringUtils.isEmpty(reformImgRequire) && StringUtils.isEmpty(validateImgRequire)){
                    rum = rowNum;
                    flag = true;
                    expenseMessage = "第"+rum+"行，有必填项为空！请检查后再导入数据！";
                    break;
                }
                spotCheckRuleDO = new SafetySpotCheckRuleDO();
                if (manageDOMaps.containsKey(teamName)) {
                    spotCheckRuleDO.setTeamId(manageDOMaps.get(teamName).getId());
                } else {
                    rum = rowNum;
                    flag = true;
                    expenseMessage = "第"+rum+"行，项目数据对照有误！请检查后再导入数据！";
                    break;
                }
                spotCheckRuleDO.setTeamName(teamName);
                spotCheckRuleDO.setCreateTime(new Date());
                spotCheckRuleDO.setUpdateEmp(realName);
                spotCheckRuleDO.setUpdateEmpNo(workNo);
                spotCheckRuleDO.setUpdateTime(new Date());
                spotCheckRuleDO.setDeleteFalg(false);
                spotCheckRuleDO.setRuleName(ruleName);
                spotCheckRuleDO.setRuleInfo(ruleInfo);
                spotCheckRuleDO.setNormalImgRequire(!RuleStateEnum.values()[Integer.parseInt(normalImgRequire)].isCode());
                spotCheckRuleDO.setExceptionImgRequire(!RuleStateEnum.values()[Integer.parseInt(exceptionImgRequire)].isCode());
                spotCheckRuleDO.setReformImgRequire(!RuleStateEnum.values()[Integer.parseInt(reformImgRequire)].isCode());
                spotCheckRuleDO.setValidateImgRequire(!RuleStateEnum.values()[Integer.parseInt(validateImgRequire)].isCode());
                spotCheckRuleDO.setRuleSort(Integer.parseInt(ruleSort.trim()));
                spotCheckRuleDO.setRemark(remark);
                insertList.add(spotCheckRuleDO);
            } catch (Exception e) {
                LogUtils.error("数据第"+rowNum+"导入行读取异常",e);
                throw new BizException(ResponseEnum.FAIL.getCode(), e.getMessage());
            }
        }
        if (flag) {
            throw new BizException(ResponseEnum.FAIL.getCode(), expenseMessage);
        }
        if (CollectionUtils.isNotEmpty(insertList)) {
            String ruleNames = insertList.stream().map(SafetySpotCheckRuleDO::getRuleName).collect(Collectors.joining(";"));
            saveLog("设备设施点检-设备巡检标准配置","批量导入",realName,"导入标准："+ruleNames);

            safetySpotCheckRuleDOMapper.batchInsert(insertList);
        }
        result = "导入成功:"+insertList.size()+"条!";
        return result;
    }

    /**
     * 数据更新操作
     * @param param 入参
     * @param token token
     */
    public void updateRequire(SpotCheckRuleRequireParam param, String token) {
        SafetySpotCheckRuleDO safetySpotCheckRuleDO = safetySpotCheckRuleDOMapper.selectByPrimaryKey(param.getId());
        if(safetySpotCheckRuleDO == null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        if (Objects.nonNull(param.getNormalImgRequire())) {
            safetySpotCheckRuleDO.setNormalImgRequire(param.getNormalImgRequire());
        }
        if (Objects.nonNull(param.getExceptionImgRequire())) {
            safetySpotCheckRuleDO.setExceptionImgRequire(param.getExceptionImgRequire());
        }
        if (Objects.nonNull(param.getReformImgRequire())) {
            safetySpotCheckRuleDO.setReformImgRequire(param.getReformImgRequire());
        }
        if (Objects.nonNull(param.getValidateImgRequire())) {
            safetySpotCheckRuleDO.setValidateImgRequire(param.getValidateImgRequire());
        }
        safetySpotCheckRuleDO.setUpdateEmpNo(tokenUser.get("workNo"));
        safetySpotCheckRuleDO.setUpdateEmp(tokenUser.get("realName"));
        safetySpotCheckRuleDO.setUpdateTime(new Date());
        safetySpotCheckRuleDOMapper.updateByPrimaryKeySelective(safetySpotCheckRuleDO);
    }

    public List<SafetySpotCheckRuleDO> selectRuleList(Map<String,Object> param){
        return safetySpotCheckRuleDOMapper.selectRuleList(param);
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

    /**
     * 校验是否已被引用
     * @param ruleIdList 规则ID
     * @return
     */
    public String checkRules(List<Integer> ruleIdList){
        List<SafetySpotRulePermDO> rulePermDOS = safetySpotRulePermService.selectByRule(ruleIdList);
        if(rulePermDOS == null || rulePermDOS.size() <= 0){
            return "";
        }
        List<Integer> ruleIds = rulePermDOS.stream().map(SafetySpotRulePermDO::getRuleId).collect(Collectors.toList());
        SpotCheckRuleListParam param = new SpotCheckRuleListParam();
        param.setIdList(ruleIds);
        List<SafetySpotCheckRuleDO> ruleDOS = list(param);
        return ruleDOS.stream().map(SafetySpotCheckRuleDO::getRuleName).collect( Collectors.joining(";"));
    }
}
