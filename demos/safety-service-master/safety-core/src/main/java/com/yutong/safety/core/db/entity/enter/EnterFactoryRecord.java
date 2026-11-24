package com.yutong.safety.core.db.entity.enter;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 入场备案表
 * </p>
 */
@Data
public class EnterFactoryRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;

    private String cardId;

    private String phone;

    private String photographUrl;

    private String dataType;
}
