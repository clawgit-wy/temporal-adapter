package com.yutong.safety.core.service.goodsOut;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.easyExcel.ColumnHeightAuto;
import com.yutong.safety.common.easyExcel.ColumnWidthAuto;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailGoodsDO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutDetailGoodsDOExample;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutGoodsExceptionDO;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutProcessDO;
import com.yutong.safety.core.db.entity.goodsOut.vo.SafetyGoodsOutDetailGoodsDOVO;
import com.yutong.safety.core.db.entity.spot.SafetyValidateEnvironmentDO;
import com.yutong.safety.core.db.mapper.goodsOut.SafetyGoodsOutDetailGoodsDOMapper;
import com.yutong.safety.core.db.mapper.goodsOut.SafetyGoodsOutExceptionDOMapper;
import com.yutong.safety.core.db.mapper.goodsOut.SafetyGoodsOutGoodsExceptionDOMapper;
import com.yutong.safety.core.db.mapper.goodsOut.SafetyGoodsOutProcessDOMapper;
import com.yutong.safety.core.db.mapper.spot.SafetyValidateEnvironmentDOBizMapper;
import com.yutong.safety.core.db.mapper.spot.SafetyValidateEnvironmentDOMapper;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsOutValidDTO;
import com.yutong.safety.core.handler.goodsOut.SafetyGoodsOutValidHandler;
import com.yutong.safety.core.service.SafetyBpmProcessService;
import com.yutong.safety.core.service.SafetyBpmWebServiceService;
import com.yutong.safety.core.service.SafetySysUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author caomg
 * @date 2024-9-26 14:44
 * @description SafetyGoodsOutValidService
 */
@Service
public class SafetyGoodsOutValidService implements SafetyGoodsOutValidHandler {

    @Autowired
    SafetyValidateEnvironmentDOMapper safetyValidateEnvironmentDOMapper;

    @Autowired
    SafetyValidateEnvironmentDOBizMapper safetyValidateEnvironmentDOBizMapper;

    @Autowired
    SafetyGoodsOutProcessDOMapper safetyGoodsOutProcessDOMapper;

    @Autowired
    SafetyGoodsOutDetailGoodsDOMapper safetyGoodsOutDetailGoodsDOMapper;

    @Autowired
    SafetyGoodsOutExceptionDOMapper safetyGoodsOutExceptionDOMapper;

    @Autowired
    SafetyGoodsOutGoodsExceptionDOMapper safetyGoodsOutGoodsExceptionDOMapper;

    @Autowired
    private SafetyBpmWebServiceService safetyBpmWebServiceService;

    @Autowired
    private SafetyBpmProcessService safetyBpmProcessService;

    @Autowired
    private SafetySysUserService safetySysUserService;

    @Override
    public Object getList(SafetyValidateEnvironmentDO en) {
        List<SafetyValidateEnvironmentDO> list = safetyValidateEnvironmentDOBizMapper.selectByExampleList(en);
        Integer total = safetyValidateEnvironmentDOBizMapper.selectByExampleCount(en);

        return new PageVO<>(total, list);
    }

    @Override
    public Object getPageList(SafetyGoodsOutValidDTO en,String token) {
        //出门证异常上报事件的流程前缀
        String goodsOutBpmUrlPre = getBpmPreUrl("safety_goods_out_process");
        en.setGoodsOutBpmUrlPre(goodsOutBpmUrlPre);
        //过滤出厂厂区权限
        String workNo = TokenUtils.getWorkNo(token);
        if(StringUtils.isNotBlank(workNo) && workNo.startsWith("01")){
            workNo = safetySysUserService.employeeConvertWorkNo(workNo);
        }
        String userName = TokenUtils.getUserName(token);
        boolean filterFlag = safetySysUserService.checkRoleByUserName(userName,"超级管理员");
        if(!filterFlag){
            String factoryId = safetySysUserService.getPowerFactory(workNo);
            en.setOutFactoryIds(factoryId);
            en.setCurPersonLogin(userName);
        }
        List<SafetyGoodsOutValidDTO> list = safetyValidateEnvironmentDOBizMapper.getPageList(en);
        long total = safetyValidateEnvironmentDOBizMapper.getPageListCount(en);

        return new PageVO<>(total, list);
    }

    /**
     * 拼接bpm链接
     * @param formId bpm的formId
     * @return 返回值
     */
    public String getBpmPreUrl(String formId){
        Map<String,String> bpmModel = safetyBpmWebServiceService.findBpmModelByFormId(formId);
        if(bpmModel != null){
            return bpmModel.get("form_url")+"?sysId=SAFETY&modelId="+bpmModel.get("model_id")+"&templateFormId="+bpmModel.get("form_id");
        }
        return "";
    }

    @Override
    public void exportExcel(SafetyValidateEnvironmentDO en, OutputStream outputStream) {
        en.setIsExport("Y");
        List<SafetyValidateEnvironmentDO> list = safetyValidateEnvironmentDOBizMapper.selectByExampleList(en);

        EasyExcel.write(outputStream, SafetyValidateEnvironmentDO.class)
                .registerWriteHandler(new ColumnHeightAuto())
                .registerWriteHandler(new ColumnWidthAuto())
                .sheet("Sheet1")
                .doWrite(list);
    }

