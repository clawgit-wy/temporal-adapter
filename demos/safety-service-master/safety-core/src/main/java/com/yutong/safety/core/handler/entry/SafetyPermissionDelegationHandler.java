package com.yutong.safety.core.handler.entry;

import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.enter.SafetyEnterIssuanceDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfig;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryConfigQueryParams;
import com.yutong.safety.core.service.SafetyDataSendLogService;
import com.yutong.safety.core.service.SafetyDictItemService;
import com.yutong.safety.core.service.SafetyFieldManageService;
import com.yutong.safety.core.service.SafetyOutsiderPromiseChangeService;
import com.yutong.safety.core.service.enter.SafetyEnterIssuanceService;
import com.yutong.safety.core.service.entry.SafetyEntryConfigService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants.*;

/**
 * 备案人员权限下发
 */
@Component
public class SafetyPermissionDelegationHandler extends VoUtil {

    @Value("${sendData.qrUrl}")
    private String qrUrl;

    @Value("${entry.card.tcmid}")
    private String entryCardTcmid;

    @Resource
    private SafetyDataSendLogService safetyDataSendLogService;

    @Resource
    private SafetyEntryConfigService safetyEntryConfigService;

    @Resource
    private SafetyEnterIssuanceService safetyEnterIssuanceService;

    @Resource
    private SafetyDictItemService safetyDictItemService;

    @Resource
    private SafetyFieldManageService safetyFieldManageService;

    @Resource
    private SafetyOutsiderPromiseChangeService safetyOutsiderPromiseChangeService;


    /**
     * 备案权限下发 定时任务
     *
     * @return 结果
     */
    public VO entryPermissionDelegationTask() {
        String startTime = DateUtils.format(new Date(), 3);
        String maxStartTime = DateUtils.reduceMinute(new Date(), 30, 3);
        List<SafetyEntryConfig> dataList = getData(maxStartTime, Lists.newArrayList(), true);
        if (CollectionUtils.isEmpty(dataList)) {
            return success();
        }

        List<String> cardList = dataList.stream().map(SafetyEntryConfig::getCardNo).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        dataList = getData(maxStartTime, cardList, false);
        if (CollectionUtils.isEmpty(dataList)) {
            return success();
        }

        issuePermissionsToCrk(dataList, startTime, "定时任务");
        return success();
    }

    /**
     * 获取数据集
     * @param maxStartTime 最大的开始时间
     * @param cardNoList 身份证号
     * @param downMark 下发标记 true 未下发的， false 全部数据
     * @return 返回需要的数据
     */
    public List<SafetyEntryConfig> getData(String maxStartTime, List<String> cardNoList, boolean downMark) {
        SafetyEntryConfigQueryParams params = new SafetyEntryConfigQueryParams();
        params.setMinEnterEndTime(DateUtils.format(new Date(), 3));
        params.setMaxEnterStartTime(maxStartTime);
        params.setStatus(ENTRY_CONFIG_STATUS_PASS);
        // 排除掉二维码下发的数据 0：刷脸+二维码，1：刷脸，2：二维码
        params.setEntryMethod(2);
        if (downMark) {
            params.setDownMark("1");
        }
        if (!CollectionUtils.isEmpty(cardNoList)) {
            params.setCardNoList(cardNoList);
        }
        return safetyEntryConfigService.selectList(params, false);
    }

