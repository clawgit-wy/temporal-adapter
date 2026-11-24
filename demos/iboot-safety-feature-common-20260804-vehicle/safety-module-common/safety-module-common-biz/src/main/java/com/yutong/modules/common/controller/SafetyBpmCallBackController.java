package com.yutong.modules.common.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.yutong.modules.common.api.bpm.ISafetyBpmCallBackService;
import com.yutong.modules.common.dto.SafetyBpmCallBackDTO;
import com.yutong.modules.common.dto.SafetyBpmCallBackQueryDTO;
import com.yutong.modules.common.entity.SafetyBpmCallBack;
import com.yutong.modules.common.vo.SafetyBpmCallBackVO;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Slf4j
@Api(tags="BPM回调方法配置")
@RestController
@RequestMapping("/bpm/callback")
public class SafetyBpmCallBackController {
	@Autowired
	private ISafetyBpmCallBackService safetyBpmCallBackService;
	
	/**
	 * 分页列表查询
	 *
	 * @param safetyBpmCallBackQueryDTO
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "BPM回调方法-分页列表查询")
	@ApiOperation(value="BPM回调方法-分页列表查询", notes="BPM回调方法-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SafetyBpmCallBackQueryDTO safetyBpmCallBackQueryDTO,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
        Page<SafetyBpmCallBackVO> page = new Page<SafetyBpmCallBackVO>(pageNo, pageSize);
        IPage<SafetyBpmCallBackVO> pageList = safetyBpmCallBackService.queryPageList(page, safetyBpmCallBackQueryDTO);
        return Result.OK(pageList);
	}

    /**
     * 分页列表查询
     *
     * @param safetyBpmCallBackQueryDTO
     * @return
     */
    @AutoLog(value = "BPM回调方法-列表查询")
    @ApiOperation(value="BPM回调方法-列表查询", notes="BPM回调方法-列表查询")
    @GetMapping(value = "/queryList")
    public Result<?> queryList(SafetyBpmCallBackQueryDTO safetyBpmCallBackQueryDTO) {
        List<SafetyBpmCallBackVO> list = safetyBpmCallBackService.queryList(safetyBpmCallBackQueryDTO);
        return Result.OK(list);
    }
	
	/**
	 * 添加
	 *
	 * @param safetyBpmCallBackDTO
	 * @return
	 */
	@AutoLog(value = "BPM回调方法-添加")
	@ApiOperation(value="BPM回调方法-添加", notes="BPM回调方法-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SafetyBpmCallBackDTO safetyBpmCallBackDTO) {
        SafetyBpmCallBack callBack = safetyBpmCallBackService.addSafetyBpmCall(safetyBpmCallBackDTO);
		return Result.OK(callBack);
	}
	
	/**
	 * 编辑
	 *
	 * @param safetyBpmCallBackDTO
	 * @return
	 */
	@AutoLog(value = "BPM回调方法-编辑")
	@ApiOperation(value="BPM回调方法-编辑", notes="BPM回调方法-编辑")
	@PostMapping(value = "/edit")
	public Result<?> edit(@RequestBody SafetyBpmCallBackDTO safetyBpmCallBackDTO) {
        SafetyBpmCallBack callBack = safetyBpmCallBackService.editSafetyBpmCall(safetyBpmCallBackDTO);
		return Result.OK(callBack);
	}
	
	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "BPM回调方法-通过id删除")
	@ApiOperation(value="BPM回调方法-通过id删除", notes="BPM回调方法-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) Long id) {
        SafetyBpmCallBack callBack = safetyBpmCallBackService.deleteSafetyBpmCall(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "BPM回调方法-通过id查询")
	@ApiOperation(value="BPM回调方法-通过id查询", notes="BPM回调方法-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) Long id) {
        SafetyBpmCallBackVO callBack = safetyBpmCallBackService.queryById(id);
		return Result.OK(callBack);
	}

}
