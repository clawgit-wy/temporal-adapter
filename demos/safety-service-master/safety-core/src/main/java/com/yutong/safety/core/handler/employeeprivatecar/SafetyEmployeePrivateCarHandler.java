package com.yutong.safety.core.handler.employeeprivatecar;

import com.yutong.safety.core.db.entity.employeeprivatecar.SafetyEmployeePrivateCarDO;
import com.yutong.safety.core.db.entity.employeeprivatecar.param.EmployeeCarAddParam;
import com.yutong.safety.core.db.entity.employeeprivatecar.param.EmployeeCarDeleteParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author caomg
 * @date 2024-10-29 13:24
 * @description SafetyEmployeePrivateCarHandler
 */
public interface SafetyEmployeePrivateCarHandler {

    /**
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    Object pageList(SafetyEmployeePrivateCarDO param);

    List<SafetyEmployeePrivateCarDO> selectList(SafetyEmployeePrivateCarDO param);

    /**
     * 查询单个
     * @param param 查询入参
     * @return 返回
     */
    SafetyEmployeePrivateCarDO getOne(SafetyEmployeePrivateCarDO param);

    /**
     * 导入
     * @param file
     * @param token
     * @return
     */
    String importData(MultipartFile file, String token);

    /**
     * 更新私家车信息
     * @param param 入参
     * @return 更新条数
     */
    void modifyPrivateCar(EmployeeCarAddParam param);

    void deleteCar(EmployeeCarDeleteParam param);

    void deleteCarForCarNo(List<String> carNoList);
}
