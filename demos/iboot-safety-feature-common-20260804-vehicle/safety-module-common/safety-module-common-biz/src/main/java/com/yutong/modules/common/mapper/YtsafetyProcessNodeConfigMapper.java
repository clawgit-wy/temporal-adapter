package com.yutong.modules.common.mapper;

import java.util.List;

import com.yutong.modules.common.entity.YtsafetyProcessNodeConfig;
import com.yutong.modules.common.vo.SafetyGroupInfoVO;
import com.yutong.modules.common.vo.SysResponsibleVO;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: 流程节点配置
 * @Author: jeecg-boot
 * @Date:   2026-07-23
 * @Version: V1.0
 */
public interface YtsafetyProcessNodeConfigMapper extends BaseMapper<YtsafetyProcessNodeConfig> {

    List<SysResponsibleVO> queryResponsibleByFactory(@Param("factoryId") Long factoryId, @Param("roleCode") String roleCode);
    /**
     * 批量查询安全责任人：对每个orgCode沿组织路径向上查找最近的安全责任人
     */
    List<SysResponsibleVO> batchQueryResponsibleByOrgCodes(@Param("orgCodes") List<String> orgCodes, @Param("roleCode") String roleCode);

    /**
     * 批量查询安全责任人（含厂区过滤）：对每个orgCode沿组织路径向上查找最近的安全责任人
     */
    List<SysResponsibleVO> batchQueryResponsibleByFactoryAndDeparts(@Param("orgCodes") List<String> orgCodes, @Param("factoryId") Long factoryId, @Param("roleCode") String roleCode);
}
