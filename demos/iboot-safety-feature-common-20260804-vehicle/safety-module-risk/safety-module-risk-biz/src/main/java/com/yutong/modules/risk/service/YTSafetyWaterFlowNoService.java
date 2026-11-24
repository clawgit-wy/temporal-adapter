package com.yutong.modules.risk.service;

import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.yutong.modules.risk.api.YTSafetyWaterFlowNoServiceApi;
import com.yutong.modules.risk.mapper.SafetyWaterFlowNoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class YTSafetyWaterFlowNoService implements YTSafetyWaterFlowNoServiceApi {

    @Autowired
    private SafetyWaterFlowNoMapper safetyWaterFlowNoMapper;

    /**
     * 自增数据
     * @param type
     * @return
     */
    @Lock4j(name = "ytsafety:flow:autoincrement:#{type}")
    public Long autoIncrementKey(String type){
        int now = safetyWaterFlowNoMapper.getNowByType(type);
        //获取下一个值
        int next = now + 1;
        Long id = IdWorker.getId();
        if(next == 1) {
            //插入下一个值
            safetyWaterFlowNoMapper.insertNew(id,type, next);
        }else{
            safetyWaterFlowNoMapper.updateNew(type, next);
        }
        //截取后4位并返回
        return (long) next;
    }

    /**
     * 批量自增，一次性获取count个序号，返回起始值
     * @param type 类型
     * @param count 需要获取的序号数量
     * @return 起始序号值
     */
    @Lock4j(name = "ytsafety:flow:autoincrement:#{type}")
    public Long batchAutoIncrementKey(String type, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must be positive");
        }
        int now = safetyWaterFlowNoMapper.getNowByType(type);
        int next = now + count;
        Long id = IdWorker.getId();
        if (now == 0) {
            safetyWaterFlowNoMapper.insertNew(id, type, next);
        } else {
            safetyWaterFlowNoMapper.updateNew(type, next);
        }
        return (long) (now + 1);
    }

    /**
     * 分日期流水
     * @param type
     * @return
     */
    public String getNowByType(String type){
        int now = safetyWaterFlowNoMapper.getNowByType(type);
        //获取下一个值
        int next = now + 1;
        Long id = IdWorker.getId();
        if(next == 1) {
            //插入下一个值
            safetyWaterFlowNoMapper.insertNew(id,type, next);
        }else{
            safetyWaterFlowNoMapper.updateNew(type, next);
        }
        //拼接保证够4位
        String num = "000"+next;
        //截取后4位并返回
        return num.substring(num.length() - 4);
    }

    @Lock4j(name = "ytsafety:flow:day:#{type}")
    public String getNowByTypeDay(String type, Date curDate){
        int now = safetyWaterFlowNoMapper.getNowByTypeOrDate(type,curDate);
        //获取下一个值
        int next = now + 1;
        Long id = IdWorker.getId();
        if(next == 1) {
            //插入下一个值
            if(curDate == null){
                safetyWaterFlowNoMapper.insertNew(id,type, next);
            }else{
                safetyWaterFlowNoMapper.insertTypeOrDate(id,type, next,curDate);
            }
        }else{
            safetyWaterFlowNoMapper.updateTypeOrDate(type, next,curDate);
        }
        String dateStr;
        String num;
        dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        num = String.format("%04d", next);
        return type + dateStr + num;
    }

    @Lock4j(name = "ytsafety:flow:day:#{type}")
    public String getNowByTypeDayNum(String type, Date curDate){
        int now = safetyWaterFlowNoMapper.getNowByTypeOrDate(type,curDate);
        //获取下一个值
        int next = now + 1;
        Long id = IdWorker.getId();
        if(next == 1) {
            if(curDate == null){
                safetyWaterFlowNoMapper.insertNew(id,type, next);
            }else{
                safetyWaterFlowNoMapper.insertTypeOrDate(id,type, next,curDate);
            }
        }else{
            safetyWaterFlowNoMapper.updateTypeOrDate(type, next,curDate);
        }
        return String.format("%04d", next);
    }

    /**
     * 批量获取日期流水号，一次DB操作获取count个序号
     */
    @Lock4j(name = "ytsafety:flow:day:#{type}")
    public List<String> batchGetNowByTypeDayNum(String type, Date curDate, int count) {
        if (count <= 0) {
            return new ArrayList<>();
        }
        int now = safetyWaterFlowNoMapper.getNowByTypeOrDate(type, curDate);
        Long id = IdWorker.getId();
        if (now == 0) {
            safetyWaterFlowNoMapper.insertTypeOrDate(id, type, count, curDate);
        } else {
            safetyWaterFlowNoMapper.updateTypeOrDate(type, now + count, curDate);
        }
        List<String> result = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            result.add(String.format("%04d", now + i));
        }
        return result;
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
        Long id = IdWorker.getId();
        //插入下一个值
        safetyWaterFlowNoMapper.insertNew(id, type, next);
        //拼接保证够4位
        String num = "000"+next;
        //截取后4位并返回
        return num.substring(num.length() - 4);
    }
}
