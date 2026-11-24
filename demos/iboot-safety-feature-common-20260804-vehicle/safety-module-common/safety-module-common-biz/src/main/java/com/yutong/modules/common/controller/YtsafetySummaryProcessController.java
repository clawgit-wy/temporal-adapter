package com.yutong.modules.common.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.common.api.bpm.IYtsafetyProcessNodeConfigService;
import com.yutong.modules.common.api.bpm.IYtsafetySummaryProcessService;
import com.yutong.modules.common.dto.BpmPassProcessDTO;
import com.yutong.modules.common.dto.YtsafetySummaryProcessQueryDTO;
import com.yutong.modules.common.entity.YtsafetyProcessNodeConfig;
import com.yutong.modules.common.entity.YtsafetySummaryProcess;
import com.yutong.modules.common.vo.YtsafetySummaryProcessVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Api(tags="流程汇总信息")
@RestController
@RequestMapping("/bpm/process")
public class YtsafetySummaryProcessController{
	@Autowired
	private IYtsafetySummaryProcessService safetySummaryProcessService;
	@Autowired
	private IYtsafetyProcessNodeConfigService processNodeConfigService;

    /**
     * 我的申请
     *
     * @return
     */
    @ApiOperation(value="流程汇总信息表-我的申请", notes="对接BPM系统模块表-我的申请")
    @GetMapping(value = "/queryMyApply")
    public Result<IPage<YtsafetySummaryProcessVO>> queryMyApply(YtsafetySummaryProcessQueryDTO safetySummaryProcessQueryDTO,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
        Page<YtsafetySummaryProcessVO> page = new Page<YtsafetySummaryProcessVO>(pageNo, pageSize);
        IPage<YtsafetySummaryProcessVO> pageList = safetySummaryProcessService.queryMyApply(page, safetySummaryProcessQueryDTO);
        List<YtsafetySummaryProcessVO> newRecords = pageList.getRecords().stream()
                .map(vo -> {
                    if(vo.getIsNickName()!=null&&vo.getIsNickName().equals("1")){
                        vo.setApplyName(vo.getNickName());
                    }
                    return vo;
                })
                .collect(Collectors.toList());
        // 覆盖分页数据
        pageList.setRecords(newRecords);
        return Result.OK(pageList);
    }

    /**
     * 流程待办
     *
     * @return
     */
    @ApiOperation(value="流程汇总信息表-流程待办", notes="对接BPM系统模块表-流程待办")
    @GetMapping(value = "/queryProcessTodo")
    public Result<IPage<YtsafetySummaryProcessVO>> queryProcessTodo(YtsafetySummaryProcessQueryDTO safetySummaryProcessQueryDTO,
                                      @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                      @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                      HttpServletRequest req) {
        Page<YtsafetySummaryProcessVO> page = new Page<YtsafetySummaryProcessVO>(pageNo, pageSize);
        IPage<YtsafetySummaryProcessVO> pageList = safetySummaryProcessService.queryProcessTodo(page, safetySummaryProcessQueryDTO);
        List<YtsafetySummaryProcessVO> newRecords = pageList.getRecords().stream()
                .map(vo -> {
                    if(vo.getIsNickName()!=null&&vo.getIsNickName().equals("1")){
                        vo.setApplyName(vo.getNickName());
                    }
                    return vo;
                })
                .collect(Collectors.toList());
        // 覆盖分页数据
        pageList.setRecords(newRecords);
        return Result.OK(pageList);
    }

    /**
     * 流程已办
     *
     * @return
     */
    @ApiOperation(value="流程汇总信息表-流程已办", notes="对接BPM系统模块表-流程已办")
    @GetMapping(value = "/queryProcessCompleted")
    public Result<IPage<YtsafetySummaryProcessVO>> queryProcessCompleted(YtsafetySummaryProcessQueryDTO safetySummaryProcessQueryDTO,
                                           @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                           @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                           HttpServletRequest req) {
        Page<YtsafetySummaryProcessVO> page = new Page<YtsafetySummaryProcessVO>(pageNo, pageSize);
        IPage<YtsafetySummaryProcessVO> pageList = safetySummaryProcessService.queryProcessCompleted(page, safetySummaryProcessQueryDTO);
        List<YtsafetySummaryProcessVO> newRecords = pageList.getRecords().stream()
                .map(vo -> {
                    if(vo.getIsNickName()!=null&&vo.getIsNickName().equals("1")){
                        vo.setApplyName(vo.getNickName());
                    }
                    return vo;
                })
                .collect(Collectors.toList());
        // 覆盖分页数据
        pageList.setRecords(newRecords);
        return Result.OK(pageList);
    }

