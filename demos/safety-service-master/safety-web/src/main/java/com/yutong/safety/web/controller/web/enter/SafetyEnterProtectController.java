package com.yutong.safety.web.controller.web.enter;

import ch.qos.logback.core.util.StringCollectionUtil;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.utils.excel.PubExcelUtil;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.enter.SafetyEnterProtect;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterProtectRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterProtectSh;
import com.yutong.safety.core.handler.enter.SafetyEnterProtectHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "安保入厂-车辆环保信息")
@RestController
@RequestMapping("/web/enter/protect/")
public class SafetyEnterProtectController {
    @Autowired
    private SafetyEnterProtectHandler enterProtectHandler;

    @ApiOperation(value = "分页查询")
    @RequestMapping(value = "selectEnterProtectPage", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectEnterProtectPage(@RequestBody SafetyEnterProtectSh protectSh) {
        PageVO pageVO = enterProtectHandler.selectEnterProtectPage(protectSh);
        return pageVO;
    }

    @ApiOperation(value = "添加")
    @RequestMapping(value = "insertEnterProtect", method = {RequestMethod.GET,RequestMethod.POST})
    public VO insertEnterProtect(@RequestBody SafetyEnterProtect protect, @RequestHeader("X-Access-Token") String token) {
        enterProtectHandler.insertEnterProtect(protect, token);
        return new VO(ResponseEnum.SUCCESS.getCode(), "添加成功");
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "deleteEnterProtect", method = {RequestMethod.GET,RequestMethod.POST})
    public VO deleteEnterProtect(Integer id, @RequestHeader("X-Access-Token") String token) {
        enterProtectHandler.deleteEnterProtect(id, token);
        return new VO(ResponseEnum.SUCCESS.getCode(), "删除成功");
    }

    @ApiOperation(value = "导出")
    @RequestMapping(value = "exportEnterProtect", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseEntity<byte[]> exportToExcelEnterProtect(SafetyEnterProtectSh protectSh){
        String fileName="物流车环保信息.xlsx";
        HttpHeaders headers = PubExcelUtil.setResponseHeader(fileName);
        byte[] value = enterProtectHandler.exportEnterProtect(protectSh);
        return new ResponseEntity<>(value, headers, HttpStatus.OK);
    }

    @ApiOperation(value = "获取选择项参数")
    @RequestMapping(value = "getSearchParams", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getSearchParams() {
        Map map = enterProtectHandler.getSearchParams();
        return map;
    }

    @ApiOperation(value = "根据车牌号获取环保车信息")
    @RequestMapping(value = "getEnterProtectByCarNo", method = {RequestMethod.GET,RequestMethod.POST})
    public Object getEnterProtectByCarNo(String carNo){
        if(StringUtils.isEmpty(carNo)){
            return new VO(ResponseEnum.FAIL.getCode(), "车牌号不能为空");
        }
        return enterProtectHandler.getEnterProtectByCarNo(carNo);
    }
}
