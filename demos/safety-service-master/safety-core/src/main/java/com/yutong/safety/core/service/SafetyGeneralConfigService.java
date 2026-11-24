package com.yutong.safety.core.service;

import com.google.common.collect.Maps;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.web.GeneralConfigParam;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.core.db.entity.SafetyGeneralConfigDO;
import com.yutong.safety.core.db.entity.SafetyGeneralConfigDOExample;
import com.yutong.safety.core.db.mapper.SafetyGeneralConfigDOMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author gwcuiyx
 * @date 2024-2-27 15:13
 */
@Service
public class SafetyGeneralConfigService {

    @Autowired
    private SafetyGeneralConfigDOMapper safetyGeneralConfigDOMapper;

    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    @Value("${iam.sendMsgSms.url}")
    private String sendMsgSmsUrl;

    @Value("${iam.sendMsgSms.smsConfigId}")
    private String smsConfigId;

    @Value("${im.sendMsg.url}")
    private String sendMsgImUrl;

    @Autowired
    private WebserviceUtil webserviceUtil;

    /**
     * 根据类型获取详细数据
     * @param type
     * @return
     */
    public SafetyGeneralConfigDO getConfigByType(String type) {
        SafetyGeneralConfigDOExample example = new SafetyGeneralConfigDOExample();
        example.createCriteria().andGeneralTypeEqualTo(type).andGeneralShowEqualTo(0);
        List<SafetyGeneralConfigDO> list = safetyGeneralConfigDOMapper.selectByExample(example);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    /**
     * 数据新增或者更新操作
     * @param param
     * @param token
     */
    public void saveOrUpdate(GeneralConfigParam param, String token) {
        boolean flag = true;
        SafetyGeneralConfigDO safetyGeneralConfigDO = new SafetyGeneralConfigDO();
        if(Objects.nonNull(param.getId())) {
            safetyGeneralConfigDO = safetyGeneralConfigDOMapper.selectByPrimaryKey(param.getId());
            if (safetyGeneralConfigDO != null) {
                flag = false;
            }
        }
        safetyGeneralConfigDO.setContent(param.getContent());
        safetyGeneralConfigDO.setGeneralShow(param.getGeneralShow());
        safetyGeneralConfigDO.setGeneralType(param.getGeneralType());
        safetyGeneralConfigDO.setUpdateTime(new Date());
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("通用数据配置");
        if (flag) {
            log.setAbility("添加");
            PreconditonsEx.checkArgument(safetyGeneralConfigDOMapper.insert(safetyGeneralConfigDO) == 1, ResponseEnum.FAIL);
        } else {
            log.setAbility("修改");
            PreconditonsEx.checkArgument(safetyGeneralConfigDOMapper.updateByPrimaryKey(safetyGeneralConfigDO) == 1, ResponseEnum.FAIL);

            //当修改小程序的弹框内容时，所有人的弹框配置都改为弹出
            if("smallPro".equals(param.getGeneralType())){
                safetyGeneralConfigDOMapper.updateDialogConfig();
            }
        }
        log.setMessage(param.getContent());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 数据列表查询
     * @return
     */
    public List<SafetyGeneralConfigDO> list() {
        SafetyGeneralConfigDOExample example = new SafetyGeneralConfigDOExample();
        example.createCriteria().andGeneralTypeNotEqualTo("switch");
        return safetyGeneralConfigDOMapper.selectByExample(example);
    }

    /**
     * 调用中台发送短信提醒功能
     * @param phones
     */
    public String sendMsgSms(String phones, String msg) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("smsConfigId", smsConfigId);
        param.put("smsType", "1"); //0-短信验证码 1-普通短信
        param.put("phoneNos", phones);//多个时用英文逗号分隔
        param.put("smsContent", msg);
        String jsonParams = JsonUtils.toString(param);
        Map<String, String> headerMap = Maps.newHashMap();
        headerMap.put("Content-Type","application/json");
        LogUtils.info("短信bodyParam：{}", jsonParams.toString());
        String ret = webserviceUtil.postJsonStr(sendMsgSmsUrl, jsonParams, headerMap);
        LogUtils.info("发送短信返回值：{}", ret);
        return ret;
    }

    /**
     * 发送宇信通消息提醒
     * @param empNo
     * @param robotId
     * @param msg
     */
    public String sendMsgIm(String empNo, String robotId, String msg) {
        String sendMsg = String.format(sendMsgImUrl, msg, robotId, empNo);
        LogUtils.info("发送宇信通信息：{}", sendMsg);
        String ret = webserviceUtil.sendHttpForGet(sendMsg);
        LogUtils.info("宇信通返回值：{}", ret);
        return ret;
    }
}
