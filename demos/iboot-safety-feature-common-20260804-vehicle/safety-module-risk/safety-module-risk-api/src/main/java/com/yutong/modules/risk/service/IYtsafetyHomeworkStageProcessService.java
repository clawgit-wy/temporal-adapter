package com.yutong.modules.risk.service;

import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.dto.YtsafetyHomeworkStageProcessDTO;
import com.yutong.modules.risk.entity.YtsafetyHomeworkStage;
import com.yutong.modules.risk.entity.YtsafetyHomeworkStageProcess;
import com.yutong.modules.risk.vo.YtsafetyHomeworkStageImportVO;
import com.yutong.modules.risk.vo.YtsafetyHomeworkStageProcessVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.risk.vo.YtsafetyHomeworkStageVO;

import java.util.List;

/**
 * @Description: 作业环节流程信息表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
public interface IYtsafetyHomeworkStageProcessService extends IService<YtsafetyHomeworkStageProcess> {

    /**
     * 添加作业环节流程
     * @param dto
     * @return
     */
    YtsafetyHomeworkStageProcess add(YtsafetyHomeworkStageProcessDTO dto);

    YtsafetyHomeworkStageProcess edit(YtsafetyHomeworkStageProcessDTO dto);

    /**
     * 查询流程详情
     * @param id
     * @return
     */
    YtsafetyHomeworkStageProcessVO queryById(Long id);

    /**
     * 根据流程id查询流程详情
     * @param id
     * @return
     */
    YtsafetyHomeworkStageProcessVO queryByProcessId(Long id);

    /**
     * 批量推送作业环节（按厂区分组，每组生成一个申请流程）
     * @param dto
     * @return
     */
    List<YtsafetyHomeworkStageProcessVO> batchPush(YtsafetyHomeworkStageProcessDTO dto);

    /**
     * 批量导入作业环节（Excel解析后的数据转换与补充）
     * @param dto 导入请求参数
     * @param excelData Excel解析后的原始数据
     * @return 补全主键后的作业环节列表
     */
    List<YtsafetyHomeworkStageVO> importData(YtsafetyHomeworkStageProcessDTO dto, List<YtsafetyHomeworkStageImportVO> excelData);
    /**
     * 流程操作
     * @param processParamDTO
     * @return
     */
    BpmReponseParamsVO executeProcess(BpmProcessParamDTO processParamDTO);
}
