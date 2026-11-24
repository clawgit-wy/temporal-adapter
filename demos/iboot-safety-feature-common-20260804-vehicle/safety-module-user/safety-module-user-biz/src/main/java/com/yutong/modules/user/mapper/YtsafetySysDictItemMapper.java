package com.yutong.modules.user.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.user.dto.SafetyDictItemDTO;
import com.yutong.modules.user.entity.SafetyDictItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.user.query.SafetyDictItemQuery;
import com.yutong.modules.user.vo.SafetyDictItemVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 字典子表
 * @Author: jeecg-boot
 * @Date:   2026-05-27
 * @Version: V1.0
 */
public interface YtsafetySysDictItemMapper extends BaseMapper<SafetyDictItem> {

    /**
     * 通过字典id查询字典项
     * @param mainId 字典id
     * @return
     */
    @Select("SELECT * FROM ytsafety_sys_dict_item WHERE DICT_ID = #{mainId} order by sort_order asc, item_value asc")
    public List<SafetyDictItem> selectItemsByMainId(Long mainId);

    IPage<SafetyDictItemVO> dictItemList(Page page, Long supperId);

    List<SafetyDictItemVO> selectByName( @Param("query") SafetyDictItemQuery query);
}
