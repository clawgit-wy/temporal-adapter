package com.yutong.safety.core.handler.hik;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yutong.safety.common.constants.HikMiddleTableSqlEnum;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.constants.SafetyHikThresholdEnum;
import com.yutong.safety.common.constants.UserTypeEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.utils.Base64ImageDecoder;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.common.vo.web.HkMiddleTableVO;
import com.yutong.safety.common.vo.web.HkPersonInfoVo;
import com.yutong.safety.core.db.entity.SafetyGroupInfoDO;
import com.yutong.safety.core.db.entity.dict.SafetySystemParamDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikAbnormalEnterDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikGroupDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikMonitorDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikOperationGroupDO;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikMonitorQueryParam;
import com.yutong.safety.core.db.entity.hik.vo.SafetyHikOneToManyVO;
import com.yutong.safety.core.handler.SafetyBaseHandler;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.SafetyGroupService;
import com.yutong.safety.core.service.dict.SafetySystemParamService;
import com.yutong.safety.core.service.hik.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 海康业务逻辑
 */
@Component
public class SafetyArtemisHandler {

    private static final String BLACK_MESSAGE_GROUP = "BLACK_MESSAGE_GROUP";
    private static final String ALL_FACES = "ALL_FACES";
    private static final Integer DEFAULT_SIZE = 4;
    private static final List<String> FACE_GROUP_INDEX_CODE_NAME = Arrays.asList("在职员工", "访客", "备案");

    /**
     * 一级组织
     */
    private static final String LEVEL_ONE_ORG = "50004002";
    private static final String ROOT_ORG_ID = "root000000";
    private static final String BLACK_LIST_ORG_ID = "HDM00001";
    private static final String BLACK_LIST_ORG_NAME = "黑名单";

    @Value("${artemis.regionIndexCode}")
    private String regionIndexCode;

    @Resource
    public SafetyArtemisService safetyArtemisService;

    @Resource
    public SafetyGroupService safetyGroupService;

    @Resource
    public SafetyHikOperationGroupService safetyHikOperationGroupService;

    @Resource
    public SafetyEmployeeMessageService safetyEmployeeMessageService;

    @Resource
    public SafetySystemParamService safetySystemParamService;

    @Resource
    public SafetyBaseHandler safetyBaseHandler;

    @Resource
    private SafetyHikMonitorService safetyHikMonitorService;

    @Resource
    private SafetyHikThresholdService safetyHikThresholdService;

    @Resource
    public SafetyHikGroupHandler safetyHikGroupHandler;

    @Resource(name = "batchExecutorService")
    private ExecutorService consumerExecutor;
    /**
     * 获取分组唯一标识
     *
     * @return 返回分组的唯一标识
     */
    private String queryFaceGroup(String faceGroup) {
        String s = safetyArtemisService.queryFaceGroup(faceGroup);
        if (StringUtils.isNotBlank(s)) {
            return s;
        }
        throw new BizException(ResponseEnum.FAIL.getCode(), "获取分组唯一标识失败，name=" + faceGroup);
    }

