package com.yutong.safety.core.handler.fireInspection;

import cn.hutool.poi.excel.ExcelReader;
import com.yutong.safety.common.constants.FireInspectionEnum;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetyGroupInfoDO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionStandardDO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionTaskSetDO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionWorkStepDO;
import com.yutong.safety.core.db.entity.fireInspection.excel.SafetyFireInspectionTaskSetExcel;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionStandardQueryParams;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskSetAddParams;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionTaskSetQueryParams;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.SafetyGroupService;
import com.yutong.safety.core.service.SafetyLogMessageService;
import com.yutong.safety.core.service.fireInspection.SafetyFireInspectionStandardService;
import com.yutong.safety.core.service.fireInspection.SafetyFireInspectionTaskSetService;
import com.yutong.safety.core.service.fireInspection.SafetyFireInspectionWorkStepService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.yutong.safety.core.db.entity.fireInspection.common.FireInspectionConstants.*;

@Component
public class SafetyFireInspectionTaskSetHandler {
    @Resource
    private SafetyGroupService safetyGroupService;
    @Resource
    private SafetyLogMessageService safetyLogMessageService;
    @Resource
    private SafetyBaseHandler safetyBaseHandler;
    @Resource
    private SafetyFireInspectionTaskSetService safetyFireInspectionTaskSetService;
    @Resource
    private SafetyFireInspectionWorkStepService safetyFireInspectionWorkStepService;
    @Resource
    private SafetyFireInspectionStandardService safetyFireInspectionStandardService;
    @Resource
    private SafetyFireInspectionStandardHandler safetyFireInspectionStandardHandler;
    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;

    /**
     * 分页数据获取
     *
     * @param param 入参
     * @param token 人员信息
     * @return 返回符合条件的数据
     */
    public PageVO<SafetyFireInspectionTaskSetDO> selectPageList(SafetyFireInspectionTaskSetQueryParams param, String token) {
        //获取登录用户的权限
        String workNo = safetyBaseHandler.getListPermission(token, FIRE_INSPECTION_ROLE);
        // 当前部门的所有子部门
        List<String> empCodeList = safetyGroupService.getEmpCodeList(param.getWorkDeptCode());
        // 获取数据
        long total = safetyFireInspectionTaskSetService.selectCount(param, workNo, empCodeList);
        List<SafetyFireInspectionTaskSetDO> list = safetyFireInspectionTaskSetService.selectList(param, workNo, empCodeList);
        if (!CollectionUtils.isEmpty(list)) {
            List<String> deptCode = list.stream().map(SafetyFireInspectionTaskSetDO::getWorkDeptCode).collect(Collectors.toList());
            List<SafetyGroupInfoDO> listByCode = safetyGroupService.getListByCode(deptCode);
            Map<String, SafetyGroupInfoDO> deptMap = listByCode.stream().collect(Collectors.toMap(SafetyGroupInfoDO::getCode, t -> t));
            list.forEach(li -> {
                SafetyFireInspectionStandardQueryParams queryParams = new SafetyFireInspectionStandardQueryParams();
                queryParams.setWorkStepCode(li.getWorkStepCode());
                queryParams.setRiskLevelList(FireInspectionEnum.getRiskLevelByItems(li.getRiskLevel()));
                long l = safetyFireInspectionStandardService.selectCount(queryParams, workNo);
                li.setInspectionSubDimensionNum(l + "项");
                SafetyGroupInfoDO infoDO = deptMap.get(li.getWorkDeptCode());
                if (infoDO != null) {
                    li.setOrgNamePath(infoDO.getOrgNamePath());
                }
                FireInspectionEnum fireInspectionEnum = FireInspectionEnum.getFireInspectionEnumByCode(li.getRiskLevel());
                li.setRiskLevel(fireInspectionEnum.getRiskLevel());
                li.setInspectionCycle(fireInspectionEnum.getInspectionCycle());
                li.setInspectionLevel(fireInspectionEnum.getInspectionLevel());
            });
        }
        return new PageVO<>(total, list);
    }

