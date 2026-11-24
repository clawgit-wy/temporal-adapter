package com.yutong.modules.user.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.user.dto.SafetySysPermDTO;
import com.yutong.modules.user.entity.SafetySysPerm;
import com.yutong.modules.user.vo.MenuTree;
import com.yutong.modules.user.vo.PublicClass;
import com.yutong.modules.user.vo.UserPermissionVO;
import org.jeecg.common.exception.JeecgBootException;

import java.util.List;

/**
 * @Description: 1
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
public interface SafetySysPermService extends IService<SafetySysPerm> {

    void deletePermission(String id) throws JeecgBootException;

    void saveOrUpdatePerm(SafetySysPermDTO permDto) throws JeecgBootException;

    List<PublicClass> getMenuClassInfo(String id) throws JeecgBootException;

    List<MenuTree> getMenuTreeList(SafetySysPermDTO permDto) throws JeecgBootException;

    boolean getPermUrlIsExist(String permUrl,String id) throws JeecgBootException;

    public UserPermissionVO queryUserPermission(String workNo);


}
