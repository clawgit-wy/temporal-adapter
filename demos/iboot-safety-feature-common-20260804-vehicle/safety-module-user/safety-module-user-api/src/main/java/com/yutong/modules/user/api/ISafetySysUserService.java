package com.yutong.modules.user.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.user.dto.SafetySysUserDTO;
import com.yutong.modules.user.dto.SafetySysUserImpoetDTO;
import com.yutong.modules.user.query.SafetySysUserQuery;
import com.yutong.modules.user.entity.SafetySysUser;
import com.yutong.modules.user.vo.MenuTree;
import com.yutong.modules.user.vo.SafetySysUserVO;
import org.jeecg.common.system.vo.LoginUser;

import java.util.List;

/**
 * @Description: 系统用户表
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
public interface ISafetySysUserService extends IService<SafetySysUser> {

    void addUser(SafetySysUserDTO safetySysUserDto);

    IPage<SafetySysUserVO> queryPageList(Page<SafetySysUserVO> page, SafetySysUserQuery safetySysUserQueryDto);

    void updateSysUserById(SafetySysUserDTO dto);

    void deleteById(String id);

    SafetySysUserVO queryById(Long id);

    List<SafetySysUserVO> queryList(SafetySysUserQuery safetySysUserQueryDto);

    List<SafetySysUserImpoetDTO> importExcel(List<SafetySysUserImpoetDTO> safetySysUserDtoList);


    List<MenuTree> getTree(Long id);

    void sync() ;

    LoginUser queryUser(String empNo);

    List<LoginUser> queryUserListByEmpNos(List<String> empNos);
}
