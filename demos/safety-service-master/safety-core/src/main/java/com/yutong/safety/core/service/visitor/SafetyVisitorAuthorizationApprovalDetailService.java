package com.yutong.safety.core.service.visitor;

import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationApprovalDetailDO;
import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationApprovalDetailDOExample;
import com.yutong.safety.core.db.mapper.visitor.SafetyVisitorAuthorizationApprovalDetailDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SafetyVisitorAuthorizationApprovalDetailService {
    @Resource
    private SafetyVisitorAuthorizationApprovalDetailDOMapper safetyVisitorAuthorizationApprovalDetailDOMapper;

    public SafetyVisitorAuthorizationApprovalDetailDO selectById(Integer id) {
        return safetyVisitorAuthorizationApprovalDetailDOMapper.selectByPrimaryKey(id);
    }


    public void saveAndUpdate(SafetyVisitorAuthorizationApprovalDetailDO detailInfo, String token) {
        if (detailInfo.getId() == null) {
            detailInfo.setCreates(token);
            safetyVisitorAuthorizationApprovalDetailDOMapper.insertSelective(detailInfo);
        } else {
            detailInfo.setUpdateInfo(token);
            safetyVisitorAuthorizationApprovalDetailDOMapper.updateByPrimaryKeySelective(detailInfo);
        }
    }

    public void updateStatus(Integer id, Integer status, String updateBy) {
        SafetyVisitorAuthorizationApprovalDetailDO record = new SafetyVisitorAuthorizationApprovalDetailDO();
        record.setUpdateTime(new Date());
        if (StringUtils.isNotBlank(updateBy)) {
            record.setUpdateBy(updateBy);
        }
        record.setStatus(status);
        SafetyVisitorAuthorizationApprovalDetailDOExample example = new SafetyVisitorAuthorizationApprovalDetailDOExample();
        example.createCriteria().andDelFlagEqualTo(0).andAuthorizationApprovalIdEqualTo(id);
        safetyVisitorAuthorizationApprovalDetailDOMapper.updateByExampleSelective(record, example);
    }

    /**
     * 授权到期自动注销 根据ID修改数据状态为注销，原因为，超出有效期
     */
    public void cancelAuthorizationInfo(List<Integer> ids, String cancelReason) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        SafetyVisitorAuthorizationApprovalDetailDO record = new SafetyVisitorAuthorizationApprovalDetailDO();
        record.setUpdateTime(new Date());
        record.setStatus(2);
        record.setCancelReason(cancelReason);
        SafetyVisitorAuthorizationApprovalDetailDOExample example = new SafetyVisitorAuthorizationApprovalDetailDOExample();
        example.createCriteria().andDelFlagEqualTo(0).andIdIn(ids);
        safetyVisitorAuthorizationApprovalDetailDOMapper.updateByExampleSelective(record, example);
    }

    /**
     * 批量创建数据
     *
     * @param saveDetailList 明细数据
     */
    public void batchSave(List<SafetyVisitorAuthorizationApprovalDetailDO> saveDetailList) {
        safetyVisitorAuthorizationApprovalDetailDOMapper.batchSave(saveDetailList);
    }

    public List<SafetyVisitorAuthorizationApprovalDetailDO> selectList(Integer processId) {
        SafetyVisitorAuthorizationApprovalDetailDOExample example = new SafetyVisitorAuthorizationApprovalDetailDOExample();
        example.createCriteria().andDelFlagEqualTo(0).andAuthorizationApprovalIdEqualTo(processId);
        return safetyVisitorAuthorizationApprovalDetailDOMapper.selectByExample(example);
    }
}
