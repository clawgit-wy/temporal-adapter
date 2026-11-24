package org.jeecg.common.domain;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BaseDO implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    // 创建时间
    private String createTime;

    // 创建人账号
    private String createUser;

    // 创建人姓名
    private String createName;

    // 修改时间
    private String updateTime;

    // 修改人账号
    private String updateUser;

    // 修改人姓名
    private String updateName;

    // 删除标识
    private Integer delFlag;

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
