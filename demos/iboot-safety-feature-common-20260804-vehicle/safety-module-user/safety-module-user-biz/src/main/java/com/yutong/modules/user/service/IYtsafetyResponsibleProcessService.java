package com.yutong.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.user.dto.YtsafetyResponsibleProcessDTO;
import com.yutong.modules.user.entity.YtsafetyResponsibleProcess;
import com.yutong.modules.user.vo.*;

import java.util.List;

/**
 * @Description: 安全责任人流程信息表
 * @Author: lvss
 * @Date:   2026-06-22
 * @Version: V1.0
 */
public interface IYtsafetyResponsibleProcessService extends IService<YtsafetyResponsibleProcess> {

    /**
     * 保存变更流程
     * @param safetyResponsibleProcessDTO
     * @return
     */
    public YtsafetyResponsibleProcess save(YtsafetyResponsibleProcessDTO safetyResponsibleProcessDTO);

    /**
     * 编辑变更流程
     * @param safetyResponsibleProcessDTO
     * @return
     */
    public YtsafetyResponsibleProcess edit(YtsafetyResponsibleProcessDTO safetyResponsibleProcessDTO);

    /**
     * 查询变更人员信息
     * @param userInfo
     * @return
     */
    public List<YtsafetyResponsibleChangeVO> queryChangeUser(String userInfo);

    /**
     * 查询变更人员信息
     * @param id
     * @return
     */
    public YtsafetyResponsibleChangeDropVO queryChangeAuth(Long id);

    /**
     * 依据Id查询流程信息
     * @param id
     * @return
     */
    public YtsafetyResponsibleProcessVO queryResponsibleProcess(Long id);

    /**
     * 执行流程
     * @param processParamDTO
     * @return
     */
    public BpmReponseParamsVO executeProcess(BpmProcessParamDTO processParamDTO);

    /**
     * 获取上级审批人员
     * @return
     */
    public List<YtsafetyResponsibleChangeVO> querySuperApprove();

}
