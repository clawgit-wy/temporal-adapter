package com.yutong.safety.core.db.hikmapper.hikmiddle;

import com.yutong.safety.common.vo.web.HkMiddleTableVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyHikMiddleDOMapper {

    /**
     * 人员信息
     */
    int batchPersonSave(@Param("list") List<HkMiddleTableVO> list);

    /**
     * 组织
     */
    int batchOrgSave(@Param("list") List<HkMiddleTableVO> list);

    /**
     * 人脸
     */
    int batchFaceSave(@Param("list") List<HkMiddleTableVO> list);
}