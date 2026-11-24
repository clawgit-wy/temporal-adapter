package com.yutong.modules.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.user.convert.SysOrgFactoryConvert;
import com.yutong.modules.user.dto.YTSafetySysOrgFactoryDTO;
import com.yutong.modules.user.dto.YTSafetySysOrgFactoryImportDTO;
import com.yutong.modules.user.entity.SafetyFieldManage;
import com.yutong.modules.user.entity.SafetyGroupInfo;
import com.yutong.modules.user.entity.YTSafetySysOrgFactory;
import com.yutong.modules.user.mapper.SafetyCommonMapper;
import com.yutong.modules.user.mapper.YTSafetySysOrgFactoryMapper;
import com.yutong.modules.user.api.ISafetyCommonService;
import com.yutong.modules.user.api.ISafetyFieldManageService;
import com.yutong.modules.user.api.ISafetyGroupInfoService;
import com.yutong.modules.user.api.IYTSafetySysOrgFactoryService;
import com.yutong.modules.user.vo.SafetyFieldManageVO;
import com.yutong.modules.user.vo.SafetyGroupInfoTreeVO;
import com.yutong.modules.user.vo.YTSafetySysOrgFactoryVO;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 组织架构映射表
 * @Author: jeecg-boot
 * @Date:   2026-05-26
 * @Version: V1.0
 */
@Service
public class YTSafetySysOrgFactoryServiceImpl extends ServiceImpl<YTSafetySysOrgFactoryMapper, YTSafetySysOrgFactory> implements IYTSafetySysOrgFactoryService {

    @Autowired
    private SysOrgFactoryConvert sysOrgFactoryConvert;

    @Autowired
    private ISafetyGroupInfoService safetyGroupInfoService;

    @Autowired
    private ISafetyFieldManageService safetyFieldManageService;

    @Autowired
    private SafetyCommonMapper commonMapper;

    @Autowired
    private ISafetyCommonService safetyCommonService;

    @Override
    public IPage<YTSafetySysOrgFactoryVO> queryPageList(Integer pageNo, Integer pageSize, String orgName, String factoryName) {
        Page<YTSafetySysOrgFactory> page = new Page<>(pageNo, pageSize);
        Page<YTSafetySysOrgFactoryVO> voPageList = baseMapper.selectPageList(page,orgName,factoryName);
        for (YTSafetySysOrgFactoryVO vo : voPageList.getRecords()){
            if (vo.getOrgNamePath() != null){
                vo.setOrgName(BusUtits.reverseCommaStr(vo.getOrgNamePath()));
            }
        }

        return voPageList;
    }

    @Override
    public boolean add(YTSafetySysOrgFactoryDTO dto) {
        //校验组织绑定关系是否存在
        LambdaQueryWrapper<YTSafetySysOrgFactory> safetySysOrgFactoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        safetySysOrgFactoryLambdaQueryWrapper.eq(YTSafetySysOrgFactory::getOrgCode,dto.getOrgCode());
        safetySysOrgFactoryLambdaQueryWrapper.eq(YTSafetySysOrgFactory::getFactoryId,dto.getFactoryId());
        safetySysOrgFactoryLambdaQueryWrapper.eq(YTSafetySysOrgFactory::getDelFlag,0);
        if (this.count(safetySysOrgFactoryLambdaQueryWrapper) > 0){
            throw new IllegalArgumentException("已存在相同组织架构与厂区映射关系，无法新增");
        }
        LambdaQueryWrapper<SafetyGroupInfo> safetyGroupInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        safetyGroupInfoLambdaQueryWrapper.eq(SafetyGroupInfo::getCode, dto.getOrgCode());
        SafetyGroupInfo safetyGroupInfo = safetyGroupInfoService.getOne(safetyGroupInfoLambdaQueryWrapper);
        if (ObjectUtil.isEmpty(safetyGroupInfo)){
            throw new IllegalArgumentException("未找到相关的组织信息");
        }
        SafetyFieldManage safetyFieldManage = safetyFieldManageService.getById(dto.getFactoryId());
        if (ObjectUtil.isEmpty(safetyFieldManage)){
            throw new IllegalArgumentException("未找到相关的厂区信息");
        }
        YTSafetySysOrgFactory ytSafetySysOrgFactory = sysOrgFactoryConvert.dtoToEntity(dto);
        ytSafetySysOrgFactory.setOrgName(safetyGroupInfo.getName());
        ytSafetySysOrgFactory.setOrgCode(safetyGroupInfo.getCode());
        ytSafetySysOrgFactory.setFactoryName(safetyFieldManage.getFieldName());
        ytSafetySysOrgFactory.setFactoryCode(safetyFieldManage.getFieldCode());
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ytSafetySysOrgFactory.setCreateTime(new Date());
        ytSafetySysOrgFactory.setCreateUser(loginUser != null ? loginUser.getRealname() : null);
        ytSafetySysOrgFactory.setUpdateTime(new Date());
        ytSafetySysOrgFactory.setUpdateUser(loginUser != null ? loginUser.getRealname() : null);
        ytSafetySysOrgFactory.setDelFlag(CommonConstant.DEL_FLAG_0);
        ytSafetySysOrgFactory.setVersion(1);
        return this.save(ytSafetySysOrgFactory);
    }

