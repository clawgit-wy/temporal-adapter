package com.yutong.safety.core.service;

import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.param.web.CarRuleWhiteListParam;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.core.db.entity.SafetyCarRuleWhiteDO;
import com.yutong.safety.core.db.mapper.SafetyCarRuleWhiteMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lkp
 * @since 2024-04-21
 */
@Service
public class SafetyCarRuleWhiteService {

    @Autowired
    private SafetyCarRuleWhiteMapper safetyCarRuleWhiteMapper;

    /**
     * 分页查询
     *
     * @param param 查询入参
     * @return 返回
     */
    public List<SafetyCarRuleWhiteDO> pageList(CarRuleWhiteListParam param) {
        return safetyCarRuleWhiteMapper.selectList(param, (param.getPageNum() - 1) * 15, 15);
    }

    /**
     * 分页总条数
     *
     * @param param 查询入参
     * @return 返回
     */
    public long pageListCount(CarRuleWhiteListParam param) {
        return safetyCarRuleWhiteMapper.countList(param);
    }

    public int saveOrUpdate(CarRuleWhiteListParam param){
        int changeNums = 0;
        Integer ruleId = param.getId();

        if (ruleId == null) {
            changeNums = safetyCarRuleWhiteMapper.insert(param);
        } else {
            changeNums = safetyCarRuleWhiteMapper.update(param);
        }
        return changeNums;
    }

    /**
     * 做删除标记
     *
     * @param id 主键
     * @return 返回
     */
    public int deleteById(int id){
        return safetyCarRuleWhiteMapper.updateDelFlagByPrimaryKey(id);
    }

    public Object importData(CommonImportParam param) {
        MultipartFile file = param.getFile();
        Workbook workbook = ExcelUtils.getWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        //获得当前sheet的开始行
        int firstRowNum  = sheet.getFirstRowNum();
        //获得当前sheet的结束行
        int lastRowNum = sheet.getLastRowNum();
        List<CarRuleWhiteListParam> insertList = new ArrayList<>();
        List<CarRuleWhiteListParam> updateList = new ArrayList<>();
        //查询当前库里所有员工号,用于判断是否需要新增
        List<String> nowStaffNos = safetyCarRuleWhiteMapper.findAll();
        String emptyNo = "";
        for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++) {
            //获得当前行
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            String empNo = ExcelUtils.getValue(row.getCell(2));//员工号
            if (StringUtils.isEmpty(empNo)) {
                emptyNo += rowNum + ";";
                continue;
            }
            String carNo = ExcelUtils.getValue(row.getCell(1));//车牌号
            String empName = ExcelUtils.getValue(row.getCell(3));//车牌号
            String dept = ExcelUtils.getValue(row.getCell(4));//车牌号
            String remark = ExcelUtils.getValue(row.getCell(5));//车牌号
            CarRuleWhiteListParam whiteListParam = new CarRuleWhiteListParam();
            whiteListParam.setCarNo(carNo);
            whiteListParam.setEmpNo(empNo);
            whiteListParam.setEmpName(empName);
            whiteListParam.setRemark(remark);
            whiteListParam.setDept(dept);

            if(nowStaffNos.contains(empNo)){
                updateList.add(whiteListParam);
            }else{
                insertList.add(whiteListParam);
            }
        }

        int insert = insertList.size()>0?safetyCarRuleWhiteMapper.batchInsert(insertList):0;
        int update = updateList.size()>0?safetyCarRuleWhiteMapper.batchUpdate(updateList):0;
        String msg = StringUtils.isEmpty(emptyNo)?"":"第"+emptyNo+"行员工号为空! ";
        msg += "本次新增: "+insert+"条; 更新: "+updateList.size()+"条!";
        return msg;
    }
}
