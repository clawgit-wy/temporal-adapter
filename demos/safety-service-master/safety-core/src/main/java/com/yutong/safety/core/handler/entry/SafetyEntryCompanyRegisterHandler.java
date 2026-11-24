package com.yutong.safety.core.handler.entry;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterAuthRt;
import com.yutong.safety.core.db.entity.entry.SafetyEntryCompanyRegisterDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryCompanyRegisterDOExample;
import com.yutong.safety.core.db.entity.entry.SafetyEntryCompanyRegisterHistoryDO;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryCompanyRegisterAddParam;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryCompanyRegisterQueryParams;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryLoginParams;
import com.yutong.safety.core.db.entity.entry.vo.EntryOverdueTaskVO;
import com.yutong.safety.core.db.mapper.enter.SafetyEnterAuthMapper;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.service.*;
import com.yutong.safety.core.service.entry.SafetyEntryCompanyRegisterHistoryService;
import com.yutong.safety.core.service.entry.SafetyEntryCompanyRegisterService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.yutong.safety.common.constants.Constants.*;
import static com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants.*;

@Component
public class SafetyEntryCompanyRegisterHandler extends AbstractSafetyWebBaseHandler<SafetyEntryCompanyRegisterDO, SafetyEntryCompanyRegisterDOExample, SafetyEntryCompanyRegisterQueryParams, SafetyEntryCompanyRegisterAddParam> {
    @Value("${iam.retention.robotId}")
    private String incomintRebotId;

    @Value("${an.eip.todo.backUrl}")
    private String anEipTodoUrl;

    @Resource
    private MailCenterUtils mailCenterUtils;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private YxtRebotUtils yxtRebotUtils;

    @Resource
    private SafetySmsHandler safetySmsHandler;

    @Resource
    private SafetyBaseHandler safetyBaseHandler;

    @Resource
    private SafetyEnterAuthMapper enterAuthMapper;

    @Resource
    private SafetyGeneralConfigService safetyGeneralConfigService;

    @Resource
    private SafetyLogMessageService safetyLogMessageService;

    @Resource
    private SafetyFieldManageService safetyFieldManageService;

    @Resource
    private SafetyDictItemService safetyDictItemService;

    @Resource
    private SafetyEntryExternalNetworkHandler safetyEntryExternalNetworkHandler;

    @Resource
    private SafetyEntryCompanyRegisterHistoryService safetyEntryCompanyRegisterHistoryService;

    @Resource
    private SafetyEntryCompanyRegisterService safetyEntryCompanyRegisterService;

    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;

    @Override
    public AbstractSafetyWebBaseService<SafetyEntryCompanyRegisterDO, SafetyEntryCompanyRegisterDOExample, SafetyEntryCompanyRegisterQueryParams> getService() {
        return safetyEntryCompanyRegisterService;
    }

    @Override
    public String getModuleName() {
        return "外部备案权限";
    }

    @Override
    protected String getTemplateName() {
        return "";
    }

    @Override
    protected String getRoleName() {
        return "超级管理员";
    }

    @Override
    public SafetyLogMessageService getLogService() {
        return safetyLogMessageService;
    }

    @Override
    public Pair<String[], String[][]> processExportData(List<SafetyEntryCompanyRegisterDO> dataList) {
        String[] title = {"姓名", "手机号", "所属公司", "对接人姓名", "对接人手机号", "申请时间", "事由", "状态"};
        // 使用流操作简化填充数据
        String[][] values = dataList.stream()
                .map(info -> new String[]{
                        info.getFdName(),
                        info.getFdPhone(),
                        info.getFdCompany(),
                        info.getButtJointName(),
                        info.getButtJointPhone(),
                        DateUtils.format(info.getCreateTime(), 2),
                        info.getReason(),
                        StringUtils.isNotBlank(COMPANY_REGISTER_STATUS.get(info.getStatus())) ? COMPANY_REGISTER_STATUS.get(info.getStatus()) : "未知状态"
                })
                .toArray(String[][]::new);
        return Pair.of(title, values);
    }

