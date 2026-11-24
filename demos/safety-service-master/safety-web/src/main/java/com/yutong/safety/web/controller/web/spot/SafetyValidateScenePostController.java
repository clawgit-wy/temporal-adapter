package com.yutong.safety.web.controller.web.spot;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.spot.ValidateScenePostAddParam;
import com.yutong.safety.common.param.web.spot.ValidateScenePostListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.handler.spot.SafetyValidateScenePostHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;

/**
 * 岗位验证-岗位验证上报及验证结果
 * @author gwxiexj
 * @since 2024-06-24 14:08
 */
@Api(tags = "岗位验证-岗位验证上报及验证结果")
@RestController
@RequestMapping("/web/safety/validate/scenePost")
public class SafetyValidateScenePostController {
    @Autowired
    private SafetyValidateScenePostHandler safetyValidateScenePostHandler;

    /**
     *岗位验证-验证结果列表查询方法
     * @param param
     * @return
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value = "/listPage", method = {RequestMethod.GET,RequestMethod.POST})
    public Object listPage(ValidateScenePostListParam param, @RequestHeader("X-Access-Token") String token) {
        try{
            return safetyValidateScenePostHandler.pageList(param,token);
        }catch (BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("分页数据列表查询接口-异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"分页数据列表查询接口-异常"+e.getMessage());
        }
    }

    /**
     * 数据导出操作
     * @param param
     * @param response
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping(value = "/export", method = {RequestMethod.GET,RequestMethod.POST})
    public Object export(ValidateScenePostListParam param, HttpServletResponse response,
                         @RequestHeader("X-Access-Token") String token) {
        int dataType = param.getDataType();
        String sheetName = "";
        if(dataType == 1){
            sheetName = "岗位验证-现场岗位验证结果";
        }
        if(dataType == 2){
            sheetName = "岗位验证-模拟岗位验证结果";
        }
        try {
            String fileName = new String((sheetName+
                    DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyValidateScenePostHandler.export(param, token);
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
            LogUtils.error("导出"+sheetName+"异常", e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"导出"+sheetName+"异常"+e.getMessage());
        }
        return null;
    }

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    @ApiOperation(value = "添加接口")
    @RequestMapping(value = "/add", method = {RequestMethod.GET,RequestMethod.POST})
    public Object add(ValidateScenePostAddParam param, @RequestHeader("X-Access-Token") String token) {
        try{
            //保存
            return safetyValidateScenePostHandler.save(param,token);
        }catch (BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("添加接口异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"新增接口异常"+e.getMessage());
        }
    }

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    @ApiOperation(value = "更新接口")
    @RequestMapping(value = "/update", method = {RequestMethod.GET,RequestMethod.POST})
    public Object update(ValidateScenePostAddParam param, @RequestHeader("X-Access-Token") String token) {
        try{
            return safetyValidateScenePostHandler.update(param,token);
        }catch (BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("更新接口异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"更新接口异常"+e.getMessage());
        }
    }

    /**
     * 数据添加操作
     * @param id ID
     * @return
     */
    @ApiOperation(value = "更新接口")
    @RequestMapping(value = "/view", method = {RequestMethod.GET,RequestMethod.POST})
    public Object view(Integer id, @RequestHeader("X-Access-Token") String token) {
        try{
            return safetyValidateScenePostHandler.selectByParimaryKey(id);
        }catch (BizException ex){
            throw new BizException(ex.getCode(),ex.getMsg());
        }catch (Exception e){
            LogUtils.error("更新接口异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"更新接口异常"+e.getMessage());
        }
    }
}
