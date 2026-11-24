package com.yutong.safety.core.service.hot;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.core.db.entity.SafetySysRoleDO;
import com.yutong.safety.core.db.entity.hot.FireApproveDOWithBLOBs;
import com.yutong.safety.core.db.entity.hot.SafetyHotWorkLensDO;
import com.yutong.safety.core.db.entity.hot.param.SafetyHotWorkLensParam;
import com.yutong.safety.core.db.entity.hot.param.SafetyMonitorVideoParam;
import com.yutong.safety.core.db.entity.hot.param.SyncFireApproveListParam;
import com.yutong.safety.core.db.entity.hot.vo.SafetyHotWorkLensVO;
import com.yutong.safety.core.db.entity.hot.vo.SyncFireApproveListVO;
import com.yutong.safety.core.db.entity.hot.vo.SyncFireApproveTreeVO;
import com.yutong.safety.core.db.mapper.SafetySysUserDOMapper;
import com.yutong.safety.core.db.mapper.hot.FireApproveDOMapper;
import com.yutong.safety.core.db.mapper.hot.SafetyHotWorkLensDOMapper;
import com.yutong.safety.core.db.mapper.login.LoginMapper;
import com.yutong.safety.core.service.SafetySysUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SafetyHotWorkService {

    @Autowired
    private SafetyHotWorkLensDOMapper safetyHotWorkLensDOMapper;

    @Autowired
    private SafetySysUserDOMapper safetySysUserDOMapper;

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private FireApproveDOMapper fireApproveDOMapper;

    @Autowired
    private SafetySysUserService safetySysUserService;

    @Value("${hot.cameraListUrl}")
    private String cameraListUrl;

    @Value("${hot.cameraGet}")
    private String cameraGet;

    @Value("${hot.playbackURLs}")
    private String playbackURLs;

    @Value("${hot.getPreviewParam}")
    private String getPreviewParam;

    @Value("${hot.host}")
    private String host;

    @Value("${hot.appKey}")
    private String appKey;

    @Value("${hot.appSecret}")
    private String appSecret;

    @Value("${hot.an_wss}")
    private String an_wss;

    public interface Constants {
        String HTTPS = "https://";
        String CONTENT_TYPE = "application/json";
        String PREVIEW_VIDEO_TYPE = "preview";
        String BACK_VIDEO_TYPE = "back";
        String DELETE_LENS_STATUS = "1";
        String SUCCESS_STATUS = "0";
        String URLS_TYPE = "wss";
        String hk_wss = "10.40.0.1:6014";
        String hk_ws =  "10.40.0.1:559";
        List<String> roles = Arrays.asList("超级管理员","管理员","动火管理员");
    }

    @PostConstruct
    public void init() {
        ArtemisConfig artemisConfig =new ArtemisConfig();
        artemisConfig.setHost(host);
        artemisConfig.setAppKey(appKey);
        artemisConfig.setAppSecret(appSecret);
    }

    public List<SafetyHotWorkLensVO> getLensList(SafetyHotWorkLensParam param) {
        int pageSize = param.getPageSize();
        int pageNum = (param.getPageNum()-1) * pageSize <= 0 ? 0 : (param.getPageNum()-1) * pageSize;
        param.setPageNum(pageNum);
        return safetyHotWorkLensDOMapper.getLensList(param);
    }
    /**
     * 获取分页总数
     * @param param
     * @return
     */
    public long pageListCount(SafetyHotWorkLensParam param) {
       return safetyHotWorkLensDOMapper.pageListCount(param);
    }

    /**
     * web 按厂区和动火级别 分组查询
     * @param fireApproveListParam
     * @param tocken
     * @return
     */
    public List<SyncFireApproveTreeVO> getFireApproveList(SyncFireApproveListParam fireApproveListParam,String tocken) {
        // 获取工厂信息,查看用户是否有工厂权限
        Map<String, String> tokenUser = TokenUtils.getTockenUser(tocken);
        if(StringUtils.isBlank(tokenUser.getOrDefault("workNo",""))){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"员工号不能为空！");
        }
        List<SyncFireApproveTreeVO> fireApproveListList = Lists.newArrayList();
        List<SyncFireApproveListVO>fireApproveList = this.getFireApproveList(fireApproveListParam);
        if(CollectionUtils.isEmpty(fireApproveList)){
            return fireApproveListList;
        }
        Map<String, Map<String, List<SyncFireApproveListVO>>> fireApproveListMap = fireApproveList.stream()
                .collect(Collectors.groupingBy(
                        SyncFireApproveListVO::getRegion,
                        Collectors.groupingBy(SyncFireApproveListVO::getFireLevel)
                ));
        Set <String> keySet = fireApproveListMap.keySet();
        for (String key : keySet) {
            SyncFireApproveTreeVO fireApproveTree =new SyncFireApproveTreeVO();
            fireApproveTree.setKey(key);
            fireApproveTree.setTitle(key);
            fireApproveTree.setChildren(setChildrenData(key,fireApproveListMap.get(key)));
            fireApproveListList.add(fireApproveTree);
        }
        return fireApproveListList;
    }
    private <T> List<SyncFireApproveTreeVO> setChildrenData(String parent ,T childrenData) {
        List<SyncFireApproveTreeVO> fireApproveListList = Lists.newArrayList();
        if(childrenData == null){
            return fireApproveListList;
        }
        if(childrenData instanceof Map){
            Map<String, List<SyncFireApproveListVO>> childrenMap = (Map<String, List<SyncFireApproveListVO>>) childrenData;
            Set <String> keySet = childrenMap.keySet();
            for (String key : keySet) {
                SyncFireApproveTreeVO fireApproveChildrenTree =new SyncFireApproveTreeVO();
                fireApproveChildrenTree.setTitle(key);
                fireApproveChildrenTree.setKey(parent+"-"+key);
                fireApproveChildrenTree.setChildren(setChildrenData(key,childrenMap.get(key)));
                fireApproveListList.add(fireApproveChildrenTree);
            }
        }else {
            List<SyncFireApproveListVO> childrenList = (List<SyncFireApproveListVO>) childrenData;
            for (SyncFireApproveListVO fireApprove : childrenList) {
                fireApproveListList.add(new SyncFireApproveTreeVO(fireApprove));
            }
        }
        return fireApproveListList;
    }

    /**
     * APP 查询动火信息
     * @param fireApproveListParam
     * @return
     */
    public List<SyncFireApproveListVO> getFireApproveList(SyncFireApproveListParam fireApproveListParam) {
        // 获取工厂信息,查看用户是否有工厂权限
        List<SyncFireApproveListVO> fireApproveList = Lists.newArrayList();
        String empNo = fireApproveListParam.getEmpNo();
        if(empNo.startsWith("01")){
            String empNoNew = safetySysUserService.employeeConvertWorkNo(empNo);
            if(StringUtils.isNotBlank(empNoNew)){
                fireApproveListParam.setEmpNo(empNoNew);
            }
        }
        // 查询用户权限
        List<SafetySysRoleDO> safetySysRoleList = loginMapper.getRoleNoDelList(fireApproveListParam.getEmpNo());
        safetySysRoleList = safetySysRoleList.stream().filter(e ->Constants.roles.contains(e.getRoleName())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(safetySysRoleList)){
            return fireApproveList;
        }
        List<Map<String,Object>> factoryList = safetySysUserDOMapper.getFactroyByEmpNo(fireApproveListParam.getEmpNo());
        if(CollectionUtils.isEmpty(factoryList)) {
            return fireApproveList;
        }
        Object factoryStr = factoryList.get(0).getOrDefault("factorys","");
        if(!Objects.nonNull(factoryStr) || StringUtils.isBlank(factoryStr.toString())){
            return fireApproveList;
        }
        List<String> factorys = Arrays.stream(factoryList.get(0).get("factorys").toString().split(","))
                .collect(Collectors.toList());
        return safetyHotWorkLensDOMapper.getFireApproveList(factorys,fireApproveListParam);
    }

    public void deleteLens(SafetyHotWorkLensParam param, String tocken) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(tocken);
        if(StringUtils.isBlank(tokenUser.getOrDefault("workNo",""))){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"员工号不能为空！");
        }
        if(param.getId() == null){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(),"删除监控设备-设备id不能为空！");
        }
        SafetyHotWorkLensDO safetyHotWorkLens =new SafetyHotWorkLensDO();
        safetyHotWorkLens.setId(param.getId());
        safetyHotWorkLens.setLensStatus(Constants.DELETE_LENS_STATUS);
        safetyHotWorkLensDOMapper.updateByPrimaryKeySelective(safetyHotWorkLens);
    }

    public String getMonitorVideoUrl(SafetyMonitorVideoParam param,String tocken) throws Exception {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(tocken);
        if(StringUtils.isBlank(tokenUser.getOrDefault("workNo",""))){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"员工号不能为空！");
        }
        return this.getMonitorVideoUrl(param);
    }

    public String getMonitorVideoUrl(SafetyMonitorVideoParam param) throws Exception {
        if(StringUtils.isBlank(param.getUrlType())){
            param.setUrlType(Constants.URLS_TYPE);
        }
        // 查询镜头信息
        String cameraIndexCode =getcameraIndexCode(param.getLensNum());
        String result;
        if(Constants.PREVIEW_VIDEO_TYPE.equals(param.getVideoType())){
            //直播
            result =  this.getPreviewParam(param,cameraIndexCode);
        }else if(Constants.BACK_VIDEO_TYPE.equals(param.getVideoType())){
            //回放
            result =  this.playbackURLs(param,cameraIndexCode);
        }else{
            JSONObject data =  new JSONObject();
            String previewVideo = this.getPreviewParam(param,cameraIndexCode);
            String backVideo = this.playbackURLs(param,cameraIndexCode);
            data.put(Constants.PREVIEW_VIDEO_TYPE,previewVideo);
            data.put(Constants.BACK_VIDEO_TYPE,backVideo);
            result = data.toJSONString();
        }
        return result;
    }

    /**
     * 根据镜头唯一编码获取监控点唯一标识
     * @param encodeDevIndexCode
     * @return
     */
    private String getcameraIndexCode(String encodeDevIndexCode){
        // 国际编码不需要转换
        if(StringUtils.startsWith(encodeDevIndexCode,"410") && encodeDevIndexCode.length()< 32){
          return encodeDevIndexCode;
        }
        JSONObject jsonBody = getJSONObject();
        jsonBody.put("encodeDevIndexCode",encodeDevIndexCode);
        String resultLens = doPostArtemis(cameraListUrl,jsonBody);
        JSONObject lensJson =JSONObject.parseObject(resultLens);
        if(!Constants.SUCCESS_STATUS.equals(lensJson.getString("code"))
                || lensJson.getJSONObject("data").getJSONArray("list").size()<1){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(),"查询监控设备-未获取到海康监控点信息！");
        }
        String cameraIndexCode = lensJson.getJSONObject("data")
                .getJSONArray("list")
                .getJSONObject(0)
                .getString("cameraIndexCode");
        if(StringUtils.isBlank(cameraIndexCode)){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(),"查询监控设备-海康监控点编号为空！");
        }
        return cameraIndexCode;
    }

    /**
     * 获取监控设备直播流
     * @param param
     * @param indexCode
     * @return
     */
    private String getPreviewParam(SafetyMonitorVideoParam param,String indexCode)throws Exception {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("indexCode",indexCode);
        jsonBody.put("netZoneCode","0");
        jsonBody.put("transmode",1);
        jsonBody.put("streamType",param.getStreamType() == null ? 0 : param.getStreamType());
        jsonBody.put("protocol",param.getUrlType());
        String result = doPostArtemis(getPreviewParam,jsonBody);
        JSONObject playbackJson =JSONObject.parseObject(result);
        if(!Constants.SUCCESS_STATUS.equals(playbackJson.getString("code"))
                || playbackJson.getJSONObject("data")==null
                || playbackJson.getJSONObject("data").size() < 1){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(),"查询监控预览链接-未获取到海康监控设备播放流信息！");
        }
        String url = playbackJson.getJSONObject("data").getString("url");
        if(StringUtils.isNotBlank(url)){
            if(Constants.URLS_TYPE.equals(param.getUrlType())){
                return url.replace(Constants.hk_wss,an_wss);
            }else{
                return url.replace(Constants.hk_ws,an_wss);
            }
        }
        return url;
    }

    /**
     * 获取监控设备回放流
     * @param param
     * @param cameraIndexCode
     * @return
     */
    private String playbackURLs(SafetyMonitorVideoParam param, String cameraIndexCode)throws Exception{
        FireApproveDOWithBLOBs fireApprove = fireApproveDOMapper.selectByPrimaryKey(param.getId());
        if(fireApprove == null){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(),"查询监控回放链接-未获取到动火流程信息！");
        }
        Date startTime = fireApprove.getFireStartTime();
        Date endTime = fireApprove.getFireEndTime();
        if(startTime == null || endTime ==null){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(),"查询监控回放链接-未获取到动火开始或结束时间！");
        }
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("cameraIndexCode",cameraIndexCode);
        jsonBody.put("recordLocation","0");
        jsonBody.put("transmode",0);
        jsonBody.put("beginTime", DateUtils.format(startTime,8));
        jsonBody.put("endTime", DateUtils.format(endTime,8));
        jsonBody.put("lockType",0);
        jsonBody.put("protocol",param.getUrlType());
        String result = doPostArtemis(playbackURLs,jsonBody);
        JSONObject playbackJson =JSONObject.parseObject(result);
        if(!Constants.SUCCESS_STATUS.equals(playbackJson.getString("code"))
                || playbackJson.getJSONObject("data")==null
                || playbackJson.getJSONObject("data").size() < 1){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(),"查询监控回放链接-未获取到海康监控设备播放流信息！");
        }
        String url = playbackJson.getJSONObject("data").getString("url");
        if(StringUtils.isNotBlank(url)){
            if(Constants.URLS_TYPE.equals(param.getUrlType())){
                return url.replace(Constants.hk_wss,an_wss);
            }else{
                return url.replace(Constants.hk_ws,an_wss);
            }
        }
        return url;
    }

    public JSONObject getLensStatus(SafetyHotWorkLensParam param, String tocken) {
        Map<String, String> tokenUser = TokenUtils.getTockenUser(tocken);
        if(StringUtils.isBlank(tokenUser.getOrDefault("workNo",""))){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"员工号不能为空！");
        }
        return this.getLensStatus(param);
    }
    public JSONObject getLensStatus(SafetyHotWorkLensParam param) {
        if(StringUtils.isBlank(param.getLensNum())){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(),"查询监控设备状态-监控设备编号不能为空！");
        }
        String indexCode = getcameraIndexCode(param.getLensNum());
        JSONObject jsonBody = getJSONObject();
        jsonBody.put("indexCodes",new String[]{indexCode});
        String body = jsonBody.toJSONString();
        String result = doPostArtemis(cameraGet,jsonBody);
        JSONObject lensJson =JSONObject.parseObject(result);
        if(!Constants.SUCCESS_STATUS.equals(lensJson.getString("code"))
                || lensJson.getJSONObject("data").getJSONArray("list").size()<1){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(),"查询监控设备状态-未获取到海康监控设备信息！");
        }
        String lensStatus = lensJson.getJSONObject("data")
                .getJSONArray("list")
                .getJSONObject(0)
                .getString("online");
        if(StringUtils.isBlank(lensStatus)){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(),"查询监控设备状态-海康监控设备编号为空！");
        }
        JSONObject data =  new JSONObject();
        data.put("lensNum",param.getLensNum());
        data.put("online",lensStatus);
        return data;
    }

    /**
     * 调用海康接口
     * @param url
     * @param jsonBody
     * @return
     * @throws Exception
     */
    public String doPostArtemis(String url, JSONObject jsonBody){
        Map<String, String> path = new HashMap<String, String>(2) {
            { put(Constants.HTTPS,url); }
        };
        String body = jsonBody.toJSONString();
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, Constants.CONTENT_TYPE);
        if (StringUtils.isBlank(result)) {
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "海康接口异常！");
        }
        return result;
    }

    /**
     * 调用海康接口
     */
    public String doPostArtemisV2(String url, String body){
        Map<String, String> path = new HashMap<String, String>(2) {
            { put(Constants.HTTPS,url); }
        };
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, Constants.CONTENT_TYPE);
        if (StringUtils.isBlank(result)) {
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "海康接口异常！");
        }
        return result;
    }

    private JSONObject getJSONObject(){
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("pageNo",1);
        jsonBody.put("pageSize",50);
        return jsonBody;
    }
}
