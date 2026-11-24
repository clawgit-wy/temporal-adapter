package com.yutong.safety.core.handler;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.sys.SysRoleListParam;
import com.yutong.safety.common.param.web.sys.SysRoleParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetySysRoleDO;
import com.yutong.safety.core.service.SafetySysRoleService;
import com.yutong.safety.core.service.tree.ZTreeService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * create gwcuiyx 2023.07.26
 * 角色表--handler
 */
@Component
public class SafetySysRoleHandler {
    @Autowired
    private SafetySysRoleService safetySysRoleService;
    @Autowired
    private ZTreeService zTreeService;

    /**
     * 分页数据列表
     * @param param
     * @return
     */
    public Object selectRoleList(SysRoleListParam param) {
        List<SafetySysRoleDO> list = safetySysRoleService.pageList(param);
        long total = safetySysRoleService.pageListCount(param);
        return new PageVO<SafetySysRoleDO>(total, list);
    }

    /**
     * 数据添加操作
     * @param param
     * @return
     */
    public Object insertSysRole(SysRoleParam param, String tocken) {
        safetySysRoleService.insertSysRole(param, tocken);
        return null;
    }

    /**
     * 数据更新操作
     * @param param
     * @return
     */
    public Object updateSysRole(SysRoleParam param, String tocken) {
        safetySysRoleService.updateSysRole(param, tocken);
        return null;
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    public Object deleteSysRoleById(Integer id, String tocken) {
        safetySysRoleService.deleteSysRole(id, tocken);
        return null;
    }

    /**
     * 数据获取操作
     * @param id
     * @return
     */
    public Object getRoleHasPerms(Integer id) {
        return  safetySysRoleService.getRoleHasPerms(id);
    }

    /**
     * 数据设计操作
     * @param roleId
     * @param permIds
     * @return
     */
    public Object setRolePerms(Integer roleId, String permIds) {
        return safetySysRoleService.setRolePerms(roleId, permIds);
    }

    /**
     * 数据列表操作
     * @return
     */
    public Object selectSysRoles() {
        return safetySysRoleService.selectSysRoles();
    }

    /**
     * 数据导出操作
     * @param param
     * @return
     */
    public HSSFWorkbook export(SysRoleListParam param, String tocken) {
        List<SafetySysRoleDO> list = safetySysRoleService.export(param, tocken);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"角色名称", "修改人", "修改时间"};
        String[][] values = new String[list.size()][];
        SafetySysRoleDO sysRoleDO;
        for(int i=0; i<list.size(); i++) {
            sysRoleDO = list.get(i);
            values[i] = new String[title.length];
            values[i][0] = sysRoleDO.getRoleName();
            values[i][1] = sysRoleDO.getLastUser();
            values[i][2] = DateUtils.format(sysRoleDO.getLastTime(), 2);
        }
        String sheetName = "角色管理列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

}
