package com.yutong.safety.core.handler;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.constants.UserTypeEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.app.EmployeeSetParam;
import com.yutong.safety.common.param.web.EmployeeMessageListParam;
import com.yutong.safety.common.param.web.EmployeeMessageUpdateParam;
import com.yutong.safety.common.param.web.EmpowerDeviceListParam;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetyEmpowerDeviceDO;
import com.yutong.safety.core.db.entity.SafetyVisitorSetDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDO;
import com.yutong.safety.core.db.entity.build.param.BuildPowerListParam;
import com.yutong.safety.core.db.entity.dict.SafetyDictItemDO;
import com.yutong.safety.core.db.entity.dict.SafetyDictSupperDO;
import com.yutong.safety.core.db.entity.emp.common.SafetyEmployeeConstants;
import com.yutong.safety.core.db.entity.result.DictItemRt;
import com.yutong.safety.core.dto.ResultDTO;
import com.yutong.safety.core.handler.build.SafetyBuildProcessHandler;
import com.yutong.safety.core.handler.spot.SafetySpotCheckTaskHandler;
import com.yutong.safety.core.service.*;
import com.yutong.safety.core.service.build.SafetyBuildPowerService;
import com.yutong.safety.core.service.dict.SafetyDictSupperService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gwcuiyx
 * @date 2024-4-2 15:51
 */
@Component
public class SafetyEmployeeMessageHandler {
    @Autowired
    private SafetyEmployeeMessageService safetyEmployeeMessageService;
    @Autowired
    private SafetyVisitorSetService safetyVisitorSetService;
    @Autowired
    SafetySpotCheckTaskHandler safetySpotCheckTaskHandler;
    @Autowired
    SafetyBuildPowerService safetyBuildPowerService;
    @Autowired
    SafetyBuildProcessHandler safetyBuildProcessHandler;

    @Resource
    private SafetyDictItemService dictItemService;

    @Resource
    private SafetyDictSupperService safetyDictSupperService;

    @Resource
    private SafetyEmpowerDeviceService safetyEmpowerDeviceService;

    @Resource
    private SafetyEmpPhotoInfoService safetyEmpPhotoInfoService;

    /**
     * 分页查询
     * @param param
     * @return
     */
    public Object pageList(EmployeeMessageListParam param) {
        List<SafetyEmployeeMessageDO> list = safetyEmployeeMessageService.pageList(param);
        long total = safetyEmployeeMessageService.pageListCount(param);
        return new PageVO<SafetyEmployeeMessageDO>(total, list);
    }

    /**
     * 数据导出接口
     * @param param
     * @param token
     * @return
     */
    public HSSFWorkbook export(EmployeeMessageListParam param, String token) {
        List<SafetyEmployeeMessageDO> list = safetyEmployeeMessageService.export(param);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"员工号", "姓名", "手机号", "照片", "人员类型","岗位","类别","部门","电子邮箱","备注"};
        String[][] values = new String[list.size()][];
        SafetyEmployeeMessageDO employeeMessageDO;
        for(int i=0; i<list.size(); i++) {
            employeeMessageDO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = employeeMessageDO.getEmpNo();//员工号
            values[i][1] = employeeMessageDO.getEmpName();//姓名
            values[i][2] = employeeMessageDO.getPhoneNo();//手机号
            values[i][3] = employeeMessageDO.getPhoto();//照片
            values[i][4] = UserTypeEnum.valueOf(employeeMessageDO.getUserType()).getName();//人员类型
            values[i][5] = employeeMessageDO.getPositionName();//岗位
            values[i][6] = employeeMessageDO.getPositionType();//类别
            values[i][7] = employeeMessageDO.getOrgNamePath();//部门路径
            values[i][8] = employeeMessageDO.getEmail();//邮件
            values[i][9] = employeeMessageDO.getRemark();//备注
        }
        String sheetName = "员工信息台账列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 批量更新备注
     * @param param
     */
    public void batchUpdateRemark(EmployeeMessageUpdateParam param) {
        safetyEmployeeMessageService.updateRemark(param);
    }

    public Object getPost() {
        return safetyEmployeeMessageService.getPost();
    }

    public Object tasksMessage(Boolean isAll, String dateTime) throws Exception{
        if(StringUtils.isNotBlank(dateTime)){
            dateTime = dateTime+" 00:00:00";
        }
        safetyEmployeeMessageService.tasksMessage(isAll, dateTime);
        return null;
    }

