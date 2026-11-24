package com.yutong.safety.core.handler.enter;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.enter.SafetyEnterIssuanceDO;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterIssuanceRT;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterIssuanceSh;
import com.yutong.safety.core.handler.entry.SafetyPermissionDelegationHandler;
import com.yutong.safety.core.service.enter.SafetyEnterIssuanceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SafetyEnterIssuanceHandler {

    @Autowired
    private SafetyEnterIssuanceService safetyEnterIssuanceService;

    @Resource
    private SafetyPermissionDelegationHandler safetyPermissionDelegationHandler;

    /**
     * 数据导出操作
     * @param issuanceSh 检索条件
     * @return 附件
     */
    public HSSFWorkbook export(SafetyEnterIssuanceSh issuanceSh, String token) {
        issuanceSh.setExport("1");
        List<SafetyEnterIssuanceRT> list = safetyEnterIssuanceService.queryList(issuanceSh);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"备案类型","姓名/车牌号", "身份证号", "准入时间-始", "准入时间-终","证件照","备案厂区","备案厂区编码","下发时间","下发结果"};
        String[][] values = new String[list.size()][];
        SafetyEnterIssuanceRT enterIssuanceRT;
        for(int i=0; i<list.size(); i++) {
            enterIssuanceRT = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = "10".equals(enterIssuanceRT.getEnterFactoryCode())?"物流车辆":"外来人员";
            values[i][1] = enterIssuanceRT.getFdName();//姓名/车牌号
            values[i][2] = StringUtils.isEmpty(enterIssuanceRT.getFdCardNo())?"无":enterIssuanceRT.getFdCardNo();//身份证号
            values[i][3] = enterIssuanceRT.getEnterStartTime();//准入时间-始
            values[i][4] = enterIssuanceRT.getEnterEndTime();//准入时间-终
            values[i][5] = StringUtils.isEmpty(enterIssuanceRT.getFdImg())?"无":enterIssuanceRT.getFdImg();//证件照
            values[i][6] = enterIssuanceRT.getEnterFactory();//备案厂区
            values[i][7] = enterIssuanceRT.getEnterFactoryCode();//备案厂区编码
            values[i][8] = enterIssuanceRT.getIssuanceTime();//下发时间
            values[i][9] = enterIssuanceRT.getIssuanceResult();//下发结果
        }
        String sheetName = "物流车辆入参备案-备案下发日志";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    public int save(SafetyEnterIssuanceDO enterFactoryIssuance,String token){
        if(StringUtils.isEmpty(token)){
            String realName = TokenUtils.getRealName(token);
            enterFactoryIssuance.setCreateBy(realName);
        }
        enterFactoryIssuance.setCreateTime(new Date());
        return safetyEnterIssuanceService.save(enterFactoryIssuance);
    }

    /**
     * 更新权限下发
     * @param token token
     * @return 返回值
     */
    public int updateIssuance(String token){
        String realName = "";
        if(StringUtils.isEmpty(token)){
            realName = TokenUtils.getRealName(token);
        }
        SafetyEnterIssuanceSh issuanceSh = new SafetyEnterIssuanceSh();
        issuanceSh.setIssuanceResult("false");
        issuanceSh.setExport("1");
        issuanceSh.setEnterStartTime(DateUtils.format(new Date(),2));
        List<SafetyEnterIssuanceRT> issuanceRTList = safetyEnterIssuanceService.queryList(issuanceSh);
        if(CollectionUtils.isEmpty(issuanceRTList)){
            return 0;
        }
        Map<String,List<SafetyEnterIssuanceRT>> issuanceRTMap = issuanceRTList.stream().collect(Collectors.groupingBy(SafetyEnterIssuanceRT::getFdEnterTypeCode));
        for(Map.Entry<String, List<SafetyEnterIssuanceRT>> entry:issuanceRTMap.entrySet()){
            String enterTypeCode = entry.getKey();
            List<SafetyEnterIssuanceRT> curRtList = entry.getValue();
            if(CollectionUtils.isEmpty(curRtList)){
                continue;
            }
            //物流车辆下发
            if("10".equals(enterTypeCode)){
                // 下发车辆权限
                for(SafetyEnterIssuanceRT curRt:curRtList){
                    SafetyEnterIssuanceDO issuanceDO = new SafetyEnterIssuanceDO();
                    BeanUtils.copyProperties(curRt,issuanceDO);
                    issuanceDO.setUpdateBy(realName);
                    safetyEnterIssuanceService.sendCarAuth(curRt.getFdFilingId(),issuanceDO);
                }
            }
            //外来人员下发
            else if ("20".equals(enterTypeCode)) {
                for(SafetyEnterIssuanceRT curRt:curRtList){
                    //下发人员权限
                    SafetyEnterIssuanceDO issuanceDO = new SafetyEnterIssuanceDO();
                    BeanUtils.copyProperties(curRt,issuanceDO);
                    issuanceDO.setUpdateBy(realName);
                    safetyEnterIssuanceService.sendPersonAuth(issuanceDO);
                }
            } else {
                List<SafetyEnterIssuanceDO> list = new ArrayList<>();
                for(SafetyEnterIssuanceRT curRt:curRtList){
                    //下发入场备案人员权限
                    SafetyEnterIssuanceDO issuanceDO = new SafetyEnterIssuanceDO();
                    BeanUtils.copyProperties(curRt,issuanceDO);
                    issuanceDO.setUpdateBy(realName);
                    list.add(issuanceDO);
                }
                safetyPermissionDelegationHandler.reissueCrk(list);
            }
        }
        return issuanceRTList.size();
    }

    /**
     * 分页
     * @param enterFactoryIssuance 入参
     * @param token token
     * @return 返回值
     */
    public PageVO pageList(SafetyEnterIssuanceSh enterFactoryIssuance, String token){
        return safetyEnterIssuanceService.pageList(enterFactoryIssuance);
    }

}
