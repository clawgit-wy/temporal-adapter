package com.yutong.modules.risk.handler;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.yutong.modules.common.api.eip.IEipDoneService;
import com.yutong.modules.common.entity.eip.done.EipSetTodoDoneArg;
import com.yutong.modules.common.entity.eip.done.SendTodoReturn;
import com.yutong.modules.common.entity.eip.todo.EipSendTodoArg;
import com.yutong.modules.common.utils.EipXmlUtil;
import com.yutong.modules.risk.entity.YTSafetyValidateScenePost;
import com.yutong.modules.risk.service.YTSafetyValidateScenePostService;
import com.yutong.modules.risk.service.YTSafetyWaterFlowNoService;
import com.yutong.modules.risk.util.YTDateUtils;
import com.yutong.modules.risk.util.YTExcelUtils;
import com.yutong.modules.risk.util.YTLogUtils;
import com.yutong.modules.risk.vo.YTSafetyValidateScenePostExportVO;
import com.yutong.modules.risk.vo.YTSafetyValidateScenePostSimulateExportVO;
import com.yutong.modules.risk.web.ValidateScenePostAddParam;
import com.yutong.modules.risk.web.ValidateScenePostListParam;
import com.yutong.modules.user.api.ISafetyFieldManageService;
import com.yutong.modules.user.api.ISafetySysUserService;
import com.yutong.modules.user.entity.SafetyFieldManage;
import com.yutong.modules.user.entity.SafetySysUser;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.jeecg.common.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 岗位验证
 * @author gwxiexj
 * @since 2024-06-24 10:28
 */
@Component
public class YTSafetyValidateScenePostHandler {

//    @Value("${bpm.backCall.formUrl}")
//    private String bpmBackCallFormUrl;

    private String bpmBackCallFormUrl;

    @Autowired
    private YTSafetyValidateScenePostService safetyValidateScenePostService;

    @Autowired
    private ISafetySysUserService safetySysUserService;

    @Autowired
    private ISafetyFieldManageService safetyFieldManageService;

    @Autowired
    private YTSafetyWaterFlowNoService safetyWaterFlowNoService;

    @Autowired
    private IEipDoneService eipDoneService;

    @Value("${ytsafety.excel.exportUrl}")
    private String exportUrl;

    @Value("${ytsafety.eip.todo.url}")
    private String toDoUrl;

    @Value("${ytsafety.custom.constant.bpmSysId}")
    private String bpmSysId;

    //    @Autowired
//    private YTEipCommonService eipCommonService;
    private static final String SCENE_MODEL_ID = "security_patrol";
    private static final String SCENE_FORM_ID = "ytsafety_validate_scene_post";
    private static String FILED_TYPE_CODE = "inspect_003_xcgwsb_1,inspect_003_mngwsb_2";

    /**
     * 新增 入参
     * @param param 入参
     * @return 新增记录的主键ID
     */
    public Long save(ValidateScenePostAddParam param){
        param.setFlowNo(getFlowNo(param.getDataType()+""));

        YTSafetyValidateScenePost scenePostDO = safetyValidateScenePostService.saveOrUpdate(param);
        Long id = scenePostDO.getId();
        //不是直接领导时，给指定的直接领导发送EIP代办 20241024发版
        if(param.getLeaderFlag() == 1 && StringUtils.isNotBlank(param.getLeaderEmpNo())){
            String operatePage = "现场岗位";
            String type = "spot";
            String modelId = "spotxc";
            String templateFormId = "spot";
            String link = "";
            String postType = "355";
            try {
                if (param.getDataType() == 2) {
                    operatePage = "模拟岗位";
                    type = "simulate";
                    modelId = "simulate";
                    templateFormId = "db";
                    postType = "356";
                }
                // 判断：为‘现场岗位上报’ 或者 ‘模拟岗位上报’ 并且‘验证是否合格’为是的时候不发送代办
                if ((param.getDataType() == 1 || param.getDataType() == 2) && param.getIsPass() == 1) {
                    return id;
                }
                String targets = param.getLeaderEmpNo();
                String docCreator = param.getCreateEmpNo();
                String subject = "请补充" + param.getCreateEmp() + "上报的" + operatePage + "处理结果";
//                String modelId = "YTSAFETY-" + SCENE_FORM_ID + "-" + scenePostDO.getId();
//                String modelName = "安保平台" + operatePage;
//                String link = exportUrl + "/taskreport" + "?sysId=SAFETY&amp;modelId=" + SCENE_MODEL_ID + "&amp;templateFormId=" + SCENE_FORM_ID+param.getDataType() + "&amp;formInstanceId=" + id + "&amp;formId=" + "&amp;processId=" + modelId;
//                String link = exportUrl + "taskreport?" + "formInstanceId=" + id +"&activeTab=" + type;
                link = toDoUrl+"?sysId=" + bpmSysId + "&modelId=ytsafety_"+modelId+"&templateFormId=ytsafety_"+templateFormId+"&BPM_NotifyTodoId=a&eip=true&isCompleted=0&id="+id+"&formInstanceId="+id+"&activeTab=" + type+"&postType="+postType;

                // 添加代办
                EipSendTodoArg eipSendTodoArg = new EipSendTodoArg();

                eipSendTodoArg.setLink(link);
                eipSendTodoArg.setModelId(String.valueOf(id));
                eipSendTodoArg.setCreateTime(DateUtils.formatDateTime());
                eipSendTodoArg.setDocCreator(EipXmlUtil.docCreator(docCreator));
                eipSendTodoArg.setTargets(EipXmlUtil.getTaragets(Arrays.asList(new String[]{targets})));
                eipSendTodoArg.setModelName("安消管理平台");
                eipSendTodoArg.setAppName("安消管理平台");
                eipSendTodoArg.setSubject(subject);
                eipSendTodoArg.setType(1);
                SendTodoReturn xml = eipDoneService.createEipTodo(eipSendTodoArg);
                YTLogUtils.info("发送待办任务:{}",xml);
            }catch (Exception e){
                YTLogUtils.error("【发送代办失败】"+operatePage+",from "+param.getCreateEmpNo()+" to "+param.getLeaderEmpNo()+":",e);
            }
        }
        return id;
    }


