package com.yutong.modules.risk.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.risk.entity.YtsafetyHomeworkStage;
import com.yutong.modules.risk.query.YtsafetyHomeworkStageQuery;
import com.yutong.modules.risk.vo.YtsafetyHomeworkStageExportVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 作业环节表
 * @Author: jeecg-boot
 * @Date:   2026-06-30
 * @Version: V1.0
 */
public interface YtsafetyHomeworkStageMapper extends BaseMapper<YtsafetyHomeworkStage> {

    IPage<YtsafetyHomeworkStage> queryPageList(Page<YtsafetyHomeworkStage> page,@Param("query") YtsafetyHomeworkStageQuery query);

    List<YtsafetyHomeworkStageExportVO> queryStageList(@Param("query") YtsafetyHomeworkStageQuery query);

}
