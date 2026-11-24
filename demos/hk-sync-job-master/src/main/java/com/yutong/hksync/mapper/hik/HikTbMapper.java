package com.yutong.hksync.mapper.hik;

import com.yutong.hksync.vo.HkMiddleTableVO;
import org.apache.ibatis.annotations.Param;


import java.util.List;


public interface HikTbMapper {
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