    /**
     * 校验被访人设置规则逻辑
     * @param param
     * @return
     */
    public boolean checkEmployeeSet(EmployeeSetParam param) {
        boolean flag = true;
        String postType= "外包岗位";
        //第一步，根据手机号获取员工信息台账主数据
        SafetyEmployeeMessageDO employeeMessageDO = safetyEmployeeMessageService.getInfoByPhone(param.getPhoneNo());
        if (Objects.isNull(employeeMessageDO)) {
            return true;
        }
        //第二步，根据主数据，判断被访人设置中的规则逻辑
        List<SafetyVisitorSetDO> list = safetyVisitorSetService.checkVisitorSet(employeeMessageDO.getPositionType());
        if (list == null) {
            return true;
        }
        //第三步，返回验证的结果
        for (SafetyVisitorSetDO visitorSetDO : list) {
            if (StringUtils.isNotEmpty(visitorSetDO.getPositionName()) && visitorSetDO.getVisitorState().equals(Constants.STATE_TRUE)
                    && employeeMessageDO.getPositionName().contains(visitorSetDO.getPositionName())) {
                flag = true;
                break;
            } else if (StringUtils.isNotEmpty(visitorSetDO.getPositionName()) && visitorSetDO.getVisitorState().equals(Constants.STATE_FALSE)
                    && employeeMessageDO.getPositionName().contains(visitorSetDO.getPositionName())) {
                flag = false;
                break;
            } else if (StringUtils.isEmpty(visitorSetDO.getPositionName()) && visitorSetDO.getVisitorState().equals(Constants.STATE_TRUE)){
                flag = true;
                break;
            } else if(StringUtils.isEmpty(visitorSetDO.getPositionName()) && visitorSetDO.getVisitorState().equals(Constants.STATE_FALSE)) {
                flag = false;
                break;
            } else if (StringUtils.isEmpty(employeeMessageDO.getPositionName()) && "空".equals(visitorSetDO.getPositionName())) {
                if (visitorSetDO.getVisitorState().equals(Constants.STATE_FALSE)) {
                    flag = false;
                } else {
                    flag = true;
                }
                break;
            } else if ((StringUtils.isEmpty(employeeMessageDO.getPositionType())) && "空".equals(visitorSetDO.getPositionType())) {
                if (visitorSetDO.getVisitorState().equals(Constants.STATE_FALSE)) {
                    flag = false;
                } else {
                    flag = true;
                }
                break;
            }
        }
        if(!flag){
            if(postType.equals(employeeMessageDO.getPositionType())){
                throw new BizException(ResponseEnum.FAIL.getCode(), "被访人未开通访客业务") ;
            }else{
                throw new BizException(ResponseEnum.FAIL.getCode(), "人员类型错误") ;
            }
        }
        return flag;
    }

    /**
     * 指定员工号同步员工信息
     * @param workNo 员工号
     * @return
     */
    public Object tasksMessageByWorkNo(String workNo){
        if(StringUtils.isBlank(workNo)){
            return null;
        }
        return safetyEmployeeMessageService.tasksMessageByWorkNo(workNo);
    }

    /**
     * 根据员工号更新更新时间
     * @param param
     * @return
     */
    public Object updateTimeByEmpNo(Map<String, Object> param){
        return safetyEmployeeMessageService.updateTimeByEmpNo(param);
    }

