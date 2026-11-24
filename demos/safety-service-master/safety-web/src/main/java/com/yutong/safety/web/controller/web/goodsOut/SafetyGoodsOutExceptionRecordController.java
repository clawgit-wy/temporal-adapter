package com.yutong.safety.web.controller.web.goodsOut;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutExceptionRecordsDO;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsOutExceptionRequestDTO;
import com.yutong.safety.core.handler.goodsOut.SafetyGoodsOutExceptionRecordHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Api(tags = "物资出门证WEB-异常上报流程")
@RestController
@RequestMapping("/web/safety/goodsOut/eventsProcess")
public class SafetyGoodsOutExceptionRecordController {
    @Autowired
    SafetyGoodsOutExceptionRecordHandler safetyGoodsOutExceptionRecordHandler;

    @ApiOperation("分页查询")
    @PostMapping("/query")
    public Object queryForList(SafetyGoodsOutExceptionRequestDTO en,
                               @RequestHeader("X-Access-Token") String token){
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName(),
                safetyGoodsOutExceptionRecordHandler.queryForList(en,token));
    }

    @ApiOperation("新增异常")
    @PostMapping("/add")
    public Object add(SafetyGoodsOutExceptionRecordsDO en,@RequestHeader("X-Access-Token") String token){
        safetyGoodsOutExceptionRecordHandler.add(en,token);
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName(),"");
    }

    @ApiOperation("编辑")
    @PostMapping("/updateExceptionInfo")
    public Object updateExceptionInfo(SafetyGoodsOutExceptionRecordsDO en){
        safetyGoodsOutExceptionRecordHandler.updateExceptionInfo(en);
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName(),"");
    }

    @ApiOperation(value = "根据条件导出")
    @RequestMapping("/exportData")
    public Object exportData(SafetyGoodsOutExceptionRequestDTO en, HttpServletResponse response,
                             @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("物资出门证异常明细信息表"+ DateUtils.format(new Date(),5) + ".xlsx").getBytes(), "ISO8859-1");

            // 设置响应头
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            safetyGoodsOutExceptionRecordHandler.exportExcel(en, response.getOutputStream(),token);
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("导出物资出门证异常明细信息表异常", e);
        }
        return null;
    }

    @ApiOperation("定时任务-生成异常数据")
    @RequestMapping("/createExceptionData")
    public Object createExceptionData(){
        try {
            safetyGoodsOutExceptionRecordHandler.createExceptionData();
        }catch (Exception e){
            LogUtils.info("定时任务-生成异常数据异常，异常信息{}",e.getMessage());
        }
        return null;
    }
}
