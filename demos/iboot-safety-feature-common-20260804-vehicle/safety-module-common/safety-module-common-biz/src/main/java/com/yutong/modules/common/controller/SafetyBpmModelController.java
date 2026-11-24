package com.yutong.modules.common.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.yutong.modules.common.api.bpm.ISafetyBpmModelService;
import com.yutong.modules.common.dto.SafetyBpmModelDTO;
import com.yutong.modules.common.dto.SafetyBpmModelQueryDTO;
import com.yutong.modules.common.entity.SafetyBpmModel;
import com.yutong.modules.common.vo.ModelTypeEnumVO;
import com.yutong.modules.common.vo.SafetyBpmModelVO;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;

import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Slf4j
@Api(tags="BPM模型配置")
@RestController
@RequestMapping("/bpm/model")
public class SafetyBpmModelController {

	@Autowired
	private ISafetyBpmModelService ytsafetyBpmModelService;
	
	/**
	 * 分页列表查询
	 *
	 * @param safetyBpmModelQueryDTO
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@ApiOperation(value="BPM模型配置-分页列表查询", notes="BPM模型配置-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SafetyBpmModelQueryDTO safetyBpmModelQueryDTO,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
        Page<SafetyBpmModelVO> page = new Page<SafetyBpmModelVO>(pageNo, pageSize);
        IPage<SafetyBpmModelVO> pageList = ytsafetyBpmModelService.queryPageList(page, safetyBpmModelQueryDTO);
		return Result.OK(pageList);
	}

    /**
     * 列表查询
     *
     * @param safetyBpmModelQueryDTO
     * @return
     */
    @ApiOperation(value="BPM模型配置-列表查询", notes="BPM模型配置-列表查询")
    @GetMapping(value = "/queryList")
    public Result<?> queryList(SafetyBpmModelQueryDTO safetyBpmModelQueryDTO) {
        List<SafetyBpmModelVO> list = ytsafetyBpmModelService.queryList(safetyBpmModelQueryDTO);
        return Result.OK(list);
    }
	
	/**
	 * 添加
	 *
	 * @param safetyBpmModelDTO
	 * @return
	 */
	@AutoLog(value = "BPM模型配置-添加")
	@ApiOperation(value="BPM模型配置-添加", notes="BPM模型配置-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SafetyBpmModelDTO safetyBpmModelDTO) {
        SafetyBpmModel model = ytsafetyBpmModelService.addSafetyBpmModel(safetyBpmModelDTO);
		return Result.OK(model);
	}
	
	/**
	 * 编辑
	 *
	 * @param safetyBpmModelDTO
	 * @return
	 */
	@AutoLog(value = "BPM模型配置-编辑")
	@ApiOperation(value="BPM模型配置-编辑", notes="BPM模型配置-编辑")
	@PostMapping(value = "/edit")
	public Result<?> edit(@RequestBody SafetyBpmModelDTO safetyBpmModelDTO) {
        SafetyBpmModel model = ytsafetyBpmModelService.editSafetyBpmModel(safetyBpmModelDTO);
		return Result.OK(model);
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "BPM模型配置-通过id删除")
	@ApiOperation(value="BPM模型配置-通过id删除", notes="BPM模型配置-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) Long id) {
        SafetyBpmModel model = ytsafetyBpmModelService.deleteSafetyBpmModel(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="BPM模型配置-通过id查询", notes="对接BPM系统模块表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) Long id) {
        SafetyBpmModelVO vo = ytsafetyBpmModelService.queryById(id);
		return Result.OK(vo);
	}

}
