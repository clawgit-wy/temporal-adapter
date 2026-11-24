package com.yutong.safety.core.db.mapper.spot;

import com.yutong.safety.common.param.app.SpotTaskListAppParam;
import com.yutong.safety.common.param.web.spot.SpotCheckTaskListParam;
import com.yutong.safety.common.vo.app.SpotCheckToDoVO;
import com.yutong.safety.common.vo.web.SpotCheckTaskReportVO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckDeviceInfoDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckRecordDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotCheckTaskDO;
import com.yutong.safety.core.db.entity.spot.SafetySpotLinkRecordRuleDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author gwxiexj
 * @since 2024-05-17 09:16
 */
public interface SafetySpotCheckTaskDOBizMapper {

    int batchInsertTask(@Param("list") List<SafetySpotCheckTaskDO> list);

    int batchInsertRecord(@Param("list")List<SafetySpotCheckRecordDO> list);

    int batchInsertRecordRule(@Param("list")List<SafetySpotLinkRecordRuleDO> list);

    List<SafetySpotCheckTaskDO> selectMaxTask(SpotCheckTaskListParam param);

    List<SafetySpotCheckTaskDO> getCurrentTask(SpotCheckTaskListParam param);

    List<SpotCheckTaskReportVO> selectCheckRecord(@Param("list") List<Integer> list);

    List<SpotCheckTaskReportVO> selectTaskReport(SpotCheckTaskListParam param);

    Long selectTaskReportCount(SpotCheckTaskListParam param);

    /**
     * 设备设施点检代办
     * @param param
     * @return
     */
    List<SpotCheckToDoVO> selectSpotToDo(SpotTaskListAppParam param);

    /**
     * 获取当前未结束的周期任务
     * @param param
     * @return
     */
    List<SpotCheckTaskReportVO> getCurrentTaskList(SpotCheckTaskListParam param);

    /**
     * 批量更新周期任务
     * @param list
     * @return
     */
    int batchUpdateTask(@Param("list") List<SafetySpotCheckTaskDO> list);

    /**
     * 获取周期任务下的设备列表
     * @param paramMap taskId 任务ID deleteFalg 删除状态 0-正常 1-异常
     * @return
     */
    List<SafetySpotCheckDeviceInfoDO> getTaskDevice(Map<String,Object> paramMap);

    /**
     * 获取周期任务下新增的设备列表
     * @param taskId 任务ID
     * @return
     */
    List<SafetySpotCheckDeviceInfoDO> getTaskDeviceUpdateList(@Param("taskId")Integer taskId);
}