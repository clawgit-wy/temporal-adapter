package com.yutong.safety.core.service.enter;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyEmpowerDeviceDO;
import com.yutong.safety.core.db.entity.enter.SafetyEnterDockCrk;
import com.yutong.safety.core.db.entity.enter.SafetyEnterIssuanceDO;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterIssuanceRT;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterIssuanceSh;
import com.yutong.safety.core.db.mapper.enter.IncomingMapper;
import com.yutong.safety.core.db.mapper.enter.SafetyEnterApproveMapper;
import com.yutong.safety.core.db.mapper.enter.SafetyEnterIssuanceMapper;
import com.yutong.safety.core.handler.enter.SafetyEnterRegisterHandler;
import com.yutong.safety.core.handler.pub.PubFileHandler;
import com.yutong.safety.core.service.SafetyDictItemService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants.ENTRY_DICT_SHOW_FLAG_VALUE;
import static com.yutong.safety.core.db.entity.entry.common.SafetyEntryConstants.ENTRY_DICT_SUPPER_CODE_TYPE;

@Service
public class SafetyEnterIssuanceService {


    @Value("${logistics.card.tcmid}")
    private String logisticsCardTcmid;

    @Autowired
    private SafetyEnterIssuanceMapper safetyEnterIssuanceMapper;

    @Autowired
    private SafetyEnterApproveMapper enterApproveMapper;

    @Autowired
    private IncomingService incomingHandler;

    @Autowired
    private IncomingMapper incomingMapper;

    @Autowired
    private PubFileHandler pubFileHandler;

    @Autowired
    private SafetyEnterRegisterHandler enterRegisterHandler;

    @Resource
    private SafetyDictItemService safetyDictItemService;

    public List<SafetyEnterIssuanceRT> queryList(SafetyEnterIssuanceSh enterFactoryIssuance){
        return safetyEnterIssuanceMapper.queryList(enterFactoryIssuance);
    }

    public int queryCount(SafetyEnterIssuanceSh enterFactoryIssuance){
        return safetyEnterIssuanceMapper.queryCount(enterFactoryIssuance);
    }

    public int save(SafetyEnterIssuanceDO enterFactoryIssuance){
        return safetyEnterIssuanceMapper.save(enterFactoryIssuance);
    }

    public int update(SafetyEnterIssuanceDO enterFactoryIssuance){
        return safetyEnterIssuanceMapper.update(enterFactoryIssuance);
    }

    public int saveBatch(List<SafetyEnterIssuanceDO> issuanceDOList){
        return safetyEnterIssuanceMapper.saveBatch(issuanceDOList);
    }

    public PageVO pageList(SafetyEnterIssuanceSh enterFactoryIssuance){
        if(StringUtils.isBlank(enterFactoryIssuance.getDownStartTime())){
            Date date = DateUtils.addDays(new Date(), -7);
            enterFactoryIssuance.setDownStartTime(DateUtils.format(date,1));
        }

        List<SafetyEnterIssuanceRT> list = safetyEnterIssuanceMapper.queryList(enterFactoryIssuance);
        int total = safetyEnterIssuanceMapper.queryCount(enterFactoryIssuance);
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, String> recordDataMap = safetyDictItemService.selectListToMap(ENTRY_DICT_SUPPER_CODE_TYPE, null);
            list.forEach(li -> {
                li.setFdEnterType(recordDataMap.get(li.getFdEnterTypeCode()));
            });
        }
        return new PageVO<>(total, list);
    }

    /**
     * 车辆权限重新下发
     * @param fileingId
     * @param issuanceRT
     */
    public void sendCarAuth(String fileingId,SafetyEnterIssuanceDO issuanceRT){
        SafetyEnterDockCrk dockCrk = incomingMapper.selectSafetyEnterDockCrk(fileingId);
        Map p = new HashMap();
        String factoryCode = issuanceRT.getEnterFactoryCode();

        String[] fs = factoryCode.replace(";",",").split(",");
        p.put("factoryCode",fs);

        p.put("filingType","物流车");
        List<SafetyEmpowerDeviceDO> list1 = enterApproveMapper.checkEmpowerDevice(p);
        if(CollectionUtils.isEmpty(list1)){
            throw new BizException(ResponseEnum.SUCCESS.getCode(),"未找到物流车入厂通道号");
        }
        dockCrk.setBeginTime(issuanceRT.getEnterStartTime());
        dockCrk.setEndTime(issuanceRT.getEnterEndTime());

        String ret = incomingHandler.insertCarAuth(dockCrk, list1);
        try{
            List<SafetyEnterDockCrk> crkList = new ArrayList<>();
            crkList.add(dockCrk);
            incomingHandler.syncSap(crkList);
        }catch (Exception e){
            LogUtils.error("下发SAP异常",e);
        }

        issuanceRT.setIssuanceResult(ret);
        issuanceRT.setIssuanceTime(new Date());
        issuanceRT.setUpdateTime(new Date());

        safetyEnterIssuanceMapper.update(issuanceRT);
    }

    /**
     * 外协人员重新下发
     * @param issuanceRT
     */
    public void sendPersonAuth(SafetyEnterIssuanceDO issuanceRT){
        Map p = new HashMap();
        String factoryCode = issuanceRT.getEnterFactoryCode();
        String[] fs = factoryCode.replace(";",",").split(",");
        p.put("factoryCode",fs);
        // 下发物流司机权限
        p.put("filingType","物流司机");
        List<SafetyEmpowerDeviceDO> list2 = enterApproveMapper.checkEmpowerDevice(p);
        if(CollectionUtils.isEmpty(list2)){
            throw new BizException(ResponseEnum.SUCCESS.getCode(),"未找到物流司机入厂通道号");
        }

        List<Integer> nos = new ArrayList<>();
        for (SafetyEmpowerDeviceDO channel : list2) {
            if (StringUtils.isBlank(channel.getEmpowerNo())) {
                LogUtils.info(channel.getFactoryName() + "通道号为空");
                continue;
            }
            String channelNo = channel.getEmpowerNo();
            String[] channelNos = channelNo.split(",");
            for (String no : channelNos) {
                nos.add(Integer.parseInt(no));
            }
        }

        int[] nums = new int[nos.size()];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = nos.get(i);
        }

        String crossdata = pubFileHandler.imgToBase64(issuanceRT.getFdImg());
        String phoneNo = issuanceRT.getFdPerPhone();
        String idno = issuanceRT.getFdCardNo();
        String staffname = issuanceRT.getFdName();
        String crosstype = "0";
        String endtime = issuanceRT.getEnterEndTime();
        String ret = enterRegisterHandler.doorQrCode("W" + phoneNo, idno, staffname, nums, crosstype, endtime, crossdata, logisticsCardTcmid);
        LogUtils.info("更新备案人操作 执行下发权限结果：{} {} {} {}", phoneNo, staffname, endtime, ret);

        issuanceRT.setIssuanceResult(ret);
        issuanceRT.setIssuanceTime(new Date());
        issuanceRT.setUpdateTime(new Date());
        safetyEnterIssuanceMapper.update(issuanceRT);
    }

    /**
     * 批量修改
     * @param list 数据
     */
    public void updateBatch(List<SafetyEnterIssuanceDO> list) {
        safetyEnterIssuanceMapper.updateBatch(list);
    }
}
