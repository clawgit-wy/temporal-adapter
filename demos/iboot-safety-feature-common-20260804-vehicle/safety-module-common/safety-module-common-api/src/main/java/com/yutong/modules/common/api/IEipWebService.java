package com.yutong.modules.common.api;


import com.yutong.modules.common.vo.EipToDoVO;

/**
 * eip 的 业务
 */
public interface IEipWebService {

    /**
     * 发送代办
     */
    void createEipToDo(EipToDoVO eipToDoVO);

}
