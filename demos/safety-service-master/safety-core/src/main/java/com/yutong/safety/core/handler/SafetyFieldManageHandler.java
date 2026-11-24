package com.yutong.safety.core.handler;

import com.alibaba.excel.util.StringUtils;
import com.yutong.safety.common.param.web.FieldManageAddParam;
import com.yutong.safety.common.param.web.FieldManageListParam;
import com.yutong.safety.common.param.web.FieldManageMoveParam;
import com.yutong.safety.common.param.web.FieldManageUpdateParam;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.SafetyFieldManageDO;
import com.yutong.safety.core.dto.FieldManagerVODTO;
import com.yutong.safety.core.handler.spot.SafetySpotCheckTaskHandler;
import com.yutong.safety.core.service.SafetyFieldManageService;
import com.yutong.safety.core.service.SafetySysUserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 *  create gwcuiyx 2023.07.11
 *  字段管理--handler
 */
@Component
public class SafetyFieldManageHandler {

    @Autowired
    private SafetyFieldManageService safetyFieldManageService;

    @Autowired
    private SafetySysUserService safetySysUserService;

    @Autowired
    private SafetySpotCheckTaskHandler safetySpotCheckTaskHandler;

    /**
     * 新增操作
     * @param param
     * @return
     */
    public Object add(FieldManageAddParam param, String tocken) {
        safetyFieldManageService.add(param, tocken);
        return null;
    }

    /**
     * 删除操作
     * @param id
     * @return
     */
    public Object delete(Integer id, String tocken) {
        return safetyFieldManageService.delete(id, tocken);
    }

    /**
     * 数据列表操作
     * @return
     */
    public Object fieldManageList() {
        return safetyFieldManageService.fieldManageList();
    }

    /**
     * 数据移动方法操作
     * @param param
     * @return
     */
    public Object moveFieldManage(FieldManageMoveParam param) {
        safetyFieldManageService.moveFieldManage(param);
        return null;
    }

    /**
     * 数据更新操作
     * @param param
     * @return
     */
    public Object update(FieldManageUpdateParam param, String tocken) {
        safetyFieldManageService.update(param, tocken);
        return null;
    }

    /**
     * 获取联动列表数据
     * @return
     */
    public Object selectLinkageList() {
        return safetyFieldManageService.selectLinkageList();
    }

    public Object selectLinkageListNew() {
        return safetyFieldManageService.selectLinkageListNew();
    }

    public Object selectLinkageListNewName() {
        return safetyFieldManageService.selectLinkageListNewName();
    }

    /**
     * 来访事由
     * @return
     */
    public Object getVisitList() {
        return safetyFieldManageService.getVisitList();
    }


    /**
     * 根据字段管理类型获取类别数
     *
     * @param param
     * @return
     * @throws Exception
     */
    public Object fieldManageTree(FieldManageListParam param) throws Exception{
        if(StringUtils.isEmpty(param.getFieldType())){
            param.setFieldType("manageType");
        }
        List<FieldManagerVODTO> voList = new ArrayList<>();
        List<SafetyFieldManageDO> doList = safetyFieldManageService.getChildList(param);
        if(doList != null && doList.size() >0){
            Optional<SafetyFieldManageDO> doMaxShort = doList.stream().max(Comparator.comparing(SafetyFieldManageDO::getFieldSort));
            Integer maxShort = doMaxShort.get().getFieldSort();
            for(SafetyFieldManageDO fieldManageDO:doList){
                FieldManagerVODTO newVO = new FieldManagerVODTO();
                BeanUtils.copyProperties(fieldManageDO,newVO);
                newVO.setMaxSort(maxShort);
                //子类
                FieldManageListParam subParam = new FieldManageListParam();
                subParam.setFieldType(fieldManageDO.getFieldCode());
                subParam.setParentId(fieldManageDO.getId());
                List<FieldManagerVODTO> subList = getChileList(subParam);
                newVO.setRegionList(subList);
                voList.add(newVO);
            }
        }
        return voList;
    }

    /**
     * 获取个子类别树
     * @param param 入参
     * @return 字段列表
     */
    private List<FieldManagerVODTO> getChileList(FieldManageListParam param) {
        List<FieldManagerVODTO> voList = new ArrayList<>();

        List<SafetyFieldManageDO> doList = safetyFieldManageService.getChildList(param);
        if(doList != null && doList.size() >0){
            Optional<SafetyFieldManageDO> doMaxShort = doList.stream().max(Comparator.comparing(SafetyFieldManageDO::getFieldSort));
            Integer maxShort = doMaxShort.get().getFieldSort();
            for(SafetyFieldManageDO fieldManageDO:doList){
                FieldManagerVODTO newVO = new FieldManagerVODTO();
                BeanUtils.copyProperties(fieldManageDO,newVO);
                newVO.setMaxSort(maxShort);
                FieldManageListParam subParam = new FieldManageListParam();
                subParam.setFieldType(param.getFieldType());
                subParam.setParentId(fieldManageDO.getId());
                List<FieldManagerVODTO> subList = getChileList(subParam);
                newVO.setRegionList(subList);
                voList.add(newVO);
            }
        }
        return voList;
    }

