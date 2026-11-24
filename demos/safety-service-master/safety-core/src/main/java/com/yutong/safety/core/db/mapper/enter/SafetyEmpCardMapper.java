package com.yutong.safety.core.db.mapper.enter;

import com.yutong.safety.core.db.entity.enter.SafetyEmpCard;
import com.yutong.safety.core.db.entity.enter.search.EmpCardParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyEmpCardMapper{


    String selectMaxVer();

    List<SafetyEmpCard> selectEmpCardDatas(@Param("empNos") List<String> empNos);

    void saveOrUpdate(@Param("list")List<SafetyEmpCard> empCardDatas);

    int updateByPrimary(SafetyEmpCard record);

    int batchUpdate(@Param("empNoList") List<String> empNoList,@Param("record") SafetyEmpCard record);

    List<SafetyEmpCard> selectEmpCardDatasForParam(EmpCardParam param);

    /**
     * 根据员工号获取信息
     * @param empNo 员工号
     * @return
     */
    SafetyEmpCard selectCrkIdByEmpNo(String empNo);

    /**
     * 根据员工号获取信息
     * @param empNo 员工号
     * @return 集合
     */
    List<SafetyEmpCard> selectListCrkIdByEmpNo(@Param("empNoList")List<String> empNo);
}