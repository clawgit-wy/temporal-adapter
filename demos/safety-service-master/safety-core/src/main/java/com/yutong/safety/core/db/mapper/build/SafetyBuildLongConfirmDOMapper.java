package com.yutong.safety.core.db.mapper.build;

import com.yutong.safety.core.db.entity.build.SafetyBuildLongConfirmDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildLongConfirmDOExample;
import java.util.List;
import com.yutong.safety.core.db.entity.build.param.BuildLongConfirmListParam;
import org.apache.ibatis.annotations.Param;

public interface SafetyBuildLongConfirmDOMapper {
    long countByExample(SafetyBuildLongConfirmDOExample example);

    int deleteByExample(SafetyBuildLongConfirmDOExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SafetyBuildLongConfirmDO record);

    int insertSelective(SafetyBuildLongConfirmDO record);

    List<SafetyBuildLongConfirmDO> selectByExample(SafetyBuildLongConfirmDOExample example);

    SafetyBuildLongConfirmDO selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SafetyBuildLongConfirmDO record, @Param("example") SafetyBuildLongConfirmDOExample example);

    int updateByPrimaryKeySelective(SafetyBuildLongConfirmDO record);

    List<SafetyBuildLongConfirmDO> queryLongTermList(BuildLongConfirmListParam param);

    int insertBatch(List<SafetyBuildLongConfirmDO> list);

    int deleteHistoryConfirm();

    void saveHistory();

    void updateOfHistoryInfo();

    void deleteHistory();
}