package com.yutong.safety.core.service.enter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.HikMiddleTableSqlEnum;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.utils.excel.OutXSSFUtil;
import com.yutong.safety.common.utils.excel.PubExcelUtil;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.web.HkMiddleTableVO;
import com.yutong.safety.core.db.entity.enter.EnterFactoryRecord;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingPer;
import com.yutong.safety.core.db.entity.enter.SafetyEnterRegister;
import com.yutong.safety.core.db.entity.enter.excel.SafetyEnterRegisterEx;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterRegisterRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterRegisterSh;
import com.yutong.safety.core.db.mapper.enter.SafetyEnterRegisterMapper;
import com.yutong.safety.core.handler.EnvironSyncRegisterHandler;
import com.yutong.safety.core.handler.enter.SafetyEnterRegisterHandler;
import com.yutong.safety.core.service.SafetyLogMessageService;
import com.yutong.safety.core.service.hik.SafetyArtemisService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@Slf4j
public class SafetyEnterRegisterService implements SafetyEnterRegisterHandler {
    @Autowired
    private SafetyEnterRegisterMapper enterRegisterMapper;

    @Autowired
    private EnvironSyncRegisterHandler environSyncRegisterHandler;

    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    @Resource
    private SafetyArtemisService safetyArtemisService;

    @Value("${sendData.qrUrl}")
    private String sendDataQrUrl;

    @Override
    public PageVO selectEnterRegisterPage(SafetyEnterRegisterSh enterRegister) {
        List<SafetyEnterRegisterRt> list = enterRegisterMapper.selectEnterRegisterList(enterRegister);
        Integer count = enterRegisterMapper.selectEnterRegisterCount(enterRegister);
        return new PageVO(count, list);
    }

    @Override
    public SafetyEnterRegisterRt selectEnterRegisterByPhone(String phone, String userName) {
        SafetyEnterRegisterRt rt = enterRegisterMapper.selectEnterRegisterHas(phone, userName);
        if(rt == null){
            throw new BizException(ResponseEnum.FAIL.getCode(), "未查询到有效数据");
        }
        return rt;
    }

    @Override
    public SafetyEnterRegisterRt selectEnterRegisterByCard(String card) {
        SafetyEnterRegisterRt rt = enterRegisterMapper.selectEnterRegisterByCard(card);
        return rt;
    }

    @Override
    public void insertEnterRegister(List<EnterFactoryRecord> factoryRecords,String sessionKey) {
        if(CollectionUtils.isEmpty(factoryRecords)){
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        List<SafetyEnterRegister> records = new ArrayList<>();
        Date date = new Date();
        for (EnterFactoryRecord factoryRecord : factoryRecords) {
            // 校验是否已添加
            Integer has = enterRegisterMapper.checkEnterRegisterOld(factoryRecord);
            if (has != null && has > 0) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "提交失败，该手机号已经登记过了");
            }

            SafetyEnterRegister enterRegister = new SafetyEnterRegister();
            enterRegister.setCreateTime(date);
            enterRegister.setCreateUser(factoryRecord.getUserName());
            enterRegister.setUpdateTime(date);
            enterRegister.setUpdateUser(factoryRecord.getUserName());
            enterRegister.setFdName(factoryRecord.getUserName());
            enterRegister.setFdCardNo(factoryRecord.getCardId());
            enterRegister.setFdPhone(factoryRecord.getPhone());
            enterRegister.setFdImg(factoryRecord.getPhotographUrl());

            records.add(enterRegister);
        }
        enterRegisterMapper.insertEnterRegisterBatch(records);

