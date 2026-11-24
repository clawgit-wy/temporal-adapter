package com.yutong.safety.core.db.mapper.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildProcessCloseDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildProcessOpenDO;
import com.yutong.safety.core.db.entity.build.param.BuildProcessAddParam;
import com.yutong.safety.core.db.entity.build.param.BuildProcessListParam;
import com.yutong.safety.core.db.entity.build.param.BuildProcessOpenListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildProcessListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyBuildProcessDOBizMapper{

    List<BuildProcessListVO> selectByExampleWithBLOBs(BuildProcessListParam listParam);

    long countByExampleWithBLOBs(BuildProcessListParam listParam);

    int modifyFlowId(BuildProcessAddParam param);

    int batchInsertBuildOpen(@Param(value="list") List<SafetyBuildProcessOpenDO> buildOpenList);

    int batchInsertBuildClose(@Param(value="list") List<SafetyBuildProcessCloseDO> buildCloseList);

    int batchUpdateOpen(@Param(value="list") List<SafetyBuildProcessOpenDO> buildOpenList);

    int batchUpdateClose(@Param(value="list") List<SafetyBuildProcessCloseDO> buildCloseList);

    List<SafetyBuildProcessOpenDO> selectOpenByExampleBiz(BuildProcessOpenListParam listParam);

    Long countOpenByExampleBiz(BuildProcessOpenListParam listParam);

    List<SafetyBuildProcessCloseDO> selectCloseList(@Param(value="processId")String processId);

    int updateOpenStatusByProcessId(@Param(value="processId")String processId);

    List<SafetyBuildProcessCloseDO> selectCloseListByProcessIds(@Param(value="list") List<String> processIds);
}