    @Override
    public boolean update(YTSafetySysOrgFactoryDTO dto) {
        if (dto.getId() == null){
            return false;
        }
        //校验组织绑定关系是否存在
        LambdaQueryWrapper<YTSafetySysOrgFactory> safetySysOrgFactoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        safetySysOrgFactoryLambdaQueryWrapper.eq(YTSafetySysOrgFactory::getOrgCode,dto.getOrgCode())
                .eq(YTSafetySysOrgFactory::getFactoryId,dto.getFactoryId())
                .ne(YTSafetySysOrgFactory::getId,dto.getId());
        if (this.count(safetySysOrgFactoryLambdaQueryWrapper) > 0){
            throw new IllegalArgumentException("已存在相同组织架构与厂区映射关系，无法修改");
        }
        LambdaQueryWrapper<SafetyGroupInfo> safetyGroupInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        safetyGroupInfoLambdaQueryWrapper.eq(SafetyGroupInfo::getCode, dto.getOrgCode());
        SafetyGroupInfo safetyGroupInfo = safetyGroupInfoService.getOne(safetyGroupInfoLambdaQueryWrapper);
        if (ObjectUtil.isEmpty(safetyGroupInfo)){
            throw new IllegalArgumentException("未找到相关的组织信息");
        }
        SafetyFieldManage safetyFieldManage = safetyFieldManageService.getById(dto.getFactoryId());
        if (ObjectUtil.isEmpty(safetyFieldManage)){
            throw new IllegalArgumentException("未找到相关的厂区信息");
        }
        YTSafetySysOrgFactory ytSafetySysOrgFactory = this.getById(dto.getId());
        if (BeanUtil.isEmpty(ytSafetySysOrgFactory)){
            throw new IllegalArgumentException("主键数据未找到");
        }
        ytSafetySysOrgFactory.setOrgName(safetyGroupInfo.getName());
        ytSafetySysOrgFactory.setOrgCode(safetyGroupInfo.getCode());
        ytSafetySysOrgFactory.setFactoryId(safetyFieldManage.getId());
        ytSafetySysOrgFactory.setFactoryName(safetyFieldManage.getFieldName());
        ytSafetySysOrgFactory.setFactoryCode(safetyFieldManage.getFieldCode());
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ytSafetySysOrgFactory.setUpdateTime(new Date());
        ytSafetySysOrgFactory.setUpdateUser(loginUser != null ? loginUser.getRealname() : null);
        return this.updateById(ytSafetySysOrgFactory);
    }

    @Override
    public boolean delete(Long id) {
        YTSafetySysOrgFactory ytSafetySysOrgFactory = this.getById(id);
        if (BeanUtil.isEmpty(ytSafetySysOrgFactory)){
            return false;
        }
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ytSafetySysOrgFactory.setUpdateUser(loginUser != null ? loginUser.getRealname() : null);
        this.updateById(ytSafetySysOrgFactory);
        return this.removeById(id);
    }

    @Override
    public YTSafetySysOrgFactoryVO getDetails(Long id) {
        YTSafetySysOrgFactoryVO orgFactoryVO = new YTSafetySysOrgFactoryVO();
        if (id == null){
            return orgFactoryVO;
        }
        YTSafetySysOrgFactory ytSafetySysOrgFactory = this.getById(id);
        if (ytSafetySysOrgFactory != null){
            orgFactoryVO = sysOrgFactoryConvert.entityToVo(ytSafetySysOrgFactory);
        }
        return orgFactoryVO;
    }

    @Override
    public List<YTSafetySysOrgFactoryVO> queryAllList(String orgName, String factoryName) {
        LambdaQueryWrapper<YTSafetySysOrgFactory> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(YTSafetySysOrgFactory::getDelFlag,CommonConstant.DEL_FLAG_0)
                .like(ObjectUtil.isNotNull(orgName),YTSafetySysOrgFactory::getOrgName,orgName)
                .like(ObjectUtil.isNotNull(factoryName),YTSafetySysOrgFactory::getFactoryName,factoryName)
                .orderByDesc(YTSafetySysOrgFactory::getUpdateTime);
        List<YTSafetySysOrgFactory> list = this.list(lambdaQueryWrapper);
        List<YTSafetySysOrgFactoryVO> voList = convertToVoList(list);
        return voList;
    }

    /**
     * 将 Entity 转换为 VO 并统计用户数量
     */
    private List<YTSafetySysOrgFactoryVO> convertToVoList(List<YTSafetySysOrgFactory> entities) {
        return sysOrgFactoryConvert.entityListToVoList(entities);
    }


