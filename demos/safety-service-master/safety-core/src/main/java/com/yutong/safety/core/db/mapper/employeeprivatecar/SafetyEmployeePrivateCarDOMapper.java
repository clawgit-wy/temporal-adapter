package com.yutong.safety.core.db.mapper.employeeprivatecar;

import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDOExample;
import java.util.List;

import com.yutong.safety.core.db.entity.employeeprivatecar.vo.SafetyCarInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SafetyEmployeePrivateCarDOMapper {

    long countByExample(SafetyEmployeePrivateCarDOExample example);

    int deleteByExample(SafetyEmployeePrivateCarDOExample example);

    int deleteByPrimaryKey(Integer id);

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
    void batchInsert(@Param("empCarList") List<SafetyEmployeePrivateCarDO> list);
    SafetyEmployeePrivateCarDO getOne(SafetyEmployeePrivateCarDO en);

    /**
     * 入参建议每次只传其中一个，或者不传
     * @param carNo 车牌号
     * @param lockCarNo 锁定的车牌号
     * @return 返回获取到的数据
     */
    List<SafetyCarInfoVO> selectListByGroupEmpNo(@Param("carNo") String carNo, @Param("lockCarNo") String lockCarNo,  @Param("empNoList") List<String> empNoList);
}