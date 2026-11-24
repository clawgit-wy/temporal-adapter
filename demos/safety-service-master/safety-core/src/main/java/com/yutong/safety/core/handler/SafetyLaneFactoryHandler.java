package com.yutong.safety.core.handler;

import com.yutong.safety.common.param.web.LaneFactoryListParam;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyLaneFactoryDOKey;
import com.yutong.safety.core.service.SafetyLaneFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author gwxiexj
 * @since 2024-04-12 15:48
 */
@Component
public class SafetyLaneFactoryHandler {

    @Autowired
    private SafetyLaneFactoryService safetyLaneFactoryService;

    /**
     * 获取工厂列表
     * @param param
     * @return
     * @throws Exception
     */
    public List<String> getFactoryList(LaneFactoryListParam param) throws Exception{
        return safetyLaneFactoryService.getFactoryList(param);
    }

    /**
     * 获取工厂列表-不分页
     * @param param
     * @return
     * @throws Exception
     */
    public PageVO getList(LaneFactoryListParam param) throws Exception{
        List<SafetyLaneFactoryDOKey> list = safetyLaneFactoryService.getList(param);
        long total = safetyLaneFactoryService.pageListCount(param);
        return new PageVO<SafetyLaneFactoryDOKey>(total, list);
    }

    /**
     * 获取工厂列表-分页
     * @param param
     * @return
     * @throws Exception
     */
    public PageVO getPageList(LaneFactoryListParam param) throws Exception{
        List<SafetyLaneFactoryDOKey> list = safetyLaneFactoryService.pageList(param);
        long total = safetyLaneFactoryService.pageListCount(param);
        return new PageVO<SafetyLaneFactoryDOKey>(total, list);
    }
}
