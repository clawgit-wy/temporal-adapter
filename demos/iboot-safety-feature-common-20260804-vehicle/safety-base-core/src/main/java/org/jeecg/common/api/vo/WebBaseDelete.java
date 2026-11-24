package org.jeecg.common.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 前端传参
 */
@Data
public class WebBaseDelete {

    @ApiModelProperty("单个操作的标记")
    private String id;

    @ApiModelProperty("批量操作的标记")
    private List<String> ids;

    @ApiModelProperty("操作原因-备注")
    private String reason;

    public void check() {
        if (StringUtils.isBlank(id) && CollectionUtils.isEmpty(ids)) {
            throw new JeecgBootException("单个操作标记或批量操作标记不能为空");
        }
        if (StringUtils.isNotBlank(id)) {
            if (CollectionUtils.isEmpty(ids)) {
                ids = new ArrayList<>();
            }
            ids.add(id);
        }
    }
}
