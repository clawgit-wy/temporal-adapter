package com.yutong.safety.core.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.core.db.entity.SafetyFieldManageDO;
import com.yutong.safety.core.db.mapper.SafetyFieldManageDOMapper;
import com.yutong.safety.core.db.mapper.SafetyOutsiderPromiseChangeMapper;
import com.yutong.safety.core.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 外协外包权限变更流程服务实现
 * @author lukunpeng
 * @time 2024-6-14
 */
@Service
public class SafetyOutsiderPromiseChangeService {

    @Autowired
    SafetyOutsiderPromiseChangeMapper safetyOutsiderPromiseChangeMapper;

    @Autowired
    private SafetyFieldManageService safetyFieldManageService;

    @Autowired
    SafetyFieldManageDOMapper safetyFieldManageDOMapper;

    public int addData(SafetyOutsiderPromiseChangeDTO dto) {
        return safetyOutsiderPromiseChangeMapper.insert(dto);
    }

    public void addDetailData(List<SafetyOutsiderPromiseChangeDetailDTO> detailDTOS, String flowId) {
        safetyOutsiderPromiseChangeMapper.insertDetail(detailDTOS, flowId);
    }

    public void addOldDetailData(List<SafetyOutsiderPromiseChangeDetailDTO> oldDetailDTOS, String flowId) {
        safetyOutsiderPromiseChangeMapper.insertOldDetail(oldDetailDTOS, flowId);
    }

    public List<SafetyOutsiderPromiseChangeDetailDTO> findOldDetailsByFlowId(String flowId) {
        return safetyOutsiderPromiseChangeMapper.findOldDetailsByFlowId(flowId);
    }
    public SafetyOutsiderPromiseChangeDTO view(String flowId) {
        return safetyOutsiderPromiseChangeMapper.findByFlowId(flowId);
    }

    public List<SafetyOutsiderPromiseChangeDetailDTO> findDetailsByFlowId(String flowId) {
        return safetyOutsiderPromiseChangeMapper.findDetailsByFlowId(flowId);
    }

    public List<SafetyOutsiderPromiseDTO> findDetailsByFlowIdList(List<String> flowIdList) {
        return safetyOutsiderPromiseChangeMapper.findDetailsByFlowIdList(flowIdList);
    }

    public List<SafetyOutsiderPromiseDTO> findPageList(SafetyOutsiderPromiseDTO dto) {
        int pageNo = dto.getPageNum();
        pageNo = pageNo==0 ? 1:pageNo;
        List<SafetyOutsiderPromiseDTO> pageList = safetyOutsiderPromiseChangeMapper.findPageList(dto, (pageNo - 1) * 15);

        if (CollectionUtils.isEmpty(pageList)) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> factoryList = safetyFieldManageDOMapper.getFactoryMap();
        Map<String, String> factoryMap = factoryList.stream().collect(Collectors.toMap(
                map -> map.get("id").toString(),
                map -> map.get("factory").toString(),
                (oldVal, newVal) -> oldVal
        ));

        for (SafetyOutsiderPromiseDTO en : pageList) {
            if (StringUtils.isBlank(en.getFactory())) {
                continue;
            }
            String factoryName = getFactoryName(en.getFactory(), factoryMap);
            en.setFactoryName(factoryName);
        }
        return pageList;
    }

    public List<SafetyFieldManageDO> processFactoryByEmpNo(String empNo){
        return safetyOutsiderPromiseChangeMapper.processFactoryByEmpNo(empNo);
    }

    /**
     * 拼接厂区名
     * @param factoryIds
     * @param factoryMap
     * @return
     */
    public String getFactoryName(String factoryIds, Map<String, String> factoryMap) {
        List<String> split = new ArrayList<>();
        if(StringUtils.isNotBlank(factoryIds)){
            split = Arrays.asList(factoryIds.split(","));
        }
        List<String> values = new ArrayList<>();
        for (String str : split) {
            if(StringUtils.isBlank(str)){
                continue;
            }
            String value = factoryMap.get(str);
            if (value != null) {
                values.add(value);
            }
        }
        return String.join(",", values);
    }

