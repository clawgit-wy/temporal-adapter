package com.yutong.modules.user.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.user.entity.YTSafetySysLog;
import com.yutong.modules.user.vo.YTSafetySysLogVO;
import org.apache.ibatis.annotations.Param;


public interface YTSafetySysLogMapper extends BaseMapper<YTSafetySysLog> {

    IPage<YTSafetySysLogVO> queryPage(Page<YTSafetySysLogVO> page,
                                      @Param(Constants.WRAPPER) LambdaQueryWrapper<YTSafetySysLog> queryWrapper);

}
