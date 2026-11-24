package com.yutong.safety.core.service;

import com.yutong.safety.common.constants.FieldMoveEnum;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.*;
import com.yutong.safety.common.utils.PinYinUtils;
import com.yutong.safety.common.utils.PreconditonsEx;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.vo.web.FactoryRegionVO;
import com.yutong.safety.common.vo.web.FieldManageVO;
import com.yutong.safety.core.db.entity.SafetyFieldManageDO;
import com.yutong.safety.core.db.entity.SafetyFieldManageDOExample;
import com.yutong.safety.core.db.entity.SafetyFkDO;
import com.yutong.safety.core.db.mapper.SafetyFieldManageDOBizMapper;
import com.yutong.safety.core.db.mapper.SafetyFieldManageDOMapper;
import com.yutong.safety.core.dto.FieldManageDTO;
import com.yutong.safety.core.dto.FieldManageListDTO;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *  create gwcuiyx 2023.07.11
 *  字段管理--service
 */
@Service
public class SafetyFieldManageService {

    @Autowired
    private SafetyFieldManageDOMapper safetyFieldManageDOMapper;
    @Autowired
    private SafetyFieldManageDOBizMapper safetyFieldManageDOBizMapper;
    @Autowired
    private SafetyLogMessageService safetyLogMessageService;


    /**
     * 添加功能
     * @param param 入参
     */
    public void add(FieldManageAddParam param, String tocken) {
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        SafetyFieldManageDO safetyFieldManageDO = new SafetyFieldManageDO();
        BeanUtils.copyProperties(param,safetyFieldManageDO);
        int maxSort = getMaxSort(param.getFieldType(),param.getParentId());
        safetyFieldManageDO.setCreateTime(new Date());
        safetyFieldManageDO.setUpdateTime(new Date());
        safetyFieldManageDO.setFieldSort(maxSort);
        safetyFieldManageDO.setDeleteFalg(false);
        if(StringUtils.isBlank(safetyFieldManageDO.getFieldCode())){
            Integer parentId = param.getParentId();
            String preCode = param.getFieldType();
            if(parentId != null){
                preCode = parentId+"";
            }
            String curCode = preCode+"_"+ PinYinUtils.getPinYinHeadChar(param.getFieldName()).toLowerCase()+"_"+maxSort;
            safetyFieldManageDO.setFieldCode(curCode);
        }
        PreconditonsEx.checkArgument(safetyFieldManageDOMapper.insert(safetyFieldManageDO) == 1, ResponseEnum.FAIL);

        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("字段管理");
        log.setAbility("添加");
        String logFileName = "";
        if ("factory".equals(param.getFieldType())) {
            logFileName = "厂区名称--"+param.getFieldName();
        } else if("visit".equals(param.getFieldType())) {
            logFileName = "来访事由--"+param.getFieldName();
        } else if("region".equals(param.getFieldType())) {
            SafetyFieldManageDO fieldManageDO = safetyFieldManageDOMapper.selectByPrimaryKey(param.getParentId());
            logFileName = fieldManageDO.getFieldName()+"--"+param.getFieldName();
        }
        log.setMessage(logFileName);
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
    }

    /**
     * 获取最大值
     * @param fieldType 字段类型
     * @return 返回值
     */
    public int getMaxSort(String fieldType,Integer parentId) {
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andFieldTypeEqualTo(fieldType);
        if(parentId != null){
            criteria.andParentIdEqualTo(parentId);
        }
        example.setOrderByClause(" field_sort desc ");
        List<SafetyFieldManageDO> list = safetyFieldManageDOMapper.selectByExample(example);
        return !CollectionUtils.isEmpty(list) ? list.get(0).getFieldSort() + 1 : 0;
    }

