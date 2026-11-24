package com.yutong.modules.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.demo.entity.DemoHelloEntity;

/**
 * 测试接口
 */
public interface IDemoHelloService extends IService<DemoHelloEntity> {

    String hello();

}
