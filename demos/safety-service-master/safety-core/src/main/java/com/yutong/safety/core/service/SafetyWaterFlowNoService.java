package com.yutong.safety.core.service;

import com.yutong.safety.core.db.mapper.SafetyWaterFlowNoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SafetyWaterFlowNoService {

    @Autowired
    private SafetyWaterFlowNoMapper safetyWaterFlowNoMapper;

    /**
     * 分日期流水
     * @param type
     * @return
     */
    public String getNowByType(String type){
        int now = safetyWaterFlowNoMapper.getNowByType(type);
        //获取下一个值
        int next = now + 1;
        if(next == 1) {
            //插入下一个值
            safetyWaterFlowNoMapper.insertNew(type, next);
        }else{
            safetyWaterFlowNoMapper.updateNew(type, next);
        }
        //拼接保证够4位
        String num = "000"+next;
        //截取后4位并返回
        return num.substring(num.length() - 4);
    }

    /**
     * 不分日期流水
     * @param type 流水前缀
     * @return 当前最大序号
     */
    public String getNowByTypeNoDay(String type){
        int now = safetyWaterFlowNoMapper.getNowByTypeNoDay(type);
        //获取下一个值
        int next = now + 1;
        //插入下一个值
        safetyWaterFlowNoMapper.insertNew(type, next);
        //拼接保证够4位
        String num = "000"+next;
        //截取后4位并返回
        return num.substring(num.length() - 4);
    }
}
