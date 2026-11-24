package com.yutong.safety.core.service.enter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.yutong.safety.core.db.entity.enter.SafetyEmpCard;
import com.yutong.safety.core.db.entity.enter.search.EmpCardParam;
import com.yutong.safety.core.db.mapper.enter.SafetyEmpCardMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SafetyEmpCardService{

    @Autowired
    private SafetyEmpCardMapper safetyEmpCardMapper;

    public String selectMaxVer() {
        return safetyEmpCardMapper.selectMaxVer();
    }

    public List<SafetyEmpCard> selectEmpCardDatas(JSONArray empNos) {
        List<SafetyEmpCard> cardInfos= new ArrayList<>();
        List<String> datas = JSON.parseArray(empNos.toString(), String.class);
        List<List<String>> partitions = Lists.partition(datas, 1000);
        for (List<String> partition : partitions) {
            cardInfos.addAll(safetyEmpCardMapper.selectEmpCardDatas(partition));
        }
        return cardInfos;
    }

    public List<SafetyEmpCard> selectEmpCardDatasByEmpList(List<String> empNoList) {
        List<SafetyEmpCard> cardInfos= new ArrayList<>();
        if(CollectionUtils.isEmpty(empNoList)){
            return cardInfos;
        }
        List<List<String>> partitions = Lists.partition(empNoList, 1000);
        for (List<String> partition : partitions) {
            cardInfos.addAll(safetyEmpCardMapper.selectEmpCardDatas(partition));
        }
        return cardInfos;
    }

    /**
     * 根据员工信息查询出对应的人事ID的MAP
     * @param empNoList
     * @return
     */
    public Map<String,SafetyEmpCard> getEmpCarDataMap(List<String> empNoList){
        List<SafetyEmpCard> list = selectEmpCardDatasByEmpList(empNoList);
        return list.stream().collect(Collectors.toMap(SafetyEmpCard::getEmpNo, e -> e));
    }

    public void saveOrUpdate(List<SafetyEmpCard> empCardDatas) {
        safetyEmpCardMapper.saveOrUpdate(empCardDatas);
    }

    public int updateByPrimary(SafetyEmpCard empCardData){
        return safetyEmpCardMapper.updateByPrimary(empCardData);
    }

    public int batchUpdate(List<String> empNoList,SafetyEmpCard record){
        if(CollectionUtils.isEmpty(empNoList) || ObjectUtils.isEmpty(record)){
            return 0;
        }
        return safetyEmpCardMapper.batchUpdate(empNoList,record);
    }

    public List<SafetyEmpCard> selectEmpCardDatasForParam(EmpCardParam param){
        if(param == null){
            param = new EmpCardParam();
        }
        return safetyEmpCardMapper.selectEmpCardDatasForParam(param);
    }

    /**
     * 根据员工号获取信息
     * @param empNo 员工号
     */
    public SafetyEmpCard selectCrkIdByEmpNo(String empNo) {
        return safetyEmpCardMapper.selectCrkIdByEmpNo(empNo);
    }
    /**
     * 根据员工号获取信息
     * @param empNo 员工号
     */
    public List<SafetyEmpCard> selectListCrkIdByEmpNo(List<String> empNo) {
        return safetyEmpCardMapper.selectListCrkIdByEmpNo(empNo);
    }
}
