package com.yutong.modules.demo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.demo.entity.DemoHelloEntity;
import com.yutong.modules.demo.mapper.DemoHelloMapper;
import com.yutong.modules.demo.service.IDemoHelloService;
import org.springframework.stereotype.Service;

/**
 * 测试Service
 */
@Service
public class DemoHelloServiceImpl extends ServiceImpl<DemoHelloMapper, DemoHelloEntity> implements IDemoHelloService {

    @Override
    public String hello() {
        return "hello ，我是 demo 微服务节点!";
    }
}