    /**
     * 实时下发
     * 条件：身份证号、截止日期大于当前日期、状态等于通过、开始时间<=time(time==null ?time+20min)
     *
     * @param cardNoList 身份证号
     * @param time 下发的时间范围， 为空的话， 默认为当前时间+20， 业务：开始时间<=time
     * @param user 下发的用户
     */
    public void issuePermissionsRealTimeToCrk(List<String> cardNoList, String time, String user) {
        if (StringUtils.isEmpty(time)) {
            // 时间加20分钟
            time = DateUtils.reduceMinute(new Date(), 20, 3);
        }
        SafetyEntryConfigQueryParams params = new SafetyEntryConfigQueryParams();
        params.setCardNoList(cardNoList);
        params.setMinEnterEndTime(DateUtils.format(new Date(), 3));
        params.setMaxEnterStartTime(time);
        params.setStatus(ENTRY_CONFIG_STATUS_PASS);
        // 排除掉二维码下发的数据 0：刷脸+二维码，1：刷脸，2：二维码
        params.setEntryMethod(2);
        List<SafetyEntryConfig> configs = safetyEntryConfigService.selectList(params, false);
        if (CollectionUtils.isEmpty(configs)) {
            return;
        }
        issuePermissionsToCrk(configs, time, user);
    }


    /**
     * 注销权限后的重新下发
     * 条件：身份证号、截止日期大于当前日期、状态等于通过
     *
     * @param cardNoList 身份证号
     * @param user 下发的用户
     */
    public void issuePermissionsAgainCrk(List<String> cardNoList, String user) {
        final String date = DateUtils.format(new Date(), 3);
        SafetyEntryConfigQueryParams params = new SafetyEntryConfigQueryParams();
        params.setCardNoList(cardNoList);
        params.setMinEnterEndTime(date);
        params.setStatus(ENTRY_CONFIG_STATUS_PASS);
        List<SafetyEntryConfig> configs = safetyEntryConfigService.selectList(params, false);
        if (CollectionUtils.isEmpty(configs)) {
            return;
        }
        issuePermissionsToCrk(configs, date, user);
    }
    /**
     * 备案下发日志-手动下发
     * @param list 需要下发权限的数据
     */
    public void reissueCrk(List<SafetyEnterIssuanceDO> list) {
        Map<String, String> recordDataMap = safetyDictItemService.selectListToMap(ENTRY_DICT_SUPPER_CODE_TYPE, ENTRY_DICT_SHOW_FLAG_VALUE);

        Date date = new Date();
        list.forEach(li -> {
            String recordType = this.getRecordType(recordDataMap, li.getFdEnterTypeCode());
            String deviceNo = getEmpowerDevice(li.getEnterFactoryCode(), recordType);
            // 数据下发 - 人脸
            issueCrk(li, deviceNo);
            li.setIssuanceTime(date);
            li.setUpdateTime(date);
        });
        safetyEnterIssuanceService.updateBatch(list);
    }

    @Async
    public void issuePermissionsToCrkAsync(List<SafetyEntryConfig> dataList, String startTime, String user) {
        this.issuePermissionsToCrk(dataList, startTime, user);
    }

