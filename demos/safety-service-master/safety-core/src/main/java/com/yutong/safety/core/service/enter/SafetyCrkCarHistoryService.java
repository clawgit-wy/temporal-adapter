package com.yutong.safety.core.service.enter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.WebserviceUtil;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.enter.SafetyCrkCarOutHistoryDO;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterFilingReportRt;
import com.yutong.safety.core.db.entity.enter.result.SatefyEnterStatisticRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyCrkCarOutHistorySh;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterFilingReportSh;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterStatisticSh;
import com.yutong.safety.core.db.mapper.enter.SafetyCrkCarOutHistoryDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车辆出入口进出厂记录管理
 */
@Service
public class SafetyCrkCarHistoryService {
    @Value("${car.in.factory.invehicle.url}")
    private String carInVehicleUrl;

    @Value("${car.out.factory.outvehicle.url}")
    private String carOutVehicleUrl;

    @Autowired
    private SafetyCrkCarOutHistoryDOMapper safetyCrkCarOutHistoryDOMapper;

    @Autowired
    private WebserviceUtil webserviceUtil;

    public int deleteByPrimaryKey(Integer id){
        return safetyCrkCarOutHistoryDOMapper.deleteByPrimaryKey(id);
    }

    public int insert(SafetyCrkCarOutHistoryDO record){
        return safetyCrkCarOutHistoryDOMapper.insert(record);
    }

    public SafetyCrkCarOutHistoryDO selectByPrimaryKey(Integer id){
        return safetyCrkCarOutHistoryDOMapper.selectByPrimaryKey(id);
    }

    public int updateByPrimaryKey(SafetyCrkCarOutHistoryDO record){
        return safetyCrkCarOutHistoryDOMapper.updateByPrimaryKey(record);
    }

    public int selectCount(SafetyCrkCarOutHistorySh crkCarOutHistorySh){
        return safetyCrkCarOutHistoryDOMapper.selectCount(crkCarOutHistorySh);
    }

    public List<SafetyCrkCarOutHistoryDO> selectList(SafetyCrkCarOutHistorySh crkCarOutHistorySh){
        return safetyCrkCarOutHistoryDOMapper.selectList(crkCarOutHistorySh);
    }

    public int updateBatch(List<SafetyCrkCarOutHistoryDO> historyDOS){
        return safetyCrkCarOutHistoryDOMapper.updateBatch(historyDOS);
    }

    public List<SafetyEnterFilingReportRt> selectFilingReportList(SafetyEnterFilingReportSh reportSh){
        reportSh.setExport("1"); //设置非分页标识
        return safetyCrkCarOutHistoryDOMapper.selectFilingReportList(reportSh);
    }

    public int selectFilingReportCount(SafetyEnterFilingReportSh reportSh){
        return safetyCrkCarOutHistoryDOMapper.selectFilingReportCount(reportSh);
    }

    public PageVO reportPageList(SafetyEnterFilingReportSh reportSh){
        List<SafetyEnterFilingReportRt> list = new ArrayList<>();
        int total = 0;

        if ("未入厂".equals(reportSh.getInStatus()) || "在厂".equals(reportSh.getInStatus())) {
            //查询当天的出厂记录
            List<Integer> idList = safetyCrkCarOutHistoryDOMapper.selectHistory();
            reportSh.setIdList(idList);
            list = safetyCrkCarOutHistoryDOMapper.selectFilingReportListWrcOrZc(reportSh);
            total = safetyCrkCarOutHistoryDOMapper.selectFilingReportCountWrcOrZc(reportSh);
        } else if ("已出厂".equals(reportSh.getInStatus())) {
            list = safetyCrkCarOutHistoryDOMapper.selectFilingReportListWrcYcc(reportSh);
            total = safetyCrkCarOutHistoryDOMapper.selectFilingReportCountWrcYcc(reportSh);
        }
        return new PageVO<>(total, list);
    }

    public List<SafetyEnterFilingReportRt> reportList(SafetyEnterFilingReportSh reportSh){
        List<SafetyEnterFilingReportRt> list = new ArrayList<>();
        if ("未入厂".equals(reportSh.getInStatus()) || "在厂".equals(reportSh.getInStatus())) {
            //查询当天的出厂记录
            List<Integer> idList = safetyCrkCarOutHistoryDOMapper.selectHistory();
            reportSh.setIdList(idList);
            list = safetyCrkCarOutHistoryDOMapper.selectFilingReportListWrcOrZc(reportSh);
        } else if ("已出厂".equals(reportSh.getInStatus())) {
            list = safetyCrkCarOutHistoryDOMapper.selectFilingReportListWrcYcc(reportSh);
        }
        return list;
    }

    /**
     * 获取车辆入参记录
     * @param where 检索条件
     * @return 返回值
     */
    public JSONArray getInvehicleInfo(String where){
        JSONArray retMap = new JSONArray();
        try {
            String bodyParams = where;
            Map headParams = new HashMap(4);
            headParams.put("Content-Type","application/json");
            String returnJson = webserviceUtil.postJsonStr(carInVehicleUrl, bodyParams, headParams);
           // LogUtils.info("出入口-获取车辆入厂记录”{}“，返回值{}",where,returnJson);
            if (StringUtils.isEmpty(returnJson)) {
                return retMap;
            }
            JSONObject retObject = JSONObject.parseObject(returnJson);
            JSONObject stateObj = retObject.getJSONObject("State");
            if(stateObj == null || !stateObj.getBoolean("IsSucess")){
                return retMap;
            }
            retMap = retObject.getJSONArray("Models");
            if(retMap == null || retMap.size()<=0){
                return retMap;
            }
        }catch (Exception e){
            LogUtils.error("出入口-获取车辆入厂记录异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"出入口-获取车辆入厂记录异常，条件”“+where+”“："+e.getMessage());
        }
        return retMap;
    }

    /**
     * 获取车辆出厂记录
     * @param where 检索条件
     * @return 返回值
     */
    public JSONArray getOutVehicleHistory(String where){
        JSONArray retMap = new JSONArray();
        String returnJson = "";
        try {
            String bodyParams = where;
            Map headParams = new HashMap(4);
            headParams.put("Content-Type","application/json");
            returnJson = webserviceUtil.postJsonStr(carOutVehicleUrl, bodyParams, headParams);
            if (StringUtils.isEmpty(returnJson)) {
                return retMap;
            }
            JSONObject retObject = JSONObject.parseObject(returnJson);
            JSONObject stateObj = retObject.getJSONObject("State");
            if(stateObj == null){
                return retMap;
            }
            retMap = retObject.getJSONArray("Models");
            if(retMap == null){
                return retMap;
            }
        }catch (Exception e){
            LogUtils.info("出入口-获取车辆出厂记录”{}“，返回值{}",where,returnJson);
            LogUtils.error("出入口-获取车辆出厂记录异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"出入口-获取车辆出厂记录异常，条件”“+where+”“："+e.getMessage());
        }
        return retMap;
    }

    public int selectFilingNum(SafetyEnterStatisticSh statisticSh){
        return safetyCrkCarOutHistoryDOMapper.selectFilingNum(statisticSh);
    }

    public SatefyEnterStatisticRt selectEnterCarNum(SafetyEnterStatisticSh statisticSh){
        List<SatefyEnterStatisticRt> list = safetyCrkCarOutHistoryDOMapper.selectEnterCarNum(statisticSh);
        return list.size()<=0?null:list.get(0);
    }
}
