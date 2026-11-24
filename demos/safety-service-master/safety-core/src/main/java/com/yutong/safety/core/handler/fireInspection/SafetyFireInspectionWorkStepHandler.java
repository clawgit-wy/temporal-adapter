package com.yutong.safety.core.handler.fireInspection;

import cn.hutool.poi.excel.ExcelReader;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyGroupInfoDO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionWorkStepDO;
import com.yutong.safety.core.db.entity.fireInspection.excel.SafetyFireInspectionWorkStepExcel;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionWorkStepAddParams;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionWorkStepQueryParams;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.service.SafetyGroupService;
import com.yutong.safety.core.service.SafetyLogMessageService;
import com.yutong.safety.core.service.fireInspection.SafetyFireInspectionStandardService;
import com.yutong.safety.core.service.fireInspection.SafetyFireInspectionTaskSetService;
import com.yutong.safety.core.service.fireInspection.SafetyFireInspectionWorkStepService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.yutong.safety.core.db.entity.fireInspection.common.FireInspectionConstants.FIRE_INSPECTION_ROLE;

@Component
public class SafetyFireInspectionWorkStepHandler {
    @Resource
    private SafetyGroupService safetyGroupService;

    @Resource
    private SafetyLogMessageService safetyLogMessageService;

    @Resource
    private SafetyBaseHandler safetyBaseHandler;

    @Resource
    private SafetyFireInspectionWorkStepService safetyFireInspectionWorkStepService;

    @Resource
    private SafetyFireInspectionStandardService safetyFireInspectionStandardService;

    @Resource
    private SafetyFireInspectionTaskSetService safetyFireInspectionTaskSetService;

    /**
     * 分页数据获取
     *
     * @param listParam 入参
     * @param token     人员信息
     * @return 返回符合条件的数据
     */
    public Object selectPageList(SafetyFireInspectionWorkStepQueryParams listParam, String token) {
        //获取登录用户的权限
        String workNo = safetyBaseHandler.getListPermission(token, FIRE_INSPECTION_ROLE);
        // 当前部门的所有子部门
        List<String> empCodeList = safetyGroupService.getEmpCodeList(listParam.getWorkDeptCode());
        // 获取数据
        long total = safetyFireInspectionWorkStepService.selectCount(listParam, workNo, empCodeList);
        List<SafetyFireInspectionWorkStepDO> list = safetyFireInspectionWorkStepService.selectList(listParam, workNo, empCodeList, true);
        // 部门全路径
        setOrgNamePath(list);
        return new PageVO<>(total, list);
    }

