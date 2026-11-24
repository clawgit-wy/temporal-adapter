package org.jeecg.common.domain;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BaseVO implements Serializable {

    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("创建人")
    private String createUser;

    @ApiModelProperty("修改时间")
    private String updateTime;

    @ApiModelProperty("修改人")
    private String updateUser;

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
