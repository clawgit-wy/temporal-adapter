package com.yutong.safety.core.handler;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.*;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.app.BlackDepartAddParam;
import com.yutong.safety.common.param.app.BlackEipListParam;
import com.yutong.safety.common.param.web.BlackMessageAddParam;
import com.yutong.safety.common.param.web.BlackMessageListParam;
import com.yutong.safety.common.param.web.BlackMessageUpdateParam;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.utils.Base64ImageDecoder;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.SafetyBlankYxtMsgVO;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.common.vo.web.HkMiddleTableVO;
import com.yutong.safety.core.db.entity.SafetyBlackMessageDO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetyGroupInfoDO;
import com.yutong.safety.core.db.entity.SafetyVisitorHairpinDO;
import com.yutong.safety.core.db.entity.dict.SafetySystemParamDO;
import com.yutong.safety.core.db.entity.blackmessage.SafetyBlackMessageForEipParam;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterRegisterRt;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDetailDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessLogDO;
import com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryBlankProcessDetailQueryParams;
import com.yutong.safety.core.db.entity.system.param.BlackCheckParam;
import com.yutong.safety.core.db.mapper.enter.SafetyEnterRegisterMapper;
import com.yutong.safety.core.handler.hik.SafetyArtemisHandler;
import com.yutong.safety.core.service.*;
import com.yutong.safety.core.service.enter.SafetyEnterRegisterService;
import com.yutong.safety.core.service.hik.SafetyArtemisService;
import com.yutong.safety.core.service.dict.SafetySystemParamService;
import com.yutong.safety.core.service.entry.SafetyEntryBlankProcessDetailService;
import com.yutong.safety.core.service.entry.SafetyEntryBlankProcessLogService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  create gwcuiyx 2023.07.10
 *  黑名单信息管理--handler
 */
@Component
public class SafetyBlackMessageHandler {
    @Resource
    private SafetyBaseHandler safetyBaseHandler;
    @Autowired
    private SafetyBlackMessageService safetyBlackMessageService;
    @Autowired
    private SafetyBaseService safetyBaseService;
    @Autowired
    private SafetyEntryBlankProcessDetailService safetyEntryBlankProcessDetailService;
    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Resource
    private SafetySystemParamService safetySystemParamService;
    @Resource
    private SafetyArtemisHandler safetyArtemisHandler;
    @Resource
    public SafetyArtemisService safetyArtemisService;
    @Resource
    private SafetyEntryBlankProcessLogService safetyEntryBlankProcessLogService;
    @Resource
    public SafetyGroupService safetyGroupService;

    @Resource
    private SafetyEnterRegisterService safetyEnterRegisterService;

    @Resource
    private SafetyVisitorHairpinService safetyVisitorHairpinService;

    /**
     * 数据列表
     * @param param
     * @return
     */
    public Object pageList(BlackMessageListParam param) {
        String positionType = param.getPositionType();
        if (StringUtils.isNotEmpty(positionType)) {
            List<String> positionTypes = Arrays.asList(positionType.split(","));
            if (CollectionUtils.isNotEmpty(positionTypes) && positionTypes.stream().anyMatch(li -> li.equals("无"))) {
                param.setPositionTypes(positionTypes.stream().filter(li -> !li.equals("无")).collect(Collectors.toList()));
                param.setNullPositionType(true);
            } else {
                param.setPositionTypes(positionTypes);
            }
        }
        List<SafetyBlackMessageDO> list = safetyBlackMessageService.pageList(param);
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(li -> {
                if (StringUtils.isNotEmpty(li.getLifePhoto())) {
                    li.setPhoto(li.getLifePhoto());
                    li.setPhotoType("生活照");
                } else if (StringUtils.isNotEmpty(li.getIdPhoto())) {
                    li.setPhoto(li.getIdPhoto());
                    li.setPhotoType("证件照");
                }
            });
        }
        long total = safetyBlackMessageService.pageListCount(param);
        return new PageVO<SafetyBlackMessageDO>(total, list);
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    public Object delete(Integer id, String tocken) {
        return safetyBlackMessageService.delete(id, tocken);
    }

    /**
     * 数据更新操作
     * @param param
     * @return
     */
    public Object update(BlackMessageUpdateParam param, String tocken) {
        safetyBlackMessageService.update(param, tocken);
        return null;
    }

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    public Object add(BlackMessageAddParam param, String tocken) {
        safetyBlackMessageService.add(param, tocken);
        return null;
    }

