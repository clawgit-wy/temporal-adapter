package com.yutong.safety.publicKafka;


import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka/heart")
public class KafkaController {

    /**
     * 心跳检查接口
     */
    @RequestMapping("/heartInfo")
    public VO getHeartBeatInfo() {
        return VoUtil.successData(true);
    }
}
