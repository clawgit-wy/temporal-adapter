package com.yutong.safety.core.service;

import com.yutong.safety.common.param.web.NoticeLogParam;
import com.yutong.safety.common.vo.NoticeLogVO;
import com.yutong.safety.core.db.mapper.SafetyNoticeLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lukunpeng
 * @time 2024-4-15
 */
@Service
public class SafetyNoticeLogService {

    @Autowired
    private SafetyNoticeLogMapper safetyNoticeLogMapper;

    public List<NoticeLogVO> queryList(NoticeLogParam param) {
        int pageNo = param.getPageNum();
        int pageSize = param.getPageSize();
        return safetyNoticeLogMapper.queryList(param, (pageNo-1) * pageSize, pageSize);
    }

    public long pageListCount(NoticeLogParam param) {
        return safetyNoticeLogMapper.pageListCount(param);
    }
}
