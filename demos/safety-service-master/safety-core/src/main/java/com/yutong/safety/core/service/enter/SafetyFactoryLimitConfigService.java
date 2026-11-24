package com.yutong.safety.core.service.enter;

import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.enter.EnterApproveCrk;
import com.yutong.safety.core.db.entity.enter.SafetyEnterDockCrk;
import com.yutong.safety.core.db.entity.enter.SafetyFactoryLimitConfigEntity;
import com.yutong.safety.core.db.mapper.SafetyGeneralConfigDOMapper;
import com.yutong.safety.core.db.mapper.enter.IncomingMapper;
import com.yutong.safety.core.db.mapper.enter.SafetyFactoryLimitConfigMapper;
import com.yutong.safety.core.handler.enter.IncomingHandler;
import com.yutong.safety.core.handler.enter.SafetyFactoryLimitConfigHandler;
import com.yutong.safety.core.service.SafetyGeneralConfigService;
import com.yutong.safety.core.service.SafetyLogMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author caomg
 * @date 2024-9-10 11:09
 * @description SafetyFactoryLimitConfigService
 */
@Service
public class SafetyFactoryLimitConfigService implements SafetyFactoryLimitConfigHandler {
    @Autowired
    SafetyFactoryLimitConfigMapper safetyFactoryLimitConfigMapper;

    @Autowired
    IncomingMapper incomingMapper;

    @Autowired
    SafetyEnterFilingService safetyEnterFilingService;

    @Autowired
    private IncomingHandler incomingHandler;

    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    @Autowired
    SafetyGeneralConfigService safetyGeneralConfigService;

    @Autowired
    SafetyGeneralConfigDOMapper safetyGeneralConfigDOMapper;

    @Override
    public void saveOrUpdate(SafetyFactoryLimitConfigEntity en,String token) {
        Integer id = en.getId();
        if (id == null) {
            safetyFactoryLimitConfigMapper.save(en);
        }else{
            safetyFactoryLimitConfigMapper.update(en);
        }

        cancelAuthority(token);
    }

    @Override
    public void updateByTime() {
        cancelAuthority("");
    }

    @Override
    public SafetyFactoryLimitConfigEntity getData() {
        return safetyFactoryLimitConfigMapper.getData();
    }

