package com.yutong.safety.core.service;

import com.yutong.safety.common.vo.NoticeLogVO;
import com.yutong.safety.core.db.entity.SafetyRetentionInformRecordDO;
import com.yutong.safety.core.db.mapper.SafetyRetentionBizMapper;
import com.yutong.safety.core.db.mapper.SafetyRetentionInformRecordDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gwxiexj
 * @since 2024-04-16 14:56
 */
@Service
public class SafetyRetentionInformLogService {
    @Autowired
    private SafetyRetentionInformRecordDOMapper safetyRetentionInformRecordDOMapper;

    @Autowired
    private SafetyRetentionBizMapper safetyRetentionBizMapper;

    /**
     * 下发日志统计
     * @return
     */
    public List<NoticeLogVO> getNoticeLogReport(){
        return safetyRetentionBizMapper.getNoticeLogReport();
    }

    public void batchInsert(List<SafetyRetentionInformRecordDO> recordList) throws Exception{
        safetyRetentionBizMapper.batchInsertNoticeLog(recordList);
    }
}
