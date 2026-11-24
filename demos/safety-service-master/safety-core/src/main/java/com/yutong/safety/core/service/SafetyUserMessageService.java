package com.yutong.safety.core.service;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.*;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.SafetyUserMessageDO;
import com.yutong.safety.core.db.entity.SafetyUserMessageDOExample;
import com.yutong.safety.core.db.mapper.SafetyUserMessageDOBizMapper;
import com.yutong.safety.core.db.mapper.SafetyUserMessageDOMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  create gwcuiyx 2023.07.13
 *  用户管理--service
 */
@Service
public class SafetyUserMessageService {
    @Autowired
    private SafetyUserMessageDOMapper safetyUserMessageDOMapper;
    @Autowired
    private SafetyUserMessageDOBizMapper safetyUserMessageDOBizMapper;
    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    /**
     * 数据列表分页
     * @param param
     * @return
     */
    public List<SafetyUserMessageDO> pageList(UserMessageListParam param) {
        SafetyUserMessageDOExample example = new SafetyUserMessageDOExample();
        SafetyUserMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if(StringUtils.isNotEmpty(param.getEmpNo())) {
            criteria.andEmpNoLike(param.getEmpNo());
        }
        if(StringUtils.isNotEmpty(param.getEmpName())) {
            criteria.andEmpNameLike(param.getEmpName());
        }
        example.setOrderByClause(" create_time desc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        return safetyUserMessageDOMapper.selectByExample(example);
    }

    /**
     * 总数据量
     * @param param
     * @return
     */
    public long pageListCount(UserMessageListParam param) {
        SafetyUserMessageDOExample example = new SafetyUserMessageDOExample();
        SafetyUserMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if(StringUtils.isNotEmpty(param.getEmpNo())) {
            criteria.andEmpNoLike(param.getEmpNo());
        }
        if(StringUtils.isNotEmpty(param.getEmpName())) {
            criteria.andEmpNameLike(param.getEmpName());
        }
        return safetyUserMessageDOMapper.countByExample(example);
    }

    /**
     * 数据导出功能
     * @param param
     * @return
     */
    public List<SafetyUserMessageDO> export(UserMessageListParam param, String tocken) {
        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        SafetyUserMessageDOExample example = new SafetyUserMessageDOExample();
        SafetyUserMessageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if(StringUtils.isNotEmpty(param.getEmpNo())) {
            criteria.andEmpNoLike(param.getEmpNo());
        }
        if(StringUtils.isNotEmpty(param.getEmpName())) {
            criteria.andEmpNameLike(param.getEmpName());
        }
        example.setOrderByClause(" create_time desc ");
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("用户管理");
        log.setAbility("导出");
        log.setMessage("导出");
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        return safetyUserMessageDOMapper.selectByExample(example);
    }

    /**
     * 数据导入操作
     * @param param
     * @return
     */
    public Object importUserMessage(CommonImportParam param, String tocken) {
        Workbook workbook = ExcelUtils.getWorkbook(param.getFile());
        Sheet sheet = workbook.getSheetAt(0);
        //获得当前sheet的开始行
        int firstRowNum  = sheet.getFirstRowNum();
        //获得当前sheet的结束行
        int lastRowNum = sheet.getLastRowNum();
        String result = "";
        List<SafetyUserMessageDO> insertList = Lists.newArrayList();
        SafetyUserMessageDO safetyUserMessageDO = null;
        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++) {
            //获得当前行
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            try {
                String empName = ExcelUtils.getValue(row.getCell(1));
                String empNo = ExcelUtils.getValue(row.getCell(2));
                String role = ExcelUtils.getValue(row.getCell(3));
                String modifyName = ExcelUtils.getValue(row.getCell(4));
                safetyUserMessageDO = new SafetyUserMessageDO();
                safetyUserMessageDO.setEmpName(empName);
                safetyUserMessageDO.setEmpNo(empNo);
                safetyUserMessageDO.setRole(empName);
                safetyUserMessageDO.setModifyEmpNo(modifyName);
                safetyUserMessageDO.setCreateTime(new Date());
                safetyUserMessageDO.setUpdateTime(new Date());
                safetyUserMessageDO.setDeleteFalg(false);
                insertList.add(safetyUserMessageDO);
            } catch (Exception e) {
                LogUtils.error("数据第"+rowNum+"导入行读取异常",e);
            }
            //操作日志记录
            LogMessageAddParam log = new LogMessageAddParam();
            log.setOperatePage("用户管理");
            log.setAbility("导入");
            log.setMessage("导入");
            log.setOperator(tockenUser.get("realName"));
            safetyLogMessageService.addLogMessage(log);
            if(CollectionUtils.isNotEmpty(insertList)) {
                //批量导入操作
                safetyUserMessageDOBizMapper.batchInsert(insertList);
            }
        }
        return result;
    }

    /**
     * 新增操作功能
     * @param param
     */
    public void add(UserMessageAddParam param, String tocken) {
        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        SafetyUserMessageDO safetyUserMessageDO = new SafetyUserMessageDO();
        BeanUtils.copyProperties(param, safetyUserMessageDO);
        safetyUserMessageDO.setDeleteFalg(false);
        safetyUserMessageDO.setCreateTime(new Date());
        safetyUserMessageDO.setUpdateTime(new Date());
        PreconditonsEx.checkArgument(safetyUserMessageDOMapper.insert(safetyUserMessageDO) == 1, ResponseEnum.FAIL);

        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("用户管理");
        log.setAbility("添加");
        log.setMessage(param.getRole());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 数据更新操作
     * @param param
     */
    public void update(UserMessageUpdateParam param, String tocken) {
        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        SafetyUserMessageDO safetyUserMessageDO = safetyUserMessageDOMapper.selectByPrimaryKey(param.getId());
        if(safetyUserMessageDO == null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        BeanUtils.copyProperties(param, safetyUserMessageDO);
        safetyUserMessageDO.setUpdateTime(new Date());
        PreconditonsEx.checkArgument(safetyUserMessageDOMapper.updateByPrimaryKey(safetyUserMessageDO) == 1, ResponseEnum.FAIL);

        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("用户管理");
        log.setAbility("更新");
        log.setMessage(param.getRole());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 数据展示
     * @param id
     * @return
     */
    public SafetyUserMessageDO info(Integer id) {
        SafetyUserMessageDO safetyUserMessageDO = safetyUserMessageDOMapper.selectByPrimaryKey(id);
        if(safetyUserMessageDO == null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        return safetyUserMessageDO;
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    public void delete(Integer id, String tocken) {
        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        SafetyUserMessageDO safetyUserMessageDO = safetyUserMessageDOMapper.selectByPrimaryKey(id);
        if(safetyUserMessageDO == null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        safetyUserMessageDO.setDeleteFalg(true);
        PreconditonsEx.checkArgument(safetyUserMessageDOMapper.updateByPrimaryKey(safetyUserMessageDO) == 1, ResponseEnum.FAIL);

        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("用户管理");
        log.setAbility("删除");
        log.setMessage(safetyUserMessageDO.getRole());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

}
