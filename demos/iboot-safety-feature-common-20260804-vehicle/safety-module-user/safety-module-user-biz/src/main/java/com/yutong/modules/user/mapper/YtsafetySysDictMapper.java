package com.yutong.modules.user.mapper;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.user.entity.SafetyDictSupper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yutong.modules.user.query.SafetyDictSupperQuery;
import com.yutong.modules.user.vo.SafetyDictSupperVO;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 字典表
 * @Author: jeecg-boot
 * @Date:   2026-05-27
 * @Version: V1.0
 */
public interface YtsafetySysDictMapper extends BaseMapper<SafetyDictSupper> {

    //数据权限拦截示例
//    @DataPermission(ruleCode="dict",orderColumn={"update_time","supper_order"},orderRule="desc")
    IPage<SafetyDictSupperVO> queryPageList(Page<SafetyDictSupperVO> page, @Param("query") SafetyDictSupperQuery safetyDictSupperQuery);

    SafetyDictSupperVO getById(Long id);
}
