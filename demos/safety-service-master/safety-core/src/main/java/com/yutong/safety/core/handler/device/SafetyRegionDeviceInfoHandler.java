package com.yutong.safety.core.handler.device;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yutong.safety.common.constants.Constants;
import com.yutong.safety.common.constants.ResponseEnum;
import com.yutong.safety.common.exception.BizException;
import com.yutong.safety.common.param.web.UserMessageListParam;
import com.yutong.safety.common.utils.DateUtils;
import com.yutong.safety.common.utils.ExcelUtils;
import com.yutong.safety.common.utils.LogUtils;
import com.yutong.safety.common.utils.YxtRebotUtils;
import com.yutong.safety.common.vo.PageVO;
import com.yutong.safety.core.db.entity.SafetyEmpowerDeviceDO;
import com.yutong.safety.core.db.entity.SafetySysUserDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildDeviceDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildEmployeePowerDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDO;
import com.yutong.safety.core.db.entity.build.SafetyBuildPowerDOExample;
import com.yutong.safety.core.db.entity.build.param.BuildDeviceListParam;
import com.yutong.safety.core.db.entity.build.param.BuildEmployeePowerListParam;
import com.yutong.safety.core.db.entity.device.SafetyRegionDeviceInfoDO;
import com.yutong.safety.core.db.entity.device.param.SafetyRegionDeviceInfoQueryParams;
import com.yutong.safety.core.db.entity.device.param.SafetyRegionDeviceInfoUpdateParams;
import com.yutong.safety.core.handler.SafetyOutsiderPromiseChangeHandler;
import com.yutong.safety.core.service.SafetyEmpowerDeviceService;
import com.yutong.safety.core.service.SafetyFieldManageService;
import com.yutong.safety.core.service.SafetySysUserService;
import com.yutong.safety.core.service.build.SafetyBuildDeviceService;
import com.yutong.safety.core.service.build.SafetyBuildEmployeePowerService;
import com.yutong.safety.core.service.build.SafetyBuildPowerService;
import com.yutong.safety.core.service.device.SafetyRegionDeviceInfoService;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SafetyRegionDeviceInfoHandler {
    @Autowired
    SafetyRegionDeviceInfoService safetyRegionDeviceInfoService;

    @Value("${crk.getDevice.url}")
    private String crkGetDeviceUrl;

    @Value("${iam.retention.robotId}")
    private String incomintRebotId;

    @Value("${an.eip.todo.backUrl}")
    private String anEipTodoUrl;

    // 定义所有需要匹配的前缀及对应的返回值
    private final Map<String, String> prefixMap = new HashMap<String, String>() {{
        put("九龙仓库KD", "九龙仓库（KD）");
        put("九龙仓库售后", "九龙仓库（售后）");
        put("精益达西区", "精益达西区");
        put("精益达东区", "精益达东区");
    }};

    @Autowired
    SafetyOutsiderPromiseChangeHandler safetyOutsiderPromiseChangeHandler;
    @Resource
    private SafetyEmpowerDeviceService safetyEmpowerDeviceService;
    @Resource
    private SafetyFieldManageService safetyFieldManageService;
    @Resource
    private YxtRebotUtils yxtRebotUtils;
    @Resource
    private SafetySysUserService safetySysUserService;
    @Resource
    private SafetyBuildDeviceService safetyBuildDeviceService;
    @Resource
    private SafetyBuildEmployeePowerService safetyBuildEmployeePowerService;
    @Resource
    private SafetyBuildPowerService safetyBuildPowerService;

    public void save() {
        try {
            String getDeviceInfo = safetyOutsiderPromiseChangeHandler.sendPost(crkGetDeviceUrl, "{\"PageSize\": 9998,\"where\":\" 1 = 1\"}");
            LogUtils.info("返回值getDeviceInfo=" + getDeviceInfo);
            JSONObject infoObj = JSONObject.parseObject(getDeviceInfo);
            Boolean sucess = infoObj.getJSONObject("State").getBoolean("IsSucess");
            if (!sucess) {
                return;
            }
            JSONArray models = infoObj.getJSONArray("Records");
            if (ObjectUtils.isEmpty(models)) {
                return;
            }

            List<SafetyRegionDeviceInfoDO> list = JSON.parseArray(models.toJSONString(), SafetyRegionDeviceInfoDO.class);
            // 数据入库操作， 返回新增的数据
            List<SafetyRegionDeviceInfoDO> addList = processData(list);
            // 新增的数据发送宇信通
            sendYuXinTong(addList);
        } catch (Exception e) {
            LogUtils.error("{\"code\":\"9999\",\"msg\":\"SafetyRegionDeviceInfoHandler.save方法异常：\"}", e);
            throw new BizException(ResponseEnum.FAIL.getCode(), "SafetyRegionDeviceInfoHandler.save方法异常");
        }
    }

    /**
     * 数据入库处理
     * 出入口同步的数据处理，区分新增的数据， 更新的数据，删除的数据，不同的数据进行不同的处理，然后入库
     *
     * @param newList 本次同步的所有数据
     */
    private List<SafetyRegionDeviceInfoDO> processData(List<SafetyRegionDeviceInfoDO> newList) {
        // 1.取出数据库的旧数据
        List<SafetyRegionDeviceInfoDO> oldList = safetyRegionDeviceInfoService.selectAllList(Lists.newArrayList());

        // 2.过滤出新增的数据
        List<SafetyRegionDeviceInfoDO> addList;
        // 3.过滤出需要删除的数据
        List<SafetyRegionDeviceInfoDO> deleteList = Lists.newArrayList();
        // 4.过滤出需要修改的数据
        List<SafetyRegionDeviceInfoDO> updateList = Lists.newArrayList();

        if (!CollectionUtils.isEmpty(oldList)) {
            List<String> oldDevNoList = oldList.stream().map(SafetyRegionDeviceInfoDO::getDevNo).collect(Collectors.toList());
            List<String> newDevNoList = newList.stream().map(SafetyRegionDeviceInfoDO::getDevNo).collect(Collectors.toList());
            // 新增的数据
            addList = newList.stream().filter(li -> !oldDevNoList.contains(li.getDevNo())).collect(Collectors.toList());
            // 修改的数据
            updateList = newList.stream().filter(li -> oldDevNoList.contains(li.getDevNo())).collect(Collectors.toList());
            // 删除的数据 只删除没有删除的数据， 已删除的数据，不进行删除
            deleteList = oldList.stream().filter(li -> !newDevNoList.contains(li.getDevNo())).collect(Collectors.toList());
        } else {
            addList = newList;
        }

        // 因为Add的数据需要返回， 所以把新增的和修改的同步到update数据集中
        updateList.addAll(addList);
        Date date = new Date();
        if (!CollectionUtils.isEmpty(updateList)) {
            updateList.forEach(li -> {
                // 因为把Add的数据和update的数据汇总到了一块，所以创建时间也增加上，SQL中会进行区分
                li.setCreateTime(date);
                li.setUpdateTime(date);
                li.setDeleteFlag("0");
            });
        }
        // 更新 + 新增
        if (!CollectionUtils.isEmpty(updateList)) {
            safetyRegionDeviceInfoService.batchSaveOrUpdateTwo(updateList);
        }

        List<String> deleteDevNoList = Lists.newArrayList();
        // 删除的数据
        if (!CollectionUtils.isEmpty(deleteList)) {
            deleteDevNoList = deleteList.stream().map(SafetyRegionDeviceInfoDO::getDevNo).collect(Collectors.toList());
            for (SafetyRegionDeviceInfoDO li : deleteList) {
                if ("1".equals(li.getDeleteFlag())) {
                    continue;
                }
                li.setUpdateTime(date);
                li.setDeleteFlag("1");
                // 删除后清空此部分数据
                li.setFactoryId(null);
                li.setFactoryName(null);
                li.setDeviceCategory(null);
            }
            // 删除出入口
            if (!CollectionUtils.isEmpty(deleteList)) {
                safetyRegionDeviceInfoService.batchSaveOrUpdate(deleteList);
            }
        }
        // 删除其他表的数据
        deleteData(deleteDevNoList);
        return addList;
    }

    /**
     * 数据标记删除
     * @param deleteDevNoList 需要删除的设备编码
     */
    private void deleteData(List<String> deleteDevNoList) {
        if (!CollectionUtils.isEmpty(deleteDevNoList)) {
            // 删除门禁设备
            safetyBuildDeviceService.updateBySelective(deleteDevNoList);

            // 员工楼宇权限明细表
            BuildEmployeePowerListParam param = new BuildEmployeePowerListParam();
            param.setDeviceCodeList(deleteDevNoList);
            SafetyBuildEmployeePowerDO record = new SafetyBuildEmployeePowerDO();
            record.setRemark("出入口数据同步删除");
            safetyBuildEmployeePowerService.deleteByExample(param, record);

            // 责任部门楼宇设备明细表
            SafetyBuildPowerDOExample parkExample = new SafetyBuildPowerDOExample();
            SafetyBuildPowerDOExample.Criteria parkCriteria = parkExample.createCriteria();
            parkCriteria.andDeviceCodeIn(deleteDevNoList);
            parkCriteria.andIsDeleteEqualTo("0");
            SafetyBuildPowerDO powerDO = new SafetyBuildPowerDO();
            powerDO.setIsDelete("1");
            powerDO.setRemark("出入口数据同步删除");
            powerDO.setUpdateTime(DateUtils.format(new Date(), 2));
            safetyBuildPowerService.updateByExample(powerDO, parkExample);
        }
    }

    /**
     * 消息发送
     */
    private void sendYuXinTong(List<SafetyRegionDeviceInfoDO> addList) {
        // 1.没有新增，则直接结束
        if (CollectionUtils.isEmpty(addList)) {
            return;
        }

        // 2.根据字典表匹配新增的和修改的所属的厂区，
        // 获取所有的厂区编码
        Map<Integer, String> dataMap = safetyFieldManageService.getFactoryNameList();
        // 匹配厂区
        List<String> factoryIdList = addList.stream().map(li -> getFactoryId(li.getDevName(), dataMap)).collect(Collectors.toList());

        // 3.可以匹配出来的修改厂区信息，发送消息给对应的厂区管理员
        Set<String> empNoList = new HashSet<>();
        // 没有厂区， 则用系统管理员
        List<String> notFactoryInfoList = factoryIdList.stream().filter("未知"::equals).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(notFactoryInfoList)) {
            String secondRole = "门禁系统管理员";
            empNoList.addAll(getEmpNo(secondRole, null));
        }
        // 存在厂区，则获取对应的厂区管理员
        Set<String> factoryInfoList = factoryIdList.stream().filter(li -> !"未知".equals(li)).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(factoryInfoList)) {
            String firstRole = "门禁巡逻组长";
            String powerFacArr = String.join(",", factoryInfoList);
            empNoList.addAll(getEmpNo(firstRole, powerFacArr));
        }

        // 4.发送消息
        String msg = "【安保管理平台】请对富士新增设备分类并维护基础信息，链接：" + anEipTodoUrl + "FSDeviceType";
        empNoList.forEach(li -> yxtRebotUtils.postForEntity(null, incomintRebotId, li, msg, new RestTemplate()));
    }

    /**
     * 获取对应的角色人员信息
     *
     * @param role      角色
     * @param factoryId 厂区ID
     * @return 返回人员信息
     */
    private Set<String> getEmpNo(String role, String factoryId) {
        UserMessageListParam userParam = new UserMessageListParam();
        userParam.setRoleName(role);
        if (StringUtils.isNotBlank(factoryId)) {
            userParam.setPowerFactoryArr(factoryId);
        }
        List<SafetySysUserDO> roleList = safetySysUserService.getList(userParam);
        if (!CollectionUtils.isEmpty(roleList)) {
            return roleList.stream().map(SafetySysUserDO::getEmpNo).collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

    /**
     * 根据匹配规则获取到的值，获取对应的厂区ID
     *
     * @return 返回厂区ID
     */
    private String getFactoryId(String devName, Map<Integer, String> dataMap) {
        String s = matchingRules(devName);
        for (Integer key : dataMap.keySet()) {
            String s1 = dataMap.get(key);
            if (StringUtils.isBlank(s1)) {
                return "未知";
            }
            if (s1.contains(s)) {
                return key.toString();
            }
        }
        return "未知";
    }

    /**
     * 匹配规则
     *
     * @param devName 出入口的值
     * @return 返回匹配规则处理后的值
     */
    private String matchingRules(String devName) {
        if (StringUtils.isBlank(devName)) {
            return "未知";
        }

        for (String key : prefixMap.keySet()) {
            if (devName.startsWith(key)) {
                return prefixMap.get(key);
            }
        }

        // 如果没有匹配到前缀，返回前三个字符或整个字符串
        return devName.length() >= 3 ? devName.substring(0, 3) : devName;
    }


    /**
     * 初始化出入口设备表的设备类型
     */
    public void initRegionDeviceType() {
        // 数据处理
        List<SafetyRegionDeviceInfoDO> list = safetyRegionDeviceInfoService.selectFactoryIdIsNullList();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        // 初始化楼宇门禁部分的数据
        List<SafetyRegionDeviceInfoDO> parkList = initRegionDeviceTypeOfBuild(list);

        // 初始化大门部分的数据
        if (!CollectionUtils.isEmpty(parkList)) {
            initRegionDeviceTypeOfPark(parkList);
        }
    }

    /**
     * 初始化楼宇门禁部分数据
     * @param list 出入口同步表的数据
     * @return 返回厂区需要同步的数据
     */
    private List<SafetyRegionDeviceInfoDO> initRegionDeviceTypeOfBuild(List<SafetyRegionDeviceInfoDO> list) {
        // 1.获取楼宇门禁全量信息， 条件： 富士、在线、未拆除
        BuildDeviceListParam param = new BuildDeviceListParam();
        param.setDeviceType("2");
        List<SafetyBuildDeviceDO> buildAllList = safetyBuildDeviceService.selectByExample(param);
        // 为空，表示楼宇门禁没有需要同步的数据，所以直接返回原数据
        if (CollectionUtils.isEmpty(buildAllList)) {
            return list;
        }

        // 过滤掉设备编码为空的数据
        buildAllList = buildAllList.stream().filter(li -> StringUtils.isNotBlank(li.getDeviceCode())).collect(Collectors.toList());
        // 数据集转换成map; key:设备编码，value：厂区编码+厂区名称
        Map<String, String> buildDataMap = buildAllList.stream().collect(Collectors.toMap(SafetyBuildDeviceDO::getDeviceCode, li -> li.getAnFactoryCode() + ";" + li.getAnFactoryName()));
        // 获取厂区编码集合
        Set<String> buildDeviceCodeList = buildAllList.stream().map(SafetyBuildDeviceDO::getDeviceCode).collect(Collectors.toSet());
        // 过滤出楼宇门禁的数据
        List<SafetyRegionDeviceInfoDO> buildList = list.stream().filter(li -> buildDeviceCodeList.contains(li.getDevNo())).collect(Collectors.toList());
        // 过滤出厂区的数据
        List<SafetyRegionDeviceInfoDO> parkList = list.stream().filter(li -> !buildDeviceCodeList.contains(li.getDevNo())).collect(Collectors.toList());

        Date date = new Date();
        // 使用流处理更新设备信息
        List<SafetyRegionDeviceInfoDO> updatedList = buildList.stream()
                .map(li -> {
                    String devNo = li.getDevNo();
                    String factoryInfo = buildDataMap.get(devNo);
                    if (StringUtils.isBlank(factoryInfo)) {
                        return li;
                    }
                    String[] parts = factoryInfo.split(";");
                    li.setUpdateTime(date);
                    li.setFactoryId("null".equals(parts[0]) ? null : parts[0]);
                    if (parts.length > 1) {
                        li.setFactoryName("null".equals(parts[1]) ? null : parts[1]);
                    }
                    li.setDeviceCategory("2");
                    return li;
                })
                .collect(Collectors.toList());
        safetyRegionDeviceInfoService.batchSaveOrUpdate(updatedList);
        return parkList;
    }

    /**
     * 初始化大门部分数据
     * @param list 出入口同步表的数据
     */
    private void initRegionDeviceTypeOfPark(List<SafetyRegionDeviceInfoDO> list) {
        // 厂区关系表信息
        List<SafetyEmpowerDeviceDO> empowerDeviceList = safetyEmpowerDeviceService.selectList();
        if (CollectionUtils.isEmpty(empowerDeviceList)) {
            return;
        }
        // 创建设备编号与工厂信息的映射
        Map<String, String> dataMap = empowerDeviceList.stream()
                .filter(li -> StringUtils.isNotBlank(li.getEmpowerNo()))
                .flatMap(li -> {
                    String[] empowerNos = li.getEmpowerNo().split(",");
                    String value = li.getFactoryId() + ";" + li.getFactoryName();
                    return Arrays.stream(empowerNos)
                            .filter(StringUtils::isNotBlank)
                            .map(empowerNo -> new AbstractMap.SimpleEntry<>(empowerNo, value));
                })
                .collect(Collectors.toMap(
                        AbstractMap.SimpleEntry::getKey,
                        AbstractMap.SimpleEntry::getValue,
                        (v1, v2) -> v1 // 决定处理重复键的方式，这里取第一个值
                ));

        Date date = new Date();
        // 使用流处理更新设备信息
        List<SafetyRegionDeviceInfoDO> updatedList = list.stream()
                .map(li -> {
                    String factoryInfo = dataMap.get(li.getDevNo());
                    if (StringUtils.isBlank(factoryInfo)) {
                        return li;
                    }
                    String[] parts = factoryInfo.split(";");
                    li.setUpdateTime(date);
                    li.setFactoryId("null".equals(parts[0]) ? null : parts[0]);
                    if (parts.length > 1) {
                        li.setFactoryName("null".equals(parts[1]) ? null : parts[1]);
                    }
                    li.setDeviceCategory("1");
                    return li;
                })
                .collect(Collectors.toList());
        safetyRegionDeviceInfoService.batchSaveOrUpdate(updatedList);
    }

    /**
     * 分页列表
     *
     * @param param 入参
     * @return 返回列表
     */
    public PageVO<SafetyRegionDeviceInfoDO> selectPageList(SafetyRegionDeviceInfoQueryParams param) {
        // 获取数据
        long total = safetyRegionDeviceInfoService.selectCount();
        List<SafetyRegionDeviceInfoDO> list = safetyRegionDeviceInfoService.selectList(param, true);
        return new PageVO<>(total, list);
    }

    /**
     * 设备类型变更
     *
     * @param params 入参
     * @return true 表示成功
     */
    public Boolean updateDeviceType(List<SafetyRegionDeviceInfoUpdateParams> params) {
        // 1. 按设备类别分组，批量更新设备类型
        Map<String, List<Integer>> deviceTypeMap = params.stream()
                .collect(Collectors.groupingBy(SafetyRegionDeviceInfoUpdateParams::getDeviceCategory,
                        Collectors.mapping(SafetyRegionDeviceInfoUpdateParams::getId, Collectors.toList())));

        // 2. 批量更新每个设备类别 ids 在具体的SQL执行方法中进行判空处理
        deviceTypeMap.forEach((category, ids) ->
                safetyRegionDeviceInfoService.batchUpdateDeviceType(ids, category));

        // 3. 获取楼宇设备列表进行后续处理
        List<Integer> buildingList = deviceTypeMap.getOrDefault("2", Collections.emptyList());
        if (CollectionUtils.isEmpty(buildingList)) {
            return true;
        }

        // 判断门禁设备表是否有数据，没有需要新增
        // 4. 查询出入口同步表中指定ID的详情信息
        List<SafetyRegionDeviceInfoDO> infoDOS = safetyRegionDeviceInfoService.selectAllList(buildingList);
        if (CollectionUtils.isEmpty(infoDOS)) {
            return true;
        }

        // 5.根据设备编号，获取楼宇门禁信息表中的信息
        List<String> devNoList = infoDOS.stream().map(SafetyRegionDeviceInfoDO::getDevNo).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        List<SafetyBuildDeviceDO> safetyBuildDeviceDOS = safetyBuildDeviceService.selectListAll(devNoList);
        // 6.需要新增的原数据
        List<SafetyRegionDeviceInfoDO> saveDevNoList;
        // 入库的门禁数据
        List<SafetyBuildDeviceDO> saveAndUpdateList = new ArrayList<>();
        if (CollectionUtils.isEmpty(safetyBuildDeviceDOS)) {
            // 楼宇门禁中没有则需要全量增
            saveDevNoList = infoDOS;
        } else {
            // 楼宇门禁中存在则需要增量和确认状态
            // 增量的数据
            Set<String> oldDeviceCode = safetyBuildDeviceDOS.stream().map(SafetyBuildDeviceDO::getDeviceCode).collect(Collectors.toSet());
            saveDevNoList = infoDOS.stream().filter(li -> !oldDeviceCode.contains(li.getDevNo())).collect(Collectors.toList());

            // 确认状态的数据
            List<SafetyBuildDeviceDO> toUpdate = safetyBuildDeviceDOS.stream()
                    .filter(li -> "1".equals(li.getIsDelete())
                            || "0".equals(li.getDeviceStatus())
                            || "1".equals(li.getRemoveFlag()))
                    .peek(li -> {
                        li.setIsDelete("0");
                        li.setDeviceStatus("1");
                        li.setRemoveFlag("0");
                    })
                    .collect(Collectors.toList());
            saveAndUpdateList.addAll(toUpdate);
        }
        // 7.生成门禁的数据
        if (!CollectionUtils.isEmpty(saveDevNoList)) {
            generalData(saveDevNoList, saveAndUpdateList);
        }
        // 8.数据入库
        if (!CollectionUtil.isEmpty(saveAndUpdateList)) {
            safetyBuildDeviceService.batchUpdate(saveAndUpdateList);
        }
        return true;
    }

    /**
     * 生成数据
     *
     * @param saveDevNoList     原数据
     * @param saveAndUpdateList 生成的新数据
     */
    private void generalData(List<SafetyRegionDeviceInfoDO> saveDevNoList, List<SafetyBuildDeviceDO> saveAndUpdateList) {
        saveDevNoList.forEach(li -> {
            SafetyBuildDeviceDO info = new SafetyBuildDeviceDO();
            info.setIsDelete("0");
            info.setDeviceStatus("1");
            info.setRemoveFlag("0");
            info.setDeviceType("2");//富士
            info.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            info.setDeviceCode(li.getDevNo());
            info.setDeviceName(li.getDevName());
            info.setFactoryCode(li.getFactoryId());
            info.setFactoryName(li.getFactoryName());
            info.setTermName(li.getDevName());
            info.setTermAddr(li.getDevAddr());
            saveAndUpdateList.add(info);
        });
    }

    public void export(HttpServletResponse response) throws IOException {
        // 生成文件名称，以当前时间为后缀，避免文件重名
        String fileName = new String(("设备分类" + DateUtils.format(new Date(), 5) + ".xlsx").getBytes(), "ISO8859-1");

        // 查询作业环节设备信息列表
        List<SafetyRegionDeviceInfoDO> list = safetyRegionDeviceInfoService.selectList(new SafetyRegionDeviceInfoQueryParams(), false);
        // 检查导出数据量是否超过限制
        if (list.size() > Constants.FILE_SIZE) {
            throw new BizException(ResponseEnum.FAIL.getCode(), String.format("导出数据不能超过 %s 条！", Constants.FILE_SIZE));
        }
        // 定义Excel标题
        String[] title = {"COMM_TYPE", "设备号", "设备名称", "GID", "厂区ID", "厂区名称"};
        String[][] values = new String[list.size()][title.length];
        for (int i = 0; i < list.size(); i++) {
            SafetyRegionDeviceInfoDO info = list.get(i);
            int j = 0;
            values[i][j++] = info.getCommType();
            values[i][j++] = info.getDevNo();
            values[i][j++] = info.getDevName();
            values[i][j++] = info.getGid();
            values[i][j++] = info.getFactoryId();
            values[i][j++] = info.getFactoryName();
        }

        String sheetName = "设备分类";
        HSSFWorkbook hssfWorkbook = ExcelUtils.getHSSFWorkbook(sheetName, title, values);

        response.setContentType("application/octet-stream;charset=ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        OutputStream os = response.getOutputStream();
        hssfWorkbook.write(os);
        os.flush();
        os.close();
    }
}
