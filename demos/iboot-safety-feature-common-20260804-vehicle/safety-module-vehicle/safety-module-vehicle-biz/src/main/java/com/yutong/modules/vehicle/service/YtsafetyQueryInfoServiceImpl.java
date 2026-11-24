package com.yutong.modules.vehicle.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.vehicle.api.YtsafetyQueryInfoService;
import com.yutong.modules.vehicle.dto.AccessRecordDTO;
import com.yutong.modules.vehicle.dto.YtsafetyQueryPermInfoDTO;
import com.yutong.modules.vehicle.dto.YtsafetyQueryPermItemDTO;
import com.yutong.modules.vehicle.entity.YtsafetyQueryPermInfo;
import com.yutong.modules.vehicle.enums.CustomConstant;
import com.yutong.modules.vehicle.mapper.YtsafetyQueryPermInfoMapper;
import com.yutong.modules.vehicle.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class YtsafetyQueryInfoServiceImpl extends ServiceImpl<YtsafetyQueryPermInfoMapper, YtsafetyQueryPermInfo> implements YtsafetyQueryInfoService {

    public static final int FAIL_CODE = 1;

    @Resource
    YtsafetyQueryPermInfoMapper ytsafetyQueryPermInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdatePerm(YtsafetyQueryPermInfoDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getRoleCode())) {
            return;
        }
        String roleCode = dto.getRoleCode();
        // 先删除该角色原有的所有权限配置
        LambdaQueryWrapper<YtsafetyQueryPermInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(YtsafetyQueryPermInfo::getRoleCode, roleCode);
        remove(queryWrapper);

        // 批量新增新的权限配置
        List<YtsafetyQueryPermItemDTO> permList = dto.getPermList();
        if (permList != null && !permList.isEmpty()) {
            List<YtsafetyQueryPermInfo> entityList = new ArrayList<>();
            for (YtsafetyQueryPermItemDTO item : permList) {
                YtsafetyQueryPermInfo entity = new YtsafetyQueryPermInfo();
                entity.setRoleCode(roleCode);
                entity.setRoleName(dto.getRoleName());
                entity.setPermCategory(item.getPermCategory());
                entity.setPermType(item.getPermType());
                entity.setQueryDays(item.getQueryDays());
                // List<String> 转逗号分隔字符串
                List<String> types = item.getQueryableTypes();
                entity.setQueryableTypes(types != null && !types.isEmpty()
                        ? String.join(",", types)
                        : null);
                entityList.add(entity);
            }
            saveBatch(entityList);
        }
    }

    @Override
    public List<YtsafetyQueryPermGroupVO> listGroupByRole(String roleCode) {
        LambdaQueryWrapper<YtsafetyQueryPermInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(roleCode), YtsafetyQueryPermInfo::getRoleCode, roleCode);
        List<YtsafetyQueryPermInfo> list = list(queryWrapper);

        // 按 roleCode 分组，保持插入顺序
        Map<String, List<YtsafetyQueryPermInfo>> grouped = list.stream()
                .collect(Collectors.groupingBy(YtsafetyQueryPermInfo::getRoleCode, LinkedHashMap::new, Collectors.toList()));

        List<YtsafetyQueryPermGroupVO> result = new ArrayList<>();
        for (Map.Entry<String, List<YtsafetyQueryPermInfo>> entry : grouped.entrySet()) {
            List<YtsafetyQueryPermInfo> items = entry.getValue();
            YtsafetyQueryPermInfo first = items.get(0);

            List<YtsafetyQueryPermItemVO> permList = items.stream().map(item -> {
                YtsafetyQueryPermItemVO vo = new YtsafetyQueryPermItemVO();
                vo.setPermCategory(item.getPermCategory());
                vo.setPermType(item.getPermType());
                vo.setQueryDays(item.getQueryDays());
                vo.setQueryableTypes(StringUtils.hasText(item.getQueryableTypes())
                        ? Arrays.asList(item.getQueryableTypes().split(","))
                        : new ArrayList<>());
                return vo;
            }).collect(Collectors.toList());

            YtsafetyQueryPermGroupVO groupVO = new YtsafetyQueryPermGroupVO();
            groupVO.setRoleCode(first.getRoleCode());
            groupVO.setRoleName(first.getRoleName());
            groupVO.setPermList(permList);
            result.add(groupVO);
        }
        return result;
    }

    @Override
    public PersonAndCarVO getPersonAndCarList(String KeyWord) {
        PersonAndCarVO personAndCarVO = new PersonAndCarVO();
        if(StringUtils.hasText(KeyWord)){
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            List<String> roleCodes = ytsafetyQueryPermInfoMapper.getUserRoleCode(String.valueOf(sysUser.getUserId()));
            if(!roleCodes.isEmpty()){
                LambdaQueryWrapper<YtsafetyQueryPermInfo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.in(YtsafetyQueryPermInfo::getRoleCode, roleCodes);
                if(KeyWord.length()==8){//员工号查询
                    queryWrapper.eq(YtsafetyQueryPermInfo::getPermCategory, CustomConstant.authType.person_info);
                    List<YtsafetyQueryPermInfo> permList = list(queryWrapper);
                    YtsafetyQueryPermInfo personInfo = permList.stream()
                            .filter(p -> CustomConstant.authType.person_info.equals(p.getPermCategory()))
                            .findFirst()
                            .orElse(null);
                    Integer permType = personInfo.getPermType();
                    if(CustomConstant.permType.allAuth.equals(permType)){
                        SafetyEmployeeMessageVO employeeInfo = ytsafetyQueryPermInfoMapper.getEmployeeInfo(KeyWord, null);
                        List<CarInfoVO> employeeCarList = ytsafetyQueryPermInfoMapper.getEmployeeCarList(KeyWord, null);
                        personAndCarVO.setSafetyEmployeeMessageVO(employeeInfo);
                        personAndCarVO.setVehicleListVO(employeeCarList);
                    }else if(CustomConstant.permType.custAuth.equals(permType)){
                         String queryableTypes = personInfo.getQueryableTypes();
                         
                    }
                    List<CarInfoVO> empNoCarList = ytsafetyQueryPermInfoMapper.getEmployeeCarList(KeyWord,null);
                }else if(KeyWord.length()==11){//手机号查询

                }else if(KeyWord.length()==18){//身份证号查询

                }else{//车牌号查询

                }
            }
        }
        return personAndCarVO;
    }

    @Override
    public List<PersonnelAccessVO> gtePersonnelInOutHistory(AccessRecordDTO accessRecordDTO) {
        if(StringUtils.hasText(accessRecordDTO.getEmpNo())){
            return ytsafetyQueryPermInfoMapper.getPersonnelAccess(accessRecordDTO.getEmpNo());
        }else if(StringUtils.hasText(accessRecordDTO.getPhoneNo())){

        }else{

        }

        return Collections.emptyList();
    }

    @Override
    public List<CarAccessVO> gteCarAccessList(AccessRecordDTO accessRecordDTO) {
        if(StringUtils.hasText(accessRecordDTO.getEmpNo())){
            return ytsafetyQueryPermInfoMapper.getCarAccessList(accessRecordDTO.getCarNo());
        }else if(StringUtils.hasText(accessRecordDTO.getPhoneNo())){

        }else{

        }
        return Collections.emptyList();
    }


}
