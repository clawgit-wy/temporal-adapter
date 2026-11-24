package com.yutong.modules.vehicle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.common.utils.DateUtils;
import com.yutong.modules.user.api.ISafetyEmployeeMessageService;
import com.yutong.modules.user.entity.SafetyEmployeeMessage;
import com.yutong.modules.vehicle.api.ISafetyEmployeePrivateCarService;
import com.yutong.modules.vehicle.constants.CarPersonnelChangeConstant;
import com.yutong.modules.vehicle.dto.SafetyEmployeePrivateCarPersonnelChangeDTO;
import com.yutong.modules.vehicle.dto.SafetyEmployeePrivateCarQueryDTO;
import com.yutong.modules.vehicle.entity.SafetyEmployeePrivateCar;
import com.yutong.modules.vehicle.entity.YtsafetyBrandVehicle;
import com.yutong.modules.vehicle.mapper.SafetyEmployeePrivateCarDOMapper;
import com.yutong.modules.vehicle.mapper.SafetyEmployeePrivateCarMapper;
import com.yutong.modules.vehicle.mapper.YtsafetyBrandVehicleMapper;
import com.yutong.modules.vehicle.vo.SafetyEmployeePrivateCarQueryVO;
import com.yutong.modules.vehicle.vo.SafetyEmployeePrivateCarVO;
import com.yutong.modules.vehicle.vo.VerifyCarBrandVO;
import org.apache.commons.lang3.StringUtils;
import com.yutong.modules.vehicle.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.modules.vehicle.employeeprivatecar.SafetyEmployeePrivateCarDOExample;
import com.yutong.modules.vehicle.employeeprivatecar.param.EmployeeCarAddParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 员工入厂私家车信息表
 * @Author: jeecg-boot
 * @Date:   2026-08-05
 * @Version: V1.0
 */
@Service
public class SafetyEmployeePrivateCarServiceImpl extends ServiceImpl<SafetyEmployeePrivateCarMapper, SafetyEmployeePrivateCar> implements ISafetyEmployeePrivateCarService {

    @Autowired
    private YtsafetyBrandVehicleMapper ytsafetyBrandVehicleMapper;

    @Autowired
    SafetyEmployeePrivateCarDOMapper safetyEmployeePrivateCarDOMapper;

    @Autowired
    ISafetyEmployeeMessageService employeeMessageService;

    @Override
    public SafetyEmployeePrivateCarVO queryOne(String carNo) {
        LambdaQueryWrapper<SafetyEmployeePrivateCar> queryWrapper = new LambdaQueryWrapper<SafetyEmployeePrivateCar>()
                .eq(SafetyEmployeePrivateCar::getCarNo, carNo)
                .orderByDesc(SafetyEmployeePrivateCar::getPublishTime)
                .last("LIMIT 1");
        SafetyEmployeePrivateCar safetyEmployeePrivateCar = baseMapper.selectOne(queryWrapper);
        SafetyEmployeePrivateCarVO safetyEmployeePrivateCarVO = new SafetyEmployeePrivateCarVO();
        if (safetyEmployeePrivateCar == null){
            return safetyEmployeePrivateCarVO;
        }
        BeanUtils.copyProperties(safetyEmployeePrivateCar, safetyEmployeePrivateCarVO);
        return safetyEmployeePrivateCarVO;
    }

    @Override
    public VerifyCarBrandVO verifyCarBrand(String carBrandName) {
        VerifyCarBrandVO verifyCarBrandVO = new VerifyCarBrandVO();
        LambdaQueryWrapper<YtsafetyBrandVehicle> queryWrapper = new LambdaQueryWrapper<YtsafetyBrandVehicle>()
                .eq(YtsafetyBrandVehicle::getBrandName, carBrandName)
                .eq(YtsafetyBrandVehicle::getDelFlag, "0");
        List<YtsafetyBrandVehicle> ytsafetyBrandVehicles = ytsafetyBrandVehicleMapper.selectList(queryWrapper);
        if (ytsafetyBrandVehicles.isEmpty()) {
            verifyCarBrandVO.setVerifyPass(true);
            return verifyCarBrandVO;
        }else {
            verifyCarBrandVO.setVerifyPass(false);
            return verifyCarBrandVO;
        }
    }

    @Override

    public Page<SafetyEmployeePrivateCarQueryVO> listPage(SafetyEmployeePrivateCarQueryDTO queryDTO) {
        Page<SafetyEmployeePrivateCarQueryVO> page = new Page<>(queryDTO.getPageNo(), queryDTO.getPageSize());
        Page<SafetyEmployeePrivateCarQueryVO> result = baseMapper.listPage(page, queryDTO);
        return result;
    }

    @Override
    public void modifyPrivateCar(EmployeeCarAddParam param) {
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
                serviceEnd = DateUtils.format(DateUtils.addYears(new Date(),1),2);
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

    @Override
    public List<SafetyEmployeePrivateCar> queryOfficialCarByEmployeeNo(String employeeNo) {
        LambdaQueryWrapper<SafetyEmployeePrivateCar> queryWrapper = new LambdaQueryWrapper<SafetyEmployeePrivateCar>()
                .eq(SafetyEmployeePrivateCar::getEmployeeNo, employeeNo)
                .eq(SafetyEmployeePrivateCar::getVehicleType, "公务车辆");
        List<SafetyEmployeePrivateCar> officialCarList = baseMapper.selectList(queryWrapper);
        return officialCarList;
    }

    @Override
    public void personnelChange(SafetyEmployeePrivateCarPersonnelChangeDTO queryDTO) {
        SafetyEmployeeMessage employee = employeeMessageService.getByNo(queryDTO.getChangeEmployeeNo());
        if(employee == null){
            throw new IllegalArgumentException("员工不存在");
        }
        SafetyEmployeePrivateCar safetyEmployeePrivateCar = baseMapper.selectById(queryDTO.getId());
        if(safetyEmployeePrivateCar == null){
            throw new IllegalArgumentException("车辆不存在");
        }
        if (!CarPersonnelChangeConstant.CarType.OFFICIAL_CAR.equals(safetyEmployeePrivateCar.getCarType())){
            throw new IllegalArgumentException("非公务车辆");
        }
        safetyEmployeePrivateCar.setEmployeeName(employee.getEmpName());
        safetyEmployeePrivateCar.setEmployeeNo(employee.getEmpNo());
        baseMapper.updateById(safetyEmployeePrivateCar);
        //缺少出入口车辆注销新增逻辑
    }
}
