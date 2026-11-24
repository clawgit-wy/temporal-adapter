package com.yutong.modules.bpmcallback;

import com.yutong.modules.bpmcallback.action.NodeAction;
import com.yutong.modules.common.api.IBpmCallBackService;
import com.yutong.modules.common.dto.BpmMethodProcessDTO;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class BpmCallBackServiceImpl implements IBpmCallBackService {

    @Autowired
    private IBpmCallBackFactory callBackFactory;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void processCallBack(BpmMethodProcessDTO event) {
        String actionCode = event.getFunctionId().getFunctionId();
        NodeAction actionService = callBackFactory.getNodeAction(actionCode);
        actionService.execute(event);
    }
}