    private String getFlowNo(String dataType){
        String date = YTDateUtils.format(new Date(),7);
        String nowNum = safetyWaterFlowNoService.getNowByType("validate_post"+dataType);
        String preStr = "XCYZ";
        if("2".equals(dataType)){
            preStr = "MNYZ";
        }
        return Joiner.on("-").join(preStr,date+nowNum);
    }

    /**
     * 更新
     * @param param 入参
     * @return 返回值
     */
    public int update(ValidateScenePostAddParam param){

        YTSafetyValidateScenePost returnPaostDO = safetyValidateScenePostService.saveOrUpdate(param);
        // 消除代办 - 20241024发版
        if(!ObjectUtils.isEmpty(returnPaostDO)){
            String processId = String.valueOf(param.getId());
//            if(StringUtils.isBlank(param.getProcessId())){
//                processId = "SAFETY-" + SCENE_FORM_ID + "-" + returnPaostDO.getId();
//            }
            try{
//                YTEipToDoVO eipToDoVO = new YTEipToDoVO(processId,"","","","","","","","");
                // 消除代办
                EipSetTodoDoneArg eipSetTodoDoneArg = new EipSetTodoDoneArg();
                eipSetTodoDoneArg.setAppName("安消管理平台");
                eipSetTodoDoneArg.setModelId(processId);
                eipSetTodoDoneArg.setType(1);
                eipSetTodoDoneArg.setModelName("安消管理平台");
                eipDoneService.eipToDoDone(eipSetTodoDoneArg);
           }catch (Exception e){
               YTLogUtils.error("【消除代办异常】"+",from "+param.getCreateEmpNo()+" to "+param.getLeaderEmpNo()+":",e);
           }
        }
         return 1;
    }

    /**
     * 分页查询（使用 MyBatis-Plus 分页插件）
     * @param page 分页对象
     * @param param 查询入参
     * @return 返回
     */
    public IPage<YTSafetyValidateScenePost> pageList(Page<YTSafetyValidateScenePost> page, ValidateScenePostListParam param) {
        //获取登录用户的权限厂区
        LoginUser loginUser = BusUtits.getLoginUser();
        String workNo = loginUser != null ? loginUser.getEmpNo() : null;
        if (StringUtils.isNotBlank(workNo)) {
            SafetySysUser user = safetySysUserService.lambdaQuery()
                    .eq(SafetySysUser::getEmpNo, workNo)
                    .eq(SafetySysUser::getDeleteFalg, 0)
                    .one();
            if (user != null && StringUtils.isNotBlank(user.getPowerFactoryId())) {
                if(param.getFactoryId() == null){
                    param.setFactoryIds(user.getPowerFactoryId());
                }
            }
        }
        return safetyValidateScenePostService.pageList(page, param);
    }

