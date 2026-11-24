package com.yutong.modules.common.entity.bpm;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
public class ProcessPage<T> {

    /**
     * 分页信息
     */
    @JsonAlias(value="page")
    private ProcessPageNum page;
    /**
     * 数据
     */
    private List<T> datas;
}
