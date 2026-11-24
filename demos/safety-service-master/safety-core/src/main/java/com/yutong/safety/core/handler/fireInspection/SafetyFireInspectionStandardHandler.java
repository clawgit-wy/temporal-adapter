package com.yutong.safety.core.handler.fireInspection;

import cn.hutool.poi.excel.ExcelReader;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.FireInspectionEnum;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionStandardDO;
import com.yutong.safety.core.db.entity.fireInspection.SafetyFireInspectionWorkStepDO;
import com.yutong.safety.core.db.entity.fireInspection.excel.SafetyFireInspectionStandardExcel;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionStandardAddParams;
import com.yutong.safety.core.db.entity.fireInspection.param.SafetyFireInspectionStandardQueryParams;
import com.yutong.safety.core.dto.fireInspection.FireInspectionMethodDto;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.service.SafetyLogMessageService;
import com.yutong.safety.core.service.fireInspection.SafetyFireInspectionStandardService;
import com.yutong.safety.core.service.fireInspection.SafetyFireInspectionWorkStepService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

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
public class SafetyFireInspectionStandardHandler {
    @Resource
    private SafetyLogMessageService safetyLogMessageService;
    @Resource
    private SafetyBaseHandler safetyBaseHandler;
    @Resource
    private SafetyFireInspectionWorkStepService safetyFireInspectionWorkStepService;
    @Resource
    private SafetyFireInspectionStandardService safetyFireInspectionStandardService;
    @Resource
    private SafetyFireInspectionSelectHandler safetyFireInspectionSelectHandler;

