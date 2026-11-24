package com.yutong.modules.common.api.bpm;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.common.dto.BpmPassProcessDTO;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.dto.BpmRequestParamsDTO;
import com.yutong.modules.common.dto.YtsafetySummaryProcessQueryDTO;
import com.yutong.modules.common.entity.YtsafetySummaryProcess;
import com.yutong.modules.common.entity.bpm.FieldAuth;
import com.yutong.modules.common.entity.bpm.NodeInfo;
import com.yutong.modules.common.entity.bpm.flowinfo.FlowInfoApprover;
import com.yutong.modules.common.vo.BmpProcessDetailsVO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.common.vo.YtsafetySummaryProcessVO;

import java.util.List;

/**
 * @Description: 流程汇总信息表
 * @Author: jeecg-boot
 * @Date:   2026-06-18
 * @Version: V1.0
 */
public interface IYtsafetySummaryProcessService extends IService<YtsafetySummaryProcess> {

    /**
     * 流程待办
     * @param page
     * @param safetySummaryProcessQueryDTO
     * @return
     */
    public IPage<YtsafetySummaryProcessVO> queryProcessTodo(Page<YtsafetySummaryProcessVO> page, YtsafetySummaryProcessQueryDTO safetySummaryProcessQueryDTO);

    /**
     * 流程已办
     * @param page
     * @param safetySummaryProcessQueryDTO
     * @return
     */
    public IPage<YtsafetySummaryProcessVO> queryProcessCompleted(Page<YtsafetySummaryProcessVO> page, YtsafetySummaryProcessQueryDTO safetySummaryProcessQueryDTO);

    /**
     * 所有流程
     * @param page
     * @param safetySummaryProcessQueryDTO
     * @return
     */
    public IPage<YtsafetySummaryProcessVO> queryAllProcess(Page<YtsafetySummaryProcessVO> page, YtsafetySummaryProcessQueryDTO safetySummaryProcessQueryDTO);

    /**
     * 草稿箱
     * @param page
     * @param safetySummaryProcessQueryDTO
     * @return
     */
    public IPage<YtsafetySummaryProcessVO> queryProcessDraft(Page<YtsafetySummaryProcessVO> page, YtsafetySummaryProcessQueryDTO safetySummaryProcessQueryDTO);


    /**
     * 我的申请
     * @param page
     * @param safetySummaryProcessQueryDTO
     * @return
     */
    public IPage<YtsafetySummaryProcessVO> queryMyApply(Page<YtsafetySummaryProcessVO> page, YtsafetySummaryProcessQueryDTO safetySummaryProcessQueryDTO);

    /**
     * 添加
     * @param reponseParamsVO
     * @return
     */
    public YtsafetySummaryProcess addSafetySummaryProcess(BpmReponseParamsVO reponseParamsVO);


    /**
     * 创建并提交流程
     * @param dto
     * @return
     */
    public BpmReponseParamsVO createAndApproveProcess(BpmRequestParamsDTO dto);

    /**
     * 创建并提交流程
     * @param dto
     * @return
     */
    public BpmReponseParamsVO createProcess(BpmRequestParamsDTO dto);


    /**
     * 保存草稿
     * @param dto
     */
    public YtsafetySummaryProcess saveDraft(BpmRequestParamsDTO dto);

    /**
     * 执行流程
     * @param processParamDTO
     * @return
     */
    public BpmReponseParamsVO executeProcess(BpmProcessParamDTO processParamDTO);

    /**
     * 判断流程是否存在
     * @param formInstanceId
     * @return
     */
    public boolean abandoned(Long formInstanceId) ;

    /**
     * 流程信息
     * @param formInstanceId
     * @return
     */
    public List<FlowInfoApprover> getFlowInfo(Long formInstanceId);



    /**
     * 流程废弃
     * @param formInstanceId
     * @return
     */
    public void updataProcessStatus(Long formInstanceId) ;

    /**
     * 通过id删除草稿内容
     * @param id
     * @return
     */
    public YtsafetySummaryProcess deleteDraftById(Long id);

    /**
     * 通过id删除草稿内容
     * @param ids
     * @return
     */
    public void batchDeleteDraftById(List<Long> ids);

    /**
     * 获取流程相关信息
     * @param formInstanceId
     * @param loginName
     * @return
     */
    public BmpProcessDetailsVO queryProcessModelInfo(Long formInstanceId,String loginName);

    /**
     * 传阅
     * @param dto
     */
    public void passProcess(BpmPassProcessDTO dto);

    /**
     * 流程信息同步-依据流程信息获取和设置节点的最新状态、历史审批记录和当前审批人员
     * @param id
     */
    public void syncProcessInfo(Long id);

    /**
     * 获取节点字段权限信息
     * @param formInstanceId
     * @return
     */
    public List<FieldAuth> queryFieldAuth(Long formInstanceId);

    /**
     * 异步刷新流程废弃转态
     * @param formInstanceId
     * @return
     */
    public void asynchronousUpdateProcess(Long formInstanceId) throws InterruptedException;

    public NodeInfo getCurrentNodeInfo(Long id);

}
