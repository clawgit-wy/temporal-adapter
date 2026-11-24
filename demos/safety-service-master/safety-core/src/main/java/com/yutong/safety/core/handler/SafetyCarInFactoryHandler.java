package com.yutong.safety.core.handler;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CarInFactoryListParam;
import com.yutong.safety.common.param.web.RetentionNoticeListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.NoticeLogVO;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.web.RetentionNoticeVO;
import com.yutong.safety.core.db.entity.*;
import com.yutong.safety.core.db.mapper.SafetyStaffEvectionDOBizMapper;
import com.yutong.safety.core.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gwxiexj
 * @since 2024-04-11 13:11
 */
@Component
public class SafetyCarInFactoryHandler {
    @Value("${iam.retention.robotId}")
    private String robotId;

    @Autowired
    private SafetyCarInFactoryService safetyCarInFactoryService;

    @Autowired
    private SafetyRetentionWhiteService safetyRetentionWhiteService;

    @Autowired
    private SafetyRetentionInformLogService safetyRetentionInformLogService;

    @Autowired
    private SafetyRetentionNoticeService safetyRetentionNoticeService;

    @Autowired
    private SafetyEmployeeMessageService safetyEmployeeMessageService;

    @Autowired
    private SafetyGeneralConfigService safetyGeneralConfigService;

    @Autowired
    private SafetyStaffEvectionDOBizMapper safetyStaffEvectionDOBizMapper;

    /**
     * 分页获取滞留车辆列表
     * @param param 入参
     * @return 分页数据
     */
    public PageVO getCarList(CarInFactoryListParam param) throws Exception{
        //设置只查询滞留车辆
        List<SafetyCarInFactoryDO> list = safetyCarInFactoryService.pageList(param);
        long total = safetyCarInFactoryService.pageListCount(param);
        return new PageVO<SafetyCarInFactoryDO>(total, list);
    }

    /**
     * 从接口获取在厂车辆定时任务
     * @return 返回
     */
    public Object taskCarInFactory() throws Exception{
        safetyCarInFactoryService.taskCarInFactory();
        return null;
    }

    /**
     * 数据导出操作
     * @param param 入参
     * @return 返回值
     */
    public HSSFWorkbook export(CarInFactoryListParam param) throws Exception{
        //设置只查询滞留车辆
        List<SafetyCarInFactoryDO> list = safetyCarInFactoryService.getList(param);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"车牌号", "车辆类型", "车辆分类", "员工号", "姓名", "入场大门", "入厂时间", "停车时长",
                "通知状态", "厂区","车辆颜色"};
        String[][] values = new String[list.size()][];
        SafetyCarInFactoryDO carInFactoryDO;
        for(int i=0; i<list.size(); i++) {
            carInFactoryDO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = carInFactoryDO.getCarNo();
            values[i][1] = carInFactoryDO.getCarType();
            values[i][2] = carInFactoryDO.getCarCategory();
            values[i][3] = carInFactoryDO.getStaffNo();
            values[i][4] = carInFactoryDO.getStaffName();
            values[i][5] = carInFactoryDO.getInDoor();
            String inTime = carInFactoryDO.getInTime();
            values[i][6] = inTime;
            values[i][7] = Math.round(DateUtils.sub(new Date(),DateUtils.parse(inTime,2))/3600)+"";
            int informStatus = carInFactoryDO.getInformStatus();
            String informStatusStr = "";
            if(informStatus == 0){
                informStatusStr = "正常";
            }
            if(informStatus == 1){
                informStatusStr = "待发送";
            }
            if(informStatus == 2){
                informStatusStr = "已发送";
            }
            values[i][8] = informStatusStr;
            values[i][9] = carInFactoryDO.getFactory();
            values[i][10] = carInFactoryDO.getCarColor();
        }
        String sheetName = "厂内车辆信息列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 处理滞留逻辑车辆
     */
    public void dealRetentionCar() throws Exception{
        CarInFactoryListParam param = new CarInFactoryListParam();
        //1.获取厂内车辆列表
        List<SafetyCarInFactoryDO> carList = safetyCarInFactoryService.selectCarList(param);
        //2.获取滞留通知规则
        List<SafetyRetentionNoticeDO> noticeList = safetyCarInFactoryService.getRetentionNoticeList();

        //查询公出人员名单
        List<SafetyStaffEvectionDO> gcList = safetyStaffEvectionDOBizMapper.getGcList();
        Map<String, SafetyStaffEvectionDO> gcMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(gcList)){
            gcMap = gcList.stream().collect(Collectors.toMap(SafetyStaffEvectionDO::getStaffNo, safetyStaffEvectionDO -> safetyStaffEvectionDO, (existing, replacement) -> existing));
        }

