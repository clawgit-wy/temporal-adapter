package com.yutong.safety.core.db.mapper.enter;

import com.yutong.safety.core.db.entity.enter.SafetyEnterAuth;
import com.yutong.safety.core.db.entity.enter.SafetyEnterLinkAuthGroup;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterAuthRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterAuthSh;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SafetyEnterAuthMapper {

    SafetyEnterAuthRt selectEnterAuthById(@Param("id") Integer id);
    List<SafetyEnterAuthRt> selectEnterAuthByEmpNo(@Param("empNo") String empNo);
    List<SafetyEnterAuthRt> selectEnterAuthList(SafetyEnterAuthSh enterAuthSh);
    Integer selectEnterAuthCount(SafetyEnterAuthSh enterAuthSh);
    Integer insertEnterAuth(SafetyEnterAuth enterAuth);
    Integer updateEnterAuth(SafetyEnterAuth enterAuth);
    Integer deleteEnterAuth(@Param("id") Integer id);
    /**
     * 查询可备案厂区
     * */
    SafetyEnterAuthRt selectAuthFactory(@Param("empNo") String empNo);
    /**
     * 校验员工信息
     * */
    SafetyEnterAuthRt selectEmpInfoByEmpNo(SafetyEnterAuthSh enterAuthSh);

    Map selectFilingFactory(@Param("filingId") String filingId);

    List<SafetyEnterAuthRt> selectEnterAuthByOrgCode(@Param("orgCode") String orgCode);

    int deleteLinkAuthGroup(@Param("authId")Integer authId);

    int insertBatchAuthGroup(@Param("list") List<SafetyEnterLinkAuthGroup> authGroups);

    SafetyEnterAuthRt selectEnterAuthByEmpNameAndPhone(@Param("empName") String empName, @Param("phone") String phone);
}
