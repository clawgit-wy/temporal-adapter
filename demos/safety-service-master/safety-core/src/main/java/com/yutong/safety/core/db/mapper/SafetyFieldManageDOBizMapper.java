package com.yutong.safety.core.db.mapper;

import com.yutong.safety.common.vo.web.FieldManageVO;
import com.yutong.safety.core.db.entity.SafetyFieldManageDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SafetyFieldManageDOBizMapper extends SafetyFieldManageDOMapper {

    /**
     * 批量更新
     * @param fieldList
     */
    public void updateBatchById(@Param("fieldList") List<SafetyFieldManageDO> fieldList);

    /**
     * 联动数据查询
     * @return
     */
    public List<FieldManageVO> selectLinkageList();
    public List<FieldManageVO> selectLinkageListNew();
    public List<String> selectLinkageListNewName();

    /**
     * 批量更新子类数据
     * @param parentId
     */
    public void updateBatchByParentId(@Param("parentId") Integer parentId);

    public List<String> fieldTypeList();

    /**
     * 获取指定字典的上级字典对象
     * @param filedId 字典ID
     * @return
     */
    public SafetyFieldManageDO getParentFieldById(@Param("filedId") Integer filedId);
}