package com.yutong.safety.core.service.enter;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.LogMessageAddParam;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetyFieldManageDO;
import com.yutong.safety.core.db.entity.enter.SafetyEnterAuth;
import com.yutong.safety.core.db.entity.enter.SafetyEnterLinkAuthGroup;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterAuthRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterAuthSh;
import com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants;
import com.yutong.safety.core.db.entity.result.DictItemRt;
import com.yutong.safety.core.db.mapper.SafetyEmployeeMessageDOBizMapper;
import com.yutong.safety.core.db.mapper.SafetyFieldManageDOMapper;
import com.yutong.safety.core.db.mapper.enter.SafetyEnterAuthMapper;
import com.yutong.safety.core.db.mapper.enter.SafetyEnterFilingMapper;
import com.yutong.safety.core.dto.entry.SafetyEntryButtJointDto;
import com.yutong.safety.core.handler.enter.SafetyEnterAuthHandler;
import com.yutong.safety.core.service.SafetyBaseService;
import com.yutong.safety.core.service.SafetyDictItemService;
import com.yutong.safety.core.service.SafetyFieldManageService;
import com.yutong.safety.core.service.SafetyLogMessageService;
import com.yutong.safety.core.service.entry.SafetyEntryCompanyRegisterHistoryService;
import com.yutong.safety.core.service.entry.SafetyEntryCompanyRegisterService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SafetyEnterAuthService implements SafetyEnterAuthHandler {
    @Autowired
    private SafetyEnterAuthMapper enterAuthMapper;
    @Autowired
    private SafetyDictItemService dictItemService;
    @Autowired
    private SafetyEnterFilingMapper enterFilingMapper;
    @Autowired
    private SafetyEmployeeMessageDOBizMapper employeeMessageDOBizMapper;
    @Autowired
    private SafetyLogMessageService safetyLogMessageService;
    @Autowired
    SafetyFieldManageDOMapper safetyFieldManageDOMapper;

    @Resource
    private SafetyEntryCompanyRegisterService safetyEntryCompanyRegisterService;

    @Resource
    private SafetyEntryCompanyRegisterHistoryService safetyEntryCompanyRegisterHistoryService;

    @Resource
    private SafetyDictItemService safetyDictItemService;
    @Resource
    private SafetyFieldManageService safetyFieldManageService;
    @Autowired
    private SafetyBaseService safetyBaseService;

    @Override
    public PageVO selectEnterAuthPage(SafetyEnterAuthSh enterAuthSh) {
        List<SafetyEnterAuthRt> list = enterAuthMapper.selectEnterAuthList(enterAuthSh);
        Integer count = enterAuthMapper.selectEnterAuthCount(enterAuthSh);
        return new PageVO(count, list);
    }

    @Override
    public SafetyEnterAuthRt selectEnterAuthInfo(Integer id) {
        SafetyEnterAuthRt rt = enterAuthMapper.selectEnterAuthById(id);
        return rt;
    }

    @Override
    public List selectEnterAuth(String token) {
        List list = new ArrayList();
        String tokenUser = TokenUtils.getWorkNo(token);
        //1.获取该用户的备案权限配置
        List<SafetyEnterAuthRt> authList = enterAuthMapper.selectEnterAuthByEmpNo(tokenUser);
        if(CollectionUtils.isEmpty(authList)){
            //2.个人权限未匹配到时，查询对应登录人所属组织查询权限
            SafetyEmployeeMessageDO messageDO = employeeMessageDOBizMapper.selectByEmpNo(tokenUser);
            if(ObjectUtils.isEmpty(messageDO) || StringUtils.isBlank(messageDO.getOrgCode())){
                return list;
            }
            authList = enterAuthMapper.selectEnterAuthByOrgCode(messageDO.getOrgCode());
        }
        if(CollectionUtils.isEmpty(authList)){
            return list;
        }
        String authCode = authList.get(0).getFdAuthCode();
        String authName = authList.get(0).getFdAuth();
        if(StringUtils.isBlank(authCode)){
            return list;
        }

        String[] arr1 = authCode.split(",");
        String[] arr2 = authName.split(",");
        for(int i=0;i<arr1.length;i++){
            Map map = new HashMap();
            map.put("authCode", arr1[i]);
            map.put("authName", arr2[i]);
            list.add(map);
        }
        return list;
    }

    @Override
    public List selectEnterAuthDict() {
        // 系统操作权限
        return dictItemService.selectDictItemListBySupperCode("enter_filing_auth");
    }

    @Override
    public void insertEnterAuth(SafetyEnterAuth enterAuth, String token) {
        List<SafetyEnterAuthRt> authList = enterAuthMapper.selectEnterAuthByEmpNo(enterAuth.getFdEmpNo());
        if(CollectionUtils.isNotEmpty(authList)){
            throw new BizException(ResponseEnum.FAIL.getCode(), "员工权限信息已存在，不能重复添加");
        }
        if(StringUtils.isBlank(enterAuth.getFdConfigType())){
            enterAuth.setFdConfigType("1"); //默认人员
        }
        String tokenUser = TokenUtils.getRealName(token);
        Date date = new Date();
        enterAuth.setCreateTime(date);
        enterAuth.setCreateUser(tokenUser);
        enterAuth.setUpdateTime(date);
        enterAuth.setUpdateUser(tokenUser);
        resetSafetyEnterAuth(enterAuth);
        enterAuthMapper.insertEnterAuth(enterAuth);
    }

    @Override
    public void updateEnterAuth(SafetyEnterAuth enterAuth, String token) {
        String tokenUser = TokenUtils.getRealName(token);
        Date date = new Date();
        enterAuth.setUpdateTime(date);
        enterAuth.setUpdateUser(tokenUser);
        resetSafetyEnterAuth(enterAuth);
        if(StringUtils.isNotBlank(enterAuth.getFdPowerOrgCode())){
            Integer authId = enterAuth.getId();
            enterAuthMapper.deleteLinkAuthGroup(enterAuth.getId());
            String[] str = enterAuth.getFdPowerOrgCode().split(",");

            List<SafetyEnterLinkAuthGroup> authGroups = new ArrayList<>();

            for(String s:str){
                SafetyEnterLinkAuthGroup authGroup = new SafetyEnterLinkAuthGroup();
                authGroup.setAuthId(authId);
                authGroup.setGroupCode(s);
                authGroups.add(authGroup);
            }
            enterAuthMapper.insertBatchAuthGroup(authGroups);
        }
        enterAuthMapper.updateEnterAuth(enterAuth);
    }

    @Override
    public void deleteEnterAuth(Integer id, String token) {
        if(id == null){
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        String tokenUser = TokenUtils.getRealName(token);
        SafetyEnterAuthRt rt = enterAuthMapper.selectEnterAuthById(id);
        LogUtils.info("{} 删除备案权限信息 {}", tokenUser, JsonUtils.toString(rt));
        enterAuthMapper.deleteEnterAuth(id);

        // 同时删除备案人负责人， 把需要删除的人同步到历史表
        safetyEntryCompanyRegisterHistoryService.saveSelect(rt.getFdEmpNo());
        safetyEntryCompanyRegisterService.deleteByEmpNo(rt.getFdEmpNo());
    }

    @Override
    public List selectAuthFactory(String token, String empNo, String filingId) {
        String[] codes = null;
        String[] names = null;
        if(StringUtils.isNotBlank(filingId)){
            Map<String,String> map = enterAuthMapper.selectFilingFactory(filingId);
            if(map != null && StringUtils.isNotBlank(map.get("factoryCode"))){
                codes = map.get("factoryCode").split(",");
            }
        }
        if(codes == null || codes.length == 0){
            if(StringUtils.isBlank(token) && StringUtils.isBlank(empNo)){
                throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token失效");
            }
            if(StringUtils.isBlank(empNo)){
                empNo = TokenUtils.getWorkNo(token);
            }

            SafetyEnterAuthRt factory = enterAuthMapper.selectAuthFactory(empNo);
            if(factory != null && StringUtils.isNotBlank(factory.getFdFactoryCode())){
                codes = factory.getFdFactoryCode().split(",");
            }

            if(codes == null || codes.length == 0){
                //2.个人权限未匹配到时，查询对应登录人所属组织查询权限
                SafetyEmployeeMessageDO messageDO = employeeMessageDOBizMapper.selectByEmpNo(empNo);
                if(!ObjectUtils.isEmpty(messageDO) && StringUtils.isNotBlank(messageDO.getOrgCode())){
                    List<SafetyEnterAuthRt> authList = enterAuthMapper.selectEnterAuthByOrgCode(messageDO.getOrgCode());
                    if(!CollectionUtils.isEmpty(authList) && StringUtils.isNotBlank(authList.get(0).getFdFactoryCode())){
                        codes = authList.get(0).getFdFactoryCode().split(",");
                    }
                }
            }
        }
        List list = new ArrayList();

        if(codes == null || codes.length == 0){
            // 备案厂区
            List<DictItemRt> dictItemRts = enterFilingMapper.selectFilingFactoryList();
            for (DictItemRt dictItemRt : dictItemRts) {
                Map<String,String> map = new HashMap<>();
                map.put("itemCode", dictItemRt.getItemCode());
                map.put("itemName", dictItemRt.getItemName());
                list.add(map);
            }
        }else {
            //取所有的厂区字典和备案类型字典
            Map<Integer,String> factoryMap = safetyFieldManageService.getFactoryNameList();
            for(int i=0;i<codes.length;i++){
                Map<String,String> map = new HashMap<>();
                String facCode = codes[i];
                if(StringUtils.isNotBlank(facCode)){
                    map.put("itemCode", codes[i]);
                    String name = factoryMap.get(Integer.valueOf(facCode));
                    map.put("itemName", name);
                    list.add(map);
                }
            }
        }
        return list;
    }

    public List<SafetyEnterAuthRt> selectList(SafetyEnterAuthSh enterAuthSh){
        enterAuthSh.setExport("1");
        return enterAuthMapper.selectEnterAuthList(enterAuthSh);
    }

    private SafetyEnterAuth resetSafetyEnterAuth(SafetyEnterAuth enterAuth){
        // 备案类型
        Map list1 = dictItemService.selectDictItemMapBySupperCode("enter_filing_type");
        // 人员类型
        Map list2 = dictItemService.selectDictItemMapBySupperCode("enter_per_type");
        // 系统操作权限
        Map list3 = dictItemService.selectDictItemMapBySupperCode("enter_filing_auth");
        // 备案厂区
        List<DictItemRt> list4 = enterFilingMapper.selectFilingFactoryList();

        String fdAuthCode = enterAuth.getFdAuth();
        if(StringUtils.isNotBlank(fdAuthCode)){
            StringBuilder fdAuth = new StringBuilder();
            String[] arr = fdAuthCode.split(",");
            for(int i=0;i<arr.length;i++){
                fdAuth.append(list3.get(arr[i]));
                if(i < arr.length - 1){
                    fdAuth.append(",");
                }
            }
            enterAuth.setFdAuthCode(fdAuthCode);
            enterAuth.setFdAuth(fdAuth.toString());
        }

        String fdFactoryCode = enterAuth.getFdFactory();
        if(StringUtils.isNotBlank(fdFactoryCode)){
            StringBuilder fdFactory = new StringBuilder();
            String[] arr = fdFactoryCode.split(",");
            for(int i=0;i<arr.length;i++){
                for (DictItemRt fm : list4) {
                    String itemCode = fm.getItemCode();
                    if (itemCode.equals(arr[i])) {
                        fdFactory.append(fm.getItemName());
                        break;
                    }
                }
                if(i < arr.length - 1){
                    fdFactory.append(",");
                }
            }
            enterAuth.setFdFactoryCode(fdFactoryCode);
            enterAuth.setFdFactory(fdFactory.toString());
        }

        boolean bool = false;
        String filingTypeCode = enterAuth.getFdFilingType();
        if(StringUtils.isNotBlank(filingTypeCode)){
            StringBuilder filingType = new StringBuilder();
            String[] arr = filingTypeCode.split(",");
            for(int i=0;i<arr.length;i++){
                filingType.append(list1.get(arr[i]));
                if(i < arr.length - 1){
                    filingType.append(",");
                }
                if("20".equals(arr[i])){
                    bool = true;
                }
            }
            enterAuth.setFdFilingTypeCode(filingTypeCode);
            enterAuth.setFdFilingType(filingType.toString());
        }


        if(bool){
            String userTypeCode = enterAuth.getFdFilingUserType();
            if(StringUtils.isNotBlank(userTypeCode)){
                StringBuilder userType = new StringBuilder();
                String[] arr = userTypeCode.split(",");
                for(int i=0;i<arr.length;i++){
                    userType.append(list2.get(arr[i]));
                    if(i < arr.length - 1){
                        userType.append(",");
                    }
                }
                enterAuth.setFdFilingUserTypeCode(userTypeCode);
                enterAuth.setFdFilingUserType(userType.toString());
            }
        }else {
            enterAuth.setFdFilingUserTypeCode("");
            enterAuth.setFdFilingUserType("");
        }

        return enterAuth;
    }

    @Override
    public Map getSearchParams() {
        // 备案类型
        List<DictItemRt> list1 = dictItemService.selectDictItemListBySupperCode("enter_filing_type");
        // 人员类型
        List<DictItemRt> list2 = dictItemService.selectDictItemListBySupperCode("enter_per_type");
        // 系统操作权限
        List<DictItemRt> list3 = dictItemService.selectDictItemListBySupperCode("enter_filing_auth");
        // 备案厂区
        List<DictItemRt> list4 = enterFilingMapper.selectFilingFactoryList();
        Map map = new HashMap();
        map.put("p1", list1);
        map.put("p2", list2);
        map.put("p3", list3);
        map.put("p4", list4);
        return map;
    }

    @Override
    public SafetyEnterAuthRt checkAuthEmpInfo(String empNo, String phone) {
        if(StringUtils.isBlank(empNo)){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "员工号不能为空");
        }

        SafetyEnterAuthSh enterAuthSh = new SafetyEnterAuthSh();
        enterAuthSh.setFdEmpNo(empNo);
        SafetyEnterAuthRt rt = enterAuthMapper.selectEmpInfoByEmpNo(enterAuthSh);
        if (rt == null) {
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "根据员工号，未查询到有效信息");
        }

        //获取厂区信息，并根据厂区表进行排序
        String fdFactoryCode = rt.getFdFactoryCode();
        if(StringUtils.isNotBlank(fdFactoryCode)){
            List<String> list = Arrays.asList(fdFactoryCode.split(","));
            List<SafetyFieldManageDO> listByIdList = safetyFieldManageDOMapper.getListByIdList(list);

            StringBuilder factoryCode = new StringBuilder();
            StringBuilder factoryName = new StringBuilder();

            List<Map> factoryList = new ArrayList<>();
            listByIdList.stream().forEach(en -> {
                factoryCode.append(",").append(en.getId());
                factoryName.append(",").append(en.getFieldName());
            });

            if (StringUtils.isNotBlank(factoryCode)) {
                rt.setFdFactoryCode(factoryCode.toString().substring(1));
            }
            if (StringUtils.isNotBlank(factoryName)) {
                rt.setFdFactory(factoryName.toString().substring(1));
            }
        }


        return rt;
    }


    @Override
    public SafetyEnterAuthRt checkAuthEmpInfoNew(String empNo, String phone,Boolean enterFlag) {
        if(StringUtils.isBlank(empNo)){
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "员工号不能为空");
        }

        SafetyEnterAuthSh enterAuthSh = new SafetyEnterAuthSh();
        enterAuthSh.setFdEmpNo(empNo);
        SafetyEnterAuthRt rt = enterAuthMapper.selectEmpInfoByEmpNo(enterAuthSh);
        if (rt == null) {
            throw new BizException(ResponseEnum.PARAM_ERROR.getCode(), "根据员工号，未查询到有效信息");
        }

        //获取厂区信息，并根据厂区表进行排序
        String fdFactoryCode = rt.getFdFactoryCode();
        if(StringUtils.isNotBlank(fdFactoryCode)){
            List<String> list = Arrays.asList(fdFactoryCode.split(","));
            List<SafetyFieldManageDO> listByIdList = safetyFieldManageDOMapper.getListByIdList(list);

            StringBuilder factoryCode = new StringBuilder();
            StringBuilder factoryName = new StringBuilder();

            List<Map> factoryList = new ArrayList<>();
            listByIdList.stream().forEach(en -> {
                factoryCode.append(",").append(en.getId());
                factoryName.append(",").append(en.getFieldName());

                Map map = new HashMap();
                map.put("factoryCode", en.getId()+"");
                map.put("factory", en.getFieldName());
                factoryList.add(map);
            });

            if (StringUtils.isNotBlank(factoryCode)) {
                rt.setFdFactoryCode(factoryCode.toString().substring(1));
            }
            if (StringUtils.isNotBlank(factoryName)) {
                rt.setFdFactory(factoryName.toString().substring(1));
            }
            rt.setFactoryList(factoryList);

            List<DictItemRt> dictItemDOS = safetyDictItemService.selectDictItemListBySupperCode(SafetyEntryConstants.ENTRY_DICT_SUPPER_CODE_TYPE);
            Map<String, DictItemRt> dictItemMap = dictItemDOS.stream().collect(Collectors.toMap(DictItemRt::getItemCode, e -> e));
            List fillTypeList = formatFillingUserType(enterFlag, rt.getFdFilingUserTypeCode(), dictItemMap);
            rt.setPerTypeList(fillTypeList);
        }

        return rt;
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

    /**
     * 更新备案权限的组织权限
     * @param id 备案信息ID
     * @param powerOrgCode 组织的编码拼接串
     * @param userName 当前修改人
     * @return 返回更新条数
     */
    public int updatePowerOrg(Integer id,String powerOrgCode,String userName){
        SafetyEnterAuthRt enterAuth = enterAuthMapper.selectEnterAuthById(id);
        if(ObjectUtils.isEmpty(enterAuth) || !"2".equals(enterAuth.getFdConfigType())){
            throw new BizException("2000","只能给“组织”类型设置组织权限");
        }
        enterAuthMapper.deleteLinkAuthGroup(id);

        String[] str = powerOrgCode.split(",");
        List<SafetyEnterLinkAuthGroup> authGroups = new ArrayList<>();
        for(String s:str){
            SafetyEnterLinkAuthGroup authGroup = new SafetyEnterLinkAuthGroup();
            authGroup.setAuthId(id);
            authGroup.setGroupCode(s);
            authGroups.add(authGroup);
        }
        try{
            //操作日志记录
            LogMessageAddParam log = new LogMessageAddParam();
            log.setOperatePage("设备物流车备案组织权限");
            log.setAbility("修改");
            log.setMessage(enterAuth.getFdEmpDepartment()+"修改为"+powerOrgCode);
            log.setOperator(userName);
            safetyLogMessageService.addLogMessage(log);
        }catch (Exception e){
            LogUtils.warn(ResponseEnum.EXCEPTION.getCode(),"保存日志异常");
        }

        return enterAuthMapper.insertBatchAuthGroup(authGroups);
    }

    public SafetyEntryButtJointDto selectButtJointInfo(String empNo) {
        List<SafetyEnterAuthRt> authList = enterAuthMapper.selectEnterAuthByEmpNo(empNo);

        if (!CollectionUtils.isEmpty(authList)) {
            SafetyEnterAuthRt authRt = authList.get(0);
            SafetyEntryButtJointDto data = new SafetyEntryButtJointDto();
            BeanUtils.copyProperties(authRt, data);
            data.setFilingPark(formatFactory(authRt.getFdFactoryCode()));
            List<DictItemRt> dictItemDOS = safetyDictItemService.selectDictItemListBySupperCode(SafetyEntryConstants.ENTRY_DICT_SUPPER_CODE_TYPE);
            if (!CollectionUtils.isEmpty(dictItemDOS)) {
                Map<String, DictItemRt> dictItemMap = dictItemDOS.stream().collect(Collectors.toMap(DictItemRt::getItemCode, e -> e));
                data.setFilingType(formatUserTypeMap(authRt.getFdFilingUserTypeCode(), dictItemMap));
            }
            return data;
        } else {
            throw new BizException(ResponseEnum.FAIL.getCode(), "对接人信息不存在");
        }
    }

    /**
     *
     * @param factoryCodeStr
     * @return
     */
    private Map<String,String> formatFactory(String factoryCodeStr){
        List<String> list = Arrays.asList(factoryCodeStr.split(","));
        List<SafetyFieldManageDO> listByIdList = safetyFieldManageDOMapper.getListByIdList(list);
        Map<String,String> retFacMap = new HashMap<>();
        listByIdList.stream().forEach(en -> {
            retFacMap.put(en.getId()+"",en.getFieldName());
        });
        return retFacMap;
    }

    /**
     *
     * @param filingUserTypeCodes
     * @param dictItemMap
     * @return
     */
    private Map<String,String> formatUserTypeMap(String filingUserTypeCodes,Map<String,DictItemRt> dictItemMap){
        Map<String,String> fillTypeMap = new HashMap<>();
        if(StringUtils.isNotBlank(filingUserTypeCodes)){
            String[] fcs = filingUserTypeCodes.split(",");
            for (String code : fcs) {
                DictItemRt curRt = dictItemMap.get(code);
                if (curRt != null) {
                    if ("1".equals(curRt.getShowFlag())) {
                        Map map = new HashMap();
                        map.put("perUserTypeCode", code);
                        map.put("perUserType", curRt.getItemName());
                        fillTypeMap.put(code,curRt.getItemName());
                    }
                }
            }
        }
        return fillTypeMap;
    }
}
