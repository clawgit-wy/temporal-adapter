package com.yutong.safety.web.controller.web.enter;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.excel.ErrorInfo;
import com.yutong.safety.common.utils.excel.PubExcelUtil;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.enter.SafetyEnterApprove;
import com.yutong.safety.core.db.entity.enter.rbody.SafetyEnterFilingBody;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterFilingHistoryRt;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterFilingInfoRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterFilingSh;
import com.yutong.safety.core.handler.enter.SafetyEnterFilingHandler;
import com.yutong.safety.core.handler.enter.SafetyEnterFilingHistoryHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 车辆备案
 * */
@Api(tags = "安保入厂-车辆备案信息")
@RestController
@RequestMapping("/web/enter/filing/")
public class SafetyEnterFilingController {
    @Autowired
    private SafetyEnterFilingHandler enterFilingHandler;
    @Autowired
    private SafetyEnterFilingHistoryHandler enterFilingHistoryHandler;

    @ApiOperation(value = "查询可选择的备案厂区")
    @RequestMapping(value = "selectFilingFactory", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectFilingFactory(String id) {
        List<String> rt = enterFilingHandler.selectFilingFactory(id);
        return rt;
    }

    @ApiOperation(value = "审批")
    @RequestMapping(value = "approveEnterFiling", method = {RequestMethod.GET,RequestMethod.POST})
    public Object approveEnterFiling(@RequestBody SafetyEnterApprove approve, @RequestHeader("X-Access-Token") String token) {
        String rt = enterFilingHandler.approveEnterFiling(approve, token);
        if(StringUtils.isNotBlank(rt) && !rt.startsWith("T")){
            return new VO(ResponseEnum.FAIL.getCode(), "审批异常", rt);
        }
        return new VO(ResponseEnum.SUCCESS.getCode(), "操作成功");
    }

    @ApiOperation(value = "批量审批")
    @RequestMapping(value = "approveEnterFilingBatch", method = {RequestMethod.GET,RequestMethod.POST})
    public Object approveEnterFilingBatch(String ids, Integer approveType, @RequestHeader("X-Access-Token") String token) {
        enterFilingHandler.approveEnterFilingBatch(ids, approveType, token);
        return VoUtil.success();
    }

    @ApiOperation(value = "获取查询条件数据")
    @RequestMapping(value = "getSearchParams", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getSearchParams() {
        Map map = enterFilingHandler.getSearchParams();
        return map;
    }

    @ApiOperation(value = "分页查询")
    @RequestMapping(value = "selectEnterFilingPage", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectEnterFilingPage(@RequestBody SafetyEnterFilingSh filingSh, @RequestHeader("X-Access-Token") String token) {
        PageVO pageVO = enterFilingHandler.selectEnterFilingPage(filingSh, token);
        return pageVO;
    }

    @ApiOperation(value = "查询备案信息")
    @RequestMapping(value = "selectEnterFilingInfo", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectEnterFilingInfo(String id) {
        SafetyEnterFilingInfoRt rt = enterFilingHandler.selectEnterFilingInfo(id);
        return new VO(ResponseEnum.SUCCESS.getCode(), "查询成功", JsonUtils.toString(rt));
    }

    @ApiOperation(value = "添加")
    @RequestMapping(value = "insertEnterFiling", method = {RequestMethod.GET,RequestMethod.POST})
    public VO insertEnterFiling(@RequestBody SafetyEnterFilingBody filingBody, @RequestHeader("X-Access-Token") String token) {
        enterFilingHandler.insertEnterFilingWeb(filingBody, token);
        return new VO(ResponseEnum.SUCCESS.getCode(), "添加成功");
    }

    @ApiOperation(value = "更新")
    @RequestMapping(value = "updateEnterFiling", method = {RequestMethod.GET,RequestMethod.POST})
    public VO updateEnterFiling(@RequestBody SafetyEnterFilingBody perBody, @RequestHeader("X-Access-Token") String token) {
        enterFilingHandler.updateEnterFiling(perBody, token);
        return new VO(ResponseEnum.SUCCESS.getCode(), "更新成功");
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "deleteEnterFiling", method = {RequestMethod.GET,RequestMethod.POST})
    public VO deleteEnterFiling(String id, @RequestHeader("X-Access-Token") String token) {
        enterFilingHandler.deleteEnterFiling(id, token);
        return new VO(ResponseEnum.SUCCESS.getCode(), "删除成功");
    }

    @ApiOperation(value = "导入")
    @RequestMapping(value = "importEnterFiling", method = {RequestMethod.GET,RequestMethod.POST})
    public VO importEnterFiling(HttpServletResponse response,
                          @RequestParam("file") MultipartFile file, @RequestHeader("X-Access-Token") String token) {
        response.setCharacterEncoding("utf-8");
        List<ErrorInfo> eList = enterFilingHandler.importEnterFiling(file, token);
        if(CollectionUtils.isNotEmpty(eList)){
            return new VO(ResponseEnum.PARAM_ERROR.getCode(), "导入数据有异常", eList);
        }
        return new VO(ResponseEnum.SUCCESS.getCode(), "导入成功");
    }

    @ApiOperation(value = "导出")
    @RequestMapping(value = "exportEnterFiling", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseEntity<byte[]> exportToExcelEnterFiling(SafetyEnterFilingSh filingSh, @RequestHeader("X-Access-Token") String token){
        String fileName="物流车备案信息.xlsx";
        HttpHeaders headers = PubExcelUtil.setResponseHeader(fileName);
        byte[] value = enterFilingHandler.exportEnterFiling(filingSh, token);
        return new ResponseEntity<>(value, headers, HttpStatus.OK);
    }

    @ApiOperation(value = "分页查询历史记录")
    @RequestMapping(value = "selectEnterFilingHistoryPage", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectEnterFilingHistoryPage(@RequestBody SafetyEnterFilingSh filingSh, @RequestHeader("X-Access-Token") String token) {
        PageVO pageVO = enterFilingHistoryHandler.selectEnterFilingHistoryPage(filingSh, token);
        return pageVO;
    }

    @ApiOperation(value = "根据ID查询历史记录")
    @RequestMapping(value = "selectEnterFilingHistoryById", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectEnterFilingHistoryById(Integer id) {
        SafetyEnterFilingHistoryRt rt = enterFilingHistoryHandler.selectEnterFilingHistoryById(id);
        return VoUtil.successData(rt);
    }

    @ApiOperation(value = "导出历史记录")
    @RequestMapping(value = "exportEnterFilingHistory", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseEntity<byte[]> exportToExcelEnterFilingHistory(SafetyEnterFilingSh filingSh, @RequestHeader("X-Access-Token") String token){
        String fileName="物流车历史备案信息.xlsx";
        HttpHeaders headers = PubExcelUtil.setResponseHeader(fileName);
        byte[] value = enterFilingHistoryHandler.exportEnterFilingHistory(filingSh,token);
        return new ResponseEntity<>(value, headers, HttpStatus.OK);
    }

    @ApiOperation(value = "物流车备案导入模板下载")
    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        String fileName = "物流车备案导入模板.xlsx";
        try {
            ClassPathResource classPathResource = new ClassPathResource("excel/enterFilingDownload.xlsx");
            InputStream inputStream =classPathResource.getInputStream();
            ExcelUtils.outWriteObject(response,inputStream,fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //详见需求：21518
    @ApiOperation(value = "入厂申请填报前信息校验")
    @RequestMapping(value = "verifyBeforeInsert", method = {RequestMethod.GET,RequestMethod.POST})
    public VO verifyBeforeInsert(@RequestBody SafetyEnterFilingBody filingBody) {
        boolean b = enterFilingHandler.verifyBeforeInsert(filingBody);
        return new VO(ResponseEnum.SUCCESS.getCode(), b + "");
    }

    @ApiOperation(value = "入厂申请审批时信息校验")
    @RequestMapping(value = "verifyBeforeApprove", method = {RequestMethod.GET,RequestMethod.POST})
    public VO verifyBeforeApprove(@RequestBody SafetyEnterApprove approve) {
        Map<String, Object> map = enterFilingHandler.verifyBeforeApprove(approve);
        return new VO(ResponseEnum.SUCCESS.getCode(), "", map);
    }
}
