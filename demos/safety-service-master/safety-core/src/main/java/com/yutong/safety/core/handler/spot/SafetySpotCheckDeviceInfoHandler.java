package com.yutong.safety.core.handler.spot;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.spot.*;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.web.SpotCheckDeviceInfoVO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckDeviceInfoDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckRecordDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckRecordDOExample;
import com.yutong.safety.core.service.SafetySysUserService;
import com.yutong.safety.core.service.spot.SafetySpotCheckDeviceInfoService;
import com.yutong.safety.core.service.spot.SafetySpotCheckRecordService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author gwcuiyx
 * @date 2024-4-28 11:26
 */
@Component
public class SafetySpotCheckDeviceInfoHandler {
    @Autowired
    private SafetySpotCheckDeviceInfoService safetySpotCheckDeviceInfoService;

    @Autowired
    private SafetySpotCheckRecordService safetySpotCheckRecordService;

    @Autowired
    private SafetySysUserService safetySysUserService;

    /**
     * 数据列表
     * @param param
     * @return
     */
    public Object pageList(SpotCheckDeviceListParam param,String token) throws Exception{
        //获取登录用户授权工厂
        String workNo = TokenUtils.getWorkNo(token);
        String factoryId = safetySysUserService.getPowerFactory(workNo);
        param.setFactoryIds(factoryId);

        List<SpotCheckDeviceInfoVO> list = safetySpotCheckDeviceInfoService.pageList(param);
        long total = safetySpotCheckDeviceInfoService.pageListCount(param);
        return new PageVO<>(total, list);
    }