    /**
     * 删除方法重写
     *
     * @param id    数据行ID
     * @param token 用户信息
     */
    @Override
    public VO delete(Integer id, String reason, String token) {
        SafetyEntryCompanyRegisterDO data = service().selectByPrimaryKey(id);
        if (data == null) {
            return fail("该记录不存在");
        }
        int count = safetyEntryCompanyRegisterService.deleteById(id);
        if (count < 0) {
            return fail("数据删除失败!");
        }
        // 删除的数据插入历史表
        SafetyEntryCompanyRegisterHistoryDO saveInfo = new SafetyEntryCompanyRegisterHistoryDO();
        BeanUtils.copyProperties(data, saveInfo);
        safetyEntryCompanyRegisterHistoryService.save(saveInfo, token);
        // 日志
        this.saveLogInfo("删除", data.toString(), token);
        return success();
    }

    /**
     * 新增后的操作，
     * 修改对应的操作为通过或者驳回
     * 新增对应的是注册
     *
     * @param data 新增的实体类，修改时没有创建时间等信息
     */
    @Override
    protected void afterSave(SafetyEntryCompanyRegisterDO data) {
        if (data.saveOrUpdate()) {
            // 新增
            String msg = "您好，您有一条备案平台注册信息待审核，链接：供应商注册信息列表页地址:" +  anEipTodoUrl + "externalPermissionRegister";
            yxtRebotUtils.postForEntity(null, incomintRebotId, data.getButtJointCode(), msg, new RestTemplate());
        } else {
            // 修改
            String msg = data.getStatus().equals(COMPANY_REGISTER_STATUS_PASS) ? COMPANY_REGISTER_SMS_OK_CONTENT : COMPANY_REGISTER_SMS_FAIL_CONTENT;
            String result = safetyGeneralConfigService.sendMsgSms(data.getFdPhone(), msg);
            LogUtils.info("通过或驳回消息推送状态：result" + result);
        }
    }

