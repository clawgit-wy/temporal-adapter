package com.yutong.safety.core.service;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.RegionDeviceEnum;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.*;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.web.FactoryRegionVO;
import com.yutong.safety.core.db.entity.SafetyRegionDeviceDO;
import com.yutong.safety.core.db.entity.SafetyRegionDeviceDOExample;
import com.yutong.safety.core.db.mapper.SafetyRegionDeviceDOBizMapper;
import com.yutong.safety.core.db.mapper.SafetyRegionDeviceDOMapper;
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
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *  create gwcuiyx 2023.07.17
 *  区域设备绑定--service
 */
@Service
public class SafetyRegionDeviceService {
    @Autowired
    private SafetyRegionDeviceDOMapper safetyRegionDeviceDOMapper;

    @Autowired
    private SafetyRegionDeviceDOBizMapper safetyRegionDeviceDOBizMapper;
    @Autowired
    private SafetyLogMessageService safetyLogMessageService;
    @Autowired
    private SafetyFieldManageService safetyFieldManageService;

    /**
     * 数据分页列表查询
     * @param param
     * @return
     */
    public List<SafetyRegionDeviceDO> pageList(RegionDeviceListParam param) {
        SafetyRegionDeviceDOExample example = new SafetyRegionDeviceDOExample();
        SafetyRegionDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if (StringUtils.isNotEmpty(param.getDeviceNo())) {
            criteria.andDeviceNoLike("%"+param.getDeviceNo()+"%");
        }
        if(StringUtils.isNotEmpty(param.getDeviceName())) {
            criteria.andDeviceNameLike("%"+param.getDeviceName()+"%");
        }
        if (Objects.nonNull(param.getFactoryId())) {
            criteria.andFactoryIdEqualTo(param.getFactoryId());
        }
        if(StringUtils.isNotEmpty(param.getFactoryName())) {
            criteria.andFactoryNameLike("%"+param.getFactoryName()+"%");
        }
        if(StringUtils.isNotEmpty(param.getRegionName())) {
            criteria.andRegionNameLike("%"+param.getRegionName()+"%");
        }
        if(Objects.nonNull(param.getDeviceType())) {
            //读卡器、人脸平板、二维码识别
            criteria.andDeviceTypeEqualTo(param.getDeviceType());
        }
        example.setOrderByClause(" id asc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        return safetyRegionDeviceDOMapper.selectByExample(example);
    }

    /**
     * 总数据量
     * @param param
     * @return
     */
    public long pageListCount(RegionDeviceListParam param) {
        SafetyRegionDeviceDOExample example = new SafetyRegionDeviceDOExample();
        SafetyRegionDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if (StringUtils.isNotEmpty(param.getDeviceNo())) {
            criteria.andDeviceNoLike("%"+param.getDeviceNo()+"%");
        }
        if(StringUtils.isNotEmpty(param.getDeviceName())) {
            criteria.andDeviceNameLike("%"+param.getDeviceName()+"%");
        }
        if (Objects.nonNull(param.getFactoryId())) {
            criteria.andFactoryIdEqualTo(param.getFactoryId());
        }
        if(StringUtils.isNotEmpty(param.getFactoryName())) {
            criteria.andFactoryNameLike("%"+param.getFactoryName()+"%");
        }
        if(StringUtils.isNotEmpty(param.getRegionName())) {
            criteria.andRegionNameLike("%"+param.getRegionName()+"%");
        }
        if(Objects.nonNull(param.getDeviceType())) {
            //读卡器、人脸平板、二维码识别
            criteria.andDeviceTypeEqualTo(param.getDeviceType());
        }
        return safetyRegionDeviceDOMapper.countByExample(example);
    }

    /**
     * 详情数据
     * @param id
     * @return
     */
    public SafetyRegionDeviceDO view(Integer id) {
        SafetyRegionDeviceDO safetyRegionDeviceDO = safetyRegionDeviceDOMapper.selectByPrimaryKey(id);
        if(safetyRegionDeviceDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        return safetyRegionDeviceDO;
    }

    /**
     * 数据删除操作
     * @param id
     */
    public void delete(Integer id, String tocken) {
        SafetyRegionDeviceDO safetyRegionDeviceDO = safetyRegionDeviceDOMapper.selectByPrimaryKey(id);
        if(safetyRegionDeviceDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        safetyRegionDeviceDO.setDeleteFalg(true);
        safetyRegionDeviceDOMapper.updateByPrimaryKey(safetyRegionDeviceDO);

        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("区域设备绑定");
        log.setAbility("删除");
        log.setMessage(safetyRegionDeviceDO.getDeviceName());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 数据添加功能
     * @param param
     */
    public void add(RegionDeviceAddParam param, String tocken) {
        SafetyRegionDeviceDO safetyRegionDeviceDO = getRegionDevice(param.getDeviceNo(), param.getDeviceType());
        if (safetyRegionDeviceDO == null) {
            safetyRegionDeviceDO = new SafetyRegionDeviceDO();
        } else {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该设备号已存在！");
        }
        BeanUtils.copyProperties(param, safetyRegionDeviceDO);
        safetyRegionDeviceDO.setDeleteFalg(false);
        safetyRegionDeviceDO.setCreateTime(new Date());
        safetyRegionDeviceDO.setUpdateTime(new Date());
        PreconditonsEx.checkArgument(safetyRegionDeviceDOMapper.insert(safetyRegionDeviceDO) == 1, ResponseEnum.FAIL);
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("区域设备绑定");
        log.setAbility("添加");
        log.setMessage(safetyRegionDeviceDO.getDeviceName());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 根据设备号获取数据
     * @param deviceNo
     * @return
     */
    public SafetyRegionDeviceDO getRegionDevice(String deviceNo, Integer deviceType) {
        SafetyRegionDeviceDOExample example = new SafetyRegionDeviceDOExample();
        SafetyRegionDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andDeviceNoEqualTo(deviceNo);
        criteria.andDeviceTypeEqualTo(deviceType);
        List<SafetyRegionDeviceDO> list = safetyRegionDeviceDOMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 数据更新操作
     * @param param
     */
    public void update(RegionDeviceUpdateParam param, String tocken) {
        SafetyRegionDeviceDO safetyRegionDeviceDO = safetyRegionDeviceDOMapper.selectByPrimaryKey(param.getId());
        if(safetyRegionDeviceDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        BeanUtils.copyProperties(param, safetyRegionDeviceDO);
        safetyRegionDeviceDO.setUpdateTime(new Date());
        PreconditonsEx.checkArgument(safetyRegionDeviceDOMapper.updateByPrimaryKey(safetyRegionDeviceDO) == 1, ResponseEnum.FAIL);

        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("区域设备绑定");
        log.setAbility("修改");
        log.setMessage(safetyRegionDeviceDO.getDeviceName());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 数据导出操作
     * @param param
     * @return
     */
    public List<SafetyRegionDeviceDO> export(RegionDeviceListParam param, String tocken) {
        SafetyRegionDeviceDOExample example = new SafetyRegionDeviceDOExample();
        SafetyRegionDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        String searchStr = "";
        if (StringUtils.isNotEmpty(param.getDeviceNo())) {
            criteria.andDeviceNoLike("%"+param.getDeviceNo()+"%");
            searchStr += param.getDeviceNo() + ";";
        }
        if(StringUtils.isNotEmpty(param.getDeviceName())) {
            criteria.andDeviceNameLike("%"+param.getDeviceName()+"%");
            searchStr += param.getDeviceName() + ";";
        }
        if (Objects.nonNull(param.getFactoryId())) {
            criteria.andFactoryIdEqualTo(param.getFactoryId());
            searchStr += param.getFactoryId() + ";";
        }
        if(StringUtils.isNotEmpty(param.getFactoryName())) {
            criteria.andFactoryNameLike("%"+param.getFactoryName()+"%");
            searchStr += param.getFactoryName() + ";";
        }
        if(StringUtils.isNotEmpty(param.getRegionName())) {
            criteria.andRegionNameLike("%"+param.getRegionName()+"%");
            searchStr += param.getRegionName() + ";";
        }
        if(Objects.nonNull(param.getDeviceType())) {
            //读卡器、人脸平板、二维码识别
            criteria.andDeviceTypeEqualTo(param.getDeviceType());
            searchStr += param.getDeviceType() + ";";
        }
        example.setOrderByClause(" id asc ");
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("区域设备绑定");
        log.setAbility("导出");
        log.setMessage(StringUtils.isNotEmpty(searchStr) ? searchStr : "全部");
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        return safetyRegionDeviceDOMapper.selectByExample(example);
    }

    /**
     * 数据导入操作
     * @param param
     * @return
     */
    public Object importRegionDevice(CommonImportParam param, String tocken) {
        Workbook workbook = ExcelUtils.getWorkbook(param.getFile());
        Sheet sheet = workbook.getSheetAt(0);
        //获得当前sheet的开始行
        int firstRowNum  = sheet.getFirstRowNum();
        //获得当前sheet的结束行
        int lastRowNum = sheet.getLastRowNum();
        String result = "";
        List<SafetyRegionDeviceDO> insertList = Lists.newArrayList();
        SafetyRegionDeviceDO regionDeviceDO = null;
        SafetyRegionDeviceDO safetyRegionDeviceDO = null;
        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //获取字段管理里面厂区和区域的数据
        List<FactoryRegionVO> factoryRegionList = safetyFieldManageService.getFactoryRegionList();
        Map<String, FactoryRegionVO> factoryRegionVOMap = factoryRegionList.stream().collect(Collectors.toMap(factoryRegionVO -> factoryRegionVO.getParentName()+"_"+factoryRegionVO.getSonName(), Function.identity()));
        FactoryRegionVO factoryRegionVO = null;
        boolean flag = false;
        int rum = 0;
        String expenseMessage = "";
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
                //设备类型
                String deviceTypeName = ExcelUtils.getValue(row.getCell(3));
                Integer deviceType = 0;
                if(deviceTypeName.equals(RegionDeviceEnum.CARDREADR.getName())) {
                    deviceType = 0;
                } else if(deviceTypeName.equals(RegionDeviceEnum.FACETABLET.getName())) {
                    deviceType = 1;
                } else if(deviceTypeName.equals(RegionDeviceEnum.QRCODE.getName())) {
                    deviceType = 2;
                } else if(deviceTypeName.equals(RegionDeviceEnum.LANE.getName())) {
                    deviceType = 3;
                }
                String deviceNo = ExcelUtils.getValue(row.getCell(4));
                String deviceName = ExcelUtils.getValue(row.getCell(5));
                String remark = ExcelUtils.getValue(row.getCell(6));
                if(StringUtils.isEmpty(factoryName) && StringUtils.isEmpty(regionName) && StringUtils.isEmpty(deviceTypeName)
                        && StringUtils.isEmpty(deviceNo) && StringUtils.isEmpty(deviceName)){
                    continue;
                }
                if(StringUtils.isEmpty(factoryName) || StringUtils.isEmpty(regionName) || StringUtils.isEmpty(deviceTypeName)
                        || StringUtils.isEmpty(deviceNo) || StringUtils.isEmpty(deviceName)){
                    rum = rowNum;
                    flag = true;
                    expenseMessage = "第"+rum+"行，有必填项为空！请检查后再导入数据！";
                    break;
                }
                safetyRegionDeviceDO = getRegionDevice(deviceNo, deviceType);
                if (safetyRegionDeviceDO != null) {
                    rum = rowNum;
                    flag = true;
                    expenseMessage = "第"+rum+"行，设备号重复！请检查后再导入数据！";
                    break;
                }
                regionDeviceDO = new SafetyRegionDeviceDO();
                regionDeviceDO.setFactoryName(factoryName);
                regionDeviceDO.setRegionName(regionName);
                regionDeviceDO.setDeviceType(deviceType);
                regionDeviceDO.setDeviceNo(deviceNo);
                regionDeviceDO.setDeviceName(deviceName);
                regionDeviceDO.setRemark(remark);
                regionDeviceDO.setDeleteFalg(false);
                regionDeviceDO.setCreateTime(new Date());
                regionDeviceDO.setUpdateTime(new Date());
                if (factoryRegionVO != null) {
                    regionDeviceDO.setFactoryId(factoryRegionVO.getParentId());
                    regionDeviceDO.setRegionId(factoryRegionVO.getSonId());
                }
                insertList.add(regionDeviceDO);
            } catch (Exception e) {
                LogUtils.error("数据第"+rowNum+"导入行读取异常",e);
            }
        }
        if (flag) {
            throw new BizException(ResponseEnum.FAIL.getCode(), expenseMessage);
        }
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("区域设备绑定");
        log.setAbility("导入");
        log.setMessage("导入");
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        if(CollectionUtils.isNotEmpty(insertList)){
            safetyRegionDeviceDOBizMapper.batchInsert(insertList);
        }
        result = "导入成功:"+insertList.size()+"条!";
        return result;
    }

    /**
     * 根据厂区和区域id获取对应数据列表
     * @param factoryId
     * @param regionId
     * @return
     */
    public List<String> getDeviceNos(Integer factoryId, Integer regionId) {
        if(Objects.isNull(factoryId)) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"factoryId不能为空");
        }
        if (Objects.isNull(regionId)) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"factoryId不能为空");
        }
        SafetyRegionDeviceDOExample example = new SafetyRegionDeviceDOExample();
        SafetyRegionDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andFactoryIdEqualTo(factoryId);
        criteria.andRegionIdEqualTo(regionId);
        List<SafetyRegionDeviceDO> list = safetyRegionDeviceDOMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().map(SafetyRegionDeviceDO::getDeviceNo).collect(Collectors.toList());
        }
        return null;
    }

}
