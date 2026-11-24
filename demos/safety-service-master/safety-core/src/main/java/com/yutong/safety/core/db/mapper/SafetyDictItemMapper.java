package com.yutong.safety.core.db.mapper;

import com.yutong.safety.core.db.entity.dict.SafetyDictItemDO;
import com.yutong.safety.core.db.entity.dict.SafetyDictItemDOExample;
import com.yutong.safety.core.db.entity.result.DictItemRt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 数据字典子项目表 Mapper 接口
 * </p>
 *
 * @author renhaoxiang
 */
@Mapper
public interface SafetyDictItemMapper {

    /*根据父编号，查询子项目集合*/
    List<DictItemRt> selectDictItemListBySupperCode(@Param("supperCode") String supperCode);
    /*根据父ID，查询子项目集合*/
    List<DictItemRt> selectDictItemListBySupperId(@Param("supperId") Integer supperId);

    //查询总数
    long countByExample(SafetyDictItemDOExample example);

    //查询
    List<SafetyDictItemDO> selectByExample(SafetyDictItemDOExample example);

    //更新
    int updateByPrimaryKeySelective(SafetyDictItemDO record);

    //插入
    int insertSelective(SafetyDictItemDO record);
    int insert(SafetyDictItemDO record);

    //删除
    void deleteSupper(@Param("id") Integer id, @Param("realName") String realName);

    void batchSave(@Param("list") List<SafetyDictItemDO> dictItems);
}
