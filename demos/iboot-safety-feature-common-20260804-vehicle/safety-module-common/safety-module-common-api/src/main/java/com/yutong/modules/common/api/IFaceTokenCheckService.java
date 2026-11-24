package com.yutong.modules.common.api;

import com.yutong.modules.common.dto.DoorIssueDTO;
import com.yutong.modules.common.vo.CrkFaceModifyVO;
import com.yutong.modules.common.vo.DoorIssueResultVO;
import com.yutong.modules.common.vo.FaceIssueResultVO;
import com.yutong.modules.common.vo.FaceModifyResultVO;
import com.yutong.modules.common.vo.FaceTokenCheckResultVO;

public interface IFaceTokenCheckService {

    /**
     * 检查人脸/卡Token状态
     */
    FaceTokenCheckResultVO checkTokenState(Integer tokenType, String token);

    /**
     * 人脸发行
     */
    FaceIssueResultVO faceIssue(CrkFaceModifyVO dto);

    /**
     * 人脸改写
     */
    FaceModifyResultVO faceModify(CrkFaceModifyVO dto);

    /**
     * 卡发行
     */
    DoorIssueResultVO doorIssue(DoorIssueDTO dto);
}
