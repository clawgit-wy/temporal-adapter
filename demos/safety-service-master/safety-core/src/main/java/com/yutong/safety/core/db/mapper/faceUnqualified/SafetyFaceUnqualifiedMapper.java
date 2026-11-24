package com.yutong.safety.core.db.mapper.faceUnqualified;

import com.yutong.safety.common.param.app.EipWriteBackParam;
import org.apache.ibatis.annotations.Param;

public interface SafetyFaceUnqualifiedMapper {

    Long selectListByEmpNo(@Param("empNo") String empNo);

    void updateById(EipWriteBackParam vo);

    void updatePhotoById(@Param("id") Long id, @Param("recognizePhoto") String recognizePhoto, @Param("remark") String remark);
}
