package com.yutong.safety.core.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yutong.safety.common.param.web.CarOutRuleAddParam;
import com.yutong.safety.common.param.web.CarOutRuleListParam;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.WebserviceUtil;
import com.yutong.safety.common.vo.SafetyCarOutRuleVO;
import com.yutong.safety.core.db.entity.SafetyCarOutRuleDO;
import com.yutong.safety.core.db.entity.SafetyCarOutRuleDOExample;
import com.yutong.safety.core.db.mapper.SafetyCarOutRuleDOBizMapper;
import com.yutong.safety.core.db.mapper.SafetyCarOutRuleDOMapper;
import com.yutong.safety.core.db.mapper.SafetyPersonCarOutRuleDOBizMapper;
import com.yutong.safety.core.db.mapper.SafetyZCustomUtilDOBizMapper;
import com.yutong.safety.core.dto.SafetyPersonCarOutRuleDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gwxiexj
 * @since 2024-04-01 17:41
 */
@Service
public class SafetyCarOutRuleService {

    @Autowired
    private SafetyCarOutRuleDOMapper safetyCarOutRuleDOMapper;

    @Autowired
    private SafetyCarOutRuleDOBizMapper safetyCarOutRuleDOBizMapper;

    @Autowired
    private SafetyZCustomUtilDOBizMapper safetyZCustomUtilDOBizMapper;

    @Autowired
    private SafetyPersonCarOutRuleDOBizMapper safetyPersonCarOutRuleDOBizMapper;

    @Value("${crk.checkRule.url}")
    private String crkCheckUrl;

    @Autowired
    private WebserviceUtil webserviceUtil;
    /**
     * 更新规则
     * @param param 更新入参
     * @return 返回
     * @throws Exception 异常
     */
    public int saveOrUpdate(CarOutRuleAddParam param) throws Exception{
        int updateRows = 0;
        SafetyCarOutRuleDO record = convertCarOutRuleVO(param);
        Integer ruleId = param.getRuleId();

        if (ruleId == null) {
            record.setCreateTime(new Date());
            updateRows = safetyCarOutRuleDOMapper.insert(record);
        } else {
            record.setId(param.getRuleId());
            updateRows = safetyCarOutRuleDOMapper.updateByPrimaryKey(record);
        }
        return updateRows;
    }

    /**
     * 做删除标记
     *
     * @param ruleId 规则主键
     * @return 返回
     */
    public int deleteById(String ruleId) throws Exception {
        int i;
        SafetyCarOutRuleDO record = new SafetyCarOutRuleDO();
        record.setIsDelete(Short.valueOf("1"));
        record.setId(Integer.parseInt(ruleId));
        i = safetyCarOutRuleDOMapper.updateDelFlagByPrimaryKey(record);
        return i;
    }

