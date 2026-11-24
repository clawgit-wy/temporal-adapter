package com.yutong.safety.core.service.spot;

import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.param.web.spot.ValidateEnvironmentAddParam;
import com.yutong.safety.common.param.web.spot.ValidateEnvironmentListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.WebserviceUtil;
import com.yutong.safety.core.db.entity.spot.SafetyValidateEnvironmentDO;
import com.yutong.safety.core.db.entity.spot.SafetyValidateEnvironmentDOExample;
import com.yutong.safety.core.db.mapper.spot.SafetyValidateEnvironmentDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SafetyValidateEnvironmentService {

    @Autowired
    private SafetyValidateEnvironmentDOMapper safetyValidateEnvironmentDOMapper;

    @Value("${car.black.list.add.url}")
    private String carBlackListAddUrl;

    @Autowired
    private WebserviceUtil webserviceUtil;

    /**
     * 新增
     * @param param 入参
     * @return 返回值
     */
    public int save(ValidateEnvironmentAddParam param){
        SafetyValidateEnvironmentDO environmentDO = new SafetyValidateEnvironmentDO();
        BeanUtils.copyProperties(param,environmentDO);
        environmentDO.setCreateTime(new Date());
        return safetyValidateEnvironmentDOMapper.insert(environmentDO);
    }

    /**
     * 更新
     * @param param 入参
     * @return 返回值
     */
    public int update(ValidateEnvironmentAddParam param){
        SafetyValidateEnvironmentDO environmentDO = new SafetyValidateEnvironmentDO();
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
    public List<SafetyValidateEnvironmentDO> selectList(ValidateEnvironmentListParam param, boolean pageFlag) {
        SafetyValidateEnvironmentDOExample example = formatCriteria(param);
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
        SafetyValidateEnvironmentDOExample example = formatCriteria(param);
        return safetyValidateEnvironmentDOMapper.countByExample(example);
    }

    public SafetyValidateEnvironmentDO selectByPrimaryKey(Integer id){
        return safetyValidateEnvironmentDOMapper.selectByPrimaryKey(id);
    }

    /**
     * 参数整理
     * @param param 入参
     * @return 返回值
     */
    private SafetyValidateEnvironmentDOExample formatCriteria(ValidateEnvironmentListParam param) {
        SafetyValidateEnvironmentDOExample example = new SafetyValidateEnvironmentDOExample();
        SafetyValidateEnvironmentDOExample.Criteria criteria = example.createCriteria();

        if(StringUtils.isBlank(param.getFlag())){
            //货车环保标准抽查
            criteria.andBusinessTypeEqualTo("货车环保标准抽查");
        }

        if (param.getFactoryId() != null) {
            criteria.andFactoryIdEqualTo(param.getFactoryId());
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
            String checkEnd = DateUtils.format(DateUtils.addDays(DateUtils.parse(param.getCheckTimeEnd(), 1), 1), 1);
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
            List<Integer> factoryList = Arrays.stream(factoryArr).map(Integer::valueOf).collect(Collectors.toList());
            criteria.andFactoryIdIn(factoryList);
        }
        if(StringUtils.isBlank(param.getOrderByClause())){
            param.setOrderByClause(" create_time desc ");
        }
        example.setOrderByClause(param.getOrderByClause());
        return example;
    }

    /**
     * 增量同步或者全量同步数据
     * @param param 入参
     * @return 返回值k
     */
    public String insertBlankList(ValidateEnvironmentAddParam param) throws Exception{
        String returnStr = "";
        if(StringUtils.isBlank(carBlackListAddUrl)){
            LogUtils.info("出入口黑名单新增接口不能为空!");
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
