package com.yutong.modules.user.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.user.dto.YTSafetySysOrgFactoryDTO;
import com.yutong.modules.user.dto.YTSafetySysOrgFactoryImportDTO;
import com.yutong.modules.user.entity.YTSafetySysOrgFactory;
import com.yutong.modules.user.vo.YTSafetySysOrgFactoryVO;

import javax.validation.Valid;
import java.util.List;

/**
 * @Description: 组织架构映射表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
public interface IYTSafetySysOrgFactoryService extends IService<YTSafetySysOrgFactory> {

    /**
     * 分页列表查询
     * @param pageNo
     * @param pageSize
     * @param orgName
     * @param factoryName
     * @return
     */
    IPage<YTSafetySysOrgFactoryVO> queryPageList(Integer pageNo, Integer pageSize, String orgName, String factoryName);

    /**
     * 添加
     * @param dto
     * @return
     */
    boolean add(@Valid YTSafetySysOrgFactoryDTO dto);

    /**
     * 编辑
     * @param dto
     * @return
     */
    boolean update(@Valid YTSafetySysOrgFactoryDTO dto);

    /**
     * 删除
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 详情
     * @param id
     * @return
     */
    YTSafetySysOrgFactoryVO getDetails(Long id);

    /**
     * 查询全部列表
     * @param orgName
     * @param factoryName
     * @return
     */
    List<YTSafetySysOrgFactoryVO> queryAllList(String orgName, String factoryName);

    /**
     * 导入数据处理
     * @param orgFactoryImportList
     * @return
     */
    List<YTSafetySysOrgFactoryImportDTO> importSysData(List<YTSafetySysOrgFactoryImportDTO> orgFactoryImportList);
}
