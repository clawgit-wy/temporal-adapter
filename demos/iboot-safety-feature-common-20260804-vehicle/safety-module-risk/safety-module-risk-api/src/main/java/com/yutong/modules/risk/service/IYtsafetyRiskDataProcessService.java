package com.yutong.modules.risk.service;

import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.dto.YtsafetyRiskDataDTO;
import com.yutong.modules.risk.entity.YtsafetyRiskDataProcess;
import com.yutong.modules.risk.vo.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 风险数据流程信息表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
public interface IYtsafetyRiskDataProcessService extends IService<YtsafetyRiskDataProcess> {

    /**
     * 添加风险数据流程
     * @param dto
     * @return
     */
    YtsafetyRiskDataProcess add(YtsafetyRiskDataDTO dto);

    /**
     * 添加风险数据流程
     * @param dto
     * @return
     */
    YtsafetyRiskDataProcess addJob(YtsafetyRiskDataDTO dto);

    /**
     * 修改风险数据流程
     * @param dto
     * @return
     */
    YtsafetyRiskDataProcess edit(YtsafetyRiskDataDTO dto);

    /**
     * 通过主键查询流程数据
     * @param id
     * @return
     */
    YtsafetyRiskDataProcessVO queryById(Long id);

    /**
     * 批量推送风险数据（按厂区和风险类型分组，每组生成一个申请流程）
     * @param dto
     * @return
     */
    List<YtsafetyRiskDataProcessVO> batchPush(YtsafetyRiskDataDTO dto);

    /**
     * 批量导入风险数据（Excel解析后的数据转换与补充）
     * @param dto 导入请求参数
     * @param excelData Excel解析后的原始数据
     * @return 补全主键后的风险数据明细列表
     */
    List<YtsafetyRiskDataDetailsVO> importData(YtsafetyRiskDataDTO dto, List<YtsafetyRiskDataDetailsImportVO> excelData);


    /**
     * 审批流程
     * @param processParamDTO
     * @return
     */
    public BpmReponseParamsVO executeProcess(BpmProcessParamDTO processParamDTO);

    /**
     * 查询风险类型字典树（仅返回叶子节点，并向上填充父级层级）
     * @return 风险类型字典VO列表
     */
    List<YtSafetyRiskTypeDictVO> queryRiskTypeDict();
}
