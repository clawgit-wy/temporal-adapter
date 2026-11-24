package com.yutong.hksync.mapper.safety;

import com.yutong.hksync.vo.HkMiddleTableVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SafetyTbMapper {

    List<HkMiddleTableVO> selectBlackMessageList(@Param("start") Date start, @Param("end") Date end);

    List<HkMiddleTableVO> selectEmployeeMessageList(@Param("start") Date start, @Param("end") Date end);

    List<HkMiddleTableVO> selectEnterRegisterList(@Param("start") Date start, @Param("end") Date end);

    List<HkMiddleTableVO> selectVisitorHairpinList(@Param("start") Date start, @Param("end") Date end);
}
