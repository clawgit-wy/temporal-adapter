package com.yutong.safety.core.handler;


import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.GroupTreeVO;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.SafetyGroupInfoDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDO;
import com.yutong.safety.core.db.entity.build.param.BuildPowerListParam;
import com.yutong.safety.core.dto.ResultDTO;
import com.yutong.safety.core.dto.sys.GroupListParam;
import com.yutong.safety.core.handler.build.SafetyBuildProcessHandler;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.SafetyGroupService;
import com.yutong.safety.core.service.build.SafetyBuildPowerService;
import org.apache.commons.lang3.StringUtils;
import com.yutong.safety.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gwxiexj
 * @since 2024-04-02 16:28
 */
@Component
public class SafetyGroupHandler {

    @Autowired
    private SafetyGroupService safetyGroupService;

    @Autowired
    SafetyEmployeeMessageService safetyEmployeeMessageService;

    @Autowired
    SafetyBuildPowerService safetyBuildPowerService;

    @Autowired
    SafetyBuildProcessHandler safetyBuildProcessHandler;

    /**
     *
     * @param key 上级组织编码
     * @return
     */
    public Object selectGroupTree(String key){
        List<GroupTreeVO> treeList = safetyGroupService.selectGroupTree(key);
        long total = treeList.size();
        return new PageVO<GroupTreeVO>(total, treeList);
    }

    public Object selectGroupTreeWithPeople(String key){
        List<GroupTreeVO> treeList = safetyGroupService.selectGroupTreeWithPeople(key);
        long total = treeList.size();
        return new PageVO<GroupTreeVO>(total, treeList);
    }

