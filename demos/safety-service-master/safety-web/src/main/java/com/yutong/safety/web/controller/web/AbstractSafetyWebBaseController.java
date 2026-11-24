package com.yutong.safety.web.controller.web;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.Check;
import com.yutong.safety.common.param.web.CommonImportParam;
import com.yutong.safety.common.param.web.SafetyWebBaseParam;
import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.SafetyWebBaseDelete;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;


/**
 * 增删改查controller层封装
 *
 * @param <E> 列表对象，数据库对应的实体类对象，列表返回值
 * @param <T> 以DOExample实体类，操作数据库的信息
 * @param <S> 入参， 查询入参
 * @param <A> 入参， 新增的入参
 */
public abstract class AbstractSafetyWebBaseController<E extends SafetyWebBaseEntity, T, S extends SafetyWebBaseParam, A extends Check> {

    public abstract AbstractSafetyWebBaseHandler<E, T, S, A> getHandler();

    public AbstractSafetyWebBaseHandler<E, T, S, A> handler() {
        AbstractSafetyWebBaseHandler<E, T, S, A> handler = getHandler();
        if (handler == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "获取handler失败！");
        }
        return handler;
    }

    /**
     * 分页数据列表查询接口
     *
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "分页数据列表查询接口-无权限查询")
    @RequestMapping(value = "/pageList", method = {RequestMethod.GET,RequestMethod.POST})
    public VO pageList(S param) {
        return handler().selectPageList(param, null);
    }

    /**
     * 分页数据列表查询接口
     *
     * @param param 请求参数
     * @return 列表清单
     */
    @ApiOperation(value = "分页数据列表查询接口-权限查询")
    @RequestMapping(value = "/pageTokenList", method = {RequestMethod.GET,RequestMethod.POST})
    public VO pageList(S param, @RequestHeader("X-Access-Token") String token) {
        return handler().selectPageList(param, token);
    }

    /**
     * 数据删除操作
     */
    @ApiOperation(value = "删除接口")
    @RequestMapping(value = "/delete", method = {RequestMethod.POST})
    public VO delete(@RequestBody SafetyWebBaseDelete param, @RequestHeader("X-Access-Token") String token) {
        return handler().delete(param.getId(),param.getReason(), token);
    }

    /**
     * 数据删除操作
     */
    @ApiOperation(value = "批量删除接口")
    @RequestMapping(value = "/batchDelete", method = {RequestMethod.POST})
    public VO batchDelete(@RequestBody SafetyWebBaseDelete param, @RequestHeader("X-Access-Token") String token) {
        return handler().batchDelete(param.getIds(),param.getReason(), token);
    }

    /**
     * 更新和新增接口
     */
    @ApiOperation(value = "更新和新增接口")
    @RequestMapping(value = "/saveAndUpdate", method = {RequestMethod.POST})
    public VO saveAndUpdate(@RequestBody A info, @RequestHeader("X-Access-Token") String token) {
        try {
            return handler().saveAndUpdate(info, token);
        } catch (BizException e) {
            return VoUtil.fail(e.getCode(), e.getMsg());
        }
    }

    /**
     * 模版下载
     */
    @ApiOperation(value = "模板下载")
    @RequestMapping(value = "/download", method = {RequestMethod.GET})
    public void download(HttpServletResponse response) {
        handler().download(response);
    }

    /**
     * 数据导出操作
     */
    @ApiOperation(value = "数据导出接口")
    @RequestMapping(value = "/export", method = {RequestMethod.GET})
    public void export(S param, HttpServletResponse response, @RequestHeader("X-Access-Token") String token) {
        handler().export(response, param, token);
    }

    /**
     * 数据导入操作
     */
    @ApiOperation(value = "数据导入接口")
    @RequestMapping(value = "/import", method = {RequestMethod.POST})
    public VO importData(CommonImportParam param, @RequestHeader("X-Access-Token") String token) {
        return handler().importData(param, token);
    }
}
