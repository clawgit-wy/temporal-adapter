package com.yutong.modules.user.api;


import com.yutong.modules.user.entity.YtsafetySysUserDataRule;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.ColumnPermVO;

import java.util.List;

/**
 * @Description: 用户菜单数据规则表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
public interface IYtsafetySysUserDataRuleService extends IService<YtsafetySysUserDataRule> {

    /**
     * 根据用户id和字段编码查询数据规则列表，userId和fieldCode为空时不作查询条件
     */
    String getListByUserId(String workNo, String permCode);

    List<ColumnPermVO> getColumnPermissions(String workNo,String permCode);
}
