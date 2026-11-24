package com.yutong.safety.core.handler;

import com.yutong.safety.common.param.web.RetentionNoticeListParam;
import com.yutong.safety.common.param.web.RetentionNoticeSaveParam;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.web.RetentionNoticeVO;
import com.yutong.safety.core.service.SafetyRetentionNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author gwxiexj
 * @since 2024-04-10 15:37
 */
@Component
public class SafetyRetentionNoticeHandler {

    @Autowired
    private SafetyRetentionNoticeService safetyRetentionNoticeService;

    /**
     *
     * @param param
     * @return 变更条数
     */
    public int saveNotice(RetentionNoticeSaveParam param){
        return safetyRetentionNoticeService.saveOrUpdate(param);
    }

    /**
     * @param param
     * @return
     */
    public PageVO getNoticeList(RetentionNoticeListParam param){
        List<RetentionNoticeVO> list = safetyRetentionNoticeService.pageList(param);
        long total = safetyRetentionNoticeService.pageListCount(param);
        return new PageVO<RetentionNoticeVO>(total, list);
    }

    public int deleteById(Integer noticeId){
        return safetyRetentionNoticeService.deleteById(noticeId);
    }
}
