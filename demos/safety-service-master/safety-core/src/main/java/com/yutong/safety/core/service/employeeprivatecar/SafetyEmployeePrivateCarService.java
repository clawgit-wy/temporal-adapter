package com.yutong.safety.core.service.employeeprivatecar;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDOExample;
import com.yutong.safety.core.db.entity.employeeprivatecar.param.EmployeeCarAddParam;
import com.yutong.safety.core.db.entity.employeeprivatecar.param.EmployeeCarDeleteParam;
import com.yutong.safety.core.db.entity.employeeprivatecar.vo.SafetyCarInfoVO;
import com.yutong.safety.core.db.mapper.employeeprivatecar.SafetyEmployeePrivateCarDOMapper;
import com.yutong.safety.core.dto.SafetyKeyValueDto;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.handler.employeeprivatecar.SafetyEmployeePrivateCarHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * @author caomg
 * @date 2024-10-29 13:25
 * @description SafetyEmployeePrivateCarService
 */
@Service
public class SafetyEmployeePrivateCarService implements SafetyEmployeePrivateCarHandler {
    @Autowired
    SafetyEmployeePrivateCarDOMapper safetyEmployeePrivateCarDOMapper;
    @Resource
    private SafetyBaseHandler safetyBaseHandler;


    @Override
    public Object pageList(SafetyEmployeePrivateCarDO param) {
        param.setPageFlag(true);
        List<SafetyEmployeePrivateCarDO> list = safetyEmployeePrivateCarDOMapper.selectByParam(param);
        long total = safetyEmployeePrivateCarDOMapper.selectByParamCount(param);
        return new PageVO<>(total, list);
    }

    @Override
    public List<SafetyEmployeePrivateCarDO> selectList(SafetyEmployeePrivateCarDO param) {
        return safetyEmployeePrivateCarDOMapper.selectByParam(param);
    }

    @Override
    public String importData(MultipartFile file, String token) {
        String mes = "导入成功";
        try {
            List<String> list = new ArrayList<>();
            List<SafetyEmployeePrivateCarDO> data = new ArrayList<>();

            EasyExcel.read(file.getInputStream(), SafetyEmployeePrivateCarDO.class, new AnalysisEventListener<SafetyEmployeePrivateCarDO>() {
                @Override
                public void invoke(SafetyEmployeePrivateCarDO en, AnalysisContext analysisContext) {
                    //处理换行符
                    if(StringUtils.isBlank(en.getCarNo()) || list.contains(en.getCarNo())){return;}
                    list.add(en.getCarNo());
                    data.add(en);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                }
            }).sheet().doRead();

            int size = data.size();
            int batchSize = 500;
            for (int i = 0; i < size; i += batchSize) {
                int toIndex = Math.min(size, i + batchSize);
                List<SafetyEmployeePrivateCarDO> batch = data.subList(i, toIndex);
                safetyEmployeePrivateCarDOMapper.batchInsert(batch);
            }
        } catch (Exception e) {
            mes = "导入失败";
            LogUtils.info("SafetyEmployeePrivateCarService[importData]:" + e);
        }
        return mes;
    }

    @Override
    public SafetyEmployeePrivateCarDO getOne(SafetyEmployeePrivateCarDO param) {
        return safetyEmployeePrivateCarDOMapper.getOne(param);
    }

