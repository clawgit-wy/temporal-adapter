package com.yutong.safety.core.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.EipStateEnum;
import com.yutong.safety.common.constants.HikMiddleTableSqlEnum;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.constants.VisitorHairpinEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.app.VisitorAppListParam;
import com.yutong.safety.common.param.web.*;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.web.HkMiddleTableVO;
import com.yutong.safety.core.db.entity.*;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDetailDO;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryBlankProcessDetailQueryParams;
import com.yutong.safety.core.db.mapper.*;
import com.yutong.safety.core.dto.*;
import com.yutong.safety.core.service.entry.SafetyEntryBlankProcessDetailService;
import com.yutong.safety.core.service.hik.SafetyArtemisService;
import com.yutong.sugar.api.IRpcLdapService;
import com.yutong.sugar.api.vo.LdapResult;
import com.yutong.sugar.api.vo.LdapUser;
import javafx.beans.binding.StringBinding;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.yutong.safety.core.db.entity.visitor.common.SafetyVisitorConstants.ID_CARD_PHOTO_DEFAULT_VALUE;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 *  create gwcuiyx 2023.07.14
 *  访客发卡--service
 */
@Service
public class SafetyVisitorHairpinService {
    public static final Map<String, String> LOCATIONS_ID = new HashMap<>();

    static {
        LOCATIONS_ID.put("147","十八里河厂区");
        LOCATIONS_ID.put("148","新能源");
        LOCATIONS_ID.put("149","研发中心");
        LOCATIONS_ID.put("150","营销中心");
        LOCATIONS_ID.put("151","宇通重工");
        LOCATIONS_ID.put("152","深澜动力");
        LOCATIONS_ID.put("153","智驱科技");
        LOCATIONS_ID.put("154","专用车工厂");
        LOCATIONS_ID.put("155","九龙成品库");
        LOCATIONS_ID.put("156","工程试验中心");
        LOCATIONS_ID.put("157","九龙仓库");
        LOCATIONS_ID.put("158","精益达西区");
        LOCATIONS_ID.put("159","精益达东区");
        LOCATIONS_ID.put("160","底盘事业部");
    }
    @Autowired
    private SafetyFieldManageDOMapper safetyFieldManageDOMapper;
    @Autowired
    private SafetyVisitorHairpinDOMapper safetyVisitorHairpinDOMapper;
    @Value("${eip.approveFlow}")
    private String approveFlowUrl;
    @Value("${eip.templateId}")
    private String templateId;
    @Value("${safety.web.file.uploadUrl}")
    private String picUploadUrl;
    @Autowired
    private IRpcLdapService rpcLdapService;
    @Autowired
    private SafetyDataSendLogService safetyDataSendLogService;
    @Autowired
    private SafetyFieldManageService safetyFieldManageService;
    @Autowired
    private SafetyVisitorHairpinDOBizMapper safetyVisitorHairpinDOBizMapper;
    @Autowired
    private SafetyLogMessageService safetyLogMessageService;
    @Autowired
    private SafetySysUserService safetySysUserService;
    @Autowired
    private WebserviceUtil webserviceUtil;
    @Autowired
    private SafetyCardRefundService safetyCardRefundService;
    @Value("${iam.retention.robotId}")
    private String robotId;
    @Autowired
    private SafetyGeneralConfigService safetyGeneralConfigService;
    @Autowired
    private YxtRebotUtils yxtRebotUtils;
    @Autowired
    private SafetyGeneralConfigDOMapper safetyGeneralConfigDOMapper;
    //安保小助手机器人ID
    @Value("${iam.retention.robotId}")
    private String incomintRebotId;

    private static final String OPEN_NAME = "访客发卡";
    /**
     * 门卫人员通过PC后台登记管理—访客发卡页面—权限下发按钮 用的tcmid, 其它情况下，用配置文件中的tcmid
     */
    private static final String NEW_TCMID = "5E2143F4-EF60-4098-96F8-932827A7CC1E";
    @Autowired
    SafetyVisitorSecretDOBizMapper safetyVisitorSecretDOBizMapper;
    @Autowired
    private SafetyBlackMessageService safetyBlackMessageService;
    @Autowired
    private SafetyEntryBlankProcessDetailService safetyEntryBlankProcessDetailService;
    @Resource
    private SafetyArtemisService safetyArtemisService;
    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;

    /**
     * 分页数据列表查询
     * @param param
     * @return
     */
    public List<VisitorHirpinAppDTO> pageList(VisitorHairpinListParam param, String authFactory) {
        //参数设置
        Map<String,Object> pm = new HashMap<>();
        pm.put("startRowNum", param.getOffset());
        pm.put("endRowNum", param.getLimit());
        pm.put("authFactory", authFactory);
        pm.put("sh", param);
        List<VisitorHirpinAppDTO> list = safetyVisitorHairpinDOBizMapper.pageList(pm);
        Map<Integer, String> authMaps = Maps.newHashMap();
        for (VisitorHirpinAppDTO hirpinAppDTO : list) {
            if(StringUtils.isNotEmpty(hirpinAppDTO.getIssueFactory())){
                hirpinAppDTO.setIssueFactory(safetyFieldManageService.getNamesByIds(hirpinAppDTO.getIssueFactory()));
            }

            hirpinAppDTO.setVendorUnit(SensitiveUtils.maskName(hirpinAppDTO.getVendorUnit()));
            hirpinAppDTO.setVisitorName(SensitiveUtils.maskName(hirpinAppDTO.getVisitorName()));
            hirpinAppDTO.setVisitorIdCard(SensitiveUtils.maskCardId(hirpinAppDTO.getVisitorIdCard()));
            hirpinAppDTO.setVisitorPhone(SensitiveUtils.maskPhoneNumber(hirpinAppDTO.getVisitorPhone()));
            //获取当初发卡的人员权限id
            Integer userId = hirpinAppDTO.getUserId();
            if (authMaps.containsKey(userId)) {
                hirpinAppDTO.setHairpinFactory(authMaps.get(userId));
                continue;
            }
            String authFactory_new = null;
            if (Objects.nonNull(userId)) {
                //获取人员
                SafetySysUserDO safetySysUserDO = safetySysUserService.safetySysUserDOById(userId);
                authFactory_new = safetySysUserDO.getFactoryId();
            }
            if (StringUtils.isNotEmpty(hirpinAppDTO.getFactoryId()) && !ObjectUtils.isEmpty(userId)) {
                String hairpinFactory = authFactoryNames(hirpinAppDTO.getFactoryId(), hirpinAppDTO.getVendorFactory(), authFactory_new);
                hirpinAppDTO.setHairpinFactory(hairpinFactory);
                authMaps.put(userId, hairpinFactory);
            }
        }
        return list;
    }

    /**
     * 总数据量
     * @param param
     * @return
     */
    public long pageListCount(VisitorHairpinListParam param, String authFactory) {
        Map<String,Object> pm = new HashMap<>();
        pm.put("startRowNum", param.getOffset());
        pm.put("endRowNum", param.getLimit());
        pm.put("authFactory", authFactory);
        pm.put("sh", param);
        return safetyVisitorHairpinDOBizMapper.pageListCount(pm);
    }

