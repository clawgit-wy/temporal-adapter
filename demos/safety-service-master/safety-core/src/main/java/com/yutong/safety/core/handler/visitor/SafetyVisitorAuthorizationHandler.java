package com.yutong.safety.core.handler.visitor;

import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.SafetyEmployeeMessageDO;
import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationDO;
import com.yutong.safety.core.db.entity.visitor.SafetyVisitorAuthorizationDOExample;
import com.yutong.safety.core.db.entity.visitor.param.SafetyVisitorAuthorizationConfigAddParam;
import com.yutong.safety.core.db.entity.visitor.param.SafetyVisitorAuthorizationConfigQueryParam;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import com.yutong.safety.core.service.SafetyEmployeeMessageService;
import com.yutong.safety.core.service.visitor.SafetyVisitorAuthorizationService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.yutong.safety.common.constants.ResponseEnum.DB_DATA_NOT_EXIT;

@Component
public class SafetyVisitorAuthorizationHandler extends AbstractSafetyWebBaseHandler<SafetyVisitorAuthorizationDO, SafetyVisitorAuthorizationDOExample, SafetyVisitorAuthorizationConfigQueryParam, SafetyVisitorAuthorizationConfigAddParam> {

    @Resource
    private SafetyEmployeeMessageService safetyEmployeeMessageService;

    @Resource
    private SafetyVisitorAuthorizationService safetyVisitorAuthorizationService;

    @Override
    public AbstractSafetyWebBaseService<SafetyVisitorAuthorizationDO, SafetyVisitorAuthorizationDOExample, SafetyVisitorAuthorizationConfigQueryParam> getService() {
        return safetyVisitorAuthorizationService;
    }

    @Override
    public String getModuleName() {
        return null;
    }

    @Override
    protected String getTemplateName() {
        return null;
    }

    @Override
    protected String getRoleName() {
        return null;
    }

    @Override
    public Pair<String[], String[][]> processExportData(List<SafetyVisitorAuthorizationDO> dataList) {
        return null;
    }

    @Override
    public VO delete(Integer id, String reason, String token) {
        SafetyVisitorAuthorizationDO data = service().selectByPrimaryKey(id);
        if (data == null) {
            return fail("该记录不存在");
        }
        if (data.getConfigType() == 0 && "0".equals(data.getEmpOrgCodePath())) {
            return fail("默认值禁止删除");
        }
        data.setDelFlag(SafetyWebBaseEntity.DELETE);
        data.setUpdateInfo(token);
        boolean b = service().updateByPrimaryKey(data);
        if (!b) {
            return fail("数据删除失败!");
        }
        return success();
    }

    @Override
    protected void beforeSave(SafetyVisitorAuthorizationDO data) {
        if (data.saveOrUpdate()) {
            if (data.getConfigType() == 1) {
                SafetyEmployeeMessageDO info = safetyEmployeeMessageService.getByNo(data.getEmpNo());
                if (info == null) {
                    throw new BizException(DB_DATA_NOT_EXIT);
                }
                data.setEmpName(info.getEmpName());
                data.setEmpOrgCodePath(info.getOrgCodePath());
                data.setEmpOrgNamePath(info.getOrgNamePath());
            }
        }
    }
}
