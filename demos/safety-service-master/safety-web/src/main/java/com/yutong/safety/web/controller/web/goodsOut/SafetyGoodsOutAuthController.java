package com.yutong.safety.web.controller.web.goodsOut;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.excel.ErrorInfo;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutAuthAddParam;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutAuthListParam;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsSelectPersonDTO;
import com.yutong.safety.core.handler.goodsOut.SafetyGoodsOutAuthHandler;
import com.yutong.safety.core.service.wechat.PushWxMesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Api(tags = "物资出门证-权限管理")
@RestController
@RequestMapping("/web/safety/goodsOut/auth")
public class SafetyGoodsOutAuthController {

    @Autowired
    private SafetyGoodsOutAuthHandler safetyGoodsOutAuthHandler;
    @Autowired
    private PushWxMesService pushWxMesService;

    /**
     * 出门证权限查询
     *
     * @param param 入参
     * @return 返回值
     */
    @ApiOperation(value = "出门证权限查询（带分页）")
    @RequestMapping(value = "/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public Object listPage(GoodsOutAuthListParam param, @RequestHeader("X-Access-Token") String token) {
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), safetyGoodsOutAuthHandler.pageList(param));
    }

    /**
     * 批量删除/删除
     *
     * @param idStr ID拼接串
     * @return 返回值
     */
    @ApiOperation(value = "批量删除/删除")
    @RequestMapping(value = "/batchDelete", method = {RequestMethod.GET, RequestMethod.POST})
    public Object batchDelete(String idStr, @RequestHeader("X-Access-Token") String token) {
        return safetyGoodsOutAuthHandler.deleteById(idStr, token);
    }

    /**
     * 数据添加操作
     *
     * @param param
     * @return 返回值
     */
    @ApiOperation(value = "添加接口")
    @RequestMapping(value = "/add", method = {RequestMethod.GET, RequestMethod.POST})
    public Object add(GoodsOutAuthAddParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyGoodsOutAuthHandler.saveOrUpdate(param, token);
    }

    /**
     * 数据添加操作
     *
     * @param param
     * @return 返回值
     */
    @ApiOperation(value = "更新接口")
    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
    public Object update(GoodsOutAuthAddParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyGoodsOutAuthHandler.saveOrUpdate(param, token);
    }

    /**
     * 设备设施点检-设备信息管理列表查询方法
     *
     * @param param
     * @return 返回值
     */
    @ApiOperation(value = "不分页数据列表查询接口")
    @PostMapping(value = "/list")
    public Object list(GoodsOutAuthListParam param, @RequestHeader("X-Access-Token") String token) {
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), safetyGoodsOutAuthHandler.getList(param));
    }

    @ApiOperation(value = "审核人最近选择")
    @PostMapping("/getRecentSelect")
    public Object getRecentSelect(SafetyGoodsSelectPersonDTO en) {
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), safetyGoodsOutAuthHandler.getRecentSelect(en));
    }

    @ApiOperation(value = "审核人搜索")
    @PostMapping("/getPersonByCondition")
    public Object getPersonByCondition(SafetyGoodsSelectPersonDTO en) {
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), safetyGoodsOutAuthHandler.getSelectList(en));
    }

    @ApiOperation(value = "审核人收藏/取消收藏")
    @PostMapping("/collectPerson")
    public Object collectConfig(@RequestParam("ownerEmpNo") String ownerEmpNo,
                                @RequestParam("authId") String authId,
                                @RequestParam("collectType") String collectType) {
        safetyGoodsOutAuthHandler.collectConfig(ownerEmpNo, authId, collectType, "");
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName());
    }

    @ApiOperation(value = "根据条件导出")
    @RequestMapping("/exportData")
    public Object exportData(GoodsOutAuthListParam param, HttpServletResponse response) {
        try {
            String fileName = new String(("出门证权限信息表" + DateUtils.format(new Date(), 5) + ".xls").getBytes(), "ISO8859-1");

            // 设置响应头
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            safetyGoodsOutAuthHandler.exportExcel(param, response.getOutputStream());
        } catch (Exception e) {
            LogUtils.error("导出出门证权限信息表异常", e);
        }
        return null;
    }

    @ApiOperation(value = "出门证权限信息表导入模板下载")
    @RequestMapping("/download")
    public void download(HttpServletResponse response) {
        String fileName = "出门证权限信息表导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/safetyGoodsOutAuth.xlsx");
            InputStream inputStream = classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response, inputStream, fileName);
        } catch (Exception e) {
            LogUtils.error("出门证权限信息表导入模板下载异常", e);
        }
    }

    @ApiOperation(value = "导入")
    @RequestMapping("/importData")
    public VO importData(HttpServletResponse response,
                         @RequestParam("file") MultipartFile file,
                         @RequestHeader("X-Access-Token") String token) {
        response.setCharacterEncoding("utf-8");
        List<ErrorInfo> eList = safetyGoodsOutAuthHandler.importData(file, token);
        if (CollectionUtils.isNotEmpty(eList)) {
            return new VO(ResponseEnum.PARAM_ERROR.getCode(), "导入数据有异常", eList);
        }
        return new VO(ResponseEnum.SUCCESS.getCode(), "导入成功");
    }

    @ApiOperation(value = "定时任务-人员权限更新")
    @RequestMapping(value = "/cancelAccess", method = {RequestMethod.GET,RequestMethod.POST})
    public Object cancelAccess() {
        safetyGoodsOutAuthHandler.cancelAccess();
        return new VO(ResponseEnum.SUCCESS.getCode(), "人员权限更新成功");
    }

    @ApiOperation(value = "外部接口-新增权限审核人")
    @RequestMapping(value = "/addAccessPer", method = {RequestMethod.GET,RequestMethod.POST})
    public Object addAccessPer(@RequestBody List<GoodsOutAuthAddParam> enList) {
        safetyGoodsOutAuthHandler.addAccessPer(enList);
        return new VO(ResponseEnum.SUCCESS.getCode(), "人员权限新增成功");
    }

    @ApiOperation(value = "外部接口-删除权限审核人")
    @RequestMapping(value = "/deleteAccessPer", method = {RequestMethod.GET,RequestMethod.POST})
    public Object deleteAccessPer(@RequestBody List<GoodsOutAuthAddParam> enList) {
        safetyGoodsOutAuthHandler.deleteAccessPer(enList);
        return new VO(ResponseEnum.SUCCESS.getCode(), "人员权限删除成功");
    }
}
