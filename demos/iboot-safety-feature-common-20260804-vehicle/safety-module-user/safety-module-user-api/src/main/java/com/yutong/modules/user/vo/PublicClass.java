package com.yutong.modules.user.vo;

import lombok.Data;

import java.util.List;

@Data
public class PublicClass {

    private String dbCode;

    private String dbName;

    private List<EnumMapping> enumMappings;

    @Data
    public static class EnumMapping{
        private String enumValue;
        private String description;
    }
}
