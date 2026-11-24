package com.yutong.safety.core.service.enter;

import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.StreamUtil;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.utils.excel.OutXSSFUtil;
import com.yutong.safety.common.utils.excel.PubExcelUtil;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingDock;
import com.yutong.safety.core.db.entity.enter.SafetyEnterFilingPer;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterFilingHistoryRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterFilingSh;
import com.yutong.safety.core.db.mapper.enter.SafetyEnterFilingHistoryMapper;
import com.yutong.safety.core.handler.enter.SafetyEnterFilingHandler;
import com.yutong.safety.core.handler.enter.SafetyEnterFilingHistoryHandler;
import com.yutong.safety.core.service.SafetySysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
public class SafetyEnterFilingHistoryService implements SafetyEnterFilingHistoryHandler {
    @Autowired
    private SafetyEnterFilingHistoryMapper enterFilingHistoryMapper;
    @Autowired
    private SafetyEnterFilingHandler enterFilingHandler;
    @Autowired
    private SafetySysUserService safetySysUserService;

    private static String SEARCH_ADMIN_ROLE_NAME = "物流车备案信息查询";

    @Override
    public PageVO selectEnterFilingHistoryPage(SafetyEnterFilingSh enterFilingSh,String token) {
        String isAdmin = enterFilingHandler.checkAdmin(token);
        if(!"T".equals(isAdmin)){
            if(checkSearchAdmin(token,SEARCH_ADMIN_ROLE_NAME)){
                isAdmin = "T";
            }
        }
        enterFilingSh.setIsAdmin(isAdmin);
        List<SafetyEnterFilingHistoryRt> list = enterFilingHistoryMapper.selectEnterFilingHistoryList(enterFilingSh);
        Integer count = enterFilingHistoryMapper.selectEnterFilingHistoryCount(enterFilingSh);
        return new PageVO(count, list);
    }

    @Override
    public SafetyEnterFilingHistoryRt selectEnterFilingHistoryById(Integer id) {
        return enterFilingHistoryMapper.selectEnterFilingHistoryById(id);
    }

    @Override
    public List<SafetyEnterFilingDock> selectEnterFilingDockHis(String phone) {
        List<SafetyEnterFilingDock> list = enterFilingHistoryMapper.selectEnterFilingDockHis(phone);
        return list;
    }

    @Override
    public byte[] exportEnterFilingHistory(SafetyEnterFilingSh enterFilingSh,String token) {
        byte[] content = null;
        //定义HSSFWorkbook对象
        XSSFWorkbook wb = null;
        //定义输出流
        ByteArrayOutputStream os = null;
        InputStream fis = null;
        try {
            String isAdmin = enterFilingHandler.checkAdmin(token);
            if(!"T".equals(isAdmin)){
                if(checkSearchAdmin(token,SEARCH_ADMIN_ROLE_NAME)){
                    isAdmin = "T";
                }
            }
            enterFilingSh.setIsAdmin(isAdmin);
            //查询要导出的数据
            enterFilingSh.setExport("Y");
            List<SafetyEnterFilingHistoryRt> list = enterFilingHistoryMapper.selectEnterFilingHistoryList(enterFilingSh);
            //获取模板
            ClassPathResource classPathResource = new ClassPathResource("excel/enterFilingHistoryExport.xlsx");
            fis = classPathResource.getInputStream();
            wb = new XSSFWorkbook(fis);
            //写入Excel数据
            XSSFSheet sheet = wb.getSheetAt(0);
            //创建数据对象，用来保存list数据
            Object[][] values = new Object[list.size()][sheet.getRow(0).getPhysicalNumberOfCells()];
            for(int i=0; i<list.size(); i++) {
                SafetyEnterFilingHistoryRt d = list.get(i);
                values[i][0] = d.getFdState();
                values[i][1] = d.getFdFilingType();
                values[i][2] = d.getFdEnterType();
                values[i][3] = d.getFdCarNo();
                values[i][4] = d.getFdFilingFactory();
                values[i][5] = d.getFdFilingBegin();
                values[i][6] = d.getFdFilingEnd();
                values[i][7] = d.getApproveFilingFactory();
                values[i][8] = d.getApproveFilingEnd();
                StringBuffer sbf = new StringBuffer();
                List<SafetyEnterFilingPer> perList = d.getPerList();
                for(int p=0;p<perList.size();p++){
                    SafetyEnterFilingPer per = perList.get(p);
                    sbf.append(per.getFdPerType()).append(" ");
                    sbf.append(per.getFdPerName()).append(" ");
                    sbf.append(per.getFdPerCard()).append(" ");
                    sbf.append(per.getFdPerPhone()).append("\r\n");
                }
                values[i][9] = "";//sbf.toString();
                values[i][10] = d.getFdProtectGrade();
                values[i][11] = d.getFdDetailedImg();
                values[i][12] = d.getFdSupplierName();
                values[i][13] = d.getFdGoodsName();
                values[i][14] = d.getFdGoodsWeight();
                values[i][15] = d.getFdCarImg();
                values[i][16] = d.getCreateTime();
                values[i][17] = d.getFdDockName();
                values[i][18] = d.getFdDockNo();
                values[i][19] = d.getFdDockDepartment();
                values[i][20] = d.getFdRemark();
                values[i][21] = d.getFdOperationTime();
                values[i][22] = d.getFdOperationUserName();
                values[i][23] = d.getFdOperationUserCode();
                values[i][24] = d.getFdOperation();
            }
            //写入数据
            OutXSSFUtil.getXSSFWorkbook(values, wb, sheet, 1, 11);
            os = new ByteArrayOutputStream();
            wb.write(os);
            content = os.toByteArray();
        }catch (Exception e){
            LogUtils.error("数据导出异常,{}", e);
        }finally {
            StreamUtil.close(fis);
            StreamUtil.close(os);
            PubExcelUtil.close(wb);
        }
        return content;
    }

    /**
     * 校验是否查询管理员权限
     * @param token 当前token
     * @param roleName 查询管理员角色名称
     * @return 是否
     */
    private boolean checkSearchAdmin(String token,String roleName){
        String userName = TokenUtils.getUserName(token);
        UserMessageListParam param = new UserMessageListParam();
        param.setRoleName(roleName);
        param.setUserName(userName);
        List<SafetySysUserDO> list = safetySysUserService.getList(param);
        if(CollectionUtils.isEmpty(list)){
            return false;
        }
        return true;
    }
}
