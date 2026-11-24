package com.yutong.safety.core.service.visitor;

import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationApprovalDO;
import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationApprovalDOExample;
import com.yutong.safety.core.db.entity.visitor.param.SafetyVisitorAuthorizationQueryParam;
import com.yutong.safety.core.db.mapper.visitor.SafetyVisitorAuthorizationApprovalDOMapper;
import com.yutong.safety.core.dto.visitor.SafetyVisitorAuthorizationDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SafetyVisitorAuthorizationApprovalService {

    @Resource
    private SafetyVisitorAuthorizationApprovalDOMapper safetyVisitorAuthorizationApprovalDOMapper;


    public List<SafetyVisitorAuthorizationDto> selectList(SafetyVisitorAuthorizationQueryParam param, boolean b) {
        param.setPageFlag(b);
        if (b) {
            param.setPageSize(param.getLimit());
        }
        return safetyVisitorAuthorizationApprovalDOMapper.selectPageList(param);
    }

    public long selectCount(SafetyVisitorAuthorizationQueryParam param) {
        return safetyVisitorAuthorizationApprovalDOMapper.selectPageCount(param);
    }

    public SafetyVisitorAuthorizationApprovalDO selectById(Integer id) {
        return safetyVisitorAuthorizationApprovalDOMapper.selectByPrimaryKey(id);
    }

    public void saveAndUpdate(SafetyVisitorAuthorizationApprovalDO mainInfo, String token) {
        if (mainInfo.getId() == null) {
            mainInfo.setCreates(token);
            safetyVisitorAuthorizationApprovalDOMapper.insertSelective(mainInfo);
        } else {
            mainInfo.setUpdateInfo(token);
            safetyVisitorAuthorizationApprovalDOMapper.updateByPrimaryKeySelective(mainInfo);
        }
    }

    /**
     * 数据库中的流程授权数据
     *
     * @param empNo 员工号
     */
    public List<SafetyVisitorAuthorizationDto> selectBpmFlowList(String empNo) {
        // 数据库中的流程授权数据
        SafetyVisitorAuthorizationQueryParam queryParam = new SafetyVisitorAuthorizationQueryParam();
        queryParam.setGrantorInfo(empNo);
        queryParam.setStatus(1);
        queryParam.setBpmFlowIdNotNull("t");
        queryParam.setGtAuthorizationEndTime(DateUtils.format(new Date(), 1));
        queryParam.setPageFlag(false);
        return safetyVisitorAuthorizationApprovalDOMapper.selectPageList(queryParam);
    }

    public void batchSave(List<SafetyVisitorAuthorizationApprovalDO> saveList) {
        safetyVisitorAuthorizationApprovalDOMapper.batchSave(saveList);
    }

    public List<SafetyVisitorAuthorizationApprovalDO> selectByExample(String processId) {
        SafetyVisitorAuthorizationApprovalDOExample example = new SafetyVisitorAuthorizationApprovalDOExample();
        example.createCriteria().andDelFlagEqualTo(0).andBpmFlowIdEqualTo(processId);
        return safetyVisitorAuthorizationApprovalDOMapper.selectByExample(example);
    }
}
