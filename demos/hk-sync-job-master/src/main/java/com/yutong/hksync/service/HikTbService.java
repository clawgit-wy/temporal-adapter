package com.yutong.hksync.service;

import com.google.common.collect.Lists;
import com.yutong.hksync.mapper.hik.HikTbMapper;
import com.yutong.hksync.vo.HkMiddleTableVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class HikTbService {

    @Resource
    private HikTbMapper hikTbMapper;

    /**
     * 同步中间表
     *
     * @param list 数据
     */
    public void syncHikMiddleTable(List<HkMiddleTableVO> list) {
        log.info("syncHikMiddleTable-数据入库开始---={}", list.size());
        try {
            List<List<HkMiddleTableVO>> partition = Lists.partition(list, 900);
            partition.forEach(li -> {
                int num = hikTbMapper.batchPersonSave(li);
                log.info("人员信息-执行结果---={}", num);
                num = hikTbMapper.batchFaceSave(li);
                log.info("人脸-执行结果---={}", num);
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("海康同步中间表失败---={}", e.getMessage());
        }
        log.info("syncHikMiddleTable-结束");
    }
}
