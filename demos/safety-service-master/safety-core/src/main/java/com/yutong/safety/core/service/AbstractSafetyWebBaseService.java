package com.yutong.safety.core.service;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.WebPageParam;
import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import com.yutong.safety.core.db.mapper.SafetyWebBaseMapper;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * 增删改查 Handler 层封装
 *
 * @param <E> 列表对象，数据库对应的实体类对象，列表返回值
 * @param <T> 以DOExample实体类，操作数据库的信息
 * @param <S> 入参， 查询入参
 */
public abstract class AbstractSafetyWebBaseService<E extends SafetyWebBaseEntity, T, S extends WebPageParam> {

    protected abstract SafetyWebBaseMapper<E, T> getMapper();

    protected abstract void formatExample(T example, S param, boolean pageType);

    protected abstract void batchDelete(T example, List<String> idList);

    protected abstract void isExist(E data, T example);

    public SafetyWebBaseMapper<E, T> mapper() {
        SafetyWebBaseMapper<E, T> mapper = getMapper();
        if (mapper == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "获取mapper失败！");
        }
        return mapper;
    }

    public long selectCount(S param) {
        T example = newInstance();
        formatExample(example, param, false);
        return mapper().countByExample(example);
    }

    public List<E> selectList(S param, boolean bool) {
        T example = newInstance();
        formatExample(example, param, bool);
        return mapper().selectByExample(example);
    }

    public E selectByPrimaryKey(Integer id) {
        return mapper().selectByPrimaryKey(id);
    }


    public boolean isExist(E data) {
        T example = newInstance();
        isExist(data, example);
        long l = mapper().countByExample(example);
        return l > 0;
    }

    public boolean updateByPrimaryKey(E data) {
        long count = mapper().updateByPrimaryKeySelective(data);
        return count > 0;
    }

    public void save(E data, String token) {
        if (data.saveOrUpdate()) {
            data.setCreates(token);
            mapper().insert(data);
        } else {
            data.setUpdateInfo(token);
            mapper().updateByPrimaryKeySelective(data);
        }
    }

    public void saveInfo(E data) {
        if (data.saveOrUpdate()) {
            mapper().insert(data);
        } else {
            mapper().updateByPrimaryKeySelective(data);
        }
    }

    public E findById(Integer id) {
        return mapper().selectByPrimaryKey(id);
    }

    private T newInstance() {
        try {
            ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
            Class<T> clazz = (Class<T>) pt.getActualTypeArguments()[1];
            return clazz.newInstance();
        } catch (Exception e) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "反射初始化对象报错！");
        }
    }

    public boolean deleteById(E record, List<String> idList) {
        T example = newInstance();
        this.batchDelete(example, idList);
        int i = mapper().updateByExampleSelective(record, example);
        return i > 0;
    }

    public E selectOne(S param) {
        param.setPageNum(0);
        param.setPageSize(1);
        T example = newInstance();
        formatExample(example, param, true);
        List<E> list = mapper().selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            return list.get(0);
        }
    }
}
