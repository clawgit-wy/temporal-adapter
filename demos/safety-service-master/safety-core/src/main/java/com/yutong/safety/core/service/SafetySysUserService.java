package com.yutong.safety.core.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.*;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.sys.SysUserVO;
import com.yutong.safety.core.db.entity.*;
import com.yutong.safety.core.db.mapper.SafetySysUserDOBizMapper;
import com.yutong.safety.core.db.mapper.SafetySysUserDOMapper;
import com.yutong.safety.core.db.mapper.SafetySysUserRoleDOMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * create gwcuiyx 2023.07.28
 * 账号信息表--service
 */
@Service
public class SafetySysUserService {
    @Autowired
    private SafetySysUserDOMapper safetySysUserDOMapper;
    @Autowired
    private SafetySysUserDOBizMapper safetySysUserDOBizMapper;
    @Autowired
    private SafetyLogMessageService safetyLogMessageService;
    @Autowired
    private SafetySysRoleService safetySysRoleService;
    @Autowired
    private SafetySysUserRoleDOMapper safetySysUserRoleDOMapper;
    @Autowired
    private SafetyFieldManageService safetyFieldManageService;

    @Value("${iam.employee.workNoConvert.url}")
    private String employeeWorkNoConvertUrl;

    @Autowired
    private WebserviceUtil webserviceUtil;

    /**
     * 数据列表分页
     * @param param
     * @return
     */
    public List<SysUserVO> pageList(UserMessageListParam param, Map<Integer, String> factoryMap) {
        //参数设置
        Map<String,Object> pm = new HashMap<>();
        pm.put("startRowNum", param.getOffset());
        pm.put("endRowNum", param.getLimit());
        pm.put("sh", param);
        if (StringUtils.isNotEmpty(param.getFactoryId())) {
            Set<String> ids = Sets.newHashSet(param.getFactoryId().split(","));
            StringBuilder serachSql = new StringBuilder();
            for (String id : ids) {
                serachSql.append("su.factory_id LIKE '%").append(id).append("%' or ");
            }
            if (StringUtils.isNotEmpty(serachSql.toString())) {
                serachSql = new StringBuilder(serachSql.substring(0, serachSql.length() - 3));
            }
            pm.put("factoryIds", serachSql.toString());
        }

        if (StringUtils.isNotEmpty(param.getPowerFactoryId())) {
            Set<String> ids = Sets.newHashSet(param.getPowerFactoryId().split(","));
            StringBuilder serachSql = new StringBuilder();
            for (String id : ids) {
                serachSql.append("su.power_factory_id LIKE '%").append(id).append("%' or ");
            }
            if (StringUtils.isNotEmpty(serachSql.toString())) {
                serachSql = new StringBuilder(serachSql.substring(0, serachSql.length() - 3));
            }
            pm.put("powerFactoryIds", serachSql.toString());
        }


        List<SysUserVO> list = safetySysUserDOBizMapper.pageList(pm);
        //组中名称
        for (SysUserVO sysUserVO : list) {
            sysUserVO.setFactoryName(StringUtils.isNotEmpty(sysUserVO.getFactoryId()) ? getFactoryNames(sysUserVO.getFactoryId(), factoryMap) : "");
            sysUserVO.setPowerFactoryName(StringUtils.isNotEmpty(sysUserVO.getPowerFactoryId()) ? getFactoryNames(sysUserVO.getPowerFactoryId(), factoryMap) : "");
        }
        return list;
    }

    /**
     * 根据id获取名称值
     * @param factroyIds
     * @param factoryMap
     * @return
     */
    public String getFactoryNames(String factroyIds, Map<Integer, String> factoryMap) {
        Set<String> ids = Sets.newHashSet(factroyIds.split(","));
        StringBuilder factoryName = new StringBuilder();
        for (String s : ids) {
            if (factoryMap.containsKey(Integer.valueOf(s))) {
                factoryName.append(factoryMap.get(Integer.valueOf(s))).append(",");
            }
        }
        if (StringUtils.isNotEmpty(factoryName.toString())) {
            return factoryName.substring(0, factoryName.length()-1);
        }
        return "";
    }