    /**
     * 数据导出操作
     * @param param
     * @return
     */
    public HSSFWorkbook export(SpotCheckDeviceListParam param, String token) {
        //获取登录用户授权工厂
        String workNo = TokenUtils.getWorkNo(token);
        String factoryId = safetySysUserService.getPowerFactory(workNo);
        param.setFactoryIds(factoryId);

        List<SpotCheckDeviceInfoVO> list = safetySpotCheckDeviceInfoService.export(param);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"设备当前状态","厂区", "类型", "项目", "名称","位置","设备编号","责任部门","点检人","应用巡检标准","累计故障次数","临时点检人","临时点检人应用开始时间","临时点检人应用结束时间","备注"};
        String[][] values = new String[list.size()][];
        SpotCheckDeviceInfoVO spotCheckDeviceInfoVO;
        for(int i=0; i<list.size(); i++) {
            spotCheckDeviceInfoVO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = spotCheckDeviceInfoVO.getDeviceStatus()==1?"异常":"正常";
            values[i][1] = spotCheckDeviceInfoVO.getFactoryName();//厂区
            values[i][2] = spotCheckDeviceInfoVO.getDeviceType();//类型
            values[i][3] = spotCheckDeviceInfoVO.getTeamName();//项目
            values[i][4] = spotCheckDeviceInfoVO.getDeviceName();//名称
            values[i][5] = spotCheckDeviceInfoVO.getLocation();//位置
            values[i][6] = spotCheckDeviceInfoVO.getDeviceCode();//设备编号
            values[i][7] = spotCheckDeviceInfoVO.getDutyName();//责任部门
            values[i][8] = spotCheckDeviceInfoVO.getCheckEmp();//点检人
            values[i][9] = spotCheckDeviceInfoVO.getRuleNames();//应用巡检标准
            values[i][10] = Objects.nonNull(spotCheckDeviceInfoVO.getFaultTotal()) ? String.valueOf(spotCheckDeviceInfoVO.getFaultTotal()) : "";//故障次数
            values[i][11] = spotCheckDeviceInfoVO.getProTemCheckEmp();//临时点检人
            values[i][12] = spotCheckDeviceInfoVO.getProTemBeginDate();//临时点检人应用开始时间
            values[i][13] = spotCheckDeviceInfoVO.getProTemEndDate();//临时点检人应用结束时间
            values[i][14] = spotCheckDeviceInfoVO.getRemark();//备注
        }
        String sheetName = "设备设施点检-设备信息管理列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 批量删除操作
     * @param param
     */
    public void batchDelete(SpotCheckBatchDeleteParam param, String token) {
        safetySpotCheckDeviceInfoService.batchDelete(param, token);
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    public Object delete(Integer id, String tocken) {
        return safetySpotCheckDeviceInfoService.delete(id, tocken);
    }

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    public Object add(SpotCheckDeviceAddParam param, String token) throws Exception{
        safetySpotCheckDeviceInfoService.add(param, token);
        return null;
    }

    /**
     * 数据更新操作
     * @param param
     * @return
     */
    public Object update(SpotCheckDeviceAddParam param, String token) throws Exception{
        safetySpotCheckDeviceInfoService.update(param, token);
        return null;
    }

    /**
     * 一键应用临时点检人
     * @param param
     * @param token
     * @return
     */
    public Object updateCheckEmp(SpotCheckBatchUpdateParam param, String token) {
        //1.修改设备信息表的临时点检人
        List<SafetySpotCheckDeviceInfoDO> list = safetySpotCheckDeviceInfoService.updateCheckEmp(param, token);
        if(list.size() > 0){
            List<Integer> deviceIdList = list.stream().map(device -> device.getId()).collect(Collectors.toList());
            SafetySpotCheckRecordDOExample recordExample = new SafetySpotCheckRecordDOExample();
            SafetySpotCheckRecordDOExample.Criteria criteria = recordExample.createCriteria();
            criteria.andDeviceIdIn(deviceIdList);
            criteria.andStatusEqualTo(0);
            criteria.andTaskBeginDateLessThanOrEqualTo(param.getProTemEndDate());
            criteria.andTaskEndDateGreaterThanOrEqualTo(param.getProTemBeginDate());

            SafetySpotCheckRecordDO recordDO = new SafetySpotCheckRecordDO();
            recordDO.setPlanCheckEmpNo(param.getProTemCheckEmpNo());
            recordDO.setPlanCheckEmp(param.getProTemCheckEmp());

            safetySpotCheckRecordService.updateByExampleSelective(recordDO,recordExample);
        }

        //如果新设置的临时点检人的点检结束日期小于T+2，就要去把设备点检人的信息更新到生成的任务中去
        String proTemEndDate = param.getProTemEndDate();
        boolean beforeStr = DateUtils.isBeforeStr(proTemEndDate, DateUtils.format(DateUtils.addDays(new Date(), 2), 1));
        if(beforeStr){
            List<String> idList = Arrays.asList(param.getIds().split(","));
            safetySpotCheckDeviceInfoService.updateCheckEmpData(idList);
        }

        return null;
    }

    /**
     * 批量设置点检人
     * @param param
     * @param token
     * @return
     */
    public Object batchUpdateCheckEmp(SpotCheckBatchUpdateParam param, String token){
        List<String> list = Arrays.asList(param.getIds().split(","));

        SafetySpotCheckDeviceInfoDO entity = new SafetySpotCheckDeviceInfoDO();
        entity.setCheckEmp(param.getCheckEmp());
        entity.setCheckEmpNo(param.getCheckEmpNo());
        entity.setUpdateEmpNo(TokenUtils.getWorkNo(token));
        entity.setUpdateEmp(TokenUtils.getRealName(token));

        safetySpotCheckDeviceInfoService.batchUpdateCheckEmp(list,entity);

        return null;
    }

    /**
     * 一键应用临时点检人
     * @param param
     * @param token
     * @return
     */
    public Object batchCancelCheckEmp(SpotCheckBatchUpdateParam param, String token) {
        //1.修改设备信息表的临时点检人
        List<SafetySpotCheckDeviceInfoDO> list = safetySpotCheckDeviceInfoService.batchCancelCheckEmp(param, token);
        if(list != null && list.size() > 0){
            List<Integer> deviceIdList = list.stream().map(SafetySpotCheckDeviceInfoDO::getId).collect(Collectors.toList());
            SpotCheckRecordListParam recordListParam = new SpotCheckRecordListParam();
            recordListParam.setStatus(0);
            recordListParam.setSpotEndDate(DateUtils.format(new Date(),1));
            recordListParam.setDeviceIdList(deviceIdList);
            safetySpotCheckRecordService.updatePlanEmpBatch(recordListParam);
        }
        return null;
    }

    /**
     * 数据导入操作
     * @param param
     * @return
     */
    public Object importSpotCheckDevice(CommonImportParam param, String token) throws Exception{
        return safetySpotCheckDeviceInfoService.importSpotCheckDevice(param, token);
    }

    /**
     * 获取任务设备列表
     * @param param
     * @return
     */
    public List selectTaskDevices(SpotCheckDeviceListParam param){

        return null;
    }
}
