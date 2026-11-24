package com.yutong.safety.web.controller.web.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.spot.PatrolRecordAddParam;
import com.yutong.safety.common.param.web.spot.PatrolRecordListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.dto.spot.SafetyProcessPatrolHistoryDTO;
import com.yutong.safety.core.handler.spot.SafetyPatrolDealHistoryHandler;
import com.yutong.safety.core.handler.spot.SafetyPatrolRecordHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;

/**
 * 治安巡查-巡查记录管理
 * @author gwxiexj
 * @since 2024-05-29 16:48
 */
@Api(tags = "治安巡查-巡查记录管理")
@RestController
@RequestMapping("/web/safety/patrol/record")
public class SafetyPatrolRecordController {

    @Autowired
    private SafetyPatrolRecordHandler safetyPatrolRecordHandler;

    @Autowired
    private SafetyPatrolDealHistoryHandler safetyPatrolDealHistoryHandler;

    /**
     * 获取列表
     * @return 返回值
     */
    @ApiOperation(value = "获取列表")
    @RequestMapping(value="/pageList",method = {RequestMethod.POST,RequestMethod.GET})
    public Object pageList(PatrolRecordListParam listParam, @RequestHeader("X-Access-Token") String token){
        try{
            return safetyPatrolRecordHandler.getList(listParam, token);
        }catch (Exception e){
            LogUtils.error("获取列表异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),""+e.getMessage());
        }
    }

    /**
     * 数据导出操作
     * @param param 入参
     * @param response 请求
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping(value="/export",method = {RequestMethod.POST,RequestMethod.GET})
    public Object export(PatrolRecordListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("巡查业务数据"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyPatrolRecordHandler.exportData(param,token);
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("导出巡查业务数据异常", e);
        }
        return null;
    }

    /**
     * 保存数据创建流程
     * @return 返回值
     */
    @ApiOperation(value = "保存数据创建流程")
    @RequestMapping(value = "/saveAndCreateProcess",method = {RequestMethod.POST,RequestMethod.GET})
    public Object saveAndCreateProcess(PatrolRecordAddParam processDTO, @RequestHeader("X-Access-Token") String tocken) {
        try{
            return safetyPatrolRecordHandler.saveAndCreateProcess(processDTO, tocken);
        }catch (Exception e){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),""+e.getMessage());
        }
    }

    /**
     * 数据新增或者更新操作
     * @param processDTO 入参
     *        token 用户登录信息
     * @return 返回值
     */
    @ApiOperation(value = "更新数据审批流程")
    @RequestMapping(value = "/updateAndAudit",method = {RequestMethod.POST,RequestMethod.GET})
    public Object updateAndAudit(@RequestBody PatrolRecordAddParam processDTO, @RequestHeader("X-Access-Token") String token) {
        try {
            return safetyPatrolRecordHandler.updateAndAudit(processDTO, token);
        } catch (Exception e) {
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "" + e.getMessage());
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
}
