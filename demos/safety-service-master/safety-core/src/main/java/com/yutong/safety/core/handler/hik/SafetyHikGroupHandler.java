package com.yutong.safety.core.handler.hik;

import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.SafetyWebBaseEntity;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.SafetyFieldManageDO;
import com.yutong.safety.core.db.entity.entry.SafetyEntryConfig;
import com.yutong.safety.core.db.entity.hik.SafetyHikGroupDO;
import com.yutong.safety.core.db.entity.hik.SafetyHikGroupDOExample;
import com.yutong.safety.core.db.entity.hik.SafetyHikOperationGroupDO;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikGroupAddParam;
import com.yutong.safety.core.db.entity.hik.param.SafetyHikGroupQueryParam;
import com.yutong.safety.core.handler.AbstractSafetyWebBaseHandler;
import com.yutong.safety.core.service.AbstractSafetyWebBaseService;
import com.yutong.safety.core.service.SafetyFieldManageService;
import com.yutong.safety.core.service.entry.SafetyEntryConfigService;
import com.yutong.safety.core.service.hik.SafetyHikGroupService;
import com.yutong.safety.core.service.hik.SafetyHikOperationGroupService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SafetyHikGroupHandler extends AbstractSafetyWebBaseHandler<SafetyHikGroupDO, SafetyHikGroupDOExample, SafetyHikGroupQueryParam, SafetyHikGroupAddParam> {
    @Resource
    private SafetyArtemisHandler safetyArtemisHandler;

    @Resource
    private SafetyFieldManageService safetyFieldManageService;

    @Resource
    public SafetyHikOperationGroupService safetyHikOperationGroupService;

    @Resource
    private SafetyHikGroupService safetyHikGroupService;

    @Resource
    private SafetyEntryConfigService safetyEntryConfigService;

    @Override
    public AbstractSafetyWebBaseService<SafetyHikGroupDO, SafetyHikGroupDOExample, SafetyHikGroupQueryParam> getService() {
        return safetyHikGroupService;
    }

    @Override
    public String getModuleName() {
        return null;
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
    public Pair<String[], String[][]> processExportData(List<SafetyHikGroupDO> dataList) {
        return null;
    }

    @Override
    protected void beforeSave(SafetyHikGroupDO data) {
        if (data.saveOrUpdate() && StringUtils.isBlank(data.getIndexCode())) {
            // 创建分组
            SafetyHikGroupDO safetyHikGroupDO = safetyArtemisHandler.additionGroup(data.getName(), data.getDescription());
            data.setIndexCode(safetyHikGroupDO.getIndexCode());
            data.setStatus(0);
        } else {
            SafetyHikGroupDO safetyHikGroupDO = safetyHikGroupService.selectByPrimaryKey(data.getId());
            if (safetyHikGroupDO == null) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "数据不存在");
            }
            data.setIndexCode(safetyHikGroupDO.getIndexCode());
            // 修改分组
            Boolean bool = safetyArtemisHandler.updateGroup(data);
            if (!Boolean.TRUE.equals(bool)) {
                throw new BizException(ResponseEnum.FAIL.getCode(), "修改人脸分组失败");
            }
        }
    }


    public VO syncUpdateInfo(String empName) {
        // 海康平台数据
        List<SafetyHikGroupDO> hikList = safetyArtemisHandler.queryFaceGroup(null, new ArrayList<>());
        if (CollectionUtils.isEmpty(hikList)) {
            return VoUtil.success();
        }

        // 安保平台数据
        List<SafetyHikGroupDO> list = safetyHikGroupService.selectList(new SafetyHikGroupQueryParam(), false);
        if (CollectionUtils.isEmpty(list)) {
            hikList.forEach(li -> {
                li.setCreates("", empName);
                if (StringUtils.isNotBlank(li.getDescription()) && li.getDescription().split(",").length == 4) {
                    String[] split = li.getDescription().split(",");
                    li.setBuildName(split[0]);
                    li.setBuildCode(split[1]);
                } else {
                    li.setBuildName(li.getName());
                    li.setBuildCode(li.getIndexCode());
                }
            });
            safetyHikGroupService.batchSave(hikList);
        } else {
            Set<String> indexCodes = list.stream().map(SafetyHikGroupDO::getIndexCode).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
            List<SafetyHikGroupDO> updateList = new ArrayList<>();
            List<SafetyHikGroupDO> saveList = new ArrayList<>();
            hikList.forEach(li -> {
                // 存在
                if (indexCodes.contains(li.getIndexCode())) {
                    li.setUpdateBy(empName);
                    li.setUpdateTime(new Date());
                    if (StringUtils.isNotBlank(li.getDescription()) && li.getDescription().split(",").length == 4) {
                        String[] split = li.getDescription().split(",");
                        li.setBuildName(split[0]);
                        li.setBuildCode(split[1]);
                    }
                    updateList.add(li);
                } else {
                    li.setCreates("", empName);
                    li.setBuildName(li.getName());
                    li.setBuildCode(li.getIndexCode());
                    saveList.add(li);
                }
            });
            Set<String> hikIndexCodes = hikList.stream().map(SafetyHikGroupDO::getIndexCode).filter(StringUtils::isNotBlank).collect(Collectors.toSet());

            List<SafetyHikGroupDO> collect = list.stream().filter(li -> StringUtils.isNotBlank(li.getIndexCode()) && !hikIndexCodes.contains(li.getIndexCode())).map(li -> {
                SafetyHikGroupDO info = new SafetyHikGroupDO();
                BeanUtils.copyProperties(li, info);
                info.setUpdateBy(empName);
                info.setUpdateTime(new Date());
                info.setStatus(1);
                return info;
            }).collect(Collectors.toList());
            updateList.addAll(collect);

            if (!CollectionUtils.isEmpty(updateList)) {
                safetyHikGroupService.batchUpdate(updateList);
            }

            if (!CollectionUtils.isEmpty(saveList)) {
                safetyHikGroupService.batchSave(saveList);
            }
        }
        return VoUtil.success();
    }

    public VO batchCreateGroupInfo(String name) {
        List<SafetyHikGroupDO> createList = new ArrayList<>();
        try {
            // 楼栋信息
            List<SafetyFieldManageDO> buildList = safetyFieldManageService.selectBuildList();
            if (CollectionUtils.isEmpty(buildList)) {
                return VoUtil.success();
            }
            // 安保平台数据
            List<SafetyHikGroupDO> list = safetyHikGroupService.selectList(new SafetyHikGroupQueryParam(), false);
            if (!CollectionUtils.isEmpty(list)) {
                List<String> buildCodes = list.stream().map(SafetyHikGroupDO::getBuildCode).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
                buildList = buildList.stream().filter(li -> !buildCodes.contains(li.getFieldCode())).collect(Collectors.toList());
            }

            buildList.forEach(li -> {
                // 创建分组
                SafetyHikGroupDO safetyHikGroupDO = safetyArtemisHandler.additionGroup(li.getFieldName() + "-" + li.getRemark(), (li.getFieldName() + "," + li.getFieldCode() + "," + li.getFieldType() + "," + li.getRemark()));
                safetyHikGroupDO.setBuildCode(li.getFieldCode());
                safetyHikGroupDO.setBuildName(li.getFieldName());
                safetyHikGroupDO.setCreates("", name);
                createList.add(safetyHikGroupDO);
            });
        } catch (Exception e) {
            LogUtils.info("创建分组失败---={}", e.getMessage());
        } finally {
            if (!CollectionUtils.isEmpty(createList)) {
                safetyHikGroupService.batchSave(createList);
            }
        }

        return VoUtil.success();
    }


    public VO saveOperationGroup() {
        String beginTime = DateUtils.format(DateUtils.addDays(new Date(), -1), 1);
        String start = beginTime + " 00:00:00";
        String end = beginTime + " 23:59:59";

        List<SafetyHikOperationGroupDO> list = safetyHikOperationGroupService.getIncrementGroupInfo(start, end);
        if (CollectionUtils.isEmpty(list)) {
            return VoUtil.successData("没有增量门禁数据");
        }

        List<SafetyHikOperationGroupDO> addGroup = new ArrayList<>();
        List<SafetyHikOperationGroupDO> delGroup = new ArrayList<>();

        List<String> addString = new ArrayList<>();
        list.forEach(li -> {
            if (li.getDelFlag() == SafetyWebBaseEntity.NORMAL) {
                li.setOperationType("ADD");
                addString.add(li.getEmpNo() + li.getFaceGroup());
                addGroup.add(li);
            } else {
                li.setOperationType("DELETE");
                delGroup.add(li);
            }
        });

        delGroup.addAll(delGroup.stream().filter(li -> !addString.contains(li.getEmpNo() + li.getFaceGroup())).collect(Collectors.toList()));
        addGroup.addAll(delGroup);

        if (!CollectionUtils.isEmpty(addGroup)) {
            safetyHikOperationGroupService.batchSave(addGroup);
        }

        return VoUtil.success();
    }

    /**
     * 外来人员分组推送
     */
    // TODO: 2026/5/12 暂时不进行外来人员的分组推送，代码没有写完
    public VO updateHikGroupFaces() {
        // 插入分组数据
        String startTime = DateUtils.reduceMinute(new Date(), -33, 3);
        String nowTime = DateUtils.format(new Date(), 3);
        String endTime = DateUtils.reduceMinute(new Date(), 33, 3);
        // 入场备案
        List<SafetyEntryConfig> addEntryList = safetyEntryConfigService.selectListByTime("0", nowTime, endTime);
        List<SafetyEntryConfig> delEntryList = safetyEntryConfigService.selectListByTime("1", startTime, nowTime);
        if (!CollectionUtils.isEmpty(delEntryList)) {
            // 如果存在删除的数据，需要判断是否存在历史数据还未过期的数据
            List<String> cardList = delEntryList.stream().map(SafetyEntryConfig::getCardNo).filter(StringUtils::isNotBlank).collect(Collectors.toList());
            List<SafetyEntryConfig> list = safetyEntryConfigService.selectListByCardNo(nowTime, cardList);
            if (!CollectionUtils.isEmpty(list)) {
                List<String> cards = list.stream().map(SafetyEntryConfig::getCardNo).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                delEntryList.stream().filter(li -> !cards.contains(li.getCardNo())).collect(Collectors.toList());
            }
        }

        // 物流车
        // 访客

        List<SafetyHikOperationGroupDO> addList = new ArrayList<>();
        List<SafetyHikOperationGroupDO> deleteList = new ArrayList<>();
        List<String> empNoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(addList)) {
            empNoList.addAll(addList.stream().map(SafetyHikOperationGroupDO::getEmpNo).filter(StringUtils::isNotBlank).collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(deleteList)) {
            empNoList.addAll(deleteList.stream().map(SafetyHikOperationGroupDO::getEmpNo).filter(StringUtils::isNotBlank).collect(Collectors.toList()));
        }
        return safetyArtemisHandler.updateHkiGroup(addList, deleteList, empNoList, false);
    }

    public List<String> selectList(List<String> faceGroupIndexCodeName) {
        SafetyHikGroupQueryParam param = new SafetyHikGroupQueryParam();
        param.setNames(faceGroupIndexCodeName);
        param.setStatus(Constants.STATUS_0);
        List<SafetyHikGroupDO> list = safetyHikGroupService.selectList(param, false);
        if (!CollectionUtils.isEmpty(list)) {
            return list.stream().map(SafetyHikGroupDO::getIndexCode).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
