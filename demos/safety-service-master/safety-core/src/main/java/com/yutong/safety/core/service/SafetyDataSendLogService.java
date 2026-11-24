package com.yutong.safety.core.service;

import com.alibaba.fastjson.JSONObject;
import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.DataSendLogAddParam;
import com.yutong.safety.common.param.web.DataSendLogListParam;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.core.db.entity.*;
import com.yutong.safety.core.db.mapper.SafetyDataSendLogDOMapper;
import com.yutong.safety.core.db.mapper.SafetyVisitorHairpinDOMapper;
import com.yutong.safety.core.service.spot.SafetyStopEmpowerService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static com.yutong.safety.core.db.entity.visitor.common.SafetyVisitorConstants.ID_CARD_PHOTO_DEFAULT_VALUE;

/**
 *  create gwcuiyx 2023.08.08
 *  数据下发日志管理--service
 */
@Service
public class SafetyDataSendLogService {
    @Autowired
    private SafetyDataSendLogDOMapper safetyDataSendLogDOMapper;
    @Value("${sendData.qrUrl}")
    private String qrUrl;
    @Value("${visitor.card.tcmid}")
    private String visitorCardTcmid;
    /**
     * 无身份证的下发tcmid
     */
    @Value("${visitor.card.tcmid.a}")
    private String visitorCardTcmIdA;
    /**
     * 有身份证的下发tcmid
     */
    @Value("${visitor.card.tcmid.b}")
    private String visitorCardTcmIdB;
    @Autowired
    private SafetyEmpowerDeviceService safetyEmpowerDeviceService;
    @Autowired
    private SafetyVisitorHairpinDOMapper safetyVisitorHairpinDOMapper;
    @Autowired
    private SafetyLogMessageService safetyLogMessageService;
    @Autowired
    private SafetyStopEmpowerService safetyStopEmpowerService;
    @Autowired
    private SafetySysUserService safetySysUserService;
    //注销入参
    private static String cardParam ="{  \"SubSystems\":[2],\"DepositMoney\":0,    \"VmsOperMoney\":0,    \"CmsOperMoney\":0,    \"Token\":\"$Token\",    \"IsExistCard\":true,    \"TokenType\":1,    \"UseModel\":0,    \"Plate\":null,\"TokenOper\":12,\"Remark\":\"访客接口操作员\",\"OperNo\":\"\",\"OperName\":\"访客接口操作员\",    \"Redate\":\"$Redate\",    \"Gid\":\"FF7F139D-B80C-46D0-B330-1118F00255B7\",    \"ProjectGids\":[\"&Gid\"],\"OldAccountBlace\":0,\"ID\":0,\"Rid\":null,    \"IsLegal\":true}";

    /**
     * 数据列表查询
     * @param param
     * @return
     */
    public List<SafetyDataSendLogDO> pageList(DataSendLogListParam param) {
        SafetyDataSendLogDOExample example = new SafetyDataSendLogDOExample();
        SafetyDataSendLogDOExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(param.getVisitorName())) {
            criteria.andVisitorNameLike("%" + param.getVisitorName() + "%");
        }
        if (StringUtils.isNotEmpty(param.getVisitorIdCard())) {
            criteria.andVisitorIdCardLike("%" + param.getVisitorIdCard() + "%");
        }
        if (Objects.nonNull(param.getFactoryId())) {
            criteria.andFactoryIdLike("%" + param.getFactoryId() + "%");
        }
        if (Objects.nonNull(param.getRegionId())) {
            criteria.andRegionIdLike("%" + param.getRegionId() + "%");
        }
        if (StringUtils.isNotEmpty(param.getStartTime())) {
            String start = param.getStartTime() + " 0:0:0";
            criteria.andSendTimeGreaterThanOrEqualTo(DateUtils.parse(start, 2));
        }
        if (StringUtils.isNotEmpty(param.getEndTime())) {
            String end = param.getEndTime() + " 23:59:59";
            criteria.andSendTimeLessThanOrEqualTo(DateUtils.parse(end, 2));
        }
        if (Objects.nonNull(param.getSendState())) {
            criteria.andSendStateEqualTo(param.getSendState());
        }
        if (StringUtils.isNotEmpty(param.getVisitorPhone())) {
            criteria.andVisitorPhoneLike("%" + param.getVisitorPhone() + "%");
        }
        example.setOrderByClause(" send_time desc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        List<SafetyDataSendLogDO> list = safetyDataSendLogDOMapper.selectByExample(example);
        list.forEach(item -> {
            item.setVisitorIdCard(SensitiveUtils.maskCardId(item.getVisitorIdCard()));
            item.setVisitorPhone(SensitiveUtils.maskPhoneNumber(item.getVisitorPhone()));
            item.setVisitorName(SensitiveUtils.maskName(item.getVisitorName()));
        });
        return list;
    }