    /**
     * 总数据量
     * @param param 入参
     * @return
     */
    public long pageListCount(UserMessageListParam param) {
        //参数设置
        Map<String,Object> pm = new HashMap<>();
        pm.put("startRowNum", param.getOffset());
        pm.put("endRowNum", param.getLimit());
        pm.put("sh", param);
        if (StringUtils.isNotEmpty(param.getFactoryId())) {
            Set<String> ids = Sets.newHashSet(param.getFactoryId().split(","));
            StringBuilder serachSql = new StringBuilder();
            for (String id : ids) {
                serachSql.append("su.factory_id LIKE '%").append(id).append("%' or ");
            }
            if (StringUtils.isNotEmpty(serachSql.toString())) {
                serachSql = new StringBuilder(serachSql.substring(0, serachSql.length() - 3));
            }
            pm.put("factoryIds", serachSql.toString());
        }

        if (StringUtils.isNotEmpty(param.getPowerFactoryId())) {
            Set<String> ids = Sets.newHashSet(param.getPowerFactoryId().split(","));
            StringBuilder serachSql = new StringBuilder();
            for (String id : ids) {
                serachSql.append("su.power_factory_id LIKE '%").append(id).append("%' or ");
            }
            if (StringUtils.isNotEmpty(serachSql.toString())) {
                serachSql = new StringBuilder(serachSql.substring(0, serachSql.length() - 3));
            }
            pm.put("powerFactoryIds", serachSql.toString());
        }

        return safetySysUserDOBizMapper.pageListCount(pm);
    }

    /**
     * 数据添加
     * @param param
     */
    public void insertSysUser(UserMessageAddParam param, String tocken) {
        //检验是否存在
        int has = checkSysUserByEmpNo(param.getEmpNo());
        if(has == 0){
            Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
            //添加账号信息
            SafetySysUserDO safetySysUserDO = getUserMessage(param.getEmpNo());
            if (safetySysUserDO == null) {
                safetySysUserDO = new SafetySysUserDO();
            } else {
                throw new BizException(ResponseEnum.FAIL.getCode(),"该用户已存在！");
            }
            BeanUtils.copyProperties(param, safetySysUserDO);
            safetySysUserDO.setDeleteFalg(false);
            safetySysUserDO.setCreateTime(new Date());
            safetySysUserDO.setUpdateTime(new Date());
            safetySysUserDO.setModifyEmp(tockenUser.get("realName"));
            safetySysUserDO.setModifyEmpNo(tockenUser.get("workNo"));
            PreconditonsEx.checkArgument(safetySysUserDOMapper.insert(safetySysUserDO) == 1, ResponseEnum.FAIL);
            //添加角色
            if(Objects.nonNull(param.getRoleId())){
                String[] roleArr = param.getRoleId().split(",");
                Map map = new HashMap();
                map.put("userId", safetySysUserDO.getId());
                map.put("roleIds", roleArr);
                safetySysUserDOBizMapper.setUserRoles(map);
            }

            //操作日志记录
            LogMessageAddParam log = new LogMessageAddParam();
            log.setOperatePage("用户管理");
            log.setAbility("添加");
            log.setMessage(param.getEmpName());
            log.setOperator(tockenUser.get("realName"));
            safetyLogMessageService.addLogMessage(log);
        } else {
            throw new BizException(ResponseEnum.FAIL.getCode(),"账号已存在");
        }
    }