    /**
     * 数据展示操作
     * @param id
     * @return
     */
    public Object view(Integer id) {
        return safetyBlackMessageService.view(id);
    }

    /**
     * 数据导出操作
     * @param param
     * @return
     */
    public HSSFWorkbook export(BlackMessageListParam param, String tocken) {
        String positionType = param.getPositionType();
        if (StringUtils.isNotEmpty(positionType)) {
            List<String> positionTypes = Arrays.asList(positionType.split(","));
            if (CollectionUtils.isNotEmpty(positionTypes) && positionTypes.stream().anyMatch(li -> li.equals("无"))) {
                param.setPositionTypes(positionTypes.stream().filter(li -> !li.equals("无")).collect(Collectors.toList()));
                param.setNullPositionType(true);
            } else {
                param.setPositionTypes(positionTypes);
            }
        }
        List<SafetyBlackMessageDO> list = safetyBlackMessageService.export(param, tocken);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"公司", "姓名", "卡号", "手机号", "照片类型", "照片", "身份证号","人员类型","车辆","状态","操作员","数据来源","更新时间","备注"};
        String[][] values = new String[list.size()][];
        SafetyBlackMessageDO blackMessageDO;
        for(int i=0; i<list.size(); i++) {
            blackMessageDO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = blackMessageDO.getCompanyName();//公司
            values[i][1] = blackMessageDO.getEmpName();//姓名
            values[i][2] = blackMessageDO.getCardNo();//卡号
            values[i][3] = blackMessageDO.getPhoneNo();//手机号
            String lifePhoto = blackMessageDO.getLifePhoto();
            String idPhoto = blackMessageDO.getIdPhoto();
            if (StringUtils.isNotEmpty(lifePhoto)) {
                values[i][4] = "生活照";//照片类型
                String s = lifePhoto.split("&apikey")[0];
                values[i][5] = s;//照片
            } else if (StringUtils.isNotEmpty(idPhoto)) {
                values[i][4] = "证件照";//照片类型
                String s = idPhoto.split("&apikey")[0];
                values[i][5] = s;//照片
            } else {
                values[i][4] = "";//照片类型
                values[i][5] = "";//照片
            }
            values[i][6] = blackMessageDO.getIdCardNo();//身份证号
            values[i][7] = blackMessageDO.getEmpType();//人员类型
            values[i][8] = blackMessageDO.getCarNo();//车辆
            values[i][9] = BlackStateEnum.values()[blackMessageDO.getState()-1].getName();
            values[i][10] = blackMessageDO.getOperator();//操作员
            values[i][11] = blackMessageDO.getSource();//数据来源
            values[i][12] = DateUtils.format(blackMessageDO.getUpdateTime(), 2);
            values[i][13] = blackMessageDO.getRemark();
        }
        String sheetName = "黑名单信息列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 数据导入操作
     * @param param
     * @return
     */
    public Object importBlackMessage(CommonImportParam param, String tocken) {
        return safetyBlackMessageService.importBlackMessage(param, tocken);
    }

    /**
     * 身份证黑名单检查
     * @param idCardNo
     * @return
     */
    public Object check(String idCardNo, String cardNo) {
        if (safetyBlackMessageService.check(idCardNo)) {
            return true;
        }
        if (StringUtils.isNotEmpty(cardNo)) {
            return safetyBlackMessageService.checkCardNo(cardNo);
        }
        return false;
    }

