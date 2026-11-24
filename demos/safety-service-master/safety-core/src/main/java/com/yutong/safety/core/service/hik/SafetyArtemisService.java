package com.yutong.safety.core.service.hik;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yutong.safety.common.constants.HikMiddleTableSqlEnum;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.Base64ImageDecoder;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.web.HkMiddleTableVO;
import com.yutong.safety.core.db.entity.dict.SafetySystemParamDO;
import com.yutong.safety.core.db.hikmapper.hikmiddle.SafetyHikMiddleDOMapper;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.handler.SafetyInterfaceLogHandler;
import com.yutong.safety.core.service.dict.SafetySystemParamService;
import com.yutong.safety.core.service.hot.SafetyHotWorkService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 海康接口
 */
@Service
public class SafetyArtemisService {

    private static final Integer LARGE_NUM = 2000;

    @Value("${artemis.group.single.addition.url}")
    private String groupSingleAdditionUrl;

    @Value("${artemis.group.single.update.url}")
    private String groupSingleUpdateUrl;

    @Value("${artemis.group.get.url}")
    private String groupGetUrl;

    @Value("${artemis.group.batch.deletion.url}")
    private String groupBatchDeletionUrl;

    @Value("${artemis.face.single.addition.url}")
    private String faceSingleAdditionUrl;

    @Value("${artemis.face.batch.addition.url}")
    private String faceBatchAdditionUrl;

    @Value("${artemis.face.batch.deletion.url}")
    private String faceBatchDeletionUrl;

    @Value("${artemis.check.oneToMany.url}")
    private String checkOneToManyUrl;

    @Value("${artemis.person.personList.url}")
    private String personListUrl;

    @Value("${artemis.check.oneToOne.url}")
    private String checkOneToOneUrl;

    @Value("${artemis.camera.advance.url}")
    private String cameraAdvanceUrl;

    @Value("${artemis.event.stranger.url}")
    private String eventStrangerUrl;

    @Resource
    private DataSource hikDataSource;

    @Resource
    private SafetyHotWorkService safetyHotWorkService;

    @Resource
    private SafetyInterfaceLogHandler safetyInterfaceLogHandler;

    @Resource
    private SafetyBaseHandler safetyBaseHandler;

    @Resource
    private SafetySystemParamService safetySystemParamService;

    @Resource
    private SafetyHikMiddleDOMapper safetyHikMiddleDOMapper;

