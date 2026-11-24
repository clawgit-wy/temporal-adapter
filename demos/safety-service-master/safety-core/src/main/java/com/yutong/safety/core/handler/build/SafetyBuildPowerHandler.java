package com.yutong.safety.core.handler.build;

import com.alibaba.excel.EasyExcel;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.easyExcel.ColumnHeightAuto;
import com.yutong.safety.common.easyExcel.ColumnWidthAuto;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDOExample;
import com.yutong.safety.core.db.entity.build.param.BuildDeviceListParam;
import com.yutong.safety.core.db.entity.build.param.BuildPowerListParam;
import com.yutong.safety.core.dto.build.SafetyBuildDeviceDTO;
import com.yutong.safety.core.dto.build.SafetyBuildPowerDTO;
import com.yutong.safety.core.dto.build.SafetyBuildPowerInfoDTO;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.service.SafetyBpmWebServiceService;
import com.yutong.safety.core.service.build.SafetyBuildDeviceService;
import com.yutong.safety.core.service.build.SafetyBuildPowerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SafetyBuildPowerHandler {

    @Resource
    private SafetyBuildPowerService safetyBuildPowerService;

    @Autowired
    SafetyBuildDeviceHandler safetyBuildDeviceHandler;
    @Autowired
    SafetyBuildDeviceService safetyBuildDeviceService;
    @Autowired
    SafetyBpmWebServiceService safetyBpmWebServiceService;
    @Resource
    private SafetyBaseHandler safetyBaseHandler;

    public Object pageList(SafetyBuildPowerDO param) {
        SafetyBuildPowerDTO safetyBuildPowerDTO = new SafetyBuildPowerDTO();
        BeanUtils.copyProperties(param, safetyBuildPowerDTO);
        //加入流程链接前缀
        String bpmUrlPre = getBpmPreUrl("safety_build_manage_process");
        safetyBuildPowerDTO.setBpmUrlPre(bpmUrlPre);

        List<SafetyBuildPowerDTO> list = safetyBuildPowerService.selectPageList(safetyBuildPowerDTO);
        long total = safetyBuildPowerService.selectListCount(safetyBuildPowerDTO);
        return new PageVO<>(total, list);
    }

    /**
     * 拼接bpm链接
     * @param formId
     * @return 返回值
     */
    public String getBpmPreUrl(String formId){
        Map<String,String> bpmModel = safetyBpmWebServiceService.findBpmModelByFormId(formId);
        if(bpmModel != null){
            return bpmModel.get("form_url")+"?sysId=SAFETY&modelId="+bpmModel.get("model_id")+"&templateFormId="+bpmModel.get("form_id");
        }
        return "";
    }

    public Object listWithSecret(String deviceCode) {
        SafetyBuildDeviceDTO result = new SafetyBuildDeviceDTO();

        //获取门禁设备详情
        Object byDeviceCode = safetyBuildDeviceHandler.getByDeviceCode(deviceCode);
        if(ObjectUtils.isEmpty(byDeviceCode)){return null;}

        SafetyBuildDeviceDO code = (SafetyBuildDeviceDO) byDeviceCode;
        BeanUtils.copyProperties(code, result);
        //楼栋编码
        String anBuildingCode = code.getAnBuildingCode();
        //根据楼栋编码查询公共区域门禁信息
        BuildDeviceListParam buildDeviceListParam = new BuildDeviceListParam();
        buildDeviceListParam.setAnBuildingCode(anBuildingCode);
        buildDeviceListParam.setIsPublic("1");
        List<SafetyBuildDeviceDO> deviceDOList = safetyBuildDeviceService.selectByExample(buildDeviceListParam);
        result.setDeviceDOList(deviceDOList);

        SafetyBuildPowerDTO param = new SafetyBuildPowerDTO();
        param.setDeviceCode(deviceCode);
        //门禁对应的责任部门和保密负责人
        List<SafetyBuildPowerDTO> peopleList = safetyBuildPowerService.getPeopleList(param);
        if (!CollectionUtils.isEmpty(peopleList)) {
            List<SafetyBuildPowerInfoDTO> safetyBuildPowerDTOS = mergePeopleList(peopleList);
            result.setPowerList(safetyBuildPowerDTOS);
        }

        return result;
    }

    public Object listWithSecretBatch(String deviceCodes) {
        if(StringUtils.isEmpty(deviceCodes)){return null;}

        List<SafetyBuildDeviceDTO> resultList = new ArrayList<>();
        List<String> list = Arrays.asList(deviceCodes.split(","));
        //获取门禁信息
        List<SafetyBuildDeviceDO> deviceDOList1 = safetyBuildDeviceService.selectList(list);

        //获取门禁对应的保密负责人
        List<SafetyBuildPowerDO> peopleList = safetyBuildPowerService.getListByDeviceCode(list);
        Map<String, List<SafetyBuildPowerDO>> peopleMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(peopleList)) {
            peopleMap = peopleList.stream().collect(Collectors.groupingBy(SafetyBuildPowerDO::getDeviceCode));
        }

        //获取楼栋的集合
        List<String> buildList = deviceDOList1.stream().map(SafetyBuildDeviceDO::getAnBuildingCode).collect(Collectors.toList());
        Map<String, List<SafetyBuildDeviceDO>> buildMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(buildList)) {
            List<SafetyBuildDeviceDO> deviceDOList = safetyBuildDeviceService.selectBuildList(buildList);

            //将deviceName更新，如果有安保自定义的就取安保的值，反之取自己
            for (SafetyBuildDeviceDO safetyBuildDeviceDO : deviceDOList) {
                if(!StringUtils.isEmpty(safetyBuildDeviceDO.getAnDeviceName())){
                    safetyBuildDeviceDO.setDeviceName(safetyBuildDeviceDO.getAnDeviceName());
                }
            }

            buildMap = deviceDOList.stream().collect(Collectors.groupingBy(SafetyBuildDeviceDO::getAnBuildingCode));
        }

        for(SafetyBuildDeviceDO en:deviceDOList1){
            SafetyBuildDeviceDTO result = new SafetyBuildDeviceDTO();
            BeanUtils.copyProperties(en, result);

            if(!StringUtils.isEmpty(result.getAnDeviceName())){
                result.setDeviceName(result.getAnDeviceName());
            }

            //公共区域门禁信息
            result.setDeviceDOList(buildMap.get(en.getAnBuildingCode()));

            //门禁对应的责任部门和保密负责人
            List<SafetyBuildPowerDO> doList = peopleMap.get(en.getDeviceCode());
            if(!CollectionUtils.isEmpty(doList)){
                List<SafetyBuildPowerDTO> dtoList = new ArrayList<>();
                for (SafetyBuildPowerDO e : doList){
                    SafetyBuildPowerDTO dto = new SafetyBuildPowerDTO();
                    BeanUtils.copyProperties(e,dto);
                    dtoList.add(dto);
                }
                if (!CollectionUtils.isEmpty(dtoList)) {
                    List<SafetyBuildPowerInfoDTO> safetyBuildPowerDTOS = mergePeopleList(dtoList);
                    result.setPowerList(safetyBuildPowerDTOS);
                }
            }

            resultList.add(result);
        }

        return resultList;
    }

    public List<SafetyBuildPowerInfoDTO> mergePeopleList(List<SafetyBuildPowerDTO> peopleList) {
        // 使用Map按dutyDeptCode分组
        Map<String, List<SafetyBuildPowerDTO>> map = new HashMap<>();
        for (SafetyBuildPowerDTO dto : peopleList) {
            String deptCode = dto.getDutyDeptCode();
            if (!map.containsKey(deptCode)) {
                map.put(deptCode, new ArrayList<>());
            }
            map.get(deptCode).add(dto);
        }

        // 对每个分组进行拼接
        List<SafetyBuildPowerInfoDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<SafetyBuildPowerDTO>> entry : map.entrySet()) {
            String deptCode = entry.getKey();
            List<SafetyBuildPowerDTO> groupList = entry.getValue();

            SafetyBuildPowerInfoDTO en = new SafetyBuildPowerInfoDTO();
            en.setDutyDeptCode(deptCode);
            en.setDutyDeptName(groupList.get(0).getDutyDeptName());
            en.setBuildSysManagerRole(groupList.get(0).getBuildSysManagerRole() );
            en.setEnList(groupList);

            result.add(en);
        }

        return result;
    }

    public Object getPeopleList(SafetyBuildPowerDTO param) {
        return safetyBuildPowerService.getPeopleList(param);
    }

    public void exportExcel(SafetyBuildPowerDO param, OutputStream outputStream) {
        List<SafetyBuildPowerDTO> list = safetyBuildPowerService.selectPageListExport(param);

        EasyExcel.write(outputStream, SafetyBuildPowerDTO.class)
                .registerWriteHandler(new ColumnHeightAuto())
                .registerWriteHandler(new ColumnWidthAuto())
                .sheet("Sheet1")
                .doWrite(list);
    }

    public void updateData(SafetyBuildPowerDO en) {
        safetyBuildPowerService.updateData(en);
    }

    /**
     * 数据删除
     * @param empNo 员工号
     * @param dutyDeptCode 责任部门
     */
    public boolean delete(String empNo, String dutyDeptCode) {
        if (StringUtils.isEmpty(empNo) || StringUtils.isEmpty(dutyDeptCode)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "员工号或责任部门编码为空");
        }
        // 删除责任部门管理的门禁信息
        SafetyBuildPowerDOExample parkExample = new SafetyBuildPowerDOExample();
        SafetyBuildPowerDOExample.Criteria parkCriteria = parkExample.createCriteria();
        parkCriteria.andEmployeeCodeEqualTo(empNo);
        parkCriteria.andDutyDeptCodeEqualTo(dutyDeptCode);
        parkCriteria.andIsDeleteEqualTo("0");
        SafetyBuildPowerDO powerDO = new SafetyBuildPowerDO();
        powerDO.setIsDelete("1");
        powerDO.setUpdateTime(DateUtils.format(new Date(), 2));
        powerDO.setRemark("菜单删除");
        safetyBuildPowerService.updateByExample(powerDO, parkExample);

        // 删除角色逻辑 1.判断是否还存在数据， 2.没有则进行删除
        BuildPowerListParam param = new BuildPowerListParam();
        param.setEmpNo(empNo);
        List<SafetyBuildPowerDO> powerDOList = safetyBuildPowerService.selectList(param);
        if (CollectionUtils.isEmpty(powerDOList)) {
            // 删除门禁管理员的角色
            safetyBaseHandler.deleteRoleByEmpNo(empNo, "门禁管理员");
        }
        return true;
    }
}
