package com.yutong.modules.user.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.user.dto.YTSafetySysJobRoleMappingDTO;
import com.yutong.modules.user.dto.YTSafetySysJobRoleMappingImportDTO;
import com.yutong.modules.user.entity.YTSafetySysJobRoleMapping;
import com.yutong.modules.user.vo.YTSafetySysJobRoleMappingVO;

import javax.validation.Valid;
import java.util.List;

/**
 * @Description: 岗位角色映射表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
public interface IYTSafetySysJobRoleMappingService extends IService<YTSafetySysJobRoleMapping> {

    /**
     * 分页列表查询
     * @param pageNo
     * @param pageSize
     * @param jobName
     * @param roleName
     * @return
     */
    IPage<YTSafetySysJobRoleMappingVO> queryPageList(Integer pageNo, Integer pageSize, String jobName, String roleName);

    /**
     * 新增
     * @param ytSafetySysJobRoleMappingDTO
     * @return
     */
    boolean add(@Valid YTSafetySysJobRoleMappingDTO ytSafetySysJobRoleMappingDTO);

    /**
     * 修改
     * @param ytSafetySysJobRoleMappingDTO
     * @return
     */
    boolean update(@Valid YTSafetySysJobRoleMappingDTO ytSafetySysJobRoleMappingDTO);

    /**
     * 删除
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 查询详情
     * @param id
     * @return
     */
    YTSafetySysJobRoleMappingVO getDetails(Long id);

    /**
     * 条件查询全部列表
     * @param jobName
     * @param roleName
     * @return
     */
    List<YTSafetySysJobRoleMappingVO> queryAllList(String jobName, String roleName);

    /**
     * 导入数据处理
     * @param orgFactoryImportList
     * @return
     */
    List<YTSafetySysJobRoleMappingImportDTO> importSysData(List<YTSafetySysJobRoleMappingImportDTO> orgFactoryImportList);
}
