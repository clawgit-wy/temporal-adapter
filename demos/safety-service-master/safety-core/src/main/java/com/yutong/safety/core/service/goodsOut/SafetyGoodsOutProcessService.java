package com.yutong.safety.core.service.goodsOut;

import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.bpm.BpmCurNodeVO;
import com.yutong.safety.core.db.entity.goodsOut.*;
import com.yutong.safety.core.db.entity.goodsOut.param.*;
import com.yutong.safety.core.db.entity.goodsOut.vo.GoodsOutProcessDTO;
import com.yutong.safety.core.db.entity.goodsOut.vo.GoodsOutProcessExtVO;
import com.yutong.safety.core.db.entity.goodsOut.vo.GoodsProcessGooutVO;
import com.yutong.safety.core.db.entity.spot.SafetyValidateEnvironmentDO;
import com.yutong.safety.core.db.entity.spot.SafetyValidateEnvironmentDOExample;
import com.yutong.safety.core.db.mapper.goodsOut.*;
import com.yutong.safety.core.db.mapper.spot.SafetyValidateEnvironmentDOMapper;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SafetyGoodsOutProcessService {

    @Autowired
    private SafetyGoodsOutProcessDOBizMapper safetyGoodsOutProcessDOMapper;

    @Autowired
    private SafetyGoodsOutDetailReceiveDOMapper safetyGoodsOutDetailReceiveDOMapper;

    @Autowired
    private SafetyGoodsOutDetailVehicleDOMapper safetyGoodsOutDetailVehicleDOMapper;

    @Autowired
    private SafetyGoodsOutDetailGoodsDOBizMapper safetyGoodsOutDetailGoodsDOMapper;

    @Autowired
    private SafetyEmployeeMessageService safetyEmployeeMessageService;

    @Autowired
    private SafetyGoodsAuthOwnerCollectDOMapper safetyGoodsAuthOwnerCollectDOMapper;

    @Autowired
    SafetyGoodsOutProcessDOBizMapper safetyGoodsOutProcessDOBizMapper;

    @Autowired
    SafetyGoodsOutExceptionDOMapper safetyGoodsOutExceptionDOMapper;

    @Autowired
    SafetyValidateEnvironmentDOMapper safetyValidateEnvironmentDOMapper;

    private static final String COLLECT_TYPE = "2";

    /**
     * 更新规则
     * @param param 更新入参
     * @return 返回
     * @throws Exception 异常
     */
    public int save(GoodsOutProcessAddParam param) throws Exception{
        SafetyGoodsOutProcessDO process = new SafetyGoodsOutProcessDO();
        BeanUtils.copyProperties(param,process);
        String createDate = param.getCreateTime();
        //设置创建时间
        if(StringUtils.isBlank(createDate)){
            process.setCreateTime(new Date());
        }else{
            if(createDate.length()<19){
                createDate = createDate.substring(0,16)+":00";
            }
            process.setCreateTime(DateUtils.parse(createDate,2));
        }
        //对扩展字段进行保存
        String goodsFileUrl = param.getGoodsFileUrl();
        if(StringUtils.isNotBlank(goodsFileUrl)||param.getAuthId()!=null){
            GoodsOutProcessExtVO extVO = new GoodsOutProcessExtVO();
            extVO.setRelationId(param.getId());
            extVO.setAuthId(String.valueOf(param.getAuthId()));
            extVO.setGoodsFileUrl(param.getGoodsFileUrl());
            try{
                List<GoodsOutProcessExtVO> extList = new ArrayList<>();
                extList.add(extVO);
                safetyGoodsOutProcessDOBizMapper.batchUpdateExt(extList);
            }catch (Exception e){
                LogUtils.error("保存流程扩展信息异常",e);
            }
        }
        //添加流程相关明细表
        List<SafetyGoodsOutDetailGoodsDO> goodsDOS = param.getGoodsList();
        if(!CollectionUtils.isEmpty(goodsDOS)){
            goodsDOS.forEach(element->{
                element.setProcessId(param.getId());
            });
            safetyGoodsOutProcessDOMapper.batchInsertDetailGoods(goodsDOS);
        }
        List<SafetyGoodsOutDetailReceiveDO> receiveDOS = param.getReceiveList();
        if(!CollectionUtils.isEmpty(receiveDOS)){
            List<String> empNoList = receiveDOS.stream().map(e->e.getReceiveEmpNo().trim()).collect(Collectors.toList());
            List<SafetyEmployeeMessageDO> empList = safetyEmployeeMessageService.getListByStaffNo(empNoList);
            Map<String,SafetyEmployeeMessageDO> empMap;
            if(!CollectionUtils.isEmpty(empList)){
                empMap = empList.stream().collect(Collectors.toMap(SafetyEmployeeMessageDO::getEmpNo, Function.identity()));
            }
            else{
                empMap = new HashMap<>();
            }
            receiveDOS.forEach(element->{
                String receiveEmpNo = element.getReceiveEmpNo().trim();
                if(StringUtils.isBlank(element.getReceiveEmpEip()) && StringUtils.isNotBlank(receiveEmpNo)){
                    SafetyEmployeeMessageDO cirDap = empMap.get(receiveEmpNo);
                    if(!ObjectUtils.isEmpty(cirDap)){
                        element.setReceiveEmpEip(StringUtils.isBlank(cirDap.getEmail())?"":cirDap.getEmail().split("@")[0]);
                    }
                }
                element.setProcessId(param.getId());
                //对员工号进行去空格处理
                element.setReceiveEmpNo(receiveEmpNo);
            });
            String receiveEipStr = receiveDOS.stream().map(SafetyGoodsOutDetailReceiveDO::getReceiveEmpEip).collect(Collectors.joining(","));
            process.setReceiveEipStr(receiveEipStr);
            safetyGoodsOutProcessDOMapper.batchInsertDetailReceive(receiveDOS);
        }
        List<SafetyGoodsOutDetailVehicleDO> vehicleDOS = param.getVehicleList();
        if(!CollectionUtils.isEmpty(vehicleDOS)){
            vehicleDOS.forEach(element->{
                element.setProcessId(param.getId());
            });
            safetyGoodsOutProcessDOMapper.batchInsertDetailVehicle(vehicleDOS);
        }
        return safetyGoodsOutProcessDOMapper.insert(process);
    }


    /**
     * 更新规则
     * @param param 更新入参
     * @return 返回
     */
    public int update(GoodsOutProcessUpdateParam param) {
        int updateRows;
        SafetyGoodsOutProcessDO record = new SafetyGoodsOutProcessDO();
        BeanUtils.copyProperties(param,record);
        LogUtils.info("更新规则-update:{}", record);
        updateRows = safetyGoodsOutProcessDOMapper.updateByPrimaryKeySelective(record);
        if(param.getIsUpdateDetail()){
            String processId = param.getId();
            safetyGoodsOutProcessDOMapper.deleteDetailGoodsByProcessId(processId);
            safetyGoodsOutProcessDOMapper.deleteDetailReceiveByProcessId(processId);
            safetyGoodsOutProcessDOMapper.deleteDetailVehicleByProcessId(processId);

            //添加流程相关明细表
            List<SafetyGoodsOutDetailGoodsDO> goodsDOS = param.getGoodsList();
            if(!CollectionUtils.isEmpty(goodsDOS)){
                for (SafetyGoodsOutDetailGoodsDO element : goodsDOS) {
                    element.setProcessId(processId);
                }
                safetyGoodsOutProcessDOMapper.batchInsertDetailGoods(goodsDOS);
            }
            List<SafetyGoodsOutDetailReceiveDO> receiveDOS = param.getReceiveList();
            if(!CollectionUtils.isEmpty(receiveDOS)){
                receiveDOS.forEach(element->{
                    element.setProcessId(processId);
                });
                safetyGoodsOutProcessDOMapper.batchInsertDetailReceive(receiveDOS);
            }
            List<SafetyGoodsOutDetailVehicleDO> vehicleDOS = param.getVehicleList();
            if(!CollectionUtils.isEmpty(vehicleDOS)){
                vehicleDOS.forEach(element->{
                    element.setProcessId(processId);
                });
                safetyGoodsOutProcessDOMapper.batchInsertDetailVehicle(vehicleDOS);
            }
        }
        return updateRows;
    }

    /**
     * 分页查询
     *
     * @param param 查询入参
     * @return 返回
     */
    public List<GoodsOutProcessDTO> getList(GoodsOutProcessListParam param) {
        if(StringUtils.isBlank(param.getOrderByClause())){
            param.setOrderByClause(" create_time desc ");
        }
        return safetyGoodsOutProcessDOMapper.getList(param);
    }

    public SafetyGoodsOutProcessDO getEntityByCondition(String id) {
        return safetyGoodsOutProcessDOMapper.selectByPrimaryKey(id);
    }

    /**
     * 分页总条数
     *
     * @param param 查询入参
     * @return 返回
     */
    public long getListCount(GoodsOutProcessListParam param) {
        return safetyGoodsOutProcessDOMapper.getListCount(param);
    }

    /**
     * 根据ID获取详情
     * @param id 主键ID
     * @return 接收结构
     */
    public GoodsOutProcessDTO getDetailById(String id){
        GoodsOutProcessDTO processDTO = safetyGoodsOutProcessDOMapper.selectByPrimaryKeyBiz(id);
        if(!ObjectUtils.isEmpty(processDTO)){
            //查询事件修改记录
            SafetyGoodsOutDetailReceiveDOExample example = new SafetyGoodsOutDetailReceiveDOExample();
            example.createCriteria().andProcessIdEqualTo(id);
            List<SafetyGoodsOutDetailReceiveDO> receiveDOS = safetyGoodsOutDetailReceiveDOMapper.selectByExample(example);
            processDTO.setReceiveList(receiveDOS);
            //查询巡查处理记录
            SafetyGoodsOutDetailVehicleDOExample example1 = new SafetyGoodsOutDetailVehicleDOExample();
            example1.createCriteria().andProcessIdEqualTo(id);
            List<SafetyGoodsOutDetailVehicleDO> vehicleDOS = safetyGoodsOutDetailVehicleDOMapper.selectByExample(example1);
            processDTO.setVehicleList(vehicleDOS);

            GoodsOutDetailGoodsListParam example2 = new GoodsOutDetailGoodsListParam();
            example2.setProcessId(id);
            List<SafetyGoodsOutDetailGoodsDO> goodsDOS = safetyGoodsOutDetailGoodsDOMapper.selectByCondition(example2);
            processDTO.setGoodsList(goodsDOS);

            //查询异常信息
            SafetyGoodsOutExceptionDOExample safetyGoodsOutExceptionDOExample = new SafetyGoodsOutExceptionDOExample();
            safetyGoodsOutExceptionDOExample.createCriteria().andProcessIdEqualTo(id);
            List<SafetyGoodsOutExceptionDO> safetyGoodsOutExceptionDOS = safetyGoodsOutExceptionDOMapper.selectByExample(safetyGoodsOutExceptionDOExample);
            if(!CollectionUtils.isEmpty(safetyGoodsOutExceptionDOS)){
                processDTO.setExceptionType(safetyGoodsOutExceptionDOS.get(0).getExceptionType());
                processDTO.setExceptionDesc(safetyGoodsOutExceptionDOS.get(0).getExceptionDesc());
            }

            //查询抽查记录
            SafetyValidateEnvironmentDOExample example3 = new SafetyValidateEnvironmentDOExample();
            example3.createCriteria().andFlowNoEqualTo(id);
            List<SafetyValidateEnvironmentDO> environmentDOS = safetyValidateEnvironmentDOMapper.selectByExample(example3);
            if(!CollectionUtils.isEmpty(environmentDOS)){
                processDTO.setBusinessType(environmentDOS.get(0).getBusinessType());
            }
        }
        return processDTO;
    }

    /**
     * 更新流程当前节点信息
     * @param param 入参
     * @return 更新条数
     */
    public int modifyCurDealHandler(GoodsOutNodeUpdateParam param){
        return safetyGoodsOutProcessDOMapper.modifyCurDealHandler(param);
    }

    /**
     * 更新流程当前节点信息
     * @param curNodeVOList 入参
     * @return 更新条数
     */
    public int batchModifyCurDeal(List<BpmCurNodeVO> curNodeVOList){
        return safetyGoodsOutProcessDOMapper.batchModifyCurDeal(curNodeVOList);
    }

    /**
     *
     * @param param-入参
     * @return 返回值
     */
    public int modifyFlowId(GoodsOutNodeUpdateParam param){
        if(StringUtils.isBlank(param.getId())){
            return 0;
        }
        return safetyGoodsOutProcessDOMapper.modifyFlowId(param);
    }

    /**
     * 分页总条数
     *
     * @param example 查询入参
     * @return 返回
     */
    public long countByExample(SafetyGoodsOutProcessDOExample example) {
        return safetyGoodsOutProcessDOMapper.countByExample(example);
    }

    public List<GoodsProcessGooutVO> getProcessGooutList(GoodsProcessGooutListParam listParam){
        return safetyGoodsOutProcessDOMapper.getProcessGooutList(listParam);
    }

    public long getProcessGooutCount(GoodsProcessGooutListParam listParam){
        return safetyGoodsOutProcessDOMapper.getProcessGooutCount(listParam);
    }

    public List<SafetyGoodsOutDetailVehicleDO> getCarList(Map<String, Object> pMap){
        return safetyGoodsOutProcessDOMapper.getCarList(pMap);
    }

    public String getReceiveMark(String processId){
        return safetyGoodsOutProcessDOMapper.getReceiveMark(processId);
    }

    public void saveRecentSelect(GoodsOutProcessAddParam dto){
        String submitNo = dto.getSubmitNo();
        Integer authId = dto.getAuthId();

        SafetyGoodsAuthOwnerCollectDOExample example = new SafetyGoodsAuthOwnerCollectDOExample();
        SafetyGoodsAuthOwnerCollectDOExample.Criteria criteria = example.createCriteria();
        String systemFlag = dto.getSystemFlag();

        if("3".equals(systemFlag)){
            submitNo = dto.getSubmitPhone();
        }
        criteria.andOwnerEmpNoEqualTo(submitNo).andAuthIdEqualTo(authId).andCollectTypeEqualTo("2");

        List<SafetyGoodsAuthOwnerCollectDO> list = safetyGoodsAuthOwnerCollectDOMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(list)) {
            SafetyGoodsAuthOwnerCollectDO en = new SafetyGoodsAuthOwnerCollectDO();
            en.setOwnerEmpNo(submitNo);
            en.setAuthId(authId);
            en.setCollectType(COLLECT_TYPE);
            en.setSourceFrom(systemFlag);
            en.setCreateTime(new Date());
            safetyGoodsAuthOwnerCollectDOMapper.insert(en);
        } else {
            SafetyGoodsAuthOwnerCollectDO en = list.get(0);
            en.setCreateTime(new Date());
            safetyGoodsAuthOwnerCollectDOMapper.updateByPrimaryKey(en);
        }
    }

    public void batchUpdate(List<GoodsOutProcessDTO> list){
        safetyGoodsOutProcessDOBizMapper.batchUpdate(list);
    }

    public void updateByPrimaryKeySelective(SafetyGoodsOutProcessDO en){
        safetyGoodsOutProcessDOMapper.updateByPrimaryKeySelective(en);
    }

    public List<SafetyGoodsOutProcessDO> getListByParamMap(Map<String, Object> pMap){
        return safetyGoodsOutProcessDOBizMapper.getListByParamMap(pMap);
    }

    public List<SafetyGoodsOutDetailReceiveDO> getUnReceiveList(Map<String, Object> pMap){
        return safetyGoodsOutProcessDOBizMapper.getUnReceiveList(pMap);
    }

    public GoodsOutProcessDTO selectByPrimaryKeyBiz(String id){
        return safetyGoodsOutProcessDOBizMapper.selectByPrimaryKeyBiz(id);
    }

    public int batchUpdateExt(List<GoodsOutProcessExtVO> extList){
        return safetyGoodsOutProcessDOBizMapper.batchUpdateExt(extList);
    }

    public void updateByEntity(SafetyGoodsOutProcessDO en){
        safetyGoodsOutProcessDOMapper.updateByPrimaryKeySelective(en);
    }

    public void deleteOutPower(String beginTime, String endTime) {
        safetyGoodsOutProcessDOBizMapper.deleteOutPower(beginTime, endTime);
    }
}
