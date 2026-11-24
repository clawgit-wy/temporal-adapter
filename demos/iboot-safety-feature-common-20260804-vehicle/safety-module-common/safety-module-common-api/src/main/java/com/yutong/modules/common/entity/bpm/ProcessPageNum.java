package com.yutong.modules.common.entity.bpm;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
public class ProcessPageNum {
    /**
     * 总条数
     */
    @JsonAlias(value="totalSize")
    private Integer totalSize;

    /**
     * 分页大小
     */
    @JsonAlias(value="pageSize")
    private Integer pageSize;

    /**
     * 当前页码
     */
    @JsonAlias(value="currentPage")
    private Integer currentPage;
}
