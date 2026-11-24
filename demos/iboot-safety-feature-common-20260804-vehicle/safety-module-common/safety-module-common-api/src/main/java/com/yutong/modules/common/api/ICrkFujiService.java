package com.yutong.modules.common.api;

import com.yutong.modules.common.vo.CrkFaceModifyVO;
import com.yutong.modules.common.query.CrkSnapshotPhotoQuery;
import com.yutong.modules.common.vo.CrkSnapshotPhotoVO;
import com.yutong.modules.common.vo.CrkStaffInfoVO;

import java.util.List;

/**
 * 富士出入口接口业务
 */
public interface ICrkFujiService {

    /**
     * 添加人事ID
     * http://10.66.230.210:60009/api/StaffService/EditStaff
     */
    String editStaffOfCrk(CrkStaffInfoVO staffInfoVO);

    /**
     * 根据人事ID查询组织ID
     * http://10.66.230.210:60009/api/Staff/Get/
     */
    String getStaffInfoOfCrk(String crkId);


    /**
     * 获取富士接口的人脸抓拍照片
     * http://10.66.230.210:60009/api/DmsOpenPic/GetByCustom  测试
     */
    List<CrkSnapshotPhotoVO> getCrkSnapshotPhoto(CrkSnapshotPhotoQuery snapshotPhotoVO);

    /**
     * 人脸发行改写接口
     */
    String faceModifyOfCrk(CrkFaceModifyVO modifyVO);
}
