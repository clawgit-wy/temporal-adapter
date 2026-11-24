package com.yutong.safety.web.controller.web.goodsOut;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutDetailListParam;
import com.yutong.safety.core.handler.goodsOut.SafetyGoodsOutDetailGoodsHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;

@Api(tags = "物资出门证-物资明细管理")
@RestController
@RequestMapping("/web/safety/goodsOut/detail/")
public class SafetyGoodsOutDetailController {

    @Autowired
    private SafetyGoodsOutDetailGoodsHandler safetyGoodsOutDetailGoodsHandler;

    @ApiOperation("分页查询")
    @RequestMapping(value = "/getPageList", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getPageList(GoodsOutDetailListParam param, @RequestHeader("X-Access-Token") String token){
        String userName = TokenUtils.getUserName(token);
        if(StringUtils.isBlank(userName)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请刷新重新登录");
        }
        try{
            return safetyGoodsOutDetailGoodsHandler.getPageList(param,token);
        }catch (Exception e){
            LogUtils.error("执行异常",e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),ResponseEnum.EXCEPTION.getName());
        }
    }

    @ApiOperation("出门证明细导出")
    @RequestMapping(value = "/exportData", method = {RequestMethod.GET,RequestMethod.POST})
    public Object exportData(GoodsOutDetailListParam param, HttpServletResponse response,
                             @RequestHeader("X-Access-Token") String token){
        try {
            String fileName = new String(("出门证物资明细"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyGoodsOutDetailGoodsHandler.exportData(param, token);
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
            LogUtils.error("导出出门证物资明细异常", e);
        }
        return null;
    }
}
