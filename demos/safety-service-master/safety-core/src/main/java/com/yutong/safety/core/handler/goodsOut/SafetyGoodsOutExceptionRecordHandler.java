package com.yutong.safety.core.handler.goodsOut;

import com.alibaba.excel.EasyExcel;
import com.yutong.safety.common.easyExcel.ColumnHeightAuto;
import com.yutong.safety.common.easyExcel.ColumnWidthAuto;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.StreamUtil;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.utils.excel.OutXSSFUtil;
import com.yutong.safety.common.utils.excel.PubExcelUtil;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterProtectRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterProtectSh;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailReceiveDO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutExceptionRecordsDO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutProcessDO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutProcessDOExample;
import com.yutong.safety.core.db.entity.spot.SafetyValidateEnvironmentDO;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsOutExceptionRequestDTO;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsOutExceptionResponseDTO;
import com.yutong.safety.core.service.SafetyBpmWebServiceService;
import com.yutong.safety.core.service.SafetySysUserService;
import com.yutong.safety.core.service.goodsOut.SafetyGoodsOutEventsProcessService;
import com.yutong.safety.core.service.goodsOut.SafetyGoodsOutExceptionRecordService;
import com.yutong.safety.core.service.goodsOut.SafetyGoodsOutProcessService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SafetyGoodsOutExceptionRecordHandler {
    @Autowired
    SafetyGoodsOutExceptionRecordService safetyGoodsOutExceptionRecordService;

    @Autowired
    SafetyGoodsOutProcessService safetyGoodsOutProcessService;

    @Autowired
    private SafetyBpmWebServiceService safetyBpmWebServiceService;

    @Autowired
    private SafetySysUserService safetySysUserService;

    /**
     * 拼接bpm链接
     * @param formId bpm的formId
     * @return 返回值
     */
    public String getBpmPreUrl(String formId){
        Map<String,String> bpmModel = safetyBpmWebServiceService.findBpmModelByFormId(formId);
        if(bpmModel != null){
            return bpmModel.get("form_url")+"?sysId=SAFETY&modelId="+bpmModel.get("model_id")+"&templateFormId="+bpmModel.get("form_id");
        }
        return "";
    }

    public PageVO queryForList(SafetyGoodsOutExceptionRequestDTO en,String token){
        //出门证异常上报事件的流程前缀
        String goodsOutBpmUrlPre = getBpmPreUrl("safety_goods_out_process");
        en.setBpmUrlPre(goodsOutBpmUrlPre);
        //此处只是为了不在mybatis中转换所以加1天
        if(!ObjectUtils.isEmpty(en.getSubmissionEndTime())){
            en.setSubmissionEndTime(DateUtils.addDays(en.getSubmissionEndTime(), 1));
        }
        //过滤出厂厂区权限
        String workNo = TokenUtils.getWorkNo(token);
        if(StringUtils.isNotBlank(workNo) && workNo.startsWith("01")){
            workNo = safetySysUserService.employeeConvertWorkNo(workNo);
        }
        String userName = TokenUtils.getUserName(token);
        boolean filterFlag = safetySysUserService.checkRoleByUserName(userName,"超级管理员");
        if(!filterFlag){
            String factoryId = safetySysUserService.getPowerFactory(workNo);
            en.setOutFactoryIds(factoryId);
            en.setCurPersonLogin(userName);
        }
        List<SafetyGoodsOutExceptionResponseDTO> list = safetyGoodsOutExceptionRecordService.pageList(en);
        long total = safetyGoodsOutExceptionRecordService.pageListCount(en);
        return new PageVO<>(total, list);
    }

    public void updateExceptionInfo(SafetyGoodsOutExceptionRecordsDO en){
        safetyGoodsOutExceptionRecordService.updateExceptionInfo(en);
    }

    public void add(SafetyGoodsOutExceptionRecordsDO en,String token){
        safetyGoodsOutExceptionRecordService.add(en,token);
    }

    public void exportExcel(SafetyGoodsOutExceptionRequestDTO en, OutputStream outputStream
            ,String token){
        en.setPageSize(5000);
        en.setPageNum(1);
        if(!ObjectUtils.isEmpty(en.getSubmissionEndTime())){
            en.setSubmissionEndTime(DateUtils.addDays(en.getSubmissionEndTime(), 1));
        }
        //过滤出厂厂区权限
        String workNo = TokenUtils.getWorkNo(token);
        if(StringUtils.isNotBlank(workNo) && workNo.startsWith("01")){
            workNo = safetySysUserService.employeeConvertWorkNo(workNo);
        }
        String userName = TokenUtils.getUserName(token);
        boolean filterFlag = safetySysUserService.checkRoleByUserName(userName,"超级管理员");
        if(!filterFlag){
            String factoryId = safetySysUserService.getPowerFactory(workNo);
            en.setOutFactoryIds(factoryId);
            en.setCurPersonLogin(userName);
        }

        List<SafetyGoodsOutExceptionResponseDTO> list = safetyGoodsOutExceptionRecordService.pageList(en);

        EasyExcel.write(outputStream, SafetyGoodsOutExceptionResponseDTO.class)
                .registerWriteHandler(new ColumnWidthAuto())
                .sheet("Sheet1")
                .doWrite(list);
    }

    public void createExceptionData(){
        //流程超时
        createTimeOutExcption();
        //超时未接收
        createUnReceiveException();
        //超时未出厂
        createNotOutException();
    }

    /**
     * 流程超时
     */
    private void createTimeOutExcption(){
        //获取当前时间的前3天
        Date daysAgo = DateUtils.getDaysAgo(3);
        Date parse = DateUtils.parse(DateUtils.format(daysAgo, 1), 2);

        //查询在parse之前创建的还未审批结束的流程
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("status","20");
        paramMap.put("beginDate",parse);
        List<SafetyGoodsOutProcessDO> list = safetyGoodsOutProcessService.getListByParamMap(paramMap);

        if(CollectionUtils.isEmpty(list)){return;}

        List<SafetyGoodsOutExceptionRecordsDO> enList = new ArrayList<>();
        list.forEach(en -> {
            SafetyGoodsOutExceptionRecordsDO entity = new SafetyGoodsOutExceptionRecordsDO();
            entity.setExitPassId(en.getId());
            entity.setExceptionType("流程超时");
            entity.setCurNodeId(en.getCurNodeId());
            entity.setCurNodeName(en.getCurNodeName());
            enList.add(entity);
        });

        safetyGoodsOutExceptionRecordService.saveBatch(enList);
    }

    /**
     * 超时未接收
     */
    private void createUnReceiveException(){
        //查询超时未接收的人员
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("status","20");
        paramMap.put("curNodeId","N75");
        paramMap.put("type","receive");
        List<SafetyGoodsOutDetailReceiveDO> list = safetyGoodsOutProcessService.getUnReceiveList(paramMap);

        if(CollectionUtils.isEmpty(list)){return;}

        List<SafetyGoodsOutExceptionRecordsDO> enList = new ArrayList<>();
        Map<String, String> map = list.stream()
                .collect(Collectors.toMap(
                        SafetyGoodsOutDetailReceiveDO::getProcessId,
                        SafetyGoodsOutDetailReceiveDO::getReceiveEmp,
                        (existing, replacement) -> existing + "," + replacement
                ));

        for (Map.Entry<String, String> entry : map.entrySet()) {
            SafetyGoodsOutExceptionRecordsDO entity = new SafetyGoodsOutExceptionRecordsDO();
            entity.setExitPassId(entry.getKey());
            entity.setExceptionType("超时未接收");
            entity.setCurNodeId("N75");
            entity.setCurNodeName("接收人接收确认");
            entity.setOperatorName(entry.getValue());
            enList.add(entity);
        }
        safetyGoodsOutExceptionRecordService.saveBatch(enList);
    }

    /**
     * 超时未出厂
     */
    private void createNotOutException(){
        //查询超时未接收的人员
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("status","20");
        paramMap.put("curNodeId","N7");
        paramMap.put("isSetAccept","是");
        paramMap.put("type","outFactory");

        List<SafetyGoodsOutDetailReceiveDO> list = safetyGoodsOutProcessService.getUnReceiveList(paramMap);

        if(CollectionUtils.isEmpty(list)){return;}

        List<SafetyGoodsOutExceptionRecordsDO> enList = new ArrayList<>();
        Map<String, String> map = list.stream()
                .collect(Collectors.toMap(
                        SafetyGoodsOutDetailReceiveDO::getProcessId,
                        SafetyGoodsOutDetailReceiveDO::getReceiveEmp,
                        (existing, replacement) -> existing + "," + replacement
                ));

        for (Map.Entry<String, String> entry : map.entrySet()) {
            SafetyGoodsOutExceptionRecordsDO entity = new SafetyGoodsOutExceptionRecordsDO();
            entity.setExitPassId(entry.getKey());
            entity.setExceptionType("超时未出厂");
            entity.setCurNodeId("N7");
            entity.setCurNodeName("门卫放行");
            entity.setOperatorName(entry.getValue());
            enList.add(entity);
        }
        safetyGoodsOutExceptionRecordService.saveBatch(enList);
    }
}
