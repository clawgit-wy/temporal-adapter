package com.yutong.safety.core.service.enter;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.SafetyEmpowerDeviceDO;
import com.yutong.safety.core.db.entity.enter.EnterApproveCrk;
import com.yutong.safety.core.db.entity.enter.SafetyEnterDockCrk;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingDock;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterApproveRt;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterAuthRt;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterProtectRt;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterRegisterRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterAuthSh;
import com.yutong.safety.core.db.entity.result.DictItemRt;
import com.yutong.safety.core.db.mapper.enter.*;
import com.yutong.safety.core.handler.enter.SafetyEnterCheckHandler;
import com.yutong.safety.core.service.SafetyDictItemService;
import com.yutong.safety.core.service.SafetyFieldManageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SafetyEnterCheckService implements SafetyEnterCheckHandler {
    @Value("${logistics.card.tcmid}")
    private String logisticsCardTcmid;
    @Autowired
    private SafetyEnterCheckMapper enterCheckMapper;
    @Autowired
    private SafetyEnterRegisterMapper enterRegisterMapper;
    @Autowired
    private SafetyEnterRegisterService enterRegisterService;
    @Autowired
    private SafetyEnterApproveMapper enterApproveMapper;
    @Autowired
    private SafetyEnterProtectMapper enterProtectMapper;
    @Autowired
    private SafetyEnterAuthService safetyEnterAuthService;
    @Autowired
    private SafetyEnterAuthMapper enterAuthMapper;
    @Autowired
    private SafetyEnterFilingMapper enterFilingMapper;
    @Autowired
    private IncomingMapper incomingMapper;
    @Autowired
    private SafetyDictItemService safetyDictItemService;

    private static final String FILLING_USER_TYPE_CODE = "enter_per_type";
    @Autowired
    private SafetyFieldManageService safetyFieldManageService;

    @Override
    public VO checkUserInBlack(String dockCode, String phoneNo) {
        if (StringUtils.isBlank(phoneNo)) {
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        VO vo;

        SafetyEnterRegisterRt registerRt = enterRegisterMapper.selectEnterRegisterByPhone(phoneNo);

        Map map = new HashMap();
        map.put("phoneNo", phoneNo);
        Integer has = enterCheckMapper.checkUserInBlack(map);
        if (has == null || has == 0) {
            // 不在黑名单
            vo = VoUtil.successData("验证通过", registerRt);
        } else {
            // 在黑名单
            if (StringUtils.isNotBlank(dockCode)) {
                String[] arr = dockCode.split(";");
                Set<String> dmNew = new HashSet<>(Arrays.asList(arr));
                if (arr.length != dmNew.size()) {
                    vo = VoUtil.successMsg("验证未通过，多个接口人不能重复", "1");
                } else {
                    vo = VoUtil.successMsg("验证通过", registerRt);
                    List<SafetyEnterAuthRt> dah = enterCheckMapper.checkDockAuthBlack(arr);
                    if (CollectionUtils.isNotEmpty(dah) && dah.size() == arr.length) {
                        for (SafetyEnterAuthRt safetyEnterAuthRt : dah) {
                            if ("是".equals(safetyEnterAuthRt.getFdBlack())) {
                                vo = VoUtil.successMsg("验证未通过，接口人无黑名单权限", "1");
                                break;
                            }
                        }
                    } else {
                        vo = VoUtil.successMsg("验证未通过，请检查接口人权限配置", "1");
                    }
                }
            } else {
                vo = VoUtil.successMsg("验证未通过", "1");
            }
        }
        return vo;
    }

    @Override
    public void checkDockInfo(List<SafetyEnterFilingDock> dockList) {
        if (CollectionUtils.isEmpty(dockList)) {
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        for (SafetyEnterFilingDock dock : dockList) {
            String empName = dock.getFdDockName();
            String empPhone = dock.getFdDockPhone();
            SafetyEnterAuthSh authSh = new SafetyEnterAuthSh();
            authSh.setFdEmpName(empName);
            authSh.setFdPhone(empPhone);
            // 先判断权限表中，是否有对接人
            List<SafetyEnterAuthRt> dockRt = safetyEnterAuthService.selectList(authSh);
            SafetyEnterAuthRt rt = null;
            if (CollectionUtils.isEmpty(dockRt)) {
                // 再判断员工信息表，是否存在
                rt = enterAuthMapper.selectEmpInfoByEmpNo(authSh);
                if (rt == null) {
                    throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), empName + "信息错误");
                }
                if (!rt.getFdEmpNo().startsWith("9")) {
                    throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), empName + "无权限");
                }
                List<SafetyEnterAuthRt> newDockRt = null;
                //根据组织结构获取权限
                if (StringUtils.isNotBlank(rt.getFdOrgCode())) {
                    newDockRt = enterAuthMapper.selectEnterAuthByOrgCode(rt.getFdOrgCode());
                }

                List factoryList = new ArrayList();

                if (CollectionUtils.isEmpty(newDockRt)) {
                    throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), empName + "对接人无权限");
                } else {
                    SafetyEnterAuthRt authRt = newDockRt.get(0);
                    String factoryCode = authRt.getFdFactoryCode();
                    String factory = authRt.getFdFactory();

                    if (StringUtils.isNotBlank(factoryCode)) {
                        String[] fcs = factoryCode.split(",");
                        String[] fs = factory.split(",");
                        for (int i = 0; i < fcs.length; i++) {
                            Map map = new HashMap();
                            map.put("factoryCode", fcs[i]);
                            map.put("factory", fs[i]);
                            factoryList.add(map);
                        }
                    }
                    dock.setDayNum(authRt.getFdLimitDay());
                }
                dock.setFdDockNo(rt.getFdEmpNo());
                dock.setFdDockDepartment(rt.getFdEmpDepartment());
                dock.setFactoryList(factoryList);
            } else {
                SafetyEnterAuthRt authRt = dockRt.get(0);
                String factoryCode = authRt.getFdFactoryCode();
                String factory = authRt.getFdFactory();
                List factoryList = new ArrayList();
                if (StringUtils.isNotBlank(factoryCode)) {
                    String[] fcs = factoryCode.split(",");
                    String[] fs = factory.split(",");
                    for (int i = 0; i < fcs.length; i++) {
                        Map map = new HashMap();
                        map.put("factoryCode", fcs[i]);
                        map.put("factory", fs[i]);
                        factoryList.add(map);
                    }
                }
                dock.setFdDockNo(authRt.getFdEmpNo());
                dock.setFdDockDepartment(authRt.getFdEmpDepartment());
                dock.setDayNum(authRt.getFdLimitDay());
                dock.setFactoryList(factoryList);
            }
        }
    }

    @Override
    public int checkCarNo(String carNo) {
        SafetyEnterProtectRt rt = enterProtectMapper.selectEnterProtectByCarNo(carNo);
        if (rt != null) {
            return 1;
        }
        return 0;
    }

    @Override
    public VO checkRecord(String phone) {
        // 用户未登记
        SafetyEnterRegisterRt registerRt = enterRegisterMapper.selectEnterRegisterByPhone(phone);
        if (registerRt == null) {
            return new VO(ResponseEnum.SUCCESS.getCode(), "用户未登记", "0");
        }
        //251017新需求，无照片时需跳转到登记界面完善照片
        if (StringUtils.isBlank(registerRt.getFdImg())) {
            return new VO(ResponseEnum.SUCCESS.getCode(), "用户无照片", "3");
        }
        // 用户未备案
        List<SafetyEnterApproveRt> rtList = enterApproveMapper.selectApproveByPhone(phone);
        if (CollectionUtils.isEmpty(rtList)) {
            return new VO(ResponseEnum.SUCCESS.getCode(), "用户未备案", "1");
        }
        Map<Integer, List<SafetyEnterApproveRt>> rtTypeMap = rtList.stream().collect(Collectors.groupingBy(SafetyEnterApproveRt::getPowerType));
        List<SafetyEnterApproveRt> curPowerList = rtTypeMap.get(1);
        if (CollectionUtils.isEmpty(curPowerList)) {
            SafetyEnterApproveRt afterPower = rtTypeMap.get(2).get(0);
            String enterBegin = afterPower.getEnterStartTime().substring(0, 10);
            String enterEnd = afterPower.getEnterEndTime().substring(0, 10);
            String retMsg = "您的入厂备案日期为" + enterBegin.substring(5, 7) + "月" + enterBegin.substring(8, 10) + "日-"
                    + enterEnd.substring(5, 7) + "月" + enterEnd.substring(8, 10) + "日,请在备案有效期内查看或重新提交备案流程";
            return new VO(ResponseEnum.SUCCESS.getCode(), retMsg, "2");
        }
        List<Map> maps = new ArrayList<>();
        Set<String> fnameSet = new HashSet<>();
        Set<String> fcodeSet = new HashSet<>();
        SafetyEnterApproveRt maxRecord = curPowerList.stream()
                .max(Comparator.comparing(SafetyEnterApproveRt::getUpdateTime)).orElse(null);
        SafetyEnterDockCrk crk = incomingMapper.selectSafetyEnterDockCrkNew(maxRecord.getFilingId());

        if (ObjectUtils.isEmpty(crk) || CollectionUtils.isEmpty(crk.getApproveList())) {
            return new VO(ResponseEnum.SUCCESS.getCode(), "用户未备案", "1");
        }
        for (EnterApproveCrk approveRt : crk.getApproveList()) {
            String curFactoryCode = approveRt.getFdFilingFactoryCode();
            if (StringUtils.isBlank(curFactoryCode)) {
                continue;
            }
            String[] fcodeArr = curFactoryCode.split(",");
            Collections.addAll(fcodeSet, fcodeArr);
            String[] fnameArr = approveRt.getFdFilingFactory().split(",");
            fnameSet.addAll(Arrays.asList(fnameArr));
        }
        Map<String, String> map = new HashMap<>();
        map.put("enterFactory", fnameSet.stream().collect(Collectors.joining(",")));
        map.put("enterFactoryCode", fcodeSet.stream().collect(Collectors.joining(",")));
        map.put("enterStartTime", curPowerList.get(0).getEnterStartTime());
        map.put("enterEndTime", curPowerList.get(0).getEnterEndTime());
        maps.add(map);

        String[] fs = fcodeSet.toArray(new String[fcodeSet.size()]);
        Map p = new HashMap();
        p.put("factoryCode", fs);
        p.put("filingType", "物流司机");
        List<SafetyEmpowerDeviceDO> list = enterApproveMapper.checkEmpowerDevice(p);
        if (CollectionUtils.isEmpty(list)) {
            return new VO(ResponseEnum.SUCCESS.getCode(), "未找到通道号", "2");
        }
        maxRecord.setFactorys(maps);

        List<Integer> nos = new ArrayList<>();
        for (SafetyEmpowerDeviceDO channel : list) {
            if (StringUtils.isBlank(channel.getEmpowerNo())) {
                LogUtils.info(channel.getFactoryName() + "通道号为空");
                continue;
            }
            String channelNo = channel.getEmpowerNo();
            String[] channelNos = channelNo.split(",");
            for (String no : channelNos) {
                nos.add(Integer.parseInt(no));
            }
        }

        int[] nums = new int[nos.size()];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = nos.get(i);
        }
        LogUtils.info("当前用户所有通道号：" + nos);
        String staffName = maxRecord.getName();
        String idNo = maxRecord.getCardId();
        String phoneNo = maxRecord.getPhone();
        String crossType = "1";
        String crossData = "";
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);

        rightNow.add(Calendar.MINUTE, +1);
        Date dt1 = rightNow.getTime();
        String oneHoursAgoTime = sdf.format(dt1);
        LogUtils.info("当前时间往后推1分钟：" + oneHoursAgoTime);

        // 根据手机号 当前系统时间往后推迟30S 生成二维码字符串
        String newPhone = "W" + phoneNo.substring(1);
        String erCode = enterRegisterService.doorQrCode(newPhone, idNo, staffName, nums, crossType, oneHoursAgoTime, crossData, logisticsCardTcmid);
        LogUtils.info("生成二维码：{}", StringUtils.isEmpty(erCode));
        maxRecord.setErwinCode(erCode);
        return new VO(ResponseEnum.SUCCESS.getCode(), "校验成功", maxRecord);
    }

    /**
     * 根据手机号展示备案详情
     *
     * @param phone
     * @return
     */
    public VO showRegisterInfo(String phone) {
        // 用户未登记
        SafetyEnterRegisterRt registerRt = enterRegisterMapper.selectEnterRegisterByPhone(phone);
        if (registerRt == null) {
            return new VO(ResponseEnum.SUCCESS.getCode(), "用户未登记", "0");
        }
        return new VO(ResponseEnum.SUCCESS.getCode(), "校验成功", registerRt);
    }


    /**
     * @param dockList
     * @param enterFlag 是否外来人员入厂备案
     */
    @Override
    public void checkDockInfoForNew(List<SafetyEnterFilingDock> dockList, Boolean enterFlag) {
        if (CollectionUtils.isEmpty(dockList)) {
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        //DICIT_ITEM 外来人员类型 super_code='enter_per_type' and show_flag='1'
        List<String> userTypeList = new ArrayList<>();
        List<DictItemRt> dictItemDOS = safetyDictItemService.selectDictItemListBySupperCode(FILLING_USER_TYPE_CODE);
        Map<String, DictItemRt> dictItemMap = dictItemDOS.stream().collect(Collectors.toMap(DictItemRt::getItemCode, e -> e));
        if (enterFlag) {
            userTypeList = dictItemDOS.stream().filter(e -> "1".equals(e.getShowFlag())).map(DictItemRt::getItemCode).collect(Collectors.toList());
        }

        //factoryDict
        Map<Integer, String> factoryMap = safetyFieldManageService.getFactoryNameList();

        for (SafetyEnterFilingDock dock : dockList) {
            String empName = dock.getFdDockName();
            String empPhone = dock.getFdDockPhone();
            SafetyEnterAuthSh authSh = new SafetyEnterAuthSh();
            authSh.setFdEmpName(empName);
            authSh.setFdPhone(empPhone);
            if (enterFlag) {
                if (CollectionUtils.isEmpty(userTypeList)) {
                    throw new BizException(ResponseEnum.FAIL.getCode(), "联系管理员维护外来人员备案类型");
                }
                authSh.setFilingUserTypeList(userTypeList);
            }
            // 先判断权限表中，是否有对接人
            List<SafetyEnterAuthRt> dockRt = safetyEnterAuthService.selectList(authSh);
            SafetyEnterAuthRt rt;
            if (CollectionUtils.isEmpty(dockRt)) {
                if (enterFlag) {
                    throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "对接人信息错误");
                } else {
                    // 再判断员工信息表，是否存在
                    rt = enterAuthMapper.selectEmpInfoByEmpNo(authSh);
                    if (rt == null) {
                        throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), empName + "信息错误");
                    }
                    if (!rt.getFdEmpNo().startsWith("9")) {
                        throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "对接人信息错误");
                    }
                    List<SafetyEnterAuthRt> newDockRt = null;
                    //根据组织结构获取权限
                    if (StringUtils.isNotBlank(rt.getFdOrgCode())) {
                        newDockRt = enterAuthMapper.selectEnterAuthByOrgCode(rt.getFdOrgCode());
                    }
                    List factoryList = new ArrayList();
                    if (CollectionUtils.isEmpty(newDockRt)) {
                        // 添加默认权限
                        List<DictItemRt> list = enterFilingMapper.selectFilingFactoryList();
                        for (int i = 0; i < list.size(); i++) {
                            Map map = new HashMap();
                            map.put("factoryCode", list.get(i).getItemCode());
                            map.put("factory", list.get(i).getItemName());
                            factoryList.add(map);
                        }
                        dock.setDayNum(1);
                    } else {
                        SafetyEnterAuthRt authRt = newDockRt.get(0);
                        String factoryCode = authRt.getFdFactoryCode();
                        //处理工厂
                        formatFactoryList(factoryList,factoryCode,factoryMap);
                        dock.setDayNum(authRt.getFdLimitDay());
                    }
                    dock.setFdDockNo(rt.getFdEmpNo());
                    dock.setFdDockDepartment(rt.getFdEmpDepartment());
                    dock.setFactoryList(factoryList);
                }
            } else {
                SafetyEnterAuthRt authRt = dockRt.get(0);
                String factoryCode = authRt.getFdFactoryCode();
                List factoryList = new ArrayList();
                //处理工厂
                formatFactoryList(factoryList,factoryCode,factoryMap);
                dock.setFdDockNo(authRt.getFdEmpNo());
                dock.setFdDockDepartment(authRt.getFdEmpDepartment());
                dock.setDayNum(authRt.getFdLimitDay());
                dock.setFactoryList(factoryList);

                List fillTypeList = formatFillingUserType(enterFlag, authRt.getFdFilingUserTypeCode(), dictItemMap);
                dock.setPerTypeList(fillTypeList);
            }
        }
    }

    /**
     * 处理工厂
     * @param factoryList
     * @param factoryCodeStr
     * @param factoryMap
     */
    private void formatFactoryList(List factoryList,String factoryCodeStr,Map<Integer,String> factoryMap){
        if (StringUtils.isNotBlank(factoryCodeStr)) {
            String[] fcs = factoryCodeStr.split(",");
            for (String fc : fcs) {
                Map map = new HashMap();
                String facId = fc;
                String facName = factoryMap.get(Integer.parseInt(facId));
                map.put("factoryCode", facId);
                map.put("factory", facName);
                factoryList.add(map);
            }
        }
    }

    /**
     *
     * @param enterFlag
     * @param filingUserTypeCodes
     * @param dictItemMap
     * @return
     */
    private List formatFillingUserType(Boolean enterFlag,String filingUserTypeCodes,Map<String,DictItemRt> dictItemMap){
        List fillTypeList = new ArrayList();
        if(StringUtils.isNotBlank(filingUserTypeCodes)){
            String[] fcs = filingUserTypeCodes.split(",");
            for (String code : fcs) {
                DictItemRt curRt = dictItemMap.get(code);
                if (curRt != null) {
                    boolean shouldAdd = enterFlag ? "1".equals(curRt.getShowFlag()) : !"1".equals(curRt.getShowFlag());
                    if (shouldAdd) {
                        Map map = new HashMap();
                        map.put("perUserTypeCode", code);
                        map.put("perUserType", curRt.getItemName());
                        fillTypeList.add(map);
                    }
                }
            }
        }
        return fillTypeList;
    }
}