    /**
     * 分页查询
     *
     * @param param 查询入参
     * @return 返回
     */
    public List<SafetyCarOutRuleVO> pageList(CarOutRuleListParam param) {
        SafetyCarOutRuleDOExample example = new SafetyCarOutRuleDOExample();
        SafetyCarOutRuleDOExample.Criteria criteria = example.createCriteria();
        //只查询未删除状态的规则
        criteria.andIsDeleteEqualTo(Short.valueOf("0"));
        if (StringUtils.isNotEmpty(param.getPostName())) {
            criteria.andPostNameLike("%" + param.getPostName() + "%");
        }
        if (StringUtils.isNotEmpty(param.getPostCategory())) {
            criteria.andPostCategoryLike("%" + param.getPostCategory() + "%");
        }
        if (!ObjectUtils.isEmpty(param.getCheckFlag())) {
            criteria.andCheckFlagEqualTo(param.getCheckFlag());
        }
        if (!ObjectUtils.isEmpty(param.getRemark())) {
            criteria.andRemarkLike("%" + param.getRemark() + "%");
        }
        example.setOrderByClause(" priority asc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        return safetyCarOutRuleDOBizMapper.selectListByExample(example);
    }

    /**
     * 分页总条数
     *
     * @param param 查询入参
     * @return 返回
     */
    public long pageListCount(CarOutRuleListParam param) {
        SafetyCarOutRuleDOExample example = new SafetyCarOutRuleDOExample();
        SafetyCarOutRuleDOExample.Criteria criteria = example.createCriteria();
        //只查询未删除状态的规则
        criteria.andIsDeleteEqualTo(Short.valueOf("0"));
        if (StringUtils.isNotEmpty(param.getPostName())) {
            criteria.andPostNameLike("%" + param.getPostName() + "%");
        }
        if (StringUtils.isNotEmpty(param.getPostCategory())) {
            criteria.andPostCategoryLike("%" + param.getPostCategory() + "%");
        }
        if (!ObjectUtils.isEmpty(param.getCheckFlag())) {
            criteria.andCheckFlagEqualTo(param.getCheckFlag());
        }
        if (!ObjectUtils.isEmpty(param.getRemark())) {
            criteria.andRemarkLike("%" + param.getRemark() + "%");
        }
        return safetyCarOutRuleDOMapper.countByExample(example);
    }

    /**
     * 对象转换
     * @param param 保存入参
     * @return 返回
     * @throws Exception 异常
     */
    private SafetyCarOutRuleDO convertCarOutRuleVO(CarOutRuleAddParam param) throws Exception {

        SafetyCarOutRuleDO record = new SafetyCarOutRuleDO();
        record.setPostName(param.getPostName() == null ? "" : param.getPostName());
        record.setPostCategory(param.getPostCategory() == null ? "" : param.getPostCategory());
        record.setOrganCode(param.getOrganCode() == null ? "" : param.getOrganCode());
        record.setOrganName(param.getOrganName() == null ? "" : param.getOrganName());
        record.setRemark(param.getRemark() == null ? "" : param.getRemark());
        record.setCheckFlag(param.getCheckFlag() == null ? "" : param.getCheckFlag());
        record.setPriority(Integer.valueOf(param.getPriority()));
        record.setUpdateTime(new Date());
        Integer isDelete = param.getIsDelete();
        //默认状态正常
        if (isDelete == null) {
            isDelete = 0;
        }
        record.setIsDelete(isDelete.shortValue());

        return record;
    }

    /**
     * 根据主键获取对象
     * @param ruleId 规则主键
     * @return 返回
     */
    public SafetyCarOutRuleDO getRuleByKey(Integer ruleId){
        if(ruleId == null){
            return null;
        }
        return safetyCarOutRuleDOMapper.selectByPrimaryKey(ruleId);
    }

    /**
     * 每天同步员工及对应检查标记给门禁系统
     *
     * @author lukunpeng
     * @time 2024-4-3
     * @return
     *
     */
    public Object doTask() {

        List<Map> changedRuleList = safetyZCustomUtilDOBizMapper.findChangedRule();
        List<String> allList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(changedRuleList)){
            allList = safetyZCustomUtilDOBizMapper.findEmpByRule(changedRuleList);
        }
        //查询当天有变更的所有员工
        List<String> empList = safetyZCustomUtilDOBizMapper.findChangedEmp();
        allList = listEliminate(allList, empList);
        //根据员工匹配查询对应标记
        if(CollectionUtils.isNotEmpty(allList)){
            //根据员工号集合获取各个类型对应的集合
            Map<String,List<String>> vehicleTypeLists = getVehicleType(allList);
            //调门禁接口传数据
            vehicleTypeLists.forEach((key,value)->sendDataToCrkDo(key,value));
            int delete = deleteRule();
            int update = updateHistory(changedRuleList);
            LogUtils.info("本次更新{}条,删除{}条",update,delete);
        }else{
            LogUtils.info("无变更记录,不做操作");
            return true;
        }
        return true;
    }

    public Object downAll() {
        //查询全部的岗位规则
        List<Map> changedRuleList = safetyZCustomUtilDOBizMapper.findChangedRuleAll();
        List<String> allList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(changedRuleList)){
            //查询岗位下对应的人员
            allList = safetyZCustomUtilDOBizMapper.findEmpByRule(changedRuleList);
        }

