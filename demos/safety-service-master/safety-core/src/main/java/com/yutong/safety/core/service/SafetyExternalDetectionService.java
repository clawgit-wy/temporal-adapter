package com.yutong.safety.core.service;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.web.ExternalDetectionListParam;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.SafetyExternalDetectionDO;
import com.yutong.safety.core.db.entity.SafetyExternalDetectionDOExample;
import com.yutong.safety.core.db.mapper.SafetyExternalDetectionDOMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author gwcuiyx
 * @date 2024-2-28 15:09
 */
@Service
public class SafetyExternalDetectionService {
    @Autowired
    private SafetyExternalDetectionDOMapper safetyExternalDetectionDOMapper;

    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    public List<SafetyExternalDetectionDO> pageList(ExternalDetectionListParam param) {
        SafetyExternalDetectionDOExample example = new SafetyExternalDetectionDOExample();
        SafetyExternalDetectionDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if (StringUtils.isNotEmpty(param.getEmpNo())) {
            criteria.andEmpNoLike("%" + param.getEmpNo() + "%");
        }
        if (StringUtils.isNotEmpty(param.getEmpName())) {
            criteria.andEmpNameLike("%" + param.getEmpName() + "%");
        }
        example.setOrderByClause(" update_time desc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        return safetyExternalDetectionDOMapper.selectByExample(example);
    }

    /**
     * 分页总条数
     * @param param
     * @return
     */
    public long pageListCount(ExternalDetectionListParam param) {
        SafetyExternalDetectionDOExample example = new SafetyExternalDetectionDOExample();
        SafetyExternalDetectionDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if (StringUtils.isNotEmpty(param.getEmpNo())) {
            criteria.andEmpNoLike("%" + param.getEmpNo() + "%");
        }
        if (StringUtils.isNotEmpty(param.getEmpName())) {
            criteria.andEmpNameLike("%" + param.getEmpName() + "%");
        }
        return safetyExternalDetectionDOMapper.countByExample(example);
    }

    /**
     * 添加方法
     * @param safetyExternalDetectionDO
     */
    public void add(SafetyExternalDetectionDO safetyExternalDetectionDO, String token) {
        PreconditonsEx.checkArgument(safetyExternalDetectionDOMapper.insert(safetyExternalDetectionDO) == 1, ResponseEnum.FAIL);
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("外协免检台账");
        log.setAbility("添加");
        log.setMessage(safetyExternalDetectionDO.getEmpName()+","+safetyExternalDetectionDO.getEmpNo());
        log.setOperator(tokenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 数据更新操作
     * @param safetyExternalDetectionDO
     */
    public void update(SafetyExternalDetectionDO safetyExternalDetectionDO, String token) {
        PreconditonsEx.checkArgument(safetyExternalDetectionDOMapper.updateByPrimaryKey(safetyExternalDetectionDO) == 1, ResponseEnum.FAIL);
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("外协免检台账");
        log.setAbility("更新");
        log.setMessage(safetyExternalDetectionDO.getEmpName()+","+safetyExternalDetectionDO.getEmpNo());
        log.setOperator(tokenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 根据数据id获取数据
     * @param id
     * @return
     */
    public SafetyExternalDetectionDO get(Integer id) {
        return safetyExternalDetectionDOMapper.selectByPrimaryKey(id);
    }

    /**
     * 删除操作
     * @param id
     * @return
     */
    public void delete(Integer id) {
        SafetyExternalDetectionDO safetyExternalDetectionDO = safetyExternalDetectionDOMapper.selectByPrimaryKey(id);
        safetyExternalDetectionDO.setUpdateTime(new Date());
        safetyExternalDetectionDO.setDeleteFalg(true);
        safetyExternalDetectionDOMapper.updateByPrimaryKey(safetyExternalDetectionDO);
    }

    /**
     * 数据导出操作
     * @param param
     * @return
     */
    public List<SafetyExternalDetectionDO> export(ExternalDetectionListParam param, String token) {
        SafetyExternalDetectionDOExample example = new SafetyExternalDetectionDOExample();
        SafetyExternalDetectionDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if (StringUtils.isNotEmpty(param.getEmpNo())) {
            criteria.andEmpNoLike("%" + param.getEmpNo() + "%");
        }
        if (StringUtils.isNotEmpty(param.getEmpName())) {
            criteria.andEmpNameLike("%" + param.getEmpName() + "%");
        }
        example.setOrderByClause(" update_time desc ");

        //根据tocken当前用户人员
        Map<String, String> tokenUser = TokenUtils.getTockenUser(token);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("外协免检台账");
        log.setAbility("导出");
        log.setMessage(JsonUtils.toString(param));
        log.setOperator(tokenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        return safetyExternalDetectionDOMapper.selectByExample(example);
    }

    /**
     * 检测数据
     * @param empNo
     * @return
     */
    public boolean checkEmpNo(String empNo) {
        SafetyExternalDetectionDOExample example = new SafetyExternalDetectionDOExample();
        example.createCriteria().andDeleteFalgEqualTo(false).andEmpNoEqualTo(empNo);
        List<SafetyExternalDetectionDO> list = safetyExternalDetectionDOMapper.selectByExample(example);
        return CollectionUtils.isEmpty(list) ? false : true;
    }

    /**
     * 检测数据
     * @param empNo
     * @param name
     * @return
     */
    public boolean checkEmpNoAndName(String empNo, String name) {
        SafetyExternalDetectionDOExample example = new SafetyExternalDetectionDOExample();
        example.createCriteria().andDeleteFalgEqualTo(false).andEmpNoEqualTo(empNo).andEmpNameEqualTo(name);
        List<SafetyExternalDetectionDO> list = safetyExternalDetectionDOMapper.selectByExample(example);
        return CollectionUtils.isEmpty(list) ? false : true;
    }

    /**
     * 更新数据
     * @param empNos
     */
    public void updateByEmpNos(List<String> empNos, boolean flag) {
        SafetyExternalDetectionDO safetyExternalDetectionDO = new SafetyExternalDetectionDO();
        safetyExternalDetectionDO.setDeleteFalg(flag);
        safetyExternalDetectionDO.setUpdateTime(new Date());
        SafetyExternalDetectionDOExample example = new SafetyExternalDetectionDOExample();
        example.createCriteria().andEmpNoIn(empNos);
        safetyExternalDetectionDOMapper.updateByExampleSelective(safetyExternalDetectionDO, example);
    }

}
