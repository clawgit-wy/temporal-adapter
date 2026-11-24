package com.yutong.modules.common.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.yutong.modules.common.api.IBpmProcessService;
import com.yutong.modules.common.api.eip.IEipDoneService;
import com.yutong.modules.common.entity.bpm.create.CreateProcess;
import com.yutong.modules.common.entity.bpm.create.CreateResponseEnvelope;
import com.yutong.modules.common.entity.bpm.flowlog.GetFlowLogList;
import com.yutong.modules.common.entity.eip.done.EipSetTodoDoneArg;
import com.yutong.modules.common.entity.eip.done.SendTodoReturn;
import com.yutong.modules.common.entity.eip.todo.EipSendTodoArg;
import com.yutong.modules.common.entity.eip.todo.SetTodoDoneReturn;
import com.yutong.modules.common.factory.ThirdApiFactory;
import com.yutong.modules.common.utils.EipXmlUtil;
import com.yutong.modules.common.utils.JaxbXmlUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.jeecg.common.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;

@Api(tags = "统一接口管理")
@RestController
@RequestMapping("/interfaceCenter")
@Slf4j
public class DemoController {

    @Autowired
    private ThirdApiFactory apiFactory;

    @Autowired
    private IEipDoneService eipDoneService;

    @Autowired
    private IBpmProcessService bpmProcessService;

    @ApiOperation(value = "统一接口对外服务接口", notes = "统一接口对外服务接口")
    @GetMapping(value = "/apiDemo")
    public Result<String> apiDemo() throws Exception {
        LoginUser loginUser =  BusUtits.getLoginUser();
        String id = IdWorker.getIdStr();
        EipSendTodoArg arg =new EipSendTodoArg();
        arg.setLink("https://anuat.yutong.com/portal/system/process/processApply");
        arg.setModelId(id);
        arg.setCreateTime(DateUtils.formatDateTime());
        arg.setDocCreator(EipXmlUtil.docCreator(loginUser.getEmpNo()));
        arg.setTargets(EipXmlUtil.getTaragets(Arrays.asList(new String[]{"90132094"})));
        arg.setModelName("安消管理平台测试");
        arg.setAppName("安消管理平台");
        arg.setSubject("安消管理平台测试EIP待办");
        arg.setType(2);
        SendTodoReturn xml = eipDoneService.createEipTodo(arg);


        EipSetTodoDoneArg arg1 = new EipSetTodoDoneArg();
        arg1.setAppName("安消管理平台");
        arg1.setModelId(id);
        arg1.setType(2);
        arg1.setModelName("安消管理平台测试");
        SetTodoDoneReturn xml1 = eipDoneService.eipToDoDone(arg1);
        return Result.OK("可以了");
    }
    @ApiOperation(value = "生成雪花ID", notes = "生成雪花ID")
    @GetMapping(value = "/genId")
    public Result<String> genId() {
        return Result.OK(IdWorker.getIdStr());
    }

}