        //查询设置的个人规则
        List<SafetyPersonCarOutRuleDTO> perList = safetyPersonCarOutRuleDOBizMapper.pageList("", "", "", "", 0, 99999);
        Map<String, SafetyPersonCarOutRuleDTO> speciaMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(perList)){
            speciaMap = perList.stream().collect(Collectors.toMap(SafetyPersonCarOutRuleDTO::getEmpNo, safetyPersonCarOutRuleDTO -> safetyPersonCarOutRuleDTO, (oldValue, newValue) -> oldValue));
        }

        //根据员工匹配查询对应标记
        if(CollectionUtils.isNotEmpty(allList)){
            //根据员工号集合获取各个类型对应的集合
            LogUtils.info("SafetyCarOutRuleService[downAll.allList]:"+allList);
            LogUtils.info("SafetyCarOutRuleService[downAll.speciaMap]:"+speciaMap);
            Map<String,List<String>> vehicleTypeLists = getVehicleType2(allList,speciaMap);
            LogUtils.info("SafetyCarOutRuleService[downAll.vehicleTypeLists]:"+vehicleTypeLists);
            //调门禁接口传数据
            vehicleTypeLists.forEach((key,value)->sendDataToCrkDo(key,value));
            int delete = deleteRule();
            int update = updateHistory(changedRuleList);
            LogUtils.info("本次更新{}条,删除{}条",update,delete);
        }else{
            LogUtils.info("无规则记录,不做操作");
            return true;
        }
        return true;
    }

    private Map<String,List<String>> getVehicleType2(List<String> allList,Map<String, SafetyPersonCarOutRuleDTO> speciaMap) {
        List<String> focusList = new ArrayList<>(); //重点检查
        List<String> normalList = new ArrayList<>(); //正常检查
        List<String> freeList = new ArrayList<>(); //免检车
        for (String empNo : allList) {
            String result = "正常检查";

            if(!ObjectUtils.isEmpty(speciaMap) && !ObjectUtils.isEmpty(speciaMap.get(empNo))){
                SafetyPersonCarOutRuleDTO en = speciaMap.get(empNo);
                result = en.getCheckFlag();
            }else{
                //根据员工类型默认查询
                Map<String,String> defFlag = safetyZCustomUtilDOBizMapper.findDefFlag(empNo);
                if(ObjectUtils.isEmpty(defFlag)){
                    //根据员工号 和 间接岗位 查询, 判断员工是不是间接,是默认不检查
                    int i = safetyZCustomUtilDOBizMapper.findEmpNums(empNo);
                    if(i > 0){
                        result = "不检查";
                    }
                }else{
                    //根据员工部门查询检查标记
                    Map<String,String> deptFlag = safetyZCustomUtilDOBizMapper.findDeptFlag(empNo);
                    //根据员工岗位查询检查标记
                    Map<String,String> postFlag = safetyZCustomUtilDOBizMapper.findPostFlag(empNo);
                    //为空 赋默认值
                    String deptNum = deptFlag==null||deptFlag.get("priority")==null?"1000":deptFlag.get("priority");
                    String postNum = postFlag==null||postFlag.get("priority")==null?"1000":postFlag.get("priority");
                    if(ObjectUtils.isEmpty(deptFlag) && ObjectUtils.isEmpty(postFlag)){
                        result = defFlag.get("flag");
                    }else {
                        //优先级：按照优先级进行标记，如一个人同时应用两个标记设置，则仅优先级高的设置生效，优先级低的设置不生效（优先级1最高-10最低）
                        LogUtils.info("员工={}, dept优先级={}, post优先级={}", empNo, deptNum, postNum);
                        result = Integer.valueOf(deptNum) >= Integer.valueOf(postNum) ? postFlag.get("flag") : deptFlag.get("flag");
                    }
                }
            }

            switch (result){
                case "免检车" : freeList.add(empNo); break;
                case "不检查" : freeList.add(empNo); break;
                case "正常检查" : normalList.add(empNo); break;
                case "重点检查" : focusList.add(empNo); break;
                case "检查" : normalList.add(empNo); break;
            }
        }
        Map<String,List<String>> vehicleTypeLists = new HashMap<>();
        vehicleTypeLists.put("重点检查",focusList);
        vehicleTypeLists.put("正常检查",normalList);
        vehicleTypeLists.put("免检车",freeList);
        LogUtils.info("SafetyCarOutRuleService[getVehicleType2.vehicleTypeLists]:"+vehicleTypeLists);
        //返回标记集合
        return vehicleTypeLists;
    }

    private int deleteRule() {
        return safetyZCustomUtilDOBizMapper.deleteRule();
    }

    private int updateHistory(List<Map> changedRuleList) {
        int total = 0;
        for (Map<String, Object> map : changedRuleList) {
            String id = map.get("id")+"";
            SafetyCarOutRuleDO ruleDO = safetyCarOutRuleDOMapper.selectByPrimaryKey(Integer.parseInt(id));
            if(ObjectUtils.isEmpty(ruleDO)){ //已删除数据不做处理
                continue;
            }else if(ObjectUtils.isEmpty(map.get("horganCode"))){ //无历史记录,插入(首次编辑)
                total += safetyZCustomUtilDOBizMapper.insertHistory(ruleDO);
            }else{  //有记录更新(多次编辑)
                total += safetyZCustomUtilDOBizMapper.updateHistory(ruleDO);
            }
        }
        return total;
    }

    /**
     * 调用接口回传门禁系统
     * @param type
     * @param empList
     */
    private void sendDataToCrkDo(String type, List<String> empList) {
        //接口单次最多10条同类型员工,按照类型集合,拆分循环调用接口
        List<List<String>> partition = Lists.partition(empList, 10);
        for (List<String> empNos: partition) {
            String empNoStr = empNos.stream().collect(Collectors.joining(","));
            JSONObject bodyParam = new JSONObject();
            bodyParam.put("StaffNo",empNoStr);
            bodyParam.put("VehicleType",type);
            String crkResult = sendPost(crkCheckUrl, bodyParam.toString());
            LogUtils.info("回传出入口车辆检查标记返回值{}",crkResult);
        }
    }

    private Map<String,List<String>> getVehicleType(List<String> empNos) {
        List<String> focusList = new ArrayList<>(); //重点检查
        List<String> normalList = new ArrayList<>(); //正常检查
        List<String> freeList = new ArrayList<>(); //免检车
        for (String empNo : empNos) {
            String result = "正常检查";
            //根据员工类型默认查询
            Map<String,String> defFlag = safetyZCustomUtilDOBizMapper.findDefFlag(empNo);
            if(ObjectUtils.isEmpty(defFlag)){
                //根据员工号 和 间接岗位 查询, 判断员工是不是间接,是默认不检查
                int i = safetyZCustomUtilDOBizMapper.findEmpNums(empNo);
                if(i > 0){
                    result = "不检查";
                }
            }else{
                //根据员工部门查询检查标记
                Map<String,String> deptFlag = safetyZCustomUtilDOBizMapper.findDeptFlag(empNo);
                //根据员工岗位查询检查标记
                Map<String,String> postFlag = safetyZCustomUtilDOBizMapper.findPostFlag(empNo);
                //为空 赋默认值
                String deptNum = deptFlag==null||deptFlag.get("priority")==null?"1000":deptFlag.get("priority");
                String postNum = postFlag==null||postFlag.get("priority")==null?"1000":postFlag.get("priority");
                if(ObjectUtils.isEmpty(deptFlag) && ObjectUtils.isEmpty(postFlag)){
                    result = defFlag.get("flag");
                }else {
                    //优先级：按照优先级进行标记，如一个人同时应用两个标记设置，则仅优先级高的设置生效，优先级低的设置不生效（优先级1最高-10最低）
                    LogUtils.info("员工={}, dept优先级={}, post优先级={}", empNo, deptNum, postNum);
                    result = Integer.valueOf(deptNum) >= Integer.valueOf(postNum) ? postFlag.get("flag") : deptFlag.get("flag");
                }
                switch (result){
                    case "免检车" : freeList.add(empNo); break;
                    case "不检查" : freeList.add(empNo); break;
                    case "正常检查" : normalList.add(empNo); break;
                    case "重点检查" : focusList.add(empNo); break;
                    case "检查" : normalList.add(empNo); break;
                }
            }
        }
        Map<String,List<String>> vehicleTypeLists = new HashMap<>();
        vehicleTypeLists.put("重点检查",focusList);
        vehicleTypeLists.put("正常检查",normalList);
        vehicleTypeLists.put("免检车",freeList);
        //返回标记集合
        return vehicleTypeLists;
    }

    /**
     * list去重合并
     * @param listA
     * @param listB
     * @return
     */
    private List<String>  listEliminate(List<String> listA,List<String> listB){
        HashSet<String> hs1 = new HashSet<>(listA);
        hs1.addAll(listB);
        List<String> retList = new ArrayList<>(hs1);
        return retList;
    }
    /**
     * 发起POST请求
     *
     * @param url
     *            url
     * @param paramJson
     *            参数的json格式
     */
    public static String sendPost(String url, String paramJson) {

        // 创建httpClient实例对象
        HttpClient httpClient = new HttpClient();
        // 设置httpClient连接主机服务器超时时间：15000毫秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        // 创建post请求方法实例对象
        PostMethod postMethod = new PostMethod(url);
        // 设置post请求超时时间
        postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        postMethod.addRequestHeader("Content-Type", "application/json");
        try {
            // json格式的参数解析
            RequestEntity entity = new StringRequestEntity(paramJson, "application/json", "UTF-8");
            postMethod.setRequestEntity(entity);

            int statusCode = httpClient.executeMethod(postMethod);
            if (statusCode == HttpStatus.SC_OK) {
                String result = postMethod.getResponseBodyAsString();
                postMethod.releaseConnection();
                return result;
            } else {
                LogUtils.error("POST请求发出失败，请求的地址为{}，参数为{}，错误信息为{}", url, paramJson, "HTTP状态码: " + statusCode);
            }
        } catch (IOException e) {
            LogUtils.error("POST请求发出失败，请求的地址为{}，参数为{}，错误信息为{}",url,paramJson, e.getMessage());
        }finally {
            postMethod.releaseConnection();
        }
        return null;
    }
}
