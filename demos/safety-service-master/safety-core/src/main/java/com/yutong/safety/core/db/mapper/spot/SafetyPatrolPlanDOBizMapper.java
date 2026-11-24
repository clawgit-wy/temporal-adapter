package com.yutong.safety.core.db.mapper.spot;

import com.yutong.safety.common.param.web.spot.PatrolPlanListParam;
import com.yutong.safety.common.param.web.spot.PatrolTaskListParam;
import com.yutong.safety.common.vo.web.spot.PatrolPlanVO;
import com.yutong.safety.core.db.entity.spot.SafetyPatrolTaskDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SafetyPatrolPlanDOBizMapper {

    /**
     * 查询列表
     * @param param 入参
     * @return 返回值
     */
    public List<PatrolPlanVO> selectList(@Param("param") PatrolPlanListParam param);

    /**
     * 查询列表
     * @param param 入参
     * @return 返回值
     */
    public long selectCount(@Param("param") PatrolPlanListParam param);

    /**
     * 删除任务的工厂信息
     * @param planId 计划ID
     */
    public void deleteByPlanId(@Param("planId")Integer planId);

    /**
     * 批量添加
     * @param linkList 关系集合
     */
    public void batchInsert(@Param("linkList") List<Map<String,Object>> linkList);

    /**
     *
     * @param param
     * @return
     */
    List<SafetyPatrolTaskDO> selectMaxTask(PatrolTaskListParam param);

    /**
     *
     * @param list
     * @return
     */
    int batchInsertTask(@Param("list") List<SafetyPatrolTaskDO> list);

    String selectRoleUser(@Param("param") Map<String,String> param);

    //查询生效的计划
    List<PatrolPlanVO> getInUsePositionPlan(Map<String,Object> param);
}