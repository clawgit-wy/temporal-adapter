package com.yutong.safety.core.db.mapper;

import com.yutong.safety.core.db.entity.SafetyVisitorDO;
import com.yutong.safety.core.db.entity.SafetyVisitorHairpinDO;
import com.yutong.safety.core.dto.VisitorHirpinAppDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SafetyVisitorHairpinDOBizMapper extends SafetyVisitorHairpinDOMapper {

    /**
     * 定时接口更改数据状态
     */
    void updateStates();

    /**
     * 发卡时候，根据卡号，将已发卡的数据全部置为已退卡
     */
    void allSendCardToEnd(@Param("hairpinNo") String hairpinNo);
    void cancelPermissionByPerson(SafetyVisitorHairpinDO safetyVisitorHairpinDO);

    /**
     * 分页列表
     * @param map
     * @return
     */
    List<VisitorHirpinAppDTO> pageList(Map map);

    /**
     * 分页列表总数据
     * @param map
     * @return
     */
    Long pageListCount(Map map);

    void updateByKafka(VisitorHirpinAppDTO en);
    List<SafetyVisitorDO> queryByKafka(VisitorHirpinAppDTO en);
    List<VisitorHirpinAppDTO> queryByKafkaInFactory(VisitorHirpinAppDTO en);
    List<VisitorHirpinAppDTO> queryByKafkaOutFactory(VisitorHirpinAppDTO en);
    void updateZLStates();

    List<SafetyVisitorHairpinDO> selectByPrimaryKeyList(@Param("ids") List<Integer> ids);

    List<SafetyVisitorHairpinDO> selectListByUpdateTime(@Param("start") Date start, @Param("end") Date end);


}