    /**
     * 导出（新方式，使用ModelAndView+@Excel注解）
     * @param param 查询入参
     * @return 返回
     */
    public List<?> exportScenePost(HttpServletRequest request, ValidateScenePostListParam param) {
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
        param.setFactoryIds(factoryIdStr);
        List<YTSafetyValidateScenePost> list = safetyValidateScenePostService.selectList(param,false);
        Integer dataType = param.getDataType();

        if(dataType == 1){
            List<YTSafetyValidateScenePostExportVO> exportList = new ArrayList<>();
            for (YTSafetyValidateScenePost vo : list) {
                YTSafetyValidateScenePostExportVO eo = new YTSafetyValidateScenePostExportVO();
                Integer isPass = vo.getIsPass();
                if(isPass == null){ isPass = 0; }
                Integer isExamine = vo.getIsExamine();
                if(isExamine == null){ isExamine = 0; }
                String validateBegin = vo.getValidateBeginTime();
                String validateEnd = vo.getValidateEndTime();
                String timeStr = YTDateUtils.format(vo.getCreateTime(),2);
                String dealStr = "";
                if(vo.getDealTime() != null){
                    dealStr = YTDateUtils.format(vo.getDealTime(),2);
                }

                eo.setFlowNo(vo.getFlowNo());
                eo.setFactory(vo.getFactory());
                eo.setPostName(vo.getPostName());
                eo.setValidateTime(validateBegin + "~" + validateEnd);
                eo.setTeamName(vo.getTeamName());
                eo.setValidateTeam(vo.getValidateTeam());
                eo.setPersonCategory(vo.getPersonCategory());
                eo.setEmpName(vo.getEmpName());
                eo.setEmpDept(vo.getEmpDept());
                eo.setValidateDesc(vo.getValidateDesc());
                eo.setIsPass(isPass == 1 ? "是" : "否");
                eo.setIsExamine(isExamine == 1 ? "是" : "否");
                eo.setReformDesc(vo.getReformDesc());
                eo.setCreateEmp(vo.getCreateEmp());
                eo.setCreateDept(vo.getCreateDept());
                eo.setLeaderEmpName(vo.getLeaderEmpName());
                eo.setCreateTime(timeStr);
                eo.setDealTime(dealStr);
                eo.setRemark(vo.getRemark());
                eo.setLink(exportUrl+"taskreport?formInstanceId="+vo.getId()+"&activeTab=spot");
                exportList.add(eo);
            }
            return exportList;
        }else{
            List<YTSafetyValidateScenePostSimulateExportVO> exportList = new ArrayList<>();
            for (YTSafetyValidateScenePost vo : list) {
                YTSafetyValidateScenePostSimulateExportVO eo = new YTSafetyValidateScenePostSimulateExportVO();
                Integer isPass = vo.getIsPass();
                Integer isExamine = vo.getIsExamine();
                if(isPass == null){ isPass = 0; }
                if(isExamine == null){ isExamine = 0; }
                String validateBegin = vo.getValidateBeginTime();
                String validateEnd = vo.getValidateEndTime();
                String timeStr = YTDateUtils.format(vo.getCreateTime(),2);
                String dealStr = "";
                if(vo.getDealTime() != null){
                    dealStr = YTDateUtils.format(vo.getDealTime(),2);
                }

                eo.setFlowNo(vo.getFlowNo());
                eo.setFactory(vo.getFactory());
                eo.setTeamName(vo.getTeamName());
                eo.setValidateTime(validateBegin + "~" + validateEnd);
                eo.setPostName(vo.getPostName());
                eo.setReachDuration(vo.getReachDuration() + "");
                eo.setEmpName(vo.getEmpName());
                eo.setEmpDept(vo.getEmpDept());
                eo.setValidateDesc(vo.getValidateDesc());
                eo.setIsPass(isPass == 1 ? "是" : "否");
                eo.setIsExamine(isExamine == 1 ? "是" : "否");
                eo.setReformDesc(vo.getReformDesc());
                eo.setCreateEmp(vo.getCreateEmp());
                eo.setCreateDept(vo.getCreateDept());
                eo.setLeaderEmpName(vo.getLeaderEmpName());
                eo.setCreateTime(timeStr);
                eo.setDealTime(dealStr);
                eo.setRemark(vo.getRemark());
                eo.setLink(exportUrl+"taskreport?formInstanceId="+vo.getId()+"&activeTab=simulate");
                exportList.add(eo);
            }
            return exportList;
        }
    }