    /**
     * 黑名单检查(一级黑名单直接返回true,二级黑名单根据时间判断是否有黑名单备案记录)
     *  PHOTO_COMPARISON_TODO：访客照片对比校验开关
     * @param param
     * @return
     */
    public Object checkForEnter(BlackCheckParam param) {
        LogUtils.info("checkForEnter2:{}", param);
        String idCardNo = param.getIdCardNo();
        Boolean aBoolean = safetyArtemisHandler.checkOneToMany(param.getPhoto(), param.getName(), idCardNo, SafetyHikThresholdEnum.VISITOR);
        if (aBoolean) {
            SafetySystemParamDO systemParamDO = safetySystemParamService.getByParamCode("PHOTO_COMPARISON_TODO");
            if (systemParamDO != null && "1".equals(systemParamDO.getParamValue())) {
                return idCardNo + "照片校验失败";
            }
        }

        SafetyEmployeeMessageDO messageDO = safetyEmployeeMessageService.getByNo(param.getDockNo());
        List<String> idCardList = new ArrayList<>();
        idCardList.add(idCardNo);
        List<SafetyBlackMessageDO> blackList = safetyBlackMessageService.getBlackMessageList(idCardList);
        //一级黑名单时直接返回异常
        if (!CollectionUtils.isEmpty(blackList)) {
            if (Constants.BLANK_FIRST_SOURCE.equals(blackList.get(0).getSource())) {
                LogUtils.info("用户{}，一级黑名单，核验失败", idCardNo);
                sendYxtMsg(SafetyYxtMsgEnum.VISITOR_EHR_MSG, param, messageDO, null);
                return idCardNo + "核验失败";
            }
            if (checkEntry(param, idCardNo)) {
                sendYxtMsg(SafetyYxtMsgEnum.VISITOR_RESIGN_MSG, param, messageDO, null);
                return idCardNo + "校验失败";
            }
            Map<String, String> competitorMap = safetyBaseHandler.toCompetitorOfBlack(Collections.singletonList(idCardNo));
            Pair<Boolean, String> pair = safetyBaseHandler.checkCompetitor(competitorMap, idCardNo);
            if (Boolean.TRUE.equals(pair.getLeft())) {
                LogUtils.info("用户{}，非友商客户，验证失败", idCardNo);
                sendYxtMsg(SafetyYxtMsgEnum.VISITOR_COMPETITOR_MSG, param, messageDO, pair.getRight());
                return idCardNo + "验证失败";
            }
        } else {
            Map<String, String> competitorMap = safetyBaseHandler.toCompetitorOfBlack(Collections.singletonList(idCardNo));
            Pair<Boolean, String> pair = safetyBaseHandler.checkCompetitor(competitorMap, idCardNo);
            if (Boolean.TRUE.equals(pair.getLeft()) && checkEntry(param, idCardNo)) {
                LogUtils.info("用户{}，非友商客户，验证失败", idCardNo);
                sendYxtMsg(SafetyYxtMsgEnum.VISITOR_COMPETITOR_MSG, param, messageDO, pair.getRight());
                return idCardNo + "验证失败";
            }
        }

        //校验员工卡号
        if (StringUtils.isNotEmpty(param.getCardNo())) {
            return safetyBlackMessageService.checkCardNo(param.getCardNo()) ? "员工卡号校验失败" : "";
        }
        return "";
    }

    private void sendYxtMsg(SafetyYxtMsgEnum yxtMsgEnum, BlackCheckParam param, SafetyEmployeeMessageDO messageDO, String competitorInfo) {
        SafetyEntryBlankProcessLogDO log = disBlankProcessLog(param, messageDO);
        log.setSubmitOpenId(param.getOpenId());
        log.setRemark(yxtMsgEnum.getErrorMessage());
        safetyEntryBlankProcessLogService.save(log);

        String empName = "无";
        if (messageDO != null) {
            empName = messageDO.getEmpName();
        }

        SafetyBlankYxtMsgVO vo = new SafetyBlankYxtMsgVO(yxtMsgEnum,
                param.getDockNo(), empName,
                param.getName(), param.getIdCardNo(), param.getPhone(),
                competitorInfo);
        safetyBaseHandler.sendYxtMsg(vo);
    }

    /**
     * 黑名单日志数据整理
     */
    private SafetyEntryBlankProcessLogDO disBlankProcessLog(BlackCheckParam param, SafetyEmployeeMessageDO messageDO) {
        SafetyEntryBlankProcessLogDO log = new SafetyEntryBlankProcessLogDO();
        log.setSubmitName(param.getName());
        log.setSubmitType("访客");
        log.setSubmitPhone(param.getPhone());
        if (messageDO != null) {
            log.setButtJointCode(messageDO.getEmpNo());
            log.setButtJointName(messageDO.getEmpName());
            log.setButtJointPhone(messageDO.getPhoneNo());
        }
        log.setBlankCard(param.getIdCardNo());
        log.setBlankPhone(param.getPhone());
        log.setBlankName(param.getName());
        log.setRemark("N");
        log.setCreates(param.getName(), "访客");
        return log;
    }

