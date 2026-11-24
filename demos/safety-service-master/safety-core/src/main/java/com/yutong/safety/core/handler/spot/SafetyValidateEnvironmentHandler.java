package com.yutong.safety.core.handler.spot;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.yutong.safety.common.param.web.spot.PatrolTaskListParam;
import com.yutong.safety.common.param.web.spot.ValidateEnvironmentAddParam;
import com.yutong.safety.common.param.web.spot.ValidateEnvironmentListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.spot.SafetyPatrolTaskDO;
import com.yutong.safety.core.db.entity.spot.SafetyValidateEnvironmentDO;
import com.yutong.safety.core.service.SafetySysUserService;
import com.yutong.safety.core.service.SafetyWaterFlowNoService;
import com.yutong.safety.core.service.spot.SafetyPatrolTaskService;
import com.yutong.safety.core.service.spot.SafetyValidateEnvironmentService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @author gwxiexj
 * @since 2024-06-11 16:46
 */
@Component
public class SafetyValidateEnvironmentHandler {

    @Autowired
    private SafetyValidateEnvironmentService safetyValidateEnvironmentService;

    @Autowired
    private SafetySysUserService safetySysUserService;

    @Autowired
    private SafetyWaterFlowNoService safetyWaterFlowNoService;

    @Autowired
    private SafetyPatrolTaskService safetyPatrolTaskService;

    /**
     * 新增 入参
     * @param param 入参
     * @return 返回值
     */
    public int save(ValidateEnvironmentAddParam param) throws Exception{
        Integer isBlank = param.getIsBlacklist();
        if(isBlank == 1){
            param.setBlackTime(DateUtils.format(new Date(),2));
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
        if(param.getTaskId() == null){
            PatrolTaskListParam param1 = new PatrolTaskListParam();
            param1.setFactoryId(param.getFactoryId()==null?"":param.getFactoryId()+"");
            param1.setPlanId(param.getPlanId());
            param1.setTaskBeginDate(param.getTaskBeginDate());
            param1.setTaskEndDate(param.getTaskEndDate());
            param1.setPatrolEmpNo(param.getCheckPersonNo());
            List<SafetyPatrolTaskDO> taskDOS = safetyPatrolTaskService.getList(param1);
            if(!CollectionUtils.isEmpty(taskDOS)){
                SafetyPatrolTaskDO zero = taskDOS.get(0);
                param.setTaskId(zero.getId());
                param.setTeamId(zero.getTeamId());
                param.setEventId(zero.getTypeId());
            }
        }
        String flowNo = getFlowNo();
        param.setFlowNo(flowNo);
        return safetyValidateEnvironmentService.save(param);
    }


    private String getFlowNo(){
        String date = DateUtils.format(new Date(),7);
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
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    public Object pageList(ValidateEnvironmentListParam param, String token) {
        String workNo = TokenUtils.getWorkNo(token);
        if(StringUtils.isNotBlank(workNo) && workNo.startsWith("01")){
            workNo = safetySysUserService.employeeConvertWorkNo(workNo);
        }
        String factoryIdStr = safetySysUserService.getPowerFactory(workNo);
        param.setFactoryIdStr(factoryIdStr);
        param.setFlag("1");
        List<SafetyValidateEnvironmentDO> list = safetyValidateEnvironmentService.selectList(param,true);
        long total = safetyValidateEnvironmentService.selectCount(param);

        return new PageVO<>(total, list);
    }

    /**
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    public HSSFWorkbook export(ValidateEnvironmentListParam param, String token) throws Exception{
        //获取登录用户授权工厂
        String workNo = TokenUtils.getWorkNo(token);
        if(StringUtils.isNotBlank(workNo) && workNo.startsWith("01")){
            workNo = safetySysUserService.employeeConvertWorkNo(workNo);
        }
        String factoryIdStr = safetySysUserService.getPowerFactory(workNo);
        param.setFactoryIdStr(factoryIdStr);
        param.setFlag("1");
        List<SafetyValidateEnvironmentDO> list = safetyValidateEnvironmentService.selectList(param,false);
        String[] title = new String[]{"事件编号", "厂区", "位置", "抽查时间", "车牌号","驾驶员姓名","联系电话"
                ,"排放标准","是否加入黑名单","抽查人","抽查人部门","加入黑名单时间","出入口接口返回状态"};
        String[][] values = new String[list.size()][];
        SafetyValidateEnvironmentDO vo;
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
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    public SafetyValidateEnvironmentDO selectByPrimaryKey(Integer id){
        return safetyValidateEnvironmentService.selectByPrimaryKey(id);
    }
}
