package com.yutong.safety.core.handler;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.param.web.sys.SysPermParam;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.SafetySysPermDO;
import com.yutong.safety.core.handler.spot.SafetySpotCheckTaskHandler;
import com.yutong.safety.core.service.SafetySysPermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 *  create gwcuiyx 2023.07.26
 *  权限资源表--handler
 */
@Component
public class SafetySysPermHandler {
    @Autowired
    private SafetySysPermService safetySysPermService;

    @Resource
    private SafetySpotCheckTaskHandler safetySpotCheckTaskHandler;

    /**
     * 查询权限资源
     * @return
     */
    public Object selectPermList() {
        return safetySysPermService.selectPermList();
    }

    /**
     * 权限资源添加
     * @param param
     * @return
     */
    public Object insertSysPerm(SysPermParam param) {
        safetySysPermService.insertSysPerm(param);
        return null;
    }

    /**
     * 权限资源更新操作
     * @param param
     * @return
     */
    public Object updateSysPerm(SysPermParam param) {
        safetySysPermService.updateSysPerm(param);
        return null;
    }

    /**
     * 权限资源删除操作
     * @param id
     * @return
     */
    public Object deleteSysPerm(Integer id) {
        safetySysPermService.deleteSysPerm(id);
        return null;
    }

    /**
     * 根据员工号获取对应的角色权先列表
     * @param empNo 员工号
     * @return 返回员工号对应的角色列表
     */
    public VO getPermListByEmpNo(String empNo) {
        if (StringUtils.isEmpty(empNo)) {
            return VoUtil.fail("员工号不能为空");
        }

        String newWorkNo = empNo;
        if(empNo.startsWith("01")){
            newWorkNo = safetySpotCheckTaskHandler.employeeConvertWorkNo(empNo);
        }

        List<SafetySysPermDO> list = safetySysPermService.selectListByEmpNoRole(newWorkNo);
        LogUtils.info("getPermListByEmpNo：----{}----{}",list.size(), newWorkNo);
        return VoUtil.successData(list);
    }
}
