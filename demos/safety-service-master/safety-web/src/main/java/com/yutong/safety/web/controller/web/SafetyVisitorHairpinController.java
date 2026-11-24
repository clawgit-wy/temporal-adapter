package com.yutong.safety.web.controller.web;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.*;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.handler.SafetyVisitorHairpinHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 *  create gwcuiyx 2023.07.14
 *  访客发卡管理--controller
 */
@Api(tags = "访客发卡管理")
@RestController
@RequestMapping("/web/safety/visitor")
public class SafetyVisitorHairpinController {
    @Autowired
    private SafetyVisitorHairpinHandler safetyVisitorHairpinHandler;

    /**
     * 数据列表查询
     * @param param
     * @return
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping("/list")
    public Object list(VisitorHairpinListParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetyVisitorHairpinHandler.pageList(param, tocken);
    }

    /**
     * 新建访客添加功能
     * @param param
     * @return
     */
    @ApiOperation(value = "数据添加接口")
    @RequestMapping("/add")
    public Object add(VisitorHairpinAddParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetyVisitorHairpinHandler.add(param, tocken);
    }

    /**
     * 根据id获取单条数据详情
     * @param id
     * @return
     */
    @ApiOperation(value = "数据详情接口")
    @RequestMapping("/view")
    public Object view(Integer id) {
        return safetyVisitorHairpinHandler.view(id);
    }

    /**
     * 根据身份证号获取数据
     * @param idCard
     * @return
     */
    @ApiOperation(value = "根据身份证号获取数据接口")
    @RequestMapping("/viewIdCar")
    public Object infoForIdCard(String idCard) {
        if (StringUtils.isEmpty(idCard)) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"身份证号不能为空");
        }
        return safetyVisitorHairpinHandler.infoForIdCard(idCard);
    }

    /**
     * 根据访客卡id获取对应详情数据
     * @param hairpinNo
     * @return
     */
    @ApiOperation(value = "根据访客卡id后去数据接口")
    @RequestMapping("/viewHairpinNo")
    public Object infoForHairpinNo(String hairpinNo) {
        if (StringUtils.isEmpty(hairpinNo)) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"访客卡号不能为空");
        }
        return safetyVisitorHairpinHandler.infoForHairpinNo(hairpinNo);
    }

    /**
     * 数据更新操作
     * @param param
     * @return
     */
    @ApiOperation(value = "数据更新接口")
    @RequestMapping("/update")
    public Object update(VisitorHairpinUpdateParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyVisitorHairpinHandler.update(param, token);
    }

    /**
     * 数据删除功能
     * @param id
     * @return
     */
    @ApiOperation(value = "数据删除接口")
    @RequestMapping("/delete")
    public Object delete(Integer id) {
        return safetyVisitorHairpinHandler.delete(id);
    }

    /**
     * 图片上传中转接口
     * @param param
     * @return
     */
    @ApiOperation(value = "图片上传中转接口")
    @RequestMapping("/picUpload")
    public Object picUpload(PicParam param, @RequestHeader("X-Access-Token") String tocken) {
        return safetyVisitorHairpinHandler.picUpload(param, tocken);
    }

    /**
     * 根据eip流程id更新数据接口
     * @param param
     * @return
     */
    @ApiOperation(value = "根据eip流程id更新数据接口")
    @RequestMapping("/updateByEipId")
    public Object updateEipStatusById(VisitorHairpinParam param) {
        return safetyVisitorHairpinHandler.updateEipStatusById(param);
    }

    /**
     * 新建访客添加功能
     * @param param
     * @return
     */
    @ApiOperation(value = "数据添加接口")
    @RequestMapping("/save")
    public Object addEipFlow(VisitorHairpinAddParam param) {
        return safetyVisitorHairpinHandler.addEipFlow(param);
    }

    /**
     * 数据列表查询
     * @param param
     * @return
     */
    @ApiOperation(value = "访客登记明细查询接口")
    @RequestMapping("/detailList")
    public Object detailList(VisitorHairpinListParam param) {
        return safetyVisitorHairpinHandler.pageDetailList(param);
    }

    /**
     * 数据导出操作
     * @param param
     * @param response
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping("/export")
    public Object export(VisitorHairpinListParam param, HttpServletResponse response, @RequestHeader("X-Access-Token") String tocken) {
        try {
            String fileName = new String(("访客登记明细查询列表"+ DateUtils.format(new Date(),5) + ".xls").getBytes(), "ISO8859-1");
            HSSFWorkbook wb = safetyVisitorHairpinHandler.export(param, tocken);
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
            LogUtils.error("导出访客登记明细查询列表异常", e);
        }
        return null;
    }

    @RequestMapping("/sendPermissionByPerson")
    @ApiOperation("手动下发权限")
    public Object sendPermissionByPerson(Integer id, @RequestHeader("X-Access-Token") String tocken) {
        safetyVisitorHairpinHandler.sendPermissionByPerson(id,tocken);
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName());
    }

    @RequestMapping("/sendPermissionByPersonList")
    @ApiOperation("批量手动下发权限")
    public Object sendPermissionByPersonList(String ids, @RequestHeader("X-Access-Token") String tocken) {
        safetyVisitorHairpinHandler.sendPermissionByPersonList(ids,tocken);
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName());
    }

    @RequestMapping("/cancelPermissionByPerson")
    @ApiOperation("手动注销权限")
    public Object cancelPermissionByPerson(Integer id, @RequestHeader("X-Access-Token") String tocken) {
        safetyVisitorHairpinHandler.cancelPermissionByPerson(id,tocken);
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName());
    }

    @RequestMapping("/permissionDelivery")
    @ApiOperation("访客发卡权限定时下发")
    public Object permissionDelivery() {
        safetyVisitorHairpinHandler.permissionDelivery();
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName());
    }
}
