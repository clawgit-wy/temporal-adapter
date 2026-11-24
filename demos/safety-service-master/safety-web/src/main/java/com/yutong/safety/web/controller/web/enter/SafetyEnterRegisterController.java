package com.yutong.safety.web.controller.web.enter;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.utils.excel.PubExcelUtil;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.enter.SafetyEnterRegister;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterRegisterSh;
import com.yutong.safety.core.handler.enter.SafetyEnterRegisterHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 已登记人员信息
 * */
@Api(tags = "安保入厂-已登记人员信息")
@RestController
@RequestMapping("/web/enter/register/")
public class SafetyEnterRegisterController {
    @Autowired
    private SafetyEnterRegisterHandler enterRegisterHandler;

    @ApiOperation(value = "分页查询")
    @RequestMapping(value = "selectEnterRegisterPage", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectEnterRegisterPage(@RequestBody SafetyEnterRegisterSh enterRegister) {
        return enterRegisterHandler.selectEnterRegisterPage(enterRegister);
    }

    @ApiOperation(value = "编辑")
    @RequestMapping(value = "updateEnterRegister", method = {RequestMethod.GET,RequestMethod.POST})
    public VO updateEnterRegister(SafetyEnterRegister enterRegister, @RequestHeader("X-Access-Token") String token) {
        enterRegisterHandler.updateEnterRegister(enterRegister, token);
        return new VO(ResponseEnum.SUCCESS.getCode(), "编辑成功");
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "deleteEnterRegister", method = {RequestMethod.GET,RequestMethod.POST})
    public VO deleteEnterRegister(Integer id, String reason,@RequestHeader("X-Access-Token") String token) {
        enterRegisterHandler.deleteEnterRegister(id,reason, token);
        return new VO(ResponseEnum.SUCCESS.getCode(), "删除成功");
    }

    /*导出已登记人员信息*/
    @ApiOperation(value = "导出")
    @RequestMapping(value = "exportEnterRegister", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseEntity<byte[]> exportToExcelEnterRegister(SafetyEnterRegisterSh enterRegisterSh){
        //设置excel文件名
        String fileName="已登记人员信息.xlsx";
        //设置HttpHeaders，设置fileName编码，排除导出文档名称乱码问题
        HttpHeaders headers = PubExcelUtil.setResponseHeader(fileName);
        //获取要导出的数据
        byte[] value = this.enterRegisterHandler.exportEnterRegister(enterRegisterSh);
        return new ResponseEntity<>(value,headers, HttpStatus.OK);
    }

    /*根据手机号获取人员注册信息*/
    @ApiOperation(value = "根据手机号获取人员注册信息")
    @RequestMapping(value = "selectEnterRegisterByPhone", method = {RequestMethod.GET,RequestMethod.POST})
    public Object selectEnterRegisterByPhone(String phone){
        return enterRegisterHandler.selectEnterRegisterByPhone(phone);
    }
}