        List<SafetyCarInFactoryDO> retentionList = new ArrayList<>();
        List<SafetyCarInFactoryDO> whiteCarToTalList = new ArrayList<>();
        if(carList.size()>0 && noticeList.size() >0 ){
            Map<String,SafetyRetentionNoticeDO> maps = noticeList.stream().collect(Collectors.toMap(SafetyRetentionNoticeDO::getCarType, e -> e));
            for(Map.Entry<String,SafetyRetentionNoticeDO> entry:maps.entrySet()){
                String carType = entry.getKey();
                SafetyRetentionNoticeDO noticeDO = entry.getValue();
                Integer retentionHour = noticeDO.getRetentionHour();
                if(StringUtils.isEmpty(carType) || retentionHour == null){
                    continue;
                }
                List<SafetyCarInFactoryDO> subList = carList.stream().filter(a->carType.equals(a.getCarType())).collect(Collectors.toList());
                if(subList.size() <= 0){
                    continue;
                }
                //3.判定入场时间+滞留时长<当前时间时，表示滞留车辆
                for(SafetyCarInFactoryDO carDO:subList){
                    String inTime = carDO.getInTime();
                    Date endTime = DateUtils.addHours(DateUtils.parse(inTime,2),retentionHour);
                    long diffTime = DateUtils.sub(new Date(),endTime);

                    //存在公出信息：停放总时长与允许滞留时长比较，如果停放总时长大于允许滞留时长，
                    //①且小于公出开始时间，当天提醒。
                    //②如果在公出时间内(当前时间大于公出开始时间小于公出结束时间)，不短信提醒。
                    //③如果停放总时长大于公出结束时间+48小时，当天8：30提醒。
                    if (!ObjectUtils.isEmpty(gcMap.get(carDO.getStaffNo()))) {
                        Date endTime2 = DateUtils.addHours(DateUtils.parse(inTime,3),retentionHour);
                        SafetyStaffEvectionDO safetyStaffEvectionDO = gcMap.get(carDO.getStaffNo());
                        String beginDate = safetyStaffEvectionDO.getBeginDate();
                        LogUtils.info("dealRetentionCar[safetyStaffEvectionDO]"+safetyStaffEvectionDO);
                        if(StringUtils.isNotBlank(beginDate) && DateUtils.sub(DateUtils.parse(beginDate,3),endTime2) > 0){
                            diffTime = 1;
                        }

                        String endDate = safetyStaffEvectionDO.getEndDate();
                        if(StringUtils.isNotBlank(beginDate) && StringUtils.isNotBlank(endDate)
                            && DateUtils.sub(DateUtils.parse(beginDate,3),endTime2) <= 0
                            && DateUtils.sub(DateUtils.parse(endDate,3),endTime2) >= 0
                        ){
                            diffTime = 0;
                        }

                        if(StringUtils.isNotBlank(endDate)
                                && DateUtils.sub(new Date(),DateUtils.addHours(DateUtils.parse(endDate,3),48)) > 0
                                && DateUtils.sub(DateUtils.parse(inTime,3),DateUtils.parse(endDate,3)) < 0
                        ){
                            //控制时间为8.30
                            if(DateUtils.sub(new Date(),DateUtils.parse(DateUtils.format(new Date(),1)+" 08:30:00",3)) >= 0){
                                diffTime = 1;
                            }else{
                                diffTime = 0;
                            }
                        }
                    }

                    if(diffTime>0){
                        carDO.setIsRetention(1);
                        if(carDO.getInformStatus() == 0){
                            carDO.setInformStatus(1);
                        }
                        retentionList.add(carDO);
                    }else{
                        whiteCarToTalList.add(carDO);
                    }
                }
            }
        }
        //3.获取白名单规则获取排除滞留车辆
        List<SafetyRetentionWhiteDO> whiteDOList = safetyRetentionWhiteService.getList(new CarInFactoryListParam());
        if(retentionList.size() > 0){
            for(SafetyRetentionWhiteDO whiteDO:whiteDOList){
                if(StringUtils.isNotEmpty(whiteDO.getEndTime())){
                    long diff = DateUtils.sub(new Date(),DateUtils.parse(whiteDO.getEndTime()+" 23:59:59",2));
                    if(diff <=0){
                        continue;
                    }
                }
                CarInFactoryListParam curParam = convertWhiteBean(whiteDO);
                List<SafetyCarInFactoryDO> whiteCarList = safetyCarInFactoryService.selectCarList(curParam);
                if(whiteCarList.size()>0){
                    //移出白名单车辆
                    for(SafetyCarInFactoryDO whiteCar:whiteCarList){
                        List<SafetyCarInFactoryDO> curWhiteCar = retentionList.stream().filter(e->whiteCar.getCarNo().equals(e.getCarNo())).collect(Collectors.toList());
                        retentionList = retentionList.stream().filter(e->!whiteCar.getCarNo().equals(e.getCarNo())).collect(Collectors.toList());
                        whiteCarToTalList.addAll(curWhiteCar);
                    }
                }
            }
        }
        //批量更新车辆状态为滞留
        if(retentionList.size()>0){
            safetyCarInFactoryService.modifyCarRetentionStatus(retentionList);
        }
        if(whiteCarToTalList.size()>0){
            List<String> whiteCarNoList =  whiteCarToTalList.stream().map(e->e.getCarNo()).collect(Collectors.toList());
            batchUpdateRetentionStauts(0,whiteCarNoList);
        }

    }

    private void batchUpdateRetentionStauts(Integer retentionStatus,List<String> whiteCarNoList){
        SafetyCarInFactoryDO record = new SafetyCarInFactoryDO();
        if(retentionStatus == null){
            retentionStatus = 0;
        }
        record.setIsRetention(retentionStatus);
        record.setUpdateTime(new Date());

        SafetyCarInFactoryDOExample example = new SafetyCarInFactoryDOExample();
        SafetyCarInFactoryDOExample.Criteria criteria = example.createCriteria();
        criteria.andCarNoIn(whiteCarNoList);
        safetyCarInFactoryService.updateByExampleSelective(record,example);
    }


    /**
     * 滞留车信息推送
     */
    public void sendRetentionNoticeTask() throws Exception{
        //1.获取车辆下发记录汇总-滞留次数和最后一次入场的下发次数和入场时间
        List<NoticeLogVO> logVOList = safetyRetentionInformLogService.getNoticeLogReport();
        Map<String,NoticeLogVO> logVOMap = logVOList.stream().collect(java.util.stream.Collectors.toMap(NoticeLogVO::getCarNo, e -> e));

        //2.获取滞留车辆列表
        CarInFactoryListParam param = new CarInFactoryListParam();
        //获取滞留车辆
        param.setIsRetention(1);
        List<SafetyCarInFactoryDO> retentionList = safetyCarInFactoryService.getList(param);
        if(retentionList.size()<=0){
            LogUtils.info("不存在滞留车辆，无需通知");
            return;
        }
        //3.获取通知规则
        RetentionNoticeListParam param1 = new RetentionNoticeListParam();
        //有效规则
        param1.setIsUsed(1);
        List<RetentionNoticeVO> ruleDOList = safetyRetentionNoticeService.getNoticeRuleList(null);
        if(ruleDOList.size()<=0){
            LogUtils.info("未设置滞留通知规则，无需通知");
            return;
        }
        //获取车辆类型集合
        List<String> carTypeList = ruleDOList.stream().map(c->c.getCarType()).distinct().collect(Collectors.toList());
        List<SafetyCarInFactoryDO> sendList = new ArrayList<>();
        List<SafetyCarInFactoryDO> toDoSendList = new ArrayList<>();
        List<SafetyRetentionInformRecordDO> recordDOList = new ArrayList<>();
        Date curDate = new Date();
        for(String carType:carTypeList){
            //获取车辆通知次数
            List<RetentionNoticeVO> curTypeRuleList = ruleDOList.stream().filter(e->carType.equals(e.getCarType())).collect(Collectors.toList());
            int totalRuleSendNum = curTypeRuleList.stream().mapToInt(RetentionNoticeVO::getSendNum).sum();
            List<RetentionNoticeVO> oneList = curTypeRuleList.stream().filter(e-> "小于等于".equals(e.getRemindRule())).collect(Collectors.toList());
            List<RetentionNoticeVO> twoList = curTypeRuleList.stream().filter(e-> "大于".equals(e.getRemindRule())).collect(Collectors.toList());
            RetentionNoticeVO oneLevel = null;
            RetentionNoticeVO twoLevel = null;
            if(oneList.size()>0){
                oneLevel = oneList.get(0);
            }
            if(twoList.size()>0){
                twoLevel = twoList.get(0);
            }
            if(oneLevel == null){
                continue;
            }
            int oneLevelNum = oneLevel.getSendNum();
            int oneLevelGap = oneLevel.getRemindGap();
            String oneRemindType = oneLevel.getRemindType();
            String oneContent  = oneLevel.getContent();

            int twoLevelGap = 30;
            String twoRemindType = "";
            String twoContent = "";

            if(twoLevel != null){
                twoLevelGap = twoLevel.getRemindGap();
                twoRemindType= twoLevel.getRemindType();
                twoContent = twoLevel.getContent();
            }
            List<SafetyCarInFactoryDO> curTypeCarList = retentionList.stream().filter(e->e.getCarType().equals(carType)).collect(Collectors.toList());
            if(curTypeCarList.size() <=0){
                LogUtils.info("“"+carType+"”无滞留车辆，无需通知");
                continue;
            }
            for(SafetyCarInFactoryDO car:curTypeCarList){
                String carNo = car.getCarNo();
                int informStatus = 1;
                int sendNum = 0;
                int retentionNum = 0;
                int sendLevel = 1;
                String sendType = "";
                String inTime = "";
                String noticeTime ;
                String sendContent = "";
                NoticeLogVO noticeLogVO = logVOMap.get(carNo);
                if(noticeLogVO != null){
                    sendNum = noticeLogVO.getNoticeNums();
                    retentionNum = noticeLogVO.getRetentionNums();
                    inTime = noticeLogVO.getInTime();
                    noticeTime = noticeLogVO.getNoticeTime();
                }else{
                    noticeTime = DateUtils.format(curDate,2);
                }
                //入场时间发生变化时，消息推送次数和最后推送时间重新赋值
                if(!car.getInTime().equals(inTime)){
                    //入场时间不一致时，滞留次数+1
                    retentionNum ++;
                    sendNum = 0;
                    noticeTime = DateUtils.format(curDate,2);
                }
                //待通知次数
                boolean sendFlag = false;
                Date nextDate = null;
                if(sendNum<totalRuleSendNum){
                    sendType = oneRemindType;
                    sendContent = oneContent;
                    if(sendNum==0){
                        sendFlag = true;
                    }
                    if(sendNum>0 && sendNum<oneLevelNum){
                        nextDate = DateUtils.addMinutes(curDate,-oneLevelGap);
                    }
                    if(sendNum>=oneLevelNum && sendNum < totalRuleSendNum){
                        sendLevel = 2;
                        nextDate = DateUtils.addMinutes(curDate,-twoLevelGap);
                        sendType = twoRemindType;
                        sendContent = twoContent;
                    }
                    if(StringUtils.isEmpty(sendType)){
                        LogUtils.info("“"+carNo+"”推送方式为空，无需通知");
                        continue;
                    }
                    if(nextDate != null){
                        long diff = DateUtils.sub(nextDate,DateUtils.parse(noticeTime,2));
                        if(diff>0){
                            sendFlag = true;
                        }
                    }
                }
                if(sendNum>0){
                    informStatus = 2;
                }
                if(sendFlag){
                    car.setRetentionNum(retentionNum);
                    toDoSendList.add(car);
                    SafetyRetentionInformRecordDO recordDO = convertInformBean(car,sendLevel,sendType,sendContent);
                    recordDO.setCreateTime(curDate);
                    recordDOList.add(recordDO);
                }else if(informStatus==2){
                    car.setRetentionNum(informStatus);
                    sendList.add(car);
                }
            }
        }
        if(recordDOList.size()>0){
            sendRetentionNotice(recordDOList,toDoSendList);
        }
        if(sendList.size()>0){
            safetyCarInFactoryService.modifyCarRetentionStatus(sendList);
        }
    }

    /**
     *
     * @param recordDOList
     */
    private void sendRetentionNotice(List<SafetyRetentionInformRecordDO> recordDOList,List<SafetyCarInFactoryDO> carList) throws Exception{
        List<SafetyCarInFactoryDO> sendList = new ArrayList<>();
        List<SafetyRetentionInformRecordDO> sendHis = new ArrayList<>();
        List<String> staffNoList = recordDOList.stream().map(e->e.getStaffNo()).collect(Collectors.toList());
        List<SafetyEmployeeMessageDO> employeeList = safetyEmployeeMessageService.getListByStaffNo(staffNoList);
        if(employeeList==null || employeeList.size() <=0 ){
            LogUtils.info("未找到对应的员工信息，不予通知");
            return;
        }
        Map<String,SafetyCarInFactoryDO> carMap = carList.stream().collect(java.util.stream.Collectors.toMap(SafetyCarInFactoryDO::getCarNo, e -> e));
        Map<String,SafetyEmployeeMessageDO> employeeMap = employeeList.stream().collect(java.util.stream.Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, e -> e));
        for(SafetyRetentionInformRecordDO recordDO:recordDOList){
            int informStatus = 1;
            String staffNo = recordDO.getStaffNo();
            String informType = recordDO.getInformType();
            String remindContent = formatSendCotent(recordDO);
            String phone = employeeMap.get(staffNo)!=null?employeeMap.get(staffNo).getPhoneNo():"";
            recordDO.setInformContent(remindContent);
            recordDO.setTelephone(phone);

            String ret = "";
            if(informType.contains("1") && StringUtils.isNotEmpty(phone)){
                ret = safetyGeneralConfigService.sendMsgSms(phone, remindContent);
                informStatus = 2;
            }
            if(informType.contains("2")){
                ret = safetyGeneralConfigService.sendMsgIm(staffNo, robotId, remindContent);
                informStatus = 2;
            }
            LogUtils.info("消息推送staffNo="+staffNo+"推送类型="+informType+"状态：ret"+ret);
            recordDO.setRemindType(informType);
            if("1,2".equals(informType)){
                SafetyRetentionInformRecordDO newRecord = new SafetyRetentionInformRecordDO();
                BeanUtils.copyProperties(recordDO,newRecord);
                newRecord.setRemindType("2");
                recordDO.setRemindType("1");
                sendHis.add(newRecord);
            }
            if(informStatus == 2){
                SafetyCarInFactoryDO car = carMap.get(recordDO.getCarNo());
                car.setInformStatus(informStatus);
                sendList.add(car);
                if(informType.contains("1") && StringUtils.isNotEmpty(phone)){
                    sendHis.add(recordDO);
                }
                if("2".equals(informType)){
                    sendHis.add(recordDO);
                }
            }
        }
        if(sendList.size()>0){
            safetyCarInFactoryService.modifyCarRetentionStatus(sendList);
        }
        if(sendHis.size()>0){
            safetyRetentionInformLogService.batchInsert(sendHis);
        }
    }

    /**
     * 白名单配置转换为厂区车辆检索条件
     *
     * @param whiteDO 入参
     * @return 返回值
     */
    private CarInFactoryListParam convertWhiteBean(SafetyRetentionWhiteDO whiteDO){
        CarInFactoryListParam curParam = new CarInFactoryListParam();
        curParam.setCarNo(whiteDO.getCarNo());
        curParam.setFactory(whiteDO.getFactory());
        curParam.setStaffNo(whiteDO.getStaffNo());
        curParam.setCarType(whiteDO.getCarType());

        return curParam;
    }

    /**
     * 推送消息格式化
     * @param carRecord 入参
     * @return 返回值
     */
    private String formatSendCotent(SafetyRetentionInformRecordDO carRecord){
        String ruleContent = carRecord.getInformContent();
        if(StringUtils.isEmpty(ruleContent)){
            ruleContent = "尊敬的车主您好！因您私家车（车牌号）在厂区即将滞留（通知次数）次，请尽快驶离厂区，将车位提供给有需要的员工。过程中如有疑问，请随时拨打我们的7*24小时服务热线0371-66718119。-司法保卫室。";
        }
        return ruleContent.replace("（车牌号）",carRecord.getCarNo()).replace("（通知次数）",carRecord.getRetentionNum()+"");
    }

    /**
     * 类型转换
     * @param car 滞留车辆
     * @param sendLevel 推送级别 1级 2级
     * @param sendType 推送方式
     * @return
     */
    private SafetyRetentionInformRecordDO convertInformBean(SafetyCarInFactoryDO car,int sendLevel,String sendType,String sendContent){
        SafetyRetentionInformRecordDO recordDO = new SafetyRetentionInformRecordDO();
        if(car != null){
            recordDO.setCarNo(car.getCarNo());
            recordDO.setCarType(car.getCarType());
            recordDO.setCarCategory(car.getCarCategory());
            recordDO.setFactory(car.getFactory());
            recordDO.setInTime(car.getInTime());
            recordDO.setStaffNo(car.getStaffNo());
            recordDO.setStaffName(car.getStaffName());
            recordDO.setNoticeId(sendLevel);
            recordDO.setRetentionNum(car.getRetentionNum());
            recordDO.setInformType(sendType);

            recordDO.setInformContent(sendContent);
        }
        return recordDO;
    }

    /**
     * 查询车辆号入参记录
     * @param plateNo 车牌号
     * @return 返回值
     */
    public Boolean getInvehicleInfo(String plateNo){
        if(StringUtils.isBlank(plateNo)){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"车牌号为空，无法查询信息！");
        }
        SafetyCarInFactoryVO vo = safetyCarInFactoryService.getInvehicleInfo(plateNo);
        if(ObjectUtils.isEmpty(vo)){
            return false;
        }
        return vo.getIsSucess();
    }
}