    public int getCount(SafetyOutsiderPromiseDTO dto) {
        return safetyOutsiderPromiseChangeMapper.findCount(dto);
    }

    public List<SafetyOutsiderPromiseDTO> exportData(SafetyOutsiderPromiseDTO processDTO) {
        List<SafetyOutsiderPromiseDTO> list = safetyOutsiderPromiseChangeMapper.findList(processDTO);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> factoryList = safetyFieldManageDOMapper.getFactoryMap();
        Map<String, String> factoryMap = factoryList.stream().collect(Collectors.toMap(
                map -> map.get("id").toString(),
                map -> map.get("factory").toString(),
                (oldVal, newVal) -> oldVal
        ));

        for (SafetyOutsiderPromiseDTO en : list) {
            if (StringUtils.isBlank(en.getFactory())) {
                continue;
            }
            String factoryName = getFactoryName(en.getFactory(), factoryMap);
            en.setFactoryName(factoryName);
        }
        return list;
    }

    public void updateFlowStatus(String processId) {
        safetyOutsiderPromiseChangeMapper.updateFlowStatus(processId);
    }

    public List<SafetyOutsiderPromiseDTO> findFlowData(String processId) {
        return safetyOutsiderPromiseChangeMapper.findFlowData(processId);
    }

    public List<SafetyOutsiderPromiseDTO> findFlowDataList(List<String> processIdList) {
        return safetyOutsiderPromiseChangeMapper.findFlowDataList(processIdList);
    }

    public void saveOrUpdate(List<SafetyOutsiderPromiseDTO> list) {
        safetyOutsiderPromiseChangeMapper.saveOrUpdate(list);
    }

    public Map<String, String> empDept(String empNos) {
        return safetyOutsiderPromiseChangeMapper.getEmpDept(empNos);
    }

    public List<SafetyOutsiderPromiseDTO> findOldPromise(String empNos, String workNo) {
        return safetyOutsiderPromiseChangeMapper.findOldPromise(Arrays.asList(empNos.split(",")), workNo);
    }

    public List<SafetyOutsiderPromiseDTO> findOldPromise(List<String> empNoList) {
        List<List<String>> partition = Lists.partition(empNoList, 300);
        return partition.stream()
                .flatMap(empNoPartition -> {
                    List<SafetyOutsiderPromiseDTO> promises = safetyOutsiderPromiseChangeMapper.findOldPromise(empNoPartition, "");
                    return promises != null ? promises.stream() : Stream.empty();
                })
                .collect(Collectors.toList());
    }

    public List<SafetyOutsiderPromiseDTO> findPromiseByName(String empStr, String workNo) {
        return safetyOutsiderPromiseChangeMapper.findPromiseByName(empStr, workNo);
    }

    public String findFactoryIds(String factory) {
        return safetyOutsiderPromiseChangeMapper.findFactoryIds(Arrays.asList(factory.split(",")));
    }

