package com.yutong.safety.core.service.park;

import com.alibaba.excel.util.StringUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerHistoryDO;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerInfoDO;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerInfoDOExample;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeePowerListParam;
import com.yutong.safety.core.db.entity.park.vo.ParkEmployeePowerVO;
import com.yutong.safety.core.db.mapper.park.SafetyParkEmployeePowerBizMapper;
import com.yutong.safety.core.db.mapper.park.SafetyParkEmployeePowerHistoryDOBizMapper;
import com.yutong.safety.core.db.mapper.park.SafetyParkEmployeePowerInfoDOBizMapper;
import com.yutong.safety.core.db.mapper.park.SafetyParkEmployeePowerInfoDOMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SafetyParkEmpPowerReportService {

    @Autowired
    private SafetyParkEmployeePowerBizMapper safetyParkEmployeePowerBizMapper;

    @Autowired
    private SafetyParkEmployeePowerInfoDOBizMapper safetyParkEmployeePowerInfoDOMapper;

    @Autowired
    private SafetyParkEmployeePowerHistoryDOBizMapper safetyParkEmployeePowerHistoryDOapper;

    /**
     * 不分页-湖区列表
     * @param param
     * @return
     */
    public List<ParkEmployeePowerVO> selectPowerList(ParkEmployeePowerListParam param){
        param.setPageFlag(false);
        if(StringUtils.isBlank(param.getOrderByClause())){
            param.setOrderByClause(" update_time desc");
        }
        return safetyParkEmployeePowerBizMapper.selectListNew(param);
    }

    public List<ParkEmployeePowerVO> selectPowerPageList(ParkEmployeePowerListParam param){
        param.setPageFlag(true);
        if(StringUtils.isBlank(param.getOrderByClause())){
            param.setOrderByClause(" update_time desc");
        }
        return safetyParkEmployeePowerBizMapper.selectListNew(param);
    }

    /**
     * 获取结果条数
     * @param param 检索条件
     * @return
     */
    public Long selectReportCount(ParkEmployeePowerListParam param){
        return safetyParkEmployeePowerBizMapper.selectCountNew(param);
    }

    /**
     * 获取未同步车辆权限的员工号清单
     * @return
     */
    public List<String> getUnSyncCarList(){
        return safetyParkEmployeePowerBizMapper.getUnSyncCarList();
    }

    /**
     * 更新车辆权限同步标记
     * @param empNoList 员工号列表
     * @return 返回值
     */
    public int modifyDownMark(List<String> empNoList){
        return safetyParkEmployeePowerBizMapper.modifyDownMark(empNoList);
    }

    public List<SafetyParkEmployeePowerInfoDO> selectByExample(ParkEmployeePowerListParam param){
        SafetyParkEmployeePowerInfoDOExample example = new SafetyParkEmployeePowerInfoDOExample();
        SafetyParkEmployeePowerInfoDOExample.Criteria criteria = example.createCriteria();
        criteria.andIsDeleteEqualTo(Short.valueOf("0"));
        if(!CollectionUtils.isEmpty(param.getNotFactoryIdList())){
            criteria.andFactoryIdNotIn(param.getNotFactoryIdList());
        }
        if(!CollectionUtils.isEmpty(param.getEmpNoList())){
            criteria.andEmployeeCodeIn(param.getEmpNoList());
        }
        if(StringUtils.isNotBlank(param.getEmpNo())){
            criteria.andEmployeeCodeEqualTo(param.getEmpNo());
        }
        if(param.getPageFlag() != null && param.getPageFlag()){
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }

        return safetyParkEmployeePowerInfoDOMapper.selectByExample(example);
    }

    /**
     * 批量删除员工大门权限
     * @param empNos
     * @param remark
     * @return
     */
    public int batchDelEmployeePower(List<String> empNos,String remark){
        if(CollectionUtils.isEmpty(empNos)){
            return 0;
        }
        SafetyParkEmployeePowerInfoDOExample example =  new SafetyParkEmployeePowerInfoDOExample();
        SafetyParkEmployeePowerInfoDOExample.Criteria criteria = example.createCriteria();
        criteria.andEmployeeCodeIn(empNos);
        criteria.andIsDeleteEqualTo(Short.valueOf("0"));

        try{
            //查询出注销人员当前的门禁权限，保存到history表中
            ParkEmployeePowerListParam powerList = new ParkEmployeePowerListParam();
            powerList.setEmpNoList(empNos);
            //查询出注销人员当前的门禁权限，保存到history表中
            List<SafetyParkEmployeePowerInfoDO> curList = safetyParkEmployeePowerInfoDOMapper.getDetailByCon(powerList);
            batchUpdateHistoryPower(curList,"注销权限");
        }catch (Exception e){
            LogUtils.error("批量保存最后大门权限异常",e);
        }
        try{
            return safetyParkEmployeePowerInfoDOMapper.deleteByExample(example);
        }catch (Exception e){
            LogUtils.error("批量更新状态失败",e);
        }
        return 0;
    }

    /**
     * 保存上一次的大门权限
     * @param curList
     * @param remark
     * @return
     */
    public int batchUpdateHistoryPower(List<SafetyParkEmployeePowerInfoDO> curList,String remark){
        if(CollectionUtils.isEmpty(curList)){
            return 0;
        }
        List<SafetyParkEmployeePowerHistoryDO> newList = new ArrayList<>();
        Map<String,List<SafetyParkEmployeePowerInfoDO>> curMap = curList.stream().collect(Collectors.groupingBy(SafetyParkEmployeePowerInfoDO::getEmployeeCode));
        curMap.forEach((key,value)->{
            SafetyParkEmployeePowerHistoryDO curRecord = new SafetyParkEmployeePowerHistoryDO();
            curRecord.setEmployeeCode(key);
            String factoryIds = value.stream().map(SafetyParkEmployeePowerInfoDO::getFactoryId).collect(Collectors.joining(","));
            String factoryNames = value.stream().map(SafetyParkEmployeePowerInfoDO::getFactoryName).collect(Collectors.joining(","));
            curRecord.setFactoryId(factoryIds);
            curRecord.setFactoryName(factoryNames);
            curRecord.setRemark(remark);
            JSONArray details = new JSONArray();
            value.forEach(e->{
                JSONObject obj = new JSONObject();
                obj.put("factoryId",e.getFactoryId());
                obj.put("factoryName",e.getFactoryName());
                obj.put("powerBeginTime",e.getPowerBeginTime().substring(0,10));
                obj.put("powerEndTime",e.getPowerEndTime().substring(0,10));
                details.put(obj);
            });
            curRecord.setDetails(details.toString().getBytes(StandardCharsets.UTF_8));
            newList.add(curRecord);
        });

        if(!CollectionUtils.isEmpty(newList)){
            safetyParkEmployeePowerHistoryDOapper.batchUpdate(newList);
        }
        return 1;
    }
}
