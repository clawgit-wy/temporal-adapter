package com.yutong.modules.common.controller;

import com.yutong.modules.common.api.bpm.IBpmWebService;
import com.yutong.modules.common.dto.BpmMethodProcessDTO;
import com.yutong.modules.common.dto.BpmTemplateFormDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags="BPM流程")
@RestController
@RequestMapping("/api/bpm")
public class BpmController {

    @Autowired
    private IBpmWebService bpmWebService;

    @Autowired
    private RedisUtil redisUtil;

    private static final String LOCK_PREFIX = "bpm:callback:lock:";
    private static final long WAIT_TIMEOUT = 7L;

    /**
     * 获取异构系统表单模板记录，用于配置流程模板
     * [{
     * "modelName":"通用",  //表单所属模块名称
     * "modelId":"ty",  //表单所属模块ID
     * "templateFormId":"ty_market",  //表单唯一标识
     * "templateFormName":"通用任务模板",  //表单名称
     * "formUrl":"https://mptest.yutong.com/task-management/"  //表单访问地址、待办地址前缀
     * }]
     *
     * @param bpmTemplateFormVO
     * @return TemplateFormResultVO
     */
    @AutoLog(value = "BPM回调接口-获取异构系统表单模板记录",operateType=1)
    @ApiOperation(value = "BPM回调接口-获取异构系统表单模板记录", notes = "BPM回调接口-获取异构系统表单模板记录，用于配置流程模板")
    @PostMapping("/getTemplateFormList")
    public String getTemplateFormList(@RequestBody BpmTemplateFormDTO bpmTemplateFormVO) {
        return bpmWebService.getTemplateFormList(bpmTemplateFormVO);
    }

    /**
     * 获取异构系统表单字段记录
     * [{
     * "fieldId":"cost_type",  //表单字段标识
     * "fieldName":"报销类型",  //表单字段名称
     * "type":"String"  //表单字段类型（String(推荐使用string类型);Number(只有需要比数字大小的场景才使用此类型);Boolean;Date(不建议使用);Array_String;Array_Number(不建议使用)）
     * }]
     *
     * @param bpmTemplateFormVO
     * @return FormFieldValueResultVO
     */
    @AutoLog(value = "BPM回调接口-获取异构系统表单字段记录",operateType=1)
    @ApiOperation(value = "BPM回调接口-获取异构系统表单字段记录", notes = "BPM回调接口-获取异构系统表单字段记录")
    @PostMapping("/getFormFieldList")
    public String getFormFieldList(@RequestBody BpmTemplateFormDTO bpmTemplateFormVO) {
        return bpmWebService.getFormFieldList(bpmTemplateFormVO);
    }


    /**
     * BPM回调接口-查询表单对应的值
     *
     * @param bpmTemplateFormVO
     * @return FormFieldResultVO
     */
    @AutoLog(value = "BPM回调接口-查询表单对应的值",operateType=1)
    @ApiOperation(value = "BPM回调接口-查询表单对应的值", notes = "BPM回调接口-查询表单对应的值")
    @PostMapping("/getFormFieldValueList")
    public String getFormFieldValueList(@RequestBody BpmTemplateFormDTO bpmTemplateFormVO) throws InterruptedException {
        //Thread.sleep(1000);
        return bpmWebService.getFormFieldValueList(bpmTemplateFormVO);
    }


    /**
     * BPM回调接口-获取不同业务逻辑的函数列表
     *
     * @param bpmTemplateFormVO
     * @return MethodInfoResultVO
     */
    @AutoLog(value = "BPM回调接口-获取不同业务逻辑的函数列表",operateType=1)
    @ApiOperation(value = "BPM回调接口-获取不同业务逻辑的函数列表", notes = "BPM回调接口-获取不同业务逻辑的函数列表")
    @PostMapping("/getMethodInfo")
    public String getMethodInfo(@RequestBody BpmTemplateFormDTO bpmTemplateFormVO) {
        return bpmWebService.getMethodInfo(bpmTemplateFormVO);
    }

    /**
     * BPM回调接口-函数回调
     * @param bpmTemplateFormVO
     * 返回结果为字符串：
     * 成功时返回 "T"
     * 失败时返回 "F:异常描述"
     * @return
     */
    @AutoLog(value = "BPM回调接口-函数回调",operateType=1)
    @ApiOperation(value = "BPM回调接口-函数回调", notes = "BPM回调接口-函数回调")
    @PostMapping("/doMethodProcess")
    public String doMethodProcess(@RequestBody BpmMethodProcessDTO bpmTemplateFormVO) {
        return bpmWebService.doMethodProcess(bpmTemplateFormVO);
    }

}
