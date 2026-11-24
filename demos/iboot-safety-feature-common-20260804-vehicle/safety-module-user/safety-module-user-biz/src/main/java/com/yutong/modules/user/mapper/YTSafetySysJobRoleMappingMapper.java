package com.yutong.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.user.entity.YTSafetySysJobRoleMapping;
import com.yutong.modules.user.vo.YTSafetySysJobRoleMappingVO;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.aspect.annotation.DataPermission;


/**
 * @Description: 岗位角色映射表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
public interface YTSafetySysJobRoleMappingMapper extends BaseMapper<YTSafetySysJobRoleMapping> {

    @DataPermission(permCode="systemd:jobrole")
    IPage<YTSafetySysJobRoleMappingVO> selectPageList(Page<YTSafetySysJobRoleMappingVO> page,
                                                      @Param("jobName") String jobName,
                                                      @Param("roleName") String roleName);
}
