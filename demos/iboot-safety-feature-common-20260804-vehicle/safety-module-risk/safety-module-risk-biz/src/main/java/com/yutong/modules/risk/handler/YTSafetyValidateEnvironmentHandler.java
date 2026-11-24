package com.yutong.modules.risk.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Joiner;
import com.yutong.modules.common.utils.YTWebserviceUtil;
import com.yutong.modules.risk.api.YTSafetyTaskService;
import com.yutong.modules.risk.entity.YTSafetyValidateEnvironment;
import com.yutong.modules.risk.service.YTSafetyValidateEnvironmentService;
import com.yutong.modules.risk.service.YTSafetyWaterFlowNoService;
import com.yutong.modules.risk.util.YTDateUtils;
import com.yutong.modules.risk.util.YTExcelUtils;
import com.yutong.modules.risk.util.YTLogUtils;
import com.yutong.modules.risk.vo.YTSafetyValidateEnvironmentExportVO;
import com.yutong.modules.risk.web.ValidateEnvironmentAddParam;
import com.yutong.modules.risk.web.ValidateEnvironmentListParam;
import com.yutong.modules.user.api.ISafetySysUserService;
import com.yutong.modules.user.entity.SafetySysUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BizException;
import org.jeecg.common.util.BusUtits;
import org.jeecg.common.util.ResponseEnum;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gwxiexj
 * @since 2024-06-11 16:46
 */
@Component
public class YTSafetyValidateEnvironmentHandler{

    @Autowired
    private YTSafetyValidateEnvironmentService safetyValidateEnvironmentService;

    @Autowired
    private ISafetySysUserService safetySysUserService;

    @Autowired
    private YTSafetyTaskService safetyTaskService;

    @Autowired
    private YTSafetyWaterFlowNoService safetyWaterFlowNoService;

    @Value("${iam.employee.workNoConvert.url}")
    private String employeeWorkNoConvertUrl;

    @Autowired
    private YTWebserviceUtil webserviceUtil;

//    private SafetyPatrolTaskService safetyPatrolTaskService;

    /**
     * 新增 入参
     * @param param 入参
     * @return 新增记录的主键ID
     */
    public Long save(ValidateEnvironmentAddParam param){
        Integer isBlank = param.getIsBlacklist();
        if(isBlank == 1){
            param.setBlackTime(YTDateUtils.format(new Date(),2));
            String result = safetyValidateEnvironmentService.insertBlankList(param);
            param.setInterfaceStatus(result);
            if(StringUtils.isNotBlank(result)){
                JSONObject curObj = JSONObject.parseObject(result);
                String IsSucess = String.valueOf(curObj.get("IsSucess"));
                if("true".equals(IsSucess)){
                    param.setBlackResult("成功");
                }else{
                    param.setBlackResult("失败");
                }
            }
        }
        //处理治安巡查任务ID
//        if(param.getTaskId() == null){
//            YTSafetyTaskInfo taskInfo = safetyTaskService.getById(param.getPlanId());
//            if(taskInfo != null){
//                param.setTaskId(taskInfo.getId());
//                param.setEventId(taskInfo.getRiskCategoryId() != null ? taskInfo.getRiskCategoryId() : null);
//            }
//        }
        String flowNo = getFlowNo();
        param.setFlowNo(flowNo);
        return safetyValidateEnvironmentService.save(param);
    }

    private String getFlowNo(){
        String date = YTDateUtils.format(new Date(),7);
        String nowNum = safetyWaterFlowNoService.getNowByType("validate_environ");
        String flowNo = Joiner.on("-").join("HBYZ",date+nowNum);
        return flowNo;
    }

    /**
     * 更新
     * @param param 入参
     * @return 返回值
     */
    public int update(ValidateEnvironmentAddParam param){
        return safetyValidateEnvironmentService.update(param);
    }

    /**
     * 分页查询（使用 MyBatis-Plus 分页插件）
     * @param page 分页对象
     * @param param 查询入参
     * @return 返回
     */
    public IPage<YTSafetyValidateEnvironment> pageList(Page<YTSafetyValidateEnvironment> page, ValidateEnvironmentListParam param) {
        LoginUser loginUser = BusUtits.getLoginUser();
        String workNo = loginUser != null ? loginUser.getEmpNo() : null;
        String factoryIdStr = "0";
        if(StringUtils.isNotBlank(workNo) && workNo.startsWith("01")){
            workNo = employeeConvertWorkNo(workNo);
        }
        if (StringUtils.isNotBlank(workNo)) {
            SafetySysUser user = safetySysUserService.lambdaQuery()
                    .eq(SafetySysUser::getEmpNo, workNo)
                    .eq(SafetySysUser::getDeleteFalg, 0)
                    .one();
            if (user != null && StringUtils.isNotBlank(user.getPowerFactoryId())) {
                factoryIdStr = user.getPowerFactoryId();
            }
        }
        param.setFactoryIdStr(factoryIdStr);
        param.setFlag("1");
        return safetyValidateEnvironmentService.pageList(page, param);
    }

