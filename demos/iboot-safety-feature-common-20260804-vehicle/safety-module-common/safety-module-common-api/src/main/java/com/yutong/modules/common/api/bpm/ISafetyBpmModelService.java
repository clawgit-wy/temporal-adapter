package com.yutong.modules.common.api.bpm;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.common.dto.SafetyBpmModelDTO;
import com.yutong.modules.common.dto.SafetyBpmModelQueryDTO;
import com.yutong.modules.common.entity.SafetyBpmModel;
import com.yutong.modules.common.vo.SafetyBpmModelVO;

import java.util.List;

/**
 * @Description: 对接BPM系统模块表
 * @Author: jeecg-boot
 * @Date:   2026-06-18
 * @Version: V1.0
 */
public interface ISafetyBpmModelService extends IService<SafetyBpmModel> {

    /**
     * 添加
     * @param safetyBpmModelDTO
     * @return
     */
    public SafetyBpmModel addSafetyBpmModel(SafetyBpmModelDTO safetyBpmModelDTO);

    /**
     * 删除
     * @param id
     * @return
     */
    public SafetyBpmModel deleteSafetyBpmModel(Long id);

    /**
     * 编辑
     * @param safetyBpmModelDTO
     * @return
     */
    public SafetyBpmModel editSafetyBpmModel(SafetyBpmModelDTO safetyBpmModelDTO);

    /**
     * 依据ID查询
     * @param id
     * @return
     */
    public SafetyBpmModelVO queryById(Long id);

    /**
     * 分页查询
     * @param page
     * @param safetyBpmModelQueryDTO
     * @return
     */
    public IPage<SafetyBpmModelVO> queryPageList(Page<SafetyBpmModelVO> page, SafetyBpmModelQueryDTO safetyBpmModelQueryDTO);

    /**
     * 列表查询
     * @param safetyBpmModelQueryDTO
     * @return
     */
    public List<SafetyBpmModelVO> queryList(SafetyBpmModelQueryDTO safetyBpmModelQueryDTO);


}
