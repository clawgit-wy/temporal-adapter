package com.yutong.safety.core.handler;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.UserMessageAddParam;
import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.param.web.UserMessageUpdateParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyUserMessageDO;
import com.yutong.safety.core.service.SafetyUserMessageService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *  create gwcuiyx 2023.07.13
 *  用户管理--handler
 */
@Component
public class SafetyUserMessageHandler {
    @Autowired
    private SafetyUserMessageService safetyUserMessageService;

    /**
     * 数据分页列表
     * @param param
     * @return
     */
    public Object pageList(UserMessageListParam param) {
        List<SafetyUserMessageDO> list = safetyUserMessageService.pageList(param);
        long total = safetyUserMessageService.pageListCount(param);
        return new PageVO<SafetyUserMessageDO>(total, list);
    }

    /**
     * 数据导出操作
     * @param param
     * @return
     */
    public HSSFWorkbook export(UserMessageListParam param, String tocken) {
        List<SafetyUserMessageDO> list = safetyUserMessageService.export(param, tocken);
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "导出数据不能超过"+Constants.FILE_SIZE+"条！");
        }
        String[] title = new String[]{"用户名", "员工号", "角色", "修改人","修改时间"};
        String[][] values = new String[list.size()][];
        SafetyUserMessageDO userMessageDO;
        for(int i=0; i<list.size(); i++) {
            userMessageDO = list.get(i);
            values[i] = new String[title.length];
            values[i][1] = userMessageDO.getEmpName();
            values[i][2] = userMessageDO.getEmpNo();
            values[i][3] = userMessageDO.getRole();
            values[i][4] = userMessageDO.getModifyEmp();
            values[i][5] = DateUtils.format(userMessageDO.getUpdateTime(), 2);
        }
        String sheetName = "用户信息列表";
        return ExcelUtils.getHSSFWorkbook(sheetName, title, values);
    }

    /**
     * 添加功能
     * @param param
     * @return
     */
    public Object add(UserMessageAddParam param, String tocken) {
        safetyUserMessageService.add(param, tocken);
        return null;
    }

    /**
     * 数据更新操作
     * @param param
     * @return
     */
    public Object update(UserMessageUpdateParam param, String tocken) {
        safetyUserMessageService.update(param, tocken);
        return null;
    }

    /**
     * 数据展示方法
     * @param id
     * @return
     */
    public Object info(Integer id) {
        return safetyUserMessageService.info(id);
    }

    /**
     * 数据删除操作
     * @param id
     * @return
     */
    public Object delete(Integer id, String tocken) {
        safetyUserMessageService.delete(id, tocken);
        return null;
    }

    /**
     * 数据批量插入操作
     * @param param
     * @return
     */
    public Object importUserMessage(CommonImportParam param, String tocken) {
        return safetyUserMessageService.importUserMessage(param, tocken);
    }

}
