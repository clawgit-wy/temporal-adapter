package com.yutong.modules.common.controller;

import com.yutong.modules.common.api.IFaceTokenCheckService;
import com.yutong.modules.common.dto.DoorIssueDTO;
import com.yutong.modules.common.dto.FaceTokenCheckDTO;
import com.yutong.modules.common.vo.CrkFaceModifyVO;
import com.yutong.modules.common.vo.DoorIssueResultVO;
import com.yutong.modules.common.vo.FaceIssueResultVO;
import com.yutong.modules.common.vo.FaceModifyResultVO;
import com.yutong.modules.common.vo.FaceTokenCheckResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "门禁设备Token状态检查")
@RestController
@RequestMapping("/api/faceToken")
public class FaceTokenCheckController {

    @Autowired
    private IFaceTokenCheckService faceTokenCheckService;

    @AutoLog(value = "门禁设备Token状态检查")
    @ApiOperation(value = "检查人脸/卡Token状态", notes = "根据TokenType和Token检查门禁设备Token状态")
    @PostMapping("/checkTokenState")
    public Result<FaceTokenCheckResultVO> checkTokenState(@RequestBody FaceTokenCheckDTO dto) {
        FaceTokenCheckResultVO result = faceTokenCheckService.checkTokenState(dto.getTokenType(), dto.getToken());
        if (result != null) {
            return Result.OK(result);
        }
        return Result.error("Token不存在或校验失败");
    }

    @AutoLog(value = "人脸发行")
    @ApiOperation(value = "人脸发行", notes = "向门禁设备发行人脸信息")
    @PostMapping("/faceIssue")
    public Result<FaceIssueResultVO> faceIssue(@RequestBody CrkFaceModifyVO dto) {
        FaceIssueResultVO result = faceTokenCheckService.faceIssue(dto);
        if (result != null) {
            return Result.OK(result);
        }
        return Result.error("人脸发行失败");
    }

    @AutoLog(value = "人脸改写")
    @ApiOperation(value = "人脸改写", notes = "向门禁设备改写人脸信息")
    @PostMapping("/faceModify")
    public Result<FaceModifyResultVO> faceModify(@RequestBody CrkFaceModifyVO dto) {
        FaceModifyResultVO result = faceTokenCheckService.faceModify(dto);
        if (result != null) {
            return Result.OK(result);
        }
        return Result.error("人脸改写失败");
    }

    @AutoLog(value = "卡发行")
    @ApiOperation(value = "卡发行", notes = "向门禁设备发行卡信息")
    @PostMapping("/doorIssue")
    public Result<DoorIssueResultVO> doorIssue(@RequestBody DoorIssueDTO dto) {
        DoorIssueResultVO result = faceTokenCheckService.doorIssue(dto);
        if (result != null && result.getState() != null && result.getState().getIsSucess()) {
            return Result.OK(result);
        }
        String errMsg = "卡发行失败";
        if (result != null && result.getModel() != null) {
            errMsg = result.getModel().getErrMessage() != null ? result.getModel().getErrMessage() : errMsg;
        }
        return Result.error(errMsg);
    }
}
