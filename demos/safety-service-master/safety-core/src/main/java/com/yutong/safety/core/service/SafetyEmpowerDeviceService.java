package com.yutong.safety.core.service;

import com.google.common.collect.Lists;
import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.*;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.web.FactoryRegionVO;
import com.yutong.safety.core.db.entity.SafetyEmpowerDeviceDO;
import com.yutong.safety.core.db.entity.SafetyEmpowerDeviceDOExample;
import com.yutong.safety.core.db.mapper.SafetyEmpowerDeviceDOBizMapper;
import com.yutong.safety.core.db.mapper.SafetyEmpowerDeviceDOMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *  create gwcuiyx 2023.07.17
 *  授权区域设备管理--service
 */
@Service
public class SafetyEmpowerDeviceService {

    @Autowired
    private SafetyEmpowerDeviceDOMapper safetyEmpowerDeviceDOMapper;
    @Autowired
    private SafetyLogMessageService safetyLogMessageService;
    @Autowired
    private SafetyFieldManageService safetyFieldManageService;
    @Autowired
    private SafetyEmpowerDeviceDOBizMapper safetyEmpowerDeviceDOBizMapper;


    /**
     * 数据分页列表查询
     * @param param
     * @return
     */
    public List<SafetyEmpowerDeviceDO> selectList(EmpowerDeviceListParam param) {
        SafetyEmpowerDeviceDOExample example = new SafetyEmpowerDeviceDOExample();
        SafetyEmpowerDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if(StringUtils.isNotEmpty(param.getFactoryName())) {
            criteria.andFactoryNameEqualTo(param.getFactoryName());
        }
        if(StringUtils.isNotEmpty(param.getEmpowerType())){
            criteria.andEmpowerTypeEqualTo(param.getEmpowerType());
        }
        if(StringUtils.isNotEmpty(param.getRegionName())) {
            criteria.andRegionNameLike("%"+param.getRegionName()+"%");
        }
        if(StringUtils.isNotEmpty(param.getEmpowerNo())) {
            criteria.andEmpowerNoLike("%"+param.getEmpowerNo()+"%");
        }
        if (Objects.nonNull(param.getFactoryId())) {
            criteria.andFactoryIdEqualTo(param.getFactoryId());
        }
        if(!CollectionUtils.isEmpty(param.getFactoryIdList())){
            criteria.andFactoryIdIn(param.getFactoryIdList());
        }
        if (StringUtils.isNotEmpty(param.getRegionId())) {
            criteria.andRegionNameLike("%"+param.getRegionId()+"%");
        }
        example.setOrderByClause(" create_time desc ");
        return safetyEmpowerDeviceDOMapper.selectByExample(example);
    }