    /**
     * 所有流程
     *
     * @return
     */
    @ApiOperation(value="流程汇总信息表-所有流程", notes="对接BPM系统模块表-所有流程")
    @GetMapping(value = "/queryAllProcess")
    public Result<IPage<YtsafetySummaryProcessVO>> queryAllProcess(YtsafetySummaryProcessQueryDTO safetySummaryProcessQueryDTO,
                                     @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                     @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                     HttpServletRequest req) {
        Page<YtsafetySummaryProcessVO> page = new Page<YtsafetySummaryProcessVO>(pageNo, pageSize);
        IPage<YtsafetySummaryProcessVO> pageList = safetySummaryProcessService.queryAllProcess(page, safetySummaryProcessQueryDTO);
        List<YtsafetySummaryProcessVO> newRecords = pageList.getRecords().stream()
                .map(vo -> {
                    if(vo.getIsNickName()!=null&&vo.getIsNickName().equals("1")){
                        vo.setApplyName(vo.getNickName());
                    }
                    return vo;
                })
                .collect(Collectors.toList());
        // 覆盖分页数据
        pageList.setRecords(newRecords);
        return Result.OK(pageList);
    }

    /**
     * 草稿箱
     *
     * @return
     */
    @ApiOperation(value="流程汇总信息表-草稿箱", notes="对接BPM系统模块表-草稿箱")
    @GetMapping(value = "/queryProcessDraft")
    public Result<IPage<YtsafetySummaryProcessVO>> queryProcessDraft(YtsafetySummaryProcessQueryDTO safetySummaryProcessQueryDTO,
                                       @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                       @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                       HttpServletRequest req) {
        Page<YtsafetySummaryProcessVO> page = new Page<YtsafetySummaryProcessVO>(pageNo, pageSize);
        IPage<YtsafetySummaryProcessVO> pageList = safetySummaryProcessService.queryProcessDraft(page, safetySummaryProcessQueryDTO);
        return Result.OK(pageList);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "流程汇总信息表-通过id删除")
    @ApiOperation(value="BPM模型配置-通过id删除", notes="BPM模型配置-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<YtsafetySummaryProcess> delete(@RequestParam(name="id",required=true) Long id) {
        return Result.OK(safetySummaryProcessService.deleteDraftById(id));
    }


    /**
     * 通过id删除
     *
     * @return
     */
    @AutoLog(value = "流程汇总信息表-通过id删除")
    @ApiOperation(value="BPM模型配置-通过id删除", notes="BPM模型配置-通过id删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name="ids",required=true) List<Long> ids) {
        safetySummaryProcessService.batchDeleteDraftById(ids);
        return Result.OK("批量删除成功");
    }

    /**
     * 流程汇总信息表-传阅
     *
     * @return
     */
    @ApiOperation(value="流程汇总信息表-传阅", notes="流程汇总信息表-传阅")
    @PostMapping(value = "/passProcess")
    public Result<String> passProcess(@RequestBody BpmPassProcessDTO dto) {
        safetySummaryProcessService.passProcess(dto);
        return Result.OK("传阅成功");
    }

    @ApiOperation(value="流程汇总信息表-传阅App", notes="流程汇总信息表-传阅App")
    @PostMapping(value = "/passProcessApp")
    public Result<String> passProcessApp(BpmPassProcessDTO dto) {
        safetySummaryProcessService.passProcess(dto);
        return Result.OK("传阅成功");
    }

    /**
     * 流程信息同步-依据流程信息获取和设置节点的最新状态、历史审批记录和当前审批人员
     *
     * @param id
     * @return
     */
    @AutoLog(value = "流程信息同步")
    @ApiOperation(value="流程信息同步", notes="依据流程信息获取和设置节点的最新状态、历史审批记录和当前审批人员")
    @GetMapping(value = "/syncProcessInfo")
    public Result<String> syncProcessInfo(@RequestParam(name="id",required=true) Long id) {
        safetySummaryProcessService.syncProcessInfo(id);
        return Result.OK("同步成功");
    }

    /**
     * 流程刷新
     *
     * @return
     */
    @ApiOperation(value="流程刷新", notes="流程刷新")
    @GetMapping(value = "/processRefresh")
    public Result<String> processRefresh(@RequestParam(name="formInstanceId",required=true) Long formInstanceId) throws InterruptedException {
        safetySummaryProcessService.asynchronousUpdateProcess(formInstanceId);
        return Result.OK("刷新成功");
    }

    /**
     * 流程节点配置-依据ID修改
     *
     * @param ytsafetyProcessNodeConfig
     * @return
     */
    @AutoLog(value = "流程节点配置-依据ID修改")
    @ApiOperation(value="流程节点配置-依据ID修改", notes="流程节点配置-依据ID修改")
    @RequestMapping(value = "/editNodeConfig", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> editNodeConfig(@RequestBody YtsafetyProcessNodeConfig ytsafetyProcessNodeConfig) {
        if (ytsafetyProcessNodeConfig.getId() == null) {
            return Result.error("id不能为空");
        }
        boolean success = processNodeConfigService.updateById(ytsafetyProcessNodeConfig);
        if (!success) {
            return Result.error("未找到对应的流程节点配置记录");
        }
        return Result.OK("修改成功!");
    }



}
