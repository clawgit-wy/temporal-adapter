package com.yutong.modules.user.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.user.dto.YTSafetySysLogDTO;
import com.yutong.modules.user.entity.YTSafetySysLog;
import com.yutong.modules.user.api.YTSafetySysLogService;
import com.yutong.modules.user.vo.YTSafetySysLogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Api(tags="日志")
@RestController
@RequestMapping("/sys/ytSafetySysLog")
public class YTSafetySysLogController {

    @Autowired
    private YTSafetySysLogService ytSafetySysLogService;

    /**
     * logType 日志类型（1登录日志，2操作日志）
     * operateType 操作类型（1查询，2添加，3修改，4删除,5导入，6导出）
     */


    @RequiresPermissions("systemd:log")
    @ApiOperation(value = "日志列表", notes = "日志列表")
    @PostMapping(value = "/list")
    public Result<IPage<YTSafetySysLogVO>> queryPageList(@RequestBody YTSafetySysLogDTO safetySysLogDTO) {
        Result<IPage<YTSafetySysLogVO>> result = new Result<IPage<YTSafetySysLogVO>>();
        try{
            LambdaQueryWrapper<YTSafetySysLog> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(StringUtils.hasText(safetySysLogDTO.getLogContent()),YTSafetySysLog::getLogContent,safetySysLogDTO.getLogContent())
                .like(StringUtils.hasText(safetySysLogDTO.getUsername()),YTSafetySysLog::getUsername,safetySysLogDTO.getUsername())
                .eq(safetySysLogDTO.getLogType()!=null,YTSafetySysLog::getLogType,safetySysLogDTO.getLogType())
                    .eq(safetySysLogDTO.getOperateType()!=null,YTSafetySysLog::getOperateType,safetySysLogDTO.getOperateType())
                    .eq(safetySysLogDTO.getRequestType()!=null,YTSafetySysLog::getRequestType,safetySysLogDTO.getRequestType())
                    .orderByDesc(YTSafetySysLog::getCreateTime);
            if(safetySysLogDTO.getStartTime()!=null && safetySysLogDTO.getEndTime()!=null){
                queryWrapper.between(YTSafetySysLog::getCreateTime,safetySysLogDTO.getStartTime(),safetySysLogDTO.getEndTime());
            }
            Page<YTSafetySysLogVO> page = new Page<YTSafetySysLogVO>(safetySysLogDTO.getPageNo(), safetySysLogDTO.getPageSize());
            IPage<YTSafetySysLogVO> pageList = ytSafetySysLogService.queryPage(page,queryWrapper);
            result.setCode(200);
            result.setSuccess(true);
            result.setResult(pageList);
        }catch (Exception e){
            log.error(e.getMessage());
            result.error500(e.getMessage());
        }
        return result;
    }

}