    public Object importData(CommonImportParam param, String workNo) {
        //获取厂区名称
        Map<String, SafetyFieldManageDO> factoryMap = safetyFieldManageService.getFactoryList();
        JSONObject retObj = new JSONObject();
        MultipartFile file = param.getFile();
        Workbook workbook = ExcelUtils.getWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        //获得当前sheet的开始行
        int firstRowNum  = sheet.getFirstRowNum();
        //获得当前sheet的结束行
        int lastRowNum = sheet.getLastRowNum();
        PreconditonsEx.checkArgument(lastRowNum <= 51, ResponseEnum.File_FORMART_ERROR.getCode(),"最多导入50条数据!");
        List<String> empNos = new ArrayList<>();
        //存放导入的变更后权限
        Map<String,String> importFactoryMap = new HashMap<>();
        for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++) {
            //获得当前行
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            String empNo = ExcelUtils.getValue(row.getCell(0));//员工号
            if (StringUtils.isEmpty(empNo)) {
                continue;
            }
            String factoryStr = ExcelUtils.getValue(row.getCell(2));//授权厂区
            if(StringUtils.isNotBlank(factoryStr)){
                String factoryIdStr = this.getFactoryIds(factoryMap,factoryStr);
                importFactoryMap.put(empNo,factoryIdStr);
            }

            empNos.add(empNo);
        }
        List<SafetyOutsiderPromiseDTO> oldPromise = safetyOutsiderPromiseChangeMapper.findOldPromise(empNos, workNo);
        //有权限,有记录的
        List<String> authNos = new ArrayList<>();
        for (SafetyOutsiderPromiseDTO dto:oldPromise) {
            String empNo = dto.getEmpNo();
            empNos.remove(empNo);
            authNos.add(empNo);
        }
        if(!authNos.isEmpty()){
            List<Map<String, String>> empDatas = safetyOutsiderPromiseChangeMapper.findEmpDatas(authNos);
            if(empDatas != null && !empDatas.isEmpty()){
                for(Map<String, String> curMap:empDatas){
                    String empNo = curMap.get("empNo");
                    //工厂名转工厂ID
                    String factoryStr = importFactoryMap.get(empNo);
                    curMap.put("factoryStr",factoryStr);
                    //组织结构格式化>>
                    String orgNamePath = curMap.get("orgPath");
                    if(StringUtils.isNotBlank(orgNamePath)){
                        orgNamePath = orgNamePath.replace(",",">>");
                        curMap.put("orgPath",orgNamePath);
                    }
                }
            }
            retObj.put("oldPromise",oldPromise);
            retObj.put("empDatas",empDatas);
            retObj.put("status","S");
        }