    /**
     * 详情页分页数据获取
     *
     * @param param 入参
     * @param token 人员信息
     * @return 返回符合条件的数据
     */
    public PageVO<SafetyFireInspectionStandardDO> selectSubDimensionList(SafetyFireInspectionTaskSetQueryParams param, String token) {
        if (StringUtils.isBlank(param.getWorkStepCode()) && StringUtils.isBlank(param.getRiskLevel())) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "作业环节编码和风险等级不能为空");
        }
        // 获取风险等级详细信息
        List<String> list = FireInspectionEnum.getRiskLevelByItems(param.getRiskLevel());
        // 分页获取对象封装
        SafetyFireInspectionStandardQueryParams queryParams = new SafetyFireInspectionStandardQueryParams();
        queryParams.setWorkStepCode(param.getWorkStepCode());
        queryParams.setRiskLevelList(list);
        return safetyFireInspectionStandardHandler.selectPageList(queryParams, token);
    }

    /**
     * 状态修改
     *
     * @param id    数据ID
     * @param token 用户信息
     */
    public Boolean updateStatus(Long id, String token) {
        SafetyFireInspectionTaskSetDO data = safetyFireInspectionTaskSetService.selectByPrimaryKey(id);
        if (data == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "该记录不存在");
        }
        String message = data.toString();
        if (data.getStatus() == 0) {
            data.setStatus(1L);
        } else {
            data.setStatus(0L);
        }
        data.setUpdateInfo(token);
        boolean b = safetyFireInspectionTaskSetService.updateByPrimaryKey(data);
        if (!b) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "数据状态变更失败！");
        }
        saveLogInfo("状态变更", "原数据：" + message, token);
        return true;
    }

    /**
     * 标记删除
     *
     * @param id    数据ID
     * @param token 人员信息
     */
    public boolean delete(Long id, String token) {
        try {
            SafetyFireInspectionTaskSetDO data = safetyFireInspectionTaskSetService.selectByPrimaryKey(id);
            if (data == null) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "该记录不存在");
            }
            data.setDelFlag(SafetyFireInspectionTaskSetDO.DELETE);
            data.setUpdateInfo(token);
            boolean b = safetyFireInspectionTaskSetService.updateByPrimaryKey(data);
            if (!b) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "数据删除失败！");
            }
            saveLogInfo("删除", "原数据：" + data.toString(), token);
            return true;
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "删除出错，ID: " + id + ", 原因: " + e.getMessage());
        }
    }

    /**
     * 批量标记删除
     *
     * @param ids   数据ID
     * @param token 人员信息
     */
    public boolean batchDelete(String ids, String token) {
        if (StringUtils.isBlank(ids)) {
            return true;
        }
        String[] idArr = ids.split(",");
        List<Long> idList = Arrays.stream(idArr).map(Long::parseLong).collect(Collectors.toList());
        boolean b = safetyFireInspectionTaskSetService.deleteById(idList, token);
        if (!b) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "批量删除失败！");
        }
        saveLogInfo("批量删除", "数据ID：" + ids, token);
        return true;
    }

    /**
     * 修改
     *
     * @param param 入参
     * @param token 用户信息
     */
    public boolean update(SafetyFireInspectionTaskSetAddParams param, String token) {
        if (param.getId() == null) throw new BizException(ResponseEnum.FAIL.getCode(), "数据修改失败，数据ID为空！");
        SafetyFireInspectionTaskSetDO data = safetyFireInspectionTaskSetService.selectByPrimaryKey(param.getId());
        if (data == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "该记录不存在");
        }
        // 重复性校验
        boolean b = safetyFireInspectionTaskSetService.selectExit(param);
        if (b) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "此数据行信息已存在！");
        }
        String message = data.toString();
        BeanUtils.copyProperties(param, data);
        data.setUpdateInfo(token);
        b = safetyFireInspectionTaskSetService.updateByPrimaryKey(data);
        if (!b) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "数据修改失败！");
        }
        saveLogInfo("修改", "原数据：" + message, token);
        return true;
    }

    /**
     * 新增
     *
     * @param param 入参对象
     * @param token 用户信息
     */
    public boolean add(SafetyFireInspectionTaskSetAddParams param, String token) {
        try {
            // 数据校验
            boolean b = safetyFireInspectionTaskSetService.selectExit(param);
            if (b) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "此作业环节编码已存在！");
            }
            // 数据新增
            SafetyFireInspectionTaskSetDO data = new SafetyFireInspectionTaskSetDO();
            BeanUtils.copyProperties(param, data);
            data.setStatus(0L);
            data.setCreates(token);
            boolean insert = safetyFireInspectionTaskSetService.insert(data);
            if (!insert) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "数据新增失败！");
            }
            saveLogInfo("新增", "新数据：" + data.toString(), token);
            return true;
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "数据新增出错, 原因: " + e.getMessage());
        }
    }

    public void download(HttpServletResponse response) {
        String fileName = "消防巡检-任务设置导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/taskSetDownload.xlsx");
            InputStream inputStream = classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response, inputStream, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据导入
     *
     * @param param 文件信息
     * @param token 用户信息
     */
    public Object importData(CommonImportParam param, String token) {
        // 文件校验
        ExcelReader excelReader = ExcelUtils.checkFile(param.getFile());

        excelReader.addHeaderAlias("任务名称", "taskName");
        excelReader.addHeaderAlias("作业环节编码", "workStepCode");
        excelReader.addHeaderAlias("风险等级", "riskLevel");
        excelReader.addHeaderAlias("任务对象", "empNo");
        excelReader.addHeaderAlias("任务开始日期", "taskBeginDate");
        excelReader.addHeaderAlias("任务结束日期", "taskEndDate");
        excelReader.addHeaderAlias("备注", "remark");

        List<SafetyFireInspectionTaskSetExcel> dataList = excelReader.read(0, 1, SafetyFireInspectionTaskSetExcel.class);
        if (CollectionUtils.isEmpty(dataList)) return null;

        // 编号赋值
        IntStream.range(0, dataList.size()).forEach(i -> {
            SafetyFireInspectionTaskSetExcel data = dataList.get(i);
            data.setNum(i + 2);
        });

        // 数据为空
        String failInfo = dataList.stream().map(SafetyFireInspectionTaskSetExcel::check).filter(ln -> StringUtils.isNotBlank(ln.getFailInfo())).map(ll -> String.format("第%s行错误：%s", ll.getNum(), ll.getFailInfo())).collect(Collectors.joining(" "));
        if (StringUtils.isNotBlank(failInfo)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), failInfo);
        }

        // 获取作业环节编码信息
        List<String> workStepList = dataList.stream().map(SafetyFireInspectionTaskSetExcel::getWorkStepCode).distinct().collect(Collectors.toList());
        List<SafetyFireInspectionWorkStepDO> workStepDOList = safetyFireInspectionWorkStepService.selectList(workStepList);
        if (CollectionUtils.isEmpty(workStepDOList))
            throw new BizException(ResponseEnum.FAIL.getCode(), "作业环节编码全部匹配失败");
        Map<String, SafetyFireInspectionWorkStepDO> dataMap = workStepDOList.stream().collect(Collectors.toMap(SafetyFireInspectionWorkStepDO::getWorkStepCode, t -> t));

        // 获取指定任务对象信息
        Set<String> empNoList = new HashSet<>();
        dataList.forEach(li -> {
            if (StringUtils.isNotBlank(li.getEmpNo())) {
                empNoList.addAll(Arrays.asList(li.getEmpNo().split(",")));
            }
        });
        Map<String, SafetyEmployeeMessageDO> defaultEmpNoMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(empNoList)) {
            List<SafetyEmployeeMessageDO> listByStaffNo = safetyEmployeeMessageService.getListByStaffNo(new ArrayList<>(empNoList));
            defaultEmpNoMap = listByStaffNo.stream().collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, t -> t));
        }

        // 作业环节编码信息校验
        StringBuffer sb = new StringBuffer();
        List<SafetyFireInspectionTaskSetDO> lastList = new ArrayList<>();
        Map<String, SafetyEmployeeMessageDO> finalDefaultEmpNoMap = defaultEmpNoMap;
        dataList.forEach(li -> {
            String workStepCode = li.getWorkStepCode();
            SafetyFireInspectionWorkStepDO stepInfo = dataMap.get(workStepCode);
            if (stepInfo == null) {
                // 作业环节基础信息校验
                sb.append("第").append(li.getNum()).append("行，作业环节编码错误，根据编码获取对应作业环节信息失败，作业环节编码：").append(workStepCode).append(";\r\n");
            } else {
                li.setWorkStepName(stepInfo.getWorkStepName());
                if (StringUtils.isNotBlank(li.getEmpNo())) {
                    // 指定任务对象
                    String[] asList = li.getEmpNo().split(",");
                    List<String> empCodes = new ArrayList<>();
                    List<String> empNames = new ArrayList<>();
                    List<String> notFound = new ArrayList<>();
                    for (String empNo : asList) {
                        SafetyEmployeeMessageDO employeeMessageDO = finalDefaultEmpNoMap.get(empNo);
                        if (employeeMessageDO == null) {
                            notFound.add(empNo);
                        } else {
                            empCodes.add(employeeMessageDO.getEmpNo());
                            empNames.add(employeeMessageDO.getEmpName());
                        }
                    }
                    if (!CollectionUtils.isEmpty(notFound)) {
                        sb.append("第").append(li.getNum()).append("行，任务对象校验失败，根据员工号获取员工信息出错，员工号：").append(notFound).append("；\r\n");
                    } else {
                        li.setEmpNo(String.join(",", empCodes));
                        li.setEmpName(String.join(",", empNames));
                    }
                }
                // 封装对象
                SafetyFireInspectionTaskSetDO info = new SafetyFireInspectionTaskSetDO();
                BeanUtils.copyProperties(li, info);
                info.setCreates(token);
                info.setWorkDeptCode(stepInfo.getWorkDeptCode());
                info.setWorkDeptName(stepInfo.getWorkDeptName());
                info.setStatus(0L);
                info.setTaskObjectType(StringUtils.isBlank(info.getEmpName()) ? TASK_OBJECT_TYPE_DEFAULT : TASK_OBJECT_TYPE_CUSTOMIZE);
                lastList.add(info);
            }
        });

        // 任务对象和作业编码校验失败报错
        if (StringUtils.isNotBlank(sb.toString())) {
            throw new BizException(ResponseEnum.FAIL.getCode(), sb.toString());
        }

        // 新增
        if (!CollectionUtils.isEmpty(lastList)) {

            // 获取任务信息 数据去重
            List<SafetyFireInspectionTaskSetDO> taskSetDOS = safetyFireInspectionTaskSetService.selectList(workStepList);
            if (!CollectionUtils.isEmpty(taskSetDOS)) {
                // 使用Set实现快速查找
                Set<String> taskSetContents = new HashSet<>(taskSetDOS.size());
                for (SafetyFireInspectionTaskSetDO taskSetDO : taskSetDOS) {
                    String contentKey = generateKey(taskSetDO);
                    taskSetContents.add(contentKey);
                }

                List<String> repeatList = new ArrayList<>();
                for (SafetyFireInspectionTaskSetDO data : lastList) {
                    String contentKey = generateKey(data);
                    if (taskSetContents.contains(contentKey)) {
                        repeatList.add(data.getTaskName());
                    }
                }

                if (!repeatList.isEmpty()) {
                    throw new BizException(ResponseEnum.FAIL.getCode(), "数据重复，以下任务名称行数据重复：" + repeatList);
                }
            }
            safetyFireInspectionTaskSetService.batchSave(lastList);
        }
        saveLogInfo("导入", "文件导入", token);
        return null;
    }

    private void saveLogInfo(String ability, String message, String token) {
        String realName = TokenUtils.getRealName(token);
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("消防巡检-任务设置");
        log.setAbility(ability);
        log.setMessage(message);
        log.setOperator(realName);
        safetyLogMessageService.addLogMessage(log);
    }

    private String generateKey(SafetyFireInspectionTaskSetDO taskSetDO) {
        return taskSetDO.getTaskName() +
                taskSetDO.getWorkStepCode() +
                taskSetDO.getRiskLevel() +
                taskSetDO.getEmpNo() +
                taskSetDO.getTaskBeginDate() +
                taskSetDO.getTaskEndDate();
    }
}
