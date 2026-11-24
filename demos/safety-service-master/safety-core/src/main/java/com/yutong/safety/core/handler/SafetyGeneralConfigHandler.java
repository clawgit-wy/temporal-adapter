package com.yutong.safety.core.handler;

import com.google.common.collect.Maps;
import com.yutong.safety.common.param.app.GeneralConfigAppParam;
import com.yutong.safety.common.param.web.GeneralConfigParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.SafetyGeneralConfigDO;
import com.yutong.safety.core.db.entity.SafetyVisitorHairpinDO;
import com.yutong.safety.core.db.mapper.SafetyVisitorSecretDOBizMapper;
import com.yutong.safety.core.service.SafetyFieldManageService;
import com.yutong.safety.core.service.SafetyGeneralConfigService;
import com.yutong.safety.core.service.SafetyVisitorHairpinService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author gwcuiyx
 * @date 2024-2-27 15:12
 */
@Component
public class SafetyGeneralConfigHandler {

    @Autowired
    private SafetyGeneralConfigService safetyGeneralConfigService;

    @Autowired
    private SafetyVisitorHairpinService safetyVisitorHairpinService;
    @Value("${fkfk.note}")
    private String noteUrl;

    @Autowired
    SafetyVisitorSecretDOBizMapper safetyVisitorSecretDOBizMapper;
    @Autowired
    SafetyFieldManageService safetyFieldManageService;

    private static Map<String, String> DATA_MAP_TYPE = Stream.of(
            new AbstractMap.SimpleEntry<>("note", "短信提示"),
            new AbstractMap.SimpleEntry<>("shake", "宇信通提示"),
            new AbstractMap.SimpleEntry<>("smallPro", "物流车入厂申报弹窗"),
            new AbstractMap.SimpleEntry<>("inNote", "进厂宇信通提示"),
            new AbstractMap.SimpleEntry<>("outNote", "出厂宇信通提示"),
            new AbstractMap.SimpleEntry<>("SMSLink", "访客短信后缀"),
            new AbstractMap.SimpleEntry<>("secret", "绝密区短息提示"),
            new AbstractMap.SimpleEntry<>("unSecret", "绝密区和非绝密区短信提示")
    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    /**
     * 根据类型获取详情数据
     * @param type
     * @return
     */
    public SafetyGeneralConfigDO getConfigByType(String type) {
        return safetyGeneralConfigService.getConfigByType(type);
    }

    /**
     * 数据添加或者更新
     * @param param
     * @param token
     */
    public void saveOrUpdate(GeneralConfigParam param, String token) {
        safetyGeneralConfigService.saveOrUpdate(param, token);
    }

    /**
     * 数据列表查询
     * @return
     */
    public Object list() {
        List<SafetyGeneralConfigDO> list = safetyGeneralConfigService.list();
        list.forEach(li -> {
            if (StringUtils.isBlank(li.getGeneralDesc())) {
                li.setGeneralDesc(DATA_MAP_TYPE.getOrDefault(li.getGeneralType(), ""));
            }
        });
        return list;
    }

    /**
     * 调用接口发送短信获取宇信通信息
     * @param param
     * @return
     */
    public Object sendNote(GeneralConfigAppParam param) {
        //获取对象数据
        List<SafetyVisitorHairpinDO> visitorList = safetyVisitorHairpinService.getVisitorListByEipId(param.getEipId(),null);
        if (CollectionUtils.isEmpty(visitorList)) {
            LogUtils.info("获取访客数据为空，此操作取消");
            return null;
        }
        SafetyVisitorHairpinDO safetyVisitorHairpinDO = visitorList.get(0);

        //判断是否访问绝密区
        //代表全是绝密区
        if(1 == safetyVisitorHairpinDO.getSecretState() && StringUtils.isEmpty(safetyVisitorHairpinDO.getIssueFactory())){
            //如果访问的厂区全部为绝密区，查secret；包含绝密区查unSecret
            param.setGeneralType("secret");
        }else if(1 == safetyVisitorHairpinDO.getSecretState() && !StringUtils.isEmpty(safetyVisitorHairpinDO.getIssueFactory())){
            param.setGeneralType("unSecret");
            String unIssueFactory = safetyVisitorHairpinDO.getUnIssueFactory();
            String unIssueFactoryName = "";

            //查询工厂信息
            Map<Integer, String> factoryNameList = safetyFieldManageService.getFactoryNameList();
            if(!StringUtils.isEmpty(unIssueFactory)){
                String[] split = unIssueFactory.split(";");
                for (String s:split) {
                    unIssueFactoryName += (factoryNameList.get(Integer.parseInt(s)) + "、");
                }

                if(unIssueFactoryName.endsWith("、")){
                    unIssueFactoryName = unIssueFactoryName.substring(0, unIssueFactoryName.length() - 1);
                }

                safetyVisitorHairpinDO.setUnIssueFactory(unIssueFactoryName);
            }
        }

        SafetyGeneralConfigDO safetyGeneralConfigDO = getConfigByType(param.getGeneralType());
        if (safetyGeneralConfigDO == null) {
            LogUtils.info("没有获取后台配置");
            return null;
        }
        if (safetyGeneralConfigDO.getGeneralShow().equals(1)) {
            LogUtils.info("后台发送开关已关闭");
            return null;
        }
        String content = safetyGeneralConfigDO.getContent();
        if (StringUtils.isEmpty(content)) {
            LogUtils.info("发送信息为空，此操作取消");
            return null;
        }

        //数据转换成对象
        Map<String, String> visitorMap = Maps.newHashMap();
        try {
            visitorMap = BeanUtils.describe(safetyVisitorHairpinDO);
        } catch (Exception e) {
            LogUtils.error("访客信息对象转map出错：{}", e);
        }
        //组装数据
        Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String mapKey = matcher.group(1);
            String mapData = visitorMap.get(mapKey);
            if ("startTime;endTime;hairpinTime;recoveryTime;eipCreateTime".indexOf(mapKey) > -1) {
                mapData = DateUtils.dateToString(mapData);
                if("endTime".equals(mapKey)){
                    mapData = mapData.substring(11);
                }
            }
            content = content.replaceFirst("\\{[^}]*\\}", mapData);
        }
        String ret = "";
        //查看数据是否正常打开
        if (("note".equals(param.getGeneralType()) || "secret".equals(param.getGeneralType()) || "unSecret".equals(param.getGeneralType())) && StringUtils.isNotEmpty(param.getPhones())) {
            SafetyGeneralConfigDO en = getConfigByType("SMSLink");

            for(SafetyVisitorHairpinDO v:visitorList){
                String visitorPhone = v.getVisitorPhone();

                if (StringUtils.isEmpty(visitorPhone) || !param.getPhones().contains(visitorPhone)) {continue;}

                String con = "";
                if (!ObjectUtils.isEmpty(en)) {
                    con = en.getContent();
                    con = con.replace("{id}",v.getId()+"");
                }
                //发短信
                ret = safetyGeneralConfigService.sendMsgSms(visitorPhone, content + con);
            };
        } else if ("shake".equals(param.getGeneralType()) && StringUtils.isNotEmpty(param.getEmpNo()) && StringUtils.isNotEmpty(param.getRobotId())) {
            //发送掌上宇通数据
            ret = safetyGeneralConfigService.sendMsgIm(param.getEmpNo(), param.getRobotId(), content);
        }
        return ret;
    }
}
