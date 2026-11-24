package com.yutong.safety.core.handler.spot;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.*;
import com.yutong.safety.common.param.web.spot.SpotCheckBatchDeleteParam;
import com.yutong.safety.common.param.web.spot.SpotCheckRuleListParam;
import com.yutong.safety.common.param.web.spot.SpotCheckRuleRequireParam;
import com.yutong.safety.common.param.web.spot.SpotCheckRuleSaveParam;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckRuleDO;
import com.yutong.safety.core.service.spot.SafetySpotCheckRuleService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gwxiexj
 * @since 2024-04-28 11:40
 */
@Component
public class SafetySpotCheckRuleHandler {

    @Autowired
    private SafetySpotCheckRuleService safetySpotCheckRuleService;

    /**
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    public Object pageList(SpotCheckRuleListParam param) {
        List<SafetySpotCheckRuleDO> list = safetySpotCheckRuleService.pageList(param);
        long total = safetySpotCheckRuleService.pageListCount(param);
        return new PageVO<SafetySpotCheckRuleDO>(total, list);
    }

    /**
     * 数据导出操作
     * @param param
     * @param token
     * @return
     */
    public HSSFWorkbook export(SpotCheckRuleListParam param, String token) {
        List<SafetySpotCheckRuleDO> list = safetySpotCheckRuleService.export(param);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"项目", "巡检标准类目", "巡检标准", "设备正常是否上传照片", "设备异常首付上传照片", "当场整改是否上传照片", "整改验证是否上传照片","排序","备注"};
        String[][] values = new String[list.size()][];
        SafetySpotCheckRuleDO safetySpotCheckRuleDO;
        for(int i=0; i<list.size(); i++) {
            safetySpotCheckRuleDO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = safetySpotCheckRuleDO.getTeamName();//项目
            values[i][1] = safetySpotCheckRuleDO.getRuleName();//巡检标准类目
            values[i][2] = safetySpotCheckRuleDO.getRuleInfo();//巡检标准
            values[i][3] = safetySpotCheckRuleDO.getNormalImgRequire()!=null&&safetySpotCheckRuleDO.getNormalImgRequire() ? "是" : "否";//设备正常是否上传照片
            values[i][4] = safetySpotCheckRuleDO.getExceptionImgRequire()!=null&&safetySpotCheckRuleDO.getExceptionImgRequire() ? "是" : "否";//设备异常首付上传照片
            values[i][5] = safetySpotCheckRuleDO.getReformImgRequire()!=null&&safetySpotCheckRuleDO.getReformImgRequire() ? "是" : "否";//当场整改是否上传照片
            values[i][6] = safetySpotCheckRuleDO.getValidateImgRequire()!=null&&safetySpotCheckRuleDO.getValidateImgRequire() ? "是" : "否";//整改验证是否上传照片
            values[i][7] = safetySpotCheckRuleDO.getRuleSort()+"";
            values[i][8] = safetySpotCheckRuleDO.getRemark();//备注
        }
        String sheetName = "设备设施点检-设备巡检标准配置表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 数据添加接口
     * @param param
     * @return
     */
    public Object add(SpotCheckRuleSaveParam param, String token){
        SpotCheckRuleListParam listParam = new SpotCheckRuleListParam();
        listParam.setTeamId(param.getTeamId());
        listParam.setRuleName(param.getRuleName());
        listParam.setUnId(param.getId());
        Long count = safetySpotCheckRuleService.pageListCount(listParam);
        if(count > 0){
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"该项目下已存在巡检标准类目“"+param.getRuleName()+"”");
        }
        return safetySpotCheckRuleService.saveOrUpdate(param,token);
    }

    /**
     * 删除-支持批量
     * @param id  ID拼接串,
     * @return 返回值
     * @throws Exception
     */
    public Object deleteById(String id){
        if(StringUtils.isEmpty(id)){
            return 0;
        }
        String[] idArr = id.split(",");
        List<Integer> idList =  Arrays.stream(idArr).map(Integer::valueOf).collect(Collectors.toList());

        return safetySpotCheckRuleService.deleteById(idList);
    }

    /**
     * 批量删除操作
     * @param param
     */
    public void batchDelete(SpotCheckBatchDeleteParam param, String token) {
        safetySpotCheckRuleService.batchDelete(param, token);
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    public Object delete(Integer id, String tocken) {
        return safetySpotCheckRuleService.delete(id, tocken);
    }

    /**
     * 数据导入操作
     * @param param
     * @return
     */
    public Object importSpotCheckRule(CommonImportParam param, String token) {
        return safetySpotCheckRuleService.importSpotCheckRule(param, token);
    }

    /**
     * 更细数据
     * @param param
     * @param token
     * @return
     */
    public Object updateRequire(SpotCheckRuleRequireParam param, String token) {
        safetySpotCheckRuleService.updateRequire(param, token);
        return null;
    }

    /**
     * 不分页数据查询
     * @return
     */
    public Object list(SpotCheckRuleListParam param) {
        return safetySpotCheckRuleService.list(param);
    }

}
