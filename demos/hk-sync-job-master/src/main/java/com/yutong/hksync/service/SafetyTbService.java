package com.yutong.hksync.service;

import com.yutong.hksync.mapper.safety.SafetyTbMapper;
import com.yutong.hksync.vo.HkMiddleTableVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SafetyTbService {

    @Resource
    private SafetyTbMapper safetyTbMapper;

    public List<HkMiddleTableVO> selectBlackMessageList(Date start, Date end) {
        return safetyTbMapper.selectBlackMessageList(start, end);
    }

    public List<HkMiddleTableVO> selectEmployeeMessageList(Date start, Date end) {
        return safetyTbMapper.selectEmployeeMessageList(start, end);
    }

    public List<HkMiddleTableVO> selectEnterRegisterList(Date start, Date end) {
        return safetyTbMapper.selectEnterRegisterList(start, end);
    }

    public List<HkMiddleTableVO> selectVisitorHairpinList(Date start, Date end) {
        return safetyTbMapper.selectVisitorHairpinList(start, end);
    }
}