    //取消入门权限
    public Object cancelAuthority(String token){
        SafetyFactoryLimitConfigEntity data = safetyFactoryLimitConfigMapper.getData();
        if(ObjectUtils.isEmpty(data)){
            jlrz(token,null);
            return null;
        }

        //获取限制时间
        Integer limitNumber = data.getLimitNumber();
        LogUtils.info("cancelAuthority[limitNumber]:"+limitNumber);

        //查询limitNumber天前审批通过的车辆信息
        List<Map<String,Object>> list = safetyFactoryLimitConfigMapper.getLastDaysData(limitNumber);
        if(CollectionUtils.isEmpty(list)){
            jlrz(token,null);
            return null;
        }
        LogUtils.info("cancelAuthority[list]:"+list);

        //查询limitNumber天内进过厂的数据
        List<String> carNoListInLimitNumber = safetyFactoryLimitConfigMapper.getCarNoListInLimitNumber(limitNumber);
        LogUtils.info("cancelAuthority[carNoListInLimitNumber]:"+carNoListInLimitNumber);

        List<String> cancelCarList = new ArrayList<>();
        list.stream().forEach(m -> {
            String carNo = m.get("fdCarNo").toString();
            String flowId = m.get("flowId").toString();
            if(CollectionUtils.isEmpty(carNoListInLimitNumber) || !carNoListInLimitNumber.contains(carNo)){
                //记录未入厂的流程id
                cancelCarList.add(flowId);
            }
        });

        if(CollectionUtils.isEmpty(cancelCarList)){
            jlrz(token,null);
            return null;
        }

        //更新权限截止时间为昨天
        safetyFactoryLimitConfigMapper.cancelCarAccess(cancelCarList);
        LogUtils.info("cancelAuthority[cancelCarList]:"+cancelCarList);

        //重新下发
        List<SafetyEnterDockCrk> safetyEnterDockCrks = incomingMapper.selectSafetyEnterDockCrkNewByList(cancelCarList);

        if(CollectionUtils.isEmpty(safetyEnterDockCrks)){
            jlrz(token,null);
            return null;
        }
        LogUtils.info("cancelAuthority[safetyEnterDockCrks]:"+safetyEnterDockCrks);

        //获取短信内容
        //String carNote = safetyGeneralConfigDOMapper.getMes("carNote");
        String carNote = "尊敬的客户： " +
                "您好！您所备案的物流车辆（{carNo}）在备案日期（{beginDate}-{endDate}）已连续{days}天未进入任何备案厂区（{factory}）。依据相关规定，我们已经关闭您与备案车辆的入厂权限。" +
                "如您仍有入厂需求，请重新提交备案申请，如有任何疑问，欢迎随时与我们联系（0371-6671 8119）。";

        StringBuilder sb = new StringBuilder();
        safetyEnterDockCrks.stream().forEach(dockCrk -> {
            if(dockCrk != null){
                dockCrk.setApproveList(null);
                List<EnterApproveCrk> enterApproveCrks = incomingMapper.selectEnterApproveList11(dockCrk.getFdKey());
                dockCrk.setApproveList(enterApproveCrks);

                safetyEnterFilingService.insertCrkNew(dockCrk, true, false);
                try{
                    List<SafetyEnterDockCrk> curCrkList = new ArrayList<>();
                    curCrkList.add(dockCrk);
                    incomingHandler.syncSap(curCrkList);
                }catch (Exception e){
                    LogUtils.error("下发SAP异常",e);
                }
            }

            //机器人通知
            String msg = "【物流车入厂备案】"+dockCrk.getFdCarNo()+"司机"+dockCrk.getEnterDrvierName()+dockCrk.getEnterDrvierPhone()+"长期未入厂，已自动取消门禁权限，备案详情：";
            safetyEnterFilingService.sendYxtMsg(dockCrk,msg,dockCrk.getFdDockNo());

            //拼接日志记录
            sb.append("车牌号：").append(dockCrk.getFdCarNo())
                    .append(";司机姓名：").append(dockCrk.getEnterDrvierName())
                    .append(";联系方式：").append(dockCrk.getEnterDrvierPhone())
                    .append(";厂区：").append(dockCrk.getFactory())
                    .append(";备案日期：").append(dockCrk.getFilingBeginDate()).append(" - ").append(dockCrk.getFilingEndDate())
                    .append("。");

            //发送短信
            if (!StringUtils.isEmpty(carNote)) {
                String beginDate = StringUtils.isEmpty(dockCrk.getFilingBeginDate()) ? "" : DateUtils.parse(dockCrk.getFilingBeginDate(), 1).toString();
                String endDate = StringUtils.isEmpty(dockCrk.getFilingEndDate()) ? "" : DateUtils.parse(dockCrk.getFilingEndDate(), 1).toString();

                carNote.replace("{craNo}", dockCrk.getFdCarNo())
                        .replace("{beginDate}", beginDate)
                        .replace("{endDate}", endDate)
                        .replace("{days}", limitNumber + "")
                        .replace("{factory}", dockCrk.getFactory());

                LogUtils.info("sendMes[before]:" + carNote);
                String s = safetyGeneralConfigService.sendMsgSms(dockCrk.getEnterDrvierPhone(), carNote);
                LogUtils.info("sendMes[after]:" + s);
            }
        });

        jlrz(token,sb);

        return null;
    }

    public void jlrz(String token, StringBuilder mes){
        //操作日志记录
        LogUtils.info("cancelAuthority[sb]:"+mes);
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("自动取消时长设置");
        log.setAbility("添加");

        if ("".equals(token)) {
            log.setOperator("定时任务");
        } else {
            String realName = TokenUtils.getRealName(token);
            log.setOperator(realName);
        }
        log.setMessage(org.apache.commons.lang3.StringUtils.isBlank(mes) ? "无司机取消权限" : mes.toString());
        LogUtils.info("cancelAuthority[log]:"+log);
        safetyLogMessageService.addLogMessage(log);
    }

}
