package com.yutong.safety.web.controller.app.goodsOut;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutAuthAddParam;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutAuthListParam;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsSelectPersonDTO;
import com.yutong.safety.core.handler.goodsOut.SafetyGoodsOutAuthHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "物资出门证-权限管理APP")
@RestController
@RequestMapping("/app/safety/goodsOut/auth")
public class SafetyGoodsOutAuthAppController {

    @Autowired
    private SafetyGoodsOutAuthHandler safetyGoodsOutAuthHandler;

    /**
     * 设备设施点检-设备信息管理列表查询方法
     * @param param 入参
     * @return 返回值
     */
    @ApiOperation(value = "分页数据列表查询接口")
    @RequestMapping(value="/listPage",method = {RequestMethod.GET,RequestMethod.POST})
    public Object listPage(GoodsOutAuthListParam param, @RequestHeader("X-Access-Token") String token) {
        return null;
    }

    /**
     * 批量删除操作
     * @param idStr ID拼接串
     * @return  返回值
     */
    @ApiOperation(value = "批量删除接口")
    @RequestMapping(value="/batchDelete",method = {RequestMethod.GET,RequestMethod.POST})
    public Object batchDelete(String idStr, @RequestHeader("X-Access-Token") String token) {
        return safetyGoodsOutAuthHandler.deleteById(idStr,token);
    }

    /**
     * 数据添加操作
     * @param param
     * @return 返回值
     */
    @ApiOperation(value = "添加接口")
    @RequestMapping(value="/add",method = {RequestMethod.GET,RequestMethod.POST})
    public Object add(GoodsOutAuthAddParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyGoodsOutAuthHandler.saveOrUpdate(param,token);
    }

    /**
     * 数据添加操作
     * @param param
     * @return 返回值
     */
    @ApiOperation(value = "更新接口")
    @RequestMapping(value="/update",method = {RequestMethod.GET,RequestMethod.POST})
    public Object update(GoodsOutAuthAddParam param, @RequestHeader("X-Access-Token") String token) {
        return safetyGoodsOutAuthHandler.saveOrUpdate(param,token);
    }

    /**
     * 设备设施点检-设备信息管理列表查询方法
     * @param param
     * @return 返回值
     */
    @ApiOperation(value = "不分页数据列表查询接口")
    @RequestMapping(value="/list",method = {RequestMethod.GET,RequestMethod.POST})
    public Object list(GoodsOutAuthListParam param, @RequestHeader("X-Access-Token") String token) {
        return null;
    }

    @ApiOperation(value = "最近选择")
    @PostMapping("/getRecentSelect")
    public Object getRecentSelect(SafetyGoodsSelectPersonDTO en){
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName(),safetyGoodsOutAuthHandler.getRecentSelect(en));
    }

    @ApiOperation(value = "搜索")
    @PostMapping("/getPersonByCondition")
    public Object getPersonByCondition(SafetyGoodsSelectPersonDTO en){
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName(),safetyGoodsOutAuthHandler.getSelectList(en));
    }

    @ApiOperation(value = "收藏/取消收藏")
    @PostMapping("/collectPerson")
    public Object collectConfig(@RequestParam(name = "ownerEmpNo",required = false)String ownerEmpNo,
                                @RequestParam("authId")String authId,
                                @RequestParam("collectType")String collectType,
                                @RequestParam(name = "phoneNum",required = false)String phoneNum){
        safetyGoodsOutAuthHandler.collectConfig(ownerEmpNo,authId,collectType,phoneNum);
        return new VO(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getName());
    }

    @ApiOperation(value = "EIP 获取审核人权限配置接口")
    @RequestMapping(value = "/queryListForEmpNoOfEip", method = {RequestMethod.POST})
    public VO queryListForEmpNoOfEip(@RequestBody GoodsOutAuthListParam param) {
        return safetyGoodsOutAuthHandler.queryListForEmpNoOfEip(param);
    }
}
