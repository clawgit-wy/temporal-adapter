package com.yutong.modules.risk.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yutong.modules.common.utils.YTWebserviceUtil;
import com.yutong.modules.risk.YTSafetyValidateEnvironmentExample;
import com.yutong.modules.risk.dto.SafetyPersonCarOutRuleDTO;
import com.yutong.modules.risk.entity.YTSafetyValidateEnvironment;
import com.yutong.modules.risk.mapper.YTSafetyValidateEnvironmentDOMapper;
import com.yutong.modules.risk.util.YTDateUtils;
import com.yutong.modules.risk.util.YTLogUtils;
import com.yutong.modules.risk.web.ValidateEnvironmentAddParam;
import com.yutong.modules.risk.web.ValidateEnvironmentListParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class YTSafetyValidateEnvironmentService {

    @Autowired
    private YTSafetyValidateEnvironmentDOMapper safetyValidateEnvironmentDOMapper;

    @Value("${car.black.list.add.url}")
    private String carBlackListAddUrl;

    @Autowired
    private YTWebserviceUtil webserviceUtil;

    /**
     * 新增
     * @param param 入参
     * @return 新增记录的主键ID
     */
    public Long save(ValidateEnvironmentAddParam param){
        YTSafetyValidateEnvironment environmentDO = new YTSafetyValidateEnvironment();
        BeanUtils.copyProperties(param,environmentDO);
        environmentDO.setCreateTime(new Date());
        // 使用雪花算法生成ID
        environmentDO.setId(IdWorker.getId());
        safetyValidateEnvironmentDOMapper.insert(environmentDO);
        return environmentDO.getId();
    }

    /**
     * 更新
     * @param param 入参
     * @return 返回值
     */
    public int update(ValidateEnvironmentAddParam param){
        YTSafetyValidateEnvironment environmentDO = new YTSafetyValidateEnvironment();
        BeanUtils.copyProperties(param,environmentDO);
        environmentDO.setUpdateTime(new Date());
        return safetyValidateEnvironmentDOMapper.updateByPrimaryKeySelective(environmentDO);
    }

    /**
     * 获取列表
     * @param param 入参
     * @param pageFlag 是否分页 0-否 1-是
     * @return 返回值
     */
    public List<YTSafetyValidateEnvironment> selectList(ValidateEnvironmentListParam param, boolean pageFlag) {
        YTSafetyValidateEnvironmentExample example = formatCriteria(param);
        //是否分页
        if(pageFlag){
            example.setLimit(param.getLimit());
            example.setOffset(param.getOffset());
        }
        return safetyValidateEnvironmentDOMapper.selectByExample(example);
    }

    /**
     * 获取条数
     * @param param 入参
     * @return 返回值
     */
    public Long selectCount(ValidateEnvironmentListParam param){
        YTSafetyValidateEnvironmentExample example = formatCriteria(param);
        return safetyValidateEnvironmentDOMapper.countByExample(example);
    }

    /**
     * 分页查询（手动count+list，避免分页插件参数绑定异常）
     * @param page 分页对象
     * @param param 查询入参
     * @return IPage 分页结果
     */
    public IPage<YTSafetyValidateEnvironment> pageList(Page<YTSafetyValidateEnvironment> page, ValidateEnvironmentListParam param) {
        YTSafetyValidateEnvironmentExample example = formatCriteria(param);
        long total = safetyValidateEnvironmentDOMapper.countByExample(example);
        List<YTSafetyValidateEnvironment> records;
        if (total > 0) {
            example.setOffset((int) ((page.getCurrent() - 1) * page.getSize()));
            example.setLimit((int) page.getSize());
            records = safetyValidateEnvironmentDOMapper.selectByExample(example);
        } else {
            records = new ArrayList<>();
        }
        page.setTotal(total);
        page.setRecords(records);
        return page;
    }

    public YTSafetyValidateEnvironment selectByPrimaryKey(Long id){
        return safetyValidateEnvironmentDOMapper.selectByPrimaryKey(id);
    }

    /**
     * 参数整理
     * @param param 入参
     * @return 返回值
     */
    private YTSafetyValidateEnvironmentExample formatCriteria(ValidateEnvironmentListParam param) {
        YTSafetyValidateEnvironmentExample example = new YTSafetyValidateEnvironmentExample();
        YTSafetyValidateEnvironmentExample.Criteria criteria = example.createCriteria();

        if(StringUtils.isBlank(param.getFlag())){
            //货车环保标准抽查
            criteria.andBusinessTypeEqualTo("货车环保标准抽查");
        }

        if (param.getFactoryId() != null) {
            criteria.andFactoryIdEqualTo(param.getFactoryId().longValue());
        }
        if (StringUtils.isNotBlank(param.getLocation())) {
            criteria.andLocationLike("%" + param.getLocation() + "%");
        }
        if (StringUtils.isNotBlank(param.getCarNumber())) {
            criteria.andCarNumberLike("%" + param.getCarNumber() + "%");
        }
        if (StringUtils.isNotBlank(param.getDriverName())) {
            criteria.andDriverNameLike("%" + param.getDriverName() + "%");
        }
        if (StringUtils.isNotBlank(param.getCheckTimeBegin()) && StringUtils.isNotBlank(param.getCheckTimeEnd())) {
            String checkEnd = YTDateUtils.format(YTDateUtils.addDays(YTDateUtils.parse(param.getCheckTimeEnd(), 1), 1), 1);
            criteria.andCheckTimeBetween(param.getCheckTimeBegin(), checkEnd);
        }
        if(StringUtils.isNotBlank(param.getCheckPerson())){
            criteria.andCheckPersonLike("%"+param.getCheckPerson()+"%");
        }
        if(StringUtils.isNotBlank(param.getFlowNo())){
            criteria.andFlowNoLike("%"+param.getFlowNo()+"%");
        }
        //工厂权限
        String factoryIdStr = param.getFactoryIdStr();
        if(StringUtils.isNotBlank(factoryIdStr)){
            String[] factoryArr = factoryIdStr.split(",");
            List<Long> factoryList = Arrays.stream(factoryArr).map(Long::valueOf).collect(Collectors.toList());
            criteria.andFactoryIdIn(factoryList);
        }
        if(StringUtils.isBlank(param.getOrderByClause())){
            param.setOrderByClause(" create_time desc ");
        }
        example.setOrderByClause(param.getOrderByClause());
        return example;
    }

    public SafetyPersonCarOutRuleDTO getBaseInfo(SafetyPersonCarOutRuleDTO en){
        if(StringUtils.isEmpty(en.getEmpName()) && StringUtils.isEmpty(en.getPhoneNo()) && StringUtils.isEmpty(en.getCarNo())){
            return null;
        }
        return safetyValidateEnvironmentDOMapper.getBaseInfo(en);
    }

    /**
     * 增量同步或者全量同步数据
     * @param param 入参
     * @return 返回值k
     */
    public String insertBlankList(ValidateEnvironmentAddParam param){
        String returnStr = "";
        if(StringUtils.isBlank(carBlackListAddUrl)){
            YTLogUtils.info("出入口黑名单新增接口不能为空!");
            return "出入口黑名单新增接口不能为空!";
        }
        JSONObject bodyParams = new JSONObject();
        bodyParams.put("TokenId",param.getCarNumber());  //车牌
        bodyParams.put("TokenType",0);
        bodyParams.put("AllRejected",1);

        bodyParams.put("BeginDate",param.getBlackTime()); //开始日期
        bodyParams.put("BeginTime","00:00:00"); //开始时间

        bodyParams.put("EndDate","2099-12-20"); //默认结束日期
        bodyParams.put("EndTime","23:59:59"); //默认结束时间
        bodyParams.put("W1",0);
        bodyParams.put("W2",0);
        bodyParams.put("W3",0);
        bodyParams.put("W4",0);
        bodyParams.put("W5",0);
        bodyParams.put("W6",0);
        bodyParams.put("W7",0);
        bodyParams.put("State",1);
        bodyParams.put("Gid","1A2B1D42-FE05-4C8B-A2C7-783426D2C4B8");
        bodyParams.put("SpecifyDate",0);
        bodyParams.put("SpecifyTime",0);
        bodyParams.put("SpecifyDay",0);
        bodyParams.put("Rid",UUID.randomUUID().toString());

        Map<String, String> headerMap = new HashMap<>();
        String result = webserviceUtil.postJsonStr(carBlackListAddUrl,bodyParams.toJSONString(),headerMap);
        if(StringUtils.isNotEmpty(result)) {
            returnStr = result;
        }
        return returnStr;
    }
}
