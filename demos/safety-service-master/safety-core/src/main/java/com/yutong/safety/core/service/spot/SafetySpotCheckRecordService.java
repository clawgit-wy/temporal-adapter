package com.yutong.safety.core.service.spot;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.param.web.spot.SpotCheckRecordListParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckRecordDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckRecordDOExample;
import com.yutong.safety.core.db.entity.spot.SafetyValidateScenePostDO;
import com.yutong.safety.core.db.mapper.spot.SafetySpotCheckRecordDOBizMapper;
import com.yutong.safety.core.db.mapper.spot.SafetySpotCheckRecordDOMapper;
import com.yutong.safety.core.dto.SpotCheckRecordDTO;
import com.yutong.safety.core.service.SafetyVisitorHairpinService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gwxiexj
 * @since 2024-05-09 16:15
 */
@Service
public class SafetySpotCheckRecordService {

    @Value("${eip.approveFlow}")
    private String approveFlowUrl;
    @Value("${eip.templateIdSbdj}")
    private String templateId;
    @Autowired
    private WebserviceUtil webserviceUtil;

    @Autowired
    SafetyVisitorHairpinService safetyVisitorHairpinService;

    @Autowired
    private SafetySpotCheckRecordDOMapper safetySpotCheckRecordDOMapper;

    @Autowired
    private SafetySpotCheckRecordDOBizMapper safetySpotCheckRecordDOBizMapper;

    public int save(SafetySpotCheckRecordDO recordDO){
        return safetySpotCheckRecordDOMapper.insert(recordDO);
    }

    public int update(SafetySpotCheckRecordDO recordDO){
        return safetySpotCheckRecordDOMapper.updateByPrimaryKeySelective(recordDO);
    }

    public int updateByExample(SafetySpotCheckRecordDO recordDO, SafetySpotCheckRecordDOExample example){
        return safetySpotCheckRecordDOMapper.updateByExample(recordDO,example);
    }

    public int updateByExampleSelective(SafetySpotCheckRecordDO recordDO, SafetySpotCheckRecordDOExample example){
        return safetySpotCheckRecordDOMapper.updateByExampleSelective(recordDO,example);
    }

    public List<SafetySpotCheckRecordDO> selectList(SafetySpotCheckRecordDOExample example){
        return safetySpotCheckRecordDOMapper.selectByExample(example);
    }

    public List<SpotCheckRecordDTO> selectRecordHistory(SpotCheckRecordListParam param) {
        return safetySpotCheckRecordDOBizMapper.selectRecordHistory(param);
    }

    public long selectRecordHistoryCount(SpotCheckRecordListParam param) {
        return safetySpotCheckRecordDOBizMapper.selectRecordHistoryCount(param);
    }

    public int updatePlanEmpBatch(SpotCheckRecordListParam param){
        return safetySpotCheckRecordDOBizMapper.updatePlanEmpBatchNew(param);
    }

    //查询出流程号不为空且状态为待整改的数据
    public List<SafetySpotCheckRecordDO> getRecordsByTaskId(List<Integer> taskIdList,String type){
        if("old".equals(type)){
            return safetySpotCheckRecordDOMapper.getRecordsByTaskId(taskIdList);
        }else if("new".equals(type)){
            return safetySpotCheckRecordDOMapper.getRecordsByTaskIdNew(taskIdList);
        }
        return null;
    }

    //批量更新
    public void batchUpdateRecord(List<SafetySpotCheckRecordDO> taskIdList){
        safetySpotCheckRecordDOBizMapper.batchUpdateRecord(taskIdList);
    }

    public Object createEIPFlow(List<Integer> idList, String eip){
        //根据点检记录id查询数据
        List<Map<String, Object>> list = safetySpotCheckRecordDOMapper.getListByIds(idList);

        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        String eipId = createEIPFlowFunc(list, eip);

        //保存
        if(StringUtils.isNotEmpty(eipId)){
            safetySpotCheckRecordDOMapper.updateEipFlowByIds(eipId,idList);
        }

        return null;
    }

