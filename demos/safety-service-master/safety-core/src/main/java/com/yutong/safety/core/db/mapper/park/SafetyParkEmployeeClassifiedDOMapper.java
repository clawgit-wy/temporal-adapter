package com.yutong.safety.core.db.mapper.park;

import com.yutong.safety.core.db.entity.park.ParkEmployeeClassifiedDO;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeeClassifiedParam;
import com.yutong.safety.core.db.entity.park.vo.ParkEmployeeClassifiedVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyParkEmployeeClassifiedDOMapper {

    /**
     * 根据检索条件获取数据信息
     *
     * @param listParam 检索条件
     * @return 返回符合条件的数据集
     */
    List<ParkEmployeeClassifiedVo> selectList(ParkEmployeeClassifiedParam listParam);

    /**
     * 根据检索条件获取数据量
     *
     * @param listParam 检索条件
     * @return 返回符合条件的数据量
     */
    long selectCount(ParkEmployeeClassifiedParam listParam);

    /**
     * 获取有指定厂区权限的员工信息
     *
     * @param deptInfo 指定的厂区列表
     * @return 返回符合条件的员工信息
     */
    List<ParkEmployeeClassifiedDO> selectParkPowerByFactoryName(@Param("list") List<String> deptInfo);

    /**
     * 删除数据
     */
    void deleteInfo();

    /**
     * 删除数据
     */
    void deleteInfoById(@Param("id") String id);

    /**
     * 获取指定员工列表中有特殊厂区楼宇门禁权限的人员
     *
     * @param deptInfo  特殊厂区
     * @param empNoList 指定员工
     * @return 返回员工号 和厂区编码
     */
    List<ParkEmployeeClassifiedDO> selectBuildPowerByEmpNo(@Param("factoryCodeList") List<String> deptInfo, @Param("empNoList") List<String> empNoList);

    /**
     * 批量添加
     *
     * @param lastInfo 数据集
     */
    void batchInsert(@Param("list") List<ParkEmployeeClassifiedDO> lastInfo);

    /**
     * 根据ID获取数据
     *
     * @param id 数据ID
     * @return 对象
     */
    ParkEmployeeClassifiedVo selectInfoById(String id);
}