    /**
     * 标记删除
     *
     * @param id    数据ID
     * @param token 人员信息
     */
    public boolean delete(Long id, String token) {
        try {
            SafetyFireInspectionWorkStepDO data = safetyFireInspectionWorkStepService.selectByPrimaryKey(id);
            if (data == null) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "该记录不存在");
            }
            data.setDelFlag(SafetyFireInspectionWorkStepDO.DELETE);
            data.setUpdateInfo(token);
            // 删除作业环节数据
            boolean b = safetyFireInspectionWorkStepService.updateByPrimaryKey(data);
            if (!b) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "数据删除失败！");
            }
            List<String> list = Collections.singletonList(data.getWorkStepCode());
            // 删除检查标准数据
            safetyFireInspectionStandardService.deleteByWorkStepCode(list, token);
            // 删除任务设置中的数据
            safetyFireInspectionTaskSetService.deleteByWorkStepCode(list, token);
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
        List<SafetyFireInspectionWorkStepDO> dataList = safetyFireInspectionWorkStepService.selectByExample(idList);
        if (CollectionUtils.isEmpty(dataList)) return true;
        List<String> list = dataList.stream().map(SafetyFireInspectionWorkStepDO::getWorkStepCode).distinct().collect(Collectors.toList());
        // 删除作业环节数据
        boolean b = safetyFireInspectionWorkStepService.deleteById(idList, token);
        if (!b) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "批量删除失败！");
        }
        // 删除检查标准数据
        safetyFireInspectionStandardService.deleteByWorkStepCode(list, token);
        // 删除任务设置中的数据
        safetyFireInspectionTaskSetService.deleteByWorkStepCode(list, token);
        saveLogInfo("批量删除", "数据ID：" + ids, token);
        return true;
    }

    /**
     * 修改
     *
     * @param param 入参
     * @param token 用户信息
     */
    public boolean update(SafetyFireInspectionWorkStepAddParams param, String token) {
        if (param.getId() == null) throw new BizException(ResponseEnum.FAIL.getCode(), "数据修改失败，数据ID为空！");
        SafetyFireInspectionWorkStepDO data = safetyFireInspectionWorkStepService.selectByPrimaryKey(param.getId());
        if (data == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "该记录不存在");
        }
        String message = data.toString();
        // 因为没有关联表查询， 所以在修改作业环节名称时，需要同步检查标准和任务设置的作业环节名称
        if (!data.getWorkStepName().equals(param.getWorkStepName()) || !data.getWorkDeptCode().equals(param.getWorkDeptCode())) {
            // 修改检查标准数据
            safetyFireInspectionStandardService.updateStepNameByStepCode(data.getWorkStepCode(), param.getWorkStepName());
            // 修改任务设置中的数据
            safetyFireInspectionTaskSetService.updateInfoByStepCode(data.getWorkStepCode(), param.getWorkStepName(), param.getWorkDeptCode(), param.getWorkDeptName());
        }
        BeanUtils.copyProperties(param, data);
        data.setUpdateInfo(token);
        boolean b = safetyFireInspectionWorkStepService.updateByPrimaryKey(data);
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
    public boolean add(SafetyFireInspectionWorkStepAddParams param, String token) {
        // 数据校验 根据作业环节编码来判断唯一
        boolean b = safetyFireInspectionWorkStepService.selectExit(param);
        if (b) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "此作业环节编码已存在！");
        }
        // 数据新增
        SafetyFireInspectionWorkStepDO data = new SafetyFireInspectionWorkStepDO();
        BeanUtils.copyProperties(param, data);
        data.setCreates(token);
        boolean insert = safetyFireInspectionWorkStepService.insert(data);
        if (!insert) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "数据新增失败！");
        }
        saveLogInfo("新增", "新数据：" + data.toString(), token);
        return true;
    }

    public void download(HttpServletResponse response) {
        String fileName = "消防巡检-风险点台账-作业环节导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/workStepDownload.xlsx");
            InputStream inputStream = classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response, inputStream, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据文件导入
     *
     * @param file 文件对象
     * @param token 用户信息
     */
    public Object importData(CommonImportParam file, String token) {
        // 文件校验
        ExcelReader excelReader = ExcelUtils.checkFile(file.getFile());
//        ExcelReader excelReader = ExcelUtil.getReader("D:\\me\\document\\开发文档\\003-消防巡检\\模板文件\\作业环节导入模板.xlsx");

        excelReader.addHeaderAlias("作业环节编码", "workStepCode");
        excelReader.addHeaderAlias("作业环节名称", "workStepName");
        excelReader.addHeaderAlias("工序", "process");
        excelReader.addHeaderAlias("作业单元", "workUnit");
        excelReader.addHeaderAlias("作业类型", "workType");
        excelReader.addHeaderAlias("作业部门", "workDeptCode");

        List<SafetyFireInspectionWorkStepExcel> dataList = excelReader.read(0, 1, SafetyFireInspectionWorkStepExcel.class);
        if (CollectionUtils.isEmpty(dataList)) return null;

        // 编号赋值
        IntStream.range(0, dataList.size()).forEach(i -> {
            SafetyFireInspectionWorkStepExcel data = dataList.get(i);
            data.setNum(i + 2);
        });


        // 数据为空和重复数据校验
        List<String> workStepCodes = new ArrayList<>();
        String failInfo = dataList.stream().map(li -> li.check(workStepCodes)).filter(ln -> StringUtils.isNotBlank(ln.getFailInfo())).map(ll -> String.format("第%s行错误：%s", ll.getNum(), ll.getFailInfo())).collect(Collectors.joining(" "));
        if (StringUtils.isNotBlank(failInfo)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), failInfo);
        }

        // 获取部门信息
        List<String> deptCode = dataList.stream().map(SafetyFireInspectionWorkStepExcel::getWorkDeptCode).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        List<SafetyGroupInfoDO> listByCode = safetyGroupService.getListByCode(deptCode);
        Map<String, SafetyGroupInfoDO> deptMap = listByCode.stream().collect(Collectors.toMap(SafetyGroupInfoDO::getCode, t -> t));

        // 部门信息校验
        StringBuffer sb = new StringBuffer();
        List<SafetyFireInspectionWorkStepDO> lastList = new ArrayList<>();
        dataList.forEach(li -> {
            String workDeptCode = li.getWorkDeptCode();
            SafetyGroupInfoDO infoDO = deptMap.get(workDeptCode);
            if (infoDO == null || StringUtils.isBlank(infoDO.getName())) {
                sb.append("第").append(li.getNum()).append("行,作业部门出错，获取作业部门编码失败，作业部门：").append(workDeptCode).append(";\r\n");
            } else {
                li.setWorkDeptName(infoDO.getName());
            }
            SafetyFireInspectionWorkStepDO stepDO = new SafetyFireInspectionWorkStepDO();
            BeanUtils.copyProperties(li, stepDO);
            stepDO.setCreates(token);
            lastList.add(stepDO);
        });

        // 部门信息校验失败报错
        if (StringUtils.isNotBlank(sb.toString())) {
            throw new BizException(ResponseEnum.FAIL.getCode(), sb.toString());
        }

        // 判断是新增还是修改
        List<SafetyFireInspectionWorkStepDO> list = safetyFireInspectionWorkStepService.selectList(workStepCodes);
        if (CollectionUtils.isEmpty(list)) {
            safetyFireInspectionWorkStepService.batchSave(lastList);
        } else {
            List<String> codes = list.stream().map(SafetyFireInspectionWorkStepDO::getWorkStepCode).collect(Collectors.toList());
            List<SafetyFireInspectionWorkStepDO> saveList = new ArrayList<>();
            List<SafetyFireInspectionWorkStepDO> updateList = new ArrayList<>();
            lastList.forEach(li -> {
                String workStepCode = li.getWorkStepCode();
                if (codes.contains(workStepCode)) {
                    // 修改
                    updateList.add(li);
                } else {
                    saveList.add(li);
                }
            });
            if (!CollectionUtils.isEmpty(saveList)) {
                safetyFireInspectionWorkStepService.batchSave(saveList);
            }
            safetyFireInspectionWorkStepService.batchUpdate(updateList);
            // 修改检查标准作业环节名称
            safetyFireInspectionStandardService.batchUpdate(updateList);
            // 修改任务设置作业环节名称和作业部门
            safetyFireInspectionTaskSetService.batchUpdate(updateList);
        }
        saveLogInfo("导入", "批量导入", token);
        return null;
    }

    /**
     * 数据导出
     */
    public void export(HttpServletResponse response, SafetyFireInspectionWorkStepQueryParams param, String token) throws IOException {

        String fileName = new String(("作业环节列表" + DateUtils.format(new Date(), 5) + ".xlsx").getBytes(), "ISO8859-1");
        //获取登录用户的权限
        String workNo = safetyBaseHandler.getListPermission(token, FIRE_INSPECTION_ROLE);
        // 当前部门的所有子部门
        List<String> empCodeList = safetyGroupService.getEmpCodeList(param.getWorkDeptCode());
        List<SafetyFireInspectionWorkStepDO> list = safetyFireInspectionWorkStepService.selectList(param, workNo, empCodeList, false);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), String.format("导出数据不能超过 %s 条！", Constants.FILE_SIZE));
        }
        // 部门全路径
        setOrgNamePath(list);

        String[] title = {"作业环节名称", "作业环节编码", "工序", "作业单元", "作业类型", "作业部门", "最近更新时间", "最近提交人"};
        String[][] values = new String[list.size()][title.length];
        for (int i = 0; i < list.size(); i++) {
            SafetyFireInspectionWorkStepDO info = list.get(i);
            int j = 0;
            values[i][j++] = info.getWorkStepName();
            values[i][j++] = info.getWorkStepCode();
            values[i][j++] = info.getProcess();
            values[i][j++] = info.getWorkUnit();
            values[i][j++] = info.getWorkType();
            values[i][j++] = getOrgNamePath(info.getOrgNamePath());
            values[i][j++] = DateUtils.format(info.getUpdateTime(), 2);
            values[i][j++] = info.getUpdateBy();
        }

        String sheetName = "作业环节";
        HSSFWorkbook hssfWorkbook = ExcelUtils.getHSSFWorkbook(sheetName, title, values);

        response.setContentType("application/octet-stream;charset=ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        OutputStream os = response.getOutputStream();
        hssfWorkbook.write(os);
        os.flush();
        os.close();
        saveLogInfo("导出", "导出", token);
    }

    private void saveLogInfo(String ability, String message, String token) {
        String realName = TokenUtils.getRealName(token);
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("消防巡检-风险点台账-作业环节");
        log.setAbility(ability);
        log.setMessage(message);
        log.setOperator(realName);
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 赋值员工部门全路径
     * @param list
     */
    private void setOrgNamePath(List<SafetyFireInspectionWorkStepDO> list) {
        if (!CollectionUtils.isEmpty(list)) {
            List<String> deptCode = list.stream().map(SafetyFireInspectionWorkStepDO::getWorkDeptCode).collect(Collectors.toList());
            List<SafetyGroupInfoDO> listByCode = safetyGroupService.getListByCode(deptCode);
            Map<String, SafetyGroupInfoDO> deptMap = listByCode.stream().collect(Collectors.toMap(SafetyGroupInfoDO::getCode, t -> t));
            list.forEach(li -> {
                SafetyGroupInfoDO infoDO = deptMap.get(li.getWorkDeptCode());
                if (infoDO != null) {
                    li.setOrgNamePath(infoDO.getOrgNamePath());
                }
            });
        }
    }
    private String getOrgNamePath(String oldOrgNamePath) {
        if (StringUtils.isBlank(oldOrgNamePath)) {
            return "";
        }
        List<String> orgNamePaths = Arrays.asList(oldOrgNamePath.split(","));
        Collections.reverse(orgNamePaths);
        return String.join("<", orgNamePaths);
    }
}
