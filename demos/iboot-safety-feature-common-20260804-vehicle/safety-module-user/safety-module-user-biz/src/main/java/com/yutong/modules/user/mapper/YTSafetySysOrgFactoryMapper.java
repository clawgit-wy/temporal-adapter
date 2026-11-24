package com.yutong.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.user.entity.YTSafetySysOrgFactory;
import com.yutong.modules.user.vo.YTSafetySysOrgFactoryVO;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.aspect.annotation.DataPermission;

/**
 * @Description: 组织架构映射表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
public interface YTSafetySysOrgFactoryMapper extends BaseMapper<YTSafetySysOrgFactory> {

    @DataPermission(permCode="systemd:orgstructure")
    Page<YTSafetySysOrgFactoryVO> selectPageList(Page<YTSafetySysOrgFactory> page,
                                                 @Param(value = "orgName") String orgName,
                                                 @Param(value = "factoryName") String factoryName);
}
