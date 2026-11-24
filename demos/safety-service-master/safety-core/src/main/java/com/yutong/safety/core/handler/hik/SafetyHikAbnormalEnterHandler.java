package com.yutong.safety.core.handler.hik;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.yutong.safety.common.constants.SafetyHikThresholdEnum;
import com.yutong.safety.common.utils.Base64ImageDecoder;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.hik.SafetyHikAbnormalEnterDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikAbnormalEnterDOExample;
import com.yutong.safety.core.db.entity.hik.SafetyHikMonitorDO;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikAbnormalEnterAddParam;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikAbnormalEnterQueryParam;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikMonitorQueryParam;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import com.yutong.safety.core.service.hik.SafetyHikAbnormalEnterService;
import com.yutong.safety.core.service.hik.SafetyHikMonitorService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SafetyHikAbnormalEnterHandler extends AbstractSafetyWebBaseHandler<SafetyHikAbnormalEnterDO, SafetyHikAbnormalEnterDOExample, SafetyHikAbnormalEnterQueryParam, SafetyHikAbnormalEnterAddParam> {

    private static final Map<Integer, String> STATUS = ImmutableMap.of(0, "处理中", 1, "已处理", 2, "预留");

    @Resource
    private SafetyArtemisHandler safetyArtemisHandler;

    @Resource
    private SafetyHikMonitorService safetyHikMonitorService;

    @Resource
    private SafetyHikAbnormalEnterService safetyHikAbnormalEnterService;

    @Override
    public AbstractSafetyWebBaseService<SafetyHikAbnormalEnterDO, SafetyHikAbnormalEnterDOExample, SafetyHikAbnormalEnterQueryParam> getService() {
        return safetyHikAbnormalEnterService;
    }

    @Override
    public String getModuleName() {
        return "异常进出记录";
    }

    @Override
    protected String getTemplateName() {
        return null;
    }

    @Override
    protected String getRoleName() {
        return null;
    }

    @Override
    public Pair<String[], String[][]> processExportData(List<SafetyHikAbnormalEnterDO> dataList) {
        String[] title = {"姓名", "员工号/手机号", "异常进出类型", "进厂方式", "厂区", "区域（楼栋）", "抓拍人脸", "抓拍设备", "设备编号", "抓拍时间", "状态"};
        // 使用流操作简化填充数据
        String[][] values = dataList.stream()
                .map(info -> new String[]{
                        info.getName(),
                        info.getEmpNo(),
                        info.getAbnormalEnterType(),
                        info.getAccessMethod(),
                        info.getFactoryName(),
                        info.getBuildName(),
                        info.getBkgUrl(),
                        info.getCameraName(),
                        info.getCameraIndexcode(),
                        info.getEventTime(),
                        info.getStatus() != null ? STATUS.get(info.getStatus()) : "未知状态",
                })
                .toArray(String[][]::new);
        return Pair.of(title, values);
    }

    public VO syncAbnormalEnter() {

        List<SafetyHikAbnormalEnterDO> list = safetyArtemisHandler.queryEventStranger(1);
        if (CollectionUtils.isEmpty(list)) {
            return VoUtil.successData("无异常进出记录");
        }

        List<SafetyHikMonitorDO> hikMonitorDOList = safetyHikMonitorService.selectList(new SafetyHikMonitorQueryParam(), false);
        Map<String, SafetyHikMonitorDO> dataMap = hikMonitorDOList.stream().collect(Collectors.toMap(SafetyHikMonitorDO::getIndexCode, t -> t, (t1, t2) -> t1));

        list.forEach(li -> {
            JSONArray jsonArray = safetyArtemisHandler.checkOneToMany(Base64ImageDecoder.imgBase64(li.getSnapUrl()), SafetyHikThresholdEnum.ABNORMAL_ENTER);
            if (jsonArray.size() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0).getJSONObject("faceInfo");
                li.setName(jsonObject.getString("name"));
                li.setEmpNo(jsonObject.getString("certificateNum"));
            }
            SafetyHikMonitorDO monitorDO = dataMap.get(li.getCameraIndexcode());
            if (monitorDO != null) {
                li.setBuildCode(monitorDO.getBuildCode());
                li.setBuildName(monitorDO.getBuildName());
                li.setFactoryCode(monitorDO.getFactoryCode());
                li.setFactoryName(monitorDO.getFactoryName());
                li.setAccessMethod(monitorDO.getAccessMethod());
            }
            li.setEventTime(DateUtils.convertDateTime(li.getEventTime()));
            li.setCreates("", "定时任务");
        });

        if (!CollectionUtils.isEmpty(list)) {
            safetyHikAbnormalEnterService.batchSave(list);
        }
        return VoUtil.success();
    }
}
