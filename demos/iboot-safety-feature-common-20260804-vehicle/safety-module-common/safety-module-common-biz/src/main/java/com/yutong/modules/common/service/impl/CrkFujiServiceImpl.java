package com.yutong.modules.common.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.modules.common.api.ICrkFujiService;
import com.yutong.modules.common.factory.ThirdApiFactory;
import com.yutong.modules.common.vo.CrkFaceModifyVO;
import com.yutong.modules.common.query.CrkSnapshotPhotoQuery;
import com.yutong.modules.common.vo.CrkSnapshotPhotoVO;
import com.yutong.modules.common.vo.CrkStaffInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CrkFujiServiceImpl implements ICrkFujiService {

    @Resource
    private ThirdApiFactory thirdApiFactory;

    @Override
    public String editStaffOfCrk(CrkStaffInfoVO staffInfoVO) {
        return null;
    }

    @Override
    public String getStaffInfoOfCrk(String crkId) {
        return null;
    }

    @Override
    public List<CrkSnapshotPhotoVO> getCrkSnapshotPhoto(CrkSnapshotPhotoQuery query) {
        List<CrkSnapshotPhotoVO> recordList = new ArrayList<>();
        try {
            int pageSize = 1000;
            if (query.getPageSize() != null) {
                pageSize = query.getPageSize();
            }
            int currentPage = 1;
            boolean hasNextPage = true;
            while (hasNextPage) {
                JSONObject request = new JSONObject();
                request.put("PageSize", pageSize);
                request.put("CurrentPage", currentPage);
                request.put("OrderBy", query.getOrderBy());
                request.put("OrderType", query.getOrderType());
                request.put("where", query.getWhere());
                request.put("Append", query.getAppend());
                request.put("TotalCount", query.getTotalCount());
                String response = thirdApiFactory.executeMethod("crk_get_customer", request.toJSONString());

                JSONObject responseObj = JSONObject.parseObject(response);
                int total = responseObj.getJSONObject("PageAttri").getInteger("TotalCount");
                if (total <= 0) {
                    hasNextPage = false;
                    continue;
                }

                JSONArray records = responseObj.getJSONArray("Records");
                if (!records.isEmpty()) {
                    recordList.addAll(records.toJavaList(CrkSnapshotPhotoVO.class));
                }

                if (query.getPageInfo()) {
                    currentPage++;
                    hasNextPage = currentPage <= getTotalPages(total, pageSize);
                } else {
                    hasNextPage = false;
                }
            }
        } catch (Exception e) {
            log.error("更新记录失败", e);
        }
        return recordList;
    }

    @Override
    public String faceModifyOfCrk(CrkFaceModifyVO modifyVO) {
        return null;
    }


    private int getTotalPages(int total, int pageSize) {
        return (int) Math.ceil((double) total / pageSize);
    }

}
