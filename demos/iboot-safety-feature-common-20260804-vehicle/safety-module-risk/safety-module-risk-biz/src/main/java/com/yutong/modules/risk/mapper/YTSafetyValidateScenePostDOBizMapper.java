package com.yutong.modules.risk.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.entity.YTSafetyValidateScenePost;
import com.yutong.modules.risk.web.ValidateScenePostListParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YTSafetyValidateScenePostDOBizMapper extends YTSafetyValidateScenePostDOMapper {

    List<YTSafetyValidateScenePost> selectList(ValidateScenePostListParam param);

    long selectCount(ValidateScenePostListParam param);

    IPage<YTSafetyValidateScenePost> selectPageList(Page<?> page, @Param("param") ValidateScenePostListParam queryParam);
}