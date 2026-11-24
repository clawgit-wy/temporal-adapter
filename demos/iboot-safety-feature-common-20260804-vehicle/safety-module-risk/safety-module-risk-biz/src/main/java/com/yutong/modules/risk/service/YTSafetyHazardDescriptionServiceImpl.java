package com.yutong.modules.risk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.common.api.FileService;
import com.yutong.modules.common.entity.YTSafetyFileInfo;
import com.yutong.modules.risk.api.IYTSafetyHazardDescriptionService;
import com.yutong.modules.risk.convert.SafetyHazardDescriptionConvert;
import com.yutong.modules.risk.dto.YTSafetyHazardDescriptionDTO;
import com.yutong.modules.risk.entity.YTSafetyHazardDescription;
import com.yutong.modules.risk.mapper.YTSafetyHazardDescriptionMapper;
import com.yutong.modules.risk.vo.YTSafetyHazardDescriptionVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class YTSafetyHazardDescriptionServiceImpl extends ServiceImpl<YTSafetyHazardDescriptionMapper, YTSafetyHazardDescription> implements IYTSafetyHazardDescriptionService {

    @Autowired
    private SafetyHazardDescriptionConvert safetyHazardDescriptionConvert;

    @Autowired
    private YTSafetyHazardDescriptionMapper ytSafetyHazardDescriptionMapper;

    @Autowired
    private FileService fileService;

    @Override
    public void clearBYHazardReportId(Long hazardReportId) {
        if (hazardReportId == null){
            return;
        }
        LambdaQueryWrapper<YTSafetyHazardDescription> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(YTSafetyHazardDescription::getProcessId,hazardReportId);
        remove(queryWrapper);
    }

    @Override
    public void batchAdd(Long reportId,List<YTSafetyHazardDescriptionDTO> descriptionDTOList) {
        List<YTSafetyHazardDescription> descriptions = safetyHazardDescriptionConvert.dtoListToEntityList(descriptionDTOList);
        List<YTSafetyHazardDescription> descriptionList = descriptions.stream().map(d -> {
            d.setProcessId(reportId);
            return d;
        }).collect(Collectors.toList());
        saveBatch(descriptionList);
    }

    @Override
    public List<YTSafetyHazardDescriptionVO> listBYHazardReportId(Long id) {
        List<YTSafetyHazardDescriptionVO> descriptionVOList = ytSafetyHazardDescriptionMapper.listBYHazardReportId(id);
        for (YTSafetyHazardDescriptionVO descriptionVO : descriptionVOList){
            String attachmentId = descriptionVO.getAttachmentIds();
            if(StringUtils.isNotBlank(attachmentId)){
                List<String> fileIdList = Arrays.stream(attachmentId.split(","))
                        .map(String::trim)
                        .filter(StringUtils::isNotEmpty)
                        .collect(Collectors.toList());
                List<YTSafetyFileInfo> list = fileService.listByIds(fileIdList);
                descriptionVO.setAttachmentList( list);
            }
        }
        return descriptionVOList;
    }

    @Override
    public List<YTSafetyHazardDescriptionVO> listByReportIds(List<Long> reportIds) {
        if (reportIds == null || reportIds.isEmpty()) {
            return Collections.emptyList();
        }
        return ytSafetyHazardDescriptionMapper.listByReportIds(reportIds);
    }
}