    public Object selectGroupTreeWithPeopleBelong(String key,String token){
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        if(com.yutong.gw.utils.StringUtils.isEmpty(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }
        SafetyEmployeeMessageDO byNo = safetyEmployeeMessageService.getByNo(workNo);
        if(byNo == null || StringUtils.isBlank(byNo.getOrgCodePath())){
            return null;
        }

        boolean isFirst = false;
        if(StringUtils.isBlank(key)){
            key = byNo.getOrgCode();
            isFirst = true;
        }

        List<GroupTreeVO> treeList = safetyGroupService.selectGroupTreeWithPeopleBelong(key,isFirst);
        long total = treeList.size();
        return new PageVO<GroupTreeVO>(total, treeList);
    }

    /**
     *
     * @param isAll
     * @param dateTime
     * @return
     */
    public Object taskGroup(Boolean isAll,String dateTime){
        safetyGroupService.taskGroup(isAll,dateTime);

        GroupListParam listParam = new GroupListParam();
        listParam.setStatus("Y"); //可用组织
        Date beginDate = DateUtils.addHours(new Date(),-1);
        listParam.setUpdateTimeBegin(DateUtils.format(beginDate,2));
        List<SafetyGroupInfoDO> groupInfoDOS = safetyGroupService.selectGroupList(listParam);
        if(CollectionUtils.isEmpty(groupInfoDOS)){
            return null;
        }
        List<String> orgCodeList = groupInfoDOS.stream().map(SafetyGroupInfoDO::getCode)
                .distinct().collect(Collectors.toList());
        //根据最新的组织变更信息更新对应的员工组织
        safetyEmployeeMessageService.updateEmpPathByOrg(orgCodeList);
        return null;
    }

    /**
     * @return
     */
    public Object selectGroupTreeNonAsyn(){
        List<GroupTreeVO> treeList = safetyGroupService.selectGroupTreeNonAsyn();
        long total = treeList.size();
        return new PageVO<GroupTreeVO>(total, treeList);
    }

    public Object selectGroupTreeContain(String token){
        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        if(com.yutong.gw.utils.StringUtils.isEmpty(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }
        SafetyEmployeeMessageDO byNo = safetyEmployeeMessageService.getByNo(workNo);
        if(byNo == null || StringUtils.isBlank(byNo.getOrgCodePath())){
            return null;
        }

        String orgCodePath = byNo.getOrgCodePath();

        List<GroupTreeVO> treeList = safetyGroupService.selectGroupTreeContain(orgCodePath);
        long total = treeList.size();
        return new PageVO<GroupTreeVO>(total, treeList);
    }

    public Object getDepartName(String keyWords){
        if (StringUtils.isBlank(keyWords)){return null;}
        return safetyGroupService.getDepartName(keyWords,"");
    }

    public Object getDepartNameBelongCurPer(String keyWords,String token){
        if (StringUtils.isBlank(keyWords)){return null;}

        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        if(com.yutong.gw.utils.StringUtils.isEmpty(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }
        SafetyEmployeeMessageDO byNo = safetyEmployeeMessageService.getByNo(workNo);
        if(byNo == null || StringUtils.isBlank(byNo.getOrgCodePath())){
            return null;
        }

        return safetyGroupService.getDepartName(keyWords,byNo.getOrgCode());
    }

    /**
     * 判断当前所选部门是否在当前登录人的责任部门下
     * @param orgPath
     * @param token
     * @return
     */
    public Object validDept(String orgPath, String token){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setAccessEight(false);
        if (StringUtils.isBlank(orgPath)){return resultDTO;}

        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        if(com.yutong.gw.utils.StringUtils.isEmpty(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }

        //根据当前用户类型 设置检索条件 2-管理员查看所有 3-保密负责人 能看管辖部门
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if("2".equals(userType)){
            resultDTO.setAccessEight(true);
            return resultDTO;
        }

        //根据员工号查询当前登录人有哪些责任部门
        BuildPowerListParam listParam = new BuildPowerListParam();
        listParam.setEmpNo(workNo);
        List<SafetyBuildPowerDO> doList = safetyBuildPowerService.selectList(listParam);

        if (CollectionUtils.isEmpty(doList)) {return resultDTO;}
        //当前登录人所有的责任部门
        Set<String> orgPathSet = new HashSet<>();
        for (SafetyBuildPowerDO en:doList) {
            orgPathSet.add(en.getDutyDeptCode());
        }

        //判断当前选中的部门是否在其责任部门下
        for (String str:orgPathSet) {
            if(orgPath.contains(str)){
                resultDTO.setAccessEight(true);
                break;
            }
        }

        return resultDTO;
    }

    /**
     * 判断所选人员是否属于当前登录人的责任部门下
     * @param userList
     * @param token
     * @return
     */
    public Object validPeople(List<SafetyEmployeeMessageDO> userList, String token){
        ResultDTO resultDTO = new ResultDTO();
        if (CollectionUtils.isEmpty(userList)){return resultDTO;}

        Map<String, String> tockenUser = TokenUtils.getTockenUser(token);
        String workNo = tockenUser.get("workNo");
        if(com.yutong.gw.utils.StringUtils.isEmpty(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录!");
        }
        //查询当前所有人的详情
        List<String> userCodeList = userList.stream().map(SafetyEmployeeMessageDO::getEmpNo).collect(Collectors.toList());
        userList = safetyEmployeeMessageService.selectListByUserCode(userCodeList);

        //根据当前用户类型 设置检索条件 2-管理员查看所有 3-保密负责人 能看管辖部门
        String userType = safetyBuildProcessHandler.checkUserType(token);
        if("2".equals(userType)){
            resultDTO.setBelongList(userList);
            resultDTO.setNotBelongList(null);
            return resultDTO;
        }

        //根据员工号查询当前登录人有哪些责任部门
        BuildPowerListParam listParam = new BuildPowerListParam();
        listParam.setEmpNo(workNo);
        List<SafetyBuildPowerDO> doList = safetyBuildPowerService.selectList(listParam);

        if (CollectionUtils.isEmpty(doList)) {return resultDTO;}
        //当前登录人所有的责任部门
        Set<String> orgPathSet = new HashSet<>();
        for (SafetyBuildPowerDO en:doList) {
            orgPathSet.add(en.getDutyDeptCode());
        }

        ArrayList<SafetyEmployeeMessageDO> belongList = new ArrayList<>();
        ArrayList<SafetyEmployeeMessageDO> notBelongList = new ArrayList<>();
        for (SafetyEmployeeMessageDO user:userList) {
            boolean isBelong = false;
            String orgCodePath = user.getOrgCodePath();
            if(StringUtils.isEmpty(orgCodePath)){
                notBelongList.add(user);
                continue;
            }

            //判断是否处于其责任部门下
            for(String str:orgPathSet){
                if(orgCodePath.contains(str)){
                    belongList.add(user);
                    isBelong = true;
                    break;
                }
            }
            if(!isBelong){notBelongList.add(user);}
        }

        resultDTO.setBelongList(belongList);
        resultDTO.setNotBelongList(notBelongList);
        return resultDTO;
    }
}
