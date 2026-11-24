package com.yutong.safety.core.handler.spot;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.spot.SpotCheckResultParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckResultDO;
import com.yutong.safety.core.service.spot.SafetySpotCheckResultService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author gwxiexj
 * @since 2024-05-16 15:44
 */
@Component
public class SafetySpotCheckResultHandler {

    @Autowired
    private SafetySpotCheckResultService safetySpotCheckResultService;

    /**
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    public Object pageList(SpotCheckResultParam param, String token) throws Exception{
        List<SafetySpotCheckResultDO> list = safetySpotCheckResultService.list(param);
        long total = safetySpotCheckResultService.pageListCount(param);
        return new PageVO<>(total, list);
    }

    /**
     * 数据导出操作
     * @param param 入参
     * @param token token
     * @return 返回值
     */
    public HSSFWorkbook export(SpotCheckResultParam param, String token) throws Exception{
        List<SafetySpotCheckResultDO> list = safetySpotCheckResultService.list(param);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"计划整改完成日期", "实际整改日期", "整改后图片", "整改人", "整改提交时间"
                , "是否延期整改","延期计划整改完成日期","整改人直接上级","整改人直接上级审批时间"};
        String[][] values = new String[list.size()][];
        SafetySpotCheckResultDO vo;
        for(int i=0; i<list.size(); i++) {
            vo = list.get(i);
            values[i] = new String[title.length];
            String planTimeStr = "";
            String finishTimeStr = "";
            String reformTimeStr = "";
            String delayTimeStr = "";
            String leaderTimeStr = "";
            if(vo.getPlanTime() != null){
                planTimeStr = DateUtils.format(vo.getPlanTime(),1);
            }
            if(vo.getFinishTime() != null){
                finishTimeStr =  DateUtils.format(vo.getFinishTime(),1);
            }
            if(vo.getReformTime() != null){
                reformTimeStr = DateUtils.format(vo.getReformTime(),1);
            }
            if(vo.getDelayTime() != null){
                delayTimeStr = DateUtils.format(vo.getDelayTime(),1);
            }
            if(vo.getReformLeaderTime() != null){
                leaderTimeStr = DateUtils.format(vo.getReformLeaderTime(),1);
            }
            values[i][0] = planTimeStr;
            values[i][1] = finishTimeStr;
            values[i][2] = vo.getReformImg();
            values[i][3] = vo.getReformName();
            values[i][4] = reformTimeStr;
            values[i][5] = vo.getReformDelay();
            values[i][6] = delayTimeStr;
            values[i][7] = vo.getReformLeader();
            values[i][8] = leaderTimeStr;
        }
        String sheetName = "设备设施点检-巡查整改记录";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }
}
