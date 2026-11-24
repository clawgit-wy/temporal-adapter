package com.yutong.safety.core.db.mapper.spot;

import com.yutong.safety.common.param.web.spot.ValidateScenePostListParam;
import com.yutong.safety.core.db.entity.spot.SafetyValidateScenePostDO;

import java.util.List;

public interface SafetyValidateScenePostDOBizMapper extends SafetyValidateScenePostDOMapper{

    List<SafetyValidateScenePostDO> selectList(ValidateScenePostListParam param);

    long selectCount(ValidateScenePostListParam param);
}