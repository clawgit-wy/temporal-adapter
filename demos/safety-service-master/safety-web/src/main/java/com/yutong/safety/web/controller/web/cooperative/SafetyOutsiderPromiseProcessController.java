package com.yutong.safety.web.controller.web.cooperative;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.dto.SafetyOutsiderPromiseProcessDTO;
import com.yutong.safety.core.handler.cooperative.SafetyOutsiderPromiseProcessHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "外协入厂权限变更")
@RestController
@RequestMapping("/web/safety/cooperative/outsiderPromiseProcess")
public class SafetyOutsiderPromiseProcessController {

    @Autowired
    SafetyOutsiderPromiseProcessHandler safetyOutsiderPromiseProcessHandler;

    /**
     * 分页查询数据
     * @param dto
     * @return
     */
    @ApiOperation("查询列表")
    @RequestMapping("/findList")
    public Object findList(SafetyOutsiderPromiseProcessDTO dto, @RequestHeader("X-Access-Token") String token){
        return new VO(ResponseEnum.SUCCESS.getCode(), "", safetyOutsiderPromiseProcessHandler.findPageList(dto, token));
    }

}
