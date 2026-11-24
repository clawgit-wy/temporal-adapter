package com.yutong.safety.core.handler;

import com.google.common.collect.Maps;
import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.EipStateEnum;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.constants.VisitorHairpinEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.app.*;
import com.yutong.safety.common.param.web.*;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.KafkaVO;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.entity.SafetyVisitorHairpinDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDetailDO;
import com.yutong.safety.core.db.mapper.SafetyVisitorSecretDOBizMapper;
import com.yutong.safety.core.dto.SafetyHairpinDTO;
import com.yutong.safety.core.dto.SafetyVisitorHairpinDTO;
import com.yutong.safety.core.dto.SafetyVisitorSecretDTO;
import com.yutong.safety.core.dto.VisitorHirpinAppDTO;
import com.yutong.safety.core.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
/**
 *  create gwcuiyx 2023.07.14
 *  访客发卡--handler
 */
@Component
public class SafetyVisitorHairpinHandler {
    public static final Map<String, String> LOCATIONS = new HashMap<>();
    // 定义状态映射
    private static final Map<String, Integer> STATE_MAPPING = Maps.newHashMap();

    static {
        LOCATIONS.put("十八里河厂区", "147");
        LOCATIONS.put("新能源", "148");
        LOCATIONS.put("研发中心", "149");
        LOCATIONS.put("营销中心", "150");
        LOCATIONS.put("宇通重工", "151");
        LOCATIONS.put("深澜动力", "152");
        LOCATIONS.put("智驱科技", "153");
        LOCATIONS.put("专用车工厂", "154");
        LOCATIONS.put("九龙成品库", "155");
        LOCATIONS.put("工程试验中心", "156");
        LOCATIONS.put("九龙仓库", "157");
        LOCATIONS.put("精益达西区", "158");
        LOCATIONS.put("精益达东区", "159");
        LOCATIONS.put("底盘事业部", "160");

        STATE_MAPPING.put(EipStateEnum.RELEASE.getCode(), VisitorHairpinEnum.WFK.getCode());
        STATE_MAPPING.put(EipStateEnum.TIMEOUT.getCode(), VisitorHairpinEnum.CSGB.getCode());
        STATE_MAPPING.put(EipStateEnum.DISCARD.getCode(), VisitorHairpinEnum.FQ.getCode());
        STATE_MAPPING.put(EipStateEnum.REJECT.getCode(), VisitorHairpinEnum.BH.getCode());
    }

    @Autowired
    private SafetyVisitorHairpinService safetyVisitorHairpinService;
    @Value("${eip.flowLink}")
    private String eipFlowLink;
    @Autowired
    private SafetySysUserService safetySysUserService;

    @Autowired
    private SafetyFieldManageService safetyFieldManageService;

    @Autowired
    SafetyVisitorSecretDOBizMapper safetyVisitorSecretDOBizMapper;

    @Resource
    private SafetyDataSendLogService safetyDataSendLogService;
    @Resource
    private SafetyLogMessageService safetyLogMessageService;
    @Resource
    private SafetyBaseService safetyBaseService;
    @Resource
    private SafetyBaseHandler safetyBaseHandler;
    @Resource
    private SafetyCardRefundService safetyCardRefundService;
    @Value("${applet.token.url}")
    private String appletTokenUrl;
    @Autowired
    private WebserviceUtil webserviceUtil;

    /**
     * 数据列表
     * @param param
     * @return
     */
    public Object pageList(VisitorHairpinListParam param, String tocken) {
        //解析tocken后，获取当前处理人的厂区权限，为空，就是全部权限，不为空，就是单独厂区的权限
        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        String workNo = tockenUser.get("workNo");
        //获取厂区权限
        String authFactory = "";
        if (StringUtils.isNotEmpty(workNo)) {
            SafetySysUserDO safetySysUserDO = safetySysUserService.getUserMessage(workNo);
            if (StringUtils.isNotEmpty(safetySysUserDO.getFactoryId())) {
                authFactory = " t.factory_id is null or ";
                String[] factorys = safetySysUserDO.getFactoryId().split(",");
                for (String s : factorys) {
                    authFactory += " INSTR(t.factory_id,"+s+") > 0 OR ";
                }
                authFactory = authFactory.substring(0, authFactory.length() - 3);
            }
        }
        List<VisitorHirpinAppDTO> list = safetyVisitorHairpinService.pageList(param, authFactory);
        long total = safetyVisitorHairpinService.pageListCount(param, authFactory);
        return new PageVO<VisitorHirpinAppDTO>(total, list);
    }

    /**
     * 新建访客
     * @param param
     * @return
     */
    public Object add(VisitorHairpinAddParam param, String tocken) {
        safetyVisitorHairpinService.add(param, tocken);
        return null;
    }

