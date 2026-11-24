package com.yutong.safety.core.handler.goodsOut;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailGoodsDO;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutDetailListParam;
import com.yutong.safety.core.db.entity.goodsOut.vo.GoodsOutDetailGoodsVO;
import com.yutong.safety.core.service.SafetySysUserService;
import com.yutong.safety.core.service.goodsOut.SafetyGoodsOutDetailGoodsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SafetyGoodsOutDetailGoodsHandler {

    @Autowired
    private SafetySysUserService safetySysUserService;

    @Autowired
    private SafetyGoodsOutDetailGoodsService safetyGoodsOutDetailGoodsService;

    public int save(SafetyGoodsOutDetailGoodsDO detailGoodsDO){
        return safetyGoodsOutDetailGoodsService.save(detailGoodsDO);
    }

    public int update(SafetyGoodsOutDetailGoodsDO detailGoodsDO){
        return safetyGoodsOutDetailGoodsService.update(detailGoodsDO);
    }

    public PageVO<GoodsOutDetailGoodsVO> getPageList(GoodsOutDetailListParam goodsListParam,String token) {
        goodsListParam.setBegin(1);
        if(!StringUtils.isBlank(goodsListParam.getCreateTimeEnd())){
            goodsListParam.setCreateTimeEnd(DateUtils.format(DateUtils.addDays(DateUtils.parse(goodsListParam.getCreateTimeEnd(),1),1),1));
        }
        String workNo = TokenUtils.getWorkNo(token);
        if(StringUtils.isNotBlank(workNo) && workNo.startsWith("01")){
            workNo = safetySysUserService.employeeConvertWorkNo(workNo);
        }
        String userName = TokenUtils.getUserName(token);
        boolean filterFlag = safetySysUserService.checkRoleByUserName(userName,"超级管理员");
        if(!filterFlag){
            String factoryId = safetySysUserService.getPowerFactory(workNo);
            goodsListParam.setOutFactoryIds(factoryId);
            goodsListParam.setCurPersonLogin(userName);
        }
        List<GoodsOutDetailGoodsVO> list = safetyGoodsOutDetailGoodsService.selectDetailList(goodsListParam);
        long total = safetyGoodsOutDetailGoodsService.selectDetailCount(goodsListParam);
        return new PageVO<>(total, list);
    }

    public HSSFWorkbook exportData(GoodsOutDetailListParam goodsListParam, String token) throws Exception {
        //不是超级管理员时，过滤授权厂区
        String workNo = TokenUtils.getWorkNo(token);
        if(StringUtils.isNotBlank(workNo) && workNo.startsWith("01")){
            workNo = safetySysUserService.employeeConvertWorkNo(workNo);
        }
        if(!StringUtils.isBlank(goodsListParam.getCreateTimeEnd())){
            goodsListParam.setCreateTimeEnd(DateUtils.format(DateUtils.addDays(DateUtils.parse(goodsListParam.getCreateTimeEnd(),1),1),1));
        }
        String userName = TokenUtils.getUserName(token);
        boolean filterFlag = safetySysUserService.checkRoleByUserName(userName,"超级管理员");
        if(!filterFlag){
            String factoryId = safetySysUserService.getPowerFactory(workNo);
            goodsListParam.setOutFactoryIds(factoryId);
            goodsListParam.setCurPersonLogin(userName);
        }

        if(!StringUtils.isBlank(goodsListParam.getCreateTimeEnd())){
            goodsListParam.setCreateTimeEnd(DateUtils.format(DateUtils.addDays(DateUtils.parse(goodsListParam.getCreateTimeEnd(),1),1),1));
        }
        List<GoodsOutDetailGoodsVO> list = safetyGoodsOutDetailGoodsService.selectDetailList(goodsListParam);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"流程编号","主题","物资编号","物资名称","单位","数量","车工号","VIN号","提交时间","出厂人","出厂厂区",
                "审核人","状态","出厂类型","备注"};
        String[][] values = new String[list.size()][];
        GoodsOutDetailGoodsVO goodsOutProcessDTO;
        for(int i=0; i<list.size(); i++) {
            goodsOutProcessDTO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = goodsOutProcessDTO.getFlowNo();
            values[i][1] = goodsOutProcessDTO.getSubject();
            values[i][2] = goodsOutProcessDTO.getGoodsCode();
            values[i][3] = goodsOutProcessDTO.getGoodsName();
            values[i][4] = goodsOutProcessDTO.getGoodsUnit();
            values[i][5] = goodsOutProcessDTO.getGoodsNum()+"";
            values[i][6] = goodsOutProcessDTO.getVehicleNo();
            values[i][7] = goodsOutProcessDTO.getVinNo();
            values[i][8] = goodsOutProcessDTO.getCreateTimeStr();
            values[i][9] = goodsOutProcessDTO.getGooutEmpName();
            values[i][10] = goodsOutProcessDTO.getOutFactoryName();
            values[i][11] = goodsOutProcessDTO.getExamineEmpName();
            values[i][12] = goodsOutProcessDTO.getStatus();
            values[i][13] = goodsOutProcessDTO.getOutTypeName();
            values[i][14] = goodsOutProcessDTO.getRemark();
        }
        String sheetName = "出门证物资明细";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }
}
