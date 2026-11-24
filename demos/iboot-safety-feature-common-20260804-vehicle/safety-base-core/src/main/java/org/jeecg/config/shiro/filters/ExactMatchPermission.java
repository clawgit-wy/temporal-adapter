package org.jeecg.config.shiro.filters;
import org.apache.shiro.authz.Permission;

/**
 * 精确匹配权限类
 * 只有完全相同的权限字符串才能匹配
 */
public class ExactMatchPermission implements Permission {
    private final String permissionString;

    public ExactMatchPermission(String permissionString) {
        this.permissionString = permissionString;
    }

    @Override
    public boolean implies(Permission p) {
        // 精确匹配：只有权限字符串完全相同才返回 true
        if (p instanceof ExactMatchPermission) {
            return this.permissionString.equals(((ExactMatchPermission) p).permissionString);
        }
        return false;
    }
}
