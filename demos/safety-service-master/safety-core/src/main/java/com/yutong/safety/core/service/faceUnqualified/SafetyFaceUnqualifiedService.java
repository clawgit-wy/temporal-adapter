package com.yutong.safety.core.service.faceUnqualified;

import com.yutong.safety.common.param.app.EipWriteBackParam;
import com.yutong.safety.core.db.mapper.faceUnqualified.SafetyFaceUnqualifiedMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SafetyFaceUnqualifiedService {
    @Resource
    private SafetyFaceUnqualifiedMapper safetyFaceUnqualifiedMapper;

    public Long selectListByEmpNo(String empNo) {
        return safetyFaceUnqualifiedMapper.selectListByEmpNo(empNo);
    }

    public void updateById(EipWriteBackParam vo) {
        safetyFaceUnqualifiedMapper.updateById(vo);
    }

    /**
     * 根据ID更新识别照
     * @param id ID
     * @param newPhoto 识别照
     * @param remark 备注
     */
    public void updateById(Long id, String newPhoto, String remark) {
        safetyFaceUnqualifiedMapper.updatePhotoById(id, newPhoto, remark);
    }
}
