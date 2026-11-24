package com.yutong.modules.user.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.user.dto.YtsafetySysResponsibleDTO;
import com.yutong.modules.user.dto.YtsafetySysResponsibleImportDTO;
import com.yutong.modules.user.dto.YtsafetySysResponsibleQueryDTO;
import com.yutong.modules.user.entity.YtsafetySysResponsible;
import com.yutong.modules.user.vo.YtsafetySysResponsibleEditVO;
import com.yutong.modules.user.vo.YtsafetySysResponsibleVO;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @Description: 安全责任信息表
 * @Author: jeecg-boot
 * @Date:   2026-05-27
 * @Version: V1.0
 */
public interface IYtsafetySysResponsibleService extends IService<YtsafetySysResponsible> {

    /**
     * 安全责任信息库-分页查询
     * @param page
     * @param sysResponsibleQueryDTO
     * @return
     */
    public IPage<YtsafetySysResponsibleVO> queryPage(Page<YtsafetySysResponsibleVO> page, YtsafetySysResponsibleQueryDTO sysResponsibleQueryDTO);


    /**
     * 安全责任信息库-保存
     * @param sysResponsibleDTO
     * @return
     */
    public boolean saveSysResponsible(YtsafetySysResponsibleDTO sysResponsibleDTO);

    /**
     * 安全责任信息库-编辑
     * @param sysResponsibleDTO
     * @return
     */
    public boolean editSysResponsible(YtsafetySysResponsibleDTO sysResponsibleDTO);

    /**
     * 安全责任信息库-依据ID删除
     * @param id
     * @return
     */
    public void deleteSysResponsible(Long id);

    /**
     * 安全责任信息库-通过ID查询回显数据
     * @param id
     * @return
     */
    public YtsafetySysResponsibleEditVO querySysResponsibleById(Long id);

    /**
     * 依据员工号查询用户信息
     * @param empNo
     * @return
     */
    public YtsafetySysResponsibleEditVO querySysUserByEmpNo(String empNo);

    /**
     * 依据员工号查询用户信息
     * @param empNo
     * @return
     */
    public YtsafetySysResponsibleEditVO queryResponsibleByEmpNo(String empNo);

    /**
     * 安全责任信息库-导出
     * @param sysResponsibleQueryDTO
     * @return
     */
    public ModelAndView exportSysResponsible(HttpServletRequest request,YtsafetySysResponsibleQueryDTO sysResponsibleQueryDTO);

    /**
     * 安全责任信息库-导入
     * @return
     */
    public List<YtsafetySysResponsibleImportDTO> importSysResponsible(List<YtsafetySysResponsibleImportDTO> safetySysUserDtoList) throws Exception;


    /**
     * 更新失效状态
     * @param currentDate
     */
    public void updateSysResponsibleStatus(Date currentDate);

    /**
     * 安全责任信息库-老数据同步到责任人映射表
     * @return 同步的责任人数量
     */
    public int syncHistoryResponsibleMapping();

    /**
     * 重建安全责任人映射数据
     * @param userId
     */
    public void rebuildResponsibleMapping(Long userId);
}
