package com.yutong.safety.core.service.goodsOut;

import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailReceiveDO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailReceiveDOExample;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutDetailReceiveListParam;
import com.yutong.safety.core.db.mapper.goodsOut.SafetyGoodsOutDetailReceiveDOBizMapper;
import com.yutong.safety.core.db.mapper.goodsOut.SafetyGoodsOutDetailReceiveDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SafetyGoodsOutDetailReceiveService {

    @Autowired
    private SafetyGoodsOutDetailReceiveDOMapper safetyGoodsOutDetailReceiveDOMapper;
    @Autowired
    private SafetyGoodsOutDetailReceiveDOBizMapper safetyGoodsOutDetailReceiveDOBizMapper;

    public int save(SafetyGoodsOutDetailReceiveDO safetyGoodsOutDetailReceiveDO){
        return safetyGoodsOutDetailReceiveDOMapper.insert(safetyGoodsOutDetailReceiveDO);
    }

    public int update(SafetyGoodsOutDetailReceiveDO safetyGoodsOutDetailReceiveDO){
        return safetyGoodsOutDetailReceiveDOMapper.updateByPrimaryKeySelective(safetyGoodsOutDetailReceiveDO);
    }

    public void updateByCondition(SafetyGoodsOutDetailReceiveDO safetyGoodsOutDetailReceiveDO){
        safetyGoodsOutDetailReceiveDOBizMapper.updateByCondition(safetyGoodsOutDetailReceiveDO);
    }

    public List<SafetyGoodsOutDetailReceiveDO> selectList(GoodsOutDetailReceiveListParam receiveListParam){
        SafetyGoodsOutDetailReceiveDOExample example = formatReceiveDOExample(receiveListParam);
        return safetyGoodsOutDetailReceiveDOMapper.selectByExample(example);
    }

    public long selectCount(GoodsOutDetailReceiveListParam receiveListParam){
        SafetyGoodsOutDetailReceiveDOExample example = formatReceiveDOExample(receiveListParam);
        return safetyGoodsOutDetailReceiveDOMapper.countByExample(example);
    }

    private SafetyGoodsOutDetailReceiveDOExample formatReceiveDOExample(GoodsOutDetailReceiveListParam receiveListParam){
        SafetyGoodsOutDetailReceiveDOExample example = new SafetyGoodsOutDetailReceiveDOExample();
        SafetyGoodsOutDetailReceiveDOExample.Criteria criteria = example.createCriteria();

        if(!StringUtils.isBlank(receiveListParam.getProcessId())){
            criteria.andProcessIdEqualTo(receiveListParam.getProcessId());
        }
        if(!CollectionUtils.isEmpty(receiveListParam.getProcessIdList())){
            criteria.andProcessIdIn(receiveListParam.getProcessIdList());
        }
        if(StringUtils.isNotBlank(receiveListParam.getReceiveEmpNo())){
            criteria.andReceiveEmpNoEqualTo(receiveListParam.getReceiveEmpNo());
        }
        if(StringUtils.isNotBlank(receiveListParam.getReceiveEmpEip())){
            criteria.andReceiveEmpEipEqualTo(receiveListParam.getReceiveEmpEip());
        }
        if(receiveListParam.getBegin() != null){
            example.setLimit(receiveListParam.getLimit());
            example.setOffset(receiveListParam.getOffset());
            example.setOrderByClause(receiveListParam.getOrderByClause());
        }
        return example;
    }

    /**
     * 获取制动流程的整体接收状态
     * @param processId 流程ID
     * @param  receiveDO 当前接收状态
     * @return 返回值
     */
    public Map<String, String> getProcessReceiveStatus(String processId, SafetyGoodsOutDetailReceiveDO receiveDO) {
        /*
            1.所有接收人都是部分接收 - 流程通过
            2.只要存在一个全部接收 - 流程通过
            3.一个接收人的情况，只有全部接收 流程通过
         */
        Map<String, String> resultMap = new HashMap<>();

        GoodsOutDetailReceiveListParam receiveListParam = new GoodsOutDetailReceiveListParam();
        receiveListParam.setProcessId(processId);
        List<SafetyGoodsOutDetailReceiveDO> receiveDOS = this.selectList(receiveListParam);
        if (!CollectionUtils.isEmpty(receiveDOS)) {
            if (receiveDOS.size() == 1) {
                SafetyGoodsOutDetailReceiveDO info = receiveDOS.get(0);
                String s = StringUtils.defaultString(info.getReceiveStatus(), "0");
                resultMap.put("unReceiveReason", info.getRemark() + "");
                resultMap.put("receiveStatus", "1".equals(s) ? s : "0");
                return resultMap;
            }
            boolean hasFullReceive = false;
            boolean isAllPartial = true;
            StringBuilder unReceiveReason = new StringBuilder();
            for (SafetyGoodsOutDetailReceiveDO li : receiveDOS) {
                String currentStatus = StringUtils.defaultString(li.getReceiveStatus(), "0");

                if (StringUtils.isNotBlank(li.getRemark())) {
                    unReceiveReason.append(li.getRemark()).append(";");
                }

                if ("1".equals(currentStatus)) {
                    hasFullReceive = true;
                    break;
                }
                if (!"2".equals(currentStatus)) {
                    isAllPartial = false;
                }
            }

            String receiveStatus;
            if (hasFullReceive || isAllPartial) {
                receiveStatus = "1";
            } else {
                receiveStatus = "0";
            }

            if (unReceiveReason.length() > 0) {
                unReceiveReason.setLength(unReceiveReason.length() - 1);
            }

            resultMap.put("unReceiveReason", unReceiveReason.toString());
            resultMap.put("receiveStatus", receiveStatus);
        }
        return resultMap;
    }

    public void updateReceiveInfo(List<SafetyGoodsOutDetailReceiveDO> changeList){
        safetyGoodsOutDetailReceiveDOBizMapper.updateReceiveInfo(changeList);
    }
}
