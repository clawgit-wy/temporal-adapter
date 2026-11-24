package com.yutong.safety.core.handler.spot;

import com.alibaba.excel.util.StringUtils;
import com.google.common.base.Joiner;
import com.yutong.safety.common.param.web.spot.ValidateScenePostAddParam;
import com.yutong.safety.common.param.web.spot.ValidateScenePostListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.EipToDoVO;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.spot.SafetyValidateScenePostDO;
import com.yutong.safety.core.service.SafetySysUserService;
import com.yutong.safety.core.service.SafetyWaterFlowNoService;
import com.yutong.safety.core.service.eip.EipCommonService;
import com.yutong.safety.core.service.spot.SafetyValidateScenePostService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

/**
 * 岗位验证
 * @author gwxiexj
 * @since 2024-06-24 10:28
 */
@Component
public class SafetyValidateScenePostHandler {

    @Value("${bpm.backCall.formUrl}")
    private String bpmBackCallFormUrl;

    @Autowired
    private SafetyValidateScenePostService safetyValidateScenePostService;

    @Autowired
    private SafetySysUserService safetySysUserService;

    @Autowired
    private SafetyWaterFlowNoService safetyWaterFlowNoService;

    @Autowired
    private EipCommonService eipCommonService;

    private static final String SCENE_MODEL_ID = "security_patrol";
    private static final String SCENE_FORM_ID = "safety_validate_scene_post";

    /**
     * 新增 入参
     * @param param 入参
     * @return 返回值
     */
    public int save(ValidateScenePostAddParam param,String token){
        param.setFlowNo(getFlowNo(param.getDataType()+""));

        SafetyValidateScenePostDO scenePostDO = safetyValidateScenePostService.saveOrUpdate(param,token);
        int id = scenePostDO.getId();
        //不是直接领导时，给指定的直接领导发送EIP代办 20241024发版
        if(param.getLeaderFlag() == 1 && StringUtils.isNotBlank(param.getLeaderEmpNo())){
            String operatePage = "现场岗位";
            try {
                if (param.getDataType() == 2) {
                    operatePage = "模拟岗位";
                }
                // 判断：为‘现场岗位上报’ 或者 ‘模拟岗位上报’ 并且‘验证是否合格’为是的时候不发送代办
                if ((param.getDataType() == 1 || param.getDataType() == 2) && param.getIsPass() == 1) {
                    return 1;
                }
                String taragets = "[{\"PersonNo\":\"" + param.getLeaderEmpNo() + "\"}]";
                String docCreator = "{\"PersonNo\":\"" + param.getCreateEmpNo() + "\"}";
                String subject = "请补充" + param.getCreateEmp() + "上报的" + operatePage + "处理结果";
                String modelId = "SAFETY-" + SCENE_FORM_ID + "-" + scenePostDO.getId();
                String modelName = "安保平台" + operatePage;
                String link = bpmBackCallFormUrl + "?sysId=SAFETY&amp;modelId=" + SCENE_MODEL_ID + "&amp;templateFormId=" + SCENE_FORM_ID+param.getDataType() + "&amp;formInstanceId=" + id + "&amp;formId=" + "&amp;processId=" + modelId;
                EipToDoVO eipToDoVO = new EipToDoVO(modelId, modelName, subject, link, null, taragets, null, docCreator, null);
                eipCommonService.createEipToDo(eipToDoVO);
            }catch (Exception e){
                LogUtils.error("【发送代办失败】"+operatePage+",from "+param.getCreateEmpNo()+" to "+param.getLeaderEmpNo()+":",e);
            }
        }
        return 1;
    }


    private String getFlowNo(String dataType){
        String date = DateUtils.format(new Date(),7);
        String nowNum = safetyWaterFlowNoService.getNowByType("validate_poat"+dataType);
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
    public int update(ValidateScenePostAddParam param,String token){

        SafetyValidateScenePostDO returnPaostDO = safetyValidateScenePostService.saveOrUpdate(param,token);
        // 消除代办 - 20241024发版
        if(!ObjectUtils.isEmpty(returnPaostDO)){
            String processId = param.getProcessId();
            if(StringUtils.isBlank(param.getProcessId())){
                processId = "SAFETY-" + SCENE_FORM_ID + "-" + returnPaostDO.getId();
            }
            try{
                EipToDoVO eipToDoVO = new EipToDoVO(processId,"","","","","","","","");
                eipCommonService.setEipToDoDone(eipToDoVO);
           }catch (Exception e){
               LogUtils.error("【消除代办异常】"+",from "+param.getCreateEmpNo()+" to "+param.getLeaderEmpNo()+":",e);
           }
        }
         return 1;
    }

    /**
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    public Object pageList(ValidateScenePostListParam param, String token) {
        //获取登录用户的权限厂区
        String workNo = TokenUtils.getWorkNo(token);
        String factoryIdStr = safetySysUserService.getPowerFactory(workNo);
        param.setFactoryIds(factoryIdStr);
        List<SafetyValidateScenePostDO> list = safetyValidateScenePostService.selectList(param,true);
        long total = safetyValidateScenePostService.selectCount(param);

        return new PageVO<>(total, list);
    }

    /**
     * 导出
     * @param param 查询入参
     * @return 返回
     */
    public HSSFWorkbook export(ValidateScenePostListParam param, String token) {
        String workNo = TokenUtils.getWorkNo(token);
        String factoryIdStr = safetySysUserService.getPowerFactory(workNo);
        param.setFactoryIds(factoryIdStr);
        List<SafetyValidateScenePostDO> list = safetyValidateScenePostService.selectList(param,false);
        int dataType = param.getDataType();
        String[] title;
        String[][] values = new String[list.size()][];
        SafetyValidateScenePostDO vo;
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
                String timeStr = DateUtils.format(vo.getCreateTime(),2);
                String dealStr = "";
                if(vo.getDealTime() != null){
                    dealStr = DateUtils.format(vo.getDealTime(),2);
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
                String timeStr = DateUtils.format(vo.getCreateTime(),2);
                String dealStr = "";
                if(vo.getDealTime() != null){
                    dealStr = DateUtils.format(vo.getDealTime(),2);
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

        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    public SafetyValidateScenePostDO selectByParimaryKey(Integer id){
        return safetyValidateScenePostService.selectByParimaryKey(id);
    }
}
