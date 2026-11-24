package com.yutong.safety.web.controller.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.handler.SafetyGroupHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gwcuiyx
 * @date 2024-1-18 10:12
 */
@Api(tags = "组织结构管理")
@RestController
@RequestMapping("/web/safety/group")
public class SafetyGroupController {

    @Autowired
    private SafetyGroupHandler safetyGroupHandler;

    @ApiOperation(value = "获取组织结构树-异步")
    @RequestMapping("/selectGroupTree")
    public Object selectGroupTree(String key){
       return safetyGroupHandler.selectGroupTree(key);
    }

    @ApiOperation(value = "获取组织结构树(包含人员)-异步")
    @RequestMapping("/selectGroupTreeWithPeople")
    public Object selectGroupTreeWithPeople(String key){
        return safetyGroupHandler.selectGroupTreeWithPeople(key);
    }

    @ApiOperation(value = "获取组织结构树(只包含子组织人员)-异步")
    @RequestMapping("/selectGroupTreeWithPeopleBelong")
    public Object selectGroupTreeWithPeopleBelong(String key,@RequestHeader("X-Access-Token") String token){
        return safetyGroupHandler.selectGroupTreeWithPeopleBelong(key,token);
    }


    @ApiOperation(value = "获取组织结构树-非异步")
    @RequestMapping("/selectGroupTreeNonAsyn")
    public Object selectGroupTreeNonAsyn(String key){
        return safetyGroupHandler.selectGroupTreeNonAsyn();
    }

    @ApiOperation(value = "获取组织结构树-非异步(查询子组织)")
    @RequestMapping("/selectGroupTreeContain")
    public Object selectGroupTreeContain(@RequestHeader("X-Access-Token") String token){
        return safetyGroupHandler.selectGroupTreeContain(token);
    }


    @ApiOperation(value = "模糊查询部门名称")
    @RequestMapping("/getDepartName")
    public Object getDepartName(String keyWords){
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), safetyGroupHandler.getDepartName(keyWords));
    }

    @ApiOperation(value = "模糊查询部门名称-只查询当前登录人下的组织")
    @RequestMapping("/getDepartNameBelongCurPer")
    public Object getDepartNameBelongCurPer(String keyWords,@RequestHeader("X-Access-Token") String token){
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), safetyGroupHandler.getDepartNameBelongCurPer(keyWords,token));
    }

    @ApiOperation(value = "验证部门")
    @RequestMapping("/validDept")
    public Object validDept(String orgPath,@RequestHeader("X-Access-Token") String token){
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), safetyGroupHandler.validDept(orgPath,token));
    }

    @ApiOperation(value = "验证人")
    @RequestMapping("/validPeople")
    public Object validPeople(@RequestBody String userLists, @RequestHeader("X-Access-Token") String token){
        if (StringUtils.isBlank(userLists)){
            return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), null);
        }
        JSONArray jsonArray = JSONObject.parseArray(JSONObject.parseObject(userLists).get("userList").toString());
        List<SafetyEmployeeMessageDO> userList = JSON.parseArray(jsonArray.toJSONString(), SafetyEmployeeMessageDO.class);
        return new VO(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getName(), safetyGroupHandler.validPeople(userList,token));
    }
}
