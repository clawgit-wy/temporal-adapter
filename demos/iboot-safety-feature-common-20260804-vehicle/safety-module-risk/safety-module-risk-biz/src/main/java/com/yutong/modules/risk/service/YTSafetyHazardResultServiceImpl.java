package com.yutong.modules.risk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yutong.modules.common.api.FileService;
import com.yutong.modules.common.entity.YTSafetyFileInfo;
import com.yutong.modules.risk.api.IYTSafetyHazardResultService;
import com.yutong.modules.risk.convert.SafetyHazardResultConvert;
import com.yutong.modules.risk.dto.YTSafetyHazardResultDTO;
import com.yutong.modules.risk.entity.YTSafetyHazardResult;
import com.yutong.modules.risk.mapper.YTSafetyHazardResultMapper;
import com.yutong.modules.risk.vo.YTSafetyHazardResultVO;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.BusUtits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class YTSafetyHazardResultServiceImpl extends ServiceImpl<YTSafetyHazardResultMapper, YTSafetyHazardResult> implements IYTSafetyHazardResultService {

    @Autowired
    private SafetyHazardResultConvert safetyHazardResultConvert;

    @Autowired
    private YTSafetyHazardResultMapper safetyHazardResultMapper;

    @Autowired
    private FileService fileService;

    @Override
    public void clearBYHazardReportId(Long hazardReportId) {
        if (hazardReportId == null){
            return;
        }
        LambdaQueryWrapper<YTSafetyHazardResult> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(YTSafetyHazardResult::getProcessId,hazardReportId);
        remove(queryWrapper);
    }

    @Override
    public void batchAdd(Long reportId, List<YTSafetyHazardResultDTO> resultDTOList) {
        LoginUser user =  BusUtits.getLoginUser();
        List<YTSafetyHazardResult> results = safetyHazardResultConvert.dtoListToEntityList(resultDTOList);
        List<YTSafetyHazardResult> resultList = results.stream().map(r -> {
            r.setProcessId(reportId);
            r.setHandlerUserEmpNo(user.getEmpNo());
            r.setHandleCompleteTime(new Date());
            return r;
        }).collect(Collectors.toList());
        saveBatch(resultList);
    }

    @Override
    public List<YTSafetyHazardResultVO> listBYHazardReportId(Long id) {
        List<YTSafetyHazardResultVO> hazardResultVOS = safetyHazardResultMapper.listBYHazardReportId(id);
        for (YTSafetyHazardResultVO resultVO : hazardResultVOS){
            String attachmentId = resultVO.getAttachmentIds();
            if(StringUtils.isNotBlank(attachmentId)){
                List<String> fileIdList = Arrays.stream(attachmentId.split(","))
                        .map(String::trim)
                        .filter(StringUtils::isNotEmpty)
                        .collect(Collectors.toList());
                List<YTSafetyFileInfo> list = fileService.listByIds(fileIdList);
                resultVO.setAttachmentList( list);
            }
        }
        return hazardResultVOS;
    }

    @Override
    public List<YTSafetyHazardResultVO> listByReportIds(List<Long> reportIds) {
        if (reportIds == null || reportIds.isEmpty()) {
            return Collections.emptyList();
        }
        return safetyHazardResultMapper.listByReportIds(reportIds);
    }

    @Override
    public YTSafetyHazardResult getNewestByProcessId(Long processId) {
        return safetyHazardResultMapper.getNewestByProcessId(processId);
    }
}
