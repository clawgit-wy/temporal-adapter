package com.yutong.modules.risk.service;

import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.risk.dto.YtsafetyLocationInfoDTO;
import com.yutong.modules.risk.entity.YtsafetyLocationInfoProcess;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoImportVO;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoProcessDetailsVO;
import com.yutong.modules.risk.vo.YtsafetyLocationInfoProcessVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 位置信息流程信息表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
public interface IYtsafetyLocationInfoProcessService extends IService<YtsafetyLocationInfoProcess> {

    /**
     * 添加位置信息流程
     * @param dto
     * @return
     */
    YtsafetyLocationInfoProcess add(YtsafetyLocationInfoDTO dto);

    /**
     * 编辑位置信息流程
     * @param dto
     */
    void edit(YtsafetyLocationInfoDTO dto);

    /**
     * 通过主键查询流程数据
     * @param id
     * @return
     */
    YtsafetyLocationInfoProcessVO queryById(Long id);

    /**
     * 批量推送位置信息流程
     * 将推送的详情按厂区归类，每个厂区创建一个申请位置信息库流程
     * @param dto 包含批量详情的入参，changeType 自动设置为批量推送
     * @return 每个厂区对应的流程VO集合
     */
    java.util.List<YtsafetyLocationInfoProcessVO> batchPush(YtsafetyLocationInfoDTO dto);

    /**
     * 导入Excel数据，转换为流程详情VO并依据名称反查补全主键字段
     * @param dto 导入参数
     * @param excelData Excel解析后的数据
     * @return 补全主键后的流程详情VO列表
     */
    java.util.List<YtsafetyLocationInfoProcessDetailsVO> importData(YtsafetyLocationInfoDTO dto, java.util.List<YtsafetyLocationInfoImportVO> excelData);

    /**
     * 执行流程操作（提交/审批/驳回等）
     * @param processParamDTO 流程参数
     * @return 流程响应参数
     */
    BpmReponseParamsVO executeProcess(BpmProcessParamDTO processParamDTO);

}
