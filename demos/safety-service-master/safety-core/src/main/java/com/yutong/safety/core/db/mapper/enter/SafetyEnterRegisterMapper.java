package com.yutong.safety.core.db.mapper.enter;

import com.yutong.safety.core.db.entity.enter.EnterFactoryRecord;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingPer;
import com.yutong.safety.core.db.entity.enter.SafetyEnterRegister;
import com.yutong.safety.core.db.entity.enter.excel.SafetyEnterRegisterEx;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterRegisterRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterRegisterSh;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface SafetyEnterRegisterMapper {

    SafetyEnterRegisterRt selectEnterRegisterById(@Param("id") Integer id);
    SafetyEnterRegisterRt selectEnterRegisterByPhone(@Param("fdPhone") String fdPhone);
    SafetyEnterRegisterRt selectEnterRegisterByCard(@Param("fdCard") String fdCard);
    SafetyEnterRegisterRt selectEnterRegisterHas(@Param("fdPhone") String fdPhone,@Param("userName") String userName);
    Integer checkEnterRegister(SafetyEnterFilingPer per);
    Integer checkEnterRegisterOld(EnterFactoryRecord per);
    List<SafetyEnterRegisterRt> selectEnterRegisterList(SafetyEnterRegisterSh enterRegister);
    Integer selectEnterRegisterCount(SafetyEnterRegisterSh enterRegister);
    Integer insertEnterRegister(SafetyEnterRegister enterRegister);
    Integer insertEnterRegisterBatch(@Param("batch")List<SafetyEnterRegister> enterRegister);
    Integer updateEnterRegister(SafetyEnterRegister enterRegister);
    Integer deleteEnterRegister(@Param("id") Integer id);

    List<SafetyEnterRegisterEx> selectEnterRegisterExport(SafetyEnterRegisterSh enterRegister);

    List<SafetyEnterRegisterRt> selectEnterRegisterByPhoneList(@Param("list") List<String> phones);

    List<SafetyEnterRegisterRt> selectEnterRegisterByCardNos(@Param("list") List<String> cardNos);

    Integer batchUpdate(@Param("list") List<SafetyEnterRegisterRt> registerRts);

    List<SafetyEnterRegisterRt> selectListByUpdateTime(@Param("start") Date start, @Param("end") Date end);
}
