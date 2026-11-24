package com.yutong.safety.core.db.mapper.goodsOut;

import com.yutong.safety.core.db.entity.bpm.BpmCurNodeVO;
import com.yutong.safety.core.db.entity.goodsOut.*;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutNodeUpdateParam;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutProcessListParam;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutProcessUpdateParam;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsProcessGooutListParam;
import com.yutong.safety.core.db.entity.goodsOut.vo.GoodsOutProcessDTO;
import com.yutong.safety.core.db.entity.goodsOut.vo.GoodsOutProcessExtVO;
import com.yutong.safety.core.db.entity.goodsOut.vo.GoodsProcessGooutVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SafetyGoodsOutProcessDOBizMapper extends SafetyGoodsOutProcessDOMapper{

    int modifyFlowId(GoodsOutNodeUpdateParam param);

    int modifyCurDealHandler(GoodsOutNodeUpdateParam param);

    int batchModifyCurDeal(@Param("list") List<BpmCurNodeVO> curNodeVOList);

    int batchInsertDetailGoods(@Param("list") List<SafetyGoodsOutDetailGoodsDO> list);

    int batchInsertDetailVehicle(@Param("list") List<SafetyGoodsOutDetailVehicleDO> list);

    int batchInsertDetailReceive(@Param("list") List<SafetyGoodsOutDetailReceiveDO> list);

    int deleteDetailGoodsByProcessId(@Param("processId") String processId);

    int deleteDetailVehicleByProcessId(@Param("processId") String processId);

    int deleteDetailReceiveByProcessId(@Param("processId") String processId);

    List<GoodsOutProcessDTO> getList(GoodsOutProcessListParam param);

    Long getListCount(GoodsOutProcessListParam param);

    GoodsOutProcessDTO selectByPrimaryKeyBiz(String id);

    List<GoodsProcessGooutVO> getProcessGooutList(GoodsProcessGooutListParam gooutListParam);

    Long getProcessGooutCount(GoodsProcessGooutListParam gooutListParam);

    void batchUpdate(@Param("list") List<GoodsOutProcessDTO> list);

    List<SafetyGoodsOutProcessDO> getListByParamMap(Map<String, Object> pMap);

    List<SafetyGoodsOutDetailReceiveDO> getUnReceiveList(Map<String, Object> pMap);

    int batchUpdateExt(@Param("list")List<GoodsOutProcessExtVO> list);

    void deleteOutPower(@Param("beginTime")String beginTime,@Param("endTime")String endTime);
}