        if(!ObjectUtils.isEmpty(empNos)){
            String msg = JSONArray.toJSONString(empNos);
            retObj.put("status","F");
            retObj.put("msg","提示您不是"+msg+"的对接人，无权限申请!");
        }
        return retObj.toString();
    }

    public List<SafetyOutsiderPromiseDTO> findOldPromiseForView(List<SafetyOutsiderPromiseChangeDetailDTO> details) {
        return safetyOutsiderPromiseChangeMapper.findOldPromiseForView(details);
    }

    public String findFactoryCodes(String factory) {
        return safetyOutsiderPromiseChangeMapper.findFactoryCodes(Arrays.asList(factory.split(",")));
    }

    public void updateDetails(List<SafetyOutsiderPromiseChangeDetailDTO> details) {
        safetyOutsiderPromiseChangeMapper.updateDetails(details);
    }

    public String findFaceDevices(String factory) {
        final String[] devices = {""};
        Arrays.asList(factory.split(",")).forEach(factoryId ->{
            devices[0] = Joiner.on(",").join(devices[0],safetyOutsiderPromiseChangeMapper.findFaceDevices(factoryId));
        });
        return devices[0];
    }

    public String findDevices(Map<String,Object> paramMap) {
        String result = "";
        List<String> devices = safetyOutsiderPromiseChangeMapper.findDevices(paramMap);
        if(!CollectionUtils.isEmpty(devices)){
            result = String.join(",", devices);
        }
        return result;
    }

    public String findCardDevices(String factory) {
        final String[] devices = {""};
        Arrays.asList(factory.split(",")).forEach(factoryId ->{
            devices[0] = Joiner.on(",").join(devices[0],safetyOutsiderPromiseChangeMapper.findCardDevices(factoryId));
        });
        return devices[0];
    }

    public List<SafetyOutsiderPromiseDTO> getForCardDistribute() {
        return safetyOutsiderPromiseChangeMapper.getForCardDistribute();
    }

    public List<Map<String, String>> getEmpListByDept(String empNos) {
        return safetyOutsiderPromiseChangeMapper.getEmpListByDept(empNos);
    }

    public int cancelPower(String empNos,SafetyOutsiderPromiseDTO record){
        return safetyOutsiderPromiseChangeMapper.cancelPower(empNos,record);
    }

    /**
     * 获取厂区id
     * @param factoryMap
     * @param factoryName
     * @return
     */
    public String getFactoryIds(Map<String, SafetyFieldManageDO> factoryMap, String factoryName) {
        Set<String> names = Sets.newHashSet(factoryName.split(","));
        StringBuilder factoryIds = new StringBuilder();
        for (String s : names) {
            if (factoryMap.containsKey(s)) {
                factoryIds.append(factoryMap.get(s).getId()).append(",");
            }
        }
        if (com.yutong.gw.utils.StringUtils.isNotEmpty(factoryIds.toString())) {
            factoryIds = new StringBuilder(factoryIds.substring(0, factoryIds.length() - 1));
        }
        return factoryIds.toString();
    }

    public void updatePromiseCardStatus(String empNo) {
        safetyOutsiderPromiseChangeMapper.updatePromiseCardStatus(empNo);
    }

    public String getEndDateByEmpNo(String empNo) {
        String endDate = safetyOutsiderPromiseChangeMapper.getEndDateByEmpNo(empNo);
        if(StringUtils.isEmpty(endDate)){
            endDate = "2099-12-31";
        }
        return endDate;
    }

    public List<SafetyOutsiderPromiseDTO> getEndDateByEmpNoList(List<String> list) {
        return safetyOutsiderPromiseChangeMapper.getEndDateByEmpNoList(list);
    }

    public String findFactoryByEmpNo(String workNo) {
        List<String> deviceIds = safetyOutsiderPromiseChangeMapper.findFactoryByEmpNo(workNo);
        String deviceNos = "";
        for (String deviceNo: deviceIds){
            deviceNos = Joiner.on(",").join(deviceNos,deviceNo);
        }
        return deviceNos;
    }

    public Map<String,String> findFactoryByEmpNoList(List<String> list) {
        Map<String,String> resultMap = new HashMap<>();
        List<SafetyOutsiderPromiseDTO> factoryByEmpNoList = safetyOutsiderPromiseChangeMapper.findFactoryByEmpNoList(list);
        if(!CollectionUtils.isEmpty(factoryByEmpNoList)){
            resultMap = factoryByEmpNoList.stream().collect(Collectors.toMap(
                    en -> en.getEmpNo(),
                    en -> en.getPowerNo(),
                    (existingValue, newValue) -> existingValue + "," + newValue
            ));
        }
        return resultMap;
    }

    public List<String> getLastTimeData(String beginTime,String endTime) {
        return safetyOutsiderPromiseChangeMapper.getLastTimeData(beginTime, endTime);
    }

    public List<String> getIssueFailData() {
        return safetyOutsiderPromiseChangeMapper.getIssueFailData();
    }

    public void toDelIssueData(String type, List<String> flowIdList) {
        if("save".equals(type)){
            safetyOutsiderPromiseChangeMapper.saveIssueDate(flowIdList);
        }else if("delete".equals(type)){
            safetyOutsiderPromiseChangeMapper.deleteIssueDate(flowIdList);
        }
    }

    public List<SafetyOutsiderPromiseProcessDTO> findProcessList(SafetyOutsiderPromiseProcessDTO dto){
        return safetyOutsiderPromiseChangeMapper.findPageProcessList(dto);
    }


    public List<SafetyOutsiderPromiseProcessDTO> findPageProcessList(SafetyOutsiderPromiseProcessDTO dto){
        List<SafetyOutsiderPromiseProcessDTO> pageList = safetyOutsiderPromiseChangeMapper.findPageProcessList(dto);

        if (CollectionUtils.isEmpty(pageList)) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> factoryList = safetyFieldManageDOMapper.getFactoryMap();
        Map<String, String> factoryMap = factoryList.stream().collect(Collectors.toMap(
                map -> map.get("id").toString(),
                map -> map.get("factory").toString(),
                (oldVal, newVal) -> oldVal
        ));

        for (SafetyOutsiderPromiseProcessDTO en : pageList) {
            if (!StringUtils.isBlank(en.getAuthorizedFacility())) {
                String factoryName = getFactoryName(en.getAuthorizedFacility(), factoryMap);
                en.setAuthorizedFacility(factoryName);
            }
            if (!StringUtils.isBlank(en.getChangedFacility())) {
                String factoryName = getFactoryName(en.getChangedFacility(), factoryMap);
                en.setChangedFacility(factoryName);
            }
        }
        return pageList;
    }

    public Integer getProcessCount(SafetyOutsiderPromiseProcessDTO dto){
        return safetyOutsiderPromiseChangeMapper.getProcessCount(dto);
    }

    public void updateCreator(SafetyOutsiderPromiseChangeDTO dto){
        safetyOutsiderPromiseChangeMapper.updateCreator(dto);
    }

    public boolean validProcess(String curEmpNo, String chooseEmpNo){
        Integer c = safetyOutsiderPromiseChangeMapper.validProcess(curEmpNo, chooseEmpNo);
        return c > 0;
    }

    public void updatePromiseByFlowId(String flowId){
        safetyOutsiderPromiseChangeMapper.updatePromiseByFlowId(flowId);
    }

    public List<SafetyOutsiderPromiseDTO> getEnById(String id){
        return safetyOutsiderPromiseChangeMapper.getEnById(id);
    }

    /**
     * 获取外协需要下发权限的数据，包含下发和注销
     * @return 数据集（包含下发和注销的数据）
     */
    public List<ParamDTO> selectListByWxInfo(List<String> empNos) {
        return safetyOutsiderPromiseChangeMapper.selectListByWxInfo(empNos);
    }

    /**
     * 批量修改下发状态
     * @param empNoList 员工号
     */
    public void updateStatus(List<String> empNoList) {
        safetyOutsiderPromiseChangeMapper.updateStatus(empNoList);
    }

    /**
     * 根据员工号获取员工的厂区信息
     * @param empNoList 员工号
     * @return 返回集合
     */
    public List<ParamDTO> selectListByEmpNoList(List<String> empNoList) {
        return safetyOutsiderPromiseChangeMapper.selectListByEmpNoList(empNoList);
    }

    /**
     * 批量修改厂区信息
     * @param list 变更的数据信息
     */
    public void updateOutsiderInfo(List<SafetyOutsiderPromiseDTO> list) {
        safetyOutsiderPromiseChangeMapper.updateOutsiderInfo(list);
    }

    public void saveOutsiderInfoTemp(List<SafetyOutsiderPromiseDTO> list) {
        safetyOutsiderPromiseChangeMapper.saveOrUpdateTemp(list);
    }

    public int batchUpdate(List<ParamDTO> list){
        return safetyOutsiderPromiseChangeMapper.batchUpdate(list);
    }

    public int batchUpdateForPhoto(List<ParamDTO> list){
        return safetyOutsiderPromiseChangeMapper.batchUpdateForPhoto(list);
    }

    /**
     * 获取员工的厂区权限
     * @param empNo 员工号
     * @return 权限对象
     */
    public SafetyOutsiderPromiseDTO getOutsiderPromiseByEmp(String empNo){
        if(StringUtils.isBlank(empNo)){
            return null;
        }
        List<String> empNos = new ArrayList<>();
        empNos.add(empNo);
        List<SafetyOutsiderPromiseDTO> list = safetyOutsiderPromiseChangeMapper.findOldPromise(empNos,"");
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }
}
