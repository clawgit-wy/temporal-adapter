package com.yutong.safety.web.controller.app.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.spot.*;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.BpmNodeReturnVO;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutProcessListParam;
import com.yutong.safety.core.dto.SafetyPersonCarOutRuleDTO;
import com.yutong.safety.core.dto.SafetyProcessPatrolDTO;
import com.yutong.safety.core.dto.spot.SafetyProcessPatrolHistoryDTO;
import com.yutong.safety.core.handler.spot.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 设备设施点击-任务设置管理
 *
 * @author gwxiexj
 * @since 2024-05-27 15:34
 */
@Api(tags = "治安巡查-点检任务app相关请求")
@RestController
@RequestMapping("/app/safety/patrol/task")
public class SafetyPatrolTaskController {

    @Autowired
    private SafetyPatrolPlanHandler safetyPatrolPlanHandler;

    @Autowired
    private SafetyPatrolTaskHandler safetyPatrolTaskHandler;

    @Autowired
    private SafetyPatrolRecordHandler safetyPatrolRecordHandler;

    @Autowired
    private SafetyValidateEnvironmentHandler safetyValidateEnvironmentHandler;

    @Autowired
    private SafetyPatrolDealHistoryHandler safetyPatrolDealHistoryHandler;

    /**
     * 时生成点检任务
     * @return 返回空
     */
    @ApiOperation(value = "定时生成点检任务")
    @RequestMapping(value = "/taskPatrol", method = {RequestMethod.GET,RequestMethod.POST})
    public Object taskPatrol() {
        try {
            safetyPatrolPlanHandler.taskPatrolCheck();
        }catch (Exception e){
            LogUtils.error("生成点检任务异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"生成点检任务异常");
        }
        return null;
    }

    /**
     * 获取点检进度列表
     * @return 返回值
     */
    @ApiOperation(value = "获取巡查进度列表")
    @RequestMapping(value = "/selectPatrolToDo", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectSpotToDo(PatrolTaskListParam param){
        try {
            return safetyPatrolTaskHandler.selectPatrolTODO(param);
        }catch (Exception e){
            LogUtils.error("获取巡查进度列表异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"获取巡查进度列表异常");
        }
    }

    /**
     * 获取点检进度列表
     * @return 返回值
     */
    @ApiOperation(value = "获取巡查进度列表")
    @RequestMapping(value = "/selectPatrolDetail", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectPatrolTODODetail(PatrolTaskListParam param){
        try {
            return safetyPatrolTaskHandler.selectPatrolTODODetail(param);
        }catch (Exception e){
            LogUtils.error("获取巡查进度列表异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"获取巡查进度列表异常");
        }
    }

    /**
     * 保存数据创建流程
     * @return 返回值
     */
    @ApiOperation(value = "保存数据创建流程")
    @RequestMapping(value = "/saveAndCreateProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object saveAndCreateProcess(PatrolRecordAddParam processDTO) {
        try{
            return safetyPatrolRecordHandler.saveAndCreateProcessByUserName(processDTO);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),""+e.getMessage());
        }
    }

    /**
     * 保存数据创建流程
     * @return 返回值
     */
    @ApiOperation(value = "编辑事件信息-不涉及流程流转")
    @RequestMapping(value = "/modifyProcess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object modifyProcess(SafetyProcessPatrolHistoryDTO updateParam) {
        try{
            return safetyPatrolRecordHandler.modifyProcess(updateParam);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),""+e.getMessage());
        }
    }

    /**
     * 审批流程
     * @return 返回值
     */
    @ApiOperation("审批流程")
    @RequestMapping(value = "/audit", method = {RequestMethod.GET,RequestMethod.POST})
    public VO audit(PatrolRecordAddParam processDTO){
        try{
            BpmNodeReturnVO returnVO = safetyPatrolRecordHandler.updateAndAuditByUserName(processDTO);
            return new VO(ResponseEnum.SUCCESS.getCode(), returnVO.getMessage(),returnVO.getStatus());
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("审批流程异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),""+e.getMessage());
        }

    }

    /**
     * 保存数据创建流程
     * @return 返回值
     */
    @ApiOperation(value = "根据主键ID获取治安巡查类详情")
    @RequestMapping(value="/getRecordDetails", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getRecordDetails(int id) {
        try{
            return safetyPatrolRecordHandler.getDetail(id);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),""+e.getMessage());
        }
    }

    /**
     * 保存数据创建流程
     * @return 返回值
     */
    @ApiOperation(value = "根据主键ID获取业务验证类详情")
    @RequestMapping(value="/getValidateDetails", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getValidateDetails(int id) {
        try{
            return safetyValidateEnvironmentHandler.selectByPrimaryKey(id);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),""+e.getMessage());
        }
    }

