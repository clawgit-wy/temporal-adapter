package com.yutong.modules.risk.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.risk.entity.YTSafetyShiftSchedule;
import com.yutong.modules.risk.vo.ShiftScheduleImportVO;
import com.yutong.modules.risk.vo.ShiftSchedulePageVO;
import com.yutong.modules.risk.web.ShiftScheduleAddParam;
import com.yutong.modules.risk.web.ShiftScheduleListParam;

import java.util.List;

/**
 * @Description: 排班表 Service 接口
 * @Author: jeecg-boot
 * @Date: 2026-07-31
 * @Version: V1.0
 */
public interface IYTSafetyShiftScheduleService extends IService<YTSafetyShiftSchedule> {

    /**
     * 分页查询
     * @param page 分页对象
     * @param param 查询参数
     * @return 分页结果
     */
    IPage<ShiftSchedulePageVO> pageList(Page<ShiftSchedulePageVO> page, ShiftScheduleListParam param);

    /**
     * 查询列表
     * @param param 查询参数
     * @return 列表
     */
    List<ShiftSchedulePageVO> selectList(ShiftScheduleListParam param);

    /**
     * 根据 ID 查询（关联员工表查询姓名、岗位）
     * @param id 主键
     * @return VO
     */
    ShiftSchedulePageVO queryById(Long id);

    /**
     * 新增
     * @param param 新增参数
     * @return 新增记录的主键ID
     */
    Long save(ShiftScheduleAddParam param);

    /**
     * 修改
     * @param param 修改参数
     * @return 修改结果
     */
    int update(ShiftScheduleAddParam param);

    /**
     * 删除（逻辑删除）
     * @param id 主键
     * @return 删除结果
     */
    int deleteById(Long id);


    /**
     * 批量导入
     * @param list 导入数据列表
     * @return 成功导入条数
     */
    int importData(List<ShiftScheduleImportVO> list);
}
