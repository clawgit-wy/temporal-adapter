package com.yutong.safety.core.handler.build;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.utils.BuildEncryptUtil;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.build.SafetyBuildInOutHistoryDO;
import com.yutong.safety.core.handler.SafetyOutsiderPromiseChangeHandler;
import com.yutong.safety.core.service.build.SafetyBuildInOutHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SafetyBuildInOutHistoryHandler {

    @Value("${build.getInOutHistory.url}")
    private String getInOutHistoryUrl;

    @Value("${build.encryption.key}")
    private String buildEncryKey;

    @Value("${build.signature.key}")
    private String buildSignKey;

    @Value("${build.appid}")
    private String buildAppID;

    @Value("${build.dpcode}")
    private String buildDpcode;

    @Value("${crk.accessRecord.url}")
    private String accessRecordUrl;

    @Resource
    private SafetyBuildInOutHistoryService safetyBuildInOutHistoryService;

    /**
     * 定时更新楼宇门禁设备
     */
    public int updateRecordTask(){
        int pageSize = 1000;
        int currentPage = 1;
        int totalPage = 1;
        int updateRow = 0;
        Long maxId = 0L;
        String endTime = DateUtils.format(new Date(),2);
        String beginTime = DateUtils.format(DateUtils.addHours(new Date(),-24),2);
        try {
            Long newMaxId = safetyBuildInOutHistoryService.getMaxHistoryId();
            maxId = newMaxId != null ? newMaxId : 0L;

            JSONObject retObj = sendBuildDevicePost(currentPage,pageSize,maxId,beginTime,endTime);
            if(retObj == null){
                return updateRow;
            }
            Integer count = retObj.getInteger("count");
            if(count == null || count == 0){
                return updateRow;
            }
            JSONArray dataArr = retObj.getJSONArray("data");
            List<SafetyBuildInOutHistoryDO> deviceDOS = formatResponse(dataArr);
            if(!CollectionUtils.isEmpty(deviceDOS)){
                updateRow = safetyBuildInOutHistoryService.batchUpdate(deviceDOS);
            }
            Integer totalCount = retObj.getInteger("totalcount");
            if(totalCount>pageSize){
                totalPage = (int) Math.ceil((double) totalCount / pageSize);
            }
        }catch (Exception e){
            LogUtils.error("调用接口异常",e);
        }
        for(int i=2;i<=totalPage-1;i++){
            try {
                JSONObject returnObj = sendBuildDevicePost(i, pageSize, maxId,beginTime,endTime);
                if (returnObj == null) {
                    continue;
                }
                Integer curCount = returnObj.getInteger("count");
                if (curCount == null || curCount == 0) {
                    continue;
                }
                JSONArray curDataArr = returnObj.getJSONArray("data");
                List<SafetyBuildInOutHistoryDO> curDeviceDOS = formatResponse(curDataArr);
                if(!CollectionUtils.isEmpty(curDeviceDOS)){
                    int curRow = safetyBuildInOutHistoryService.batchUpdate(curDeviceDOS);
                    updateRow += curRow;
                }
            }catch (Exception cure){
                LogUtils.error("调用接口异常",cure);
            }
        }
        // 富士
        updateRecordFsTasks();

        return updateRow;
    }

    /**
     * 富士人员进出信息
     */
    public void updateRecordFsTasks() {
        String endTime = DateUtils.format(new Date(), 2);
        String beginTime = DateUtils.format(DateUtils.addHours(new Date(), -24), 2);
        String coreQuery = String.format("OperateDate > '%s' and OperateDate < '%s'", beginTime, endTime);

        final int pageSize = 1000;
        try {
            int currentPage = 1;
            boolean hasNextPage = true;

            while (hasNextPage) {
                JSONObject request = new JSONObject();
                request.put("PageSize", pageSize);
                request.put("CurrentPage", currentPage);
                request.put("OrderBy", "ID");
                request.put("OrderType", true);
                request.put("where", coreQuery);
                request.put("Append", "");
                request.put("TotalCount", 1);

                String response = SafetyOutsiderPromiseChangeHandler.sendPost(accessRecordUrl, request.toJSONString());
                LogUtils.info("返回值=" + response);

                JSONObject responseObj = JSONObject.parseObject(response);
                int total = responseObj.getJSONObject("PageAttri").getInteger("TotalCount");
                if (total <= 0) {
                    hasNextPage = false;
                    continue;
                }

                JSONArray records = responseObj.getJSONArray("Records");
                if (!records.isEmpty()) {
                    formatResponseFs(records);
                }

                currentPage++;
                hasNextPage = currentPage <= getTotalPages(total, pageSize);
            }
        } catch (Exception e) {
            LogUtils.error("更新记录失败", e);
        }
    }

    private int getTotalPages(int total, int pageSize) {
        return (int) Math.ceil((double) total / pageSize);
    }
    /**
     * 调用楼宇门禁接口获取出入记录
     * @param buildPageNo 分页参数
     * @param buildPageSize 分页参数
     * @param maxId 当前最大记录ID
     * @return
     * @throws Exception
     */
    private JSONObject sendBuildDevicePost(int buildPageNo,int buildPageSize,Long maxId,String beginTime,String endTime) throws Exception{
        JSONObject firstData = new JSONObject();
        firstData.put("pageno",buildPageNo);
        firstData.put("pagesize",buildPageSize);
        firstData.put("ispage",true);
        if(maxId > 1){
            firstData.put("id",maxId);
        }else{
            firstData.put("startdate",beginTime);
            firstData.put("enddate",endTime);
        }

        String retStr = BuildEncryptUtil.sendBuildHttpPost(getInOutHistoryUrl, firstData.toJSONString(),
                buildEncryKey,buildSignKey,buildAppID,buildDpcode);
        if(ResponseEnum.FAIL.getName().equals(retStr)){
            return null;
        }
        return JSONObject.parseObject(retStr);
    }

    /**
     * 处理设备相关信息
     * @param jsonArray 设备结果集
     * @return 对象集合
     */
    private List<SafetyBuildInOutHistoryDO> formatResponse(JSONArray jsonArray){
        List<SafetyBuildInOutHistoryDO> recordDOS = new ArrayList<>();
        if(CollectionUtils.isEmpty(jsonArray)){
            return recordDOS;
        }
        String curTime = DateUtils.format(new Date(),2);
        for(int i=0;i<jsonArray.size();i++){
            JSONObject curObj = jsonArray.getJSONObject(i);
            String empno = String.valueOf(curObj.get("outid"));
            if(StringUtils.isNotBlank(empno) && !"null".equals(empno)){
                SafetyBuildInOutHistoryDO curDeviceDO = new SafetyBuildInOutHistoryDO();
                curDeviceDO.setDeviceCode(String.valueOf(curObj.get("extendid")));
                curDeviceDO.setEmpNo(String.valueOf(curObj.get("outid")));
                curDeviceDO.setEmpName(String.valueOf(curObj.get("name")));
                curDeviceDO.setIoFlag(String.valueOf(curObj.get("ioflag")));
                curDeviceDO.setOperTime(String.valueOf(curObj.get("opertime")).replaceAll("T"," "));
                curDeviceDO.setTypeName(String.valueOf(curObj.get("idtypename")));
                curDeviceDO.setHistoryId(curObj.getLong("id"));
                curDeviceDO.setUpdateTime(curTime);
                recordDOS.add(curDeviceDO);
            }
        }
        return recordDOS;
    }

    /**
     * 处理设备相关信息
     * @param jsonArray 设备结果集
     * @return 对象集合
     */
    private void formatResponseFs(JSONArray jsonArray){
        List<SafetyBuildInOutHistoryDO> recordDOS = new ArrayList<>();
        if(CollectionUtils.isEmpty(jsonArray)){
            return;
        }
        String curTime = DateUtils.format(new Date(),2);
        for(int i=0;i<jsonArray.size();i++){
            JSONObject curObj = jsonArray.getJSONObject(i);
            String staffNo = curObj.getString("StaffNo");
            if(StringUtils.isNotBlank(staffNo) && !"null".equals(staffNo)){
                SafetyBuildInOutHistoryDO curDeviceDO = new SafetyBuildInOutHistoryDO();
                curDeviceDO.setDeviceCode(curObj.getString("DevNo"));
                curDeviceDO.setEmpNo(staffNo);
                curDeviceDO.setEmpName(curObj.getString("StaffName"));
                curDeviceDO.setIoFlag(curObj.getString("ComeGo"));

                curDeviceDO.setOperTime(curObj.getString("OperateDate").replaceAll("T"," "));
                curDeviceDO.setTypeName(curObj.getString("WorkStationName"));
                curDeviceDO.setHistoryId(null);
                curDeviceDO.setUpdateTime(curTime);
                recordDOS.add(curDeviceDO);
            }
        }

        if(!CollectionUtils.isEmpty(recordDOS)){
            int curRow = safetyBuildInOutHistoryService.batchUpdate(recordDOS);
        }
    }
}
