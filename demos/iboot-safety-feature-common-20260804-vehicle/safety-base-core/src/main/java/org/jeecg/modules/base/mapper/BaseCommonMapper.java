package org.jeecg.modules.base.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.jeecg.common.api.dto.LogDTO;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: BaseCommonMapper
 * @author: jeecg-boot
 */
public interface BaseCommonMapper {

    /**
     * 保存日志
     *
     * @param dto
     */
    @InterceptorIgnore(illegalSql = "true", tenantLine = "true")
    void saveLog(@Param("dto") LogDTO dto);

    /**
     * 获取用户权限
     *
     * @param orgCategory String
     * @param userId String
     * @param orgId String
     * @return java.lang.String
     * @author kougy
     * @date 2023/3/21 17:15
     */
    String getUserDataPermission(String orgCategory,String userId, String orgId);
}