    public String login(SafetyEntryLoginParams params) {
        String phone = params.getPhone();
        String code = params.getCode();
        // 验证码校验
        safetySmsHandler.verifyCode(phone, code, SMS_CODE_TYPE_LOGIN);

        // 逻辑校验
        // 1.注册的信息
        SafetyEntryCompanyRegisterDO info = safetyEntryCompanyRegisterService.selectInfo(phone);
        if (info == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "信息未注册");
        }
        if (!COMPANY_REGISTER_STATUS_PASS.equals(info.getStatus())) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "注册申请未通过");
        }
        // 2.备案权限管理
        List<SafetyEnterAuthRt> authList = enterAuthMapper.selectEnterAuthByEmpNo(info.getButtJointCode());
        if (CollectionUtils.isEmpty(authList)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "对接人权限变更，请重新注册");
        }

        // 3.对接人判断
        SafetyEmployeeMessageDO employeeMessageInfo = safetyEmployeeMessageService.selectInfoByEmpNo(info.getButtJointCode());
        if (employeeMessageInfo == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "对接人不存在");
        }
        if (DISABLED.equals(info.getStatus())) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "对接人权限变更，请重新注册");
        }

        // 存储
        String uuid = UUID.randomUUID().toString();
        String token = TokenUtils.generateNetworkToken(phone, info.getFdName(), info.getButtJointCode(), uuid);
        redisUtil.set(REDIS_ENTRY_TOKEN_PRE + uuid, phone, REDIS_ENTRY_TOKEN_EXPIR_TIME);
        return token;
    }

    /**
     * 注册
     *
     * @param param 入参
     */
    public Object register(SafetyEntryCompanyRegisterAddParam param) {
        // 1.验证码校验
        safetySmsHandler.verifyCode(param.getFdPhone(), param.getCode(), SMS_CODE_TYPE_REGION);

        // 2.手机号是否已经注册
        SafetyEntryCompanyRegisterDO info = safetyEntryCompanyRegisterService.selectInfo(param.getFdPhone());
        if (info != null && !COMPANY_REGISTER_STATUS_REJECT.equals(info.getStatus())) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "信息已注册，请直接登录");
        }

        // 3.对接人姓名和手机号校验
        SafetyEnterAuthRt authRt = enterAuthMapper.selectEnterAuthByEmpNameAndPhone(param.getButtJointName(), param.getButtJointPhone());
        if (authRt == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "对接人信息错误");
        }

        // 4.备案人公司信息
        String supplyCompanyName = safetyBaseHandler.getSupplyCompanyName(param.getFdCompanyCode());
        if (StringUtils.isBlank(supplyCompanyName)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "公司代码错误");
        }

        // 5.数据整理
        SafetyEntryCompanyRegisterDO data = new SafetyEntryCompanyRegisterDO();
        BeanUtils.copyProperties(param, data);
        data.setButtJointCode(authRt.getFdEmpNo());
        data.setFdCompany(supplyCompanyName);
        data.setStatus(COMPANY_REGISTER_STATUS_WAIT);
        data.setCreatesAndUpdates(param.getFdName());

        // 数据入库
        safetyEntryCompanyRegisterService.save(data);
        data.setId(null);
        this.afterSave(data);
        return null;
    }

    public Object logout(String token) {
        String redisKey = TokenUtils.getString(token, "redisKey");
        String cacheKey = REDIS_ENTRY_TOKEN_PRE + redisKey;
        redisUtil.del(cacheKey);
        return null;
    }

    public SafetyEntryCompanyRegisterDO getSupplyInfo(String phone) {
        return safetyEntryCompanyRegisterService.selectInfo(phone);
    }

    /**
     * 外来备案人员逾期提醒， 提醒的内容是当天存在即将过期的备案人员， 提醒人为对接人
     *
     * @return 返回结果
     */
    public VO entryOverdueTask() {
        // 获取符合提醒的数据,对接人中 1.备案过期提醒字段为是的，2.配置类型为人员的， 备案人员信息中：1.状态为审批通过的， 2.准入时间-截止时间为今天的
        List<EntryOverdueTaskVO> taskVOS = safetyEntryCompanyRegisterService.selectEntryOverdueTask();
        if (CollectionUtils.isEmpty(taskVOS)) {
            return success();
        }
        // 备案信息
        Map<String, String> recordDataMap = safetyDictItemService.selectListToMap(ENTRY_DICT_SUPPER_CODE_TYPE, ENTRY_DICT_SHOW_FLAG_VALUE);
        // 厂区信息
        Map<Integer, String> factoryDataMap = safetyFieldManageService.getFactoryNameList();
        // 数据整理
        taskVOS.forEach(li -> {
            li.setRecordType(safetyEntryExternalNetworkHandler.getRecordType(recordDataMap, li.getRecordTypeCode()));
            li.setEnterFactory(safetyEntryExternalNetworkHandler.getFilingPark(factoryDataMap, li.getEnterFactoryCode()));
        });
        Map<String, List<EntryOverdueTaskVO>> dataMap = taskVOS.stream().collect(Collectors.groupingBy(li -> li.getButtJointCode() + "," + li.getEmail()));

        // 邮件提醒
        dataMap.keySet().forEach(li -> {
            List<EntryOverdueTaskVO> entryOverdueTaskVOS = dataMap.get(li);
            mailCenterUtils.sendMailNew("安保系统备案人授权过期提醒", sendMailMsg(entryOverdueTaskVOS), li.split(",")[1], "治安管理邮箱", "zagl@yutong.com", null);
        });

        return success();
    }

    public String sendMailMsg(List<EntryOverdueTaskVO> list) {
        String firstCell = "<tr><td height=\"18\" width=\"141\" style=\"color:black;font-size:11pt;font-weight:400;font-style:normal;text-decoration:none;font-family:宋体;text-align:center;vertical-align:middle;border-right:0.5pt solid black;border-bottom:0.5pt solid black;border-left:0.5pt solid black;height:13.5pt;border-top:0.5pt dotted #AAA;width:106pt;\">";
        String otherCell = "</td><td width=\"141\" style=\"color:black;font-size:11pt;font-weight:400;font-style:normal;text-decoration:none;font-family:宋体;text-align:center;vertical-align:middle;border-right:0.5pt solid black;border-bottom:0.5pt solid black;border-top:0.5pt dotted #AAA;border-left:0.5pt dotted #AAA;width:106pt;\">";
        String endCell = "</td></tr>";
        StringBuilder sb = new StringBuilder();
        sb.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"1044\" style=\"border-collapse:collapse;\" bordercolor=\"#000\" height=\"58\">\n" +
                "    <tbody>\n" +
                "    <tr>\n" +
                "        <td height=\"18\" width=\"141\" style=\"font-size:11pt;font-weight:400;font-style:normal;text-decoration:none;font-family:宋体;color:#17181A;text-align:center;vertical-align:middle;border:0.5pt solid black;background:#BFBFBF;height:13.5pt;width:106pt;\"><span style=\"white-space:pre-wrap\">序号</span></td>\n" +
                "        <td width=\"141\" style=\"font-size:11pt;font-weight:400;font-style:normal;text-decoration:none;font-family:宋体;color:#17181A;text-align:center;vertical-align:middle;border-top:0.5pt solid black;border-right:0.5pt solid black;border-bottom:0.5pt solid black;background:#BFBFBF;border-left:0.5pt dotted #AAA;width:106pt;\"><span style=\"white-space:pre-wrap\">所属公司</span></td>\n" +
                "        <td width=\"141\" style=\"font-size:11pt;font-weight:400;font-style:normal;text-decoration:none;font-family:宋体;color:#17181A;text-align:center;vertical-align:middle;border-top:0.5pt solid black;border-right:0.5pt solid black;border-bottom:0.5pt solid black;background:#BFBFBF;border-left:0.5pt dotted #AAA;width:106pt;\"><span style=\"white-space:pre-wrap\">姓名</span></td>\n" +
                "        <td width=\"141\" style=\"font-size:11pt;font-weight:400;font-style:normal;text-decoration:none;font-family:宋体;color:#17181A;text-align:center;vertical-align:middle;border-top:0.5pt solid black;border-right:0.5pt solid black;border-bottom:0.5pt solid black;background:#BFBFBF;border-left:0.5pt dotted #AAA;width:106pt;\"><span style=\"white-space:pre-wrap\">手机号</span></td>\n" +
                "        <td width=\"141\" style=\"font-size:11pt;font-weight:400;font-style:normal;text-decoration:none;font-family:宋体;color:#17181A;text-align:center;vertical-align:middle;border-top:0.5pt solid black;border-right:0.5pt solid black;border-bottom:0.5pt solid black;background:#BFBFBF;border-left:0.5pt dotted #AAA;width:106pt;\"><span style=\"white-space:pre-wrap\">备案类型</span></td>\n" +
                "        <td width=\"141\" style=\"font-size:11pt;font-weight:400;font-style:normal;text-decoration:none;font-family:宋体;color:#17181A;text-align:center;vertical-align:middle;border-top:0.5pt solid black;border-right:0.5pt solid black;border-bottom:0.5pt solid black;background:#BFBFBF;border-left:0.5pt dotted #AAA;width:106pt;\"><span style=\"white-space:pre-wrap\">厂区</span></td>\n" +
                "        <td width=\"141\" style=\"font-size:11pt;font-weight:400;font-style:normal;text-decoration:none;font-family:宋体;color:#17181A;text-align:center;vertical-align:middle;border-top:0.5pt solid black;border-right:0.5pt solid black;border-bottom:0.5pt solid black;background:#BFBFBF;border-left:0.5pt dotted #AAA;width:106pt;\"><span style=\"white-space:pre-wrap\">授权开始时间</span></td>\n" +
                "        <td width=\"141\" style=\"font-size:11pt;font-weight:400;font-style:normal;text-decoration:none;font-family:宋体;color:#17181A;text-align:center;vertical-align:middle;border-top:0.5pt solid black;border-right:0.5pt solid black;border-bottom:0.5pt solid black;background:#BFBFBF;border-left:0.5pt dotted #AAA;width:106pt;\"><span style=\"white-space:pre-wrap\">授权结束时间</span></td>\n" +
                "    </tr>");

        int num = 1;
        for (EntryOverdueTaskVO dto : list) {
            sb.append(firstCell).append(num).append(otherCell)
                    .append(dto.getCompanyName()).append(otherCell)
                    .append(dto.getName()).append(otherCell)
                    .append(dto.getPhone()).append(otherCell)
                    .append(dto.getRecordType()).append(otherCell)
                    .append(dto.getEnterFactory()).append(otherCell)
                    .append(dto.getEnterStartTime()).append(otherCell)
                    .append(dto.getEnterEndTime()).append(endCell);
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }
}