    /**
     * 删除操作
     * @param id 主键ID
     * @return 返回值
     */
    public boolean delete(Integer id, String tocken) {
        SafetyFieldManageDO safetyFieldManageDO = safetyFieldManageDOMapper.selectByPrimaryKey(id);
        if(safetyFieldManageDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        safetyFieldManageDO.setDeleteFalg(true);
        //删除操作
        int sum = safetyFieldManageDOMapper.updateByPrimaryKey(safetyFieldManageDO);
        //删除后，将排序好重新排列
        //recountFieldMessage(safetyFieldManageDO.getFieldSort(), safetyFieldManageDO.getFieldType());
        deleteSon(id);
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("字段管理");
        log.setAbility("添加");
        String logFileName = "";
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        if ("factory".equals(safetyFieldManageDO.getFieldType())) {
            logFileName = "厂区名称--"+safetyFieldManageDO.getFieldName();
        } else if("visit".equals(safetyFieldManageDO.getFieldType())) {
            logFileName = "来访事由--"+safetyFieldManageDO.getFieldName();
        } else if("region".equals(safetyFieldManageDO.getFieldType())) {
            SafetyFieldManageDO fieldManageDO = safetyFieldManageDOMapper.selectByPrimaryKey(safetyFieldManageDO.getParentId());
            logFileName = fieldManageDO.getFieldName()+"--"+safetyFieldManageDO.getFieldName();
        }else{
            SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
            SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
            criteria.andFieldCodeEqualTo(safetyFieldManageDO.getFieldType());
            List<SafetyFieldManageDO> fieldManageDOList = safetyFieldManageDOMapper.selectByExample(example);
            if(!CollectionUtils.isEmpty(fieldManageDOList)){
                logFileName = fieldManageDOList.get(0).getFieldName()+"--"+safetyFieldManageDO.getFieldName();
            }
        }
        log.setMessage(logFileName);
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        return sum > 0;
    }

    /**
     * 批量更新子类数据
     * @param parentId 上级ID
     */
    public void deleteSon(Integer parentId) {
        safetyFieldManageDOBizMapper.updateBatchByParentId(parentId);
    }

    /**
     * 重新计算顺序
     * @param fieldSort 字段排序
     */
    public void recountFieldMessage(int fieldSort, String fieldType) {
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andFieldTypeEqualTo(fieldType);
        criteria.andFieldSortGreaterThanOrEqualTo(fieldSort);
        List<SafetyFieldManageDO> list = safetyFieldManageDOMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(list)) {
            for (SafetyFieldManageDO fieldManageDO : list) {
                fieldManageDO.setFieldSort(fieldManageDO.getFieldSort()-1);
            }
            safetyFieldManageDOBizMapper.updateBatchById(list);
        }
    }

    /**
     * 数据列表功能查询
     * @return 返回值
     */
    public FieldManageListDTO fieldManageList() {
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        example.setOrderByClause(" field_type, field_sort ASC");
        List<SafetyFieldManageDO> list = safetyFieldManageDOMapper.selectByExample(example);
        FieldManageListDTO fieldManageListDTO = null;
        FieldManageDTO safetyFieldManageDTO = null;
        if(!CollectionUtils.isEmpty(list)) {
            fieldManageListDTO = new FieldManageListDTO();
            List<FieldManageDTO> factoryList = Lists.newArrayList();
            List<FieldManageDTO> visitList = Lists.newArrayList();
            List<FieldManageDTO> regionList = Lists.newArrayList();
            List<FieldManageDTO> authTypeList = Lists.newArrayList();
            List<FieldManageDTO> inspectionList = Lists.newArrayList();

            for (SafetyFieldManageDO fieldManageDO : list) {
                safetyFieldManageDTO = new  FieldManageDTO();
                BeanUtils.copyProperties(fieldManageDO, safetyFieldManageDTO);
                if("factory".equals(fieldManageDO.getFieldType())) {
                    factoryList.add(safetyFieldManageDTO);
                } else if("visit".equals(fieldManageDO.getFieldType())) {
                    visitList.add(safetyFieldManageDTO);
                } else if("region".equals(fieldManageDO.getFieldType())) {
                    regionList.add(safetyFieldManageDTO);
                } else if("authType".equals(fieldManageDO.getFieldType())) {
                    authTypeList.add(safetyFieldManageDTO);
                }
            }
            if (!CollectionUtils.isEmpty(factoryList)) {
                for (FieldManageDTO fieldDTO : factoryList) {
                    fieldDTO.setRegionList(getChildrenRegion(fieldDTO.getId(), regionList, authTypeList));
                }
            }
            fieldManageListDTO.setFactoryList(factoryList);
            fieldManageListDTO.setVisitList(visitList);
        }
        return fieldManageListDTO;
    }

