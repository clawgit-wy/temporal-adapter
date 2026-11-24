package com.yutong.safety.core.handler;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.UserMessageAddParam;
import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.param.web.UserMessageUpdateParam;
import com.yutong.safety.common.utils.*;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.sys.SysUserVO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.service.SafetyFieldManageService;
import com.yutong.safety.core.service.SafetyEmpPhotoInfoService;
import com.yutong.safety.core.service.SafetySysUserService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 *  create gwcuiyx 2023.07.28
 *  账号信息表--handler
 */
@Component
public class SafetySysUserHandler {
    @Autowired
    private SafetySysUserService safetySysUserService;
    @Autowired
    private SafetyFieldManageService safetyFieldManageService;
    @Autowired
    private SafetyEmpPhotoInfoService safetyEmpPhotoInfoService;

    /**
     * 用户数据列表
     * @param param
     * @return
     */
    public Object pageList(UserMessageListParam param) {
        //获取厂区名称
        Map<Integer, String> factoryMap = safetyFieldManageService.getFactoryNameList();
        long total = safetySysUserService.pageListCount(param);
        List<SysUserVO> list = safetySysUserService.pageList(param, factoryMap);
        return new PageVO<SysUserVO>(total, list);
    }

    /**
     * 数据新增操作
     * @param param
     * @return
     */
    public Object insertSysUser(UserMessageAddParam param, String tocken){
        safetySysUserService.insertSysUser(param, tocken);
        return null;
    }

    /**
     * 数据更新
     * @param param
     * @return
     */
    public Object updateSysUserById(UserMessageUpdateParam param, String tocken) {
        safetySysUserService.updateSysUserById(param, tocken);
        return null;
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    public Object deleteSysUserById(Integer id, String tocken) {
        safetySysUserService.deleteSysUserById(id, tocken);
        return null;
    }

    /**
     * 角色设置操作
     * @param userId
     * @param roleIds
     * @return
     */
    public Object setUserRoles(Integer userId, String roleIds) {
        safetySysUserService.setUserRoles(userId, roleIds);
        return null;
    }

    /**
     *  查询已设置的角色集合
     * @param id
     * @return
     */
    public Object getUserHasRoles(Integer id) {
        return safetySysUserService.getUserHasRoles(id);
    }

    /**
     * 数据导入
     * @param param
     * @return
     */
    public Object importSysUser(CommonImportParam param, String tocken) {
        return safetySysUserService.importSysUser(param, tocken);
    }

    /**
     * 数据导出操作
     * @param param
     * @return
     */
    public HSSFWorkbook export(UserMessageListParam param, String tocken) {
        //获取厂区名称
        Map<Integer, String> factoryMap = safetyFieldManageService.getFactoryNameList();
        List<SysUserVO> list = safetySysUserService.export(param, tocken, factoryMap);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"用户名", "姓名", "员工号", "角色","访客发卡厂区","授权厂区", "修改人","修改时间"};
        String[][] values = new String[list.size()][];
        SysUserVO sysUserVO;
        for(int i=0; i<list.size(); i++) {
            sysUserVO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = sysUserVO.getUserName();
            values[i][1] = sysUserVO.getEmpName();
            values[i][2] = sysUserVO.getEmpNo();
            values[i][3] = sysUserVO.getRoleName();
            values[i][4] = sysUserVO.getFactoryName();
            values[i][5] = sysUserVO.getPowerFactoryName();
            values[i][6] = sysUserVO.getModifyEmp();
            values[i][7] = DateUtils.format(sysUserVO.getUpdateTime(), 2);
        }
        String sheetName = "用户信息列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    public Object updatePhotoIncrementInfo(String dateTime) {
        safetyEmpPhotoInfoService.updatePhotoIncrementInfo(dateTime);
        return null;
    }

    public Object updatePhotoIncrementInfoNew(String dateTime) {
        safetyEmpPhotoInfoService.updatePhotoIncrementInfoNew(dateTime);
        return null;
    }

    public List<SafetySysUserDO> getList(UserMessageListParam param){
        return safetySysUserService.getList(param);
    }
}
