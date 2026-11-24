package com.yutong.safety.core.service.goodsOut;

import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyLaneFactoryDOKey;
import com.yutong.safety.core.db.entity.goodsOut.*;
import com.yutong.safety.core.db.mapper.goodsOut.SafetyGoodsOutExceptionRecordsDOExtMapper;
import com.yutong.safety.core.db.mapper.goodsOut.SafetyGoodsOutExceptionRecordsDOMapper;
import com.yutong.safety.core.db.mapper.goodsOut.SafetyGoodsOutProcessDOMapper;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsOutEventProcessDTO;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsOutExceptionRequestDTO;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsOutExceptionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SafetyGoodsOutExceptionRecordService {
    @Autowired
    SafetyGoodsOutExceptionRecordsDOMapper safetyGoodsOutExceptionRecordsDOMapper;

    @Autowired
    SafetyGoodsOutProcessDOMapper safetyGoodsOutProcessDOMapper;

    @Autowired
    SafetyGoodsOutExceptionRecordsDOExtMapper safetyGoodsOutExceptionRecordsDOExtMapper;

    public void updateProcessId(SafetyGoodsOutEventProcessDTO entity){
        SafetyGoodsOutExceptionRecordsDO en = new SafetyGoodsOutExceptionRecordsDO();
        en.setId(entity.getExceptionRecordId());
        en.setViolationReportId(entity.getId());

        safetyGoodsOutExceptionRecordsDOMapper.updateByPrimaryKeySelective(en);
    }

    public List<SafetyGoodsOutExceptionResponseDTO> pageList(SafetyGoodsOutExceptionRequestDTO en){
        return safetyGoodsOutExceptionRecordsDOExtMapper.pageList(en);
    }

    public Integer pageListCount(SafetyGoodsOutExceptionRequestDTO en){
        return safetyGoodsOutExceptionRecordsDOExtMapper.pageListCount(en);
    }

    public void saveBatch(List<SafetyGoodsOutExceptionRecordsDO> enList){
        safetyGoodsOutExceptionRecordsDOExtMapper.saveBatch(enList);
    }

    public void save(SafetyGoodsOutExceptionRecordsDO en){
        safetyGoodsOutExceptionRecordsDOMapper.insertSelective(en);
    }

    public List<SafetyGoodsOutDetailReceiveDO> getDetailReceiveList(Map<String,Object> map){
        return safetyGoodsOutExceptionRecordsDOExtMapper.getDetailReceiveList(map);
    }

    public void updateExceptionStatus(Map<String,Object> map){
        safetyGoodsOutExceptionRecordsDOExtMapper.updateExceptionStatus(map);
    }

    public void updateExceptionInfo(SafetyGoodsOutExceptionRecordsDO en){
        en.setEventUpdatedTime(new Date());
        safetyGoodsOutExceptionRecordsDOMapper.updateByPrimaryKeySelective(en);
    }

    public void add(SafetyGoodsOutExceptionRecordsDO en,String token){
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        en.setOperatorCode(tockenUser.get("workNo"));
        en.setOperatorName(tockenUser.get("userName"));
        en.setOperatorEip(tockenUser.get("userName"));

        SafetyGoodsOutProcessDO process = safetyGoodsOutProcessDOMapper.selectByPrimaryKey(en.getExitPassId());
        en.setCurNodeId(process.getCurNodeId());
        en.setCurNodeName(process.getCurNodeName());

        safetyGoodsOutExceptionRecordsDOMapper.insertSelective(en);
    }
}
