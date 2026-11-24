package com.yutong.safety.core.handler.spot;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.app.SpotTaskListAppParam;
import com.yutong.safety.common.param.web.spot.SpotCheckTaskListParam;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.app.SpotCheckToDoVO;
import com.yutong.safety.common.vo.web.SpotCheckTaskReportVO;
import com.yutong.safety.core.service.spot.SafetySpotCheckTaskService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import sun.swing.StringUIClientPropertyKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gwxiexj
 * @since 2024-05-13 16:32
 */
@Component
public class SafetySpotCheckTaskHandler {

    @Autowired
    private SafetySpotCheckTaskService safetySpotCheckTaskService;


    /**
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    public Object pageList(SpotCheckTaskListParam param, String token) throws Exception{
        param.setPageFlag(true);

        List<SpotCheckTaskReportVO> list = safetySpotCheckTaskService.selectTaskReport(param);
        long total = safetySpotCheckTaskService.selectTaskReportCount(param);
        safetySpotCheckTaskService.selectTaskReport(list);
        return new PageVO<>(total, list);
    }

    /**
     * 数据导出操作
     * @param param 入参
     * @param token token
     * @return 返回值
     */
    public HSSFWorkbook export(SpotCheckTaskListParam param, String token){
        param.setPageFlag(false);

        List<SpotCheckTaskReportVO> list = safetySpotCheckTaskService.selectTaskReport(param);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        safetySpotCheckTaskService.selectTaskReport(list);
        String[] title = new String[]{"厂区", "类型", "项目", "点检周期", "当期点检开始日期", "当期点检结束日期","期次", "项目设备数量","周期内已点检次数"
                , "周期内未点检次数","周期内点检完成率","设备点检覆盖率","点检延期数量","点检延期率","点检周期内异常数量","已整改数量","整改完成率"
                ,"整改延期数量","整改延期率","整改验证数量"};
        String[][] values = new String[list.size()][];
        SpotCheckTaskReportVO reportVO;
        for(int i=0; i<list.size(); i++) {
            reportVO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = reportVO.getFactoryName();
            values[i][1] = reportVO.getDeviceType();
            values[i][2] = reportVO.getTeamName();
            values[i][3] = reportVO.getCycleDesc();
            values[i][4] = reportVO.getTaskBeginDate();
            values[i][5] = reportVO.getTaskEndDate();
            values[i][6] = reportVO.getCurOrder()+"";
            values[i][7] = reportVO.getDeviceNum()+"";
            values[i][8] = reportVO.getCheckNum()+"";
            values[i][9] = reportVO.getUnCheckNum()+"";
            values[i][10] = reportVO.getCheckRate()+"%";
            values[i][11] = reportVO.getCheckRate()+"%";
            values[i][12] = reportVO.getSpotDelayNum()+"";
            values[i][13] = reportVO.getSpotDelayRate()+"%";
            values[i][14] = reportVO.getExceptionNum()+"";
            values[i][15] = reportVO.getReformNum()+"";
            values[i][16] = reportVO.getReformRate()+"";
            values[i][17] = reportVO.getReformDelayNum()+"";
            values[i][18] = reportVO.getReformDelayRate()+"%";
            values[i][19] = reportVO.getValidateNum()+"";
        }
        String sheetName = "设备设施点检-巡查日志";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     *
     * @param param 入参
     * @return 返回值
     * @throws Exception
     */
    public List<SpotCheckToDoVO> selectSpotToDo(SpotTaskListAppParam param){
        List<SpotCheckToDoVO> returnList = new ArrayList<>();
        //开始日期和结束日期相等时，判断为日统计
        String beginDate = param.getBeginDate();
        String endDate = param.getEndDate();
        if(StringUtils.isNotBlank(beginDate) && beginDate.equals(endDate)){
            param.setDayDate(beginDate);
            param.setBeginDate(null);
            param.setEndDate(null);
        }
        //01开头的员工号转为60开头的新员工号
        String checkEmpNo = param.getCheckEmpNo();
        if(StringUtils.isNotBlank(checkEmpNo) && checkEmpNo.startsWith("01")){
           String newWorkNo = safetySpotCheckTaskService.employeeConvertWorkNo(checkEmpNo);
           param.setCheckEmpNo(newWorkNo);
        }
        List<SpotCheckToDoVO> voList = safetySpotCheckTaskService.selectSpotToDo(param);
        if(!CollectionUtils.isEmpty(voList)){
            //分组
            Map<String, List<SpotCheckToDoVO>> groupByType = voList.stream().collect(Collectors.groupingBy(SpotCheckToDoVO::getTypeName));
            //遍历分组
            for (Map.Entry<String, List<SpotCheckToDoVO>> entryUser : groupByType.entrySet()) {
                SpotCheckToDoVO curVO = new SpotCheckToDoVO();
                String typeName = entryUser.getKey();
                List<SpotCheckToDoVO> todoList = entryUser.getValue();
                //获取集合中已点检总数量
                int totalCheckNum = todoList.stream().mapToInt(SpotCheckToDoVO::getCheckNum).sum();
                //获取集合中未点检总数量
                int totalUnCheckNum = todoList.stream().mapToInt(SpotCheckToDoVO::getUnCheckNum).sum();
                //计算当前点检率
                int checkRate = 0;
                if(totalCheckNum >0 && (totalCheckNum+totalUnCheckNum)>0){
                    checkRate = totalCheckNum*100/(totalCheckNum+totalUnCheckNum);
                }
                curVO.setCheckNum(totalCheckNum);
                curVO.setUnCheckNum(totalUnCheckNum);
                curVO.setCheckRate(checkRate+"%");

                curVO.setTypeName(typeName);
                curVO.setSubList(todoList);

                returnList.add(curVO);
            }
        }
        return returnList;
    }


    /**
     * 转换员工号
     * @param oldWorkNo 旧员工号
     * @return 返回值
     */
    public String employeeConvertWorkNo(String oldWorkNo) {
        //接收到员工号长度<8时，补齐8位，再转
        if(StringUtils.isNotBlank(oldWorkNo) && oldWorkNo.length()<8){
            oldWorkNo = String.format("%08d", Integer.parseInt(oldWorkNo));
        }
        return safetySpotCheckTaskService.employeeConvertWorkNo(oldWorkNo);
    }

}