    /**
     * 数据合并下发
     */
    public void issuePermissionsToCrk(List<SafetyEntryConfig> configs, String issueTime, String user) {
        try {
            Map<String, String> recordDataMap = safetyDictItemService.selectListToMap(ENTRY_DICT_SUPPER_CODE_TYPE, ENTRY_DICT_SHOW_FLAG_VALUE);
            Map<Integer, String> dataMap = safetyFieldManageService.getFactoryNameList();

            // 3.数据整合
            Map<String, List<SafetyEntryConfig>> cardNoDataMap = configs.stream().collect(Collectors.groupingBy(li -> li.getCardNo() + "," + li.getRecordTypeCode()));

            List<SafetyEnterIssuanceDO> list = new ArrayList<>();
            Date currentDateTime = new Date();
            cardNoDataMap.forEach((key, value) -> {

                String[] split = key.split(",");
                String cardNo = split[0];
                String recordTypeCode = split[1];
                String recordType = this.getRecordType(recordDataMap, recordTypeCode);

                Set<String> factoryCodes = new HashSet<>();
                Set<String> configIds = new HashSet<>();
                Date maxEndTime = null;
                String photoUrl = "";
                for (SafetyEntryConfig li : value) {
                    factoryCodes.addAll(Arrays.asList(li.getEnterFactoryCode().split(",")));
                    configIds.add(li.getId().toString());

                    Date enterEndTime = DateUtils.parse(li.getEnterEndTime(), 3);
                    if (maxEndTime == null || DateUtils.compareDates(enterEndTime, maxEndTime) == 1) {
                        maxEndTime = enterEndTime;
                    }
                    if (StringUtils.isEmpty(photoUrl) && StringUtils.isNotEmpty(li.getPhotoUrl())) {
                        photoUrl = li.getPhotoUrl();
                    }
                }

                String factoryCodeStr = String.join(",", factoryCodes);
                String filingPark = this.getFilingPark(dataMap, factoryCodeStr);

                // 创建入库实体
                SafetyEnterIssuanceDO issuance = new SafetyEnterIssuanceDO();
                issuance.setFdName(value.get(0).getName());
                issuance.setFdCardNo(cardNo);
                issuance.setFdEnterTypeCode(recordTypeCode);
                issuance.setFdImg(photoUrl);
                issuance.setIsDelete(0);
                issuance.setEnterFactoryCode(factoryCodeStr);
                issuance.setEnterFactory(filingPark);
                issuance.setEnterStartTime(issueTime);
                issuance.setEnterEndTime(DateUtils.format(maxEndTime, 3));
                issuance.setFdFilingId(String.join(",", configIds));
                issuance.setFdPerPhone(value.get(0).getPhone());
                issuance.setCreateTime(currentDateTime);
                issuance.setCreateBy(user);
                issuance.setUpdateTime(currentDateTime);
                issuance.setUpdateBy(user);
                issuance.setIssuanceTime(currentDateTime);

                String deviceNo = getEmpowerDevice(factoryCodeStr, recordType);
                if (deviceNo != null) {
                    // 数据下发 - 人脸
                    boolean b = issueCrk(issuance, deviceNo);
                    list.add(issuance);

                    // 回写下发标记
                    if (b) {
                        safetyEntryConfigService.batchUpdateDownMark(new ArrayList<>(configIds), "0");
                    }
                }
            });

            if (!CollectionUtils.isEmpty(list)) {
                safetyEnterIssuanceService.saveBatch(list);
            }
        } catch (BizException e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "入场权限下发失败");
        }
    }


    /**
     * 下发 人脸
     *
     * @param dto          基础数据
     * @param deviceCodeNo 下发设备号
     * @return true成功， false失败
     */
    public boolean issueCrk(SafetyEnterIssuanceDO dto, String deviceCodeNo) {
        try {
            // 定义请求参数
            JSONObject params = new JSONObject();
            MediaType mediaType = MediaType.parse("application/json");

            // 填充请求参数
            if (StringUtils.isNotEmpty(dto.getFdImg())) {
                String crossData = safetyDataSendLogService.getImgUrlToBase64(dto.getFdImg());
                params.put("crossdata", crossData);
            }

            params.put("phone", "B" + dto.getFdPerPhone());
            params.put("idno", dto.getFdCardNo());
            params.put("staffname", dto.getFdName());
            params.put("devnos", deviceCodeNo.split(","));
            // 0人脸，1二维码，2门禁
            params.put("crosstype", "0");
            params.put("stafftype", 0);
            params.put("endtime", DateUtils.format(DateUtils.parse(dto.getEnterEndTime(), 3), 3));
            params.put("tcmid", entryCardTcmid);

            LogUtils.info("入场备案调用入参：" + params.toJSONString());

            // 构建请求
            Request request = new Request.Builder()
                    .url(qrUrl)
                    .post(okhttp3.RequestBody.create(mediaType, params.toJSONString()))
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .build();

            // 发送请求
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            LogUtils.info("入场备案接口返回状态：" + response.toString());

            if (!response.isSuccessful()) {
                LogUtils.warn("接口调用失败，响应码" + response.code());
                dto.setIssuanceResult("false");
                return false;
            }

            String responseBody = response.body() != null ? response.body().string() : null;
            LogUtils.info("入场备案调用接口出参：" + responseBody);
            if (StringUtils.isEmpty(responseBody)) {
                LogUtils.warn("接口响应为空");
                dto.setIssuanceResult("false");
                return false;
            }

            Map<String, Object> resultMap = JsonUtils.toMap(responseBody);
            Boolean aBoolean = handleFaceIssue(resultMap);
            dto.setIssuanceResult(aBoolean ? "true" : "false");

            return aBoolean;
        } catch (Exception e) {
            LogUtils.error("入场备案下发异常", e);
            dto.setIssuanceResult("false");
            return false;
        }
    }

    private Boolean handleFaceIssue(Map<String, Object> resultMap) {
        if (resultMap.get("IsSucess") != null && "true".equals(resultMap.get("IsSucess").toString())) {
            LogUtils.info("人脸下发成功");
            return true;
        } else {
            LogUtils.warn("人脸下发失败");
            return false;
        }
    }

    private void updateQRCode(String erCode, SafetyEnterIssuanceDO dto) {
        try {
            List<String> ids = Arrays.stream(dto.getFdFilingId().split(",")).collect(Collectors.toList());
            safetyEntryConfigService.batchUpdateQrCode(ids, erCode);
        } catch (Exception e) {
            LogUtils.error("更新二维码失败", e);
        }
    }
    /**
     * 数据构建
     *
     * @param config 备案信息
     * @param user   用户信息
     * @return 返回构建的数据
     */
    public SafetyEnterIssuanceDO getSafetyEnterIssuanceDO(SafetyEntryConfig config, String user) {
        Date date = new Date();
        SafetyEnterIssuanceDO info = new SafetyEnterIssuanceDO();
        info.setFdName(config.getName());
        info.setFdCardNo(config.getCardNo());
        info.setFdEnterTypeCode(config.getRecordTypeCode());
        info.setFdImg(config.getPhotoUrl());
        info.setIsDelete(0);
        info.setEnterFactoryCode(config.getEnterFactoryCode());
        info.setEnterFactory(config.getEnterFactory());
        info.setEnterStartTime(config.getEnterStartTime());
        info.setEnterEndTime(config.getEnterEndTime());
        info.setFdFilingId(config.getId().toString());
        info.setFdPerPhone(config.getPhone());
        info.setCreateTime(date);
        info.setCreateBy(user);
        info.setUpdateTime(date);
        info.setUpdateBy(user);
        info.setIssuanceTime(date);
        return info;
    }

    /**
     * 获取厂区，区域，备案类型，对应的设备信息
     * @param factoryCodes 厂区
     * @param empowerType 备案信息
     * @return 返回设备信息
     */
    public String getEmpowerDevice(String factoryCodes, String empowerType) {
        List<String> list = Arrays.asList(factoryCodes.split(","));
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("empowerType", empowerType);
        paramMap.put("factoryIds", list);
        paramMap.put("regionName", "大门人脸");
        return safetyOutsiderPromiseChangeService.findDevices(paramMap);
    }

    /**
     * 备案厂区賦值
     * @param filingPark 所有的厂区信息
     * @param factoryCode 指定的厂区编码
     * @return 匹配到的厂区名称
     */
    private String getFilingPark(Map<Integer, String> filingPark, String factoryCode) {
        // 为空校验
        if (StringUtils.isBlank(factoryCode)) {
            return "";
        }
        // 数据获取
        return Arrays.stream(factoryCode.split(",")).map(Integer::parseInt).distinct().map(filingPark::get).filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
    }

    /**
     * 备案类型賦值
     * @param recordDataMap 所有的备案类型信息
     * @param recordTypeCode 指定的备案类型编码
     * @return 匹配到的厂区名称
     */
    public String getRecordType(Map<String, String> recordDataMap, String recordTypeCode) {
        // 为空校验
        if (StringUtils.isBlank(recordTypeCode)) {
            return "";
        }
        // 数据获取
        String recordType = recordDataMap.get(recordTypeCode);
        if (StringUtils.isNotBlank(recordType)) {
            return recordType;
        }
        return "";
    }

}
