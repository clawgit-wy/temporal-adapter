package com.yutong.safety.core.handler.goodsOut;

import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.utils.excel.ErrorInfo;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.common.vo.VO;
import com.yutong.safety.common.vo.VoUtil;
import com.yutong.safety.core.db.entity.goodsOut.SafetyGoodsOutAuthDO;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutAuthAddParam;
import com.yutong.safety.core.db.entity.goodsOut.param.GoodsOutAuthListParam;
import com.yutong.safety.core.dto.goodsOut.SafetyGoodsSelectPersonDTO;
import com.yutong.safety.core.service.goodsOut.SafetyGoodsOutAuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SafetyGoodsOutAuthHandler {

    @Autowired
    private SafetyGoodsOutAuthService safetyGoodsOutAuthService;

    /**
     * 最近选择
     * @param en
     * @return
     */
    public Object getRecentSelect(SafetyGoodsSelectPersonDTO en){
        return safetyGoodsOutAuthService.getRecentSelect(en);
    }

    /**
     * 搜索
     * @param en
     * @return
     */
    public Object getSelectList(SafetyGoodsSelectPersonDTO en){
        return safetyGoodsOutAuthService.getSelectList(en);
    }

    /**
     * 收藏/取消收藏
     * @param ownerEmpNo
     * @param authId
     * @param collectType
     * @return
     */
    public Object collectConfig(String ownerEmpNo,String authId,String collectType,String phoneNum){
        return safetyGoodsOutAuthService.collectConfig(ownerEmpNo,authId,collectType,phoneNum);
    }

    /**
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    public Object pageList(GoodsOutAuthListParam param) {
        List<SafetyGoodsOutAuthDO> list = safetyGoodsOutAuthService.pageList(param);
        long total = safetyGoodsOutAuthService.pageListCount(param);
        return new PageVO<SafetyGoodsOutAuthDO>(total, list);
    }

    /**
     * 数据添加接口
     * @param param
     * @return
     */
    public Object saveOrUpdate(GoodsOutAuthAddParam param, String token){
        String realName = TokenUtils.getRealName(token);
        param.setModifyBy(realName);

        return safetyGoodsOutAuthService.saveOrUpdate(param);
    }

    /**
     * 删除-支持批量
     * @param id  ID拼接串,
     * @return 返回值
     */
    public Object deleteById(String id,String token){
        if(StringUtils.isEmpty(id)){
            return 0;
        }
        String realName = TokenUtils.getRealName(token);
        String[] idArr = id.split(",");
        List<Integer> idList =  Arrays.stream(idArr).map(Integer::valueOf).collect(Collectors.toList());

        return safetyGoodsOutAuthService.deleteById(idList,realName);
    }

    /**
     * 分页查询
     * @param param 查询入参
     * @return 返回
     */
    public List<SafetyGoodsOutAuthDO> getList(GoodsOutAuthListParam param) {
        return safetyGoodsOutAuthService.list(param);
    }

    //导出
    public void exportExcel(GoodsOutAuthListParam param, OutputStream outputStream){
        safetyGoodsOutAuthService.exportExcel(param,outputStream);
    }

    /**
     * 导入
     * @param file
     * @param token
     * @return
     */
    public List<ErrorInfo> importData(MultipartFile file, String token) {
        return safetyGoodsOutAuthService.importData(file,token);
    }

    /**
     * 定时任务取消权限
     * @param file
     * @param token
     */
    public void cancelAccess() {
        safetyGoodsOutAuthService.cancelAccess();
    }

    public void addAccessPer(List<GoodsOutAuthAddParam> enList) {
        safetyGoodsOutAuthService.addAccessPer(enList);
    }
    public void deleteAccessPer(List<GoodsOutAuthAddParam> enList) {
        safetyGoodsOutAuthService.deleteAccessPer(enList);
    }

    public VO queryListForEmpNoOfEip(GoodsOutAuthListParam param) {
        List<SafetyGoodsOutAuthDO> list = safetyGoodsOutAuthService.list(param);
        return VoUtil.successData(list);
    }
}
