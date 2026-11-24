package com.yutong.safety.common.param.web.spot;

import com.yutong.safety.common.param.web.WebPageParam;
import lombok.Data;

import java.util.List;

/**
 * @author gwxiexj
 * @since 2024-04-28 11:19
 */
@Data
public class SpotCheckRuleListParam extends WebPageParam {
    /**
     * 项目id
     */
    private Integer teamId;

    /**
     * 项目名称
     */
    private String teamName;
    /**
     * 标准类目
     */
    private String ruleName;
    /**
     * 备注
     */
    private String remark;

    private List<Integer> idList;
    /**
     * 要排除掉的ID
     */
    private Integer unId;
}
