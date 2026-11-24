package com.yutong.modules.vehicle.service;

public interface IUserJob {

    /**
     * 同步人员信息
     * @param isAll
     * @param dateTime
     */
    public void tasksMessage(Boolean isAll, String dateTime);
}
