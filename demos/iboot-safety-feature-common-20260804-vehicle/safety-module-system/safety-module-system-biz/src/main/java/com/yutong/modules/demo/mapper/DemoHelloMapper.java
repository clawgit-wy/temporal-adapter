package com.yutong.modules.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.demo.entity.DemoHelloEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemoHelloMapper extends BaseMapper<DemoHelloEntity> {

}
