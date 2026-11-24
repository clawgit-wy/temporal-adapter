package com.yutong.safety.core.service.park;

import com.yutong.safety.core.db.entity.park.ParkEmployeeClassifiedDO;
import com.yutong.safety.core.db.entity.park.param.ParkEmployeeClassifiedParam;
import com.yutong.safety.core.db.entity.park.vo.ParkEmployeeClassifiedVo;
import com.yutong.safety.core.db.mapper.park.SafetyParkEmployeeClassifiedDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SafetyParkEmployeeClassifiedService {

    @Resource
    private SafetyParkEmployeeClassifiedDOMapper safetyParkEmployeeClassifiedDOMapper;

    /**
     * 获取符合条件的数据（根据未进场天数倒叙-无涉密业务关联员工）
     *
     * @param listParam 检索条件
     * @return 返回 无涉密业务关联员工 列表
     */
    public List<ParkEmployeeClassifiedVo> selectList(ParkEmployeeClassifiedParam listParam) {
        if (StringUtils.isBlank(listParam.getOrderByClause())) {
            listParam.setOrderByClause(" not_in_out_day desc ");
        }
        return safetyParkEmployeeClassifiedDOMapper.selectList(listParam);
    }

    /**
     * 分页获取数据时，获取符合条件的数据总数
     *
     * @param listParam 检索条件
     * @return 符合条件的数量
     */
    public long selectCount(ParkEmployeeClassifiedParam listParam) {
        return safetyParkEmployeeClassifiedDOMapper.selectCount(listParam);
    }

    /**
     * 获取员工有研发中心或工程试验中心大门权限的数据信息
     *
     * @param dept_info 特殊部门
     * @return 数据集
     */
    public List<ParkEmployeeClassifiedDO> selectParkPowerByFactoryName(List<String> dept_info) {
        return safetyParkEmployeeClassifiedDOMapper.selectParkPowerByFactoryName(dept_info);
    }

    /**
     * 获取指定员工列表中有特殊厂区楼宇门禁权限的人员
     *
     * @param deptInfo  特殊厂区
     * @param empNoList 指定员工
     * @return 返回员工号
     */
    public List<ParkEmployeeClassifiedDO> selectBuildPowerByEmpNo(List<String> deptInfo, List<String> empNoList) {
        return safetyParkEmployeeClassifiedDOMapper.selectBuildPowerByEmpNo(deptInfo, empNoList);
    }

    /**
     * 删除数据
     */
    public void deleteInfo() {
        safetyParkEmployeeClassifiedDOMapper.deleteInfo();
    }

    /**
     * 删除数据根据ID
     */
    public void deleteInfoById(String id) {
        safetyParkEmployeeClassifiedDOMapper.deleteInfoById(id);
    }

    /**
     * 批量保存数据
     *
     * @param lastInfo 数据集
     */
    public void batchInsertInfo(List<ParkEmployeeClassifiedDO> lastInfo) {
        safetyParkEmployeeClassifiedDOMapper.batchInsert(lastInfo);
    }

    public ParkEmployeeClassifiedVo selectInfoById(String id) {
        return safetyParkEmployeeClassifiedDOMapper.selectInfoById(id);
    }
}
