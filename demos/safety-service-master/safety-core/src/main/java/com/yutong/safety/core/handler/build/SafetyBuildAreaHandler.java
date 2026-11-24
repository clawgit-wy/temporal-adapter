package com.yutong.safety.core.handler.build;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.utils.BuildEncryptUtil;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.build.SafetyBuildAreaDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceDO;
import com.yutong.safety.core.db.entity.build.param.BuildAreaListParam;
import com.yutong.safety.core.service.build.SafetyBuildAreaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

@Component
public class SafetyBuildAreaHandler {

    @Value("${build.getArea.url}")
    private String getBuildAreaUrl;

    @Value("${build.encryption.key}")
    private String buildEncryKey;

    @Value("${build.signature.key}")
    private String buildSignKey;

    @Value("${build.appid}")
    private String buildAppID;

    @Value("${build.dpcode}")
    private String buildDpcode;

    @Resource
    private SafetyBuildAreaService safetyBuildAreaService;


    /**
     * 定时更新楼宇门禁设备
     */
    public int updateAreaTask(){
        int buildPageSize = 100;
        int buildPageNo = 1;
        int totalPage = 1;
        int updateRow = 0;
        try {
            JSONObject retObj = sendBuildAreaPost(buildPageNo,buildPageSize);
            if(retObj == null){
                return updateRow;
            }
            Integer count = retObj.getInteger("count");
            if(count == null || count == 0){
                return updateRow;
            }
            JSONArray dataArr = retObj.getJSONArray("data");
            List<SafetyBuildAreaDO> deviceDOS = formatAreaResponse(dataArr);
            updateRow = safetyBuildAreaService.batchUpdate(deviceDOS);
            Integer totalCount = retObj.getInteger("totalcount");
            if(totalCount>buildPageSize){
                totalPage = totalCount%buildPageSize>0?totalCount/buildPageSize+1:totalCount/buildPageSize;
            }
        }catch (Exception e){
            LogUtils.error("调用接口异常",e);
        }
        for(int i=1;i<=totalPage-1;i++){
            try {
                JSONObject returnObj = sendBuildAreaPost(buildPageNo + i, buildPageSize);
                if (returnObj == null) {
                    continue;
                }
                Integer curCount = returnObj.getInteger("count");
                if (curCount == null || curCount == 0) {
                    continue;
                }
                JSONArray curDataArr = returnObj.getJSONArray("data");
                List<SafetyBuildAreaDO> curDeviceDOS = formatAreaResponse(curDataArr);
                int curRow = safetyBuildAreaService.batchUpdate(curDeviceDOS);
                updateRow += curRow;
            }catch (Exception cure){
                LogUtils.error("调用接口异常",cure);
            }
        }
        return updateRow;
    }

    /**
     * 调用请求
     * @param buildPageNo 页码
     * @param buildPageSize 页条数
     * @return 返回值
     * @throws Exception
     */
    private JSONObject sendBuildAreaPost(int buildPageNo,int buildPageSize) throws Exception{
        JSONObject firstData = new JSONObject();
        firstData.put("pageno",buildPageNo);
        firstData.put("pagesize",buildPageSize);
        String retStr = BuildEncryptUtil.sendBuildHttpPost(getBuildAreaUrl, firstData.toJSONString(),
                buildEncryKey,buildSignKey,buildAppID,buildDpcode);
        if(ResponseEnum.FAIL.getName().equals(retStr)){
            return null;
        }
        return JSONObject.parseObject(retStr);
    }

    /**
     * 处理接口返回的设备集合
     * @param jsonArray 门禁设备结果集
     * @return 返回值
     */
    private List<SafetyBuildAreaDO> formatAreaResponse(JSONArray jsonArray){
        List<SafetyBuildAreaDO> deviceDOS = new ArrayList<>();
        if(CollectionUtils.isEmpty(jsonArray)){
            return deviceDOS;
        }
        for(int i=0;i<jsonArray.size();i++){
            JSONObject curObj = jsonArray.getJSONObject(i);
            SafetyBuildAreaDO curDeviceDO = JSONObject.parseObject(curObj.toJSONString(),SafetyBuildAreaDO.class);
            deviceDOS.add(curDeviceDO);
        }
        return deviceDOS;
    }

}