    /**
     * 更新私家车
     * @param param 入参
     */
    public void modifyPrivateCar(EmployeeCarAddParam param){
        SafetyEmployeePrivateCarDO newPrivateCar = new SafetyEmployeePrivateCarDO();
        BeanUtils.copyProperties(param,newPrivateCar);
        if("是".equals(param.getChangeFlag())){
            newPrivateCar.setId(null);
        }
        //注销时，删除旧车辆信息
        if("是".equals(param.getCancelFlag())){
            if(param.getId() != null){
                safetyEmployeePrivateCarDOMapper.deleteByPrimaryKey(param.getId());
            }else if(StringUtils.isNotBlank(param.getOldCarNo()) && StringUtils.isNotBlank(param.getEmployeeNo())){
                SafetyEmployeePrivateCarDOExample example = new SafetyEmployeePrivateCarDOExample();
                example.createCriteria().andCarNoEqualTo(param.getOldCarNo()).andEmployeeNoEqualTo(param.getEmployeeNo());
                safetyEmployeePrivateCarDOMapper.deleteByExample(example);
            }
        }
        //租赁车辆时，到期时间为空则设置为当前时间的1年后到期
        String CAR_RELATIONSHIP_TYPE = "租赁";
        if(CAR_RELATIONSHIP_TYPE.equals(param.getRelationship())){
            String serviceEnd = param.getServerEndDate();
            if(StringUtils.isBlank(serviceEnd)){
                serviceEnd = com.yutong.safety.common.utils.DateUtils.format(DateUtils.addYears(new Date(),1),2);
            }
            newPrivateCar.setServerEndDate(serviceEnd);
        }
        //判断车牌号是注销车辆时，不再执行添加操作
        if(!param.getCarNo().equals(param.getOldCarNo()) || !"是".equals(param.getCancelFlag())){
            List<SafetyEmployeePrivateCarDO> batch = new ArrayList<>();
            batch.add(newPrivateCar);
            safetyEmployeePrivateCarDOMapper.batchInsert(batch);
        }
    }

    /**
     * 获取私家车数据信息
     * @param empCodeList 员工号集合
     * @return 返回数据信息
     */
    public List<SafetyEmployeePrivateCarDO> selectByExample(List<String> empCodeList){
        SafetyEmployeePrivateCarDOExample example = new SafetyEmployeePrivateCarDOExample();
        SafetyEmployeePrivateCarDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFlagEqualTo("0");
        if (!CollectionUtils.isEmpty(empCodeList)){
            criteria.andEmployeeNoIn(empCodeList);
        }
        return safetyEmployeePrivateCarDOMapper.selectByExample(example);
    }

    @Override
    public void deleteCar(EmployeeCarDeleteParam param) {
        // 入参封装
        SafetyKeyValueDto safetyKeyValueDto = new SafetyKeyValueDto();
        safetyKeyValueDto.setKey(param.getCarNo());

        // 1.调用出入口接口注销车牌号
        safetyBaseHandler.carNoLogOut(Collections.singletonList(safetyKeyValueDto));
        if (Constants.Y.equals(safetyKeyValueDto.getValue())) {
            // 2.私家车删除接口
            SafetyEmployeePrivateCarDOExample example = new SafetyEmployeePrivateCarDOExample();
            example.createCriteria().andCarNoEqualTo(param.getCarNo()).andEmployeeNoEqualTo(param.getEmpNo()).andDeleteFlagEqualTo("0");
            SafetyEmployeePrivateCarDO record = new SafetyEmployeePrivateCarDO();
            record.setDeleteFlag("1");
            record.setOtherRemark("删除车辆注销");
            safetyEmployeePrivateCarDOMapper.updateByExampleSelective(record, example);
        } else {
            throw new BizException(ResponseEnum.FAIL.getCode(), "车辆注销失败"+ safetyKeyValueDto.getValueTwo());
        }
    }

    public void deleteCarForCarNo(List<String> carNos) {
        if(CollectionUtils.isEmpty(carNos)){
            return;
        }
        // 2.私家车删除接口
        SafetyEmployeePrivateCarDOExample example = new SafetyEmployeePrivateCarDOExample();
        example.createCriteria().andCarNoIn(carNos).andDeleteFlagEqualTo("0");
        SafetyEmployeePrivateCarDO record = new SafetyEmployeePrivateCarDO();
        //标记删除
        record.setDeleteFlag("1");
        record.setOtherRemark("到期注销");
        safetyEmployeePrivateCarDOMapper.updateByExampleSelective(record, example);
    }

    public List<SafetyCarInfoVO> selectListByGroupEmpNo(String carNo, String lockCarNo, List<String> empNoList) {
        return safetyEmployeePrivateCarDOMapper.selectListByGroupEmpNo(carNo, lockCarNo, empNoList);
    }
}
