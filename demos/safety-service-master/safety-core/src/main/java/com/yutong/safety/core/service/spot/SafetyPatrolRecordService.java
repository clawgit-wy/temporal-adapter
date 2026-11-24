package com.yutong.safety.core.service.spot;

import com.yutong.safety.common.param.web.spot.PatrolNodeUpdateParam;
import com.yutong.safety.common.param.web.spot.PatrolRecordAddParam;
import com.yutong.safety.common.param.web.spot.PatrolRecordListParam;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.web.spot.PatrolRecordVO;
import com.yutong.safety.core.db.entity.bpm.BpmCurNodeVO;
import com.yutong.safety.core.db.entity.build.SafetyBuildProcessDOExample;
import com.yutong.safety.core.db.entity.build.SafetyBuildProcessDOWithBLOBs;
import com.yutong.safety.core.db.entity.build.param.BuildProcessListParam;
import com.yutong.safety.core.db.entity.goodsOut.vo.GoodsOutProcessDTO;
import com.yutong.safety.core.db.entity.spot.*;
import com.yutong.safety.core.db.mapper.enter.SafetyEnterFilingMapper;
import com.yutong.safety.core.db.mapper.spot.SafetyPatrolDealHistoryDOMapper;
import com.yutong.safety.core.db.mapper.spot.SafetyPatrolRecordHistoryDOMapper;
import com.yutong.safety.core.db.mapper.spot.SafetyProcessPatrolDOBizMapper;
import com.yutong.safety.core.db.mapper.spot.SafetyProcessPatrolHistoryDOMapper;
import com.yutong.safety.core.dto.SafetyPersonCarOutRuleDTO;
import com.yutong.safety.core.dto.SafetyProcessPatrolDTO;
import com.yutong.safety.core.dto.spot.SafetyProcessPatrolHistoryDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 治安巡查记录
 * @author gwxiexj
 * @since 2024-05-29 16:45
 */
@Service
public class SafetyPatrolRecordService {

    @Autowired
    private SafetyProcessPatrolDOBizMapper safetyProcessPatrolDOMapper;

    @Autowired
    private SafetyPatrolRecordHistoryDOMapper safetyPatrolRecordHistoryDOMapper;

    @Autowired
    private SafetyPatrolDealHistoryDOMapper safetyPatrolDealHistoryDOMapper;

    @Autowired
    private SafetyProcessPatrolHistoryDOMapper safetyProcessPatrolHistoryDOMapper;

    @Autowired
    SafetyEnterFilingMapper safetyEnterFilingMapper;

    /**
     * 更新规则
     * @param param 更新入参
     * @return 返回
     */
    public int save(PatrolRecordAddParam param) {
        SafetyProcessPatrolDO record = new SafetyProcessPatrolDO();
        BeanUtils.copyProperties(param,record);
        record.setCreateTime(new Date());
        safetyProcessPatrolDOMapper.insert(record);

        SafetyProcessPatrolHistoryDO safetyProcessPatrolHistoryDO = new SafetyProcessPatrolHistoryDO();
        BeanUtils.copyProperties(param,safetyProcessPatrolHistoryDO);
        safetyProcessPatrolHistoryDO.setCreateTime(new Date());
        safetyProcessPatrolHistoryDO.setUpdateUser(param.getSubmitNo());
        safetyProcessPatrolHistoryDO.setPatrolId(record.getId());
        safetyProcessPatrolHistoryDO.setId(null);
        safetyProcessPatrolHistoryDOMapper.insert(safetyProcessPatrolHistoryDO);

        return record.getId();
    }


    /**
     * 更新规则
     * @param param 更新入参
     * @return 返回
     */
    public int update(PatrolRecordAddParam param) {
        int updateRows = 0;
        SafetyProcessPatrolDO record = new SafetyProcessPatrolDO();
        BeanUtils.copyProperties(param,record);
        Integer id = param.getId();

        if (id == null) {
            record.setCreateTime(new Date());
            updateRows = safetyProcessPatrolDOMapper.insert(record);
        } else {
            updateRows = safetyProcessPatrolDOMapper.updateByPrimaryKeySelective(record);
        }
        return updateRows;
    }

    /**
     * 分页查询
     *
     * @param param 查询入参
     * @return 返回
     */
    public List<PatrolRecordVO> pageList(PatrolRecordListParam param) {
        param.setBegin(1);
        return safetyProcessPatrolDOMapper.getPatrolRecordList(param);
    }

    /**
     * 分页查询
     *
     * @param param 查询入参
     * @return 返回
     */
    public List<PatrolRecordVO> getList(PatrolRecordListParam param) {
        return safetyProcessPatrolDOMapper.getPatrolRecordList(param);
    }

    /**
     * 分页总条数
     *
     * @param param 查询入参
     * @return 返回
     */
    public long pageListCount(PatrolRecordListParam param) {
        return safetyProcessPatrolDOMapper.getPatrolRecordCount(param);
    }

    public SafetyProcessPatrolDO getProcessInfoById(Integer id){
        return safetyProcessPatrolDOMapper.selectRecordsById(id);
    }