    /**
     * 导出
     * @param param 查询入参
     * @return 返回
     */
    public HSSFWorkbook export(ValidateScenePostListParam param) {
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
        param.setFactoryIds(factoryIdStr);
        List<YTSafetyValidateScenePost> list = safetyValidateScenePostService.selectList(param,false);
        int dataType = param.getDataType();
        String[] title;
        String[][] values = new String[list.size()][];
        YTSafetyValidateScenePost vo;
        String sheetName;
        if(dataType == 1){
            sheetName = "岗位验证-现场岗位验证结果";
            title = new String[]{"事件编号", "厂区", "位置/岗位", "验证时间", "验证项目类型","验证项目描述","人员分类"
                    ,"验证对象","验证对象部门班组","验证情况描述","验证是否合格","是否考核兑现","整改处理结果","验证人姓名"
                    ,"验证人班组部门","验证人对象直系领导","登记时间","处理时间","备注"};
            for(int i=0; i<list.size(); i++) {
                vo = list.get(i);
                values[i] = new String[title.length];
                Integer isPass = vo.getIsPass();
                if(isPass == null){
                    isPass = 0;
                }
                Integer isExamine = vo.getIsExamine();
                if(isExamine == null){
                    isExamine = 0;
                }
                String validateBegin = vo.getValidateBeginTime();
                String validateEnd = vo.getValidateEndTime();
                String timeStr = YTDateUtils.format(vo.getCreateTime(),2);
                String dealStr = "";
                if(vo.getDealTime() != null){
                    dealStr = YTDateUtils.format(vo.getDealTime(),2);
                }

                values[i][0] = vo.getFlowNo();
                values[i][1] = vo.getFactory();
                values[i][2] = vo.getPostName();
                values[i][3] = validateBegin+"~"+validateEnd;
                values[i][4] = vo.getTeamName();
                values[i][5] = vo.getValidateTeam();
                values[i][6] = vo.getPersonCategory();
                values[i][7] = vo.getEmpName();
                values[i][8] = vo.getEmpDept();
                values[i][9] = vo.getValidateDesc();
                values[i][10] = isPass == 1?"是":"否";
                values[i][11] = isExamine == 1?"是":"否";
                values[i][12] = vo.getReformDesc();
                values[i][13] = vo.getCreateEmp();
                values[i][14] = vo.getCreateDept();
                values[i][15] = vo.getLeaderEmpName();
                values[i][16] = timeStr;
                values[i][17] = dealStr;
                values[i][18] = vo.getRemark();

            }
        }else{
            sheetName = "岗位验证-模拟岗位验证结果";

            title = new String[]{"事件编号", "厂区","验证项目", "验证时间", "模拟验证位置", "到达现场用时","出警人","出警人部门班组"
                    ,"验证过程动作及结构","验证是否合格","是否考核兑现","整改处理结果","验证人姓名","验证人班组部门","出警人直系领导",
                    "登记时间","处理时间","备注"};

            for(int i=0; i<list.size(); i++) {
                vo = list.get(i);
                values[i] = new String[title.length];
                Integer isPass = vo.getIsPass();
                Integer isExamine = vo.getIsExamine();
                if(isPass == null){
                    isPass = 0;
                }
                if(isExamine == null){
                    isExamine = 0;
                }
                String validateBegin = vo.getValidateBeginTime();
                String validateEnd = vo.getValidateEndTime();
                String timeStr = YTDateUtils.format(vo.getCreateTime(),2);
                String dealStr = "";
                if(vo.getDealTime() != null){
                    dealStr = YTDateUtils.format(vo.getDealTime(),2);
                }

                values[i][0] = vo.getFlowNo();
                values[i][1] = vo.getFactory();
                values[i][2] = vo.getTeamName();
                values[i][3] = validateBegin+"~"+validateEnd;
                values[i][4] = vo.getPostName();
                values[i][5] = vo.getReachDuration()+"";
                values[i][6] = vo.getEmpName();
                values[i][7] = vo.getEmpDept();
                values[i][8] = vo.getValidateDesc();
                values[i][9] = isPass == 1?"是":"否";
                values[i][10] = isExamine == 1?"是":"否";
                values[i][11] = vo.getReformDesc();
                values[i][12] = vo.getCreateEmp();
                values[i][13] = vo.getCreateDept();
                values[i][14] = vo.getLeaderEmpName();
                values[i][15] = timeStr;
                values[i][16] = dealStr;
                values[i][17] = vo.getRemark();
            }
        }

        return YTExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    public YTSafetyValidateScenePost selectByParimaryKey(Long id){
        YTSafetyValidateScenePost ytSafetyValidateScenePost = safetyValidateScenePostService.selectByParimaryKey(id);
        if(ytSafetyValidateScenePost == null){
            return ytSafetyValidateScenePost;
        }
        return ytSafetyValidateScenePost;
    }

    /**
     * 根据id获取名称值
     * @param factoryMap
     * @return
     */
    public String getTeamNames(String teamIds, Map<Long, SafetyFieldManage> factoryMap) {
        Set<String> ids = Sets.newHashSet(teamIds.split(","));
        StringBuilder factoryName = new StringBuilder();
        for (String s : ids) {
            if (factoryMap.containsKey(Integer.valueOf(s.trim()))) {
                SafetyFieldManage fieldManageDO = factoryMap.get(Integer.valueOf(s));
                if(!ObjectUtils.isEmpty(fieldManageDO)){
                    factoryName.append(fieldManageDO.getFieldName()).append(",");
                }
            }
        }
        if (org.apache.commons.lang3.ObjectUtils.isNotEmpty(factoryName.toString())) {
            return factoryName.substring(0, factoryName.length()-1);
        }
        return "";
    }
}
