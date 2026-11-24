package org.jeecg.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 业务相关帮助类
 */
public class BusUtits {

    /**
     * 获取员工号
     * @return
     */
    public static String getUserEmpNo(Result<JSONObject> result){
        String workNo = "";
        if (Objects.nonNull(result) && Objects.nonNull(result.getResult())) {
            JSONObject userInfo = result.getResult().getJSONObject("userInfo");
            if (Objects.nonNull(userInfo)) {
                workNo = userInfo.getString("workNo");
            }
        }
        return workNo;
    }

    /**
     * 获取用户信息
     * @return
     */
    public static LoginUser getLoginUser() {
        LoginUser sysUser = null;
        try {
            sysUser = SecurityUtils.getSubject().getPrincipal() != null ? (LoginUser) SecurityUtils.getSubject().getPrincipal() : null;
        } catch (Exception e) {
            //e.printStackTrace();
            sysUser = null;
        }
        return sysUser;
    }

    /**
     * 逗号分隔字符串倒序重组
     * @param str 原字符串，例：苹果,香蕉,橙子
     * @return 倒序字符串 橙子,香蕉,苹果
     */
    public static String reverseCommaStr(String str) {
        // 空值校验
        if (!StringUtils.hasText(str)) {
            return str;
        }
        // 按逗号拆分数组
        String[] arr = str.split(",");
        // 逆序遍历拼接
        StringBuilder sb = new StringBuilder();
        for (int i = arr.length - 1; i >= 0; i--) {
            sb.append(arr[i]);
            if (i != 0) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * 流程提交前等待500ms，确保前置数据已刷新到从库
     */
    public static void sleepBeforeSubmit() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
