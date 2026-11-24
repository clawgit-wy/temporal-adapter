package com.yutong.modules.user.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yutong.modules.user.dto.EmployeeMessageListParam;
import com.yutong.modules.user.entity.SafetyEmployeeMessage;
import com.yutong.modules.user.vo.JobOptionListVO;

import java.util.List;

/**
 * @Description: EHR员工信息
 * @Author: jeecg-boot
 * @Date:   2026-05-22
 * @Version: V1.0
 */
public interface ISafetyEmployeeMessageService extends IService<SafetyEmployeeMessage> {
    /**
     * 获取岗位下拉框
     * @return
     */
    List<JobOptionListVO> jobOptionList();

    /**
     * 根据员工号获取 EIP 登录名（邮箱 @ 前缀部分）
     * @param empNo 员工号
     * @return 登录名，不存在返回空字符串
     */
    String getLoginNameByNo(String empNo);

    /**
     * 根据员工号获取在职员工信息
     * @param empNo 员工号
     * @return 员工信息，不存在返回 null
     */
    SafetyEmployeeMessage getByNo(String empNo);

    /**
     * 分页查询员工列表
     * @param param 查询参数
     * @return 分页结果
     */
    List<SafetyEmployeeMessage> pageList(EmployeeMessageListParam param);

    /**
     * 根据员工号和手机号获取员工信息
     * @param workNo 员工号
     * @param phone 手机号
     * @return 员工信息，不存在返回 null
     */
    SafetyEmployeeMessage getEmployeeByWorkNo(String workNo, String phone);

    /**
     * 修改人员台账的用户照片， 根据人脸不合格记录变更
     * @param ids 人脸不合格记录的数据行ID
     */
    int updateByEmpNoOfUnqualifiedRecord(List<String> ids);

    /**
     * 员工或者有EIP的外协员工，员工生活照或者抓拍照长时间未更新且30天内无尽处记录，登记至不合格人员记录中
     * @return
     */
    List<SafetyEmployeeMessage> selectListForUnqualified(String dateStr);

    /**
     * 获取人员台账中，人员信息在人脸不合格记录中，不包含90天的员工并且存在照片的在职的数据信息
     */
    List<Integer> selectListByLowQuality(String dateStr);

    List<SafetyEmployeeMessage> selectListByLowQualityOfId(String dateStr, List<Integer> dataIdList);
}