    /**
     * 获取个子类别树
     * @param param 入参
     * @return 返回列表
     */
    public List<FieldManagerVODTO> getSubTree(FieldManageListParam param) {
        if(param.getParentId() == null && !StringUtils.isEmpty(param.getFieldType())){
            List<SafetyFieldManageDO> list = safetyFieldManageService.getSubTree(param);
            if(list != null && list.size() > 0){
                param.setParentId(list.get(0).getId());
            }
        }
        List<FieldManagerVODTO> voList = new ArrayList<>();

        List<SafetyFieldManageDO> doList = safetyFieldManageService.getChildList(param);
        if(doList != null && doList.size() >0){
            Optional<SafetyFieldManageDO> doMaxShort = doList.stream().max(Comparator.comparing(SafetyFieldManageDO::getFieldSort));
            Integer maxShort = doMaxShort.get().getFieldSort();
            for(SafetyFieldManageDO fieldManageDO:doList){
                FieldManagerVODTO newVO = new FieldManagerVODTO();
                BeanUtils.copyProperties(fieldManageDO,newVO);
                newVO.setMaxSort(maxShort);
                FieldManageListParam subParam = new FieldManageListParam();
                subParam.setFieldType(param.getFieldType());
                subParam.setParentId(fieldManageDO.getId());
                if(!param.isTopFlag()){
                    List<FieldManagerVODTO> subList = getChileList(subParam);
                    newVO.setRegionList(subList);
                }
                voList.add(newVO);
            }
        }
        return voList;
    }

    public List<SafetyFieldManageDO> fieldChildByFieldType(String filedType) {
        FieldManageListParam fieldManageListParam = new FieldManageListParam();
        fieldManageListParam.setFieldType(filedType);
        return safetyFieldManageService.getChildList(fieldManageListParam);
    }

    /**
     * 获取用户工厂权限列表
     * @param workNo 员工号
     * @return 返回值
     */
    public List<FieldManagerVODTO> selectPowerFactoryList(String workNo){
        List<FieldManagerVODTO> voList = new ArrayList<>();

        String newWorkNo = workNo;
        if(org.apache.commons.lang3.StringUtils.isNotBlank(workNo) && workNo.startsWith("01")){
            newWorkNo = safetySpotCheckTaskHandler.employeeConvertWorkNo(workNo);
        }
        String powFactoryIds = safetySysUserService.getPowerFactory(newWorkNo);

        FieldManageListParam param = new FieldManageListParam();
        param.setFieldType("factory");
        param.setFactoryIds(powFactoryIds);
        List<SafetyFieldManageDO> doList = safetyFieldManageService.getChildList(param);
        if(doList != null && doList.size() >0){
            Optional<SafetyFieldManageDO> doMaxShort = doList.stream().max(Comparator.comparing(SafetyFieldManageDO::getFieldSort));
            Integer maxShort = doMaxShort.get().getFieldSort();
            for(SafetyFieldManageDO fieldManageDO:doList){
                FieldManagerVODTO newVO = new FieldManagerVODTO();
                BeanUtils.copyProperties(fieldManageDO,newVO);
                newVO.setMaxSort(maxShort);
                FieldManageListParam subParam = new FieldManageListParam();
                subParam.setFieldType(param.getFieldType());
                subParam.setParentId(fieldManageDO.getId());
                if(!param.isTopFlag()){
                    List<FieldManagerVODTO> subList = getChileList(subParam);
                    newVO.setRegionList(subList);
                }
                voList.add(newVO);
            }
        }
        return voList;
    }


    /**
     * 获取用户工厂权限列表
     * @param workNo 员工号
     * @return 返回值
     */
    public List<FieldManagerVODTO> selectCardFactoryList(String workNo){
        List<FieldManagerVODTO> voList = new ArrayList<>();

        String newWorkNo = workNo;
        if(org.apache.commons.lang3.StringUtils.isNotBlank(workNo) && workNo.startsWith("01")){
            newWorkNo = safetySpotCheckTaskHandler.employeeConvertWorkNo(workNo);
        }
        List<Integer> factoryIds = safetySysUserService.getFactoryPower(newWorkNo);
        factoryIds.add(0);

        FieldManageListParam param = new FieldManageListParam();
        param.setFieldType("factory");
        param.setFactoryIdList(factoryIds);
        List<SafetyFieldManageDO> doList = safetyFieldManageService.getChildList(param);
        if(doList != null && doList.size() >0){
            Optional<SafetyFieldManageDO> doMaxShort = doList.stream().max(Comparator.comparing(SafetyFieldManageDO::getFieldSort));
            Integer maxShort = doMaxShort.get().getFieldSort();
            for(SafetyFieldManageDO fieldManageDO:doList){
                FieldManagerVODTO newVO = new FieldManagerVODTO();
                BeanUtils.copyProperties(fieldManageDO,newVO);
                newVO.setMaxSort(maxShort);
                FieldManageListParam subParam = new FieldManageListParam();
                subParam.setFieldType(param.getFieldType());
                subParam.setParentId(fieldManageDO.getId());
                if(!param.isTopFlag()){
                    List<FieldManagerVODTO> subList = getChileList(subParam);
                    newVO.setRegionList(subList);
                }
                voList.add(newVO);
            }
        }
        return voList;
    }

    public Object findBuildList(String factoryId){
        if(StringUtils.isBlank(factoryId)){return Collections.emptyList();}
        return safetyFieldManageService.findBuildList(factoryId);
    }

    /**
     * 下拉框
     */
    public VO dropDownBox() {
        List<SafetyFieldManageDO> factoryList = safetyFieldManageService.getInfoByFieldType("buildingAccessFactoryManage");
        List<SafetyFieldManageDO> buildList = safetyFieldManageService.selectBuildList();

        HashMap<String, Object> map = new HashMap<>();
        map.put("factoryList", factoryList);
        map.put("buildList", buildList);
        return VoUtil.successData(map);
    }
}
