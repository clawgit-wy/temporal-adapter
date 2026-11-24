package com.yutong.safety.core.service.login;

import com.yutong.gw.utils.StringUtils;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.login.UserRoleMenuVO;
import com.yutong.safety.core.db.entity.SafetySysRoleDO;
import com.yutong.safety.core.db.entity.login.UserRoleMenu;
import com.yutong.safety.core.db.mapper.login.LoginMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * create gwcuiyx 2023.08.07
 * 登录用户--service
 */
@Service
public class LoginService {
    @Autowired
    private LoginMapper loginMapper;

    /**
     * 查询用户拥有的权限资源
     * @return
     */
    public List<UserRoleMenuVO> selectMainMenuList(String tocken) {
        //根据tocken获取当前用户
        String workNo = TokenUtils.getWorkNo(tocken);
        if(StringUtils.isEmpty(workNo)){
            throw new BizException(ResponseEnum.TOCKENFAIL.getCode(),"token已失效，请重新登录");
        }
        //获取当前用户的角色id
        List<Integer> roleIdList = loginMapper.getRoleIdList(workNo);
        if (CollectionUtils.isEmpty(roleIdList)) {
            roleIdList = Lists.newArrayList();
            roleIdList.add(loginMapper.getRoleId(workNo));
        }
        //先用默认的角色id
        Map map = new HashMap();
        map.put("roleId", roleIdList);
        List<UserRoleMenuVO> menus = this.loginMapper.selectUserRolesMenus(map);
        return menus;
    }

    /**
     * 递归，设置子目录
     * @param sysUserMenu
     * @param menus
     */
    public void getChildMenus(UserRoleMenu sysUserMenu, List<UserRoleMenuVO> menus){
        int pId = sysUserMenu.getId();
        List<UserRoleMenu> childList = Lists.newArrayList();
        for(int j=0,len=menus.size();j<len;j++){
            int sId = menus.get(j).getPid();
            if(pId==sId){
                childList.add(childMenu(menus.get(j)));
                menus.remove(j);
                len--;
                j--;
            }
        }
        if(childList.size() > 0){
            sysUserMenu.setChildren(childList);
            for (UserRoleMenu userRoleMenu : childList) {
                if (userRoleMenu.getCate() == 1 || userRoleMenu.getCate() == 2) {
                    getChildMenus(userRoleMenu, menus);
                }
            }
        }
    }

    public UserRoleMenu childMenu(UserRoleMenuVO menuVo){
        UserRoleMenu child = new UserRoleMenu();
        child.setId(menuVo.getId());
        child.setTitle(menuVo.getTitle());
        child.setUrl(menuVo.getUrl());
        child.setCate(menuVo.getCate());
        child.setCode(menuVo.getCode());
        child.setPid(menuVo.getPid());
        return child;
    }

    public List<SafetySysRoleDO> getRoleList(String empNo){
        return loginMapper.getRoleList(empNo);
    }
}
