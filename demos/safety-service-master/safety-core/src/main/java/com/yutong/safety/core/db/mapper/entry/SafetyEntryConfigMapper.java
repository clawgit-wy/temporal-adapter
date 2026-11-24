package com.yutong.safety.core.db.mapper.entry;

import com.yutong.safety.core.db.entity.enter.result.SafetyEnterApproveRt;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfig;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfigExample;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyEntryConfigMapper extends SafetyWebBaseMapper<SafetyEntryConfig, SafetyEntryConfigExample> {

    List<SafetyEnterApproveRt> getEntryConfigByPhone(@Param("phone")String phone);

    void batchSave(@Param("list") List<SafetyEntryConfig> list);

    void batchUpdate(@Param("list") List<SafetyEntryConfig> list);

    void clearDisabledConfig(@Param("maxEndTime")String maxEndTime);

    void batchUpdateById(@Param("list") List<String> ids, @Param("downMark") String downMark, @Param("qrCode") String qrCode);

    long countHistoryByExample(SafetyEntryConfigExample example);

    List<SafetyEntryConfig> selectHistoryByExample(SafetyEntryConfigExample example);

    /**
     * 判断员工是否存在入场方式配置信息
     * @param workNo 员工号
     * @return 数量
     */
    long selectCountOfEntryMethod(@Param("workNo") String workNo);

    /**
     * 根据员工号获取入场方式配置信息
     * @param workNo 员工号
     * @return 返回入场配置信息 0：刷脸+二维码，1：刷脸，2：二维码
     */
    Integer selectEntryMethodByWorkNo(@Param("workNo")String workNo);

    /**
     * 新增入场方式配置
     * @param workNo 员工号
     * @param entryMethod 入厂方式配置 0：刷脸+二维码，1：刷脸，2：二维码
     */
    void saveEntryMethodInfo(@Param("workNo")String workNo, @Param("entryMethod") Integer entryMethod);

    /**
     * 修改入场方式配置
     * @param workNo 员工号
     * @param entryMethod 入厂方式配置 0：刷脸+二维码，1：刷脸，2：二维码
     */
    void updateEntryMethodInfo(@Param("workNo")String workNo, @Param("entryMethod") Integer entryMethod);
}