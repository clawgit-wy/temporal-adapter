package com.yutong.safety.core.handler;

import com.alibaba.excel.util.StringUtils;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.param.web.SafetyWebBaseParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import com.yutong.safety.core.service.SafetyLogMessageService;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 增删改查 Handler 层封装
 *
 * @param <E> 列表对象，数据库对应的实体类对象，列表返回值
 * @param <T> 以DOExample实体类，操作数据库的信息
 * @param <S> 入参， 查询入参
 * @param <A> 入参， 新增的入参
 */
public abstract class AbstractSafetyWebBaseHandler<E extends SafetyWebBaseEntity, T, S extends SafetyWebBaseParam, A extends Check> extends VoUtil {

    @Resource
    private SafetyBaseHandler safetyBaseHandler;

    public abstract AbstractSafetyWebBaseService<E, T, S> getService();

    /**
     * 模块名称
     */
    public abstract String getModuleName();

    /**
     * 模版名称
     */
    protected abstract String getTemplateName();

    /**
     * 角色变量，权限查询的条件，在子类为它赋值，有值则需要权限查询，没有值则不需要
     */
    protected abstract String getRoleName();

    protected AbstractSafetyWebBaseService<E, T, S> service() {
        AbstractSafetyWebBaseService<E, T, S> service = getService();
        if (service == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "获取service失败！");
        }
        return service;
    }

    protected SafetyLogMessageService logMessageService() {
        SafetyLogMessageService logService = getLogService();
        if (logService == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "获取日志Service失败！");
        }
        return logService;
    }

    /**
     * 日志Service, 吐过用到日志，请重写本方法
     */
    public SafetyLogMessageService getLogService() {
        return null;
    }

    /**
     * 导出的数据结构整合
     *
     * @param dataList 需要导出的数据
     * @return 返回值 left:表头， right:数据行
     */
    public abstract Pair<String[], String[][]> processExportData(List<E> dataList);

    /**
     * 数据分页
     *
     * @param param 参数信息
     * @param token 登录人信息
     * @return 返回查询结果集
     */
    public VO selectPageList(S param, String token) {
        // 数据查询的权限条件，跟据角色判断，如果有要求角色则为空，没有则返回登录人信息
        param.setRoleWorkNo(getWorkNoForRole(token));
        // 前置入参处理
        beforeSelect(param);
        // 数据获取
        Pair<Long, List<E>> pair = pageDataSelect(param);
        List<E> list = pair.getRight();
        // 数据后置处理
        afterSelect(list);
        PageVO<E> pageInfo = new PageVO<>(pair.getLeft(), list);
        return VoUtil.successData(pageInfo);

    }

    /**
     * 数据查询的权限条件，跟据角色判断，如果有要求角色则为空，没有则返回登录人信息
     * 表示有角色可以看到所欲数据，没有的话只能看到自己创建的数据
     * 如果不符合条件可以重写此方法
     *
     * @param token 登录人信息
     * @return 返回工号或者空
     */
    public String getWorkNoForRole(String token) {
        String roleName = getRoleName();
        if (StringUtils.isNotBlank(token) && StringUtils.isNotBlank(roleName)) {
            return safetyBaseHandler.getListPermission(token, roleName);
        }
        return "";
    }

    public VO delete(Integer id, String reason,String token) {
        E data = service().selectByPrimaryKey(id);
        if (data == null) {
            return fail("该记录不存在");
        }
        data.setDelFlag(SafetyWebBaseEntity.DELETE);
        data.setUpdateInfo(token);
        boolean b = service().updateByPrimaryKey(data);
        if (!b) {
            return fail("数据删除失败!");
        }
        afterDelete(data,reason,token);
        return success();
    }

    /**
     * 创建和新增方法，唯一值判断 isExist方法进行判断
     *
     * @param info  新增的数据实体类
     * @param token 员工信息
     * @return 返回值
     */
    public VO saveAndUpdate(A info, String token) {
        E data = newInstance();
        BeanUtils.copyProperties(info, data);
        // 数据唯一性校验
        boolean isUsed = service().isExist(data);
        if (isUsed) {
            return fail("此数据已存在!");
        }

        this.beforeSave(data);
        service().save(data, token);
        this.afterSave(data);
        return success();
    }

    /**
     * 批量删除操作
     *
     * @param ids   数据行ID字符串
     * @param token 人员信息
     * @return 返回值
     */
    public VO batchDelete(String ids,String reason, String token) {
        if (StringUtils.isBlank(ids)) {
            return success();
        }
        String[] idArr = ids.split(",");
        List<String> idList = Arrays.stream(idArr).collect(Collectors.toList());
        E record = newInstance();
        record.setUpdateInfo(token);
        record.setDelFlag(SafetyWebBaseEntity.DELETE);
        if(StringUtils.isNotBlank(reason)){
            record.setRemark(reason); //设置删除原因
        }
        boolean b = service().deleteById(record, idList);
        if (!b) {
            return fail("批量删除失败！");
        }
        this.afterBatchDelete(idList,reason,token);
        return success();
    }

    /**
     * 导出模版下载
     */
    public void download(HttpServletResponse response) {
        String fileName = getModuleName() + "导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/" + getTemplateName() + ".xlsx");
            InputStream inputStream = classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response, inputStream, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void export(HttpServletResponse response, S param, String token) {
        try {
            String fileName = new String((getModuleName() + DateUtils.format(new Date(), 5) + ".xlsx").getBytes(), "ISO8859-1");
            // 数据查询的权限条件，跟据角色判断，如果有要求角色则为空，没有则返回登录人信息
            param.setRoleWorkNo(getWorkNoForRole(token));
            // 前置参数处理
            beforeSelect(param);
            // 数据查询
            List<E> dataList = getExportData(param);
            // 后置数据处理
            afterSelect(dataList);

            // 数据整合
            Pair<String[], String[][]> pair = processExportData(dataList);
            String[] left = pair.getLeft();
            String[][] right = pair.getRight();
            if (left == null || right == null) {
//                return fail("数据导出异常，请检查重写的processExportData方法！");
                throw new BizException(ResponseEnum.FAIL.getCode(), "数据导出异常，请检查重写的processExportData方法！");
            }

            String sheetName = getModuleName();
            HSSFWorkbook hssfWorkbook = ExcelUtils.getHSSFWorkbook(sheetName, left, right);

            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            OutputStream os = response.getOutputStream();
            hssfWorkbook.write(os);
            os.flush();
            os.close();
//            return success();
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
//            return fail();
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出列表异常");
//            return fail("导出" + getModuleName() + "列表异常");
        }
    }

    /**
     * 默认的获取需要导出的数据的方法，如果不符合要求可以重写此方法
     *
     * @param param 入参
     * @return 返回需要的数据
     */
    public List<E> getExportData(S param) {
        List<E> dataList = service().selectList(param, false);
        if (dataList.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), String.format("导出数据不能超过 %s 条！", Constants.FILE_SIZE));
        }
        return dataList;
    }

    /**
     * 数据的前置处理，在查询中后置处理部分数据信息
     *
     * @param param 入参
     */
    public void beforeSelect(S param) {
    }

    /**
     * 分页数据获取，部分页面可能存在特殊的场景，SQL可能需要重写，所以可以进行重写此方法
     *
     * @param param 入参
     * @return left：数据量， right：数据集
     */
    public Pair<Long, List<E>> pageDataSelect(S param) {
        // 总数
        long total = service().selectCount(param);
        List<E> list = service().selectList(param, true);
        return Pair.of(total, list);
    }

    /**
     * 数据的后置处理，在查询中后置处理部分数据信息
     *
     * @param list 数据集
     */
    public void afterSelect(List<E> list) {
    }

    /**
     * 新增后的操作
     *
     * @param data 新增的实体类，修改时没有创建时间等信息
     */
    protected void afterSave(E data) {
    }

    /**
     * 新增前的操作
     *
     * @param data 新增的实体类，修改时没有创建时间等信息
     */
    protected void beforeSave(E data) {
    }

    /**
     * 删除后的操作
     *
     * @param data 数据ID
     */
    protected void afterDelete(E data,String reason, String token) {
    }

    /**
     * 批量删除后的操作
     *
     * @param idList 数据行ID集合
     */
    protected void afterBatchDelete(List<String> idList,String reason, String token) {
    }

    private E newInstance() {
        try {
            ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
            Class<E> clazz = (Class<E>) pt.getActualTypeArguments()[0];
            return clazz.newInstance();
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "反射初始化对象报错！");
        }
    }

    /**
     * 操作日志入库
     * @param ability 功能
     * @param message 操作内容
     * @param token 操作人
     */
    protected void saveLogInfo(String ability, String message, String token) {
        String realName = TokenUtils.getRealName(token);
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage(getModuleName());
        log.setAbility(ability);
        log.setMessage(message);
        log.setOperator(realName);
        logMessageService().addLogMessage(log);
    }

    /**
     * 数据导入 暂时重写此方法
     */
    public VO importData(CommonImportParam param, String token) {
        return success();
    }
}
