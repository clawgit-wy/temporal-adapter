package com.yutong.safety.core.service;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CarInFactoryListParam;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.LaneFactoryListParam;
import com.yutong.safety.common.param.web.RetentionWhiteAddParam;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.core.db.entity.SafetyRetentionWhiteDO;
import com.yutong.safety.core.db.entity.SafetyRetentionWhiteDOExample;
import com.yutong.safety.core.db.mapper.SafetyRetentionBizMapper;
import com.yutong.safety.core.db.mapper.SafetyRetentionWhiteDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 滞留车提醒-白名单管理Service
 * @author gwxiexj
 * @since 2024-04-11 16:08
 */
@Service
public class SafetyRetentionWhiteService {

    @Autowired
    private SafetyRetentionWhiteDOMapper safetyRetentionWhiteDOMapper;

    @Autowired
    private SafetyLaneFactoryService safetyLaneFactoryService;

    @Autowired
    private SafetyRetentionBizMapper safetyRetentionBizMapper;


    /**
     * 查询列表
     * @param param 查询-入参
     * @return 返回结果集
     */
    public List<SafetyRetentionWhiteDO> pageList(CarInFactoryListParam param){
        SafetyRetentionWhiteDOExample example = new SafetyRetentionWhiteDOExample();
        SafetyRetentionWhiteDOExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(param.getFactory())){
            criteria.andFactoryEqualTo(param.getFactory());
        }
        if(StringUtils.isNotEmpty(param.getCarNo())){
            criteria.andCarNoLike(param.getCarNo());
        }
        if(StringUtils.isNotEmpty(param.getCarType())){
            criteria.andCarTypeEqualTo(param.getCarType());
        }
        if(StringUtils.isNotEmpty(param.getStaffNo())){
            criteria.andStaffNoLike(param.getStaffNo());
        }
        if(StringUtils.isNotEmpty(param.getStaffName())){
            criteria.andStaffNameLike(param.getStaffName());
        }
        if(StringUtils.isNotEmpty(param.getRemark())){
            criteria.andRemarkLike(param.getRemark());
        }
        example.setOrderByClause(" update_time desc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        return safetyRetentionWhiteDOMapper.selectByExample(example);
    }

    /**
     * 分页总条数
     *
     * @param param 查询入参
     * @return 返回
     */
    public long pageListCount(CarInFactoryListParam param) {
        SafetyRetentionWhiteDOExample example = new SafetyRetentionWhiteDOExample();
        SafetyRetentionWhiteDOExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(param.getFactory())){
            criteria.andFactoryEqualTo(param.getFactory());
        }
        if(StringUtils.isNotEmpty(param.getCarNo())){
            criteria.andCarNoLike(param.getCarNo());
        }
        if(StringUtils.isNotEmpty(param.getCarType())){
            criteria.andCarTypeEqualTo(param.getCarType());
        }
        if(StringUtils.isNotEmpty(param.getStaffNo())){
            criteria.andStaffNoLike(param.getStaffNo());
        }
        if(StringUtils.isNotEmpty(param.getStaffName())){
            criteria.andStaffNameLike(param.getStaffName());
        }
        if(StringUtils.isNotEmpty(param.getRemark())){
            criteria.andRemarkLike(param.getRemark());
        }
        return safetyRetentionWhiteDOMapper.countByExample(example);
    }

    /**
     * 更新规则
     * @param param 更新入参
     * @return 返回
     * @throws Exception 异常
     */
    public int saveOrUpdate(RetentionWhiteAddParam param) throws Exception{
        int updateRows;
        SafetyRetentionWhiteDO record = new SafetyRetentionWhiteDO();
        BeanUtils.copyProperties(param,record);
        Integer id = param.getId();

        record.setUpdateTime( new Date());
        if (id == null) {
            record.setCreateTime(new Date());
            updateRows = safetyRetentionWhiteDOMapper.insert(record);
        } else {
            record.setId(param.getId());
            updateRows = safetyRetentionWhiteDOMapper.updateByPrimaryKey(record);
        }
        return updateRows;
    }

    /**
     * 做删除标记
     *
     * @param whiteId 主键
     * @return 返回
     */
    public int deleteById(List<Integer> whiteId) throws Exception {
        SafetyRetentionWhiteDOExample example = new SafetyRetentionWhiteDOExample();
        SafetyRetentionWhiteDOExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(whiteId);
        return safetyRetentionWhiteDOMapper.deleteByExample(example);
    }

    /**
     * 查询列表-不分页
     * @param param 查询-入参
     * @return 返回结果集
     */
    public List<SafetyRetentionWhiteDO> getList(CarInFactoryListParam param){
        SafetyRetentionWhiteDOExample example = new SafetyRetentionWhiteDOExample();
        SafetyRetentionWhiteDOExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(param.getFactory())){
            criteria.andFactoryEqualTo(param.getFactory());
        }
        if(StringUtils.isNotEmpty(param.getCarNo())){
            criteria.andCarNoLike(param.getCarNo());
        }
        if(StringUtils.isNotEmpty(param.getCarType())){
            criteria.andCarTypeEqualTo(param.getCarType());
        }
        if(StringUtils.isNotEmpty(param.getStaffNo())){
            criteria.andStaffNoLike(param.getStaffNo());
        }
        if(StringUtils.isNotEmpty(param.getStaffName())){
            criteria.andStaffNameLike(param.getStaffName());
        }
        if(StringUtils.isNotEmpty(param.getRemark())){
            criteria.andRemarkLike(param.getRemark());
        }
        example.setOrderByClause(" update_time desc ");
        return safetyRetentionWhiteDOMapper.selectByExample(example);
    }

    /**
     *
     * @param param
     * @return
     */
    public Object importData(CommonImportParam param) throws Exception{
        Workbook workbook = ExcelUtils.getWorkbook(param.getFile());
        Sheet sheet = workbook.getSheetAt(0);
        //获得当前sheet的开始行
        int firstRowNum  = sheet.getFirstRowNum();
        //获得当前sheet的结束行
        int lastRowNum = sheet.getLastRowNum();
        String result = "";
        List<SafetyRetentionWhiteDO> whiteDOList = new ArrayList<>();
        //获取工厂列表
        List<String> factoryList = safetyLaneFactoryService.getFactoryList(new LaneFactoryListParam());
        for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++) {
            //获得当前行
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            try {
                //用户名
                String carNo = ExcelUtils.getValue(row.getCell(1));
                String staffNo = ExcelUtils.getValue(row.getCell(2));
                String factory = ExcelUtils.getValue(row.getCell(3));
                String carType = ExcelUtils.getValue(row.getCell(4));
                String remark = ExcelUtils.getValue(row.getCell(5));

                if(StringUtils.isEmpty(carType)){
                    throw new BizException("999", "第"+rowNum+"行类型为空，导入终止！");
                }
                if(StringUtils.isNotEmpty(factory)){
                    if(!factoryList.contains(factory)){
                        throw new BizException("999", "第"+rowNum+"行工厂“"+factory+"不存在”，导入终止！");
                    }
                }
                SafetyRetentionWhiteDO whiteDO = new SafetyRetentionWhiteDO();
                whiteDO.setCarNo(carNo);
                whiteDO.setStaffNo(staffNo);
                whiteDO.setFactory(factory);
                whiteDO.setRemark(remark);

                whiteDOList.add(whiteDO);

            }catch (Exception e){
                throw new BizException("999", "第"+rowNum+"行导入异常，导入终止！"+e.getMessage());
            }
        }
        if(whiteDOList.size()>0){
            safetyRetentionBizMapper.batchInsertWhite(whiteDOList);
        }
        return null;
    }
}
