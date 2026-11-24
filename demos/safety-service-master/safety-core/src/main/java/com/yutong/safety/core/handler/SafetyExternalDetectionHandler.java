package com.yutong.safety.core.handler;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.app.FindEmpParam;
import com.yutong.safety.common.param.web.ExternalDetectionAddParam;
import com.yutong.safety.common.param.web.ExternalDetectionListParam;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.web.LdapUserVO;
import com.yutong.safety.core.db.entity.SafetyExternalDetectionDO;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.SafetyExternalDetectionService;
import com.yutong.sugar.api.IRpcLdapService;
import com.yutong.sugar.api.vo.LdapResult;
import com.yutong.sugar.api.vo.LdapUser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author gwcuiyx
 * @date 2024-2-28 15:09
 */
@SuppressWarnings("ALL")
@Component
public class SafetyExternalDetectionHandler {

    @Autowired
    private SafetyExternalDetectionService safetyExternalDetectionService;

    @Autowired
    private IRpcLdapService rpcLdapService;

    @Autowired
    private SafetyEmployeeMessageService safetyEmployeeMessageService;

    /**
     * 分页列表查询
     * @param param
     * @param token
     * @return
     */
    public Object pageList(ExternalDetectionListParam param, String token) {
        List<SafetyExternalDetectionDO> list = safetyExternalDetectionService.pageList(param);
        long total = safetyExternalDetectionService.pageListCount(param);
        return new PageVO<SafetyExternalDetectionDO>(total, list);
    }

    /**
     * 数据添加接口
     * @param param
     * @return
     */
    public Object addOrUpdate(ExternalDetectionAddParam param, String token) {
        SafetyExternalDetectionDO safetyExternalDetectionDO = null;
        if (Objects.nonNull(param.getId())) {
            safetyExternalDetectionDO = safetyExternalDetectionService.get(param.getId());
        } else {
            safetyExternalDetectionDO = new SafetyExternalDetectionDO();
            safetyExternalDetectionDO.setCreateTime(new Date());
        }
        BeanUtils.copyProperties(param, safetyExternalDetectionDO);
        safetyExternalDetectionDO.setUpdateTime(new Date());
        safetyExternalDetectionDO.setDeleteFalg(false);
        if (Objects.nonNull(param.getId())) {
            safetyExternalDetectionService.update(safetyExternalDetectionDO, token);
        } else {
            safetyExternalDetectionService.add(safetyExternalDetectionDO, token);
        }
        return null;
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    public Object delete(Integer id) {
        safetyExternalDetectionService.delete(id);
        return null;
    }

    /**
     * 数据导出
     * @param param
     * @param token
     * @return
     */
    public HSSFWorkbook export(ExternalDetectionListParam param, String token) {
        List<SafetyExternalDetectionDO> list = safetyExternalDetectionService.export(param, token);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"导出数据不能超过5000条！");
        }
        String[] title = new String[]{"员工号", "员工姓名", "公司", "部门","备注"};
        String[][] values = new String[list.size()][];
        SafetyExternalDetectionDO safetyExternalDetectionDO;
        for(int i=0; i<list.size(); i++) {
            safetyExternalDetectionDO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = safetyExternalDetectionDO.getEmpNo();
            values[i][1] = safetyExternalDetectionDO.getEmpName();
            values[i][2] = safetyExternalDetectionDO.getCompanyName();
            values[i][3] = safetyExternalDetectionDO.getDeptName();
            values[i][4] = safetyExternalDetectionDO.getRemark();
        }
        String sheetName = "外协免检台账列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 查询员工
     * @param findEmpParam
     */
    public LdapUser findEmp(FindEmpParam findEmpParam) {
        return getUserInfo(findEmpParam.getName(), findEmpParam.getMobile()) ;
    }

    /**
     *      * 获取用户信息
     *      * @param name
     *      * @param mobile
     *      * @return
     */
    public LdapUser getUserInfo(String name, String mobile) {

        LdapResult<List<LdapUser>> listLdapResult = rpcLdapService.findUserinfo(mobile, "1", 0, 20) ;
        PreconditonsEx.checkArgument(listLdapResult.isSuccess() && CollectionUtils.isNotEmpty(listLdapResult.getData()), ResponseEnum.FAIL.getCode(), "请输入正确的被访人姓名或电话");
        boolean hasEip = false ;
        boolean hasSafety = false;
        List<LdapUser> ldapUsers = listLdapResult.getData();
        ldapUsers.sort((LdapUser a, LdapUser b)->b.getUid().compareTo(a.getUid()));
        for(LdapUser ldapUser : ldapUsers) {
            //判断是否在被访人白名单中
            if (!safetyExternalDetectionService.checkEmpNo(ldapUser.getUid())) {
                hasSafety = true;
                break;
            }
            if(StringUtils.isNotBlank(ldapUser.getEip())) {
                if(name.equals(ldapUser.getName())) {
                    return ldapUser ;
                }
                hasEip = true ;
            }
        }
        if (hasSafety) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "提交失败，被访人未开通访客业务") ;
        }
        if(!hasEip) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "提交失败，请到现场进行办理") ;
        } else {
            throw new BizException(ResponseEnum.FAIL.getCode(), "提交失败，请输入正确的被访人姓名或电话") ;
        }
    }

    /**
     * 查询员工
     * @param findEmpParam
     */
    public LdapUserVO findEmpNew(FindEmpParam findEmpParam) {
        return getUserInfoNew(findEmpParam.getName(), findEmpParam.getMobile()) ;
    }

    /**
     *      * 获取用户信息
     *      * @param name
     *      * @param mobile
     *      * @return
     */
    public LdapUserVO getUserInfoNew(String name, String mobile) {
        LdapUserVO returnVO = new LdapUserVO();
        LdapResult<List<LdapUser>> listLdapResult = rpcLdapService.findUserinfo(mobile, "1", 0, 20) ;
        PreconditonsEx.checkArgument(listLdapResult.isSuccess() && CollectionUtils.isNotEmpty(listLdapResult.getData()), ResponseEnum.FAIL.getCode(), "请输入正确的被访人姓名或电话");
        boolean hasEip = false ;
        boolean hasSafety = false;
        List<LdapUser> ldapUsers = listLdapResult.getData();
        ldapUsers.sort((LdapUser a, LdapUser b)->b.getUid().compareTo(a.getUid()));
        for(LdapUser ldapUser : ldapUsers) {
            //判断是否在白名单中
            if (!safetyExternalDetectionService.checkEmpNoAndName(ldapUser.getUid(), name)) {
                hasSafety = true;
                break;
            }
            //判断是否有EIP账号
            if(StringUtils.isNotBlank(ldapUser.getEip())) {
                if(name.equals(ldapUser.getName())) {
                    BeanUtils.copyProperties(ldapUser,returnVO);
                    returnVO.setCheckType(1);
                    return returnVO ;
                }
                hasEip = true ;
            }
        }

        if (hasSafety || !hasEip) {
            if (ldapUsers.get(0).getName().equals(name)) {
                BeanUtils.copyProperties(ldapUsers.get(0),returnVO);
                returnVO.setCheckType(0);
                return returnVO;
            }
        }
        throw new BizException(ResponseEnum.FAIL.getCode(), "请输入正确的被访人/对接人姓名或电话") ;
    }
}
