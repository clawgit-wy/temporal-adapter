package com.yutong.safety.core.handler;

import com.google.common.base.Joiner;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.ProcessParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDOExample;
import com.yutong.safety.core.db.entity.SafetyLogMessageDO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.dto.SafetyMonitorPostProcessDTO;
import com.yutong.safety.core.handler.spot.SafetyPatrolRecordHandler;
import com.yutong.safety.core.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lukunpeng
 * @time 2024-4-30
 *
 * 监控岗位上报流程handler
 */
@Component
public class SafetyMonitorPostHandler {

    @Autowired
    SafetyBpmProcessService safetyBpmProcessService;

    @Autowired
    SafetyMonitorPostService safetyMonitorPostService;

    @Autowired
    SafetyWaterFlowNoService safetyWaterFlowNoService;

    @Autowired
    SafetySysUserService safetySysUserService;

    @Autowired
    SafetyPatrolRecordHandler safetyPatrolRecordHandler;

    @Autowired
    private SafetyEmployeeMessageService safetyEmployeeMessageService;

    public Object saveAndCerateProcess(SafetyMonitorPostProcessDTO dto, String tocken) {
        String flowId = safetyBpmProcessService.createAndApproveProcess(dto.getProcessParam().getSubject(),  dto.getProcessParam().getModelId(), dto.getProcessParam().getFormId(), tocken);
        String flowNo = getFlowNo();
        dto.setFlowNo(flowNo);
        dto.setFlowId(flowId.split(",")[1]);
        dto.setStatus(20);
        String workNo = dto.getAbnormalCreatorNo();
        if(StringUtils.isNotBlank(workNo)){
            String eipName = safetyEmployeeMessageService.getLoginNameByNo(workNo);
            dto.setAbnormalCreatorEip(eipName);
        }
        safetyMonitorPostService.addData(dto);
        return flowId;
    }

    public Object updateAndAudit(SafetyMonitorPostProcessDTO dto, String tocken) {
        String status = safetyBpmProcessService.approveProcess(dto.getId()+"", dto.getProcessParam().getFlowId(), dto.getProcessParam().getModelId(), dto.getProcessParam().getFormId(), dto.getProcessParam().getFdParameterJson(), tocken);
        //if ("T".equals(status)){
            safetyMonitorPostService.insertCheckor(dto);
            int update = safetyMonitorPostService.updateData(dto);
           // return update;
        //}
        return status.substring(0,1);
    }

    private String getFlowNo(){
        String date = DateUtils.format(new Date(),7);
        String nowNum = safetyWaterFlowNoService.getNowByType("monitor_post");
        String flowNo = Joiner.on("-").join("JKYZ",date+nowNum);
        return flowNo;
    }

    public SafetyMonitorPostProcessDTO getDetail(int id) {
        return safetyMonitorPostService.getDetail(id);
    }

    public Object getList(SafetyMonitorPostProcessDTO processDTO, String tocken) {
        String workNo = TokenUtils.getWorkNo(tocken);
        LogUtils.info("打印token了！！！token="+tocken+",workNo="+workNo);
        if(StringUtils.isNotBlank(workNo)){
            SafetySysUserDO userMessage = safetySysUserService.getUserMessage(workNo);
            if(!ObjectUtils.isEmpty(userMessage)){
                String factoryId = userMessage.getPowerFactoryId();
                if(processDTO.getFactory() == null){
                    processDTO.setFactory(factoryId);
                }
            }
        }

        //查询流程前缀
        String bpmUrlPre = safetyPatrolRecordHandler.getBpmPreUrl("safety_process_patrol");
        processDTO.setBpmUrlPre(bpmUrlPre);

        List<SafetyMonitorPostProcessDTO> list = safetyMonitorPostService.getList(processDTO);
        int total = safetyMonitorPostService.getCount(processDTO);
        return new PageVO<>(total, list);
    }

    public HSSFWorkbook exportData(SafetyMonitorPostProcessDTO processDTO, String tocken) {
        String workNo = TokenUtils.getWorkNo(tocken);
        LogUtils.info("打印token了！！！token="+tocken+",workNo="+workNo);
        if(StringUtils.isNotBlank(workNo)){
            SafetySysUserDO userMessage = safetySysUserService.getUserMessage(workNo);
            if(!ObjectUtils.isEmpty(userMessage)){
                String factoryId = userMessage.getPowerFactoryId();
                if(processDTO.getFactory() == null){
                    processDTO.setFactory(factoryId);
                }
            }
        }
        List<SafetyMonitorPostProcessDTO> list = safetyMonitorPostService.exportData(processDTO);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"事件编号","厂区","位置/岗位","验证时间","异常事件类型","异常事件描述","异常事件接收人","异常事件接收人部门班组","验证对象","验证对象部门班组","是否异常","是否考核兑现","整改处理结果","镜头名称","方式途径","验证人姓名","验证人部门班组","登记时间","处理时间","备注"};
        String[][] values = new String[list.size()][];
        SafetyMonitorPostProcessDTO safetyMonitorPostProcessDTO;
        for(int i=0; i<list.size(); i++) {
            safetyMonitorPostProcessDTO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = safetyMonitorPostProcessDTO.getFlowNo();
            values[i][1] = safetyMonitorPostProcessDTO.getFactory();
            values[i][2] = safetyMonitorPostProcessDTO.getPositionPost();
            values[i][3] = safetyMonitorPostProcessDTO.getCheckTimeStart();
            values[i][4] = safetyMonitorPostProcessDTO.getAbnormalType();
            values[i][5] = safetyMonitorPostProcessDTO.getAbnormalDesc();
            values[i][6] = safetyMonitorPostProcessDTO.getAbnormalCreator();
            values[i][7] = safetyMonitorPostProcessDTO.getAbnormalDept();
            if(safetyMonitorPostProcessDTO.getCheckName() != null){
                values[i][8] = Joiner.on("-").join(safetyMonitorPostProcessDTO.getCheckName(),safetyMonitorPostProcessDTO.getCheckUserno());
            }
            values[i][9] = safetyMonitorPostProcessDTO.getCheckDept();
            values[i][10] = safetyMonitorPostProcessDTO.getCheckException();
            values[i][11] = safetyMonitorPostProcessDTO.getExamFulfill();
            values[i][12] = safetyMonitorPostProcessDTO.getImproveResult();
            values[i][13] = safetyMonitorPostProcessDTO.getLensName();
            values[i][14] = safetyMonitorPostProcessDTO.getWaysMeans();
            values[i][15] = safetyMonitorPostProcessDTO.getSubmitName();
            values[i][16] = safetyMonitorPostProcessDTO.getSubmitDept();
            values[i][17] = DateUtils.format(safetyMonitorPostProcessDTO.getCreateTime(), 2);
            if(safetyMonitorPostProcessDTO.getPublishTime() != null){
                values[i][18] = DateUtils.format(safetyMonitorPostProcessDTO.getPublishTime(), 2);
            }
            values[i][19] = safetyMonitorPostProcessDTO.getRemark();
        }
        String sheetName = "监控岗位验证结果列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

}
