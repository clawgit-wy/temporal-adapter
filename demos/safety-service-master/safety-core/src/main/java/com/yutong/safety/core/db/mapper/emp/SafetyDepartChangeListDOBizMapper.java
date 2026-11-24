package com.yutong.safety.core.db.mapper.emp;


import com.yutong.safety.core.db.entity.emp.SafetyDepartChangeListDO;
import com.yutong.safety.core.db.entity.emp.param.DepartChangeListParam;
import com.yutong.safety.core.db.entity.emp.vo.DepartChangeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyDepartChangeListDOBizMapper extends SafetyDepartChangeListDOMapper {
    List<String> getChangeEmpCodeList(SafetyDepartChangeListDO en);

    int saveOrUpdate(@Param("list") List<SafetyDepartChangeListDO> en);

    List<SafetyDepartChangeListDO> selectChangList(DepartChangeListParam listParam);

    List<DepartChangeVO> selectChangEmpList(DepartChangeListParam listParam);

    List<String> selectEmpNoList(@Param("beginTime")String beginTime,@Param("endTime")String endTime);

}