    /**
     * 数据新增或者更新操作
     * @param processDTO 入参
     * @return 返回值
     */
    @ApiOperation(value = "转派流程")
    @RequestMapping(value = "/updateTransfer", method = {RequestMethod.GET,RequestMethod.POST})
    public Object updateTransfer(PatrolRecordAddParam processDTO) {
        try {
            BpmNodeReturnVO returnVO = safetyPatrolRecordHandler.updateTransferByUserName(processDTO);
            return new VO(ResponseEnum.SUCCESS.getCode(), returnVO.getMessage(),returnVO.getStatus());
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        } catch (Exception e) {
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "" + e.getMessage());
        }
    }

    /**
     * 数据新增或者更新操作
     * @param recordListParam 入参
     * @return 返回值
     */
    @ApiOperation(value = "获取用户上报事件-入参：eventSource-2，creator-用户名，createBegin-时间起，createEnd-时间终")
    @RequestMapping(value = "/getOwnerReporting", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getOwnerReporting(PatrolRecordListParam recordListParam){
        try {
            String createEnd = recordListParam.getCreateEnd();
            if(StringUtils.isBlank(createEnd)){
                recordListParam.setCreateEnd(DateUtils.format(DateUtils.addDays(new Date(),1),1));
            }else{
                recordListParam.setCreateEnd(DateUtils.format(DateUtils.addDays(DateUtils.parse(createEnd,1),1),1));
            }
            return safetyPatrolRecordHandler.getListNoPower(recordListParam);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        } catch (Exception e) {
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "" + e.getMessage());
        }
    }


    /**
     * 数据新增或者更新操作
     * @param recordListParam 入参
     * @return 返回值
     */
    @ApiOperation(value = "获取厂区上报事件-入参：eventSource-2，creator-当前用户名，createBegin-时间起，createEnd-时间终")
    @RequestMapping(value = "/getSubmitReporting", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getSubmitReporting(PatrolRecordListParam recordListParam){
        try {
            String createEnd = recordListParam.getCreateEnd();
            if(StringUtils.isBlank(createEnd)){
                recordListParam.setCreateEnd(DateUtils.format(DateUtils.addDays(new Date(),1),1));
            }else{
                recordListParam.setCreateEnd(DateUtils.format(DateUtils.addDays(DateUtils.parse(createEnd,1),1),1));
            }
            return safetyPatrolRecordHandler.getListByRolePower(recordListParam);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        } catch (Exception e) {
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "" + e.getMessage());
        }
    }

    /**
     * 数据新增或者更新操作
     * @param nodeUpdateParam 入参
     * @return 返回值
     */
    @ApiOperation(value = "更新节点处理人")
    @RequestMapping(value = "/modifyCurDealHandler", method = {RequestMethod.GET,RequestMethod.POST})
    public Object modifyCurDealHandler(PatrolNodeUpdateParam nodeUpdateParam){
        try {
            if(nodeUpdateParam == null|| nodeUpdateParam.getId() == null || StringUtils.isBlank(nodeUpdateParam.getFlowId())){
                throw new BizException("9999","入参id和flowId都必填");
            }
            return safetyPatrolRecordHandler.modifyCurDealHandler(nodeUpdateParam);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        } catch (Exception e) {
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "" + e.getMessage());
        }
    }

    @ApiOperation(value = "根据治安巡查记录ID获取处理记录")
    @RequestMapping(value="/getDealHistoryList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getDealHistoryList(int processId){
        try{
            return safetyPatrolDealHistoryHandler.getDealHistoryDO(processId);
        }catch(BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),""+e.getMessage());
        }
    }

    @ApiOperation("分页查询")
    @RequestMapping(value = "/getPageList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getPageList(SafetyProcessPatrolHistoryDTO param){
        return safetyPatrolRecordHandler.getPageList(param);
    }

    @ApiOperation("基本信息查询")
    @RequestMapping(value = "/getBaseInfo", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getBaseInfo(SafetyPersonCarOutRuleDTO en){
        return safetyPatrolRecordHandler.getBaseInfo(en);
    }


    /**
     *
     * @param flowIdStr BPM流程ID拼接串
     * @return 返回指向结构
     */
    @ApiOperation(value = "任务-批量更新流程当前节点信息")
    @RequestMapping(value = "batchModifyProcessHandler", method = {RequestMethod.GET,RequestMethod.POST})
    public Object batchModifyProcessHandler(String flowIdStr){
        return safetyPatrolRecordHandler.batchModifyProcessHandler(flowIdStr);
    }

}