    /**
     * 根据员工号获取数据
     * @param empNo
     * @return
     */
    public SafetySysUserDO getUserMessage(String empNo) {
        SafetySysUserDOExample example = new SafetySysUserDOExample();
        SafetySysUserDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andEmpNoEqualTo(empNo);
        List<SafetySysUserDO> list = safetySysUserDOMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 数据数量
     * @param empNo
     * @return
     */
    public int checkSysUserByEmpNo(String empNo) {
        SafetySysUserDOExample example = new SafetySysUserDOExample();
        SafetySysUserDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andEmpNoEqualTo(empNo);
        List<SafetySysUserDO> list = safetySysUserDOMapper.selectByExample(example);
        return list.size();
    }

    /**
     * 数据更新操作
     * @param param
     */
    public void updateSysUserById(UserMessageUpdateParam param, String tocken) {
        SafetySysUserDO safetySysUserDO = safetySysUserDOMapper.selectByPrimaryKey(param.getId());
        if(safetySysUserDO == null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //同步删除账号对应的角色
        safetySysUserDOBizMapper.deleteUserRolesByUserId(param.getId());
        BeanUtils.copyProperties(param, safetySysUserDO);
        safetySysUserDO.setUpdateTime(new Date());
        safetySysUserDO.setModifyEmpNo(tockenUser.get("workNo"));
        safetySysUserDO.setModifyEmp(tockenUser.get("realName"));
        PreconditonsEx.checkArgument(safetySysUserDOMapper.updateByPrimaryKey(safetySysUserDO) == 1, ResponseEnum.FAIL);
        //添加角色
        if(StringUtils.isNotEmpty(param.getRoleId())){
            String[] roleArr = param.getRoleId().split(",");
            Map map = new HashMap();
            map.put("userId", safetySysUserDO.getId());
            map.put("roleIds", roleArr);
            safetySysUserDOBizMapper.setUserRoles(map);
        }

        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("用户管理");
        log.setAbility("修改");
        log.setMessage(param.getEmpName());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 数据删除操作
     * @param id
     */
    public void deleteSysUserById(Integer id, String tocken) {
        SafetySysUserDO safetySysUserDO = safetySysUserDOMapper.selectByPrimaryKey(id);
        if(safetySysUserDO == null){
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        safetySysUserDO.setDeleteFalg(true);
        //删除用户记录
        PreconditonsEx.checkArgument(safetySysUserDOMapper.updateByPrimaryKey(safetySysUserDO) == 1, ResponseEnum.FAIL);
        //同步删除账号对应的角色
        safetySysUserDOBizMapper.deleteUserRolesByUserId(id);
        //清除缓存
//        ShiroUtil.clearCache();

        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("用户管理");
        log.setAbility("删除");
        log.setMessage(safetySysUserDO.getEmpName());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 数据设置
     * @param userId
     * @param roleIds
     */
    public void setUserRoles(Integer userId, String roleIds) {
        //删除原来的用户关联的角色
        safetySysUserDOBizMapper.deleteUserRolesByUserId(userId);
        //添加用户角色关联记录
        if(roleIds.length() > 0){
            String[] roleArr = roleIds.split(",");
            Map map = new HashMap();
            map.put("userId", userId);
            map.put("roleIds", roleArr);
            safetySysUserDOBizMapper.setUserRoles(map);
        }
        //修改角色信息变更日期
        SafetySysUserDO safetySysUserDO = new SafetySysUserDO();
        safetySysUserDO.setId(userId);
        safetySysUserDO.setUpdateTime(new Date());
        PreconditonsEx.checkArgument(safetySysUserDOMapper.insert(safetySysUserDO) == 1, ResponseEnum.FAIL);
        //清除缓存
//        ShiroUtil.clearCache();
    }

    /**
     * 根据用户名获取数据
     * @param userName
     * @return
     */
    public SafetySysUserDO getSysUserByUserName(String userName) {
        SafetySysUserDOExample example = new SafetySysUserDOExample();
        SafetySysUserDOExample.Criteria criteria = example.createCriteria();
        criteria.andUserNameEqualTo(userName);
        List<SafetySysUserDO> sysUserList = safetySysUserDOMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(sysUserList)) {
            return sysUserList.get(0);
        }
        return null;
    }

    /**
     * 获取数据
     * @param id
     * @return
     */
    public List<Integer> getUserHasRoles(Integer id) {
        return safetySysUserDOBizMapper.getUserHasRoles(id);
    }

    /**
     * 数据导入操作
     * @param param
     * @return
     */
    public Object importSysUser(CommonImportParam param, String tocken) {
        Workbook workbook = ExcelUtils.getWorkbook(param.getFile());
        Sheet sheet = workbook.getSheetAt(0);
        //获得当前sheet的开始行
        int firstRowNum  = sheet.getFirstRowNum();
        //获得当前sheet的结束行
        int lastRowNum = sheet.getLastRowNum();
        String result;
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        SafetySysUserDO safetySysUserDO = null;
        SafetySysUserDO csysUserDO = null;
        SafetySysUserRoleDO safetySysUserRoleDO = null;
        SafetySysRoleDO safetySysRoleDO = null;
        List<SafetySysRoleDO> roleList = safetySysRoleService.getAllRoleList();
        Map<String, SafetySysRoleDO> roleMap = roleList.stream().collect(Collectors.toMap(SafetySysRoleDO::getRoleName, Function.identity()));
        Map<String, SafetyFieldManageDO> factoryMap = safetyFieldManageService.getFactoryList();
        int rowCount = 0;
        boolean flag = false;
        int rum = 0;
        List<SafetySysUserDO> insertList = Lists.newArrayList();
        String expenseMessage = "";
        for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++) {
            //获得当前行
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            try {
                //用户名
                String userName = ExcelUtils.getValue(row.getCell(1));
                //姓名
                String empName = ExcelUtils.getValue(row.getCell(2));
                //员工号
                String empNo = ExcelUtils.getValue(row.getCell(3));
                //角色
                String roleName = ExcelUtils.getValue(row.getCell(4));
                //授权厂区
                String factoryName = ExcelUtils.getValue(row.getCell(5));
                //授权厂区
                String powerFactoryName = ExcelUtils.getValue(row.getCell(6));

                if (StringUtils.isEmpty(empName) && StringUtils.isEmpty(empNo) && StringUtils.isEmpty(roleName)) {
                    continue;
                }
                if(StringUtils.isEmpty(empName) || StringUtils.isEmpty(empNo) || StringUtils.isEmpty(roleName)) {
                    rum = rowNum;
                    flag = true;
                    expenseMessage = "第"+rum+"行，有必填项为空！请检查后再导入数据！";
                    break;
                }
                csysUserDO = getUserMessage(empNo);
                if (csysUserDO != null) {
                    rum = rowNum;
                    flag = true;
                    expenseMessage = "第"+rum+"行，该员工已存在！请检查后再导入数据！";
                    break;
                }
                safetySysUserDO = new SafetySysUserDO();
                safetySysUserDO.setUserName(userName);
                safetySysUserDO.setEmpName(empName);
                safetySysUserDO.setEmpNo(empNo);
                safetySysUserDO.setModifyEmp(tockenUser.get("realName"));
                safetySysUserDO.setModifyEmpNo(tockenUser.get("workNo"));
                safetySysUserDO.setUpdateTime(new Date());
                safetySysUserDO.setDeleteFalg(false);
                safetySysUserDO.setCreateTime(new Date());
                safetySysUserDO.setUserName(roleName);
                //访客发卡厂区权限
                if (StringUtils.isNotEmpty(factoryName)) {
                    safetySysUserDO.setFactoryId(getFactoryIds(factoryMap, factoryName));
                }
                //授权厂区权限
                if (StringUtils.isNotEmpty(powerFactoryName)) {
                    safetySysUserDO.setPowerFactoryId(getFactoryIds(factoryMap, powerFactoryName));
                }
                insertList.add(safetySysUserDO);
            } catch (Exception e) {
                LogUtils.error("数据第"+rowNum+"导入行读取异常",e);
            }
        }
        if (flag) {
            throw new BizException(ResponseEnum.FAIL.getCode(), expenseMessage);
        }
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("用户管理");
        log.setAbility("导入");
        log.setMessage(safetySysUserDO.getEmpName());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);

        if(CollectionUtils.isNotEmpty(insertList)){
            for (SafetySysUserDO sysUserDO : insertList) {
                if(roleMap.containsKey(sysUserDO.getUserName())) {
                    safetySysRoleDO = roleMap.get(sysUserDO.getUserName());
                    //插入人员数据
                    sysUserDO.setUserName("");
                    safetySysUserDOMapper.insert(sysUserDO);
                    //插入人员对应角色数据
                    safetySysUserRoleDO = new SafetySysUserRoleDO();
                    safetySysUserRoleDO.setRoleId(safetySysRoleDO.getId());
                    safetySysUserRoleDO.setUserId(sysUserDO.getId());
                    //插入数据
                    safetySysUserRoleDOMapper.insert(safetySysUserRoleDO);
                    rowCount++;
                }
            }
        }
        result = "导入成功:"+rowCount+"条!";
        return result;
    }

    /**
     * 获取厂区id
     * @param factoryMap
     * @param factoryName
     * @return
     */
    public String getFactoryIds(Map<String, SafetyFieldManageDO> factoryMap, String factoryName) {
        Set<String> names = Sets.newHashSet(factoryName.split(","));
        String factoryIds = "";
        for (String s : names) {
            if (factoryMap.containsKey(s)) {
                factoryIds += factoryMap.get(s).getId() + ",";
            }
        }
        if (StringUtils.isNotEmpty(factoryIds)) {
            factoryIds = factoryIds.substring(0, factoryIds.length()-1);
        }
        return factoryIds;
    }

    /**
     * 数据导出操作
     * @param param
     * @return
     */
    public List<SysUserVO> export(UserMessageListParam param, String tocken, Map<Integer, String> factoryMap) {
        Map<String,Object> pm = new HashMap<>();
        pm.put("sh", param);
        List<SysUserVO> list = safetySysUserDOBizMapper.exportList(pm);
        for (SysUserVO sysUserVO : list) {
            sysUserVO.setFactoryName(StringUtils.isNotEmpty(sysUserVO.getFactoryId()) ? getFactoryNames(sysUserVO.getFactoryId(), factoryMap) : "");
            sysUserVO.setPowerFactoryName(StringUtils.isNotEmpty(sysUserVO.getPowerFactoryId()) ? getFactoryNames(sysUserVO.getPowerFactoryId(), factoryMap) : "");
        }
        //根据tocken当前用户人员
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("用户管理");
        log.setAbility("导出");
        log.setMessage("所有");
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        }
        return null;
    }

    /**
     * 根据id获取对象
     * @param id
     * @return
     */
    public SafetySysUserDO safetySysUserDOById(Integer id) {
        return safetySysUserDOMapper.selectByPrimaryKey(id);
    }


    /**
     * 根据员工号获取工厂权限
     * @param empNo
     * @return
     */
    public List<Integer> getFactoryPower(String empNo){
        List<Integer> factoryIdList = new ArrayList<>();
        if(StringUtils.isEmpty(empNo)){
            return factoryIdList;
        }
        SafetySysUserDOExample example = new SafetySysUserDOExample();
        SafetySysUserDOExample.Criteria criteria = example.createCriteria();
        criteria.andEmpNoEqualTo(empNo);
        criteria.andDeleteFalgEqualTo(false);
        List<SafetySysUserDO> userList = safetySysUserDOMapper.selectByExample(example);
        if(userList ==null || userList.size() <= 0){
            return factoryIdList;
        }
        for(SafetySysUserDO userDO:userList){
            String factoryId = userDO.getFactoryId();
            if(StringUtils.isEmpty(factoryId)){
                continue;
            }
            String[] idArr = factoryId.split(",");
            List<Integer> idList = Arrays.stream(idArr).map(Integer::valueOf).collect(Collectors.toList());
            factoryIdList.addAll(idList);
        }
        return factoryIdList;
    }

    /**
     * 获取指定角色、工厂下用户列表
     * @param param
     * @return
     */
    public List<SafetySysUserDO> getList(UserMessageListParam param) {
        String factoryCode = param.getFactoryCode();
        //增加入参，支持根据工厂编码获取对应人员
        if(StringUtils.isNotEmpty(factoryCode)){
            FieldManageListParam param1 = new FieldManageListParam();
            param1.setFieldType("factory");
            param1.setFieldCode(factoryCode);
           List<SafetyFieldManageDO> fatoryList = safetyFieldManageService.getChildList(param1);
            if (!CollectionUtils.isEmpty(fatoryList)) {
                param.setPowerFactoryId(fatoryList.get(0).getId().toString());
            }
        }
        return safetySysUserDOBizMapper.getUserList(param);
    }

    /**
     * 获取登录用户的权限厂区
     * @param workNo 员工号
     * @return
     */
    public String getPowerFactory(String workNo){
        if(StringUtils.isEmpty(workNo)){
            return "0";
        }
        SafetySysUserDO userMessage = this.getUserMessage(workNo);
        if(userMessage == null){
            return "0";
        }
        String factoryId = userMessage.getPowerFactoryId();
        if(org.apache.commons.lang3.StringUtils.isBlank(factoryId)){
            factoryId = "0";
        }
        return factoryId;
    }

    /**
     * 校验是否制定角色
     * @param token
     * @param roleName
     * @return
     */
    public boolean checkRole(String token,String roleName){
        String userName = TokenUtils.getUserName(token);
        UserMessageListParam param = new UserMessageListParam();
        param.setUserName(userName);
        if(StringUtils.isNotEmpty(roleName)){
            param.setRoleNameArr(roleName);
        }else{
            param.setRoleName("超级管理员");
        }
        List<SafetySysUserDO> list = this.getList(param);
        return !org.springframework.util.CollectionUtils.isEmpty(list);
    }


    /**
     * 校验是否制定角色
     * @param userName
     * @param roleName
     * @return
     */
    public boolean checkRoleByUserName(String userName,String roleName){
        UserMessageListParam param = new UserMessageListParam();
        param.setUserName(userName);
        if(StringUtils.isNotEmpty(roleName)){
            param.setRoleNameArr(roleName);
        }else{
            param.setRoleName("超级管理员");
        }
        List<SafetySysUserDO> list = this.getList(param);
        return !org.springframework.util.CollectionUtils.isEmpty(list);
    }

    /**
     * 根据userCode查询人员
     * @param userCodeList
     * @return
     */
    public List<Integer> getUserIdByCodeList(List<String> userCodeList){
        if(CollectionUtils.isEmpty(userCodeList)){return Collections.emptyList();}
        return safetySysUserDOBizMapper.getUserIdByCodeList(userCodeList);
    }

    public List<Integer> getUserRoleList(Integer roleId){
        return safetySysUserDOBizMapper.getUserRoleList(roleId);
    }

    public void batchInserUserRole(List<SafetySysUserRoleDO> paramList){
        safetySysUserDOBizMapper.batchInserUserRole(paramList);
    }

    public List<SafetySysUserDO> getUserListByRoleId(Integer roleId){
        return safetySysUserDOBizMapper.getUserListByRoleId(roleId);
    }

    /**
     * 转换员工号
     * @param oldWorkNo 旧员工号
     * @return 返回值
     */
    public String employeeConvertWorkNo(String oldWorkNo){
        String newWorkNo = oldWorkNo;
        if(StringUtils.isEmpty(employeeWorkNoConvertUrl)){
            LogUtils.info("转换员工号接口“iam.employee.workNoConvert.url”为空");
            throw new BizException(ResponseEnum.EXCEPTION.getCode(),"转换员工号接口“iam.employee.workNoConvert.url”为空");
        }
        //请求接口查询员工信息
        String url = String.format(employeeWorkNoConvertUrl, "ABYJS", oldWorkNo);
        String result = webserviceUtil.sendHttpForGet(url);
        if(StringUtils.isNotEmpty(result)) {
            JSONObject userJson = (JSONObject) JsonUtils.parse(result);
            if(result.contains("success") && userJson.getBoolean("success")) {
                newWorkNo = userJson.getString("result");
            }
        }
        return newWorkNo;
    }

    public List<SafetySysUserDO> getUserListByParam(Map<String,Object> paramMap){
        return safetySysUserDOBizMapper.getUserListByParam(paramMap);
    }

    /**
     * 根据指定的员工号批量新增安保 用户
     * @param empNos 员工号集合
     * @return
     */
    public int batchInsertUserForEmpnos(List<String> empNos){
        if(CollectionUtils.isEmpty(empNos)){
            return 0;
        }
        return safetySysUserDOBizMapper.batchInsertUserForEmpnos(empNos);
    }

}