    private boolean checkEntry(BlackCheckParam param, String idCardNo) {
        //判断二级黑名单是否有“黑名单入厂申请”记录
        SafetyEntryBlankProcessDetailQueryParams detailQueryParams = new SafetyEntryBlankProcessDetailQueryParams();
        detailQueryParams.setCardNo(idCardNo);
        detailQueryParams.setStatus(EipStateEnum.RELEASE.getCode());
        detailQueryParams.setApplyStatus(SafetyEntryConstants.ENTRY_BLACK_PROCESS_DETAIL_STATUS_PASS); //审批通过的
        detailQueryParams.setContainBeginTime(param.getEnterBeginTime());
        detailQueryParams.setContainEndTime(param.getEtnerEndTime());
        detailQueryParams.setButtJoint(param.getDockNo()); //增加对接人
        long count = safetyEntryBlankProcessDetailService.selectProcessAndDetailCount(detailQueryParams);
        //没有“黑名单入厂申请”的记录时，返回true
        return count <= 0;
    }

    public Object tasksMessage(Boolean isAll, String dateTime) {
        dateTime = DateUtils.format(LocalDateTime.now().toDate(), 1);
        safetyBlackMessageService.tasksMessage(isAll, dateTime);
        return null;
    }

    /**
     * 根据身份证号id更新状态数据
     * @param idCardId
     * @return
     */
    public Object updateStateByCardId(String idCardId) {
        safetyBlackMessageService.updateStateByCardId(idCardId);
        return null;
    }

    /**
     * eip离职人员数据添加接口
     * @param param 入参
     * @return
     */
    public Object addDepart(BlackEipListParam param) {
        List<BlackDepartAddParam> list = param.getDepartList();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        for (BlackDepartAddParam addParam : list) {
            safetyBlackMessageService.addDepart(addParam);
        }
        return null;
    }

    public Object test() {
        safetyBlackMessageService.test();
        return null;
    }

    public Object isBlackBatchQuery(String carNoList){
        return safetyBlackMessageService.isBlackBatchQuery(carNoList);
    }

    /**
     * EIP 回写特批授权截止日期， 并且变更状态为特批入场
     * @param param 入参
     * @return 返回成功或失败的结果
     */
    public VO queryAuthorizationDateEip(SafetyBlackMessageForEipParam param) {
        String empNo = param.getEmpNo();
        String authorizationDate = param.getAuthorizationDate();
        // 检查数据是否存在
        BlackMessageListParam listParam = new BlackMessageListParam();
        listParam.setCardNo(empNo);
        List<SafetyBlackMessageDO> list = safetyBlackMessageService.selectList(listParam);
        if (CollectionUtils.isEmpty(list)) {
            return VoUtil.fail("此员工号不存在黑名单信息");
        }
        // 变更时间和变更状态
        safetyBlackMessageService.updateInfo(empNo, authorizationDate);
        return VoUtil.success();
    }

    /**
     * 定时变更状态，根据特批授权截止日期，如果当前时间大于特批授权截止日期，并且状态为特批入厂的，则变更状态为禁止入厂
     * @return 返回成功或失败的结果
     */
    public VO updateBlackMessageStatus() {
        // 获取符合条件的数据 特批授权截止日期不为空，特批授权截止日期小于当前时间，状态为特批入厂，正常数据
        BlackMessageListParam listParam = new BlackMessageListParam();
        listParam.setState(2);
        listParam.setAuthorizationDateIsNotNull(true);
        listParam.setLessAuthorizationDate(DateUtils.format(new Date(), 1));
        List<SafetyBlackMessageDO> list = safetyBlackMessageService.selectList(listParam);
        if (CollectionUtils.isEmpty(list)) {
            return VoUtil.success();
        }

        // 修改状态
        List<Integer> ids = list.stream().map(SafetyBlackMessageDO::getId).collect(Collectors.toList());
        safetyBlackMessageService.updateInfo(ids);
        return VoUtil.success();
    }

    /**
     * 同步EHR黑名单数据
     * @param begTime 起始时间
     * @param endTime 截止时间
     * @return 返回同步结果
     */
    public Object syncEhrBlack(String begTime,String endTime){
        if(StringUtils.isEmpty(endTime)){
            endTime = DateUtils.format(new Date(),2);
        }
        if(StringUtils.isEmpty(begTime)){
            Date btime = DateUtils.addDays(DateUtils.parse(endTime,2),-1);
            begTime = DateUtils.format(btime,2);
        }
        //获取EHR黑名单
        List<SafetyBlackMessageDO> blackList = safetyBaseService.selectEhrBlank(begTime,endTime);
        //TODO 根据身份证号去中台匹配员工号
        safetyBaseService.formatBlackMessageFromMp(blackList);
        //区分新增和删除
        List<SafetyBlackMessageDO> modifyList = blackList.stream().filter(e->!e.getDeleteFalg()).collect(Collectors.toList());
        //1.批量更新
        if(!CollectionUtils.isEmpty(modifyList)){
            safetyBlackMessageService.batchUpdateForEhr(modifyList);
        }
        List<SafetyBlackMessageDO> removeList = blackList.stream().filter(e->e.getDeleteFalg()).collect(Collectors.toList());
        //批量删除
        if(!CollectionUtils.isEmpty(removeList)){
            safetyBlackMessageService.batchDelForEhr(removeList);
        }
        return null;
    }