    /**
     * 数据添加功能
     * @param param
     */
    public void add(VisitorHairpinAddParam param, String tocken) {
        SafetyVisitorHairpinDO visitorHairpinDO = null;
        if (StringUtils.isNotEmpty(param.getEipId())) {
            visitorHairpinDO = getVisitorByEipId(param.getEipId(), param.getVisitorIdCard());
        }
        boolean isExit = false;
        if (visitorHairpinDO == null) {
            visitorHairpinDO = new SafetyVisitorHairpinDO();
        } else {
            isExit = true;
        }
        visitorHairpinDO.setVisitedName(param.getVisitedName());
        visitorHairpinDO.setVisitedPhone(param.getVisitedPhone());
        visitorHairpinDO.setVisitorName(param.getVisitorName());
        visitorHairpinDO.setVisitorPhone(param.getVisitorPhone());
        visitorHairpinDO.setVisitorIdCard(param.getVisitorIdCard());
        visitorHairpinDO.setVendorUnit(param.getVendorUnit());
        visitorHairpinDO.setVendorMatter(param.getVendorMatter());
        visitorHairpinDO.setCardNo(param.getCardNo());
        visitorHairpinDO.setStartTime(DateUtils.parse(param.getStartTime(), 3));
        visitorHairpinDO.setEndTime(DateUtils.parse(param.getEndTime(), 3));
        visitorHairpinDO.setCreateTime(new Date());
        visitorHairpinDO.setUpdateTime(new Date());
        visitorHairpinDO.setDeleteFalg(false);
        visitorHairpinDO.setVendorState(3);//web端提交的数据，需要生成eip流程，对应的状态值是 审批中
        visitorHairpinDO.setSecretState(0);
        visitorHairpinDO.setPhoto(param.getPhoto());
        visitorHairpinDO.setWxPhone(param.getWxPhone());
        visitorHairpinDO.setVisitedDept(param.getVisitedDept());
        visitorHairpinDO.setIdCardPhoto(param.getIdCardPhoto());
        visitorHairpinDO.setIsSaveCardPhoto(param.getIsSaveCardPhoto());
        if (StringUtils.isNotEmpty(param.getEipCreateTime())) {
            Date eipCreateTime = DateUtils.parse(param.getEipCreateTime(), 3);
            PreconditonsEx.checkArgument(eipCreateTime != null, ResponseEnum.FAIL);
            visitorHairpinDO.setEipCreateTime(eipCreateTime);
        }
        String eipFlowId = param.getEipId();
        if(!"Y".equals(param.getWxFlag())) {
            String eipLog = getEipLog(visitorHairpinDO.getVisitedName(), visitorHairpinDO.getVisitedPhone());
            if (eipLog == null) {
                LogUtils.info("{\"code\":\"9999\",\"msg\":\"eipFlowId="+eipFlowId+",提交失败，请输入正确的被访人姓名或电话\"}");
                throw new BizException(ResponseEnum.FAIL.getCode(),"提交失败，请输入正确的被访人姓名或电话");
            }
            eipFlowId = createEipFlow(visitorHairpinDO, eipLog);
        }
        if(StringUtils.isNotEmpty(eipFlowId)) {
            visitorHairpinDO.setEipFlowId(eipFlowId);
            if (isExit) {
                PreconditonsEx.checkArgument(safetyVisitorHairpinDOMapper.updateByPrimaryKey(visitorHairpinDO) == 1, ResponseEnum.FAIL);
            } else {
                PreconditonsEx.checkArgument(safetyVisitorHairpinDOMapper.insert(visitorHairpinDO) == 1, ResponseEnum.FAIL);
            }
        } else {
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"入参param="+param+",生成eip流程失败，请重试！\"}");
            throw new BizException(ResponseEnum.FAIL.getCode(),"生成eip流程失败，请重试！");
        }
        if (StringUtils.isNotEmpty(tocken)) {
            //根据tocken当前用户人员
            String realName = TokenUtils.getRealName(tocken);
            if(StringUtils.isEmpty(realName)){
                throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token失效，记录操作日志异常");
            }
            //操作日志记录
            LogMessageAddParam log = new LogMessageAddParam();
            log.setOperatePage("访客发卡");
            log.setAbility("添加");
            log.setMessage(visitorHairpinDO.getVisitorName()+";"+visitorHairpinDO.getVisitorPhone()+";"+visitorHairpinDO.getVisitorIdCard());
            log.setOperator(realName);
            safetyLogMessageService.addLogMessage(log);
        }
    }

    /**
     * 根据id获取详情数据
     * @param id
     * @return
     */
    public SafetyVisitorHairpinDTO view(Integer id) {
        SafetyVisitorHairpinDO safetyVisitorHairpinDO = safetyVisitorHairpinDOMapper.selectByPrimaryKey(id);
        if(safetyVisitorHairpinDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        SafetyVisitorHairpinDTO safetyVisitorHairpinDTO = new SafetyVisitorHairpinDTO();
        BeanUtils.copyProperties(safetyVisitorHairpinDO, safetyVisitorHairpinDTO);
        //获取当初发卡的人员权限id
        Integer userId = safetyVisitorHairpinDO.getUserId();
        String authFactory = null;
        if (Objects.nonNull(userId)) {
            //获取人员
            SafetySysUserDO safetySysUserDO = safetySysUserService.safetySysUserDOById(userId);
            authFactory = safetySysUserDO.getFactoryId();
        }
        if (StringUtils.isNotEmpty(safetyVisitorHairpinDO.getFactoryId()) && !ObjectUtils.isEmpty(userId)) {
            safetyVisitorHairpinDTO.setAuthFactoryNames(authFactoryNames(safetyVisitorHairpinDTO.getFactoryId(), safetyVisitorHairpinDTO.getVendorFactory(), authFactory));
        }
        //数据脱敏操作
        return  safetyVisitorHairpinDTO;
    }

    /**
     * 用于数据脱敏
     * @param id
     * @return
     */
    public SafetyVisitorHairpinDTO newView(Integer id) {
        SafetyVisitorHairpinDO safetyVisitorHairpinDO = safetyVisitorHairpinDOMapper.selectByPrimaryKey(id);
        if(safetyVisitorHairpinDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        SafetyVisitorHairpinDTO safetyVisitorHairpinDTO = new SafetyVisitorHairpinDTO();
        BeanUtils.copyProperties(safetyVisitorHairpinDO, safetyVisitorHairpinDTO);
        //获取当初发卡的人员权限id
        Integer userId = safetyVisitorHairpinDO.getUserId();
        String authFactory = null;
        if (Objects.nonNull(userId)) {
            //获取人员
            SafetySysUserDO safetySysUserDO = safetySysUserService.safetySysUserDOById(userId);
            authFactory = safetySysUserDO.getFactoryId();
        }
        if (StringUtils.isNotEmpty(safetyVisitorHairpinDO.getFactoryId()) && !ObjectUtils.isEmpty(userId)) {
            safetyVisitorHairpinDTO.setAuthFactoryNames(authFactoryNames(safetyVisitorHairpinDTO.getFactoryId(), safetyVisitorHairpinDTO.getVendorFactory(), authFactory));
        }
        //数据脱敏操作
        safetyVisitorHairpinDTO.setVisitorIdCard(SensitiveUtils.maskCardId(safetyVisitorHairpinDTO.getVisitorIdCard()));
        safetyVisitorHairpinDTO.setVisitorPhone(SensitiveUtils.maskPhoneNumber(safetyVisitorHairpinDTO.getVisitorPhone()));
        safetyVisitorHairpinDTO.setVisitorName(SensitiveUtils.maskName(safetyVisitorHairpinDTO.getVisitorName()));
        safetyVisitorHairpinDTO.setVendorUnit(SensitiveUtils.maskName(safetyVisitorHairpinDTO.getVendorUnit()));
        return safetyVisitorHairpinDTO;
    }

    /**
     * 根据idCard获取详情数据
     * @param idCard
     * @return
     */
    public SafetyVisitorHairpinDTO infoForIdCard(String idCard) {
        SafetyVisitorHairpinDOExample example = new SafetyVisitorHairpinDOExample();
        SafetyVisitorHairpinDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andVisitorIdCardEqualTo(idCard);
        criteria.andEndTimeGreaterThanOrEqualTo(new Date());
        //排除废弃订单数据
        criteria.andVendorStateNotEqualTo(7);
        example.setOrderByClause(" create_time desc ");
        List<SafetyVisitorHairpinDO> list = safetyVisitorHairpinDOMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        SafetyVisitorHairpinDTO safetyVisitorHairpinDTO = new SafetyVisitorHairpinDTO();
        BeanUtils.copyProperties(list.get(0), safetyVisitorHairpinDTO);
        safetyVisitorHairpinDTO.setRowCount(list.size());
        //获取当初发卡的人员权限id
        Integer userId = safetyVisitorHairpinDTO.getUserId();
        String authFactory = null;
        if (Objects.nonNull(userId)) {
            //获取人员
            SafetySysUserDO safetySysUserDO = safetySysUserService.safetySysUserDOById(userId);
            authFactory = safetySysUserDO.getFactoryId();
        }
        if (StringUtils.isNotEmpty(safetyVisitorHairpinDTO.getFactoryId()) && !ObjectUtils.isEmpty(userId)) {
            safetyVisitorHairpinDTO.setAuthFactoryNames(authFactoryNames(safetyVisitorHairpinDTO.getFactoryId(), safetyVisitorHairpinDTO.getVendorFactory(), authFactory));
        }
        //数据脱敏操作
        safetyVisitorHairpinDTO.setVisitorIdCard(SensitiveUtils.maskCardId(safetyVisitorHairpinDTO.getVisitorIdCard()));
        safetyVisitorHairpinDTO.setVisitorPhone(SensitiveUtils.maskPhoneNumber(safetyVisitorHairpinDTO.getVisitorPhone()));
        safetyVisitorHairpinDTO.setVisitorName(SensitiveUtils.maskName(safetyVisitorHairpinDTO.getVisitorName()));
        safetyVisitorHairpinDTO.setVendorUnit(SensitiveUtils.maskName(safetyVisitorHairpinDTO.getVendorUnit()));
        return safetyVisitorHairpinDTO;
    }

    /**
     * 根据访客卡号获取访客详情数据
     * @param hairpinNo
     * @return
     */
    public SafetyVisitorHairpinDO infoForHairpinNo(String hairpinNo) {
        SafetyVisitorHairpinDOExample example = new SafetyVisitorHairpinDOExample();
        SafetyVisitorHairpinDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andHairpinNoEqualTo(hairpinNo);
        List<Integer> vendorStateList = new ArrayList<>();
        vendorStateList.add(1);
        vendorStateList.add(8);
        vendorStateList.add(9);
        criteria.andVendorStateIn(vendorStateList);
//        criteria.andEndTimeGreaterThanOrEqualTo(new Date());
        example.setOrderByClause(" create_time desc ");
        List<SafetyVisitorHairpinDO> list = safetyVisitorHairpinDOMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 数据更新操作
     * @param param
     */
    public void update(VisitorHairpinUpdateParam param, String token) {
        SafetyVisitorHairpinDO safetyVisitorHairpinDO = safetyVisitorHairpinDOMapper.selectByPrimaryKey(param.getId());
        if(safetyVisitorHairpinDO == null) {
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"入参："+param+",未找到对应记录\"}");
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        if(StringUtils.isEmpty(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }
        if(StringUtils.isNotEmpty(param.getHairpinNo())) {
            safetyVisitorHairpinDO.setHairpinNo(param.getHairpinNo());
        }
        if(StringUtils.isNotEmpty(param.getRecoveryNo())) {
            safetyVisitorHairpinDO.setRecoveryNo(param.getRecoveryNo());
        }
        if(StringUtils.isNotEmpty(param.getVisitedName())) {
            safetyVisitorHairpinDO.setVisitedName(param.getVisitedName());
        }
        if(StringUtils.isNotEmpty(param.getVisitedPhone())) {
            safetyVisitorHairpinDO.setVisitedPhone(param.getVisitedPhone());
        }
        if(StringUtils.isNotEmpty(param.getVendorMatter())) {
            safetyVisitorHairpinDO.setVendorMatter(param.getVendorMatter());
        }
        if(StringUtils.isNotEmpty(param.getCardNo())) {
            safetyVisitorHairpinDO.setCardNo(param.getCardNo());
        }
        if(StringUtils.isNotEmpty(param.getPhoto())) {
            safetyVisitorHairpinDO.setPhoto(param.getPhoto());
        }
        if(StringUtils.isNotEmpty(param.getStartTime())) {
            safetyVisitorHairpinDO.setStartTime(DateUtils.parse(param.getStartTime(), 3));
        }
        if(StringUtils.isNotEmpty(param.getEndTime())) {
            safetyVisitorHairpinDO.setEndTime(DateUtils.parse(param.getEndTime(), 3));
        }

        if (Objects.nonNull(param.getVendorState())) {
            if (param.getVendorState() == 1) {
                if (DateUtils.sub(safetyVisitorHairpinDO.getEndTime(), new Date()) == 0) {
                    LogUtils.info("{\"code\":\"9999\",\"msg\":\"入参："+param+",当前时间已超过入厂结束时间！\"}");
                    throw new BizException(ResponseEnum.FAIL.getCode(),"当前时间已超过入厂结束时间！");
                }
                //获取权限
                String factoryA = null;
                safetyVisitorHairpinDO.setHairpinTime(new Date());
                if (StringUtils.isNotEmpty(workNo)) {
                    SafetySysUserDO safetySysUserDO = safetySysUserService.getUserMessage(workNo);
                    safetyVisitorHairpinDO.setUserId(safetySysUserDO.getId());
                    factoryA = safetySysUserDO.getFactoryId();
                }
                //发卡的时候，将已发卡，但是没退卡的数据，全部置为已退卡
                //注销所有同一的卡号
                hairpinNoBack(safetyVisitorHairpinDO, param.getHairpinNo());
                //发卡动作的时候，调用发卡接口进行数据下发动作，人脸。
                if (StringUtils.isNotEmpty(safetyVisitorHairpinDO.getPhoto())) {
                    safetyDataSendLogService.sendDataRecord(safetyVisitorHairpinDO, "0", "", "人脸", false, safetyVisitorHairpinDO.getHairpinNo(), factoryA);
                }
                //下发卡门禁
                safetyDataSendLogService.sendDataRecord(safetyVisitorHairpinDO, "2", "", "门禁", false, safetyVisitorHairpinDO.getHairpinNo(), factoryA);
            } else if(param.getVendorState() == 2) {
                //单独退卡的时候，注销卡
                //调用接口进行卡注销
                safetyVisitorHairpinDO.setRecoveryTime(new Date());
                safetyDataSendLogService.hairpinNoBack(safetyVisitorHairpinDO);
            }
            safetyVisitorHairpinDO.setVendorState(param.getVendorState());
        }
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("访客发卡");
        String msg = "";
        if (param.getVendorState() == 1) {
            log.setAbility("发卡");
            msg = "[访客流程通过]:";
            if (StringUtils.isNotEmpty(safetyVisitorHairpinDO.getIdCardPhoto())) {
                msg = "[访客上传身份证(" + safetyVisitorHairpinDO.getIdCardPhoto() + ")]:";
            }
        } else if (param.getVendorState() == 2) {
            log.setAbility("退卡");
        }
        log.setMessage(msg + safetyVisitorHairpinDO.getVisitorName()+";"+safetyVisitorHairpinDO.getVisitorPhone()+";"+safetyVisitorHairpinDO.getVisitorIdCard());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        safetyVisitorHairpinDOMapper.updateByPrimaryKey(safetyVisitorHairpinDO);
        //未发卡数据调用
        if(!StringUtils.isEmpty(safetyVisitorHairpinDO.getHairpinNo())){
            safetyCardRefundService.deleteByHairpinNo(safetyVisitorHairpinDO.getHairpinNo());
        }
        if (param.getVendorState() == 1 && safetyVisitorHairpinDO.getStartTime().compareTo(new Date()) > 0) {
            //发卡记录日志的时候，发送被访人宇信通通知
            //获取员工号
            String staffNo = getEipNoLog(safetyVisitorHairpinDO.getVisitedName(), safetyVisitorHairpinDO.getVisitedPhone());
            if (StringUtils.isEmpty(staffNo)) {
                LogUtils.info("{\"code\":\"9999\",\"msg\":\" [访客管理] 中早于 [入场开始时间] 发卡后，安保小助手消息提醒被访人的时候获取被访人的员工号失败：{id:{}} \"}", safetyVisitorHairpinDO.getId());
                return;
            }
            String remindContent = "尊敬的" + safetyVisitorHairpinDO.getVisitedName() + "老师：您的访客" +
                    safetyVisitorHairpinDO.getVisitorName() + safetyVisitorHairpinDO.getVisitorPhone() +
                    "已提前到访，请及时接待";
            safetyGeneralConfigService.sendMsgIm(staffNo, robotId, remindContent);
        }
    }

    /**
     * 所有需要退卡的数据
     * @param hairpinNo
     */
    public void hairpinNoBack(SafetyVisitorHairpinDO safetyVisitorHairpinDO, String hairpinNo) {
        //判断现有状态
        if (safetyVisitorHairpinDO.getVendorState() == 1 || safetyVisitorHairpinDO.getVendorState() == 8 || safetyVisitorHairpinDO.getVendorState() == 9) {
            //已发卡，没退卡
            //先退卡
            safetyDataSendLogService.hairpinNoBack(safetyVisitorHairpinDO);
        }
        safetyVisitorHairpinDOBizMapper.allSendCardToEnd(hairpinNo);
    }

    public void hairpinNoBackNoReview(String hairpinNo) {
        safetyVisitorHairpinDOBizMapper.allSendCardToEnd(hairpinNo);
    }

    /**
     * 数据删除工作
     * @param id
     */
    public void delete(Integer id) {
        SafetyVisitorHairpinDO safetyVisitorHairpinDO = safetyVisitorHairpinDOMapper.selectByPrimaryKey(id);
        if(safetyVisitorHairpinDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        safetyVisitorHairpinDO.setDeleteFalg(true);
        safetyVisitorHairpinDOMapper.updateByPrimaryKey(safetyVisitorHairpinDO);
    }

    /**
     * 图片上传
     * @param param
     * @return
     */
    public String picUpload(PicParam param, String tocken) {
        LogUtils.info("picUploadUrl:{}", picUploadUrl);
        String picFileUrl = HttpFileUtil.uploadNewFile(picUploadUrl, param.getFile(), tocken);
        if(StringUtils.isNotEmpty(picFileUrl)) {
            JSONObject resultJson = (JSONObject) JsonUtils.parse(picFileUrl);
            if("0".equals(resultJson.getString("code"))) {
                String picFilePath = resultJson.getString("data");
                picFilePath = org.apache.commons.lang3.StringUtils.substringBefore(picFilePath, "_");
                return picFilePath;
            }
        }
        return null;
    }

    /**
     * 创建eip流程
     * @param visitorHairpin
     * @return
     */
    public String createEipFlow(SafetyVisitorHairpinDO visitorHairpin, String eipLog) {
        //对应的EIP模板有自动生成标题的规则，需求变更时注意调整档期subject参数
        String subject = "测试生成eip流程";
        //获取表单设置对应字段
        Map<String, Object> map = new HashMap<>();
        //字段拼接
        //被访人姓名
        map.put("fd_3c080504f00be4", visitorHairpin.getVisitedName());
        //被访人手机号
        map.put("fd_35c5fe1d4fead4", visitorHairpin.getVisitedPhone());
        List sonList = Lists.newArrayList();
        Map<String, Object> sonMap = new HashMap<>();
        //访客姓名
        sonMap.put("fd_35c5fd6efad1ee", visitorHairpin.getVisitorName());
        //访客身份证号
        sonMap.put("fd_35c5fdbb88317a", visitorHairpin.getVisitorIdCard());
        //访客手机号
        sonMap.put("fd_35c5fd6d6fec06", visitorHairpin.getVisitorPhone());
        //照片
        sonMap.put("fd_3c1e279c8fa2fc", visitorHairpin.getPhoto());
        sonList.add(sonMap);
        map.put("fd_3c0804d5f75548", sonList);
        //访客单位
        map.put("fd_35c5fd6d13395a", visitorHairpin.getVendorUnit());
        //车牌号
        map.put("fd_388dd64ddae0de", visitorHairpin.getCardNo());
        //访问事由
        map.put("fd_35c5fd51cf5b8c", visitorHairpin.getVendorMatter());
        //入厂开始时间
        map.put("fd_35c5fd3e1eae64", DateUtils.format(visitorHairpin.getStartTime(), 3));
        //入厂结束时间
        map.put("fd_35c5fd3ee0d36c", DateUtils.format(visitorHairpin.getEndTime(), 3));
        //web端发起流程
        map.put("fd_3c22bb1731957c", "Y");
        String formDataXML = webserviceUtil.createXML(map);
        StringBuilder params = new StringBuilder();
        params.append("<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ith=\"http://com/yt/eip/ws/third/service/IThirdPartyWfService.wsdl\">");
        params.append("<soapenv:Header/><soapenv:Body><ith:execute soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><in0 xsi:type=\"mod:ThirdPartyParameter\" xs:type=\"type:ThirdPartyParameter\" xmlns:mod=\"http://model.third.ws.eip.yt.com\" xmlns:xs=\"http://www.w3.org/2000/XMLSchema-instance\">");
        params.append("<creatorName xsi:type=\"xsd:string\" xs:type=\"type:string\">").append(eipLog).append("</creatorName>");
        params.append("<formDataXML xsi:type=\"xsd:string\" xs:type=\"type:string\">");
        params.append("<![CDATA["+formDataXML+"]]>");
        params.append("</formDataXML>");
        params.append("<modelName xsi:type=\"xsd:string\" xs:type=\"type:string\">com.landray.kmss.km.review.model.KmReviewMain</modelName><nodeId xsi:type=\"xsd:string\" xs:type=\"type:string\"/><notifyMethod xsi:type=\"xsd:string\" xs:type=\"type:string\"/><notifyType xsi:type=\"xsd:string\" xs:type=\"type:string\"/><relationUrl xsi:type=\"xsd:string\" xs:type=\"type:string\"/>");
        // 主题
        params.append("<subject xsi:type=\"xsd:string\" xs:type=\"type:string\">").append(subject).append("</subject>");
        // 流程状态 10 草稿 20 待审批
        params.append("<submitStatus xsi:type=\"xsd:string\" xs:type=\"type:string\">").append("20").append("</submitStatus>");
        // 模板id
        params.append("<templateId xsi:type=\"xsd:string\" xs:type=\"type:string\">").append(templateId).append("</templateId>");
        // 触发系统标识
        params.append("<thirdSystemStart xsi:type=\"xsd:string\" xs:type=\"type:string\">").append("ZY").append("</thirdSystemStart>");
        params.append("<webServiceUrl xsi:type=\"xsd:string\" xs:type=\"type:string\"/></in0></ith:execute></soapenv:Body></soapenv:Envelope>");
        String webResult = webserviceUtil.doWebservicePost(approveFlowUrl, params.toString());
        Map<String, Object> resultMap = JsonUtils.toMap(webResult);
        Map<String, Object> envelopeMap = JsonUtils.toMap(String.valueOf(resultMap.get("soapenv:Envelope")));
        Map<String, Object> bodyMap = JsonUtils.toMap(String.valueOf(envelopeMap.get("soapenv:Body")));
        Map<String, Object> multiRefMap = JsonUtils.toMap(String.valueOf(bodyMap.get("multiRef")));
        Map<String, Object> modelIdMap = JsonUtils.toMap(String.valueOf(multiRefMap.get("modelId")));
        String eipId = String.valueOf(modelIdMap.get("content"));
        LogUtils.info("eip流程id:{}", eipId);
        if(StringUtils.isNotEmpty(eipId)) {
            return eipId;
        }
        return null;
    }

    /**
     * 获取数据
     * @param name
     * @param mobile
     * @return
     */
    public String getEipLog(String name, String mobile) {
        LdapResult<List<LdapUser>> listLdapResult = rpcLdapService.findUserinfo(mobile, "1", 0, 20) ;
        List<LdapUser> ldapUsers = listLdapResult.getData();
        if (ldapUsers == null) {
            return null;
        }
        ldapUsers.sort((LdapUser a, LdapUser b)->b.getUid().compareTo(a.getUid()));
        for(LdapUser ldapUser : ldapUsers) {
            if(org.apache.commons.lang3.StringUtils.isNotBlank(ldapUser.getEip())) {
                if(name.equals(ldapUser.getName())) {
                    return ldapUser.getEip();
                }
            }
        }
        return null;
    }

    /**
     * 获取员工号
     * @param name
     * @param mobile
     * @return
     */
    public String getEipNoLog(String name, String mobile) {
        LdapResult<List<LdapUser>> listLdapResult = rpcLdapService.findUserinfo(mobile, "1", 0, 20) ;
        List<LdapUser> ldapUsers = listLdapResult.getData();
        if (ldapUsers == null) {
            return null;
        }
        ldapUsers.sort((LdapUser a, LdapUser b)->b.getUid().compareTo(a.getUid()));
        for(LdapUser ldapUser : ldapUsers) {
            if(org.apache.commons.lang3.StringUtils.isNotBlank(ldapUser.getEip())) {
                if(name.equals(ldapUser.getName())) {
                    return ldapUser.getUid();
                }
            }
        }
        return null;
    }

    public void updateAuthorizedInfo(VisitorHairpinParam param) {
        String authorizedByNo = param.getAuthorizedByNo();
        String authorizedByName = "";
        String authorizedNo = param.getAuthorizedNo();
        String authorizedName = "";
        if (StringUtils.isEmpty(authorizedByNo) && StringUtils.isEmpty(authorizedNo)) {
            return;
        }
        SafetyEmployeeMessageDO authorizedByInfo = safetyEmployeeMessageService.getByNo(authorizedByNo);
        if (authorizedByInfo != null) {
            authorizedByName = authorizedByInfo.getEmpName();
        }
        SafetyEmployeeMessageDO authorizedInfo = safetyEmployeeMessageService.getByNo(authorizedNo);
        if (authorizedInfo != null) {
            authorizedName = authorizedInfo.getEmpName();
        }
        SafetyVisitorHairpinDO info = new SafetyVisitorHairpinDO();
        info.setAuthorizedNo(authorizedNo);
        info.setAuthorizedName(authorizedName);
        info.setAuthorizedByNo(authorizedByNo);
        info.setAuthorizedByName(authorizedByName);
        SafetyVisitorHairpinDOExample example = new SafetyVisitorHairpinDOExample();
        example.createCriteria().andDeleteFalgEqualTo(false).andEipFlowIdEqualTo(param.getEipFlowId());
        safetyVisitorHairpinDOMapper.updateByExampleSelective(info, example);
    }

    /**
     * 根据eip流程id更新数据
     * @param param
     */
    public void updateEipStatusById(VisitorHairpinParam param) {
        //根据eip流程id获取数据
        List<SafetyVisitorHairpinDO> list = getVisitorListByEipId(param.getEipFlowId(),null);
        if(CollectionUtils.isEmpty(list)) {
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"方法updateEipStatusById，入参："+param+",该记录不存在\"}");
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        // 更新授权人和被授权人信息
        updateAuthorizedInfo(param);

        //查询绝密区
        List<SafetyVisitorSecretDTO> secretFactoryList = safetyVisitorSecretDOBizMapper.getSecretFactoryList();
        Map<String, SafetyVisitorSecretDTO> secretMap = null;
        if(CollectionUtils.isNotEmpty(secretFactoryList)){
            secretMap = secretFactoryList.stream().collect(Collectors.toMap(SafetyVisitorSecretDTO::getSecretPosition,
                    dto -> dto,
                    (oldV, newV) -> newV));
        }
        //获取访客的黑名单人员及黑名单申请入厂的权限
        List<String> idCardList = list.stream().map(SafetyVisitorHairpinDO::getVisitorIdCard).distinct().collect(Collectors.toList());
        Map<String,List<SafetyEntryBlankProcessDetailDO>> blackApplyMap = filterBlankProcess(idCardList);
        Set<String> blackSet = blackApplyMap.keySet();

        for (SafetyVisitorHairpinDO safetyVisitorHairpinDO : list) {
            //根据传的状态更新数据
            if(EipStateEnum.RELEASE.getCode().equals(param.getState())) {
                safetyVisitorHairpinDO.setVendorState(VisitorHairpinEnum.WFK.getCode());
            } else if(EipStateEnum.TIMEOUT.getCode().equals(param.getState())) {
                safetyVisitorHairpinDO.setVendorState(VisitorHairpinEnum.CSGB.getCode());
            } else if (EipStateEnum.DISCARD.getCode().equals(param.getState())) {
                safetyVisitorHairpinDO.setVendorState(VisitorHairpinEnum.FQ.getCode());
            } else if (EipStateEnum.REJECT.getCode().equals(param.getState())) {
                safetyVisitorHairpinDO.setVendorState(VisitorHairpinEnum.BH.getCode());
            }
            if(StringUtils.isNotEmpty(param.getFactoryId())) {
                //获取
                safetyVisitorHairpinDO.setVendorFactory(safetyFieldManageService.getNamesByIds(param.getFactoryId()));
                safetyVisitorHairpinDO.setFactoryId(param.getFactoryId());
            }
            if (StringUtils.isNotEmpty(param.getRegionId())) {
                //获取数据
                safetyVisitorHairpinDO.setVendorRegion(safetyFieldManageService.getNamesByIds(param.getRegionId()));
                safetyVisitorHairpinDO.setRegionId(param.getRegionId());
            }
            if(StringUtils.isNotEmpty(param.getVendorFactory())) {
                safetyVisitorHairpinDO.setVendorFactory(param.getVendorFactory());
            }
            if(StringUtils.isNotEmpty(param.getVendorRegion())) {
                safetyVisitorHairpinDO.setVendorRegion(param.getVendorRegion());
            }
            if(StringUtils.isNotEmpty(param.getSecretState())) {
                safetyVisitorHairpinDO.setSecretState(Integer.valueOf(param.getSecretState()));
            }
            if(StringUtils.isNotEmpty(param.getSecretRegion())) {
                safetyVisitorHairpinDO.setSecretRegion(param.getSecretRegion());
            }
            safetyVisitorHairpinDO.setUpdateTime(new Date());

            //流程中的访客没有上传人脸照片的不自动下发权限
            if(EipStateEnum.RELEASE.getCode().equals(param.getState()) && StringUtils.isNotEmpty(safetyVisitorHairpinDO.getPhoto())) {
                //查询访客的开关
                Integer openStatus = safetyVisitorHairpinDOMapper.getOpenStatus(OPEN_NAME);
                if(0==openStatus){
                    LogUtils.info("访客审批通过，自动下发权限");
                    Date startTime = safetyVisitorHairpinDO.getStartTime();
                    Date date = DateUtils.addMinutes(new Date(),40);
                    long abs = date.getTime()-startTime.getTime();

                    if(abs>=0){
                        String curIdCard = safetyVisitorHairpinDO.getVisitorIdCard();
                        List<SafetyEntryBlankProcessDetailDO> curBlackApplyList = null;
                        if(blackSet.contains(curIdCard)){
                            curBlackApplyList = blackApplyMap.get(curIdCard);
                            if(curBlackApplyList == null){
                                curBlackApplyList = new ArrayList<>();
                            }
                        }
                        sendCard(safetyVisitorHairpinDO,secretMap,curBlackApplyList);
                    }
                }
            }
            PreconditonsEx.checkArgument(safetyVisitorHairpinDOMapper.updateByPrimaryKey(safetyVisitorHairpinDO) == 1, ResponseEnum.FAIL);
            //检查是否已退卡，未退卡，直接将数据写入到未退卡表中
            if (StringUtils.isEmpty(safetyVisitorHairpinDO.getRecoveryNo())) {
                safetyCardRefundService.add(safetyVisitorHairpinDO.getId());
            }
        }
    }

    /**
     * 获取二级黑名单及黑名单对应的入厂申请权限
     * @param idCardList 身份证
     */
    public Map<String,List<SafetyEntryBlankProcessDetailDO>> filterBlankProcess(List<String> idCardList){
        Map<String,List<SafetyEntryBlankProcessDetailDO>> blankApplyMap = new HashMap<>();
        List<SafetyBlackMessageDO> blackMessageDOS = safetyBlackMessageService.getBlackMessageList(idCardList);
        if(CollectionUtils.isEmpty(blackMessageDOS)){
            return blankApplyMap;
        }
        //黑名单中的身份证集合
        Set<String> idCardSet = blackMessageDOS.stream().map(SafetyBlackMessageDO::getIdCardNo).collect(Collectors.toSet());
        //查询对应黑名单入厂申请集合-排除掉已失效的
        SafetyEntryBlankProcessDetailQueryParams detailQueryParams = new SafetyEntryBlankProcessDetailQueryParams();
        detailQueryParams.setCardNoList(new ArrayList<>(idCardSet));
        detailQueryParams.setApplyStatus("1"); //审批通过的
        detailQueryParams.setCheckNow(true); //校验当前未失效的
        detailQueryParams.setNullRecordType(true); //查询备案类型为空的数据
        List<SafetyEntryBlankProcessDetailDO> applyList = safetyEntryBlankProcessDetailService.selectList(detailQueryParams,false);
        Map<String,List<SafetyEntryBlankProcessDetailDO>> applyMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(applyList)){
            applyMap = applyList.stream().collect(Collectors.groupingBy(SafetyEntryBlankProcessDetailDO::getCardNo));
        }
        for(String key:idCardSet){
            blankApplyMap.put(key, applyMap.get(key));
        }
        return blankApplyMap;
    }

    /**
     * 处理黑名单内的访客，过滤入厂权限
     * @param safetyVisitorHairpinDO 访客申请数据
     * @param curBlackApplyList 对应的黑名单入厂申请权限集合  null表示非黑名单；空对象表示无入参权限
     */
    public void dealBlankApply(SafetyVisitorHairpinDO safetyVisitorHairpinDO,
                               List<SafetyEntryBlankProcessDetailDO> curBlackApplyList){
        if(curBlackApplyList == null){
            return;
        }
        String message = "访客"+safetyVisitorHairpinDO.getVisitorName()
                +"("+safetyVisitorHairpinDO.getVisitorIdCard()+")在黑名单内，"
                +safetyVisitorHairpinDO.getStartTime()+"~"+safetyVisitorHairpinDO.getEndTime()+"时间段内";
        if(CollectionUtils.isEmpty(curBlackApplyList)){
            LogUtils.info(message+"无入厂权限");
            safetyVisitorHairpinDO.setIssueFactory("");
            return;
        }
        //开始时间早于当前时间时，按照当前时间对比
        Date beginTime = safetyVisitorHairpinDO.getStartTime().before(new Date()) ? new Date() : safetyVisitorHairpinDO.getStartTime();
        Date endTime = safetyVisitorHairpinDO.getEndTime();
        List<SafetyEntryBlankProcessDetailDO> subList = curBlackApplyList.stream().
                filter(entity -> {
                    try {
                        // 解析实体时间字段
                        Date entityStart = DateUtils.parse(entity.getEnterStartTime().substring(0,16),3);
                        Date entityEnd = DateUtils.parse(entity.getEnterEndTime().substring(0,16),3);
                        // 核心交集判断逻辑：
                        // 判断实体时间段是否与目标时间段存在重叠
                        return !entityEnd.before(endTime)  && !entityStart.after(beginTime);
                    } catch (DateTimeParseException e) {
                        // 时间格式错误处理（可根据需要记录日志）
                        return false;
                    }
                })
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(subList)){
            LogUtils.info(message+"无入厂权限");
            safetyVisitorHairpinDO.setIssueFactory("");
            return;
        }
        String areaFactory = subList.stream().map(SafetyEntryBlankProcessDetailDO::getEnterFactoryCode).distinct().collect(Collectors.joining(","));
        Set<String> areaFactoryArr = new HashSet<>(Arrays.asList(areaFactory.split(",")));
        Set<String> targetSet = new HashSet<>(Arrays.asList(safetyVisitorHairpinDO.getIssueFactory().split(";")));
        Set<String> innerArr = areaFactoryArr.stream()
                .filter(targetSet::contains).collect(Collectors.toSet());// 只保留arr2中存在的元素
        String newFactoryA = String.join(";",innerArr);
        //实际下发厂区
        safetyVisitorHairpinDO.setIssueFactory(newFactoryA);
        LogUtils.info(message+"厂区权限【"+newFactoryA+"】");
    }

    /**
     * 处理涉密厂区
     * @param safetyVisitorHairpinDO
     * @param secretMap
     */
    public void dealSecret(SafetyVisitorHairpinDO safetyVisitorHairpinDO, Map<String, SafetyVisitorSecretDTO> secretMap){
        //实际下发厂区
        safetyVisitorHairpinDO.setIssueFactory(safetyVisitorHairpinDO.getFactoryId());
        //尚未下发厂区
        safetyVisitorHairpinDO.setUnIssueFactory("");
        if (ObjectUtils.isEmpty(secretMap) || (1 != safetyVisitorHairpinDO.getSecretState())) {
            return;
        }

        String secretRegion = safetyVisitorHairpinDO.getSecretRegion();
        String[] split = secretRegion.split(";");

        if(split.length == 0){
            return;
        }


        //获取探访工厂
        String factoryId = safetyVisitorHairpinDO.getFactoryId()+";";

        Set<String> factorySet = new HashSet<>(Arrays.asList(factoryId.split(";")));
        Set<String> unIssueFactorySet = new HashSet<>();

        String factoryA = "",unIssueFactory = "";
        for (String str : split) {
            if (!ObjectUtils.isEmpty(secretMap.get(str))) {
                String factoryCode = secretMap.get(str).getFactoryCode();
                if (!ObjectUtils.isEmpty(factoryCode) && !unIssueFactorySet.contains(factoryCode)) {
                    unIssueFactorySet.add(factoryCode);
                    factorySet.remove(factoryCode);
                }
            }
        }
        factoryA = String.join(";", factorySet);
        unIssueFactory = String.join(";", unIssueFactorySet);
        //实际下发厂区
        safetyVisitorHairpinDO.setIssueFactory(factoryA);
        //尚未下发厂区
        safetyVisitorHairpinDO.setUnIssueFactory(unIssueFactory);
    }

    /**
     * 权限下发
     * @param safetyVisitorHairpinDO
     * @param secretMap
     * @param curBlackApplyList 黑名单入厂申请清单 null标识不需要检验
     */
    public void sendCard(SafetyVisitorHairpinDO safetyVisitorHairpinDO,
                         Map<String, SafetyVisitorSecretDTO> secretMap,
                         List<SafetyEntryBlankProcessDetailDO> curBlackApplyList){
        boolean mark = false;
        LogMessageAddParam log = new LogMessageAddParam();

        //是否涉及绝密区，是否去除绝密区
        dealSecret(safetyVisitorHairpinDO,secretMap);
        String factoryA = safetyVisitorHairpinDO.getIssueFactory();

        if(StringUtils.isEmpty(factoryA)){
            LogUtils.info("{\"code\":\"8888\",\"msg\":\"方法sendCard，参数：ID="+safetyVisitorHairpinDO.getId()+"," +
                    "secret_state="+safetyVisitorHairpinDO.getSecretState()+
                    ",secret_region="+safetyVisitorHairpinDO.getSecretRegion()+
                    ",factory_id="+safetyVisitorHairpinDO.getFactoryId()+"权限下发时工厂为空，不下发！\"}");
            return;
        }

        //是否黑名单，是否根据黑名单入厂申请记录过滤入厂权限
        if(curBlackApplyList != null){
            dealBlankApply(safetyVisitorHairpinDO,curBlackApplyList);
        }

        //获取员工号
        String staffNo = getEipNoLog(safetyVisitorHairpinDO.getVisitedName(), safetyVisitorHairpinDO.getVisitedPhone());
        if (StringUtils.isNotEmpty(staffNo)) {
            log.setOperator(staffNo);
            SafetySysUserDO safetySysUserDO = safetySysUserService.getUserMessage(staffNo);
            if(!ObjectUtils.isEmpty(safetySysUserDO)){
                safetyVisitorHairpinDO.setUserId(safetySysUserDO.getId());
            }
        }else{
            LogUtils.info("从ladp取被访人时为空，被访人：{}，手机号：{}",safetyVisitorHairpinDO.getVisitedName(),safetyVisitorHairpinDO.getVisitedPhone());
            safetyVisitorHairpinDO.setInFactoryProcess("异常");
            safetyVisitorHairpinDO.setUserId(null);
            //实际下发厂区
            safetyVisitorHairpinDO.setIssueFactory("");
            //尚未下发厂区
            safetyVisitorHairpinDO.setUnIssueFactory("");
            return;
        }

        //发卡的时候，将已发卡，但是没退卡的数据，全部置为已退卡
        hairpinNoBackNoReview(safetyVisitorHairpinDO.getHairpinNo());
        //发卡动作的时候，调用发卡接口进行数据下发动作，人脸。
        if (StringUtils.isNotEmpty(safetyVisitorHairpinDO.getPhoto())) {
            SafetyVisitorHairpinDO info = mergeParam(safetyVisitorHairpinDO, secretMap);
            if (StringUtils.isEmpty(info.getIssueFactory())) {
                LogUtils.info("{\"code\":\"8888\",\"msg\":\"方法sendCard，参数：ID="+safetyVisitorHairpinDO.getId()+"," +
                        "secret_state="+safetyVisitorHairpinDO.getSecretState()+
                        ",secret_region="+safetyVisitorHairpinDO.getSecretRegion()+
                        ",is_sure_factory="+safetyVisitorHairpinDO.getIssueFactory()+"权限下发时工厂为空，不下发！\"}");
                return;
            }
            String mes = safetyDataSendLogService.sendDataRecord(info, "0", "", "人脸", false, info.getHairpinNo(), info.getIssueFactory());

            log.setOperatePage("访客发卡");
            log.setAbility("人脸");
            String msg = "[访客流程通过]:";
            if (StringUtils.isNotEmpty(safetyVisitorHairpinDO.getIdCardPhoto())) {
                msg = "[访客上传身份证(" + safetyVisitorHairpinDO.getIdCardPhoto() + ")]:";
            }
            log.setMessage(msg + safetyVisitorHairpinDO.getVisitorName()+";"+safetyVisitorHairpinDO.getVisitorPhone()+";"+safetyVisitorHairpinDO.getVisitorIdCard());
            safetyLogMessageService.addLogMessage(log);

            if ("true".equals(mes)) {
                mark = true;
            }
        }else{
            mark = true;
        }
        if (mark) {
            safetyVisitorHairpinDO.setInFactoryProcess("正常");
            safetyVisitorHairpinDO.setVendorState(1);
            //发卡时间
            safetyVisitorHairpinDO.setHairpinTime(new Date());
        }else{
            safetyVisitorHairpinDO.setInFactoryProcess("异常");
            safetyVisitorHairpinDO.setUserId(null);
            //实际下发厂区
            safetyVisitorHairpinDO.setIssueFactory("");
            //尚未下发厂区
            safetyVisitorHairpinDO.setUnIssueFactory("");
        }
    }


    public void sendCardByHand(SafetyVisitorHairpinDO safetyVisitorHairpinDO,SafetySysUserDO safetySysUserDO,String workNo){
        boolean mark = false;
        safetyVisitorHairpinDO.setUserId(safetySysUserDO.getId());

        //日志对象
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperator(workNo);

        //下发人实际拥有权限的厂区
        String factoryA = safetySysUserDO.getFactoryId();
        List<String> downSplit = Arrays.asList(factoryA.split(","));

        //未下发厂区
        String unIssueFactory = "";
        //原有的已经下发工厂
        String issueFactory = StringUtils.isEmpty(safetyVisitorHairpinDO.getIssueFactory())?"":safetyVisitorHairpinDO.getIssueFactory();

        //访客实际请求的所有访问厂区
        String vendorFactory = safetyVisitorHairpinDO.getFactoryId();
        String[] vendorSplit = vendorFactory.split(";");

        //在已下发厂区后面追加  新下发的厂区（下发人所辖的所有厂区，包含涉密厂区）
        for (String s : vendorSplit) {
            if(issueFactory.contains(s)){continue;}

            if(downSplit.contains(s)){
                issueFactory += (";" + s);
            }else{
                unIssueFactory += (s + ";");
            }
        }

        //处理数据格式
        if(issueFactory.startsWith(";")){
            issueFactory = issueFactory.substring(1);
        }
        if(issueFactory.endsWith(";")){
            issueFactory = issueFactory.substring(0,issueFactory.length()-1);
        }
        if(unIssueFactory.endsWith(";")){
            unIssueFactory = unIssueFactory.substring(0,unIssueFactory.length()-1);
        }

        //发卡的时候，将已发卡，但是没退卡的数据，全部置为已退卡
        //注销所有同一的卡号
        hairpinNoBackNoReview(safetyVisitorHairpinDO.getHairpinNo());

        //发卡动作的时候，调用发卡接口进行数据下发动作，人脸。
        if (StringUtils.isNotEmpty(safetyVisitorHairpinDO.getPhoto())) {
            // 合并下发的参数
            SafetyVisitorHairpinDO info = mergeParam(safetyVisitorHairpinDO, new HashMap<>());
            check(info, downSplit);
            if (StringUtils.isEmpty(info.getIssueFactory())) {
                return;
            }
            // 此处按有身份证下发， 所以没有值时，给他一个照片，为下发中的判断做准备
            if (StringUtils.isEmpty(info.getIdCardPhoto())) {
                info.setIdCardPhoto(ID_CARD_PHOTO_DEFAULT_VALUE);
            }
            String mes = safetyDataSendLogService.sendDataRecord(info, "0", "", "人脸", false, info.getHairpinNo(), info.getIssueFactory());

            log.setOperatePage("访客发卡");
            log.setAbility("人脸");
            log.setMessage("[门卫核验访客身份]:" + safetyVisitorHairpinDO.getVisitorName()+";"+safetyVisitorHairpinDO.getVisitorPhone()+";"+safetyVisitorHairpinDO.getVisitorIdCard());
            safetyLogMessageService.addLogMessage(log);

            if ("true".equals(mes)) {
                mark = true;
            }
        }else{
            mark = true;
        }
        if (mark) {
            safetyVisitorHairpinDO.setInFactoryProcess("正常");
            // 访客页面权限下发和批量下发，状态值有之前的1，已发卡调整成11，权限已下发
            safetyVisitorHairpinDO.setVendorState(11);
            //发卡时间
            safetyVisitorHairpinDO.setHairpinTime(new Date());
            //实际下发厂区
            safetyVisitorHairpinDO.setIssueFactory(issueFactory);
            //未下发厂区
            safetyVisitorHairpinDO.setUnIssueFactory(unIssueFactory);
        }else{
            safetyVisitorHairpinDO.setInFactoryProcess("异常");
            safetyVisitorHairpinDO.setUserId(null);
        }
    }


    public SafetyVisitorHairpinDO mergeParam(SafetyVisitorHairpinDO en, Map<String, SafetyVisitorSecretDTO> secretMap) {
        String visitorCard = en.getVisitorPhone() + "" + en.getVisitorIdCard();
        // 查询需要合并下发的人员
        List<SafetyVisitorHairpinDO> infoList = this.selectListOfPhoneIdCard(Collections.singleton(visitorCard));
        infoList.add(en);
        SafetyVisitorHairpinDO info = new SafetyVisitorHairpinDO();
        BeanUtils.copyProperties(en, info);
        if (CollectionUtils.isNotEmpty(infoList) && infoList.size() > 1) {
            String factoryIds = mergeFieldValues(infoList.stream().map(SafetyVisitorHairpinDO::getFactoryId));
            String isSureFactory = mergeFieldValues(infoList.stream().map(SafetyVisitorHairpinDO::getIssueFactory));
            String unIsSureFactory = mergeFieldValues(infoList.stream().map(SafetyVisitorHairpinDO::getUnIssueFactory));
            String secretRegions = mergeFieldValues(infoList.stream().map(SafetyVisitorHairpinDO::getSecretRegion));
            List<String> idCardPhotos = infoList.stream().map(SafetyVisitorHairpinDO::getIdCardPhoto).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
            Date startTime = infoList.stream().map(SafetyVisitorHairpinDO::getEndTime).filter(Objects::nonNull).max(Date::compareTo).orElse(en.getEndTime());
            boolean b = infoList.stream().map(SafetyVisitorHairpinDO::getSecretState).anyMatch(li -> li == 1);
            info.setFactoryId(factoryIds);
            info.setIssueFactory(isSureFactory);
            info.setUnIssueFactory(unIsSureFactory);
            info.setEndTime(startTime);
            info.setSecretState(b ? 1 : 0);
            info.setSecretRegion(secretRegions);
            if (CollectionUtils.isNotEmpty(idCardPhotos)) {
                // 无实际下发作用，只用于判断下发类型
                info.setIdCardPhoto(idCardPhotos.get(0));
            }
        }
        //是否涉及绝密区，是否去除绝密区
        if (secretMap.size() > 1) {
            dealSecret(info, secretMap);
        }
        return info;
    }

    private String mergeFieldValues(Stream<String> fieldStream) {
        return fieldStream
                .filter(StringUtils::isNotEmpty)
                .flatMap(s -> Arrays.stream(s.split(";")))
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.joining(";"));
    }

    private void check(SafetyVisitorHairpinDO safetyVisitorHairpinDO, List<String> downSplit) {
        //未下发厂区
        String unIssueFactory = "";
        //原有的已经下发工厂
        String issueFactory = StringUtils.isEmpty(safetyVisitorHairpinDO.getIssueFactory())?"":safetyVisitorHairpinDO.getIssueFactory();

        //访客实际请求的所有访问厂区
        String vendorFactory = safetyVisitorHairpinDO.getFactoryId();
        String[] vendorSplit = vendorFactory.split(";");

        //在已下发厂区后面追加  新下发的厂区（下发人所辖的所有厂区，包含涉密厂区）
        for (String s : vendorSplit) {
            if(issueFactory.contains(s)){continue;}

            if(downSplit.contains(s)){
                issueFactory += (";" + s);
            }else{
                unIssueFactory += (s + ";");
            }
        }
        if(issueFactory.startsWith(";")){
            issueFactory = issueFactory.substring(1);
        }
        safetyVisitorHairpinDO.setIssueFactory(issueFactory);
    }


    public void sendCardByHandList(SafetyVisitorHairpinDO safetyVisitorHairpinDO,SafetySysUserDO safetySysUserDO,String workNo,Map<String, SafetyVisitorSecretDTO> secretMap){
        //设置成功与否的标识
        boolean mark = false;

        //设置下发人
        safetyVisitorHairpinDO.setUserId(safetySysUserDO.getId());

        //创建日志对象
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperator(workNo);

        //获取下发人实际拥有权限的厂区
        String factoryA = safetySysUserDO.getFactoryId();
        List<String> downSplit = Arrays.asList(factoryA.split(","));

        //获取实际请求访问的厂区
        String vendorFactory = safetyVisitorHairpinDO.getFactoryId();
        String[] vendorSplit = vendorFactory.split(";");

        //筛选涉密厂区
        StringBuilder secretSb = new StringBuilder();
        if(1==safetyVisitorHairpinDO.getSecretState() && StringUtils.isNotEmpty(safetyVisitorHairpinDO.getSecretRegion())){
            String[] secretArray = safetyVisitorHairpinDO.getSecretRegion().split(";");
            for (String s : secretArray) {
                if(!ObjectUtils.isEmpty(secretMap.get(s))){
                    secretSb.append(secretMap.get(s).getFactoryCode()).append(";");
                }
            }
        }
        String secretStr = secretSb.toString();

        //初始化未下发厂区/下发厂区
        String unIssueFactory = "",issueFactory = "";
        //一键下发权限按钮只下发自己所辖并且非涉密的厂区
        for (String s : vendorSplit) {
            if(issueFactory.contains(s)){continue;}

            if(downSplit.contains(s) && !secretStr.contains(s)){
                issueFactory +=  (s + ";");
            }else{
                unIssueFactory += (s + ";");
            }
        }

        if(StringUtils.isEmpty(issueFactory)){return;}

        //处理数据格式
        if(issueFactory.endsWith(";")){
            issueFactory = issueFactory.substring(0,issueFactory.length()-1);
        }
        if(unIssueFactory.endsWith(";")){
            unIssueFactory = unIssueFactory.substring(0,unIssueFactory.length()-1);
        }

        //发卡的时候，将已发卡，但是没退卡的数据，全部置为已退卡
        //注销所有同一的卡号
        hairpinNoBackNoReview(safetyVisitorHairpinDO.getHairpinNo());

        //发卡动作的时候，调用发卡接口进行数据下发动作，人脸。
        if (StringUtils.isNotEmpty(safetyVisitorHairpinDO.getPhoto())) {
            // 合并下发的参数
            SafetyVisitorHairpinDO info = mergeParam(safetyVisitorHairpinDO, new HashMap<>());
            check(info, secretMap, downSplit);
            if (StringUtils.isEmpty(info.getIssueFactory())) {
                return;
            }
            // 此处按有身份证下发， 所以没有值时，给他一个照片，为下发中的判断做准备
            if (StringUtils.isEmpty(info.getIdCardPhoto())) {
                info.setIdCardPhoto(ID_CARD_PHOTO_DEFAULT_VALUE);
            }
            String mes = safetyDataSendLogService.sendDataRecord(info, "0", "", "人脸", false, info.getHairpinNo(), info.getIssueFactory());

            log.setOperatePage("访客发卡");
            log.setAbility("人脸");
            log.setMessage("[门卫核验访客身份]:" + safetyVisitorHairpinDO.getVisitorName()+";"+safetyVisitorHairpinDO.getVisitorPhone()+";"+safetyVisitorHairpinDO.getVisitorIdCard());
            safetyLogMessageService.addLogMessage(log);

            if ("true".equals(mes)) {
                mark = true;
            }
        }else{
            mark = true;
        }
        if (mark) {
            safetyVisitorHairpinDO.setInFactoryProcess("正常");
            // 访客页面权限下发和批量下发，状态值有之前的1，已发卡调整成11，权限已下发
            safetyVisitorHairpinDO.setVendorState(11);
            //发卡时间
            safetyVisitorHairpinDO.setHairpinTime(new Date());
            //实际下发厂区
            safetyVisitorHairpinDO.setIssueFactory(issueFactory);
            //未下发厂区
            safetyVisitorHairpinDO.setUnIssueFactory(unIssueFactory);
        }else{
            safetyVisitorHairpinDO.setInFactoryProcess("异常");
            safetyVisitorHairpinDO.setUserId(null);
        }
    }

    public void check(SafetyVisitorHairpinDO safetyVisitorHairpinDO, Map<String, SafetyVisitorSecretDTO> secretMap, List<String> downSplit) {

        //筛选涉密厂区
        StringBuilder secretSb = new StringBuilder();
        if(1==safetyVisitorHairpinDO.getSecretState() && StringUtils.isNotEmpty(safetyVisitorHairpinDO.getSecretRegion())){
            String[] secretArray = safetyVisitorHairpinDO.getSecretRegion().split(";");
            for (String s : secretArray) {
                if(!ObjectUtils.isEmpty(secretMap.get(s))){
                    secretSb.append(secretMap.get(s).getFactoryCode()).append(";");
                }
            }
        }
        String secretStr = secretSb.toString();

        if (StringUtils.isEmpty(safetyVisitorHairpinDO.getFactoryId())) {
            return;
        }
        //初始化未下发厂区/下发厂区
        String unIssueFactory = "",issueFactory = "";
        //一键下发权限按钮只下发自己所辖并且非涉密的厂区
        for (String s : safetyVisitorHairpinDO.getFactoryId().split("")) {
            if(issueFactory.contains(s)){continue;}

            if(downSplit.contains(s) && !secretStr.contains(s)){
                issueFactory +=  (s + ";");
            }else{
                unIssueFactory += (s + ";");
            }
        }
        if(issueFactory.startsWith(";")){
            issueFactory = issueFactory.substring(1);
        }
        safetyVisitorHairpinDO.setIssueFactory(issueFactory);
    }

    public SafetyVisitorHairpinDO getById(Integer id){
        return safetyVisitorHairpinDOMapper.selectByPrimaryKey(id);
    }
    public List<SafetyVisitorHairpinDO> getByIdList(List<Integer> ids){
        return safetyVisitorHairpinDOBizMapper.selectByPrimaryKeyList(ids);
    }
    public void updateByEn(List<SafetyVisitorHairpinDO> list){
        safetyVisitorHairpinDOMapper.updateByEn(list);
    }

    /**
     * 查询申请入场开始时间小于等于当前时间加40分钟的未发卡的数据
     * @return
     */
    public List<SafetyVisitorHairpinDO> getDsDate(){
        SafetyVisitorHairpinDOExample example = new SafetyVisitorHairpinDOExample();
        SafetyVisitorHairpinDOExample.Criteria criteria = example.createCriteria();
        criteria.andVendorStateEqualTo(0);
        Date date = DateUtils.addMinutes(new Date(), 40);
        criteria.andStartTimeLessThanOrEqualTo(date);
        criteria.andStartTimeGreaterThanOrEqualTo(new Date());
        return safetyVisitorHairpinDOMapper.selectByExample(example);
    }

    public void updateVendorStates(List<SafetyVisitorHairpinDO> list){
        safetyVisitorHairpinDOMapper.updateVendorStates(list);
    }

    /**
     * 根据eip流程id获取数据
     * @param eipFlowId
     * @return
     */
    public SafetyVisitorHairpinDO getVisitorByEipId(String eipFlowId, String visitorIdCard) {
        SafetyVisitorHairpinDOExample example = new SafetyVisitorHairpinDOExample();
        SafetyVisitorHairpinDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andEipFlowIdEqualTo(eipFlowId);
        criteria.andVisitorIdCardEqualTo(visitorIdCard);
        List<SafetyVisitorHairpinDO> list = safetyVisitorHairpinDOMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 根据eipid获取对应数据列表
     * @param eipFlowId
     * @return
     */
    public List<SafetyVisitorHairpinDO> getVisitorListByEipId(String eipFlowId,Integer vendorState) {
        SafetyVisitorHairpinDOExample example = new SafetyVisitorHairpinDOExample();
        SafetyVisitorHairpinDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andEipFlowIdEqualTo(eipFlowId);
        if(vendorState != null){
            criteria.andVendorStateEqualTo(vendorState);
        }
        List<SafetyVisitorHairpinDO> list = safetyVisitorHairpinDOMapper.selectByExample(example);
        return list;
    }

    public void addEipFlow(VisitorHairpinAddParam param) {
        SafetyVisitorHairpinDO visitorHairpinDO = new SafetyVisitorHairpinDO();
        visitorHairpinDO.setVisitedName(param.getVisitedName());
        visitorHairpinDO.setVisitedPhone(param.getVisitedPhone());
        visitorHairpinDO.setVisitorName(param.getVisitorName());
        visitorHairpinDO.setVisitorPhone(param.getVisitorPhone());
        visitorHairpinDO.setVisitorIdCard(param.getVisitorIdCard());
        visitorHairpinDO.setVendorUnit(param.getVendorUnit());
        visitorHairpinDO.setVendorMatter(param.getVendorMatter());
        visitorHairpinDO.setCardNo(param.getCardNo());
        visitorHairpinDO.setStartTime(DateUtils.parse(param.getStartTime(), 3));
        visitorHairpinDO.setEndTime(DateUtils.parse(param.getEndTime(), 3));
        visitorHairpinDO.setCreateTime(new Date());
        visitorHairpinDO.setUpdateTime(new Date());
        visitorHairpinDO.setDeleteFalg(false);
        visitorHairpinDO.setVendorState(VisitorHairpinEnum.WFK.getCode());
        visitorHairpinDO.setSecretState(VisitorHairpinEnum.WFK.getCode());
        visitorHairpinDO.setEipFlowId(param.getEipId());
        PreconditonsEx.checkArgument(safetyVisitorHairpinDOMapper.insert(visitorHairpinDO) == 1, ResponseEnum.FAIL);
    }

    /**
     * 分页数据列表查询
     * @param param
     * @return
     */
    public List<VisitorHirpinAppDTO> pageDetailList(VisitorHairpinListParam param) {
        SafetyVisitorHairpinDOExample example = new SafetyVisitorHairpinDOExample();
        SafetyVisitorHairpinDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if(StringUtils.isNotEmpty(param.getVisitorName())) {
            criteria.andVisitorNameLike("%"+param.getVisitorName()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVisitorPhone())) {
            criteria.andVisitorPhoneLike("%"+param.getVisitorPhone()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVisitorIdCard())) {
            criteria.andVisitorIdCardLike("%"+param.getVisitorIdCard()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVisitedName())) {
            criteria.andVisitedNameLike("%"+param.getVisitedName()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVisitedPhone())) {
            criteria.andVisitedPhoneLike("%"+param.getVisitedPhone()+"%");
        }
        if(Objects.nonNull(param.getVendorState())) {
            criteria.andVendorStateEqualTo(param.getVendorState());
        }
        if (StringUtils.isNotEmpty(param.getVendorFactory())) {
            criteria.andVendorFactoryEqualTo(param.getVendorFactory());
        }
        if(StringUtils.isNotEmpty(param.getVendorRegion())) {
            criteria.andVendorRegionLike("%"+param.getVendorRegion()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVendorMatter())) {
            criteria.andVendorMatterLike("%"+param.getVendorMatter()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVendorUnit())) {
            criteria.andVendorUnitLike("%"+param.getVendorUnit()+"%");
        }
        if(StringUtils.isNotEmpty(param.getCardNo())) {
            criteria.andCardNoLike("%"+param.getCardNo()+"%");
        }
        if (StringUtils.isNotEmpty(param.getHairpinTime())) {
            String start = param.getHairpinTime() + " 00:00:00";
            criteria.andHairpinTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
            String end = param.getHairpinTime() + " 23:59:59";
            criteria.andHairpinTimeLessThanOrEqualTo(DateUtils.parse(end,2));
        }
        if(StringUtils.isNotEmpty(param.getRecoveryTime())) {
            String start = param.getRecoveryTime() + " 00:00:00";
            criteria.andRecoveryTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
            String end = param.getRecoveryTime() + " 23:59:59";
            criteria.andRecoveryTimeLessThanOrEqualTo(DateUtils.parse(end,2));
        }
        if (Objects.nonNull(param.getFactoryId())) {
            criteria.andFactoryIdLike("%"+param.getFactoryId()+"%");
        }
        if (StringUtils.isNotEmpty(param.getStartTime())) {
            String startDate = param.getStartTime() + " 00:00:00";
            criteria.andStartTimeGreaterThanOrEqualTo(DateUtils.parse(startDate,2));
        }
        if (StringUtils.isNotEmpty(param.getEndTime())) {
            String endDate = param.getEndTime() + " 23:59:59";
            criteria.andStartTimeLessThanOrEqualTo(DateUtils.parse(endDate,2));
        }
        if (StringUtils.isNotEmpty(param.getHairpinNo())) {
            criteria.andHairpinNoLike("%" + param.getHairpinNo() + "%");
        }

        if (StringUtils.isNotEmpty(param.getInFactoryProcess())) {
            criteria.andInFactoryProcessEqualTo(param.getInFactoryProcess());
        }
        if (StringUtils.isNotEmpty(param.getActInFactory()) && StringUtils.isNotEmpty(LOCATIONS_ID.get(param.getActInFactory()))) {
            criteria.andActInFactoryLike("%" + LOCATIONS_ID.get(param.getActInFactory()) + "%");
        }

        if(StringUtils.isNotEmpty(param.getSecretState())) {
            criteria.andSecretStateEqualTo(Integer.parseInt(param.getSecretState()));
        }

        example.setOrderByClause(" create_time desc ");
        example.setLimit(param.getLimit());
        example.setOffset(param.getOffset());
        List<SafetyVisitorHairpinDO> list = safetyVisitorHairpinDOMapper.selectByExample(example);
        List<VisitorHirpinAppDTO> newList = Lists.newArrayList();
        VisitorHirpinAppDTO appDTO = null;
        Date nowTime = new Date();
        for (SafetyVisitorHairpinDO hairpinDO : list) {
            appDTO = new VisitorHirpinAppDTO();
            BeanUtils.copyProperties(hairpinDO, appDTO);
            appDTO.setNowTime(nowTime);
            appDTO.setVendorUnit(SensitiveUtils.maskName(hairpinDO.getVendorUnit()));
            appDTO.setVisitorName(SensitiveUtils.maskName(hairpinDO.getVisitorName()));
            appDTO.setVisitorIdCard(SensitiveUtils.maskCardId(hairpinDO.getVisitorIdCard()));
            appDTO.setVisitorPhone(SensitiveUtils.maskPhoneNumber(hairpinDO.getVisitorPhone()));
            newList.add(appDTO);
        }
        return newList;
    }

    /**
     * 总数据量
     * @param param
     * @return
     */
    public long pageDetailListCount(VisitorHairpinListParam param) {
        SafetyVisitorHairpinDOExample example = new SafetyVisitorHairpinDOExample();
        SafetyVisitorHairpinDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if(StringUtils.isNotEmpty(param.getVisitorName())) {
            criteria.andVisitorNameLike("%"+param.getVisitorName()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVisitorPhone())) {
            criteria.andVisitorPhoneLike("%"+param.getVisitorPhone()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVisitorIdCard())) {
            criteria.andVisitorIdCardLike("%"+param.getVisitorIdCard()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVisitedName())) {
            criteria.andVisitedNameLike("%"+param.getVisitedName()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVisitedPhone())) {
            criteria.andVisitedPhoneLike("%"+param.getVisitedPhone()+"%");
        }
        if(Objects.nonNull(param.getVendorState())) {
            criteria.andVendorStateEqualTo(param.getVendorState());
        }
        if (StringUtils.isNotEmpty(param.getVendorFactory())) {
            criteria.andVendorFactoryEqualTo(param.getVendorFactory());
        }
        if(StringUtils.isNotEmpty(param.getVendorRegion())) {
            criteria.andVendorRegionLike("%"+param.getVendorRegion()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVendorMatter())) {
            criteria.andVendorMatterLike("%"+param.getVendorMatter()+"%");
        }
        if(StringUtils.isNotEmpty(param.getVendorUnit())) {
            criteria.andVendorUnitLike("%"+param.getVendorUnit()+"%");
        }
        if(StringUtils.isNotEmpty(param.getCardNo())) {
            criteria.andCardNoLike("%"+param.getCardNo()+"%");
        }
        if (StringUtils.isNotEmpty(param.getHairpinTime())) {
            String start = param.getHairpinTime() + " 00:00:00";
            criteria.andHairpinTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
            String end = param.getHairpinTime() + " 23:59:59";
            criteria.andHairpinTimeLessThanOrEqualTo(DateUtils.parse(end,2));
        }
        if(StringUtils.isNotEmpty(param.getRecoveryTime())) {
            String start = param.getRecoveryTime() + " 00:00:00";
            criteria.andRecoveryTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
            String end = param.getRecoveryTime() + " 23:59:59";
            criteria.andRecoveryTimeLessThanOrEqualTo(DateUtils.parse(end,2));
        }
        if (Objects.nonNull(param.getFactoryId())) {
            criteria.andFactoryIdLike("%"+param.getFactoryId()+"%");
        }
        if (StringUtils.isNotEmpty(param.getStartTime())) {
            String startDate = param.getStartTime() + " 00:00:00";
            criteria.andStartTimeGreaterThanOrEqualTo(DateUtils.parse(startDate,2));
        }
        if (StringUtils.isNotEmpty(param.getEndTime())) {
            String endDate = param.getEndTime() + " 23:59:59";
            criteria.andStartTimeLessThanOrEqualTo(DateUtils.parse(endDate,2));
        }
        if (StringUtils.isNotEmpty(param.getHairpinNo())) {
            criteria.andHairpinNoLike("%" + param.getHairpinNo() + "%");
        }

        if (StringUtils.isNotEmpty(param.getInFactoryProcess())) {
            criteria.andInFactoryProcessEqualTo(param.getInFactoryProcess());
        }
        if (StringUtils.isNotEmpty(param.getActInFactory()) && StringUtils.isNotEmpty(LOCATIONS_ID.get(param.getActInFactory()))) {
            criteria.andActInFactoryLike("%" + LOCATIONS_ID.get(param.getActInFactory()) + "%");
        }
        return safetyVisitorHairpinDOMapper.countByExample(example);
    }

    /**
     * 根据id获取值
     * @param param
     * @return
     */
    public List<VisitorHirpinAppDTO> listByWxPhone(VisitorAppListParam param) {
        List<SafetyVisitorHairpinDO> list = safetyVisitorHairpinDOMapper.listByWxPhone(param);
        if(CollectionUtils.isEmpty(list)){return null;}
        List<VisitorHirpinAppDTO> visitorList = Lists.newArrayList();
        VisitorHirpinAppDTO hirpin = null;
        Date nowTime = new Date();
        for (SafetyVisitorHairpinDO visitor : list) {
            hirpin = new VisitorHirpinAppDTO();
            BeanUtils.copyProperties(visitor, hirpin);
            hirpin.setNowTime(nowTime);
            visitorList.add(hirpin);
        }
        return visitorList;
    }

    /**
     * 条数
     * @param param
     * @return
     */
    public int listByWxPhoneCount(VisitorAppListParam param) {
        SafetyVisitorHairpinDOExample example = new SafetyVisitorHairpinDOExample();
        SafetyVisitorHairpinDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andWxPhoneEqualTo(param.getWxPhone());
        List<SafetyVisitorHairpinDO> list = safetyVisitorHairpinDOMapper.selectByExample(example);
        return list.size();
    }

    /**
     * 根据id获取详情
     * @param id
     * @return
     */
    public SafetyVisitorHairpinDTO detailById(Integer id) {
        SafetyVisitorHairpinDO safetyVisitorHairpinDO = safetyVisitorHairpinDOMapper.selectByPrimaryKey(id);
        if(safetyVisitorHairpinDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }

        SafetyVisitorHairpinDTO safetyVisitorHairpinDTO = new SafetyVisitorHairpinDTO();
        BeanUtils.copyProperties(safetyVisitorHairpinDO, safetyVisitorHairpinDTO);

        //如果实际下发厂区为空，就代表没有进场权限
        if(StringUtils.isEmpty(safetyVisitorHairpinDO.getIssueFactory())){
            return safetyVisitorHairpinDTO;
        }

        if (safetyVisitorHairpinDTO.getVendorState() == 1 || safetyVisitorHairpinDTO.getVendorState() == 8 || safetyVisitorHairpinDTO.getVendorState() == 9) {
            if (DateUtils.sub(safetyVisitorHairpinDTO.getEndTime(), new Date()) > 0) {
                //获取当初发卡的人员权限id
                Integer userId = safetyVisitorHairpinDTO.getUserId();
                /*String authFactory = null;
                if (Objects.nonNull(userId)) {
                    //获取人员
                    SafetySysUserDO safetySysUserDO = safetySysUserService.safetySysUserDOById(userId);
                    authFactory = safetySysUserDO.getFactoryId();
                }*/
                //直接取实际下发的厂区
                String authFactory = safetyVisitorHairpinDO.getIssueFactory();

                String endDate = DateUtils.SECODEDATE(35);
                //二维码数据下发
                String eqCode = safetyDataSendLogService.sendDataRecord(safetyVisitorHairpinDO, "1", endDate, "人脸", false, safetyVisitorHairpinDO.getHairpinNo(), authFactory);
                safetyVisitorHairpinDTO.setQrCode(eqCode);
                if (StringUtils.isNotEmpty(safetyVisitorHairpinDO.getFactoryId()) && !ObjectUtils.isEmpty(userId)) {
                    authFactory = StringUtils.isNotEmpty(authFactory) ? authFactory.replaceAll(";",",") : authFactory;
                    safetyVisitorHairpinDTO.setAuthFactoryNames(authFactoryNames(safetyVisitorHairpinDTO.getFactoryId(), safetyVisitorHairpinDTO.getVendorFactory(), authFactory));
                }

                //已授权厂区
                if(StringUtils.isNotEmpty(safetyVisitorHairpinDO.getIssueFactory())){
                    String[] split = safetyVisitorHairpinDO.getIssueFactory().split(";");
                    List<Integer> factoryList = Arrays.stream(split).map(Integer::parseInt).collect(Collectors.toList());
                    safetyVisitorHairpinDTO.setIssueFactory(safetyFieldManageService.getNamesByListIds(factoryList));
                }

                //未授权厂区
                if(StringUtils.isNotEmpty(safetyVisitorHairpinDO.getUnIssueFactory())){
                    String[] unSplit = safetyVisitorHairpinDO.getUnIssueFactory().split(";");
                    List<Integer> unFactoryList = Arrays.stream(unSplit).map(Integer::parseInt).collect(Collectors.toList());
                    safetyVisitorHairpinDTO.setUnIssueFactory(safetyFieldManageService.getNamesByListIds(unFactoryList));
                }
            }
        }
        return safetyVisitorHairpinDTO;
    }

    /**
     * 获取授权的厂区名称
     * @param factoryIds
     * @param userFactoryIds
     * @return
     */
    public String authFactoryNames(String factoryIds, String factoryNames, String userFactoryIds) {
        if (StringUtils.isEmpty(userFactoryIds)) {
            return factoryNames;
        }
        String[] visiteds = factoryIds.split(";");
        List<Integer> visitedList = Arrays.stream(visiteds).map(Integer::parseInt).collect(Collectors.toList());
        String[] users = userFactoryIds.split(",");
        List<Integer> userList = Arrays.stream(users).map(Integer::parseInt).collect(Collectors.toList());
        List<Integer> intersection = visitedList.stream().filter(userList::contains).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(intersection)){
            return "";
        }
        return safetyFieldManageService.getNamesByListIds(intersection);
    }

    /**
     * 定时更改状态
     */
    public void updateStates() {
        safetyVisitorHairpinDOBizMapper.updateStates();
    }

    public void updateZLStates() {
        safetyVisitorHairpinDOBizMapper.updateZLStates();
    }

    /**
     * 数据导出功能
     * @param param
     * @return
     */
    public List<SafetyVisitorHairpinDO> export(VisitorHairpinListParam param, String tocken) {
        SafetyVisitorHairpinDOExample example = new SafetyVisitorHairpinDOExample();
        SafetyVisitorHairpinDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        String searchStr = "";
        if(StringUtils.isNotEmpty(param.getVisitorName())) {
            criteria.andVisitorNameLike("%"+param.getVisitorName()+"%");
            searchStr += param.getVisitorName() + ";";
        }
        if(StringUtils.isNotEmpty(param.getVisitorPhone())) {
            criteria.andVisitorPhoneLike("%"+param.getVisitorPhone()+"%");
            searchStr += param.getVisitorPhone() + ";";
        }
        if(StringUtils.isNotEmpty(param.getVisitorIdCard())) {
            criteria.andVisitorIdCardLike("%"+param.getVisitorIdCard()+"%");
            searchStr += param.getVisitorIdCard() + ";";
        }
        if(StringUtils.isNotEmpty(param.getVisitedName())) {
            criteria.andVisitedNameLike("%"+param.getVisitedName()+"%");
            searchStr += param.getVisitedName() + ";";
        }
        if(StringUtils.isNotEmpty(param.getVisitedPhone())) {
            criteria.andVisitedPhoneLike("%"+param.getVisitedPhone()+"%");
            searchStr += param.getVisitedPhone() + ";";
        }
        if(Objects.nonNull(param.getVendorState())) {
            criteria.andVendorStateEqualTo(param.getVendorState());
            searchStr += param.getVendorState() + ";";
        }
        if (StringUtils.isNotEmpty(param.getVendorFactory())) {
            criteria.andVendorFactoryEqualTo(param.getVendorFactory());
            searchStr += param.getVendorFactory() + ";";
        }
        if(StringUtils.isNotEmpty(param.getVendorRegion())) {
            criteria.andVendorRegionLike("%"+param.getVendorRegion()+"%");
            searchStr += param.getVendorRegion() + ";";
        }
        if(StringUtils.isNotEmpty(param.getVendorMatter())) {
            criteria.andVendorMatterLike("%"+param.getVendorMatter()+"%");
            searchStr += param.getVendorMatter() + ";";
        }
        if(StringUtils.isNotEmpty(param.getVendorUnit())) {
            criteria.andVendorUnitLike("%"+param.getVendorUnit()+"%");
            searchStr += param.getVendorUnit() + ";";
        }
        if(StringUtils.isNotEmpty(param.getCardNo())) {
            criteria.andCardNoLike("%"+param.getCardNo()+"%");
            searchStr += param.getCardNo() + ";";
        }
        if (StringUtils.isNotEmpty(param.getHairpinTime())) {
            String start = param.getHairpinTime() + " 00:00:00";
            criteria.andHairpinTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
            String end = param.getHairpinTime() + " 23:59:59";
            criteria.andHairpinTimeLessThanOrEqualTo(DateUtils.parse(end,2));
            searchStr += param.getHairpinTime() + ";";
        }
        if(StringUtils.isNotEmpty(param.getRecoveryTime())) {
            String start = param.getRecoveryTime() + " 00:00:00";
            criteria.andRecoveryTimeGreaterThanOrEqualTo(DateUtils.parse(start,2));
            String end = param.getRecoveryTime() + " 23:59:59";
            criteria.andRecoveryTimeLessThanOrEqualTo(DateUtils.parse(end,2));
            searchStr += param.getRecoveryTime() + ";";
        }
        if (Objects.nonNull(param.getFactoryId())) {
            criteria.andFactoryIdLike("%"+param.getFactoryId()+"%");
            searchStr += param.getFactoryId() + ";";
        }
        if (StringUtils.isNotEmpty(param.getStartTime())) {
            String startDate = param.getStartTime() + " 00:00:00";
            criteria.andStartTimeGreaterThanOrEqualTo(DateUtils.parse(startDate,2));
            searchStr += param.getStartTime() + ";";
        }
        if (StringUtils.isNotEmpty(param.getEndTime())) {
            String endDate = param.getEndTime() + " 23:59:59";
            criteria.andStartTimeLessThanOrEqualTo(DateUtils.parse(endDate,2));
            searchStr += param.getEndTime() + ";";
        }
        if (StringUtils.isNotEmpty(param.getHairpinNo())) {
            criteria.andHairpinNoLike("%" + param.getHairpinNo() + "%");
            searchStr += param.getHairpinNo() + ";";
        }

        if (StringUtils.isNotEmpty(param.getInFactoryProcess())) {
            criteria.andInFactoryProcessEqualTo(param.getInFactoryProcess());
        }
        if (StringUtils.isNotEmpty(param.getActInFactory()) && StringUtils.isNotEmpty(LOCATIONS_ID.get(param.getActInFactory()))) {
            criteria.andActInFactoryLike("%" + LOCATIONS_ID.get(param.getActInFactory()) + "%");
        }

        example.setOrderByClause(" create_time desc ");
        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("访客登记明细查询");
        log.setAbility("导出");
        log.setMessage(StringUtils.isNotEmpty(searchStr) ? searchStr : "所有");
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        return safetyVisitorHairpinDOMapper.selectByExample(example);
    }

    /**
     * 根据id获取值
     * @param wxPhone
     * @return
     */
    public SafetyVisitorHairpinDTO initVisitor(String wxPhone) {
        SafetyVisitorHairpinDOExample example = new SafetyVisitorHairpinDOExample();
        SafetyVisitorHairpinDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andWxPhoneEqualTo(wxPhone);
        criteria.andEndTimeGreaterThanOrEqualTo(DateUtils.parse(DateUtils.format(new Date(), 2),2));
        criteria.andStartTimeLessThanOrEqualTo(DateUtils.parse(DateUtils.format(new Date(), 2),2));
        example.setOrderByClause(" create_time desc ");
        List<SafetyVisitorHairpinDO> list = safetyVisitorHairpinDOMapper.selectByExample(example);
        SafetyVisitorHairpinDTO safetyVisitorHairpinDTO = new SafetyVisitorHairpinDTO();
        if(CollectionUtils.isNotEmpty(list)) {
            int stateNum = 0;
            boolean flag = true;
            for (SafetyVisitorHairpinDO visitorHairpinDO : list) {
                if (visitorHairpinDO.getVendorState() == 1) {
                    BeanUtils.copyProperties(visitorHairpinDO, safetyVisitorHairpinDTO);
                    safetyVisitorHairpinDTO.setShowNum(0);
                    stateNum++;
                    flag = false;
                }
            }
            if (flag) {
                safetyVisitorHairpinDTO.setShowNum(1);
            } else {
                if (stateNum > 1) {
                    safetyVisitorHairpinDTO.setShowNum(1);
                }
            }
        } else {
            safetyVisitorHairpinDTO.setShowNum(2);
        }
        return safetyVisitorHairpinDTO;
    }

    /**
     * 根据eip流程id检查状态
     * @param param
     */
    public void checkStateById(VisitorHairpinParam param) {
        //根据eip流程id获取数据
        List<SafetyVisitorHairpinDO> list = getVisitorListByEipId(param.getEipFlowId(),VisitorHairpinEnum.WFK.getCode());
        if(CollectionUtils.isEmpty(list)) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        for (SafetyVisitorHairpinDO safetyVisitorHairpinDO : list) {
            safetyVisitorHairpinDO.setVendorState(VisitorHairpinEnum.WDF.getCode());
            safetyVisitorHairpinDO.setUpdateTime(new Date());
            PreconditonsEx.checkArgument(safetyVisitorHairpinDOMapper.updateByPrimaryKey(safetyVisitorHairpinDO) == 1, ResponseEnum.FAIL);
        }

    }

    public List<SafetyHairpinDTO> getHistoryByIdCard(String phoneNum, Integer pageSize, Integer pageNum) {
        Integer beginNum = (pageNum - 1) * pageSize;
        Integer endNum = pageNum * pageSize;

        // 根据当前登录人的手机号查询申请过的流程号
        List<SafetyHairpinDTO> flowInfoList = safetyVisitorHairpinDOMapper.getFlowInfoList(phoneNum, beginNum, endNum);

        if (flowInfoList.isEmpty()) {
            return flowInfoList;
        }

        // 提取流程号
        List<String> flowNumbers = flowInfoList.stream()
                .map(SafetyHairpinDTO::getEipFlow)
                .collect(Collectors.toList());

        // 批量查询访问者信息
        List<SafetyHairpinVisitorDTO> visitorList = safetyVisitorHairpinDOMapper.getVisitorList(flowNumbers);

        // 分组访问者信息
        Map<String, List<SafetyHairpinVisitorDTO>> visitorMap = visitorList.stream()
                .collect(Collectors.groupingBy(SafetyHairpinVisitorDTO::getEipFlow));

        // 设置访问者列表
        flowInfoList.forEach(flowInfo -> {
            flowInfo.setVisitorList(visitorMap.getOrDefault(flowInfo.getEipFlow(), Collections.emptyList()));
        });

        return flowInfoList;
    }

    public void cancelPermissionByPerson(SafetyVisitorHairpinDO safetyVisitorHairpinDO,String token ) {
        LogMessageAddParam log = new LogMessageAddParam();
        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        log.setOperator(workNo);

        String authFactory = null;
        if (Objects.nonNull(safetyVisitorHairpinDO.getUserId())) {
            //获取人员
            SafetySysUserDO safetySysUserDO = safetySysUserService.safetySysUserDOById(safetyVisitorHairpinDO.getUserId());
            authFactory = safetySysUserDO.getFactoryId();
        }

        //发卡动作的时候，调用发卡接口进行数据下发动作，人脸。
        String endDate = DateUtils.format(new Date(), 2);
        if (StringUtils.isNotEmpty(safetyVisitorHairpinDO.getPhoto())) {
            safetyDataSendLogService.sendDataRecord(safetyVisitorHairpinDO, "0", endDate, "人脸", false, safetyVisitorHairpinDO.getHairpinNo(), authFactory);

            log.setOperatePage("访客发卡-注销");
            log.setAbility("人脸");
            log.setMessage(safetyVisitorHairpinDO.getVisitorName()+";"+safetyVisitorHairpinDO.getVisitorPhone()+";"+safetyVisitorHairpinDO.getVisitorIdCard()+";"+endDate);
            safetyLogMessageService.addLogMessage(log);
        }
        safetyDataSendLogService.sendDataRecord(safetyVisitorHairpinDO, "1", endDate, "人脸", false, safetyVisitorHairpinDO.getHairpinNo(), authFactory);
        safetyVisitorHairpinDOBizMapper.cancelPermissionByPerson(safetyVisitorHairpinDO);
    }

    public void updateByKafka(VisitorHirpinAppDTO en){
        List<SafetyVisitorDO> ids = safetyVisitorHairpinDOBizMapper.queryByKafka(en);
        if (CollectionUtils.isEmpty(ids)){return;}
        en.setIds(ids);

        boolean type = true;
        for (SafetyVisitorDO e : ids) {
            if (StringUtils.isEmpty(e.getActInFactory())) {
                e.setActInFactory(en.getActInFactory());
            } else if (!e.getActInFactory().contains(en.getActInFactory())) {
                e.setActInFactory(e.getActInFactory() + "," + en.getActInFactory());
            }
            if (StringUtils.isNotEmpty(e.getIdCardPhoto())) {
                type = false;
            }
        }

        safetyVisitorHairpinDOBizMapper.updateByKafka(en);
        // 未上传身份证访客首次入厂后：安保管理平台定时检测未上传身份证访客当天是否有进出记录，若有进出记录，则将访客卡A改为访客卡B，操作备注：访客当天首次身份证识别入厂
        if (en.getVendorState() == 8 && type) {
            // 判断是否c存在身份证
            Map<String, List<SafetyVisitorDO>> map = ids.stream().collect(Collectors.groupingBy(li -> li.getVisitorPhone() + "" + li.getVisitorIdCard()));
            map.forEach((key, value) -> {
                SafetyVisitorHairpinDO info = safetyVisitorHairpinDOBizMapper.selectByPrimaryKey(value.get(0).getId());
                SafetyVisitorHairpinDO mergeInfo = mergeParam(info, new HashMap<>());
                if (StringUtils.isNotEmpty(mergeInfo.getIdCardPhoto())) {
                    return;
                }
                mergeInfo.setIdCardPhoto(ID_CARD_PHOTO_DEFAULT_VALUE);
                String mes = safetyDataSendLogService.sendDataRecord(mergeInfo, "0", "", "人脸", false, mergeInfo.getHairpinNo(), mergeInfo.getIssueFactory());
                LogUtils.info("首次进厂下发结果：{}", mes);
                //日志对象
                LogMessageAddParam log = new LogMessageAddParam();
                log.setOperator("首次进厂发卡");
                log.setOperatePage("访客发卡");
                log.setAbility("人脸");
                log.setMessage("[访客当天首次身份证识别入厂]:"+mergeInfo.getVisitorName()+";"+mergeInfo.getVisitorPhone()+";"+mergeInfo.getVisitorIdCard());
                safetyLogMessageService.addLogMessage(log);
            });
        }

        //发消息
        sendYxtMes(en);
    }

    public void sendYxtMes(VisitorHirpinAppDTO en){
        //查询宇信通模板
        SafetyGeneralConfigDOExample example = new SafetyGeneralConfigDOExample();
        SafetyGeneralConfigDOExample.Criteria criteria = example.createCriteria();
        criteria.andGeneralShowEqualTo(0);

        //进厂
        List<VisitorHirpinAppDTO> list = new ArrayList<>();
        if(8==en.getVendorState()){
            criteria.andGeneralTypeEqualTo("inNote");
            list = safetyVisitorHairpinDOBizMapper.queryByKafkaInFactory(en);
        }else if(9==en.getVendorState()){//出厂
            criteria.andGeneralTypeEqualTo("outNote");
            list = safetyVisitorHairpinDOBizMapper.queryByKafkaOutFactory(en);
        }

        if(CollectionUtils.isEmpty(list)){return;}

        List<SafetyGeneralConfigDO> safetyGeneralConfigDOS = safetyGeneralConfigDOMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(safetyGeneralConfigDOS)){return;}

        String content = safetyGeneralConfigDOS.get(0).getContent();
        if(StringUtils.isEmpty(content)){return;}

        for (VisitorHirpinAppDTO e:list) {
            String msg = content.replace("{visitedName}",e.getVisitedName())
                    .replace("{vendorUnit}",e.getVendorUnit())
                    .replace("{visitorName}",e.getVisitorName())
                    .replace("{time}",DateUtils.format(en.getEndTime(),2))
                    .replace("{当前进出厂区}",en.getActInFactory());

            RestTemplate restTemplate = new RestTemplate();
            if(StringUtils.isEmpty(e.getEmpNo())){continue;}
            yxtRebotUtils.postForEntity(null,incomintRebotId,e.getEmpNo(),msg,restTemplate);
        }
    }

    public List<SafetyVisitorHairpinDO> selectList(Date startTime, Date maxStartTime) {
        SafetyVisitorHairpinDOExample example = new SafetyVisitorHairpinDOExample();
        example.createCriteria()
                .andDeleteFalgEqualTo(false)
                .andEndTimeBetween(startTime, maxStartTime);
        return safetyVisitorHairpinDOBizMapper.selectByExample(example);
    }

    public List<SafetyVisitorHairpinDO> selectListOfPhoneIdCard(Set<String> visitorInfos) {
        return safetyVisitorHairpinDOMapper.selectListOfPhoneIdCard(visitorInfos);
    }

    public Integer openStatus() {
        return safetyVisitorHairpinDOMapper.getOpenStatus(OPEN_NAME);
    }

    public int updateByPrimaryKey(SafetyVisitorHairpinDO safetyVisitorHairpinDO) {
        return safetyVisitorHairpinDOMapper.updateByPrimaryKey(safetyVisitorHairpinDO);
    }

    public List<SafetyVisitorHairpinDO> selectListByUpdateTime(Date start, Date end){
        return safetyVisitorHairpinDOBizMapper.selectListByUpdateTime(start, end);
    }

    public void syncHikMiddleTableVisitor(List<SafetyVisitorHairpinDO> hairpinList) {
        LogUtils.info("syncHikMiddleTableOutsider 开始同步----size={}", hairpinList.size());
        List<HkMiddleTableVO> tableVOList = new ArrayList<>();
        for (SafetyVisitorHairpinDO li : hairpinList) {// 必填项
            if (isBlank(li.getVisitorPhone()) || isBlank(li.getVisitorName()) || isBlank(li.getPhoto())) {
                continue;
            }

            HkMiddleTableVO info = new HkMiddleTableVO();
            info.setOrgId("FK000002");
            info.setJobNo(li.getVisitorPhone());
            info.setCertType(990);
            info.setCertificateNo(li.getVisitorPhone());
            info.setName(li.getVisitorName());
            info.setSex(0);
            info.setStatus(0);
            info.setMobile(li.getVisitorPhone());
            info.setTimeStr(li.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            info.setPositionOrgId("");
            info.setFaceData(Base64ImageDecoder.imgBase64(li.getPhoto()));
            tableVOList.add(info);
        }
        LogUtils.info("syncHikMiddleTableVisitor 同步的数据----={}", tableVOList.size());
        if (!CollectionUtils.isEmpty(tableVOList)) {
            safetyArtemisService.syncHikMiddleTable(tableVOList, HikMiddleTableSqlEnum.TB_MIDDLE_PERSON);
        }
    }
}
