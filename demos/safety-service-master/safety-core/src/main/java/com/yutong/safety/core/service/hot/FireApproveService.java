package com.yutong.safety.core.service.hot;

import com.yutong.safety.core.db.entity.hot.FireApprove;
import com.yutong.safety.core.db.entity.hot.FireApproveExtendDO;
import com.yutong.safety.core.db.mapper.hot.FireApproveDOMapper;
import com.yutong.safety.core.db.mapper.hot.FireApproveExtendDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FireApproveService {
    @Autowired
    private FireApproveDOMapper fireApproveDOMapper;

    @Autowired
    private FireApproveExtendDOMapper fireApproveExtendDOMapper;
    /**
     * 批量删除动火审批
     *
     * @param ids 需要删除的动火审批ID
     * @return 结果
     */
    public int deleteFireApproveByIds(Long[] ids) {
        return fireApproveDOMapper.deleteFireApproveByIds(ids);
    }

    /**
     * 删除动火审批信息
     *
     * @param id 动火审批ID
     * @return 结果
     */
    public int deleteFireApproveById(Long id) {
        return fireApproveDOMapper.deleteFireApproveById(id);
    }
    /**
     * 根据eip流程编号查询动火作业审批
     * @param documentNum
     * @return
     */
    public FireApprove selectFireApproveByDocumentNum(String documentNum) {
        return fireApproveDOMapper.selectFireApproveByDocumentNum(documentNum);
    }

    /**
     * 添加动火作业
     * @param fireApprove
     * @return
     */
    public int insertFireApprove(FireApprove fireApprove) {
        return fireApproveDOMapper.insertFireApprove(fireApprove);
    }

    /**
     * 修改动火作业审批信息
     * @param fireApprove
     * @return
     */
    public int updateFireApprove(FireApprove fireApprove) {
        return fireApproveDOMapper.updateFireApprove(fireApprove);
    }

    /**
     * 根据动火作业查询扩展表信息
     * @param fireApproveId
     * @return
     */
    public FireApproveExtendDO selectFireApproveExtendByfireApproveId(Long fireApproveId) {
        return fireApproveExtendDOMapper.selectFireApproveExtendByfireApproveId(fireApproveId);
    }

    /**
     * 保存扩展表信息
     * @param fireApproveExtendDO
     */
    public void insertFireApproveExtend(FireApproveExtendDO fireApproveExtendDO) {
        fireApproveExtendDOMapper.insert(fireApproveExtendDO);
    }

    /**
     * 修改扩展表信息
     * @param fireApproveExtend
     */
    public void updateFireApproveExtend(FireApproveExtendDO fireApproveExtend) {
        fireApproveExtendDOMapper.updateByPrimaryKey(fireApproveExtend);
    }
}