    @Override
    public Object getDetailInfo(String flowNo,String businessType) {
        SafetyValidateEnvironmentDO en = new SafetyValidateEnvironmentDO();
        en.setFlowNo(flowNo);
        en.setBusinessType(businessType);

        //车辆及货物信息
        en.setCarAndGoodsInfoMap(safetyGoodsOutProcessDOMapper.getCarAndGoodsInfoByProcessId(flowNo));

        //货物明细
        //if("双工厂货车抽查".equals(businessType) || "大宗物资抽查".equals(businessType) || "封箱物资抽查抽查".equals(businessType) || "废料车复磅出厂抽查".equals(businessType)){
        SafetyGoodsOutDetailGoodsDOExample example = new SafetyGoodsOutDetailGoodsDOExample();
        SafetyGoodsOutDetailGoodsDOExample.Criteria criteria = example.createCriteria();
        criteria.andProcessIdEqualTo(flowNo);

        List<SafetyGoodsOutDetailGoodsDO> goodsList = safetyGoodsOutProcessDOMapper.selectByFlowId(flowNo);

        List<SafetyGoodsOutDetailGoodsDOVO> goodsList1 = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(goodsList)){
            goodsList.forEach(ee -> {
                SafetyGoodsOutDetailGoodsDOVO e = new SafetyGoodsOutDetailGoodsDOVO();
                BeanUtils.copyProperties(ee,e);
                goodsList1.add(e);
            });
        }
        en.setGoogsList(goodsList1);
        //}

        return en;
    }

    @Override
    public Object getDetailInfoAll(String processId) {
        return safetyValidateEnvironmentDOBizMapper.getDetailInfoAll(processId);
    }

    @Override
    public Object getGoodsDetail(String processId, String selectContent) {
        return safetyGoodsOutProcessDOMapper.getGoodsDetail(processId,selectContent);
    }

    @Override
    public Object saveDetailInfo(SafetyValidateEnvironmentDO en) {
        //gwxiexj factoryId=null区分出门证提交的验证,前端默认为null，但是重新选择厂区后，ID会被重新赋值
        en.setFactoryId(null);
        boolean isException = false;
        String businessType = en.getBusinessType();
        String flowNo = en.getFlowNo();

        if (StringUtils.isBlank(businessType)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"业务类型不能为空！");
        }
        if (StringUtils.isBlank(flowNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"流程编号不能为空！");
        }
        //先根据流程id查询流程详情
        SafetyGoodsOutProcessDO processDTO = safetyGoodsOutProcessDOMapper.selectByPrimaryKey(flowNo);

        en.setCreator("");
        en.setCreateTime(new Date());
        en.setUpdatePersonNo("");
        en.setUpdateTime(new Date());

        if(1==en.getIsBlacklist()){en.setBlackTime(DateUtils.format(new Date(),1));}

        safetyValidateEnvironmentDOMapper.insertSelective(en);
        LogUtils.info("SafetyGoodsOutValidService[saveDetailInfo]:"+en);

        //保存业务验证和物资异常的关联关系
        List<SafetyGoodsOutDetailGoodsDOVO> googsList = en.getGoogsList();
        if (CollectionUtils.isEmpty(googsList)) {
            return null;
        }

        if(!ObjectUtils.isEmpty(en.getExceptionDo())){
            en.getExceptionDo().setCreateTime(new Date());
            safetyGoodsOutExceptionDOMapper.insertSelective(en.getExceptionDo());

            for(SafetyGoodsOutDetailGoodsDOVO e:googsList){
                String isAccord = e.getIsAccord();
                if("0".equals(isAccord) && !isException){
                    isException = true;
                }

                SafetyGoodsOutGoodsExceptionDO safetyGoodsOutGoodsExceptionDO = new SafetyGoodsOutGoodsExceptionDO();
                safetyGoodsOutGoodsExceptionDO.setProcessId(flowNo);
                safetyGoodsOutGoodsExceptionDO.setGoodsId(e.getId());
                safetyGoodsOutGoodsExceptionDO.setIsAccord(isAccord);
                safetyGoodsOutGoodsExceptionDO.setValidId(en.getId());
                safetyGoodsOutGoodsExceptionDO.setExceptionId(en.getExceptionDo().getId());

                safetyGoodsOutGoodsExceptionDOMapper.insertSelective(safetyGoodsOutGoodsExceptionDO);
            };

            processDTO.setExceptionDesc(en.getExceptionDo().getExceptionType());
            processDTO.setExceptionRemark(en.getExceptionDo().getExceptionDesc());
        }


        //处理流程节点
        //更新流程审批信息
        processDTO.setReleaseEmp(en.getLoginName());
        processDTO.setReleaseEmpEip(en.getLoginNameEip());
        processDTO.setReleasePhone(en.getLoginNamePhone());
        processDTO.setReleaseEmpNo(en.getLoginNameCode());
        processDTO.setReleaseTime(DateUtils.format(new Date(),2));
        processDTO.setReleaseStatus(isException?"4":"1");
        processDTO.setIsTimeOut("0");
        safetyGoodsOutProcessDOMapper.updateByPrimaryKeySelective(processDTO);

        //更新流程的审批
        safetyBpmProcessService.approveProcessByTaskId(processDTO.getId(),
                processDTO.getFlowId(),
                "goods_outsider",
                "safety_goods_out_process",
                en.getLoginNameEip(),
                "同意");

        return null;
    }

    @Override
    public void updateDetails(SafetyValidateEnvironmentDO en) {
        en.setUpdateTime(new Date());
        safetyValidateEnvironmentDOMapper.updateByPrimaryKeySelective(en);
    }
}