    /**
     * 用工类型下拉框
     * @return 返回下拉框的值
     */
    public VO querySelect() {
        List<String> originalList = safetyBlackMessageService.querySelect();
        List<String> result = originalList.stream()
                .map(li -> StringUtils.isEmpty(li) ? "无" : li)
                .collect(Collectors.toList());
        return VoUtil.successData(result);
    }

    /**
     * 增量同步
     */
    public VO syncToHkAdd() {
        Date start = DateUtils.parse(DateUtils.reduceMinute(new Date(), -123, 2), 2);
        Date end = new Date();

        syncBlackMessage(start, end);
        syncEmployeeMessage(start, end);
        syncOutsider(start, end);
        return VoUtil.success();
    }

    private void syncBlackMessage(Date start, Date end) {
        try {
            // 黑名单
            List<SafetyBlackMessageDO> list = safetyBlackMessageService.selectUpdateList(start, end);
            LogUtils.info("syncToHkAdd-黑名单增量数据{}", list.size());
            if (CollectionUtils.isNotEmpty(list)) {
                // 同步海康
                safetyBlackMessageService.syncHikMiddleTable(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.info("syncToHkAdd-黑名单同步失败");
        }
    }

    private void syncEmployeeMessage(Date start, Date end) {
        try {
            // 员工台账
            List<SafetyEmployeeMessageDO> employeeMessageDOList = safetyEmployeeMessageService.selectUpdateList(start, end);
            LogUtils.info("syncToHkAdd-员工台账增量数据{}", employeeMessageDOList.size());
            if (CollectionUtils.isNotEmpty(employeeMessageDOList)) {
                // 同步海康
                safetyBlackMessageService.syncHikMiddleTableInfo(employeeMessageDOList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.info("syncToHkAdd-员工台账同步失败");
        }
    }

    private void syncOrg(Date start, Date end) {
        try {
            // 组织
            List<SafetyGroupInfoDO> groupList = safetyGroupService.selectUpdateList(start, end);
            LogUtils.info("syncToHkAdd-组织架构增量数据{}", groupList.size());
            if (CollectionUtils.isNotEmpty(groupList)) {
                List<HkMiddleTableVO> orgList = groupList.stream().filter(li -> StringUtils.isNotEmpty(li.getParentCode()))
                        .map(ln -> {
                            HkMiddleTableVO info = new HkMiddleTableVO();
                            info.setOrgId(ln.getCode());
                            info.setOrgName(ln.getName());
                            info.setParentOrgId(ln.getParentCode());
                            if ("50004002".equals(ln.getParentCode())) {
                                // 海康的一级组织编码
                                info.setParentOrgId("root000000");
                            }
                            if ("N".equals(ln.getStatus())) {
                                info.setStatus(-1);
                            }
                            return info;
                        }).collect(Collectors.toList());
                safetyArtemisService.syncHikMiddleTable(orgList, HikMiddleTableSqlEnum.TB_MIDDLE_ORG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.info("syncToHkAdd-组织同步失败");
        }
    }

    private void syncOutsider(Date start, Date end) {
        // 已登记人员信息
        List<SafetyEnterRegisterRt> list = safetyEnterRegisterService.selectList(start, end);
        LogUtils.info("syncToHkAdd-外来人员备案增量数据{}", list.size());
        if (CollectionUtils.isNotEmpty(list)) {
            // 同步海康
            safetyEnterRegisterService.syncHikMiddleTableOutsider(list);
        }

        List<SafetyVisitorHairpinDO> hairpinList = safetyVisitorHairpinService.selectListByUpdateTime(start, end);
        LogUtils.info("syncToHkAdd-访客增量数据{}", list.size());
        if (CollectionUtils.isNotEmpty(hairpinList)) {
            // 同步海康
            safetyVisitorHairpinService.syncHikMiddleTableVisitor(hairpinList);
        }
    }
}