    @Override
    public List<YTSafetySysOrgFactoryImportDTO> importSysData(List<YTSafetySysOrgFactoryImportDTO> importList) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<YTSafetySysOrgFactoryImportDTO> msgVo = new ArrayList<YTSafetySysOrgFactoryImportDTO>();
        List<YTSafetySysOrgFactory> saveList = new ArrayList<>();

        List<SafetyFieldManageVO> factorys = commonMapper.queryFactoryDrop();
        Map<String, SafetyFieldManageVO> factoryMap = factorys.stream()
                .collect(Collectors.toMap(SafetyFieldManageVO::getFieldName, f -> f, (a, b) -> a));

        Map<String, SafetyGroupInfoTreeVO> groupInfoMap = safetyCommonService.queryGroupInfoFactoryMap();


        //校验数据库是否已存在
        // 提取表格中不重复的厂区编码
        List<String> orgNamePathList = importList.stream()
                .map(YTSafetySysOrgFactoryImportDTO::getOrgName)
                .distinct()
                .collect(Collectors.toList());

        Set<String> orgNames = new HashSet<>();
        orgNamePathList.forEach(orgNamePath->{
            SafetyGroupInfoTreeVO safetyGroupInfoPathVO = groupInfoMap.get(orgNamePath);
            if (safetyGroupInfoPathVO != null){
                orgNames.add(safetyGroupInfoPathVO.getName());
            }
        });

        // 查询数据库中已存在的组织编码+厂区组合
        Set<String> existingOrgNameSet = new HashSet<>();
        if (!orgNames.isEmpty()){
            LambdaQueryWrapper<YTSafetySysOrgFactory> existWrapper = new LambdaQueryWrapper<>();
            existWrapper.eq(YTSafetySysOrgFactory::getDelFlag, 0)
                    .in(YTSafetySysOrgFactory::getOrgName, orgNames);
            List<YTSafetySysOrgFactory> existingList = this.list(existWrapper);
            existingOrgNameSet = existingList.stream()
                    .map(entity -> entity.getOrgCode() + "|" + entity.getFactoryName())
                    .collect(Collectors.toSet());
        }

        // 统计导入文件中各组织+厂区组合出现次数
        Map<String, Long> orgFactoryCountMap = importList.stream()
                .filter(item -> item.getOrgName() != null && !item.getOrgName().trim().isEmpty()
                        && item.getFactoryName() != null && !item.getFactoryName().trim().isEmpty())
                .collect(Collectors.groupingBy(
                        item -> item.getOrgName() + "|" + item.getFactoryName(),
                        Collectors.counting()));

        for (YTSafetySysOrgFactoryImportDTO item : importList) {
            if (BeanUtil.isEmpty(item)){
                continue;
            }

            // 校验导入文件内的重复组织+厂区组合：出现大于1次的都不导入
            String orgFactoryKey = item.getOrgName() + "|" + (item.getFactoryName() != null ? item.getFactoryName() : "");
            long orgFactoryCount = orgFactoryCountMap.getOrDefault(orgFactoryKey, 0L);
            if (orgFactoryCount > 1) {
                item.setErrorMsg("组织架构与厂区在文件中重复");
                msgVo.add(item);
                continue;
            }

            SafetyGroupInfoTreeVO safetyGroupInfo = groupInfoMap.get(item.getOrgName());
            if (safetyGroupInfo == null){
                item.setErrorMsg("未找到组织信息");
                msgVo.add(item);
                continue;
            }

            SafetyFieldManageVO fieldManage = factoryMap.get(item.getFactoryName());
            if (fieldManage == null) {
                item.setErrorMsg("未找到厂区信息");
                msgVo.add(item);
                continue;
            }

            // 校验数据库中已存在的组织+厂区绑定关系
            if (existingOrgNameSet.contains(safetyGroupInfo.getCode() + "|" + fieldManage.getFieldName())) {
                item.setErrorMsg("已存在相同组织架构与厂区映射关系");
                msgVo.add(item);
                continue;
            }

            YTSafetySysOrgFactory entity = new YTSafetySysOrgFactory();

            entity.setOrgName(safetyGroupInfo.getName());
            entity.setOrgCode(safetyGroupInfo.getCode());
            entity.setFactoryId(fieldManage.getId());
            entity.setFactoryName(fieldManage.getFieldName());
            entity.setFactoryCode(fieldManage.getFieldCode());
            entity.setCreateUser(loginUser != null ? loginUser.getRealname() : null);
            entity.setCreateTime(new Date());
            entity.setUpdateUser(loginUser != null ? loginUser.getRealname() : null);
            entity.setUpdateTime(new Date());
            entity.setDelFlag(0);
            entity.setVersion(1);
            saveList.add(entity);
        }
        if (!saveList.isEmpty()) {
            this.saveBatch(saveList);
        }
        return msgVo;
    }
}