    /**
     * 总数据量
     * @param param
     * @return
     */
    public long pageListCount(DataSendLogListParam param) {
        SafetyDataSendLogDOExample example = new SafetyDataSendLogDOExample();
        SafetyDataSendLogDOExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(param.getVisitorName())) {
            criteria.andVisitorNameLike("%" + param.getVisitorName() + "%");
        }
        if (StringUtils.isNotEmpty(param.getVisitorIdCard())) {
            criteria.andVisitorIdCardLike("%" + param.getVisitorIdCard() + "%");
        }
        if (Objects.nonNull(param.getFactoryId())) {
            criteria.andFactoryIdLike("%" + param.getFactoryId() + "%");
        }
        if (Objects.nonNull(param.getRegionId())) {
            criteria.andRegionIdLike("%" + param.getRegionId() + "%");
        }
        if (StringUtils.isNotEmpty(param.getStartTime())) {
            String start = param.getStartTime() + " 0:0:0";
            criteria.andSendTimeGreaterThanOrEqualTo(DateUtils.parse(start, 2));
        }
        if (StringUtils.isNotEmpty(param.getEndTime())) {
            String end = param.getEndTime() + " 23:59:59";
            criteria.andSendTimeLessThanOrEqualTo(DateUtils.parse(end, 2));
        }
        if (Objects.nonNull(param.getSendState())) {
            criteria.andSendStateEqualTo(param.getSendState());
        }
        if (StringUtils.isNotEmpty(param.getVisitorPhone())) {
            criteria.andVisitorPhoneLike("%" + param.getVisitorPhone() + "%");
        }
        return safetyDataSendLogDOMapper.countByExample(example);
    }

    /**
     * 数据导出操作
     * @param param
     * @return
     */
    public List<SafetyDataSendLogDO> export(DataSendLogListParam param, String token) {
        SafetyDataSendLogDOExample example = new SafetyDataSendLogDOExample();
        SafetyDataSendLogDOExample.Criteria criteria = example.createCriteria();
        String searchStr = "";
        if (StringUtils.isNotEmpty(param.getVisitorName())) {
            criteria.andVisitorNameLike("%" + param.getVisitorName() + "%");
            searchStr += param.getVisitorName() + ";";
        }
        if (StringUtils.isNotEmpty(param.getVisitorIdCard())) {
            criteria.andVisitorIdCardLike("%" + param.getVisitorIdCard() + "%");
            searchStr += param.getVisitorIdCard() + ";";
        }
        if (Objects.nonNull(param.getFactoryId())) {
            criteria.andFactoryIdLike("%" + param.getFactoryId() + "%");
            searchStr += param.getFactoryId() + ";";
        }
        if (Objects.nonNull(param.getRegionId())) {
            criteria.andRegionIdLike("%" + param.getRegionId() + "%");
            searchStr += param.getRegionId() + ";";
        }
        if (StringUtils.isNotEmpty(param.getStartTime())) {
            String start = param.getStartTime() + " 0:0:0";
            criteria.andSendTimeGreaterThanOrEqualTo(DateUtils.parse(start, 2));
            searchStr += param.getStartTime() + ";";
        }
        if (StringUtils.isNotEmpty(param.getEndTime())) {
            String end = param.getEndTime() + " 23:59:59";
            criteria.andSendTimeLessThanOrEqualTo(DateUtils.parse(end, 2));
            searchStr += param.getEndTime() + ";";
        }
        if (Objects.nonNull(param.getSendState())) {
            criteria.andSendStateEqualTo(param.getSendState());
            searchStr += param.getSendState() + ";";
        }
        if (StringUtils.isNotEmpty(param.getVisitorPhone())) {
            criteria.andVisitorPhoneLike("%" + param.getVisitorPhone() + "%");
        }
        example.setOrderByClause(" send_time desc ");

        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("数据下发日志");
        log.setAbility("导出");
        log.setMessage(StringUtils.isNotEmpty(searchStr) ? searchStr : "全部");
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        return safetyDataSendLogDOMapper.selectByExample(example);
    }

    /**
     * 数据添加功能
     * @param param
     */
    public void add(DataSendLogAddParam param) {
        SafetyDataSendLogDO safetyDataSendLogDO = new SafetyDataSendLogDO();
        BeanUtils.copyProperties(param, safetyDataSendLogDO);
        safetyDataSendLogDO.setSendTime(new Date());
        safetyDataSendLogDOMapper.insert(safetyDataSendLogDO);
    }
    /**
     * 数据下发动作
     * @param safetyVisitorHairpinDO
     * @return
     */
     public String sendDataRecord(SafetyVisitorHairpinDO safetyVisitorHairpinDO, String crosstype, String endDate, String deviceType, boolean sendAgain, String hairpinNo, String authFactory) {
         //获取设备号
         LogUtils.info("下发的工厂{}",authFactory);
         int[] nos = safetyEmpowerDeviceService.getEmpowerNos(safetyVisitorHairpinDO.getFactoryId(), safetyVisitorHairpinDO.getRegionId(), deviceType, authFactory);
         String photo = safetyVisitorHairpinDO.getPhoto();
         String crossdata = "";
         if (StringUtils.isNotEmpty(photo) && "0".equals(crosstype)) {
             crossdata = getImgUrlToBase64(photo);
         }
         if ("2".equals(crosstype)) {
             crossdata = hairpinNo;
         }
         OkHttpClient client = new OkHttpClient();
         MediaType mediaType = MediaType.parse("application/json");
         com.alibaba.fastjson.JSONObject param3 = new com.alibaba.fastjson.JSONObject();
         String stafftype ="1";
         param3.put("phone", "F" + safetyVisitorHairpinDO.getVisitorPhone());//手机号
         param3.put("idno", safetyVisitorHairpinDO.getVisitorIdCard());//身份证号
         param3.put("staffname", safetyVisitorHairpinDO.getVisitorName());//姓名
         param3.put("devnos", nos);//设备号
         param3.put("crosstype", crosstype);//0人脸，1二维码，2门禁
         param3.put("crossdata", crossdata);//照片的转换值
         param3.put("stafftype", stafftype);//备案人员0，访客1
         // 20260721需求调整tcmid 逻辑：有身份证按访客卡B， 无身份证按访客卡A，门卫下发的（页面上的下发按有身份证下发）
         if (StringUtils.isNotEmpty(safetyVisitorHairpinDO.getIdCardPhoto())) {
             param3.put("tcmid", visitorCardTcmIdB);
         } else {
             param3.put("tcmid", visitorCardTcmIdA);
         }
         if (StringUtils.isNotEmpty(endDate)) {
             param3.put("endtime", endDate);//截止时间
         } else {
             param3.put("endtime", DateUtils.format(safetyVisitorHairpinDO.getEndTime(), 2));//截止时间
         }
         LogUtils.info("调用生成二维码入参：" + param3.toJSONString());
         okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, param3.toJSONString());
         Request request = new Request.Builder()
                 .url(qrUrl)
                 .post(body)
                 .addHeader("content-type", "application/json")
                 .addHeader("cache-control", "no-cache")
                 .build();
         LogUtils.info("生成二维码 接口地址 : {} \n 请求参数 {}", "http://10.66.32.54:60011/api/VisitorService/VisitorReg", param3.toJSONString());
         Response response = null;
         //组装数据下发日志
         DataSendLogAddParam dataSendLogAddParam = new DataSendLogAddParam();
         dataSendLogAddParam.setVisitorName(safetyVisitorHairpinDO.getVisitorName());
         dataSendLogAddParam.setVisitorIdCard(safetyVisitorHairpinDO.getVisitorIdCard());
         dataSendLogAddParam.setVendorFactory(safetyVisitorHairpinDO.getVendorFactory());
         dataSendLogAddParam.setVendorRegion(safetyVisitorHairpinDO.getVendorRegion());
         dataSendLogAddParam.setFactoryId(safetyVisitorHairpinDO.getFactoryId());
         dataSendLogAddParam.setRegionId(safetyVisitorHairpinDO.getRegionId());
         dataSendLogAddParam.setStartTime(safetyVisitorHairpinDO.getStartTime());
         dataSendLogAddParam.setEndTime(safetyVisitorHairpinDO.getEndTime());
         dataSendLogAddParam.setHairpinId(safetyVisitorHairpinDO.getId());
         dataSendLogAddParam.setPhoto(photo);
         dataSendLogAddParam.setVisitorPhone(safetyVisitorHairpinDO.getVisitorPhone());
         dataSendLogAddParam.setEipCreateTime(safetyVisitorHairpinDO.getEipCreateTime());
         if (StringUtils.isNotEmpty(safetyVisitorHairpinDO.getIdCardPhoto()) && !ID_CARD_PHOTO_DEFAULT_VALUE.equals(safetyVisitorHairpinDO.getIdCardPhoto())) {
             dataSendLogAddParam.setIdCardPhoto(safetyVisitorHairpinDO.getIdCardPhoto());
         }
         //如果是人脸而且还有车牌号，将数据写入到停车场授权记录表里面
         if ("0".equals(crosstype) && StringUtils.isNotEmpty(safetyVisitorHairpinDO.getCardNo())) {
             SafetyStopEmpowerDO safetyStopEmpowerDO = new SafetyStopEmpowerDO();
             safetyStopEmpowerDO.setCardNo(safetyVisitorHairpinDO.getCardNo());
             safetyStopEmpowerDO.setRegisterTime(new Date());
             safetyStopEmpowerDO.setVisitorName(safetyVisitorHairpinDO.getVisitorName());
             safetyStopEmpowerDO.setVisitorIdCard(safetyVisitorHairpinDO.getVisitorIdCard());
             safetyStopEmpowerDO.setVisitorPhone(safetyVisitorHairpinDO.getVisitorPhone());
             safetyStopEmpowerDO.setEmpowerFactory(safetyStopEmpowerDO.getEmpowerFactory());
             safetyStopEmpowerDO.setEmpowerNo(safetyStopEmpowerDO.getEmpowerNo());
             safetyStopEmpowerDO.setRemark(safetyStopEmpowerDO.getRemark());
             safetyStopEmpowerDO.setStartTime(safetyVisitorHairpinDO.getStartTime());
             safetyStopEmpowerDO.setEndTime(safetyVisitorHairpinDO.getEndTime());
             safetyStopEmpowerService.add(safetyStopEmpowerDO);
         }
         try {
             response = client.newCall(request).execute();
             String str = response.body().string();
             dataSendLogAddParam.setJointResult(str);
             LogUtils.info("接口调用成功：" + str);
             Map<String, Object> resultMap = JsonUtils.toMap(str);
             if(("1").equals(crosstype)){
                 if (resultMap.get("Describe") != null) {
                     String erCode = resultMap.get("Describe").toString();
                     LogUtils.info("生成二维码成功：" + StringUtils.isNotEmpty(erCode));
                     dataSendLogAddParam.setSendState(3);
                     if (!sendAgain) {
                         add(dataSendLogAddParam);
                     }
                     return erCode;
                 }
             } else if(("0").equals(crosstype)){
                 if(resultMap.get("IsSucess") != null){
                     String IsSucess = resultMap.get("IsSucess").toString();
                     if ("true".equals(IsSucess)) {
                         LogUtils.info("人脸下发日志成功：" + IsSucess);
                         dataSendLogAddParam.setSendState(0);
                         if (!sendAgain) {
                             add(dataSendLogAddParam);
                         }
                         return IsSucess;
                     }
                 }
             } else if(("2").equals(crosstype)){
                 if(resultMap.get("IsSucess") != null){
                     String IsSucess = resultMap.get("IsSucess").toString();
                     if ("true".equals(IsSucess)) {
                         LogUtils.info("门禁下发日志成功：" + IsSucess);
                         dataSendLogAddParam.setSendState(5);
                         if (!sendAgain) {
                             add(dataSendLogAddParam);
                         }
                         return IsSucess;
                     }
                 }
             }
         } catch (IOException e) {
             if(("1").equals(crosstype)){
                 dataSendLogAddParam.setSendState(1);
             } else if(("0").equals(crosstype)){
                 dataSendLogAddParam.setSendState(2);
             } else if(("2").equals(crosstype)){
                 dataSendLogAddParam.setSendState(4);
             }
             if (!sendAgain) {
                 add(dataSendLogAddParam);
             }
             e.printStackTrace();
             LogUtils.info("接口调用失败{}",e.getMessage());
         } catch (IllegalStateException e){
             LogUtils.info("接口调用失败IllegalStateException,{}",e.getMessage());
         }
         return null;
     }

    /**
     * 照片转BASE64
     * @param imgUrl
     * @return
     */
    public String getImgUrlToBase64(String imgUrl) {
        byte[] buffer = null;
        InputStream inputStream = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // 创建URL
            URL url = new URL(imgUrl);
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            inputStream = conn.getInputStream();
            // 将内容读取内存中
            buffer = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            buffer = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    // 关闭inputStream流
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对字节数组Base64编码
        Base64.Encoder encode = Base64.getEncoder();
        return encode.encodeToString(buffer);
    }

    public void sendAgain(Integer id, String token) {
        if(Objects.isNull(id)) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"id参数为空！");
        }
        SafetyDataSendLogDO safetyDataSendLogDO = safetyDataSendLogDOMapper.selectByPrimaryKey(id);
        SafetyVisitorHairpinDO safetyVisitorHairpinDO = safetyVisitorHairpinDOMapper.selectByPrimaryKey(safetyDataSendLogDO.getHairpinId());
        //区分人脸和门禁
        String result = null;
        int state = 0;
        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        //获取权限
        SafetySysUserDO safetySysUserDO = safetySysUserService.getUserMessage(workNo);
        if ("2".equals(String.valueOf(safetyDataSendLogDO.getSendState()))) {
            result = sendDataRecord(safetyVisitorHairpinDO, "0" , "", "人脸", true, safetyVisitorHairpinDO.getHairpinNo(), safetySysUserDO.getFactoryId());
            state = 0;
        } else if ("4".equals(String.valueOf(safetyDataSendLogDO.getSendState()))) {
            result = sendDataRecord(safetyVisitorHairpinDO, "2" , "", "门禁", true, safetyVisitorHairpinDO.getHairpinNo(), safetySysUserDO.getFactoryId());
            state = 5;
        }
        if (StringUtils.isNotEmpty(result)) {
            safetyDataSendLogDO.setSendState(state);
            safetyDataSendLogDOMapper.updateByPrimaryKey(safetyDataSendLogDO);
        }
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("数据下发日志");
        log.setAbility("重新下发");
        String msg = "[访客流程通过]:";
        if (StringUtils.isNotEmpty(safetyVisitorHairpinDO.getIdCardPhoto())) {
            msg = "[访客上传身份证(" + safetyVisitorHairpinDO.getIdCardPhoto() + ")]:";
        }
        log.setMessage(msg + safetyVisitorHairpinDO.getVisitorName()+" "+DateUtils.format(safetyVisitorHairpinDO.getStartTime(), 2)+" "+DateUtils.format(safetyVisitorHairpinDO.getEndTime(), 2));
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 退卡操作
     * @param safetyVisitorHairpinDO
     */
        public void hairpinNoBack(SafetyVisitorHairpinDO safetyVisitorHairpinDO) {
            //组装数据下发日志
            String[] gids = {"8A0ADE28-BB77-46B6-AF94-3FC1F552973A","FF7F139D-B80C-46D0-B330-1118F00255B7","35EC9884-6C33-4196-A195-22856503C7B9"};
            String redate = DateUtils.format(new Date(), 2);
            String returnValue = sendPost("http://10.66.32.54:60009/api/Staff/GetByFunc", "\"staffno='" + safetyVisitorHairpinDO.getVisitorPhone() + "' \"");
            LogUtils.info("退卡获取gid：{}", returnValue);
            if (StringUtils.isNotEmpty(returnValue)) {
                //先根据员工号查询到gid
                JSONObject jsonObject = JSONObject.parseObject(returnValue);
                Boolean aBoolean = jsonObject.getJSONObject("State").getBoolean("IsSucess");
                if (aBoolean) {
                    JSONObject models = jsonObject.getJSONObject("Model");
                    if (models.containsKey("Reserve2")) {
                        //获取到卡号
                        String Reserve2 = models.getString("Reserve2");
                        if (StringUtils.isNotEmpty(Reserve2)) {
                            //循环退人脸
                            for (String s : gids) {
                                String param = cardParam.replace("$Token", Reserve2).replaceAll("&Gid", s).replace("$Redate",redate);
                                LogUtils.info("退卡获取传参：{}", param);
                                String rest = sendPost("http://10.66.32.54:60009/api/TokenService/Unregister", param);
                                LogUtils.info("退卡结果：{}", rest);
                                if(StringUtils.isEmpty(rest)){
                                    LogUtils.warn("需要注销的人脸卡失败：{id:{},hairpinNo:{}}",safetyVisitorHairpinDO.getId(), safetyVisitorHairpinDO.getHairpinNo());
                                }
                                JSONObject od = JSONObject.parseObject(rest);
                                if (od.containsKey("IsSucess") && od.getBoolean("IsSucess")){
                                    LogUtils.info("需要注销的人脸卡成功：{id:{},hairpinNo:{}}",safetyVisitorHairpinDO.getId(), safetyVisitorHairpinDO.getHairpinNo());
                                } else {
                                    LogUtils.warn("需要注销的人脸卡失败：{id:{},hairpinNo:{}}",safetyVisitorHairpinDO.getId(), safetyVisitorHairpinDO.getHairpinNo());
                                }
                            }
                        }
                    }
                }
            }
            //注销卡
            if(!StringUtils.isEmpty(safetyVisitorHairpinDO.getHairpinNo())){
                for (String sk : gids) {
                    String params = cardParam.replace("$Token", safetyVisitorHairpinDO.getHairpinNo()).replaceAll("&Gid", sk).replace("$Redate",redate);
                    LogUtils.info("退卡获取传参：{}", params);
                    String rest = sendPost("http://10.66.32.54:60009/api/TokenService/Unregister", params);
                    LogUtils.info("退卡结果：{}", rest);
                    if (StringUtils.isNotEmpty(rest)) {
                        JSONObject od = JSONObject.parseObject(rest);
                        if (od.getBoolean("IsSucess")){
                            LogUtils.info("需要注销的卡成功：{id:{},hairpinNo:{}}",safetyVisitorHairpinDO.getId(), safetyVisitorHairpinDO.getHairpinNo());
                        } else {
                            LogUtils.warn("需要注销的卡失败：{id:{},hairpinNo:{}}",safetyVisitorHairpinDO.getId(), safetyVisitorHairpinDO.getHairpinNo());
                        }
                    }
                }
            }
        }

    public void test() {
        String empNo = "40C1A77B";
        //先根据员工号查询到gid
        //String returnValue = sendPost("http://10.66.32.54:60009/api/IssueDoorView/GetByfunc", "\"MobileNo='" + empNo + "'\"");
        String returnValue = sendPost("http://10.66.32.54:60009/api/IssueDoorView/GetByfunc", "\"TokenId='" + empNo + "' and MobileNo='15517809131' \"");
        JSONObject jsonObject = JSONObject.parseObject(returnValue);
        System.out.println("jsonObject:"+jsonObject);
    }

    /**
     * 发起POST请求
     *
     * @param url
     *            url
     * @param paramJson
     *            参数的json格式
     */
    public static String sendPost(String url, String paramJson) {
        String result = null;
                // 创建httpClient实例对象
        HttpClient httpClient = new HttpClient();
        // 设置httpClient连接主机服务器超时时间：15000毫秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        // 创建post请求方法实例对象
        PostMethod postMethod = new PostMethod(url);
        // 设置post请求超时时间
        postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        postMethod.addRequestHeader("Content-Type", "application/json");
        try {
            // json格式的参数解析
            RequestEntity entity = new StringRequestEntity(paramJson, "application/json", "UTF-8");
            postMethod.setRequestEntity(entity);

            int statusCode = httpClient.executeMethod(postMethod);
            result = postMethod.getResponseBodyAsString();
            if (statusCode == HttpStatus.SC_OK) {
                postMethod.releaseConnection();
                return result;
            } else {
                LogUtils.error("POST请求发出失败，请求的地址为{}，参数为{}，错误信息为{}", url, paramJson, "HTTP状态码: " + statusCode);
            }
            return result;
        } catch (IOException e) {
            LogUtils.error("POST请求发出失败，请求的地址为{}，参数为{}，错误信息为{}",url,paramJson, e.getMessage());
        }finally {
            postMethod.releaseConnection();
        }
        return result;
    }
}