package com.yutong.safety.core.db.mapper;

import com.yutong.safety.core.db.entity.SafetyBlackMessageDO;
import com.yutong.safety.core.dto.BlackMessageDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyBlackManageDOBizMapper extends SafetyBlackMessageDOMapper {

    /**
     * 批量添加
     * @param blackList
     */
    public void batchInsert(@Param("blackList") List<SafetyBlackMessageDO> blackList);

    /**
     * 批量修改
     * @param blackList 修改
     */
    void batchUpdate(@Param("list") List<SafetyBlackMessageDO> blackList);


    void batchUpdateForEhr(@Param("list") List<SafetyBlackMessageDO> blackList);

    void batchUpdatePhoto(@Param("list") List<SafetyBlackMessageDO> blackList);

    /**
     * 获取数据
     * @param idCardList 修改
     */
    List<SafetyBlackMessageDO> selectListByIdCard(@Param("list") List<String> idCardList);

    /**
     * 根据身份证号获取员工离职前的员工类型和任职部门
     * @param idCardList 身份证号
     * @return 返回数据集合
     */
    List<BlackMessageDto> selectBlackEmpInfo(@Param("list") List<String> idCardList);

    List<String> querySelect();

    Integer selectMaxId();
}