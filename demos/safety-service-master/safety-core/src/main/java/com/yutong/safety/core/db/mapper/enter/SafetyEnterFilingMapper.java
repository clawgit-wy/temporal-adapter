package com.yutong.safety.core.db.mapper.enter;

import com.yutong.safety.core.db.entity.enter.SafetyEnterFiling;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingDock;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingPer;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterFilingAppRt;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterFilingInfoRt;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterFilingRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterFilingAppSh;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterFilingSh;
import com.yutong.safety.core.db.entity.result.DictItemRt;
import com.yutong.safety.core.dto.SafetyPersonCarOutRuleDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SafetyEnterFilingMapper {

    SafetyEnterFilingInfoRt selectEnterFilingById(@Param("id") String id);
    List<SafetyEnterFilingRt> selectEnterFilingList(SafetyEnterFilingSh enterFilingSh);
    Integer selectEnterFilingCount(SafetyEnterFilingSh enterFilingSh);
    List<SafetyEnterFilingPer> selectEnterFilingPerList(@Param("filingId") String filingId);

    Integer insertEnterFiling(SafetyEnterFiling enterFiling);
    Integer insertEnterFilingBatch(List<SafetyEnterFiling> list);

    Integer insertEnterFilingPerBatch(List<SafetyEnterFilingPer> list);
    Integer updateEnterFiling(SafetyEnterFiling enterFiling);
    Integer updateEnterFilingState(SafetyEnterFiling enterFiling);
    String selectEnterFilingState(@Param("id") String id);
    List<String> selectEnterTimeOut();
    Integer updateEnterTimeOut(Map map);
    Integer deleteEnterFilingPerAll(@Param("filingId") String filingId);
    Integer deleteEnterFiling(@Param("id") String id);
    List<Map> selectDockList(@Param("id") String id);
    List<SafetyEnterFilingDock> selectDockListByKey(@Param("fdKey") String fdKey);
    List<Map> selectCarList(@Param("fdCarNo") String fdCarNo);
    String  selectFilingFactory(@Param("id") String id);

    Integer insertEnterFilingPer(SafetyEnterFilingPer per);
    Integer updateEnterFilingPer(SafetyEnterFilingPer per);
    Integer deleteEnterFilingPer(Map map);
    Integer checkEnterFilingPerChange(SafetyEnterFilingPer per);

    List<SafetyEnterFilingAppRt> selectEnterFilingAppList(SafetyEnterFilingAppSh appSh);
    Integer selectEnterFilingAppCount(SafetyEnterFilingAppSh appSh);

    List<DictItemRt> selectFilingFactoryList();

    SafetyEnterFiling selectByPrimary(String id);

    List<String> selectAdministrator(@Param("userName") String userName);

    List<String> selectTodayEnterList(Map map);

    String selectFilingFactoryByCode(@Param("code") String code);

    /**
     * 根据获取车辆具有下发权限的最新审批时间的ID
     * @param map
     * @return
     */
    List<String> selectTodayEnterMaxList(Map map);

    //根据身份证、车牌号以及申请入场时间查询数据
    List<Map<String,Object>> verifyBeforeInsert(@Param("paramMap") Map<String,Object> paramMap);
    List<Map<String,Object>> verifyBeforeApprove(@Param("paramMap") Map<String,Object> paramMap);

    Map<String,Object> getPersonByProcessId(@Param("processId")String processId);

    //获取重叠数据
    List<Map<String,Object>> getOverlapDataList(SafetyEnterFiling safetyEnterFiling);

    //删除审批完成的数据
    void deleteByProcessIdList(@Param("processIdList") List<String> processIdList);

    //根据手机号获取弹框信息
    Map<String, Object> getAlertMes(@Param("phoneNo") String phoneNo);

    //根据手机号设置弹框提示信息不再展示
    void setAlertMesHidden(@Param("phoneNo") String phoneNo, @Param("mesId") String mesId);

    //根据手机号删除
    void deleteByPhoneNo(@Param("phoneNo") String phoneNo);

    List<SafetyEnterFilingRt> selectEnterList(@Param("processId") String processId);

    void updateStatusByList(@Param("enList")List<SafetyEnterFilingRt> enList);
    void updateStatusHistoryByList(@Param("enList")List<SafetyEnterFilingRt> enList);

    List<SafetyEnterFilingRt> getMesByKafka(Map<String,String> map);

    SafetyPersonCarOutRuleDTO getBaseInfo(SafetyPersonCarOutRuleDTO en);

    List<SafetyEnterFilingRt> selectEnterThirdList(@Param("processId") String processId);

    Integer selectCountByCarNo(@Param("id") String id, @Param("fdCarNo") String fdCarNo);

    List<SafetyEnterFilingPer> selectMergeEnterFiling(@Param("list") List<String> cardNos);
}