    /**
     * 新建访客批量新建
     * @return
     */
    public Object addList(VisitorHairpinListAddParam param) {

        if (CollectionUtils.isEmpty(param.getVisitorList())) {
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"入参="+param+",访客列表不能为空\"}");
            throw new BizException(ResponseEnum.EXCEPTION.getCode(), "访客列表不能为空");
        }
        VisitorHairpinAddParam addParam = new VisitorHairpinAddParam();
        BeanUtils.copyProperties(param, addParam);

        for (VisitorParam visitor : param.getVisitorList()) {
            if (visitor == null) {
                continue;
            }
            VisitorHairpinAddParam tempParam = new VisitorHairpinAddParam();
            BeanUtils.copyProperties(addParam, tempParam);
            tempParam.setVisitorName(visitor.getVisitorName());
            tempParam.setVisitorPhone(visitor.getVisitorPhone());
            tempParam.setVisitorIdCard(visitor.getVisitorIdCard());
            tempParam.setPhoto(visitor.getPhoto());
            tempParam.setWxPhone(param.getWxPhone());
            tempParam.setIdCardPhoto(visitor.getIdCardPhoto());
            tempParam.setIsSaveCardPhoto(visitor.getIsSaveCardPhoto());

            safetyVisitorHairpinService.add(tempParam, null);
        }

