package com.yutong.modules.common.vo;

import lombok.Data;

import java.util.List;

@Data
public class DoorIssueResultVO {
    private DoorIssueStateVO State;
    private DoorIssueModelVO Model;
    private List<DoorIssueModelVO> Models;

    @Data
    public static class DoorIssueStateVO {
        private Boolean IsSucess;
        private Boolean IsError;
        private Integer Code;
        private Integer RecordAffected;
        private Object Describe;
    }

    @Data
    public static class DoorIssueModelVO {
        private Boolean Result;
        private Object Data;
        private Integer ErrCode;
        private String ErrMessage;
        private Object Gid;
        private Integer ID;
        private Object Rid;
        private Boolean IsLegal;
    }
}
