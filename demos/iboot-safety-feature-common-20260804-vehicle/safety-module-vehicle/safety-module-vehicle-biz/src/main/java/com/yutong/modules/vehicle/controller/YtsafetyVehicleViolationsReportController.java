package com.yutong.modules.vehicle.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.common.dto.BpmProcessParamDTO;
import com.yutong.modules.common.vo.BpmReponseParamsVO;
import com.yutong.modules.vehicle.api.IYtsafetyVehicleViolationsReportService;
import com.yutong.modules.vehicle.entity.YtsafetyVehicleViolationsReport;
import com.yutong.modules.vehicle.param.YtsafetyVehicleViolationsReportAddParam;
import com.yutong.modules.vehicle.param.YtsafetyVehicleViolationsReportEditParam;
import com.yutong.modules.vehicle.param.YtsafetyVehicleViolationsReportQueryParam;
import com.yutong.modules.vehicle.vo.YtsafetyVehicleViolationsReportVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @Description: 车辆违规上报
 * @Author: jeecg-boot
 * @Date:   2026-08-04
 * @Version: V1.0
 */
@Slf4j
@Api(tags="车辆违规上报")
@RestController
@RequestMapping("/ytsafety/vehicle/violationsReport")
public class YtsafetyVehicleViolationsReportController extends JeecgController<YtsafetyVehicleViolationsReport, IYtsafetyVehicleViolationsReportService> {
	@Autowired
	private IYtsafetyVehicleViolationsReportService ytsafetyVehicleViolationsReportService;
	
	/**
	 * 分页列表查询
	 *
	 * @param queryParam
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "车辆违规台账-分页列表查询")
	@ApiOperation(value="车辆违规台账-分页列表查询", notes="车辆违规台账-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(YtsafetyVehicleViolationsReportQueryParam queryParam,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
		Page<YtsafetyVehicleViolationsReport> page = new Page<YtsafetyVehicleViolationsReport>(pageNo, pageSize);
		IPage<YtsafetyVehicleViolationsReportVo> pageList = ytsafetyVehicleViolationsReportService.queryList(page, queryParam);
		return Result.OK(pageList);
	}

//     @AutoLog(value = "车辆违规上报-暂存草稿",operateType = 2)
//     @ApiOperation(value="车辆违规上报-暂存草稿", notes="车辆违规上报-暂存草稿")
//     @PostMapping(value = "/saveVehicleViolations")
//     public Result<String> saveVehicleViolations(@RequestBody YtsafetyVehicleViolationsReportAddParam addParam ) {
//         ytsafetyVehicleViolationsReportService.saveVehicleViolations(addParam);
//         return Result.OK("暂存草稿成功！");
//     }
//
//
//     @AutoLog(value = "车辆违规上报-更新草稿",operateType = 2)
//     @ApiOperation(value="车辆违规上报-更新草稿", notes="车辆违规上报-更新草稿")
//     @PostMapping(value = "/updateVehicleViolations")
//     public Result<String> updateVehicleViolations(@RequestBody YtsafetyVehicleViolationsReportAddParam addParam ) {
//         ytsafetyVehicleViolationsReportService.updateVehicleViolations(addParam);
//         return Result.OK("暂存草稿成功！");
//     }


     @AutoLog(value = "车辆违规上报-提交上报",operateType = 2)
     @ApiOperation(value="车辆违规上报-提交上报", notes="车辆违规上报-提交上报")
     @PostMapping(value = "/submitReport")
     public Result<YtsafetyVehicleViolationsReportVo> submitReport(@RequestBody YtsafetyVehicleViolationsReportAddParam addParam) {
         YtsafetyVehicleViolationsReportVo ytsafetyVehicleViolationsReportVo =new YtsafetyVehicleViolationsReportVo();
         if(addParam.getOpType().equals("0")&& addParam.getId()!=null){
             ytsafetyVehicleViolationsReportVo= ytsafetyVehicleViolationsReportService.updateVehicleViolations(addParam);
         }else if(addParam.getOpType().equals("0")&& addParam.getId()==null){
             ytsafetyVehicleViolationsReportVo= ytsafetyVehicleViolationsReportService.saveVehicleViolations(addParam);
         }else if(addParam.getOpType().equals("1")){
             ytsafetyVehicleViolationsReportVo = ytsafetyVehicleViolationsReportService.submitReport(addParam);
         }
         return Result.OK(ytsafetyVehicleViolationsReportVo);
     }

     @AutoLog(value = "车辆违规上报-提交上报App",operateType = 2)
     @ApiOperation(value="车辆违规上报-提交上报App", notes="车辆违规上报-提交上报App")
     @PostMapping(value = "/submitReportApp")
     public Result<YtsafetyVehicleViolationsReportVo> submitReportApp(YtsafetyVehicleViolationsReportAddParam addParam) {
         YtsafetyVehicleViolationsReportVo ytsafetyVehicleViolationsReportVo = ytsafetyVehicleViolationsReportService.submitReport(addParam);
         return Result.OK(ytsafetyVehicleViolationsReportVo);
     }


     @AutoLog(value = "车辆违规上报-审核",operateType = 3)
     @ApiOperation(value="车辆违规上报-审核", notes="车辆违规上报-审核")
     @PostMapping(value = "/reviewReport")
     public Result<String> reviewReport(@RequestBody YtsafetyVehicleViolationsReportEditParam editParam) {
         ytsafetyVehicleViolationsReportService.reviewReport(editParam);
         return Result.OK("审核修改成功");
     }

     @AutoLog(value = "车辆违规上报-审核App",operateType = 3)
     @ApiOperation(value="车辆违规上报-审核App", notes="车辆违规上报-审核App")
     @PostMapping(value = "/reviewReportApp")
     public Result<String> reviewReportApp(YtsafetyVehicleViolationsReportEditParam editParam) {
         ytsafetyVehicleViolationsReportService.reviewReport(editParam);
         return Result.OK("审核修改成功");
     }

     @ApiOperation(value="车辆违规上报-通过id查询", notes="车辆违规上报-通过id查询")
     @GetMapping(value = "/queryById")
     public Result<YtsafetyVehicleViolationsReportVo> queryById(@RequestParam(name="id",required=true) Long id) {
         YtsafetyVehicleViolationsReportVo ytsafetyVehicleViolationsReportVo = ytsafetyVehicleViolationsReportService.getReportDetail(id);
         return Result.OK(ytsafetyVehicleViolationsReportVo);
     }

    @AutoLog(value = "车辆违规上报-批量删除", operateType = 4)
    @ApiOperation(value="车辆违规上报-批量删除", notes="车辆违规上报-批量删除")
    @DeleteMapping(value = "/removeVehicleViolationsByIds")
    public Result<String> removeVehicleViolationsByIds(@RequestParam(name="ids",required=true) String ids) {
        ytsafetyVehicleViolationsReportService.removeVehicleViolationsByIds(Arrays.asList(ids.split(",")));
        return Result.OK("删除成功！");
    }

    @AutoLog(value = "车辆违规上报-流程操作",operateType=3)
    @ApiOperation(value="车辆违规上报-流程操作", notes="车辆违规上报-流程操作")
    @PostMapping(value = "/processApprove")
    public Result<BpmReponseParamsVO> processApprove(@RequestBody BpmProcessParamDTO processParamDTO) {
        return Result.OK(ytsafetyVehicleViolationsReportService.executeProcess(processParamDTO));
    }

}
