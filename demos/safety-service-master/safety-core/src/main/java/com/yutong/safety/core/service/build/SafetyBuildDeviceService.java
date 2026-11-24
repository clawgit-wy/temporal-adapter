package com.yutong.safety.core.service.build;

import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceDOExample;
import com.yutong.safety.core.db.entity.build.param.BuildDeviceListParam;
import com.yutong.safety.core.db.entity.build.vo.BuildDeviceVO;
import com.yutong.safety.core.db.mapper.build.SafetyBuildDeviceDOBizMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class SafetyBuildDeviceService {

    @Autowired
    private SafetyBuildDeviceDOBizMapper safetyBuildDeviceDOMapper;

    public int updateByPrimaryKeySelective(SafetyBuildDeviceDO deviceDO){
        return safetyBuildDeviceDOMapper.updateByPrimaryKeySelective(deviceDO);
    }

    public int updateBySelective(SafetyBuildDeviceDO record,BuildDeviceListParam param){
        param.setPageFlag(null);
        SafetyBuildDeviceDOExample example = formatExample(param);
        return safetyBuildDeviceDOMapper.updateByExampleSelective(record,example);
    }

    /**
     *
     * @param param 入参
     * @return 返回
     */
    public List<SafetyBuildDeviceDO> selectByExample(BuildDeviceListParam param) {
        param.setPageFlag(null);
        SafetyBuildDeviceDOExample example = formatExample(param);
        return safetyBuildDeviceDOMapper.selectByExample(example);
    }

    /**
     *
     * @param param 入参
     * @return 返回
     */
    public List<BuildDeviceVO> selectList(BuildDeviceListParam param) {
        param.setPageFlag(null);
        SafetyBuildDeviceDOExample example = formatExample(param);
        return safetyBuildDeviceDOMapper.selectByExampleBiz(example);
    }

    /**
     *
     * @param param 入参
     * @return 返回
     */
    public List<BuildDeviceVO> selectPageList(BuildDeviceListParam param) {
        param.setPageFlag(1);
        SafetyBuildDeviceDOExample example = formatExample(param);
        return safetyBuildDeviceDOMapper.selectByExampleBiz(example);
    }

    /**
     * 分页总条数
     * @param param 查询入参
     * @return 返回
     */
    public long selectListCount(BuildDeviceListParam param) {
        SafetyBuildDeviceDOExample example = formatExample(param);
        return safetyBuildDeviceDOMapper.countByExample(example);
    }

    public List<BuildDeviceVO> selectPageListTwo(BuildDeviceListParam param) {
        param.setPageFlag(1);
        return safetyBuildDeviceDOMapper.selectByExampleBizTwo(param);
    }

    /**
     * 分页总条数
     * @param param 查询入参
     * @return 返回
     */
    public long selectListCountTwo(BuildDeviceListParam param) {
        return safetyBuildDeviceDOMapper.countByExampleBizTwo(param);
    }

    /**
     * 根据ID获取详情
     * @param id 主键ID
     * @return 接收结构
     */
    public SafetyBuildDeviceDO selectByPrimaryKey(String id){
        return safetyBuildDeviceDOMapper.selectByPrimaryKey(id);
    }

    /**
     * 参数整理
     * @param param 入参
     * @return example
     */
    private SafetyBuildDeviceDOExample formatExample(BuildDeviceListParam param){
        SafetyBuildDeviceDOExample example = new SafetyBuildDeviceDOExample();
        SafetyBuildDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andRemoveFlagEqualTo("0"); //未移除的设备
        criteria.andDeviceStatusEqualTo("1"); //启用状态
        if(StringUtils.isNotBlank(param.getFactoryCode())){
            criteria.andAnFactoryCodeEqualTo(param.getFactoryCode());
        }
        if(!CollectionUtils.isEmpty(param.getFactoryList())){
            criteria.andAnFactoryCodeIn(param.getFactoryList());
        }
        if(StringUtils.isNotBlank(param.getDeviceName())){
            criteria.andDeviceNameLike('%'+param.getDeviceName()+"%");
        }
        if(StringUtils.isNotBlank(param.getRemoveFlag())){
            criteria.andRemoveFlagEqualTo(param.getRemoveFlag());
        }
        if(StringUtils.isNotBlank(param.getSecurityLevel())){
            criteria.andSecurityLevelEqualTo(param.getSecurityLevel());
        }
        if(StringUtils.isNotBlank(param.getDeviceStatus())){
            criteria.andDeviceStatusEqualTo(param.getDeviceStatus());
        }
        if(StringUtils.isNotBlank(param.getExtendId())){
            criteria.andDeviceCodeEqualTo(param.getExtendId());
        }
        if("1".equals(param.getTopSecretFlag())){
            criteria.andTopSecretEmpLoginIsNotNull();
        }
        if(StringUtils.isNotBlank(param.getIsPublic())){
            criteria.andIsPublicEqualTo(param.getIsPublic());
        }
        if(StringUtils.isNotBlank(param.getBuilding())){
            criteria.andAnBuildingCodeEqualTo(param.getBuilding());
        }
        if(!CollectionUtils.isEmpty(param.getDeviceCodeList())){
            criteria.andDeviceCodeIn(param.getDeviceCodeList());
        }
        if (StringUtils.isNotBlank(param.getDeviceCode())) {
            criteria.andDeviceCodeEqualTo(param.getDeviceCode());
        }
        if(StringUtils.isNotBlank(param.getCreateBegin())){
            Date cdate = DateUtils.parse(param.getCreateBegin(),2);
            criteria.andCreateTimeGreaterThanOrEqualTo(cdate);
        }
        if(StringUtils.isNotBlank(param.getUpdateBegin())){
            Date cdate = DateUtils.parse(param.getUpdateBegin(),1);
            criteria.andUpdateTimeLessThan(cdate);
        }
        if(StringUtils.isNotBlank(param.getDeviceType())){
            criteria.andDeviceTypeEqualTo(param.getDeviceType());
        }
        if(StringUtils.isNotBlank(param.getSecurityLevelList())){
            criteria.andSecurityLevelIn(Arrays.asList(param.getSecurityLevelList().split(",")));
        }
        example.setOrderByClause(" create_time desc, update_time asc ");
        if(param.getPageFlag() != null){
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
            if(StringUtils.isNotBlank(param.getOrderByClause())){
                example.setOrderByClause(param.getOrderByClause());
            }
        }
        return example;
    }

    /**
     * 批量更新
     * @param deviceDOS 入参集合
     * @return 返回值
     */
    public int batchUpdate(List<SafetyBuildDeviceDO> deviceDOS){
        if(CollectionUtils.isEmpty(deviceDOS)){
            return 0;
        }
        return safetyBuildDeviceDOMapper.batchUpdate(deviceDOS);
    }

    public List<SafetyBuildDeviceDO> selectList(List<String> codeList){
        SafetyBuildDeviceDOExample example = new SafetyBuildDeviceDOExample();
        SafetyBuildDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeviceCodeIn(codeList).
                andRemoveFlagEqualTo("0")
                .andDeviceStatusEqualTo("1");

        return safetyBuildDeviceDOMapper.selectByExample(example);
    }

    public List<SafetyBuildDeviceDO> selectBuildList(List<String> codeList){
        SafetyBuildDeviceDOExample example = new SafetyBuildDeviceDOExample();
        SafetyBuildDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andAnBuildingCodeIn(codeList)
                .andRemoveFlagEqualTo("0")
                .andDeviceStatusEqualTo("1")
                .andIsPublicEqualTo("1");

        return safetyBuildDeviceDOMapper.selectByExample(example);
    }

    public List<SafetyBuildDeviceDO> selectByRemoveAndDisableList() {
        return safetyBuildDeviceDOMapper.selectByRemoveAndDisableList();
    }

    public Object getCopyDeviceInfo(String deviceNameOrCode) {
        SafetyBuildDeviceDOExample example = new SafetyBuildDeviceDOExample();
        SafetyBuildDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andRemoveFlagEqualTo("0"); //未移除的设备
        criteria.andDeviceStatusEqualTo("1"); //启用状态
        if (StringUtils.isNotBlank(deviceNameOrCode)) {
            criteria.andAnDeviceNameAndDeviceCodeLike(deviceNameOrCode);
        }
        example.setLimit(50);
        example.setOffset(0);
        return safetyBuildDeviceDOMapper.selectByExample(example);
    }

    public List<SafetyBuildDeviceDO> selectListAll(List<String> codeList){
        SafetyBuildDeviceDOExample example = new SafetyBuildDeviceDOExample();
        SafetyBuildDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeviceCodeIn(codeList);

        return safetyBuildDeviceDOMapper.selectByExample(example);
    }

    /**
     * 标记删除
     * @param deviceCodeList 门禁编码
     * @return 三处的数量
     */
    public int updateBySelective(List<String> deviceCodeList){
        SafetyBuildDeviceDO record = new SafetyBuildDeviceDO();
        record.setIsDelete("1");
        record.setRemoveFlag("1");
        record.setAnRemark("出入口数据同步删除");
        SafetyBuildDeviceDOExample example = new SafetyBuildDeviceDOExample();
        SafetyBuildDeviceDOExample.Criteria criteria = example.createCriteria();
        criteria.andDeviceCodeIn(deviceCodeList);
        criteria.andIsDeleteEqualTo("0");
        return safetyBuildDeviceDOMapper.updateByExampleSelective(record,example);
    }

}
