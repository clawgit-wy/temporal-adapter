package com.yutong.modules.common.api.bpm;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.common.dto.SafetyBpmCallBackDTO;
import com.yutong.modules.common.dto.SafetyBpmCallBackQueryDTO;
import com.yutong.modules.common.entity.SafetyBpmCallBack;
import com.yutong.modules.common.vo.SafetyBpmCallBackVO;

import java.util.List;

/**
 * @Description: 对接BPM回调方法表
 * @Author: jeecg-boot
 * @Date:   2026-06-18
 * @Version: V1.0
 */
public interface ISafetyBpmCallBackService extends IService<SafetyBpmCallBack> {

    /**
     * 添加
     * @param safetyBpmCallBackDTO
     * @return
     */
    public SafetyBpmCallBack addSafetyBpmCall(SafetyBpmCallBackDTO safetyBpmCallBackDTO);

    /**
     * 删除
     * @param id
     * @return
     */
    public SafetyBpmCallBack deleteSafetyBpmCall(Long id);

    /**
     * 编辑
     * @param safetyBpmCallBackDTO
     * @return
     */
    public SafetyBpmCallBack editSafetyBpmCall(SafetyBpmCallBackDTO safetyBpmCallBackDTO);

    /**
     * 依据ID查询
     * @param id
     * @return
     */
    public SafetyBpmCallBackVO queryById(Long id);

    /**
     * 分页查询
     * @param page
     * @param safetyBpmModelQueryDTO
     * @return
     */
    public IPage<SafetyBpmCallBackVO> queryPageList(Page<SafetyBpmCallBackVO> page, SafetyBpmCallBackQueryDTO safetyBpmModelQueryDTO);

    /**
     * 分页查询
     * @param safetyBpmModelQueryDTO
     * @return
     */
    public List<SafetyBpmCallBackVO> queryList(SafetyBpmCallBackQueryDTO safetyBpmModelQueryDTO);


}
