package com.yutong.safety.core.handler.park;


import com.alibaba.excel.util.StringUtils;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.build.SafetyBuildIssuedLogDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildWhiteListDO;
import com.yutong.safety.core.db.entity.build.vo.BuildGroupDutyEmpNoVO;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.db.entity.park.ParkEmployeeClassifiedDO;
import com.yutong.safety.core.db.entity.park.SafetyParkEmployeePowerInfoDO;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeeClassifiedParam;
import com.yutong.safety.core.db.entity.park.vo.ParkEmployeeClassifiedVo;
import com.yutong.safety.core.dto.ParamDTO;
import com.yutong.safety.core.handler.build.SafetyBuildProcessHandler;
import com.yutong.safety.core.handler.employeeprivatecar.SafetyEmployeePrivateCarHandler;
import com.yutong.safety.core.service.build.SafetyBuildDeviceCollectService;
import com.yutong.safety.core.service.build.SafetyBuildIssuedLogService;
import com.yutong.safety.core.service.build.SafetyBuildPowerService;
import com.yutong.safety.core.service.build.SafetyBuildWhiteListService;
import com.yutong.safety.core.service.employeeprivatecar.SafetyEmployeePrivateCarService;
import com.yutong.safety.core.service.park.SafetyParkEmployeeClassifiedService;
import com.yutong.safety.core.service.park.SafetyParkEmployeePowerInfoService;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SafetyParkEmployeeClassifiedHandler {

    @Resource
    private SafetyBuildProcessHandler safetyBuildProcessHandler;

    @Resource
    private SafetyParkEmployeeClassifiedService safetyParkEmployeeClassifiedService;

    @Resource
    private SafetyBuildPowerService safetyBuildPowerService;

    @Resource
    private SafetyBuildDeviceCollectService safetyBuildDeviceCollectService;
    /**
     * 白名单
     */
    @Resource
    private SafetyBuildWhiteListService safetyBuildWhiteListService;
    /**
     * 私家车
     */
    @Resource
    private SafetyEmployeePrivateCarService safetyEmployeePrivateCarService;

    @Resource
    private SafetyParkPowerProcessHandler safetyParkPowerProcessHandler;

    @Resource
    private SafetyParkEmployeePowerInfoService safetyParkEmployeePowerInfoService;

    @Resource
    private SafetyBuildIssuedLogService safetyBuildIssuedLogService;

    @Resource
    private SafetyEmployeePrivateCarHandler safetyEmployeePrivateCarHandler;

    // 符合条件的厂区ID
    private final List<String> DEPT_INFO = Arrays.asList("149", "156");

    /**
     * 数据分页获取
     *
     * @param listParam 入参
     * @param token     token信息
     * @return 返回分页数据
     */
    public Object selectPageList(ParkEmployeeClassifiedParam listParam, String token) {
        //根据当前用户类型 设置检索条件 1-员工 只能看自己提交的；2-管理员查看所有 3-保密负责人 能看部门以及自己提交的
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if (userType.startsWith("3")) {
            String dutyDeptCode = userType.split(":")[1];
            listParam.setFilterDutyDept(dutyDeptCode);
        } else {
            listParam.setFilterDutyDept(null);
        }
        if (StringUtils.isNotBlank(listParam.getEndDate())) {
            String endDate = DateUtils.format(DateUtils.addDays(DateUtils.parse(listParam.getEndDate(), 1), 1), 1);
            listParam.setEndDate(endDate);
        }
        listParam.setPageFlag(true);
        // 获取数据
        long total = safetyParkEmployeeClassifiedService.selectCount(listParam);
        List<ParkEmployeeClassifiedVo> list = safetyParkEmployeeClassifiedService.selectList(listParam);
        list.forEach(ParkEmployeeClassifiedVo::init); // 初始化部分数据
        return new PageVO<>(total, list);
    }

    /**
     * 数据导出
     *
     * @param param 入参
     * @param token token信息
     * @return 返回文档对象
     */
    public HSSFWorkbook export(ParkEmployeeClassifiedParam param, String token) {
        String userName = TokenUtils.getUserName(token);
        if (StringUtils.isBlank(userName)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "token已失效");
        }
        if (StringUtils.isNotBlank(param.getEndDate())) {
            String endDate = DateUtils.format(DateUtils.addDays(DateUtils.parse(param.getEndDate(), 1), 1), 1);
            param.setEndDate(endDate);
        }

        //根据当前用户类型 设置检索条件 1-员工 只能看自己提交的；2-管理员查看所有 3-保密负责人 能看部门以及自己提交的
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if (userType.startsWith("3")) {
            String dutyDeptCode = userType.split(":")[1];
            param.setFilterDutyDept(dutyDeptCode);
        } else {
            param.setFilterDutyDept(null);
        }
        List<ParkEmployeeClassifiedVo> list = safetyParkEmployeeClassifiedService.selectList(param);
        list.forEach(ParkEmployeeClassifiedVo::init); // 初始化部分数据
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), String.format("导出数据不能超过 %s 条！", Constants.FILE_SIZE));
        }

        String[] title = {"员工号", "姓名", "组织", "人员类型", "员工车辆", "授权厂区", "有效期", "授权厂区来源", "厂区未进出天数", "是否白名单", "白名单添加角色"};
        String[][] values = new String[list.size()][title.length];
        for (int i = 0; i < list.size(); i++) {
            ParkEmployeeClassifiedVo info = list.get(i);
            int j = 0;
            values[i][j++] = info.getEmpNo();
            values[i][j++] = info.getEmpName();
            values[i][j++] = info.getOrgNamePath();
            values[i][j++] = info.getUserTypeCh();
            values[i][j++] = info.getAllCarNo();
            values[i][j++] = info.getFactoryName();
            values[i][j++] = info.getPeriodValidity();
            values[i][j++] = info.getTargetId();
            values[i][j++] = String.valueOf(info.getNotInOutDay());
            values[i][j++] = info.getWhetherWhite();
            values[i][j++] = info.getWhiteRole();
        }

        String sheetName = "无涉密业务关联员工列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 定时任务，添加无涉密业务员工数据
     * 条件：1.员工有研发中心和工程试验中的厂区权限；2.员工没有研发中心和工程试验中心的楼宇门禁权限； 3.员工部门的所属部门在研发中心和工程试验中心没有管理门禁
     * 要求：以上条件需要同时满足
     */
    public void autoGeneralClassifiedData() {
        // 1.获取员工有研发中心或工程试验中心大门权限的数据信息
        List<ParkEmployeeClassifiedDO> list = safetyParkEmployeeClassifiedService.selectParkPowerByFactoryName(DEPT_INFO);
        if (CollectionUtils.isEmpty(list)) return;
        // 1.1 先过滤掉组织编码为空的数据，下面要用到，此处过滤可以减少业务交互
        list = list.stream().filter(li -> StringUtils.isNotBlank(li.getOrgCode())).collect(Collectors.toList());
        // 1.2 取出员工号
        List<String> empNoList = list.stream().map(ParkEmployeeClassifiedDO::getEmpNo).collect(Collectors.toList());


        // 2.员工没有研发中心或工程试验中心 楼宇门禁权限 （实现方法:排除掉有研发中心或工程试验中心楼宇门禁权限的员工）
        // 2.1 获取指定员工列表中有楼宇门禁权限的人员
        List<ParkEmployeeClassifiedDO> empNoPowerList = safetyParkEmployeeClassifiedService.selectBuildPowerByEmpNo(DEPT_INFO, empNoList);
        List<String> empNoFactoryList = empNoPowerList.stream().filter(li -> StringUtils.isNotBlank(li.getFactoryCode())).map(li -> (li.getEmpNo()+li.getFactoryCode())).collect(Collectors.toList());
        // 2.2 存在数据时要进行过滤（查询到的数据是存在特殊部门的楼宇门禁权限的数据， 要过滤掉此部分员工信息）,不存在数据表示对应的员工没有楼宇门禁权限
        if (!CollectionUtils.isEmpty(empNoPowerList)) {
            list = list.stream().filter(li -> !empNoFactoryList.contains(li.getEmpNo() + li.getFactoryCode())).collect(Collectors.toList());
        }

        // 3.过滤掉员工部门对应的所属责任部门在研发中心或工程试验中心中没有管理门禁的数据
        // 3.1 获取特殊部门对应的门禁编码
        List<String> deviceCodes = safetyBuildDeviceCollectService.getListByFactoryName(DEPT_INFO);
        // 3.2 循环判断没个员工的部门所属的责任部门在特殊厂区中是否存在管理门禁， 不存在的符合要求
        List<ParkEmployeeClassifiedDO> lastInfo = Lists.newArrayList();
        list.forEach(li -> {
            // 获取员工部门所属的责任部门的数据信息
            List<BuildGroupDutyEmpNoVO> groupDutyDeviceCode = safetyBuildPowerService.getGroupDutyDeviceCode(li.getOrgCode());
            // 如果数据为空表示此员工部门所属的责任部门不存在，符合要求
            if (CollectionUtils.isEmpty(groupDutyDeviceCode)) {
                lastInfo.add(li);
            } else {
                // 判断此员工部门所属的责任部门的门禁编码是否在特殊部门的门禁编码中 在的话，不符合数据， 不在的话符合要求 b: true 表示有交集，false 表示没有交集， false时符合要求
                boolean b = groupDutyDeviceCode.stream().filter(deCode -> StringUtils.isNotBlank(deCode.getDeviceCode())).map(BuildGroupDutyEmpNoVO::getDeviceCode).anyMatch(deviceCodes::contains);
                if (!b) {
                    lastInfo.add(li);
                }
            }
        });

        // 4.不存在数据表示有无涉密业务关联员工数据
        if (CollectionUtils.isEmpty(lastInfo)) return;

        // 5.数据整理
        // 获取员工号
        empNoList = lastInfo.stream().map(ParkEmployeeClassifiedDO::getEmpNo).collect(Collectors.toList());
        // 5.1 匹配白名单
        List<SafetyBuildWhiteListDO> whiteListDOS = safetyBuildWhiteListService.selectByEmpCodeList(empNoList);
        // 把结果转成MAP ，因为employeeCode 是唯一值，所以不会存在多条数据value直接存储的对象 但是此处也做了一层防护
        Map<String, SafetyBuildWhiteListDO> whiteListDOMap = whiteListDOS.stream().collect(Collectors.toMap(SafetyBuildWhiteListDO::getEmployeeCode, t -> t, (t1, t2) -> t2));

        // 5.2 匹配车辆
        List<SafetyEmployeePrivateCarDO> carDOList = safetyEmployeePrivateCarService.selectByExample(empNoList);
        // 锁定车辆
        List<SafetyEmployeePrivateCarDO> lockCarList = carDOList.stream().filter(ln -> !ln.getLockFlag().equals("0")).collect(Collectors.toList());
        // 锁定车辆转成map 过滤掉车牌号为空的数据，如果key重复时，进行拼接
        Map<String, String> lockCarMap = lockCarList.stream().filter(ln -> StringUtils.isNotBlank(ln.getCarNo())).collect(Collectors.toMap(SafetyEmployeePrivateCarDO::getEmployeeNo, SafetyEmployeePrivateCarDO::getCarNo, (t1, t2) -> t1 + "," + t2));
        // 非锁定车辆
        List<SafetyEmployeePrivateCarDO> carList = carDOList.stream().filter(ln -> ln.getLockFlag().equals("0")).collect(Collectors.toList());
        // 非锁定车辆转成map 过滤掉车牌号为空的数据，如果key重复时，进行拼接
        Map<String, String> carMap = carList.stream().filter(ln -> StringUtils.isNotBlank(ln.getCarNo())).collect(Collectors.toMap(SafetyEmployeePrivateCarDO::getEmployeeNo, SafetyEmployeePrivateCarDO::getCarNo, (t1, t2) -> t1 + "," + t2));

        // 5.3 匹配 授权厂区来源
        // TODO: 2025/7/17 授权厂区来源暂时不取（逻辑需要完善）

        // 5.4 匹配 厂区未进出天数
        // TODO: 2025/7/17 厂区未进出天数需要计算

        lastInfo.forEach(li -> {
            String empNo = li.getEmpNo();
            // 赋值白名单
            SafetyBuildWhiteListDO safetyBuildWhiteListDO = whiteListDOMap.get(empNo);
            if (safetyBuildWhiteListDO != null) {
                li.setWhetherWhite("是");
                li.setWhiteRole(safetyBuildWhiteListDO.getCreateUserRole());
            } else {
                li.setWhetherWhite("否");
            }
            // 赋值车牌号信息
            String lockCarNo = lockCarMap.get(empNo);
            if (StringUtils.isNotBlank(lockCarNo)) {
                li.setLockCarNo(lockCarNo);
            }
            String carNo = carMap.get(empNo);
            if (StringUtils.isNotBlank(carNo)) {
                li.setCarNo(carNo);
            }
        });

        // 6.保存数据
        safetyParkEmployeeClassifiedService.deleteInfo();
        safetyParkEmployeeClassifiedService.batchInsertInfo(lastInfo);
    }

    /**
     * 行级按钮，取消授权功能
     *
     * @param id 取消授权的数据行ID
     */
    public Object dealBuild(String id, String token) {
        String realName = TokenUtils.getRealName(token);
        // 1.获取无涉密数据信息
        ParkEmployeeClassifiedVo info = safetyParkEmployeeClassifiedService.selectInfoById(id);
        if (info == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "无涉密业务员工数据不存在");
        }
        info.init();
        String empNo = info.getEmpNo();
        String endTime = DateUtils.format(DateUtils.addDays(new Date(), -1), 1) + " 23:59:59";
        // 2.封装下发厂区需要的参数实体类 - 关闭的
        ParamDTO paramDTO = new ParamDTO();
        BeanUtils.copyProperties(info, paramDTO);
        paramDTO.setFactoryId(info.getFactoryCode());
        paramDTO.setPowerEndTime(endTime);
        paramDTO.setEndDate(endTime);
        // 获取接口的数据
        Pair<String, Object> pair = safetyParkPowerProcessHandler.generalOrgId(empNo);
        paramDTO.setOrgId(pair.getLeft());
        paramDTO.setCurId(pair.getRight().toString());
        // 获取厂区对应的门禁设备，只取了富士的
        safetyParkPowerProcessHandler.getDeviceId(paramDTO);

        // 3.获取车辆信息

        //查询员工对应的车辆信息
        SafetyEmployeePrivateCarDO privateCarDO = new SafetyEmployeePrivateCarDO();
        privateCarDO.setEmployeeNo(info.getEmpNo());
        privateCarDO.setLockFlag("0"); //只查询未锁定的车辆
        List<SafetyEmployeePrivateCarDO> privateCarDOS = safetyEmployeePrivateCarHandler.selectList(privateCarDO);

        // 4.取消授权
        boolean result = safetyParkPowerProcessHandler.powerUpdate(paramDTO, privateCarDOS);
        List<SafetyBuildIssuedLogDO> logList = new ArrayList<>();
        safetyParkPowerProcessHandler.generatePowerLogRecord(logList, paramDTO, "关闭", "无涉密业务员工取消授权", result);

        // 5.删除此厂区ID的授权记录（标记删除）； 删除无涉密中的此条数据
        SafetyParkEmployeePowerInfoDO record = new SafetyParkEmployeePowerInfoDO();
        record.setUpdateTime(new Date());
        record.setIsDelete((short)1);
        safetyParkEmployeePowerInfoService.cancelEmployeePower(record, empNo, info.getFactoryCode());
        // 删除无涉密中的此条数据
        safetyParkEmployeeClassifiedService.deleteInfoById(id);

        // 6.开通此员工其它剩余权限
        List<String> empNos = Collections.singletonList(empNo);
        List<ParamDTO> paramDTOS = safetyParkEmployeePowerInfoService.selectByEmpNoList(empNos);
        // 获取到数据表示有权限需要执行， 反之无需继续进行
        if (!paramDTOS.isEmpty()) {
            // 7.封装下发厂区需要的参数实体类 - 开通的
            ParamDTO paramDTO1 = paramDTOS.get(0);
            // 取消授权的设备ID在上面已经进行了删除，所以此处不用去重，直接获取未删除的重新下发权限
            if (StringUtils.isNotBlank(paramDTO1.getFactoryId())) {
                // 不为空表示此员工还有其它厂区的权限，需要开通， 反之就不需要开通权限了
                paramDTO.setFactoryId(paramDTO1.getFactoryId());
                paramDTO.setFactoryName(paramDTO1.getFactoryName());
                // 设置过期时间
                if (StringUtils.isNotBlank(paramDTO1.getPowerEndTime())) {
                    paramDTO.setPowerEndTime(paramDTO1.getPowerEndTime());
                    paramDTO.setEndDate(paramDTO1.getPowerEndTime());
                } else {
                    // 自定义过期时间
                    endTime = "2099-12-31 23:59:59";
                    paramDTO.setPowerEndTime(endTime);
                    paramDTO.setEndDate(endTime);
                }
                // 获取厂区对应的门禁设备，只取了富士的 (厂区设备不一样了，需要重新执行)
                safetyParkPowerProcessHandler.getDeviceId(paramDTO);
                // 8.开通其它剩余厂区的权限
                result = safetyParkPowerProcessHandler.powerUpdate(paramDTO, privateCarDOS);
                safetyParkPowerProcessHandler.generatePowerLogRecord(logList, paramDTO, "开通", "无涉密业务员工开通", result);
            }
        }

        // 9.日志记录
        if (!CollectionUtils.isEmpty(logList)) {
            logList.forEach(li -> li.setCreateBy(realName));
            safetyBuildIssuedLogService.batchInsert(logList);
        }
        return null;
    }
}
