package com.yutong.safety.core.handler.build;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.EmployeeMessageListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.build.*;
import com.yutong.safety.core.db.entity.build.param.BuildEmployeePowerListParam;
import com.yutong.safety.core.db.entity.build.param.BuildPowerListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildGroupDutyVO;
import com.yutong.safety.core.db.entity.emp.SafetyDepartChangeListDO;
import com.yutong.safety.core.dto.SafetyEmployeeMessageDTO;
import com.yutong.safety.core.dto.build.SafetyBuildPowerDTO;
import com.yutong.safety.core.handler.spot.SafetySpotCheckTaskHandler;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.build.SafetyBuildConfigService;
import com.yutong.safety.core.service.build.SafetyBuildEmployeePowerService;
import com.yutong.safety.core.service.build.SafetyBuildPowerService;
import com.yutong.safety.core.service.build.SafetyBuildWhiteListService;
import com.yutong.safety.core.service.emp.SafetyDepartChangeListService;
import com.yutong.safety.core.service.spot.SafetySpotCheckProcessService;
import com.yutong.safety.core.service.spot.SafetySpotCheckTaskService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SafetyBuildWhiteListHandler {

    @Resource
    SafetyBuildWhiteListService safetyBuildWhiteListService;

    @Resource
    SafetyBuildProcessHandler safetyBuildProcessHandler;

    @Resource
    SafetyEmployeeMessageService safetyEmployeeMessageService;

    @Resource
    SafetyBuildPowerService safetyBuildPowerService;

    @Autowired
    SafetySpotCheckTaskHandler safetySpotCheckTaskHandler;

    @Autowired
    SafetyBuildConfigService safetyBuildConfigService;

    @Autowired
    SafetyDepartChangeListService safetyDepartChangeListService;

    @Autowired
    SafetyBuildEmployeePowerService safetyBuildEmployeePowerService;

    @Autowired
    SafetySpotCheckTaskService safetySpotCheckTaskService;
    @Autowired
    SafetySpotCheckProcessService safetySpotCheckProcessService;

    public Object pageList(SafetyBuildWhiteListDO param, String token) {
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        if (com.yutong.gw.utils.StringUtils.isEmpty(workNo)) {
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(), "token已失效，请重新登录!");
        }

        //根据当前用户类型 1-员工 2-管理员查看所有 3-保密负责人
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if (!"2".equals(userType)) {
            ArrayList<String> empList = new ArrayList<>();
            empList.add(workNo);
            List<SafetyBuildPowerDO> list = safetyBuildPowerService.getListByUserCode(empList);

            if (!CollectionUtils.isEmpty(list)) {
                param.setDutyDeptCode(list.stream().map(SafetyBuildPowerDO::getDutyDeptCode).distinct().collect(Collectors.joining(";")));
            }else{
                param.setDutyDeptCode("-1");
            }
        }

        List<SafetyBuildWhiteListDO> list = safetyBuildWhiteListService.selectPageList(param);

        Map<String, String> extracted = extracted(list);
        if(extracted != null){
            for (SafetyBuildWhiteListDO safetyBuildWhiteListDO : list) {
                String employeeCode = safetyBuildWhiteListDO.getEmployeeCode();
                String s = extracted.get(employeeCode);
                safetyBuildWhiteListDO.setDeviceName(s);
            }
        }

        long total = safetyBuildWhiteListService.selectPageCount(param);
        return new PageVO<>(total, list);
    }

    private Map<String, String> extracted(List<SafetyBuildWhiteListDO> list) {
        Map<String, String> resultMap = new HashMap<>();
        if (CollectionUtils.isEmpty(list)) {return resultMap;}
        List<String> collect = list.stream().map(SafetyBuildWhiteListDO::getEmployeeCode).collect(Collectors.toList());

        BuildEmployeePowerListParam en = new BuildEmployeePowerListParam();
        en.setEmployeeCodeList(collect);
        en.setDeleteFlag("0");
        List<SafetyBuildEmployeePowerDO> buildList = safetyBuildEmployeePowerService.selectByExample(en);
        if (CollectionUtils.isEmpty(buildList)) {return resultMap;}
        resultMap = buildList.stream().collect(Collectors.toMap(
                SafetyBuildEmployeePowerDO::getEmployeeCode,
                item -> item.getDeviceName() != null ? item.getDeviceName() : "",
                (existingValue, newValue) -> {
                    if (existingValue.isEmpty()) {
                        return newValue != null ? newValue : "";
                    }
                    if (StringUtils.isBlank(newValue)) {
                        return existingValue;
                    }
                    if (!existingValue.contains(newValue)) {
                        return existingValue + "," + newValue;
                    }
                    return existingValue;
                }
        ));

        return resultMap;
    }
    public void delete(Integer id) {
        safetyBuildWhiteListService.delete(id);
    }

    public String save(SafetyBuildWhiteListDO param, String token) {
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        if (com.yutong.gw.utils.StringUtils.isEmpty(workNo)) {
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(), "token已失效，请重新登录!");
        }

        //查询人员是否重复添加
        SafetyBuildWhiteListDO p = new SafetyBuildWhiteListDO();
        p.setEmployeeCode(param.getEmployeeCode());
        long l1 = safetyBuildWhiteListService.selectPageCount(p);
        if (l1 > 0) {
            return "该人员已经在白名单中，故不能再次添加！";
        }

        param.setIsDelete("0");
        param.setCreateTime(DateUtils.format(new Date(), 2));
        param.setUpdateTime(DateUtils.format(new Date(), 2));
        param.setSourceFrom("0");

        //根据要新增人的员工号查询其基本信息
        //01开头的员工号转为60开头的新员工号
        if(param.getEmployeeCode().startsWith("01")){
            String newPatrolEmpNo = safetySpotCheckTaskService.employeeConvertWorkNo(param.getEmployeeCode());
            param.setEmployeeCode(newPatrolEmpNo);
        }
        SafetyEmployeeMessageDO emp = safetyEmployeeMessageService.getByNo(param.getEmployeeCode());
        String orgCodePath = emp.getOrgCodePath();
        if (StringUtils.isEmpty(orgCodePath)) {
            LogUtils.info("{}在添加{}到白名单管理的时候，其组织路径为空！",tockenUser.get("realName"),emp.getEmpName());
            return "被添加人的组织路径为空，不允许添加！";
        }

        //查询当前登录人的责任部门
        BuildPowerListParam listParam = new BuildPowerListParam();
        listParam.setEmpNo(workNo);
        List<SafetyBuildPowerDO> doList = safetyBuildPowerService.selectList(listParam);
        if (CollectionUtils.isEmpty(doList)) {
            LogUtils.info("{}在添加{}到白名单管理的时候，其对应的责任部门为空！",tockenUser.get("realName"),emp.getEmpName());
            return "被添加人的对应的责任部门为空，不允许添加！";
        }

        //根据当前用户类型 1-员工 2-管理员查看所有 3-保密负责人
        String userType = safetyBuildProcessHandler.checkUserType(token);
        userType = userType.split(":")[0];
        param.setCreateUserRole("2".equals(userType) ? "门禁系统管理员" : "门禁管理员");
        param.setCreateUserCode(workNo);
        param.setCreateUserName(tockenUser.get("realName"));
        String dutyDeptCode;
        if ("3".equals(userType)) {
            Optional<SafetyBuildPowerDO> optional = doList.stream()
                    .filter(info -> StringUtils.isNotBlank(info.getDutyDeptCode())
                            && orgCodePath.contains(info.getDutyDeptCode()))
                    .findFirst();

            if (!optional.isPresent()) {
                return "当前员工不在管理员的管辖责任部门下";
            } else {
                SafetyBuildPowerDO info = optional.get();
                param.setDutyDeptCode(info.getDutyDeptCode());
                param.setDutyDeptName(info.getDutyDeptName());
                dutyDeptCode = info.getDutyDeptCode();
            }
        } else {
            //责任部门code（都是codePath）
            dutyDeptCode = doList.get(0).getDutyDeptCode();
            param.setDutyDeptCode(dutyDeptCode);
            param.setDutyDeptName(doList.get(0).getDutyDeptName());
        }

        //拼接设备信息
        Set<String> deviceCodeSet = new HashSet<>();
        Set<String> deviceNameSet = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        for (SafetyBuildPowerDO powerDO : doList) {
            deviceCodeSet.add(powerDO.getDeviceCode());
            deviceNameSet.add(powerDO.getDeviceName());
            if(sb.indexOf(powerDO.getEmployeeCode())==-1){
                sb.append(powerDO.getEmployeeName()+"("+powerDO.getEmployeeCode()+")、");
            }
        }
        param.setDeviceCode(String.join(",", deviceCodeSet));
        param.setDeviceName(String.join(",", deviceNameSet));

        //去验证该责任部门的白名单人数是否超过限制数量
        //查询该责任部门设置的可授权白名单数量
        //查询私设的白名单人数限制
        BuildPowerListParam buildPowerListParam = new BuildPowerListParam();
        buildPowerListParam.setDutyDeptCode(dutyDeptCode);
        Integer whiteNum = 0;
        if(doList.get(0).getEmposerNum() != null){
            whiteNum = doList.get(0).getEmposerNum();
        }else{
            //查询公共的白名单限制数量
            List<SafetyBuildConfigDO> configDOList = safetyBuildConfigService.query();
            if (CollectionUtils.isEmpty(configDOList)) {
                return "白名单限制数量未配置，不允许新增！";
            }
            whiteNum = configDOList.get(0).getDepartGrantNum();
        }

        //查询该责任部门现在的白名单人数
        SafetyBuildWhiteListDO countParam = new SafetyBuildWhiteListDO();
        countParam.setDutyDeptCode(dutyDeptCode);
        long l = safetyBuildWhiteListService.selectPageCount(countParam);

        //判断是否超过限制
        if(whiteNum <= l){
            return "您部门可添加的白名单人员数量超过限制，请联系门禁管理员 "+sb.toString();
        }

        safetyBuildWhiteListService.batchSave(Collections.singletonList(param));
        return "添加成功！";
    }

    public Object importData(MultipartFile file, String token) {
        SafetyBuildWhiteListDO param = new SafetyBuildWhiteListDO();
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        if (com.yutong.gw.utils.StringUtils.isEmpty(workNo)) {
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(), "token已失效，请重新登录!");
        }

        param.setIsDelete("0");
        param.setCreateTime(DateUtils.format(new Date(), 2));
        param.setUpdateTime(DateUtils.format(new Date(), 2));
        param.setSourceFrom("0");

        //根据当前用户类型 1-员工 2-门禁系统管理员 3-门禁管理员
        String userType = safetyBuildProcessHandler.checkUserType(token);
        userType = userType.split(":")[0];
        param.setCreateUserRole("2".equals(userType) ? "门禁系统管理员" : "门禁管理员");
        param.setCreateUserCode(workNo);
        param.setCreateUserName(tockenUser.get("realName"));

        //查询当前登录人的责任部门
        BuildPowerListParam listParam = new BuildPowerListParam();
        listParam.setEmpNo(workNo);
        List<SafetyBuildPowerDO> doList = safetyBuildPowerService.selectList(listParam);
        String dutyDeptCode = "";
        if (!CollectionUtils.isEmpty(doList)) {
            //责任部门code（都是codePath
            dutyDeptCode = doList.get(0).getDutyDeptCode();
            param.setDutyDeptCode(dutyDeptCode);
            param.setDutyDeptName(doList.get(0).getDutyDeptName());
        }

        Workbook workbook = ExcelUtils.getWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        //获得当前sheet的开始行
        int firstRowNum = sheet.getFirstRowNum();
        //获得当前sheet的结束行
        int lastRowNum = sheet.getLastRowNum();

        //判定人数是否超限
        if("3".equals(userType) && !StringUtils.isBlank(dutyDeptCode)){
            //查询该责任部门的可设置白名单的人数
            BuildPowerListParam buildPowerListParam = new BuildPowerListParam();
            buildPowerListParam.setDutyDeptCode(dutyDeptCode);
            Integer whiteNum = 0;
            if(doList.get(0).getEmposerNum() != null){
                whiteNum = doList.get(0).getEmposerNum();
            }else{
                //查询公共的白名单限制数量
                List<SafetyBuildConfigDO> configDOList = safetyBuildConfigService.query();
                if (CollectionUtils.isEmpty(configDOList)) {
                    return "白名单限制数量未配置，不允许新增！";
                }
                whiteNum = configDOList.get(0).getDepartGrantNum();
            }

            if(whiteNum < (lastRowNum - firstRowNum)){
                return "当前责任部门可设置的白名单超出限制，不允许新增！";
            }
        }

        List<SafetyBuildWhiteListDO> insertList = new ArrayList<>();
        String emptyNo = "";
        List<String> empList = new ArrayList<>();
        for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
            //获得当前行
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            String staffNo = ExcelUtils.getValue(row.getCell(0));//员工号
            if (StringUtils.isEmpty(staffNo)) {
                emptyNo += rowNum + ";";
                continue;
            }

            if (empList.contains(staffNo)) {
                continue;
            }
            empList.add(staffNo);

            String staffName = ExcelUtils.getValue(row.getCell(1));//姓名
            String reason = ExcelUtils.getValue(row.getCell(2));//原因

            SafetyBuildWhiteListDO en = new SafetyBuildWhiteListDO();
            BeanUtils.copyProperties(param, en);
            en.setEmployeeCode(staffNo);
            en.setEmployeeName(staffName);
            en.setCreateReason(reason);

            insertList.add(en);
        }
        String msg = StringUtils.isEmpty(emptyNo) ? "" : "第" + emptyNo + "行员工号为空! ";

        // 导入的员工号
        List<String> empNos = insertList.stream().map(SafetyBuildWhiteListDO::getEmployeeCode).collect(Collectors.toList());
        // 员工台账信息
        List<SafetyEmployeeMessageDO> list = safetySpotCheckProcessService.getEmpInfoByUserCodeList(empNos);
        if (CollectionUtils.isEmpty(list)) {
            return "员工信息不存在。";
        }
        List<String> empNoList = list.stream().map(SafetyEmployeeMessageDO::getEmpNo).collect(Collectors.toList());
        List<SafetyBuildWhiteListDO> dataList = insertList.stream().filter(li -> empNoList.contains(li.getEmployeeCode())).collect(Collectors.toList());
        List<String> notFoundData = insertList.stream().filter(li -> !empNoList.contains(li.getEmployeeCode())).map(SafetyBuildWhiteListDO::getEmployeeCode).collect(Collectors.toList());
        msg += CollectionUtils.isEmpty(notFoundData) ? "" : notFoundData + "员工不存在。";

        // 赋值岗位编码
        // 员工数据转换成map
        Map<String, SafetyEmployeeMessageDO> dataMap = list.stream().collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, t -> t));
        dataList.forEach(li -> {
            SafetyEmployeeMessageDO employeeMessageDO = dataMap.get(li.getEmployeeCode());
            if (employeeMessageDO != null) {
                li.setPositionCode(employeeMessageDO.getPositionCode());
                li.setPositionName(employeeMessageDO.getPositionName());
            }
        });

        // 门禁管理员导入逻辑
        if ("3".equals(userType)) {
            String s = saveUploadData(dataList, list, doList);
            msg += s;
        } else {
            safetyBuildWhiteListService.batchSave(dataList);
            msg += "本次新增: " + dataList.size() + "条!";
        }
        return msg;
    }

    /**
     * 门禁管理员导入数据处理
     * @param insertList 导入的数据
     * @param list 员工台账信息
     * @param doList 门禁管理员管理的责任部门信息
     * @return 返回导入的信息
     */
    private String saveUploadData(List<SafetyBuildWhiteListDO> insertList, List<SafetyEmployeeMessageDO> list, List<SafetyBuildPowerDO> doList) {
        // 若为门禁管理员，校验员工是否在管理员的管辖责任部门下，赋值员工的责任部门
        List<String> notDutyList = new ArrayList<>();
        List<SafetyBuildWhiteListDO> batchList = new ArrayList<>();
        // 门禁管理员的责任部门
        List<String> dutyDeptCodeList = doList.stream().map(SafetyBuildPowerDO::getDutyDeptCode).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        // 员工数据转换成map
        Map<String, String> dataMap = list.stream().collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, SafetyEmployeeMessageDO::getOrgCodePath));
        for (SafetyBuildWhiteListDO li : insertList) {
            String employeeCode = li.getEmployeeCode();
            // 获取组织路径
            String orgCodePath = dataMap.get(employeeCode);
            if (StringUtils.isBlank(orgCodePath)) {
                notDutyList.add(employeeCode);
                continue;
            }
            // 员工的组织路径是否包含门禁管理员的责任部门 true 包含  false不包含
            boolean flag = dutyDeptCodeList.stream().anyMatch(orgCodePath::contains);
            if (!flag) {
                // 不在责任部门下
                notDutyList.add(employeeCode);
                continue;
            }

            // 在门禁责任部门下
            for (SafetyBuildPowerDO info : doList) {
                if (orgCodePath.contains(info.getDutyDeptCode())) {
                    li.setDutyDeptCode(info.getDutyDeptCode());
                    li.setDutyDeptName(info.getDutyDeptName());
                    break;
                }
            }
            batchList.add(li);
        }

        safetyBuildWhiteListService.batchSave(batchList);

        String msg = CollectionUtils.isEmpty(notDutyList) ? "" :  notDutyList + "员工不在管理员的管辖责任部门下。";
        msg += "本次新增: " + batchList.size() + "条!";
        return msg;
    }

    public HSSFWorkbook exportData(SafetyBuildWhiteListDO param, String token) throws Exception {
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        if (com.yutong.gw.utils.StringUtils.isEmpty(workNo)) {
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(), "token已失效，请重新登录!");
        }

        //根据当前用户类型 1-员工 2-管理员查看所有 3-保密负责人
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if (!"2".equals(userType)) {
            ArrayList<String> empList = new ArrayList<>();
            empList.add(workNo);
            List<SafetyBuildPowerDO> list = safetyBuildPowerService.getListByUserCode(empList);

            if (!CollectionUtils.isEmpty(list)) {
                param.setDutyDeptCode(list.get(0).getDutyDeptCode());
            }else{
                param.setDutyDeptCode("-1");
            }
        }
        param.setPageSize(5000);

        List<SafetyBuildWhiteListDO> list = safetyBuildWhiteListService.selectPageList(param);

        Map<String, String> extracted = extracted(list);

        String[] title = new String[]{"姓名", "员工号", "岗位", "责任部门", "授权门禁", "添加角色", "添加人", "添加时间", "原因"};
        String[][] values = new String[list.size()][];
        SafetyBuildWhiteListDO en;
        for (int i = 0; i < list.size(); i++) {
            en = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = en.getEmployeeName();
            values[i][1] = en.getEmployeeCode();
            values[i][2] = en.getPositionName();
            values[i][3] = en.getDutyDeptName();
            if(extracted != null){
                values[i][4] = extracted.get(en.getEmployeeCode());
            }
            values[i][5] = en.getCreateUserRole();
            values[i][6] = en.getCreateUserName();
            values[i][7] = en.getCreateTime();
            values[i][8] = en.getCreateReason();
        }
        String sheetName = "白名单";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    public void updateWhiteList() {
        //查询符合白名单规则的人，并按照规则的更新时间进行倒序排列
        List<SafetyEmployeeMessageDTO> userList = safetyBuildWhiteListService.selectRuleUser();
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }

        // user_type=LD 或者 org_name_path 里包含绿都 或者 emp_no 是0开头 或者员工号为空的 不能加入白名单
        userList = userList.stream().filter(li ->
                (StringUtils.isEmpty(li.getUserType()) || !li.getUserType().equals(Constants.USER_TYPE_LD)) &&
                        StringUtils.isNotEmpty(li.getEmpNo()) && !li.getEmpNo().startsWith("0") &&
                        (StringUtils.isEmpty(li.getOrgNamePath()) || !li.getOrgNamePath().contains(Constants.ORG_NAME_KEYS))).collect(Collectors.toList());
        //去重
        List<String> empCodeList = new ArrayList<>();
        List<SafetyEmployeeMessageDTO> userCollect = new ArrayList<>();
        for (SafetyEmployeeMessageDTO en : userList) {
            //判断是否重复或者白名单已存在
            if (empCodeList.contains(en.getEmpNo())) {
                continue;
            }
            empCodeList.add(en.getEmpNo());
            userCollect.add(en);
        }
        List<String> empOrgList = userCollect.stream().map(SafetyEmployeeMessageDTO::getOrgCode).distinct().collect(Collectors.toList());
        //根据人查询对应的责任部门和授权门禁，只取最新的一条
        List<SafetyBuildPowerDTO> list = getDutyDeptForEmpNos(empOrgList,userList);

        //遍历所有人，开始组装插入的数据
        List<SafetyBuildWhiteListDO> insertList = new ArrayList<>();
        for (SafetyEmployeeMessageDTO curEmpDto : userCollect) {
            SafetyBuildWhiteListDO w = new SafetyBuildWhiteListDO();
            w.setCreateUserRole("管理员");

            w.setEmployeeCode(curEmpDto.getEmpNo());
            w.setEmployeeName(curEmpDto.getEmpName());
            w.setPositionCode(curEmpDto.getPositionCode());
            w.setPositionName(curEmpDto.getPositionName());

            w.setDeptCode(curEmpDto.getOrgCode());
            w.setDeptName(curEmpDto.getOrgName());
            w.setCreateReason(curEmpDto.getWhiteReason() + "-" + curEmpDto.getWhiteId());
            w.setCreateUserName(curEmpDto.getWhiteCreateBy()); //设置创建人为规则创建人
            w.setCreateTime(DateUtils.format(new Date(), 2));
            w.setUpdateTime(DateUtils.format(new Date(), 2));
            w.setIsDelete("0");
            w.setSourceFrom("1");
            if(!CollectionUtils.isEmpty(list)){
                List<SafetyBuildPowerDTO> curList = list.stream().filter(e->e.getTmpList().contains(curEmpDto.getEmpNo())).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(curList)){
                    w.setDeviceCode(curList.get(0).getDeviceCode());
                    w.setDeviceName(curList.get(0).getDeviceName());
                    w.setDutyDeptCode(curList.get(0).getDutyDeptCode());
                    w.setDutyDeptName(curList.get(0).getDutyDeptName());
                }
            }
            insertList.add(w);
        }
        //删除以往按照规则新增的数据
        safetyBuildWhiteListService.delete();
        //新增数据
        safetyBuildWhiteListService.batchSave(insertList);

    }

    /**
     * 获取员工所属的责任部门
     * @param orgCodeList-员工所属组织
     * @Param employeelists 组织编码
     * @return
     */
    public List<SafetyBuildPowerDTO> getDutyDeptForEmpNos(List<String> orgCodeList,List<SafetyEmployeeMessageDTO> employeelists){
        List<SafetyBuildPowerDTO> dtoList = new ArrayList<>();
        List<BuildGroupDutyVO> groupDutyVOS = safetyBuildPowerService.getGroupDuty(orgCodeList);
        if(CollectionUtils.isEmpty(groupDutyVOS)){
            LogUtils.info("根据范围内人员未找到对应责任部门！");
            return dtoList;
        }
        List<String> dutyCodeList = groupDutyVOS.stream().map(BuildGroupDutyVO::getDutyDeptCode).collect(Collectors.toList());
        List<SafetyBuildPowerDO> buildPowerDOS = safetyBuildPowerService.getOnyDutyDeptList(dutyCodeList);
        if(CollectionUtils.isEmpty(buildPowerDOS)){
            LogUtils.info("根据范围内人员未找到对应责任部门！");
            return dtoList;
        }
        Map<String,SafetyBuildPowerDO> buildPowerMap = buildPowerDOS.stream().collect(Collectors.toMap(SafetyBuildPowerDO::getDutyDeptCode, e -> e));
        Map<String,List<BuildGroupDutyVO>> dutyGroupMap = groupDutyVOS.stream().collect(Collectors.groupingBy(BuildGroupDutyVO::getDutyDeptCode));
        dutyGroupMap.forEach((key,value)->{
            List<String> ogrList = value.stream().map(BuildGroupDutyVO::getCode).collect(Collectors.toList());
            List<SafetyEmployeeMessageDTO> messageDOS = employeelists.stream().filter(e->ogrList.contains(e.getOrgCode())).collect(Collectors.toList());
            SafetyBuildPowerDO  curPow= buildPowerMap.get(key);
            if(curPow == null || CollectionUtils.isEmpty(messageDOS)){
                return;
            }
            List<String> messageCodeList = messageDOS.stream().map(SafetyEmployeeMessageDTO::getEmpNo).distinct().collect(Collectors.toList());
            SafetyBuildPowerDTO newDto = new SafetyBuildPowerDTO();
            BeanUtils.copyProperties(curPow,newDto);
            newDto.setTmpList(messageCodeList);

            dtoList.add(newDto);
        });

        return dtoList;
    }

    public SafetyEmployeeMessageDO getEmployeeByWorkNo(String workNo, String token) {
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String curWorkNo = tockenUser.get("workNo");
        if (com.yutong.gw.utils.StringUtils.isEmpty(curWorkNo)) {
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(), "token已失效，请重新登录!");
        }

        if (StringUtils.isNotBlank(workNo) && workNo.startsWith("01")) {
            workNo = safetySpotCheckTaskHandler.employeeConvertWorkNo(workNo);
        }
        EmployeeMessageListParam listParam = new EmployeeMessageListParam();
        listParam.setFullEmpNo(workNo);

        //根据当前用户类型 1-员工 2-管理员查看所有 3-保密负责人
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if (!StringUtils.isBlank(userType) && userType.startsWith("3")) {
            //保密负责人查询自己对应的责任部门
            ArrayList<String> list1 = new ArrayList<>();
            list1.add(curWorkNo);
            List<SafetyBuildPowerDTO> list2 = safetyBuildPowerService.getListWithPosByUserCode(list1);
            if (CollectionUtils.isEmpty(list2)) {
                LogUtils.info("{}对应的责任部门为空，不支持新增！",curWorkNo);
                return null;
            }
            listParam.setOrgCode(list2.get(0).getOrgCode());
        }

        List<SafetyEmployeeMessageDO> list = safetyEmployeeMessageService.pageList(listParam);
        if (CollectionUtils.isEmpty(list)) {
            return new SafetyEmployeeMessageDO();
        }
        if (list.size() > 1) {
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "根据入参获取到多条数据，请核查");
        }
        return list.get(0);
    }

    public void updateDutyUserAuto(){
        //昨儿个离职的
        SafetyDepartChangeListDO en = new SafetyDepartChangeListDO();
        en.setCreateTime(DateUtils.format(DateUtils.addDays(new Date(), -1), 1));
        en.setChangeType("是");
        List<String> leaveList = safetyDepartChangeListService.getChangeEmpCodeList(en);
        if (CollectionUtils.isEmpty(leaveList)) {
            return;
        }


    }
}