    public SafetyEmployeeMessageDO getEmployeeByWorkNo(String workNo,String phone){
        if(StringUtils.isNotBlank(workNo) && workNo.startsWith("01")){
            workNo = safetySpotCheckTaskHandler.employeeConvertWorkNo(workNo);
        }
        EmployeeMessageListParam listParam = new EmployeeMessageListParam();
        listParam.setFullEmpNo(workNo);
        listParam.setPhoneNo(phone);
        List<SafetyEmployeeMessageDO> list = safetyEmployeeMessageService.pageList(listParam);
        if(CollectionUtils.isEmpty(list)){
            return new SafetyEmployeeMessageDO();
        }
        if(list.size()>1){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"根据入参获取到多条数据，请核查");
        }
        return list.get(0);
    }

    public Object getUserListByCondition(EmployeeMessageListParam param) {
        List<SafetyEmployeeMessageDO> list = safetyEmployeeMessageService.getUserListByCondition(param);
        long total = safetyEmployeeMessageService.getCountUserListByCondition(param);
        return new PageVO<>(total, list);
    }

    public Object getUserListByConditionContain(EmployeeMessageListParam param,String token) {
        param.setPageFlag(true);
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        if(com.yutong.gw.utils.StringUtils.isEmpty(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }
        SafetyEmployeeMessageDO byNo = safetyEmployeeMessageService.getByNo(workNo);
        if(byNo == null || StringUtils.isBlank(byNo.getOrgCodePath())){
            return null;
        }
        param.setOrgPathCode(byNo.getOrgCodePath());

        List<SafetyEmployeeMessageDO> list = safetyEmployeeMessageService.getUserListByCondition(param);
        long total = safetyEmployeeMessageService.getCountUserListByCondition(param);
        return new PageVO<SafetyEmployeeMessageDO>(total, list);
    }

    public Object classPeople(String userCodeStr,String token) {
        if(StringUtils.isBlank(userCodeStr)){
            return null;
        }
        List<String> userCodeList = Arrays.asList(userCodeStr.split(","));

        ResultDTO resultDTO = new ResultDTO();
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        if(com.yutong.gw.utils.StringUtils.isEmpty(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }

        //根据员工号查询当前登录人有哪些责任部门
        BuildPowerListParam listParam = new BuildPowerListParam();
        listParam.setEmpNo(workNo);
        List<SafetyBuildPowerDO> doList = safetyBuildPowerService.selectList(listParam);

        //查询当前所有人的详情
        List<SafetyEmployeeMessageDO> userList = safetyEmployeeMessageService.selectListByUserCode(userCodeList);

        //根据当前用户类型 设置检索条件 2-管理员查看所有 3-保密负责人 能看管辖部门
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if("2".equals(userType)){
            resultDTO.setBelongList(userList);
            resultDTO.setNotBelongList(null);
            return resultDTO;
        }

        //责任部门为空，直接返回
        if (CollectionUtils.isEmpty(doList)) {
            resultDTO.setNotBelongList(userList);
            return resultDTO;
        }
        //当前登录人所有的责任部门
        Set<String> orgPathSet = new HashSet<>();
        for (SafetyBuildPowerDO en:doList) {
            orgPathSet.add(en.getDutyDeptCode());
        }

        List<SafetyEmployeeMessageDO> belongList = new ArrayList<>();
        List<SafetyEmployeeMessageDO> notBelongList = new ArrayList<>();

        for (SafetyEmployeeMessageDO user : userList) {
            boolean isBelong = false;
            String orgCodePath = user.getOrgCodePath();
            if(StringUtils.isBlank(orgCodePath)){
                notBelongList.add(user);
                continue;
            }

            //判断是否处于其责任部门下
            for(String str:orgPathSet){
                if(orgCodePath.contains(str)){
                    belongList.add(user);
                    isBelong = true;
                    break;
                }
            }

            if(!isBelong){notBelongList.add(user);}
        }

        resultDTO.setBelongList(belongList);
        resultDTO.setNotBelongList(notBelongList);
        return resultDTO;
    }


    public Object classPeopleForAll(String userCodeStr,String token) {
        if(StringUtils.isBlank(userCodeStr)){
            return null;
        }
        List<String> userCodeList = Arrays.asList(userCodeStr.split(","));

        ResultDTO resultDTO = new ResultDTO();
        //查询当前所有人的详情
        List<SafetyEmployeeMessageDO> userList = safetyEmployeeMessageService.selectListByUserCode(userCodeList);
        List<SafetyEmployeeMessageDO> belongList = new ArrayList<>();
        List<SafetyEmployeeMessageDO> notBelongList = new ArrayList<>();
        belongList.addAll(userList);

        resultDTO.setBelongList(belongList);
        resultDTO.setNotBelongList(notBelongList);
        return resultDTO;
    }

    public SafetyEmployeeMessageDO getEmpById(String userCode){
        return safetyEmployeeMessageService.getByNo(userCode);
    }

    /**
     * 下拉框
     */
    public VO dropDownBox() {
        List<DictItemRt> empTypeList = dictItemService.selectDictItemListBySupperCode(SafetyEmployeeConstants.EMP_LEDGER_EMP_TYPE);
        List<DictItemRt> jobTypeList = dictItemService.selectDictItemListBySupperCode(SafetyEmployeeConstants.EMP_LEDGER_JOB_TYPE);
        HashMap<String, List> map = new HashMap<>();
        map.put("empTypeList", empTypeList);
        map.put("jobTypeList", jobTypeList);
        return VoUtil.successData(map);
    }

    /**
     * 下拉框数据更新
     */
    public VO saveDropDownBox(String token) {
        String realName = TokenUtils.getRealName(token);
        SafetyDictSupperDO record = new SafetyDictSupperDO();
        record.setUpdateUser(realName);
        record.setSupperCode(SafetyEmployeeConstants.EMP_LEDGER_EMP_TYPE);
        record.setSupperName("职类");
        record.setSupperDesc("员工台账的职类类型");
        saveInfo(record, "emp_type");

        record = new SafetyDictSupperDO();
        record.setUpdateUser(realName);
        record.setSupperCode(SafetyEmployeeConstants.EMP_LEDGER_JOB_TYPE);
        record.setSupperName("职种");
        record.setSupperDesc("员工台账的职种类型");
        saveInfo(record, "job_type_name");
        return VoUtil.success();
    }

    /**
     * 更新员工台账需要的下拉框数据字典值
     */
    private void saveInfo(SafetyDictSupperDO record, String fieldName) {
        // 获取需要更新的数据
        List<String> list = safetyEmployeeMessageService.selectDistinctList(fieldName);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // 获取已经更新的数据
        list = list.stream().filter(li -> StringUtils.isNotBlank(li) && !StringUtils.equals(li, "null")).collect(Collectors.toList());
        List<DictItemRt> empTypeList = dictItemService.selectDictItemListBySupperCode(record.getSupperCode());
        List<String> itemCodes = empTypeList.stream().map(DictItemRt::getItemName).collect(Collectors.toList());
        List<String> collect = list.stream().filter(li -> !itemCodes.contains(li)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return;
        }

        long initialCode = 1;
        if (!empTypeList.isEmpty()) {
            // 确定初始代码为现有最大代码 + 1
            long maxCode = empTypeList.stream().mapToLong(item -> Long.parseLong(item.getItemCode())).max().orElse(0);
            initialCode = maxCode + 1;
        }

        SafetyDictSupperDO safetyDictSupperDO = safetyDictSupperService.selectInfo(record);

        List<SafetyDictItemDO> dictItems = new ArrayList<>();
        for (String s : collect) {
            SafetyDictItemDO info = new SafetyDictItemDO();
            info.setItemCode(String.valueOf(initialCode));
            info.setItemName(s);
            info.setSupperCode(record.getSupperCode());
            info.setSupperId(safetyDictSupperDO.getId());
            info.setUpdateUser(record.getUpdateUser());
            info.setUpdateTime(new Date());
            info.setIsUse("1");
            info.setItemOrder(initialCode);
            dictItems.add(info);
            initialCode++;
        }

        dictItemService.batchSave(dictItems);
    }

    public VO jobFaceUnqualified() {
        List<SafetyEmployeeMessageDO> list = safetyEmployeeMessageService.selectListOfFaceUnqualified();
        if (CollectionUtils.isEmpty(list)) {
            return VoUtil.success("无需要更新人脸下发的数据");
        }
        //2.取值正式员工大门人脸各厂区对应设备集合
        EmpowerDeviceListParam empowerParam = new EmpowerDeviceListParam();
        empowerParam.setEmpowerType("正式员工");
        empowerParam.setRegionName("大门人脸");
        List<SafetyEmpowerDeviceDO> zsEmpowerDeviceS = safetyEmpowerDeviceService.selectList(empowerParam);

        EmpowerDeviceListParam empowerParamForWX = new EmpowerDeviceListParam();
        empowerParamForWX.setEmpowerType("外协");
        empowerParamForWX.setRegionName("大门人脸");
        List<SafetyEmpowerDeviceDO> wxEmpowerDeviceS = safetyEmpowerDeviceService.selectList(empowerParamForWX);
        if(CollectionUtils.isEmpty(zsEmpowerDeviceS) ||CollectionUtils.isEmpty(wxEmpowerDeviceS)){
            LogUtils.error("\"code\":\"9999\",\"msg\":\"正式员工/外协的大门人脸设备未维护，请维护!\"");
        }
        // 下发
        safetyEmpPhotoInfoService.sendCrkForFaceUnqualified(list, zsEmpowerDeviceS, wxEmpowerDeviceS);
        return VoUtil.success();
    }
}