        try{
            environSyncRegisterHandler.syncEnvironRegister(factoryRecords,sessionKey);
        }catch (Exception e){
            LogUtils.error("警告通知-人员登记同步environ异常",e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "警告通知-人员登记同步environ异常");
        }
    }

    @Override
    public void updateEnterRegister(SafetyEnterRegister enterRegister, String token) {
        if(StringUtils.isBlank(enterRegister.getFdName()) ||
                StringUtils.isBlank(enterRegister.getFdCardNo()) ||
                StringUtils.isBlank(enterRegister.getFdPhone())){
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        String tokenUser = TokenUtils.getRealName(token);
        Date date = new Date();
        enterRegister.setUpdateTime(date);
        enterRegister.setUpdateUser(tokenUser);
        enterRegisterMapper.updateEnterRegister(enterRegister);
    }

    @Override
    public void updateEnterRegisterProcess(List<SafetyEnterFilingPer> perList, String updateUser) {

    }

    @Override
    public void deleteEnterRegister(Integer id, String delReason,String token) {
        if(id == null){
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        Map<String,String> tokenUser = TokenUtils.getTockenUser(token);
        SafetyEnterRegisterRt rt = enterRegisterMapper.selectEnterRegisterById(id);
        String realName = tokenUser.get("realName");
        LogUtils.info("{} 删除已登记人员信息 {}", realName, JsonUtils.toString(rt));
        enterRegisterMapper.deleteEnterRegister(id);
        try{
            String delMsg = "姓名："+rt.getFdName()+";"+"手机号："+rt.getFdPhone()+"；身份证号："+rt.getFdCardNo()+"；删除原因："+delReason+"；照片链接："+rt.getFdImg();
            saveLogInfo("删除",delMsg,realName);
        }catch (Exception e){
            LogUtils.error(ResponseEnum.EXCEPTION.getCode(),"删除登记人信息日志保存异常");
            LogUtils.error("删除登记人信息日志保存异常",e);
        }
    }

    /**
     * 保存操作日志
     * @param ability 操作
     * @param message
     * @param realName
     */
    private void saveLogInfo(String ability, String message, String realName) {
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("人员信息登记表");
        log.setAbility(ability);
        log.setMessage(message);
        log.setOperator(realName);
        safetyLogMessageService.addLogMessage(log);
    }

    @Override
    public byte[] exportEnterRegister(SafetyEnterRegisterSh enterRegister) {
        byte[] content = null;
        //定义HSSFWorkbook对象
        XSSFWorkbook wb = null;
        //定义输出流
        ByteArrayOutputStream os = null;
        InputStream fis = null;
        try {
            //查询要导出的数据
            List<SafetyEnterRegisterEx> list = enterRegisterMapper.selectEnterRegisterExport(enterRegister);
            //获取模板
            ClassPathResource classPathResource = new ClassPathResource("excel/enterRegisterExport.xlsx");
            fis = classPathResource.getInputStream();
            wb = new XSSFWorkbook(fis);
            //写入Excel数据
            XSSFSheet sheet = wb.getSheetAt(0);
            //创建数据对象，用来保存list数据
            Object[][] values = new Object[list.size()][sheet.getRow(0).getPhysicalNumberOfCells()];
            for(int i=0; i<list.size(); i++) {
                SafetyEnterRegisterEx d = list.get(i);
                values[i][0] = d.getFdName();
                values[i][1] = d.getFdCardNo();
                values[i][2] = d.getFdPhone();
                values[i][3] = d.getFdImg();
                values[i][4] = d.getCreateTime();
                values[i][5] = d.getFdInFactory();
            }
            //写入数据
            OutXSSFUtil.getXSSFWorkbook(values, wb, sheet, 1, 11);
            os = new ByteArrayOutputStream();
            wb.write(os);
            content = os.toByteArray();
        }catch (Exception e){
            LogUtils.error("数据导出异常", e);
        }finally {
            StreamUtil.close(fis);
            StreamUtil.close(os);
            PubExcelUtil.close(wb);
        }
        return content;
    }

    @Override
    public String doorQrCode(String phoneNo, String idNo, String staffName, int[] nos, String crossType, String endTime, String crossData, String tcmid) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        com.alibaba.fastjson.JSONObject param3 = new com.alibaba.fastjson.JSONObject();
        String staffType ="0";
        param3.put("phone", phoneNo);
        param3.put("idno", idNo);
        param3.put("staffname", staffName);
        param3.put("devnos", nos);
        param3.put("crosstype", crossType);
        param3.put("stafftype", staffType);
        param3.put("endtime", endTime);
        LogUtils.info("调用生成二维码入参：" + param3.toJSONString());
        param3.put("crossdata", crossData);
        param3.put("tcmid", tcmid);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, param3.toJSONString());
        Request request = new Request.Builder()
                .url(sendDataQrUrl)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            String str = response.body().string();
            LogUtils.info("接口调用成功：" + str);
            JSONObject obj = JSON.parseObject(str);
            if(("1").equals(crossType)){
                if (obj.get("Describe") != null) {
                    String erCode = obj.get("Describe").toString();
                    LogUtils.info("生成二维码成功：" + StringUtils.isNotEmpty(erCode));
                    return erCode;
                }
            }else{
                if(obj.get("IsSucess") != null){
                    String IsSucess = obj.get("IsSucess").toString();
                    LogUtils.info("人脸下发日志成功：" + IsSucess);
                    return IsSucess;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.error("二维码接口调用失败",e);
        }
        return "false";
    }

    @Override
    public SafetyEnterRegisterRt selectEnterRegisterByPhone(String phone) {
        SafetyEnterRegisterRt rt = enterRegisterMapper.selectEnterRegisterByPhone(phone);
        if(rt == null){
            throw new BizException(ResponseEnum.FAIL.getCode(), "未查询到有效数据");
        }
        return rt;
    }

    public void updatePhone(SafetyEnterRegister enterRegister) {
        Date date = new Date();
        enterRegister.setUpdateTime(date);
        enterRegisterMapper.updateEnterRegister(enterRegister);
    }

    public List<SafetyEnterRegisterRt> selectInfoByPhones(List<String> phones) {
        if (CollectionUtils.isEmpty(phones)) {
            return Lists.newArrayList();
        }
        return enterRegisterMapper.selectEnterRegisterByPhoneList(phones);
    }

    public List<SafetyEnterRegisterRt> selectInfoByCardNos(List<String> cardNos) {
        if (CollectionUtils.isEmpty(cardNos)) {
            return Lists.newArrayList();
        }
        return enterRegisterMapper.selectEnterRegisterByCardNos(cardNos);
    }

    public SafetyEnterRegisterRt selectInfo(String phone) {
        return enterRegisterMapper.selectEnterRegisterByPhone(phone);
    }

    public void batchUpdate(List<SafetyEnterRegisterRt> registerRts){
        enterRegisterMapper.batchUpdate(registerRts);
    }

    public List<SafetyEnterRegisterRt> selectList(Date start, Date end) {
        return enterRegisterMapper.selectListByUpdateTime(start, end);
    }

    public void syncHikMiddleTableOutsider(List<SafetyEnterRegisterRt> list) {
        LogUtils.info("syncHikMiddleTableOutsider 开始同步----size={}", list.size());
        List<HkMiddleTableVO> tableVOList = new ArrayList<>();
        for (SafetyEnterRegisterRt li : list) {// 必填项
            if (isBlank(li.getFdPhone()) || isBlank(li.getFdName()) || isBlank(li.getFdImg())) {
                continue;
            }

            HkMiddleTableVO info = new HkMiddleTableVO();
            info.setOrgId("BA000003");
            info.setJobNo(li.getFdPhone());
            info.setCertType(990);
            info.setCertificateNo(li.getFdPhone());
            info.setName(li.getFdName());
            info.setSex(0);
            info.setStatus(0);
            info.setMobile(li.getFdPhone());
            Date date = DateUtils.parse(li.getCreateTime(), 1);
            info.setTimeStr(LocalDate.of(date.getYear(), date.getMonth(), date.getDay()));
            info.setPositionOrgId("");
            info.setFaceData(Base64ImageDecoder.imgBase64(li.getFdImg()));
            tableVOList.add(info);
        }
        LogUtils.info("syncHikMiddleTableInfo同步的数据----={}", tableVOList.size());
        if (!CollectionUtils.isEmpty(tableVOList)) {
            safetyArtemisService.syncHikMiddleTable(tableVOList, HikMiddleTableSqlEnum.TB_MIDDLE_PERSON);
        }
    }
}