        return null;
    }

    /**
     * 根据id获取详情
     * @param id
     * @return
     */
    public Object view(Integer id) {
        return safetyVisitorHairpinService.newView(id);
    }

    /**
     * 根据身份证号获取访客详情数据
     * @param idCard
     * @return
     */
    public Object infoForIdCard(String idCard) {
        return safetyVisitorHairpinService.infoForIdCard(idCard);
    }

    /**
     * 根据访客卡id获取对应详情数据
     * @param hairpinNo
     * @return
     */
    public Object infoForHairpinNo(String hairpinNo) {
        return safetyVisitorHairpinService.infoForHairpinNo(hairpinNo);
    }

    /**
     * 数据更细操作
     * @param param
     * @return
     */
    public Object update(VisitorHairpinUpdateParam param, String token) {
        safetyVisitorHairpinService.update(param, token);
        return null;
    }

    /**
     * 数据删除功能
     * @param id
     * @return
     */
    public Object delete(Integer id) {
        safetyVisitorHairpinService.delete(id);
        return null;
    }

    /**
     * 图片上传中转
     * @param param
     * @return
     */
    public Object picUpload(PicParam param, String tocken) {
        return safetyVisitorHairpinService.picUpload(param, tocken);
    }

    /**
     * 对外接口，根据eip流程id更新对应数据
     * @param param
     * @return
     */
    public Object updateEipStatusById(VisitorHairpinParam param) {
        safetyVisitorHairpinService.updateEipStatusById(param);
//        this.updateEipStatusByIdTwo(param);

        return null;
    }

    /**
     * eip流程提交后，数据更新到访客系统接口
     * @param param
     * @return
     */
    public Object addEipFlow(VisitorHairpinAddParam param) {
        safetyVisitorHairpinService.addEipFlow(param);
        return null;
    }

    /**
     * 数据列表
     * @param param
     * @return
     */
    public Object pageDetailList(VisitorHairpinListParam param) {
        List<VisitorHirpinAppDTO> list = safetyVisitorHairpinService.pageDetailList(param);
        long total = safetyVisitorHairpinService.pageDetailListCount(param);
        return new PageVO<VisitorHirpinAppDTO>(total, list);
    }

    /**
     *  手机端查询数据列表
     * @param param
     * @return
     */
    public Object listByWxPhone(VisitorAppListParam param) {
        List<VisitorHirpinAppDTO> list = safetyVisitorHairpinService.listByWxPhone(param);
        long total = safetyVisitorHairpinService.listByWxPhoneCount(param);
        return new PageVO<VisitorHirpinAppDTO>(total, list);
    }

    /**
     * 根据id获取数据
     * @param param
     * @return
     */
    public Object detailById(AppletVisitorParam param) {
        if (StringUtils.isEmpty(param.getAppletToken())) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "登录信息不能为空");
        }
        String url = String.format(appletTokenUrl, param.getAppletToken());
        String resultStr = webserviceUtil.sendHttpForGet(url);
        if (StringUtils.isEmpty(resultStr)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "登录信息已失效或请重新登录再重试");
        }
        AppletVO appletVO = JsonUtils.parse(resultStr, AppletVO.class);
        if (!"0".equals(appletVO.getCode())) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "登录信息已失效或请重新登录再重试");
        }
        String sessionPhone = appletVO.getData().getUser().getUsername();
        LogUtils.info("解析后的数据：{}", sessionPhone);
        SafetyVisitorHairpinDTO safetyVisitorHairpinDTO = safetyVisitorHairpinService.detailById(param.getId());
        if (safetyVisitorHairpinDTO.getWxPhone().equals(sessionPhone) || safetyVisitorHairpinDTO.getVisitorPhone().equals(sessionPhone)) {
            return safetyVisitorHairpinDTO;
        }
        return null;
    }

    /**
     * 更改状态
     * @return
     */
    public Object updateStates() {
        safetyVisitorHairpinService.updateStates();
        return null;
    }

    /**
     * 数据导出操作
     * @param param
     * @return
     */
    public HSSFWorkbook export(VisitorHairpinListParam param, String token) {
        List<SafetyVisitorHairpinDO> list = safetyVisitorHairpinService.export(param, token);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"起草时间","访客姓名", "访客手机号", "访客身份证号", "访问厂区","访问区域", "是否是绝密区", "绝密区", "被访人部门","被访人姓名","被访人手机号","访问事由",
                "入厂开始时间","入厂结束时间","访客单位","车牌号","照片","访问状态","发卡时间","退卡时间","卡ID","备注","EIP链接","实际进出厂","进出厂权限状态"};
        String[][] values = new String[list.size()][];
        SafetyVisitorHairpinDO visitorHairpinDO;
        for(int i=0; i<list.size(); i++) {
            visitorHairpinDO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = Objects.nonNull(visitorHairpinDO.getEipCreateTime()) ? DateUtils.format(visitorHairpinDO.getEipCreateTime(),2) : "";
            values[i][1] = SensitiveUtils.maskName(visitorHairpinDO.getVisitorName());
            values[i][2] = SensitiveUtils.maskPhoneNumber(visitorHairpinDO.getVisitorPhone());
            values[i][3] = SensitiveUtils.maskCardId(visitorHairpinDO.getVisitorIdCard());
            values[i][4] = visitorHairpinDO.getVendorFactory();
            values[i][5] = visitorHairpinDO.getVendorRegion();
            values[i][6] = Objects.nonNull(visitorHairpinDO.getSecretState()) ? (visitorHairpinDO.getSecretState().equals(0) ? "否" : "是") : "否";
            values[i][7] = visitorHairpinDO.getSecretRegion();
            values[i][8] = visitorHairpinDO.getVisitedDept();
            values[i][9] = visitorHairpinDO.getVisitedName();
            values[i][10] = visitorHairpinDO.getVisitedPhone();
            values[i][11] = visitorHairpinDO.getVendorMatter();
            values[i][12] =  DateUtils.format(visitorHairpinDO.getStartTime(),3);
            values[i][13] = DateUtils.format(visitorHairpinDO.getEndTime(),3);
            values[i][14] = SensitiveUtils.maskName(visitorHairpinDO.getVendorUnit());
            values[i][15] = visitorHairpinDO.getCardNo();
            values[i][16] = visitorHairpinDO.getPhoto();
            values[i][17] = ObjectUtils.isEmpty(visitorHairpinDO.getVendorState())?"":VisitorHairpinEnum.values()[visitorHairpinDO.getVendorState()].getName();
            values[i][18] = Objects.nonNull(visitorHairpinDO.getHairpinTime()) ? DateUtils.format(visitorHairpinDO.getHairpinTime(),2) : "";
            values[i][19] = Objects.nonNull(visitorHairpinDO.getRecoveryTime()) ? DateUtils.format(visitorHairpinDO.getRecoveryTime(),2) : "";
            values[i][20] = visitorHairpinDO.getHairpinNo();
            values[i][21] = "";
            values[i][22] = String.format(eipFlowLink, visitorHairpinDO.getEipFlowId());
            values[i][23] = visitorHairpinDO.getActInFactory();
            values[i][24] = visitorHairpinDO.getInFactoryProcess();
        }
        String sheetName = "访客登记明细查询列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 初始化加载数据
     * @param wxPhone
     * @return
     */
    public Object initVisitor(String wxPhone) {
        return safetyVisitorHairpinService.initVisitor(wxPhone);
    }

    /**
     * 根据eip的流程id检查人员是否到访
     * @param param
     * @return
     */
    public Object checkStateByEipId(VisitorHairpinParam param) {
        safetyVisitorHairpinService.checkStateById(param);
        return null;
    }

    /**
     * 根据身份证号查询历史记录
     * @param phoneNum
     * @param pageSize
     * @param pageNum
     * @return
     */
    public List<SafetyHairpinDTO> getHistoryByIdCard(String phoneNum, Integer pageSize, Integer pageNum) {
        if(pageSize == null){
            pageSize = 15;
        }
        if(pageNum == null){
            pageNum = 1;
        }
        return safetyVisitorHairpinService.getHistoryByIdCard(phoneNum, pageSize, pageNum);
    }


    public void sendPermissionByPerson(Integer id,String token) {
        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        if(StringUtils.isEmpty(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }
        //获取员工号
        SafetySysUserDO safetySysUserDO = safetySysUserService.getUserMessage(workNo);

        //根据记录id查询数据
        SafetyVisitorHairpinDO en = safetyVisitorHairpinService.getById(id);
        if (DateUtils.sub(en.getEndTime(), new Date()) == 0) {
            LogUtils.info("{\"code\":\"8888\",\"msg\":\"方法sendPermissionByPerson，EndTime："+en.getEndTime()+",id="+en.getId()+",当前时间已超过入厂结束时间\"}");
            throw new BizException(ResponseEnum.FAIL.getCode(),"当前时间已超过入厂结束时间！");
        }
        //获取实际请求访问的厂区
        if(StringUtils.isEmpty(en.getFactoryId())){
            LogUtils.info("{\"code\":\"8888\",\"msg\":\"方法sendPermissionByPerson，入参：id="+en.getId()+",权限下发工厂为空，不下发！\"}");
            return;
        }

        //下发人实际拥有权限的厂区
        String factoryA = safetySysUserDO.getFactoryId();
        if(StringUtils.isEmpty(factoryA)){
            LogUtils.info("{\"code\":\"8888\",\"msg\":\"方法sendPermissionByPerson，入参：workNo="+workNo+"该门卫拥有的工厂为空，不下发！\"}");
            return;
        }

        safetyVisitorHairpinService.sendCardByHand(en,safetySysUserDO,workNo);

        Map<Integer, String> factoryNameList = safetyFieldManageService.getFactoryNameList();
        if(StringUtils.isNotEmpty(en.getFactoryId())) {
            //获取
            en.setVendorFactory(factoryNameList.get(en.getFactoryId()));
        }
        if (StringUtils.isNotEmpty(en.getRegionId())) {
            //获取数据
            en.setVendorRegion(safetyFieldManageService.getNamesByIds(en.getRegionId()));
        }
        en.setUpdateTime(new Date());

        ArrayList<SafetyVisitorHairpinDO> list = new ArrayList<>();
        list.add(en);
        safetyVisitorHairpinService.updateByEn(list);
    }

    public void sendPermissionByPersonList(String id,String token) {
        if(StringUtils.isEmpty(id)){return;}

        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        if(StringUtils.isEmpty(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }
        //获取员工号
        SafetySysUserDO safetySysUserDO = safetySysUserService.getUserMessage(workNo);

        //下发人实际拥有权限的厂区
        String factoryA = safetySysUserDO.getFactoryId();
        if(StringUtils.isEmpty(factoryA)){
            LogUtils.info("{\"code\":\"8888\",\"msg\":\"方法sendPermissionByPersonList，一键下发权限的时候：workNo="+workNo+"门卫拥有的工厂为空，不下发！\"}");
            return;
        }

        //查询绝密区
        List<SafetyVisitorSecretDTO> secretFactoryList = safetyVisitorSecretDOBizMapper.getSecretFactoryList();
        Map<String, SafetyVisitorSecretDTO> secretMap = null;
        if(CollectionUtils.isNotEmpty(secretFactoryList)){
            secretMap = secretFactoryList.stream().collect(Collectors.toMap(SafetyVisitorSecretDTO::getSecretPosition,
                    dto -> dto,
                    (oldV, newV) -> newV));
        }

        String[] idArray = id.split(",");

        // 将字符串数组转换为整数列表
        List<Integer> ids = new ArrayList<>();
        for (String s : idArray) {
            ids.add(Integer.parseInt(s.trim())); // 去掉可能的空格并转换为整数
        }
        //查询工厂
        Map<Integer, String> factoryNameList = safetyFieldManageService.getFactoryNameList();

        //根据记录id批量查询数据
        List<SafetyVisitorHairpinDO> enList = safetyVisitorHairpinService.getByIdList(ids);
        if(CollectionUtils.isEmpty(enList)){return;}
        for (SafetyVisitorHairpinDO en:enList) {
            if (DateUtils.sub(en.getEndTime(), new Date()) == 0) {
                LogUtils.info("{\"code\":\"8888\",\"msg\":\"方法sendPermissionByPersonList，EndTime："+en.getEndTime()+",id="+en.getId()+",当前时间已超过入厂结束时间\"}");
                continue;
            }

            if(StringUtils.isEmpty(en.getFactoryId())){
                LogUtils.info("{\"code\":\"8888\",\"msg\":\"方法sendPermissionByPersonList，入参："+en.getId()+",实际请求下发工厂为空，不下发！\"}");
                continue;
            }

            safetyVisitorHairpinService.sendCardByHandList(en,safetySysUserDO,workNo,secretMap);

            if(!"正常".equals(en.getInFactoryProcess()) && !"异常".equals(en.getInFactoryProcess())){
                continue;
            }

            if(StringUtils.isNotEmpty(en.getFactoryId())) {
                //获取
                en.setVendorFactory(factoryNameList.get(en.getFactoryId()));
            }
            if (StringUtils.isNotEmpty(en.getRegionId())) {
                //获取数据
                en.setVendorRegion(safetyFieldManageService.getNamesByIds(en.getRegionId()));
            }
            en.setUpdateTime(new Date());
        };
        safetyVisitorHairpinService.updateByEn(enList);
    }

    public void cancelPermissionByPerson(Integer id,String token) {
        //根据记录id查询数据
        SafetyVisitorHairpinDO en = safetyVisitorHairpinService.getById(id);

        if(1==en.getVendorState() || 8==en.getVendorState() || 9==en.getVendorState() || 5==en.getVendorState() || 11 == en.getVendorState()){
            en.setInFactoryProcess("注销");
            en.setVendorState(2);
            en.setRecoveryTime(new Date());

            safetyVisitorHairpinService.cancelPermissionByPerson(en,token);
        }
    }

    public void updateStatuWithKafka(String employNo,String factoryName,String comeGo,Date opendate){
        if(StringUtils.isEmpty(employNo) || StringUtils.isEmpty(factoryName)){
            return;
        }
        // 手机号处理，因为下发权限时手机号增加了前缀，所以在此处对手机号进行处理
        if (employNo.length() != 11 && employNo.length() != 12) {
            return;
        }
        if (employNo.length() == 11 && (employNo.startsWith("W") || employNo.startsWith("F") || employNo.startsWith("B"))) {
            employNo = "1" + employNo.substring(1);
        }
        if (employNo.length() == 12 && (employNo.startsWith("W") || employNo.startsWith("F") || employNo.startsWith("B"))) {
            employNo = employNo.substring(1);
        }
        if (!employNo.startsWith("1")) {
            return;
        }

        //根据employNo更新对应的访客信息
        VisitorHirpinAppDTO en = new VisitorHirpinAppDTO();
        en.setVisitorPhone(employNo);
        Integer vState = null;
        if(KafkaVO._P10.equals(comeGo)){
            vState = KafkaVO.YJC;
        }else if(KafkaVO._P20.equals(comeGo)){
            vState = KafkaVO.YCC;
        }

        if(vState == null){return;}

        en.setVendorState(vState);
        en.setActInFactory(factoryName);

        if(StringUtils.isEmpty(LOCATIONS.get(factoryName))){
            LogUtils.info("{\"code\":\"8888\",\"msg\":\"{}进厂的厂区编号为空{}\"}",employNo,factoryName);
        }

        en.setFactoryId(LOCATIONS.get(factoryName));
        //有可能提前40分钟入厂，开始时间加40分钟再和开始时间比较
        en.setStartTime(DateUtils.addMinutes(opendate,40));
        en.setEndTime(opendate);
        safetyVisitorHairpinService.updateByKafka(en);
    }

    public void permissionDelivery(){
        //更新到访问结束时间还未出厂的数据为滞留状态
        safetyVisitorHairpinService.updateZLStates();

        //查询绝密区
        List<SafetyVisitorSecretDTO> secretFactoryList = safetyVisitorSecretDOBizMapper.getSecretFactoryList();
        Map<String, SafetyVisitorSecretDTO> secretMap = null;
        if(CollectionUtils.isNotEmpty(secretFactoryList)){
            secretMap = secretFactoryList.stream().collect(Collectors.toMap(SafetyVisitorSecretDTO::getSecretPosition,
                    dto -> dto,
                    (oldV, newV) -> newV));
        }

        //查询入厂时间<当前时间+40分钟，并且未发卡的数据
        List<SafetyVisitorHairpinDO> list = safetyVisitorHairpinService.getDsDate();
        if(CollectionUtils.isEmpty(list)){return;}
        //访客-流程中的访客没有上传人脸照片的不自动下发权限
        list = list.stream().filter(li -> StringUtils.isNotEmpty(li.getPhoto())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(list)){return;}

        Map<Integer, String> factoryNameList = safetyFieldManageService.getFactoryNameList();

        //获取访客的黑名单人员及黑名单申请入厂的权限
        List<String> idCardList = list.stream().map(SafetyVisitorHairpinDO::getVisitorIdCard).distinct().collect(Collectors.toList());
        Map<String,List<SafetyEntryBlankProcessDetailDO>> blackApplyMap = safetyVisitorHairpinService.filterBlankProcess(idCardList);
        Set<String> blackSet = blackApplyMap.keySet();

        //权限的下发
        for (SafetyVisitorHairpinDO en:list){
            if (DateUtils.sub(en.getEndTime(), new Date()) == 0) {
                LogUtils.info("{\"code\":\"8888\",\"msg\":\"方法permissionDelivery，EndTime："+en.getEndTime()+",当前时间已超过入厂结束时间,入厂权限不下发\"}");
                continue;
            }
            String curIdCard = en.getVisitorIdCard();
            List<SafetyEntryBlankProcessDetailDO> curBlackApplyList = null;
            if(blackSet.contains(curIdCard)){
                curBlackApplyList = blackApplyMap.get(curIdCard);
                if(curBlackApplyList == null){
                    curBlackApplyList = new ArrayList<>();
                }
            }
            safetyVisitorHairpinService.sendCard(en,secretMap,null);

            if(StringUtils.isNotEmpty(en.getFactoryId())) {
                //获取
                en.setVendorFactory(factoryNameList.get(en.getFactoryId()));
            }
            if (StringUtils.isNotEmpty(en.getRegionId())) {
                //获取数据
                en.setVendorRegion(safetyFieldManageService.getNamesByIds(en.getRegionId()));
            }
            en.setUpdateTime(new Date());
        };

        //更新卡的下发状态
        safetyVisitorHairpinService.updateByEn(list);
    }

    /**
     * 定时关闭访客权限
     * 1.获取当前时间前30分钟的数据，简称过期的访客数据
     * 2.根据过期的访客数据，获取其对应没过期的信息，
     * 3.关闭所有权限，只有一条访客记录的过滤掉，表示其权限在下发的时候不牵涉合并下发逻辑
     * 4.开通未过期的访客人员的数据权限
     * @return
     */
    public VO closeVisitorPower() {

        // 获取符合条件的数据，时间区间为：当前时间-33分钟 < time < 当前时间， 未删除的数据
        Date startTime = DateUtils.parse(DateUtils.reduceMinute(new Date(), -33, 2), 2);
        Date endTime = new Date();
        List<SafetyVisitorHairpinDO> hairpinDOList = safetyVisitorHairpinService.selectList(startTime, endTime);
        if (CollectionUtils.isEmpty(hairpinDOList)) {
            return VoUtil.success();
        }

        // 汇总需要合并下发的人员
        Set<String> visitorInfos = hairpinDOList.stream().map(ln -> ln.getVisitorPhone() + "" + ln.getVisitorIdCard()).collect(Collectors.toSet());

        // 查询需要合并下发的人员
        List<SafetyVisitorHairpinDO> safetyVisitorHairpinDOS = safetyVisitorHairpinService.selectListOfPhoneIdCard(visitorInfos);

        // 关闭权限
        hairpinDOList.addAll(safetyVisitorHairpinDOS);
        isSureCrk(hairpinDOList,new HashMap<>(), false);

        // 开通权限
        if (CollectionUtils.isNotEmpty(safetyVisitorHairpinDOS)) {
            //查询绝密区
            List<SafetyVisitorSecretDTO> secretFactoryList = safetyVisitorSecretDOBizMapper.getSecretFactoryList();
            Map<String, SafetyVisitorSecretDTO> secretMap = null;
            if(CollectionUtils.isNotEmpty(secretFactoryList)){
                secretMap = secretFactoryList.stream().collect(Collectors.toMap(SafetyVisitorSecretDTO::getSecretPosition,
                        dto -> dto,
                        (oldV, newV) -> newV));
            }
            isSureCrk(safetyVisitorHairpinDOS, secretMap, true);
        }
        return VoUtil.success();
    }

    /**
     * 权限下发
     * @param hairpinDOList 需要下发的数据
     * @param bool true下发  false 关闭
     */
    private void isSureCrk(List<SafetyVisitorHairpinDO> hairpinDOList, Map<String, SafetyVisitorSecretDTO> secretMap, boolean bool) {
        hairpinDOList = hairpinDOList.stream().filter(li -> StringUtils.isNotEmpty(li.getVisitorPhone()) && StringUtils.isNotEmpty(li.getVisitorIdCard())).collect(Collectors.toList());
        Map<String, List<SafetyVisitorHairpinDO>> collect = hairpinDOList.stream().collect(Collectors.groupingBy(ln -> ln.getVisitorPhone() + "" + ln.getVisitorIdCard()));

        collect.forEach((key, value) -> {
            if (!bool && value.size() == 1) {
                return;
            }
            SafetyVisitorHairpinDO safetyVisitorHairpinDO = value.get(0);

            String factoryIds = value.stream().map(SafetyVisitorHairpinDO::getFactoryId).filter(StringUtils::isNotEmpty).flatMap(li -> Arrays.stream(li.split(";"))).filter(StringUtils::isNotEmpty).distinct().collect(Collectors.joining(";"));
            String isSureFactory = value.stream().map(SafetyVisitorHairpinDO::getIssueFactory).filter(StringUtils::isNotEmpty).flatMap(li -> Arrays.stream(li.split(";"))).filter(StringUtils::isNotEmpty).distinct().collect(Collectors.joining(";"));
            String unIsSureFactory = value.stream().map(SafetyVisitorHairpinDO::getUnIssueFactory).filter(StringUtils::isNotEmpty).flatMap(li -> Arrays.stream(li.split(";"))).filter(StringUtils::isNotEmpty).distinct().collect(Collectors.joining(";"));
            String secretRegions = value.stream().map(SafetyVisitorHairpinDO::getSecretRegion).filter(StringUtils::isNotEmpty).flatMap(li -> Arrays.stream(li.split(";"))).filter(StringUtils::isNotEmpty).distinct().collect(Collectors.joining(";"));
            boolean b = value.stream().map(SafetyVisitorHairpinDO::getSecretState).anyMatch(li -> li == 1);
            List<String> idCardPhotos = value.stream().map(SafetyVisitorHairpinDO::getIdCardPhoto).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
            Date startTime = DateUtils.parse(DateUtils.reduceMinute(new Date(), -33, 2), 2);
            if (bool) {
                startTime = value.stream().map(SafetyVisitorHairpinDO::getEndTime).filter(Objects::nonNull).max(Date::compareTo).orElse(safetyVisitorHairpinDO.getEndTime());
            }
            safetyVisitorHairpinDO.setEndTime(startTime);
            safetyVisitorHairpinDO.setFactoryId(factoryIds);
            safetyVisitorHairpinDO.setIssueFactory(isSureFactory);
            safetyVisitorHairpinDO.setSecretState(b ? 1 : 0);
            safetyVisitorHairpinDO.setSecretRegion(secretRegions);
            String msg = "[访客流程通过]:";
            if (CollectionUtils.isNotEmpty(idCardPhotos)) {
                // 无实际下发作用，只用于判断下发类型
                safetyVisitorHairpinDO.setIdCardPhoto(idCardPhotos.get(0));
                msg = "[访客上传身份证(" + idCardPhotos.get(0) + ")]:";
            }

            //是否涉及绝密区，是否去除绝密区
            safetyVisitorHairpinService.dealSecret(safetyVisitorHairpinDO, secretMap);

            if(StringUtils.isEmpty(factoryIds)){
                LogUtils.info("{\"code\":\"8888\",\"msg\":\"方法sendCard，参数：ID="+safetyVisitorHairpinDO.getId()+"," +
                        "secret_state="+safetyVisitorHairpinDO.getSecretState()+
                        ",secret_region="+safetyVisitorHairpinDO.getSecretRegion()+
                        ",factory_id="+safetyVisitorHairpinDO.getFactoryId()+"权限下发时工厂为空，不下发！\"}");
                return;
            }

            safetyDataSendLogService.sendDataRecord(safetyVisitorHairpinDO, "0", "", "人脸", false, safetyVisitorHairpinDO.getHairpinNo(), factoryIds);
            //日志对象
            LogMessageAddParam log = new LogMessageAddParam();
            log.setOperator("定时任务");
            log.setOperatePage("访客发卡");
            log.setAbility("人脸");
            log.setMessage(bool ? msg : "" + safetyVisitorHairpinDO.getVisitorName()+";"+safetyVisitorHairpinDO.getVisitorPhone()+";"+safetyVisitorHairpinDO.getVisitorIdCard());
            safetyLogMessageService.addLogMessage(log);
        });
    }


    public void updateEipStatusByIdTwo(VisitorHairpinParam param) {
        // 1.根据EIP流程ID获取数据
        List<SafetyVisitorHairpinDO> list = safetyVisitorHairpinService.getVisitorListByEipId(param.getEipFlowId(),null);
        if(CollectionUtils.isEmpty(list)) {
            LogUtils.info("{\"code\":\"9999\",\"msg\":\"方法updateEipStatusById，入参："+param+",该记录不存在\"}");
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        // 2.获取绝密区的厂区信息
        Map<String, SafetyVisitorSecretDTO> secretMap = safetyBaseService.getSecretFactoryMap();

        // 3.获取访客的黑名单人员及黑名单申请入厂的权限
        List<String> idCardList = list.stream().map(SafetyVisitorHairpinDO::getVisitorIdCard).distinct().collect(Collectors.toList());
        Map<String,List<SafetyEntryBlankProcessDetailDO>> blackApplyMap = safetyVisitorHairpinService.filterBlankProcess(idCardList);
        Set<String> blackSet = blackApplyMap.keySet();

        list.forEach(hairpinDO -> {
            // 数据整理
            setVendorInfo(hairpinDO, param);

            // 涉密数据处理
            safetyVisitorHairpinService.dealSecret(hairpinDO, secretMap);

            // 黑名单处理
            String curIdCard = hairpinDO.getVisitorIdCard();
            List<SafetyEntryBlankProcessDetailDO> curBlackApplyList = blackSet.contains(curIdCard) ? blackApplyMap.getOrDefault(curIdCard, new ArrayList<>()) : new ArrayList<>();
            if (CollectionUtils.isNotEmpty(curBlackApplyList)) {
                safetyVisitorHairpinService.dealBlankApply(hairpinDO, curBlackApplyList);
            }

            // 员工号获取
            String userName = "";
            Boolean bool = getUserId(hairpinDO, userName);
            if (Boolean.FALSE.equals(bool)) {
                return;
            }

            if (StringUtils.isEmpty(hairpinDO.getIssueFactory())) {
                LogUtils.info("{\"code\":\"8888\",\"msg\":\"方法sendCard，参数：ID="+hairpinDO.getId()+"," + "secret_state="+hairpinDO.getSecretState()+ ",secret_region="+hairpinDO.getSecretRegion()+ ",is_sure_factory="+hairpinDO.getIssueFactory()+"权限下发时工厂为空，不下发！\"}");
                return;
            }

            if (EipStateEnum.RELEASE.getCode().equals(param.getState()) && StringUtils.isNotEmpty(hairpinDO.getPhoto())) {
                Integer openStatus = safetyVisitorHairpinService.openStatus();
                if (0 == openStatus  && hairpinDO.getStartTime() != null) {
                    Date startTime = hairpinDO.getStartTime();
                    Date date = DateUtils.addMinutes(new Date(), 40);
                    long abs = date.getTime() - startTime.getTime();
                    if (abs >= 0) {
                        sendCardOfMerge(hairpinDO, secretMap, userName);
                    }
                }
            }
        });

        list.forEach(li -> {
            PreconditonsEx.checkArgument(safetyVisitorHairpinService.updateByPrimaryKey(li) == 1, ResponseEnum.FAIL);
            //检查是否已退卡，未退卡，直接将数据写入到未退卡表中

            if (StringUtils.isEmpty(li.getRecoveryNo())) {
                safetyCardRefundService.add(li.getId());
            }
        });
    }

    private void setVendorInfo(SafetyVisitorHairpinDO hairpinDO, VisitorHairpinParam param) {
        // 更新状态
        if (STATE_MAPPING.containsKey(param.getState())) {
            hairpinDO.setVendorState(STATE_MAPPING.get(param.getState()));
        }
        if (StringUtils.isNotEmpty(param.getFactoryId())) {
            hairpinDO.setVendorFactory(safetyFieldManageService.getNamesByIds(param.getFactoryId()));
            hairpinDO.setFactoryId(param.getFactoryId());
        }
        if (StringUtils.isNotEmpty(param.getRegionId())) {
            hairpinDO.setVendorRegion(safetyFieldManageService.getNamesByIds(param.getRegionId()));
            hairpinDO.setRegionId(param.getRegionId());
        }
        if (StringUtils.isNotEmpty(param.getVendorFactory())) {
            hairpinDO.setVendorFactory(param.getVendorFactory());
        }
        if (StringUtils.isNotEmpty(param.getVendorRegion())) {
            hairpinDO.setVendorRegion(param.getVendorRegion());
        }

        if (StringUtils.isNotEmpty(param.getSecretState())) {
            hairpinDO.setSecretState(Integer.valueOf(param.getSecretState()));
        }
        if (StringUtils.isNotEmpty(param.getSecretRegion())) {
            hairpinDO.setSecretRegion(param.getSecretRegion());
        }
        hairpinDO.setUpdateTime(new Date());
    }



    private Boolean getUserId(SafetyVisitorHairpinDO hairpinDO, String name) {
        //获取员工号
        String staffNo = safetyVisitorHairpinService.getEipNoLog(hairpinDO.getVisitedName(), hairpinDO.getVisitedPhone());
        if (StringUtils.isNotEmpty(staffNo)) {
            name =  staffNo;
            SafetySysUserDO safetySysUserDO = safetySysUserService.getUserMessage(staffNo);
            if(!ObjectUtils.isEmpty(safetySysUserDO)){
                hairpinDO.setUserId(safetySysUserDO.getId());
            }
            return true;
        }else{
            LogUtils.info("从ladp取被访人时为空，被访人：{}，手机号：{}",hairpinDO.getVisitedName(),hairpinDO.getVisitedPhone());
            hairpinDO.setInFactoryProcess("异常");
            return false;
        }
    }


    private void sendCardOfMerge(SafetyVisitorHairpinDO hairpinDO, Map<String, SafetyVisitorSecretDTO> secretMap, String userName) {
        boolean mark = false;

        //发卡的时候，将已发卡，但是没退卡的数据，全部置为已退卡
        safetyVisitorHairpinService.hairpinNoBackNoReview(hairpinDO.getHairpinNo());
        //发卡动作的时候，调用发卡接口进行数据下发动作，人脸。

        SafetyVisitorHairpinDO info = safetyVisitorHairpinService.mergeParam(hairpinDO, secretMap);
        String mes = safetyDataSendLogService.sendDataRecord(info, "0", "", "人脸", false, info.getHairpinNo(), info.getIssueFactory());
        safetyBaseHandler.addSendLog(hairpinDO, userName, "人脸");

        if ("true".equals(mes)) {
            mark = true;
        }

        if (mark) {
            hairpinDO.setInFactoryProcess("正常");
            hairpinDO.setVendorState(1);
            hairpinDO.setHairpinTime(new Date());
        }else{
            hairpinDO.setInFactoryProcess("异常");
            hairpinDO.setUserId(null);
            hairpinDO.setIssueFactory("");
            hairpinDO.setUnIssueFactory("");
        }
    }
}