    /**
     * 转换员工号
     * @param oldWorkNo 旧员工号
     * @return 返回值
     */
    public String employeeConvertWorkNo(String oldWorkNo){
        String newWorkNo = oldWorkNo;
        if(StringUtils.isEmpty(employeeWorkNoConvertUrl)){
            YTLogUtils.info("转换员工号接口“iam.employee.workNoConvert.url”为空");
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"转换员工号接口“iam.employee.workNoConvert.url”为空");
        }
        //请求接口查询员工信息
        String url = String.format(employeeWorkNoConvertUrl, "ABYJS", oldWorkNo);
        String result = webserviceUtil.sendHttpForGet(url);
        if(StringUtils.isNotEmpty(result)) {
            JSONObject userJson = (JSONObject) JSON.parse(result);
            if(result.contains("success") && userJson.getBoolean("success")) {
                newWorkNo = userJson.getString("result");
            }
        }
        return newWorkNo;
    }

    /**
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    public ModelAndView exportEnvironment(HttpServletRequest request, ValidateEnvironmentListParam param) {
        //获取登录用户授权工厂
        LoginUser loginUser = BusUtits.getLoginUser();
        String workNo = loginUser != null ? loginUser.getEmpNo() : null;
        String factoryIdStr = "0";
        if(StringUtils.isNotBlank(workNo) && workNo.startsWith("01")){
            workNo = employeeConvertWorkNo(workNo);
        }
        if (StringUtils.isNotBlank(workNo)) {
            SafetySysUser user = safetySysUserService.lambdaQuery()
                    .eq(SafetySysUser::getEmpNo, workNo)
                    .eq(SafetySysUser::getDeleteFalg, 0)
                    .one();
            if (user != null && StringUtils.isNotBlank(user.getPowerFactoryId())) {
                factoryIdStr = user.getPowerFactoryId();
            }
        }
        param.setFactoryIdStr(factoryIdStr);
        param.setFlag("1");
        List<YTSafetyValidateEnvironment> list = safetyValidateEnvironmentService.selectList(param,false);
        List<YTSafetyValidateEnvironmentExportVO> exportList = new ArrayList<>();
        for (YTSafetyValidateEnvironment vo : list) {
            YTSafetyValidateEnvironmentExportVO eo = new YTSafetyValidateEnvironmentExportVO();
            eo.setFlowNo(vo.getFlowNo());
            eo.setFactory(vo.getFactory());
            eo.setLocation(vo.getLocation());
            eo.setCheckTime(vo.getCheckTime());
            eo.setCarNumber(vo.getCarNumber());
            eo.setDriverName(vo.getDriverName());
            eo.setTelephone(vo.getTelephone());
            eo.setEmissionStandard(vo.getEmissionStandard());
            eo.setIsBlacklist(vo.getIsBlacklist() == 1 ? "是" : "否");
            eo.setCheckPerson(vo.getCheckPerson());
            eo.setCheckDept(vo.getCheckDept());
            eo.setBlackResult(vo.getBlackResult());
            eo.setBlackTime(vo.getBlackTime());
            eo.setInterfaceStatus(vo.getInterfaceStatus());
            exportList.add(eo);
        }
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(org.jeecgframework.poi.excel.def.NormalExcelConstants.FILE_NAME, "业务验证-货车环保标准抽查结果");
        mv.addObject(org.jeecgframework.poi.excel.def.NormalExcelConstants.CLASS, YTSafetyValidateEnvironmentExportVO.class);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("业务验证-货车环保标准抽查结果", "导出人:" + (sysUser != null ? sysUser.getRealname() : ""), "业务验证-货车环保标准抽查结果");
        mv.addObject(org.jeecgframework.poi.excel.def.NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(org.jeecgframework.poi.excel.def.NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }

    public HSSFWorkbook export(ValidateEnvironmentListParam param) throws Exception{
        //获取登录用户授权工厂
        LoginUser loginUser = BusUtits.getLoginUser();
        String workNo = loginUser != null ? loginUser.getEmpNo() : null;
        String factoryIdStr = "0";
        if (StringUtils.isNotBlank(workNo)) {
            SafetySysUser user = safetySysUserService.lambdaQuery()
                    .eq(SafetySysUser::getEmpNo, workNo)
                    .eq(SafetySysUser::getDeleteFalg, 0)
                    .one();
            if (user != null && StringUtils.isNotBlank(user.getPowerFactoryId())) {
                factoryIdStr = user.getPowerFactoryId();
            }
        }
        param.setFactoryIdStr(factoryIdStr);
        param.setFlag("1");
        List<YTSafetyValidateEnvironment> list = safetyValidateEnvironmentService.selectList(param,false);
        String[] title = new String[]{"事件编号", "厂区", "位置", "抽查时间", "车牌号","驾驶员姓名","联系电话"
                ,"排放标准","是否加入黑名单","抽查人","抽查人部门","加入黑名单时间","出入口接口返回状态"};
        String[][] values = new String[list.size()][];
        YTSafetyValidateEnvironment vo;
        for(int i=0; i<list.size(); i++) {
            vo = list.get(i);
            values[i] = new String[title.length];
            Integer isBlankList = vo.getIsBlacklist();

            values[i][0] = vo.getFlowNo();
            values[i][1] = vo.getFactory();
            values[i][2] = vo.getLocation();
            values[i][3] = vo.getCheckTime();
            values[i][4] = vo.getCarNumber();
            values[i][5] = vo.getDriverName();
            values[i][6] = vo.getTelephone();
            values[i][7] = vo.getEmissionStandard();
            values[i][8] = isBlankList==1?"是":"否";
            values[i][9] = vo.getCheckPerson();
            values[i][10] = vo.getCheckDept();
            values[i][11] = vo.getBlackTime();
            values[i][12] = vo.getInterfaceStatus();
        }
        String sheetName = "业务验证-货车环保标准抽查结果";
        return YTExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    public YTSafetyValidateEnvironment selectByPrimaryKey(Long id){
        return safetyValidateEnvironmentService.selectByPrimaryKey(id);
    }
}
