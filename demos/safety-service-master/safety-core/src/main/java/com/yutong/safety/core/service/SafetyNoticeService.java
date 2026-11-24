package com.yutong.safety.core.service;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.param.web.NoticeParam;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.SafetyNoticeDO;
import com.yutong.safety.core.db.entity.SafetyNoticeDOExample;
import com.yutong.safety.core.db.mapper.SafetyNoticeDOMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *  create gwcuiyx 2023.07.12
 *  访客须知--service
 */
@Service
public class SafetyNoticeService {
    @Autowired
    private SafetyNoticeDOMapper safetyNoticeDOMapper;
    @Autowired
    private SafetyLogMessageService safetyLogMessageService;

    /**
     * 获取数据内容
     * @return
     */
    public SafetyNoticeDO getNotice() {
        SafetyNoticeDOExample example = new SafetyNoticeDOExample();
        SafetyNoticeDOExample.Criteria criteria = example.createCriteria();
        example.setOrderByClause(" update_time desc ");
        List<SafetyNoticeDO> list = safetyNoticeDOMapper.selectByExampleWithBLOBs(example);
        if(CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 数据存储和更新操作
     * @param param
     */
    public void saveOrUpdate(NoticeParam param, String tocken) {
        boolean flag = true;
        SafetyNoticeDO safetyNoticeDO = new SafetyNoticeDO();
        if(Objects.nonNull(param.getId())) {
            safetyNoticeDO = safetyNoticeDOMapper.selectByPrimaryKey(param.getId());
            if(safetyNoticeDO != null) {
                flag = false;
            }
        }
        safetyNoticeDO.setUpdateTime(new Date());
        safetyNoticeDO.setContent(param.getContent());
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("访客须知");
        if (flag) {
            log.setAbility("添加");
            PreconditonsEx.checkArgument(safetyNoticeDOMapper.insert(safetyNoticeDO) == 1, ResponseEnum.FAIL);
        } else {
            log.setAbility("修改");
            PreconditonsEx.checkArgument(safetyNoticeDOMapper.updateByPrimaryKeyWithBLOBs(safetyNoticeDO) == 1, ResponseEnum.FAIL);
        }
        log.setMessage("/");
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

}