    /**
     * 分页数据获取
     *
     * @param param 入参
     * @param token 人员信息
     * @return 返回符合条件的数据
     */
    public PageVO<SafetyFireInspectionStandardDO> selectPageList(SafetyFireInspectionStandardQueryParams param, String token) {
        //获取登录用户的权限
        String workNo = safetyBaseHandler.getListPermission(token, FIRE_INSPECTION_ROLE);
        // 获取数据
        long total = safetyFireInspectionStandardService.selectCount(param, workNo);
        List<SafetyFireInspectionStandardDO> list = safetyFireInspectionStandardService.selectList(param, workNo, true);
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(li -> li.setRiskLevel(FireInspectionEnum.getFireInspectionEnumByCode(li.getRiskLevel()).getRiskLevel()));
        }
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
            SafetyFireInspectionStandardDO data = safetyFireInspectionStandardService.selectByPrimaryKey(id);
            if (data == null) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "该记录不存在");
            }
            data.setDelFlag(SafetyFireInspectionStandardDO.DELETE);
            data.setUpdateInfo(token);
            boolean b = safetyFireInspectionStandardService.updateByPrimaryKey(data);
            if (!b) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "数据删除失败！");
            }
            saveLogInfo("删除", "数据：" + data.toString(), token);
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
        boolean b = safetyFireInspectionStandardService.deleteById(idList, token);
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
    public boolean update(SafetyFireInspectionStandardAddParams param, String token) {
        try {
            if (param.getId() == null) throw new BizException(ResponseEnum.FAIL.getCode(), "数据修改失败，数据ID为空！");
            SafetyFireInspectionStandardDO data = safetyFireInspectionStandardService.selectByPrimaryKey(param.getId());
            if (data == null) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "该记录不存在");
            }
            // 重复性校验
            boolean b = safetyFireInspectionStandardService.selectExit(param);
            if (b) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "此数据行信息已存在！");
            }
            String message = data.toString();
            BeanUtils.copyProperties(param, data);
            data.setUpdateInfo(token);
            // 修改
            b = safetyFireInspectionStandardService.updateByPrimaryKey(data);
            if (!b) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "数据修改失败！");
            }
            saveLogInfo("修改", "原数据：" + message, token);
            return true;
        } catch (BizException e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "修改异常，原因："+ e.getMsg());
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "修改异常，原因："+ e.getMessage());
        }
    }

    /**
     * 新增
     *
     * @param param 入参对象
     * @param token 用户信息
     */
    public boolean add(SafetyFireInspectionStandardAddParams param, String token) {
        try {
            // 获取检查方法
            boolean b = safetyFireInspectionStandardService.selectExit(param);
            if (b) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "此数据行信息已存在！");
            }
            // 数据新增
            SafetyFireInspectionStandardDO data = new SafetyFireInspectionStandardDO();
            BeanUtils.copyProperties(param, data);
            data.setCreates(token);
            boolean insert = safetyFireInspectionStandardService.insert(data);
            if (!insert) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "数据新增失败！");
            }
            saveLogInfo("新增", "新数据：" + data.toString(), token);
            return true;
        } catch (BizException e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "修改异常，原因："+ e.getMsg());
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "新增异常，原因："+ e.getMessage());
        }
    }

    /**
     * 模版文件下载
     */
    public void download(HttpServletResponse response) {
        String fileName = "消防巡检-风险点台账-检查标准导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/inspectionStandardDownload.xlsx");
            InputStream inputStream = classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response, inputStream, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据文件导入
     *
     * @param param 文件对象
     * @param token 用户信息
     */
    public Object importData(CommonImportParam param, String token) {
        // 文件校验
        ExcelReader excelReader = ExcelUtils.checkFile(param.getFile());

        excelReader.addHeaderAlias("作业环节编码", "workStepCode");
        excelReader.addHeaderAlias("检查项目", "inspectionItem");
        excelReader.addHeaderAlias("检查对象", "inspectionObject");
        excelReader.addHeaderAlias("检查类别", "inspectionCategory");
        excelReader.addHeaderAlias("检查细分维度", "inspectionSubDimension");
        excelReader.addHeaderAlias("检查内容", "inspectionContent");
        excelReader.addHeaderAlias("检查方法", "inspectionMethod");
        excelReader.addHeaderAlias("等级", "riskLevel");

        List<SafetyFireInspectionStandardExcel> dataList = excelReader.read(5, 6, SafetyFireInspectionStandardExcel.class);
        if (CollectionUtils.isEmpty(dataList)) return null;

        // 编号赋值
        IntStream.range(0, dataList.size()).forEach(i -> {
            SafetyFireInspectionStandardExcel data = dataList.get(i);
            data.setNum(i + 7);
        });

        // 数据为空
        String failInfo = dataList.stream().map(SafetyFireInspectionStandardExcel::check).filter(ln -> StringUtils.isNotBlank(ln.getFailInfo())).map(ll -> String.format("第%s行错误：%s", ll.getNum(), ll.getFailInfo())).collect(Collectors.joining(" "));
        if (StringUtils.isNotBlank(failInfo)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), failInfo);
        }

        // 获取作业环节编码信息
        List<String> workStepList = dataList.stream().map(SafetyFireInspectionStandardExcel::getWorkStepCode).distinct().collect(Collectors.toList());
        List<SafetyFireInspectionWorkStepDO> selectList = safetyFireInspectionWorkStepService.selectList(workStepList);
        if (CollectionUtils.isEmpty(selectList)) throw new BizException(ResponseEnum.FAIL.getCode(), "作业环节编码全部匹配失败");
        Map<String, String> dataMap = selectList.stream().collect(Collectors.toMap(SafetyFireInspectionWorkStepDO::getWorkStepCode, SafetyFireInspectionWorkStepDO::getWorkStepName));

        // 获取检查标准信息 数据去重
        List<SafetyFireInspectionStandardDO> standardDOS = safetyFireInspectionStandardService.selectList(workStepList);
        if (!CollectionUtils.isEmpty(standardDOS)) {
            // 使用Set实现快速查找
            Set<String> standardContentSet = new HashSet<>(standardDOS.size());
            for (SafetyFireInspectionStandardDO standard : standardDOS) {
                String contentKey = generateKey(standard);
                standardContentSet.add(contentKey);
            }

            List<Integer> repeatList = new ArrayList<>();
            for (SafetyFireInspectionStandardExcel data : dataList) {
                String contentKey = data.generateKey();
                if (standardContentSet.contains(contentKey)) {
                    repeatList.add(data.getNum());
                }
            }

            if (!repeatList.isEmpty()) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "数据重复，以下行数据重复：" + repeatList);
            }
        }

        // 检查方法
        List<FireInspectionMethodDto> inspectionMethod = safetyFireInspectionSelectHandler.getInspectionMethod();
        Map<String, Integer> methodMap = inspectionMethod.stream().collect(Collectors.toMap(FireInspectionMethodDto::getName, FireInspectionMethodDto::getCode));

        // 作业环节编码信息和检查方法校验
        StringBuffer sb = new StringBuffer();
        List<SafetyFireInspectionStandardDO> lastList = new ArrayList<>();
        dataList.forEach(li -> {
            String workStepCode = li.getWorkStepCode();
            String s = dataMap.get(workStepCode);
            Integer methodId = methodMap.get(li.getInspectionMethod());
            String msg = "";
            if (StringUtils.isBlank(s)) {
                msg = "作业环节编码错误，根据编码获取对应名称失败，作业环节编码：" + workStepCode + ";";
                sb.append("第").append(li.getNum()).append("行，作业环节编码错误，根据编码获取对应名称失败，作业环节编码：").append(workStepCode).append(";\r\n");
            } else if (methodId == null) {
                msg = "检查方法错误，检查方法不存在，请检查或者在安保平台维护：" + li.getInspectionMethod() + ";";
            } else {
                SafetyFireInspectionStandardDO info = new SafetyFireInspectionStandardDO();
                BeanUtils.copyProperties(li, info);
                info.setInspectionMethodId(Long.valueOf(methodId));
                info.setCreates(token);
                info.setWorkStepName(s);
                lastList.add(info);
            }
            if (StringUtils.isNotBlank(msg)) {
                sb.append("第").append(li.getNum()).append("行,").append(msg);
            }
        });

        // 部门信息校验失败报错
        if (StringUtils.isNotBlank(sb.toString())) {
            throw new BizException(ResponseEnum.FAIL.getCode(), sb.toString());
        }

        // 新增
        if (!CollectionUtils.isEmpty(lastList)) {
            safetyFireInspectionStandardService.batchSave(lastList);
        }
        saveLogInfo("导入", "文件导入", token);
        return null;
    }

    /**
     * 数据导出
     *
     * @param response 响应对象
     * @param param    数据入参
     * @param token    用户信息
     * @throws IOException IO异常
     */
    public void export(HttpServletResponse response, SafetyFireInspectionStandardQueryParams param, String token) throws IOException {
        String fileName = new String(("检查标准-" + DateUtils.format(new Date(), 5) + ".xlsx").getBytes(), "ISO8859-1");
        //获取登录用户的权限
        String workNo = safetyBaseHandler.getListPermission(token, FIRE_INSPECTION_ROLE);

        List<SafetyFireInspectionStandardDO> list = safetyFireInspectionStandardService.selectList(param, workNo, false);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), String.format("导出数据不能超过 %s 条！", Constants.FILE_SIZE));
        }
        String[] title = {"检查编码", "作业环节名称", "检查项目", "检查对象", "检查类别", "检查细分维度", "检查内容", "风险等级", "检查方法", "最近更新时间", "最近提交人"};
        String[][] values = new String[list.size()][title.length];
        for (int i = 0; i < list.size(); i++) {
            SafetyFireInspectionStandardDO info = list.get(i);
            int j = 0;
            values[i][j++] = String.valueOf(info.getId());
            values[i][j++] = info.getWorkStepName();
            values[i][j++] = info.getInspectionItem();
            values[i][j++] = info.getInspectionObject();
            values[i][j++] = info.getInspectionCategory();
            values[i][j++] = info.getInspectionSubDimension();
            values[i][j++] = info.getInspectionContent();
            values[i][j++] = info.getRiskLevel();
            values[i][j++] = info.getInspectionMethod();
            values[i][j++] = DateUtils.format(info.getUpdateTime(), 2);
            values[i][j++] = info.getUpdateBy();
        }

        String sheetName = "检查标准";
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
        log.setOperatePage("消防巡检-风险点台账-检查标准");
        log.setAbility(ability);
        log.setMessage(message);
        log.setOperator(realName);
        safetyLogMessageService.addLogMessage(log);
    }

    private String generateKey(SafetyFireInspectionStandardDO standard) {
        return standard.getWorkStepCode() +
                standard.getInspectionItem() +
                standard.getInspectionObject() +
                standard.getInspectionCategory() +
                standard.getInspectionSubDimension() +
                standard.getInspectionContent() +
                standard.getRiskLevel() +
                standard.getInspectionMethod();
    }

    /**
     * 初始化接口
     * @param file 文件
     */
    public Object importData(MultipartFile file) {
        // 文件校验
        ExcelReader excelReader = ExcelUtils.checkFile(file);

        excelReader.addHeaderAlias("作业环节编码", "workStepCode");
        excelReader.addHeaderAlias("检查项目", "inspectionItem");
        excelReader.addHeaderAlias("检查对象", "inspectionObject");
        excelReader.addHeaderAlias("检查类别", "inspectionCategory");
        excelReader.addHeaderAlias("检查细分维度", "inspectionSubDimension");
        excelReader.addHeaderAlias("检查内容", "inspectionContent");
        excelReader.addHeaderAlias("检查方法", "inspectionMethod");
        excelReader.addHeaderAlias("等级", "riskLevel");

        List<SafetyFireInspectionStandardExcel> dataList = excelReader.read(5, 6, SafetyFireInspectionStandardExcel.class);
        if (CollectionUtils.isEmpty(dataList)) return null;

        // 编号赋值
        IntStream.range(0, dataList.size()).forEach(i -> {
            SafetyFireInspectionStandardExcel data = dataList.get(i);
            data.setNum(i + 7);
        });

        // 数据为空
        String failInfo = dataList.stream().map(SafetyFireInspectionStandardExcel::check).filter(ln -> StringUtils.isNotBlank(ln.getFailInfo())).map(ll -> String.format("第%s行错误：%s", ll.getNum(), ll.getFailInfo())).collect(Collectors.joining(" "));
        if (StringUtils.isNotBlank(failInfo)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), failInfo);
        }

        // 获取作业环节编码信息
        List<String> workStepList = dataList.stream().map(SafetyFireInspectionStandardExcel::getWorkStepCode).distinct().collect(Collectors.toList());
        List<SafetyFireInspectionWorkStepDO> selectList = safetyFireInspectionWorkStepService.selectList(workStepList);
        if (CollectionUtils.isEmpty(selectList)) throw new BizException(ResponseEnum.FAIL.getCode(), "作业环节编码全部匹配失败");
        Map<String, String> dataMap = selectList.stream().collect(Collectors.toMap(SafetyFireInspectionWorkStepDO::getWorkStepCode, SafetyFireInspectionWorkStepDO::getWorkStepName));

        // 获取检查标准信息 数据去重
        List<SafetyFireInspectionStandardDO> standardDOS = safetyFireInspectionStandardService.selectList(workStepList);
        if (!CollectionUtils.isEmpty(standardDOS)) {
            // 使用Set实现快速查找
            Set<String> standardContentSet = new HashSet<>(standardDOS.size());
            for (SafetyFireInspectionStandardDO standard : standardDOS) {
                String contentKey = generateKey(standard);
                standardContentSet.add(contentKey);
            }

            List<Integer> repeatList = new ArrayList<>();
            for (SafetyFireInspectionStandardExcel data : dataList) {
                String contentKey = data.generateKey();
                if (standardContentSet.contains(contentKey)) {
                    repeatList.add(data.getNum());
                }
            }

            if (!repeatList.isEmpty()) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "数据重复，以下行数据重复：" + repeatList);
            }
        }

        // 检查方法
        List<FireInspectionMethodDto> inspectionMethod = safetyFireInspectionSelectHandler.getInspectionMethod();
        Map<String, Integer> methodMap = inspectionMethod.stream().collect(Collectors.toMap(FireInspectionMethodDto::getName, FireInspectionMethodDto::getCode));

        // 作业环节编码信息和检查方法校验
        StringBuffer sb = new StringBuffer();
        List<SafetyFireInspectionStandardDO> lastList = new ArrayList<>();
        dataList.forEach(li -> {
            String workStepCode = li.getWorkStepCode();
            String s = dataMap.get(workStepCode);
            Integer methodId = methodMap.get(li.getInspectionMethod());
            String msg = "";
            if (StringUtils.isBlank(s)) {
                msg = "作业环节编码错误，根据编码获取对应名称失败，作业环节编码：" + workStepCode + ";";
                sb.append("第").append(li.getNum()).append("行，作业环节编码错误，根据编码获取对应名称失败，作业环节编码：").append(workStepCode).append(";\r\n");
            } else if (methodId == null) {
                msg = "检查方法错误，检查方法不存在，请检查或者在安保平台维护：" + li.getInspectionMethod() + ";";
            } else {
                SafetyFireInspectionStandardDO info = new SafetyFireInspectionStandardDO();
                BeanUtils.copyProperties(li, info);
                info.setInspectionMethodId(Long.valueOf(methodId));
                info.setCreates();
                info.setWorkStepName(s);
                lastList.add(info);
            }
            if (StringUtils.isNotBlank(msg)) {
                sb.append("第").append(li.getNum()).append("行,").append(msg);
            }
        });

        // 部门信息校验失败报错
        if (StringUtils.isNotBlank(sb.toString())) {
            throw new BizException(ResponseEnum.FAIL.getCode(), sb.toString());
        }

        // 新增
        if (!CollectionUtils.isEmpty(lastList)) {
            safetyFireInspectionStandardService.batchSave(lastList);
        }

        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("消防巡检-风险点台账-检查标准");
        log.setAbility("导入");
        log.setMessage("数据初始化导入");
        log.setOperator("司永奇");
        safetyLogMessageService.addLogMessage(log);
        return null;
    }
}