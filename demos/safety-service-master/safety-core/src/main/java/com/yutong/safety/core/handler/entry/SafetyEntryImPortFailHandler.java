package com.yutong.safety.core.handler.entry;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.core.db.entity.entry.SafetyEntryBlankProcessDetailDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryImportFailDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryImportFailDOExample;
import com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryImportFailAddParams;
import com.yutong.safety.core.db.entity.entry.param.SafetyEntryImportFailQueryParams;
import com.yutong.safety.core.dto.BlackMessageDto;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import com.yutong.safety.core.service.SafetyBlackMessageService;
import com.yutong.safety.core.service.entry.SafetyEntryImportFailService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SafetyEntryImPortFailHandler extends AbstractSafetyWebBaseHandler<SafetyEntryImportFailDO, SafetyEntryImportFailDOExample, SafetyEntryImportFailQueryParams, SafetyEntryImportFailAddParams> {

    @Resource
    private SafetyEntryImportFailService safetyEntryImportFailService;
    @Autowired
    private SafetyBlackMessageService safetyBlackMessageService;

    @Override
    public AbstractSafetyWebBaseService<SafetyEntryImportFailDO, SafetyEntryImportFailDOExample, SafetyEntryImportFailQueryParams> getService() {
        return safetyEntryImportFailService;
    }

    @Override
    public String getModuleName() {
        return "失败信息";
    }

    @Override
    protected String getTemplateName() {
        return null;
    }

    @Override
    protected String getRoleName() {
        return null;
    }

    @Override
    public Pair<String[], String[][]> processExportData(List<SafetyEntryImportFailDO> dataList) {
        String[] title = {"序号", "姓名*", "公司名称*", "备案类型编码*", "身份证号*", "手机号*", "准入厂区编码*", "准入时间-始*(yyyy-MM-dd hh:mm)", "准入时间-终*(yyyy-MM-dd hh:mm)", "是否夜间作业*", "备注", "失败原因"};
        // 使用流操作简化填充数据
        String[][] values = dataList.stream()
                .map(info -> new String[]{
                        String.valueOf(dataList.indexOf(info) + 1),
                        info.getName(),
                        info.getCompanyName(),
                        info.getRecordTypeCode(),
                        info.getCardNo(),
                        info.getPhone(),
                        info.getEnterFactoryCode(),
                        info.getEnterStartTime(),
                        info.getEnterEndTime(),
                        ObjectUtils.isEmpty(info.getNightFlag()) ? "" : (info.getNightFlag() == 0 ? "否" : "是"),
                        info.getRemark(),
                        info.getFailRemark(),
                })
                .toArray(String[][]::new);

        return Pair.of(title, values);
    }

    /**
     * 根据版本号删除导出失败的数据
     * @param params 入参 版本号
     */
    public boolean deleteFailList(SafetyEntryImportFailQueryParams params) {
        String versionNo = params.getVersionNo();
        if (StringUtils.isBlank(versionNo)) {
            throw new BizException(ResponseEnum.FAIL.getCode(), "版本号不能为空");
        }
        safetyEntryImportFailService.deleteByVersionNo(versionNo);
        return true;
    }

    public Object getDtoListByVersionNo(String versionNo){
        List<SafetyEntryBlankProcessDetailDO> dtos = new ArrayList<>();
        if(StringUtils.isBlank(versionNo)){
            return dtos;
        }
        SafetyEntryImportFailQueryParams params = new SafetyEntryImportFailQueryParams();
        params.setVersionNo(versionNo);
        params.setFailRemark(SafetyEntryConstants.CONFIG_FAILE_BLANK2);
        List<SafetyEntryImportFailDO> failList = safetyEntryImportFailService.selectList(params,false);
        // 根据离职前的信息
        List<String> cardNoList = failList.stream().map(SafetyEntryImportFailDO::getCardNo).collect(Collectors.toList());
        List<BlackMessageDto> black = new ArrayList<>();
        if(!CollectionUtils.isEmpty(cardNoList)){
            black = safetyBlackMessageService.selectBlackEmpInfo(cardNoList);
        }
        Map<String, BlackMessageDto> dataMap = black.stream().collect(Collectors.toMap(BlackMessageDto::getIdCardNo, t -> t, (oldInfo, newInfo) -> oldInfo, HashMap::new));

        failList.forEach(li->{
            SafetyEntryBlankProcessDetailDO dto = new SafetyEntryBlankProcessDetailDO();
            BeanUtils.copyProperties(li,dto);
            BlackMessageDto blackMessageDto = dataMap.get(dto.getCardNo());
            if (blackMessageDto != null) {
                dto.setOrgCodePath(blackMessageDto.getOrgCodePath());
                dto.setOrgNamePath(blackMessageDto.getOrgNamePath());
                dto.setPositionType(blackMessageDto.getPositionType());
            }
            dtos.add(dto);
        });
        return dtos;
    }
}














