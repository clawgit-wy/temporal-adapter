package com.yutong.safety.core.service;

import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.param.web.SafetyStaffEvectionParam;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.core.db.entity.SafetyStaffEvectionDO;
import com.yutong.safety.core.db.mapper.SafetyStaffEvectionDOBizMapper;
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
 * @author lukunpeng
 * @time 2024-4-12
 */
@Service
public class SafetyStaffEvectionService {

    @Autowired
    SafetyStaffEvectionDOBizMapper safetyStaffEvectionDOBizMapper;

    public Object importData(MultipartFile file) {
        Workbook workbook = ExcelUtils.getWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        //获得当前sheet的开始行
        int firstRowNum  = sheet.getFirstRowNum();
        //获得当前sheet的结束行
        int lastRowNum = sheet.getLastRowNum();

        List<SafetyStaffEvectionDO> insertList = new ArrayList<>();
        List<SafetyStaffEvectionDO> updateList = new ArrayList<>();
        //查询当前库里所有员工号,用于判断是否需要新增
        List<String> nowStaffNos = safetyStaffEvectionDOBizMapper.findAll();
        String emptyNo = "";
        for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++) {
            //获得当前行
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            String staffNo = ExcelUtils.getValue(row.getCell(1));//员工号
            if(StringUtils.isEmpty(staffNo)){
                emptyNo += rowNum+";";
                continue;
            }
            String staffName = ExcelUtils.getValue(row.getCell(2));//姓名
            String beginTime = ExcelUtils.getValue(row.getCell(3));//开始时间
            String endTime = ExcelUtils.getValue(row.getCell(4));//结束时间
            String remark = ExcelUtils.getValue(row.getCell(5));//备注

            SafetyStaffEvectionDO staffEvectionDO = new SafetyStaffEvectionDO();
            staffEvectionDO.setStaffNo(staffNo);
            staffEvectionDO.setStaffName(staffName);
            staffEvectionDO.setBeginDate(beginTime);
            staffEvectionDO.setEndDate(endTime);
            staffEvectionDO.setRemark(remark);

            if(nowStaffNos.contains(staffNo)){
                updateList.add(staffEvectionDO);
            }else{
                insertList.add(staffEvectionDO);
            }
        }
        int insert = insertList.size()>0?safetyStaffEvectionDOBizMapper.batchInsert(insertList):0;
        int update = updateList.size()>0?safetyStaffEvectionDOBizMapper.batchUpdate(updateList):0;
        String msg = StringUtils.isEmpty(emptyNo)?"":"第"+emptyNo+"行员工号为空! ";
        msg += "本次新增: "+insert+"条; 更新: "+updateList.size()+"条!";
        return msg;
    }

    public List<SafetyStaffEvectionDO> queryList(SafetyStaffEvectionParam param) {
        int pageNo = param.getPageNum();
        int pageSize = param.getPageSize();
        return safetyStaffEvectionDOBizMapper.queryList(param, (pageNo-1) * pageSize, pageSize);
    }

    public long pageListCount(SafetyStaffEvectionParam param) {
        return safetyStaffEvectionDOBizMapper.pageListCount(param);
    }

    public Object add(SafetyStaffEvectionParam param) {
        if(StringUtils.isEmpty(param.getStaffNo()) || StringUtils.isEmpty(param.getBeginDate()) || StringUtils.isEmpty(param.getEndDate())){
            return "员工号/公休开始/公休结束时间不能为空!";
        }
        return safetyStaffEvectionDOBizMapper.add(param);
    }

    public Object update(SafetyStaffEvectionParam param) {
        if(StringUtils.isEmpty(param.getStaffNo()) || StringUtils.isEmpty(param.getBeginDate()) || StringUtils.isEmpty(param.getEndDate())){
            return "员工号/公休开始/公休结束时间不能为空!";
        }
        return safetyStaffEvectionDOBizMapper.update(param);
    }

    public List<SafetyStaffEvectionDO> queryListByEmpNos(List<String> users) {
        return safetyStaffEvectionDOBizMapper.queryListByEmpNos(users);
    }

    public void batchInsert(List<SafetyStaffEvectionDO> staffList) {
        safetyStaffEvectionDOBizMapper.batchInsert(staffList);
    }
}
