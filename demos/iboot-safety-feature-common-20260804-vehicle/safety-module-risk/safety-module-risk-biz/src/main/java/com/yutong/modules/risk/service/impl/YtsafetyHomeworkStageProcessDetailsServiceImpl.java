package com.yutong.modules.risk.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.common.api.IBpmProcessService;
import com.yutong.modules.common.utils.PinyinUtil;
import com.yutong.modules.risk.dto.YtsafetyHomeworkStageProcessDTO;
import com.yutong.modules.risk.dto.YtsafetyHomeworkStageProcessDetailsDTO;
import com.yutong.modules.risk.entity.YtsafetyHomeworkStageProcessDetails;
import com.yutong.modules.risk.mapper.YtsafetyHomeworkStageProcessDetailsMapper;
import com.yutong.modules.risk.service.IYtsafetyHomeworkStageProcessDetailsService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import com.yutong.modules.risk.vo.YtsafetyHomeworkStageDetailsExportVO;
import org.apache.commons.beanutils.BeanUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 作业环节变更流程明细表
 * @Author: jeecg-boot
 * @Date:   2026-07-01
 * @Version: V1.0
 */
@Log4j2
@Service
public class YtsafetyHomeworkStageProcessDetailsServiceImpl extends ServiceImpl<YtsafetyHomeworkStageProcessDetailsMapper, YtsafetyHomeworkStageProcessDetails> implements IYtsafetyHomeworkStageProcessDetailsService {

    @Autowired
    private YtsafetyHomeworkStageProcessDetailsMapper processDetailsMapper;

    @Autowired
    private IBpmProcessService bpmProcessService;

    @Override
    public IPage<YtsafetyHomeworkStageProcessDetails> queryPageList(Page<YtsafetyHomeworkStageProcessDetails> page, YtsafetyHomeworkStageProcessDTO query) {
        return null;
    }

    @SneakyThrows
    @Override
    public void add(YtsafetyHomeworkStageProcessDTO dto) {
        LoginUser loginUser = BusUtits.getLoginUser();
        if(CollectionUtil.isNotEmpty(dto.getDetails())){
            List<YtsafetyHomeworkStageProcessDetailsDTO> detailsDTOList = dto.getDetails();
            List<YtsafetyHomeworkStageProcessDetails> detailsList = new ArrayList<>();
            BeanUtils.copyProperties(detailsList,detailsDTOList);
            detailsList.stream().forEach(details ->{
                setUpdateData(details, loginUser);
                setAddData(details, loginUser);
                details.setId(IdWorker.getId());
                String areaCode = PinyinUtil.getFirstLetter(details.getAreaName());
                String linkName = PinyinUtil.getFirstLetterAndInt(details.getLinkName());
                String linkCode = "ZYHJ" + details.getFactoryCode()+ "-" + areaCode + "-"+linkName;
                details.setLinkCode(linkCode);
            });
            saveBatch(detailsList);
        }
    }

    @Override
    public void edit(YtsafetyHomeworkStageProcessDTO dto) {
        YtsafetyHomeworkStageProcessDetails details = BeanUtil.toBean(dto, YtsafetyHomeworkStageProcessDetails.class);
        LoginUser loginUser = BusUtits.getLoginUser();
        setUpdateData(details, loginUser);
    }


    /**
     * 封装修改字段
     * @param details
     * @param loginUser
     */
    private void setUpdateData(YtsafetyHomeworkStageProcessDetails details, LoginUser loginUser) {
        details.setUpdateUser(loginUser.getEmpNo());
        details.setUpdateTime(new Date());
        details.setUpdateName(loginUser.getRealname());
    }
    /**
     * 封装新增字段
     * @param details
     * @param loginUser
     */
    private void setAddData(YtsafetyHomeworkStageProcessDetails details, LoginUser loginUser) {
        details.setCreateUser(loginUser.getEmpNo());
        details.setCreateTime(new Date());
        details.setCreateName(loginUser.getRealname());
    }

    @Override
    public void deleteByProcessId(Long processId) {
        processDetailsMapper.deleteByProcessId(processId);
    }

    @Override
    public List<YtsafetyHomeworkStageDetailsExportVO> queryExportList(Long processId) {
        List<YtsafetyHomeworkStageProcessDetails> detailsList = list(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<YtsafetyHomeworkStageProcessDetails>()
                        .eq(YtsafetyHomeworkStageProcessDetails::getProcessId, processId)
        );
        return detailsList.stream().map(details -> {
            YtsafetyHomeworkStageDetailsExportVO vo = new YtsafetyHomeworkStageDetailsExportVO();
            vo.setLinkName(details.getLinkName());
            vo.setLinkCode(details.getLinkCode());
            vo.setFactoryName(details.getFactoryName());
            vo.setAreaName(details.getAreaName());
            vo.setOrgName(details.getOrgName());
            vo.setLocationNames(details.getLocationNames());
            vo.setSafetyStaff(details.getSafetyStaff());
            vo.setSafetyManager(details.getSafetyManager());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<YtsafetyHomeworkStageProcessDetails> queryListByChangeId(Long changeId) {
        List<YtsafetyHomeworkStageProcessDetails> detailsList = list(
                new LambdaQueryWrapper<YtsafetyHomeworkStageProcessDetails>()
                        .eq(YtsafetyHomeworkStageProcessDetails::getChangeId, changeId)
        );
        return detailsList;
    }

    @Override
    public List<YtsafetyHomeworkStageProcessDetails> queryDetailsList(Long processId) {
        List<YtsafetyHomeworkStageProcessDetails> detailsList = list(
                new LambdaQueryWrapper<YtsafetyHomeworkStageProcessDetails>().eq(YtsafetyHomeworkStageProcessDetails::getProcessId, processId)
        );
        return detailsList;
    }

}