    /**
     * 按条件查询人脸分组
     *
     * @return 返回人脸分组唯一标识码
     */
    public String queryFaceGroup(String name) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("name", name);
        String result = null;
        Date startTime = new Date();
        try {
            result = safetyHotWorkService.doPostArtemis(groupGetUrl, jsonBody);
            JSONObject respBody = JSONObject.parseObject(result);
            String code = respBody.getString("code");
            if (!StringUtils.equals(code, "0") || respBody.getJSONArray("data").size() < 1) {
                throw new BizException(ResponseEnum.FAIL.getCode(), respBody.getString("msg"));
            }
            JSONArray data = respBody.getJSONArray("data");
            return data.getJSONObject(0).getString("indexCode");
        } finally {
            safetyInterfaceLogHandler.recordLog("HK_QUERY_FACE_GROUP", groupGetUrl, startTime, JSON.toJSONString(jsonBody), result);
        }
    }

    /**
     * 查询监控点列表v2
     *
     * @return 返回监控点列表
     */
    public String queryCameraAdvance(JSONObject jsonBody) {
        String respBody = null;
        Date startTime = new Date();
        try {
            respBody = safetyHotWorkService.doPostArtemis(cameraAdvanceUrl, jsonBody);
        } finally {
            safetyInterfaceLogHandler.recordLog("HK_QUERY_CAMERA_ADVANCE", cameraAdvanceUrl, startTime, JSON.toJSONString(jsonBody), respBody);
        }
        return respBody;
    }

    /**
     * 按条件查询陌生人事件
     *
     * @return 返回监控点列表
     */
    public String queryEventStranger(JSONObject jsonBody) {
        String respBody = null;
        Date startTime = new Date();
        try {
            respBody = safetyHotWorkService.doPostArtemis(eventStrangerUrl, jsonBody);
        } finally {
            safetyInterfaceLogHandler.recordLog("HK_QUERY_EVENT_STRANGER", eventStrangerUrl, startTime, JSON.toJSONString(jsonBody), respBody);
        }
        return respBody;
    }

    /**
     * 按条件查询人脸分组
     *
     * @return 返回人脸分组唯一标识码
     */
    public String queryFaceGroup(JSONObject jsonBody) {
        String respBody = null;
        Date startTime = new Date();
        try {
            respBody = safetyHotWorkService.doPostArtemis(groupGetUrl, jsonBody);
        } finally {
            safetyInterfaceLogHandler.recordLog("HK_QUERY_FACE_GROUP", groupGetUrl, startTime, JSON.toJSONString(jsonBody), respBody);
        }
        return respBody;
    }

    /**
     * 单个修改人脸分组
     */
    public String updateGroup(JSONObject jsonBody) {
        String respBody = null;
        Date startTime = new Date();
        try {
            respBody = safetyHotWorkService.doPostArtemis(groupSingleUpdateUrl, jsonBody);
        } finally {
            safetyInterfaceLogHandler.recordLog("HK_UPDATE_GROUP", groupSingleUpdateUrl, startTime, JSON.toJSONString(jsonBody), respBody);
        }
        return respBody;
    }

    /**
     * 单个添加人脸分组
     */
    public String additionGroup(JSONObject jsonBody) {
        String respBody = null;
        Date startTime = new Date();
        try {
            respBody = safetyHotWorkService.doPostArtemis(groupSingleAdditionUrl, jsonBody);
        } finally {
            safetyInterfaceLogHandler.recordLog("HK_ADDITION_GROUP", groupSingleAdditionUrl, startTime, JSON.toJSONString(jsonBody), respBody);
        }
        return respBody;
    }

    /**
     * 单个添加人脸
     */
    public String addFace(JSONObject jsonBody) {
        String respBody = null;
        Date startTime = new Date();
        try {
            respBody = safetyHotWorkService.doPostArtemis(faceSingleAdditionUrl, jsonBody);
        } finally {
            safetyInterfaceLogHandler.recordLog("HK_SINGLE_ADD_FACE", faceSingleAdditionUrl, startTime, JSON.toJSONString(jsonBody), respBody);
        }
        return respBody;
    }

    /**
     * 人脸分组1VN检索
     */
    public String checkOneToMany(JSONObject jsonBody, String photo) {
        String respBody = null;
        Date startTime = new Date();
        try {
            respBody = safetyHotWorkService.doPostArtemis(checkOneToManyUrl, jsonBody);
        } finally {
            // 因为入参太大，所以报文中把照片换成了路径
            jsonBody.put("facePicBinaryData", photo);
            safetyInterfaceLogHandler.recordLog("HK_CHECK_ONE_TO_MANY", checkOneToManyUrl, startTime, JSON.toJSONString(jsonBody), respBody);
        }
        return respBody;
    }

    /**
     * 人脸照片1V1检索
     * @param jsonBody 请求报文
     * @param photo 照片
     * @param targetPhoto 目标照片
     */
    public String checkOneToOne(JSONObject jsonBody, String photo, String targetPhoto) {
        String respBody = null;
        Date startTime = new Date();
        try {
            respBody = safetyHotWorkService.doPostArtemis(checkOneToOneUrl, jsonBody);
        } finally {
            // 因为入参太大，所以报文中把照片换成了路径
            jsonBody.put("srcFacePicBinaryData", photo);
            jsonBody.put("distFacePicBinaryData", targetPhoto);
            safetyInterfaceLogHandler.recordLog("HK_CHECK_ONE_TO_ONE", checkOneToOneUrl, startTime, JSON.toJSONString(jsonBody), respBody);
        }
        return respBody;
    }

    /**
     * 查询人员列表
     */
    public String queryPersonList(JSONObject jsonBody) {
        String respBody = null;
        Date startTime = new Date();
        try {
            respBody = safetyHotWorkService.doPostArtemis(personListUrl, jsonBody);
        } finally {
            if (StringUtils.isBlank(jsonBody.toJSONString()) || jsonBody.toJSONString().length() < LARGE_NUM) {
                safetyInterfaceLogHandler.recordLog("HK_QUERY_PERSON_LIST", personListUrl, startTime, JSON.toJSONString(jsonBody), respBody);
            }
        }
        return respBody;
    }

    /**
     * 批量添加人脸
     */
    public String addBatchFace(String body) {
        String respBody = null;
        Date startTime = new Date();
        try {
            respBody = safetyHotWorkService.doPostArtemisV2(faceBatchAdditionUrl, body);
        } finally {
            if (StringUtils.isBlank(body) || body.length() < LARGE_NUM) {
                safetyInterfaceLogHandler.recordLog("HK_BATCH_ADDITION", faceBatchAdditionUrl, startTime, body, respBody);
            }
        }
        return respBody;
    }

    /**
     * 批量删除人脸
     */
    public String deleteBatchFace(JSONObject jsonBody) {
        String respBody = null;
        Date startTime = new Date();
        try {
            respBody = safetyHotWorkService.doPostArtemis(faceBatchDeletionUrl, jsonBody);
        } finally {
            if (StringUtils.isBlank(jsonBody.toJSONString()) || jsonBody.toJSONString().length() < LARGE_NUM) {
                safetyInterfaceLogHandler.recordLog("HK_DELETE_ADDITION", faceBatchDeletionUrl, startTime, JSON.toJSONString(jsonBody), respBody);
            }
        }
        return respBody;
    }


    /**
     * 同步中间表
     * @param list 数据
     * @param tableSqlEnum 类型，表示同步的某一张表
     */
    public void syncHikMiddleTable(List<HkMiddleTableVO> list, HikMiddleTableSqlEnum tableSqlEnum) {
        LogUtils.info("syncHikMiddleTable-数据入库开始---={}", tableSqlEnum);
        try {
            List<List<HkMiddleTableVO>> partition = Lists.partition(list, 900);
            partition.forEach(li -> {
                if (tableSqlEnum.equals(HikMiddleTableSqlEnum.TB_MIDDLE_PERSON)) {
                    int num = safetyHikMiddleDOMapper.batchPersonSave(li);
                    LogUtils.info("人员信息-执行结果---={}", num);
                    num = safetyHikMiddleDOMapper.batchFaceSave(li);
                    LogUtils.info("人脸-执行结果---={}", num);
                } else if (tableSqlEnum.equals(HikMiddleTableSqlEnum.TB_MIDDLE_ORG)) {
                    int num = safetyHikMiddleDOMapper.batchOrgSave(li);
                    LogUtils.info("组织-执行结果---={}", num);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.error("海康同步中间表失败---={}", e.getMessage());
        }
        LogUtils.info("syncHikMiddleTable-结束");
    }
}
