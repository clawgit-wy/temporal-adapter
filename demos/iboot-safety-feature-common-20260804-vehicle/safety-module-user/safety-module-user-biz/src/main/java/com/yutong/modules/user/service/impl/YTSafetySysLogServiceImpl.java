package com.yutong.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.user.entity.YTSafetySysLog;
import com.yutong.modules.user.mapper.YTSafetySysLogMapper;
import com.yutong.modules.user.api.YTSafetySysLogService;
import com.yutong.modules.user.vo.YTSafetySysLogVO;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YTSafetySysLogServiceImpl extends ServiceImpl<YTSafetySysLogMapper, YTSafetySysLog> implements YTSafetySysLogService {

    @Autowired YTSafetySysLogMapper ytSafetySysLogMapper;

    @Override
    public IPage<YTSafetySysLogVO> queryPage(Page<YTSafetySysLogVO> page, LambdaQueryWrapper queryWrapper) throws JeecgBootException {

        return ytSafetySysLogMapper.queryPage(page,queryWrapper);
    }
}
