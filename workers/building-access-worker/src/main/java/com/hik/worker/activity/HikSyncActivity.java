package com.hik.worker.activity;

import com.hik.worker.model.HkMiddleTableVO;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.util.Date;
import java.util.List;

/**
 * 海康数据同步 Activity 接口
 */
@ActivityInterface
public interface HikSyncActivity {
    /**
     * 查询黑名单增量数据
     */
    @ActivityMethod
    List<HkMiddleTableVO> queryBlackMessageList(String startTime, String endTime);

    /**
     * 查询员工台账增量数据
     */
    @ActivityMethod
    List<HkMiddleTableVO> queryEmployeeMessageList(String startTime, String endTime);

    /**
     * 查询外来人员备案增量数据
     */
    @ActivityMethod
    List<HkMiddleTableVO> queryEnterRegisterList(String startTime, String endTime);

    /**
     * 查询访客增量数据
     */
    @ActivityMethod
    List<HkMiddleTableVO> queryVisitorHairpinList(String startTime, String endTime);

    /**
     * 处理人脸图片URL
     * 返回处理后的VO列表(过滤掉无图片的记录)
     */
    @ActivityMethod
    List<HkMiddleTableVO> processFaceImages(List<HkMiddleTableVO> rawList);

    /**
     * 批量写入海康中间库(人员+人脸)
     */
    @ActivityMethod
    int batchSyncToHik(List<HkMiddleTableVO> list, int batchSize);
}
