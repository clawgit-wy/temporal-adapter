package com.yutong.safety.core.service.enter;

import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.utils.JsonUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.StreamUtil;
import com.yutong.safety.common.utils.TokenUtils;
import com.yutong.safety.common.utils.excel.OutXSSFUtil;
import com.yutong.safety.common.utils.excel.PubExcelUtil;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.enter.SafetyEnterProtect;
import com.yutong.safety.core.db.entity.enter.result.SafetyEnterProtectRt;
import com.yutong.safety.core.db.entity.enter.search.SafetyEnterProtectSh;
import com.yutong.safety.core.db.entity.result.DictItemRt;
import com.yutong.safety.core.db.mapper.enter.SafetyEnterProtectMapper;
import com.yutong.safety.core.handler.enter.SafetyEnterProtectHandler;
import com.yutong.safety.core.service.SafetyDictItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SafetyEnterProtectService implements SafetyEnterProtectHandler {
    @Autowired
    private SafetyEnterProtectMapper enterProtectMapper;
    @Autowired
    private SafetyDictItemService dictItemService;

    @Override
    public PageVO selectEnterProtectPage(SafetyEnterProtectSh enterProtectSh) {
        List<SafetyEnterProtectRt> list = enterProtectMapper.selectEnterProtectList(enterProtectSh);
        Integer count = enterProtectMapper.selectEnterProtectCount(enterProtectSh);
        return new PageVO(count, list);
    }

    @Override
    public void insertEnterProtect(SafetyEnterProtect enterProtect, String token) {
        String tokenUser = TokenUtils.getRealName(token);
        enterProtect.setCreateTime(new Date());
        enterProtect.setCreateUser(tokenUser);
        enterProtectMapper.insertEnterProtect(enterProtect);
    }

    @Override
    public void deleteEnterProtect(Integer id, String token) {
        if(id == null){
            throw new BizException(ResponseEnum.PARAM_ERROR);
        }
        String tokenUser = TokenUtils.getRealName(token);
        SafetyEnterProtectRt rt = enterProtectMapper.selectEnterProtectById(id);
        LogUtils.info("{} 删除物流车环保信息 {}", tokenUser, JsonUtils.toString(rt));
        enterProtectMapper.deleteEnterProtect(id);
    }

    @Override
    public byte[] exportEnterProtect(SafetyEnterProtectSh enterProtectSh) {
        byte[] content = null;
        //定义HSSFWorkbook对象
        XSSFWorkbook wb = null;
        //定义输出流
        ByteArrayOutputStream os = null;
        InputStream fis = null;
        try {
            //查询要导出的数据
            List<SafetyEnterProtectRt> list = enterProtectMapper.selectEnterProtectList(enterProtectSh);
            //获取模板
            ClassPathResource classPathResource = new ClassPathResource("excel/enterProtectExport.xlsx");
            fis = classPathResource.getInputStream();
            wb = new XSSFWorkbook(fis);
            //写入Excel数据
            XSSFSheet sheet = wb.getSheetAt(0);
            //创建数据对象，用来保存list数据
            Object[][] values = new Object[list.size()][sheet.getRow(0).getPhysicalNumberOfCells()];
            for(int i=0; i<list.size(); i++) {
                SafetyEnterProtectRt d = list.get(i);
                values[i][0] = d.getFdCarNo();
                values[i][1] = d.getFdCarBelong();
                values[i][2] = d.getFdDriverName();
                values[i][3] = d.getFdProtectGrade();
                values[i][4] = d.getFdDetailedImg();
                values[i][5] = d.getFdVin();
                values[i][6] = d.getFdEngine();
                values[i][7] = d.getFdCarImg();
                values[i][8] = d.getFdInFactory();
                values[i][9] = d.getCreateTime();
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

    @Override
    public Map getSearchParams() {
        // 环保等级
        List<DictItemRt> list1 = dictItemService.selectDictItemListBySupperCode("protect_grade");
        Map map = new HashMap();
        map.put("p1", list1);
        return map;
    }

    /**
     * 根据车牌号获取对象
     * @param carNo 车牌号
     * @return 返回值
     */
    public SafetyEnterProtectRt getEnterProtectByCarNo(String carNo){
        SafetyEnterProtectSh enterProtectSh = new SafetyEnterProtectSh();
        enterProtectSh.setFdCarNo(carNo);
        return enterProtectMapper.selectEnterProtectByCarNo(carNo);
    }
}
