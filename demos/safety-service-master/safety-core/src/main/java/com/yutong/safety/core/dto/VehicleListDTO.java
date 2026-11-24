package com.yutong.safety.core.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class VehicleListDTO implements Serializable {
    private String plate;
    private String vehicleBand;
    private String vehicleColor;
    private String tcmName;
    private String tcmType;
    private String tokenId;
    private String tokenType;
    private String useMode;
    private String regDate;
    private String gid;


}
