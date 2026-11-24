package org.jeecg.config.shiro.filters;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
/**
 * 自定义权限解析器 - 实现精确匹配
 */
public class CustomPermissionResolver implements PermissionResolver {
    @Override
    public Permission resolvePermission(String permissionString) {
        // 返回自定义的精确匹配权限对象
        return new ExactMatchPermission(permissionString);
    }
}