    /**
     * 根据ID获取详情
     * @param id 主键ID
     * @return
     */
    public SafetyProcessPatrolDTO getDetailById(Integer id){
        SafetyProcessPatrolDTO processPatrolDTO = new SafetyProcessPatrolDTO();
        SafetyProcessPatrolDO processPatrolDO = safetyProcessPatrolDOMapper.selectRecordsById(id);
        BeanUtils.copyProperties(processPatrolDO,processPatrolDTO);
        //查询事件修改记录
        SafetyPatrolRecordHistoryDOExample example = new SafetyPatrolRecordHistoryDOExample();
        example.createCriteria().andProcessIdEqualTo(id);
        List<SafetyPatrolRecordHistoryDO> recordHistoryDOS = safetyPatrolRecordHistoryDOMapper.selectByExample(example);
        processPatrolDTO.setRecordHistoryDOS(recordHistoryDOS);
        //查询巡查处理记录
        SafetyPatrolDealHistoryDOExample example1 = new SafetyPatrolDealHistoryDOExample();
        example1.createCriteria().andProcessIdEqualTo(id);
        List<SafetyPatrolDealHistoryDO> dealHistoryDOS = safetyPatrolDealHistoryDOMapper.selectByExample(example1);
        processPatrolDTO.setDealHistoryDOS(dealHistoryDOS);

        return processPatrolDTO;
    }

    public int modifyCurDealHandler(PatrolNodeUpdateParam param){
        return safetyProcessPatrolDOMapper.modifyCurDealHandler(param);
    }

    public int modifyFlowId(PatrolNodeUpdateParam param){
        if(param.getId() == null){
            return 0;
        }
        return safetyProcessPatrolDOMapper.modifyFlowId(param);
    }

    public void saveUpdateHistory(SafetyProcessPatrolHistoryDO en){
        safetyProcessPatrolHistoryDOMapper.insertSelective(en);
    }

    public List<SafetyProcessPatrolHistoryDO> getHistoryPageList(SafetyProcessPatrolHistoryDTO param){
        //查询
        SafetyProcessPatrolHistoryDOExample example = getExample(param);
        return safetyProcessPatrolHistoryDOMapper.selectByExample(example);
    }

    public long getHistoryPageListCount(SafetyProcessPatrolHistoryDTO param){
        //查询
        SafetyProcessPatrolHistoryDOExample example = getExample(param);
        return safetyProcessPatrolHistoryDOMapper.countByExample(example);
    }

    public SafetyProcessPatrolHistoryDOExample getExample(SafetyProcessPatrolHistoryDTO param){
        SafetyProcessPatrolHistoryDOExample example = new SafetyProcessPatrolHistoryDOExample();
        SafetyProcessPatrolHistoryDOExample.Criteria criteria = example.createCriteria();
        criteria.andPatrolIdEqualTo(param.getPatrolId());
        example.setOrderByClause(" create_time desc");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        return example;
    }

    public SafetyPersonCarOutRuleDTO getBaseInfo(SafetyPersonCarOutRuleDTO en){
        if(StringUtils.isEmpty(en.getEmpName()) && StringUtils.isEmpty(en.getPhoneNo()) && StringUtils.isEmpty(en.getCarNo())){
            return null;
        }
        return safetyEnterFilingMapper.getBaseInfo(en);
    }

    public List<SafetyProcessPatrolHistoryDO> getRecordList(SafetyProcessPatrolHistoryDO en){
        SafetyProcessPatrolHistoryDOExample example = new SafetyProcessPatrolHistoryDOExample();
        SafetyProcessPatrolHistoryDOExample.Criteria criteria = example.createCriteria();
        if(en.getPatrolId() != null){
            criteria.andPatrolIdEqualTo(en.getPatrolId());
        }

        example.setOrderByClause(" create_time desc");
        example.setLimit(1);
        return safetyProcessPatrolHistoryDOMapper.selectByExample(example);
    }

    public int updateByExampleSelective(String bpmFlowId, SafetyProcessPatrolDO record){
        SafetyProcessPatrolDOExample example = new SafetyProcessPatrolDOExample();
        example.createCriteria().andFlowIdEqualTo(bpmFlowId);
        return safetyProcessPatrolDOMapper.updateByExampleSelective(record,example);
    }

    /**
     * 更新流程当前节点信息
     * @param curNodeVOList 入参
     * @return 更新条数
     */
    public int batchModifyCurDeal(List<BpmCurNodeVO> curNodeVOList){
        return safetyProcessPatrolDOMapper.batchModifyCurDeal(curNodeVOList);
    }

    /**
     * 分页查询
     *
     * @param param 查询入参
     * @return 返回
     */
    public List<SafetyProcessPatrolDO> selectByExample(PatrolRecordListParam param) {
        SafetyProcessPatrolDOExample example = new SafetyProcessPatrolDOExample();
        SafetyProcessPatrolDOExample.Criteria criteria = example.createCriteria();
        if(param.getStatus() != null){
            criteria.andStatusEqualTo(param.getStatus().toString());
        }
        if("1".equals(param.getNodeIdIsNull())){
            criteria.andCurNodeIdIsNull();
        }
        return safetyProcessPatrolDOMapper.selectByExample(example);
    }
}
