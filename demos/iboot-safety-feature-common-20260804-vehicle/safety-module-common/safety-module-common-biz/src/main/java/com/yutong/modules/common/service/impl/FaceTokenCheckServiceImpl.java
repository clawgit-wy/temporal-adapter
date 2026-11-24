package com.yutong.modules.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yutong.modules.common.api.IFaceTokenCheckService;
import com.yutong.modules.common.dto.DoorIssueDTO;
import com.yutong.modules.common.vo.CrkFaceModifyVO;
import com.yutong.modules.common.vo.DoorIssueResultVO;
import com.yutong.modules.common.vo.FaceIssueResultVO;
import com.yutong.modules.common.vo.FaceModifyResultVO;
import com.yutong.modules.common.vo.FaceTokenCheckResultVO;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.HttpFileUtil;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FaceTokenCheckServiceImpl implements IFaceTokenCheckService {

    private static final String CHECK_TOKEN_URL = "http://10.66.230.210:60009/Api/FaceHandSet/CheckTokenState";
    private static final String FACE_ISSUE_URL = "http://10.66.230.210:60009/Api/FaceHandSet/FaceIssue";
    private static final String FACE_MODIFY_URL = "http://10.66.230.210:60009/api/FaceHandSet/FaceModify";
    private static final String DOOR_ISSUE_URL = "http://10.66.230.210:60009/api/FaceHandSet/Doorissue";

    @Override
    public FaceTokenCheckResultVO checkTokenState(Integer tokenType, String token) {
        JSONObject param = new JSONObject();
        param.put("TokenType", tokenType);
        param.put("Token", token);
        String result = HttpFileUtil.sendPost(CHECK_TOKEN_URL, param.toJSONString());
        if (result == null) {
            log.error("检查Token状态失败，请求结果为null，参数：{}", param.toJSONString());
            return null;
        }
        JSONObject resp = JSON.parseObject(result);
        if (resp == null) {
            log.error("检查Token状态失败，响应解析为空，响应：{}", result);
            return null;
        }
        Integer code = resp.getInteger("code");
        if (code == null || code != 200) {
            String msg = resp.getString("msg");
            log.info("检查Token状态失败：{}", msg);
            return null;
        }
        return resp.getObject("result", FaceTokenCheckResultVO.class);
    }

    @Override
    public FaceIssueResultVO faceIssue(CrkFaceModifyVO dto) {
        String result = HttpFileUtil.sendPost(FACE_ISSUE_URL, JSON.toJSONString(dto));
        if (result == null) {
            log.error("人脸发行失败，请求结果为null");
            return null;
        }
        JSONObject resp = JSON.parseObject(result);
        if (resp == null) {
            log.error("人脸发行失败，响应解析为空，响应：{}", result);
            return null;
        }
        return JSON.parseObject(result, FaceIssueResultVO.class);
    }

    @Override
    public FaceModifyResultVO faceModify(CrkFaceModifyVO dto) {
        String result = HttpFileUtil.sendPost(FACE_MODIFY_URL, JSON.toJSONString(dto));
        if (result == null) {
            log.error("人脸改写失败，请求结果为null");
            return null;
        }
        JSONObject resp = JSON.parseObject(result);
        if (resp == null) {
            log.error("人脸改写失败，响应解析为空，响应：{}", result);
            return null;
        }
        return JSON.parseObject(result, FaceModifyResultVO.class);
    }

    @Override
    public DoorIssueResultVO doorIssue(DoorIssueDTO dto) {
        String result = HttpFileUtil.sendPost(DOOR_ISSUE_URL, JSON.toJSONString(dto));
        if (result == null) {
            log.error("卡发行失败，请求结果为null");
            return null;
        }
        JSONObject resp = JSON.parseObject(result);
        if (resp == null) {
            log.error("卡发行失败，响应解析为空，响应：{}", result);
            return null;
        }
        return JSON.parseObject(result, DoorIssueResultVO.class);
    }
}
