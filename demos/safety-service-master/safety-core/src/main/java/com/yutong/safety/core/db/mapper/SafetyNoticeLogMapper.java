package com.yutong.safety.core.db.mapper;


import com.yutong.safety.common.param.web.NoticeLogParam;
import com.yutong.safety.common.vo.NoticeLogVO;
import com.yutong.safety.core.db.entity.SafetyStaffEvectionDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyNoticeLogMapper {

    List<NoticeLogVO> queryList(@Param("param") NoticeLogParam param, @Param("start") int start, @Param("end") int end);

    long pageListCount(@Param("param")NoticeLogParam param);

}