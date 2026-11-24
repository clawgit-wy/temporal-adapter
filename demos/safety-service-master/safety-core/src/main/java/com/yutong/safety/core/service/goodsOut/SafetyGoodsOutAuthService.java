package com.yutong.safety.core.service.goodsOut;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.easyExcel.ColumnWidthAuto;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.utils.excel.ErrorInfo;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsAuthOwnerCollectDO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutAuthDO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutAuthDOExample;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutAuthAddParam;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutAuthListParam;
import com.yutong.safety.core.db.mapper.SafetyEmployeeMessageDOMapper;
import com.yutong.safety.core.db.mapper.SafetyFieldManageDOMapper;
import com.yutong.safety.core.db.mapper.goodsOut.SafetyGoodsAuthOwnerCollectDOMapper;
import com.yutong.safety.core.db.mapper.goodsOut.SafetyGoodsOutAuthDOBizMapper;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsSelectPersonDTO;
import com.yutong.safety.core.service.SafetyLogMessageService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SafetyGoodsOutAuthService {

    @Autowired
    private SafetyGoodsOutAuthDOBizMapper safetyGoodsOutAuthDOMapper;
    @Autowired
    SafetyGoodsAuthOwnerCollectDOMapper safetyGoodsAuthOwnerCollectDOMapper;
    @Autowired
    private SafetyLogMessageService safetyLogMessageService;
    @Autowired
    SafetyEmployeeMessageDOMapper safetyEmployeeMessageDOMapper;
    @Autowired
    SafetyFieldManageDOMapper safetyFieldManageDOMapper;

    @Value("${iam.empInfo.url}")
    private String empInfoUrl;
    //安保小助手机器人ID
    @Value("${iam.retention.robotId}")
    private String incomintRebotId;

    @Autowired
    private WebserviceUtil webserviceUtil;
    @Autowired
    private YxtRebotUtils yxtRebotUtils;

    private static final String targetId="ABYJS";

    private static final String isAll="false";

    public Object getRecentSelect(SafetyGoodsSelectPersonDTO en){
        if (StringUtils.isBlank(en.getOwnerEmpNo()) && StringUtils.isBlank(en.getPhoneNum())){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"员工号和手机号不能同时为空！");
        }

        if ("3".equals(en.getSystemFlag())){
            en.setOwnerEmpNo(en.getPhoneNum());
        }else{
            en.setOwnerEmpNo(en.getOwnerEmpNo());
        }

        return safetyGoodsAuthOwnerCollectDOMapper.getRecentSelect(en);
    }


    public int saveOrUpdate(GoodsOutAuthAddParam paramAddParam){
        SafetyGoodsOutAuthDO systemParamDO = new SafetyGoodsOutAuthDO();
        BeanUtils.copyProperties(paramAddParam,systemParamDO);
        int i;
        String modifyBy = paramAddParam.getModifyBy();
        String ability = "更新";
        if(paramAddParam.getId() == null){
            systemParamDO.setCreateTime(new Date());
            systemParamDO.setCreateUser(modifyBy);
            ability = "新增";
            i = safetyGoodsOutAuthDOMapper.insert(systemParamDO);
        }else{
            systemParamDO.setUpdateTime(new Date());
            systemParamDO.setUpdateUser(modifyBy);
            i = safetyGoodsOutAuthDOMapper.updateByPrimaryKeySelective(systemParamDO);
        }
        String paramStr = JsonUtils.toString(paramAddParam);
        int maxLen = Math.min(paramStr.length(), 200);
        this.saveLog("出门证权限配置",ability,modifyBy, "入参："+paramStr.substring(0,maxLen));
        return i;
    }

    /**
     * 做删除标记
     *
     * @param ruleId 规则主键
     * @return 返回
     */
    public int deleteById(List<Integer> ruleId, String modify) {
        SafetyGoodsOutAuthDOExample example = new SafetyGoodsOutAuthDOExample();
        example.createCriteria().andIdIn(ruleId);
        List<SafetyGoodsOutAuthDO> queryList = safetyGoodsOutAuthDOMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(queryList)){
            String paramCodes = queryList.stream().map(SafetyGoodsOutAuthDO::getEmpNo).collect(Collectors.joining(","));
            String paramName =  queryList.stream().map(SafetyGoodsOutAuthDO::getPowerFactoryId).collect(Collectors.joining(","));
            int i  = safetyGoodsOutAuthDOMapper.deleteByExample(example);
            this.saveLog("出门证权限配置","删除",modify, "删除范围："+paramCodes+","+paramName);

            //同步删除个人最近选择和收藏
            safetyGoodsAuthOwnerCollectDOMapper.deleteByAuthIds(ruleId);

            return i;
        }

        return 0;
    }

    /**
     * 分页查询
     *
     * @param param 查询入参
     * @return 返回
     */
    public List<SafetyGoodsOutAuthDO> pageList(GoodsOutAuthListParam param) {
        return safetyGoodsOutAuthDOMapper.selectByConditions(param);
    }

    /**
     * 分页总条数
     *
     * @param param 查询入参
     * @return 返回
     */
    public long pageListCount(GoodsOutAuthListParam param) {
        SafetyGoodsOutAuthDOExample example = formatExample(param);
        return safetyGoodsOutAuthDOMapper.selectByConditionsCount(param);
    }

    /**
     * 查询-不分页
     *
     * @param param 查询入参
     * @return 返回
     */
    public List<SafetyGoodsOutAuthDO> list(GoodsOutAuthListParam param) {
        param.setPageSize(999999999);
        return safetyGoodsOutAuthDOMapper.selectByConditions(param);
    }

    public Object getSelectList(SafetyGoodsSelectPersonDTO en){
        if (StringUtils.isBlank(en.getOwnerEmpNo()) && StringUtils.isBlank(en.getPhoneNum())){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"员工号和手机号不能同时为空！");
        }

        if (StringUtils.isBlank(en.getOwnerEmpNo())){
            en.setOwnerEmpNo(en.getPhoneNum());
        }
        return safetyGoodsAuthOwnerCollectDOMapper.getSelectList(en);
    }
    /**
     * 处理检索条件
     * @param param 入参
     * @return 返回值
     */
    private SafetyGoodsOutAuthDOExample formatExample(GoodsOutAuthListParam param){
        SafetyGoodsOutAuthDOExample example = new SafetyGoodsOutAuthDOExample();
        SafetyGoodsOutAuthDOExample.Criteria criteria = example.createCriteria();

        //只查询未删除状态的规则
        if(StringUtils.isNotBlank(param.getEmpStr())){
            criteria.andEmpNameEqualTo(param.getEmpStr());
        }
        if(StringUtils.isNotBlank(param.getEmpDept())){
            criteria.andEmpDeptEqualTo(param.getEmpDept());
        }
        if(StringUtils.isNotEmpty(param.getOutType())){
            criteria.andOutTypeEqualTo(param.getOutType());
        }
        return example;
    }

    public Object collectConfig(String ownerEmpNo,String authId,String collectType,String phoneNum){
        if (StringUtils.isBlank(ownerEmpNo) && StringUtils.isBlank(phoneNum)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"员工号和手机号不能同时为空！");
        }

        SafetyGoodsAuthOwnerCollectDO en = new SafetyGoodsAuthOwnerCollectDO();

        if (!StringUtils.isBlank(ownerEmpNo)){
            en.setOwnerEmpNo(ownerEmpNo);
        }else{
            SafetyEmployeeMessageDO infoByPhoneNO = safetyEmployeeMessageDOMapper.getInfoByPhoneNO(phoneNum);
            en.setOwnerEmpNo(infoByPhoneNO.getEmpNo());
        }

        en.setAuthId(Integer.parseInt(authId));

        if("1".equals(collectType)){
            en.setCollectType("1");
            en.setCreateTime(new Date());
            safetyGoodsAuthOwnerCollectDOMapper.insertSelective(en);
        }else if("2".equals(collectType)){
            safetyGoodsAuthOwnerCollectDOMapper.unCollect(en);
        }
        return null;
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
     * 导出
     * @param param
     * @param
     */
    public void exportExcel(GoodsOutAuthListParam param, OutputStream outputStream){
        param.setPageSize(5000);
        List<SafetyGoodsOutAuthDO> list = safetyGoodsOutAuthDOMapper.selectByConditions(param);

        EasyExcel.write(outputStream, SafetyGoodsOutAuthDO.class)
                .registerWriteHandler(new ColumnWidthAuto())
                .sheet("Sheet1")
                .doWrite(list);
    }

    public List<ErrorInfo> importData(MultipartFile file, String token) {
        try {
            List<SafetyGoodsOutAuthDO> list = new ArrayList<>();

            String workNo = TokenUtils.getWorkNo(token);
            if(StringUtils.isEmpty(workNo)){
                throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已过期，请重新登录！");
            }

            //查询工厂编码
            List<Map<String, Object>> factoryMapList = safetyFieldManageDOMapper.getFactoryMap();
            LogUtils.info("SafetyGoodsOutAuthService.importData[factoryMapList]:" + factoryMapList);
            Map<String, String> factoryMap = factoryMapList.stream().collect(Collectors.toMap(
                    map -> map.get("factory").toString(),
                    map -> map.get("id").toString(),
                    (oldVal,newVal) -> oldVal
            ));
            LogUtils.info("SafetyGoodsOutAuthService.importData[factoryMap]:" + factoryMap);


            EasyExcel.read(file.getInputStream(), SafetyGoodsOutAuthDO.class, new AnalysisEventListener<SafetyGoodsOutAuthDO>() {
                @Override
                public void invoke(SafetyGoodsOutAuthDO en, AnalysisContext analysisContext) {
                    //处理换行符
                    String powerDesc = en.getPowerDesc();
                    if (powerDesc != null) {
                        powerDesc = powerDesc.replaceAll("_x000D_", "\t");
                        en.setPowerDesc(powerDesc);
                    }

                    if(!StringUtils.isBlank(en.getPowerFactory())){
                        en.setPowerFactoryId(factoryMap.get(en.getPowerFactory()));
                    }
                    en.setCreateUser(workNo);
                    en.setCreateTime(new Date());
                    en.setUpdateUser(workNo);
                    en.setUpdateTime(new Date());

                    list.add(en);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                }
            }).sheet().doRead();

            LogUtils.info("SafetyGoodsOutAuthService.importData[beforeInsert]:" + factoryMap);

            //开始数据库插入
            //safetyGoodsOutAuthDOMapper.batchInsert(list);
            for (SafetyGoodsOutAuthDO en : list) {
                try {
                    safetyGoodsOutAuthDOMapper.insertSelective(en);
                } catch (Exception e) {
                    jlrz(token,en.toString(),e.getMessage());
                }
            }

            LogUtils.info("SafetyGoodsOutAuthService.importData[afterInsert]:" + factoryMap);

        } catch (Exception e) {
            LogUtils.info("SafetyGoodsOutAuthService[importData]:" + e.getMessage());
        }

        return null;
    }

    /**
     * 日志记录
     * @param token
     * @param mes
     * @param erroMes
     */
    public void jlrz(String token, String mes, String erroMes){
        //操作日志记录
        LogUtils.info("SafetyGoodsOutAuthService[importData]:"+mes);
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("审核人权限导入");
        log.setAbility("添加");

        if ("".equals(token)) {
            log.setOperator("");
        } else {
            String realName = TokenUtils.getRealName(token);
            log.setOperator(realName);
        }
        log.setMessage("导入出错：数据：" + mes + "；失败原因：" + erroMes);
        LogUtils.info("SafetyGoodsOutAuthService[log]:"+log);
        safetyLogMessageService.addLogMessage(log);
    }


    public void cancelAccess() {
        String dateTime = "";
        if(StringUtils.isEmpty(dateTime)){
            dateTime = DateUtils.format(LocalDateTime.now().minusHours(1).toDate(), 2);
        }

        JSONObject resultJSON = null;
        JSONObject userJson = null;
        JSONArray records = null;
        JSONObject record = null;
        int pageSize = 0;

        //定义url
        String url = String.format(empInfoUrl, isAll, dateTime, targetId, 1,1000);
        //根据url从中台获取增量信息
        String result = webserviceUtil.sendHttpForGet(url);
        if (StringUtils.isEmpty(result)) {
            LogUtils.info("中台接口返回信息为空");
            return;
        }
        userJson=(JSONObject) JsonUtils.parse(result);
        if (!userJson.containsKey("success") || !userJson.getBoolean("success")) {
            LogUtils.info("中台获取数据失败！");
            return;
        }
        //获取result对象
        resultJSON = userJson.getJSONObject("result");
        if (ObjectUtils.isEmpty(resultJSON)){
            LogUtils.info("获取result对象为空！");
            return;
        }
        //获取总页码数
        pageSize = resultJSON.getInteger("pages");
        if (pageSize < 1) {
            return;
        }
        for (int i = 1; i <=pageSize ; i++) {
            url = String.format(empInfoUrl, isAll, dateTime, targetId, i,1000);
            result = webserviceUtil.sendHttpForGet(url);
            LogUtils.info("SafetyGoodsOutAuthService[cancelAccess]result:"+result);
            if (!StringUtils.isNotEmpty(result)) {
                continue;
            }
            userJson = (JSONObject) JsonUtils.parse(result);
            if (!userJson.containsKey("success") || !userJson.getBoolean("success")) {
                continue;
            }
            resultJSON = userJson.getJSONObject("result");
            records = resultJSON.getJSONArray("records");
            for (int j = 0; j < records.size(); j++) {
                String workNo = "";
                try{
                    record = records.getJSONObject(j);
                    if (ObjectUtils.isEmpty(record)) {
                        continue;
                    }

                    workNo = record.getString("workNo");
                    //离职清权限
                    if("DISABLED".equals(record.getString("status"))){
                        LogUtils.info("SafetyGoodsOutAuthService[cancelAccess]:"+workNo+"离职清权限");
                        controls(workNo);
                    }

                    //组织变动清除权限
                    //查询现有的
                    String orgCode = safetyGoodsOutAuthDOMapper.getOrgByWorkNo(workNo);
                    //和此次做对比
                    String orgCodeNew = record.getString("workNo");

                    if(!orgCode.equals(orgCodeNew)){
                        LogUtils.info("SafetyGoodsOutAuthService[cancelAccess]:"+workNo+"组织变动清除权限");
                        controls(workNo);
                    }
                }catch(Exception e){
                    LogUtils.info("SafetyGoodsOutAuthService[cancelAccess]:"+workNo+"权限取消失败;原因："+e.getMessage());
                }
            }
        }
    }

    /**
     * 取消权限并发送宇信通提醒
     * @param workNo
     */
    public void controls(String workNo){
        //删除权限
        safetyGoodsAuthOwnerCollectDOMapper.deleteByAuthCode(workNo);
        safetyGoodsOutAuthDOMapper.deleteByEmpNo(workNo);

        //宇信通提醒
        RestTemplate restTemplate = new RestTemplate();
        String sendmsg = "因您组织变更，物质出门证权限已关闭，如有审批需求可重新提交流程。";
        yxtRebotUtils.postForEntity(null,incomintRebotId,workNo,sendmsg,restTemplate);
    }

    public void addAccessPer(List<GoodsOutAuthAddParam> enList) {
        if(CollectionUtils.isEmpty(enList)){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"数据为空！");
        }

        for(GoodsOutAuthAddParam en:enList){
            if(StringUtils.isBlank(en.getEmpNo())){throw new BizException(ResponseEnum.EXCEPTION.getCode(),"员工号不能为空！");}
            if(StringUtils.isBlank(en.getPowerFactoryId())){throw new BizException(ResponseEnum.EXCEPTION.getCode(),"厂区不能为空！");}
            if(StringUtils.isBlank(en.getOutType())){throw new BizException(ResponseEnum.EXCEPTION.getCode(),"出门类型不能为空！");}
            if(StringUtils.isBlank(en.getEmpDept())){throw new BizException(ResponseEnum.EXCEPTION.getCode(),"审核部门不能为空！");}

            String empNo = en.getEmpNo();
            String powerFactoryId = en.getPowerFactoryId();
            String outType = en.getOutType();
            String empDept = en.getEmpDept();

            //查询
            SafetyGoodsOutAuthDOExample example = new SafetyGoodsOutAuthDOExample();
            example.createCriteria().andEmpNoEqualTo(empNo)
                    .andPowerFactoryIdEqualTo(powerFactoryId)
                    .andOutTypeEqualTo(outType)
                    .andEmpDeptEqualTo(empDept);
            List<SafetyGoodsOutAuthDO> safetyGoodsOutAuthDOS = safetyGoodsOutAuthDOMapper.selectByExample(example);

            //为空更新；不为空新增
            if(CollectionUtils.isEmpty(safetyGoodsOutAuthDOS)){
                SafetyGoodsOutAuthDO entity = new SafetyGoodsOutAuthDO();
                entity.setEmpNo(empNo);
                entity.setPowerFactoryId(powerFactoryId);
                entity.setOutType(outType);
                entity.setEmpDept(empDept);
                entity.setEmpName(en.getEmpName());
                entity.setEmpEip(en.getEmpEip());
                entity.setCreateTime(new Date());
                entity.setPowerDesc(en.getPowerDesc());

                safetyGoodsOutAuthDOMapper.insertSelective(entity);
            }else{
                safetyGoodsOutAuthDOS.stream().forEach(e -> {
                    e.setUpdateTime(new Date());
                    e.setPowerDesc(en.getPowerDesc());
                });

                safetyGoodsOutAuthDOMapper.batchUpdate(safetyGoodsOutAuthDOS);
            }
        }
    }

    public void deleteAccessPer(List<GoodsOutAuthAddParam> enList) {
        if(CollectionUtils.isEmpty(enList)){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"数据为空！");
        }

        for(GoodsOutAuthAddParam en:enList) {
            if (StringUtils.isBlank(en.getEmpNo())) {
                throw new BizException(ResponseEnum.EXCEPTION.getCode(), "员工号不能为空！");
            }
            if (StringUtils.isBlank(en.getPowerFactoryId())) {
                throw new BizException(ResponseEnum.EXCEPTION.getCode(), "厂区不能为空！");
            }
            if (StringUtils.isBlank(en.getOutType())) {
                throw new BizException(ResponseEnum.EXCEPTION.getCode(), "出门类型不能为空！");
            }
            if (StringUtils.isBlank(en.getEmpDept())) {
                throw new BizException(ResponseEnum.EXCEPTION.getCode(), "审核部门不能为空！");
            }

            safetyGoodsAuthOwnerCollectDOMapper.deleteByEntity(en);
            safetyGoodsOutAuthDOMapper.deleteByEntity(en);
        }
    }

    public List<SafetyGoodsOutAuthDO> selectList(List<String> empNoList) {
        SafetyGoodsOutAuthDOExample example = new SafetyGoodsOutAuthDOExample();
        example.createCriteria().andEmpNoIn(empNoList);
        return safetyGoodsOutAuthDOMapper.selectByExample(example);
    }
}
