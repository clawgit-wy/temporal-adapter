package com.yutong.modules.vehicle.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.vehicle.dto.AccessRecordDTO;
import com.yutong.modules.vehicle.dto.YtsafetyQueryPermInfoDTO;
import com.yutong.modules.vehicle.entity.YtsafetyQueryPermInfo;
import com.yutong.modules.vehicle.vo.CarAccessVO;
import com.yutong.modules.vehicle.vo.PersonAndCarVO;
import com.yutong.modules.vehicle.vo.PersonnelAccessVO;
import com.yutong.modules.vehicle.vo.YtsafetyQueryPermGroupVO;

import java.util.List;

public interface YtsafetyQueryInfoService extends IService<YtsafetyQueryPermInfo> {

    /**
     * 保存/更新角色查询权限配置
     * @param dto 权限配置（含角色编码及权限项列表）
     */
    void saveOrUpdatePerm(YtsafetyQueryPermInfoDTO dto);

    /**
     * 按角色分组查询权限配置
     * @param roleCode 角色编码（为空则查全部）
     * @return 按角色分组的权限配置列表
     */
    List<YtsafetyQueryPermGroupVO> listGroupByRole(String roleCode);

    /**
     * 支持输入员工号、手机号、车牌号、身份证号自动匹配查询；也可拍摄车牌自动识别查询
     * @param KeyWord
     * @return
     */
    PersonAndCarVO getPersonAndCarList(String KeyWord);

    /**
     * 查询员工进出记录
     * @param
     * @return
     */
    List<PersonnelAccessVO> gtePersonnelInOutHistory(AccessRecordDTO accessRecordDTO);


    /**
     * 查询员车辆进出记录
     * @param
     * @return
     */
    List<CarAccessVO> gteCarAccessList(AccessRecordDTO accessRecordDTO);

}
