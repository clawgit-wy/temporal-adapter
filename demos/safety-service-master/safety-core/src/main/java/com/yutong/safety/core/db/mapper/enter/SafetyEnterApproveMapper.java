package com.yutong.safety.core.db.mapper.enter;

import com.yutong.safety.core.db.entity.SafetyEmpowerDeviceDO;
import com.yutong.safety.core.db.entity.enter.SafetyEnterApprove;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterApproveRt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SafetyEnterApproveMapper {

    /**
     * 根据手机号查询所有有效的审批记录
     * @param phone 手机号
     * @return 返回值
     */
    List<SafetyEnterApproveRt> selectApproveByPhone(@Param("phone") String phone);
    /**
     * 验证是否备案
     * */
    List<SafetyEnterApproveRt> checkEnterApprove(@Param("phone") String phone);

    List<SafetyEmpowerDeviceDO> checkEmpowerDevice(Map map);

    Integer updateEnterFilingApprove(SafetyEnterApprove approve);
    Integer insertEnterFilingApprove(SafetyEnterApprove approve);

    Integer insertEnterFilingApproveBatch(List<SafetyEnterApprove> list);

    int deleteEnterFilingApproveAll(@Param("filingId") String filingId);
}