    /**
     * 查询监控点列表v2
     * @param type 同步数据类型，0-增量，1-全量, 为空也是增量
     * @param pageNo 页码
     * @return 返回数据集
     */
    public List<SafetyHikMonitorDO> queryCameraAdvance(String type, int pageNo) {
        try {
            JSONArray regionIndexCodes = new JSONArray();
            regionIndexCodes.add(regionIndexCode);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("regionIndexCodes", regionIndexCodes);
            jsonBody.put("isSubRegion", true);
            jsonBody.put("pageNo", pageNo);
            jsonBody.put("pageSize", 1000);
            if (StringUtils.isBlank(type) || "0".equals(type)) {
                String startTime = DateUtils.getShanghaiTime(LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIDNIGHT));
                String endTime = DateUtils.getShanghaiTime(LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX));
                JSONArray value = new JSONArray();
                value.add(startTime);
                value.add(endTime);
                JSONObject expression = new JSONObject();
                expression.put("key", "updateTime");
                expression.put("operator", 5);
                expression.put("values", value);
                JSONArray expressions = new JSONArray();
                expressions.add(expression);
                jsonBody.put("expressions", expressions);
            }
            String result = safetyArtemisService.queryCameraAdvance(jsonBody);
            JSONObject respBody = JSONObject.parseObject(result);
            String code = respBody.getString("code");
            if (!StringUtils.equals(code, "0") || StringUtils.isBlank(respBody.getString("data"))) {
                throw new BizException(ResponseEnum.FAIL.getCode(), respBody.getString("msg"));
            }
            JSONObject data = respBody.getJSONObject("data");
            Integer total = data.getInteger("total");
            Integer pageSize = data.getInteger("pageSize");
            JSONArray list = data.getJSONArray("list");
            List<SafetyHikMonitorDO> resultList = new ArrayList<>(list.toJavaList(SafetyHikMonitorDO.class));
            if (total > (pageNo * pageSize)) {
                resultList.addAll(queryCameraAdvance(type, pageNo + 1));
            }
            return resultList;
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "查询监控点列表失败");
        }
    }

    /**
     * 按条件查询陌生人事件
     * @param pageNo 页码
     * @return 返回数据
     */
    public List<SafetyHikAbnormalEnterDO> queryEventStranger(int pageNo) {
        List<SafetyHikMonitorDO> safetyHikMonitorDOS = safetyHikMonitorService.selectList(new SafetyHikMonitorQueryParam(), false);
        List<String> indexCodes = safetyHikMonitorDOS.stream().map(SafetyHikMonitorDO::getIndexCode).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(indexCodes)) {
            return Lists.newArrayList();
        }

        String startTime = DateUtils.getShanghaiTime(LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIDNIGHT));
        String endTime = DateUtils.getShanghaiTime(LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX));
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("pageNo", pageNo);
            jsonBody.put("pageSize", 1000);
            jsonBody.put("startTime", startTime);
            jsonBody.put("endTime", endTime);
            jsonBody.put("cameraIndexCodes", indexCodes);
            String result = safetyArtemisService.queryEventStranger(jsonBody);
            JSONObject respBody = JSONObject.parseObject(result);
            String code = respBody.getString("code");
            if (!StringUtils.equals(code, "0") || StringUtils.isBlank(respBody.getString("data"))) {
                throw new BizException(ResponseEnum.FAIL.getCode(), respBody.getString("msg"));
            }
            JSONObject data = respBody.getJSONObject("data");
            Integer total = data.getInteger("total");
            Integer pageSize = data.getInteger("pageSize");
            JSONArray list = data.getJSONArray("list");
            List<SafetyHikAbnormalEnterDO> resultList = new ArrayList<>(list.toJavaList(SafetyHikAbnormalEnterDO.class));
            if (total > (pageNo * pageSize)) {
                resultList.addAll(queryEventStranger(pageNo + 1));
            }
            return resultList;
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "按条件查询陌生人抓拍事件失败");
        }
    }

    /**
     * 查询所有分组
     *
     * @return 返回人脸分组唯一标识码
     */
    public List<SafetyHikGroupDO> queryFaceGroup(String name, List<String> indexCodes) {
        try {
            JSONArray objects = new JSONArray();
            objects.add(indexCodes);
            JSONObject jsonBody = new JSONObject();
            if (StringUtils.isNotBlank(name)) {
                jsonBody.put("name", name);
            }
            if (!CollectionUtils.isEmpty(indexCodes)) {
                jsonBody.put("indexCodes", objects);
            }
            String result = safetyArtemisService.queryFaceGroup(jsonBody);
            JSONObject respBody = JSONObject.parseObject(result);
            String code = respBody.getString("code");
            if (!StringUtils.equals(code, "0") || respBody.getJSONArray("data").size() < 1) {
                throw new BizException(ResponseEnum.FAIL.getCode(), respBody.getString("msg"));
            }
            JSONArray data = respBody.getJSONArray("data");
            return data.toJavaList(SafetyHikGroupDO.class);
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "查询所有分组信息失败");
        }
    }

    /**
     * 单个修改人脸分组
     *
     * @return 返回人脸分组唯一标识码
     */
    public Boolean updateGroup(SafetyHikGroupDO groupDO) {
        String msg = "";
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("indexCode", groupDO.getIndexCode());
            jsonBody.put("name", groupDO.getName());
            jsonBody.put("description", groupDO.getDescription());
            String result = safetyArtemisService.updateGroup(jsonBody);
            JSONObject respBody = JSONObject.parseObject(result);
            msg = respBody.getString("msg");
            return respBody.getBoolean("data");
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "修改人脸分组失败" + msg);
        }
    }

    /**
     * 创建分组信息
     */
    public SafetyHikGroupDO additionGroup(String name, String description) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name);
            jsonBody.put("description", description);
            String result = safetyArtemisService.additionGroup(jsonBody);
            JSONObject respBody = JSONObject.parseObject(result);
            String code = respBody.getString("code");
            if (!StringUtils.equals(code, "0") || StringUtils.isBlank(respBody.getString("data"))) {
                throw new BizException(ResponseEnum.FAIL.getCode(), respBody.getString("msg"));
            }
            String data = respBody.getString("data");
            return JsonUtils.parse(data, SafetyHikGroupDO.class);
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "无法创建分组信息");
        }
    }

    /**
     * 人脸分组1VN检索 判断当前人脸是否存在黑名单分组中
     *
     * @param photo 照片
     * @return true：表示存在黑名单，false：不存在
     */
    public Boolean checkOneToMany(String photo, String name, String cardNum, SafetyHikThresholdEnum thresholdEnum) {
        try {
            // 获取配置的阈值
            String value = safetyHikThresholdService.selectValueByCode(thresholdEnum);
            String base64_str = Base64ImageDecoder.imgBase64(photo);
            JSONObject jsonBody = new JSONObject();
            JSONArray objects = new JSONArray();
            objects.add(queryFaceGroup(BLACK_MESSAGE_GROUP));
            jsonBody.put("pageNo", 1);
            jsonBody.put("pageSize", 20);
            jsonBody.put("searchNum", 99);
            jsonBody.put("minSimilarity", Integer.parseInt(value));
            jsonBody.put("facePicBinaryData", base64_str);
            jsonBody.put("faceGroupIndexCodes", objects);
            jsonBody.put("name", name);
            jsonBody.put("certificateType", "111");
            jsonBody.put("certificateNum", cardNum);
            String result = safetyArtemisService.checkOneToMany(jsonBody, photo);
            JSONObject respBody = JSONObject.parseObject(result);
            String code = respBody.getString("code");
            if (!StringUtils.equals(code, "0") || StringUtils.isBlank(respBody.getJSONObject("data").toJSONString())) {
                throw new BizException(ResponseEnum.FAIL.getCode(), respBody.getString("msg"));
            }
            JSONObject data = respBody.getJSONObject("data");
            Integer total = data.getInteger("total");
            return total > 0;
        } catch (Exception e) {
            LogUtils.error("人脸分组1VN检索失败 photo={},name={}, cardNum={}", photo, name, cardNum);
            return false;
        }
    }

    /**
     * 人脸照片1V1检索
     * @param photo 对比照片
     * @param targetPhoto 目标照片
     * @param type 员工类型，
     * @return true 成功； false 失败
     */
    public Boolean checkOneToOne(String photo, String targetPhoto, UserTypeEnum type, SafetyHikThresholdEnum thresholdEnum) {
        try {
            String base64StrOne = Base64ImageDecoder.imgBase64(photo);
            String base64StrTwo = Base64ImageDecoder.imgBase64(targetPhoto);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("srcFacePicBinaryData", base64StrOne);
            jsonBody.put("distFacePicBinaryData", base64StrTwo);
            String result = safetyArtemisService.checkOneToOne(jsonBody, photo, targetPhoto);
            JSONObject respBody = JSONObject.parseObject(result);
            String code = respBody.getString("code");
            if (!StringUtils.equals(code, "0") || StringUtils.isBlank(respBody.getJSONObject("data").toJSONString())) {
                throw new BizException(ResponseEnum.FAIL.getCode(), respBody.getString("msg"));
            }
            JSONObject data = respBody.getJSONObject("data");
            String similarity = data.getString("similarity");
            // 获取配置的阈值
            String value = safetyHikThresholdService.selectValueByCode(thresholdEnum);
            return Integer.parseInt(value) < Integer.parseInt(similarity);
        } catch (Exception e) {
            // 当API接口调取失败时正式员工按照通过处理，外协员工按照未通过，均需要二次校验手动核对
            LogUtils.error("人脸照片1V1检索失败 photo={},targetPhoto={}", photo, targetPhoto);
            return UserTypeEnum.ZS.equals(type);
        }
    }

    /**
     * 查询人员列表
     *
     * @param list 员工工号
     * @return 返回人员ID
     */
    private List<HkPersonInfoVo> queryPersonList(List<String> list) {
        List<List<String>> partition = Lists.partition(list, 1000);
        List<HkPersonInfoVo> resultList = new ArrayList<>();
        partition.forEach(li -> {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("paramName", "jobNo");
            jsonBody.put("paramValue", li);
            String result = safetyArtemisService.queryPersonList(jsonBody);
            JSONObject respBody = JSONObject.parseObject(result);
            String code = respBody.getString("code");
            if (!StringUtils.equals(code, "0")) {
                throw new BizException(ResponseEnum.FAIL.getCode(), respBody.getString("msg"));
            }
            JSONObject data = respBody.getJSONObject("data");
            JSONArray jsonArray = data.getJSONArray("list");
            List<HkPersonInfoVo> infoVos = jsonArray.toJavaList(HkPersonInfoVo.class);
            resultList.addAll(infoVos);
        });
        return resultList;
    }

    /**
     * 定时变更海康分组
     */
    public VO updateHkiGroup() {
        List<SafetyHikOperationGroupDO> groupDOList = safetyHikOperationGroupService.selectList();
        if (CollectionUtils.isEmpty(groupDOList)) {
            return VoUtil.success();
        }

        List<SafetyHikOperationGroupDO> addList = new ArrayList<>();
        List<SafetyHikOperationGroupDO> deleteList = new ArrayList<>();
        List<String> empNoList = new ArrayList<>();
        groupDOList.forEach(li -> {
            if (StringUtils.equals(li.getOperationType(), "ADD")) {
                addList.add(li);
            } else if (StringUtils.equals(li.getOperationType(), "DELETE")) {
                deleteList.add(li);
            }
            empNoList.add(li.getEmpNo());
        });

        return updateHkiGroup(addList, deleteList, empNoList, true);
    }


    public VO updateHkiGroup(List<SafetyHikOperationGroupDO> addList, List<SafetyHikOperationGroupDO> deleteList, List<String> empNoList, Boolean type) {
        if (CollectionUtils.isEmpty(empNoList)) {
            return VoUtil.success();
        }
        // 获取海康人员信息
        List<HkPersonInfoVo> list = queryPersonList(empNoList);
        if (CollectionUtils.isEmpty(list)) {
            return VoUtil.success();
        }
        Map<String, String> collect = list.stream().collect(Collectors.toMap(HkPersonInfoVo::getJobNo, HkPersonInfoVo::getPersonId, (t1, t2) -> t1));

        // 增加分组
        if (!CollectionUtils.isEmpty(addList)) {
            addList.forEach(li -> li.setPersonId(collect.getOrDefault(li.getEmpNo(), "")));
            List<SafetyHikOperationGroupDO> lastAddList = addList.stream().filter(li -> StringUtils.isNotBlank(li.getPersonId())).collect(Collectors.toList());
            Boolean aBoolean = addGroup(lastAddList);
            if (aBoolean && type) {
                List<Integer> idx = lastAddList.stream().map(SafetyHikOperationGroupDO::getId).collect(Collectors.toList());
                safetyHikOperationGroupService.deleteByIds(idx);
            }
        }

        // 删除分组
        if (!CollectionUtils.isEmpty(deleteList)) {
            deleteList.forEach(li -> li.setPersonId(collect.getOrDefault(li.getEmpNo(), "")));
            List<SafetyHikOperationGroupDO> lastDeleteList = deleteList.stream().filter(li -> StringUtils.isNotBlank(li.getPersonId())).collect(Collectors.toList());
            Boolean aBoolean = deleteGroup(lastDeleteList);
            if (aBoolean && type) {
                List<Integer> idx = lastDeleteList.stream().map(SafetyHikOperationGroupDO::getId).collect(Collectors.toList());
                safetyHikOperationGroupService.deleteByIds(idx);
            }
        }

        return VoUtil.success();
    }

    /**
     * 添加分组
     */
    private Boolean addGroup(List<SafetyHikOperationGroupDO> list) {
        try {
            List<List<SafetyHikOperationGroupDO>> partition = Lists.partition(list, 1000);
            partition.forEach(li -> {
                List<JSONObject> collect = li.stream().map(ln -> {
                    JSONObject jb = new JSONObject();
                    jb.put("indexCode", ln.getPersonId());
                    jb.put("faceGroupIndexCode", ln.getFaceGroup());
                    return jb;
                }).collect(Collectors.toList());
                String result = safetyArtemisService.addBatchFace(collect.toString());
                JSONObject respBody = JSONObject.parseObject(result);
                String code = respBody.getString("code");
                Boolean data = respBody.getBoolean("data");
                if (!StringUtils.equals(code, "0") || !data) {
                    throw new BizException(ResponseEnum.FAIL.getCode(), respBody.getString("msg"));
                }
            });
            return true;
        } catch (BizException e) {
            return false;
        }
    }

    /**
     * 删除分组
     */
    private Boolean deleteGroup(List<SafetyHikOperationGroupDO> list) {
        try {
            Map<String, List<SafetyHikOperationGroupDO>> collect = list.stream().collect(Collectors.groupingBy(SafetyHikOperationGroupDO::getFaceGroup));
            collect.forEach((key, value) -> {
                List<List<SafetyHikOperationGroupDO>> partition = Lists.partition(value, 1000);
                partition.forEach(li -> {
                    List<String> personIdList = li.stream().map(SafetyHikOperationGroupDO::getPersonId).collect(Collectors.toList());
                    JSONObject jb = new JSONObject();
                    jb.put("indexCodes", personIdList);
                    jb.put("faceGroupIndexCode", key);
                    String result = safetyArtemisService.deleteBatchFace(jb);
                    JSONObject respBody = JSONObject.parseObject(result);
                    String code = respBody.getString("code");
                    Boolean data = respBody.getBoolean("data");
                    if (!StringUtils.equals(code, "0") || !data) {
                        throw new BizException(ResponseEnum.FAIL.getCode(), respBody.getString("msg"));
                    }
                });
            });
            return true;
        } catch (BizException e) {
            return false;
        }
    }

    /**
     * 照片检索
     * @param imageUrl 照片路径
     */
    public VO searchPhoto(String imageUrl) {
        if (StringUtils.isBlank(imageUrl)) {
            return VoUtil.fail("图片不能为空");
        }

        String base64_str = Base64ImageDecoder.imgBase64(imageUrl);
        JSONArray jsonArray = checkOneToMany(base64_str, SafetyHikThresholdEnum.FACE_QUERY);
        if (jsonArray.isEmpty()) {
            return VoUtil.successData(jsonArray);
        }
        return VoUtil.successData(processJsonArray(jsonArray));
    }

    private JSONArray processJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.isEmpty()) {
            return new JSONArray();
        }
        int size = jsonArray.size();
        int limit = Math.min(DEFAULT_SIZE, size);
        JSONArray result = new JSONArray();
        for (int i = 0; i < limit; i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            JSONObject facePic = json.getJSONObject("facePic");
            if (facePic != null) {
                facePic.put("targetPicData", Base64ImageDecoder.imgBase64(facePic.getString("targetPicUrl")));
                facePic.put("faceData", Base64ImageDecoder.imgBase64(facePic.getString("faceUrl")));
                json.put("facePic", facePic);
            }
            result.add(json);
        }
        return result;
    }

    /**
     * 照片检索
     * @param param
     * @return
     */
    public VO searchPhoto(CommonImportParam param) {
        MultipartFile file = param.getFile();
        String fileName = file.getOriginalFilename();
        if (StringUtils.isBlank(fileName) || !Base64ImageDecoder.isPhoto(fileName)) {
            return VoUtil.fail("文件格式不正确，请上传图片");
        }
        String base64_str;
        try {
            InputStream input = file.getInputStream();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            byte[] data = output.toByteArray();
            base64_str = DatatypeConverter.printBase64Binary(data);
        } catch (IOException e) {
            e.printStackTrace();
            return VoUtil.fail("文件解析失败");
        }
        JSONArray jsonArray = checkOneToMany(base64_str, SafetyHikThresholdEnum.FACE_QUERY);
        if (jsonArray.isEmpty()) {
            return VoUtil.successData(jsonArray);
        }
        return VoUtil.successData(processJsonArray(jsonArray));
    }

    /**
     * 人脸分组1VN检索 获取检索到的数据信息
     * @param photo 照片case64编码信息
     * @return 返回检索到的数据
     */
    public JSONArray checkOneToMany(String photo, SafetyHikThresholdEnum safetyHikThresholdEnum) {
        try {
            // 获取配置的阈值
            String value = safetyHikThresholdService.selectValueByCode(safetyHikThresholdEnum);
            JSONObject jsonBody = new JSONObject();
            JSONArray objects = new JSONArray();
            if (safetyHikThresholdEnum.equals(SafetyHikThresholdEnum.FACE_QUERY)) {
                objects.add(queryFaceGroup(BLACK_MESSAGE_GROUP));
            }
            List<String> groupIndexCodes = safetyHikGroupHandler.selectList(FACE_GROUP_INDEX_CODE_NAME);
            objects.addAll(groupIndexCodes);
            jsonBody.put("pageNo", 1);
            jsonBody.put("pageSize", 1000);
            jsonBody.put("searchNum", 99);
            jsonBody.put("minSimilarity", Integer.parseInt(value));
            jsonBody.put("facePicBinaryData", photo);
            jsonBody.put("faceGroupIndexCodes", objects);
            String result = safetyArtemisService.checkOneToMany(jsonBody, "人脸信息检索");
            JSONObject respBody = JSONObject.parseObject(result);
            String code = respBody.getString("code");
            if (!StringUtils.equals(code, "0") || StringUtils.isBlank(respBody.getJSONObject("data").toJSONString())) {
                throw new BizException(ResponseEnum.FAIL.getCode(), respBody.getString("msg"));
            }
            JSONObject data = respBody.getJSONObject("data");
            return data.getJSONArray("list");
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "人脸信息检索失败");
        }
    }

    /**
     * 多线程调用接口
     */
    public List<SafetyHikOneToManyVO> check(List<SafetyHikOneToManyVO> list) {

        SafetySystemParamDO systemParamDO = safetySystemParamService.getByParamCode("PHOTO_COMPARISON_ENTRY_TODO");
        List<Future<SafetyHikOneToManyVO>> futureTaskList = new ArrayList<>();
        for (SafetyHikOneToManyVO dto : list) {
            Future<SafetyHikOneToManyVO> future = consumerExecutor.submit(() -> {
                Boolean checkResult = this.checkOneToMany(
                        dto.getPhoto(),
                        dto.getName(),
                        dto.getCardNum(),
                        SafetyHikThresholdEnum.ENTRY
                );
                if (checkResult && systemParamDO != null && "1".equals(systemParamDO.getParamValue())) {
                    dto.setResult(dto.getName() + "人脸校验失败");
                }
                return dto;
            });
            futureTaskList.add(future);
        }

        // 3、阻塞收集所有线程校验结果
        List<SafetyHikOneToManyVO> finalDataList = new ArrayList<>();
        for (Future<SafetyHikOneToManyVO> future : futureTaskList) {
            try {
                SafetyHikOneToManyVO resultDto = future.get(30, TimeUnit.SECONDS);
                finalDataList.add(resultDto);
            } catch (Exception e) {
                LogUtils.error("获取校验任务结果失败", e);
            }
        }
        return finalDataList;
    }
}
