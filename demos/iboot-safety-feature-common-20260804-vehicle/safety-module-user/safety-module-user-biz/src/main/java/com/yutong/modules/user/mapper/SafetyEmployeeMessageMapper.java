package com.yutong.modules.user.mapper;

import java.util.List;

import com.yutong.modules.user.vo.SafetyEmployeeMessageVO;
import com.yutong.modules.user.entity.SafetyEmployeeMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: EHR员工信息
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
public interface SafetyEmployeeMessageMapper extends BaseMapper<SafetyEmployeeMessage> {


    List<SafetyEmployeeMessage> selectDistinctPosition();

    int updateByEmpNoOfUnqualifiedRecord(@Param("ids") List<String> ids);

    List<SafetyEmployeeMessage> selectListForUnqualified(@Param("dateStr") String dateStr);

    List<Integer> selectListByLowQuality(@Param("dateStr") String dateStr);

    List<SafetyEmployeeMessage> selectListByLowQualityOfId(@Param("dateStr") String dateStr,@Param("ids") List<Integer> dataIdList);
}
