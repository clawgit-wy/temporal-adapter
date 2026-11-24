package com.yutong.safety.core.db.mapper.enter;

import com.yutong.safety.core.db.entity.enter.SafetyEnterProtect;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterProtectRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterProtectSh;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SafetyEnterProtectMapper {

    SafetyEnterProtectRt selectEnterProtectById(@Param("id") Integer id);
    List<SafetyEnterProtectRt> selectEnterProtectList(SafetyEnterProtectSh enterProtectSh);
    Integer selectEnterProtectCount(SafetyEnterProtectSh enterProtectSh);
    SafetyEnterProtectRt selectEnterProtectByCarNo(@Param("fdCarNo") String fdCarNo);
    Integer insertEnterProtect(SafetyEnterProtect enterProtect);
    Integer updateEnterProtect(SafetyEnterProtect enterProtect);
    Integer deleteEnterProtect(@Param("id") Integer id);
}
