package com.yutong.safety.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CarInFactoryListParam;
import com.yutong.safety.common.param.web.LaneFactoryListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.WebserviceUtil;
import com.yutong.safety.core.db.entity.*;
import com.yutong.safety.core.db.mapper.SafetyCarInFactoryDOMapper;
import com.yutong.safety.core.db.mapper.SafetyRetentionBizMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 厂内车辆管理
 * @author gwxiexj
 * @since 2024-04-11 08:48
 */
@Service
public class SafetyCarInFactoryService {

    @Value("${car.in.factory.url}")
    private String carInFactoryUrl;

    @Value("${car.in.factory.hour}")
    private Integer carInFactoryHour;

    @Value("${car.in.factory.invehicle.url}")
    private String carInFactoryInvehicleUrl;

    private static Integer PAGE_SIZE = 100;

    @Autowired
    private SafetyCarInFactoryDOMapper safetyCarInFactoryDOMapper;

    @Autowired
    private SafetyRetentionBizMapper safetyRetentionBizMapper;

    @Autowired
    private WebserviceUtil webserviceUtil;

    @Autowired
    private SafetyLaneFactoryService safetyLaneFactoryService;

    /**
     * 查询列表
     * @param param 分页查询-入参
     * @return 返回结果集
     */
    public List<SafetyCarInFactoryDO> pageList(CarInFactoryListParam param) throws Exception{
        SafetyCarInFactoryDOExample example = new SafetyCarInFactoryDOExample();
        SafetyCarInFactoryDOExample.Criteria criteria = example.createCriteria();
        //只查询在厂车辆
        criteria.andParkStatusEqualTo(0);
        if(param.getIsRetention() != null){
            criteria.andIsRetentionEqualTo(param.getIsRetention());
        }
        if(StringUtils.isNotEmpty(param.getCarNo())){
            criteria.andCarNoLike(param.getCarNo());
        }
        if(StringUtils.isNotEmpty(param.getCarType())){
            criteria.andCarTypeEqualTo(param.getCarType());
        }
        if(StringUtils.isNotEmpty(param.getFactory())){
            criteria.andFactoryEqualTo(param.getFactory());
        }
        if(StringUtils.isNotEmpty(param.getBeginDate())){
            String endDate = param.getEndDate();
            Date edate;
            if(StringUtils.isNotEmpty(endDate)){
                edate = DateUtils.parse(endDate,1);
            }else{
                edate = new Date();
            }
            String newEnd = DateUtils.format(DateUtils.addDays(edate,1),1);
            criteria.andInTimeBetween(param.getBeginDate(),newEnd);
        }
        if(StringUtils.isNotEmpty(param.getStaffNo())){
            criteria.andStaffNoLike(param.getStaffNo());
        }
        if(StringUtils.isNotEmpty(param.getStaffName())){
            criteria.andStaffNameLike(param.getStaffName());
        }
        example.setOrderByClause(" in_time asc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        return safetyRetentionBizMapper.selectByExampleCarInFactory(example);
    }

    /**
     * 分页总条数
     *
     * @param param 查询入参
     * @return 返回
     */
    public long pageListCount(CarInFactoryListParam param) {
        SafetyCarInFactoryDOExample example = new SafetyCarInFactoryDOExample();
        SafetyCarInFactoryDOExample.Criteria criteria = example.createCriteria();
        criteria.andParkStatusEqualTo(0);
        if(param.getIsRetention() != null){
            criteria.andIsRetentionEqualTo(param.getIsRetention());
        }
        if(StringUtils.isNotEmpty(param.getCarNo())){
            criteria.andCarNoLike(param.getCarNo());
        }
        if(StringUtils.isNotEmpty(param.getCarType())){
            criteria.andCarTypeEqualTo(param.getCarType());
        }
        if(StringUtils.isNotEmpty(param.getFactory())){
            criteria.andFactoryEqualTo(param.getFactory());
        }
        if(StringUtils.isNotEmpty(param.getBeginDate())){
            String endDate = param.getEndDate();
            Date edate;
            if(StringUtils.isNotEmpty(endDate)){
                edate = DateUtils.parse(endDate,1);
            }else{
                edate = new Date();
            }
            String newEnd = DateUtils.format(DateUtils.addDays(edate,1),1);
            criteria.andInTimeBetween(param.getBeginDate(),newEnd);
        }
        if(StringUtils.isNotEmpty(param.getStaffNo())){
            criteria.andStaffNoLike(param.getStaffNo());
        }
        if(StringUtils.isNotEmpty(param.getStaffName())){
            criteria.andStaffNameLike(param.getStaffName());
        }
        return safetyCarInFactoryDOMapper.countByExample(example);
    }

    /**
     * 在厂车辆数据更新
     */
    public void taskCarInFactory() throws Exception{
        String carInFactoryType = "员工车辆,外协车辆";
        if(StringUtils.isEmpty(carInFactoryType)){
            LogUtils.info("在厂车辆配置-车辆类型为空!");
            return;
        }
        if(StringUtils.isEmpty(carInFactoryUrl)){
            LogUtils.info("在厂车辆接口为空!");
            return;
        }
        String curDate = DateUtils.format(DateUtils.addHours(new Date(),-carInFactoryHour),2);
        String[] carType = carInFactoryType.split(",");
        //1.设置车辆状态不在厂
        updateParkStaus();
        //2.获取车道与厂区关系
        LaneFactoryListParam param2 = new LaneFactoryListParam();
        List<SafetyLaneFactoryDOKey> fatoryList = safetyLaneFactoryService.getList(param2);
        Map<String,SafetyLaneFactoryDOKey> factoryMap = fatoryList.stream().collect(java.util.stream.Collectors.toMap(SafetyLaneFactoryDOKey::getLaneName, e -> e));
        for(String cur:carType){
            taskCarByType(cur,curDate,factoryMap);
        }
    }

    /**
     * 定时任务同步在厂车辆清单
     * @param carType 车辆类型
     */
    private void taskCarByType(String carType,String curDate,Map<String,SafetyLaneFactoryDOKey> factoryMap) throws Exception{
        JSONObject object = new JSONObject();
        String whereStr = " TcmName='"+carType+"' and InTime<'"+curDate+"'";
        object.put("PageSize",PAGE_SIZE);
        object.put("CurrentPage",1);
        object.put("OrderBy","InTime");
        object.put("where",whereStr);
        object.put("OrderType",true);

        String bodyParams = object.toJSONString();
        Map headParams = new HashMap(4);
        String returnJson = webserviceUtil.postJsonStr(carInFactoryUrl,bodyParams,headParams);
        if(StringUtils.isEmpty(returnJson)){
            LogUtils.info("taskCarInFactory方法，在厂车辆接口返回值为空!");
            return;
        }
        //2.逐页更新在厂车辆信息
        JSONObject retObject = JSONObject.parseObject(returnJson);
        int totalCount = retObject.getJSONObject("PageAttri").getInteger("TotalCount");
        if(totalCount == 0){
            LogUtils.info("未获取到在厂车辆列表，接口返回值="+returnJson);
            return;
        }
        JSONArray records = retObject.getJSONArray("Records");
        batchUpdate(records,factoryMap);

        if(records == null || records.size() <=0){
            LogUtils.info("未获取到在厂车辆列表，接口返回值=="+returnJson);
            return;
        }
        int totalPage = totalCount/PAGE_SIZE;
        int moreNum = totalCount%PAGE_SIZE;
        if(moreNum > 0){
            totalPage += 1;
        }
        for(int i=2;i<=totalPage;i++){
            JSONArray curRecords = getListByHttp(i,whereStr);
            //批量更新
            batchUpdate(curRecords,factoryMap);
        }
    }

    /**
     * 从接口获取厂区车辆
     *
     * @param curPage 当前页
     * @param where 接口过滤条件
     * @return 返回值
     */
    private JSONArray getListByHttp(int curPage,String where){

        JSONObject object = new JSONObject();
        object.put("PageSize",PAGE_SIZE);
        object.put("CurrentPage",curPage);
        object.put("OrderBy","InTime");
        object.put("where",where);
        object.put("OrderType",true);
        JSONArray retArray = new JSONArray();
        String returnJson = "";
        try {
            String bodyParams = object.toJSONString();
            Map headParams = new HashMap(4);
            returnJson = webserviceUtil.postJsonStr(carInFactoryUrl, bodyParams, headParams);

            if (StringUtils.isEmpty(returnJson)) {
                LogUtils.info("taskCarInFactory方法，在厂车辆接口返回值为空!");
                return retArray;
            }
            JSONObject retObject = JSONObject.parseObject(returnJson);
            retArray = retObject.getJSONArray("Records");
            if (retArray == null || retArray.size() <= 0) {
                LogUtils.info("第" + curPage + "未获取到在厂车辆列表，接口返回值==" + returnJson);
                return retArray;
            }
        }catch (Exception e){
            LogUtils.error("接口返回值异常",e);
            LogUtils.error("第" + curPage + "未获取到在厂车辆列表，接口返回值==" + returnJson);
            return retArray;
        }
        return retArray;
    }

    /**
     * 批量更新
     * @param records JSONArray数据
     */
    private void batchUpdate(JSONArray records,Map<String,SafetyLaneFactoryDOKey> factoryMap) throws Exception{
        List<SafetyCarInFactoryDO> carList = new ArrayList<>();
        if(records == null || records.size() <=0){
            return;
        }
        for (Object record : records) {
            JSONObject obj = (JSONObject) record;
            SafetyCarInFactoryDO carDao = convertBean(obj,factoryMap);
            carList.add(carDao);
        }
        if(carList.size()>0){
            safetyRetentionBizMapper.updateBatchCarInFactory(carList);
        }
    }

    /**
     * 类型转化
     * @param record 在厂车辆信息
     * @return 返回
     */
    private SafetyCarInFactoryDO convertBean(JSONObject record,Map<String,SafetyLaneFactoryDOKey> factoryMap){
        SafetyCarInFactoryDO carDao = new SafetyCarInFactoryDO();
        carDao.setCarNo(record.getString("TokenNo"));
        carDao.setCarType(record.getString("TcmName"));
        carDao.setCarCategory(record.getString("VehicleCategory"));
        carDao.setCarColor(record.getString("VehicleColor"));
        carDao.setStaffNo(record.getString("StaffNo"));
        carDao.setStaffName(record.getString("StaffName"));
        carDao.setInDoor(record.getString("InOperatorName"));
        String laneName = record.getString("InLaneName");
        String factory = "";
        if(StringUtils.isNotEmpty(laneName)){
            SafetyLaneFactoryDOKey laneFactory = factoryMap.get(laneName);
            if(laneFactory!=null){
                factory = laneFactory.getFactoryName();
            }
        }
        carDao.setInLaneName(laneName);
        carDao.setFactory(factory);
        String inTime = record.getString("InTime");
        if(StringUtils.isNotEmpty(inTime)){
            inTime = inTime.replace("T"," ");
        }
        //设置状态为在厂车辆
        carDao.setParkStatus(0);
        carDao.setInTime(inTime);
        return carDao;
    }

    /**
     * 更新车辆停车状态
     */
    private void updateParkStaus(){
        safetyRetentionBizMapper.updateParkStatus();
    }

    /**
     * 查询列表
     * @param param 不分页查询-入参
     * @return 返回结果集
     */
    public List<SafetyCarInFactoryDO> getList(CarInFactoryListParam param) throws Exception{
        SafetyCarInFactoryDOExample example = new SafetyCarInFactoryDOExample();
        SafetyCarInFactoryDOExample.Criteria criteria = example.createCriteria();
        //只查询在厂车辆
        criteria.andParkStatusEqualTo(0);
        if(param.getIsRetention() != null){
            criteria.andIsRetentionEqualTo(param.getIsRetention());
        }
        if(StringUtils.isNotEmpty(param.getCarNo())){
            criteria.andCarNoLike(param.getCarNo());
        }
        if(StringUtils.isNotEmpty(param.getCarType())){
            criteria.andCarTypeEqualTo(param.getCarType());
        }
        if(StringUtils.isNotEmpty(param.getFactory())){
            criteria.andFactoryEqualTo(param.getFactory());
        }
        if(StringUtils.isNotEmpty(param.getBeginDate())){
            String endDate = param.getEndDate();
            Date edate;
            if(StringUtils.isNotEmpty(endDate)){
                edate = DateUtils.parse(endDate,1);
            }else{
                edate = new Date();
            }
            String newEnd = DateUtils.format(DateUtils.addDays(edate,1),1);
            criteria.andInTimeBetween(param.getBeginDate(),newEnd);
        }
        if(StringUtils.isNotEmpty(param.getStaffNo())){
            criteria.andStaffNoLike(param.getStaffNo());
        }
        if(StringUtils.isNotEmpty(param.getStaffName())){
            criteria.andStaffNameLike(param.getStaffName());
        }
        example.setOrderByClause(" in_time asc ");
        return safetyCarInFactoryDOMapper.selectByExample(example);
    }

    public List<SafetyCarInFactoryDO> selectCarList(CarInFactoryListParam param) throws Exception{
        return safetyRetentionBizMapper.selectCarList(param);
    }

    public List<SafetyRetentionNoticeDO> getRetentionNoticeList(){
        return safetyRetentionBizMapper.getRetentionNoticeList();
    }

    public void modifyCarRetentionStatus(List<SafetyCarInFactoryDO> list) throws Exception{
        safetyRetentionBizMapper.modifyCarRetentionStatus(list);
    }

    public void updateByExampleSelective(SafetyCarInFactoryDO record,SafetyCarInFactoryDOExample example){
        safetyCarInFactoryDOMapper.updateByExampleSelective(record,example);
    }

    /**
     * 获取车辆最近进出场记录
     * @param plateNo 车牌号
     * @return 返回值
     */
    public SafetyCarInFactoryVO getInvehicleInfo(String plateNo){
        SafetyCarInFactoryVO carInFactoryVO = new SafetyCarInFactoryVO();
        try {
            carInFactoryVO.setIsSucess(false); //默认设置车辆不在厂

            String bodyParams = "\"tokenNo='"+plateNo+"'\"";
            Map headParams = new HashMap(4);
            headParams.put("Content-Type","application/json");
            String returnJson = webserviceUtil.postJsonStr(carInFactoryInvehicleUrl, bodyParams, headParams);

            if (StringUtils.isEmpty(returnJson)) {
                LogUtils.info("出入口接口未找到{}的出入场数据",plateNo);
                return null;
            }
            JSONObject retObject = JSONObject.parseObject(returnJson);
            JSONObject stateObj = retObject.getJSONObject("State");
            if(stateObj == null){
                return carInFactoryVO;
            }
            JSONObject retMap = retObject.getJSONObject("Model");
            if(retMap == null){
                return carInFactoryVO;
            }
            carInFactoryVO = retMap.toJavaObject(SafetyCarInFactoryVO.class);
            String inTime = carInFactoryVO.getInTime().replace("T"," ");
            String lastChargeTime = carInFactoryVO.getLastChargeTime().replace("T"," ");
            carInFactoryVO.setInTime(inTime);
            carInFactoryVO.setLastChargeTime(lastChargeTime);
            carInFactoryVO.setIsSucess(stateObj.getBoolean("IsSucess"));
        }catch (Exception e){
            LogUtils.error("查询出入口异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"查询出入口异常："+e.getMessage());
        }
        return carInFactoryVO;
    }
}
