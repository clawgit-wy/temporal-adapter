package com.yutong.modules.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.user.entity.SafetySysRole;
import com.yutong.modules.user.query.SafetySysRoleQuery;
import com.yutong.modules.user.vo.SafetySysRoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.aspect.annotation.DataPermission;


import java.util.List;

/**
 * @Description: 系统角色
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
@Mapper
public interface SafetySysRoleMapper extends BaseMapper<SafetySysRole> {

    /**
     * 分页查询角色列表（支持数据权限控制）
     *
     * @param page      分页参数
     * @param query     查询条件
     * @return 分页结果
     */
    @DataPermission(permCode="systemd:roles")
    IPage<SafetySysRoleVO> selectRolePage(IPage<SafetySysRoleVO> page,
                                          @Param("query") SafetySysRoleQuery query);

    /**
     * 查询角色全部列表（支持数据权限控制）
     *
     * @param query     查询条件
     * @return 角色VO列表（含userCount）
     */
    @DataPermission(permCode="systemd:roles")
    List<SafetySysRoleVO> queryAllList(@Param("query") SafetySysRoleQuery query);
}
