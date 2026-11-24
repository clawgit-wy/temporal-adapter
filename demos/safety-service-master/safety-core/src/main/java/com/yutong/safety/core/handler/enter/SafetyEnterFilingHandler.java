package com.yutong.safety.core.handler.enter;

import com.yutong.safety.common.utils.excel.ErrorInfo;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.core.db.entity.enter.SafetyEnterApprove;
import com.yutong.safety.core.db.entity.enter.SafetyEnterCrk;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFiling;
import com.yutong.safety.core.db.entity.enter.rbody.SafetyEnterFilingBody;
import com.yutong.safety.core.db.entity.enter.rbody.SafetyEnterFilingPerBody;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterFilingInfoRt;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterFilingPerAppRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterFilingAppSh;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterFilingSh;
import com.yutong.safety.core.db.entity.result.BpmModelRt;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface SafetyEnterFilingHandler {
    /**
     * 审核
     * */
    List<String> selectFilingFactory(String id);
    String approveEnterFiling(SafetyEnterApprove approve, String token);
    void approveEnterFilingBatch(String ids, Integer approveType, String token);
    void insertCrk(String id, String token, List<SafetyEnterCrk> list);
    /**
     * 查询
     * */
    Map getSearchParams();
    PageVO selectEnterFilingPage(SafetyEnterFilingSh enterFilingSh, String token);
    PageVO selectEnterFilingListApp(SafetyEnterFilingAppSh enterFilingAppSh);
    SafetyEnterFilingPerAppRt selectEnterFilingPerApp(String id);
    List selectEnterFilingCar(String carNo);
    SafetyEnterFilingInfoRt selectEnterFilingInfo(String id);
    /**
     * 新增
     * */
    void insertEnterFilingWeb(SafetyEnterFilingBody enterFiling, String token);
    void insertEnterFilingApp(SafetyEnterFilingBody enterFiling);
    void insertEnterFiling(SafetyEnterFilingBody body, String workNo, String tokenUser, BpmModelRt bpm);
    /**
     * 编辑
     * */
    void updateEnterFiling(SafetyEnterFilingBody enterFiling, String token);
    void updateEnterFilingPer(SafetyEnterFilingPerBody perBody);
    String updateEnterFilingState(String formId, String processData, String state);
    String updateEnterTimeOut();
    String updateEnterOwer(String id, String carNo, String dockNo, String date, Boolean isTask);
    /**
     * 删除
     * */
    void deleteEnterFiling(String id, String token);
    /**
     * 导入
     * */
    List<ErrorInfo> importEnterFiling(MultipartFile file, String token);
    /**
     * 导出
     * */
    byte[] exportEnterFiling(SafetyEnterFilingSh enterFilingSh, String token);

    void updateFilingGoods(SafetyEnterFiling enterFiling);

    Map drivingLicense(String url);

    String checkAdmin(String token);

    boolean validTelNumberByIdCard(String phone, String idCard);

    //入厂申请填报前信息校验
    boolean verifyBeforeInsert(SafetyEnterFilingBody enterFiling);

    //入厂申请审批时信息校验
    Map<String, Object> verifyBeforeApprove(SafetyEnterApprove approve);

    //根据手机号获取弹框信息
    Map<String, Object> getAlertMes(String phoneNo);

    //根据手机号设置弹框提示信息不再展示
    void setAlertMesHidden(String phoneNo, String mesId);

    //发送宇信通消息
    void sendYXTMes(Map<String, String> paramMap);

    VO getRegisterInfo(String phone);
}
