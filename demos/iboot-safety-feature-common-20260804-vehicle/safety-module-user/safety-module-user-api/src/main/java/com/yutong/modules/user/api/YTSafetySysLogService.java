package com.yutong.modules.user.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.user.entity.YTSafetySysLog;
import com.yutong.modules.user.vo.YTSafetySysLogVO;
import org.jeecg.common.exception.JeecgBootException;

public interface YTSafetySysLogService extends IService<YTSafetySysLog> {

    IPage<YTSafetySysLogVO> queryPage(Page<YTSafetySysLogVO> page, LambdaQueryWrapper queryWrapper) throws JeecgBootException;
}