    /**
     * 数据分页列表查询
     * @param param
     * @return
     */
    public List<SafetyEmpowerDeviceDO> pageList(EmpowerDeviceListParam param) {
        SafetyEmpowerDeviceDOExample example = new SafetyEmpowerDeviceDOExample();
        SafetyEmpowerDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if(StringUtils.isNotEmpty(param.getFactoryName())) {
            criteria.andFactoryNameEqualTo(param.getFactoryName());
        }
        if(StringUtils.isNotEmpty(param.getRegionName())) {
            criteria.andRegionNameLike("%"+param.getRegionName()+"%");
        }
        if(StringUtils.isNotEmpty(param.getEmpowerNo())) {
            criteria.andEmpowerNoLike("%"+param.getEmpowerNo()+"%");
        }
        if (Objects.nonNull(param.getFactoryId())) {
            criteria.andFactoryIdEqualTo(param.getFactoryId());
        }
        if (StringUtils.isNotEmpty(param.getRegionId())) {
            criteria.andRegionNameLike("%"+param.getRegionId()+"%");
        }
        example.setOrderByClause(" create_time desc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        return safetyEmpowerDeviceDOMapper.selectByExample(example);
    }

    /**
     * 总数据量
     * @param param
     * @return
     */
    public long pageListCount(EmpowerDeviceListParam param) {
        SafetyEmpowerDeviceDOExample example = new SafetyEmpowerDeviceDOExample();
        SafetyEmpowerDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if(StringUtils.isNotEmpty(param.getFactoryName())) {
            criteria.andFactoryNameEqualTo(param.getFactoryName());
        }
        if(StringUtils.isNotEmpty(param.getRegionName())) {
            criteria.andRegionNameLike("%"+param.getRegionName()+"%");
        }
        if(StringUtils.isNotEmpty(param.getEmpowerNo())) {
            criteria.andEmpowerNoLike("%"+param.getEmpowerNo()+"%");
        }
        if (Objects.nonNull(param.getFactoryId())) {
            criteria.andFactoryIdEqualTo(param.getFactoryId());
        }
        if (StringUtils.isNotEmpty(param.getRegionId())) {
            criteria.andRegionNameLike("%"+param.getRegionId()+"%");
        }
        return safetyEmpowerDeviceDOMapper.countByExample(example);
    }

    /**
     * 详情数据
     * @param id
     * @return
     */
    public SafetyEmpowerDeviceDO view(Integer id) {
        SafetyEmpowerDeviceDO safetyEmpowerDeviceDO = safetyEmpowerDeviceDOMapper.selectByPrimaryKey(id);
        if(safetyEmpowerDeviceDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        return safetyEmpowerDeviceDO;
    }

    /**
     * 数据删除操作
     * @param id
     */
    public void delete(Integer id, String tocken) {
        SafetyEmpowerDeviceDO safetyEmpowerDeviceDO = safetyEmpowerDeviceDOMapper.selectByPrimaryKey(id);
        if(safetyEmpowerDeviceDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        safetyEmpowerDeviceDO.setDeleteFalg(true);
        safetyEmpowerDeviceDOMapper.updateByPrimaryKey(safetyEmpowerDeviceDO);

        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("授权区域设备管理");
        log.setAbility("删除");
        log.setMessage(safetyEmpowerDeviceDO.getRegionName());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 数据更新操作
     * @param param
     */
    public void update(EmpowerDeviceUpdateParam param, String tocken) {
        SafetyEmpowerDeviceDO safetyEmpowerDeviceDO = safetyEmpowerDeviceDOMapper.selectByPrimaryKey(param.getId());
        if(safetyEmpowerDeviceDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        BeanUtils.copyProperties(param, safetyEmpowerDeviceDO);
        safetyEmpowerDeviceDO.setUpdateTime(new Date());
        PreconditonsEx.checkArgument(safetyEmpowerDeviceDOMapper.updateByPrimaryKey(safetyEmpowerDeviceDO) == 1, ResponseEnum.FAIL);

        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("授权区域设备管理");
        log.setAbility("添加");
        log.setMessage(safetyEmpowerDeviceDO.getRegionName());
        log.setOperator(tockenUser.get("realName"));
    }

    /**
     * 数据导出操作
     * @param param
     * @return
     */
    public List<SafetyEmpowerDeviceDO> export(EmpowerDeviceListParam param, String tocken) {
        SafetyEmpowerDeviceDOExample example = new SafetyEmpowerDeviceDOExample();
        SafetyEmpowerDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        String searchStr = "";
        if(StringUtils.isNotEmpty(param.getFactoryName())) {
            criteria.andFactoryNameEqualTo(param.getFactoryName());
            searchStr += param.getFactoryName() + ";";
        }
        if(StringUtils.isNotEmpty(param.getRegionName())) {
            criteria.andRegionNameLike("%"+param.getRegionName()+"%");
            searchStr += param.getRegionName() + ";";
        }
        if(StringUtils.isNotEmpty(param.getEmpowerNo())) {
            criteria.andEmpowerNoLike("%"+param.getEmpowerNo()+"%");
            searchStr += param.getEmpowerNo() + ";";
        }
        if (Objects.nonNull(param.getFactoryId())) {
            criteria.andFactoryIdEqualTo(param.getFactoryId());
            searchStr += param.getFactoryId() + ";";
        }
        if (StringUtils.isNotEmpty(param.getRegionId())) {
            criteria.andRegionNameLike("%"+param.getRegionId()+"%");
            searchStr += param.getRegionId() + ";";
        }
        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("授权区域设备管理");
        log.setAbility("导出");
        log.setMessage(searchStr);
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        return safetyEmpowerDeviceDOMapper.selectByExample(example);
    }

    /**
     * 数据导入操作
     * @param param
     * @return
     */
    public Object importEmpowerDevice(CommonImportParam param, String tocken) {
        Workbook workbook = ExcelUtils.getWorkbook(param.getFile());
        Sheet sheet = workbook.getSheetAt(0);
        //获得当前sheet的开始行
        int firstRowNum  = sheet.getFirstRowNum();
        //获得当前sheet的结束行
        int lastRowNum = sheet.getLastRowNum();
        String result = "";
        List<SafetyEmpowerDeviceDO> insertList = Lists.newArrayList();
        SafetyEmpowerDeviceDO empowerDeviceDO = null;
        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //获取字段管理里面厂区和区域的数据
        List<FactoryRegionVO> factoryRegionList = safetyFieldManageService.getFactoryRegionList();
        Map<String, FactoryRegionVO> factoryRegionVOMap = factoryRegionList.stream().collect(Collectors.toMap(factoryRegionVO -> factoryRegionVO.getParentName()+"_"+factoryRegionVO.getSonName(), Function.identity()));
        FactoryRegionVO factoryRegionVO = null;
        boolean flag = false;
        int rum = 0;
        for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++) {
            //获得当前行
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            try {
                String factoryName = ExcelUtils.getValue(row.getCell(1));
                String regionName = ExcelUtils.getValue(row.getCell(2));
                String factoryKey = factoryName+"_"+regionName;
                factoryRegionVO = factoryRegionVOMap.get(factoryKey);
                String empowerNo =  ExcelUtils.getValue(row.getCell(3));
                String remark = ExcelUtils.getValue(row.getCell(4));
                if(StringUtils.isEmpty(factoryName) && StringUtils.isEmpty(regionName) && StringUtils.isEmpty(empowerNo)){
                    continue;
                }
                if(StringUtils.isEmpty(factoryName) || StringUtils.isEmpty(regionName) || StringUtils.isEmpty(empowerNo)){
                    rum = rowNum;
                    flag = true;
                    break;
                }
                empowerDeviceDO = new SafetyEmpowerDeviceDO();
                empowerDeviceDO.setFactoryName(factoryName);
                empowerDeviceDO.setRegionName(regionName);
                empowerDeviceDO.setEmpowerNo(empowerNo);
                empowerDeviceDO.setCreateTime(new Date());
                empowerDeviceDO.setUpdateTime(new Date());
                empowerDeviceDO.setDeleteFalg(false);
                if (factoryRegionVO != null) {
                    empowerDeviceDO.setFactoryId(factoryRegionVO.getParentId());
                    empowerDeviceDO.setRegionId(factoryRegionVO.getSonId());
                }
                empowerDeviceDO.setRemark(remark);
                insertList.add(empowerDeviceDO);
            } catch (Exception e) {
                LogUtils.error("数据第"+rowNum+"导入行读取异常",e);
            }
        }
        if (flag) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"第"+rum+"行，有必填项为空！请检查后再导入数据！");
        }
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("授权区域设备管理");
        log.setAbility("导入");
        log.setMessage("导入");
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        if(CollectionUtils.isNotEmpty(insertList)){
            safetyEmpowerDeviceDOBizMapper.batchInsert(insertList);
        }
        result = "导入成功:"+insertList.size()+"条!";
        return result;
    }

    /**
     * 数据添加功能
     * @param param
     */
    public void add(EmpowerDeviceAddParam param, String tocken) {
        SafetyEmpowerDeviceDO safetyEmpowerDeviceDO = new SafetyEmpowerDeviceDO();
        BeanUtils.copyProperties(param, safetyEmpowerDeviceDO);
        safetyEmpowerDeviceDO.setDeleteFalg(false);
        safetyEmpowerDeviceDO.setCreateTime(new Date());
        safetyEmpowerDeviceDO.setUpdateTime(new Date());
        PreconditonsEx.checkArgument(safetyEmpowerDeviceDOMapper.insert(safetyEmpowerDeviceDO) == 1, ResponseEnum.FAIL);

        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("授权区域设备管理");
        log.setAbility("添加");
        log.setMessage(safetyEmpowerDeviceDO.getRegionName());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    public SafetyEmpowerDeviceDO getEmpowerDeviceByFactoryId(Integer factoryId) {
        SafetyEmpowerDeviceDOExample example = new SafetyEmpowerDeviceDOExample();
        SafetyEmpowerDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andFactoryIdEqualTo(factoryId);
        List<SafetyEmpowerDeviceDO> list = safetyEmpowerDeviceDOMapper.selectByExample(example);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据工厂id和区域获取设备号
     * @param factroyId
     * @param regionId
     * @return
     */
    public int[] getEmpowerNos(String factroyId, String regionId, String deviceType, String authFactory) {
        if (StringUtils.isEmpty(factroyId)) {
            return null;
        }
        String[] factoryIds = factroyId.split(";");
        List<Integer> factoryList = Lists.newArrayList();
        authFactory = StringUtils.isNotEmpty(authFactory) ? authFactory+"," : authFactory;
        authFactory = StringUtils.isNotEmpty(authFactory) ? authFactory.replaceAll(";",",") : authFactory;
        for (String s : factoryIds) {
            if (StringUtils.isNotEmpty(authFactory)) {
                if (authFactory.indexOf(s+",") > -1) {
                    factoryList.add(Integer.valueOf(s));
                }
            } else {
                factoryList.add(Integer.valueOf(s));
            }
        }
        SafetyEmpowerDeviceDOExample example = new SafetyEmpowerDeviceDOExample();
        SafetyEmpowerDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if(CollectionUtils.isEmpty(factoryList)){
            factoryList.add(0);
        }
        criteria.andFactoryIdIn(factoryList);
        criteria.andRegionNameLike("%"+deviceType+"%");
        criteria.andEmpowerTypeEqualTo("访客");
        List<SafetyEmpowerDeviceDO> list = safetyEmpowerDeviceDOMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(list)) {
            String empowerNos = "";
            for (SafetyEmpowerDeviceDO empowerDeviceDO : list) {
                if (StringUtils.isNotEmpty(regionId)) {
                    if (empowerDeviceDO.getRegionName().indexOf("大门") > -1) {
                        empowerNos += empowerDeviceDO.getEmpowerNo() + ",";
                    } else {
                        if (StringUtils.isNotEmpty(empowerDeviceDO.getEmpowerNo()) && regionId.indexOf(String.valueOf(empowerDeviceDO.getRegionId())) > -1 && !"/".equals(empowerDeviceDO.getEmpowerNo())) {
                            empowerNos += empowerDeviceDO.getEmpowerNo() + ",";
                        }
                    }
                } else {
                    if (StringUtils.isNotEmpty(empowerDeviceDO.getEmpowerNo()) && !"/".equals(empowerDeviceDO.getEmpowerNo())) {
                        empowerNos += empowerDeviceDO.getEmpowerNo() + ",";
                    }
                }
            }
            //组装数据完成
            if (StringUtils.isNotEmpty(empowerNos)) {
                empowerNos = empowerNos.substring(0, empowerNos.length()-1);
                String[] emList = empowerNos.split(",");
                int[] em = Arrays.stream(emList).mapToInt(Integer::parseInt).toArray();
                return em;
            }
        }
        return null;
    }

    public List<SafetyEmpowerDeviceDO> selectList() {
        SafetyEmpowerDeviceDOExample example = new SafetyEmpowerDeviceDOExample();
        SafetyEmpowerDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andFactoryIdIsNotNull();
        return safetyEmpowerDeviceDOMapper.selectByExample(example);
    }
}
