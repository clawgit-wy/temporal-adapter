package com.yutong.safety.web.controller.web.park;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeeClassifiedParam;
import com.yutong.safety.core.handler.park.SafetyParkEmployeeClassifiedHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;

@Api(tags = "安保服务-无涉密业务关联员工")
@RestController
@RequestMapping("/web/park/safety/parkEmployeeClassified")
public class SafetyParkEmployeeClassifiedController {

    @Resource
    private SafetyParkEmployeeClassifiedHandler safetyParkEmployeeClassifiedHandler;

    /**
     * 分页数据列表查询接口
     *
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value = "/pageList", method = {RequestMethod.GET, RequestMethod.POST})
    public Object pageList(ParkEmployeeClassifiedParam param,
                           @RequestHeader("X-Access-Token") String token) {
        try {
            return safetyParkEmployeeClassifiedHandler.selectPageList(param, token);
        } catch (Exception e) {
            LogUtils.error("{\"code\":\"9999\",\"msg\":\"分页查询异常，{}\"}", e);
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "分页查询异常" + e.getMessage());
        }
    }

    /**
     * 取消授权
     *
     * @param id    数据行id
     * @param token token
     * @return
     */
    @ApiOperation(value = "取消授权")
    @ApiImplicitParam(name = "id", value = "数据行ID", dataType = "String", paramType = "query")
    @RequestMapping(value = "/dealBuild", method = {RequestMethod.GET, RequestMethod.POST})
    public Object dealBuild(String id, @RequestHeader("X-Access-Token") String token) {
        String userName = TokenUtils.getUserName(token);
        if (StringUtils.isBlank(userName)) {
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(), "token已失效，请刷新重新登录");
        }
        if (id == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "请确认入参是否正确");
        }
        try {
            return safetyParkEmployeeClassifiedHandler.dealBuild(id, token);
        } catch (BizException biz) {
            throw biz;
        } catch (Exception e) {
            LogUtils.error("无涉密关联业务员工-取消授权异常", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "无涉密关联业务员工-取消授权异常");
        }
    }

    /**
     * 导出
     *
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "导出")
    @RequestMapping(value = "/export", method = {RequestMethod.POST, RequestMethod.GET})
    public Object export(ParkEmployeeClassifiedParam param,
                         HttpServletResponse response,
                         @RequestHeader("X-Access-Token") String token) {
        try {
            String fileName = new String(("安保服务-无涉密业务关联员工" + DateUtils.format(new Date(), 5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyParkEmployeeClassifiedHandler.export(param, token);
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (BizException ex) {
            throw new BizException(ex.getCode(), ex.getMsg());
        } catch (Exception e) {
            LogUtils.error("导出安保服务-无涉密业务关联员工列表异常", e);
        }
        return null;
    }
}
