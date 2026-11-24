package com.yutong.modules.vehicle.mapper;

import org.apache.ibatis.annotations.Param;
import com.yutong.modules.vehicle.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.modules.vehicle.employeeprivatecar.SafetyEmployeePrivateCarDOExample;
import com.yutong.modules.vehicle.employeeprivatecar.vo.SafetyCarInfoVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SafetyEmployeePrivateCarDOMapper {

    int deleteByPrimaryKey(Integer id);

    int deleteByExample(SafetyEmployeePrivateCarDOExample example);

    void batchInsert(@Param("empCarList") List<SafetyEmployeePrivateCarDO> list);



    long countByExample(SafetyEmployeePrivateCarDOExample example);
    int insert(SafetyEmployeePrivateCarDO record);

    int insertSelective(SafetyEmployeePrivateCarDO record);

    List<SafetyEmployeePrivateCarDO> selectByExample(SafetyEmployeePrivateCarDOExample example);

    SafetyEmployeePrivateCarDO selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SafetyEmployeePrivateCarDO record, @Param("example") SafetyEmployeePrivateCarDOExample example);

    int updateByExample(@Param("record") SafetyEmployeePrivateCarDO record, @Param("example") SafetyEmployeePrivateCarDOExample example);

    int updateByPrimaryKeySelective(SafetyEmployeePrivateCarDO record);

    int updateByPrimaryKey(SafetyEmployeePrivateCarDO record);

    List<SafetyEmployeePrivateCarDO> selectByParam(SafetyEmployeePrivateCarDO en);
    Integer selectByParamCount(SafetyEmployeePrivateCarDO en);

    SafetyEmployeePrivateCarDO getOne(SafetyEmployeePrivateCarDO en);

    /**
     * 入参建议每次只传其中一个，或者不传
     * @param carNo 车牌号
     * @param lockCarNo 锁定的车牌号
     * @return 返回获取到的数据
     */
    List<SafetyCarInfoVO> selectListByGroupEmpNo(@Param("carNo") String carNo, @Param("lockCarNo") String lockCarNo, @Param("empNoList") List<String> empNoList);
}