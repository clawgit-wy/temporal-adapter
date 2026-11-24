package com.yutong.safety.core.handler.spot;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.constants.SpotCheckEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.spot.SpotCheckRecordListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.app.SpotCheckPlanRetVO;
import com.yutong.safety.core.dto.SpotCheckRecordDTO;
import com.yutong.safety.core.dto.SpotCheckRecordItemDTO;
import com.yutong.safety.core.service.SafetyBpmWebServiceService;
import com.yutong.safety.core.service.SafetySysUserService;
import com.yutong.safety.core.service.spot.SafetySpotCheckRecordService;
import com.yutong.safety.core.service.spot.SafetySpotCheckTaskService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gwxiexj
 * @since 2024-05-15 11:42
 */
@Component
public class SafetySpotCheckRecordHandler {

    @Autowired
    private SafetySpotCheckRecordService safetySpotCheckRecordService;

    @Autowired
    private SafetySpotCheckTaskService safetySpotCheckTaskService;

    @Autowired
    private SafetySysUserService safetySysUserService;

    @Autowired
    private SafetyBpmWebServiceService safetyBpmWebServiceService;

    /**
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    public Object pageList(SpotCheckRecordListParam param, String token) {
        //获取token用户的工厂权限
        String workNo = TokenUtils.getWorkNo(token);
        String factoryIds = safetySysUserService.getPowerFactory(workNo);
        param.setFactoryIds(factoryIds);
        param.setPageFlag(true);
        String spotTimeEnd = param.getSpotEndDate();
        if(StringUtils.isNotBlank(spotTimeEnd)){
            param.setSpotEndDate(DateUtils.format(DateUtils.addDays(DateUtils.parse(spotTimeEnd,1),1),1));
        }
        String bpmUrlPre = getBpmPreUrl("safety_spot_check_process");
        param.setBpmUrlPre(bpmUrlPre);
        List<SpotCheckRecordDTO> list = safetySpotCheckRecordService.selectRecordHistory(param);
        long total = safetySpotCheckRecordService.selectRecordHistoryCount(param);
        return new PageVO<>(total, list);
    }

    /**
     * 数据导出操作
     * @param param 入参
     * @param token token
     * @return 返回值
     */
    public HSSFWorkbook export(SpotCheckRecordListParam param, String token) {
        param.setPageFlag(false);
        //加入流程链接前缀
        String bpmUrlPre = getBpmPreUrl("safety_spot_check_process");
        param.setBpmUrlPre(bpmUrlPre);
        String spotTimeEnd = param.getSpotEndDate();
        if(StringUtils.isNotBlank(spotTimeEnd)){
            param.setSpotEndDate(DateUtils.format(DateUtils.addDays(DateUtils.parse(spotTimeEnd,1),1),1));
        }
        long total = safetySpotCheckRecordService.selectRecordHistoryCount(param);
        if (total > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        List<SpotCheckRecordDTO> list = safetySpotCheckRecordService.selectRecordHistory(param);
        String[] title = new String[]{"厂区", "类型", "项目", "位置", "设备编号", "责任部门","点检时间", "设备状态","异常描述"
                , "设备照片","实际点检人","点检人员工号","点检人部门","预计整改完成时间","整改次数","整改状态","当场整改图片","整改责任人","整改验证结果"
                ,"验证时间","验证人","业务流链接"};
        String[][] values = new String[list.size()][];
        SpotCheckRecordDTO spotCheckRecordDTO;
        for(int i=0; i<list.size(); i++) {
            spotCheckRecordDTO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = spotCheckRecordDTO.getFactoryName();//厂区
            values[i][1] = spotCheckRecordDTO.getDeviceType();//类型
            values[i][2] = spotCheckRecordDTO.getTeamName();//项目
            values[i][3] = spotCheckRecordDTO.getLocation(); //位置
            values[i][4] = spotCheckRecordDTO.getDeviceCode();//设备编号
            values[i][5] = spotCheckRecordDTO.getDutyName();//责任部门
            values[i][6] = spotCheckRecordDTO.getCheckTimeStr(); //点检时间
            values[i][7] = SpotCheckEnum.DEVICE_STATUS_MAP.get(spotCheckRecordDTO.getDeviceStatus()); //设备状态
            values[i][8] = spotCheckRecordDTO.getExceptionDesc(); //异常描述
            values[i][9] = spotCheckRecordDTO.getCheckImg();//设备照片
            values[i][10] = spotCheckRecordDTO.getCheckEmp();
            values[i][11] = spotCheckRecordDTO.getCheckEmpNo();
            values[i][12] = spotCheckRecordDTO.getCheckOrgName();
            values[i][13] = spotCheckRecordDTO.getPlanReformDate();
            values[i][14] = spotCheckRecordDTO.getReformNum()==null?"":spotCheckRecordDTO.getReformNum()+"";
            values[i][15] = SpotCheckEnum.REFORM_STATUS_MAP.get(spotCheckRecordDTO.getIsReform());
            values[i][16] = spotCheckRecordDTO.getReformImg();
            values[i][17] = spotCheckRecordDTO.getReformEmp();
            values[i][18] = SpotCheckEnum.VALIDATE_STATUS_MAP.get(spotCheckRecordDTO.getValidateStatus());
            values[i][19] = spotCheckRecordDTO.getValidateTimeStr();
            values[i][20] = spotCheckRecordDTO.getValidateEmp();
            values[i][21] = spotCheckRecordDTO.getFlowUrl();
        }
        String sheetName = "设备设施点检-巡查日志";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 获取设备设施点检详情
     * @param param 查询入参
     * @return 返回
     */
    public List<SpotCheckRecordDTO> getTaskDetails(SpotCheckRecordListParam param) {
        param.setPageFlag(false);
        String bpmUrlPre = getBpmPreUrl("safety_spot_check_process");
        param.setBpmUrlPre(bpmUrlPre);
        //01开头的员工号转为60开头的新员工号
        String checkEmpNo = param.getPlanEmpNo();
        if(StringUtils.isNotBlank(checkEmpNo) && checkEmpNo.startsWith("01")){
            String newWorkNo = safetySpotCheckTaskService.employeeConvertWorkNo(checkEmpNo);
            param.setPlanEmpNo(newWorkNo);
        }
        return safetySpotCheckRecordService.selectRecordHistory(param);
    }


    /**
     * 获取设备设施点检详情
     * @param param 查询入参
     * @return 返回
     */
    public Object getRecordList(SpotCheckRecordListParam param) {
        List<SpotCheckRecordItemDTO> itemDTOList = new ArrayList();
        param.setPageFlag(false);
        String bpmUrlPre = getBpmPreUrl("safety_spot_check_process");
        param.setBpmUrlPre(bpmUrlPre);
        //01开头的员工号转为60开头的新员工号
        String checkEmpNo = param.getPlanEmpNo();
        if(StringUtils.isNotBlank(checkEmpNo) && checkEmpNo.startsWith("01")){
            String newWorkNo = safetySpotCheckTaskService.employeeConvertWorkNo(checkEmpNo);
            param.setPlanEmpNo(newWorkNo);
        }
        //01开头的员工号转为60开头的新员工号
        String spotEmpNo = param.getSpotEmpNo();
        if(StringUtils.isNotBlank(spotEmpNo) && spotEmpNo.startsWith("01")){
            String newWorkNo = safetySpotCheckTaskService.employeeConvertWorkNo(spotEmpNo);
            param.setSpotEmpNo(newWorkNo);
        }
        List<SpotCheckRecordDTO> dtoList = safetySpotCheckRecordService.selectRecordHistory(param);
        Map<String, List<SpotCheckRecordDTO>> itemMap = dtoList.stream().collect(Collectors.groupingBy(SpotCheckRecordDTO::getDeviceType));
        for(Map.Entry<String, List<SpotCheckRecordDTO>> entryUser : itemMap.entrySet()){
            SpotCheckRecordItemDTO itemDTO = new SpotCheckRecordItemDTO();
            itemDTO.setDeviceType(entryUser.getKey());
            itemDTO.setSubList(entryUser.getValue());
            itemDTOList.add(itemDTO);
        }
        return itemDTOList;
    }

    /**
     * 拼接bpm链接
     * @param formId
     * @return 返回值
     */
    public String getBpmPreUrl(String formId){
        Map<String,String> bpmModel = safetyBpmWebServiceService.findBpmModelByFormId(formId);
        if(bpmModel != null){
            return bpmModel.get("form_url")+"?sysId=SAFETY&modelId="+bpmModel.get("model_id")+"&templateFormId="+bpmModel.get("form_id");
        }
        return "";
    }

    /**
     * 校验设备的点检情况
     * @param param 入参
     * @return 返回值
     */
    public SpotCheckPlanRetVO checkDevicePower(SpotCheckRecordListParam param){
        String curDay = DateUtils.format(new Date(),1);
        param.setTaskBeginDate(curDay);
        param.setTaskEndDate(curDay);
        SpotCheckPlanRetVO retVO = new SpotCheckPlanRetVO();
        //01开头的员工号转为60开头的新员工号
        String checkEmpNo = param.getPlanEmpNo();
        if(StringUtils.isNotBlank(checkEmpNo) && checkEmpNo.startsWith("01")){
            String newWorkNo = safetySpotCheckTaskService.employeeConvertWorkNo(checkEmpNo);
            param.setPlanEmpNo(newWorkNo);
            return retVO;
        }
        String empNo =  param.getPlanEmpNo();
        param.setPageFlag(false);
        String bpmUrlPre = getBpmPreUrl("safety_spot_check_process");
        param.setBpmUrlPre(bpmUrlPre);
        List<SpotCheckRecordDTO> recordList = safetySpotCheckRecordService.selectRecordHistory(param);
        if(recordList == null || recordList.size() <= 0){
            retVO.setRetType(1);
            retVO.setData("员工‘"+empNo+"’无权限点检此设备！");
            return retVO;
        }
        String bpmUrl;
        List<SpotCheckRecordDTO> unCheckList = recordList.stream().filter((SpotCheckRecordDTO dto)->dto.getStatus() ==0).collect(Collectors.toList());
        if(unCheckList.size() <= 0){
            Optional<SpotCheckRecordDTO> max = recordList.stream().max(Comparator.comparing(SpotCheckRecordDTO::getCheckTimeStr));
            SpotCheckRecordDTO maxOne = max.get();
            bpmUrl = maxOne.getFlowUrl();
            if(!ObjectUtils.isEmpty(maxOne)){
                if(maxOne.getDeviceStatus() == 0 || maxOne.getValidateStatus() == 1 ||
                        (maxOne.getIsReform() == 1 && maxOne.getValidateStatus() == null)){
                    retVO.setRetType(3);
                    retVO.setData(bpmUrl);
                    return retVO;
                }
            }

            retVO.setRetType(2);
            retVO.setData(bpmUrl);
            return retVO;
        }
        Set<Integer> idSet = unCheckList.stream().map(SpotCheckRecordDTO::getId).collect(Collectors.toSet());
        retVO.setRetType(0);
        retVO.setData(StringUtils.join(idSet,","));
        return retVO;
    }

    public Object createEIPFlow(List<Integer> idList, String eip){
        return safetySpotCheckRecordService.createEIPFlow(idList,eip);
    }
}
