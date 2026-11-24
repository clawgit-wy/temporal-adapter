package com.yutong.safety.core.db.mapper.enter;

import com.yutong.safety.core.db.entity.enter.EnterApproveCrk;
import com.yutong.safety.core.db.entity.enter.SafetyEnterCrk;
import com.yutong.safety.core.db.entity.enter.SafetyEnterDockCrk;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingPer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface IncomingMapper {

    List<SafetyEnterCrk> selectSafetyEnterCrk(@Param("id") String id);
    SafetyEnterDockCrk selectSafetyEnterDockCrk(@Param("id") String id);

    List<SafetyEnterFilingPer> selectEnterFilingPerList(@Param("filingId") String filingId);
    Integer selectDockNum(@Param("fdKey") String fdKey);
    List<EnterApproveCrk> selectEnterApproveList(@Param("fdKey") String fdKey);

    SafetyEnterDockCrk selectSafetyEnterDockCrkNew(@Param("id") String id);
    List<SafetyEnterDockCrk> selectSafetyEnterDockCrkNewByList(@Param("ids") List<String> ids);

    List<EnterApproveCrk> selectEnterApproveList11(@Param("fdKey") String id);
    /**
     *
     * @param id
     * @param enterTimeBegin
     * @return
     */
    SafetyEnterDockCrk selectEnterDockCrkByMap(@Param("id") String id,
                                                     @Param("enterTimeBegin") String enterTimeBegin);

    List<SafetyEnterDockCrk> selectSafetyEnterDockCrkList(@Param("idStr") String idStr);
}
