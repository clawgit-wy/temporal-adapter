package com.yutong.safety.core.handler.enter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.entity.enter.SafetyCrkCarOutHistoryDO;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterFilingReportRt;
import com.yutong.safety.core.db.entity.enter.result.SatefyEnterStatisticRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterFilingReportSh;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterStatisticSh;
import com.yutong.safety.core.service.SafetySysUserService;
import com.yutong.safety.core.service.enter.SafetyCrkCarHistoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SafetyCrkCarHistoryHandler implements Serializable {

    private static String TCM_NAME = "运输车辆";

    private static String SEARCH_ADMIN_ROLE_NAME = "物流车备案信息查询";

    @Autowired
    private SafetyCrkCarHistoryService safetyCrkCarHistoryService;

    @Autowired
    private SafetySysUserService safetySysUserService;

    public int updateBatch(String beginTime){
        List<SafetyCrkCarOutHistoryDO> historyDOList = new ArrayList<>();
        if(StringUtils.isEmpty(beginTime)){
            beginTime = DateUtils.format(DateUtils.addMinutes(new Date(),-30),2);
        }
        //获取历史出厂记录
        String outQueryWhere = "\"TcmName='"+TCM_NAME+"' and OutTime>'"+beginTime+"'\"";
        JSONArray outArray = safetyCrkCarHistoryService.getOutVehicleHistory(outQueryWhere);
        if(outArray.size() > 0){
            List<SafetyCrkCarOutHistoryDO> outHistoryDOS = formatCrkCarHistory(outArray);
            outHistoryDOS.stream().map(e->{
                String inTime=e.getInTime().replace("T"," ");
                e.setInTime(inTime);
                return e;
            }).collect(Collectors.toList());

            outHistoryDOS.stream().map(e->{
                String outTime=e.getOutTime().replace("T"," ");
                e.setOutTime(outTime);
                return e;
            }).collect(Collectors.toList());

            historyDOList.addAll(outHistoryDOS);
        }
        //获取出当前入场记录-在厂车进厂记录
        String inQueryWhere = "\"TcmName='"+TCM_NAME+"' and inTime>'"+beginTime.substring(0,10)+"'\"";
        JSONArray inArray = safetyCrkCarHistoryService.getInvehicleInfo(inQueryWhere);
        if(inArray.size() > 0){
            List<SafetyCrkCarOutHistoryDO> inHistoryDOS = formatCrkCarHistory(inArray);
            inHistoryDOS.stream().map(e->{
                String inTime=e.getInTime().replace("T"," ");
                e.setInTime(inTime);
                return e;
            }).collect(Collectors.toList());

            historyDOList.addAll(inHistoryDOS);
        }

        //批量插入记录表中
        if(CollectionUtils.isEmpty(historyDOList)){
            return 0;
        }
        return safetyCrkCarHistoryService.updateBatch(historyDOList);
    }

    /**
     * 整理数据
     * @param inArray
     * @return
    private List<SafetyCrkCarOutHistoryDO> formatCrkCarHistory(JSONArray inArray) {
        try {
            return inArray.stream()
                    .map(obj -> {
                        JSONObject jsonObject = (JSONObject) obj;
                        // 假设 jsonObject 包含必要的字段
                        SafetyCrkCarOutHistoryDO historyDO = JSONObject.parseObject(jsonObject.toString(), SafetyCrkCarOutHistoryDO.class);
                        historyDO.setCrkId(jsonObject.getInteger("ID"));
                        String inTime = historyDO.getInTime().replace("T", " ");
                        historyDO.setInTime(inTime);
                        return historyDO;
                    })
                    .collect(Collectors.toList());
        }catch (Exception e){
            LogUtils.error("车辆信息格式化失败，入参{}",inArray.toJSONString());
        }
        return new ArrayList<>();
    }*/

    /**
     * 整理数据
     * @param jsonArray
     * @return
     */
    private List<SafetyCrkCarOutHistoryDO> formatCrkCarHistory(JSONArray jsonArray){
        List<SafetyCrkCarOutHistoryDO> carOutHistoryDOS = new ArrayList<>();
        for(int i= 0;i<jsonArray.size();i++){
            JSONObject curObj = jsonArray.getJSONObject(i);
            SafetyCrkCarOutHistoryDO historyDO = JSONObject.parseObject(curObj.toJSONString(),SafetyCrkCarOutHistoryDO.class);
            historyDO.setCrkId(curObj.getIntValue("ID"));
            carOutHistoryDOS.add(historyDO);
        }
        return carOutHistoryDOS;
    }

    /**
     * 数据导出操作
     * @param reportSh 检索条件
     * @return 附件
     */
    public HSSFWorkbook reportExport(SafetyEnterFilingReportSh reportSh, String token) {
        String isAdmin = checkAdmin(token);
        if(!"T".equals(isAdmin)){
            if(checkSearchAdmin(token,SEARCH_ADMIN_ROLE_NAME)){
                isAdmin = "T";
            }
        }
        reportSh.setIsAdmin(isAdmin);
        reportSh.setPageFlag("1"); //标识不分页
        List<SafetyEnterFilingReportRt> list = safetyCrkCarHistoryService.reportList(reportSh);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"车牌号","备案厂区", "备案时间-始", "备案时间-终", "姓名","手机号","厂区"
                ,"入厂大门","入厂时间","入厂识别照片","出厂大门","出厂时间","出厂识别照片","停车时长","备案期内进厂次数"
                ,"供应商名称","运输货物名称","车主姓名/运输公司名称","接口人员工号","接口人姓名","接口人部门"};
        String[][] values = new String[list == null ? 0 : list.size()][];
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                SafetyEnterFilingReportRt reportRt = list.get(i);
                values[i] = new String[title.length];
                values[i][0] = reportRt.getFdCarNo(); //车牌号
                values[i][1] = reportRt.getFdFilingFactory();//备案厂区
                values[i][2] = reportRt.getFdFilingBegin();//备案时间-始
                values[i][3] = reportRt.getFdFilingEnd();//备案时间-终
                values[i][4] = reportRt.getFdPerName();//姓名
                values[i][5] = reportRt.getFdPerPhone();//手机号
                values[i][6] = reportRt.getInFactory();  //厂区
                values[i][7] = reportRt.getInOperatorName();//入厂大门
                values[i][8] = reportRt.getInTime();//入厂时间
                values[i][9] = reportRt.getInPicture();//入厂识别照片
                values[i][10] = reportRt.getOutOperatorName();//出厂大门
                values[i][11] = reportRt.getOutTime();//出厂时间
                values[i][12] = reportRt.getOutPicture();//出厂识别照片
                values[i][13] = reportRt.getStayLasts();//停车时长
                values[i][14] = reportRt.getEnterNum() == null ? "0" : reportRt.getEnterNum().intValue() + "";//备案期内进厂次数
                values[i][15] = reportRt.getFdSupplierName();//供应商名称
                values[i][16] = reportRt.getFdGoodsName();//运输货物名称
                values[i][17] = reportRt.getFdDriverName();//车主姓名/运输公司名称
                values[i][18] = reportRt.getApproveCode();//接口人员工号
                values[i][19] = reportRt.getApproveName();//接口人姓名
                values[i][20] = reportRt.getApproveDepart();//接口人部门
            }
        }
        String sheetName = "备案物流车进厂分析";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 分页
     * @param reportSh 入参
     * @param token token
     * @return 返回值
     */
    public PageVO reportPageList(SafetyEnterFilingReportSh reportSh, String token){
        String isAdmin = checkAdmin(token);
        if(!"T".equals(isAdmin)){
            if(checkSearchAdmin(token,SEARCH_ADMIN_ROLE_NAME)){
                isAdmin = "T";
            }
        }
        reportSh.setIsAdmin(isAdmin);
        return safetyCrkCarHistoryService.reportPageList(reportSh);
    }


    /**
     * 数据统计
     * @param statisticSh 入参
     * @param token token
     * @return 返回值
     */
    public SatefyEnterStatisticRt enterStatistic(SafetyEnterStatisticSh statisticSh,
                                                 String token){
        String isAdmin = checkAdmin(token);
        if(!"T".equals(isAdmin)){
            if(checkSearchAdmin(token,SEARCH_ADMIN_ROLE_NAME)){
                isAdmin = "T";
            }
        }
        statisticSh.setIsAdmin(isAdmin);

        String endTime = statisticSh.getFdFilingEnd();
        if(StringUtils.isNotBlank(endTime)){
            statisticSh.setFdFilingEnd(DateUtils.format(DateUtils.addDays(DateUtils.parse(endTime,1),1),1));
        }
        SatefyEnterStatisticRt statisticRt = safetyCrkCarHistoryService.selectEnterCarNum(statisticSh);
        if(statisticRt == null){
            statisticRt = new SatefyEnterStatisticRt();
            statisticRt.setEnterCarNum(0);
            statisticRt.setEnterTotalNum(0);
        }
        int processNum = safetyCrkCarHistoryService.selectFilingNum(statisticSh);
        statisticRt.setProcessNum(processNum);
        return statisticRt;
    }

    private String checkAdmin(String token){
        String userCode = TokenUtils.getWorkNo(token);
        String userName = TokenUtils.getUserName(token);
        UserMessageListParam param = new UserMessageListParam();
        param.setRoleName("超级管理员");
        List<SafetySysUserDO> list = safetySysUserService.getList(param);
        for (SafetySysUserDO userDO:list){
            if (userDO.getUserName().equals(userName)){
                return "T";
            }
        }
        return userCode;
    }

    /**
     * 校验是否查询管理员权限
     * @param token 当前token
     * @param roleName 查询管理员角色名称
     * @return 是否
     */
    private boolean checkSearchAdmin(String token,String roleName){
        String userName = TokenUtils.getUserName(token);
        UserMessageListParam param = new UserMessageListParam();
        param.setRoleName(roleName);
        param.setUserName(userName);
        List<SafetySysUserDO> list = safetySysUserService.getList(param);
        if(CollectionUtils.isEmpty(list)){
            return false;
        }
        return true;
    }
}
