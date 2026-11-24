package com.yutong.safety.web.controller.web.goodsOut;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.spot.SafetyValidateEnvironmentDO;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsOutValidDTO;
import com.yutong.safety.core.handler.goodsOut.SafetyGoodsOutValidHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Api(tags = "物资出门证-业务验证")
@RestController
@RequestMapping("/web/safety/goodsOut/valid")
public class SafetyGoodsOutValidController {

    @Autowired
    SafetyGoodsOutValidHandler safetyGoodsOutValidHandler;

    @ApiOperation(value = "列表")
    @PostMapping("/list")
    public Object getList(@RequestBody SafetyValidateEnvironmentDO en) {
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName(),safetyGoodsOutValidHandler.getList(en));
    }

    @ApiOperation(value = "根据条件导出")
    @RequestMapping(value = "/exportData", method = {RequestMethod.GET,RequestMethod.POST})
    public Object exportData(@RequestBody SafetyValidateEnvironmentDO en, HttpServletResponse response,
                             @RequestHeader("X-Access-Token") String tocken) {
        try {
            String fileName = new String(("物资出门证业务详情信息表"+ DateUtils.format(new Date(),5) + ".xlsx").getBytes(), "ISO8859-1");

            // 设置响应头
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            safetyGoodsOutValidHandler.exportExcel(en, response.getOutputStream());
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("导出物资出门证异常信息异常", e);
        }
        return null;
    }

    @ApiOperation(value = "查询货物明细")
    @PostMapping("/getGoodsDetail")
    public Object getGoodsDetail(@RequestParam("processId")String processId, @RequestParam(name = "selectContent",required = false)String selectContent) {
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName(),safetyGoodsOutValidHandler.getGoodsDetail(processId,selectContent));
    }

    @ApiOperation(value = "业务验证详情页")
    @PostMapping("/getDetailInfo")
    public Object getDetailInfo(@RequestParam("processId")String processId, @RequestParam(name = "businessType",required = false)String businessType) {
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName(),safetyGoodsOutValidHandler.getDetailInfo(processId,businessType));
    }

    @ApiOperation(value = "业务验证详情保存")
    @PostMapping("/saveDetailInfo")
    public Object saveDetailInfo(@RequestBody SafetyValidateEnvironmentDO en) {
        safetyGoodsOutValidHandler.saveDetailInfo(en);
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName());
    }

    @ApiOperation(value = "业务验证台账列表")
    @RequestMapping(value = "/getPageList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getPageList(SafetyGoodsOutValidDTO en, @RequestHeader("X-Access-Token") String token) {
        try{
            return safetyGoodsOutValidHandler.getPageList(en,token);
        }catch (Exception e){
            LogUtils.error("执行异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),ResponseEnum.EXCEPTION.getName());
        }
    }

    @ApiOperation(value = "业务验证台账编辑")
    @PostMapping("/updateDetails")
    public Object updateDetails(SafetyValidateEnvironmentDO en) {
        safetyGoodsOutValidHandler.updateDetails(en);
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName(),"");
    }

    @ApiOperation(value = "业务验证台账详情页")
    @PostMapping("/getDetailInfoAll")
    public Object getDetailInfoAll(@RequestParam("processId")String processId) {
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName(),safetyGoodsOutValidHandler.getDetailInfoAll(processId));
    }
}
