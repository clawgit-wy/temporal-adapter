package com.yutong.safety.core.handler;

import com.yutong.safety.common.param.web.CarInFactoryListParam;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.RetentionWhiteAddParam;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyRetentionWhiteDO;
import com.yutong.safety.core.service.SafetyRetentionWhiteService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gwxiexj
 * @since 2024-04-11 17:07
 */
@Component
public class SafetyRetentionWhiteHandler {

    @Autowired
    private SafetyRetentionWhiteService safetyRetentionWhiteService;

    /**
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    public Object pageList(CarInFactoryListParam param) {
        List<SafetyRetentionWhiteDO> list = safetyRetentionWhiteService.pageList(param);
        long total = safetyRetentionWhiteService.pageListCount(param);
        return new PageVO<SafetyRetentionWhiteDO>(total, list);
    }

    /**
     * 数据添加接口
     * @param param
     * @return
     */
    public Object add(RetentionWhiteAddParam param) throws Exception{
        safetyRetentionWhiteService.saveOrUpdate(param);
        return null;
    }

    /**
     * 删除-支持批量
     * @param id  ID拼接串,
     * @return 返回值
     * @throws Exception
     */
    public Object deleteById(String id) throws Exception{
        if(StringUtils.isEmpty(id)){
            return 0;
        }
        String[] idArr = id.split(",");
        List<Integer> idList =  Arrays.stream(idArr).map(Integer::valueOf).collect(Collectors.toList());

        return safetyRetentionWhiteService.deleteById(idList);
    }

    /**
     *
     * @param param
     * @return
     */
    public Object importData(CommonImportParam param) throws Exception{
        safetyRetentionWhiteService.importData(param);
        return null;
    }
}