    /**
     * 创建eip流程
     * @param list
     * @param eip
     */
    public String createEIPFlowFunc(List<Map<String, Object>> list, String eip){
        //获取表单设置对应字段
        Map<String, Object> map = new HashMap<>();
        //字段拼接
        //厂区
        String factoryName = list.get(0).get("factoryName").toString();

        //设备类型
        String deviceType = list.get(0).get("deviceType").toString();

        map.put("fd_3a0e1c4354548e", factoryName);
        //需求设备数量
        map.put("fd_3bbd8f64e0d0a6", list.size());
        List sonList = Lists.newArrayList();

        list.stream().forEach(m->{
            Map<String, Object> sonMap = new HashMap<>();
            //工厂
            sonMap.put("fd_3a0e1ce12d0180", m.get("factoryName"));
            //设备名称
            sonMap.put("fd_3a0e1cec76cb18", m.get("deviceName"));
            //设备安装位置
            sonMap.put("fd_3a0e1cedc4343c", m.get("location"));
            //设备类型
            sonMap.put("fd_3a1ce1ebbd3f0a", m.containsKey("teamName") ? m.get("teamName") : m.get("deviceType"));
            //故障原因
            sonMap.put("fd_3a0e1cee54b40c", m.get("exceptionDesc"));
            //备注
            sonMap.put("fd_3a0e1cefe651c0", m.get("bz"));
            sonList.add(sonMap);
        });

        String eipLog = eip;

        //明细表id
        map.put("fd_3a0e1c8509ce42", sonList);
        String formDataXML = webserviceUtil.createXML(map);
        StringBuilder params = new StringBuilder();
        params.append("<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ith=\"http://com/yt/eip/ws/third/service/IThirdPartyWfService.wsdl\">");
        params.append("<soapenv:Header/><soapenv:Body><ith:execute soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><in0 xsi:type=\"mod:ThirdPartyParameter\" xs:type=\"type:ThirdPartyParameter\" xmlns:mod=\"http://model.third.ws.eip.yt.com\" xmlns:xs=\"http://www.w3.org/2000/XMLSchema-instance\">");
        params.append("<creatorName xsi:type=\"xsd:string\" xs:type=\"type:string\">").append(eipLog).append("</creatorName>");
        params.append("<formDataXML xsi:type=\"xsd:string\" xs:type=\"type:string\">");
        params.append("<![CDATA["+formDataXML+"]]>");
        params.append("</formDataXML>");
        params.append("<modelName xsi:type=\"xsd:string\" xs:type=\"type:string\">com.landray.kmss.km.review.model.KmReviewMain</modelName><nodeId xsi:type=\"xsd:string\" xs:type=\"type:string\"/><notifyMethod xsi:type=\"xsd:string\" xs:type=\"type:string\"/><notifyType xsi:type=\"xsd:string\" xs:type=\"type:string\"/><relationUrl xsi:type=\"xsd:string\" xs:type=\"type:string\"/>");
        // 主题
        params.append("<subject xsi:type=\"xsd:string\" xs:type=\"type:string\">").append(factoryName).append(deviceType).append("设备需求评审流程").append("</subject>");
        // 流程状态 10 草稿 20 待审批
        params.append("<submitStatus xsi:type=\"xsd:string\" xs:type=\"type:string\">").append("10").append("</submitStatus>");
        // 模板id
        params.append("<templateId xsi:type=\"xsd:string\" xs:type=\"type:string\">").append(templateId).append("</templateId>");
        // 触发系统标识
        params.append("<thirdSystemStart xsi:type=\"xsd:string\" xs:type=\"type:string\">").append("ZY").append("</thirdSystemStart>");
        params.append("<webServiceUrl xsi:type=\"xsd:string\" xs:type=\"type:string\"/></in0></ith:execute></soapenv:Body></soapenv:Envelope>");
        String webResult = webserviceUtil.doWebservicePost(approveFlowUrl, params.toString());
        Map<String, Object> resultMap = JsonUtils.toMap(webResult);
        Map<String, Object> envelopeMap = JsonUtils.toMap(String.valueOf(resultMap.get("soapenv:Envelope")));
        Map<String, Object> bodyMap = JsonUtils.toMap(String.valueOf(envelopeMap.get("soapenv:Body")));
        Map<String, Object> multiRefMap = JsonUtils.toMap(String.valueOf(bodyMap.get("multiRef")));
        Map<String, Object> modelIdMap = JsonUtils.toMap(String.valueOf(multiRefMap.get("modelId")));
        String eipId = String.valueOf(modelIdMap.get("content"));
        LogUtils.info("eip流程id:{}", eipId);
        if(StringUtils.isNotEmpty(eipId) && !"null".equals(eipId)) {
            return eipId;
        }
        return null;
    }
}