    /**
     * 组合厂区子类数据
     * @param parentId 上级ID
     * @param list 列表
     * @return 返回值
     */
    public List<FieldManageDTO> getChildrenRegion(Integer parentId, List<FieldManageDTO> list, List<FieldManageDTO> authTypeList) {
        List<FieldManageDTO> sonList = Lists.newArrayList();
        for (FieldManageDTO fieldManage : list) {
            if(parentId.toString().equals(fieldManage.getParentId().toString())) {
                if (!CollectionUtils.isEmpty(authTypeList)){
                    fieldManage.setRegionList(getChildrenAuthType(fieldManage.getId(), authTypeList));
                }
                sonList.add(fieldManage);
            }
        }
        return sonList;
    }

    /**
     * 组合区域子类授权分类数据
     * @param parentId 上级ID
     * @param list ；欧恩
     * @return 返回值
     */
    public List<FieldManageDTO> getChildrenAuthType(Integer parentId, List<FieldManageDTO> list) {
        List<FieldManageDTO> sonList = Lists.newArrayList();
        for (FieldManageDTO fieldManage : list) {
            if(parentId.toString().equals(fieldManage.getParentId().toString())) {
                sonList.add(fieldManage);
            }
        }
        return sonList;
    }

    /**
     * 移动功能操作
     * @param param 入参
     */
    public void moveFieldManage(FieldManageMoveParam param) {
        //获取原来数据
        SafetyFieldManageDO oldFiledManageDO = safetyFieldManageDOMapper.selectByPrimaryKey(param.getId());
        if(oldFiledManageDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        List<SafetyFieldManageDO> fieldList = Lists.newArrayList();
        SafetyFieldManageDO newFiledManageDO = null;
        int oldSort = oldFiledManageDO.getFieldSort();
        int newSort = (param.getMove() == FieldMoveEnum.UP.getType() ? oldSort - 1 : oldSort + 1);
        //获取对应数据
        newFiledManageDO = getFieldManage(oldFiledManageDO.getFieldType(),oldFiledManageDO.getParentId(), newSort);
        if(newFiledManageDO == null){
            return;
        }
        //调整数据
        oldFiledManageDO.setFieldSort(newSort);
        newFiledManageDO.setFieldSort(oldSort);
        fieldList.add(oldFiledManageDO);
        fieldList.add(newFiledManageDO);
        safetyFieldManageDOBizMapper.updateBatchById(fieldList);
    }

    /**
     * 根据排序和类型获取数据
     * @param fieldType 字段类型
     * @param fieldSort 字段排序
     * @return 返回值
     */
    public SafetyFieldManageDO getFieldManage(String fieldType,Integer parentId, int fieldSort) {
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andFieldTypeEqualTo(fieldType);
        criteria.andFieldSortEqualTo(fieldSort);
        if(parentId != null){
            criteria.andParentIdEqualTo(parentId);
        }
        List<SafetyFieldManageDO> list = safetyFieldManageDOMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 数据更新操作
     * @param param 入参
     */
    public void update(FieldManageUpdateParam param, String tocken) {
        SafetyFieldManageDO safetyFieldManageDO = safetyFieldManageDOMapper.selectByPrimaryKey(param.getId());
        if(safetyFieldManageDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        Map<String, String> tockenUser = TokenUtils.getTockenUser(tocken);
        safetyFieldManageDO.setFieldType(param.getFieldType());
        safetyFieldManageDO.setFieldName(param.getFieldName());
        safetyFieldManageDO.setFieldCode(param.getFieldCode());
        safetyFieldManageDO.setUpdateTime(new Date());
        safetyFieldManageDO.setFieldValue(param.getFieldValue());
        safetyFieldManageDO.setEnvirInfo(param.getEnvirInfo());
        //操作日志记录
        LogMessageAddParam log = new LogMessageAddParam();
        log.setOperatePage("字段管理");
        log.setAbility("更新");
        log.setMessage(param.getFieldName());
        log.setOperator(tockenUser.get("realName"));
        safetyLogMessageService.addLogMessage(log);
        PreconditonsEx.checkArgument(safetyFieldManageDOMapper.updateByPrimaryKey(safetyFieldManageDO) == 1, ResponseEnum.FAIL);
    }

    /**
     * 联动数据列表数据查询
     * @return 返回值
     */
    public Object selectLinkageList() {
        List<FieldManageVO> list = safetyFieldManageDOBizMapper.selectLinkageList();
        List<FieldManageVO> factoryList = Lists.newArrayList();
        List<FieldManageVO> regionList = Lists.newArrayList();
        if(!CollectionUtils.isEmpty(list)) {
            FieldManageVO fieldManage = null;
            for (FieldManageVO fieldManageVO : list) {
                fieldManage = new FieldManageVO();
                BeanUtils.copyProperties(fieldManageVO, fieldManage);
                if(Objects.nonNull(fieldManageVO.getParentId())) {
                    regionList.add(fieldManage);
                } else {
                    factoryList.add(fieldManage);
                }
            }
        }
        for (FieldManageVO factoryMessage : factoryList) {
            factoryMessage.setRegionList(getChildren(factoryMessage.getId(), regionList));
        }
        return factoryList;
    }

    public Object selectLinkageListNew() {
        List<FieldManageVO> list = safetyFieldManageDOBizMapper.selectLinkageListNew();
        List<FieldManageVO> factoryList = Lists.newArrayList();
        List<FieldManageVO> regionList = Lists.newArrayList();
        if(!CollectionUtils.isEmpty(list)) {
            FieldManageVO fieldManage = null;
            for (FieldManageVO fieldManageVO : list) {
                fieldManage = new FieldManageVO();
                BeanUtils.copyProperties(fieldManageVO, fieldManage);
                if(Objects.nonNull(fieldManageVO.getParentId())) {
                    regionList.add(fieldManage);
                } else {
                    factoryList.add(fieldManage);
                }
            }
        }
        for (FieldManageVO factoryMessage : factoryList) {
            factoryMessage.setRegionList(getChildren(factoryMessage.getId(), regionList));
        }
        return factoryList;
    }

    public Object selectLinkageListNewName() {
        return safetyFieldManageDOBizMapper.selectLinkageListNewName();
    }

    public List<FieldManageVO> getChildren(Integer parentId, List<FieldManageVO> list) {
        List<FieldManageVO> sonList = Lists.newArrayList();
        for (FieldManageVO fieldManage : list) {
            if(parentId.toString().equals(fieldManage.getParentId().toString())) {
                sonList.add(fieldManage);
            }
        }
        return sonList;
    }

    /**
     * 来访事由
     * @return 返回值
     */
    public Object getVisitList() {
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andFieldTypeEqualTo("visit");
        example.setOrderByClause(" field_type, field_sort ASC");
        return safetyFieldManageDOMapper.selectByExample(example);
    }

    public List<FactoryRegionVO> getFactoryRegionList() {
        List<String> fieldTypes = Lists.newArrayList();
        fieldTypes.add("factory");
        fieldTypes.add("region");
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andFieldTypeIn(fieldTypes);
        example.setOrderByClause(" field_type, field_sort ASC");
        List<SafetyFieldManageDO> list = safetyFieldManageDOMapper.selectByExample(example);
        FieldManageDTO safetyFieldManageDTO = null;
        if(!CollectionUtils.isEmpty(list)) {
            List<FieldManageDTO> factoryList = Lists.newArrayList();
            List<FieldManageDTO> regionList = Lists.newArrayList();
            for (SafetyFieldManageDO fieldManageDO : list) {
                safetyFieldManageDTO = new  FieldManageDTO();
                BeanUtils.copyProperties(fieldManageDO, safetyFieldManageDTO);
                if("factory".equals(fieldManageDO.getFieldType())) {
                    factoryList.add(safetyFieldManageDTO);
                }else if("region".equals(fieldManageDO.getFieldType())) {
                    regionList.add(safetyFieldManageDTO);
                }
            }
            //父类的id加名称
            Map<Integer, FieldManageDTO> factoryMap = factoryList.stream().collect(Collectors.toMap(FieldManageDTO::getId, Function.identity()));
            List<FactoryRegionVO> factoryRegionList = Lists.newArrayList();
            FactoryRegionVO factoryRegionVO;
            FieldManageDTO factoryDTO = null;
            for (FieldManageDTO fieldDTO : regionList) {
                factoryRegionVO = new FactoryRegionVO();
                factoryRegionVO.setParentId(fieldDTO.getParentId());
                factoryRegionVO.setSonId(fieldDTO.getId());
                factoryRegionVO.setSonName(fieldDTO.getFieldName());
                factoryRegionVO.setSonCode(fieldDTO.getFieldCode());
                factoryDTO = factoryMap.get(fieldDTO.getParentId());
                if (factoryDTO != null) {
                    factoryRegionVO.setParentName(factoryDTO.getFieldName());
                    factoryRegionList.add(factoryRegionVO);
                }
            }
            return factoryRegionList;
        }
        return null;
    }

    /**
     * 根据id获取名称
     * @param ids ID拼接串
     * @return 返回值
     */
    public String getNamesByIds(String ids) {
        String[] idss = ids.split(";");
        List<Integer> idList = Lists.newArrayList();
        for (String s : idss) {
            idList.add(Integer.valueOf(s));
        }
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(idList);
        List<SafetyFieldManageDO> list = safetyFieldManageDOMapper.selectByExample(example);
        return list.stream().map(SafetyFieldManageDO::getFieldName).collect(Collectors.toList()).toString();
    }

    /**
     * 获取可用工厂名称
     * @return 返回值
     */
    public Map<Integer, String> getFactoryNameList() {
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andFieldTypeEqualTo("factory");
        List<SafetyFieldManageDO> list = safetyFieldManageDOMapper.selectByExample(example);
        return list.stream().collect(Collectors.toMap(SafetyFieldManageDO::getId, SafetyFieldManageDO::getFieldName));
    }

    /**
     * 根据名称获取对象
     * @return 返回值
     */
    public Map<String, SafetyFieldManageDO> getFactoryList() {
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andFieldTypeEqualTo("factory");
        List<SafetyFieldManageDO> list = safetyFieldManageDOMapper.selectByExample(example);
        return list.stream().collect(Collectors.toMap(SafetyFieldManageDO::getFieldName, Function.identity()));
    }

    /**
     * 根据名称获取对象
     * @return 返回值
     */
    public List<SafetyFieldManageDO> getFactoryListNew() {
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andFieldTypeEqualTo("factory");
        return safetyFieldManageDOMapper.selectByExample(example);
    }

    /**
     * 获取名称
     * @param ids ID品额吉
     * @return 返回值
     */ 
    public String getNamesByListIds(List<Integer> ids) {
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        List<SafetyFieldManageDO> list = safetyFieldManageDOMapper.selectByExample(example);
        return list.stream().map(SafetyFieldManageDO::getFieldName).collect(Collectors.toList()).toString();
    }

    /**
     *
     * @param param 入参
     * @return 返回值
     */
    public List<SafetyFieldManageDO> getChildList(FieldManageListParam param){
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if(StringUtils.isNotEmpty(param.getFieldType())){
            criteria.andFieldTypeEqualTo(param.getFieldType());
        }
        if(StringUtils.isNotBlank(param.getFieldCode())){
            criteria.andFieldCodeEqualTo(param.getFieldCode());
        }
        if(param.getParentId() != null){
            criteria.andParentIdEqualTo(param.getParentId());
        }else{
            criteria.andParentIdIsNull();
        }
        if(StringUtils.isNotBlank(param.getFactoryIds())){
            String[] ids = param.getFactoryIds().split(",");
            List<Integer> idList =  Arrays.stream(ids).map(Integer::valueOf).collect(Collectors.toList());
            criteria.andIdIn(idList);
        }
        if(!CollectionUtils.isEmpty(param.getFactoryIdList())){
           criteria.andIdIn(param.getFactoryIdList());
        }
        example.setOrderByClause(" field_type, field_sort ASC");
        return safetyFieldManageDOMapper.selectByExample(example);
    }

    public List<String> fieldTypeList(){
        return safetyFieldManageDOBizMapper.fieldTypeList();
    }

    /**
     * 获取设备管理中的对应项目数据字段数据
     * @return 返回值
     */
    public Map<String, SafetyFieldManageDO> getSpotTeams() {
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false).andFieldTypeEqualTo("spotTeam");

        List<SafetyFieldManageDO> list = safetyFieldManageDOBizMapper.selectByExample(example);
        return list.stream().collect(Collectors.toMap(SafetyFieldManageDO::getFieldName, Function.identity()));
    }

    public List<SafetyFieldManageDO> getSubTree(FieldManageListParam param){
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        if(StringUtils.isNotEmpty(param.getFieldType())){
            criteria.andFieldCodeEqualTo(param.getFieldType());
        }
        return safetyFieldManageDOMapper.selectByExample(example);
    }

    /**
     *
     * @param id 主键ID
     * @return 返回值
     */
    public SafetyFieldManageDO getInfoById(Integer id) {
        SafetyFieldManageDO safetyFieldManageDO = safetyFieldManageDOBizMapper.selectByPrimaryKey(id);
        if (safetyFieldManageDO == null) {
            throw new BizException(ResponseEnum.FAIL.getCode(),"该记录不存在");
        }
        return safetyFieldManageDO;
    }

    /**
     * 根据名称获取对象
     * @return 返回值
     */
    public Map<Integer, SafetyFieldManageDO> getFiledManageList(String filedTypeCode) {
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        List<String> typeCodeList = Arrays.asList(filedTypeCode.split(","));
        criteria.andFieldTypeIn(typeCodeList);
        List<SafetyFieldManageDO> list = safetyFieldManageDOMapper.selectByExample(example);
        return list.stream().collect(Collectors.toMap(SafetyFieldManageDO::getId, Function.identity()));
    }

    public List<SafetyFkDO> getFactoryBySet(List<String> phoneList){
        return safetyFieldManageDOBizMapper.getFactoryBySet(phoneList);
    }

    public List<SafetyFieldManageDO> getInfoByFieldType(String fieldType) {
        SafetyFieldManageDOExample e = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = e.createCriteria();
        criteria.andFieldTypeEqualTo(fieldType);
        criteria.andDeleteFalgEqualTo(false);
        return safetyFieldManageDOMapper.selectByExample(e);
    }


    /**
     * 根据排序和类型获取数据
     * @param fieldType 字段类型
     * @param filedCode 字典编码
     * @return 返回值
     */
    public SafetyFieldManageDO getByFiledCode(String fieldType,String filedCode) {
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andFieldTypeEqualTo(fieldType);
        criteria.andFieldCodeEqualTo(filedCode);
        List<SafetyFieldManageDO> list = safetyFieldManageDOMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    public Object findBuildList(String factoryId){
        SafetyFieldManageDOExample example = new SafetyFieldManageDOExample();
        SafetyFieldManageDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFalgEqualTo(false);
        criteria.andFieldTypeEqualTo(factoryId);
        example.setOrderByClause(" field_sort ");

        return safetyFieldManageDOMapper.selectByExample(example);
    }

    public List<SafetyFieldManageDO> selectBuildList() {
        return safetyFieldManageDOMapper.selectBuildList();
    }
}
