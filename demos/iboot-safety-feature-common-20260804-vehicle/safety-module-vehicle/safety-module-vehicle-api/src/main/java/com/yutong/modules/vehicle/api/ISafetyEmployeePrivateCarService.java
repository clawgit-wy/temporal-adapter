package com.yutong.modules.vehicle.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.vehicle.dto.SafetyEmployeePrivateCarPersonnelChangeDTO;
import com.yutong.modules.vehicle.dto.SafetyEmployeePrivateCarQueryDTO;
import com.yutong.modules.vehicle.entity.SafetyEmployeePrivateCar;
import com.yutong.modules.vehicle.vo.SafetyEmployeePrivateCarQueryVO;
import com.yutong.modules.vehicle.vo.SafetyEmployeePrivateCarVO;
import com.yutong.modules.vehicle.vo.VerifyCarBrandVO;
import com.yutong.modules.vehicle.employeeprivatecar.param.EmployeeCarAddParam;

import java.util.List;

/**
 * @Description: 员工入厂私家车信息表
 * @Author: jeecg-boot
 * @Date:   2026-08-05
 * @Version: V1.0
 */
public interface ISafetyEmployeePrivateCarService extends IService<SafetyEmployeePrivateCar> {

    /**
     * 查询单个备案车辆信息
     * @param carNo
     * @return
     */
    SafetyEmployeePrivateCarVO queryOne(String carNo);

    /**
     * 校验车辆品牌库
     * @param carBrand
     * @return
     */
    VerifyCarBrandVO verifyCarBrand(String carBrand);

    /**
     * 查询（带分页）
     * @param queryDTO
     * @return
     */
    Page<SafetyEmployeePrivateCarQueryVO> listPage(SafetyEmployeePrivateCarQueryDTO queryDTO);

    /**
     * 更新私家车信息
     * @param param 入参
     * @return 更新条数
     */
    void modifyPrivateCar(EmployeeCarAddParam param);

    /**
     * 查询员工车辆公务车类型
     * @param employeeNo
     * @return
     */
    List<SafetyEmployeePrivateCar> queryOfficialCarByEmployeeNo(String employeeNo);

    /**
     * 人员变更
     * @param queryDTO
     */
    void personnelChange(SafetyEmployeePrivateCarPersonnelChangeDTO queryDTO);
}
