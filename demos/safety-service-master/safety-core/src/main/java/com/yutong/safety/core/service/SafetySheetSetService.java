package com.yutong.safety.core.service;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.constants.SheetSetEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.param.web.SheetSetAddParam;
import com.yutong.safety.common.param.web.SheetSetParam;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.SafetySheetSetDO;
import com.yutong.safety.core.db.entity.SafetySheetSetDOExample;
import com.yutong.safety.core.db.mapper.SafetySheetSetDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *  create gwcuiyx 2023.07.12
 *  表单设置--service
 */
@Service
public class SafetySheetSetService {

    @Autowired
    private SafetySheetSetDOMapper safetySheetSetDOMapper;
    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    /**
     * 数据列表操作
     * @return
     */
    public List<SafetySheetSetDO> sheetSetList() {
        SafetySheetSetDOExample example = new SafetySheetSetDOExample();
        SafetySheetSetDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        example.setOrderByClause(" create_time asc ");
        return safetySheetSetDOMapper.selectByExample(example);
    }

    /**
     * app端返回数据列表
     * @return
     */
    public List<SafetySheetSetDO> appList() {
        SafetySheetSetDOExample example = new SafetySheetSetDOExample();
        SafetySheetSetDOExample.Criteria criteria = example.createCriteria();
        criteria.andTakeEffectEqualTo(true);
        example.setOrderByClause(" create_time asc ");
        return safetySheetSetDOMapper.selectByExample(example);
    }

    /**
     * 数据更新操作
     * @param param
     */
    public void updateSheetSet(SheetSetParam param, String tocken) {
        SafetySheetSetDO safetySheetSetDO = safetySheetSetDOMapper.selectByPrimaryKey(param.getId());
        if(safetySheetSetDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        if(Objects.nonNull(param.getMustSet())) {
            safetySheetSetDO.setMustSet(SheetSetEnum.values()[param.getMustSet()].isCode());
        }
        if(Objects.nonNull(param.getTakeEffect())) {
            safetySheetSetDO.setTakeEffect(SheetSetEnum.values()[param.getTakeEffect()].isCode());
        }
        if(StringUtils.isNotEmpty(param.getEipId())) {
            safetySheetSetDO.setEipId(param.getEipId());
        }
        if (StringUtils.isNotEmpty(param.getInfo())) {
            safetySheetSetDO.setInfo(param.getInfo());
        }
        PreconditonsEx.checkArgument(safetySheetSetDOMapper.updateByPrimaryKey(safetySheetSetDO) == 1, ResponseEnum.FAIL);
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("表单设置");
        log.setAbility("修改");
        log.setMessage(safetySheetSetDO.getVisitor());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 数据新增或者整体保存
     */
    public void saveOrUpdate(String sheetSet, String tocken) {
        if(StringUtils.isEmpty(sheetSet)) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"参数不能为空");
        }
        List<SheetSetParam> paramList = JsonUtils.parseList(sheetSet, SheetSetParam.class);
        SafetySheetSetDO safetySheetSetDO = null;
        for (SheetSetParam param : paramList) {
            safetySheetSetDO = safetySheetSetDOMapper.selectByPrimaryKey(param.getId());
            safetySheetSetDO.setMustSet(SheetSetEnum.values()[param.getMustSet()].isCode());
            safetySheetSetDO.setTakeEffect(SheetSetEnum.values()[param.getTakeEffect()].isCode());
            safetySheetSetDO.setVisitor(param.getVisitor());
            safetySheetSetDO.setEipId(param.getEipId());
            safetySheetSetDO.setInfo(param.getInfo());
            safetySheetSetDOMapper.updateByPrimaryKey(safetySheetSetDO);
        }

        //操作日志记录
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("表单设置");
        log.setAbility("修改");
        log.setMessage(safetySheetSetDO.getVisitor());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 数据添加功能
     * @param param
     */
    public void save(SheetSetAddParam param, String tocken) {
        SafetySheetSetDO safetySheetSetDO = new SafetySheetSetDO();
        safetySheetSetDO.setCreateTime(new Date());
        safetySheetSetDO.setEipId(param.getEipId());
        safetySheetSetDO.setVisitor(param.getVisitor());
        safetySheetSetDO.setTakeEffect(false);
        safetySheetSetDO.setMustSet(false);
        safetySheetSetDO.setDeleteFalg(false);
        safetySheetSetDO.setInfo(param.getInfo());
        PreconditonsEx.checkArgument(safetySheetSetDOMapper.insert(safetySheetSetDO) == 1, ResponseEnum.FAIL);
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("表单设置");
        log.setAbility("添加");
        log.setMessage(safetySheetSetDO.getVisitor());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 数据删除操作
     * @param id
     */
    public void delete(Integer id, String tocken) {
        SafetySheetSetDO safetySheetSetDO = safetySheetSetDOMapper.selectByPrimaryKey(id);
        if(safetySheetSetDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        safetySheetSetDO.setDeleteFalg(true);
        PreconditonsEx.checkArgument(safetySheetSetDOMapper.updateByPrimaryKey(safetySheetSetDO) == 1, ResponseEnum.FAIL);

        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("表单设置");
        log.setAbility("删除");
        log.setMessage(safetySheetSetDO.getVisitor());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 心跳检查接口
     * @return
     */
    public long getHeartBeatInfo() {
        SafetySheetSetDOExample example = new SafetySheetSetDOExample();
        SafetySheetSetDOExample.Criteria criteria = example.createCriteria();
        criteria.andTakeEffectEqualTo(true);
        return safetySheetSetDOMapper.countByExample(example);
    }

}
