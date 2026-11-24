package com.yutong.safety.core.db.mapper.spot;

import com.yutong.safety.common.param.web.spot.SpotCheckPlanListParam;
import com.yutong.safety.common.vo.web.SpotCheckPlanVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SafetySpotCheckPlanDOBizMapper extends SafetySpotCheckPlanDOMapper{

    /**
     * 查询列表
     * @param param
     * @return
     */
    public List<SpotCheckPlanVO> selectList(@Param("param") SpotCheckPlanListParam param);

    /**
     * 查询列表
     * @param param
     * @return
     */
    public long selectCount(@Param("param") SpotCheckPlanListParam param);

    /**
     * 删除任务的工厂信息
     * @param planId
     */
    public void deleteByPlanId(@Param("planId")Integer planId);

    /**
     * 批量添加
     * @param linkList
     */
    public void batchInsert(@Param("linkList") List<Map<String,Object>> linkList);
}