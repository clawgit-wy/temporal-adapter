package com.yutong.modules.auth.aspect;

import com.alibaba.fastjson.JSONObject;
import com.yutong.modules.auth.util.ColumnFilterUtil;
import com.yutong.modules.user.api.IYtsafetySysUserDataRuleService;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jeecg.common.api.vo.ColumnPermVO;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.ColumnPermission;
import org.jeecg.common.system.api.ISysAuthBaseAPI;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Aspect
@Component
public class ColumnPermissionAspect {
    @Autowired
    private IYtsafetySysUserDataRuleService ytsafetySysUserDataRuleService;

    @Lazy
    @Resource
    private ISysAuthBaseAPI isysAuthBaseAPI;

    // 拦截带 @ColumnPermission 的接口
    @Around("@annotation(columnPermission)")
    public Object around(ProceedingJoinPoint point, ColumnPermission columnPermission) throws Throwable {
        // 1. 执行原方法
        Object result = point.proceed();
        // 2. 获取列权限
        Result<JSONObject> jsonObjectResultresult = isysAuthBaseAPI.getUserInfo();
        String workNo = "";
        if (Objects.nonNull(jsonObjectResultresult) && Objects.nonNull(jsonObjectResultresult.getResult())) {
            JSONObject userInfo = jsonObjectResultresult.getResult().getJSONObject("userInfo");
            if (Objects.nonNull(userInfo)) {
                workNo = userInfo.getString("workNo");
            }
        }
        String  permCode = columnPermission.permCode();
        List<ColumnPermVO> perms = ytsafetySysUserDataRuleService.getColumnPermissions(workNo,permCode);
        if (!CollectionUtils.isEmpty(perms)){
            // 3. 过滤隐藏字段
            if (result instanceof Result<?>) {
                Result<?> r = (Result<?>) result;
                r.setColumnPerms(null);
                ColumnFilterUtil.filter(r.getResult(), perms);
                r.setColumnPerms(perms);
            } else if (result instanceof List<?>) {
                ColumnFilterUtil.filter((List<?>) result, perms);
            } else if (result instanceof ModelAndView) {
                ModelAndView mv = (ModelAndView) result;
                Object dataList = mv.getModelMap().get(NormalExcelConstants.DATA_LIST);
                if (dataList instanceof List<?>) {
                    ColumnFilterUtil.filter((List<?>) dataList, perms);
                }
                List<String> fields = getAnnotatedFields(dataList,perms);
                if(!fields.isEmpty()) {
                    mv.getModelMap().put(NormalExcelConstants.EXPORT_FIELDS, String.join(",",fields) );
                }
            }
        }
        return result;
    }

    public List<String> getAnnotatedFields(Object obj,List<ColumnPermVO> perms) {
        List<String> results = new ArrayList<>();
        Class<?> clazz = null;
        if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            clazz = list.get(0).getClass();
        }
        if (clazz == null) {
            return results;
        }
        List<String> hideCols = perms.stream().map(ColumnPermVO::getColumnKey).collect(Collectors.toList());
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Excel.class)) {
                // 获取注解实例
                if(!hideCols.contains(field.getName())) {
                    results.add(field.getName());
                }
            }
        }
        return results;
    }
}
